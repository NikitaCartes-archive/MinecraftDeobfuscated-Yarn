package net.minecraft.client.toast;

import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class TutorialToast implements Toast {
	private final TutorialToast.Type type;
	private final Text title;
	private final Text description;
	private Toast.Visibility visibility = Toast.Visibility.SHOW;
	private long lastTime;
	private float lastProgress;
	private float progress;
	private final boolean hasProgressBar;

	public TutorialToast(TutorialToast.Type type, Text title, @Nullable Text description, boolean hasProgressBar) {
		this.type = type;
		this.title = title;
		this.description = description;
		this.hasProgressBar = hasProgressBar;
	}

	@Override
	public Toast.Visibility draw(MatrixStack matrixStack, ToastManager toastManager, long l) {
		toastManager.getGame().getTextureManager().bindTexture(TOASTS_TEX);
		RenderSystem.color3f(1.0F, 1.0F, 1.0F);
		toastManager.drawTexture(matrixStack, 0, 0, 0, 96, 160, 32);
		this.type.drawIcon(matrixStack, toastManager, 6, 6);
		if (this.description == null) {
			toastManager.getGame().textRenderer.draw(matrixStack, this.title, 30.0F, 12.0F, -11534256);
		} else {
			toastManager.getGame().textRenderer.draw(matrixStack, this.title, 30.0F, 7.0F, -11534256);
			toastManager.getGame().textRenderer.draw(matrixStack, this.description, 30.0F, 18.0F, -16777216);
		}

		if (this.hasProgressBar) {
			DrawableHelper.fill(matrixStack, 3, 28, 157, 29, -1);
			float f = (float)MathHelper.clampedLerp((double)this.lastProgress, (double)this.progress, (double)((float)(l - this.lastTime) / 100.0F));
			int i;
			if (this.progress >= this.lastProgress) {
				i = -16755456;
			} else {
				i = -11206656;
			}

			DrawableHelper.fill(matrixStack, 3, 28, (int)(3.0F + 154.0F * f), 29, i);
			this.lastProgress = f;
			this.lastTime = l;
		}

		return this.visibility;
	}

	public void hide() {
		this.visibility = Toast.Visibility.HIDE;
	}

	public void setProgress(float progress) {
		this.progress = progress;
	}

	@Environment(EnvType.CLIENT)
	public static enum Type {
		MOVEMENT_KEYS(0, 0),
		MOUSE(1, 0),
		TREE(2, 0),
		RECIPE_BOOK(0, 1),
		WOODEN_PLANKS(1, 1);

		private final int textureSlotX;
		private final int textureSlotY;

		private Type(int textureSlotX, int textureSlotY) {
			this.textureSlotX = textureSlotX;
			this.textureSlotY = textureSlotY;
		}

		public void drawIcon(MatrixStack matrixStack, DrawableHelper drawableHelper, int i, int j) {
			RenderSystem.enableBlend();
			drawableHelper.drawTexture(matrixStack, i, j, 176 + this.textureSlotX * 20, this.textureSlotY * 20, 20, 20);
			RenderSystem.enableBlend();
		}
	}
}
