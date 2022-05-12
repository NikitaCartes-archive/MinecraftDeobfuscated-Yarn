package net.minecraft.server.dedicated;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.InetAddress;
import java.net.Proxy;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.network.ChatDecorator;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.SaveLoader;
import net.minecraft.server.ServerConfigHandler;
import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.dedicated.gui.DedicatedServerGui;
import net.minecraft.server.filter.TextFilterer;
import net.minecraft.server.filter.TextStream;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.rcon.QueryResponseHandler;
import net.minecraft.server.rcon.RconCommandOutput;
import net.minecraft.server.rcon.RconListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.SystemDetails;
import net.minecraft.util.UserCache;
import net.minecraft.util.Util;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.logging.UncaughtExceptionHandler;
import net.minecraft.util.logging.UncaughtExceptionLogger;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.level.storage.LevelStorage;
import org.slf4j.Logger;

public class MinecraftDedicatedServer extends MinecraftServer implements DedicatedServer {
	static final Logger LOGGER = LogUtils.getLogger();
	private static final int field_29662 = 5000;
	private static final int field_29663 = 2;
	private final List<PendingServerCommand> commandQueue = Collections.synchronizedList(Lists.newArrayList());
	@Nullable
	private QueryResponseHandler queryResponseHandler;
	private final RconCommandOutput rconCommandOutput;
	@Nullable
	private RconListener rconServer;
	private final ServerPropertiesLoader propertiesLoader;
	@Nullable
	private DedicatedServerGui gui;
	@Nullable
	private final TextFilterer filterer;
	private final ChatDecorator chatDecorator;

	public MinecraftDedicatedServer(
		Thread serverThread,
		LevelStorage.Session session,
		ResourcePackManager dataPackManager,
		SaveLoader saveLoader,
		ServerPropertiesLoader propertiesLoader,
		DataFixer dataFixer,
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
			Proxy.NO_PROXY,
			dataFixer,
			sessionService,
			gameProfileRepo,
			userCache,
			worldGenerationProgressListenerFactory
		);
		this.propertiesLoader = propertiesLoader;
		this.rconCommandOutput = new RconCommandOutput(this);
		this.filterer = TextFilterer.load(propertiesLoader.getPropertiesHandler().textFilteringConfig);
		this.chatDecorator = this.getProperties().testRainbowChat ? ChatDecorator.testRainbowChat() : ChatDecorator.NOOP;
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
		LOGGER.info("Starting minecraft server version {}", SharedConstants.getGameVersion().getName());
		if (Runtime.getRuntime().maxMemory() / 1024L / 1024L < 512L) {
			LOGGER.warn("To start the server with more ram, launch it as \"java -Xmx1024M -Xms1024M -jar minecraft_server.jar\"");
		}

		LOGGER.info("Loading properties");
		ServerPropertiesHandler serverPropertiesHandler = this.propertiesLoader.getPropertiesHandler();
		if (this.isSingleplayer()) {
			this.setServerIp("127.0.0.1");
		} else {
			this.setOnlineMode(serverPropertiesHandler.onlineMode);
			this.setPreventProxyConnections(serverPropertiesHandler.preventProxyConnections);
			this.setServerIp(serverPropertiesHandler.serverIp);
		}

		this.setPvpEnabled(serverPropertiesHandler.pvp);
		this.setFlightEnabled(serverPropertiesHandler.allowFlight);
		this.setMotd(serverPropertiesHandler.motd);
		super.setPlayerIdleTimeout(serverPropertiesHandler.playerIdleTimeout.get());
		this.setEnforceWhitelist(serverPropertiesHandler.enforceWhitelist);
		this.saveProperties.setGameMode(serverPropertiesHandler.gameMode);
		LOGGER.info("Default game type: {}", serverPropertiesHandler.gameMode);
		InetAddress inetAddress = null;
		if (!this.getServerIp().isEmpty()) {
			inetAddress = InetAddress.getByName(this.getServerIp());
		}

		if (this.getServerPort() < 0) {
			this.setServerPort(serverPropertiesHandler.serverPort);
		}

