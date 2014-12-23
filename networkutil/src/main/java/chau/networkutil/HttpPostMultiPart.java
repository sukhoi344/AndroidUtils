package chau.networkutil;

import android.graphics.Bitmap;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import chau.networkutil.crypt.DecryptException;
import chau.networkutil.crypt.Decryptor;
import chau.networkutil.crypt.EncryptException;
import chau.networkutil.crypt.Encryptor;
import chau.networkutil.security.CustomSSLSocketFactory;
import chau.networkutil.security.CustomX509TrustManager;

/**
 * Created by chauthai on 12/23/14.
 */
public class HttpPostMultiPart {private static final long CONN_MGR_TIMEOUT = 20000;
    private static final int CONN_TIMEOUT = 60000;
    private static final int SO_TIMEOUT = 60000;

    private Encryptor encryptor = null;
    private Decryptor decryptor = null;

    private final String URL;
    private MultipartEntityBuilder builder;

    public HttpPostMultiPart(String url) {
        this.URL = url;
        builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
    }

    public boolean addParams(String name, String value) {
        try {
            if (encryptor != null)
                value = encryptor.encrypt(value);
            builder.addPart(name, new StringBody(value, ContentType.DEFAULT_TEXT));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean addFile(String name, File file) throws EncryptException {
        if (file != null) {
            FileBody fileBody = new FileBody(file);
            builder.addPart(name, fileBody);
        }

        return true;
    }

    public boolean addBitmap(String name, Bitmap bitmap) throws EncryptException {
        if(bitmap == null)
            return false;

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, bos);
        byte[] data = bos.toByteArray();

        if (encryptor != null)
            data = encryptor.encrypt(data);

        ByteArrayBody bab = new ByteArrayBody(data, ContentType.create("image/jpeg"), "image.jpg");
        builder.addPart(name, bab);

        return false;
    }

    public String getResponse() throws IOException, NoSuchAlgorithmException, KeyManagementException,
            UnrecoverableKeyException,
            KeyStoreException, DecryptException {

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(URL);

        httpPost.setEntity(builder.build());

        // ====================== Warning starts !! ========================
        SSLContext ctx = SSLContext.getInstance("TLS");
        ctx.init(null, new TrustManager[] { new CustomX509TrustManager() },
                new SecureRandom());

        SSLSocketFactory ssf = new CustomSSLSocketFactory(ctx);
        ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        ClientConnectionManager ccm = httpClient.getConnectionManager();
        SchemeRegistry sr = ccm.getSchemeRegistry();
        sr.register(new Scheme("https", ssf, 443));

        HttpParams params = httpClient.getParams();

        ConnManagerParams.setTimeout(params, CONN_MGR_TIMEOUT);
        HttpConnectionParams.setConnectionTimeout(params, CONN_TIMEOUT);
        HttpConnectionParams.setSoTimeout(params, SO_TIMEOUT);

        DefaultHttpClient sslClient = new DefaultHttpClient(ccm,  params);
        //  ====================== Warning ends !! =====================

        HttpResponse httpResponse = sslClient.execute(httpPost);
//        printResponse(httpResponse);

        String responseString = EntityUtils.toString(httpResponse.getEntity());

        if (decryptor != null) {
            responseString = decryptor.decrypt(responseString);
        }
//        Log.d("HttpPostMultipart", "HttpPost response: " + responseString);

        return responseString;
    }

    public void setEncryptor(Encryptor encryptor) {
        this.encryptor = encryptor;
    }

    public void setDecryptor(Decryptor decryptor) {
        this.decryptor = decryptor;
    }

    private void printResponse(HttpResponse response) {
        try {
            System.out.println(response.getStatusLine().getReasonPhrase());
            System.out.println(response.getStatusLine().getStatusCode());

            ByteArrayOutputStream outstream = new ByteArrayOutputStream();
            response.getEntity().writeTo(outstream);
            byte [] responseBody = outstream.toByteArray();

            System.out.println("printing!");
            System.out.println(new String(responseBody));

        } catch (Exception e ) {
            e.printStackTrace();
        }
    }

}
