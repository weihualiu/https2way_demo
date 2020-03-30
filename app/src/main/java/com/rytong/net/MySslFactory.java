package com.rytong.net;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class MySslFactory {
        private static final String KEY_STORE_TYPE_BKS = "bks";//证书类型
        private static final String KEY_STORE_TYPE_P12 = "PKCS12";//证书类型


        private static final String KEY_STORE_PASSWORD = "123456";//证书密码（应该是客户端证书密码）
        private static final String KEY_STORE_TRUST_PASSWORD = "123456";//授信证书密码（应该是服务端证书密码）

        public static SSLSocketFactory getSocketFactory(Context context) {


            try {
                InputStream trust_input = context.getResources().getAssets().open("client/client.bks");//服务器授信证书
                InputStream client_input = context.getResources().getAssets().open("client/client.truststore");//客户端证书

                SSLContext sslContext = SSLContext.getInstance("TLS");
                KeyStore trustStore = KeyStore.getInstance(KEY_STORE_TYPE_BKS);
                trustStore.load(trust_input, KEY_STORE_TRUST_PASSWORD.toCharArray());
                KeyStore keyStore = KeyStore.getInstance(KEY_STORE_TYPE_BKS);
                keyStore.load(client_input, KEY_STORE_PASSWORD.toCharArray());
                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init(trustStore);

                KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                keyManagerFactory.init(keyStore, KEY_STORE_PASSWORD.toCharArray());
                sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());
                SSLSocketFactory factory = sslContext.getSocketFactory();
                try {
                    trust_input.close();
                    client_input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return factory;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {

            }
        }
}
