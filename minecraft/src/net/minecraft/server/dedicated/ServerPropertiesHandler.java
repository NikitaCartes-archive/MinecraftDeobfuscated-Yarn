package net.minecraft.server.dedicated;

import com.google.common.base.Strings;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.server.MinecraftServer;
import net.minecraft.structure.StructureSet;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.WorldPreset;
import net.minecraft.world.gen.WorldPresets;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import org.slf4j.Logger;

public class ServerPropertiesHandler extends AbstractPropertiesHandler<ServerPropertiesHandler> {
	static final Logger field_37276 = LogUtils.getLogger();
	private static final Pattern SHA1_PATTERN = Pattern.compile("^[a-fA-F0-9]{40}$");
	public final boolean onlineMode = this.parseBoolean("online-mode", true);
	public final boolean preventProxyConnections = this.parseBoolean("prevent-proxy-connections", false);
	public final String serverIp = this.getString("server-ip", "");
	public final boolean spawnAnimals = this.parseBoolean("spawn-animals", true);
	public final boolean spawnNpcs = this.parseBoolean("spawn-npcs", true);
	public final boolean pvp = this.parseBoolean("pvp", true);
	public final boolean allowFlight = this.parseBoolean("allow-flight", false);
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
	public final boolean hardcore = this.parseBoolean("hardcore", false);
	public final boolean allowNether = this.parseBoolean("allow-nether", true);
	public final boolean spawnMonsters = this.parseBoolean("spawn-monsters", true);
	public final boolean useNativeTransport = this.parseBoolean("use-native-transport", true);
	public final boolean enableCommandBlock = this.parseBoolean("enable-command-block", false);
	public final int spawnProtection = this.getInt("spawn-protection", 16);
	public final int opPermissionLevel = this.getInt("op-permission-level", 4);
	public final int functionPermissionLevel = this.getInt("function-permission-level", 2);
	public final long maxTickTime = this.parseLong("max-tick-time", TimeUnit.MINUTES.toMillis(1L));
	public final int maxChainedNeighborUpdates = this.getInt("max-chained-neighbor-updates", 1000000);
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
	public Optional<MinecraftServer.ServerResourcePackProperties> serverResourcePackProperties;
	public final boolean previewsChat = this.parseBoolean("previews-chat", false);
	public final AbstractPropertiesHandler<ServerPropertiesHandler>.PropertyAccessor<Integer> playerIdleTimeout = this.intAccessor("player-idle-timeout", 0);
	public final AbstractPropertiesHandler<ServerPropertiesHandler>.PropertyAccessor<Boolean> whiteList = this.booleanAccessor("white-list", false);
	public final boolean enforceSecureProfile = this.parseBoolean("enforce-secure-profile", true);
	private final ServerPropertiesHandler.WorldGenProperties worldGenProperties = new ServerPropertiesHandler.WorldGenProperties(
		this.getString("level-seed", ""),
		this.get("generator-settings", generatorSettings -> JsonHelper.deserialize(!generatorSettings.isEmpty() ? generatorSettings : "{}"), new JsonObject()),
		this.parseBoolean("generate-structures", true),
		this.get("level-type", type -> type.toLowerCase(Locale.ROOT), WorldPresets.DEFAULT.getValue().toString())
	);
	@Nullable
	private GeneratorOptions generatorOptions;

	public ServerPropertiesHandler(Properties properties) {
		super(properties);
		this.serverResourcePackProperties = getServerResourcePackProperties(
			this.getString("resource-pack", ""),
			this.getString("resource-pack-sha1", ""),
			this.getDeprecatedString("resource-pack-hash"),
			this.parseBoolean("require-resource-pack", false),
			this.getString("resource-pack-prompt", "")
		);
	}

	public static ServerPropertiesHandler load(Path path) {
		return new ServerPropertiesHandler(loadProperties(path));
	}

	protected ServerPropertiesHandler create(DynamicRegistryManager dynamicRegistryManager, Properties properties) {
		ServerPropertiesHandler serverPropertiesHandler = new ServerPropertiesHandler(properties);
		serverPropertiesHandler.getGeneratorOptions(dynamicRegistryManager);
		return serverPropertiesHandler;
	}

	@Nullable
	private static Text parseResourcePackPrompt(String prompt) {
		if (!Strings.isNullOrEmpty(prompt)) {
			try {
				return Text.Serializer.fromJson(prompt);
			} catch (Exception var2) {
				field_37276.warn("Failed to parse resource pack prompt '{}'", prompt, var2);
			}
		}

		return null;
	}

