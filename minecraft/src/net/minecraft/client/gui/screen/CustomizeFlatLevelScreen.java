package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorLayer;
import net.minecraft.world.level.LevelGeneratorOptions;
import net.minecraft.world.level.LevelGeneratorType;

@Environment(EnvType.CLIENT)
public class CustomizeFlatLevelScreen extends Screen {
	private final CreateWorldScreen parent;
	private FlatChunkGeneratorConfig config = FlatChunkGeneratorConfig.getDefaultConfig();
	private Text tileText;
	private Text heightText;
	private CustomizeFlatLevelScreen.SuperflatLayersListWidget layers;
	private ButtonWidget widgetButtonRemoveLayer;

	public CustomizeFlatLevelScreen(CreateWorldScreen parent, LevelGeneratorOptions levelGeneratorOptions) {
		super(new TranslatableText("createWorld.customize.flat.title"));
		this.parent = parent;
		if (levelGeneratorOptions.getType() == LevelGeneratorType.FLAT) {
			this.config = FlatChunkGeneratorConfig.fromDynamic(levelGeneratorOptions.getDynamic());
		}
	}

	public String getConfigString() {
		return this.config.toString();
	}

	public void setConfigString(String config) {
		this.config = FlatChunkGeneratorConfig.fromString(config);
	}

	@Override
	protected void init() {
		this.tileText = new TranslatableText("createWorld.customize.flat.tile");
		this.heightText = new TranslatableText("createWorld.customize.flat.height");
		this.layers = new CustomizeFlatLevelScreen.SuperflatLayersListWidget();
		this.children.add(this.layers);
		this.widgetButtonRemoveLayer = this.addButton(
			new ButtonWidget(
				this.width / 2 - 155,
				this.height - 52,
				150,
				20,
				new TranslatableText("createWorld.customize.flat.removeLayer"),
				buttonWidget -> {
					if (this.method_2147()) {
						List<FlatChunkGeneratorLayer> list = this.config.getLayers();
						int i = this.layers.children().indexOf(this.layers.getSelected());
						int j = list.size() - i - 1;
						list.remove(j);
						this.layers
							.setSelected(
								list.isEmpty() ? null : (CustomizeFlatLevelScreen.SuperflatLayersListWidget.SuperflatLayerItem)this.layers.children().get(Math.min(i, list.size() - 1))
							);
						this.config.updateLayerBlocks();
						this.method_2145();
					}
				}
			)
		);
		this.addButton(new ButtonWidget(this.width / 2 + 5, this.height - 52, 150, 20, new TranslatableText("createWorld.customize.presets"), buttonWidget -> {
			this.client.openScreen(new PresetsScreen(this));
			this.config.updateLayerBlocks();
			this.method_2145();
		}));
		this.addButton(new ButtonWidget(this.width / 2 - 155, this.height - 28, 150, 20, ScreenTexts.DONE, buttonWidget -> {
			this.parent.generatorOptions = LevelGeneratorType.FLAT.loadOptions(this.config.toDynamic(NbtOps.INSTANCE));
			this.client.openScreen(this.parent);
			this.config.updateLayerBlocks();
			this.method_2145();
		}));
		this.addButton(new ButtonWidget(this.width / 2 + 5, this.height - 28, 150, 20, ScreenTexts.CANCEL, buttonWidget -> {
			this.client.openScreen(this.parent);
			this.config.updateLayerBlocks();
			this.method_2145();
		}));
		this.config.updateLayerBlocks();
		this.method_2145();
	}

	public void method_2145() {
		this.widgetButtonRemoveLayer.active = this.method_2147();
		this.layers.method_19372();
	}

	private boolean method_2147() {
		return this.layers.getSelected() != null;
	}

