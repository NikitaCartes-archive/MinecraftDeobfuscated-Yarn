package net.minecraft.server.integrated;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.NetworkEncryptionUtils;
import net.minecraft.server.LanServerPinger;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.UserCache;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.profiler.DisableableProfiler;
import net.minecraft.util.snooper.Snooper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class IntegratedServer extends MinecraftServer {
	private static final Logger LOGGER = LogManager.getLogger();
	private final MinecraftClient client;
	private final LevelInfo levelInfo;
	private boolean field_5524;
	private int lanPort = -1;
	private LanServerPinger lanPinger;
	private UUID localPlayerUuid;

	public IntegratedServer(
		MinecraftClient minecraftClient,
		String string,
		String string2,
		LevelInfo levelInfo,
		YggdrasilAuthenticationService yggdrasilAuthenticationService,
		MinecraftSessionService minecraftSessionService,
		GameProfileRepository gameProfileRepository,
		UserCache userCache,
		WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory
	) {
		super(
			new File(minecraftClient.runDirectory, "saves"),
			minecraftClient.getNetworkProxy(),
			minecraftClient.getDataFixer(),
			new CommandManager(false),
			yggdrasilAuthenticationService,
			minecraftSessionService,
			gameProfileRepository,
			userCache,
			worldGenerationProgressListenerFactory,
			string
		);
		this.setUserName(minecraftClient.getSession().getUsername());
		this.setServerName(string2);
		this.setDemo(minecraftClient.isDemo());
		this.setBonusChest(levelInfo.hasBonusChest());
		this.setWorldHeight(256);
		this.setPlayerManager(new IntegratedPlayerManager(this));
		this.client = minecraftClient;
		this.levelInfo = this.isDemo() ? MinecraftServer.WORLD_INFO : levelInfo;
	}

	@Override
	public void loadWorld(String string, String string2, long l, LevelGeneratorType levelGeneratorType, JsonElement jsonElement) {
		this.upgradeWorld(string);
		WorldSaveHandler worldSaveHandler = this.getLevelStorage().createSaveHandler(string, this);
		this.loadWorldResourcePack(this.getLevelName(), worldSaveHandler);
		LevelProperties levelProperties = worldSaveHandler.readProperties();
		if (levelProperties == null) {
			levelProperties = new LevelProperties(this.levelInfo, string2);
		} else {
			levelProperties.setLevelName(string2);
		}

		this.loadWorldDataPacks(worldSaveHandler.getWorldDir(), levelProperties);
		WorldGenerationProgressListener worldGenerationProgressListener = this.worldGenerationProgressListenerFactory.create(11);
		this.createWorlds(worldSaveHandler, levelProperties, this.levelInfo, worldGenerationProgressListener);
		if (this.getWorld(DimensionType.field_13072).getLevelProperties().getDifficulty() == null) {
			this.setDifficulty(this.client.options.difficulty, true);
		}

		this.prepareStartRegion(worldGenerationProgressListener);
	}

	@Override
	public boolean setupServer() throws IOException {
		LOGGER.info("Starting integrated minecraft server version " + SharedConstants.getGameVersion().getName());
		this.setOnlineMode(true);
		this.setSpawnAnimals(true);
		this.setSpawnNpcs(true);
		this.setPvpEnabled(true);
		this.setFlightEnabled(true);
		LOGGER.info("Generating keypair");
		this.setKeyPair(NetworkEncryptionUtils.generateServerKeyPair());
		this.loadWorld(this.getLevelName(), this.getServerName(), this.levelInfo.getSeed(), this.levelInfo.getGeneratorType(), this.levelInfo.getGeneratorOptions());
		this.setMotd(this.getUserName() + " - " + this.getWorld(DimensionType.field_13072).getLevelProperties().getLevelName());
		return true;
	}

	@Override
	public void tick(BooleanSupplier booleanSupplier) {
		boolean bl = this.field_5524;
		this.field_5524 = MinecraftClient.getInstance().getNetworkHandler() != null && MinecraftClient.getInstance().isPaused();
		DisableableProfiler disableableProfiler = this.getProfiler();
		if (!bl && this.field_5524) {
			disableableProfiler.push("autoSave");
			LOGGER.info("Saving and pausing game...");
			this.getPlayerManager().saveAllPlayerData();
			this.save(false, false, false);
			disableableProfiler.pop();
		}

		if (!this.field_5524) {
			super.tick(booleanSupplier);
			int i = Math.max(2, this.client.options.viewDistance + -2);
			if (i != this.getPlayerManager().getViewDistance()) {
				LOGGER.info("Changing view distance to {}, from {}", i, this.getPlayerManager().getViewDistance());
				this.getPlayerManager().setViewDistance(i, i - 2);
			}
		}
	}

	@Override
	public boolean shouldGenerateStructures() {
		return false;
	}

	@Override
	public GameMode getDefaultGameMode() {
		return this.levelInfo.getGameMode();
	}

	@Override
	public Difficulty getDefaultDifficulty() {
		return this.client.world.getLevelProperties().getDifficulty();
	}

	@Override
	public boolean isHardcore() {
		return this.levelInfo.isHardcore();
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
	public void setCrashReport(CrashReport crashReport) {
		this.client.setCrashReport(crashReport);
	}

	@Override
	public CrashReport populateCrashReport(CrashReport crashReport) {
		crashReport = super.populateCrashReport(crashReport);
		crashReport.getSystemDetailsSection().add("Type", "Integrated Server (map_client.txt)");
		crashReport.getSystemDetailsSection()
			.method_577(
				"Is Modded",
				() -> {
					String string = ClientBrandRetriever.getClientModName();
					if (!string.equals("vanilla")) {
						return "Definitely; Client brand changed to '" + string + "'";
					} else {
						string = this.getServerModName();
						if (!"vanilla".equals(string)) {
							return "Definitely; Server brand changed to '" + string + "'";
						} else {
							return MinecraftClient.class.getSigners() == null
								? "Very likely; Jar signature invalidated"
								: "Probably not. Jar signature remains and both client + server brands are untouched.";
						}
					}
				}
			);
		return crashReport;
	}

	@Override
	public void addSnooperInfo(Snooper snooper) {
		super.addSnooperInfo(snooper);
		snooper.addInfo("snooper_partner", this.client.getSnooper().getToken());
	}

	@Override
	public boolean openToLan(GameMode gameMode, boolean bl, int i) {
		try {
			this.getNetworkIo().bind(null, i);
			LOGGER.info("Started serving on {}", i);
			this.lanPort = i;
			this.lanPinger = new LanServerPinger(this.getServerMotd(), i + "");
			this.lanPinger.start();
			this.getPlayerManager().setGameMode(gameMode);
			this.getPlayerManager().setCheatsAllowed(bl);
			int j = this.getPermissionLevel(this.client.player.getGameProfile());
			this.client.player.setClientPermissionLevel(j);

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
		this.executeSync(() -> {
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

	public void setLocalPlayerUuid(UUID uUID) {
		this.localPlayerUuid = uUID;
	}

	@Override
	public boolean isOwner(GameProfile gameProfile) {
		return gameProfile.getName().equalsIgnoreCase(this.getUserName());
	}
}
