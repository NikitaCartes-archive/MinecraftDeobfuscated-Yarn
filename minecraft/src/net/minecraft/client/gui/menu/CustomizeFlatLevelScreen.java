package net.minecraft.client.gui.menu;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.datafixers.NbtOps;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorLayer;

@Environment(EnvType.CLIENT)
public class CustomizeFlatLevelScreen extends Screen {
	private final NewLevelScreen parent;
	private FlatChunkGeneratorConfig config = FlatChunkGeneratorConfig.getDefaultConfig();
	private String tileText;
	private String heightText;
	private CustomizeFlatLevelScreen.SuperflatLayersListWidget field_2424;
	private ButtonWidget widgetButtonRemoveLayer;

	public CustomizeFlatLevelScreen(NewLevelScreen newLevelScreen, CompoundTag compoundTag) {
		super(new TranslatableTextComponent("createWorld.customize.flat.title"));
		this.parent = newLevelScreen;
		this.method_2144(compoundTag);
	}

	public String getConfigString() {
		return this.config.toString();
	}

	public CompoundTag method_2140() {
		return (CompoundTag)this.config.toDynamic(NbtOps.INSTANCE).getValue();
	}

	public void method_2139(String string) {
		this.config = FlatChunkGeneratorConfig.fromString(string);
	}

	public void method_2144(CompoundTag compoundTag) {
		this.config = FlatChunkGeneratorConfig.fromDynamic(new Dynamic<>(NbtOps.INSTANCE, compoundTag));
	}

	@Override
	protected void init() {
		this.tileText = I18n.translate("createWorld.customize.flat.tile");
		this.heightText = I18n.translate("createWorld.customize.flat.height");
		this.field_2424 = new CustomizeFlatLevelScreen.SuperflatLayersListWidget();
		this.children.add(this.field_2424);
		this.widgetButtonRemoveLayer = this.addButton(
			new ButtonWidget(
				this.width / 2 - 155,
				this.height - 52,
				150,
				20,
				I18n.translate("createWorld.customize.flat.removeLayer"),
				buttonWidget -> {
					if (this.method_2147()) {
						List<FlatChunkGeneratorLayer> list = this.config.getLayers();
						int i = this.field_2424.children().indexOf(this.field_2424.getSelectedItem());
						int j = list.size() - i - 1;
						list.remove(j);
						this.field_2424
							.method_20094(
								list.isEmpty()
									? null
									: (CustomizeFlatLevelScreen.SuperflatLayersListWidget.SuperflatLayerItem)this.field_2424.children().get(Math.min(i, list.size() - 1))
							);
						this.config.updateLayerBlocks();
						this.method_2145();
					}
				}
			)
		);
		this.addButton(new ButtonWidget(this.width / 2 + 5, this.height - 52, 150, 20, I18n.translate("createWorld.customize.presets"), buttonWidget -> {
			this.minecraft.openScreen(new NewLevelPresetsScreen(this));
			this.config.updateLayerBlocks();
			this.method_2145();
		}));
		this.addButton(new ButtonWidget(this.width / 2 - 155, this.height - 28, 150, 20, I18n.translate("gui.done"), buttonWidget -> {
			this.parent.field_18979 = this.method_2140();
			this.minecraft.openScreen(this.parent);
			this.config.updateLayerBlocks();
			this.method_2145();
		}));
		this.addButton(new ButtonWidget(this.width / 2 + 5, this.height - 28, 150, 20, I18n.translate("gui.cancel"), buttonWidget -> {
			this.minecraft.openScreen(this.parent);
			this.config.updateLayerBlocks();
			this.method_2145();
		}));
		this.config.updateLayerBlocks();
		this.method_2145();
	}

	public void method_2145() {
		this.widgetButtonRemoveLayer.active = this.method_2147();
		this.field_2424.method_19372();
	}

