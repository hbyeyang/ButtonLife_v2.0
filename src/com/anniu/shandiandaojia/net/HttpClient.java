package com.anniu.shandiandaojia.net;

import android.content.Context;
import android.text.TextUtils;

import com.anniu.shandiandaojia.base.ActivityMgr;
import com.anniu.shandiandaojia.net.bean.HttpReq;
import com.anniu.shandiandaojia.net.bean.HttpRsp;
import com.anniu.shandiandaojia.utils.MyLog;
import com.anniu.shandiandaojia.utils.NetworkUtils;
import com.anniu.shandiandaojia.utils.SPUtils;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

public class HttpClient {
    public String TAG = "HttpClient";
    private static int HTTP_TIMEOUT = 15 * 1000;

    public void sendReq(HttpReq req) {
        String url = UrlInfo.getUrl(req.id);
        String params = req.getParams();
        HttpRsp rsp = new HttpRsp();
        if (!TextUtils.isEmpty(params)) {
            url += params;
        }
        MyLog.i(TAG, url);
        HttpURLConnection conn = null;
        InputStream is = null;
        DataInputStream dis = null;

        try {
            //------------------------------------------------------------------
            java.net.CookieManager manager = new java.net.CookieManager();
            manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
            CookieHandler.setDefault(manager);

            URL urlPath = new URL(url);
            Context context = ActivityMgr.getContext();
            int systemConnection = NetworkUtils.getNetworkState(context);
            conn = getURLConnection(urlPath, systemConnection);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setInstanceFollowRedirects(true);
            // 设置连接主机超时（单位：毫秒）
            conn.setConnectTimeout(HTTP_TIMEOUT);
            // 设置从主机读取数据超时（单位：毫秒）
            conn.setReadTimeout(HTTP_TIMEOUT);
            conn.setRequestProperty("Accept", "*/*");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestMethod("GET");

            String sCookie = SPUtils.getString(ActivityMgr.getContext(), GlobalValue.KEY_COOKIE, null);
            if (sCookie != null && sCookie.length() > 0) {
                conn.setRequestProperty("Cookie", "" + sCookie);
            }

            int responseCode = conn.getResponseCode();
            rsp.code = responseCode;
            MyLog.i(TAG, "httpGet() requestId = " + req.id + " , responseCode = " + responseCode);
            if (HttpURLConnection.HTTP_OK == responseCode) {
                is = conn.getInputStream();
                dis = new DataInputStream(is);
                byte[] responseData = getData(dis);
                rsp.data = responseData;
                MyLog.i(TAG, "httpGet() requestId = " + req.id + ", responseData: " + new String(responseData, "utf-8"));
                req.parseData(new String(responseData, "UTF-8"));
            }
        } catch (SocketTimeoutException e) {
            MyLog.i(TAG, e.toString());
            rsp.code = HttpRsp.CODE_TIMEOUT;
        } catch (UnknownHostException e) {
            MyLog.i(TAG, e.toString());
            rsp.code = HttpRsp.CODE_UNKNOW_HOST;
        } catch (IOException e) {
            MyLog.i(TAG, e.toString());
            rsp.code = HttpRsp.CODE_NET_ERROR;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            req.getOnHttpRsp().onHttpRsp(rsp);
            try {
                if (dis != null) {
                    dis.close();
                }
                if (is != null) {
                    is.close();
                }
                if (conn != null) {
                    conn.disconnect();
                }
            } catch (Exception e) {
            } finally {
                dis = null;
                is = null;
                conn = null;
            }
        }
    }

