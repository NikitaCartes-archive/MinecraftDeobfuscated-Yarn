package net.minecraft;

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
public abstract class class_4346<T extends class_4346<T>> {
	protected HttpURLConnection field_19596;
	private boolean field_19598;
	protected String field_19597;

	public class_4346(String string, int i, int j) {
		try {
			this.field_19597 = string;
			Proxy proxy = class_4344.method_21034();
			if (proxy != null) {
				this.field_19596 = (HttpURLConnection)new URL(string).openConnection(proxy);
			} else {
				this.field_19596 = (HttpURLConnection)new URL(string).openConnection();
			}

			this.field_19596.setConnectTimeout(i);
			this.field_19596.setReadTimeout(j);
		} catch (MalformedURLException var5) {
			throw new class_4354(var5.getMessage(), var5);
		} catch (IOException var6) {
			throw new class_4354(var6.getMessage(), var6);
		}
	}

	public void method_21042(String string, String string2) {
		method_21046(this.field_19596, string, string2);
	}

	public static void method_21046(HttpURLConnection httpURLConnection, String string, String string2) {
		String string3 = httpURLConnection.getRequestProperty("Cookie");
		if (string3 == null) {
			httpURLConnection.setRequestProperty("Cookie", string + "=" + string2);
		} else {
			httpURLConnection.setRequestProperty("Cookie", string3 + ";" + string + "=" + string2);
		}
	}

	public int method_21038() {
		return method_21044(this.field_19596);
	}

	public static int method_21044(HttpURLConnection httpURLConnection) {
		String string = httpURLConnection.getHeaderField("Retry-After");

		try {
			return Integer.valueOf(string);
		} catch (Exception var3) {
			return 5;
		}
	}

	public int method_21047() {
		try {
			this.method_21054();
			return this.field_19596.getResponseCode();
		} catch (Exception var2) {
			throw new class_4354(var2.getMessage(), var2);
		}
	}

	public String method_21051() {
		try {
			this.method_21054();
			String string = null;
			if (this.method_21047() >= 400) {
				string = this.method_21039(this.field_19596.getErrorStream());
			} else {
				string = this.method_21039(this.field_19596.getInputStream());
			}

			this.method_21056();
			return string;
		} catch (IOException var2) {
			throw new class_4354(var2.getMessage(), var2);
		}
	}

	private String method_21039(InputStream inputStream) throws IOException {
		if (inputStream == null) {
			return "";
		} else {
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
			StringBuilder stringBuilder = new StringBuilder();

			for (int i = inputStreamReader.read(); i != -1; i = inputStreamReader.read()) {
				stringBuilder.append((char)i);
			}

			return stringBuilder.toString();
		}
	}

	private void method_21056() {
		byte[] bs = new byte[1024];

		try {
			int i = 0;
			InputStream inputStream = this.field_19596.getInputStream();

			while (inputStream.read(bs) > 0) {
			}

			inputStream.close();
			return;
		} catch (Exception var10) {
			try {
				InputStream inputStream = this.field_19596.getErrorStream();
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
			if (this.field_19596 != null) {
				this.field_19596.disconnect();
			}
		}
	}

	protected T method_21054() {
		if (this.field_19598) {
			return (T)this;
		} else {
			T lv = this.method_21055();
			this.field_19598 = true;
			return lv;
		}
	}

	protected abstract T method_21055();

	public static class_4346<?> method_21040(String string) {
		return new class_4346.class_4348(string, 5000, 60000);
	}

	public static class_4346<?> method_21041(String string, int i, int j) {
		return new class_4346.class_4348(string, i, j);
	}

	public static class_4346<?> method_21049(String string, String string2) {
		return new class_4346.class_4349(string, string2, 5000, 60000);
	}

	public static class_4346<?> method_21043(String string, String string2, int i, int j) {
		return new class_4346.class_4349(string, string2, i, j);
	}

	public static class_4346<?> method_21048(String string) {
		return new class_4346.class_4347(string, 5000, 60000);
	}

	public static class_4346<?> method_21053(String string, String string2) {
		return new class_4346.class_4350(string, string2, 5000, 60000);
	}

	public static class_4346<?> method_21050(String string, String string2, int i, int j) {
		return new class_4346.class_4350(string, string2, i, j);
	}

	public String method_21052(String string) {
		return method_21045(this.field_19596, string);
	}

	public static String method_21045(HttpURLConnection httpURLConnection, String string) {
		try {
			return httpURLConnection.getHeaderField(string);
		} catch (Exception var3) {
			return "";
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_4347 extends class_4346<class_4346.class_4347> {
		public class_4347(String string, int i, int j) {
			super(string, i, j);
		}

		public class_4346.class_4347 method_21057() {
			try {
				this.field_19596.setDoOutput(true);
				this.field_19596.setRequestMethod("DELETE");
				this.field_19596.connect();
				return this;
			} catch (Exception var2) {
				throw new class_4354(var2.getMessage(), var2);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_4348 extends class_4346<class_4346.class_4348> {
		public class_4348(String string, int i, int j) {
			super(string, i, j);
		}

		public class_4346.class_4348 method_21058() {
			try {
				this.field_19596.setDoInput(true);
				this.field_19596.setDoOutput(true);
				this.field_19596.setUseCaches(false);
				this.field_19596.setRequestMethod("GET");
				return this;
			} catch (Exception var2) {
				throw new class_4354(var2.getMessage(), var2);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_4349 extends class_4346<class_4346.class_4349> {
		private final String field_19599;

		public class_4349(String string, String string2, int i, int j) {
			super(string, i, j);
			this.field_19599 = string2;
		}

		public class_4346.class_4349 method_21059() {
			try {
				if (this.field_19599 != null) {
					this.field_19596.setRequestProperty("Content-Type", "application/json; charset=utf-8");
				}

				this.field_19596.setDoInput(true);
				this.field_19596.setDoOutput(true);
				this.field_19596.setUseCaches(false);
				this.field_19596.setRequestMethod("POST");
				OutputStream outputStream = this.field_19596.getOutputStream();
				OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");
				outputStreamWriter.write(this.field_19599);
				outputStreamWriter.close();
				outputStream.flush();
				return this;
			} catch (Exception var3) {
				throw new class_4354(var3.getMessage(), var3);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_4350 extends class_4346<class_4346.class_4350> {
		private final String field_19600;

		public class_4350(String string, String string2, int i, int j) {
			super(string, i, j);
			this.field_19600 = string2;
		}

		public class_4346.class_4350 method_21060() {
			try {
				if (this.field_19600 != null) {
					this.field_19596.setRequestProperty("Content-Type", "application/json; charset=utf-8");
				}

				this.field_19596.setDoOutput(true);
				this.field_19596.setDoInput(true);
				this.field_19596.setRequestMethod("PUT");
				OutputStream outputStream = this.field_19596.getOutputStream();
				OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");
				outputStreamWriter.write(this.field_19600);
				outputStreamWriter.close();
				outputStream.flush();
				return this;
			} catch (Exception var3) {
				throw new class_4354(var3.getMessage(), var3);
			}
		}
	}
}
