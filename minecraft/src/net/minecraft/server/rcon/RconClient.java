package net.minecraft.server.rcon;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import net.minecraft.server.dedicated.DedicatedServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RconClient extends RconBase {
	private static final Logger LOGGER = LogManager.getLogger();
	private boolean authenticated;
	private Socket socket;
	private final byte[] packetBuffer = new byte[1460];
	private final String password;

	RconClient(DedicatedServer dedicatedServer, String string, Socket socket) {
		super(dedicatedServer, "RCON Client");
		this.socket = socket;

		try {
			this.socket.setSoTimeout(0);
		} catch (Exception var5) {
			this.running = false;
		}

		this.password = string;
		this.info("Rcon connection from: " + socket.getInetAddress());
	}

	public void run() {
		try {
			try {
				while (this.running) {
					BufferedInputStream bufferedInputStream = new BufferedInputStream(this.socket.getInputStream());
					int i = bufferedInputStream.read(this.packetBuffer, 0, 1460);
					if (10 > i) {
						return;
					}

					int j = 0;
					int k = BufferHelper.getIntLE(this.packetBuffer, 0, i);
					if (k != i - 4) {
						return;
					}

					j += 4;
					int l = BufferHelper.getIntLE(this.packetBuffer, j, i);
					j += 4;
					int m = BufferHelper.getIntLE(this.packetBuffer, j);
					j += 4;
					switch (m) {
						case 2:
							if (this.authenticated) {
								String string2 = BufferHelper.getString(this.packetBuffer, j, i);

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
							String string = BufferHelper.getString(this.packetBuffer, j, i);
							j += string.length();
							if (!string.isEmpty() && string.equals(this.password)) {
								this.authenticated = true;
								this.method_14790(l, 2, "");
								break;
							}

							this.authenticated = false;
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
				LOGGER.error("Exception whilst parsing RCON input", (Throwable)var19);
			}
		} finally {
			this.close();
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
		this.socket.getOutputStream().write(byteArrayOutputStream.toByteArray());
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

	private void close() {
		if (null != this.socket) {
			try {
				this.socket.close();
			} catch (IOException var2) {
				this.warn("IO: " + var2.getMessage());
			}

			this.socket = null;
		}
	}
}
