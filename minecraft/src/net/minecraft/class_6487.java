package net.minecraft;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.ClosedByInterruptException;
import java.util.List;
import java.util.Locale;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_6487 {
	private static final Logger field_34360 = LogManager.getLogger();
	private final int field_34361;
	private final PlayerManager field_34362;
	private final int field_34363;
	private boolean field_34364 = false;
	private ServerSocket field_34365;

	public class_6487(int i, PlayerManager playerManager, int j) {
		this.field_34361 = i;
		this.field_34362 = playerManager;
		this.field_34363 = j;
	}

	public void method_37879() throws IOException {
		if (this.field_34365 != null && !this.field_34365.isClosed()) {
			field_34360.warn("Remote control server was asked to start, but it is already running. Will ignore.");
		} else {
			this.field_34364 = true;
			this.field_34365 = new ServerSocket(this.field_34361);
			new Thread(this::method_37884).start();
		}
	}

	public void method_37882() {
		this.field_34364 = false;
		if (this.field_34365 != null) {
			try {
				this.field_34365.close();
			} catch (IOException var2) {
				field_34360.error("Failed to close remote control server socket", (Throwable)var2);
			}

			this.field_34365 = null;
		}
	}

	public void method_37884() {
		try {
			while (this.field_34364) {
				field_34360.info("Remote control server is listening for connections on port " + this.field_34361);
				Socket socket = this.field_34365.accept();
				field_34360.info("Remote control server received client connection on port " + socket.getPort());
				new Thread(() -> this.method_37881(socket)).start();
			}
		} catch (ClosedByInterruptException var12) {
			if (this.field_34364) {
				field_34360.info("Remote control server closed by interrupt");
			}
		} catch (IOException var13) {
			if (this.field_34364) {
				field_34360.error("Remote control server closed because of an IO exception", (Throwable)var13);
			}
		} finally {
			if (this.field_34365 != null && !this.field_34365.isClosed()) {
				try {
					this.field_34365.close();
				} catch (IOException var11) {
					field_34360.warn("Failed to close remote control server socket", (Throwable)var11);
				}
			}
		}

		field_34360.info("Remote control server is now stopped");
		this.field_34364 = false;
	}

	private void method_37881(Socket socket) {
		try {
			DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

			while (this.field_34364) {
				Thread.sleep((long)this.field_34363);
				this.method_37880(dataOutputStream);
			}
		} catch (InterruptedException var13) {
			field_34360.info("Remote control client broadcast socket was interrupted and will be closed");
		} catch (IOException var14) {
			field_34360.info("Remote control client broadcast socket got an IO exception and will be closed", (Throwable)var14);
		} finally {
			try {
				if (!socket.isClosed()) {
					socket.close();
				}
			} catch (IOException var12) {
				field_34360.warn("Failed to close remote control client socket", (Throwable)var12);
			}
		}

		field_34360.info("Closed connection to remote control client");
	}

	private void method_37880(DataOutputStream dataOutputStream) throws IOException {
		List<ServerPlayerEntity> list = this.field_34362.getPlayerList();
		if (!list.isEmpty()) {
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)list.get(0);
			String string = String.format(
				Locale.ROOT,
				"/execute in %s run tp @s %.2f %.2f %.2f %.2f %.2f",
				serverPlayerEntity.world.getRegistryKey().getValue(),
				serverPlayerEntity.getX(),
				serverPlayerEntity.getY(),
				serverPlayerEntity.getZ(),
				serverPlayerEntity.getYaw(),
				serverPlayerEntity.getPitch()
			);
			dataOutputStream.writeUTF(string);
		}
	}
}
