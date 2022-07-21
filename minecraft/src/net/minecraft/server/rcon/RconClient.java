package net.minecraft.server.rcon;

import com.mojang.logging.LogUtils;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import net.minecraft.server.dedicated.DedicatedServer;
import org.slf4j.Logger;

public class RconClient extends RconBase {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final int field_29799 = 3;
	private static final int field_29800 = 2;
	private static final int field_29801 = 0;
	private static final int field_29802 = 2;
	private static final int field_29803 = -1;
	private boolean authenticated;
	private final Socket socket;
	private final byte[] packetBuffer = new byte[1460];
	private final String password;
	private final DedicatedServer server;

	RconClient(DedicatedServer server, String password, Socket socket) {
		super("RCON Client " + socket.getInetAddress());
		this.server = server;
		this.socket = socket;

		try {
			this.socket.setSoTimeout(0);
		} catch (Exception var5) {
			this.running = false;
		}

		this.password = password;
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
									this.respond(l, this.server.executeRconCommand(string2));
								} catch (Exception var15) {
									this.respond(l, "Error executing: " + string2 + " (" + var15.getMessage() + ")");
								}
								break;
							}

							this.fail();
							break;
						case 3:
							String string = BufferHelper.getString(this.packetBuffer, j, i);
							j += string.length();
							if (!string.isEmpty() && string.equals(this.password)) {
								this.authenticated = true;
								this.respond(l, 2, "");
								break;
							}

							this.authenticated = false;
							this.fail();
							break;
						default:
							this.respond(l, String.format(Locale.ROOT, "Unknown request %s", Integer.toHexString(m)));
					}
				}

				return;
			} catch (IOException var16) {
			} catch (Exception var17) {
				LOGGER.error("Exception whilst parsing RCON input", (Throwable)var17);
			}
		} finally {
			this.close();
			LOGGER.info("Thread {} shutting down", this.description);
			this.running = false;
		}
	}

	private void respond(int sessionToken, int responseType, String message) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1248);
		DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
		byte[] bs = message.getBytes(StandardCharsets.UTF_8);
		dataOutputStream.writeInt(Integer.reverseBytes(bs.length + 10));
		dataOutputStream.writeInt(Integer.reverseBytes(sessionToken));
		dataOutputStream.writeInt(Integer.reverseBytes(responseType));
		dataOutputStream.write(bs);
		dataOutputStream.write(0);
		dataOutputStream.write(0);
		this.socket.getOutputStream().write(byteArrayOutputStream.toByteArray());
	}

	private void fail() throws IOException {
		this.respond(-1, 2, "");
	}

	private void respond(int sessionToken, String message) throws IOException {
		int i = message.length();

		do {
			int j = 4096 <= i ? 4096 : i;
			this.respond(sessionToken, 0, message.substring(0, j));
			message = message.substring(j);
			i = message.length();
		} while (0 != i);
	}

	@Override
	public void stop() {
		this.running = false;
		this.close();
		super.stop();
	}

	private void close() {
		try {
			this.socket.close();
		} catch (IOException var2) {
			LOGGER.warn("Failed to close socket", (Throwable)var2);
		}
	}
}
