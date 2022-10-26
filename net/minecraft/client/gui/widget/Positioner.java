/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public interface Positioner {
    public Positioner margin(int var1);

    public Positioner margin(int var1, int var2);

    public Positioner margin(int var1, int var2, int var3, int var4);

    public Positioner marginLeft(int var1);

    public Positioner marginTop(int var1);

    public Positioner marginRight(int var1);

    public Positioner marginBottom(int var1);

    public Positioner marginX(int var1);

    public Positioner marginY(int var1);

    public Positioner relative(float var1, float var2);

    public Positioner relativeX(float var1);

    public Positioner relativeY(float var1);

    default public Positioner alignLeft() {
        return this.relativeX(0.0f);
    }

    default public Positioner alignHorizontalCenter() {
        return this.relativeX(0.5f);
    }

    default public Positioner alignRight() {
        return this.relativeX(1.0f);
    }

    default public Positioner alignTop() {
        return this.relativeY(0.0f);
    }

    default public Positioner alignVerticalCenter() {
        return this.relativeY(0.5f);
    }

    default public Positioner alignBottom() {
        return this.relativeY(1.0f);
    }

    public Positioner copy();

    public Impl toImpl();

    public static Positioner create() {
        return new Impl();
    }

    @Environment(value=EnvType.CLIENT)
    public static class Impl
    implements Positioner {
        public int marginLeft;
        public int marginTop;
        public int marginRight;
        public int marginBottom;
        public float relativeX;
        public float relativeY;

        public Impl() {
        }

        public Impl(Impl original) {
            this.marginLeft = original.marginLeft;
            this.marginTop = original.marginTop;
            this.marginRight = original.marginRight;
            this.marginBottom = original.marginBottom;
            this.relativeX = original.relativeX;
            this.relativeY = original.relativeY;
        }

        @Override
        public Impl margin(int i) {
            return this.margin(i, i);
        }

        @Override
        public Impl margin(int i, int j) {
            return this.marginX(i).marginY(j);
        }

        @Override
        public Impl margin(int i, int j, int k, int l) {
            return this.marginLeft(i).marginRight(k).marginTop(j).marginBottom(l);
        }

        @Override
        public Impl marginLeft(int i) {
            this.marginLeft = i;
            return this;
        }

        @Override
        public Impl marginTop(int i) {
            this.marginTop = i;
            return this;
        }

        @Override
        public Impl marginRight(int i) {
            this.marginRight = i;
            return this;
        }

        @Override
        public Impl marginBottom(int i) {
            this.marginBottom = i;
            return this;
        }

        @Override
        public Impl marginX(int i) {
            return this.marginLeft(i).marginRight(i);
        }

        @Override
        public Impl marginY(int i) {
            return this.marginTop(i).marginBottom(i);
        }

        @Override
        public Impl relative(float f, float g) {
            this.relativeX = f;
            this.relativeY = g;
            return this;
        }

        @Override
        public Impl relativeX(float f) {
            this.relativeX = f;
            return this;
        }

        @Override
        public Impl relativeY(float f) {
            this.relativeY = f;
            return this;
        }

        @Override
        public Impl copy() {
            return new Impl(this);
        }

        @Override
        public Impl toImpl() {
            return this;
        }

        @Override
        public /* synthetic */ Positioner copy() {
            return this.copy();
        }

        @Override
        public /* synthetic */ Positioner relativeY(float relativeY) {
            return this.relativeY(relativeY);
        }

        @Override
        public /* synthetic */ Positioner relativeX(float relativeX) {
            return this.relativeX(relativeX);
        }

        @Override
        public /* synthetic */ Positioner relative(float x, float y) {
            return this.relative(x, y);
        }

        @Override
        public /* synthetic */ Positioner marginY(int marginY) {
            return this.marginY(marginY);
        }

        @Override
        public /* synthetic */ Positioner marginX(int marginX) {
            return this.marginX(marginX);
        }

        @Override
        public /* synthetic */ Positioner marginBottom(int marginBottom) {
            return this.marginBottom(marginBottom);
        }

        @Override
        public /* synthetic */ Positioner marginRight(int marginRight) {
            return this.marginRight(marginRight);
        }

        @Override
        public /* synthetic */ Positioner marginTop(int marginTop) {
            return this.marginTop(marginTop);
        }

        @Override
        public /* synthetic */ Positioner marginLeft(int marginLeft) {
            return this.marginLeft(marginLeft);
        }

        @Override
        public /* synthetic */ Positioner margin(int left, int top, int right, int bottom) {
            return this.margin(left, top, right, bottom);
        }

        @Override
        public /* synthetic */ Positioner margin(int x, int y) {
            return this.margin(x, y);
        }

        @Override
        public /* synthetic */ Positioner margin(int value) {
            return this.margin(value);
        }
    }
}

