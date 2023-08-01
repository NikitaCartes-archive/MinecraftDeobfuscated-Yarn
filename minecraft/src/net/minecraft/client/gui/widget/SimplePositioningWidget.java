package net.minecraft.client.gui.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

/**
 * A widget that positions its children relative to the widget's position.
 * This does not attempt to prevent widgets overlapping with each other;
 * if this is necessary, consider {@link GridWidget}.
 */
@Environment(EnvType.CLIENT)
public class SimplePositioningWidget extends WrapperWidget {
	private final List<SimplePositioningWidget.Element> elements = new ArrayList();
	private int minHeight;
	private int minWidth;
	private final Positioner mainPositioner = Positioner.create().relative(0.5F, 0.5F);

	public SimplePositioningWidget() {
		this(0, 0, 0, 0);
	}

	public SimplePositioningWidget(int width, int height) {
		this(0, 0, width, height);
	}

	public SimplePositioningWidget(int i, int j, int k, int l) {
		super(i, j, k, l);
		this.setDimensions(k, l);
	}

	public SimplePositioningWidget setDimensions(int minWidth, int minHeight) {
		return this.setMinWidth(minWidth).setMinHeight(minHeight);
	}

	public SimplePositioningWidget setMinHeight(int minWidth) {
		this.minWidth = minWidth;
		return this;
	}

	public SimplePositioningWidget setMinWidth(int minHeight) {
		this.minHeight = minHeight;
		return this;
	}

	public Positioner copyPositioner() {
		return this.mainPositioner.copy();
	}

	public Positioner getMainPositioner() {
		return this.mainPositioner;
	}

	@Override
	public void refreshPositions() {
		super.refreshPositions();
		int i = this.minHeight;
		int j = this.minWidth;

		for (SimplePositioningWidget.Element element : this.elements) {
			i = Math.max(i, element.getWidth());
			j = Math.max(j, element.getHeight());
		}

		for (SimplePositioningWidget.Element element : this.elements) {
			element.setX(this.getX(), i);
			element.setY(this.getY(), j);
		}

		this.width = i;
		this.height = j;
	}

	public <T extends Widget> T add(T widget) {
		return this.add(widget, this.copyPositioner());
	}

	public <T extends Widget> T add(T widget, Positioner positioner) {
		this.elements.add(new SimplePositioningWidget.Element(widget, positioner));
		return widget;
	}

	public <T extends Widget> T add(T widget, Consumer<Positioner> callback) {
		return this.add(widget, Util.make(this.copyPositioner(), callback));
	}

	@Override
	public void forEachElement(Consumer<Widget> consumer) {
		this.elements.forEach(element -> consumer.accept(element.widget));
	}

	public static void setPos(Widget widget, int left, int top, int right, int bottom) {
		setPos(widget, left, top, right, bottom, 0.5F, 0.5F);
	}

	public static void setPos(Widget widget, ScreenRect rect) {
		setPos(widget, rect.position().x(), rect.position().y(), rect.width(), rect.height());
	}

	public static void setPos(Widget widget, ScreenRect rect, float relativeX, float relativeY) {
		setPos(widget, rect.getLeft(), rect.getTop(), rect.width(), rect.height(), relativeX, relativeY);
	}

	public static void setPos(Widget widget, int left, int top, int right, int bottom, float relativeX, float relativeY) {
		setPos(left, right, widget.getWidth(), widget::setX, relativeX);
		setPos(top, bottom, widget.getHeight(), widget::setY, relativeY);
	}

	public static void setPos(int low, int high, int length, Consumer<Integer> setter, float relative) {
		int i = (int)MathHelper.lerp(relative, 0.0F, (float)(high - length));
		setter.accept(low + i);
	}

	@Environment(EnvType.CLIENT)
	static class Element extends WrapperWidget.WrappedElement {
		protected Element(Widget widget, Positioner positioner) {
			super(widget, positioner);
		}
	}
}