    public void sendPost(HttpReq req) {
        String url = UrlInfo.getUrl(req.id);
        String params = null;
        try {
            params = URLDecoder.decode(req.getParams(), "utf-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        MyLog.i(TAG, " requestId = " + req.id + " , url = " + url + "\n params = " + " params = " + params);
        HttpRsp rsp = new HttpRsp();

        HttpURLConnection conn = null;
        InputStream is = null;
        DataInputStream dis = null;

        OutputStream os = null;
        int responseCode;
        try {
            URL urlPath = new URL(url);
            conn = (HttpURLConnection) urlPath.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setUseCaches(false);
            conn.setInstanceFollowRedirects(true);
            // 设置连接主机超时（单位：毫秒）
            conn.setConnectTimeout(HTTP_TIMEOUT);
            // 设置从主机读取数据超时（单位：毫秒）
            conn.setReadTimeout(HTTP_TIMEOUT);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Accept", "*/*");
            conn.setRequestProperty("Connection", "keep-alive");
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");

            String sCookie = SPUtils.getString(ActivityMgr.getContext(), GlobalValue.KEY_COOKIE, null);
            if (sCookie != null && sCookie.length() > 0) {
                conn.setRequestProperty("Cookie", "" + sCookie);
            }

            byte[] bytes = params.getBytes();
            if (bytes != null && bytes.length > 0) {
                conn.setRequestProperty("Content-Length", String.valueOf(bytes.length));
            }
            conn.connect();
            if (bytes != null) {
                os = conn.getOutputStream();
                os.write(bytes);
                os.flush();
            }
            responseCode = conn.getResponseCode();
            rsp.code = responseCode;
            MyLog.i(TAG, "httpPost() requestId = " + req.id + " , responseCode = " + responseCode);
            if (HttpURLConnection.HTTP_OK == responseCode) {

                Map<String, List<String>> maps = conn.getHeaderFields();
                List<String> coolist = maps.get("Set-Cookie");

                String passport;
                if (coolist != null) {
                    for (String val : coolist) {
                        if (val.startsWith("passport")) {
                            String cookeVal = coolist.get(coolist.size() - 1);
                            passport = cookeVal.substring(0, cookeVal.indexOf(";"));
                            SPUtils.saveString(ActivityMgr.getContext(), GlobalValue.KEY_COOKIE, passport);
                        }
                    }
                }

                is = conn.getInputStream();
                dis = new DataInputStream(is);
                byte[] responseData = getData(dis);
                rsp.data = responseData;
                MyLog.i(TAG, "httpPost() requestId = " + req.id + ", responseData: " + new String(responseData, "utf-8"));
                req.parseData(new String(responseData, "UTF-8"));
            }
        } catch (SocketTimeoutException e) {
            MyLog.i(TAG, e.toString());
            rsp.code = HttpRsp.CODE_TIMEOUT;
        } catch (UnknownHostException e) {
            MyLog.i(TAG, e.toString());
            rsp.code = HttpRsp.CODE_UNKNOW_HOST;
        } catch (IOException e) {
            MyLog.i(TAG, e.toString());
            rsp.code = HttpRsp.CODE_NET_ERROR;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            req.getOnHttpRsp().onHttpRsp(rsp);

            try {
                if (dis != null) {
                    dis.close();
                }
                if (is != null) {
                    is.close();
                }
                if (conn != null) {
                    conn.disconnect();
                }
                if (os != null) {
                    os.close();
                }
            } catch (Exception e) {
            } finally {
                dis = null;
                is = null;
                conn = null;
                os = null;
            }
        }
    }

    /**
     * 从流中获取未知长度的数据
     *
     * @param is 输入流
     * @return 返回对到从流中读取的数据
     * @throws IOException
     * @throws Exception
     */
    private byte[] getData(DataInputStream is) throws IOException {
        int dataLength = 0;
        byte[] buffer = new byte[4096];
        ByteArrayOutputStream bos = new ByteArrayOutputStream(buffer.length);
        int size = 0;
        while ((size = is.read(buffer)) != -1) {
            bos.write(buffer, 0, size);
            dataLength += size;
        }
        MyLog.i("getData", "read " + String.valueOf(dataLength) + " byte");
        return bos.toByteArray();
    }

    public final static HttpURLConnection getURLConnection(URL url, int systemConnection) throws IOException {
        String proxyHost = android.net.Proxy.getDefaultHost();
        if (NetworkUtils.TYPE_MOBILE_CTWAP == systemConnection && proxyHost != null) {
            java.net.Proxy p = new java.net.Proxy(java.net.Proxy.Type.HTTP,
                    new InetSocketAddress(android.net.Proxy.getDefaultHost(),
                            android.net.Proxy.getDefaultPort()));
            return (HttpURLConnection) url.openConnection(p);
        } else {
            return (HttpURLConnection) url.openConnection();
        }
    }
}