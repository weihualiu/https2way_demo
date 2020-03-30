package com.rytong.net;


import org.apache.http.conn.ssl.SSLSocketFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import cn.liuweihua.app.https2way_demo.MainActivity;

/**
 * 证书绑定使用的SSLSocketFactory
 * Created by rytong on 2019/4/17.
 */

public class SSLBindSocketFactoryEx extends SSLSocketFactory {
    SSLContext mSSLContext = SSLContext.getInstance("TLS");
    private X509Certificate x509Certificate = null;

    public SSLBindSocketFactoryEx(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
        super(truststore);

        TrustManager tm = new X509TrustManager() {

            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType)
                    throws CertificateException {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType)
                    throws CertificateException {
                //这里会返回证书链内容，角标从小到大顺序为：网站证书到根证书
                //此处只进行网站证书校验
                if (chain != null && chain.length >= 1){
                    X509Certificate x509Certificate = chain[0];
                    x509Certificate.checkValidity();

                    try {
                        //证书链中的第一个证书由用户所信任的CA颁布()
                        x509Certificate.verify(getRootCertificate().getPublicKey());
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                        throw new CertificateException("verify NoSuchAlgorithmException");
                    } catch (InvalidKeyException e) {
                        e.printStackTrace();
                        throw new CertificateException("verify InvalidKeyException");
                    } catch (NoSuchProviderException e) {
                        e.printStackTrace();
                        throw new CertificateException("verify NoSuchProviderException");
                    } catch (SignatureException e) {
                        e.printStackTrace();
                        throw new CertificateException("verify SignatureException");
                    }catch (CertificateException e){
                        throw new CertificateException("证书不合法");
                    }
                }
            }
        };

        mSSLContext.init(null, new TrustManager[] { tm }, null);
    }

    /**
     * 获取校验证书
     * @return
     */
    public X509Certificate getRootCertificate() {
        if (x509Certificate == null){
//            Activity activity = AndroidEMPBuilder.getActivity(AndroidResources.getInstance().getEMPRender());
            try {
                InputStream crtInputStream = new BufferedInputStream(MainActivity.getActivity().getAssets().open("client/client.bks"));
                CertificateFactory factory = CertificateFactory.getInstance("X.509");
                x509Certificate = (X509Certificate) factory.generateCertificate(crtInputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }catch (CertificateException e) {
                e.printStackTrace();
            }
        }
        return x509Certificate;
    }

    @Override
    public Socket createSocket() throws IOException {
        return mSSLContext.getSocketFactory().createSocket();
    }

    @Override
    public Socket createSocket(Socket socket, String host, int port,
                               boolean autoClose) throws IOException, UnknownHostException {
        return mSSLContext.getSocketFactory().createSocket(socket, host, port, autoClose);
    }
}
