package net.minecraft.client.gui.menu;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.datafixers.NbtOps;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorLayer;

@Environment(EnvType.CLIENT)
public class CustomizeFlatLevelScreen extends Screen {
	private final NewLevelScreen parent;
	private FlatChunkGeneratorConfig config = FlatChunkGeneratorConfig.getDefaultConfig();
	private String titleText;
	private String tileText;
	private String heightText;
	private CustomizeFlatLevelScreen.class_4192 field_2424;
	private ButtonWidget widgetButtonRemoveLayer;
	public int widgetButtonAddLayer = -1;

	public CustomizeFlatLevelScreen(NewLevelScreen newLevelScreen, CompoundTag compoundTag) {
		this.parent = newLevelScreen;
		this.method_2144(compoundTag);
	}

	public String method_2138() {
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
	protected void onInitialized() {
		this.titleText = I18n.translate("createWorld.customize.flat.title");
		this.tileText = I18n.translate("createWorld.customize.flat.tile");
		this.heightText = I18n.translate("createWorld.customize.flat.height");
		this.field_2424 = new CustomizeFlatLevelScreen.class_4192();
		this.listeners.add(this.field_2424);
		this.widgetButtonRemoveLayer = this.addButton(
			new ButtonWidget(this.screenWidth / 2 - 155, this.screenHeight - 52, 150, 20, I18n.translate("createWorld.customize.flat.removeLayer")) {
				@Override
				public void onPressed() {
					if (CustomizeFlatLevelScreen.this.method_2147()) {
						List<FlatChunkGeneratorLayer> list = CustomizeFlatLevelScreen.this.config.getLayers();
						int i = list.size() - CustomizeFlatLevelScreen.this.widgetButtonAddLayer - 1;
						list.remove(i);
						CustomizeFlatLevelScreen.this.widgetButtonAddLayer = Math.min(CustomizeFlatLevelScreen.this.widgetButtonAddLayer, list.size() - 1);
						CustomizeFlatLevelScreen.this.config.updateLayerBlocks();
						CustomizeFlatLevelScreen.this.method_2145();
					}
				}
			}
		);
		this.addButton(new ButtonWidget(this.screenWidth / 2 + 5, this.screenHeight - 52, 150, 20, I18n.translate("createWorld.customize.presets")) {
			@Override
			public void onPressed() {
				CustomizeFlatLevelScreen.this.client.openScreen(new NewLevelPresetsScreen(CustomizeFlatLevelScreen.this));
				CustomizeFlatLevelScreen.this.config.updateLayerBlocks();
				CustomizeFlatLevelScreen.this.method_2145();
			}
		});
		this.addButton(new ButtonWidget(this.screenWidth / 2 - 155, this.screenHeight - 28, 150, 20, I18n.translate("gui.done")) {
			@Override
			public void onPressed() {
				CustomizeFlatLevelScreen.this.parent.field_3200 = CustomizeFlatLevelScreen.this.method_2140();
				CustomizeFlatLevelScreen.this.client.openScreen(CustomizeFlatLevelScreen.this.parent);
				CustomizeFlatLevelScreen.this.config.updateLayerBlocks();
				CustomizeFlatLevelScreen.this.method_2145();
			}
		});
		this.addButton(new ButtonWidget(this.screenWidth / 2 + 5, this.screenHeight - 28, 150, 20, I18n.translate("gui.cancel")) {
			@Override
			public void onPressed() {
				CustomizeFlatLevelScreen.this.client.openScreen(CustomizeFlatLevelScreen.this.parent);
				CustomizeFlatLevelScreen.this.config.updateLayerBlocks();
				CustomizeFlatLevelScreen.this.method_2145();
			}
		});
		this.config.updateLayerBlocks();
		this.method_2145();
	}

	public void method_2145() {
		this.widgetButtonRemoveLayer.enabled = this.method_2147();
		this.field_2424.method_19372();
	}

	private boolean method_2147() {
		return this.widgetButtonAddLayer > -1 && this.widgetButtonAddLayer < this.config.getLayers().size();
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawBackground();
		this.field_2424.draw(i, j, f);
		this.drawStringCentered(this.fontRenderer, this.titleText, this.screenWidth / 2, 8, 16777215);
		int k = this.screenWidth / 2 - 92 - 16;
		this.drawString(this.fontRenderer, this.tileText, k, 32, 16777215);
		this.drawString(this.fontRenderer, this.heightText, k + 2 + 213 - this.fontRenderer.getStringWidth(this.heightText), 32, 16777215);
		super.draw(i, j, f);
	}

	@Environment(EnvType.CLIENT)
	class class_4192 extends EntryListWidget<CustomizeFlatLevelScreen.class_4193> {
		public class_4192() {
			super(
				CustomizeFlatLevelScreen.this.client,
				CustomizeFlatLevelScreen.this.screenWidth,
				CustomizeFlatLevelScreen.this.screenHeight,
				43,
				CustomizeFlatLevelScreen.this.screenHeight - 60,
				24
			);

			for (int i = 0; i < CustomizeFlatLevelScreen.this.config.getLayers().size(); i++) {
				this.addEntry(CustomizeFlatLevelScreen.this.new class_4193());
			}
		}

		@Override
		protected boolean isSelectedEntry(int i) {
			return i == CustomizeFlatLevelScreen.this.widgetButtonAddLayer;
		}

		@Override
		protected void method_19351(int i) {
			CustomizeFlatLevelScreen.this.widgetButtonAddLayer = MathHelper.clamp(CustomizeFlatLevelScreen.this.widgetButtonAddLayer + i, 0, this.getEntryCount() - 1);
			if (CustomizeFlatLevelScreen.this.widgetButtonAddLayer > -1 && CustomizeFlatLevelScreen.this.widgetButtonAddLayer < this.getEntryCount()) {
				this.method_19349((EntryListWidget.Entry)this.getInputListeners().get(CustomizeFlatLevelScreen.this.widgetButtonAddLayer));
			}

			CustomizeFlatLevelScreen.this.method_2145();
		}

		@Override
		protected boolean method_19352() {
			return CustomizeFlatLevelScreen.this.getFocused() == this;
		}

		@Override
		public boolean hasFocus() {
			return true;
		}

		@Override
		public void setHasFocus(boolean bl) {
			if (bl && CustomizeFlatLevelScreen.this.widgetButtonAddLayer < 0) {
				CustomizeFlatLevelScreen.this.widgetButtonAddLayer = 0;
				CustomizeFlatLevelScreen.this.method_2145();
			}
		}

		@Override
		protected int getScrollbarPosition() {
			return this.width - 70;
		}

		public void method_19372() {
			this.clearEntries();

			for (int i = 0; i < CustomizeFlatLevelScreen.this.config.getLayers().size(); i++) {
				this.addEntry(CustomizeFlatLevelScreen.this.new class_4193());
			}
		}
	}

	@Environment(EnvType.CLIENT)
	class class_4193 extends EntryListWidget.Entry<CustomizeFlatLevelScreen.class_4193> {
		private class_4193() {
		}

		@Override
		public void draw(int i, int j, int k, int l, boolean bl, float f) {
			FlatChunkGeneratorLayer flatChunkGeneratorLayer = (FlatChunkGeneratorLayer)CustomizeFlatLevelScreen.this.config
				.getLayers()
				.get(CustomizeFlatLevelScreen.this.config.getLayers().size() - this.field_2143 - 1);
			BlockState blockState = flatChunkGeneratorLayer.getBlockState();
			Block block = blockState.getBlock();
			Item item = block.getItem();
			if (item == Items.AIR) {
				if (block == Blocks.field_10382) {
					item = Items.field_8705;
				} else if (block == Blocks.field_10164) {
					item = Items.field_8187;
				}
			}

			ItemStack itemStack = new ItemStack(item);
			String string = item.getTranslatedNameTrimmed(itemStack).getFormattedText();
			this.method_19375(this.getX(), this.getY(), itemStack);
			CustomizeFlatLevelScreen.this.fontRenderer.draw(string, (float)(this.getX() + 18 + 5), (float)(this.getY() + 3), 16777215);
			String string2;
			if (this.field_2143 == 0) {
				string2 = I18n.translate("createWorld.customize.flat.layer.top", flatChunkGeneratorLayer.getThickness());
			} else if (this.field_2143 == CustomizeFlatLevelScreen.this.config.getLayers().size() - 1) {
				string2 = I18n.translate("createWorld.customize.flat.layer.bottom", flatChunkGeneratorLayer.getThickness());
			} else {
				string2 = I18n.translate("createWorld.customize.flat.layer", flatChunkGeneratorLayer.getThickness());
			}

			CustomizeFlatLevelScreen.this.fontRenderer
				.draw(string2, (float)(this.getX() + 2 + 213 - CustomizeFlatLevelScreen.this.fontRenderer.getStringWidth(string2)), (float)(this.getY() + 3), 16777215);
		}

		@Override
		public boolean mouseClicked(double d, double e, int i) {
			if (i == 0) {
				CustomizeFlatLevelScreen.this.widgetButtonAddLayer = this.field_2143;
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
			this.method_19374(i, j, 0, 0);
		}

		private void method_19374(int i, int j, int k, int l) {
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			CustomizeFlatLevelScreen.this.client.getTextureManager().bindTexture(DrawableHelper.STAT_ICONS);
			float f = 0.0078125F;
			float g = 0.0078125F;
			int m = 18;
			int n = 18;
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
			bufferBuilder.begin(7, VertexFormats.POSITION_UV);
			bufferBuilder.vertex((double)(i + 0), (double)(j + 18), (double)CustomizeFlatLevelScreen.this.zOffset)
				.texture((double)((float)(k + 0) * 0.0078125F), (double)((float)(l + 18) * 0.0078125F))
				.next();
			bufferBuilder.vertex((double)(i + 18), (double)(j + 18), (double)CustomizeFlatLevelScreen.this.zOffset)
				.texture((double)((float)(k + 18) * 0.0078125F), (double)((float)(l + 18) * 0.0078125F))
				.next();
			bufferBuilder.vertex((double)(i + 18), (double)(j + 0), (double)CustomizeFlatLevelScreen.this.zOffset)
				.texture((double)((float)(k + 18) * 0.0078125F), (double)((float)(l + 0) * 0.0078125F))
				.next();
			bufferBuilder.vertex((double)(i + 0), (double)(j + 0), (double)CustomizeFlatLevelScreen.this.zOffset)
				.texture((double)((float)(k + 0) * 0.0078125F), (double)((float)(l + 0) * 0.0078125F))
				.next();
			tessellator.draw();
		}
	}
}
