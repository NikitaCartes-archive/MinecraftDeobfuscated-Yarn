package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.datafixers.NbtOps;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorLayer;

@Environment(EnvType.CLIENT)
public class CustomizeFlatLevelScreen extends Screen {
	private final CreateWorldScreen parent;
	private FlatChunkGeneratorConfig config = FlatChunkGeneratorConfig.getDefaultConfig();
	private String tileText;
	private String heightText;
	private CustomizeFlatLevelScreen.SuperflatLayersListWidget layers;
	private ButtonWidget widgetButtonRemoveLayer;

	public CustomizeFlatLevelScreen(CreateWorldScreen parent, CompoundTag generatorOptions) {
		super(new TranslatableText("createWorld.customize.flat.title"));
		this.parent = parent;
		this.setConfigTag(generatorOptions);
	}

	public String getConfigString() {
		return this.config.toString();
	}

	public CompoundTag getConfigTag() {
		return (CompoundTag)this.config.toDynamic(NbtOps.INSTANCE).getValue();
	}

	public void setConfigString(String config) {
		this.config = FlatChunkGeneratorConfig.fromString(config);
	}

	public void setConfigTag(CompoundTag config) {
		this.config = FlatChunkGeneratorConfig.fromDynamic(new Dynamic<>(NbtOps.INSTANCE, config));
	}

	@Override
	protected void init() {
		this.tileText = I18n.translate("createWorld.customize.flat.tile");
		this.heightText = I18n.translate("createWorld.customize.flat.height");
		this.layers = new CustomizeFlatLevelScreen.SuperflatLayersListWidget();
		this.children.add(this.layers);
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
		this.addButton(new ButtonWidget(this.width / 2 + 5, this.height - 52, 150, 20, I18n.translate("createWorld.customize.presets"), buttonWidget -> {
			this.minecraft.openScreen(new PresetsScreen(this));
			this.config.updateLayerBlocks();
			this.method_2145();
		}));
		this.addButton(new ButtonWidget(this.width / 2 - 155, this.height - 28, 150, 20, I18n.translate("gui.done"), buttonWidget -> {
			this.parent.generatorOptionsTag = this.getConfigTag();
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
		this.layers.method_19372();
	}

	private boolean method_2147() {
		return this.layers.getSelected() != null;
	}

	@Override
	public void onClose() {
		this.minecraft.openScreen(this.parent);
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		this.renderBackground();
		this.layers.render(mouseX, mouseY, delta);
		this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, 8, 16777215);
		int i = this.width / 2 - 92 - 16;
		this.drawString(this.font, this.tileText, i, 32, 16777215);
		this.drawString(this.font, this.heightText, i + 2 + 213 - this.font.getStringWidth(this.heightText), 32, 16777215);
		super.render(mouseX, mouseY, delta);
	}

	@Environment(EnvType.CLIENT)
	class SuperflatLayersListWidget extends AlwaysSelectedEntryListWidget<CustomizeFlatLevelScreen.SuperflatLayersListWidget.SuperflatLayerItem> {
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
			public void render(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
				FlatChunkGeneratorLayer flatChunkGeneratorLayer = (FlatChunkGeneratorLayer)CustomizeFlatLevelScreen.this.config
					.getLayers()
					.get(CustomizeFlatLevelScreen.this.config.getLayers().size() - i - 1);
				BlockState blockState = flatChunkGeneratorLayer.getBlockState();
				Block block = blockState.getBlock();
				Item item = block.asItem();
				if (item == Items.AIR) {
					if (block == Blocks.WATER) {
						item = Items.WATER_BUCKET;
					} else if (block == Blocks.LAVA) {
						item = Items.LAVA_BUCKET;
					}
				}

				ItemStack itemStack = new ItemStack(item);
				String string = item.getName(itemStack).asFormattedString();
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
			public boolean mouseClicked(double mouseX, double mouseY, int button) {
				if (button == 0) {
					SuperflatLayersListWidget.this.setSelected(this);
					CustomizeFlatLevelScreen.this.method_2145();
					return true;
				} else {
					return false;
				}
			}

			private void method_19375(int i, int j, ItemStack itemStack) {
				this.method_19373(i + 1, j + 1);
				RenderSystem.enableRescaleNormal();
				if (!itemStack.isEmpty()) {
					CustomizeFlatLevelScreen.this.itemRenderer.renderGuiItemIcon(itemStack, i + 2, j + 2);
				}

				RenderSystem.disableRescaleNormal();
			}

			private void method_19373(int i, int j) {
				RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				SuperflatLayersListWidget.this.minecraft.getTextureManager().bindTexture(DrawableHelper.STATS_ICON_LOCATION);
				DrawableHelper.blit(i, j, CustomizeFlatLevelScreen.this.getBlitOffset(), 0.0F, 0.0F, 18, 18, 128, 128);
			}
		}
	}
}
