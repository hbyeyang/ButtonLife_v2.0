package com.anniu.shandiandaojia.task;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import com.anniu.shandiandaojia.R;
import com.anniu.shandiandaojia.utils.GlobalInfo;
import com.anniu.shandiandaojia.utils.MyLog;
import com.anniu.shandiandaojia.utils.NetUtils;

import org.apache.http.client.ClientProtocolException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName: DownApkAsyncTask
 * @Description: 下载apk的任务
 * @author zxl
 * @date 2014年11月10日 下午2:46:43
 */
public class DownApkAsyncTask extends AsyncTask<Object, Integer, Boolean> {
	private String TAG = "DownApkAsyncTask";
	private NotificationManager nm;
	private Notification notification;
	private int notificationId = 1234;
	private String downUrl;
	private Context context;
	private int progress;

	private String mSavePath;
	/** 是否取消更新 */
	private String apkName = "news.apk";
	private boolean cancelUpdate = false;
	private final String CMWAP_PROXY = "10.0.0.172";

	public DownApkAsyncTask() {
		super();
	}

	@Override
	protected Boolean doInBackground(Object... params) {
		nm = (NotificationManager) params[0];
		notification = (Notification) params[1];
		notificationId = (Integer) params[2];
		downUrl = (String) params[3];
		context = (Context) params[4];

		return downapk1();
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		if (result) {
			Toast.makeText(context, "下载完成", Toast.LENGTH_LONG).show();
			autoInstall();
		} else {
			Toast.makeText(context, "下载失败", Toast.LENGTH_LONG).show();
		}
		nm.cancel(notificationId);
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		// super.onProgressUpdate(values);
		int value = values[0];
		MyLog.e("DownApkAsyncTask", "onProgressUpdate value= " + value);
		notification.contentView.setProgressBar(R.id.pbDownload, 100, value,false);
		if (value != 100) {
			notification.contentView.setTextViewText(R.id.tvProcess, "已下载"
					+ value + "%");
		} else {
			notification.contentView.setTextViewText(R.id.tvProcess, " 下载完成");
		}

		nm.notify(notificationId, notification);
	}

	private boolean downapk1() {
		MyLog.e(TAG, "DoanApkAsyncTask, downUrl=" + downUrl);
		if (downUrl == null) {
			return false;
		}
		int systemConnection = NetUtils.checkNetwork(context);
		if (systemConnection == GlobalInfo.OPTION_NET_NO) {
			return false;
		}
		URL urlPath = null;
		HttpURLConnection conn = null;
		InputStream inputStream = null;
		try {

			String service = null;

			if (systemConnection == GlobalInfo.OPTION_NET_CMWAP_PROXY) {

				service = getServiceUrl(downUrl);

				this.downUrl = this.downUrl.replace(service, CMWAP_PROXY);
			}

			urlPath = new URL(this.downUrl);

			conn = (HttpURLConnection) urlPath.openConnection();

			conn.setConnectTimeout(15 * 1000);

			conn.setReadTimeout(15 * 1000);

			if (systemConnection == GlobalInfo.OPTION_NET_CMWAP_PROXY)
				conn.setRequestProperty("X-Online-Host", service);

			conn.setRequestMethod("GET");

			conn.setRequestProperty(
					"Accept",
					"image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");

			conn.setRequestProperty("Accept-Language", "zh-CN");

			conn.setRequestProperty("Referer", downUrl);

			conn.setRequestProperty("Charset", "UTF-8");

			conn.setRequestProperty(
					"User-Agent",
					"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");

			conn.setRequestProperty("Connection", "Keep-Alive");

			conn.connect();

			int responseCode = conn.getResponseCode();
			MyLog.e(TAG, "DoanApkAsyncTask, responseCode=" + responseCode);

			if (HttpURLConnection.HTTP_OK != responseCode) {
				return false;
			}
			int length = conn.getContentLength();
			// int length = 950272;
			inputStream = conn.getInputStream();
			this.apkName = getFileName1(conn);
			MyLog.e("DownApkAsyncTask", "fileSize=" + length);
			return writeFile(inputStream, length);

		} catch (ClientProtocolException e) {
			MyLog.e(TAG, "DoanApkAsyncTask, e:" + e.toString());
			return false;
		} catch (IOException e) {
			MyLog.e(TAG, "DoanApkAsyncTask, e:" + e.toString());
			return false;
		} finally {
			if (null != inputStream) {
				try {
					inputStream.close();
				} catch (Exception e) {
					MyLog.e(TAG, e.toString());
				}
			}

			if (null != conn) {
				try {
					conn.disconnect();
				} catch (Exception e) {
					MyLog.e(TAG, e.toString());
				}
			}
		}
	}

