/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.world.LevelScreenProvider;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.world.GeneratorOptionsHolder;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.registry.tag.WorldPresetTags;
import net.minecraft.resource.DataConfiguration;
import net.minecraft.text.Text;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.WorldPreset;
import net.minecraft.world.gen.WorldPresets;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class WorldCreator {
    private final List<Consumer<WorldCreator>> listeners = new ArrayList<Consumer<WorldCreator>>();
    private String worldName = I18n.translate("selectWorld.newWorld", new Object[0]);
    private boolean named = true;
    private Mode gameMode = Mode.SURVIVAL;
    private Difficulty difficulty = Difficulty.NORMAL;
    @Nullable
    private Boolean cheatsEnabled;
    private String seed;
    private boolean generateStructures;
    private boolean bonusChestEnabled;
    private GeneratorOptionsHolder generatorOptionsHolder;
    private WorldType worldType;
    private final List<WorldType> normalWorldTypes = new ArrayList<WorldType>();
    private final List<WorldType> extendedWorldTypes = new ArrayList<WorldType>();
    private GameRules gameRules = new GameRules();

    public WorldCreator(GeneratorOptionsHolder generatorOptionsHolder, Optional<RegistryKey<WorldPreset>> worldType, OptionalLong seed) {
        this.generatorOptionsHolder = generatorOptionsHolder;
        this.worldType = new WorldType(WorldCreator.getWorldPreset(generatorOptionsHolder, worldType).orElse(null));
        this.updateWorldTypeLists();
        this.seed = seed.isPresent() ? Long.toString(seed.getAsLong()) : "";
        this.generateStructures = generatorOptionsHolder.generatorOptions().shouldGenerateStructures();
        this.bonusChestEnabled = generatorOptionsHolder.generatorOptions().hasBonusChest();
    }

    public void addListener(Consumer<WorldCreator> listener) {
        this.listeners.add(listener);
    }

    public void update() {
        boolean bl2;
        boolean bl = this.isBonusChestEnabled();
        if (bl != this.generatorOptionsHolder.generatorOptions().hasBonusChest()) {
            this.generatorOptionsHolder = this.generatorOptionsHolder.apply(options -> options.withBonusChest(bl));
        }
        if ((bl2 = this.shouldGenerateStructures()) != this.generatorOptionsHolder.generatorOptions().shouldGenerateStructures()) {
            this.generatorOptionsHolder = this.generatorOptionsHolder.apply(options -> options.withStructures(bl2));
        }
        for (Consumer<WorldCreator> consumer : this.listeners) {
            consumer.accept(this);
        }
        this.named = false;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
        this.named = true;
        this.update();
    }

    public String getWorldName() {
        return this.worldName;
    }

    public boolean isNamed() {
        return this.named;
    }

    public void setGameMode(Mode gameMode) {
        this.gameMode = gameMode;
        this.update();
    }

    public Mode getGameMode() {
        if (this.isDebug()) {
            return Mode.DEBUG;
        }
        return this.gameMode;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
        this.update();
    }

    public Difficulty getDifficulty() {
        if (this.isHardcore()) {
            return Difficulty.HARD;
        }
        return this.difficulty;
    }

    public boolean isHardcore() {
        return this.getGameMode() == Mode.HARDCORE;
    }

    public void setCheatsEnabled(boolean cheatsEnabled) {
        this.cheatsEnabled = cheatsEnabled;
        this.update();
    }

    public boolean areCheatsEnabled() {
        if (this.isDebug()) {
            return true;
        }
        if (this.isHardcore()) {
            return false;
        }
        if (this.cheatsEnabled == null) {
            return this.getGameMode() == Mode.CREATIVE;
        }
        return this.cheatsEnabled;
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
        if (this.isDebug()) {
            return false;
        }
        return this.generateStructures;
    }

    public void setBonusChestEnabled(boolean bonusChestEnabled) {
        this.bonusChestEnabled = bonusChestEnabled;
        this.update();
    }

    public boolean isBonusChestEnabled() {
        if (this.isDebug() || this.isHardcore()) {
            return false;
        }
        return this.bonusChestEnabled;
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
        if (dataConfiguration2.dataPacks().getEnabled().equals(dataConfiguration.dataPacks().getEnabled()) && dataConfiguration2.enabledFeatures().equals(dataConfiguration.enabledFeatures())) {
            this.generatorOptionsHolder = new GeneratorOptionsHolder(this.generatorOptionsHolder.generatorOptions(), this.generatorOptionsHolder.dimensionOptionsRegistry(), this.generatorOptionsHolder.selectedDimensions(), this.generatorOptionsHolder.combinedDynamicRegistries(), this.generatorOptionsHolder.dataPackContents(), dataConfiguration);
            return true;
        }
        return false;
    }

    public boolean isDebug() {
        return this.generatorOptionsHolder.selectedDimensions().isDebug();
    }

    public void setWorldType(WorldType worldType) {
        this.worldType = worldType;
        RegistryEntry<WorldPreset> registryEntry = worldType.preset();
        if (registryEntry != null) {
            this.applyModifier((registryManager, registryHolder) -> ((WorldPreset)registryEntry.value()).createDimensionsRegistryHolder());
        }
    }

    public WorldType getWorldType() {
        return this.worldType;
    }

    @Nullable
    public LevelScreenProvider getLevelScreenProvider() {
        RegistryEntry<WorldPreset> registryEntry = this.getWorldType().preset();
        return registryEntry != null ? LevelScreenProvider.WORLD_PRESET_TO_SCREEN_PROVIDER.get(registryEntry.getKey()) : null;
    }

    public List<WorldType> getNormalWorldTypes() {
        return this.normalWorldTypes;
    }

    public List<WorldType> getExtendedWorldTypes() {
        return this.extendedWorldTypes;
    }

    private void updateWorldTypeLists() {
        Registry<WorldPreset> registry = this.getGeneratorOptionsHolder().getCombinedRegistryManager().get(RegistryKeys.WORLD_PRESET);
        this.normalWorldTypes.clear();
        this.normalWorldTypes.addAll(WorldCreator.getWorldPresetList(registry, WorldPresetTags.NORMAL).orElseGet(() -> registry.streamEntries().map(WorldType::new).toList()));
        this.extendedWorldTypes.clear();
        this.extendedWorldTypes.addAll((Collection<WorldType>)WorldCreator.getWorldPresetList(registry, WorldPresetTags.EXTENDED).orElse(this.normalWorldTypes));
        RegistryEntry<WorldPreset> registryEntry = this.worldType.preset();
        if (registryEntry != null) {
            this.worldType = WorldCreator.getWorldPreset(this.getGeneratorOptionsHolder(), registryEntry.getKey()).map(WorldType::new).orElse(this.normalWorldTypes.get(0));
        }
    }

    private static Optional<RegistryEntry<WorldPreset>> getWorldPreset(GeneratorOptionsHolder generatorOptionsHolder, Optional<RegistryKey<WorldPreset>> key) {
        return key.flatMap(registryKey -> generatorOptionsHolder.getCombinedRegistryManager().get(RegistryKeys.WORLD_PRESET).getEntry((RegistryKey<WorldPreset>)registryKey));
    }

    private static Optional<List<WorldType>> getWorldPresetList(Registry<WorldPreset> registry, TagKey<WorldPreset> tag) {
        return registry.getEntryList(tag).map(entryList -> entryList.stream().map(WorldType::new).toList()).filter(list -> !list.isEmpty());
    }

    public void setGameRules(GameRules gameRules) {
        this.gameRules = gameRules;
        this.update();
    }

    public GameRules getGameRules() {
        return this.gameRules;
    }

    @Environment(value=EnvType.CLIENT)
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

    @Environment(value=EnvType.CLIENT)
    public record WorldType(@Nullable RegistryEntry<WorldPreset> preset) {
        private static final Text CUSTOM_GENERATOR_TEXT = Text.translatable("generator.custom");

        public Text getName() {
            return Optional.ofNullable(this.preset).flatMap(RegistryEntry::getKey).map(key -> Text.translatable(key.getValue().toTranslationKey("generator"))).orElse(CUSTOM_GENERATOR_TEXT);
        }

        public boolean isAmplified() {
            return Optional.ofNullable(this.preset).flatMap(RegistryEntry::getKey).filter(key -> key.equals(WorldPresets.AMPLIFIED)).isPresent();
        }

        @Nullable
        public RegistryEntry<WorldPreset> preset() {
            return this.preset;
        }
    }
}

