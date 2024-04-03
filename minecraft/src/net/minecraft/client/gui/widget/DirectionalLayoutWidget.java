package net.minecraft.client.gui.widget;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Util;

/**
 * A widget implementing one-directional layout. The elements can be
 * positioned either horizontally or vertically.
 * 
 * <p>Use {@link AxisGridWidget} to allow multiple elements per row or column.
 * 
 * @see AxisGridWidget
 */
@Environment(EnvType.CLIENT)
public class DirectionalLayoutWidget implements LayoutWidget {
	private final GridWidget grid;
	private final DirectionalLayoutWidget.DisplayAxis axis;
	private int currentIndex = 0;

	private DirectionalLayoutWidget(DirectionalLayoutWidget.DisplayAxis axis) {
		this(0, 0, axis);
	}

	public DirectionalLayoutWidget(int x, int y, DirectionalLayoutWidget.DisplayAxis axis) {
		this.grid = new GridWidget(x, y);
		this.axis = axis;
	}

	public DirectionalLayoutWidget spacing(int spacing) {
		this.axis.setSpacing(this.grid, spacing);
		return this;
	}

	public Positioner copyPositioner() {
		return this.grid.copyPositioner();
	}

	public Positioner getMainPositioner() {
		return this.grid.getMainPositioner();
	}

	public <T extends Widget> T add(T widget, Positioner positioner) {
		return this.axis.add(this.grid, widget, this.currentIndex++, positioner);
	}

	public <T extends Widget> T add(T widget) {
		return this.add(widget, this.copyPositioner());
	}

	public <T extends Widget> T add(T widget, Consumer<Positioner> callback) {
		return this.axis.add(this.grid, widget, this.currentIndex++, Util.make(this.copyPositioner(), callback));
	}

	@Override
	public void forEachElement(Consumer<Widget> consumer) {
		this.grid.forEachElement(consumer);
	}

	@Override
	public void refreshPositions() {
		this.grid.refreshPositions();
	}

	@Override
	public int getWidth() {
		return this.grid.getWidth();
	}

	@Override
	public int getHeight() {
		return this.grid.getHeight();
	}

	@Override
	public void setX(int x) {
		this.grid.setX(x);
	}

	@Override
	public void setY(int y) {
		this.grid.setY(y);
	}

	@Override
	public int getX() {
		return this.grid.getX();
	}

	@Override
	public int getY() {
		return this.grid.getY();
	}

	public static DirectionalLayoutWidget vertical() {
		return new DirectionalLayoutWidget(DirectionalLayoutWidget.DisplayAxis.VERTICAL);
	}

	public static DirectionalLayoutWidget horizontal() {
		return new DirectionalLayoutWidget(DirectionalLayoutWidget.DisplayAxis.HORIZONTAL);
	}

	@Environment(EnvType.CLIENT)
	public static enum DisplayAxis {
		HORIZONTAL,
		VERTICAL;

		void setSpacing(GridWidget grid, int spacing) {
			switch (this) {
				case HORIZONTAL:
					grid.setColumnSpacing(spacing);
					break;
				case VERTICAL:
					grid.setRowSpacing(spacing);
			}
		}

		public <T extends Widget> T add(GridWidget grid, T widget, int index, Positioner positioner) {
			return (T)(switch (this) {
				case HORIZONTAL -> (Widget)grid.add(widget, 0, index, positioner);
				case VERTICAL -> (Widget)grid.add(widget, index, 0, positioner);
			});
		}
	}
}
