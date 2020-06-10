package net.minecraft.server.integrated;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.NetworkEncryptionUtils;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ServerResourceManager;
import net.minecraft.server.LanServerPinger;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.UserCache;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.RegistryTracker;
import net.minecraft.util.snooper.Snooper;
import net.minecraft.world.GameMode;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.level.storage.LevelStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class IntegratedServer extends MinecraftServer {
	private static final Logger LOGGER = LogManager.getLogger();
	private final MinecraftClient client;
	private boolean paused;
	private int lanPort = -1;
	private LanServerPinger lanPinger;
	private UUID localPlayerUuid;

	public IntegratedServer(
		Thread thread,
		MinecraftClient minecraftClient,
		RegistryTracker.Modifiable modifiable,
		LevelStorage.Session session,
		ResourcePackManager<ResourcePackProfile> resourcePackManager,
		ServerResourceManager serverResourceManager,
		SaveProperties saveProperties,
		MinecraftSessionService minecraftSessionService,
		GameProfileRepository gameProfileRepository,
		UserCache userCache,
		WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory
	) {
		super(
			thread,
			modifiable,
			session,
			saveProperties,
			resourcePackManager,
			minecraftClient.getNetworkProxy(),
			minecraftClient.getDataFixer(),
			serverResourceManager,
			minecraftSessionService,
			gameProfileRepository,
			userCache,
			worldGenerationProgressListenerFactory
		);
		this.setServerName(minecraftClient.getSession().getUsername());
		this.setDemo(minecraftClient.isDemo());
		this.setWorldHeight(256);
		this.setPlayerManager(new IntegratedPlayerManager(this, this.dimensionTracker, this.field_24371));
		this.client = minecraftClient;
	}

	@Override
	public boolean setupServer() {
		LOGGER.info("Starting integrated minecraft server version " + SharedConstants.getGameVersion().getName());
		this.setOnlineMode(true);
		this.setPvpEnabled(true);
		this.setFlightEnabled(true);
		LOGGER.info("Generating keypair");
		this.setKeyPair(NetworkEncryptionUtils.generateServerKeyPair());
		this.loadWorld();
		this.setMotd(this.getUserName() + " - " + this.getSaveProperties().getLevelName());
		return true;
	}

	@Override
	public void tick(BooleanSupplier shouldKeepTicking) {
		boolean bl = this.paused;
		this.paused = MinecraftClient.getInstance().getNetworkHandler() != null && MinecraftClient.getInstance().isPaused();
		Profiler profiler = this.getProfiler();
		if (!bl && this.paused) {
			profiler.push("autoSave");
			LOGGER.info("Saving and pausing game...");
			this.getPlayerManager().saveAllPlayerData();
			this.save(false, false, false);
			profiler.pop();
		}

		if (!this.paused) {
			super.tick(shouldKeepTicking);
			int i = Math.max(2, this.client.options.viewDistance + -1);
			if (i != this.getPlayerManager().getViewDistance()) {
				LOGGER.info("Changing view distance to {}, from {}", i, this.getPlayerManager().getViewDistance());
				this.getPlayerManager().setViewDistance(i);
			}
		}
	}

	@Override
	public boolean shouldBroadcastRconToOps() {
		return true;
	}

	@Override
	public boolean shouldBroadcastConsoleToOps() {
		return true;
	}

	@Override
	public File getRunDirectory() {
		return this.client.runDirectory;
	}

	@Override
	public boolean isDedicated() {
		return false;
	}

	@Override
	public boolean isUsingNativeTransport() {
		return false;
	}

	@Override
	public void setCrashReport(CrashReport report) {
		this.client.setCrashReport(report);
	}

	@Override
	public CrashReport populateCrashReport(CrashReport crashReport) {
		crashReport = super.populateCrashReport(crashReport);
		crashReport.getSystemDetailsSection().add("Type", "Integrated Server (map_client.txt)");
		crashReport.getSystemDetailsSection()
			.add(
				"Is Modded",
				(CrashCallable<String>)(() -> (String)this.getModdedStatusMessage()
						.orElse("Probably not. Jar signature remains and both client + server brands are untouched."))
			);
		return crashReport;
	}

	@Override
	public Optional<String> getModdedStatusMessage() {
		String string = ClientBrandRetriever.getClientModName();
		if (!string.equals("vanilla")) {
			return Optional.of("Definitely; Client brand changed to '" + string + "'");
		} else {
			string = this.getServerModName();
			if (!"vanilla".equals(string)) {
				return Optional.of("Definitely; Server brand changed to '" + string + "'");
			} else {
				return MinecraftClient.class.getSigners() == null ? Optional.of("Very likely; Jar signature invalidated") : Optional.empty();
			}
		}
	}

	@Override
	public void addSnooperInfo(Snooper snooper) {
		super.addSnooperInfo(snooper);
		snooper.addInfo("snooper_partner", this.client.getSnooper().getToken());
	}

	@Override
	public boolean openToLan(GameMode gameMode, boolean cheatsAllowed, int port) {
		try {
			this.getNetworkIo().bind(null, port);
			LOGGER.info("Started serving on {}", port);
			this.lanPort = port;
			this.lanPinger = new LanServerPinger(this.getServerMotd(), port + "");
			this.lanPinger.start();
			this.getPlayerManager().setGameMode(gameMode);
			this.getPlayerManager().setCheatsAllowed(cheatsAllowed);
			int i = this.getPermissionLevel(this.client.player.getGameProfile());
			this.client.player.setClientPermissionLevel(i);

			for (ServerPlayerEntity serverPlayerEntity : this.getPlayerManager().getPlayerList()) {
				this.getCommandManager().sendCommandTree(serverPlayerEntity);
			}

			return true;
		} catch (IOException var7) {
			return false;
		}
	}

	@Override
	public void shutdown() {
		super.shutdown();
		if (this.lanPinger != null) {
			this.lanPinger.interrupt();
			this.lanPinger = null;
		}
	}

	@Override
	public void stop(boolean bl) {
		this.submitAndJoin(() -> {
			for (ServerPlayerEntity serverPlayerEntity : Lists.newArrayList(this.getPlayerManager().getPlayerList())) {
				if (!serverPlayerEntity.getUuid().equals(this.localPlayerUuid)) {
					this.getPlayerManager().remove(serverPlayerEntity);
				}
			}
		});
		super.stop(bl);
		if (this.lanPinger != null) {
			this.lanPinger.interrupt();
			this.lanPinger = null;
		}
	}

	@Override
	public boolean isRemote() {
		return this.lanPort > -1;
	}

	@Override
	public int getServerPort() {
		return this.lanPort;
	}

	@Override
	public void setDefaultGameMode(GameMode gameMode) {
		super.setDefaultGameMode(gameMode);
		this.getPlayerManager().setGameMode(gameMode);
	}

	@Override
	public boolean areCommandBlocksEnabled() {
		return true;
	}

	@Override
	public int getOpPermissionLevel() {
		return 2;
	}

	@Override
	public int getFunctionPermissionLevel() {
		return 2;
	}

	public void setLocalPlayerUuid(UUID localPlayerUuid) {
		this.localPlayerUuid = localPlayerUuid;
	}

	@Override
	public boolean isHost(GameProfile profile) {
		return profile.getName().equalsIgnoreCase(this.getUserName());
	}

	@Override
	public int adjustTrackingDistance(int initialDistance) {
		return (int)(this.client.options.entityDistanceScaling * (float)initialDistance);
	}

	@Override
	public boolean syncChunkWrites() {
		return this.client.options.field_25623;
	}
}
