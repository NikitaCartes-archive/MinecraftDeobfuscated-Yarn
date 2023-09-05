package net.minecraft.server.network;

import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import javax.annotation.Nullable;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.listener.ServerCommonPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.common.CommonPongC2SPacket;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.network.packet.c2s.common.KeepAliveC2SPacket;
import net.minecraft.network.packet.c2s.common.ResourcePackStatusC2SPacket;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.network.packet.s2c.common.DisconnectS2CPacket;
import net.minecraft.network.packet.s2c.common.KeepAliveS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.annotation.Debug;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import org.slf4j.Logger;

public abstract class ServerCommonNetworkHandler implements ServerCommonPacketListener {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final int KEEP_ALIVE_INTERVAL = 15000;
	private static final Text TIMEOUT_TEXT = Text.translatable("disconnect.timeout");
	protected final MinecraftServer server;
	protected final ClientConnection connection;
	private long lastKeepAliveTime;
	private boolean waitingForKeepAlive;
	private long keepAliveId;
	private int latency;
	private volatile boolean flushDisabled = false;

	public ServerCommonNetworkHandler(MinecraftServer server, ClientConnection connection, ConnectedClientData clientData) {
		this.server = server;
		this.connection = connection;
		this.lastKeepAliveTime = Util.getMeasuringTimeMs();
		this.latency = clientData.latency();
	}

	@Override
	public void onDisconnected(Text reason) {
		if (this.isHost()) {
			LOGGER.info("Stopping singleplayer server as player logged out");
			this.server.stop(false);
		}
	}

	@Override
	public void onKeepAlive(KeepAliveC2SPacket packet) {
		if (this.waitingForKeepAlive && packet.getId() == this.keepAliveId) {
			int i = (int)(Util.getMeasuringTimeMs() - this.lastKeepAliveTime);
			this.latency = (this.latency * 3 + i) / 4;
			this.waitingForKeepAlive = false;
		} else if (!this.isHost()) {
			this.disconnect(TIMEOUT_TEXT);
		}
	}

	@Override
	public void onPong(CommonPongC2SPacket packet) {
	}

	@Override
	public void onCustomPayload(CustomPayloadC2SPacket packet) {
	}

	@Override
	public void onResourcePackStatus(ResourcePackStatusC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.server);
		if (packet.getStatus() == ResourcePackStatusC2SPacket.Status.DECLINED && this.server.requireResourcePack()) {
			LOGGER.info("Disconnecting {} due to resource pack rejection", this.getProfile().getName());
			this.disconnect(Text.translatable("multiplayer.requiredTexturePrompt.disconnect"));
		}
	}

	protected void baseTick() {
		this.server.getProfiler().push("keepAlive");
		long l = Util.getMeasuringTimeMs();
		if (l - this.lastKeepAliveTime >= 15000L) {
			if (this.waitingForKeepAlive) {
				this.disconnect(TIMEOUT_TEXT);
			} else {
				this.waitingForKeepAlive = true;
				this.lastKeepAliveTime = l;
				this.keepAliveId = l;
				this.sendPacket(new KeepAliveS2CPacket(this.keepAliveId));
			}
		}

		this.server.getProfiler().pop();
	}

	public void disableFlush() {
		this.flushDisabled = true;
	}

	public void enableFlush() {
		this.flushDisabled = false;
		this.connection.flush();
	}

	public void sendPacket(Packet<?> packet) {
		this.send(packet, null);
	}

	public void send(Packet<?> packet, @Nullable PacketCallbacks callbacks) {
		boolean bl = !this.flushDisabled || !this.server.isOnThread();

		try {
			this.connection.send(packet, callbacks, bl);
		} catch (Throwable var7) {
			CrashReport crashReport = CrashReport.create(var7, "Sending packet");
			CrashReportSection crashReportSection = crashReport.addElement("Packet being sent");
			crashReportSection.add("Packet class", (CrashCallable<String>)(() -> packet.getClass().getCanonicalName()));
			throw new CrashException(crashReport);
		}
	}

	public void disconnect(Text reason) {
		this.connection.send(new DisconnectS2CPacket(reason), PacketCallbacks.always(() -> this.connection.disconnect(reason)));
		this.connection.tryDisableAutoRead();
		this.server.submitAndJoin(this.connection::handleDisconnection);
	}

	protected boolean isHost() {
		return this.server.isHost(this.getProfile());
	}

	protected abstract GameProfile getProfile();

	@Debug
	public GameProfile getDebugProfile() {
		return this.getProfile();
	}

	public int getLatency() {
		return this.latency;
	}

	protected ConnectedClientData createClientData(SyncedClientOptions syncedOptions) {
		return new ConnectedClientData(this.getProfile(), this.latency, syncedOptions);
	}
}
