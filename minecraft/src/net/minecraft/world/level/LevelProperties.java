package net.minecraft.world.level;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.Dynamic;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.class_5219;
import net.minecraft.class_5268;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.timer.Timer;
import net.minecraft.world.timer.TimerCallbackSerializer;

public class LevelProperties implements class_5268, class_5219 {
	private final String versionName;
	private final int versionId;
	private final boolean versionSnapshot;
	private final long randomSeed;
	private final LevelGeneratorOptions generatorOptions;
	@Nullable
	private String legacyCustomOptions;
	private int spawnX;
	private int spawnY;
	private int spawnZ;
	private long time;
	private long timeOfDay;
	private long lastPlayed;
	private long sizeOnDisk;
	@Nullable
	private final DataFixer dataFixer;
	private final int dataVersion;
	private boolean playerDataLoaded;
	private CompoundTag playerData;
	private final String levelName;
	private final int version;
	private int clearWeatherTime;
	private boolean raining;
	private int rainTime;
	private boolean thundering;
	private int thunderTime;
	private GameMode gameMode;
	private final boolean structures;
	private final boolean hardcore;
	private final boolean commandsAllowed;
	private final boolean field_24192;
	private boolean initialized;
	private Difficulty difficulty = Difficulty.NORMAL;
	private boolean difficultyLocked;
	private WorldBorder.class_5200 field_24193 = WorldBorder.field_24122;
	private final Set<String> disabledDataPacks = Sets.<String>newHashSet();
	private final Set<String> enabledDataPacks = Sets.<String>newLinkedHashSet();
	private final Map<DimensionType, CompoundTag> worldData = Maps.<DimensionType, CompoundTag>newIdentityHashMap();
	@Nullable
	private CompoundTag customBossEvents;
	private int wanderingTraderSpawnDelay;
	private int wanderingTraderSpawnChance;
	private UUID wanderingTraderId;
	private Set<String> serverBrands = Sets.<String>newLinkedHashSet();
	private boolean modded;
	private final GameRules gameRules = new GameRules();
	private final Timer<MinecraftServer> scheduledEvents = new Timer<>(TimerCallbackSerializer.INSTANCE);

