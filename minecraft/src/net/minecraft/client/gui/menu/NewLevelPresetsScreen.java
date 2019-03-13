package net.minecraft.client.gui.menu;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4185;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorLayer;

@Environment(EnvType.CLIENT)
public class NewLevelPresetsScreen extends Screen {
	private static final List<NewLevelPresetsScreen.class_431> field_2518 = Lists.<NewLevelPresetsScreen.class_431>newArrayList();
	private final CustomizeFlatLevelScreen field_2519;
	private String field_2522;
	private String field_2520;
	private String field_2524;
	private NewLevelPresetsScreen.class_4196 field_2521;
	private class_4185 field_2525;
	private TextFieldWidget field_2523;
	public int field_18746 = -1;

	public NewLevelPresetsScreen(CustomizeFlatLevelScreen customizeFlatLevelScreen) {
		this.field_2519 = customizeFlatLevelScreen;
	}

	@Override
	protected void onInitialized() {
		this.client.keyboard.enableRepeatEvents(true);
		this.field_2523 = new TextFieldWidget(this.fontRenderer, 50, 40, this.screenWidth - 100, 20);
		this.field_2523.setMaxLength(1230);
		this.field_2523.setText(this.field_2519.method_2138());
		this.listeners.add(this.field_2523);
		this.field_2522 = I18n.translate("createWorld.customize.presets.title");
		this.field_2520 = I18n.translate("createWorld.customize.presets.share");
		this.field_2524 = I18n.translate("createWorld.customize.presets.list");
		this.field_2521 = new NewLevelPresetsScreen.class_4196();
		this.listeners.add(this.field_2521);
		this.field_2525 = this.addButton(
			new class_4185(this.screenWidth / 2 - 155, this.screenHeight - 28, 150, 20, I18n.translate("createWorld.customize.presets.select")) {
				@Override
				public void method_1826() {
					NewLevelPresetsScreen.this.field_2519.method_2139(NewLevelPresetsScreen.this.field_2523.getText());
					NewLevelPresetsScreen.this.client.method_1507(NewLevelPresetsScreen.this.field_2519);
				}
			}
		);
		this.addButton(new class_4185(this.screenWidth / 2 + 5, this.screenHeight - 28, 150, 20, I18n.translate("gui.cancel")) {
			@Override
			public void method_1826() {
				NewLevelPresetsScreen.this.client.method_1507(NewLevelPresetsScreen.this.field_2519);
			}
		});
		this.method_2191();
	}

	@Override
	public boolean mouseScrolled(double d, double e, double f) {
		return this.field_2521.mouseScrolled(d, e, f);
	}

	@Override
	public void onScaleChanged(MinecraftClient minecraftClient, int i, int j) {
		String string = this.field_2523.getText();
		this.initialize(minecraftClient, i, j);
		this.field_2523.setText(string);
	}

	@Override
	public void onClosed() {
		this.client.keyboard.enableRepeatEvents(false);
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawBackground();
		this.field_2521.draw(i, j, f);
		this.drawStringCentered(this.fontRenderer, this.field_2522, this.screenWidth / 2, 8, 16777215);
		this.drawString(this.fontRenderer, this.field_2520, 50, 30, 10526880);
		this.drawString(this.fontRenderer, this.field_2524, 50, 70, 10526880);
		this.field_2523.draw(i, j, f);
		super.draw(i, j, f);
	}

	@Override
	public void update() {
		this.field_2523.tick();
		super.update();
	}

	public void method_2191() {
		this.field_2525.enabled = this.method_2197();
	}

	private boolean method_2197() {
		return this.field_18746 > -1 && this.field_18746 < field_2518.size() || this.field_2523.getText().length() > 1;
	}

	private static void method_2195(String string, ItemProvider itemProvider, Biome biome, List<String> list, FlatChunkGeneratorLayer... flatChunkGeneratorLayers) {
		FlatChunkGeneratorConfig flatChunkGeneratorConfig = ChunkGeneratorType.field_12766.method_12117();

		for (int i = flatChunkGeneratorLayers.length - 1; i >= 0; i--) {
			flatChunkGeneratorConfig.getLayers().add(flatChunkGeneratorLayers[i]);
		}

		flatChunkGeneratorConfig.setBiome(biome);
		flatChunkGeneratorConfig.updateLayerBlocks();

		for (String string2 : list) {
			flatChunkGeneratorConfig.getStructures().put(string2, Maps.newHashMap());
		}

		field_2518.add(new NewLevelPresetsScreen.class_431(itemProvider.getItem(), string, flatChunkGeneratorConfig.toString()));
	}

