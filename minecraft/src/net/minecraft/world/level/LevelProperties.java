package net.minecraft.world.level;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.class_5315;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.dynamic.DynamicSerializableUuid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.timer.Timer;
import net.minecraft.world.timer.TimerCallbackSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LevelProperties implements ServerWorldProperties, SaveProperties {
	private static final Logger LOGGER = LogManager.getLogger();
	private LevelInfo field_25030;
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
	private final Set<String> disabledDataPacks;
	private final Set<String> enabledDataPacks;
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
		LinkedHashSet<String> enabledDataPacks,
		Set<String> disabledDataPacks,
		Timer<MinecraftServer> scheduledEvents,
		@Nullable CompoundTag customBossEvents,
		CompoundTag compoundTag,
		LevelInfo levelInfo
	) {
		this.dataFixer = dataFixer;
		this.modded = modded;
		this.field_25030 = levelInfo;
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
		this.scheduledEvents = scheduledEvents;
		this.enabledDataPacks = enabledDataPacks;
		this.disabledDataPacks = disabledDataPacks;
		this.customBossEvents = customBossEvents;
		this.field_25031 = compoundTag;
	}

	public LevelProperties(LevelInfo levelInfo) {
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
			Sets.newLinkedHashSet(),
			Sets.<String>newHashSet(),
			new Timer<>(TimerCallbackSerializer.INSTANCE),
			null,
			new CompoundTag(),
			levelInfo.method_28385()
		);
	}

	public static LevelProperties method_29029(
		Dynamic<Tag> dynamic, DataFixer dataFixer, int i, @Nullable CompoundTag compoundTag, LevelInfo levelInfo, class_5315 arg
	) {
		long l = dynamic.get("Time").asLong(0L);
		OptionalDynamic<?> optionalDynamic = dynamic.get("DataPacks");
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
			arg.method_29022(),
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
			(UUID)dynamic.get("WanderingTraderId").read(DynamicSerializableUuid.field_25122).result().map(DynamicSerializableUuid::getUuid).orElse(null),
			(LinkedHashSet<String>)dynamic.get("ServerBrands")
				.asStream()
				.flatMap(dynamicx -> Util.stream(dynamicx.asString().result()))
				.collect(Collectors.toCollection(Sets::newLinkedHashSet)),
			(LinkedHashSet<String>)optionalDynamic.get("Enabled")
				.asStream()
				.flatMap(dynamicx -> Util.stream(dynamicx.asString().result()))
				.collect(Collectors.toCollection(Sets::newLinkedHashSet)),
			(Set<String>)optionalDynamic.get("Disabled").asStream().flatMap(dynamicx -> Util.stream(dynamicx.asString().result())).collect(Collectors.toSet()),
			new Timer<>(TimerCallbackSerializer.INSTANCE, dynamic.get("ScheduledEvents").asStream()),
			(CompoundTag)dynamic.get("CustomBossEvents").orElseEmptyMap().getValue(),
			compoundTag2,
			levelInfo
		);
	}

	@Override
	public CompoundTag cloneWorldTag(@Nullable CompoundTag tag) {
		this.loadPlayerData();
		if (tag == null) {
			tag = this.playerData;
		}

		CompoundTag compoundTag = new CompoundTag();
		this.updateProperties(compoundTag, tag);
		return compoundTag;
	}

	private void updateProperties(CompoundTag levelTag, CompoundTag playerTag) {
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
		GeneratorOptions.CODEC
			.encodeStart(NbtOps.INSTANCE, this.field_25030.getGeneratorOptions())
			.resultOrPartial(Util.method_29188("WorldGenSettings: ", LOGGER::error))
			.ifPresent(tag -> levelTag.put("WorldGenSettings", tag));
		levelTag.putInt("GameType", this.field_25030.getGameMode().getId());
		levelTag.putInt("SpawnX", this.spawnX);
		levelTag.putInt("SpawnY", this.spawnY);
		levelTag.putInt("SpawnZ", this.spawnZ);
		levelTag.putLong("Time", this.time);
		levelTag.putLong("DayTime", this.timeOfDay);
		levelTag.putLong("LastPlayed", Util.getEpochTimeMs());
		levelTag.putString("LevelName", this.field_25030.getLevelName());
		levelTag.putInt("version", 19133);
		levelTag.putInt("clearWeatherTime", this.clearWeatherTime);
		levelTag.putInt("rainTime", this.rainTime);
		levelTag.putBoolean("raining", this.raining);
		levelTag.putInt("thunderTime", this.thunderTime);
		levelTag.putBoolean("thundering", this.thundering);
		levelTag.putBoolean("hardcore", this.field_25030.hasStructures());
		levelTag.putBoolean("allowCommands", this.field_25030.isHardcore());
		levelTag.putBoolean("initialized", this.initialized);
		this.worldBorder.toTag(levelTag);
		levelTag.putByte("Difficulty", (byte)this.field_25030.getDifficulty().getId());
		levelTag.putBoolean("DifficultyLocked", this.difficultyLocked);
		levelTag.put("GameRules", this.field_25030.getGameRules().toNbt());
		levelTag.put("DragonFight", this.field_25031);
		if (playerTag != null) {
			levelTag.put("Player", playerTag);
		}

		CompoundTag compoundTag2 = new CompoundTag();
		ListTag listTag2 = new ListTag();

		for (String string : this.enabledDataPacks) {
			listTag2.add(StringTag.of(string));
		}

		compoundTag2.put("Enabled", listTag2);
		ListTag listTag3 = new ListTag();

		for (String string2 : this.disabledDataPacks) {
			listTag3.add(StringTag.of(string2));
		}

		compoundTag2.put("Disabled", listTag3);
		levelTag.put("DataPacks", compoundTag2);
		if (this.customBossEvents != null) {
			levelTag.put("CustomBossEvents", this.customBossEvents);
		}

		levelTag.put("ScheduledEvents", this.scheduledEvents.toTag());
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
	public void method_29034(long l) {
		this.time = l;
	}

	@Override
	public void method_29035(long l) {
		this.timeOfDay = l;
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
	public void populateCrashReport(CrashReportSection crashReportSection) {
		ServerWorldProperties.super.populateCrashReport(crashReportSection);
		SaveProperties.super.populateCrashReport(crashReportSection);
	}

	@Override
	public GeneratorOptions method_28057() {
		return this.field_25030.getGeneratorOptions();
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
	public Set<String> getDisabledDataPacks() {
		return this.disabledDataPacks;
	}

	@Override
	public Set<String> getEnabledDataPacks() {
		return this.enabledDataPacks;
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