	public LevelProperties(CompoundTag tag, DataFixer dataFixer, int dataVersion, @Nullable CompoundTag playerData) {
		this.dataFixer = dataFixer;
		ListTag listTag = tag.getList("ServerBrands", 8);

		for (int i = 0; i < listTag.size(); i++) {
			this.serverBrands.add(listTag.getString(i));
		}

		this.modded = tag.getBoolean("WasModded");
		if (tag.contains("Version", 10)) {
			CompoundTag compoundTag = tag.getCompound("Version");
			this.versionName = compoundTag.getString("Name");
			this.versionId = compoundTag.getInt("Id");
			this.versionSnapshot = compoundTag.getBoolean("Snapshot");
		} else {
			this.versionName = SharedConstants.getGameVersion().getName();
			this.versionId = SharedConstants.getGameVersion().getWorldVersion();
			this.versionSnapshot = !SharedConstants.getGameVersion().isStable();
		}

		this.randomSeed = tag.getLong("RandomSeed");
		if (tag.contains("generatorName", 8)) {
			String string = tag.getString("generatorName");
			LevelGeneratorType levelGeneratorType = LevelGeneratorType.getTypeFromName(string);
			if (levelGeneratorType == null) {
				levelGeneratorType = LevelGeneratorType.DEFAULT;
			} else if (levelGeneratorType == LevelGeneratorType.CUSTOMIZED) {
				this.legacyCustomOptions = tag.getString("generatorOptions");
			} else if (levelGeneratorType.isVersioned()) {
				int j = 0;
				if (tag.contains("generatorVersion", 99)) {
					j = tag.getInt("generatorVersion");
				}

				levelGeneratorType = levelGeneratorType.getTypeForVersion(j);
			}

			CompoundTag compoundTag2 = tag.getCompound("generatorOptions");
			Dynamic<?> dynamic = new Dynamic<>(NbtOps.INSTANCE, compoundTag2);
			Dynamic<?> dynamic2 = updateGeneratorOptionsData(levelGeneratorType, dynamic, dataVersion, dataFixer);
			this.generatorOptions = levelGeneratorType.loadOptions(dynamic2);
		} else {
			this.generatorOptions = LevelGeneratorType.DEFAULT.getDefaultOptions();
		}

		this.gameMode = GameMode.byId(tag.getInt("GameType"));
		if (tag.contains("legacy_custom_options", 8)) {
			this.legacyCustomOptions = tag.getString("legacy_custom_options");
		}

		if (tag.contains("MapFeatures", 99)) {
			this.structures = tag.getBoolean("MapFeatures");
		} else {
			this.structures = true;
		}

		this.spawnX = tag.getInt("SpawnX");
		this.spawnY = tag.getInt("SpawnY");
		this.spawnZ = tag.getInt("SpawnZ");
		this.time = tag.getLong("Time");
		if (tag.contains("DayTime", 99)) {
			this.timeOfDay = tag.getLong("DayTime");
		} else {
			this.timeOfDay = this.time;
		}

		this.lastPlayed = tag.getLong("LastPlayed");
		this.sizeOnDisk = tag.getLong("SizeOnDisk");
		this.levelName = tag.getString("LevelName");
		this.version = tag.getInt("version");
		this.clearWeatherTime = tag.getInt("clearWeatherTime");
		this.rainTime = tag.getInt("rainTime");
		this.raining = tag.getBoolean("raining");
		this.thunderTime = tag.getInt("thunderTime");
		this.thundering = tag.getBoolean("thundering");
		this.hardcore = tag.getBoolean("hardcore");
		if (tag.contains("initialized", 99)) {
			this.initialized = tag.getBoolean("initialized");
		} else {
			this.initialized = true;
		}

		if (tag.contains("allowCommands", 99)) {
			this.commandsAllowed = tag.getBoolean("allowCommands");
		} else {
			this.commandsAllowed = this.gameMode == GameMode.CREATIVE;
		}

		this.field_24192 = tag.getBoolean("BonusChest");
		this.dataVersion = dataVersion;
		if (playerData != null) {
			this.playerData = playerData;
		}

		if (tag.contains("GameRules", 10)) {
			this.gameRules.load(tag.getCompound("GameRules"));
		}

		if (tag.contains("Difficulty", 99)) {
			this.difficulty = Difficulty.byOrdinal(tag.getByte("Difficulty"));
		}

		if (tag.contains("DifficultyLocked", 1)) {
			this.difficultyLocked = tag.getBoolean("DifficultyLocked");
		}

		this.field_24193 = WorldBorder.class_5200.method_27358(tag, WorldBorder.field_24122);
		if (tag.contains("DimensionData", 10)) {
			CompoundTag compoundTag = tag.getCompound("DimensionData");

			for (String string2 : compoundTag.getKeys()) {
				this.worldData.put(DimensionType.byRawId(Integer.parseInt(string2)), compoundTag.getCompound(string2));
			}
		}

		if (tag.contains("DataPacks", 10)) {
			CompoundTag compoundTag = tag.getCompound("DataPacks");
			ListTag listTag2 = compoundTag.getList("Disabled", 8);

			for (int j = 0; j < listTag2.size(); j++) {
				this.disabledDataPacks.add(listTag2.getString(j));
			}

			ListTag listTag3 = compoundTag.getList("Enabled", 8);

			for (int k = 0; k < listTag3.size(); k++) {
				this.enabledDataPacks.add(listTag3.getString(k));
			}
		}

		if (tag.contains("CustomBossEvents", 10)) {
			this.customBossEvents = tag.getCompound("CustomBossEvents");
		}

		if (tag.contains("ScheduledEvents", 9)) {
			this.scheduledEvents.fromTag(tag.getList("ScheduledEvents", 10));
		}

		if (tag.contains("WanderingTraderSpawnDelay", 99)) {
			this.wanderingTraderSpawnDelay = tag.getInt("WanderingTraderSpawnDelay");
		}

		if (tag.contains("WanderingTraderSpawnChance", 99)) {
			this.wanderingTraderSpawnChance = tag.getInt("WanderingTraderSpawnChance");
		}

		if (tag.containsUuidNew("WanderingTraderId")) {
			this.wanderingTraderId = tag.getUuidNew("WanderingTraderId");
		}
	}

