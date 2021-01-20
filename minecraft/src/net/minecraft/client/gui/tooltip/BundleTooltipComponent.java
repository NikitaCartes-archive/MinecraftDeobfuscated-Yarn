package net.minecraft.client.gui.tooltip;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.item.BundleTooltipData;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

@Environment(EnvType.CLIENT)
public class BundleTooltipComponent implements TooltipComponent {
	public static final Identifier TEXTURE = new Identifier("textures/gui/container/bundle.png");
	private final DefaultedList<ItemStack> inventory;
	private final int field_28360;

	public BundleTooltipComponent(BundleTooltipData bundleTooltipData) {
		this.inventory = bundleTooltipData.getInventory();
		this.field_28360 = bundleTooltipData.getBundleOccupancy();
	}

	@Override
	public int getHeight() {
		return this.method_33290() * 20 + 2 + 4;
	}

	@Override
	public int getWidth(TextRenderer textRenderer) {
		return this.method_33289() * 18 + 2;
	}

	@Override
	public void drawItems(TextRenderer textRenderer, int x, int y, MatrixStack matrices, ItemRenderer itemRenderer, int z, TextureManager textureManager) {
		int i = this.method_33289();
		int j = this.method_33290();
		boolean bl = this.field_28360 >= 64;
		int k = 0;

		for (int l = 0; l < j; l++) {
			for (int m = 0; m < i; m++) {
				int n = x + m * 18 + 1;
				int o = y + l * 20 + 1;
				this.method_33287(n, o, k++, bl, textRenderer, matrices, itemRenderer, z, textureManager);
			}
		}

		this.method_33286(x, y, i, j, matrices, z, textureManager);
	}

	private void method_33287(
		int i, int j, int k, boolean bl, TextRenderer textRenderer, MatrixStack matrixStack, ItemRenderer itemRenderer, int l, TextureManager textureManager
	) {
		if (k >= this.inventory.size()) {
			this.method_33288(matrixStack, i, j, l, textureManager, bl ? BundleTooltipComponent.class_5771.field_28362 : BundleTooltipComponent.class_5771.field_28361);
		} else {
			ItemStack itemStack = this.inventory.get(k);
			this.method_33288(matrixStack, i, j, l, textureManager, BundleTooltipComponent.class_5771.field_28361);
			itemRenderer.renderInGuiWithOverrides(itemStack, i + 1, j + 1, k);
			itemRenderer.renderGuiItemOverlay(textRenderer, itemStack, i + 1, j + 1);
			if (k == 0) {
				HandledScreen.method_33285(matrixStack, i + 1, j + 1, l);
			}
		}
	}

	private void method_33286(int i, int j, int k, int l, MatrixStack matrixStack, int m, TextureManager textureManager) {
		this.method_33288(matrixStack, i, j, m, textureManager, BundleTooltipComponent.class_5771.field_28366);
		this.method_33288(matrixStack, i + k * 18 + 1, j, m, textureManager, BundleTooltipComponent.class_5771.field_28366);

		for (int n = 0; n < k; n++) {
			this.method_33288(matrixStack, i + 1 + n * 18, j, m, textureManager, BundleTooltipComponent.class_5771.field_28364);
			this.method_33288(matrixStack, i + 1 + n * 18, j + l * 20, m, textureManager, BundleTooltipComponent.class_5771.field_28365);
		}

		for (int n = 0; n < l; n++) {
			this.method_33288(matrixStack, i, j + n * 20 + 1, m, textureManager, BundleTooltipComponent.class_5771.field_28363);
			this.method_33288(matrixStack, i + k * 18 + 1, j + n * 20 + 1, m, textureManager, BundleTooltipComponent.class_5771.field_28363);
		}

		this.method_33288(matrixStack, i, j + l * 20, m, textureManager, BundleTooltipComponent.class_5771.field_28367);
		this.method_33288(matrixStack, i + k * 18 + 1, j + l * 20, m, textureManager, BundleTooltipComponent.class_5771.field_28367);
	}

	private void method_33288(MatrixStack matrixStack, int i, int j, int k, TextureManager textureManager, BundleTooltipComponent.class_5771 arg) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		textureManager.bindTexture(TEXTURE);
		DrawableHelper.drawTexture(matrixStack, i, j, k, (float)arg.field_28368, (float)arg.field_28369, arg.field_28370, arg.field_28371, 128, 128);
	}

	private int method_33289() {
		return Math.max(2, (int)Math.ceil(Math.sqrt((double)this.inventory.size() + 1.0)));
	}

	private int method_33290() {
		return (int)Math.ceil(((double)this.inventory.size() + 1.0) / (double)this.method_33289());
	}

	@Environment(EnvType.CLIENT)
	static enum class_5771 {
		field_28361(0, 0, 18, 20),
		field_28362(0, 40, 18, 20),
		field_28363(0, 18, 1, 20),
		field_28364(0, 20, 18, 1),
		field_28365(0, 60, 18, 1),
		field_28366(0, 20, 1, 1),
		field_28367(0, 60, 1, 1);

		public final int field_28368;
		public final int field_28369;
		public final int field_28370;
		public final int field_28371;

		private class_5771(int j, int k, int l, int m) {
			this.field_28368 = j;
			this.field_28369 = k;
			this.field_28370 = l;
			this.field_28371 = m;
		}
	}
}
