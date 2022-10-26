package net.minecraft.client.gui.widget;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.util.math.Divider;

@Environment(EnvType.CLIENT)
public class AxisGridWidget extends WrapperWidget {
	private final AxisGridWidget.DisplayAxis axis;
	private final List<AxisGridWidget.Element> elements = new ArrayList();
	private final List<ClickableWidget> children = Collections.unmodifiableList(Lists.transform(this.elements, element -> element.widget));
	private final Positioner mainPositioner = Positioner.create();

	public AxisGridWidget(int width, int height, AxisGridWidget.DisplayAxis axis) {
		this(0, 0, width, height, axis);
	}

	public AxisGridWidget(int x, int y, int width, int height, AxisGridWidget.DisplayAxis axis) {
		super(x, y, width, height, Text.empty());
		this.axis = axis;
	}

	public void recalculateDimensions() {
		if (!this.elements.isEmpty()) {
			int i = 0;
			int j = this.axis.getOtherAxisLength(this);

			for (AxisGridWidget.Element element : this.elements) {
				i += this.axis.getSameAxisLength(element);
				j = Math.max(j, this.axis.getOtherAxisLength(element));
			}

			int k = this.axis.getSameAxisLength(this) - i;
			int l = this.axis.getSameAxisCoordinate(this);
			Iterator<AxisGridWidget.Element> iterator = this.elements.iterator();
			AxisGridWidget.Element element2 = (AxisGridWidget.Element)iterator.next();
			this.axis.setSameAxisCoordinate(element2, l);
			l += this.axis.getSameAxisLength(element2);
			if (this.elements.size() >= 2) {
				Divider divider = new Divider(k, this.elements.size() - 1);

				while (divider.hasNext()) {
					l += divider.nextInt();
					AxisGridWidget.Element element3 = (AxisGridWidget.Element)iterator.next();
					this.axis.setSameAxisCoordinate(element3, l);
					l += this.axis.getSameAxisLength(element3);
				}
			}

			int m = this.axis.getOtherAxisCoordinate(this);

			for (AxisGridWidget.Element element4 : this.elements) {
				this.axis.setOtherAxisCoordinate(element4, m, j);
			}

			this.axis.setOtherAxisLength(this, j);
		}
	}

	@Override
	protected List<? extends ClickableWidget> wrappedWidgets() {
		return this.children;
	}

	public Positioner copyPositioner() {
		return this.mainPositioner.copy();
	}

	public Positioner getMainPositioner() {
		return this.mainPositioner;
	}

	public <T extends ClickableWidget> T add(T widget) {
		return this.add(widget, this.copyPositioner());
	}

	public <T extends ClickableWidget> T add(T widget, Positioner positioner) {
		this.elements.add(new AxisGridWidget.Element(widget, positioner));
		return widget;
	}

	@Environment(EnvType.CLIENT)
	public static enum DisplayAxis {
		HORIZONTAL,
		VERTICAL;

		int getSameAxisLength(ClickableWidget widget) {
			return switch (this) {
				case HORIZONTAL -> widget.getWidth();
				case VERTICAL -> widget.getHeight();
			};
		}

		int getSameAxisLength(AxisGridWidget.Element element) {
			return switch (this) {
				case HORIZONTAL -> element.getWidth();
				case VERTICAL -> element.getHeight();
			};
		}

		int getOtherAxisLength(ClickableWidget widget) {
			return switch (this) {
				case HORIZONTAL -> widget.getHeight();
				case VERTICAL -> widget.getWidth();
			};
		}

		int getOtherAxisLength(AxisGridWidget.Element element) {
			return switch (this) {
				case HORIZONTAL -> element.getHeight();
				case VERTICAL -> element.getWidth();
			};
		}

		void setSameAxisCoordinate(AxisGridWidget.Element element, int low) {
			switch (this) {
				case HORIZONTAL:
					element.setX(low, element.getWidth());
					break;
				case VERTICAL:
					element.setY(low, element.getHeight());
			}
		}

		void setOtherAxisCoordinate(AxisGridWidget.Element element, int low, int high) {
			switch (this) {
				case HORIZONTAL:
					element.setY(low, high);
					break;
				case VERTICAL:
					element.setX(low, high);
			}
		}

		int getSameAxisCoordinate(ClickableWidget widget) {
			return switch (this) {
				case HORIZONTAL -> widget.getX();
				case VERTICAL -> widget.getY();
			};
		}

		int getOtherAxisCoordinate(ClickableWidget widget) {
			return switch (this) {
				case HORIZONTAL -> widget.getY();
				case VERTICAL -> widget.getX();
			};
		}

		void setOtherAxisLength(ClickableWidget widget, int value) {
			switch (this) {
				case HORIZONTAL:
					widget.height = value;
					break;
				case VERTICAL:
					widget.width = value;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	static class Element extends WrapperWidget.WrappedElement {
		protected Element(ClickableWidget clickableWidget, Positioner positioner) {
			super(clickableWidget, positioner);
		}
	}
}
