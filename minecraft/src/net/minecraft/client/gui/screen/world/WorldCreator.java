package net.minecraft.client.gui.screen.world;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.GeneratorOptionsHolder;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.registry.tag.WorldPresetTags;
import net.minecraft.resource.DataConfiguration;
import net.minecraft.text.Text;
import net.minecraft.util.PathUtil;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.WorldPreset;
import net.minecraft.world.gen.WorldPresets;

@Environment(EnvType.CLIENT)
public class WorldCreator {
	private static final Text NEW_WORLD_NAME = Text.translatable("selectWorld.newWorld");
	private final List<Consumer<WorldCreator>> listeners = new ArrayList();
	private String worldName = NEW_WORLD_NAME.getString();
	private WorldCreator.Mode gameMode = WorldCreator.Mode.SURVIVAL;
	private Difficulty difficulty = Difficulty.NORMAL;
	@Nullable
	private Boolean cheatsEnabled;
	private String seed;
	private boolean generateStructures;
	private boolean bonusChestEnabled;
	private final Path savesDirectory;
	private String worldDirectoryName;
	private GeneratorOptionsHolder generatorOptionsHolder;
	private WorldCreator.WorldType worldType;
	private final List<WorldCreator.WorldType> normalWorldTypes = new ArrayList();
	private final List<WorldCreator.WorldType> extendedWorldTypes = new ArrayList();
	private GameRules gameRules = new GameRules();

	public WorldCreator(Path savesDirectory, GeneratorOptionsHolder generatorOptionsHolder, Optional<RegistryKey<WorldPreset>> defaultWorldType, OptionalLong seed) {
		this.savesDirectory = savesDirectory;
		this.generatorOptionsHolder = generatorOptionsHolder;
		this.worldType = new WorldCreator.WorldType((RegistryEntry<WorldPreset>)getWorldPreset(generatorOptionsHolder, defaultWorldType).orElse(null));
		this.updateWorldTypeLists();
		this.seed = seed.isPresent() ? Long.toString(seed.getAsLong()) : "";
		this.generateStructures = generatorOptionsHolder.generatorOptions().shouldGenerateStructures();
		this.bonusChestEnabled = generatorOptionsHolder.generatorOptions().hasBonusChest();
		this.worldDirectoryName = this.toDirectoryName(this.worldName);
	}

	public void addListener(Consumer<WorldCreator> listener) {
		this.listeners.add(listener);
	}

	public void update() {
		boolean bl = this.isBonusChestEnabled();
		if (bl != this.generatorOptionsHolder.generatorOptions().hasBonusChest()) {
			this.generatorOptionsHolder = this.generatorOptionsHolder.apply(options -> options.withBonusChest(bl));
		}

		boolean bl2 = this.shouldGenerateStructures();
		if (bl2 != this.generatorOptionsHolder.generatorOptions().shouldGenerateStructures()) {
			this.generatorOptionsHolder = this.generatorOptionsHolder.apply(options -> options.withStructures(bl2));
		}

		for (Consumer<WorldCreator> consumer : this.listeners) {
			consumer.accept(this);
		}
	}

	public void setWorldName(String worldName) {
		this.worldName = worldName;
		this.worldDirectoryName = this.toDirectoryName(worldName);
		this.update();
	}

	private String toDirectoryName(String worldName) {
		String string = worldName.trim();

		try {
			return PathUtil.getNextUniqueName(this.savesDirectory, !string.isEmpty() ? string : NEW_WORLD_NAME.getString(), "");
		} catch (Exception var5) {
			try {
				return PathUtil.getNextUniqueName(this.savesDirectory, "World", "");
			} catch (IOException var4) {
				throw new RuntimeException("Could not create save folder", var4);
			}
		}
	}

	public String getWorldName() {
		return this.worldName;
	}

	public String getWorldDirectoryName() {
		return this.worldDirectoryName;
	}