	@Override
	public void onClose() {
		this.client.openScreen(this.parent);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		this.layers.render(matrices, mouseX, mouseY, delta);
		this.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 16777215);
		int i = this.width / 2 - 92 - 16;
		this.drawTextWithShadow(matrices, this.textRenderer, this.tileText, i, 32, 16777215);
		this.drawTextWithShadow(matrices, this.textRenderer, this.heightText, i + 2 + 213 - this.textRenderer.getWidth(this.heightText), 32, 16777215);
		super.render(matrices, mouseX, mouseY, delta);
	}

	@Environment(EnvType.CLIENT)
	class SuperflatLayersListWidget extends AlwaysSelectedEntryListWidget<CustomizeFlatLevelScreen.SuperflatLayersListWidget.SuperflatLayerItem> {
		public SuperflatLayersListWidget() {
			super(
				CustomizeFlatLevelScreen.this.client,
				CustomizeFlatLevelScreen.this.width,
				CustomizeFlatLevelScreen.this.height,
				43,
				CustomizeFlatLevelScreen.this.height - 60,
				24
			);

			for (int i = 0; i < CustomizeFlatLevelScreen.this.config.getLayers().size(); i++) {
				this.addEntry(new CustomizeFlatLevelScreen.SuperflatLayersListWidget.SuperflatLayerItem());
			}
		}

		public void setSelected(@Nullable CustomizeFlatLevelScreen.SuperflatLayersListWidget.SuperflatLayerItem superflatLayerItem) {
			super.setSelected(superflatLayerItem);
			if (superflatLayerItem != null) {
				FlatChunkGeneratorLayer flatChunkGeneratorLayer = (FlatChunkGeneratorLayer)CustomizeFlatLevelScreen.this.config
					.getLayers()
					.get(CustomizeFlatLevelScreen.this.config.getLayers().size() - this.children().indexOf(superflatLayerItem) - 1);
				Item item = flatChunkGeneratorLayer.getBlockState().getBlock().asItem();
				if (item != Items.AIR) {
					NarratorManager.INSTANCE.narrate(new TranslatableText("narrator.select", item.getName(new ItemStack(item))).getString());
				}
			}
		}

		@Override
		protected void moveSelection(int amount) {
			super.moveSelection(amount);
			CustomizeFlatLevelScreen.this.method_2145();
		}

		@Override
		protected boolean isFocused() {
			return CustomizeFlatLevelScreen.this.getFocused() == this;
		}

		@Override
		protected int getScrollbarPositionX() {
			return this.width - 70;
		}

		public void method_19372() {
			int i = this.children().indexOf(this.getSelected());
			this.clearEntries();

			for (int j = 0; j < CustomizeFlatLevelScreen.this.config.getLayers().size(); j++) {
				this.addEntry(new CustomizeFlatLevelScreen.SuperflatLayersListWidget.SuperflatLayerItem());
			}

			List<CustomizeFlatLevelScreen.SuperflatLayersListWidget.SuperflatLayerItem> list = this.children();
			if (i >= 0 && i < list.size()) {
				this.setSelected((CustomizeFlatLevelScreen.SuperflatLayersListWidget.SuperflatLayerItem)list.get(i));
			}
		}

		@Environment(EnvType.CLIENT)
		class SuperflatLayerItem extends AlwaysSelectedEntryListWidget.Entry<CustomizeFlatLevelScreen.SuperflatLayersListWidget.SuperflatLayerItem> {
			private SuperflatLayerItem() {
			}

			@Override
			public void render(MatrixStack matrices, int x, int y, int width, int height, int mouseX, int mouseY, int i, boolean bl, float tickDelta) {
				FlatChunkGeneratorLayer flatChunkGeneratorLayer = (FlatChunkGeneratorLayer)CustomizeFlatLevelScreen.this.config
					.getLayers()
					.get(CustomizeFlatLevelScreen.this.config.getLayers().size() - x - 1);
				BlockState blockState = flatChunkGeneratorLayer.getBlockState();
				Item item = blockState.getBlock().asItem();
				if (item == Items.AIR) {
					if (blockState.isOf(Blocks.WATER)) {
						item = Items.WATER_BUCKET;
					} else if (blockState.isOf(Blocks.LAVA)) {
						item = Items.LAVA_BUCKET;
					}
				}

				ItemStack itemStack = new ItemStack(item);
				this.method_19375(matrices, width, y, itemStack);
				CustomizeFlatLevelScreen.this.textRenderer.draw(matrices, item.getName(itemStack), (float)(width + 18 + 5), (float)(y + 3), 16777215);
				String string;
				if (x == 0) {
					string = I18n.translate("createWorld.customize.flat.layer.top", flatChunkGeneratorLayer.getThickness());
				} else if (x == CustomizeFlatLevelScreen.this.config.getLayers().size() - 1) {
					string = I18n.translate("createWorld.customize.flat.layer.bottom", flatChunkGeneratorLayer.getThickness());
				} else {
					string = I18n.translate("createWorld.customize.flat.layer", flatChunkGeneratorLayer.getThickness());
				}

				CustomizeFlatLevelScreen.this.textRenderer
					.draw(matrices, string, (float)(width + 2 + 213 - CustomizeFlatLevelScreen.this.textRenderer.getWidth(string)), (float)(y + 3), 16777215);
			}

			@Override
			public boolean mouseClicked(double mouseX, double mouseY, int button) {
				if (button == 0) {
					SuperflatLayersListWidget.this.setSelected(this);
					CustomizeFlatLevelScreen.this.method_2145();
					return true;
				} else {
					return false;
				}
			}

			private void method_19375(MatrixStack matrixStack, int i, int j, ItemStack itemStack) {
				this.method_19373(matrixStack, i + 1, j + 1);
				RenderSystem.enableRescaleNormal();
				if (!itemStack.isEmpty()) {
					CustomizeFlatLevelScreen.this.itemRenderer.renderGuiItemIcon(itemStack, i + 2, j + 2);
				}

				RenderSystem.disableRescaleNormal();
			}

			private void method_19373(MatrixStack matrixStack, int i, int j) {
				RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				SuperflatLayersListWidget.this.client.getTextureManager().bindTexture(DrawableHelper.STATS_ICON_TEXTURE);
				DrawableHelper.drawTexture(matrixStack, i, j, CustomizeFlatLevelScreen.this.getZOffset(), 0.0F, 0.0F, 18, 18, 128, 128);
			}
		}
	}
}
