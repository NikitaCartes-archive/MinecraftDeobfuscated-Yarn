package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.function.Consumer;
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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorLayer;

@Environment(EnvType.CLIENT)
public class CustomizeFlatLevelScreen extends Screen {
	private static final int field_32239 = 128;
	private static final int field_32240 = 18;
	private static final int field_32241 = 20;
	private static final int field_32242 = 1;
	private static final int field_32243 = 1;
	private static final int field_32244 = 2;
	private static final int field_32245 = 2;
	protected final CreateWorldScreen parent;
	private final Consumer<FlatChunkGeneratorConfig> configConsumer;
	FlatChunkGeneratorConfig config;
	private Text tileText;
	private Text heightText;
	private CustomizeFlatLevelScreen.SuperflatLayersListWidget layers;
	private ButtonWidget widgetButtonRemoveLayer;

	public CustomizeFlatLevelScreen(CreateWorldScreen parent, Consumer<FlatChunkGeneratorConfig> configConsumer, FlatChunkGeneratorConfig config) {
		super(new TranslatableText("createWorld.customize.flat.title"));
		this.parent = parent;
		this.configConsumer = configConsumer;
		this.config = config;
	}

	public FlatChunkGeneratorConfig getConfig() {
		return this.config;
	}

	public void setConfig(FlatChunkGeneratorConfig config) {
		this.config = config;
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
					if (this.hasLayerSelected()) {
						List<FlatChunkGeneratorLayer> list = this.config.getLayers();
						int i = this.layers.children().indexOf(this.layers.getSelected());
						int j = list.size() - i - 1;
						list.remove(j);
						this.layers
							.setSelected(
								list.isEmpty()
									? null
									: (CustomizeFlatLevelScreen.SuperflatLayersListWidget.SuperflatLayerEntry)this.layers.children().get(Math.min(i, list.size() - 1))
							);
						this.config.updateLayerBlocks();
						this.layers.updateLayers();
						this.updateRemoveLayerButton();
					}
				}
			)
		);
		this.addButton(new ButtonWidget(this.width / 2 + 5, this.height - 52, 150, 20, new TranslatableText("createWorld.customize.presets"), buttonWidget -> {
			this.client.openScreen(new PresetsScreen(this));
			this.config.updateLayerBlocks();
			this.updateRemoveLayerButton();
		}));
		this.addButton(new ButtonWidget(this.width / 2 - 155, this.height - 28, 150, 20, ScreenTexts.DONE, buttonWidget -> {
			this.configConsumer.accept(this.config);
			this.client.openScreen(this.parent);
			this.config.updateLayerBlocks();
		}));
		this.addButton(new ButtonWidget(this.width / 2 + 5, this.height - 28, 150, 20, ScreenTexts.CANCEL, buttonWidget -> {
			this.client.openScreen(this.parent);
			this.config.updateLayerBlocks();
		}));
		this.config.updateLayerBlocks();
		this.updateRemoveLayerButton();
	}

	void updateRemoveLayerButton() {
		this.widgetButtonRemoveLayer.active = this.hasLayerSelected();
	}

	private boolean hasLayerSelected() {
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
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 16777215);
		int i = this.width / 2 - 92 - 16;
		drawTextWithShadow(matrices, this.textRenderer, this.tileText, i, 32, 16777215);
		drawTextWithShadow(matrices, this.textRenderer, this.heightText, i + 2 + 213 - this.textRenderer.getWidth(this.heightText), 32, 16777215);
		super.render(matrices, mouseX, mouseY, delta);
	}

	@Environment(EnvType.CLIENT)
	class SuperflatLayersListWidget extends AlwaysSelectedEntryListWidget<CustomizeFlatLevelScreen.SuperflatLayersListWidget.SuperflatLayerEntry> {
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
				this.addEntry(new CustomizeFlatLevelScreen.SuperflatLayersListWidget.SuperflatLayerEntry());
			}
		}

		public void setSelected(@Nullable CustomizeFlatLevelScreen.SuperflatLayersListWidget.SuperflatLayerEntry superflatLayerEntry) {
			super.setSelected(superflatLayerEntry);
			if (superflatLayerEntry != null) {
				FlatChunkGeneratorLayer flatChunkGeneratorLayer = (FlatChunkGeneratorLayer)CustomizeFlatLevelScreen.this.config
					.getLayers()
					.get(CustomizeFlatLevelScreen.this.config.getLayers().size() - this.children().indexOf(superflatLayerEntry) - 1);
				ItemStack itemStack = new ItemStack(flatChunkGeneratorLayer.getBlockState().getBlock());
				if (!itemStack.isEmpty()) {
					NarratorManager.INSTANCE.narrate(new TranslatableText("narrator.select", itemStack.getItem().getName(itemStack)).getString());
				}
			}

			CustomizeFlatLevelScreen.this.updateRemoveLayerButton();
		}

		@Override
		protected boolean isFocused() {
			return CustomizeFlatLevelScreen.this.getFocused() == this;
		}

		@Override
		protected int getScrollbarPositionX() {
			return this.width - 70;
		}

		public void updateLayers() {
			int i = this.children().indexOf(this.getSelected());
			this.clearEntries();

			for (int j = 0; j < CustomizeFlatLevelScreen.this.config.getLayers().size(); j++) {
				this.addEntry(new CustomizeFlatLevelScreen.SuperflatLayersListWidget.SuperflatLayerEntry());
			}

			List<CustomizeFlatLevelScreen.SuperflatLayersListWidget.SuperflatLayerEntry> list = this.children();
			if (i >= 0 && i < list.size()) {
				this.setSelected((CustomizeFlatLevelScreen.SuperflatLayersListWidget.SuperflatLayerEntry)list.get(i));
			}
		}

		@Environment(EnvType.CLIENT)
		class SuperflatLayerEntry extends AlwaysSelectedEntryListWidget.Entry<CustomizeFlatLevelScreen.SuperflatLayersListWidget.SuperflatLayerEntry> {
			@Override
			public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
				FlatChunkGeneratorLayer flatChunkGeneratorLayer = (FlatChunkGeneratorLayer)CustomizeFlatLevelScreen.this.config
					.getLayers()
					.get(CustomizeFlatLevelScreen.this.config.getLayers().size() - index - 1);
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
				this.renderIcon(matrices, x, y, itemStack);
				CustomizeFlatLevelScreen.this.textRenderer.draw(matrices, item.getName(itemStack), (float)(x + 18 + 5), (float)(y + 3), 16777215);
				String string;
				if (index == 0) {
					string = I18n.translate("createWorld.customize.flat.layer.top", flatChunkGeneratorLayer.getThickness());
				} else if (index == CustomizeFlatLevelScreen.this.config.getLayers().size() - 1) {
					string = I18n.translate("createWorld.customize.flat.layer.bottom", flatChunkGeneratorLayer.getThickness());
				} else {
					string = I18n.translate("createWorld.customize.flat.layer", flatChunkGeneratorLayer.getThickness());
				}

				CustomizeFlatLevelScreen.this.textRenderer
					.draw(matrices, string, (float)(x + 2 + 213 - CustomizeFlatLevelScreen.this.textRenderer.getWidth(string)), (float)(y + 3), 16777215);
			}

			@Override
			public boolean mouseClicked(double mouseX, double mouseY, int button) {
				if (button == 0) {
					SuperflatLayersListWidget.this.setSelected(this);
					return true;
				} else {
					return false;
				}
			}

			private void renderIcon(MatrixStack matrices, int x, int y, ItemStack iconItem) {
				this.renderIconBackgroundTexture(matrices, x + 1, y + 1);
				if (!iconItem.isEmpty()) {
					CustomizeFlatLevelScreen.this.itemRenderer.renderGuiItemIcon(iconItem, x + 2, y + 2);
				}
			}

			private void renderIconBackgroundTexture(MatrixStack matrices, int x, int y) {
				RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
				RenderSystem.setShaderTexture(0, DrawableHelper.STATS_ICON_TEXTURE);
				DrawableHelper.drawTexture(matrices, x, y, CustomizeFlatLevelScreen.this.getZOffset(), 0.0F, 0.0F, 18, 18, 128, 128);
			}
		}
	}
}
