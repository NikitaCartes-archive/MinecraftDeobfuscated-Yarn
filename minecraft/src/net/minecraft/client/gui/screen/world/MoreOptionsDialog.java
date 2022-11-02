package net.minecraft.client.gui.screen.world;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.DataResult.PartialResult;
import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.GeneratorOptionsHolder;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.integrated.IntegratedServerLoader;
import net.minecraft.tag.TagKey;
import net.minecraft.tag.WorldPresetTags;
import net.minecraft.text.Text;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.dimension.DimensionOptionsRegistryHolder;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.WorldPreset;
import net.minecraft.world.gen.WorldPresets;
import net.minecraft.world.level.WorldGenSettings;
import org.lwjgl.util.tinyfd.TinyFileDialogs;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class MoreOptionsDialog implements Drawable {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Text CUSTOM_TEXT = Text.translatable("generator.custom");
	private static final Text AMPLIFIED_INFO_TEXT = Text.translatable("generator.minecraft.amplified.info");
	private static final Text MAP_FEATURES_INFO_TEXT = Text.translatable("selectWorld.mapFeatures.info");
	private static final Text SELECT_SETTINGS_FILE_TEXT = Text.translatable("selectWorld.import_worldgen_settings.select_file");
	private MultilineText amplifiedInfoText = MultilineText.EMPTY;
	private TextRenderer textRenderer;
	private int parentWidth;
	private TextFieldWidget seedTextField;
	private CyclingButtonWidget<Boolean> mapFeaturesButton;
	private CyclingButtonWidget<Boolean> bonusItemsButton;
	private CyclingButtonWidget<RegistryEntry<WorldPreset>> mapTypeButton;
	private ButtonWidget unchangeableMapTypeButton;
	private ButtonWidget customizeTypeButton;
	private ButtonWidget importSettingsButton;
	private GeneratorOptionsHolder generatorOptionsHolder;
	private Optional<RegistryEntry<WorldPreset>> presetEntry;
	private long seed;

	public MoreOptionsDialog(GeneratorOptionsHolder generatorOptionsHolder, Optional<RegistryKey<WorldPreset>> presetKey, long seed) {
		this.generatorOptionsHolder = generatorOptionsHolder;
		this.presetEntry = createPresetEntry(generatorOptionsHolder, presetKey);
		this.seed = seed;
	}

	public MoreOptionsDialog(GeneratorOptionsHolder generatorOptionsHolder, Optional<RegistryKey<WorldPreset>> presetKey) {
		this.generatorOptionsHolder = generatorOptionsHolder;
		this.presetEntry = createPresetEntry(generatorOptionsHolder, presetKey);
		this.seed = GeneratorOptions.getRandomSeed();
	}

	private static Optional<RegistryEntry<WorldPreset>> createPresetEntry(
		GeneratorOptionsHolder generatorOptionsHolder, Optional<RegistryKey<WorldPreset>> presetKey
	) {
		return presetKey.flatMap(key -> generatorOptionsHolder.getCombinedRegistryManager().get(Registry.WORLD_PRESET_KEY).getEntry(key));
	}

	public void init(CreateWorldScreen parent, MinecraftClient client, TextRenderer textRenderer) {
		this.textRenderer = textRenderer;
		this.parentWidth = parent.width;
		this.seedTextField = new TextFieldWidget(this.textRenderer, this.parentWidth / 2 - 100, 60, 200, 20, Text.translatable("selectWorld.enterSeed"));
		this.seedTextField.setText(Long.toString(this.seed));
		this.seedTextField.setChangedListener(seedText -> this.seed = GeneratorOptions.parseSeed(this.seedTextField.getText()));
		parent.addSelectableChild(this.seedTextField);
		int i = this.parentWidth / 2 - 155;
		int j = this.parentWidth / 2 + 5;
		this.mapFeaturesButton = parent.addDrawableChild(
			CyclingButtonWidget.onOffBuilder(this.generatorOptionsHolder.generatorOptions().shouldGenerateStructures())
				.narration(button -> ScreenTexts.joinSentences(button.getGenericNarrationMessage(), Text.translatable("selectWorld.mapFeatures.info")))
				.build(
					i,
					100,
					150,
					20,
					Text.translatable("selectWorld.mapFeatures"),
					(button, structures) -> this.apply(generatorOptions -> generatorOptions.withStructures(structures))
				)
		);
		this.mapFeaturesButton.visible = false;
		Registry<WorldPreset> registry = this.generatorOptionsHolder.getCombinedRegistryManager().get(Registry.WORLD_PRESET_KEY);
		List<RegistryEntry<WorldPreset>> list = (List<RegistryEntry<WorldPreset>>)collectPresets(registry, WorldPresetTags.NORMAL)
			.orElseGet(() -> (List)registry.streamEntries().collect(Collectors.toUnmodifiableList()));
		List<RegistryEntry<WorldPreset>> list2 = (List<RegistryEntry<WorldPreset>>)collectPresets(registry, WorldPresetTags.EXTENDED).orElse(list);
		this.mapTypeButton = parent.addDrawableChild(
			CyclingButtonWidget.<RegistryEntry<WorldPreset>>builder(MoreOptionsDialog::getText)
				.values(list, list2)
				.narration(
					button -> isAmplified((RegistryEntry<WorldPreset>)button.getValue())
							? ScreenTexts.joinSentences(button.getGenericNarrationMessage(), AMPLIFIED_INFO_TEXT)
							: button.getGenericNarrationMessage()
				)
				.build(j, 100, 150, 20, Text.translatable("selectWorld.mapType"), (button, presetEntry) -> {
					this.presetEntry = Optional.of(presetEntry);
					this.apply((dynamicRegistryManager, dimensionsRegistryHolder) -> ((WorldPreset)presetEntry.value()).createDimensionsRegistryHolder());
					parent.setMoreOptionsOpen();
				})
		);
		this.presetEntry.ifPresent(this.mapTypeButton::setValue);
		this.mapTypeButton.visible = false;
		this.unchangeableMapTypeButton = parent.addDrawableChild(
			ButtonWidget.createBuilder(ScreenTexts.composeGenericOptionText(Text.translatable("selectWorld.mapType"), CUSTOM_TEXT), button -> {
			}).setPositionAndSize(j, 100, 150, 20).build()
		);
		this.unchangeableMapTypeButton.active = false;
		this.unchangeableMapTypeButton.visible = false;
		this.customizeTypeButton = parent.addDrawableChild(
			ButtonWidget.createBuilder(
					Text.translatable("selectWorld.customizeType"),
					button -> {
						LevelScreenProvider levelScreenProvider = (LevelScreenProvider)LevelScreenProvider.WORLD_PRESET_TO_SCREEN_PROVIDER
							.get(this.presetEntry.flatMap(RegistryEntry::getKey));
						if (levelScreenProvider != null) {
							client.setScreen(levelScreenProvider.createEditScreen(parent, this.generatorOptionsHolder));
						}
					}
				)
				.setPositionAndSize(j, 120, 150, 20)
				.build()
		);
		this.customizeTypeButton.visible = false;
		this.bonusItemsButton = parent.addDrawableChild(
			CyclingButtonWidget.onOffBuilder(this.generatorOptionsHolder.generatorOptions().hasBonusChest() && !parent.hardcore)
				.build(
					i,
					151,
					150,
					20,
					Text.translatable("selectWorld.bonusItems"),
					(button, bonusChest) -> this.apply(generationOptions -> generationOptions.withBonusChest(bonusChest))
				)
		);
		this.bonusItemsButton.visible = false;
		this.importSettingsButton = parent.addDrawableChild(
			ButtonWidget.createBuilder(
					Text.translatable("selectWorld.import_worldgen_settings"),
					button -> {
						String string = TinyFileDialogs.tinyfd_openFileDialog(SELECT_SETTINGS_FILE_TEXT.getString(), null, null, null, false);
						if (string != null) {
							DynamicOps<JsonElement> dynamicOps = RegistryOps.of(JsonOps.INSTANCE, this.generatorOptionsHolder.getCombinedRegistryManager());

							DataResult<WorldGenSettings> dataResult;
							try {
								BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(string));

								try {
									JsonElement jsonElement = JsonParser.parseReader(bufferedReader);
									dataResult = WorldGenSettings.CODEC.parse(dynamicOps, jsonElement);
								} catch (Throwable var11) {
									if (bufferedReader != null) {
										try {
											bufferedReader.close();
										} catch (Throwable var10) {
											var11.addSuppressed(var10);
										}
									}

									throw var11;
								}

								if (bufferedReader != null) {
									bufferedReader.close();
								}
							} catch (Exception var12) {
								dataResult = DataResult.error("Failed to parse file: " + var12.getMessage());
							}

							if (dataResult.error().isPresent()) {
								Text text = Text.translatable("selectWorld.import_worldgen_settings.failure");
								String string2 = ((PartialResult)dataResult.error().get()).message();
								LOGGER.error("Error parsing world settings: {}", string2);
								Text text2 = Text.literal(string2);
								client.getToastManager().add(SystemToast.create(client, SystemToast.Type.WORLD_GEN_SETTINGS_TRANSFER, text, text2));
							} else {
								Lifecycle lifecycle = dataResult.lifecycle();
								dataResult.resultOrPartial(LOGGER::error)
									.ifPresent(
										worldGenSettings -> IntegratedServerLoader.tryLoad(
												client, parent, lifecycle, () -> this.importOptions(worldGenSettings.generatorOptions(), worldGenSettings.dimensionOptionsRegistryHolder())
											)
									);
							}
						}
					}
				)
				.setPositionAndSize(i, 185, 150, 20)
				.build()
		);
		this.importSettingsButton.visible = false;
		this.amplifiedInfoText = MultilineText.create(textRenderer, AMPLIFIED_INFO_TEXT, this.mapTypeButton.getWidth());
	}

	private static Optional<List<RegistryEntry<WorldPreset>>> collectPresets(Registry<WorldPreset> presetRegistry, TagKey<WorldPreset> tag) {
		return presetRegistry.getEntryList(tag).map(entryList -> entryList.stream().toList()).filter(entries -> !entries.isEmpty());
	}

	private static boolean isAmplified(RegistryEntry<WorldPreset> presetEntry) {
		return presetEntry.getKey().filter(key -> key.equals(WorldPresets.AMPLIFIED)).isPresent();
	}

	private static Text getText(RegistryEntry<WorldPreset> presetEntry) {
		return (Text)presetEntry.getKey().map(key -> Text.translatable(key.getValue().toTranslationKey("generator"))).orElse(CUSTOM_TEXT);
	}

	private void importOptions(GeneratorOptions generatorOptions, DimensionOptionsRegistryHolder dimensionsRegistryHolder) {
		this.generatorOptionsHolder = this.generatorOptionsHolder.with(generatorOptions, dimensionsRegistryHolder);
		this.presetEntry = createPresetEntry(this.generatorOptionsHolder, WorldPresets.getWorldPreset(dimensionsRegistryHolder.dimensions()));
		this.setMapTypeButtonVisible(true);
		this.seed = generatorOptions.getSeed();
		this.seedTextField.setText(Long.toString(this.seed));
	}

	public void tickSeedTextField() {
		this.seedTextField.tick();
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		if (this.mapFeaturesButton.visible) {
			this.textRenderer.drawWithShadow(matrices, MAP_FEATURES_INFO_TEXT, (float)(this.parentWidth / 2 - 150), 122.0F, -6250336);
		}

		this.seedTextField.render(matrices, mouseX, mouseY, delta);
		if (this.presetEntry.filter(MoreOptionsDialog::isAmplified).isPresent()) {
			this.amplifiedInfoText.drawWithShadow(matrices, this.mapTypeButton.getX() + 2, this.mapTypeButton.getY() + 22, 9, 10526880);
		}
	}

	void apply(GeneratorOptionsHolder.RegistryAwareModifier modifier) {
		this.generatorOptionsHolder = this.generatorOptionsHolder.apply(modifier);
	}

	private void apply(GeneratorOptionsHolder.Modifier modifier) {
		this.generatorOptionsHolder = this.generatorOptionsHolder.apply(modifier);
	}

	void setGeneratorOptionsHolder(GeneratorOptionsHolder generatorOptionsHolder) {
		this.generatorOptionsHolder = generatorOptionsHolder;
	}

	public GeneratorOptions getGeneratorOptionsHolder(boolean debug, boolean hardcore) {
		long l = GeneratorOptions.parseSeed(this.seedTextField.getText());
		GeneratorOptions generatorOptions = this.generatorOptionsHolder.generatorOptions();
		if (debug || hardcore) {
			generatorOptions = generatorOptions.withBonusChest(false);
		}

		if (debug) {
			generatorOptions = generatorOptions.withStructures(false);
		}

		return generatorOptions.withSeed(l);
	}

	public boolean isDebugWorld() {
		return this.generatorOptionsHolder.selectedDimensions().isDebug();
	}

	public void setVisible(boolean visible) {
		this.setMapTypeButtonVisible(visible);
		if (this.isDebugWorld()) {
			this.mapFeaturesButton.visible = false;
			this.bonusItemsButton.visible = false;
			this.customizeTypeButton.visible = false;
			this.importSettingsButton.visible = false;
		} else {
			this.mapFeaturesButton.visible = visible;
			this.bonusItemsButton.visible = visible;
			this.customizeTypeButton.visible = visible
				&& LevelScreenProvider.WORLD_PRESET_TO_SCREEN_PROVIDER.containsKey(this.presetEntry.flatMap(RegistryEntry::getKey));
			this.importSettingsButton.visible = visible;
		}

		this.seedTextField.setVisible(visible);
	}

	private void setMapTypeButtonVisible(boolean visible) {
		if (this.presetEntry.isPresent()) {
			this.mapTypeButton.visible = visible;
			this.unchangeableMapTypeButton.visible = false;
		} else {
			this.mapTypeButton.visible = false;
			this.unchangeableMapTypeButton.visible = visible;
		}
	}

	public GeneratorOptionsHolder getGeneratorOptionsHolder() {
		return this.generatorOptionsHolder;
	}

	public DynamicRegistryManager getRegistryManager() {
		return this.generatorOptionsHolder.getCombinedRegistryManager();
	}

	public void disableBonusItems() {
		this.bonusItemsButton.active = false;
		this.bonusItemsButton.setValue(false);
	}

	public void enableBonusItems() {
		this.bonusItemsButton.active = true;
		this.bonusItemsButton.setValue(this.generatorOptionsHolder.generatorOptions().hasBonusChest());
	}
}
