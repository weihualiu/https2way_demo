package com.rytong.net;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.security.KeyStore;

import cn.liuweihua.app.https2way_demo.MainActivity;

public class SSLStore {

    private static final String KEY_STORE_TYPE_BKS = "BKS";
    private static final String KEY_STORE_TYPE_P12 = "PKCS12";

    public static KeyStore getKeyStore() {
        KeyStore keyStore = null;
        try{
            InputStream inputStream = new BufferedInputStream(MainActivity.getActivity().getAssets().open("client/client.p12"));
            keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(inputStream, "123456".toCharArray());
        }catch (Exception e){
            e.printStackTrace();
        }finally {

        }
        return keyStore;
    }

    public static KeyStore getTrustStore() {
        KeyStore keyStore = null;
        try{
            InputStream inputStream = new BufferedInputStream(MainActivity.getActivity().getAssets().open("client/client.truststore"));
            keyStore = KeyStore.getInstance("BKS");
            keyStore.load(inputStream, "123456".toCharArray());
        }catch (Exception e){
            e.printStackTrace();
        }finally {

        }
        return keyStore;
    }
}