	private static <T> Dynamic<T> updateGeneratorOptionsData(LevelGeneratorType type, Dynamic<T> dynamic, int dataVersion, DataFixer dataFixer) {
		int i = Math.max(dataVersion, 2501);
		Dynamic<T> dynamic2 = dynamic.merge(dynamic.createString("levelType"), dynamic.createString(type.getStoredName()));
		return dataFixer.update(TypeReferences.CHUNK_GENERATOR_SETTINGS, dynamic2, i, SharedConstants.getGameVersion().getWorldVersion()).remove("levelType");
	}

	public LevelProperties(LevelInfo levelInfo) {
		this.dataFixer = null;
		this.dataVersion = SharedConstants.getGameVersion().getWorldVersion();
		this.randomSeed = levelInfo.getSeed();
		this.gameMode = levelInfo.getGameMode();
		this.difficulty = levelInfo.method_27340();
		this.structures = levelInfo.hasStructures();
		this.hardcore = levelInfo.isHardcore();
		this.generatorOptions = levelInfo.getGeneratorOptions();
		this.commandsAllowed = levelInfo.allowCommands();
		this.field_24192 = levelInfo.hasBonusChest();
		this.levelName = levelInfo.method_27339();
		this.version = 19133;
		this.initialized = false;
		this.versionName = SharedConstants.getGameVersion().getName();
		this.versionId = SharedConstants.getGameVersion().getWorldVersion();
		this.versionSnapshot = !SharedConstants.getGameVersion().isStable();
		this.gameRules.setAllValues(levelInfo.method_27341(), null);
	}

