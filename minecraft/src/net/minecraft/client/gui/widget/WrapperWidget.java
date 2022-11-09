package net.minecraft.client.gui.widget;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Narratable;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public abstract class WrapperWidget extends ClickableWidget implements ParentElement {
	@Nullable
	private Element focusedElement;
	private boolean dragging;

	public WrapperWidget(int i, int j, int k, int l, Text text) {
		super(i, j, k, l, text);
	}

	@Override
	public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		for (ClickableWidget clickableWidget : this.wrappedWidgets()) {
			clickableWidget.render(matrices, mouseX, mouseY, delta);
		}
	}

	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		for (ClickableWidget clickableWidget : this.wrappedWidgets()) {
			if (clickableWidget.isMouseOver(mouseX, mouseY)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public void mouseMoved(double mouseX, double mouseY) {
		this.wrappedWidgets().forEach(widget -> widget.mouseMoved(mouseX, mouseY));
	}

	@Override
	public List<? extends Element> children() {
		return this.wrappedWidgets();
	}

	protected abstract List<? extends ClickableWidget> wrappedWidgets();

	@Override
	public boolean isDragging() {
		return this.dragging;
	}

	@Override
	public void setDragging(boolean dragging) {
		this.dragging = dragging;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		boolean bl = false;

		for (ClickableWidget clickableWidget : this.wrappedWidgets()) {
			if (clickableWidget.isMouseOver(mouseX, mouseY) && clickableWidget.mouseScrolled(mouseX, mouseY, amount)) {
				bl = true;
			}
		}

		return bl || super.mouseScrolled(mouseX, mouseY, amount);
	}

	@Override
	public boolean changeFocus(boolean lookForwards) {
		return ParentElement.super.changeFocus(lookForwards);
	}

	@Nullable
	protected Element getHoveredElement() {
		for (ClickableWidget clickableWidget : this.wrappedWidgets()) {
			if (clickableWidget.hovered) {
				return clickableWidget;
			}
		}

		return null;
	}

	@Nullable
	@Override
	public Element getFocused() {
		return this.focusedElement;
	}

	@Override
	public void setFocused(@Nullable Element focused) {
		this.focusedElement = focused;
	}

	@Override
	public void appendClickableNarrations(NarrationMessageBuilder builder) {
		Element element = this.getHoveredElement();
		if (element != null) {
			if (element instanceof Narratable narratable) {
				narratable.appendNarrations(builder.nextMessage());
			}
		} else {
			Element element2 = this.getFocused();
			if (element2 != null && element2 instanceof Narratable narratable2) {
				narratable2.appendNarrations(builder.nextMessage());
			}
		}
	}

	@Override
	public Selectable.SelectionType getType() {
		if (this.hovered || this.getHoveredElement() != null) {
			return Selectable.SelectionType.HOVERED;
		} else {
			return this.focusedElement != null ? Selectable.SelectionType.FOCUSED : super.getType();
		}
	}

	@Override
	public void setX(int x) {
		for (ClickableWidget clickableWidget : this.wrappedWidgets()) {
			int i = clickableWidget.getX() + (x - this.getX());
			clickableWidget.setX(i);
		}

		super.setX(x);
	}

	@Override
	public void setY(int y) {
		for (ClickableWidget clickableWidget : this.wrappedWidgets()) {
			int i = clickableWidget.getY() + (y - this.getY());
			clickableWidget.setY(i);
		}

		super.setY(y);
	}

	@Override
	public Optional<Element> hoveredElement(double mouseX, double mouseY) {
		return ParentElement.super.hoveredElement(mouseX, mouseY);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		return ParentElement.super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		return ParentElement.super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		return ParentElement.super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}

	@Environment(EnvType.CLIENT)
	protected abstract static class WrappedElement {
		public final ClickableWidget widget;
		public final Positioner.Impl positioner;

		protected WrappedElement(ClickableWidget widget, Positioner positioner) {
			this.widget = widget;
			this.positioner = positioner.toImpl();
		}

		public int getHeight() {
			return this.widget.getHeight() + this.positioner.marginTop + this.positioner.marginBottom;
		}

		public int getWidth() {
			return this.widget.getWidth() + this.positioner.marginLeft + this.positioner.marginRight;
		}

		public void setX(int left, int right) {
			float f = (float)this.positioner.marginLeft;
			float g = (float)(right - this.widget.getWidth() - this.positioner.marginRight);
			int i = (int)MathHelper.lerp(this.positioner.relativeX, f, g);
			this.widget.setX(i + left);
		}

		public void setY(int top, int bottom) {
			float f = (float)this.positioner.marginTop;
			float g = (float)(bottom - this.widget.getHeight() - this.positioner.marginBottom);
			int i = (int)MathHelper.lerp(this.positioner.relativeY, f, g);
			this.widget.setY(i + top);
		}
	}
}
