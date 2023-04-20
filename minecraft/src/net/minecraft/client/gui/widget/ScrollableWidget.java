package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

/**
 * A widget that can be focused and vertically scrolled.
 */
@Environment(EnvType.CLIENT)
public abstract class ScrollableWidget extends ClickableWidget implements Drawable, Element {
	private static final int FOCUSED_BORDER_COLOR = -1;
	private static final int UNFOCUSED_BORDER_COLOR = -6250336;
	private static final int BOX_COLOR = -16777216;
	private static final int PADDING = 4;
	private double scrollY;
	private boolean scrollbarDragged;

	public ScrollableWidget(int i, int j, int k, int l, Text text) {
		super(i, j, k, l, text);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (!this.visible) {
			return false;
		} else {
			boolean bl = this.isWithinBounds(mouseX, mouseY);
			boolean bl2 = this.overflows()
				&& mouseX >= (double)(this.getX() + this.width)
				&& mouseX <= (double)(this.getX() + this.width + 8)
				&& mouseY >= (double)this.getY()
				&& mouseY < (double)(this.getY() + this.height);
			if (bl2 && button == 0) {
				this.scrollbarDragged = true;
				return true;
			} else {
				return bl || bl2;
			}
		}
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (button == 0) {
			this.scrollbarDragged = false;
		}

		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (this.visible && this.isFocused() && this.scrollbarDragged) {
			if (mouseY < (double)this.getY()) {
				this.setScrollY(0.0);
			} else if (mouseY > (double)(this.getY() + this.height)) {
				this.setScrollY((double)this.getMaxScrollY());
			} else {
				int i = this.getScrollbarThumbHeight();
				double d = (double)Math.max(1, this.getMaxScrollY() / (this.height - i));
				this.setScrollY(this.scrollY + deltaY * d);
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		if (!this.visible) {
			return false;
		} else {
			this.setScrollY(this.scrollY - amount * this.getDeltaYPerScroll());
			return true;
		}
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		boolean bl = keyCode == GLFW.GLFW_KEY_UP;
		boolean bl2 = keyCode == GLFW.GLFW_KEY_DOWN;
		if (bl || bl2) {
			double d = this.scrollY;
			this.setScrollY(this.scrollY + (double)(bl ? -1 : 1) * this.getDeltaYPerScroll());
			if (d != this.scrollY) {
				return true;
			}
		}

		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
		if (this.visible) {
			this.drawBox(context);
			context.enableScissor(this.getX() + 1, this.getY() + 1, this.getX() + this.width - 1, this.getY() + this.height - 1);
			context.getMatrices().push();
			context.getMatrices().translate(0.0, -this.scrollY, 0.0);
			this.renderContents(context, mouseX, mouseY, delta);
			context.getMatrices().pop();
			context.disableScissor();
			this.renderOverlay(context);
		}
	}

	private int getScrollbarThumbHeight() {
		return MathHelper.clamp((int)((float)(this.height * this.height) / (float)this.getContentsHeightWithPadding()), 32, this.height);
	}

	/**
	 * Renders overlays that are not scrolled but part of the widget.
	 * 
	 * <p>This renders the scrollbar by default. Subclasses can override this to
	 * render other overlays, but {@code super} call is necessary to make sure the scrollbar
	 * renders when it should.
	 */
	protected void renderOverlay(DrawContext context) {
		if (this.overflows()) {
			this.drawScrollbar(context);
		}
	}

	protected int getPadding() {
		return 4;
	}

	protected int getPaddingDoubled() {
		return this.getPadding() * 2;
	}

	protected double getScrollY() {
		return this.scrollY;
	}

	protected void setScrollY(double scrollY) {
		this.scrollY = MathHelper.clamp(scrollY, 0.0, (double)this.getMaxScrollY());
	}

	protected int getMaxScrollY() {
		return Math.max(0, this.getContentsHeightWithPadding() - (this.height - 4));
	}

	private int getContentsHeightWithPadding() {
		return this.getContentsHeight() + 4;
	}

	/**
	 * Draws the box that the contents are rendered over, including its borders.
	 */
	private void drawBox(DrawContext context) {
		int i = this.isFocused() ? -1 : -6250336;
		context.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, i);
		context.fill(this.getX() + 1, this.getY() + 1, this.getX() + this.width - 1, this.getY() + this.height - 1, -16777216);
	}

	private void drawScrollbar(DrawContext context) {
		int i = this.getScrollbarThumbHeight();
		int j = this.getX() + this.width;
		int k = this.getX() + this.width + 8;
		int l = Math.max(this.getY(), (int)this.scrollY * (this.height - i) / this.getMaxScrollY() + this.getY());
		int m = l + i;
		context.fill(j, l, k, m, -8355712);
		context.fill(j, l, k - 1, m - 1, -4144960);
	}

	protected boolean isVisible(int top, int bottom) {
		return (double)bottom - this.scrollY >= (double)this.getY() && (double)top - this.scrollY <= (double)(this.getY() + this.height);
	}

	protected boolean isWithinBounds(double mouseX, double mouseY) {
		return mouseX >= (double)this.getX()
			&& mouseX < (double)(this.getX() + this.width)
			&& mouseY >= (double)this.getY()
			&& mouseY < (double)(this.getY() + this.height);
	}

	/**
	 * {@return the total height of the contents}
	 */
	protected abstract int getContentsHeight();

	/**
	 * {@return whether the contents overflow and needs a scrollbar}
	 */
	protected abstract boolean overflows();

	protected abstract double getDeltaYPerScroll();

	/**
	 * Renders the scrolled contents. Subclasses must override this. The rendered contents
	 * may overflow; the caller should trim those using {@link
	 * net.minecraft.client.gui.DrawContext#enableScissor}.
	 */
	protected abstract void renderContents(DrawContext context, int mouseX, int mouseY, float delta);
}