	public void setGameMode(WorldCreator.Mode gameMode) {
		this.gameMode = gameMode;
		this.update();
	}

	public WorldCreator.Mode getGameMode() {
		return this.isDebug() ? WorldCreator.Mode.DEBUG : this.gameMode;
	}

	public void setDifficulty(Difficulty difficulty) {
		this.difficulty = difficulty;
		this.update();
	}

	public Difficulty getDifficulty() {
		return this.isHardcore() ? Difficulty.HARD : this.difficulty;
	}

	public boolean isHardcore() {
		return this.getGameMode() == WorldCreator.Mode.HARDCORE;
	}

	public void setCheatsEnabled(boolean cheatsEnabled) {
		this.cheatsEnabled = cheatsEnabled;
		this.update();
	}

	public boolean areCheatsEnabled() {
		if (this.isDebug()) {
			return true;
		} else if (this.isHardcore()) {
			return false;
		} else {
			return this.cheatsEnabled == null ? this.getGameMode() == WorldCreator.Mode.CREATIVE : this.cheatsEnabled;
		}
	}

	public void setSeed(String seed) {
		this.seed = seed;
		this.generatorOptionsHolder = this.generatorOptionsHolder.apply(options -> options.withSeed(GeneratorOptions.parseSeed(this.getSeed())));
		this.update();
	}

	public String getSeed() {
		return this.seed;
	}

	public void setGenerateStructures(boolean generateStructures) {
		this.generateStructures = generateStructures;
		this.update();
	}

	public boolean shouldGenerateStructures() {
		return this.isDebug() ? false : this.generateStructures;
	}

	public void setBonusChestEnabled(boolean bonusChestEnabled) {
		this.bonusChestEnabled = bonusChestEnabled;
		this.update();
	}

	public boolean isBonusChestEnabled() {
		return !this.isDebug() && !this.isHardcore() ? this.bonusChestEnabled : false;
	}

	public void setGeneratorOptionsHolder(GeneratorOptionsHolder generatorOptionsHolder) {
		this.generatorOptionsHolder = generatorOptionsHolder;
		this.updateWorldTypeLists();
		this.update();
	}

	public GeneratorOptionsHolder getGeneratorOptionsHolder() {
		return this.generatorOptionsHolder;
	}

	public void applyModifier(GeneratorOptionsHolder.RegistryAwareModifier modifier) {
		this.generatorOptionsHolder = this.generatorOptionsHolder.apply(modifier);
		this.update();
	}

	protected boolean updateDataConfiguration(DataConfiguration dataConfiguration) {
		DataConfiguration dataConfiguration2 = this.generatorOptionsHolder.dataConfiguration();
		if (dataConfiguration2.dataPacks().getEnabled().equals(dataConfiguration.dataPacks().getEnabled())
			&& dataConfiguration2.enabledFeatures().equals(dataConfiguration.enabledFeatures())) {
			this.generatorOptionsHolder = new GeneratorOptionsHolder(
				this.generatorOptionsHolder.generatorOptions(),
				this.generatorOptionsHolder.dimensionOptionsRegistry(),
				this.generatorOptionsHolder.selectedDimensions(),
				this.generatorOptionsHolder.combinedDynamicRegistries(),
				this.generatorOptionsHolder.dataPackContents(),
				dataConfiguration
			);
			return true;
		} else {
			return false;
		}
	}

	public boolean isDebug() {
		return this.generatorOptionsHolder.selectedDimensions().isDebug();
	}

	public void setWorldType(WorldCreator.WorldType worldType) {
		this.worldType = worldType;
		RegistryEntry<WorldPreset> registryEntry = worldType.preset();
		if (registryEntry != null) {
			this.applyModifier((registryManager, registryHolder) -> registryEntry.value().createDimensionsRegistryHolder());
		}
	}

	public WorldCreator.WorldType getWorldType() {
		return this.worldType;
	}

