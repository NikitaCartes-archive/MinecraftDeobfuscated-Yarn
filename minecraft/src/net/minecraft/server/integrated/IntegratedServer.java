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
import net.minecraft.server.command.ServerCommandManager;
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
	private LanServerPinger field_5519;
	private UUID field_5521;

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
			new ServerCommandManager(false),
			yggdrasilAuthenticationService,
			minecraftSessionService,
			gameProfileRepository,
			userCache,
			worldGenerationProgressListenerFactory,
			string
		);
		this.setUserName(minecraftClient.method_1548().getUsername());
		this.setServerName(string2);
		this.setDemo(minecraftClient.isDemo());
		this.setBonusChest(levelInfo.hasBonusChest());
		this.setWorldHeight(256);
		this.method_3846(new IntegratedPlayerManager(this));
		this.client = minecraftClient;
		this.levelInfo = this.isDemo() ? MinecraftServer.field_17704 : levelInfo;
	}

	@Override
	public void method_3735(String string, String string2, long l, LevelGeneratorType levelGeneratorType, JsonElement jsonElement) {
		this.method_3755(string);
		WorldSaveHandler worldSaveHandler = this.getLevelStorage().method_242(string, this);
		this.method_3861(this.getLevelName(), worldSaveHandler);
		LevelProperties levelProperties = worldSaveHandler.readProperties();
		if (levelProperties == null) {
			levelProperties = new LevelProperties(this.levelInfo, string2);
		} else {
			levelProperties.setLevelName(string2);
		}

		this.method_3800(worldSaveHandler.getWorldDir(), levelProperties);
		WorldGenerationProgressListener worldGenerationProgressListener = this.field_17439.create(11);
		this.method_3786(worldSaveHandler, levelProperties, this.levelInfo, worldGenerationProgressListener);
		if (this.method_3847(DimensionType.field_13072).method_8401().getDifficulty() == null) {
			this.setDifficulty(this.client.field_1690.difficulty, true);
		}

		this.method_3774(worldGenerationProgressListener);
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
		this.method_3735(this.getLevelName(), this.getServerName(), this.levelInfo.getSeed(), this.levelInfo.method_8576(), this.levelInfo.getGeneratorOptions());
		this.setMotd(this.getUserName() + " - " + this.method_3847(DimensionType.field_13072).method_8401().getLevelName());
		return true;
	}

	@Override
	public void method_3748(BooleanSupplier booleanSupplier) {
		boolean bl = this.field_5524;
		this.field_5524 = MinecraftClient.getInstance().method_1562() != null && MinecraftClient.getInstance().isPaused();
		DisableableProfiler disableableProfiler = this.getProfiler();
		if (!bl && this.field_5524) {
			disableableProfiler.push("autoSave");
			LOGGER.info("Saving and pausing game...");
			this.method_3760().saveAllPlayerData();
			this.save(false, true, false);
			disableableProfiler.pop();
		}

		if (!this.field_5524) {
			super.method_3748(booleanSupplier);
			int i = Math.max(2, this.client.field_1690.viewDistance + -2);
			if (i != this.method_3760().getViewDistance()) {
				LOGGER.info("Changing view distance to {}, from {}", i, this.method_3760().getViewDistance());
				this.method_3760().setViewDistance(i, i - 2);
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
		return this.client.field_1687.method_8401().getDifficulty();
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
		this.client.method_1494(crashReport);
	}

	@Override
	public CrashReport populateCrashReport(CrashReport crashReport) {
		crashReport = super.populateCrashReport(crashReport);
		crashReport.method_567().add("Type", "Integrated Server (map_client.txt)");
		crashReport.method_567()
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
			this.method_3787().method_14354(null, i);
			LOGGER.info("Started serving on {}", i);
			this.lanPort = i;
			this.field_5519 = new LanServerPinger(this.getServerMotd(), i + "");
			this.field_5519.start();
			this.method_3760().setGameMode(gameMode);
			this.method_3760().setCheatsAllowed(bl);
			int j = this.getPermissionLevel(this.client.field_1724.getGameProfile());
			this.client.field_1724.setClientPermissionLevel(j);

			for (ServerPlayerEntity serverPlayerEntity : this.method_3760().getPlayerList()) {
				this.getCommandManager().method_9241(serverPlayerEntity);
			}

			return true;
		} catch (IOException var7) {
			return false;
		}
	}

	@Override
	public void shutdown() {
		super.shutdown();
		if (this.field_5519 != null) {
			this.field_5519.interrupt();
			this.field_5519 = null;
		}
	}

	@Override
	public void stop(boolean bl) {
		this.executeFuture(() -> {
			for (ServerPlayerEntity serverPlayerEntity : Lists.newArrayList(this.method_3760().getPlayerList())) {
				if (!serverPlayerEntity.getUuid().equals(this.field_5521)) {
					this.method_3760().method_14611(serverPlayerEntity);
				}
			}
		}).join();
		super.stop(bl);
		if (this.field_5519 != null) {
			this.field_5519.interrupt();
			this.field_5519 = null;
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
		this.method_3760().setGameMode(gameMode);
	}

	@Override
	public boolean areCommandBlocksEnabled() {
		return true;
	}

	@Override
	public int getOpPermissionLevel() {
		return 2;
	}

	public void method_4817(UUID uUID) {
		this.field_5521 = uUID;
	}

	@Override
	public boolean method_19466(GameProfile gameProfile) {
		return gameProfile.getName().equalsIgnoreCase(this.getUserName());
	}
}
