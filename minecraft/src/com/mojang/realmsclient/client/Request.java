package com.mojang.realmsclient.client;

import com.mojang.realmsclient.exception.RealmsHttpException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class Request<T extends Request<T>> {
	protected HttpURLConnection connection;
	private boolean connected;
	protected String url;

	public Request(String url, int connectTimeout, int readTimeout) {
		try {
			this.url = url;
			Proxy proxy = RealmsClientConfig.getProxy();
			if (proxy != null) {
				this.connection = (HttpURLConnection)new URL(url).openConnection(proxy);
			} else {
				this.connection = (HttpURLConnection)new URL(url).openConnection();
			}

			this.connection.setConnectTimeout(connectTimeout);
			this.connection.setReadTimeout(readTimeout);
		} catch (MalformedURLException var5) {
			throw new RealmsHttpException(var5.getMessage(), var5);
		} catch (IOException var6) {
			throw new RealmsHttpException(var6.getMessage(), var6);
		}
	}

	public void cookie(String key, String value) {
		cookie(this.connection, key, value);
	}

	public static void cookie(HttpURLConnection connection, String key, String value) {
		String string = connection.getRequestProperty("Cookie");
		if (string == null) {
			connection.setRequestProperty("Cookie", key + "=" + value);
		} else {
			connection.setRequestProperty("Cookie", string + ";" + key + "=" + value);
		}
	}

	public int getRetryAfterHeader() {
		return getRetryAfterHeader(this.connection);
	}

	public static int getRetryAfterHeader(HttpURLConnection connection) {
		String string = connection.getHeaderField("Retry-After");

		try {
			return Integer.valueOf(string);
		} catch (Exception var3) {
			return 5;
		}
	}

	public int responseCode() {
		try {
			this.connect();
			return this.connection.getResponseCode();
		} catch (Exception var2) {
			throw new RealmsHttpException(var2.getMessage(), var2);
		}
	}

	public String text() {
		try {
			this.connect();
			String string = null;
			if (this.responseCode() >= 400) {
				string = this.read(this.connection.getErrorStream());
			} else {
				string = this.read(this.connection.getInputStream());
			}

			this.dispose();
			return string;
		} catch (IOException var2) {
			throw new RealmsHttpException(var2.getMessage(), var2);
		}
	}

	private String read(InputStream in) throws IOException {
		if (in == null) {
			return "";
		} else {
			InputStreamReader inputStreamReader = new InputStreamReader(in, "UTF-8");
			StringBuilder stringBuilder = new StringBuilder();

			for (int i = inputStreamReader.read(); i != -1; i = inputStreamReader.read()) {
				stringBuilder.append((char)i);
			}

			return stringBuilder.toString();
		}
	}

	private void dispose() {
		byte[] bs = new byte[1024];

		try {
			int i = 0;
			InputStream inputStream = this.connection.getInputStream();

			while (inputStream.read(bs) > 0) {
			}

			inputStream.close();
			return;
		} catch (Exception var10) {
			try {
				InputStream inputStream = this.connection.getErrorStream();
				int j = 0;
				if (inputStream != null) {
					while (inputStream.read(bs) > 0) {
					}

					inputStream.close();
					return;
				}
			} catch (IOException var9) {
				return;
			}
		} finally {
			if (this.connection != null) {
				this.connection.disconnect();
			}
		}
	}

	protected T connect() {
		if (this.connected) {
			return (T)this;
		} else {
			T request = this.doConnect();
			this.connected = true;
			return request;
		}
	}

	protected abstract T doConnect();

	public static Request<?> get(String url) {
		return new Request.Get(url, 5000, 60000);
	}

	public static Request<?> get(String url, int connectTimeoutMillis, int readTimeoutMillis) {
		return new Request.Get(url, connectTimeoutMillis, readTimeoutMillis);
	}

	public static Request<?> post(String uri, String content) {
		return new Request.Post(uri, content, 5000, 60000);
	}

	public static Request<?> post(String uri, String content, int connectTimeoutMillis, int readTimeoutMillis) {
		return new Request.Post(uri, content, connectTimeoutMillis, readTimeoutMillis);
	}

	public static Request<?> delete(String url) {
		return new Request.Delete(url, 5000, 60000);
	}

	public static Request<?> put(String url, String content) {
		return new Request.Put(url, content, 5000, 60000);
	}

	public static Request<?> put(String url, String content, int connectTimeoutMillis, int readTimeoutMillis) {
		return new Request.Put(url, content, connectTimeoutMillis, readTimeoutMillis);
	}

	public String getHeader(String header) {
		return getHeader(this.connection, header);
	}

	public static String getHeader(HttpURLConnection connection, String header) {
		try {
			return connection.getHeaderField(header);
		} catch (Exception var3) {
			return "";
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Delete extends Request<Request.Delete> {
		public Delete(String uri, int connectTimeout, int readTimeout) {
			super(uri, connectTimeout, readTimeout);
		}

		public Request.Delete method_21057() {
			try {
				this.connection.setDoOutput(true);
				this.connection.setRequestMethod("DELETE");
				this.connection.connect();
				return this;
			} catch (Exception var2) {
				throw new RealmsHttpException(var2.getMessage(), var2);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Get extends Request<Request.Get> {
		public Get(String uri, int connectTimeout, int readTimeout) {
			super(uri, connectTimeout, readTimeout);
		}

		public Request.Get method_21058() {
			try {
				this.connection.setDoInput(true);
				this.connection.setDoOutput(true);
				this.connection.setUseCaches(false);
				this.connection.setRequestMethod("GET");
				return this;
			} catch (Exception var2) {
				throw new RealmsHttpException(var2.getMessage(), var2);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Post extends Request<Request.Post> {
		private final String content;

		public Post(String uri, String content, int connectTimeout, int readTimeout) {
			super(uri, connectTimeout, readTimeout);
			this.content = content;
		}

		public Request.Post method_21059() {
			try {
				if (this.content != null) {
					this.connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
				}

				this.connection.setDoInput(true);
				this.connection.setDoOutput(true);
				this.connection.setUseCaches(false);
				this.connection.setRequestMethod("POST");
				OutputStream outputStream = this.connection.getOutputStream();
				OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");
				outputStreamWriter.write(this.content);
				outputStreamWriter.close();
				outputStream.flush();
				return this;
			} catch (Exception var3) {
				throw new RealmsHttpException(var3.getMessage(), var3);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Put extends Request<Request.Put> {
		private final String content;

		public Put(String uri, String content, int connectTimeout, int readTimeout) {
			super(uri, connectTimeout, readTimeout);
			this.content = content;
		}

		public Request.Put method_21060() {
			try {
				if (this.content != null) {
					this.connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
				}

				this.connection.setDoOutput(true);
				this.connection.setDoInput(true);
				this.connection.setRequestMethod("PUT");
				OutputStream outputStream = this.connection.getOutputStream();
				OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");
				outputStreamWriter.write(this.content);
				outputStreamWriter.close();
				outputStream.flush();
				return this;
			} catch (Exception var3) {
				throw new RealmsHttpException(var3.getMessage(), var3);
			}
		}
	}
}