	private static Optional<MinecraftServer.ServerResourcePackProperties> getServerResourcePackProperties(
		String url, String sha1, @Nullable String hash, boolean required, String prompt
	) {
		if (url.isEmpty()) {
			return Optional.empty();
		} else {
			String string;
			if (!sha1.isEmpty()) {
				string = sha1;
				if (!Strings.isNullOrEmpty(hash)) {
					field_37276.warn("resource-pack-hash is deprecated and found along side resource-pack-sha1. resource-pack-hash will be ignored.");
				}
			} else if (!Strings.isNullOrEmpty(hash)) {
				field_37276.warn("resource-pack-hash is deprecated. Please use resource-pack-sha1 instead.");
				string = hash;
			} else {
				string = "";
			}

			if (string.isEmpty()) {
				field_37276.warn("You specified a resource pack without providing a sha1 hash. Pack will be updated on the client only if you change the name of the pack.");
			} else if (!SHA1_PATTERN.matcher(string).matches()) {
				field_37276.warn("Invalid sha1 for resource-pack-sha1");
			}

			Text text = parseResourcePackPrompt(prompt);
			return Optional.of(new MinecraftServer.ServerResourcePackProperties(url, string, required, text));
		}
	}

	public GeneratorOptions getGeneratorOptions(DynamicRegistryManager dynamicRegistryManager) {
		if (this.generatorOptions == null) {
			this.generatorOptions = this.worldGenProperties.createGeneratorOptions(dynamicRegistryManager);
		}

		return this.generatorOptions;
	}

	public static record WorldGenProperties(String levelSeed, JsonObject generatorSettings, boolean generateStructures, String levelType) {
		private static final Map<String, RegistryKey<WorldPreset>> LEVEL_TYPE_TO_PRESET_KEY = Map.of(
			"default", WorldPresets.DEFAULT, "largebiomes", WorldPresets.LARGE_BIOMES
		);

		public GeneratorOptions createGeneratorOptions(DynamicRegistryManager dynamicRegistryManager) {
			long l = GeneratorOptions.parseSeed(this.levelSeed()).orElse(Random.create().nextLong());
			Registry<WorldPreset> registry = dynamicRegistryManager.get(Registry.WORLD_PRESET_KEY);
			RegistryEntry<WorldPreset> registryEntry = (RegistryEntry<WorldPreset>)registry.getEntry(WorldPresets.DEFAULT)
				.or(() -> registry.streamEntries().findAny())
				.orElseThrow(() -> new IllegalStateException("Invalid datapack contents: can't find default preset"));
			RegistryEntry<WorldPreset> registryEntry2 = (RegistryEntry<WorldPreset>)Optional.ofNullable(Identifier.tryParse(this.levelType))
				.map(levelTypeId -> RegistryKey.of(Registry.WORLD_PRESET_KEY, levelTypeId))
				.or(() -> Optional.ofNullable((RegistryKey)LEVEL_TYPE_TO_PRESET_KEY.get(this.levelType)))
				.flatMap(registry::getEntry)
				.orElseGet(
					() -> {
						ServerPropertiesHandler.field_37276
							.warn(
								"Failed to parse level-type {}, defaulting to {}", this.levelType, registryEntry.getKey().map(key -> key.getValue().toString()).orElse("[unnamed]")
							);
						return registryEntry;
					}
				);
			GeneratorOptions generatorOptions = registryEntry2.value().createGeneratorOptions(l, this.generateStructures, false);
			if (registryEntry2.matchesKey(WorldPresets.FLAT)) {
				RegistryOps<JsonElement> registryOps = RegistryOps.of(JsonOps.INSTANCE, dynamicRegistryManager);
				Optional<FlatChunkGeneratorConfig> optional = FlatChunkGeneratorConfig.CODEC
					.parse(new Dynamic<>(registryOps, this.generatorSettings()))
					.resultOrPartial(ServerPropertiesHandler.field_37276::error);
				if (optional.isPresent()) {
					Registry<StructureSet> registry2 = dynamicRegistryManager.get(Registry.STRUCTURE_SET_KEY);
					return GeneratorOptions.create(dynamicRegistryManager, generatorOptions, new FlatChunkGenerator(registry2, (FlatChunkGeneratorConfig)optional.get()));
				}
			}

			return generatorOptions;
		}
	}
}
