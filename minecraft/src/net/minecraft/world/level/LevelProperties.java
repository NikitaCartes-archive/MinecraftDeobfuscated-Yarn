package net.minecraft.world.level;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.resource.DataConfiguration;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import net.minecraft.util.Uuids;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
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
import org.slf4j.Logger;

public class LevelProperties implements ServerWorldProperties, SaveProperties {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final String LEVEL_NAME_KEY = "LevelName";
	protected static final String PLAYER_KEY = "Player";
	protected static final String WORLD_GEN_SETTINGS_KEY = "WorldGenSettings";
	private LevelInfo levelInfo;
	private final GeneratorOptions generatorOptions;
	private final LevelProperties.SpecialProperty specialProperty;
	private final Lifecycle lifecycle;
	private BlockPos spawnPos;
	private float spawnAngle;
	private long time;
	private long timeOfDay;
	@Nullable
	private final NbtCompound playerData;
	private final int version;
	private int clearWeatherTime;
	private boolean raining;
	private int rainTime;
	private boolean thundering;
	private int thunderTime;
	private boolean initialized;
	private boolean difficultyLocked;
	private WorldBorder.Properties worldBorder;
	private EnderDragonFight.Data dragonFight;
	@Nullable
	private NbtCompound customBossEvents;
	private int wanderingTraderSpawnDelay;
	private int wanderingTraderSpawnChance;
	@Nullable
	private UUID wanderingTraderId;
	private final Set<String> serverBrands;
	private boolean modded;
	private final Set<String> removedFeatures;
	private final Timer<MinecraftServer> scheduledEvents;

	private LevelProperties(
		@Nullable NbtCompound playerData,
		boolean modded,
		BlockPos spawnPos,
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
		Set<String> removedFeatures,
		Timer<MinecraftServer> scheduledEvents,
		@Nullable NbtCompound customBossEvents,
		EnderDragonFight.Data dragonFight,
		LevelInfo levelInfo,
		GeneratorOptions generatorOptions,
		LevelProperties.SpecialProperty specialProperty,
		Lifecycle lifecycle
	) {
		this.modded = modded;
		this.spawnPos = spawnPos;
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
		this.removedFeatures = removedFeatures;
		this.playerData = playerData;
		this.scheduledEvents = scheduledEvents;
		this.customBossEvents = customBossEvents;
		this.dragonFight = dragonFight;
		this.levelInfo = levelInfo;
		this.generatorOptions = generatorOptions;
		this.specialProperty = specialProperty;
		this.lifecycle = lifecycle;
	}

	public LevelProperties(LevelInfo levelInfo, GeneratorOptions generatorOptions, LevelProperties.SpecialProperty specialProperty, Lifecycle lifecycle) {
		this(
			null,
			false,
			BlockPos.ORIGIN,
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
			new HashSet(),
			new Timer<>(TimerCallbackSerializer.INSTANCE),
			null,
			EnderDragonFight.Data.DEFAULT,
			levelInfo.withCopiedGameRules(),
			generatorOptions,
			specialProperty,
			lifecycle
		);
	}

