package net.minecraft.world.level;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.hash.Hashing;
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
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.timer.Timer;
import net.minecraft.world.timer.TimerCallbackSerializer;

public class LevelProperties {
	private String versionName;
	private int versionId;
	private boolean versionSnapshot;
	public static final Difficulty DEFAULT_DIFFICULTY = Difficulty.NORMAL;
	private long randomSeed;
	private LevelGeneratorOptions generatorOptions = LevelGeneratorType.DEFAULT.getDefaultOptions();
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
	private String levelName;
	private int version;
	private int clearWeatherTime;
	private boolean raining;
	private int rainTime;
	private boolean thundering;
	private int thunderTime;
	private GameMode gameMode;
	private boolean structures;
	private boolean hardcore;
	private boolean commandsAllowed;
	private boolean initialized;
	private Difficulty difficulty;
	private boolean difficultyLocked;
	private double borderCenterX;
	private double borderCenterZ;
	private double borderSize = 6.0E7;
	private long borderSizeLerpTime;
	private double borderSizeLerpTarget;
	private double borderSafeZone = 5.0;
	private double borderDamagePerBlock = 0.2;
	private int borderWarningBlocks = 5;
	private int borderWarningTime = 15;
	private final Set<String> disabledDataPacks = Sets.<String>newHashSet();
	private final Set<String> enabledDataPacks = Sets.<String>newLinkedHashSet();
	private final Map<DimensionType, CompoundTag> worldData = Maps.<DimensionType, CompoundTag>newIdentityHashMap();
	private CompoundTag customBossEvents;
	private int wanderingTraderSpawnDelay;
	private int wanderingTraderSpawnChance;
	private UUID wanderingTraderId;
	private Set<String> serverBrands = Sets.<String>newLinkedHashSet();
	private boolean modded;
	private final GameRules gameRules = new GameRules();
	private final Timer<MinecraftServer> scheduledEvents = new Timer<>(TimerCallbackSerializer.INSTANCE);

	protected LevelProperties() {
		this.dataFixer = null;
		this.dataVersion = SharedConstants.getGameVersion().getWorldVersion();
	}

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

		if (tag.contains("BorderCenterX", 99)) {
			this.borderCenterX = tag.getDouble("BorderCenterX");
		}

		if (tag.contains("BorderCenterZ", 99)) {
			this.borderCenterZ = tag.getDouble("BorderCenterZ");
		}

		if (tag.contains("BorderSize", 99)) {
			this.borderSize = tag.getDouble("BorderSize");
		}

		if (tag.contains("BorderSizeLerpTime", 99)) {
			this.borderSizeLerpTime = tag.getLong("BorderSizeLerpTime");
		}

		if (tag.contains("BorderSizeLerpTarget", 99)) {
			this.borderSizeLerpTarget = tag.getDouble("BorderSizeLerpTarget");
		}

		if (tag.contains("BorderSafeZone", 99)) {
			this.borderSafeZone = tag.getDouble("BorderSafeZone");
		}

		if (tag.contains("BorderDamagePerBlock", 99)) {
			this.borderDamagePerBlock = tag.getDouble("BorderDamagePerBlock");
		}

		if (tag.contains("BorderWarningBlocks", 99)) {
			this.borderWarningBlocks = tag.getInt("BorderWarningBlocks");
		}

		if (tag.contains("BorderWarningTime", 99)) {
			this.borderWarningTime = tag.getInt("BorderWarningTime");
		}

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

	public LevelProperties(LevelInfo info, String levelName) {
		this.dataFixer = null;
		this.dataVersion = SharedConstants.getGameVersion().getWorldVersion();
		this.loadLevelInfo(info);
		this.levelName = levelName;
		this.difficulty = DEFAULT_DIFFICULTY;
		this.initialized = false;
	}

	public void loadLevelInfo(LevelInfo levelInfo) {
		this.randomSeed = levelInfo.getSeed();
		this.gameMode = levelInfo.getGameMode();
		this.structures = levelInfo.hasStructures();
		this.hardcore = levelInfo.isHardcore();
		this.generatorOptions = levelInfo.getGeneratorOptions();
		this.commandsAllowed = levelInfo.allowCommands();
	}

