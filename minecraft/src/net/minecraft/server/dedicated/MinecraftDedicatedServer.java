package net.minecraft.server.dedicated;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.datafixers.DataFixer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.class_5219;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.network.NetworkEncryptionUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerConfigHandler;
import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.dedicated.gui.DedicatedServerGui;
import net.minecraft.server.rcon.QueryResponseHandler;
import net.minecraft.server.rcon.RconServer;
import net.minecraft.util.UserCache;
import net.minecraft.util.Util;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.logging.UncaughtExceptionHandler;
import net.minecraft.util.logging.UncaughtExceptionLogger;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.snooper.Snooper;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.storage.LevelStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MinecraftDedicatedServer extends MinecraftServer implements DedicatedServer {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Pattern SHA1_PATTERN = Pattern.compile("^[a-fA-F0-9]{40}$");
	private final List<PendingServerCommand> commandQueue = Collections.synchronizedList(Lists.newArrayList());
	private QueryResponseHandler queryResponseHandler;
	private final ServerCommandOutput rconCommandOutput;
	private RconServer rconServer;
	private final ServerPropertiesLoader propertiesLoader;
	@Nullable
	private DedicatedServerGui gui;

	public MinecraftDedicatedServer(
		LevelStorage.Session session,
		class_5219 arg,
		ServerPropertiesLoader serverPropertiesLoader,
		DataFixer dataFixer,
		MinecraftSessionService minecraftSessionService,
		GameProfileRepository gameProfileRepository,
		UserCache userCache,
		WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory
	) {
		super(
			session,
			arg,
			Proxy.NO_PROXY,
			dataFixer,
			new CommandManager(true),
			minecraftSessionService,
			gameProfileRepository,
			userCache,
			worldGenerationProgressListenerFactory
		);
		this.propertiesLoader = serverPropertiesLoader;
		this.rconCommandOutput = new ServerCommandOutput(this);
		new Thread("Server Infinisleeper") {
			{
				this.setDaemon(true);
				this.setUncaughtExceptionHandler(new UncaughtExceptionLogger(MinecraftDedicatedServer.LOGGER));
				this.start();
			}

			public void run() {
				while (true) {
					try {
						Thread.sleep(2147483647L);
					} catch (InterruptedException var2) {
					}
				}
			}
		};
	}

	@Override
	public boolean setupServer() throws IOException {
		Thread thread = new Thread("Server console handler") {
			public void run() {
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));

				String string;
				try {
					while (!MinecraftDedicatedServer.this.isStopped() && MinecraftDedicatedServer.this.isRunning() && (string = bufferedReader.readLine()) != null) {
						MinecraftDedicatedServer.this.enqueueCommand(string, MinecraftDedicatedServer.this.getCommandSource());
					}
				} catch (IOException var4) {
					MinecraftDedicatedServer.LOGGER.error("Exception handling console input", (Throwable)var4);
				}
			}
		};
		thread.setDaemon(true);
		thread.setUncaughtExceptionHandler(new UncaughtExceptionLogger(LOGGER));
		thread.start();
		LOGGER.info("Starting minecraft server version " + SharedConstants.getGameVersion().getName());
		if (Runtime.getRuntime().maxMemory() / 1024L / 1024L < 512L) {
			LOGGER.warn("To start the server with more ram, launch it as \"java -Xmx1024M -Xms1024M -jar minecraft_server.jar\"");
		}

		LOGGER.info("Loading properties");
		ServerPropertiesHandler serverPropertiesHandler = this.propertiesLoader.getPropertiesHandler();
		if (this.isSinglePlayer()) {
			this.setServerIp("127.0.0.1");
		} else {
			this.setOnlineMode(serverPropertiesHandler.onlineMode);
			this.setPreventProxyConnections(serverPropertiesHandler.preventProxyConnections);
			this.setServerIp(serverPropertiesHandler.serverIp);
		}

		this.setPvpEnabled(serverPropertiesHandler.pvp);
		this.setFlightEnabled(serverPropertiesHandler.allowFlight);
		this.setResourcePack(serverPropertiesHandler.resourcePack, this.createResourcePackHash());
		this.setMotd(serverPropertiesHandler.motd);
		this.setForceGameMode(serverPropertiesHandler.forceGameMode);
		super.setPlayerIdleTimeout(serverPropertiesHandler.playerIdleTimeout.get());
		this.setEnforceWhitelist(serverPropertiesHandler.enforceWhitelist);
		this.field_24372.setGameMode(serverPropertiesHandler.gameMode);
		LOGGER.info("Default game type: {}", serverPropertiesHandler.gameMode);
		InetAddress inetAddress = null;
		if (!this.getServerIp().isEmpty()) {
			inetAddress = InetAddress.getByName(this.getServerIp());
		}

		if (this.getServerPort() < 0) {
			this.setServerPort(serverPropertiesHandler.serverPort);
		}

		LOGGER.info("Generating keypair");
		this.setKeyPair(NetworkEncryptionUtils.generateServerKeyPair());
		LOGGER.info("Starting Minecraft server on {}:{}", this.getServerIp().isEmpty() ? "*" : this.getServerIp(), this.getServerPort());

		try {
			this.getNetworkIo().bind(inetAddress, this.getServerPort());
		} catch (IOException var10) {
			LOGGER.warn("**** FAILED TO BIND TO PORT!");
			LOGGER.warn("The exception was: {}", var10.toString());
			LOGGER.warn("Perhaps a server is already running on that port?");
			return false;
		}

		if (!this.isOnlineMode()) {
			LOGGER.warn("**** SERVER IS RUNNING IN OFFLINE/INSECURE MODE!");
			LOGGER.warn("The server will make no attempt to authenticate usernames. Beware.");
			LOGGER.warn(
				"While this makes the game possible to play without internet access, it also opens up the ability for hackers to connect with any username they choose."
			);
			LOGGER.warn("To change this, set \"online-mode\" to \"true\" in the server.properties file.");
		}

		if (this.convertData()) {
			this.getUserCache().save();
		}

		if (!ServerConfigHandler.checkSuccess(this)) {
			return false;
		} else {
			this.setPlayerManager(new DedicatedPlayerManager(this, this.field_24371));
			long l = Util.getMeasuringTimeNano();
			this.setWorldHeight(serverPropertiesHandler.maxBuildHeight);
			SkullBlockEntity.setUserCache(this.getUserCache());
			SkullBlockEntity.setSessionService(this.getSessionService());
			UserCache.setUseRemote(this.isOnlineMode());
			LOGGER.info("Preparing level \"{}\"", this.getLevelName());
			this.loadWorld();
			long m = Util.getMeasuringTimeNano() - l;
			String string = String.format(Locale.ROOT, "%.3fs", (double)m / 1.0E9);
			LOGGER.info("Done ({})! For help, type \"help\"", string);
			if (serverPropertiesHandler.announcePlayerAchievements != null) {
				this.getGameRules().get(GameRules.ANNOUNCE_ADVANCEMENTS).set(serverPropertiesHandler.announcePlayerAchievements, this);
			}

			if (serverPropertiesHandler.enableQuery) {
				LOGGER.info("Starting GS4 status listener");
				this.queryResponseHandler = new QueryResponseHandler(this);
				this.queryResponseHandler.start();
			}

			if (serverPropertiesHandler.enableRcon) {
				LOGGER.info("Starting remote control listener");
				this.rconServer = new RconServer(this);
				this.rconServer.start();
			}

			if (this.getMaxTickTime() > 0L) {
				Thread thread2 = new Thread(new DedicatedServerWatchdog(this));
				thread2.setUncaughtExceptionHandler(new UncaughtExceptionHandler(LOGGER));
				thread2.setName("Server Watchdog");
				thread2.setDaemon(true);
				thread2.start();
			}

			Items.AIR.appendStacks(ItemGroup.SEARCH, DefaultedList.of());
			if (serverPropertiesHandler.enableJmxMonitoring) {
				ServerMBean.register(this);
			}

			return true;
		}
	}

	@Override
	public boolean shouldSpawnAnimals() {
		return this.getProperties().spawnAnimals && super.shouldSpawnAnimals();
	}

	@Override
	public boolean isMonsterSpawningEnabled() {
		return this.propertiesLoader.getPropertiesHandler().spawnMonsters && super.isMonsterSpawningEnabled();
	}

	@Override
	public boolean shouldSpawnNpcs() {
		return this.propertiesLoader.getPropertiesHandler().spawnNpcs && super.shouldSpawnNpcs();
	}

	public String createResourcePackHash() {
		ServerPropertiesHandler serverPropertiesHandler = this.propertiesLoader.getPropertiesHandler();
		String string;
		if (!serverPropertiesHandler.resourcePackSha1.isEmpty()) {
			string = serverPropertiesHandler.resourcePackSha1;
			if (!Strings.isNullOrEmpty(serverPropertiesHandler.resourcePackHash)) {
				LOGGER.warn("resource-pack-hash is deprecated and found along side resource-pack-sha1. resource-pack-hash will be ignored.");
			}
		} else if (!Strings.isNullOrEmpty(serverPropertiesHandler.resourcePackHash)) {
			LOGGER.warn("resource-pack-hash is deprecated. Please use resource-pack-sha1 instead.");
			string = serverPropertiesHandler.resourcePackHash;
		} else {
			string = "";
		}

		if (!string.isEmpty() && !SHA1_PATTERN.matcher(string).matches()) {
			LOGGER.warn("Invalid sha1 for ressource-pack-sha1");
		}

		if (!serverPropertiesHandler.resourcePack.isEmpty() && string.isEmpty()) {
			LOGGER.warn("You specified a resource pack without providing a sha1 hash. Pack will be updated on the client only if you change the name of the pack.");
		}

		return string;
	}

	@Override
	public ServerPropertiesHandler getProperties() {
		return this.propertiesLoader.getPropertiesHandler();
	}

	@Override
	public void method_27731() {
		this.setDifficulty(this.getProperties().difficulty, true);
	}

	@Override
	public boolean isHardcore() {
		return this.getProperties().hardcore;
	}

	@Override
	public CrashReport populateCrashReport(CrashReport crashReport) {
		crashReport = super.populateCrashReport(crashReport);
		crashReport.getSystemDetailsSection().add("Is Modded", (CrashCallable<String>)(() -> (String)this.getModdedStatusMessage().orElse("Unknown (can't tell)")));
		crashReport.getSystemDetailsSection().add("Type", (CrashCallable<String>)(() -> "Dedicated Server (map_server.txt)"));
		return crashReport;
	}

	@Override
	public Optional<String> getModdedStatusMessage() {
		String string = this.getServerModName();
		return !"vanilla".equals(string) ? Optional.of("Definitely; Server brand changed to '" + string + "'") : Optional.empty();
	}

	@Override
	public void exit() {
		if (this.gui != null) {
			this.gui.stop();
		}

		if (this.rconServer != null) {
			this.rconServer.stop();
		}

		if (this.queryResponseHandler != null) {
			this.queryResponseHandler.stop();
		}
	}

	@Override
	public void tickWorlds(BooleanSupplier shouldKeepTicking) {
		super.tickWorlds(shouldKeepTicking);
		this.executeQueuedCommands();
	}

	@Override
	public boolean isNetherAllowed() {
		return this.getProperties().allowNether;
	}

	@Override
	public void addSnooperInfo(Snooper snooper) {
		snooper.addInfo("whitelist_enabled", this.getPlayerManager().isWhitelistEnabled());
		snooper.addInfo("whitelist_count", this.getPlayerManager().getWhitelistedNames().length);
		super.addSnooperInfo(snooper);
	}

	public void enqueueCommand(String string, ServerCommandSource serverCommandSource) {
		this.commandQueue.add(new PendingServerCommand(string, serverCommandSource));
	}

	public void executeQueuedCommands() {
		while (!this.commandQueue.isEmpty()) {
			PendingServerCommand pendingServerCommand = (PendingServerCommand)this.commandQueue.remove(0);
			this.getCommandManager().execute(pendingServerCommand.source, pendingServerCommand.command);
		}
	}

	@Override
	public boolean isDedicated() {
		return true;
	}

	@Override
	public boolean isUsingNativeTransport() {
		return this.getProperties().useNativeTransport;
	}

	public DedicatedPlayerManager getPlayerManager() {
		return (DedicatedPlayerManager)super.getPlayerManager();
	}

	@Override
	public boolean isRemote() {
		return true;
	}

	@Override
	public String getHostname() {
		return this.getServerIp();
	}

	@Override
	public int getPort() {
		return this.getServerPort();
	}

	@Override
	public String getMotd() {
		return this.getServerMotd();
	}

	public void createGui() {
		if (this.gui == null) {
			this.gui = DedicatedServerGui.create(this);
		}
	}

	@Override
	public boolean hasGui() {
		return this.gui != null;
	}

	@Override
	public boolean openToLan(GameMode gameMode, boolean cheatsAllowed, int port) {
		return false;
	}

	@Override
	public boolean areCommandBlocksEnabled() {
		return this.getProperties().enableCommandBlock;
	}

	@Override
	public int getSpawnProtectionRadius() {
		return this.getProperties().spawnProtection;
	}

	@Override
	public boolean isSpawnProtected(World world, BlockPos pos, PlayerEntity player) {
		if (world.dimension.getType() != DimensionType.OVERWORLD) {
			return false;
		} else if (this.getPlayerManager().getOpList().isEmpty()) {
			return false;
		} else if (this.getPlayerManager().isOperator(player.getGameProfile())) {
			return false;
		} else if (this.getSpawnProtectionRadius() <= 0) {
			return false;
		} else {
			BlockPos blockPos = world.getSpawnPos();
			int i = MathHelper.abs(pos.getX() - blockPos.getX());
			int j = MathHelper.abs(pos.getZ() - blockPos.getZ());
			int k = Math.max(i, j);
			return k <= this.getSpawnProtectionRadius();
		}
	}

	@Override
	public int getOpPermissionLevel() {
		return this.getProperties().opPermissionLevel;
	}

	@Override
	public int getFunctionPermissionLevel() {
		return this.getProperties().functionPermissionLevel;
	}

	@Override
	public void setPlayerIdleTimeout(int playerIdleTimeout) {
		super.setPlayerIdleTimeout(playerIdleTimeout);
		this.propertiesLoader.apply(serverPropertiesHandler -> serverPropertiesHandler.playerIdleTimeout.set(playerIdleTimeout));
	}

	@Override
	public boolean shouldBroadcastRconToOps() {
		return this.getProperties().broadcastRconToOps;
	}

	@Override
	public boolean shouldBroadcastConsoleToOps() {
		return this.getProperties().broadcastConsoleToOps;
	}

	@Override
	public int getMaxWorldBorderRadius() {
		return this.getProperties().maxWorldSize;
	}

	@Override
	public int getNetworkCompressionThreshold() {
		return this.getProperties().networkCompressionThreshold;
	}

	protected boolean convertData() {
		boolean bl = false;

		for (int i = 0; !bl && i <= 2; i++) {
			if (i > 0) {
				LOGGER.warn("Encountered a problem while converting the user banlist, retrying in a few seconds");
				this.sleepFiveSeconds();
			}

			bl = ServerConfigHandler.convertBannedPlayers(this);
		}

		boolean bl2 = false;

		for (int var7 = 0; !bl2 && var7 <= 2; var7++) {
			if (var7 > 0) {
				LOGGER.warn("Encountered a problem while converting the ip banlist, retrying in a few seconds");
				this.sleepFiveSeconds();
			}

			bl2 = ServerConfigHandler.convertBannedIps(this);
		}

		boolean bl3 = false;

		for (int var8 = 0; !bl3 && var8 <= 2; var8++) {
			if (var8 > 0) {
				LOGGER.warn("Encountered a problem while converting the op list, retrying in a few seconds");
				this.sleepFiveSeconds();
			}

			bl3 = ServerConfigHandler.convertOperators(this);
		}

		boolean bl4 = false;

		for (int var9 = 0; !bl4 && var9 <= 2; var9++) {
			if (var9 > 0) {
				LOGGER.warn("Encountered a problem while converting the whitelist, retrying in a few seconds");
				this.sleepFiveSeconds();
			}

			bl4 = ServerConfigHandler.convertWhitelist(this);
		}

		boolean bl5 = false;

		for (int var10 = 0; !bl5 && var10 <= 2; var10++) {
			if (var10 > 0) {
				LOGGER.warn("Encountered a problem while converting the player save files, retrying in a few seconds");
				this.sleepFiveSeconds();
			}

			bl5 = ServerConfigHandler.convertPlayerFiles(this);
		}

		return bl || bl2 || bl3 || bl4 || bl5;
	}

	private void sleepFiveSeconds() {
		try {
			Thread.sleep(5000L);
		} catch (InterruptedException var2) {
		}
	}

	public long getMaxTickTime() {
		return this.getProperties().maxTickTime;
	}

	@Override
	public String getPlugins() {
		return "";
	}

	@Override
	public String executeRconCommand(String string) {
		this.rconCommandOutput.clear();
		this.submitAndJoin(() -> this.getCommandManager().execute(this.rconCommandOutput.createReconCommandSource(), string));
		return this.rconCommandOutput.asString();
	}

	public void setUseWhitelist(boolean bl) {
		this.propertiesLoader.apply(serverPropertiesHandler -> serverPropertiesHandler.whiteList.set(bl));
	}

	@Override
	public void shutdown() {
		super.shutdown();
		Util.shutdownServerWorkerExecutor();
	}

	@Override
	public boolean isHost(GameProfile profile) {
		return false;
	}

	@Override
	public String getLevelName() {
		return this.session.getDirectoryName();
	}

	@Override
	public boolean syncChunkWrites() {
		return this.propertiesLoader.getPropertiesHandler().syncChunkWrites;
	}
}
