package net.minecraft.server.network;

import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.annotation.Nullable;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.DisconnectionInfo;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.listener.ServerConfigurationPacketListener;
import net.minecraft.network.listener.TickablePacketListener;
import net.minecraft.network.packet.BrandCustomPayload;
import net.minecraft.network.packet.c2s.common.ClientOptionsC2SPacket;
import net.minecraft.network.packet.c2s.common.ResourcePackStatusC2SPacket;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.network.packet.c2s.config.ReadyC2SPacket;
import net.minecraft.network.packet.c2s.config.SelectKnownPacksC2SPacket;
import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;
import net.minecraft.network.packet.s2c.common.DisconnectS2CPacket;
import net.minecraft.network.packet.s2c.common.ServerLinksS2CPacket;
import net.minecraft.network.packet.s2c.config.FeaturesS2CPacket;
import net.minecraft.network.state.PlayStateFactories;
import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.registry.ServerDynamicRegistryType;
import net.minecraft.registry.VersionedIdentifier;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.ServerLinks;
import net.minecraft.text.Text;
import org.slf4j.Logger;

public class ServerConfigurationNetworkHandler extends ServerCommonNetworkHandler implements ServerConfigurationPacketListener, TickablePacketListener {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Text INVALID_PLAYER_DATA_TEXT = Text.translatable("multiplayer.disconnect.invalid_player_data");
	private final GameProfile profile;
	private final Queue<ServerPlayerConfigurationTask> tasks = new ConcurrentLinkedQueue();
	@Nullable
	private ServerPlayerConfigurationTask currentTask;
	private SyncedClientOptions syncedOptions;
	@Nullable
	private SynchronizeRegistriesTask synchronizedRegistriesTask;

	public ServerConfigurationNetworkHandler(MinecraftServer minecraftServer, ClientConnection clientConnection, ConnectedClientData connectedClientData) {
		super(minecraftServer, clientConnection, connectedClientData);
		this.profile = connectedClientData.gameProfile();
		this.syncedOptions = connectedClientData.syncedOptions();
	}

	@Override
	protected GameProfile getProfile() {
		return this.profile;
	}

	@Override
	public void onDisconnected(DisconnectionInfo info) {
		LOGGER.info("{} lost connection: {}", this.profile, info.reason().getString());
		super.onDisconnected(info);
	}

	@Override
	public boolean isConnectionOpen() {
		return this.connection.isOpen();
	}

	public void sendConfigurations() {
		this.sendPacket(new CustomPayloadS2CPacket(new BrandCustomPayload(this.server.getServerModName())));
		ServerLinks serverLinks = this.server.getServerLinks();
		if (!serverLinks.isEmpty()) {
			this.sendPacket(new ServerLinksS2CPacket(serverLinks));
		}

		CombinedDynamicRegistries<ServerDynamicRegistryType> combinedDynamicRegistries = this.server.getCombinedDynamicRegistries();
		List<VersionedIdentifier> list = this.server.getResourceManager().streamResourcePacks().flatMap(pack -> pack.getInfo().knownPackInfo().stream()).toList();
		this.sendPacket(new FeaturesS2CPacket(FeatureFlags.FEATURE_MANAGER.toId(this.server.getSaveProperties().getEnabledFeatures())));
		this.synchronizedRegistriesTask = new SynchronizeRegistriesTask(list, combinedDynamicRegistries);
		this.tasks.add(this.synchronizedRegistriesTask);
		this.queueSendResourcePackTask();
		this.tasks.add(new JoinWorldTask());
		this.pollTask();
	}

	public void endConfiguration() {
		this.tasks.add(new JoinWorldTask());
		this.pollTask();
	}

	private void queueSendResourcePackTask() {
		this.server.getResourcePackProperties().ifPresent(properties -> this.tasks.add(new SendResourcePackTask(properties)));
	}

	@Override
	public void onClientOptions(ClientOptionsC2SPacket packet) {
		this.syncedOptions = packet.options();
	}

	@Override
	public void onResourcePackStatus(ResourcePackStatusC2SPacket packet) {
		super.onResourcePackStatus(packet);
		if (packet.status().hasFinished()) {
			this.onTaskFinished(SendResourcePackTask.KEY);
		}
	}

	@Override
	public void onSelectKnownPacks(SelectKnownPacksC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.server);
		if (this.synchronizedRegistriesTask == null) {
			throw new IllegalStateException("Unexpected response from client: received pack selection, but no negotiation ongoing");
		} else {
			this.synchronizedRegistriesTask.onSelectKnownPacks(packet.knownPacks(), this::sendPacket);
			this.onTaskFinished(SynchronizeRegistriesTask.KEY);
		}
	}

	@Override
	public void onReady(ReadyC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.server);
		this.onTaskFinished(JoinWorldTask.KEY);
		this.connection.transitionOutbound(PlayStateFactories.S2C.bind(RegistryByteBuf.makeFactory(this.server.getRegistryManager())));

		try {
			PlayerManager playerManager = this.server.getPlayerManager();
			if (playerManager.getPlayer(this.profile.getId()) != null) {
				this.disconnect(PlayerManager.DUPLICATE_LOGIN_TEXT);
				return;
			}

			Text text = playerManager.checkCanJoin(this.connection.getAddress(), this.profile);
			if (text != null) {
				this.disconnect(text);
				return;
			}

			ServerPlayerEntity serverPlayerEntity = playerManager.createPlayer(this.profile, this.syncedOptions);
			playerManager.onPlayerConnect(this.connection, serverPlayerEntity, this.createClientData(this.syncedOptions));
		} catch (Exception var5) {
			LOGGER.error("Couldn't place player in world", (Throwable)var5);
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
