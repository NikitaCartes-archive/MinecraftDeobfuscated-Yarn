package net.minecraft.client.gui.menu;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.AbstractListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
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
	private NewLevelPresetsScreen.class_432 field_2521;
	private ButtonWidget field_2525;
	private TextFieldWidget field_2523;

	public NewLevelPresetsScreen(CustomizeFlatLevelScreen customizeFlatLevelScreen) {
		this.field_2519 = customizeFlatLevelScreen;
	}

	@Override
	protected void onInitialized() {
		this.client.keyboard.enableRepeatEvents(true);
		this.field_2522 = I18n.translate("createWorld.customize.presets.title");
		this.field_2520 = I18n.translate("createWorld.customize.presets.share");
		this.field_2524 = I18n.translate("createWorld.customize.presets.list");
		this.field_2523 = new TextFieldWidget(2, this.fontRenderer, 50, 40, this.width - 100, 20);
		this.field_2521 = new NewLevelPresetsScreen.class_432();
		this.listeners.add(this.field_2521);
		this.field_2523.setMaxLength(1230);
		this.field_2523.setText(this.field_2519.method_2138());
		this.listeners.add(this.field_2523);
		this.field_2525 = this.addButton(
			new ButtonWidget(0, this.width / 2 - 155, this.height - 28, 150, 20, I18n.translate("createWorld.customize.presets.select")) {
				@Override
				public void onPressed(double d, double e) {
					NewLevelPresetsScreen.this.field_2519.method_2139(NewLevelPresetsScreen.this.field_2523.getText());
					NewLevelPresetsScreen.this.client.openScreen(NewLevelPresetsScreen.this.field_2519);
				}
			}
		);
		this.addButton(new ButtonWidget(1, this.width / 2 + 5, this.height - 28, 150, 20, I18n.translate("gui.cancel")) {
			@Override
			public void onPressed(double d, double e) {
				NewLevelPresetsScreen.this.client.openScreen(NewLevelPresetsScreen.this.field_2519);
			}
		});
		this.method_2191();
		this.setFocused(this.field_2521);
	}

	@Override
	public boolean mouseScrolled(double d) {
		return this.field_2521.mouseScrolled(d);
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
		this.drawStringCentered(this.fontRenderer, this.field_2522, this.width / 2, 8, 16777215);
		this.drawString(this.fontRenderer, this.field_2520, 50, 30, 10526880);
		this.drawString(this.fontRenderer, this.field_2524, 50, 70, 10526880);
		this.field_2523.render(i, j, f);
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
		return this.field_2521.field_2531 > -1 && this.field_2521.field_2531 < field_2518.size() || this.field_2523.getText().length() > 1;
	}

	private static void method_2195(String string, ItemProvider itemProvider, Biome biome, List<String> list, FlatChunkGeneratorLayer... flatChunkGeneratorLayers) {
		FlatChunkGeneratorConfig flatChunkGeneratorConfig = ChunkGeneratorType.field_12766.createSettings();

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
			Biomes.biome,
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
			Biomes.biome,
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
			Biomes.biome,
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
	class class_432 extends AbstractListWidget {
		public int field_2531 = -1;

		public class_432() {
			super(NewLevelPresetsScreen.this.client, NewLevelPresetsScreen.this.width, NewLevelPresetsScreen.this.height, 80, NewLevelPresetsScreen.this.height - 37, 24);
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
			this.method_2199(i, j, 0, 0);
		}

		private void method_2199(int i, int j, int k, int l) {
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.client.getTextureManager().bindTexture(Drawable.STAT_ICONS);
			float f = 0.0078125F;
			float g = 0.0078125F;
			int m = 18;
			int n = 18;
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
			bufferBuilder.begin(7, VertexFormats.POSITION_UV);
			bufferBuilder.vertex((double)(i + 0), (double)(j + 18), (double)this.zOffset)
				.texture((double)((float)(k + 0) * 0.0078125F), (double)((float)(l + 18) * 0.0078125F))
				.next();
			bufferBuilder.vertex((double)(i + 18), (double)(j + 18), (double)this.zOffset)
				.texture((double)((float)(k + 18) * 0.0078125F), (double)((float)(l + 18) * 0.0078125F))
				.next();
			bufferBuilder.vertex((double)(i + 18), (double)(j + 0), (double)this.zOffset)
				.texture((double)((float)(k + 18) * 0.0078125F), (double)((float)(l + 0) * 0.0078125F))
				.next();
			bufferBuilder.vertex((double)(i + 0), (double)(j + 0), (double)this.zOffset)
				.texture((double)((float)(k + 0) * 0.0078125F), (double)((float)(l + 0) * 0.0078125F))
				.next();
			tessellator.draw();
		}

		@Override
		protected int getEntryCount() {
			return NewLevelPresetsScreen.field_2518.size();
		}

		@Override
		protected boolean selectEntry(int i, int j, double d, double e) {
			this.field_2531 = i;
			NewLevelPresetsScreen.this.method_2191();
			NewLevelPresetsScreen.this.field_2523
				.setText(((NewLevelPresetsScreen.class_431)NewLevelPresetsScreen.field_2518.get(NewLevelPresetsScreen.this.field_2521.field_2531)).field_2526);
			NewLevelPresetsScreen.this.field_2523.method_1870();
			return true;
		}

		@Override
		protected boolean isSelectedEntry(int i) {
			return i == this.field_2531;
		}

		@Override
		protected void drawBackground() {
		}

		@Override
		protected void drawEntry(int i, int j, int k, int l, int m, int n, float f) {
			NewLevelPresetsScreen.class_431 lv = (NewLevelPresetsScreen.class_431)NewLevelPresetsScreen.field_2518.get(i);
			this.method_2200(j, k, lv.field_2527);
			NewLevelPresetsScreen.this.fontRenderer.draw(lv.field_2528, (float)(j + 18 + 5), (float)(k + 6), 16777215);
		}
	}
}
