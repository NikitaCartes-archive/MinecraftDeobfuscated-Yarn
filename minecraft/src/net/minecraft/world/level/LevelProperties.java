package net.minecraft.world.level;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.dynamic.DynamicSerializableUuid;
import net.minecraft.util.dynamic.RegistryReadingOps;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryTracker;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
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
	private LevelInfo field_25030;
	private final GeneratorOptions field_25425;
	private final Lifecycle field_25426;
	private int spawnX;
	private int spawnY;
	private int spawnZ;
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
	private CompoundTag field_25031;
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
		LinkedHashSet<String> serverBrands,
		Timer<MinecraftServer> timer,
		@Nullable CompoundTag compoundTag,
		CompoundTag compoundTag2,
		LevelInfo levelInfo,
		GeneratorOptions generatorOptions,
		Lifecycle lifecycle
	) {
		this.dataFixer = dataFixer;
		this.modded = modded;
		this.spawnX = spawnX;
		this.spawnY = spawnY;
		this.spawnZ = spawnZ;
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
		this.scheduledEvents = timer;
		this.customBossEvents = compoundTag;
		this.field_25031 = compoundTag2;
		this.field_25030 = levelInfo;
		this.field_25425 = generatorOptions;
		this.field_25426 = lifecycle;
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
			Sets.newLinkedHashSet(),
			new Timer<>(TimerCallbackSerializer.INSTANCE),
			null,
			new CompoundTag(),
			levelInfo.method_28385(),
			generatorOptions,
			lifecycle
		);
	}

	public static LevelProperties method_29029(
		Dynamic<Tag> dynamic,
		DataFixer dataFixer,
		int i,
		@Nullable CompoundTag compoundTag,
		LevelInfo levelInfo,
		SaveVersionInfo saveVersionInfo,
		GeneratorOptions generatorOptions,
		Lifecycle lifecycle
	) {
		long l = dynamic.get("Time").asLong(0L);
		CompoundTag compoundTag2 = (CompoundTag)dynamic.get("DragonFight")
			.result()
			.map(Dynamic::getValue)
			.orElseGet(() -> dynamic.get("DimensionData").get("1").get("DragonFight").orElseEmptyMap().getValue());
		return new LevelProperties(
			dataFixer,
			i,
			compoundTag,
			dynamic.get("WasModded").asBoolean(false),
			dynamic.get("SpawnX").asInt(0),
			dynamic.get("SpawnY").asInt(0),
			dynamic.get("SpawnZ").asInt(0),
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
			(UUID)dynamic.get("WanderingTraderId").read(DynamicSerializableUuid.field_25122).result().orElse(null),
			(LinkedHashSet<String>)dynamic.get("ServerBrands")
				.asStream()
				.flatMap(dynamicx -> Util.stream(dynamicx.asString().result()))
				.collect(Collectors.toCollection(Sets::newLinkedHashSet)),
			new Timer<>(TimerCallbackSerializer.INSTANCE, dynamic.get("ScheduledEvents").asStream()),
			(CompoundTag)dynamic.get("CustomBossEvents").orElseEmptyMap().getValue(),
			compoundTag2,
			levelInfo,
			generatorOptions,
			lifecycle
		);
	}

	@Override
	public CompoundTag cloneWorldTag(RegistryTracker registryTracker, @Nullable CompoundTag compoundTag) {
		this.loadPlayerData();
		if (compoundTag == null) {
			compoundTag = this.playerData;
		}

		CompoundTag compoundTag2 = new CompoundTag();
		this.updateProperties(registryTracker, compoundTag2, compoundTag);
		return compoundTag2;
	}

	private void updateProperties(RegistryTracker registryTracker, CompoundTag compoundTag, @Nullable CompoundTag compoundTag2) {
		ListTag listTag = new ListTag();
		this.serverBrands.stream().map(StringTag::of).forEach(listTag::add);
		compoundTag.put("ServerBrands", listTag);
		compoundTag.putBoolean("WasModded", this.modded);
		CompoundTag compoundTag3 = new CompoundTag();
		compoundTag3.putString("Name", SharedConstants.getGameVersion().getName());
		compoundTag3.putInt("Id", SharedConstants.getGameVersion().getWorldVersion());
		compoundTag3.putBoolean("Snapshot", !SharedConstants.getGameVersion().isStable());
		compoundTag.put("Version", compoundTag3);
		compoundTag.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
		RegistryReadingOps<Tag> registryReadingOps = RegistryReadingOps.of(NbtOps.INSTANCE, registryTracker);
		GeneratorOptions.CODEC
			.encodeStart(registryReadingOps, this.field_25425)
			.resultOrPartial(Util.method_29188("WorldGenSettings: ", LOGGER::error))
			.ifPresent(tag -> compoundTag.put("WorldGenSettings", tag));
		compoundTag.putInt("GameType", this.field_25030.getGameMode().getId());
		compoundTag.putInt("SpawnX", this.spawnX);
		compoundTag.putInt("SpawnY", this.spawnY);
		compoundTag.putInt("SpawnZ", this.spawnZ);
		compoundTag.putLong("Time", this.time);
		compoundTag.putLong("DayTime", this.timeOfDay);
		compoundTag.putLong("LastPlayed", Util.getEpochTimeMs());
		compoundTag.putString("LevelName", this.field_25030.getLevelName());
		compoundTag.putInt("version", 19133);
		compoundTag.putInt("clearWeatherTime", this.clearWeatherTime);
		compoundTag.putInt("rainTime", this.rainTime);
		compoundTag.putBoolean("raining", this.raining);
		compoundTag.putInt("thunderTime", this.thunderTime);
		compoundTag.putBoolean("thundering", this.thundering);
		compoundTag.putBoolean("hardcore", this.field_25030.hasStructures());
		compoundTag.putBoolean("allowCommands", this.field_25030.isHardcore());
		compoundTag.putBoolean("initialized", this.initialized);
		this.worldBorder.toTag(compoundTag);
		compoundTag.putByte("Difficulty", (byte)this.field_25030.getDifficulty().getId());
		compoundTag.putBoolean("DifficultyLocked", this.difficultyLocked);
		compoundTag.put("GameRules", this.field_25030.getGameRules().toNbt());
		compoundTag.put("DragonFight", this.field_25031);
		if (compoundTag2 != null) {
			compoundTag.put("Player", compoundTag2);
		}

		DataPackSettings.CODEC.encodeStart(NbtOps.INSTANCE, this.field_25030.method_29558()).result().ifPresent(tag -> compoundTag.put("DataPacks", tag));
		if (this.customBossEvents != null) {
			compoundTag.put("CustomBossEvents", this.customBossEvents);
		}

		compoundTag.put("ScheduledEvents", this.scheduledEvents.toTag());
		compoundTag.putInt("WanderingTraderSpawnDelay", this.wanderingTraderSpawnDelay);
		compoundTag.putInt("WanderingTraderSpawnChance", this.wanderingTraderSpawnChance);
		if (this.wanderingTraderId != null) {
			compoundTag.putUuid("WanderingTraderId", this.wanderingTraderId);
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
	public void setTime(long time) {
		this.time = time;
	}

	@Override
	public void setTimeOfDay(long time) {
		this.timeOfDay = time;
	}

	@Override
	public void setSpawnPos(BlockPos pos) {
		this.spawnX = pos.getX();
		this.spawnY = pos.getY();
		this.spawnZ = pos.getZ();
	}

	@Override
	public String getLevelName() {
		return this.field_25030.getLevelName();
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
		return this.field_25030.getGameMode();
	}

	@Override
	public void setGameMode(GameMode gameMode) {
		this.field_25030 = this.field_25030.method_28382(gameMode);
	}

	@Override
	public boolean isHardcore() {
		return this.field_25030.hasStructures();
	}

	@Override
	public boolean areCommandsAllowed() {
		return this.field_25030.isHardcore();
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
		return this.field_25030.getGameRules();
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
		return this.field_25030.getDifficulty();
	}

	@Override
	public void setDifficulty(Difficulty difficulty) {
		this.field_25030 = this.field_25030.method_28381(difficulty);
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
	public void populateCrashReport(CrashReportSection reportSection) {
		ServerWorldProperties.super.populateCrashReport(reportSection);
		SaveProperties.super.populateCrashReport(reportSection);
	}

	@Override
	public GeneratorOptions getGeneratorOptions() {
		return this.field_25425;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public Lifecycle method_29588() {
		return this.field_25426;
	}

	@Override
	public CompoundTag method_29036() {
		return this.field_25031;
	}

	@Override
	public void method_29037(CompoundTag compoundTag) {
		this.field_25031 = compoundTag;
	}

	@Override
	public DataPackSettings method_29589() {
		return this.field_25030.method_29558();
	}

	@Override
	public void method_29590(DataPackSettings dataPackSettings) {
		this.field_25030 = this.field_25030.method_29557(dataPackSettings);
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
		return this.field_25030.method_28385();
	}
}