		this.generateKeyPair();
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
			this.setPlayerManager(new DedicatedPlayerManager(this, this.getRegistryManager(), this.saveHandler));
			long l = Util.getMeasuringTimeNano();
			SkullBlockEntity.setServices(this.getUserCache(), this.getSessionService(), this);
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
				this.queryResponseHandler = QueryResponseHandler.create(this);
			}

			if (serverPropertiesHandler.enableRcon) {
				LOGGER.info("Starting remote control listener");
				this.rconServer = RconListener.create(this);
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
				LOGGER.info("JMX monitoring enabled");
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

	@Override
	public ServerPropertiesHandler getProperties() {
		return this.propertiesLoader.getPropertiesHandler();
	}

	@Override
	public void updateDifficulty() {
		this.setDifficulty(this.getProperties().difficulty, true);
	}

	@Override
	public boolean isHardcore() {
		return this.getProperties().hardcore;
	}

	@Override
	public SystemDetails addExtraSystemDetails(SystemDetails details) {
		details.addSection("Is Modded", (Supplier<String>)(() -> this.getModStatus().getMessage()));
		details.addSection("Type", (Supplier<String>)(() -> "Dedicated Server (map_server.txt)"));
		return details;
	}

	@Override
	public void dumpProperties(Path file) throws IOException {
		ServerPropertiesHandler serverPropertiesHandler = this.getProperties();
		Writer writer = Files.newBufferedWriter(file);

		try {
			writer.write(String.format("sync-chunk-writes=%s%n", serverPropertiesHandler.syncChunkWrites));
			writer.write(String.format("gamemode=%s%n", serverPropertiesHandler.gameMode));
			writer.write(String.format("spawn-monsters=%s%n", serverPropertiesHandler.spawnMonsters));
			writer.write(String.format("entity-broadcast-range-percentage=%d%n", serverPropertiesHandler.entityBroadcastRangePercentage));
			writer.write(String.format("max-world-size=%d%n", serverPropertiesHandler.maxWorldSize));
			writer.write(String.format("spawn-npcs=%s%n", serverPropertiesHandler.spawnNpcs));
			writer.write(String.format("view-distance=%d%n", serverPropertiesHandler.viewDistance));
			writer.write(String.format("simulation-distance=%d%n", serverPropertiesHandler.simulationDistance));
			writer.write(String.format("spawn-animals=%s%n", serverPropertiesHandler.spawnAnimals));
			writer.write(String.format("generate-structures=%s%n", serverPropertiesHandler.getGeneratorOptions(this.getRegistryManager()).shouldGenerateStructures()));
			writer.write(String.format("use-native=%s%n", serverPropertiesHandler.useNativeTransport));
			writer.write(String.format("rate-limit=%d%n", serverPropertiesHandler.rateLimit));
		} catch (Throwable var7) {
			if (writer != null) {
				try {
					writer.close();
				} catch (Throwable var6) {
					var7.addSuppressed(var6);
				}
			}

			throw var7;
		}

		if (writer != null) {
			writer.close();
		}
	}

	@Override
	public void exit() {
		if (this.filterer != null) {
			this.filterer.close();
		}

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

	public void enqueueCommand(String command, ServerCommandSource commandSource) {
		this.commandQueue.add(new PendingServerCommand(command, commandSource));
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
	public int getRateLimit() {
		return this.getProperties().rateLimit;
	}

	@Override
	public boolean isUsingNativeTransport() {
		return this.getProperties().useNativeTransport;
	}

	@Override
	public boolean shouldPreviewChat() {
		return this.getProperties().previewsChat;
	}

	@Override
	public ChatDecorator getChatDecorator() {
		return this.chatDecorator;
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
	public boolean areCommandBlocksEnabled() {
		return this.getProperties().enableCommandBlock;
	}

	@Override
	public int getSpawnProtectionRadius() {
		return this.getProperties().spawnProtection;
	}

	@Override
	public boolean isSpawnProtected(ServerWorld world, BlockPos pos, PlayerEntity player) {
		if (world.getRegistryKey() != World.OVERWORLD) {
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
	public boolean acceptsStatusQuery() {
		return this.getProperties().enableStatus;
	}

	@Override
	public boolean hideOnlinePlayers() {
		return this.getProperties().hideOnlinePlayers;
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
		this.propertiesLoader.apply(serverPropertiesHandler -> serverPropertiesHandler.playerIdleTimeout.set(this.getRegistryManager(), playerIdleTimeout));
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

	@Override
	public boolean shouldEnforceSecureProfile() {
		return this.getProperties().enforceSecureProfile;
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
	public int getMaxChainedNeighborUpdates() {
		return this.getProperties().maxChainedNeighborUpdates;
	}

	@Override
	public String getPlugins() {
		return "";
	}

	@Override
	public String executeRconCommand(String command) {
		this.rconCommandOutput.clear();
		this.submitAndJoin(() -> this.getCommandManager().execute(this.rconCommandOutput.createRconCommandSource(), command));
		return this.rconCommandOutput.asString();
	}

	public void setUseWhitelist(boolean useWhitelist) {
		this.propertiesLoader.apply(serverPropertiesHandler -> serverPropertiesHandler.whiteList.set(this.getRegistryManager(), useWhitelist));
	}

	@Override
	public void shutdown() {
		super.shutdown();
		Util.shutdownExecutors();
		SkullBlockEntity.clearServices();
	}

	@Override
	public boolean isHost(GameProfile profile) {
		return false;
	}

	@Override
	public int adjustTrackingDistance(int initialDistance) {
		return this.getProperties().entityBroadcastRangePercentage * initialDistance / 100;
	}

	@Override
	public String getLevelName() {
		return this.session.getDirectoryName();
	}

	@Override
	public boolean syncChunkWrites() {
		return this.propertiesLoader.getPropertiesHandler().syncChunkWrites;
	}

	@Override
	public TextStream createFilterer(ServerPlayerEntity player) {
		return this.filterer != null ? this.filterer.createFilterer(player.getGameProfile()) : TextStream.UNFILTERED;
	}

	@Nullable
	@Override
	public GameMode getForcedGameMode() {
		return this.propertiesLoader.getPropertiesHandler().forceGameMode ? this.saveProperties.getGameMode() : null;
	}

	@Override
	public Optional<MinecraftServer.ServerResourcePackProperties> getResourcePackProperties() {
		return this.propertiesLoader.getPropertiesHandler().serverResourcePackProperties;
	}
}