	public CompoundTag cloneWorldTag(@Nullable CompoundTag playerTag) {
		this.loadPlayerData();
		if (playerTag == null) {
			playerTag = this.playerData;
		}

		CompoundTag compoundTag = new CompoundTag();
		this.updateProperties(compoundTag, playerTag);
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
		levelTag.putInt("version", this.version);
		levelTag.putInt("clearWeatherTime", this.clearWeatherTime);
		levelTag.putInt("rainTime", this.rainTime);
		levelTag.putBoolean("raining", this.raining);
		levelTag.putInt("thunderTime", this.thunderTime);
		levelTag.putBoolean("thundering", this.thundering);
		levelTag.putBoolean("hardcore", this.hardcore);
		levelTag.putBoolean("allowCommands", this.commandsAllowed);
		levelTag.putBoolean("initialized", this.initialized);
		levelTag.putDouble("BorderCenterX", this.borderCenterX);
		levelTag.putDouble("BorderCenterZ", this.borderCenterZ);
		levelTag.putDouble("BorderSize", this.borderSize);
		levelTag.putLong("BorderSizeLerpTime", this.borderSizeLerpTime);
		levelTag.putDouble("BorderSafeZone", this.borderSafeZone);
		levelTag.putDouble("BorderDamagePerBlock", this.borderDamagePerBlock);
		levelTag.putDouble("BorderSizeLerpTarget", this.borderSizeLerpTarget);
		levelTag.putDouble("BorderWarningBlocks", (double)this.borderWarningBlocks);
		levelTag.putDouble("BorderWarningTime", (double)this.borderWarningTime);
		if (this.difficulty != null) {
			levelTag.putByte("Difficulty", (byte)this.difficulty.getId());
		}

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

	public long getSeed() {
		return this.randomSeed;
	}

	public static long sha256Hash(long seed) {
		return Hashing.sha256().hashLong(seed).asLong();
	}

	public int getSpawnX() {
		return this.spawnX;
	}

	public int getSpawnY() {
		return this.spawnY;
	}

	public int getSpawnZ() {
		return this.spawnZ;
	}

	public long getTime() {
		return this.time;
	}

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

	public CompoundTag getPlayerData() {
		this.loadPlayerData();
		return this.playerData;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public void setTimeOfDay(long timeOfDay) {
		this.timeOfDay = timeOfDay;
	}

	public void setSpawnPos(BlockPos pos) {
		this.spawnX = pos.getX();
		this.spawnY = pos.getY();
		this.spawnZ = pos.getZ();
	}

	public String getLevelName() {
		return this.levelName;
	}

	public void setLevelName(String levelName) {
		this.levelName = levelName;
	}

	public int getVersion() {
		return this.version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Environment(EnvType.CLIENT)
	public long getLastPlayed() {
		return this.lastPlayed;
	}

	public int getClearWeatherTime() {
		return this.clearWeatherTime;
	}

	public void setClearWeatherTime(int clearWeatherTime) {
		this.clearWeatherTime = clearWeatherTime;
	}

	public boolean isThundering() {
		return this.thundering;
	}

	public void setThundering(boolean thundering) {
		this.thundering = thundering;
	}

	public int getThunderTime() {
		return this.thunderTime;
	}

	public void setThunderTime(int thunderTime) {
		this.thunderTime = thunderTime;
	}

	public boolean isRaining() {
		return this.raining;
	}

	public void setRaining(boolean raining) {
		this.raining = raining;
	}

	public int getRainTime() {
		return this.rainTime;
	}

	public void setRainTime(int rainTime) {
		this.rainTime = rainTime;
	}

	public GameMode getGameMode() {
		return this.gameMode;
	}

	public boolean hasStructures() {
		return this.structures;
	}

	public void setStructures(boolean structures) {
		this.structures = structures;
	}

	public void setGameMode(GameMode gameMode) {
		this.gameMode = gameMode;
	}

	public boolean isHardcore() {
		return this.hardcore;
	}

	public void setHardcore(boolean hardcore) {
		this.hardcore = hardcore;
	}

	public LevelGeneratorType getGeneratorType() {
		return this.generatorOptions.getType();
	}

	public LevelGeneratorOptions getGeneratorOptions() {
		return this.generatorOptions;
	}

	public void setGeneratorOptions(LevelGeneratorOptions options) {
		this.generatorOptions = options;
	}

	public boolean areCommandsAllowed() {
		return this.commandsAllowed;
	}

	public void setCommandsAllowed(boolean commandsAllowed) {
		this.commandsAllowed = commandsAllowed;
	}

	public boolean isInitialized() {
		return this.initialized;
	}

	public void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}

	public GameRules getGameRules() {
		return this.gameRules;
	}

	public double getBorderCenterX() {
		return this.borderCenterX;
	}

	public double getBorderCenterZ() {
		return this.borderCenterZ;
	}

	public double getBorderSize() {
		return this.borderSize;
	}

	public void setBorderSize(double borderSize) {
		this.borderSize = borderSize;
	}

	public long getBorderSizeLerpTime() {
		return this.borderSizeLerpTime;
	}

	public void setBorderSizeLerpTime(long borderSizeLerpTime) {
		this.borderSizeLerpTime = borderSizeLerpTime;
	}

	public double getBorderSizeLerpTarget() {
		return this.borderSizeLerpTarget;
	}

	public void setBorderSizeLerpTarget(double borderSizeLerpTarget) {
		this.borderSizeLerpTarget = borderSizeLerpTarget;
	}

	public void borderCenterZ(double borderCenterZ) {
		this.borderCenterZ = borderCenterZ;
	}

	public void setBorderCenterX(double borderCenterX) {
		this.borderCenterX = borderCenterX;
	}

	public double getBorderSafeZone() {
		return this.borderSafeZone;
	}

	public void setBorderSafeZone(double borderSafeZone) {
		this.borderSafeZone = borderSafeZone;
	}

	public double getBorderDamagePerBlock() {
		return this.borderDamagePerBlock;
	}

	public void setBorderDamagePerBlock(double borderDamagePerBlock) {
		this.borderDamagePerBlock = borderDamagePerBlock;
	}

	public int getBorderWarningBlocks() {
		return this.borderWarningBlocks;
	}

	public int getBorderWarningTime() {
		return this.borderWarningTime;
	}

	public void setBorderWarningBlocks(int borderWarningBlocks) {
		this.borderWarningBlocks = borderWarningBlocks;
	}

	public void setBorderWarningTime(int borderWarningTime) {
		this.borderWarningTime = borderWarningTime;
	}

	public Difficulty getDifficulty() {
		return this.difficulty;
	}

	public void setDifficulty(Difficulty difficulty) {
		this.difficulty = difficulty;
	}

	public boolean isDifficultyLocked() {
		return this.difficultyLocked;
	}

	public void setDifficultyLocked(boolean difficultyLocked) {
		this.difficultyLocked = difficultyLocked;
	}

	public Timer<MinecraftServer> getScheduledEvents() {
		return this.scheduledEvents;
	}

	public void populateCrashReport(CrashReportSection section) {
		section.add("Level name", (CrashCallable<String>)(() -> this.levelName));
		section.add("Level seed", (CrashCallable<String>)(() -> String.valueOf(this.randomSeed)));
		section.add(
			"Level generator",
			(CrashCallable<String>)(() -> String.format(
					"ID %02d - %s, ver %d. Features enabled: %b",
					this.generatorOptions.getType().getId(),
					this.generatorOptions.getType().getName(),
					this.generatorOptions.getType().getVersion(),
					this.structures
				))
		);
		section.add("Level generator options", (CrashCallable<String>)(() -> this.generatorOptions.getDynamic().toString()));
		section.add("Level spawn location", (CrashCallable<String>)(() -> CrashReportSection.createPositionString(this.spawnX, this.spawnY, this.spawnZ)));
		section.add("Level time", (CrashCallable<String>)(() -> String.format("%d game time, %d day time", this.time, this.timeOfDay)));
		section.add("Known server brands", (CrashCallable<String>)(() -> String.join(", ", this.serverBrands)));
		section.add("Level was modded", (CrashCallable<String>)(() -> Boolean.toString(this.modded)));
		section.add("Level storage version", (CrashCallable<String>)(() -> {
			String string = "Unknown?";

			try {
				switch (this.version) {
					case 19132:
						string = "McRegion";
						break;
					case 19133:
						string = "Anvil";
				}
			} catch (Throwable var3) {
			}

			return String.format("0x%05X - %s", this.version, string);
		}));
		section.add(
			"Level weather",
			(CrashCallable<String>)(() -> String.format(
					"Rain time: %d (now: %b), thunder time: %d (now: %b)", this.rainTime, this.raining, this.thunderTime, this.thundering
				))
		);
		section.add(
			"Level game mode",
			(CrashCallable<String>)(() -> String.format(
					"Game mode: %s (ID %d). Hardcore: %b. Cheats: %b", this.gameMode.getName(), this.gameMode.getId(), this.hardcore, this.commandsAllowed
				))
		);
	}

	public CompoundTag getWorldData(DimensionType type) {
		CompoundTag compoundTag = (CompoundTag)this.worldData.get(type);
		return compoundTag == null ? new CompoundTag() : compoundTag;
	}

	public void setWorldData(DimensionType type, CompoundTag tag) {
		this.worldData.put(type, tag);
	}

	@Environment(EnvType.CLIENT)
	public int getVersionId() {
		return this.versionId;
	}

	@Environment(EnvType.CLIENT)
	public boolean isVersionSnapshot() {
		return this.versionSnapshot;
	}

	@Environment(EnvType.CLIENT)
	public String getVersionName() {
		return this.versionName;
	}

	public Set<String> getDisabledDataPacks() {
		return this.disabledDataPacks;
	}

	public Set<String> getEnabledDataPacks() {
		return this.enabledDataPacks;
	}

	@Nullable
	public CompoundTag getCustomBossEvents() {
		return this.customBossEvents;
	}

	public void setCustomBossEvents(@Nullable CompoundTag customBossEvents) {
		this.customBossEvents = customBossEvents;
	}

	public int getWanderingTraderSpawnDelay() {
		return this.wanderingTraderSpawnDelay;
	}

	public void setWanderingTraderSpawnDelay(int wanderingTraderSpawnDelay) {
		this.wanderingTraderSpawnDelay = wanderingTraderSpawnDelay;
	}

	public int getWanderingTraderSpawnChance() {
		return this.wanderingTraderSpawnChance;
	}

	public void setWanderingTraderSpawnChance(int wanderingTraderSpawnChance) {
		this.wanderingTraderSpawnChance = wanderingTraderSpawnChance;
	}

	public void setWanderingTraderId(UUID wanderingTraderId) {
		this.wanderingTraderId = wanderingTraderId;
	}

	public void addServerBrand(String brand, boolean moddedMessagePresent) {
		this.serverBrands.add(brand);
		this.modded |= moddedMessagePresent;
	}
}