	@Nullable
	public LevelScreenProvider getLevelScreenProvider() {
		RegistryEntry<WorldPreset> registryEntry = this.getWorldType().preset();
		return registryEntry != null ? (LevelScreenProvider)LevelScreenProvider.WORLD_PRESET_TO_SCREEN_PROVIDER.get(registryEntry.getKey()) : null;
	}

	public List<WorldCreator.WorldType> getNormalWorldTypes() {
		return this.normalWorldTypes;
	}

	public List<WorldCreator.WorldType> getExtendedWorldTypes() {
		return this.extendedWorldTypes;
	}

	private void updateWorldTypeLists() {
		Registry<WorldPreset> registry = this.getGeneratorOptionsHolder().getCombinedRegistryManager().get(RegistryKeys.WORLD_PRESET);
		this.normalWorldTypes.clear();
		this.normalWorldTypes
			.addAll((Collection)getWorldPresetList(registry, WorldPresetTags.NORMAL).orElseGet(() -> registry.streamEntries().map(WorldCreator.WorldType::new).toList()));
		this.extendedWorldTypes.clear();
		this.extendedWorldTypes.addAll((Collection)getWorldPresetList(registry, WorldPresetTags.EXTENDED).orElse(this.normalWorldTypes));
		RegistryEntry<WorldPreset> registryEntry = this.worldType.preset();
		if (registryEntry != null) {
			this.worldType = (WorldCreator.WorldType)getWorldPreset(this.getGeneratorOptionsHolder(), registryEntry.getKey())
				.map(WorldCreator.WorldType::new)
				.orElse((WorldCreator.WorldType)this.normalWorldTypes.get(0));
		}
	}

	private static Optional<RegistryEntry<WorldPreset>> getWorldPreset(GeneratorOptionsHolder generatorOptionsHolder, Optional<RegistryKey<WorldPreset>> key) {
		return key.flatMap(key2 -> generatorOptionsHolder.getCombinedRegistryManager().get(RegistryKeys.WORLD_PRESET).getEntry(key2));
	}

	private static Optional<List<WorldCreator.WorldType>> getWorldPresetList(Registry<WorldPreset> registry, TagKey<WorldPreset> tag) {
		return registry.getEntryList(tag).map(entryList -> entryList.stream().map(WorldCreator.WorldType::new).toList()).filter(list -> !list.isEmpty());
	}

	public void setGameRules(GameRules gameRules) {
		this.gameRules = gameRules;
		this.update();
	}

	public GameRules getGameRules() {
		return this.gameRules;
	}

	@Environment(EnvType.CLIENT)
	public static enum Mode {
		SURVIVAL("survival", GameMode.SURVIVAL),
		HARDCORE("hardcore", GameMode.SURVIVAL),
		CREATIVE("creative", GameMode.CREATIVE),
		DEBUG("spectator", GameMode.SPECTATOR);

		public final GameMode defaultGameMode;
		public final Text name;
		private final Text info;

		private Mode(String name, GameMode defaultGameMode) {
			this.defaultGameMode = defaultGameMode;
			this.name = Text.translatable("selectWorld.gameMode." + name);
			this.info = Text.translatable("selectWorld.gameMode." + name + ".info");
		}

		public Text getInfo() {
			return this.info;
		}
	}

	@Environment(EnvType.CLIENT)
	public static record WorldType(@Nullable RegistryEntry<WorldPreset> preset) {
		private static final Text CUSTOM_GENERATOR_TEXT = Text.translatable("generator.custom");

		public Text getName() {
			return (Text)Optional.ofNullable(this.preset)
				.flatMap(RegistryEntry::getKey)
				.map(key -> Text.translatable(key.getValue().toTranslationKey("generator")))
				.orElse(CUSTOM_GENERATOR_TEXT);
		}

		public boolean isAmplified() {
			return Optional.ofNullable(this.preset).flatMap(RegistryEntry::getKey).filter(key -> key.equals(WorldPresets.AMPLIFIED)).isPresent();
		}
	}
}
