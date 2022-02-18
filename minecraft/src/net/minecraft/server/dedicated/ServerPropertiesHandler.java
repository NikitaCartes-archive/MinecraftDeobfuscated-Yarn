package net.minecraft.server.dedicated;

import com.google.gson.JsonObject;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.gen.GeneratorOptions;

public class ServerPropertiesHandler extends AbstractPropertiesHandler<ServerPropertiesHandler> {
	public final boolean onlineMode = this.parseBoolean("online-mode", true);
	public final boolean preventProxyConnections = this.parseBoolean("prevent-proxy-connections", false);
	public final String serverIp = this.getString("server-ip", "");
	public final boolean spawnAnimals = this.parseBoolean("spawn-animals", true);
	public final boolean spawnNpcs = this.parseBoolean("spawn-npcs", true);
	public final boolean pvp = this.parseBoolean("pvp", true);
	public final boolean allowFlight = this.parseBoolean("allow-flight", false);
	public final String resourcePack = this.getString("resource-pack", "");
	public final boolean requireResourcePack = this.parseBoolean("require-resource-pack", false);
	public final String resourcePackPrompt = this.getString("resource-pack-prompt", "");
	public final String motd = this.getString("motd", "A Minecraft Server");
	public final boolean forceGameMode = this.parseBoolean("force-gamemode", false);
	public final boolean enforceWhitelist = this.parseBoolean("enforce-whitelist", false);
	public final Difficulty difficulty = this.get("difficulty", combineParser(Difficulty::byOrdinal, Difficulty::byName), Difficulty::getName, Difficulty.EASY);
	public final GameMode gameMode = this.get("gamemode", combineParser(GameMode::byId, GameMode::byName), GameMode::getName, GameMode.SURVIVAL);
	public final String levelName = this.getString("level-name", "world");
	public final int serverPort = this.getInt("server-port", 25565);
	@Nullable
	public final Boolean announcePlayerAchievements = this.getDeprecatedBoolean("announce-player-achievements");
	public final boolean enableQuery = this.parseBoolean("enable-query", false);
	public final int queryPort = this.getInt("query.port", 25565);
	public final boolean enableRcon = this.parseBoolean("enable-rcon", false);
	public final int rconPort = this.getInt("rcon.port", 25575);
	public final String rconPassword = this.getString("rcon.password", "");
	@Nullable
	public final String resourcePackHash = this.getDeprecatedString("resource-pack-hash");
	public final String resourcePackSha1 = this.getString("resource-pack-sha1", "");
	public final boolean hardcore = this.parseBoolean("hardcore", false);
	public final boolean allowNether = this.parseBoolean("allow-nether", true);
	public final boolean spawnMonsters = this.parseBoolean("spawn-monsters", true);
	public final boolean useNativeTransport = this.parseBoolean("use-native-transport", true);
	public final boolean enableCommandBlock = this.parseBoolean("enable-command-block", false);
	public final int spawnProtection = this.getInt("spawn-protection", 16);
	public final int opPermissionLevel = this.getInt("op-permission-level", 4);
	public final int functionPermissionLevel = this.getInt("function-permission-level", 2);
	public final long maxTickTime = this.parseLong("max-tick-time", TimeUnit.MINUTES.toMillis(1L));
	public final int rateLimit = this.getInt("rate-limit", 0);
	public final int viewDistance = this.getInt("view-distance", 10);
	public final int simulationDistance = this.getInt("simulation-distance", 10);
	public final int maxPlayers = this.getInt("max-players", 20);
	public final int networkCompressionThreshold = this.getInt("network-compression-threshold", 256);
	public final boolean broadcastRconToOps = this.parseBoolean("broadcast-rcon-to-ops", true);
	public final boolean broadcastConsoleToOps = this.parseBoolean("broadcast-console-to-ops", true);
	public final int maxWorldSize = this.transformedParseInt("max-world-size", maxWorldSize -> MathHelper.clamp(maxWorldSize, 1, 29999984), 29999984);
	public final boolean syncChunkWrites = this.parseBoolean("sync-chunk-writes", true);
	public final boolean enableJmxMonitoring = this.parseBoolean("enable-jmx-monitoring", false);
	public final boolean enableStatus = this.parseBoolean("enable-status", true);
	public final boolean hideOnlinePlayers = this.parseBoolean("hide-online-players", false);
	public final int entityBroadcastRangePercentage = this.transformedParseInt(
		"entity-broadcast-range-percentage", percentage -> MathHelper.clamp(percentage, 10, 1000), 100
	);
	public final String textFilteringConfig = this.getString("text-filtering-config", "");
	public final AbstractPropertiesHandler<ServerPropertiesHandler>.PropertyAccessor<Integer> playerIdleTimeout = this.intAccessor("player-idle-timeout", 0);
	public final AbstractPropertiesHandler<ServerPropertiesHandler>.PropertyAccessor<Boolean> whiteList = this.booleanAccessor("white-list", false);
	private final ServerPropertiesHandler.class_7044 field_37039 = new ServerPropertiesHandler.class_7044(
		this.getString("level-seed", ""),
		this.get("generator-settings", JsonHelper::deserialize, new JsonObject()),
		this.parseBoolean("generate-structures", true),
		this.get("level-type", string -> string.toLowerCase(Locale.ROOT), "default")
	);
	@Nullable
	private GeneratorOptions generatorOptions;

	public ServerPropertiesHandler(Properties properties) {
		super(properties);
	}

	public static ServerPropertiesHandler load(Path path) {
		return new ServerPropertiesHandler(loadProperties(path));
	}

	protected ServerPropertiesHandler create(DynamicRegistryManager dynamicRegistryManager, Properties properties) {
		ServerPropertiesHandler serverPropertiesHandler = new ServerPropertiesHandler(properties);
		serverPropertiesHandler.getGeneratorOptions(dynamicRegistryManager);
		return serverPropertiesHandler;
	}

	public GeneratorOptions getGeneratorOptions(DynamicRegistryManager registryManager) {
		if (this.generatorOptions == null) {
			this.generatorOptions = GeneratorOptions.fromProperties(registryManager, this.field_37039);
		}

		return this.generatorOptions;
	}

	public static record class_7044(String levelSeed, JsonObject generatorSettings, boolean generateStructures, String levelType) {
	}
}
