package com.anniu.shandiandaojia.utils;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

/**
 * 文件下载工具
 * @author DavikChen
 */
public class DownloadUtils {
	 private static final int CONNECT_TIMEOUT = 10000; // 连接超时
	    private static final int DATA_TIMEOUT = 40000; // 读取数超时
	    private final static int DATA_BUFFER = 8192; // 缓冲区大小

	    /**
	     * 回调接口
	     * @author DavikChen
	     *
	     */
	    public interface DownloadListener {
	    	
	    	/**
	    	 * 下载中
	    	 * @param progress 进度
	    	 */
	        public void downloading(int progress);
	        
	        /**
	         * 下载完成
	         */
	        public void downloaded();
	    }

	    /**
	     * 下载指定路径文件
	     * @param urlStr 文件下载地址
	     * @param dest 文件存放路径
	     * @param append 是否追加下载文件
	     * @param downloadListener 下载进度监听器
	     * @return 文件下载大小
	     * @throws Exception
	     */
	    public static long download(String urlStr, File dest, boolean append, DownloadListener downloadListener) throws Exception {
	        int downloadProgress = 0; // 进度
	        long remoteSize = 0; // 文件大小
	        int currentSize = 0; // 上次下载大小
	        long totalSize = -1; // 总大小

	        if(!append && dest.exists() && dest.isFile()) {
	            dest.delete(); // 不追加 删除
	        }

	        if(append && dest.exists() && dest.exists()) {
	            FileInputStream fis = null;
	            try {
	                fis = new FileInputStream(dest);
	                currentSize = fis.available();
	            } catch(IOException e) {
	                throw e;
	            } finally {
	                if(fis != null) {
	                    fis.close();
	                }
	            }
	        }

	        HttpGet request = new HttpGet(urlStr);

	        if(currentSize > 0) {
	            request.addHeader("RANGE", "bytes=" + currentSize + "-"); // 断点下载
	        }

	        HttpParams params = new BasicHttpParams();
	        HttpConnectionParams.setConnectionTimeout(params, CONNECT_TIMEOUT);
	        HttpConnectionParams.setSoTimeout(params, DATA_TIMEOUT);
	        HttpClient httpClient = new DefaultHttpClient(params);

	        InputStream is = null;
	        FileOutputStream os = null;
	        try {
	            HttpResponse response = httpClient.execute(request);
	            if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
	                is = response.getEntity().getContent();
	                remoteSize = response.getEntity().getContentLength();
	                Header contentEncoding = response.getFirstHeader("Content-Encoding");
	                if(contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
	                    is = new GZIPInputStream(is);
	                }
	                os = new FileOutputStream(dest, append);
	                byte buffer[] = new byte[DATA_BUFFER];
	                int readSize = 0;
	                long lastTime = 0;
	                while((readSize = is.read(buffer)) > 0){
	                    os.write(buffer, 0, readSize);
	                    os.flush();
	                    totalSize += readSize;
	                    if(downloadListener!= null){
	                        downloadProgress = (int) (totalSize*100/remoteSize);
	                        	long cuurentTime = System.currentTimeMillis();
	                        	if (lastTime==0) {
	                        		 downloadListener.downloading(downloadProgress);
	                        		 lastTime = cuurentTime;
							}else if((cuurentTime-lastTime) >5000){
								 downloadListener.downloading(downloadProgress);
                        		 lastTime = cuurentTime;
							}
	                       
	                    }
	                }
	                if(totalSize < 0) {
	                    totalSize = 0;
	                }
	            }
	        } finally {
	            if(os != null) {
	                os.close();
	            }
	            if(is != null) {
	                is.close();
	            }
	        }

	        if(totalSize < 0) {
	            throw new Exception("Download file fail: " + urlStr);
	        }

	        if(downloadListener!= null){
	            downloadListener.downloaded();
	        }

	        return totalSize;
	    }
	
	
	
}
