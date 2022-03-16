package net.minecraft.client.gui.screen;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.structure.StructureSet;
import net.minecraft.tag.FlatLevelGeneratorPresetTags;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.FlatLevelGeneratorPreset;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorLayer;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class PresetsScreen extends Screen {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final int ICON_TEXTURE_SIZE = 128;
	private static final int ICON_SIZE = 18;
	private static final int BUTTON_HEIGHT = 20;
	private static final int ICON_BACKGROUND_OFFSET_X = 1;
	private static final int ICON_BACKGROUND_OFFSET_Y = 1;
	private static final int ICON_OFFSET_X = 2;
	private static final int ICON_OFFSET_Y = 2;
	private static final RegistryKey<Biome> BIOME_KEY = BiomeKeys.PLAINS;
	public static final Text UNKNOWN_PRESET_TEXT = new TranslatableText("flat_world_preset.unknown");
	private final CustomizeFlatLevelScreen parent;
	private Text shareText;
	private Text listText;
	private PresetsScreen.SuperflatPresetsListWidget listWidget;
	private ButtonWidget selectPresetButton;
	TextFieldWidget customPresetField;
	FlatChunkGeneratorConfig config;

	public PresetsScreen(CustomizeFlatLevelScreen parent) {
		super(new TranslatableText("createWorld.customize.presets.title"));
		this.parent = parent;
	}

	/**
	 * Parse a string like {@code "60*minecraft:stone"} to a {@link FlatChunkGeneratorLayer}.
	 */
	@Nullable
	private static FlatChunkGeneratorLayer parseLayerString(String layer, int layerStartHeight) {
		String[] strings = layer.split("\\*", 2);
		int i;
		if (strings.length == 2) {
			try {
				i = Math.max(Integer.parseInt(strings[0]), 0);
			} catch (NumberFormatException var10) {
				LOGGER.error("Error while parsing flat world string => {}", var10.getMessage());
				return null;
			}
		} else {
			i = 1;
		}

		int j = Math.min(layerStartHeight + i, DimensionType.MAX_HEIGHT);
		int k = j - layerStartHeight;
		String string = strings[strings.length - 1];

		Block block;
		try {
			block = (Block)Registry.BLOCK.getOrEmpty(new Identifier(string)).orElse(null);
		} catch (Exception var9) {
			LOGGER.error("Error while parsing flat world string => {}", var9.getMessage());
			return null;
		}

		if (block == null) {
			LOGGER.error("Error while parsing flat world string => Unknown block, {}", string);
			return null;
		} else {
			return new FlatChunkGeneratorLayer(k, block);
		}
	}

	/**
	 * Parse a string like {@code "minecraft:bedrock,3*minecraft:dirt,minecraft:grass_block"}
	 * to a list of {@link FlatChunkGeneratorLayer}.
	 */
	private static List<FlatChunkGeneratorLayer> parsePresetLayersString(String layers) {
		List<FlatChunkGeneratorLayer> list = Lists.<FlatChunkGeneratorLayer>newArrayList();
		String[] strings = layers.split(",");
		int i = 0;

		for (String string : strings) {
			FlatChunkGeneratorLayer flatChunkGeneratorLayer = parseLayerString(string, i);
			if (flatChunkGeneratorLayer == null) {
				return Collections.emptyList();
			}

			list.add(flatChunkGeneratorLayer);
			i += flatChunkGeneratorLayer.getThickness();
		}

		return list;
	}

	public static FlatChunkGeneratorConfig parsePresetString(
		Registry<Biome> biomeRegistry, Registry<StructureSet> structureSetRegistry, String preset, FlatChunkGeneratorConfig generatorConfig
	) {
		Iterator<String> iterator = Splitter.on(';').split(preset).iterator();
		if (!iterator.hasNext()) {
			return FlatChunkGeneratorConfig.getDefaultConfig(biomeRegistry, structureSetRegistry);
		} else {
			List<FlatChunkGeneratorLayer> list = parsePresetLayersString((String)iterator.next());
			if (list.isEmpty()) {
				return FlatChunkGeneratorConfig.getDefaultConfig(biomeRegistry, structureSetRegistry);
			} else {
				FlatChunkGeneratorConfig flatChunkGeneratorConfig = generatorConfig.withLayers(list, generatorConfig.method_41139());
				RegistryKey<Biome> registryKey = BIOME_KEY;
				if (iterator.hasNext()) {
					try {
						Identifier identifier = new Identifier((String)iterator.next());
						registryKey = RegistryKey.of(Registry.BIOME_KEY, identifier);
						biomeRegistry.getOrEmpty(registryKey).orElseThrow(() -> new IllegalArgumentException("Invalid Biome: " + identifier));
					} catch (Exception var9) {
						LOGGER.error("Error while parsing flat world string => {}", var9.getMessage());
						registryKey = BIOME_KEY;
					}
				}

				flatChunkGeneratorConfig.setBiome(biomeRegistry.getOrCreateEntry(registryKey));
				return flatChunkGeneratorConfig;
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
		this.client.keyboard.setRepeatEvents(true);
		this.shareText = new TranslatableText("createWorld.customize.presets.share");
		this.listText = new TranslatableText("createWorld.customize.presets.list");
		this.customPresetField = new TextFieldWidget(this.textRenderer, 50, 40, this.width - 100, 20, this.shareText);
		this.customPresetField.setMaxLength(1230);
		DynamicRegistryManager dynamicRegistryManager = this.parent.parent.moreOptionsDialog.getRegistryManager();
		Registry<Biome> registry = dynamicRegistryManager.get(Registry.BIOME_KEY);
		Registry<StructureSet> registry2 = dynamicRegistryManager.get(Registry.STRUCTURE_SET_KEY);
		this.customPresetField.setText(getGeneratorConfigString(this.parent.getConfig()));
		this.config = this.parent.getConfig();
		this.addSelectableChild(this.customPresetField);
		this.listWidget = new PresetsScreen.SuperflatPresetsListWidget(this.parent.parent.moreOptionsDialog.getRegistryManager());
		this.addSelectableChild(this.listWidget);
		this.selectPresetButton = this.addDrawableChild(
			new ButtonWidget(this.width / 2 - 155, this.height - 28, 150, 20, new TranslatableText("createWorld.customize.presets.select"), buttonWidget -> {
				FlatChunkGeneratorConfig flatChunkGeneratorConfig = parsePresetString(registry, registry2, this.customPresetField.getText(), this.config);
				this.parent.setConfig(flatChunkGeneratorConfig);
				this.client.setScreen(this.parent);
			})
		);
		this.addDrawableChild(new ButtonWidget(this.width / 2 + 5, this.height - 28, 150, 20, ScreenTexts.CANCEL, button -> this.client.setScreen(this.parent)));
		this.updateSelectButton(this.listWidget.getSelectedOrNull() != null);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		return this.listWidget.mouseScrolled(mouseX, mouseY, amount);
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
	public void removed() {
		this.client.keyboard.setRepeatEvents(false);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		this.listWidget.render(matrices, mouseX, mouseY, delta);
		matrices.push();
		matrices.translate(0.0, 0.0, 400.0);
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 16777215);
		drawTextWithShadow(matrices, this.textRenderer, this.shareText, 50, 30, 10526880);
		drawTextWithShadow(matrices, this.textRenderer, this.listText, 50, 70, 10526880);
		matrices.pop();
		this.customPresetField.render(matrices, mouseX, mouseY, delta);
		super.render(matrices, mouseX, mouseY, delta);
	}

	@Override
	public void tick() {
		this.customPresetField.tick();
		super.tick();
	}

	public void updateSelectButton(boolean hasSelected) {
		this.selectPresetButton.active = hasSelected || this.customPresetField.getText().length() > 1;
	}

	@Environment(EnvType.CLIENT)
	class SuperflatPresetsListWidget extends AlwaysSelectedEntryListWidget<PresetsScreen.SuperflatPresetsListWidget.SuperflatPresetEntry> {
		public SuperflatPresetsListWidget(DynamicRegistryManager dynamicRegistryManager) {
			super(PresetsScreen.this.client, PresetsScreen.this.width, PresetsScreen.this.height, 80, PresetsScreen.this.height - 37, 24);

			for (RegistryEntry<FlatLevelGeneratorPreset> registryEntry : dynamicRegistryManager.get(Registry.FLAT_LEVEL_GENERATOR_PRESET_WORLDGEN)
				.iterateEntries(FlatLevelGeneratorPresetTags.VISIBLE)) {
				this.addEntry(new PresetsScreen.SuperflatPresetsListWidget.SuperflatPresetEntry(registryEntry));
			}
		}

		public void setSelected(@Nullable PresetsScreen.SuperflatPresetsListWidget.SuperflatPresetEntry superflatPresetEntry) {
			super.setSelected(superflatPresetEntry);
			PresetsScreen.this.updateSelectButton(superflatPresetEntry != null);
		}

		@Override
		protected boolean isFocused() {
			return PresetsScreen.this.getFocused() == this;
		}

		@Override
		public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
			if (super.keyPressed(keyCode, scanCode, modifiers)) {
				return true;
			} else {
				if ((keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) && this.getSelectedOrNull() != null) {
					this.getSelectedOrNull().setPreset();
				}

				return false;
			}
		}

		@Environment(EnvType.CLIENT)
		public class SuperflatPresetEntry extends AlwaysSelectedEntryListWidget.Entry<PresetsScreen.SuperflatPresetsListWidget.SuperflatPresetEntry> {
			private final FlatLevelGeneratorPreset preset;
			private final Text text;

			public SuperflatPresetEntry(RegistryEntry<FlatLevelGeneratorPreset> preset) {
				this.preset = preset.value();
				this.text = (Text)preset.getKey()
					.map(key -> new TranslatableText(key.getValue().toTranslationKey("flat_world_preset")))
					.orElse(PresetsScreen.UNKNOWN_PRESET_TEXT);
			}

			@Override
			public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
				this.renderIcon(matrices, x, y, this.preset.displayItem().value());
				PresetsScreen.this.textRenderer.draw(matrices, this.text, (float)(x + 18 + 5), (float)(y + 6), 16777215);
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
				PresetsScreen.this.customPresetField.setCursorToStart();
			}

			private void renderIcon(MatrixStack matrices, int x, int y, Item iconItem) {
				this.drawIconBackground(matrices, x + 1, y + 1);
				PresetsScreen.this.itemRenderer.renderGuiItemIcon(new ItemStack(iconItem), x + 2, y + 2);
			}

			private void drawIconBackground(MatrixStack matrices, int x, int y) {
				RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
				RenderSystem.setShaderTexture(0, DrawableHelper.STATS_ICON_TEXTURE);
				DrawableHelper.drawTexture(matrices, x, y, PresetsScreen.this.getZOffset(), 0.0F, 0.0F, 18, 18, 128, 128);
			}

			@Override
			public Text getNarration() {
				return new TranslatableText("narrator.select", this.text);
			}
		}
	}
}
