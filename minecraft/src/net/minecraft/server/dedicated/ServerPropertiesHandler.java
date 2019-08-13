package net.minecraft.server.dedicated;

import java.nio.file.Path;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.level.LevelGeneratorType;

public class ServerPropertiesHandler extends AbstractPropertiesHandler<ServerPropertiesHandler> {
	public final boolean onlineMode = this.parseBoolean("online-mode", true);
	public final boolean preventProxyConnections = this.parseBoolean("prevent-proxy-connections", false);
	public final String serverIp = this.getString("server-ip", "");
	public final boolean spawnAnimals = this.parseBoolean("spawn-animals", true);
	public final boolean spawnNpcs = this.parseBoolean("spawn-npcs", true);
	public final boolean pvp = this.parseBoolean("pvp", true);
	public final boolean allowFlight = this.parseBoolean("allow-flight", false);
	public final String resourcePack = this.getString("resource-pack", "");
	public final String motd = this.getString("motd", "A Minecraft Server");
	public final boolean forceGameMode = this.parseBoolean("force-gamemode", false);
	public final boolean enforceWhitelist = this.parseBoolean("enforce-whitelist", false);
	public final boolean generateStructures = this.parseBoolean("generate-structures", true);
	public final Difficulty difficulty = this.get(
		"difficulty", wrapIntParsingFunction(Difficulty::byOrdinal, Difficulty::byName), Difficulty::getName, Difficulty.field_5805
	);
	public final GameMode gameMode = this.get("gamemode", wrapIntParsingFunction(GameMode::byId, GameMode::byName), GameMode::getName, GameMode.field_9215);
	public final String levelName = this.getString("level-name", "world");
	public final String levelSeed = this.getString("level-seed", "");
	public final LevelGeneratorType levelType = this.get(
		"level-type", LevelGeneratorType::getTypeFromName, LevelGeneratorType::getName, LevelGeneratorType.DEFAULT
	);
	public final String generatorSettings = this.getString("generator-settings", "");
	public final int serverPort = this.getInt("server-port", 25565);
	public final int maxBuildHeight = this.parseIntWithOperation("max-build-height", integer -> MathHelper.clamp((integer + 8) / 16 * 16, 64, 256), 256);
	public final Boolean announcePlayerAchievements = this.getDeprecatedBoolean("announce-player-achievements");
	public final boolean enableQuery = this.parseBoolean("enable-query", false);
	public final int queryPort = this.getInt("query.port", 25565);
	public final boolean enableRcon = this.parseBoolean("enable-rcon", false);
	public final int rconPort = this.getInt("rcon.port", 25575);
	public final String rconPassword = this.getString("rcon.password", "");
	public final String resourcePackHash = this.getDeprecatedString("resource-pack-hash");
	public final String resourcePackSha1 = this.getString("resource-pack-sha1", "");
	public final boolean hardcore = this.parseBoolean("hardcore", false);
	public final boolean allowNether = this.parseBoolean("allow-nether", true);
	public final boolean spawnMonsters = this.parseBoolean("spawn-monsters", true);
	public final boolean snooperEnabled;
	public final boolean useNativeTransport;
	public final boolean enableCommandBlock;
	public final int spawnProtection;
	public final int opPermissionLevel;
	public final int field_20324;
	public final long maxTickTime;
	public final int viewDistance;
	public final int maxPlayers;
	public final int networkCompressionThreshold;
	public final boolean broadcastRconToOps;
	public final boolean broadcastConsoleToOps;
	public final int maxWorldSize;
	public final AbstractPropertiesHandler<ServerPropertiesHandler>.PropertyAccessor<Integer> playerIdleTimeout;
	public final AbstractPropertiesHandler<ServerPropertiesHandler>.PropertyAccessor<Boolean> whiteList;

	public ServerPropertiesHandler(Properties properties) {
		super(properties);
		if (this.parseBoolean("snooper-enabled", true)) {
		}

		this.snooperEnabled = false;
		this.useNativeTransport = this.parseBoolean("use-native-transport", true);
		this.enableCommandBlock = this.parseBoolean("enable-command-block", false);
		this.spawnProtection = this.getInt("spawn-protection", 16);
		this.opPermissionLevel = this.getInt("op-permission-level", 4);
		this.field_20324 = this.getInt("function-permission-level", 2);
		this.maxTickTime = this.parseLong("max-tick-time", TimeUnit.MINUTES.toMillis(1L));
		this.viewDistance = this.getInt("view-distance", 10);
		this.maxPlayers = this.getInt("max-players", 20);
		this.networkCompressionThreshold = this.getInt("network-compression-threshold", 256);
		this.broadcastRconToOps = this.parseBoolean("broadcast-rcon-to-ops", true);
		this.broadcastConsoleToOps = this.parseBoolean("broadcast-console-to-ops", true);
		this.maxWorldSize = this.parseIntWithOperation("max-world-size", integer -> MathHelper.clamp(integer, 1, 29999984), 29999984);
		this.playerIdleTimeout = this.intAccessor("player-idle-timeout", 0);
		this.whiteList = this.booleanAccessor("white-list", false);
	}

	public static ServerPropertiesHandler load(Path path) {
		return new ServerPropertiesHandler(load(path));
	}

	protected ServerPropertiesHandler method_16713(Properties properties) {
		return new ServerPropertiesHandler(properties);
	}
}
