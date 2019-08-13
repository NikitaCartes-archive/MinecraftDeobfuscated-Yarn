package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorLayer;

@Environment(EnvType.CLIENT)
public class PresetsScreen extends Screen {
	private static final List<PresetsScreen.SuperflatPreset> presets = Lists.<PresetsScreen.SuperflatPreset>newArrayList();
	private final CustomizeFlatLevelScreen parent;
	private String shareText;
	private String listText;
	private PresetsScreen.SuperflatPresetsListWidget listWidget;
	private ButtonWidget selectPresetButton;
	private TextFieldWidget customPresetField;

	public PresetsScreen(CustomizeFlatLevelScreen customizeFlatLevelScreen) {
		super(new TranslatableText("createWorld.customize.presets.title"));
		this.parent = customizeFlatLevelScreen;
	}

	@Override
	protected void init() {
		this.minecraft.keyboard.enableRepeatEvents(true);
		this.shareText = I18n.translate("createWorld.customize.presets.share");
		this.listText = I18n.translate("createWorld.customize.presets.list");
		this.customPresetField = new TextFieldWidget(this.font, 50, 40, this.width - 100, 20, this.shareText);
		this.customPresetField.setMaxLength(1230);
		this.customPresetField.setText(this.parent.getConfigString());
		this.children.add(this.customPresetField);
		this.listWidget = new PresetsScreen.SuperflatPresetsListWidget();
		this.children.add(this.listWidget);
		this.selectPresetButton = this.addButton(
			new ButtonWidget(this.width / 2 - 155, this.height - 28, 150, 20, I18n.translate("createWorld.customize.presets.select"), buttonWidget -> {
				this.parent.method_2139(this.customPresetField.getText());
				this.minecraft.openScreen(this.parent);
			})
		);
		this.addButton(
			new ButtonWidget(this.width / 2 + 5, this.height - 28, 150, 20, I18n.translate("gui.cancel"), buttonWidget -> this.minecraft.openScreen(this.parent))
		);
		this.updateSelectButton(this.listWidget.getSelected() != null);
	}

	@Override
	public boolean mouseScrolled(double d, double e, double f) {
		return this.listWidget.mouseScrolled(d, e, f);
	}

	@Override
	public void resize(MinecraftClient minecraftClient, int i, int j) {
		String string = this.customPresetField.getText();
		this.init(minecraftClient, i, j);
		this.customPresetField.setText(string);
	}