	/**
	 * 保存到目录中。
	 * 
	 * @param inputStream
	 * @param length
	 */
	private boolean writeFile(InputStream inputStream, int length) {
		MyLog.e("DownApkAsyncTask2", "writeFile...");
		if (null == inputStream) {
			return false;
		}
		File file = null;
		File apkFile = null;
		// FileOutputStream fos = null;
		FileOutputStream threadfile = null;
		try {
			String sdpath = Environment.getExternalStorageDirectory() + "/";
			/** 下载保存路径 */
			mSavePath = sdpath + "download";
			file = new File(mSavePath);
			// 判断文件目录是否存在
			if (!file.exists()) {
				file.mkdir();
			}
			apkFile = new File(mSavePath, apkName);
			if (apkFile.exists()) {
				apkFile.delete();
			}
			byte buf[] = new byte[1024];
			int numread = 0;
			threadfile = new FileOutputStream(apkFile);
			// threadfile.seek(numread); //threadfile = new
			// FileOutputStream(apkFile);//new RandomAccessFile(apkFile, "rwd");
			int count = 0;
			progress = 0;
			// 写入到文件中
			do {
				// 缓存
				numread = inputStream.read(buf);
				MyLog.e("DownApkAsyncTask", "numread=" + numread);
				if (numread == -1) {
					break;
				}
				// 写入文件
				threadfile.write(buf, 0, numread);
				// count += numread;
				count = count + numread;
				MyLog.e("DownApkAsyncTask", "count=" + count);
				// 计算进度条位置
				int progress_temp = (int) (((float) count / length) * 100);
				if (progress != progress_temp) {
					progress = progress_temp;
					MyLog.e("DownApkAsyncTask", "progress=" + progress);
					// publishProgress(progress);
					onProgressUpdate(progress);
				}

			} while (!cancelUpdate);// 点击取消就停止下载.
			return true;
		} catch (FileNotFoundException e) {
			MyLog.e(TAG, e.toString());
			return false;
		} catch (IOException e) {
			MyLog.e(TAG, e.toString());
			return false;
		} finally {
			if (null != threadfile) {
				try {
					threadfile.close();
				} catch (Exception e) {
					MyLog.e(TAG, e.toString());
				}
			}
		}
	}

	/**
	 * 下载完之后自动安装
	 */
	private void autoInstall() {
		File f = new File(mSavePath, apkName);
		if (!f.exists()) {
			return;
		}
		Intent intent = new Intent();

		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		intent.setAction(android.content.Intent.ACTION_VIEW);

		/* 调用getMIMEType()来取得MimeType */

		String type = "application/vnd.android.package-archive";

		/* 设置intent的file与MimeType */

		intent.setDataAndType(Uri.fromFile(f), type);

		context.startActivity(intent);

	}

	/**
	 * 解析文件名称
	 * 
	 * @param conn
	 *            网络连接对象
	 */
	private String getFileName1(HttpURLConnection conn) {

		String filename = this.downUrl
				.substring(this.downUrl.lastIndexOf('/') + 1);

		// 如果获取不到文件名称
		if (filename == null || "".equals(filename.trim())) {

			for (int i = 0;; i++) {

				String mine = conn.getHeaderField(i);

				if (mine == null)
					break;

				if ("content-disposition".equals(conn.getHeaderFieldKey(i)
						.toLowerCase())) {

					Matcher m = Pattern.compile(".*filename=(.*)").matcher(
							mine.toLowerCase());

					if (m.find())
						return m.group(1);

				}
			}

			// 默认取一个文件名
			filename = UUID.randomUUID() + ".tmp";
		}
		return filename;
	}

	private String getServiceUrl(String downloadUrl) {

		String startStr = "http://";
		String endStr = "com";

		int beginIndex = downloadUrl.indexOf(startStr);

		beginIndex = beginIndex + startStr.length();

		int endIndex = downloadUrl.indexOf(endStr);

		endIndex = endIndex + endStr.length();

		return downloadUrl.substring(beginIndex, endIndex);
	}
}