	public static <T> LevelProperties readProperties(
		Dynamic<T> dynamic, LevelInfo info, LevelProperties.SpecialProperty specialProperty, GeneratorOptions generatorOptions, Lifecycle lifecycle
	) {
		long l = dynamic.get("Time").asLong(0L);
		return new LevelProperties(
			(NbtCompound)NbtCompound.CODEC.parse(dynamic.get("Player").orElseEmptyMap()).result().orElse(null),
			dynamic.get("WasModded").asBoolean(false),
			new BlockPos(dynamic.get("SpawnX").asInt(0), dynamic.get("SpawnY").asInt(0), dynamic.get("SpawnZ").asInt(0)),
			dynamic.get("SpawnAngle").asFloat(0.0F),
			l,
			dynamic.get("DayTime").asLong(l),
			SaveVersionInfo.fromDynamic(dynamic).getLevelFormatVersion(),
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
			(UUID)dynamic.get("WanderingTraderId").read(Uuids.INT_STREAM_CODEC).result().orElse(null),
			(Set<String>)dynamic.get("ServerBrands")
				.asStream()
				.flatMap(dynamicx -> dynamicx.asString().result().stream())
				.collect(Collectors.toCollection(Sets::newLinkedHashSet)),
			(Set<String>)dynamic.get("removed_features").asStream().flatMap(dynamicx -> dynamicx.asString().result().stream()).collect(Collectors.toSet()),
			new Timer<>(TimerCallbackSerializer.INSTANCE, dynamic.get("ScheduledEvents").asStream()),
			(NbtCompound)dynamic.get("CustomBossEvents").orElseEmptyMap().getValue(),
			(EnderDragonFight.Data)dynamic.get("DragonFight").read(EnderDragonFight.Data.CODEC).resultOrPartial(LOGGER::error).orElse(EnderDragonFight.Data.DEFAULT),
			info,
			generatorOptions,
			specialProperty,
			lifecycle
		);
	}

	@Override
	public NbtCompound cloneWorldNbt(DynamicRegistryManager registryManager, @Nullable NbtCompound playerNbt) {
		if (playerNbt == null) {
			playerNbt = this.playerData;
		}

		NbtCompound nbtCompound = new NbtCompound();
		this.updateProperties(registryManager, nbtCompound, playerNbt);
		return nbtCompound;
	}

	private void updateProperties(DynamicRegistryManager registryManager, NbtCompound levelNbt, @Nullable NbtCompound playerNbt) {
		levelNbt.put("ServerBrands", createStringList(this.serverBrands));
		levelNbt.putBoolean("WasModded", this.modded);
		if (!this.removedFeatures.isEmpty()) {
			levelNbt.put("removed_features", createStringList(this.removedFeatures));
		}

		NbtCompound nbtCompound = new NbtCompound();
		nbtCompound.putString("Name", SharedConstants.getGameVersion().getName());
		nbtCompound.putInt("Id", SharedConstants.getGameVersion().getSaveVersion().getId());
		nbtCompound.putBoolean("Snapshot", !SharedConstants.getGameVersion().isStable());
		nbtCompound.putString("Series", SharedConstants.getGameVersion().getSaveVersion().getSeries());
		levelNbt.put("Version", nbtCompound);
		NbtHelper.putDataVersion(levelNbt);
		DynamicOps<NbtElement> dynamicOps = registryManager.getOps(NbtOps.INSTANCE);
		WorldGenSettings.encode(dynamicOps, this.generatorOptions, registryManager)
			.resultOrPartial(Util.addPrefix("WorldGenSettings: ", LOGGER::error))
			.ifPresent(nbtElement -> levelNbt.put("WorldGenSettings", nbtElement));
		levelNbt.putInt("GameType", this.levelInfo.getGameMode().getId());
		levelNbt.putInt("SpawnX", this.spawnPos.getX());
		levelNbt.putInt("SpawnY", this.spawnPos.getY());
		levelNbt.putInt("SpawnZ", this.spawnPos.getZ());
		levelNbt.putFloat("SpawnAngle", this.spawnAngle);
		levelNbt.putLong("Time", this.time);
		levelNbt.putLong("DayTime", this.timeOfDay);
		levelNbt.putLong("LastPlayed", Util.getEpochTimeMs());
		levelNbt.putString("LevelName", this.levelInfo.getLevelName());
		levelNbt.putInt("version", 19133);
		levelNbt.putInt("clearWeatherTime", this.clearWeatherTime);
		levelNbt.putInt("rainTime", this.rainTime);
		levelNbt.putBoolean("raining", this.raining);
		levelNbt.putInt("thunderTime", this.thunderTime);
		levelNbt.putBoolean("thundering", this.thundering);
		levelNbt.putBoolean("hardcore", this.levelInfo.isHardcore());
		levelNbt.putBoolean("allowCommands", this.levelInfo.areCommandsAllowed());
		levelNbt.putBoolean("initialized", this.initialized);
		this.worldBorder.writeNbt(levelNbt);
		levelNbt.putByte("Difficulty", (byte)this.levelInfo.getDifficulty().getId());
		levelNbt.putBoolean("DifficultyLocked", this.difficultyLocked);
		levelNbt.put("GameRules", this.levelInfo.getGameRules().toNbt());
		levelNbt.put("DragonFight", Util.getResult(EnderDragonFight.Data.CODEC.encodeStart(NbtOps.INSTANCE, this.dragonFight), IllegalStateException::new));
		if (playerNbt != null) {
			levelNbt.put("Player", playerNbt);
		}

		DataResult<NbtElement> dataResult = DataConfiguration.CODEC.encodeStart(NbtOps.INSTANCE, this.levelInfo.getDataConfiguration());
		dataResult.get()
			.ifLeft(dataConfiguration -> levelNbt.copyFrom((NbtCompound)dataConfiguration))
			.ifRight(result -> LOGGER.warn("Failed to encode configuration {}", result.message()));
		if (this.customBossEvents != null) {
			levelNbt.put("CustomBossEvents", this.customBossEvents);
		}

		levelNbt.put("ScheduledEvents", this.scheduledEvents.toNbt());
		levelNbt.putInt("WanderingTraderSpawnDelay", this.wanderingTraderSpawnDelay);
		levelNbt.putInt("WanderingTraderSpawnChance", this.wanderingTraderSpawnChance);
		if (this.wanderingTraderId != null) {
			levelNbt.putUuid("WanderingTraderId", this.wanderingTraderId);
		}
	}

