package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class class_339 extends class_332 implements class_4068, class_364 {
	public static final class_2960 WIDGETS_LOCATION = new class_2960("textures/gui/widgets.png");
	private static final int NARRATE_DELAY_MOUSE = 750;
	private static final int NARRATE_DELAY_FOCUS = 200;
	protected int width;
	protected int height;
	public int x;
	public int y;
	private String message;
	private boolean wasHovered;
	protected boolean isHovered;
	public boolean active = true;
	public boolean visible = true;
	protected float alpha = 1.0F;
	protected long nextNarration = Long.MAX_VALUE;
	private boolean focused;

	public class_339(int i, int j, String string) {
		this(i, j, 200, 20, string);
	}

	public class_339(int i, int j, int k, int l, String string) {
		this.x = i;
		this.y = j;
		this.width = k;
		this.height = l;
		this.message = string;
	}

	protected int getYImage(boolean bl) {
		int i = 1;
		if (!this.active) {
			i = 0;
		} else if (bl) {
			i = 2;
		}

		return i;
	}

	@Override
	public void render(int i, int j, float f) {
		if (this.visible) {
			this.isHovered = i >= this.x && j >= this.y && i < this.x + this.width && j < this.y + this.height;
			if (this.wasHovered != this.isHovered()) {
				if (this.isHovered()) {
					if (this.focused) {
						this.nextNarration = class_156.method_658() + 200L;
					} else {
						this.nextNarration = class_156.method_658() + 750L;
					}
				} else {
					this.nextNarration = Long.MAX_VALUE;
				}
			}

			if (this.visible) {
				this.renderButton(i, j, f);
			}

			this.narrate();
			this.wasHovered = this.isHovered();
		}
	}

	protected void narrate() {
		if (this.active && this.isHovered() && class_156.method_658() > this.nextNarration) {
			String string = this.getNarrationMessage();
			if (!string.isEmpty()) {
				class_333.field_2054.method_19788(string);
				this.nextNarration = Long.MAX_VALUE;
			}
		}
	}

	protected String getNarrationMessage() {
		return this.message.isEmpty() ? "" : class_1074.method_4662("gui.narrate.button", this.getMessage());
	}

	public void renderButton(int i, int j, float f) {
		class_310 lv = class_310.method_1551();
		class_327 lv2 = lv.field_1772;
		lv.method_1531().method_4618(WIDGETS_LOCATION);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, this.alpha);
		int k = this.getYImage(this.isHovered());
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(
			GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
		);
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		this.blit(this.x, this.y, 0, 46 + k * 20, this.width / 2, this.height);
		this.blit(this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + k * 20, this.width / 2, this.height);
		this.renderBg(lv, i, j);
		int l = 14737632;
		if (!this.active) {
			l = 10526880;
		} else if (this.isHovered()) {
			l = 16777120;
		}

		this.drawCenteredString(lv2, this.message, this.x + this.width / 2, this.y + (this.height - 8) / 2, l | class_3532.method_15386(this.alpha * 255.0F) << 24);
	}

	protected void renderBg(class_310 arg, int i, int j) {
	}

	public void onClick(double d, double e) {
	}

	public void onRelease(double d, double e) {
	}

	protected void onDrag(double d, double e, double f, double g) {
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (this.active && this.visible) {
			if (i == 0) {
				boolean bl = this.clicked(d, e);
				if (bl) {
					this.playDownSound(class_310.method_1551().method_1483());
					this.onClick(d, e);
					return true;
				}
			}

			return false;
		} else {
			return false;
		}
	}

	@Override
	public boolean mouseReleased(double d, double e, int i) {
		if (i == 0) {
			this.onRelease(d, e);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean mouseDragged(double d, double e, int i, double f, double g) {
		if (i == 0) {
			this.onDrag(d, e, f, g);
			return true;
		} else {
			return false;
		}
	}

	protected boolean clicked(double d, double e) {
		return this.active && this.visible && d >= (double)this.x && e >= (double)this.y && d < (double)(this.x + this.width) && e < (double)(this.y + this.height);
	}

	public boolean isHovered() {
		return this.isHovered || this.focused;
	}

	@Override
	public void onFocusChanged(boolean bl, boolean bl2) {
		this.focused = bl2;
	}

	@Override
	public boolean isPartOfFocusCycle() {
		return this.active && this.visible;
	}

	@Override
	public boolean isMouseOver(double d, double e) {
		return this.active && this.visible && d >= (double)this.x && e >= (double)this.y && d < (double)(this.x + this.width) && e < (double)(this.y + this.height);
	}

	public void renderToolTip(int i, int j) {
	}

	public void playDownSound(class_1144 arg) {
		arg.method_4873(class_1109.method_4758(class_3417.field_15152, 1.0F));
	}

	public int getWidth() {
		return this.width;
	}

	public void setWidth(int i) {
		this.width = i;
	}

	public void setAlpha(float f) {
		this.alpha = f;
	}

	public void setMessage(String string) {
		if (!Objects.equals(string, this.message)) {
			this.nextNarration = class_156.method_658() + 250L;
		}

		this.message = string;
	}

	public String getMessage() {
		return this.message;
	}
}
