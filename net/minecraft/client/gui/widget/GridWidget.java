/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.Positioner;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.WrapperWidget;
import net.minecraft.util.math.Divider;
import net.minecraft.util.math.MathHelper;

/**
 * A widget that positions its children in a 2D grid.
 * 
 * @see net.minecraft.util.math.Divider
 */
@Environment(value=EnvType.CLIENT)
public class GridWidget
extends WrapperWidget {
    private final List<Widget> children = new ArrayList<Widget>();
    private final List<Element> grids = new ArrayList<Element>();
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
        int m;
        int l;
        int k;
        super.refreshPositions();
        int i = 0;
        int j = 0;
        for (Element element : this.grids) {
            i = Math.max(element.getRowEnd(), i);
            j = Math.max(element.getColumnEnd(), j);
        }
        int[] is = new int[j + 1];
        int[] js = new int[i + 1];
        for (Element element2 : this.grids) {
            k = element2.getHeight() - (element2.occupiedRows - 1) * this.rowSpacing;
            Divider divider = new Divider(k, element2.occupiedRows);
            for (l = element2.row; l <= element2.getRowEnd(); ++l) {
                js[l] = Math.max(js[l], divider.nextInt());
            }
            l = element2.getWidth() - (element2.occupiedColumns - 1) * this.columnSpacing;
            Divider divider2 = new Divider(l, element2.occupiedColumns);
            for (m = element2.column; m <= element2.getColumnEnd(); ++m) {
                is[m] = Math.max(is[m], divider2.nextInt());
            }
        }
        int[] ks = new int[j + 1];
        int[] ls = new int[i + 1];
        ks[0] = 0;
        for (k = 1; k <= j; ++k) {
            ks[k] = ks[k - 1] + is[k - 1] + this.columnSpacing;
        }
        ls[0] = 0;
        for (k = 1; k <= i; ++k) {
            ls[k] = ls[k - 1] + js[k - 1] + this.rowSpacing;
        }
        for (Element element3 : this.grids) {
            int n;
            l = 0;
            for (n = element3.column; n <= element3.getColumnEnd(); ++n) {
                l += is[n];
            }
            element3.setX(this.getX() + ks[element3.column], l += this.columnSpacing * (element3.occupiedColumns - 1));
            n = 0;
            for (m = element3.row; m <= element3.getRowEnd(); ++m) {
                n += js[m];
            }
            element3.setY(this.getY() + ls[element3.row], n += this.rowSpacing * (element3.occupiedRows - 1));
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

    public <T extends Widget> T add(T widget, int row, int column, int occupiedRows, int occupiedColumns) {
        return this.add(widget, row, column, occupiedRows, occupiedColumns, this.copyPositioner());
    }

    public <T extends Widget> T add(T widget, int row, int column, int occupiedRows, int occupiedColumns, Positioner positioner) {
        if (occupiedRows < 1) {
            throw new IllegalArgumentException("Occupied rows must be at least 1");
        }
        if (occupiedColumns < 1) {
            throw new IllegalArgumentException("Occupied columns must be at least 1");
        }
        this.grids.add(new Element(widget, row, column, occupiedRows, occupiedColumns, positioner));
        this.children.add(widget);
        return widget;
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

    public Adder createAdder(int columns) {
        return new Adder(columns);
    }

    @Environment(value=EnvType.CLIENT)
    static class Element
    extends WrapperWidget.WrappedElement {
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

    @Environment(value=EnvType.CLIENT)
    public final class Adder {
        private final int columns;
        private int totalOccupiedColumns;

        Adder(int columns) {
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
                ++i;
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
}