	@Override
	public void removed() {
		this.minecraft.keyboard.enableRepeatEvents(false);
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		this.listWidget.render(i, j, f);
		this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, 8, 16777215);
		this.drawString(this.font, this.shareText, 50, 30, 10526880);
		this.drawString(this.font, this.listText, 50, 70, 10526880);
		this.customPresetField.render(i, j, f);
		super.render(i, j, f);
	}

	@Override
	public void tick() {
		this.customPresetField.tick();
		super.tick();
	}

	public void updateSelectButton(boolean bl) {
		this.selectPresetButton.active = bl || this.customPresetField.getText().length() > 1;
	}

	private static void addPreset(
		String string, ItemConvertible itemConvertible, Biome biome, List<String> list, FlatChunkGeneratorLayer... flatChunkGeneratorLayers
	) {
		FlatChunkGeneratorConfig flatChunkGeneratorConfig = ChunkGeneratorType.field_12766.createSettings();

		for (int i = flatChunkGeneratorLayers.length - 1; i >= 0; i--) {
			flatChunkGeneratorConfig.getLayers().add(flatChunkGeneratorLayers[i]);
		}

		flatChunkGeneratorConfig.setBiome(biome);
		flatChunkGeneratorConfig.updateLayerBlocks();

		for (String string2 : list) {
			flatChunkGeneratorConfig.getStructures().put(string2, Maps.newHashMap());
		}

		presets.add(new PresetsScreen.SuperflatPreset(itemConvertible.asItem(), string, flatChunkGeneratorConfig.toString()));
	}

	static {
		addPreset(
			I18n.translate("createWorld.customize.preset.classic_flat"),
			Blocks.field_10219,
			Biomes.field_9451,
			Arrays.asList("village"),
			new FlatChunkGeneratorLayer(1, Blocks.field_10219),
			new FlatChunkGeneratorLayer(2, Blocks.field_10566),
			new FlatChunkGeneratorLayer(1, Blocks.field_9987)
		);
		addPreset(
			I18n.translate("createWorld.customize.preset.tunnelers_dream"),
			Blocks.field_10340,
			Biomes.field_9472,
			Arrays.asList("biome_1", "dungeon", "decoration", "stronghold", "mineshaft"),
			new FlatChunkGeneratorLayer(1, Blocks.field_10219),
			new FlatChunkGeneratorLayer(5, Blocks.field_10566),
			new FlatChunkGeneratorLayer(230, Blocks.field_10340),
			new FlatChunkGeneratorLayer(1, Blocks.field_9987)
		);
		addPreset(
			I18n.translate("createWorld.customize.preset.water_world"),
			Items.field_8705,
			Biomes.field_9446,
			Arrays.asList("biome_1", "oceanmonument"),
			new FlatChunkGeneratorLayer(90, Blocks.field_10382),
			new FlatChunkGeneratorLayer(5, Blocks.field_10102),
			new FlatChunkGeneratorLayer(5, Blocks.field_10566),
			new FlatChunkGeneratorLayer(5, Blocks.field_10340),
			new FlatChunkGeneratorLayer(1, Blocks.field_9987)
		);
		addPreset(
			I18n.translate("createWorld.customize.preset.overworld"),
			Blocks.field_10479,
			Biomes.field_9451,
			Arrays.asList("village", "biome_1", "decoration", "stronghold", "mineshaft", "dungeon", "lake", "lava_lake", "pillager_outpost"),
			new FlatChunkGeneratorLayer(1, Blocks.field_10219),
			new FlatChunkGeneratorLayer(3, Blocks.field_10566),
			new FlatChunkGeneratorLayer(59, Blocks.field_10340),
			new FlatChunkGeneratorLayer(1, Blocks.field_9987)
		);
		addPreset(
			I18n.translate("createWorld.customize.preset.snowy_kingdom"),
			Blocks.field_10477,
			Biomes.field_9452,
			Arrays.asList("village", "biome_1"),
			new FlatChunkGeneratorLayer(1, Blocks.field_10477),
			new FlatChunkGeneratorLayer(1, Blocks.field_10219),
			new FlatChunkGeneratorLayer(3, Blocks.field_10566),
			new FlatChunkGeneratorLayer(59, Blocks.field_10340),
			new FlatChunkGeneratorLayer(1, Blocks.field_9987)
		);
		addPreset(
			I18n.translate("createWorld.customize.preset.bottomless_pit"),
			Items.field_8153,
			Biomes.field_9451,
			Arrays.asList("village", "biome_1"),
			new FlatChunkGeneratorLayer(1, Blocks.field_10219),
			new FlatChunkGeneratorLayer(3, Blocks.field_10566),
			new FlatChunkGeneratorLayer(2, Blocks.field_10445)
		);
		addPreset(
			I18n.translate("createWorld.customize.preset.desert"),
			Blocks.field_10102,
			Biomes.field_9424,
			Arrays.asList("village", "biome_1", "decoration", "stronghold", "mineshaft", "dungeon"),
			new FlatChunkGeneratorLayer(8, Blocks.field_10102),
			new FlatChunkGeneratorLayer(52, Blocks.field_9979),
			new FlatChunkGeneratorLayer(3, Blocks.field_10340),
			new FlatChunkGeneratorLayer(1, Blocks.field_9987)
		);
		addPreset(
			I18n.translate("createWorld.customize.preset.redstone_ready"),
			Items.field_8725,
			Biomes.field_9424,
			Collections.emptyList(),
			new FlatChunkGeneratorLayer(52, Blocks.field_9979),
			new FlatChunkGeneratorLayer(3, Blocks.field_10340),
			new FlatChunkGeneratorLayer(1, Blocks.field_9987)
		);
		addPreset(
			I18n.translate("createWorld.customize.preset.the_void"),
			Blocks.field_10499,
			Biomes.field_9473,
			Arrays.asList("decoration"),
			new FlatChunkGeneratorLayer(1, Blocks.field_10124)
		);
	}

	@Environment(EnvType.CLIENT)
	static class SuperflatPreset {
		public final Item icon;
		public final String name;
		public final String config;

		public SuperflatPreset(Item item, String string, String string2) {
			this.icon = item;
			this.name = string;
			this.config = string2;
		}
	}

	@Environment(EnvType.CLIENT)
	class SuperflatPresetsListWidget extends AlwaysSelectedEntryListWidget<PresetsScreen.SuperflatPresetsListWidget.SuperflatPresetEntry> {
		public SuperflatPresetsListWidget() {
			super(PresetsScreen.this.minecraft, PresetsScreen.this.width, PresetsScreen.this.height, 80, PresetsScreen.this.height - 37, 24);

			for (int i = 0; i < PresetsScreen.presets.size(); i++) {
				this.addEntry(new PresetsScreen.SuperflatPresetsListWidget.SuperflatPresetEntry());
			}
		}

		public void method_20103(@Nullable PresetsScreen.SuperflatPresetsListWidget.SuperflatPresetEntry superflatPresetEntry) {
			super.setSelected(superflatPresetEntry);
			if (superflatPresetEntry != null) {
				NarratorManager.INSTANCE
					.narrate(
						new TranslatableText("narrator.select", ((PresetsScreen.SuperflatPreset)PresetsScreen.presets.get(this.children().indexOf(superflatPresetEntry))).name)
							.getString()
					);
			}
		}

		@Override
		protected void moveSelection(int i) {
			super.moveSelection(i);
			PresetsScreen.this.updateSelectButton(true);
		}

		@Override
		protected boolean isFocused() {
			return PresetsScreen.this.getFocused() == this;
		}

		@Override
		public boolean keyPressed(int i, int j, int k) {
			if (super.keyPressed(i, j, k)) {
				return true;
			} else {
				if ((i == 257 || i == 335) && this.getSelected() != null) {
					this.getSelected().setPreset();
				}

				return false;
			}
		}

		@Environment(EnvType.CLIENT)
		public class SuperflatPresetEntry extends AlwaysSelectedEntryListWidget.Entry<PresetsScreen.SuperflatPresetsListWidget.SuperflatPresetEntry> {
			@Override
			public void render(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
				PresetsScreen.SuperflatPreset superflatPreset = (PresetsScreen.SuperflatPreset)PresetsScreen.presets.get(i);
				this.method_2200(k, j, superflatPreset.icon);
				PresetsScreen.this.font.draw(superflatPreset.name, (float)(k + 18 + 5), (float)(j + 6), 16777215);
			}

			@Override
			public boolean mouseClicked(double d, double e, int i) {
				if (i == 0) {
					this.setPreset();
				}

				return false;
			}

			private void setPreset() {
				SuperflatPresetsListWidget.this.method_20103(this);
				PresetsScreen.this.updateSelectButton(true);
				PresetsScreen.this.customPresetField
					.setText(((PresetsScreen.SuperflatPreset)PresetsScreen.presets.get(SuperflatPresetsListWidget.this.children().indexOf(this))).config);
				PresetsScreen.this.customPresetField.method_1870();
			}

			private void method_2200(int i, int j, Item item) {
				this.method_2198(i + 1, j + 1);
				GlStateManager.enableRescaleNormal();
				GuiLighting.enableForItems();
				PresetsScreen.this.itemRenderer.renderGuiItemIcon(new ItemStack(item), i + 2, j + 2);
				GuiLighting.disable();
				GlStateManager.disableRescaleNormal();
			}

			private void method_2198(int i, int j) {
				GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				SuperflatPresetsListWidget.this.minecraft.getTextureManager().bindTexture(DrawableHelper.STATS_ICON_LOCATION);
				DrawableHelper.blit(i, j, PresetsScreen.this.blitOffset, 0.0F, 0.0F, 18, 18, 128, 128);
			}
		}
	}
}
