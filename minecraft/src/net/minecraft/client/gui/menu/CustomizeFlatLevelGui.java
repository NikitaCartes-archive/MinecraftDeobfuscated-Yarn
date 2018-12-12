package net.minecraft.client.gui.menu;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3229;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiEventListener;
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

@Environment(EnvType.CLIENT)
public class CustomizeFlatLevelGui extends Gui {
	private final NewLevelGui parent;
	private FlatChunkGeneratorConfig field_2419 = FlatChunkGeneratorConfig.method_14309();
	private String titleText;
	private String tileText;
	private String heightText;
	private CustomizeFlatLevelGui.class_414 field_2424;
	private ButtonWidget widgetButtonAddLayer;
	private ButtonWidget widgetButtonEditLayer;
	private ButtonWidget widgetButtonRemoveLayer;

	public CustomizeFlatLevelGui(NewLevelGui newLevelGui, CompoundTag compoundTag) {
		this.parent = newLevelGui;
		this.method_2144(compoundTag);
	}

	public String method_2138() {
		return this.field_2419.toString();
	}

	public CompoundTag method_2140() {
		return (CompoundTag)this.field_2419.method_14313(NbtOps.INSTANCE).getValue();
	}

	public void method_2139(String string) {
		this.field_2419 = FlatChunkGeneratorConfig.method_14319(string);
	}

	public void method_2144(CompoundTag compoundTag) {
		this.field_2419 = FlatChunkGeneratorConfig.method_14323(new Dynamic<>(NbtOps.INSTANCE, compoundTag));
	}

	@Override
	protected void onInitialized() {
		this.titleText = I18n.translate("createWorld.customize.flat.title");
		this.tileText = I18n.translate("createWorld.customize.flat.tile");
		this.heightText = I18n.translate("createWorld.customize.flat.height");
		this.field_2424 = new CustomizeFlatLevelGui.class_414();
		this.listeners.add(this.field_2424);
		this.widgetButtonAddLayer = this.addButton(
			new ButtonWidget(2, this.width / 2 - 154, this.height - 52, 100, 20, I18n.translate("createWorld.customize.flat.addLayer") + " (NYI)") {
				@Override
				public void onPressed(double d, double e) {
					CustomizeFlatLevelGui.this.field_2419.method_14330();
					CustomizeFlatLevelGui.this.method_2145();
				}
			}
		);
		this.widgetButtonEditLayer = this.addButton(
			new ButtonWidget(3, this.width / 2 - 50, this.height - 52, 100, 20, I18n.translate("createWorld.customize.flat.editLayer") + " (NYI)") {
				@Override
				public void onPressed(double d, double e) {
					CustomizeFlatLevelGui.this.field_2419.method_14330();
					CustomizeFlatLevelGui.this.method_2145();
				}
			}
		);
		this.widgetButtonRemoveLayer = this.addButton(
			new ButtonWidget(4, this.width / 2 - 155, this.height - 52, 150, 20, I18n.translate("createWorld.customize.flat.removeLayer")) {
				@Override
				public void onPressed(double d, double e) {
					if (CustomizeFlatLevelGui.this.method_2147()) {
						List<class_3229> list = CustomizeFlatLevelGui.this.field_2419.getLayers();
						int i = list.size() - CustomizeFlatLevelGui.this.field_2424.field_2428 - 1;
						list.remove(i);
						CustomizeFlatLevelGui.this.field_2424.field_2428 = Math.min(CustomizeFlatLevelGui.this.field_2424.field_2428, list.size() - 1);
						CustomizeFlatLevelGui.this.field_2419.method_14330();
						CustomizeFlatLevelGui.this.method_2145();
					}
				}
			}
		);
		this.addButton(new ButtonWidget(0, this.width / 2 - 155, this.height - 28, 150, 20, I18n.translate("gui.done")) {
			@Override
			public void onPressed(double d, double e) {
				CustomizeFlatLevelGui.this.parent.field_3200 = CustomizeFlatLevelGui.this.method_2140();
				CustomizeFlatLevelGui.this.client.openGui(CustomizeFlatLevelGui.this.parent);
				CustomizeFlatLevelGui.this.field_2419.method_14330();
				CustomizeFlatLevelGui.this.method_2145();
			}
		});
		this.addButton(new ButtonWidget(5, this.width / 2 + 5, this.height - 52, 150, 20, I18n.translate("createWorld.customize.presets")) {
			@Override
			public void onPressed(double d, double e) {
				CustomizeFlatLevelGui.this.client.openGui(new NewLevelPresetsGui(CustomizeFlatLevelGui.this));
				CustomizeFlatLevelGui.this.field_2419.method_14330();
				CustomizeFlatLevelGui.this.method_2145();
			}
		});
		this.addButton(new ButtonWidget(1, this.width / 2 + 5, this.height - 28, 150, 20, I18n.translate("gui.cancel")) {
			@Override
			public void onPressed(double d, double e) {
				CustomizeFlatLevelGui.this.client.openGui(CustomizeFlatLevelGui.this.parent);
				CustomizeFlatLevelGui.this.field_2419.method_14330();
				CustomizeFlatLevelGui.this.method_2145();
			}
		});
		this.widgetButtonAddLayer.visible = false;
		this.widgetButtonEditLayer.visible = false;
		this.field_2419.method_14330();
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
		return this.field_2424.field_2428 > -1 && this.field_2424.field_2428 < this.field_2419.getLayers().size();
	}