	static {
		method_2195(
			I18n.translate("createWorld.customize.preset.classic_flat"),
			Blocks.field_10219,
			Biomes.field_9451,
			Arrays.asList("village"),
			new FlatChunkGeneratorLayer(1, Blocks.field_10219),
			new FlatChunkGeneratorLayer(2, Blocks.field_10566),
			new FlatChunkGeneratorLayer(1, Blocks.field_9987)
		);
		method_2195(
			I18n.translate("createWorld.customize.preset.tunnelers_dream"),
			Blocks.field_10340,
			Biomes.field_9472,
			Arrays.asList("biome_1", "dungeon", "decoration", "stronghold", "mineshaft"),
			new FlatChunkGeneratorLayer(1, Blocks.field_10219),
			new FlatChunkGeneratorLayer(5, Blocks.field_10566),
			new FlatChunkGeneratorLayer(230, Blocks.field_10340),
			new FlatChunkGeneratorLayer(1, Blocks.field_9987)
		);
		method_2195(
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
		method_2195(
			I18n.translate("createWorld.customize.preset.overworld"),
			Blocks.field_10479,
			Biomes.field_9451,
			Arrays.asList("village", "biome_1", "decoration", "stronghold", "mineshaft", "dungeon", "lake", "lava_lake"),
			new FlatChunkGeneratorLayer(1, Blocks.field_10219),
			new FlatChunkGeneratorLayer(3, Blocks.field_10566),
			new FlatChunkGeneratorLayer(59, Blocks.field_10340),
			new FlatChunkGeneratorLayer(1, Blocks.field_9987)
		);
		method_2195(
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
		method_2195(
			I18n.translate("createWorld.customize.preset.bottomless_pit"),
			Items.field_8153,
			Biomes.field_9451,
			Arrays.asList("village", "biome_1"),
			new FlatChunkGeneratorLayer(1, Blocks.field_10219),
			new FlatChunkGeneratorLayer(3, Blocks.field_10566),
			new FlatChunkGeneratorLayer(2, Blocks.field_10445)
		);
		method_2195(
			I18n.translate("createWorld.customize.preset.desert"),
			Blocks.field_10102,
			Biomes.field_9424,
			Arrays.asList("village", "biome_1", "decoration", "stronghold", "mineshaft", "dungeon"),
			new FlatChunkGeneratorLayer(8, Blocks.field_10102),
			new FlatChunkGeneratorLayer(52, Blocks.field_9979),
			new FlatChunkGeneratorLayer(3, Blocks.field_10340),
			new FlatChunkGeneratorLayer(1, Blocks.field_9987)
		);
		method_2195(
			I18n.translate("createWorld.customize.preset.redstone_ready"),
			Items.field_8725,
			Biomes.field_9424,
			Collections.emptyList(),
			new FlatChunkGeneratorLayer(52, Blocks.field_9979),
			new FlatChunkGeneratorLayer(3, Blocks.field_10340),
			new FlatChunkGeneratorLayer(1, Blocks.field_9987)
		);
		method_2195(
			I18n.translate("createWorld.customize.preset.the_void"),
			Blocks.field_10499,
			Biomes.field_9473,
			Arrays.asList("decoration"),
			new FlatChunkGeneratorLayer(1, Blocks.field_10124)
		);
	}

	@Environment(EnvType.CLIENT)
	class class_4196 extends EntryListWidget<NewLevelPresetsScreen.class_432> {
		public class_4196() {
			super(
				NewLevelPresetsScreen.this.client,
				NewLevelPresetsScreen.this.screenWidth,
				NewLevelPresetsScreen.this.screenHeight,
				80,
				NewLevelPresetsScreen.this.screenHeight - 37,
				24
			);

			for (int i = 0; i < NewLevelPresetsScreen.field_2518.size(); i++) {
				this.addEntry(NewLevelPresetsScreen.this.new class_432());
			}
		}

		@Override
		protected boolean isSelectedEntry(int i) {
			return i == NewLevelPresetsScreen.this.field_18746;
		}

		@Override
		protected void method_19351(int i) {
			NewLevelPresetsScreen.this.field_18746 = MathHelper.clamp(NewLevelPresetsScreen.this.field_18746 + i, 0, this.getEntryCount() - 1);
			if (NewLevelPresetsScreen.this.field_18746 > -1 && NewLevelPresetsScreen.this.field_18746 < NewLevelPresetsScreen.field_2518.size()) {
				this.method_19349((EntryListWidget.Entry)this.getInputListeners().get(NewLevelPresetsScreen.this.field_18746));
			}

			NewLevelPresetsScreen.this.method_2191();
		}

		@Override
		protected boolean method_19352() {
			return NewLevelPresetsScreen.this.method_19357() == this;
		}

		@Override
		public boolean hasFocus() {
			return true;
		}

		@Override
		public void setHasFocus(boolean bl) {
			if (bl && NewLevelPresetsScreen.this.field_18746 < 0) {
				NewLevelPresetsScreen.this.field_18746 = 0;
				NewLevelPresetsScreen.this.method_2191();
			}
		}

		@Override
		public boolean keyPressed(int i, int j, int k) {
			if (super.keyPressed(i, j, k)) {
				return true;
			} else {
				if ((i == 257 || i == 335)
					&& NewLevelPresetsScreen.this.field_18746 > -1
					&& NewLevelPresetsScreen.this.field_18746 < NewLevelPresetsScreen.field_2518.size()) {
					((NewLevelPresetsScreen.class_432)this.getInputListeners().get(NewLevelPresetsScreen.this.field_18746)).method_19389();
				}

				return false;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	static class class_431 {
		public final Item field_2527;
		public final String field_2528;
		public final String field_2526;

		public class_431(Item item, String string, String string2) {
			this.field_2527 = item;
			this.field_2528 = string;
			this.field_2526 = string2;
		}
	}

	@Environment(EnvType.CLIENT)
	public class class_432 extends EntryListWidget.Entry<NewLevelPresetsScreen.class_432> {
		@Override
		public void draw(int i, int j, int k, int l, boolean bl, float f) {
			NewLevelPresetsScreen.class_431 lv = (NewLevelPresetsScreen.class_431)NewLevelPresetsScreen.field_2518.get(this.field_2143);
			this.method_2200(this.getX(), this.getY(), lv.field_2527);
			NewLevelPresetsScreen.this.fontRenderer.draw(lv.field_2528, (float)(this.getX() + 18 + 5), (float)(this.getY() + 6), 16777215);
		}

		@Override
		public boolean mouseClicked(double d, double e, int i) {
			if (i == 0) {
				this.method_19389();
			}

			return false;
		}

		private void method_19389() {
			NewLevelPresetsScreen.this.field_18746 = this.field_2143;
			NewLevelPresetsScreen.this.method_2191();
			NewLevelPresetsScreen.this.field_2523.setText(((NewLevelPresetsScreen.class_431)NewLevelPresetsScreen.field_2518.get(this.field_2143)).field_2526);
			NewLevelPresetsScreen.this.field_2523.method_1870();
		}

		private void method_2200(int i, int j, Item item) {
			this.method_2198(i + 1, j + 1);
			GlStateManager.enableRescaleNormal();
			GuiLighting.enableForItems();
			NewLevelPresetsScreen.this.field_2560.renderGuiItemIcon(new ItemStack(item), i + 2, j + 2);
			GuiLighting.disable();
			GlStateManager.disableRescaleNormal();
		}

		private void method_2198(int i, int j) {
			this.method_2199(i, j, 0, 0);
		}

		private void method_2199(int i, int j, int k, int l) {
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			NewLevelPresetsScreen.this.client.method_1531().method_4618(DrawableHelper.field_2052);
			float f = 0.0078125F;
			float g = 0.0078125F;
			int m = 18;
			int n = 18;
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
			bufferBuilder.method_1328(7, VertexFormats.field_1585);
			bufferBuilder.vertex((double)(i + 0), (double)(j + 18), (double)NewLevelPresetsScreen.this.zOffset)
				.texture((double)((float)(k + 0) * 0.0078125F), (double)((float)(l + 18) * 0.0078125F))
				.next();
			bufferBuilder.vertex((double)(i + 18), (double)(j + 18), (double)NewLevelPresetsScreen.this.zOffset)
				.texture((double)((float)(k + 18) * 0.0078125F), (double)((float)(l + 18) * 0.0078125F))
				.next();
			bufferBuilder.vertex((double)(i + 18), (double)(j + 0), (double)NewLevelPresetsScreen.this.zOffset)
				.texture((double)((float)(k + 18) * 0.0078125F), (double)((float)(l + 0) * 0.0078125F))
				.next();
			bufferBuilder.vertex((double)(i + 0), (double)(j + 0), (double)NewLevelPresetsScreen.this.zOffset)
				.texture((double)((float)(k + 0) * 0.0078125F), (double)((float)(l + 0) * 0.0078125F))
				.next();
			tessellator.draw();
		}
	}
}