	private static NbtList createStringList(Set<String> strings) {
		NbtList nbtList = new NbtList();
		strings.stream().map(NbtString::of).forEach(nbtList::add);
		return nbtList;
	}

	@Override
	public BlockPos getSpawnPos() {
		return this.spawnPos;
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

	@Nullable
	@Override
	public NbtCompound getPlayerData() {
		return this.playerData;
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
		this.spawnPos = pos.toImmutable();
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
	public void setWorldBorder(WorldBorder.Properties worldBorder) {
		this.worldBorder = worldBorder;
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
	public void setDifficultyLocked(boolean difficultyLocked) {
		this.difficultyLocked = difficultyLocked;
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

	@Override
	public boolean isFlatWorld() {
		return this.specialProperty == LevelProperties.SpecialProperty.FLAT;
	}

	@Override
	public boolean isDebugWorld() {
		return this.specialProperty == LevelProperties.SpecialProperty.DEBUG;
	}

	@Override
	public Lifecycle getLifecycle() {
		return this.lifecycle;
	}

	@Override
	public EnderDragonFight.Data getDragonFight() {
		return this.dragonFight;
	}

	@Override
	public void setDragonFight(EnderDragonFight.Data dragonFight) {
		this.dragonFight = dragonFight;
	}

	@Override
	public DataConfiguration getDataConfiguration() {
		return this.levelInfo.getDataConfiguration();
	}

	@Override
	public void updateLevelInfo(DataConfiguration dataConfiguration) {
		this.levelInfo = this.levelInfo.withDataConfiguration(dataConfiguration);
	}

	@Nullable
	@Override
	public NbtCompound getCustomBossEvents() {
		return this.customBossEvents;
	}

	@Override
	public void setCustomBossEvents(@Nullable NbtCompound customBossEvents) {
		this.customBossEvents = customBossEvents;
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

	@Nullable
	@Override
	public UUID getWanderingTraderId() {
		return this.wanderingTraderId;
	}

	@Override
	public void setWanderingTraderId(UUID wanderingTraderId) {
		this.wanderingTraderId = wanderingTraderId;
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
	public Set<String> getRemovedFeatures() {
		return Set.copyOf(this.removedFeatures);
	}

	@Override
	public ServerWorldProperties getMainWorldProperties() {
		return this;
	}

	@Override
	public LevelInfo getLevelInfo() {
		return this.levelInfo.withCopiedGameRules();
	}

	@Deprecated
	public static enum SpecialProperty {
		NONE,
		FLAT,
		DEBUG;
	}
}