	@Nullable
	@Override
	public GuiEventListener getFocused() {
		return this.field_2424;
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawBackground();
		this.field_2424.draw(i, j, f);
		this.drawStringCentered(this.fontRenderer, this.titleText, this.width / 2, 8, 16777215);
		int k = this.width / 2 - 92 - 16;
		this.drawString(this.fontRenderer, this.tileText, k, 32, 16777215);
		this.drawString(this.fontRenderer, this.heightText, k + 2 + 213 - this.fontRenderer.getStringWidth(this.heightText), 32, 16777215);
		super.draw(i, j, f);
	}

	@Environment(EnvType.CLIENT)
	class class_414 extends AbstractListWidget {
		public int field_2428 = -1;

		public class_414() {
			super(CustomizeFlatLevelGui.this.client, CustomizeFlatLevelGui.this.width, CustomizeFlatLevelGui.this.height, 43, CustomizeFlatLevelGui.this.height - 60, 24);
		}

		private void renderGuiItemStack(int i, int j, ItemStack itemStack) {
			this.method_2149(i + 1, j + 1);
			GlStateManager.enableRescaleNormal();
			if (!itemStack.isEmpty()) {
				GuiLighting.enableForItems();
				CustomizeFlatLevelGui.this.itemRenderer.renderItemWithPropertyOverrides(itemStack, i + 2, j + 2);
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
			return CustomizeFlatLevelGui.this.field_2419.getLayers().size();
		}

		@Override
		protected boolean selectEntry(int i, int j, double d, double e) {
			this.field_2428 = i;
			CustomizeFlatLevelGui.this.method_2145();
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
			class_3229 lv = (class_3229)CustomizeFlatLevelGui.this.field_2419.getLayers().get(CustomizeFlatLevelGui.this.field_2419.getLayers().size() - i - 1);
			BlockState blockState = lv.method_14286();
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
			CustomizeFlatLevelGui.this.fontRenderer.draw(string, (float)(j + 18 + 5), (float)(k + 3), 16777215);
			String string2;
			if (i == 0) {
				string2 = I18n.translate("createWorld.customize.flat.layer.top", lv.method_14289());
			} else if (i == CustomizeFlatLevelGui.this.field_2419.getLayers().size() - 1) {
				string2 = I18n.translate("createWorld.customize.flat.layer.bottom", lv.method_14289());
			} else {
				string2 = I18n.translate("createWorld.customize.flat.layer", lv.method_14289());
			}

			CustomizeFlatLevelGui.this.fontRenderer
				.draw(string2, (float)(j + 2 + 213 - CustomizeFlatLevelGui.this.fontRenderer.getStringWidth(string2)), (float)(k + 3), 16777215);
		}

		@Override
		protected int getScrollbarPosition() {
			return this.width - 70;
		}
	}
}
