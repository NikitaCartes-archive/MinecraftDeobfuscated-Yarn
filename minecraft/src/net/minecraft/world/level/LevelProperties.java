package net.minecraft.world.level;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.dynamic.DynamicSerializableUuid;
import net.minecraft.util.dynamic.RegistryReadingOps;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.storage.SaveVersionInfo;
import net.minecraft.world.timer.Timer;
import net.minecraft.world.timer.TimerCallbackSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LevelProperties implements ServerWorldProperties, SaveProperties {
	private static final Logger LOGGER = LogManager.getLogger();
	private LevelInfo levelInfo;
	private final GeneratorOptions generatorOptions;
	private final Lifecycle lifecycle;
	private int spawnX;
	private int spawnY;
	private int spawnZ;
	private float spawnAngle;
	private long time;
	private long timeOfDay;
	@Nullable
	private final DataFixer dataFixer;
	private final int dataVersion;
	private boolean playerDataLoaded;
	@Nullable
	private CompoundTag playerData;
	private final int version;
	private int clearWeatherTime;
	private boolean raining;
	private int rainTime;
	private boolean thundering;
	private int thunderTime;
	private boolean initialized;
	private boolean difficultyLocked;
	private WorldBorder.Properties worldBorder;
	private CompoundTag dragonFight;
	@Nullable
	private CompoundTag customBossEvents;
	private int wanderingTraderSpawnDelay;
	private int wanderingTraderSpawnChance;
	@Nullable
	private UUID wanderingTraderId;
	private final Set<String> serverBrands;
	private boolean modded;
	private final Timer<MinecraftServer> scheduledEvents;

	private LevelProperties(
		@Nullable DataFixer dataFixer,
		int dataVersion,
		@Nullable CompoundTag playerData,
		boolean modded,
		int spawnX,
		int spawnY,
		int spawnZ,
		float spawnAngle,
		long time,
		long timeOfDay,
		int version,
		int clearWeatherTime,
		int rainTime,
		boolean raining,
		int thunderTime,
		boolean thundering,
		boolean initialized,
		boolean difficultyLocked,
		WorldBorder.Properties worldBorder,
		int wanderingTraderSpawnDelay,
		int wanderingTraderSpawnChance,
		@Nullable UUID wanderingTraderId,
		Set<String> serverBrands,
		Timer<MinecraftServer> scheduledEvents,
		@Nullable CompoundTag customBossEvents,
		CompoundTag dragonFight,
		LevelInfo levelInfo,
		GeneratorOptions generatorOptions,
		Lifecycle lifecycle
	) {
		this.dataFixer = dataFixer;
		this.modded = modded;
		this.spawnX = spawnX;
		this.spawnY = spawnY;
		this.spawnZ = spawnZ;
		this.spawnAngle = spawnAngle;
		this.time = time;
		this.timeOfDay = timeOfDay;
		this.version = version;
		this.clearWeatherTime = clearWeatherTime;
		this.rainTime = rainTime;
		this.raining = raining;
		this.thunderTime = thunderTime;
		this.thundering = thundering;
		this.initialized = initialized;
		this.difficultyLocked = difficultyLocked;
		this.worldBorder = worldBorder;
		this.wanderingTraderSpawnDelay = wanderingTraderSpawnDelay;
		this.wanderingTraderSpawnChance = wanderingTraderSpawnChance;
		this.wanderingTraderId = wanderingTraderId;
		this.serverBrands = serverBrands;
		this.playerData = playerData;
		this.dataVersion = dataVersion;
		this.scheduledEvents = scheduledEvents;
		this.customBossEvents = customBossEvents;
		this.dragonFight = dragonFight;
		this.levelInfo = levelInfo;
		this.generatorOptions = generatorOptions;
		this.lifecycle = lifecycle;
	}

	public LevelProperties(LevelInfo levelInfo, GeneratorOptions generatorOptions, Lifecycle lifecycle) {
		this(
			null,
			SharedConstants.getGameVersion().getWorldVersion(),
			null,
			false,
			0,
			0,
			0,
			0.0F,
			0L,
			0L,
			19133,
			0,
			0,
			false,
			0,
			false,
			false,
			false,
			WorldBorder.DEFAULT_BORDER,
			0,
			0,
			null,
			Sets.<String>newLinkedHashSet(),
			new Timer<>(TimerCallbackSerializer.INSTANCE),
			null,
			new CompoundTag(),
			levelInfo.withCopiedGameRules(),
			generatorOptions,
			lifecycle
		);
	}

	public static LevelProperties readProperties(
		Dynamic<Tag> dynamic,
		DataFixer dataFixer,
		int dataVersion,
		@Nullable CompoundTag playerData,
		LevelInfo levelInfo,
		SaveVersionInfo saveVersionInfo,
		GeneratorOptions generatorOptions,
		Lifecycle lifecycle
	) {
		long l = dynamic.get("Time").asLong(0L);
		CompoundTag compoundTag = (CompoundTag)dynamic.get("DragonFight")
			.result()
			.map(Dynamic::getValue)
			.orElseGet(() -> dynamic.get("DimensionData").get("1").get("DragonFight").orElseEmptyMap().getValue());
		return new LevelProperties(
			dataFixer,
			dataVersion,
			playerData,
			dynamic.get("WasModded").asBoolean(false),
			dynamic.get("SpawnX").asInt(0),
			dynamic.get("SpawnY").asInt(0),
			dynamic.get("SpawnZ").asInt(0),
			dynamic.get("SpawnAngle").asFloat(0.0F),
			l,
			dynamic.get("DayTime").asLong(l),
			saveVersionInfo.getLevelFormatVersion(),
			dynamic.get("clearWeatherTime").asInt(0),
			dynamic.get("rainTime").asInt(0),
			dynamic.get("raining").asBoolean(false),
			dynamic.get("thunderTime").asInt(0),
			dynamic.get("thundering").asBoolean(false),
			dynamic.get("initialized").asBoolean(true),
			dynamic.get("DifficultyLocked").asBoolean(false),
			WorldBorder.Properties.fromDynamic(dynamic, WorldBorder.DEFAULT_BORDER),
			dynamic.get("WanderingTraderSpawnDelay").asInt(0),
			dynamic.get("WanderingTraderSpawnChance").asInt(0),
			(UUID)dynamic.get("WanderingTraderId").read(DynamicSerializableUuid.CODEC).result().orElse(null),
			(Set<String>)dynamic.get("ServerBrands")
				.asStream()
				.flatMap(dynamicx -> Util.stream(dynamicx.asString().result()))
				.collect(Collectors.toCollection(Sets::newLinkedHashSet)),
			new Timer<>(TimerCallbackSerializer.INSTANCE, dynamic.get("ScheduledEvents").asStream()),
			(CompoundTag)dynamic.get("CustomBossEvents").orElseEmptyMap().getValue(),
			compoundTag,
			levelInfo,
			generatorOptions,
			lifecycle
		);
	}

	@Override
	public CompoundTag cloneWorldNbt(DynamicRegistryManager registryManager, @Nullable CompoundTag playerTag) {
		this.loadPlayerData();
		if (playerTag == null) {
			playerTag = this.playerData;
		}

		CompoundTag compoundTag = new CompoundTag();
		this.updateProperties(registryManager, compoundTag, playerTag);
		return compoundTag;
	}

	private void updateProperties(DynamicRegistryManager registryManager, CompoundTag levelTag, @Nullable CompoundTag playerTag) {
		ListTag listTag = new ListTag();
		this.serverBrands.stream().map(StringTag::of).forEach(listTag::add);
		levelTag.put("ServerBrands", listTag);
		levelTag.putBoolean("WasModded", this.modded);
		CompoundTag compoundTag = new CompoundTag();
		compoundTag.putString("Name", SharedConstants.getGameVersion().getName());
		compoundTag.putInt("Id", SharedConstants.getGameVersion().getWorldVersion());
		compoundTag.putBoolean("Snapshot", !SharedConstants.getGameVersion().isStable());
		levelTag.put("Version", compoundTag);
		levelTag.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
		RegistryReadingOps<Tag> registryReadingOps = RegistryReadingOps.of(NbtOps.INSTANCE, registryManager);
		GeneratorOptions.CODEC
			.encodeStart(registryReadingOps, this.generatorOptions)
			.resultOrPartial(Util.addPrefix("WorldGenSettings: ", LOGGER::error))
			.ifPresent(tag -> levelTag.put("WorldGenSettings", tag));
		levelTag.putInt("GameType", this.levelInfo.getGameMode().getId());
		levelTag.putInt("SpawnX", this.spawnX);
		levelTag.putInt("SpawnY", this.spawnY);
		levelTag.putInt("SpawnZ", this.spawnZ);
		levelTag.putFloat("SpawnAngle", this.spawnAngle);
		levelTag.putLong("Time", this.time);
		levelTag.putLong("DayTime", this.timeOfDay);
		levelTag.putLong("LastPlayed", Util.getEpochTimeMs());
		levelTag.putString("LevelName", this.levelInfo.getLevelName());
		levelTag.putInt("version", 19133);
		levelTag.putInt("clearWeatherTime", this.clearWeatherTime);
		levelTag.putInt("rainTime", this.rainTime);
		levelTag.putBoolean("raining", this.raining);
		levelTag.putInt("thunderTime", this.thunderTime);
		levelTag.putBoolean("thundering", this.thundering);
		levelTag.putBoolean("hardcore", this.levelInfo.isHardcore());
		levelTag.putBoolean("allowCommands", this.levelInfo.areCommandsAllowed());
		levelTag.putBoolean("initialized", this.initialized);
		this.worldBorder.writeNbt(levelTag);
		levelTag.putByte("Difficulty", (byte)this.levelInfo.getDifficulty().getId());
		levelTag.putBoolean("DifficultyLocked", this.difficultyLocked);
		levelTag.put("GameRules", this.levelInfo.getGameRules().toNbt());
		levelTag.put("DragonFight", this.dragonFight);
		if (playerTag != null) {
			levelTag.put("Player", playerTag);
		}

		DataPackSettings.CODEC.encodeStart(NbtOps.INSTANCE, this.levelInfo.getDataPackSettings()).result().ifPresent(tag -> levelTag.put("DataPacks", tag));
		if (this.customBossEvents != null) {
			levelTag.put("CustomBossEvents", this.customBossEvents);
		}

		levelTag.put("ScheduledEvents", this.scheduledEvents.toNbt());
		levelTag.putInt("WanderingTraderSpawnDelay", this.wanderingTraderSpawnDelay);
		levelTag.putInt("WanderingTraderSpawnChance", this.wanderingTraderSpawnChance);
		if (this.wanderingTraderId != null) {
			levelTag.putUuid("WanderingTraderId", this.wanderingTraderId);
		}
	}

	@Override
	public int getSpawnX() {
		return this.spawnX;
	}

	@Override
	public int getSpawnY() {
		return this.spawnY;
	}

	@Override
	public int getSpawnZ() {
		return this.spawnZ;
	}

	@Override
	public float getSpawnAngle() {
		return this.spawnAngle;
	}

	@Override
	public long getTime() {
		return this.time;
	}

	@Override
	public long getTimeOfDay() {
		return this.timeOfDay;
	}

	private void loadPlayerData() {
		if (!this.playerDataLoaded && this.playerData != null) {
			if (this.dataVersion < SharedConstants.getGameVersion().getWorldVersion()) {
				if (this.dataFixer == null) {
					throw (NullPointerException)Util.throwOrPause(new NullPointerException("Fixer Upper not set inside LevelData, and the player tag is not upgraded."));
				}

				this.playerData = NbtHelper.update(this.dataFixer, DataFixTypes.PLAYER, this.playerData, this.dataVersion);
			}

			this.playerDataLoaded = true;
		}
	}

	@Override
	public CompoundTag getPlayerData() {
		this.loadPlayerData();
		return this.playerData;
	}

	@Override
	public void setSpawnX(int spawnX) {
		this.spawnX = spawnX;
	}

	@Override
	public void setSpawnY(int spawnY) {
		this.spawnY = spawnY;
	}

	@Override
	public void setSpawnZ(int spawnZ) {
		this.spawnZ = spawnZ;
	}

	@Override
	public void setSpawnAngle(float angle) {
		this.spawnAngle = angle;
	}

	@Override
	public void setTime(long time) {
		this.time = time;
	}

	@Override
	public void setTimeOfDay(long timeOfDay) {
		this.timeOfDay = timeOfDay;
	}

	@Override
	public void setSpawnPos(BlockPos pos, float angle) {
		this.spawnX = pos.getX();
		this.spawnY = pos.getY();
		this.spawnZ = pos.getZ();
		this.spawnAngle = angle;
	}

	@Override
	public String getLevelName() {
		return this.levelInfo.getLevelName();
	}

	@Override
	public int getVersion() {
		return this.version;
	}

	@Override
	public int getClearWeatherTime() {
		return this.clearWeatherTime;
	}

	@Override
	public void setClearWeatherTime(int clearWeatherTime) {
		this.clearWeatherTime = clearWeatherTime;
	}

	@Override
	public boolean isThundering() {
		return this.thundering;
	}

	@Override
	public void setThundering(boolean thundering) {
		this.thundering = thundering;
	}

	@Override
	public int getThunderTime() {
		return this.thunderTime;
	}

	@Override
	public void setThunderTime(int thunderTime) {
		this.thunderTime = thunderTime;
	}

	@Override
	public boolean isRaining() {
		return this.raining;
	}

	@Override
	public void setRaining(boolean raining) {
		this.raining = raining;
	}

	@Override
	public int getRainTime() {
		return this.rainTime;
	}

	@Override
	public void setRainTime(int rainTime) {
		this.rainTime = rainTime;
	}

	@Override
	public GameMode getGameMode() {
		return this.levelInfo.getGameMode();
	}

	@Override
	public void setGameMode(GameMode gameMode) {
		this.levelInfo = this.levelInfo.withGameMode(gameMode);
	}

	@Override
	public boolean isHardcore() {
		return this.levelInfo.isHardcore();
	}

	@Override
	public boolean areCommandsAllowed() {
		return this.levelInfo.areCommandsAllowed();
	}

	@Override
	public boolean isInitialized() {
		return this.initialized;
	}

	@Override
	public void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}

	@Override
	public GameRules getGameRules() {
		return this.levelInfo.getGameRules();
	}

	@Override
	public WorldBorder.Properties getWorldBorder() {
		return this.worldBorder;
	}

	@Override
	public void setWorldBorder(WorldBorder.Properties properties) {
		this.worldBorder = properties;
	}

	@Override
	public Difficulty getDifficulty() {
		return this.levelInfo.getDifficulty();
	}

	@Override
	public void setDifficulty(Difficulty difficulty) {
		this.levelInfo = this.levelInfo.withDifficulty(difficulty);
	}

	@Override
	public boolean isDifficultyLocked() {
		return this.difficultyLocked;
	}

	@Override
	public void setDifficultyLocked(boolean locked) {
		this.difficultyLocked = locked;
	}

	@Override
	public Timer<MinecraftServer> getScheduledEvents() {
		return this.scheduledEvents;
	}

	@Override
	public void populateCrashReport(CrashReportSection reportSection, HeightLimitView world) {
		ServerWorldProperties.super.populateCrashReport(reportSection, world);
		SaveProperties.super.populateCrashReport(reportSection);
	}

	@Override
	public GeneratorOptions getGeneratorOptions() {
		return this.generatorOptions;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public Lifecycle getLifecycle() {
		return this.lifecycle;
	}

	@Override
	public CompoundTag getDragonFight() {
		return this.dragonFight;
	}

	@Override
	public void setDragonFight(CompoundTag tag) {
		this.dragonFight = tag;
	}

	@Override
	public DataPackSettings getDataPackSettings() {
		return this.levelInfo.getDataPackSettings();
	}

	@Override
	public void updateLevelInfo(DataPackSettings dataPackSettings) {
		this.levelInfo = this.levelInfo.withDataPackSettings(dataPackSettings);
	}

	@Nullable
	@Override
	public CompoundTag getCustomBossEvents() {
		return this.customBossEvents;
	}

	@Override
	public void setCustomBossEvents(@Nullable CompoundTag tag) {
		this.customBossEvents = tag;
	}

	@Override
	public int getWanderingTraderSpawnDelay() {
		return this.wanderingTraderSpawnDelay;
	}

	@Override
	public void setWanderingTraderSpawnDelay(int wanderingTraderSpawnDelay) {
		this.wanderingTraderSpawnDelay = wanderingTraderSpawnDelay;
	}

	@Override
	public int getWanderingTraderSpawnChance() {
		return this.wanderingTraderSpawnChance;
	}

	@Override
	public void setWanderingTraderSpawnChance(int wanderingTraderSpawnChance) {
		this.wanderingTraderSpawnChance = wanderingTraderSpawnChance;
	}

	@Override
	public void setWanderingTraderId(UUID uuid) {
		this.wanderingTraderId = uuid;
	}

	@Override
	public void addServerBrand(String brand, boolean modded) {
		this.serverBrands.add(brand);
		this.modded |= modded;
	}

	@Override
	public boolean isModded() {
		return this.modded;
	}

	@Override
	public Set<String> getServerBrands() {
		return ImmutableSet.copyOf(this.serverBrands);
	}

	@Override
	public ServerWorldProperties getMainWorldProperties() {
		return this;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public LevelInfo getLevelInfo() {
		return this.levelInfo.withCopiedGameRules();
	}
}
