package net.minecraft;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3389 extends class_3359 {
	private static final Logger field_14491 = LogManager.getLogger();
	private boolean field_14488;
	private Socket field_14489;
	private final byte[] field_14490 = new byte[1460];
	private final String field_14492;

	class_3389(class_2994 arg, String string, Socket socket) {
		super(arg, "RCON Client");
		this.field_14489 = socket;

		try {
			this.field_14489.setSoTimeout(0);
		} catch (Exception var5) {
			this.field_14431 = false;
		}

		this.field_14492 = string;
		this.method_14729("Rcon connection from: " + socket.getInetAddress());
	}

	public void run() {
		try {
			try {
				while (this.field_14431) {
					BufferedInputStream bufferedInputStream = new BufferedInputStream(this.field_14489.getInputStream());
					int i = bufferedInputStream.read(this.field_14490, 0, 1460);
					if (10 > i) {
						return;
					}

					int j = 0;
					int k = class_3347.method_14696(this.field_14490, 0, i);
					if (k != i - 4) {
						return;
					}

					j += 4;
					int l = class_3347.method_14696(this.field_14490, j, i);
					j += 4;
					int m = class_3347.method_14695(this.field_14490, j);
					j += 4;
					switch (m) {
						case 2:
							if (this.field_14488) {
								String string2 = class_3347.method_14697(this.field_14490, j, i);

								try {
									this.method_14789(l, this.field_14425.method_12934(string2));
								} catch (Exception var16) {
									this.method_14789(l, "Error executing: " + string2 + " (" + var16.getMessage() + ")");
								}
								break;
							}

							this.method_14787();
							break;
						case 3:
							String string = class_3347.method_14697(this.field_14490, j, i);
							j += string.length();
							if (!string.isEmpty() && string.equals(this.field_14492)) {
								this.field_14488 = true;
								this.method_14790(l, 2, "");
								break;
							}

							this.field_14488 = false;
							this.method_14787();
							break;
						default:
							this.method_14789(l, String.format("Unknown request %s", Integer.toHexString(m)));
					}
				}

				return;
			} catch (SocketTimeoutException var17) {
			} catch (IOException var18) {
			} catch (Exception var19) {
				field_14491.error("Exception whilst parsing RCON input", (Throwable)var19);
			}
		} finally {
			this.method_14788();
		}
	}

	private void method_14790(int i, int j, String string) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1248);
		DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
		byte[] bs = string.getBytes("UTF-8");
		dataOutputStream.writeInt(Integer.reverseBytes(bs.length + 10));
		dataOutputStream.writeInt(Integer.reverseBytes(i));
		dataOutputStream.writeInt(Integer.reverseBytes(j));
		dataOutputStream.write(bs);
		dataOutputStream.write(0);
		dataOutputStream.write(0);
		this.field_14489.getOutputStream().write(byteArrayOutputStream.toByteArray());
	}

	private void method_14787() throws IOException {
		this.method_14790(-1, 2, "");
	}

	private void method_14789(int i, String string) throws IOException {
		int j = string.length();

		do {
			int k = 4096 <= j ? 4096 : j;
			this.method_14790(i, 0, string.substring(0, k));
			string = string.substring(k);
			j = string.length();
		} while (0 != j);
	}

	@Override
	public void method_18050() {
		super.method_18050();
		this.method_14788();
	}

	private void method_14788() {
		if (null != this.field_14489) {
			try {
				this.field_14489.close();
			} catch (IOException var2) {
				this.method_14727("IO: " + var2.getMessage());
			}

			this.field_14489 = null;
		}
	}
}
