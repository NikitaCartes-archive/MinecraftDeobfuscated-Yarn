package net.minecraft.client.toast;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class TutorialToast implements Toast {
	private final TutorialToast.Type type;
	private final String title;
	private final String description;
	private Toast.Visibility visibility = Toast.Visibility.field_2210;
	private long lastTime;
	private float lastProgress;
	private float progress;
	private final boolean hasProgressBar;

	public TutorialToast(TutorialToast.Type type, Text text, @Nullable Text text2, boolean bl) {
		this.type = type;
		this.title = text.asFormattedString();
		this.description = text2 == null ? null : text2.asFormattedString();
		this.hasProgressBar = bl;
	}

	@Override
	public Toast.Visibility method_1986(ToastManager toastManager, long l) {
		toastManager.getGame().method_1531().bindTexture(TOASTS_TEX);
		GlStateManager.color3f(1.0F, 1.0F, 1.0F);
		toastManager.blit(0, 0, 0, 96, 160, 32);
		this.type.drawIcon(toastManager, 6, 6);
		if (this.description == null) {
			toastManager.getGame().field_1772.draw(this.title, 30.0F, 12.0F, -11534256);
		} else {
			toastManager.getGame().field_1772.draw(this.title, 30.0F, 7.0F, -11534256);
			toastManager.getGame().field_1772.draw(this.description, 30.0F, 18.0F, -16777216);
		}

		if (this.hasProgressBar) {
			DrawableHelper.fill(3, 28, 157, 29, -1);
			float f = (float)MathHelper.clampedLerp((double)this.lastProgress, (double)this.progress, (double)((float)(l - this.lastTime) / 100.0F));
			int i;
			if (this.progress >= this.lastProgress) {
				i = -16755456;
			} else {
				i = -11206656;
			}

			DrawableHelper.fill(3, 28, (int)(3.0F + 154.0F * f), 29, i);
			this.lastProgress = f;
			this.lastTime = l;
		}

		return this.visibility;
	}

	public void hide() {
		this.visibility = Toast.Visibility.field_2209;
	}

	public void setProgress(float f) {
		this.progress = f;
	}

	@Environment(EnvType.CLIENT)
	public static enum Type {
		field_2230(0, 0),
		field_2237(1, 0),
		field_2235(2, 0),
		field_2233(0, 1),
		field_2236(1, 1);

		private final int textureSlotX;
		private final int textureSlotY;

		private Type(int j, int k) {
			this.textureSlotX = j;
			this.textureSlotY = k;
		}

		public void drawIcon(DrawableHelper drawableHelper, int i, int j) {
			GlStateManager.enableBlend();
			drawableHelper.blit(i, j, 176 + this.textureSlotX * 20, this.textureSlotY * 20, 20, 20);
			GlStateManager.enableBlend();
		}
	}
}
