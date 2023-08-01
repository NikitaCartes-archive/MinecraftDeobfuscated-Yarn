package net.minecraft.server.network;

import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.annotation.Nullable;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.listener.ServerConfigurationPacketListener;
import net.minecraft.network.listener.TickablePacketListener;
import net.minecraft.network.packet.c2s.common.ResourcePackStatusC2SPacket;
import net.minecraft.network.packet.c2s.config.ReadyC2SPacket;
import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;
import net.minecraft.network.packet.s2c.common.DisconnectS2CPacket;
import net.minecraft.network.packet.s2c.common.SynchronizeTagsS2CPacket;
import net.minecraft.network.packet.s2c.config.DynamicRegistriesS2CPacket;
import net.minecraft.network.packet.s2c.config.FeaturesS2CPacket;
import net.minecraft.network.packet.s2c.custom.BrandCustomPayload;
import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.SerializableRegistries;
import net.minecraft.registry.ServerDynamicRegistryType;
import net.minecraft.registry.tag.TagPacketSerializer;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.text.Text;
import org.slf4j.Logger;

public class ServerConfigurationNetworkHandler extends ServerCommonNetworkHandler implements TickablePacketListener, ServerConfigurationPacketListener {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Text INVALID_PLAYER_DATA_TEXT = Text.translatable("multiplayer.disconnect.invalid_player_data");
	private final GameProfile profile;
	private final Queue<ServerPlayerConfigurationTask> tasks = new ConcurrentLinkedQueue();
	@Nullable
	private ServerPlayerConfigurationTask currentTask;

	public ServerConfigurationNetworkHandler(MinecraftServer server, ClientConnection connection, GameProfile profile) {
		super(server, connection, 0);
		this.profile = profile;
	}

	@Override
	protected GameProfile getProfile() {
		return this.profile;
	}

	@Override
	public void onDisconnected(Text reason) {
		LOGGER.info("{} lost connection: {}", this.profile, reason.getString());
		super.onDisconnected(reason);
	}

	@Override
	public boolean isConnectionOpen() {
		return this.connection.isOpen();
	}

	public void sendConfigurations() {
		this.sendPacket(new CustomPayloadS2CPacket(new BrandCustomPayload(this.server.getServerModName())));
		CombinedDynamicRegistries<ServerDynamicRegistryType> combinedDynamicRegistries = this.server.getCombinedDynamicRegistries();
		this.sendPacket(new FeaturesS2CPacket(FeatureFlags.FEATURE_MANAGER.toId(this.server.getSaveProperties().getEnabledFeatures())));
		this.sendPacket(
			new DynamicRegistriesS2CPacket(
				new DynamicRegistryManager.ImmutableImpl(SerializableRegistries.streamDynamicEntries(combinedDynamicRegistries)).toImmutable()
			)
		);
		this.sendPacket(new SynchronizeTagsS2CPacket(TagPacketSerializer.serializeTags(combinedDynamicRegistries)));
		this.queueSendResourcePackTask();
		this.tasks.add(new JoinWorldTask());
		this.pollTask();
	}

	public void queueJoinWorldTask() {
		this.tasks.add(new JoinWorldTask());
		this.pollTask();
	}

	private void queueSendResourcePackTask() {
		this.server.getResourcePackProperties().ifPresent(properties -> this.tasks.add(new SendResourcePackTask(properties)));
	}

	@Override
	public void onResourcePackStatus(ResourcePackStatusC2SPacket packet) {
		super.onResourcePackStatus(packet);
		if (packet.getStatus() != ResourcePackStatusC2SPacket.Status.ACCEPTED) {
			this.onTaskFinished(SendResourcePackTask.KEY);
		}
	}

	@Override
	public void onReady(ReadyC2SPacket packet) {
		this.connection.disableAutoRead();
		NetworkThreadUtils.forceMainThread(packet, this, this.server);
		this.onTaskFinished(JoinWorldTask.KEY);

		try {
			PlayerManager playerManager = this.server.getPlayerManager();
			if (playerManager.getPlayer(this.profile.getId()) != null) {
				this.disconnect(PlayerManager.DUPLICATE_LOGIN_TEXT);
				return;
			}

			ServerPlayerEntity serverPlayerEntity = playerManager.createPlayer(this.profile);
			playerManager.onPlayerConnect(this.connection, serverPlayerEntity, this.getLatency());
			this.connection.enableAutoRead();
		} catch (Exception var4) {
			LOGGER.error("Couldn't place player in world", (Throwable)var4);
			this.connection.send(new DisconnectS2CPacket(INVALID_PLAYER_DATA_TEXT));
			this.connection.disconnect(INVALID_PLAYER_DATA_TEXT);
		}
	}

	@Override
	public void tick() {
		this.baseTick();
	}

	private void pollTask() {
		if (this.currentTask != null) {
			throw new IllegalStateException("Task " + this.currentTask.getKey().id() + " has not finished yet");
		} else if (this.isConnectionOpen()) {
			ServerPlayerConfigurationTask serverPlayerConfigurationTask = (ServerPlayerConfigurationTask)this.tasks.poll();
			if (serverPlayerConfigurationTask != null) {
				this.currentTask = serverPlayerConfigurationTask;
				serverPlayerConfigurationTask.sendPacket(this::sendPacket);
			}
		}
	}

	private void onTaskFinished(ServerPlayerConfigurationTask.Key key) {
		ServerPlayerConfigurationTask.Key key2 = this.currentTask != null ? this.currentTask.getKey() : null;
		if (!key.equals(key2)) {
			throw new IllegalStateException("Unexpected request for task finish, current task: " + key2 + ", requested: " + key);
		} else {
			this.currentTask = null;
			this.pollTask();
		}
	}
}
