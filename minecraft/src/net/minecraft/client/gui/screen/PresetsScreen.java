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
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorLayer;
import net.minecraft.world.gen.chunk.StructureConfig;
import net.minecraft.world.gen.chunk.StructuresConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class PresetsScreen extends Screen {
	private static final Logger field_25043 = LogManager.getLogger();
	private static final List<PresetsScreen.SuperflatPreset> presets = Lists.<PresetsScreen.SuperflatPreset>newArrayList();
	private final CustomizeFlatLevelScreen parent;
	private Text shareText;
	private Text listText;
	private PresetsScreen.SuperflatPresetsListWidget listWidget;
	private ButtonWidget selectPresetButton;
	private TextFieldWidget customPresetField;
	private FlatChunkGeneratorConfig field_25044;

	public PresetsScreen(CustomizeFlatLevelScreen parent) {
		super(new TranslatableText("createWorld.customize.presets.title"));
		this.parent = parent;
	}

	@Nullable
	private static FlatChunkGeneratorLayer method_29059(String string, int i) {
		String[] strings = string.split("\\*", 2);
		int j;
		if (strings.length == 2) {
			try {
				j = Math.max(Integer.parseInt(strings[0]), 0);
			} catch (NumberFormatException var10) {
				field_25043.error("Error while parsing flat world string => {}", var10.getMessage());
				return null;
			}
		} else {
			j = 1;
		}

		int k = Math.min(i + j, 256);
		int l = k - i;
		String string2 = strings[strings.length - 1];

		Block block;
		try {
			block = (Block)Registry.BLOCK.getOrEmpty(new Identifier(string2)).orElse(null);
		} catch (Exception var9) {
			field_25043.error("Error while parsing flat world string => {}", var9.getMessage());
			return null;
		}

		if (block == null) {
			field_25043.error("Error while parsing flat world string => Unknown block, {}", string2);
			return null;
		} else {
			FlatChunkGeneratorLayer flatChunkGeneratorLayer = new FlatChunkGeneratorLayer(l, block);
			flatChunkGeneratorLayer.setStartY(i);
			return flatChunkGeneratorLayer;
		}
	}

	private static List<FlatChunkGeneratorLayer> method_29058(String string) {
		List<FlatChunkGeneratorLayer> list = Lists.<FlatChunkGeneratorLayer>newArrayList();
		String[] strings = string.split(",");
		int i = 0;

		for (String string2 : strings) {
			FlatChunkGeneratorLayer flatChunkGeneratorLayer = method_29059(string2, i);
			if (flatChunkGeneratorLayer == null) {
				return Collections.emptyList();
			}

			list.add(flatChunkGeneratorLayer);
			i += flatChunkGeneratorLayer.getThickness();
		}

		return list;
	}

	public static FlatChunkGeneratorConfig method_29060(Registry<Biome> registry, String string, FlatChunkGeneratorConfig flatChunkGeneratorConfig) {
		Iterator<String> iterator = Splitter.on(';').split(string).iterator();
		if (!iterator.hasNext()) {
			return FlatChunkGeneratorConfig.getDefaultConfig();
		} else {
			List<FlatChunkGeneratorLayer> list = method_29058((String)iterator.next());
			if (list.isEmpty()) {
				return FlatChunkGeneratorConfig.getDefaultConfig();
			} else {
				FlatChunkGeneratorConfig flatChunkGeneratorConfig2 = flatChunkGeneratorConfig.method_29965(list, flatChunkGeneratorConfig.getConfig());
				Biome biome = Biomes.PLAINS;
				if (iterator.hasNext()) {
					try {
						Identifier identifier = new Identifier((String)iterator.next());
						biome = (Biome)registry.getOrEmpty(identifier).orElseThrow(() -> new IllegalArgumentException("Invalid Biome: " + identifier));
					} catch (Exception var8) {
						field_25043.error("Error while parsing flat world string => {}", var8.getMessage());
					}
				}

				flatChunkGeneratorConfig2.setBiome(biome);
				return flatChunkGeneratorConfig2;
			}
		}
	}

	private static String method_29062(DynamicRegistryManager dynamicRegistryManager, FlatChunkGeneratorConfig flatChunkGeneratorConfig) {
		StringBuilder stringBuilder = new StringBuilder();

		for (int i = 0; i < flatChunkGeneratorConfig.getLayers().size(); i++) {
			if (i > 0) {
				stringBuilder.append(",");
			}

			stringBuilder.append(flatChunkGeneratorConfig.getLayers().get(i));
		}

		stringBuilder.append(";");
		stringBuilder.append(dynamicRegistryManager.get(Registry.BIOME_KEY).getId(flatChunkGeneratorConfig.getBiome()));
		return stringBuilder.toString();
	}

	@Override
	protected void init() {
		this.client.keyboard.enableRepeatEvents(true);
		this.shareText = new TranslatableText("createWorld.customize.presets.share");
		this.listText = new TranslatableText("createWorld.customize.presets.list");
		this.customPresetField = new TextFieldWidget(this.textRenderer, 50, 40, this.width - 100, 20, this.shareText);
		this.customPresetField.setMaxLength(1230);
		this.customPresetField.setText(method_29062(this.parent.parent.moreOptionsDialog.method_29700(), this.parent.method_29055()));
		this.field_25044 = this.parent.method_29055();
		this.children.add(this.customPresetField);
		this.listWidget = new PresetsScreen.SuperflatPresetsListWidget();
		this.children.add(this.listWidget);
		this.selectPresetButton = this.addButton(
			new ButtonWidget(
				this.width / 2 - 155,
				this.height - 28,
				150,
				20,
				new TranslatableText("createWorld.customize.presets.select"),
				buttonWidget -> {
					FlatChunkGeneratorConfig flatChunkGeneratorConfig = method_29060(
						this.parent.parent.moreOptionsDialog.method_29700().get(Registry.BIOME_KEY), this.customPresetField.getText(), this.field_25044
					);
					this.parent.method_29054(flatChunkGeneratorConfig);
					this.client.openScreen(this.parent);
				}
			)
		);
		this.addButton(new ButtonWidget(this.width / 2 + 5, this.height - 28, 150, 20, ScreenTexts.CANCEL, buttonWidget -> this.client.openScreen(this.parent)));
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
		this.client.keyboard.enableRepeatEvents(false);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		this.listWidget.render(matrices, mouseX, mouseY, delta);
		RenderSystem.pushMatrix();
		RenderSystem.translatef(0.0F, 0.0F, 400.0F);
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 16777215);
		drawTextWithShadow(matrices, this.textRenderer, this.shareText, 50, 30, 10526880);
		drawTextWithShadow(matrices, this.textRenderer, this.listText, 50, 70, 10526880);
		RenderSystem.popMatrix();
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
		Text text,
		ItemConvertible icon,
		Biome biome,
		List<StructureFeature<?>> structures,
		boolean bl,
		boolean bl2,
		boolean bl3,
		FlatChunkGeneratorLayer... flatChunkGeneratorLayers
	) {
		Map<StructureFeature<?>, StructureConfig> map = Maps.<StructureFeature<?>, StructureConfig>newHashMap();

		for (StructureFeature<?> structureFeature : structures) {
			map.put(structureFeature, StructuresConfig.DEFAULT_STRUCTURES.get(structureFeature));
		}

		StructuresConfig structuresConfig = new StructuresConfig(bl ? Optional.of(StructuresConfig.DEFAULT_STRONGHOLD) : Optional.empty(), map);
		FlatChunkGeneratorConfig flatChunkGeneratorConfig = new FlatChunkGeneratorConfig(structuresConfig);
		if (bl2) {
			flatChunkGeneratorConfig.method_28911();
		}

		if (bl3) {
			flatChunkGeneratorConfig.method_28916();
		}

		for (int i = flatChunkGeneratorLayers.length - 1; i >= 0; i--) {
			flatChunkGeneratorConfig.getLayers().add(flatChunkGeneratorLayers[i]);
		}

		flatChunkGeneratorConfig.setBiome(biome);
		flatChunkGeneratorConfig.updateLayerBlocks();
		presets.add(new PresetsScreen.SuperflatPreset(icon.asItem(), text, flatChunkGeneratorConfig.method_28912(structuresConfig)));
	}

	static {
		addPreset(
			new TranslatableText("createWorld.customize.preset.classic_flat"),
			Blocks.GRASS_BLOCK,
			Biomes.PLAINS,
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
			Biomes.MOUNTAINS,
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
			Biomes.DEEP_OCEAN,
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
			Biomes.PLAINS,
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
			Biomes.SNOWY_TUNDRA,
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
			Biomes.PLAINS,
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
			Biomes.DESERT,
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
			Biomes.DESERT,
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
			Biomes.THE_VOID,
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
		public final FlatChunkGeneratorConfig field_25045;

		public SuperflatPreset(Item icon, Text text, FlatChunkGeneratorConfig flatChunkGeneratorConfig) {
			this.icon = icon;
			this.name = text;
			this.field_25045 = flatChunkGeneratorConfig;
		}

		public Text method_27571() {
			return this.name;
		}
	}

	@Environment(EnvType.CLIENT)
	class SuperflatPresetsListWidget extends AlwaysSelectedEntryListWidget<PresetsScreen.SuperflatPresetsListWidget.SuperflatPresetEntry> {
		public SuperflatPresetsListWidget() {
			super(PresetsScreen.this.client, PresetsScreen.this.width, PresetsScreen.this.height, 80, PresetsScreen.this.height - 37, 24);

			for (int i = 0; i < PresetsScreen.presets.size(); i++) {
				this.addEntry(new PresetsScreen.SuperflatPresetsListWidget.SuperflatPresetEntry());
			}
		}

		public void setSelected(@Nullable PresetsScreen.SuperflatPresetsListWidget.SuperflatPresetEntry superflatPresetEntry) {
			super.setSelected(superflatPresetEntry);
			if (superflatPresetEntry != null) {
				NarratorManager.INSTANCE
					.narrate(
						new TranslatableText(
								"narrator.select", ((PresetsScreen.SuperflatPreset)PresetsScreen.presets.get(this.children().indexOf(superflatPresetEntry))).method_27571()
							)
							.getString()
					);
			}

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
				if ((keyCode == 257 || keyCode == 335) && this.getSelected() != null) {
					this.getSelected().setPreset();
				}

				return false;
			}
		}

		@Environment(EnvType.CLIENT)
		public class SuperflatPresetEntry extends AlwaysSelectedEntryListWidget.Entry<PresetsScreen.SuperflatPresetsListWidget.SuperflatPresetEntry> {
			@Override
			public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
				PresetsScreen.SuperflatPreset superflatPreset = (PresetsScreen.SuperflatPreset)PresetsScreen.presets.get(index);
				this.method_2200(matrices, x, y, superflatPreset.icon);
				PresetsScreen.this.textRenderer.draw(matrices, superflatPreset.name, (float)(x + 18 + 5), (float)(y + 6), 16777215);
			}

			@Override
			public boolean mouseClicked(double mouseX, double mouseY, int button) {
				if (button == 0) {
					this.setPreset();
				}

				return false;
			}

			private void setPreset() {
				SuperflatPresetsListWidget.this.setSelected(this);
				PresetsScreen.SuperflatPreset superflatPreset = (PresetsScreen.SuperflatPreset)PresetsScreen.presets
					.get(SuperflatPresetsListWidget.this.children().indexOf(this));
				PresetsScreen.this.customPresetField
					.setText(PresetsScreen.method_29062(PresetsScreen.this.parent.parent.moreOptionsDialog.method_29700(), superflatPreset.field_25045));
				PresetsScreen.this.customPresetField.setCursorToStart();
				PresetsScreen.this.field_25044 = superflatPreset.field_25045;
			}

			private void method_2200(MatrixStack matrixStack, int i, int j, Item item) {
				this.method_2198(matrixStack, i + 1, j + 1);
				RenderSystem.enableRescaleNormal();
				PresetsScreen.this.itemRenderer.renderGuiItemIcon(new ItemStack(item), i + 2, j + 2);
				RenderSystem.disableRescaleNormal();
			}

			private void method_2198(MatrixStack matrixStack, int i, int j) {
				RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				SuperflatPresetsListWidget.this.client.getTextureManager().bindTexture(DrawableHelper.STATS_ICON_TEXTURE);
				DrawableHelper.drawTexture(matrixStack, i, j, PresetsScreen.this.getZOffset(), 0.0F, 0.0F, 18, 18, 128, 128);
			}
		}
	}
}
