package net.minecraft.client.toast;

import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class TutorialToast implements Toast {
	public static final int PROGRESS_BAR_WIDTH = 154;
	public static final int PROGRESS_BAR_HEIGHT = 1;
	public static final int PROGRESS_BAR_X = 3;
	public static final int PROGRESS_BAR_Y = 28;
	private final TutorialToast.Type type;
	private final Text title;
	@Nullable
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
	public Toast.Visibility draw(DrawContext context, ToastManager manager, long startTime) {
		context.drawTexture(TEXTURE, 0, 0, 0, 96, this.getWidth(), this.getHeight());
		this.type.drawIcon(context, 6, 6);
		if (this.description == null) {
			context.drawText(manager.getClient().textRenderer, this.title, 30, 12, -11534256, false);
		} else {
			context.drawText(manager.getClient().textRenderer, this.title, 30, 7, -11534256, false);
			context.drawText(manager.getClient().textRenderer, this.description, 30, 18, -16777216, false);
		}

		if (this.hasProgressBar) {
			context.fill(3, 28, 157, 29, -1);
			float f = MathHelper.clampedLerp(this.lastProgress, this.progress, (float)(startTime - this.lastTime) / 100.0F);
			int i;
			if (this.progress >= this.lastProgress) {
				i = -16755456;
			} else {
				i = -11206656;
			}

			context.fill(3, 28, (int)(3.0F + 154.0F * f), 29, i);
			this.lastProgress = f;
			this.lastTime = startTime;
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
		WOODEN_PLANKS(1, 1),
		SOCIAL_INTERACTIONS(2, 1),
		RIGHT_CLICK(3, 1);

		private final int textureSlotX;
		private final int textureSlotY;

		private Type(int textureSlotX, int textureSlotY) {
			this.textureSlotX = textureSlotX;
			this.textureSlotY = textureSlotY;
		}

		public void drawIcon(DrawContext context, int x, int y) {
			RenderSystem.enableBlend();
			context.drawTexture(Toast.TEXTURE, x, y, 176 + this.textureSlotX * 20, this.textureSlotY * 20, 20, 20);
		}
	}
}
