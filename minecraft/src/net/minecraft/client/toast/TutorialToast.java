package net.minecraft.client.toast;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class TutorialToast implements Toast {
	private static final Identifier TEXTURE = Identifier.ofVanilla("toast/tutorial");
	public static final int PROGRESS_BAR_WIDTH = 154;
	public static final int PROGRESS_BAR_HEIGHT = 1;
	public static final int PROGRESS_BAR_X = 3;
	public static final int PROGRESS_BAR_Y = 28;
	private final TutorialToast.Type type;
	private final Text title;
	@Nullable
	private final Text description;
	private Toast.Visibility visibility = Toast.Visibility.SHOW;
	private long field_52791;
	private float field_52792;
	private float progress;
	private final boolean hasProgressBar;
	private final int field_52793;

	public TutorialToast(TutorialToast.Type type, Text title, @Nullable Text description, boolean hasProgressBar, int i) {
		this.type = type;
		this.title = title;
		this.description = description;
		this.hasProgressBar = hasProgressBar;
		this.field_52793 = i;
	}

	public TutorialToast(TutorialToast.Type type, Text text, @Nullable Text text2, boolean bl) {
		this(type, text, text2, bl, 0);
	}

	@Override
	public Toast.Visibility getVisibility() {
		return this.visibility;
	}

	@Override
	public void update(ToastManager manager, long time) {
		if (this.field_52793 > 0) {
			this.progress = Math.min((float)time / (float)this.field_52793, 1.0F);
			this.field_52792 = this.progress;
			this.field_52791 = time;
			if (time > (long)this.field_52793) {
				this.hide();
			}
		} else if (this.hasProgressBar) {
			this.field_52792 = MathHelper.clampedLerp(this.field_52792, this.progress, (float)(time - this.field_52791) / 100.0F);
			this.field_52791 = time;
		}
	}

	@Override
	public void draw(DrawContext context, TextRenderer textRenderer, long startTime) {
		context.drawGuiTexture(RenderLayer::getGuiTextured, TEXTURE, 0, 0, this.getWidth(), this.getHeight());
		this.type.drawIcon(context, 6, 6);
		if (this.description == null) {
			context.drawText(textRenderer, this.title, 30, 12, Colors.PURPLE, false);
		} else {
			context.drawText(textRenderer, this.title, 30, 7, Colors.PURPLE, false);
			context.drawText(textRenderer, this.description, 30, 18, Colors.BLACK, false);
		}

		if (this.hasProgressBar) {
			context.fill(3, 28, 157, 29, -1);
			int i;
			if (this.progress >= this.field_52792) {
				i = -16755456;
			} else {
				i = -11206656;
			}

			context.fill(3, 28, (int)(3.0F + 154.0F * this.field_52792), 29, i);
		}
	}

	public void hide() {
		this.visibility = Toast.Visibility.HIDE;
	}

	public void setProgress(float progress) {
		this.progress = progress;
	}

	@Environment(EnvType.CLIENT)
	public static enum Type {
		MOVEMENT_KEYS(Identifier.ofVanilla("toast/movement_keys")),
		MOUSE(Identifier.ofVanilla("toast/mouse")),
		TREE(Identifier.ofVanilla("toast/tree")),
		RECIPE_BOOK(Identifier.ofVanilla("toast/recipe_book")),
		WOODEN_PLANKS(Identifier.ofVanilla("toast/wooden_planks")),
		SOCIAL_INTERACTIONS(Identifier.ofVanilla("toast/social_interactions")),
		RIGHT_CLICK(Identifier.ofVanilla("toast/right_click"));

		private final Identifier texture;

		private Type(final Identifier texture) {
			this.texture = texture;
		}

		public void drawIcon(DrawContext drawContext, int x, int y) {
			drawContext.drawGuiTexture(RenderLayer::getGuiTextured, this.texture, x, y, 20, 20);
		}
	}
}
