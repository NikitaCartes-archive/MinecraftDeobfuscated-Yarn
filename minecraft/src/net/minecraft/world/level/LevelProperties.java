package net.minecraft.world.level;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DataFixTypes;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.JsonOps;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.datafixers.NbtOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.TagHelper;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.crash.ICrashCallable;
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
	private LevelGeneratorType generatorType = LevelGeneratorType.DEFAULT;
	private CompoundTag generatorOptions = new CompoundTag();
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
	private final int playerWorldId;
	private boolean playerDataLoaded;
	private CompoundTag playerData;
	private int dimension;
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
	private final GameRules gameRules = new GameRules();
	private final Timer<MinecraftServer> scheduledEvents = new Timer<>(TimerCallbackSerializer.INSTANCE);

	protected LevelProperties() {
		this.dataFixer = null;
		this.playerWorldId = SharedConstants.getGameVersion().getWorldVersion();
		this.setGeneratorOptions(new CompoundTag());
	}

	public LevelProperties(CompoundTag compoundTag, DataFixer dataFixer, int i, @Nullable CompoundTag compoundTag2) {
		this.dataFixer = dataFixer;
		if (compoundTag.containsKey("Version", 10)) {
			CompoundTag compoundTag3 = compoundTag.getCompound("Version");
			this.versionName = compoundTag3.getString("Name");
			this.versionId = compoundTag3.getInt("Id");
			this.versionSnapshot = compoundTag3.getBoolean("Snapshot");
		}

		this.randomSeed = compoundTag.getLong("RandomSeed");
		if (compoundTag.containsKey("generatorName", 8)) {
			String string = compoundTag.getString("generatorName");
			this.generatorType = LevelGeneratorType.getTypeFromName(string);
			if (this.generatorType == null) {
				this.generatorType = LevelGeneratorType.DEFAULT;
			} else if (this.generatorType == LevelGeneratorType.CUSTOMIZED) {
				this.legacyCustomOptions = compoundTag.getString("generatorOptions");
			} else if (this.generatorType.isVersioned()) {
				int j = 0;
				if (compoundTag.containsKey("generatorVersion", 99)) {
					j = compoundTag.getInt("generatorVersion");
				}

				this.generatorType = this.generatorType.getTypeForVersion(j);
			}

			this.setGeneratorOptions(compoundTag.getCompound("generatorOptions"));
		}

		this.gameMode = GameMode.byId(compoundTag.getInt("GameType"));
		if (compoundTag.containsKey("legacy_custom_options", 8)) {
			this.legacyCustomOptions = compoundTag.getString("legacy_custom_options");
		}

		if (compoundTag.containsKey("MapFeatures", 99)) {
			this.structures = compoundTag.getBoolean("MapFeatures");
		} else {
			this.structures = true;
		}

		this.spawnX = compoundTag.getInt("SpawnX");
		this.spawnY = compoundTag.getInt("SpawnY");
		this.spawnZ = compoundTag.getInt("SpawnZ");
		this.time = compoundTag.getLong("Time");
		if (compoundTag.containsKey("DayTime", 99)) {
			this.timeOfDay = compoundTag.getLong("DayTime");
		} else {
			this.timeOfDay = this.time;
		}

		this.lastPlayed = compoundTag.getLong("LastPlayed");
		this.sizeOnDisk = compoundTag.getLong("SizeOnDisk");
		this.levelName = compoundTag.getString("LevelName");
		this.version = compoundTag.getInt("version");
		this.clearWeatherTime = compoundTag.getInt("clearWeatherTime");
		this.rainTime = compoundTag.getInt("rainTime");
		this.raining = compoundTag.getBoolean("raining");
		this.thunderTime = compoundTag.getInt("thunderTime");
		this.thundering = compoundTag.getBoolean("thundering");
		this.hardcore = compoundTag.getBoolean("hardcore");
		if (compoundTag.containsKey("initialized", 99)) {
			this.initialized = compoundTag.getBoolean("initialized");
		} else {
			this.initialized = true;
		}

		if (compoundTag.containsKey("allowCommands", 99)) {
			this.commandsAllowed = compoundTag.getBoolean("allowCommands");
		} else {
			this.commandsAllowed = this.gameMode == GameMode.field_9220;
		}

		this.playerWorldId = i;
		if (compoundTag2 != null) {
			this.playerData = compoundTag2;
		}

		if (compoundTag.containsKey("GameRules", 10)) {
			this.gameRules.deserialize(compoundTag.getCompound("GameRules"));
		}

		if (compoundTag.containsKey("Difficulty", 99)) {
			this.difficulty = Difficulty.getDifficulty(compoundTag.getByte("Difficulty"));
		}

		if (compoundTag.containsKey("DifficultyLocked", 1)) {
			this.difficultyLocked = compoundTag.getBoolean("DifficultyLocked");
		}

		if (compoundTag.containsKey("BorderCenterX", 99)) {
			this.borderCenterX = compoundTag.getDouble("BorderCenterX");
		}

		if (compoundTag.containsKey("BorderCenterZ", 99)) {
			this.borderCenterZ = compoundTag.getDouble("BorderCenterZ");
		}

		if (compoundTag.containsKey("BorderSize", 99)) {
			this.borderSize = compoundTag.getDouble("BorderSize");
		}

		if (compoundTag.containsKey("BorderSizeLerpTime", 99)) {
			this.borderSizeLerpTime = compoundTag.getLong("BorderSizeLerpTime");
		}

		if (compoundTag.containsKey("BorderSizeLerpTarget", 99)) {
			this.borderSizeLerpTarget = compoundTag.getDouble("BorderSizeLerpTarget");
		}

		if (compoundTag.containsKey("BorderSafeZone", 99)) {
			this.borderSafeZone = compoundTag.getDouble("BorderSafeZone");
		}

		if (compoundTag.containsKey("BorderDamagePerBlock", 99)) {
			this.borderDamagePerBlock = compoundTag.getDouble("BorderDamagePerBlock");
		}

		if (compoundTag.containsKey("BorderWarningBlocks", 99)) {
			this.borderWarningBlocks = compoundTag.getInt("BorderWarningBlocks");
		}

		if (compoundTag.containsKey("BorderWarningTime", 99)) {
			this.borderWarningTime = compoundTag.getInt("BorderWarningTime");
		}

		if (compoundTag.containsKey("DimensionData", 10)) {
			CompoundTag compoundTag3 = compoundTag.getCompound("DimensionData");

			for (String string2 : compoundTag3.getKeys()) {
				this.worldData.put(DimensionType.byRawId(Integer.parseInt(string2)), compoundTag3.getCompound(string2));
			}
		}

		if (compoundTag.containsKey("DataPacks", 10)) {
			CompoundTag compoundTag3 = compoundTag.getCompound("DataPacks");
			ListTag listTag = compoundTag3.getList("Disabled", 8);

			for (int k = 0; k < listTag.size(); k++) {
				this.disabledDataPacks.add(listTag.getString(k));
			}

			ListTag listTag2 = compoundTag3.getList("Enabled", 8);

			for (int l = 0; l < listTag2.size(); l++) {
				this.enabledDataPacks.add(listTag2.getString(l));
			}
		}

		if (compoundTag.containsKey("CustomBossEvents", 10)) {
			this.customBossEvents = compoundTag.getCompound("CustomBossEvents");
		}

		if (compoundTag.containsKey("ScheduledEvents", 9)) {
			this.scheduledEvents.fromTag(compoundTag.getList("ScheduledEvents", 10));
		}

		if (compoundTag.containsKey("WanderingTraderSpawnDelay", 99)) {
			this.wanderingTraderSpawnDelay = compoundTag.getInt("WanderingTraderSpawnDelay");
		}

		if (compoundTag.containsKey("WanderingTraderSpawnChance", 99)) {
			this.wanderingTraderSpawnChance = compoundTag.getInt("WanderingTraderSpawnChance");
		}

		if (compoundTag.containsKey("WanderingTraderId", 8)) {
			this.wanderingTraderId = UUID.fromString(compoundTag.getString("WanderingTraderId"));
		}
	}

	public LevelProperties(LevelInfo levelInfo, String string) {
		this.dataFixer = null;
		this.playerWorldId = SharedConstants.getGameVersion().getWorldVersion();
		this.loadLevelInfo(levelInfo);
		this.levelName = string;
		this.difficulty = DEFAULT_DIFFICULTY;
		this.initialized = false;
	}

	public void loadLevelInfo(LevelInfo levelInfo) {
		this.randomSeed = levelInfo.getSeed();
		this.gameMode = levelInfo.getGameMode();
		this.structures = levelInfo.hasStructures();
		this.hardcore = levelInfo.isHardcore();
		this.generatorType = levelInfo.getGeneratorType();
		this.setGeneratorOptions((CompoundTag)Dynamic.convert(JsonOps.INSTANCE, NbtOps.INSTANCE, levelInfo.getGeneratorOptions()));
		this.commandsAllowed = levelInfo.allowCommands();
	}

	public CompoundTag cloneWorldTag(@Nullable CompoundTag compoundTag) {
		this.loadPlayerData();
		if (compoundTag == null) {
			compoundTag = this.playerData;
		}

		CompoundTag compoundTag2 = new CompoundTag();
		this.updateProperties(compoundTag2, compoundTag);
		return compoundTag2;
	}

	private void updateProperties(CompoundTag compoundTag, CompoundTag compoundTag2) {
		CompoundTag compoundTag3 = new CompoundTag();
		compoundTag3.putString("Name", SharedConstants.getGameVersion().getName());
		compoundTag3.putInt("Id", SharedConstants.getGameVersion().getWorldVersion());
		compoundTag3.putBoolean("Snapshot", !SharedConstants.getGameVersion().isStable());
		compoundTag.put("Version", compoundTag3);
		compoundTag.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
		compoundTag.putLong("RandomSeed", this.randomSeed);
		compoundTag.putString("generatorName", this.generatorType.getStoredName());
		compoundTag.putInt("generatorVersion", this.generatorType.getVersion());
		if (!this.generatorOptions.isEmpty()) {
			compoundTag.put("generatorOptions", this.generatorOptions);
		}

		if (this.legacyCustomOptions != null) {
			compoundTag.putString("legacy_custom_options", this.legacyCustomOptions);
		}

		compoundTag.putInt("GameType", this.gameMode.getId());
		compoundTag.putBoolean("MapFeatures", this.structures);
		compoundTag.putInt("SpawnX", this.spawnX);
		compoundTag.putInt("SpawnY", this.spawnY);
		compoundTag.putInt("SpawnZ", this.spawnZ);
		compoundTag.putLong("Time", this.time);
		compoundTag.putLong("DayTime", this.timeOfDay);
		compoundTag.putLong("SizeOnDisk", this.sizeOnDisk);
		compoundTag.putLong("LastPlayed", SystemUtil.getEpochTimeMs());
		compoundTag.putString("LevelName", this.levelName);
		compoundTag.putInt("version", this.version);
		compoundTag.putInt("clearWeatherTime", this.clearWeatherTime);
		compoundTag.putInt("rainTime", this.rainTime);
		compoundTag.putBoolean("raining", this.raining);
		compoundTag.putInt("thunderTime", this.thunderTime);
		compoundTag.putBoolean("thundering", this.thundering);
		compoundTag.putBoolean("hardcore", this.hardcore);
		compoundTag.putBoolean("allowCommands", this.commandsAllowed);
		compoundTag.putBoolean("initialized", this.initialized);
		compoundTag.putDouble("BorderCenterX", this.borderCenterX);
		compoundTag.putDouble("BorderCenterZ", this.borderCenterZ);
		compoundTag.putDouble("BorderSize", this.borderSize);
		compoundTag.putLong("BorderSizeLerpTime", this.borderSizeLerpTime);
		compoundTag.putDouble("BorderSafeZone", this.borderSafeZone);
		compoundTag.putDouble("BorderDamagePerBlock", this.borderDamagePerBlock);
		compoundTag.putDouble("BorderSizeLerpTarget", this.borderSizeLerpTarget);
		compoundTag.putDouble("BorderWarningBlocks", (double)this.borderWarningBlocks);
		compoundTag.putDouble("BorderWarningTime", (double)this.borderWarningTime);
		if (this.difficulty != null) {
			compoundTag.putByte("Difficulty", (byte)this.difficulty.getId());
		}

		compoundTag.putBoolean("DifficultyLocked", this.difficultyLocked);
		compoundTag.put("GameRules", this.gameRules.serialize());
		CompoundTag compoundTag4 = new CompoundTag();

		for (Entry<DimensionType, CompoundTag> entry : this.worldData.entrySet()) {
			compoundTag4.put(String.valueOf(((DimensionType)entry.getKey()).getRawId()), (Tag)entry.getValue());
		}

		compoundTag.put("DimensionData", compoundTag4);
		if (compoundTag2 != null) {
			compoundTag.put("Player", compoundTag2);
		}

		CompoundTag compoundTag5 = new CompoundTag();
		ListTag listTag = new ListTag();

		for (String string : this.enabledDataPacks) {
			listTag.add(new StringTag(string));
		}

		compoundTag5.put("Enabled", listTag);
		ListTag listTag2 = new ListTag();

		for (String string2 : this.disabledDataPacks) {
			listTag2.add(new StringTag(string2));
		}

		compoundTag5.put("Disabled", listTag2);
		compoundTag.put("DataPacks", compoundTag5);
		if (this.customBossEvents != null) {
			compoundTag.put("CustomBossEvents", this.customBossEvents);
		}

		compoundTag.put("ScheduledEvents", this.scheduledEvents.toTag());
		compoundTag.putInt("WanderingTraderSpawnDelay", this.wanderingTraderSpawnDelay);
		compoundTag.putInt("WanderingTraderSpawnChance", this.wanderingTraderSpawnChance);
		if (this.wanderingTraderId != null) {
			compoundTag.putString("WanderingTraderId", this.wanderingTraderId.toString());
		}
	}

	public long getSeed() {
		return this.randomSeed;
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
			if (this.playerWorldId < SharedConstants.getGameVersion().getWorldVersion()) {
				if (this.dataFixer == null) {
					throw new NullPointerException("Fixer Upper not set inside LevelData, and the player tag is not upgraded.");
				}

				this.playerData = TagHelper.update(this.dataFixer, DataFixTypes.PLAYER, this.playerData, this.playerWorldId);
			}

			this.dimension = this.playerData.getInt("Dimension");
			this.playerDataLoaded = true;
		}
	}

	public CompoundTag getPlayerData() {
		this.loadPlayerData();
		return this.playerData;
	}

	@Environment(EnvType.CLIENT)
	public int getDimension() {
		this.loadPlayerData();
		return this.dimension;
	}

	@Environment(EnvType.CLIENT)
	public void setSpawnX(int i) {
		this.spawnX = i;
	}

	@Environment(EnvType.CLIENT)
	public void setSpawnY(int i) {
		this.spawnY = i;
	}

	@Environment(EnvType.CLIENT)
	public void setSpawnZ(int i) {
		this.spawnZ = i;
	}

	public void setTime(long l) {
		this.time = l;
	}

	public void setTimeOfDay(long l) {
		this.timeOfDay = l;
	}

	public void setSpawnPos(BlockPos blockPos) {
		this.spawnX = blockPos.getX();
		this.spawnY = blockPos.getY();
		this.spawnZ = blockPos.getZ();
	}

	public String getLevelName() {
		return this.levelName;
	}

	public void setLevelName(String string) {
		this.levelName = string;
	}

	public int getVersion() {
		return this.version;
	}

	public void setVersion(int i) {
		this.version = i;
	}

	@Environment(EnvType.CLIENT)
	public long getLastPlayed() {
		return this.lastPlayed;
	}

	public int getClearWeatherTime() {
		return this.clearWeatherTime;
	}

	public void setClearWeatherTime(int i) {
		this.clearWeatherTime = i;
	}

	public boolean isThundering() {
		return this.thundering;
	}

	public void setThundering(boolean bl) {
		this.thundering = bl;
	}

	public int getThunderTime() {
		return this.thunderTime;
	}

	public void setThunderTime(int i) {
		this.thunderTime = i;
	}

	public boolean isRaining() {
		return this.raining;
	}

	public void setRaining(boolean bl) {
		this.raining = bl;
	}

	public int getRainTime() {
		return this.rainTime;
	}

	public void setRainTime(int i) {
		this.rainTime = i;
	}

	public GameMode getGameMode() {
		return this.gameMode;
	}

	public boolean hasStructures() {
		return this.structures;
	}

	public void setStructures(boolean bl) {
		this.structures = bl;
	}

	public void setGameMode(GameMode gameMode) {
		this.gameMode = gameMode;
	}

	public boolean isHardcore() {
		return this.hardcore;
	}

	public void setHardcore(boolean bl) {
		this.hardcore = bl;
	}

	public LevelGeneratorType getGeneratorType() {
		return this.generatorType;
	}

	public void setGeneratorType(LevelGeneratorType levelGeneratorType) {
		this.generatorType = levelGeneratorType;
	}

	public CompoundTag getGeneratorOptions() {
		return this.generatorOptions;
	}

	public void setGeneratorOptions(CompoundTag compoundTag) {
		this.generatorOptions = compoundTag;
	}

	public boolean areCommandsAllowed() {
		return this.commandsAllowed;
	}

	public void setCommandsAllowed(boolean bl) {
		this.commandsAllowed = bl;
	}

	public boolean isInitialized() {
		return this.initialized;
	}

	public void setInitialized(boolean bl) {
		this.initialized = bl;
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

	public void setBorderSize(double d) {
		this.borderSize = d;
	}

	public long getBorderSizeLerpTime() {
		return this.borderSizeLerpTime;
	}

	public void setBorderSizeLerpTime(long l) {
		this.borderSizeLerpTime = l;
	}

	public double getBorderSizeLerpTarget() {
		return this.borderSizeLerpTarget;
	}

	public void setBorderSizeLerpTarget(double d) {
		this.borderSizeLerpTarget = d;
	}

	public void borderCenterZ(double d) {
		this.borderCenterZ = d;
	}

	public void setBorderCenterX(double d) {
		this.borderCenterX = d;
	}

	public double getBorderSafeZone() {
		return this.borderSafeZone;
	}

	public void setBorderSafeZone(double d) {
		this.borderSafeZone = d;
	}

	public double getBorderDamagePerBlock() {
		return this.borderDamagePerBlock;
	}

	public void setBorderDamagePerBlock(double d) {
		this.borderDamagePerBlock = d;
	}

	public int getBorderWarningBlocks() {
		return this.borderWarningBlocks;
	}

	public int getBorderWarningTime() {
		return this.borderWarningTime;
	}

	public void setBorderWarningBlocks(int i) {
		this.borderWarningBlocks = i;
	}

	public void setBorderWarningTime(int i) {
		this.borderWarningTime = i;
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

	public void setDifficultyLocked(boolean bl) {
		this.difficultyLocked = bl;
	}

	public Timer<MinecraftServer> getScheduledEvents() {
		return this.scheduledEvents;
	}

	public void populateCrashReport(CrashReportSection crashReportSection) {
		crashReportSection.add("Level seed", (ICrashCallable<String>)(() -> String.valueOf(this.getSeed())));
		crashReportSection.add(
			"Level generator",
			(ICrashCallable<String>)(() -> String.format(
					"ID %02d - %s, ver %d. Features enabled: %b", this.generatorType.getId(), this.generatorType.getName(), this.generatorType.getVersion(), this.structures
				))
		);
		crashReportSection.add("Level generator options", (ICrashCallable<String>)(() -> this.generatorOptions.toString()));
		crashReportSection.add("Level spawn location", (ICrashCallable<String>)(() -> CrashReportSection.createPositionString(this.spawnX, this.spawnY, this.spawnZ)));
		crashReportSection.add("Level time", (ICrashCallable<String>)(() -> String.format("%d game time, %d day time", this.time, this.timeOfDay)));
		crashReportSection.add("Level dimension", (ICrashCallable<String>)(() -> String.valueOf(this.dimension)));
		crashReportSection.add("Level storage version", (ICrashCallable<String>)(() -> {
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
		crashReportSection.add(
			"Level weather",
			(ICrashCallable<String>)(() -> String.format(
					"Rain time: %d (now: %b), thunder time: %d (now: %b)", this.rainTime, this.raining, this.thunderTime, this.thundering
				))
		);
		crashReportSection.add(
			"Level game mode",
			(ICrashCallable<String>)(() -> String.format(
					"Game mode: %s (ID %d). Hardcore: %b. Cheats: %b", this.gameMode.getName(), this.gameMode.getId(), this.hardcore, this.commandsAllowed
				))
		);
	}

	public CompoundTag getWorldData(DimensionType dimensionType) {
		CompoundTag compoundTag = (CompoundTag)this.worldData.get(dimensionType);
		return compoundTag == null ? new CompoundTag() : compoundTag;
	}

	public void setWorldData(DimensionType dimensionType, CompoundTag compoundTag) {
		this.worldData.put(dimensionType, compoundTag);
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

	public void setCustomBossEvents(@Nullable CompoundTag compoundTag) {
		this.customBossEvents = compoundTag;
	}

	public int getWanderingTraderSpawnDelay() {
		return this.wanderingTraderSpawnDelay;
	}

	public void setWanderingTraderSpawnDelay(int i) {
		this.wanderingTraderSpawnDelay = i;
	}

	public int getWanderingTraderSpawnChance() {
		return this.wanderingTraderSpawnChance;
	}

	public void setWanderingTraderSpawnChance(int i) {
		this.wanderingTraderSpawnChance = i;
	}

	public void setWanderingTraderId(UUID uUID) {
		this.wanderingTraderId = uUID;
	}
}
