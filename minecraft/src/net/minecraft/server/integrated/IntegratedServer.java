package net.minecraft.server.integrated;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.server.LanServerPinger;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.SaveLoader;
import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.ModStatus;
import net.minecraft.util.SystemDetails;
import net.minecraft.util.UserCache;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.GameMode;
import net.minecraft.world.level.storage.LevelStorage;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class IntegratedServer extends MinecraftServer {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final int field_34964 = 2;
	private final MinecraftClient client;
	private boolean paused = true;
	private int lanPort = -1;
	@Nullable
	private GameMode forcedGameMode;
	@Nullable
	private LanServerPinger lanPinger;
	@Nullable
	private UUID localPlayerUuid;
	private int simulationDistance = 0;

	public IntegratedServer(
		Thread serverThread,
		MinecraftClient client,
		LevelStorage.Session session,
		ResourcePackManager dataPackManager,
		SaveLoader saveLoader,
		MinecraftSessionService sessionService,
		GameProfileRepository gameProfileRepo,
		UserCache userCache,
		WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory
	) {
		super(
			serverThread,
			session,
			dataPackManager,
			saveLoader,
			client.getNetworkProxy(),
			client.getDataFixer(),
			sessionService,
			gameProfileRepo,
			userCache,
			worldGenerationProgressListenerFactory
		);
		this.setSinglePlayerName(client.getSession().getUsername());
		this.setDemo(client.isDemo());
		this.setPlayerManager(new IntegratedPlayerManager(this, this.getRegistryManager(), this.saveHandler));
		this.client = client;
	}

	@Override
	public boolean setupServer() {
		LOGGER.info("Starting integrated minecraft server version {}", SharedConstants.getGameVersion().getName());
		this.setOnlineMode(true);
		this.setPvpEnabled(true);
		this.setFlightEnabled(true);
		this.generateKeyPair();
		this.loadWorld();
		this.setMotd(this.getSinglePlayerName() + " - " + this.getSaveProperties().getLevelName());
		return true;
	}

	@Override
	public void tick(BooleanSupplier shouldKeepTicking) {
		boolean bl = this.paused;
		this.paused = MinecraftClient.getInstance().isPaused();
		Profiler profiler = this.getProfiler();
		if (!bl && this.paused) {
			profiler.push("autoSave");
			LOGGER.info("Saving and pausing game...");
			this.saveAll(false, false, false);
			profiler.pop();
		}

		boolean bl2 = MinecraftClient.getInstance().getNetworkHandler() != null;
		if (bl2 && this.paused) {
			this.incrementTotalWorldTimeStat();
		} else {
			super.tick(shouldKeepTicking);
			int i = Math.max(2, this.client.options.viewDistance);
			if (i != this.getPlayerManager().getViewDistance()) {
				LOGGER.info("Changing view distance to {}, from {}", i, this.getPlayerManager().getViewDistance());
				this.getPlayerManager().setViewDistance(i);
			}

			int j = Math.max(2, this.client.options.simulationDistance);
			if (j != this.simulationDistance) {
				LOGGER.info("Changing simulation distance to {}, from {}", j, this.simulationDistance);
				this.getPlayerManager().setSimulationDistance(j);
				this.simulationDistance = j;
			}
		}
	}

	private void incrementTotalWorldTimeStat() {
		for (ServerPlayerEntity serverPlayerEntity : this.getPlayerManager().getPlayerList()) {
			serverPlayerEntity.incrementStat(Stats.TOTAL_WORLD_TIME);
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
	public int getRateLimit() {
		return 0;
	}

	@Override
	public boolean isUsingNativeTransport() {
		return false;
	}

	@Override
	public void setCrashReport(CrashReport report) {
		this.client.setCrashReportSupplier(() -> report);
	}

	@Override
	public SystemDetails addExtraSystemDetails(SystemDetails details) {
		details.addSection("Type", "Integrated Server (map_client.txt)");
		details.addSection("Is Modded", (Supplier<String>)(() -> this.getModStatus().getMessage()));
		return details;
	}

	@Override
	public ModStatus getModStatus() {
		return MinecraftClient.getModStatus().combine(super.getModStatus());
	}

	@Override
	public boolean openToLan(@Nullable GameMode gameMode, boolean cheatsAllowed, int port) {
		try {
			this.client.loadBlockList();
			this.getNetworkIo().bind(null, port);
			LOGGER.info("Started serving on {}", port);
			this.lanPort = port;
			this.lanPinger = new LanServerPinger(this.getServerMotd(), port + "");
			this.lanPinger.start();
			this.forcedGameMode = gameMode;
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
		this.forcedGameMode = null;
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
		return profile.getName().equalsIgnoreCase(this.getSinglePlayerName());
	}

	@Override
	public int adjustTrackingDistance(int initialDistance) {
		return (int)(this.client.options.entityDistanceScaling * (float)initialDistance);
	}

	@Override
	public boolean syncChunkWrites() {
		return this.client.options.syncChunkWrites;
	}

	@Nullable
	@Override
	public GameMode getForcedGameMode() {
		return this.isRemote() ? MoreObjects.firstNonNull(this.forcedGameMode, this.saveProperties.getGameMode()) : null;
	}
}