	@Override
	public CompoundTag cloneWorldTag(@Nullable CompoundTag compoundTag) {
		this.loadPlayerData();
		if (compoundTag == null) {
			compoundTag = this.playerData;
		}

		CompoundTag compoundTag2 = new CompoundTag();
		this.updateProperties(compoundTag2, compoundTag);
		return compoundTag2;
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
		levelTag.putLong("RandomSeed", this.randomSeed);
		levelTag.putString("generatorName", this.generatorOptions.getType().getStoredName());
		levelTag.putInt("generatorVersion", this.generatorOptions.getType().getVersion());
		CompoundTag compoundTag2 = (CompoundTag)this.generatorOptions.getDynamic().convert(NbtOps.INSTANCE).getValue();
		if (!compoundTag2.isEmpty()) {
			levelTag.put("generatorOptions", compoundTag2);
		}

		if (this.legacyCustomOptions != null) {
			levelTag.putString("legacy_custom_options", this.legacyCustomOptions);
		}

		levelTag.putInt("GameType", this.gameMode.getId());
		levelTag.putBoolean("MapFeatures", this.structures);
		levelTag.putInt("SpawnX", this.spawnX);
		levelTag.putInt("SpawnY", this.spawnY);
		levelTag.putInt("SpawnZ", this.spawnZ);
		levelTag.putLong("Time", this.time);
		levelTag.putLong("DayTime", this.timeOfDay);
		levelTag.putLong("SizeOnDisk", this.sizeOnDisk);
		levelTag.putLong("LastPlayed", Util.getEpochTimeMs());
		levelTag.putString("LevelName", this.levelName);
		levelTag.putInt("version", 19133);
		levelTag.putInt("clearWeatherTime", this.clearWeatherTime);
		levelTag.putInt("rainTime", this.rainTime);
		levelTag.putBoolean("raining", this.raining);
		levelTag.putInt("thunderTime", this.thunderTime);
		levelTag.putBoolean("thundering", this.thundering);
		levelTag.putBoolean("hardcore", this.hardcore);
		levelTag.putBoolean("allowCommands", this.commandsAllowed);
		levelTag.putBoolean("BonusChest", this.field_24192);
		levelTag.putBoolean("initialized", this.initialized);
		this.field_24193.method_27357(levelTag);
		levelTag.putByte("Difficulty", (byte)this.difficulty.getId());
		levelTag.putBoolean("DifficultyLocked", this.difficultyLocked);
		levelTag.put("GameRules", this.gameRules.toNbt());
		CompoundTag compoundTag3 = new CompoundTag();

		for (Entry<DimensionType, CompoundTag> entry : this.worldData.entrySet()) {
			compoundTag3.put(String.valueOf(((DimensionType)entry.getKey()).getRawId()), (Tag)entry.getValue());
		}

		levelTag.put("DimensionData", compoundTag3);
		if (playerTag != null) {
			levelTag.put("Player", playerTag);
		}

		CompoundTag compoundTag4 = new CompoundTag();
		ListTag listTag2 = new ListTag();

		for (String string : this.enabledDataPacks) {
			listTag2.add(StringTag.of(string));
		}

		compoundTag4.put("Enabled", listTag2);
		ListTag listTag3 = new ListTag();

		for (String string2 : this.disabledDataPacks) {
			listTag3.add(StringTag.of(string2));
		}

		compoundTag4.put("Disabled", listTag3);
		levelTag.put("DataPacks", compoundTag4);
		if (this.customBossEvents != null) {
			levelTag.put("CustomBossEvents", this.customBossEvents);
		}

		levelTag.put("ScheduledEvents", this.scheduledEvents.toTag());
		levelTag.putInt("WanderingTraderSpawnDelay", this.wanderingTraderSpawnDelay);
		levelTag.putInt("WanderingTraderSpawnChance", this.wanderingTraderSpawnChance);
		if (this.wanderingTraderId != null) {
			levelTag.putUuidNew("WanderingTraderId", this.wanderingTraderId);
		}
	}

