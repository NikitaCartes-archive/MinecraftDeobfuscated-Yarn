package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
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
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
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
	private Text shareText;
	private Text listText;
	private PresetsScreen.SuperflatPresetsListWidget listWidget;
	private ButtonWidget selectPresetButton;
	private TextFieldWidget customPresetField;

	public PresetsScreen(CustomizeFlatLevelScreen parent) {
		super(new TranslatableText("createWorld.customize.presets.title"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		this.client.keyboard.enableRepeatEvents(true);
		this.shareText = new TranslatableText("createWorld.customize.presets.share");
		this.listText = new TranslatableText("createWorld.customize.presets.list");
		this.customPresetField = new TextFieldWidget(this.textRenderer, 50, 40, this.width - 100, 20, this.shareText);
		this.customPresetField.setMaxLength(1230);
		this.customPresetField.setText(this.parent.getConfigString());
		this.children.add(this.customPresetField);
		this.listWidget = new PresetsScreen.SuperflatPresetsListWidget();
		this.children.add(this.listWidget);
		this.selectPresetButton = this.addButton(
			new ButtonWidget(this.width / 2 - 155, this.height - 28, 150, 20, new TranslatableText("createWorld.customize.presets.select"), buttonWidget -> {
				this.parent.setConfigString(this.customPresetField.getText());
				this.client.openScreen(this.parent);
			})
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
		this.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 16777215);
		this.drawTextWithShadow(matrices, this.textRenderer, this.shareText, 50, 30, 10526880);
		this.drawTextWithShadow(matrices, this.textRenderer, this.listText, 50, 70, 10526880);
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

	private static void addPreset(Text text, ItemConvertible icon, Biome biome, List<String> structures, FlatChunkGeneratorLayer... layers) {
		FlatChunkGeneratorConfig flatChunkGeneratorConfig = ChunkGeneratorType.FLAT.createConfig();

		for (int i = layers.length - 1; i >= 0; i--) {
			flatChunkGeneratorConfig.getLayers().add(layers[i]);
		}

		flatChunkGeneratorConfig.setBiome(biome);
		flatChunkGeneratorConfig.updateLayerBlocks();

		for (String string : structures) {
			flatChunkGeneratorConfig.getStructures().put(string, Maps.newHashMap());
		}

		presets.add(new PresetsScreen.SuperflatPreset(icon.asItem(), text, flatChunkGeneratorConfig.toString()));
	}

	static {
		addPreset(
			new TranslatableText("createWorld.customize.preset.classic_flat"),
			Blocks.GRASS_BLOCK,
			Biomes.PLAINS,
			Arrays.asList("village"),
			new FlatChunkGeneratorLayer(1, Blocks.GRASS_BLOCK),
			new FlatChunkGeneratorLayer(2, Blocks.DIRT),
			new FlatChunkGeneratorLayer(1, Blocks.BEDROCK)
		);
		addPreset(
			new TranslatableText("createWorld.customize.preset.tunnelers_dream"),
			Blocks.STONE,
			Biomes.MOUNTAINS,
			Arrays.asList("biome_1", "dungeon", "decoration", "stronghold", "mineshaft"),
			new FlatChunkGeneratorLayer(1, Blocks.GRASS_BLOCK),
			new FlatChunkGeneratorLayer(5, Blocks.DIRT),
			new FlatChunkGeneratorLayer(230, Blocks.STONE),
			new FlatChunkGeneratorLayer(1, Blocks.BEDROCK)
		);
		addPreset(
			new TranslatableText("createWorld.customize.preset.water_world"),
			Items.WATER_BUCKET,
			Biomes.DEEP_OCEAN,
			Arrays.asList("biome_1", "oceanmonument"),
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
			Arrays.asList("village", "biome_1", "decoration", "stronghold", "mineshaft", "dungeon", "lake", "lava_lake", "pillager_outpost", "ruined_portal"),
			new FlatChunkGeneratorLayer(1, Blocks.GRASS_BLOCK),
			new FlatChunkGeneratorLayer(3, Blocks.DIRT),
			new FlatChunkGeneratorLayer(59, Blocks.STONE),
			new FlatChunkGeneratorLayer(1, Blocks.BEDROCK)
		);
		addPreset(
			new TranslatableText("createWorld.customize.preset.snowy_kingdom"),
			Blocks.SNOW,
			Biomes.SNOWY_TUNDRA,
			Arrays.asList("village", "biome_1"),
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
			Arrays.asList("village", "biome_1"),
			new FlatChunkGeneratorLayer(1, Blocks.GRASS_BLOCK),
			new FlatChunkGeneratorLayer(3, Blocks.DIRT),
			new FlatChunkGeneratorLayer(2, Blocks.COBBLESTONE)
		);
		addPreset(
			new TranslatableText("createWorld.customize.preset.desert"),
			Blocks.SAND,
			Biomes.DESERT,
			Arrays.asList("village", "biome_1", "decoration", "stronghold", "mineshaft", "dungeon"),
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
			new FlatChunkGeneratorLayer(52, Blocks.SANDSTONE),
			new FlatChunkGeneratorLayer(3, Blocks.STONE),
			new FlatChunkGeneratorLayer(1, Blocks.BEDROCK)
		);
		addPreset(
			new TranslatableText("createWorld.customize.preset.the_void"),
			Blocks.BARRIER,
			Biomes.THE_VOID,
			Arrays.asList("decoration"),
			new FlatChunkGeneratorLayer(1, Blocks.AIR)
		);
	}

	@Environment(EnvType.CLIENT)
	static class SuperflatPreset {
		public final Item icon;
		public final Text name;
		public final String config;

		public SuperflatPreset(Item icon, Text text, String config) {
			this.icon = icon;
			this.name = text;
			this.config = config;
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
		}

		@Override
		protected void moveSelection(int amount) {
			super.moveSelection(amount);
			PresetsScreen.this.updateSelectButton(true);
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
			public void render(MatrixStack matrices, int x, int y, int width, int height, int mouseX, int mouseY, int i, boolean bl, float tickDelta) {
				PresetsScreen.SuperflatPreset superflatPreset = (PresetsScreen.SuperflatPreset)PresetsScreen.presets.get(x);
				this.method_2200(matrices, width, y, superflatPreset.icon);
				PresetsScreen.this.textRenderer.draw(matrices, superflatPreset.name, (float)(width + 18 + 5), (float)(y + 6), 16777215);
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
				PresetsScreen.this.updateSelectButton(true);
				PresetsScreen.this.customPresetField
					.setText(((PresetsScreen.SuperflatPreset)PresetsScreen.presets.get(SuperflatPresetsListWidget.this.children().indexOf(this))).config);
				PresetsScreen.this.customPresetField.setCursorToStart();
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
