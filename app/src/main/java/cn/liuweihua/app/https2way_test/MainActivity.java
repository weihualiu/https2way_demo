package cn.liuweihua.app.https2way_test;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.rytong.net.NetUtils;
import com.rytong.net.TrustAllCerts;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import java.net.URI;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends Activity implements View.OnClickListener {

    private static Activity mActivity;
    protected static final int SHOW_RESPONSE = 0;
    protected static final int SHOW_RESPONSE2 = 1;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mActivity = this;

        Button button = findViewById(R.id.send);
        button.setOnClickListener(this);
        Button okhttpButton = findViewById(R.id.okhttp_send);
        okhttpButton.setOnClickListener(this);
        textView = findViewById(R.id.show);
    }

    public static Activity getActivity() {
        return mActivity;
    }

    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SHOW_RESPONSE :
                    String response2 = (String)msg.obj;
                    textView.setText(response2);
            }
        }
    };

    public void send() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpClient httpClient = NetUtils.getNewHttpClient();
                    HttpGet httpGet = new HttpGet();
                    httpGet.setURI(new URI("https://tesp.bankcomm.com:4002"));
                    HttpResponse httpResponse = httpClient.execute(httpGet);
                    if(httpResponse.getStatusLine().getStatusCode() == 200){
                        HttpEntity httpEntity = httpResponse.getEntity();
                        String content = EntityUtils.toString(httpEntity);
                        Message message = new Message();
                        message.what = 0;
                        message.obj = content;
                        handler.sendMessage(message);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }finally {

                }
            }
        }).start();
    }

    public void okhttp_send() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient.Builder()
                            .connectionSpecs(Arrays.asList(ConnectionSpec.COMPATIBLE_TLS))
                            .sslSocketFactory(TrustAllCerts.createSSLSocketFactory(), new TrustAllCerts())
                            .hostnameVerifier(new TrustAllCerts.TrustAllHostnameVerifier())
                            .build();
                    Request request = new Request.Builder()
                            .url("https://tesp.bankcomm.com:4002")
                            .build();
                    Call call = client.newCall(request);
                    Response response = call.execute();
                    if(response.code() == 200){
                        Message message = new Message();
                        message.what = 0;
                        message.obj = response.body().string();
                        handler.sendMessage(message);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }finally {

                }
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.send:
                send();
                break;
            case R.id.okhttp_send:
                okhttp_send();
                break;
            default:
                break;
        }
    }
}
