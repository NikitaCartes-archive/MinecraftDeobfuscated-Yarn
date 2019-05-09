package net.minecraft.client.gui.menu;

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
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorLayer;

@Environment(EnvType.CLIENT)
public class NewLevelPresetsScreen extends Screen {
	private static final List<NewLevelPresetsScreen.SuperflatPreset> presets = Lists.<NewLevelPresetsScreen.SuperflatPreset>newArrayList();
	private final CustomizeFlatLevelScreen parent;
	private String shareText;
	private String listText;
	private NewLevelPresetsScreen.SuperflatPresetsListWidget field_2521;
	private ButtonWidget field_2525;
	private TextFieldWidget customPresetField;

	public NewLevelPresetsScreen(CustomizeFlatLevelScreen customizeFlatLevelScreen) {
		super(new TranslatableComponent("createWorld.customize.presets.title"));
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
		this.field_2521 = new NewLevelPresetsScreen.SuperflatPresetsListWidget();
		this.children.add(this.field_2521);
		this.field_2525 = this.addButton(
			new ButtonWidget(this.width / 2 - 155, this.height - 28, 150, 20, I18n.translate("createWorld.customize.presets.select"), buttonWidget -> {
				this.parent.method_2139(this.customPresetField.getText());
				this.minecraft.openScreen(this.parent);
			})
		);
		this.addButton(
			new ButtonWidget(this.width / 2 + 5, this.height - 28, 150, 20, I18n.translate("gui.cancel"), buttonWidget -> this.minecraft.openScreen(this.parent))
		);
		this.method_20102(this.field_2521.getSelected() != null);
	}

	@Override
	public boolean mouseScrolled(double d, double e, double f) {
		return this.field_2521.mouseScrolled(d, e, f);
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
		this.field_2521.render(i, j, f);
		this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 8, 16777215);
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

	public void method_20102(boolean bl) {
		this.field_2525.active = bl || this.customPresetField.getText().length() > 1;
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

		presets.add(new NewLevelPresetsScreen.SuperflatPreset(itemConvertible.asItem(), string, flatChunkGeneratorConfig.toString()));
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
		public final Item field_2527;
		public final String field_2528;
		public final String field_2526;

		public SuperflatPreset(Item item, String string, String string2) {
			this.field_2527 = item;
			this.field_2528 = string;
			this.field_2526 = string2;
		}
	}

	@Environment(EnvType.CLIENT)
	class SuperflatPresetsListWidget extends AlwaysSelectedEntryListWidget<NewLevelPresetsScreen.SuperflatPresetsListWidget.SuperflatPresetItem> {
		public SuperflatPresetsListWidget() {
			super(
				NewLevelPresetsScreen.this.minecraft, NewLevelPresetsScreen.this.width, NewLevelPresetsScreen.this.height, 80, NewLevelPresetsScreen.this.height - 37, 24
			);

			for (int i = 0; i < NewLevelPresetsScreen.presets.size(); i++) {
				this.addEntry(new NewLevelPresetsScreen.SuperflatPresetsListWidget.SuperflatPresetItem());
			}
		}

		public void method_20103(@Nullable NewLevelPresetsScreen.SuperflatPresetsListWidget.SuperflatPresetItem superflatPresetItem) {
			super.setSelected(superflatPresetItem);
			if (superflatPresetItem != null) {
				NarratorManager.INSTANCE
					.narrate(
						new TranslatableComponent(
								"narrator.select", ((NewLevelPresetsScreen.SuperflatPreset)NewLevelPresetsScreen.presets.get(this.children().indexOf(superflatPresetItem))).field_2528
							)
							.getString()
					);
			}
		}

		@Override
		protected void moveSelection(int i) {
			super.moveSelection(i);
			NewLevelPresetsScreen.this.method_20102(true);
		}

		@Override
		protected boolean isFocused() {
			return NewLevelPresetsScreen.this.getFocused() == this;
		}

		@Override
		public boolean keyPressed(int i, int j, int k) {
			if (super.keyPressed(i, j, k)) {
				return true;
			} else {
				if ((i == 257 || i == 335) && this.getSelected() != null) {
					this.getSelected().method_19389();
				}

				return false;
			}
		}

		@Environment(EnvType.CLIENT)
		public class SuperflatPresetItem extends AlwaysSelectedEntryListWidget.Entry<NewLevelPresetsScreen.SuperflatPresetsListWidget.SuperflatPresetItem> {
			@Override
			public void render(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
				NewLevelPresetsScreen.SuperflatPreset superflatPreset = (NewLevelPresetsScreen.SuperflatPreset)NewLevelPresetsScreen.presets.get(i);
				this.method_2200(k, j, superflatPreset.field_2527);
				NewLevelPresetsScreen.this.font.draw(superflatPreset.field_2528, (float)(k + 18 + 5), (float)(j + 6), 16777215);
			}

			@Override
			public boolean mouseClicked(double d, double e, int i) {
				if (i == 0) {
					this.method_19389();
				}

				return false;
			}

			private void method_19389() {
				SuperflatPresetsListWidget.this.method_20103(this);
				NewLevelPresetsScreen.this.method_20102(true);
				NewLevelPresetsScreen.this.customPresetField
					.setText(((NewLevelPresetsScreen.SuperflatPreset)NewLevelPresetsScreen.presets.get(SuperflatPresetsListWidget.this.children().indexOf(this))).field_2526);
				NewLevelPresetsScreen.this.customPresetField.method_1870();
			}

			private void method_2200(int i, int j, Item item) {
				this.method_2198(i + 1, j + 1);
				GlStateManager.enableRescaleNormal();
				GuiLighting.enableForItems();
				NewLevelPresetsScreen.this.itemRenderer.renderGuiItemIcon(new ItemStack(item), i + 2, j + 2);
				GuiLighting.disable();
				GlStateManager.disableRescaleNormal();
			}

			private void method_2198(int i, int j) {
				GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				SuperflatPresetsListWidget.this.minecraft.getTextureManager().bindTexture(DrawableHelper.STATS_ICON_LOCATION);
				DrawableHelper.blit(i, j, NewLevelPresetsScreen.this.blitOffset, 0.0F, 0.0F, 18, 18, 128, 128);
			}
		}
	}
}
