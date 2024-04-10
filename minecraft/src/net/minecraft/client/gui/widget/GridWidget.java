package net.minecraft.client.gui.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Util;
import net.minecraft.util.math.Divider;
import net.minecraft.util.math.MathHelper;

/**
 * A widget that positions its children in a 2D grid.
 * 
 * @see net.minecraft.util.math.Divider
 */
@Environment(EnvType.CLIENT)
public class GridWidget extends WrapperWidget {
	private final List<Widget> children = new ArrayList();
	private final List<GridWidget.Element> grids = new ArrayList();
	private final Positioner mainPositioner = Positioner.create();
	private int rowSpacing = 0;
	private int columnSpacing = 0;

	public GridWidget() {
		this(0, 0);
	}

	public GridWidget(int x, int y) {
		super(x, y, 0, 0);
	}

	@Override
	public void refreshPositions() {
		super.refreshPositions();
		int i = 0;
		int j = 0;

		for (GridWidget.Element element : this.grids) {
			i = Math.max(element.getRowEnd(), i);
			j = Math.max(element.getColumnEnd(), j);
		}

		int[] is = new int[j + 1];
		int[] js = new int[i + 1];

		for (GridWidget.Element element2 : this.grids) {
			int k = element2.getHeight() - (element2.occupiedRows - 1) * this.rowSpacing;
			Divider divider = new Divider(k, element2.occupiedRows);

			for (int l = element2.row; l <= element2.getRowEnd(); l++) {
				js[l] = Math.max(js[l], divider.nextInt());
			}

			int l = element2.getWidth() - (element2.occupiedColumns - 1) * this.columnSpacing;
			Divider divider2 = new Divider(l, element2.occupiedColumns);

			for (int m = element2.column; m <= element2.getColumnEnd(); m++) {
				is[m] = Math.max(is[m], divider2.nextInt());
			}
		}

		int[] ks = new int[j + 1];
		int[] ls = new int[i + 1];
		ks[0] = 0;

		for (int k = 1; k <= j; k++) {
			ks[k] = ks[k - 1] + is[k - 1] + this.columnSpacing;
		}

		ls[0] = 0;

		for (int k = 1; k <= i; k++) {
			ls[k] = ls[k - 1] + js[k - 1] + this.rowSpacing;
		}

		for (GridWidget.Element element3 : this.grids) {
			int l = 0;

			for (int n = element3.column; n <= element3.getColumnEnd(); n++) {
				l += is[n];
			}

			l += this.columnSpacing * (element3.occupiedColumns - 1);
			element3.setX(this.getX() + ks[element3.column], l);
			int n = 0;

			for (int m = element3.row; m <= element3.getRowEnd(); m++) {
				n += js[m];
			}

			n += this.rowSpacing * (element3.occupiedRows - 1);
			element3.setY(this.getY() + ls[element3.row], n);
		}

		this.width = ks[j] + is[j];
		this.height = ls[i] + js[i];
	}

	public <T extends Widget> T add(T widget, int row, int column) {
		return this.add(widget, row, column, this.copyPositioner());
	}

	public <T extends Widget> T add(T widget, int row, int column, Positioner positioner) {
		return this.add(widget, row, column, 1, 1, positioner);
	}

	public <T extends Widget> T add(T widget, int row, int column, Consumer<Positioner> callback) {
		return this.add(widget, row, column, 1, 1, Util.make(this.copyPositioner(), callback));
	}

	public <T extends Widget> T add(T widget, int row, int column, int occupiedRows, int occupiedColumns) {
		return this.add(widget, row, column, occupiedRows, occupiedColumns, this.copyPositioner());
	}

	public <T extends Widget> T add(T widget, int row, int column, int occupiedRows, int occupiedColumns, Positioner positioner) {
		if (occupiedRows < 1) {
			throw new IllegalArgumentException("Occupied rows must be at least 1");
		} else if (occupiedColumns < 1) {
			throw new IllegalArgumentException("Occupied columns must be at least 1");
		} else {
			this.grids.add(new GridWidget.Element(widget, row, column, occupiedRows, occupiedColumns, positioner));
			this.children.add(widget);
			return widget;
		}
	}

	public <T extends Widget> T add(T widget, int row, int column, int occupiedBelow, int occupiedAbove, Consumer<Positioner> callback) {
		return this.add(widget, row, column, occupiedBelow, occupiedAbove, Util.make(this.copyPositioner(), callback));
	}

	public GridWidget setColumnSpacing(int columnSpacing) {
		this.columnSpacing = columnSpacing;
		return this;
	}

	public GridWidget setRowSpacing(int rowSpacing) {
		this.rowSpacing = rowSpacing;
		return this;
	}

	public GridWidget setSpacing(int spacing) {
		return this.setColumnSpacing(spacing).setRowSpacing(spacing);
	}

	@Override
	public void forEachElement(Consumer<Widget> consumer) {
		this.children.forEach(consumer);
	}

	public Positioner copyPositioner() {
		return this.mainPositioner.copy();
	}

	public Positioner getMainPositioner() {
		return this.mainPositioner;
	}

	public GridWidget.Adder createAdder(int columns) {
		return new GridWidget.Adder(columns);
	}

	@Environment(EnvType.CLIENT)
	public final class Adder {
		private final int columns;
		private int totalOccupiedColumns;

		Adder(final int columns) {
			this.columns = columns;
		}

		public <T extends Widget> T add(T widget) {
			return this.add(widget, 1);
		}

		public <T extends Widget> T add(T widget, int occupiedColumns) {
			return this.add(widget, occupiedColumns, this.getMainPositioner());
		}

		public <T extends Widget> T add(T widget, Positioner positioner) {
			return this.add(widget, 1, positioner);
		}

		public <T extends Widget> T add(T widget, int occupiedColumns, Positioner positioner) {
			int i = this.totalOccupiedColumns / this.columns;
			int j = this.totalOccupiedColumns % this.columns;
			if (j + occupiedColumns > this.columns) {
				i++;
				j = 0;
				this.totalOccupiedColumns = MathHelper.roundUpToMultiple(this.totalOccupiedColumns, this.columns);
			}

			this.totalOccupiedColumns += occupiedColumns;
			return GridWidget.this.add(widget, i, j, 1, occupiedColumns, positioner);
		}

		public GridWidget getGridWidget() {
			return GridWidget.this;
		}

		public Positioner copyPositioner() {
			return GridWidget.this.copyPositioner();
		}

		public Positioner getMainPositioner() {
			return GridWidget.this.getMainPositioner();
		}
	}

	@Environment(EnvType.CLIENT)
	static class Element extends WrapperWidget.WrappedElement {
		final int row;
		final int column;
		final int occupiedRows;
		final int occupiedColumns;

		Element(Widget widget, int row, int column, int occupiedRows, int occupiedColumns, Positioner positioner) {
			super(widget, positioner.toImpl());
			this.row = row;
			this.column = column;
			this.occupiedRows = occupiedRows;
			this.occupiedColumns = occupiedColumns;
		}

		public int getRowEnd() {
			return this.row + this.occupiedRows - 1;
		}

		public int getColumnEnd() {
			return this.column + this.occupiedColumns - 1;
		}
	}
}
