package net.minecraft.client.gui.screen;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorLayer;
import net.minecraft.world.gen.chunk.StructureConfig;
import net.minecraft.world.gen.chunk.StructuresConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class PresetsScreen extends Screen {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final int field_32263 = 128;
	private static final int field_32264 = 18;
	private static final int field_32265 = 20;
	private static final int field_32266 = 1;
	private static final int field_32267 = 1;
	private static final int field_32268 = 2;
	private static final int field_32269 = 2;
	static final List<PresetsScreen.SuperflatPreset> PRESETS = Lists.<PresetsScreen.SuperflatPreset>newArrayList();
	private static final RegistryKey<Biome> BIOME_KEY = BiomeKeys.PLAINS;
	final CustomizeFlatLevelScreen parent;
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

	public static FlatChunkGeneratorConfig parsePresetString(Registry<Biome> biomeRegistry, String preset, FlatChunkGeneratorConfig generatorConfig) {
		Iterator<String> iterator = Splitter.on(';').split(preset).iterator();
		if (!iterator.hasNext()) {
			return FlatChunkGeneratorConfig.getDefaultConfig(biomeRegistry);
		} else {
			List<FlatChunkGeneratorLayer> list = parsePresetLayersString((String)iterator.next());
			if (list.isEmpty()) {
				return FlatChunkGeneratorConfig.getDefaultConfig(biomeRegistry);
			} else {
				FlatChunkGeneratorConfig flatChunkGeneratorConfig = generatorConfig.withLayers(list, generatorConfig.getStructuresConfig());
				RegistryKey<Biome> registryKey = BIOME_KEY;
				if (iterator.hasNext()) {
					try {
						Identifier identifier = new Identifier((String)iterator.next());
						registryKey = RegistryKey.of(Registry.BIOME_KEY, identifier);
						biomeRegistry.getOrEmpty(registryKey).orElseThrow(() -> new IllegalArgumentException("Invalid Biome: " + identifier));
					} catch (Exception var8) {
						LOGGER.error("Error while parsing flat world string => {}", var8.getMessage());
						registryKey = BIOME_KEY;
					}
				}

				RegistryKey<Biome> registryKey2 = registryKey;
				flatChunkGeneratorConfig.setBiome(() -> biomeRegistry.getOrThrow(registryKey2));
				return flatChunkGeneratorConfig;
			}
		}
	}

	static String getGeneratorConfigString(Registry<Biome> biomeRegistry, FlatChunkGeneratorConfig generatorConfig) {
		StringBuilder stringBuilder = new StringBuilder();

		for (int i = 0; i < generatorConfig.getLayers().size(); i++) {
			if (i > 0) {
				stringBuilder.append(",");
			}

			stringBuilder.append(generatorConfig.getLayers().get(i));
		}

		stringBuilder.append(";");
		stringBuilder.append(biomeRegistry.getId(generatorConfig.getBiome()));
		return stringBuilder.toString();
	}

	@Override
	protected void init() {
		this.client.keyboard.setRepeatEvents(true);
		this.shareText = new TranslatableText("createWorld.customize.presets.share");
		this.listText = new TranslatableText("createWorld.customize.presets.list");
		this.customPresetField = new TextFieldWidget(this.textRenderer, 50, 40, this.width - 100, 20, this.shareText);
		this.customPresetField.setMaxLength(1230);
		Registry<Biome> registry = this.parent.parent.moreOptionsDialog.getRegistryManager().get(Registry.BIOME_KEY);
		this.customPresetField.setText(getGeneratorConfigString(registry, this.parent.getConfig()));
		this.config = this.parent.getConfig();
		this.addSelectableChild(this.customPresetField);
		this.listWidget = new PresetsScreen.SuperflatPresetsListWidget();
		this.addSelectableChild(this.listWidget);
		this.selectPresetButton = this.addDrawableChild(
			new ButtonWidget(this.width / 2 - 155, this.height - 28, 150, 20, new TranslatableText("createWorld.customize.presets.select"), button -> {
				FlatChunkGeneratorConfig flatChunkGeneratorConfig = parsePresetString(registry, this.customPresetField.getText(), this.config);
				this.parent.setConfig(flatChunkGeneratorConfig);
				this.client.openScreen(this.parent);
			})
		);
		this.addDrawableChild(new ButtonWidget(this.width / 2 + 5, this.height - 28, 150, 20, ScreenTexts.CANCEL, button -> this.client.openScreen(this.parent)));
		this.updateSelectButton(this.listWidget.getSelected() != null);
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
	public void onClose() {
		this.client.openScreen(this.parent);
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

	private static void addPreset(
		Text presetName,
		ItemConvertible icon,
		RegistryKey<Biome> presetBiome,
		List<StructureFeature<?>> structures,
		boolean generateStronghold,
		boolean generateFeatures,
		boolean generateLakes,
		FlatChunkGeneratorLayer... layers
	) {
		PRESETS.add(new PresetsScreen.SuperflatPreset(icon.asItem(), presetName, registry -> {
			Map<StructureFeature<?>, StructureConfig> map = Maps.<StructureFeature<?>, StructureConfig>newHashMap();

			for (StructureFeature<?> structureFeature : structures) {
				map.put(structureFeature, StructuresConfig.DEFAULT_STRUCTURES.get(structureFeature));
			}

			StructuresConfig structuresConfig = new StructuresConfig(generateStronghold ? Optional.of(StructuresConfig.DEFAULT_STRONGHOLD) : Optional.empty(), map);
			FlatChunkGeneratorConfig flatChunkGeneratorConfig = new FlatChunkGeneratorConfig(structuresConfig, registry);
			if (generateFeatures) {
				flatChunkGeneratorConfig.enableFeatures();
			}

			if (generateLakes) {
				flatChunkGeneratorConfig.enableLakes();
			}

			for (int i = layers.length - 1; i >= 0; i--) {
				flatChunkGeneratorConfig.getLayers().add(layers[i]);
			}

			flatChunkGeneratorConfig.setBiome(() -> registry.getOrThrow(presetBiome));
			flatChunkGeneratorConfig.updateLayerBlocks();
			return flatChunkGeneratorConfig.withStructuresConfig(structuresConfig);
		}));
	}

	static {
		addPreset(
			new TranslatableText("createWorld.customize.preset.classic_flat"),
			Blocks.GRASS_BLOCK,
			BiomeKeys.PLAINS,
			Arrays.asList(StructureFeature.VILLAGE),
			false,
			false,
			false,
			new FlatChunkGeneratorLayer(1, Blocks.GRASS_BLOCK),
			new FlatChunkGeneratorLayer(2, Blocks.DIRT),
			new FlatChunkGeneratorLayer(1, Blocks.BEDROCK)
		);
		addPreset(
			new TranslatableText("createWorld.customize.preset.tunnelers_dream"),
			Blocks.STONE,
			BiomeKeys.MOUNTAINS,
			Arrays.asList(StructureFeature.MINESHAFT),
			true,
			true,
			false,
			new FlatChunkGeneratorLayer(1, Blocks.GRASS_BLOCK),
			new FlatChunkGeneratorLayer(5, Blocks.DIRT),
			new FlatChunkGeneratorLayer(230, Blocks.STONE),
			new FlatChunkGeneratorLayer(1, Blocks.BEDROCK)
		);
		addPreset(
			new TranslatableText("createWorld.customize.preset.water_world"),
			Items.WATER_BUCKET,
			BiomeKeys.DEEP_OCEAN,
			Arrays.asList(StructureFeature.OCEAN_RUIN, StructureFeature.SHIPWRECK, StructureFeature.MONUMENT),
			false,
			false,
			false,
			new FlatChunkGeneratorLayer(90, Blocks.WATER),
			new FlatChunkGeneratorLayer(5, Blocks.SAND),
			new FlatChunkGeneratorLayer(5, Blocks.DIRT),
			new FlatChunkGeneratorLayer(5, Blocks.STONE),
			new FlatChunkGeneratorLayer(1, Blocks.BEDROCK)
		);
		addPreset(
			new TranslatableText("createWorld.customize.preset.overworld"),
			Blocks.GRASS,
			BiomeKeys.PLAINS,
			Arrays.asList(StructureFeature.VILLAGE, StructureFeature.MINESHAFT, StructureFeature.PILLAGER_OUTPOST, StructureFeature.RUINED_PORTAL),
			true,
			true,
			true,
			new FlatChunkGeneratorLayer(1, Blocks.GRASS_BLOCK),
			new FlatChunkGeneratorLayer(3, Blocks.DIRT),
			new FlatChunkGeneratorLayer(59, Blocks.STONE),
			new FlatChunkGeneratorLayer(1, Blocks.BEDROCK)
		);
		addPreset(
			new TranslatableText("createWorld.customize.preset.snowy_kingdom"),
			Blocks.SNOW,
			BiomeKeys.SNOWY_TUNDRA,
			Arrays.asList(StructureFeature.VILLAGE, StructureFeature.IGLOO),
			false,
			false,
			false,
			new FlatChunkGeneratorLayer(1, Blocks.SNOW),
			new FlatChunkGeneratorLayer(1, Blocks.GRASS_BLOCK),
			new FlatChunkGeneratorLayer(3, Blocks.DIRT),
			new FlatChunkGeneratorLayer(59, Blocks.STONE),
			new FlatChunkGeneratorLayer(1, Blocks.BEDROCK)
		);
		addPreset(
			new TranslatableText("createWorld.customize.preset.bottomless_pit"),
			Items.FEATHER,
			BiomeKeys.PLAINS,
			Arrays.asList(StructureFeature.VILLAGE),
			false,
			false,
			false,
			new FlatChunkGeneratorLayer(1, Blocks.GRASS_BLOCK),
			new FlatChunkGeneratorLayer(3, Blocks.DIRT),
			new FlatChunkGeneratorLayer(2, Blocks.COBBLESTONE)
		);
		addPreset(
			new TranslatableText("createWorld.customize.preset.desert"),
			Blocks.SAND,
			BiomeKeys.DESERT,
			Arrays.asList(StructureFeature.VILLAGE, StructureFeature.DESERT_PYRAMID, StructureFeature.MINESHAFT),
			true,
			true,
			false,
			new FlatChunkGeneratorLayer(8, Blocks.SAND),
			new FlatChunkGeneratorLayer(52, Blocks.SANDSTONE),
			new FlatChunkGeneratorLayer(3, Blocks.STONE),
			new FlatChunkGeneratorLayer(1, Blocks.BEDROCK)
		);
		addPreset(
			new TranslatableText("createWorld.customize.preset.redstone_ready"),
			Items.REDSTONE,
			BiomeKeys.DESERT,
			Collections.emptyList(),
			false,
			false,
			false,
			new FlatChunkGeneratorLayer(52, Blocks.SANDSTONE),
			new FlatChunkGeneratorLayer(3, Blocks.STONE),
			new FlatChunkGeneratorLayer(1, Blocks.BEDROCK)
		);
		addPreset(
			new TranslatableText("createWorld.customize.preset.the_void"),
			Blocks.BARRIER,
			BiomeKeys.THE_VOID,
			Collections.emptyList(),
			false,
			true,
			false,
			new FlatChunkGeneratorLayer(1, Blocks.AIR)
		);
	}

	@Environment(EnvType.CLIENT)
	static class SuperflatPreset {
		public final Item icon;
		public final Text name;
		public final Function<Registry<Biome>, FlatChunkGeneratorConfig> generatorConfigProvider;

		public SuperflatPreset(Item icon, Text name, Function<Registry<Biome>, FlatChunkGeneratorConfig> generatorConfigProvider) {
			this.icon = icon;
			this.name = name;
			this.generatorConfigProvider = generatorConfigProvider;
		}

		public Text getName() {
			return this.name;
		}
	}

	@Environment(EnvType.CLIENT)
	class SuperflatPresetsListWidget extends AlwaysSelectedEntryListWidget<PresetsScreen.SuperflatPresetsListWidget.SuperflatPresetEntry> {
		public SuperflatPresetsListWidget() {
			super(PresetsScreen.this.client, PresetsScreen.this.width, PresetsScreen.this.height, 80, PresetsScreen.this.height - 37, 24);

			for (PresetsScreen.SuperflatPreset superflatPreset : PresetsScreen.PRESETS) {
				this.addEntry(new PresetsScreen.SuperflatPresetsListWidget.SuperflatPresetEntry(superflatPreset));
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
				if ((keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) && this.getSelected() != null) {
					this.getSelected().setPreset();
				}

				return false;
			}
		}

		@Environment(EnvType.CLIENT)
		public class SuperflatPresetEntry extends AlwaysSelectedEntryListWidget.Entry<PresetsScreen.SuperflatPresetsListWidget.SuperflatPresetEntry> {
			private final PresetsScreen.SuperflatPreset preset;

			public SuperflatPresetEntry(PresetsScreen.SuperflatPreset preset) {
				this.preset = preset;
			}

			@Override
			public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
				this.renderIcon(matrices, x, y, this.preset.icon);
				PresetsScreen.this.textRenderer.draw(matrices, this.preset.name, (float)(x + 18 + 5), (float)(y + 6), 16777215);
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
				Registry<Biome> registry = PresetsScreen.this.parent.parent.moreOptionsDialog.getRegistryManager().get(Registry.BIOME_KEY);
				PresetsScreen.this.config = (FlatChunkGeneratorConfig)this.preset.generatorConfigProvider.apply(registry);
				PresetsScreen.this.customPresetField.setText(PresetsScreen.getGeneratorConfigString(registry, PresetsScreen.this.config));
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
			public Text method_37006() {
				return new TranslatableText("narrator.select", this.preset.getName());
			}
		}
	}
}
