package net.minecraft.client.gui.screen;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.input.KeyCodes;
import net.minecraft.client.world.GeneratorOptionsHolder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.FlatLevelGeneratorPresetTags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.structure.StructureSet;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.FlatLevelGeneratorPreset;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorLayer;
import net.minecraft.world.gen.feature.PlacedFeature;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class PresetsScreen extends Screen {
	static final Identifier SLOT_TEXTURE = new Identifier("container/slot");
	static final Logger LOGGER = LogUtils.getLogger();
	private static final int ICON_SIZE = 18;
	private static final int BUTTON_HEIGHT = 20;
	private static final int ICON_BACKGROUND_OFFSET_X = 1;
	private static final int ICON_BACKGROUND_OFFSET_Y = 1;
	private static final int ICON_OFFSET_X = 2;
	private static final int ICON_OFFSET_Y = 2;
	private static final RegistryKey<Biome> BIOME_KEY = BiomeKeys.PLAINS;
	public static final Text UNKNOWN_PRESET_TEXT = Text.translatable("flat_world_preset.unknown");
	private final CustomizeFlatLevelScreen parent;
	private Text shareText;
	private Text listText;
	private PresetsScreen.SuperflatPresetsListWidget listWidget;
	private ButtonWidget selectPresetButton;
	TextFieldWidget customPresetField;
	FlatChunkGeneratorConfig config;

	public PresetsScreen(CustomizeFlatLevelScreen parent) {
		super(Text.translatable("createWorld.customize.presets.title"));
		this.parent = parent;
	}

	/**
	 * Parse a string like {@code "60*minecraft:stone"} to a {@link FlatChunkGeneratorLayer}.
	 */
	@Nullable
	private static FlatChunkGeneratorLayer parseLayerString(RegistryEntryLookup<Block> blockLookup, String layer, int layerStartHeight) {
		List<String> list = Splitter.on('*').limit(2).splitToList(layer);
		int i;
		String string;
		if (list.size() == 2) {
			string = (String)list.get(1);

			try {
				i = Math.max(Integer.parseInt((String)list.get(0)), 0);
			} catch (NumberFormatException var11) {
				LOGGER.error("Error while parsing flat world string", (Throwable)var11);
				return null;
			}
		} else {
			string = (String)list.get(0);
			i = 1;
		}

		int j = Math.min(layerStartHeight + i, DimensionType.MAX_HEIGHT);
		int k = j - layerStartHeight;

		Optional<RegistryEntry.Reference<Block>> optional;
		try {
			optional = blockLookup.getOptional(RegistryKey.of(RegistryKeys.BLOCK, new Identifier(string)));
		} catch (Exception var10) {
			LOGGER.error("Error while parsing flat world string", (Throwable)var10);
			return null;
		}

		if (optional.isEmpty()) {
			LOGGER.error("Error while parsing flat world string => Unknown block, {}", string);
			return null;
		} else {
			return new FlatChunkGeneratorLayer(k, (Block)((RegistryEntry.Reference)optional.get()).value());
		}
	}

	/**
	 * Parse a string like {@code "minecraft:bedrock,3*minecraft:dirt,minecraft:grass_block"}
	 * to a list of {@link FlatChunkGeneratorLayer}.
	 */
	private static List<FlatChunkGeneratorLayer> parsePresetLayersString(RegistryEntryLookup<Block> blockLookup, String layers) {
		List<FlatChunkGeneratorLayer> list = Lists.<FlatChunkGeneratorLayer>newArrayList();
		String[] strings = layers.split(",");
		int i = 0;

		for (String string : strings) {
			FlatChunkGeneratorLayer flatChunkGeneratorLayer = parseLayerString(blockLookup, string, i);
			if (flatChunkGeneratorLayer == null) {
				return Collections.emptyList();
			}

			list.add(flatChunkGeneratorLayer);
			i += flatChunkGeneratorLayer.getThickness();
		}

		return list;
	}

	public static FlatChunkGeneratorConfig parsePresetString(
		RegistryEntryLookup<Block> blockLookup,
		RegistryEntryLookup<Biome> biomeLookup,
		RegistryEntryLookup<StructureSet> structureSetLookup,
		RegistryEntryLookup<PlacedFeature> placedFeatureLookup,
		String preset,
		FlatChunkGeneratorConfig config
	) {
		Iterator<String> iterator = Splitter.on(';').split(preset).iterator();
		if (!iterator.hasNext()) {
			return FlatChunkGeneratorConfig.getDefaultConfig(biomeLookup, structureSetLookup, placedFeatureLookup);
		} else {
			List<FlatChunkGeneratorLayer> list = parsePresetLayersString(blockLookup, (String)iterator.next());
			if (list.isEmpty()) {
				return FlatChunkGeneratorConfig.getDefaultConfig(biomeLookup, structureSetLookup, placedFeatureLookup);
			} else {
				RegistryEntry.Reference<Biome> reference = biomeLookup.getOrThrow(BIOME_KEY);
				RegistryEntry<Biome> registryEntry = reference;
				if (iterator.hasNext()) {
					String string = (String)iterator.next();
					registryEntry = (RegistryEntry<Biome>)Optional.ofNullable(Identifier.tryParse(string))
						.map(biomeId -> RegistryKey.of(RegistryKeys.BIOME, biomeId))
						.flatMap(biomeLookup::getOptional)
						.orElseGet(() -> {
							LOGGER.warn("Invalid biome: {}", string);
							return reference;
						});
				}

				return config.with(list, config.getStructureOverrides(), registryEntry);
			}
		}
	}

	static String getGeneratorConfigString(FlatChunkGeneratorConfig config) {
		StringBuilder stringBuilder = new StringBuilder();

		for (int i = 0; i < config.getLayers().size(); i++) {
			if (i > 0) {
				stringBuilder.append(",");
			}

			stringBuilder.append(config.getLayers().get(i));
		}

		stringBuilder.append(";");
		stringBuilder.append(config.getBiome().getKey().map(RegistryKey::getValue).orElseThrow(() -> new IllegalStateException("Biome not registered")));
		return stringBuilder.toString();
	}

	@Override
	protected void init() {
		this.shareText = Text.translatable("createWorld.customize.presets.share");
		this.listText = Text.translatable("createWorld.customize.presets.list");
		this.customPresetField = new TextFieldWidget(this.textRenderer, 50, 40, this.width - 100, 20, this.shareText);
		this.customPresetField.setMaxLength(1230);
		GeneratorOptionsHolder generatorOptionsHolder = this.parent.parent.getWorldCreator().getGeneratorOptionsHolder();
		DynamicRegistryManager dynamicRegistryManager = generatorOptionsHolder.getCombinedRegistryManager();
		FeatureSet featureSet = generatorOptionsHolder.dataConfiguration().enabledFeatures();
		RegistryEntryLookup<Biome> registryEntryLookup = dynamicRegistryManager.getWrapperOrThrow(RegistryKeys.BIOME);
		RegistryEntryLookup<StructureSet> registryEntryLookup2 = dynamicRegistryManager.getWrapperOrThrow(RegistryKeys.STRUCTURE_SET);
		RegistryEntryLookup<PlacedFeature> registryEntryLookup3 = dynamicRegistryManager.getWrapperOrThrow(RegistryKeys.PLACED_FEATURE);
		RegistryEntryLookup<Block> registryEntryLookup4 = dynamicRegistryManager.getWrapperOrThrow(RegistryKeys.BLOCK).withFeatureFilter(featureSet);
		this.customPresetField.setText(getGeneratorConfigString(this.parent.getConfig()));
		this.config = this.parent.getConfig();
		this.addSelectableChild(this.customPresetField);
		this.listWidget = new PresetsScreen.SuperflatPresetsListWidget(dynamicRegistryManager, featureSet);
		this.addSelectableChild(this.listWidget);
		this.selectPresetButton = this.addDrawableChild(
			ButtonWidget.builder(
					Text.translatable("createWorld.customize.presets.select"),
					buttonWidget -> {
						FlatChunkGeneratorConfig flatChunkGeneratorConfig = parsePresetString(
							registryEntryLookup4, registryEntryLookup, registryEntryLookup2, registryEntryLookup3, this.customPresetField.getText(), this.config
						);
						this.parent.setConfig(flatChunkGeneratorConfig);
						this.client.setScreen(this.parent);
					}
				)
				.dimensions(this.width / 2 - 155, this.height - 28, 150, 20)
				.build()
		);
		this.addDrawableChild(
			ButtonWidget.builder(ScreenTexts.CANCEL, button -> this.client.setScreen(this.parent)).dimensions(this.width / 2 + 5, this.height - 28, 150, 20).build()
		);
		this.updateSelectButton(this.listWidget.getSelectedOrNull() != null);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
		return this.listWidget.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
	}

	@Override
	public void resize(MinecraftClient client, int width, int height) {
		String string = this.customPresetField.getText();
		this.init(client, width, height);
		this.customPresetField.setText(string);
	}

	@Override
	public void close() {
		this.client.setScreen(this.parent);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		this.listWidget.render(context, mouseX, mouseY, delta);
		context.getMatrices().push();
		context.getMatrices().translate(0.0F, 0.0F, 400.0F);
		context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 8, 16777215);
		context.drawTextWithShadow(this.textRenderer, this.shareText, 51, 30, 10526880);
		context.drawTextWithShadow(this.textRenderer, this.listText, 51, 70, 10526880);
		context.getMatrices().pop();
		this.customPresetField.render(context, mouseX, mouseY, delta);
	}

	public void updateSelectButton(boolean hasSelected) {
		this.selectPresetButton.active = hasSelected || this.customPresetField.getText().length() > 1;
	}

	@Environment(EnvType.CLIENT)
	class SuperflatPresetsListWidget extends AlwaysSelectedEntryListWidget<PresetsScreen.SuperflatPresetsListWidget.SuperflatPresetEntry> {
		public SuperflatPresetsListWidget(DynamicRegistryManager dynamicRegistryManager, FeatureSet featureSet) {
			super(PresetsScreen.this.client, PresetsScreen.this.width, PresetsScreen.this.height, 80, PresetsScreen.this.height - 37, 24);

			for (RegistryEntry<FlatLevelGeneratorPreset> registryEntry : dynamicRegistryManager.get(RegistryKeys.FLAT_LEVEL_GENERATOR_PRESET)
				.iterateEntries(FlatLevelGeneratorPresetTags.VISIBLE)) {
				Set<Block> set = (Set<Block>)registryEntry.value()
					.settings()
					.getLayers()
					.stream()
					.map(layer -> layer.getBlockState().getBlock())
					.filter(block -> !block.isEnabled(featureSet))
					.collect(Collectors.toSet());
				if (!set.isEmpty()) {
					PresetsScreen.LOGGER
						.info(
							"Discarding flat world preset {} since it contains experimental blocks {}",
							registryEntry.getKey().map(key -> key.getValue().toString()).orElse("<unknown>"),
							set
						);
				} else {
					this.addEntry(new PresetsScreen.SuperflatPresetsListWidget.SuperflatPresetEntry(registryEntry));
				}
			}
		}

		public void setSelected(@Nullable PresetsScreen.SuperflatPresetsListWidget.SuperflatPresetEntry superflatPresetEntry) {
			super.setSelected(superflatPresetEntry);
			PresetsScreen.this.updateSelectButton(superflatPresetEntry != null);
		}

		@Override
		public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
			if (super.keyPressed(keyCode, scanCode, modifiers)) {
				return true;
			} else {
				if (KeyCodes.isToggle(keyCode) && this.getSelectedOrNull() != null) {
					this.getSelectedOrNull().setPreset();
				}

				return false;
			}
		}

		@Environment(EnvType.CLIENT)
		public class SuperflatPresetEntry extends AlwaysSelectedEntryListWidget.Entry<PresetsScreen.SuperflatPresetsListWidget.SuperflatPresetEntry> {
			private static final Identifier STATS_ICONS_TEXTURE = new Identifier("textures/gui/container/stats_icons.png");
			private final FlatLevelGeneratorPreset preset;
			private final Text text;

			public SuperflatPresetEntry(RegistryEntry<FlatLevelGeneratorPreset> preset) {
				this.preset = preset.value();
				this.text = (Text)preset.getKey()
					.map(key -> Text.translatable(key.getValue().toTranslationKey("flat_world_preset")))
					.orElse(PresetsScreen.UNKNOWN_PRESET_TEXT);
			}

			@Override
			public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
				this.renderIcon(context, x, y, this.preset.displayItem().value());
				context.drawText(PresetsScreen.this.textRenderer, this.text, x + 18 + 5, y + 6, 16777215, false);
			}

			@Override
			public boolean mouseClicked(double mouseX, double mouseY, int button) {
				if (button == 0) {
					this.setPreset();
				}

				return false;
			}

			void setPreset() {
				SuperflatPresetsListWidget.this.setSelected(this);
				PresetsScreen.this.config = this.preset.settings();
				PresetsScreen.this.customPresetField.setText(PresetsScreen.getGeneratorConfigString(PresetsScreen.this.config));
				PresetsScreen.this.customPresetField.setCursorToStart(false);
			}

			private void renderIcon(DrawContext context, int x, int y, Item iconItem) {
				this.drawIconBackground(context, x + 1, y + 1);
				context.drawItemWithoutEntity(new ItemStack(iconItem), x + 2, y + 2);
			}

			private void drawIconBackground(DrawContext context, int x, int y) {
				context.drawGuiTexture(PresetsScreen.SLOT_TEXTURE, x, y, 0, 18, 18);
			}

			@Override
			public Text getNarration() {
				return Text.translatable("narrator.select", this.text);
			}
		}
	}
}
