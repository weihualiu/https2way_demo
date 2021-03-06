package com.rytong.net;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ssl.SSLSocketFactory;

public class SSLSocketFactoryEx extends SSLSocketFactory {

	private SSLSocketFactory delegate;

	SSLContext mSSLContext = SSLContext.getInstance("TLS");

	public SSLSocketFactoryEx(KeyStore keyStore, String keyStorePasswd, KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException,
			KeyStoreException, UnrecoverableKeyException {
		super(keyStore, keyStorePasswd, truststore);

		TrustManager tm = new X509TrustManager() {

			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			@Override
			public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
					throws java.security.cert.CertificateException {

			}

			@Override
			public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
					throws java.security.cert.CertificateException {

			}
		};

		mSSLContext.init(null, new TrustManager[] { tm }, null);
	}

	@Override
	public Socket createSocket(Socket socket, String host, int port,
			boolean autoClose) throws IOException, UnknownHostException {

		SSLSocket sslSocket = (SSLSocket)mSSLContext.getSocketFactory().createSocket(socket, host, port, autoClose);
		sslSocket.setEnabledProtocols(new String[]{"TLSv1.1", "TLSv1.2"});
		return sslSocket;
	}

	@Override
	public Socket createSocket() throws IOException {
		SSLSocket sslSocket = (SSLSocket) mSSLContext.getSocketFactory().createSocket();
		sslSocket.setEnabledProtocols(new String[]{"TLSv1.1", "TLSv1.2"});
		return sslSocket;
	}
}
