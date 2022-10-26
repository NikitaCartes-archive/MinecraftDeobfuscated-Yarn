package net.minecraft.client.gui.widget;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

/**
 * A widget that positions its children relative to the widget's position.
 * This does not attempt to prevent widgets overlapping with each other;
 * if this is necessary, consider {@link GridWidget}.
 */
@Environment(EnvType.CLIENT)
public class SimplePositioningWidget extends WrapperWidget {
	private final List<SimplePositioningWidget.Element> elements = new ArrayList();
	private final List<ClickableWidget> children = Collections.unmodifiableList(Lists.transform(this.elements, element -> element.widget));
	private int minHeight;
	private int minWidth;
	private final Positioner mainPositioner = Positioner.create().relative(0.5F, 0.5F);

	public static SimplePositioningWidget of(int minWidth, int minHeight) {
		return new SimplePositioningWidget(0, 0, 0, 0).setDimensions(minWidth, minHeight);
	}

	public SimplePositioningWidget() {
		this(0, 0, 0, 0);
	}

	public SimplePositioningWidget(int x, int y, int width, int height) {
		super(x, y, width, height, Text.empty());
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

	public void recalculateDimensions() {
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

	public <T extends ClickableWidget> T add(T widget) {
		return this.add(widget, this.copyPositioner());
	}

	public <T extends ClickableWidget> T add(T widget, Positioner positioner) {
		this.elements.add(new SimplePositioningWidget.Element(widget, positioner));
		return widget;
	}

	@Override
	protected List<ClickableWidget> wrappedWidgets() {
		return this.children;
	}

	public static void setPos(ClickableWidget widget, int left, int top, int right, int bottom) {
		setPos(widget, left, top, right, bottom, 0.5F, 0.5F);
	}

	public static void setPos(ClickableWidget widget, int left, int top, int right, int bottom, float relativeX, float relativeY) {
		setPos(left, right, widget.getWidth(), widget::setX, relativeX);
		setPos(top, bottom, widget.getHeight(), widget::setY, relativeY);
	}

	public static void setPos(int low, int high, int length, Consumer<Integer> setter, float relative) {
		int i = (int)MathHelper.lerp(relative, 0.0F, (float)(high - length));
		setter.accept(low + i);
	}

	@Environment(EnvType.CLIENT)
	static class Element extends WrapperWidget.WrappedElement {
		protected Element(ClickableWidget clickableWidget, Positioner positioner) {
			super(clickableWidget, positioner);
		}
	}
}