	private boolean method_2147() {
		return this.field_2424.getSelectedItem() != null;
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		this.field_2424.render(i, j, f);
		this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 8, 16777215);
		int k = this.width / 2 - 92 - 16;
		this.drawString(this.font, this.tileText, k, 32, 16777215);
		this.drawString(this.font, this.heightText, k + 2 + 213 - this.font.getStringWidth(this.heightText), 32, 16777215);
		super.render(i, j, f);
	}

	@Environment(EnvType.CLIENT)
	class SuperflatLayersListWidget extends AlwaysSelectedItemListWidget<CustomizeFlatLevelScreen.SuperflatLayersListWidget.SuperflatLayerItem> {
		public SuperflatLayersListWidget() {
			super(
				CustomizeFlatLevelScreen.this.minecraft,
				CustomizeFlatLevelScreen.this.width,
				CustomizeFlatLevelScreen.this.height,
				43,
				CustomizeFlatLevelScreen.this.height - 60,
				24
			);

			for (int i = 0; i < CustomizeFlatLevelScreen.this.config.getLayers().size(); i++) {
				this.addItem(new CustomizeFlatLevelScreen.SuperflatLayersListWidget.SuperflatLayerItem());
			}
		}

		public void method_20094(@Nullable CustomizeFlatLevelScreen.SuperflatLayersListWidget.SuperflatLayerItem superflatLayerItem) {
			super.selectItem(superflatLayerItem);
			if (superflatLayerItem != null) {
				FlatChunkGeneratorLayer flatChunkGeneratorLayer = (FlatChunkGeneratorLayer)CustomizeFlatLevelScreen.this.config
					.getLayers()
					.get(CustomizeFlatLevelScreen.this.config.getLayers().size() - this.children().indexOf(superflatLayerItem) - 1);
				net.minecraft.item.Item item = flatChunkGeneratorLayer.getBlockState().getBlock().getItem();
				if (item != net.minecraft.item.Items.AIR) {
					NarratorManager.INSTANCE.method_19788(new TranslatableTextComponent("narrator.select", item.getTranslatedNameTrimmed(new ItemStack(item))).getString());
				}
			}
		}

		@Override
		protected void moveSelection(int i) {
			super.moveSelection(i);
			CustomizeFlatLevelScreen.this.method_2145();
		}

		@Override
		protected boolean isFocused() {
			return CustomizeFlatLevelScreen.this.getFocused() == this;
		}

		@Override
		protected int getScrollbarPosition() {
			return this.width - 70;
		}

		public void method_19372() {
			int i = this.children().indexOf(this.getSelectedItem());
			this.clearItems();

			for (int j = 0; j < CustomizeFlatLevelScreen.this.config.getLayers().size(); j++) {
				this.addItem(new CustomizeFlatLevelScreen.SuperflatLayersListWidget.SuperflatLayerItem());
			}

			List<CustomizeFlatLevelScreen.SuperflatLayersListWidget.SuperflatLayerItem> list = this.children();
			if (i >= 0 && i < list.size()) {
				this.method_20094((CustomizeFlatLevelScreen.SuperflatLayersListWidget.SuperflatLayerItem)list.get(i));
			}
		}

		@Environment(EnvType.CLIENT)
		class SuperflatLayerItem extends AlwaysSelectedItemListWidget.class_4281<CustomizeFlatLevelScreen.SuperflatLayersListWidget.SuperflatLayerItem> {
			private SuperflatLayerItem() {
			}

			@Override
			public void render(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
				FlatChunkGeneratorLayer flatChunkGeneratorLayer = (FlatChunkGeneratorLayer)CustomizeFlatLevelScreen.this.config
					.getLayers()
					.get(CustomizeFlatLevelScreen.this.config.getLayers().size() - i - 1);
				BlockState blockState = flatChunkGeneratorLayer.getBlockState();
				Block block = blockState.getBlock();
				net.minecraft.item.Item item = block.getItem();
				if (item == net.minecraft.item.Items.AIR) {
					if (block == Blocks.field_10382) {
						item = net.minecraft.item.Items.field_8705;
					} else if (block == Blocks.field_10164) {
						item = net.minecraft.item.Items.field_8187;
					}
				}

				ItemStack itemStack = new ItemStack(item);
				String string = item.getTranslatedNameTrimmed(itemStack).getFormattedText();
				this.method_19375(k, j, itemStack);
				CustomizeFlatLevelScreen.this.font.draw(string, (float)(k + 18 + 5), (float)(j + 3), 16777215);
				String string2;
				if (i == 0) {
					string2 = I18n.translate("createWorld.customize.flat.layer.top", flatChunkGeneratorLayer.getThickness());
				} else if (i == CustomizeFlatLevelScreen.this.config.getLayers().size() - 1) {
					string2 = I18n.translate("createWorld.customize.flat.layer.bottom", flatChunkGeneratorLayer.getThickness());
				} else {
					string2 = I18n.translate("createWorld.customize.flat.layer", flatChunkGeneratorLayer.getThickness());
				}

				CustomizeFlatLevelScreen.this.font
					.draw(string2, (float)(k + 2 + 213 - CustomizeFlatLevelScreen.this.font.getStringWidth(string2)), (float)(j + 3), 16777215);
			}

			@Override
			public boolean mouseClicked(double d, double e, int i) {
				if (i == 0) {
					SuperflatLayersListWidget.this.method_20094(this);
					CustomizeFlatLevelScreen.this.method_2145();
					return true;
				} else {
					return false;
				}
			}

			private void method_19375(int i, int j, ItemStack itemStack) {
				this.method_19373(i + 1, j + 1);
				GlStateManager.enableRescaleNormal();
				if (!itemStack.isEmpty()) {
					GuiLighting.enableForItems();
					CustomizeFlatLevelScreen.this.itemRenderer.renderGuiItemIcon(itemStack, i + 2, j + 2);
					GuiLighting.disable();
				}

				GlStateManager.disableRescaleNormal();
			}

			private void method_19373(int i, int j) {
				GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				SuperflatLayersListWidget.this.client.getTextureManager().bindTexture(DrawableHelper.STATS_ICON_LOCATION);
				DrawableHelper.blit(i, j, CustomizeFlatLevelScreen.this.blitOffset, 0.0F, 0.0F, 18, 18, 128, 128);
			}
		}
	}
}
