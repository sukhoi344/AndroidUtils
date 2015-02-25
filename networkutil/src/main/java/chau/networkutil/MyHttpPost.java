package chau.networkutil;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import chau.networkutil.security.CustomSSLSocketFactory;
import chau.networkutil.security.CustomX509TrustManager;

/**
 * Created by chauthai on 12/23/14.
 */
public class MyHttpPost {
    private static final long CONN_MGR_TIMEOUT = 10000;
    private static final int CONN_TIMEOUT = 30000;
    private static final int SO_TIMEOUT = 30000;

    private final String URL;
    private List<NameValuePair> nameValuePairs = new ArrayList<>();


    public MyHttpPost(String url) {
        this.URL = url;
    }

    /** Add name and value param to POST request  */
    public void addParams(String name, String value)  {
        nameValuePairs.add(new BasicNameValuePair(name, value));
    }

    /** Execute Http POST request and return the respond data */
    public String getResponse() throws IOException, NoSuchAlgorithmException, KeyManagementException,
            UnrecoverableKeyException,
            KeyStoreException
    {
        SSLContext ctx = SSLContext.getInstance("TLS");
        ctx.init(null, new TrustManager[] { new CustomX509TrustManager() },
                new SecureRandom());

        HttpClient client = new DefaultHttpClient();
        org.apache.http.client.methods.HttpPost httppost = new org.apache.http.client.methods.HttpPost(URL);

        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));

//        printRequest(httppost);

        SSLSocketFactory ssf = new CustomSSLSocketFactory(ctx);
        ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        ClientConnectionManager ccm = client.getConnectionManager();
        SchemeRegistry sr = ccm.getSchemeRegistry();
        sr.register(new Scheme("https", ssf, 443));

        HttpParams params = client.getParams();

        ConnManagerParams.setTimeout(params, CONN_MGR_TIMEOUT);
        HttpConnectionParams.setConnectionTimeout(params, CONN_TIMEOUT);
        HttpConnectionParams.setSoTimeout(params, SO_TIMEOUT);

        DefaultHttpClient sslClient = new DefaultHttpClient(ccm,  params);

        HttpResponse response = sslClient.execute(httppost);

        String responseString = EntityUtils.toString(response.getEntity());

//        Log.d("HttpPost", "HttpPost response: " + responseString);

        return responseString;
    }

    private void printRequest(HttpPost httpPost) {
        try {
            Log.d("HttpPost", URL + "&" + EntityUtils.toString(httpPost.getEntity()));
        } catch (Exception e) { }
    }

}
