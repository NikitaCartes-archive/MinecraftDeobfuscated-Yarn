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
import net.minecraft.client.gui.GuiEventListener;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.AbstractListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
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
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorLayer;

@Environment(EnvType.CLIENT)
public class CustomizeFlatLevelScreen extends Screen {
	private final NewLevelScreen parent;
	private FlatChunkGeneratorConfig config = FlatChunkGeneratorConfig.getDefaultConfig();
	private String titleText;
	private String tileText;
	private String heightText;
	private CustomizeFlatLevelScreen.class_414 field_2424;
	private ButtonWidget widgetButtonAddLayer;
	private ButtonWidget widgetButtonEditLayer;
	private ButtonWidget widgetButtonRemoveLayer;

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
		this.field_2424 = new CustomizeFlatLevelScreen.class_414();
		this.listeners.add(this.field_2424);
		this.widgetButtonAddLayer = this.addButton(
			new ButtonWidget(this.width / 2 - 154, this.height - 52, 100, 20, I18n.translate("createWorld.customize.flat.addLayer") + " (NYI)") {
				@Override
				public void onPressed(double d, double e) {
					CustomizeFlatLevelScreen.this.config.updateLayerBlocks();
					CustomizeFlatLevelScreen.this.method_2145();
				}
			}
		);
		this.widgetButtonEditLayer = this.addButton(
			new ButtonWidget(this.width / 2 - 50, this.height - 52, 100, 20, I18n.translate("createWorld.customize.flat.editLayer") + " (NYI)") {
				@Override
				public void onPressed(double d, double e) {
					CustomizeFlatLevelScreen.this.config.updateLayerBlocks();
					CustomizeFlatLevelScreen.this.method_2145();
				}
			}
		);
		this.widgetButtonRemoveLayer = this.addButton(
			new ButtonWidget(this.width / 2 - 155, this.height - 52, 150, 20, I18n.translate("createWorld.customize.flat.removeLayer")) {
				@Override
				public void onPressed(double d, double e) {
					if (CustomizeFlatLevelScreen.this.method_2147()) {
						List<FlatChunkGeneratorLayer> list = CustomizeFlatLevelScreen.this.config.getLayers();
						int i = list.size() - CustomizeFlatLevelScreen.this.field_2424.field_2428 - 1;
						list.remove(i);
						CustomizeFlatLevelScreen.this.field_2424.field_2428 = Math.min(CustomizeFlatLevelScreen.this.field_2424.field_2428, list.size() - 1);
						CustomizeFlatLevelScreen.this.config.updateLayerBlocks();
						CustomizeFlatLevelScreen.this.method_2145();
					}
				}
			}
		);
		this.addButton(new ButtonWidget(this.width / 2 - 155, this.height - 28, 150, 20, I18n.translate("gui.done")) {
			@Override
			public void onPressed(double d, double e) {
				CustomizeFlatLevelScreen.this.parent.field_3200 = CustomizeFlatLevelScreen.this.method_2140();
				CustomizeFlatLevelScreen.this.client.openScreen(CustomizeFlatLevelScreen.this.parent);
				CustomizeFlatLevelScreen.this.config.updateLayerBlocks();
				CustomizeFlatLevelScreen.this.method_2145();
			}
		});
		this.addButton(new ButtonWidget(this.width / 2 + 5, this.height - 52, 150, 20, I18n.translate("createWorld.customize.presets")) {
			@Override
			public void onPressed(double d, double e) {
				CustomizeFlatLevelScreen.this.client.openScreen(new NewLevelPresetsScreen(CustomizeFlatLevelScreen.this));
				CustomizeFlatLevelScreen.this.config.updateLayerBlocks();
				CustomizeFlatLevelScreen.this.method_2145();
			}
		});
		this.addButton(new ButtonWidget(this.width / 2 + 5, this.height - 28, 150, 20, I18n.translate("gui.cancel")) {
			@Override
			public void onPressed(double d, double e) {
				CustomizeFlatLevelScreen.this.client.openScreen(CustomizeFlatLevelScreen.this.parent);
				CustomizeFlatLevelScreen.this.config.updateLayerBlocks();
				CustomizeFlatLevelScreen.this.method_2145();
			}
		});
		this.widgetButtonAddLayer.visible = false;
		this.widgetButtonEditLayer.visible = false;
		this.config.updateLayerBlocks();
		this.method_2145();
	}

	public void method_2145() {
		boolean bl = this.method_2147();
		this.widgetButtonRemoveLayer.enabled = bl;
		this.widgetButtonEditLayer.enabled = bl;
		this.widgetButtonEditLayer.enabled = false;
		this.widgetButtonAddLayer.enabled = false;
	}

	private boolean method_2147() {
		return this.field_2424.field_2428 > -1 && this.field_2424.field_2428 < this.config.getLayers().size();
	}

	@Nullable
	@Override
	public GuiEventListener getFocused() {
		return this.field_2424;
	}

	@Override
	public void method_18326(int i, int j, float f) {
		this.drawBackground();
		this.field_2424.method_18326(i, j, f);
		this.drawStringCentered(this.fontRenderer, this.titleText, this.width / 2, 8, 16777215);
		int k = this.width / 2 - 92 - 16;
		this.drawString(this.fontRenderer, this.tileText, k, 32, 16777215);
		this.drawString(this.fontRenderer, this.heightText, k + 2 + 213 - this.fontRenderer.getStringWidth(this.heightText), 32, 16777215);
		super.method_18326(i, j, f);
	}

	@Environment(EnvType.CLIENT)
	class class_414 extends AbstractListWidget {
		public int field_2428 = -1;

		public class_414() {
			super(
				CustomizeFlatLevelScreen.this.client,
				CustomizeFlatLevelScreen.this.width,
				CustomizeFlatLevelScreen.this.height,
				43,
				CustomizeFlatLevelScreen.this.height - 60,
				24
			);
		}

		private void renderGuiItemStack(int i, int j, ItemStack itemStack) {
			this.method_2149(i + 1, j + 1);
			GlStateManager.enableRescaleNormal();
			if (!itemStack.isEmpty()) {
				GuiLighting.enableForItems();
				CustomizeFlatLevelScreen.this.itemRenderer.renderGuiItemIcon(itemStack, i + 2, j + 2);
				GuiLighting.disable();
			}

			GlStateManager.disableRescaleNormal();
		}

		private void method_2149(int i, int j) {
			this.method_2150(i, j, 0, 0);
		}

		private void method_2150(int i, int j, int k, int l) {
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.client.getTextureManager().bindTexture(STAT_ICONS);
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
			return CustomizeFlatLevelScreen.this.config.getLayers().size();
		}

		@Override
		protected boolean selectEntry(int i, int j, double d, double e) {
			this.field_2428 = i;
			CustomizeFlatLevelScreen.this.method_2145();
			return true;
		}

		@Override
		protected boolean isSelectedEntry(int i) {
			return i == this.field_2428;
		}

		@Override
		protected void drawBackground() {
		}

		@Override
		protected void drawEntry(int i, int j, int k, int l, int m, int n, float f) {
			FlatChunkGeneratorLayer flatChunkGeneratorLayer = (FlatChunkGeneratorLayer)CustomizeFlatLevelScreen.this.config
				.getLayers()
				.get(CustomizeFlatLevelScreen.this.config.getLayers().size() - i - 1);
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
			this.renderGuiItemStack(j, k, itemStack);
			CustomizeFlatLevelScreen.this.fontRenderer.draw(string, (float)(j + 18 + 5), (float)(k + 3), 16777215);
			String string2;
			if (i == 0) {
				string2 = I18n.translate("createWorld.customize.flat.layer.top", flatChunkGeneratorLayer.getThickness());
			} else if (i == CustomizeFlatLevelScreen.this.config.getLayers().size() - 1) {
				string2 = I18n.translate("createWorld.customize.flat.layer.bottom", flatChunkGeneratorLayer.getThickness());
			} else {
				string2 = I18n.translate("createWorld.customize.flat.layer", flatChunkGeneratorLayer.getThickness());
			}

			CustomizeFlatLevelScreen.this.fontRenderer
				.draw(string2, (float)(j + 2 + 213 - CustomizeFlatLevelScreen.this.fontRenderer.getStringWidth(string2)), (float)(k + 3), 16777215);
		}

		@Override
		protected int getScrollbarPosition() {
			return this.width - 70;
		}
	}
}
