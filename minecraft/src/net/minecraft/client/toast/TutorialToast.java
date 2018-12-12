package net.minecraft.client.toast;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Drawable;
import net.minecraft.text.TextComponent;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class TutorialToast implements Toast {
	private final TutorialToast.class_373 field_2225;
	private final String field_2224;
	private final String field_2226;
	private Toast.class_369 field_2227 = Toast.class_369.field_2210;
	private long field_2223;
	private float field_2229;
	private float field_2228;
	private final boolean field_2222;

	public TutorialToast(TutorialToast.class_373 arg, TextComponent textComponent, @Nullable TextComponent textComponent2, boolean bl) {
		this.field_2225 = arg;
		this.field_2224 = textComponent.getFormattedText();
		this.field_2226 = textComponent2 == null ? null : textComponent2.getFormattedText();
		this.field_2222 = bl;
	}

	@Override
	public Toast.class_369 draw(ToastManager toastManager, long l) {
		toastManager.getGame().getTextureManager().bindTexture(TOASTS_TEX);
		GlStateManager.color3f(1.0F, 1.0F, 1.0F);
		toastManager.drawTexturedRect(0, 0, 0, 96, 160, 32);
		this.field_2225.method_1994(toastManager, 6, 6);
		if (this.field_2226 == null) {
			toastManager.getGame().fontRenderer.draw(this.field_2224, 30.0F, 12.0F, -11534256);
		} else {
			toastManager.getGame().fontRenderer.draw(this.field_2224, 30.0F, 7.0F, -11534256);
			toastManager.getGame().fontRenderer.draw(this.field_2226, 30.0F, 18.0F, -16777216);
		}

		if (this.field_2222) {
			Drawable.drawRect(3, 28, 157, 29, -1);
			float f = (float)MathHelper.lerpClamped((double)this.field_2229, (double)this.field_2228, (double)((float)(l - this.field_2223) / 100.0F));
			int i;
			if (this.field_2228 >= this.field_2229) {
				i = -16755456;
			} else {
				i = -11206656;
			}

			Drawable.drawRect(3, 28, (int)(3.0F + 154.0F * f), 29, i);
			this.field_2229 = f;
			this.field_2223 = l;
		}

		return this.field_2227;
	}

	public void method_1993() {
		this.field_2227 = Toast.class_369.field_2209;
	}

	public void method_1992(float f) {
		this.field_2228 = f;
	}

	@Environment(EnvType.CLIENT)
	public static enum class_373 {
		field_2230(0, 0),
		field_2237(1, 0),
		field_2235(2, 0),
		field_2233(0, 1),
		field_2236(1, 1);

		private final int field_2232;
		private final int field_2231;

		private class_373(int j, int k) {
			this.field_2232 = j;
			this.field_2231 = k;
		}

		public void method_1994(Drawable drawable, int i, int j) {
			GlStateManager.enableBlend();
			drawable.drawTexturedRect(i, j, 176 + this.field_2232 * 20, this.field_2231 * 20, 20, 20);
			GlStateManager.enableBlend();
		}
	}
}