	@Override
	public long getSeed() {
		return this.randomSeed;
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
	public void method_27416(int i) {
		this.spawnX = i;
	}

	@Override
	public void method_27417(int i) {
		this.spawnY = i;
	}

	@Override
	public void method_27419(int i) {
		this.spawnZ = i;
	}

	@Override
	public void setTime(long l) {
		this.time = l;
	}

	@Override
	public void setTimeOfDay(long l) {
		this.timeOfDay = l;
	}

	@Override
	public void setSpawnPos(BlockPos blockPos) {
		this.spawnX = blockPos.getX();
		this.spawnY = blockPos.getY();
		this.spawnZ = blockPos.getZ();
	}

	@Override
	public String getLevelName() {
		return this.levelName;
	}

	@Override
	public int getVersion() {
		return this.version;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public long getLastPlayed() {
		return this.lastPlayed;
	}

	@Override
	public int getClearWeatherTime() {
		return this.clearWeatherTime;
	}

	@Override
	public void setClearWeatherTime(int i) {
		this.clearWeatherTime = i;
	}

	@Override
	public boolean isThundering() {
		return this.thundering;
	}

	@Override
	public void setThundering(boolean bl) {
		this.thundering = bl;
	}

	@Override
	public int getThunderTime() {
		return this.thunderTime;
	}

	@Override
	public void setThunderTime(int i) {
		this.thunderTime = i;
	}

	@Override
	public boolean isRaining() {
		return this.raining;
	}

	@Override
	public void setRaining(boolean bl) {
		this.raining = bl;
	}

	@Override
	public int getRainTime() {
		return this.rainTime;
	}

	@Override
	public void setRainTime(int i) {
		this.rainTime = i;
	}

	@Override
	public GameMode getGameMode() {
		return this.gameMode;
	}

	@Override
	public boolean method_27420() {
		return this.structures;
	}

	@Override
	public void setGameMode(GameMode gameMode) {
		this.gameMode = gameMode;
	}

	@Override
	public boolean isHardcore() {
		return this.hardcore;
	}

	@Override
	public LevelGeneratorType getGeneratorType() {
		return this.generatorOptions.getType();
	}

	@Override
	public LevelGeneratorOptions method_27421() {
		return this.generatorOptions;
	}

	@Override
	public boolean areCommandsAllowed() {
		return this.commandsAllowed;
	}

	@Override
	public boolean isInitialized() {
		return this.initialized;
	}

	@Override
	public void setInitialized(boolean bl) {
		this.initialized = bl;
	}

	@Override
	public GameRules getGameRules() {
		return this.gameRules;
	}

	@Override
	public WorldBorder.class_5200 method_27422() {
		return this.field_24193;
	}

	@Override
	public void method_27415(WorldBorder.class_5200 arg) {
		this.field_24193 = arg;
	}

	@Override
	public Difficulty getDifficulty() {
		return this.difficulty;
	}

	@Override
	public void setDifficulty(Difficulty difficulty) {
		this.difficulty = difficulty;
	}

	@Override
	public boolean isDifficultyLocked() {
		return this.difficultyLocked;
	}

	@Override
	public void setDifficultyLocked(boolean bl) {
		this.difficultyLocked = bl;
	}

	@Override
	public Timer<MinecraftServer> getScheduledEvents() {
		return this.scheduledEvents;
	}

	@Override
	public void populateCrashReport(CrashReportSection crashReportSection) {
		class_5268.super.populateCrashReport(crashReportSection);
		class_5219.super.populateCrashReport(crashReportSection);
	}

	@Override
	public CompoundTag method_27434(DimensionType dimensionType) {
		CompoundTag compoundTag = (CompoundTag)this.worldData.get(dimensionType);
		return compoundTag == null ? new CompoundTag() : compoundTag;
	}

	@Override
	public void method_27435(DimensionType dimensionType, CompoundTag compoundTag) {
		this.worldData.put(dimensionType, compoundTag);
	}

	@Override
	public CompoundTag getWorldData() {
		return this.method_27434(DimensionType.OVERWORLD);
	}

	@Override
	public void setWorldData(CompoundTag compoundTag) {
		this.method_27435(DimensionType.OVERWORLD, compoundTag);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public int getVersionId() {
		return this.versionId;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean isVersionSnapshot() {
		return this.versionSnapshot;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public String getVersionName() {
		return this.versionName;
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
	public void setCustomBossEvents(@Nullable CompoundTag compoundTag) {
		this.customBossEvents = compoundTag;
	}

	@Override
	public int getWanderingTraderSpawnDelay() {
		return this.wanderingTraderSpawnDelay;
	}

	@Override
	public void setWanderingTraderSpawnDelay(int i) {
		this.wanderingTraderSpawnDelay = i;
	}

	@Override
	public int getWanderingTraderSpawnChance() {
		return this.wanderingTraderSpawnChance;
	}

	@Override
	public void setWanderingTraderSpawnChance(int i) {
		this.wanderingTraderSpawnChance = i;
	}

	@Override
	public void setWanderingTraderId(UUID uUID) {
		this.wanderingTraderId = uUID;
	}

	@Override
	public void addServerBrand(String string, boolean bl) {
		this.serverBrands.add(string);
		this.modded |= bl;
	}

	@Override
	public boolean method_27431() {
		return this.modded;
	}

	@Override
	public Set<String> method_27432() {
		return ImmutableSet.copyOf(this.serverBrands);
	}

	@Override
	public class_5268 method_27859() {
		return this;
	}

	@Override
	public LevelInfo method_27433() {
		LevelInfo levelInfo = new LevelInfo(
			this.levelName, this.randomSeed, this.gameMode, this.structures, this.hardcore, this.difficulty, this.generatorOptions, this.gameRules.copy()
		);
		if (this.field_24192) {
			levelInfo = levelInfo.setBonusChest();
		}

		if (this.commandsAllowed) {
			levelInfo = levelInfo.enableCommands();
		}

		return levelInfo;
	}
}
