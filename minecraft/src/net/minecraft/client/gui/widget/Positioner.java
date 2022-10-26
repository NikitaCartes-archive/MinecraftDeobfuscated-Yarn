package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface Positioner {
	Positioner margin(int value);

	Positioner margin(int x, int y);

	Positioner margin(int left, int top, int right, int bottom);

	Positioner marginLeft(int marginLeft);

	Positioner marginTop(int marginTop);

	Positioner marginRight(int marginRight);

	Positioner marginBottom(int marginBottom);

	Positioner marginX(int marginX);

	Positioner marginY(int marginY);

	Positioner relative(float x, float y);

	Positioner relativeX(float relativeX);

	Positioner relativeY(float relativeY);

	default Positioner alignLeft() {
		return this.relativeX(0.0F);
	}

	default Positioner alignHorizontalCenter() {
		return this.relativeX(0.5F);
	}

	default Positioner alignRight() {
		return this.relativeX(1.0F);
	}

	default Positioner alignTop() {
		return this.relativeY(0.0F);
	}

	default Positioner alignVerticalCenter() {
		return this.relativeY(0.5F);
	}

	default Positioner alignBottom() {
		return this.relativeY(1.0F);
	}

	Positioner copy();

	Positioner.Impl toImpl();

	static Positioner create() {
		return new Positioner.Impl();
	}

	@Environment(EnvType.CLIENT)
	public static class Impl implements Positioner {
		public int marginLeft;
		public int marginTop;
		public int marginRight;
		public int marginBottom;
		public float relativeX;
		public float relativeY;

		public Impl() {
		}

		public Impl(Positioner.Impl original) {
			this.marginLeft = original.marginLeft;
			this.marginTop = original.marginTop;
			this.marginRight = original.marginRight;
			this.marginBottom = original.marginBottom;
			this.relativeX = original.relativeX;
			this.relativeY = original.relativeY;
		}

		public Positioner.Impl margin(int i) {
			return this.margin(i, i);
		}

		public Positioner.Impl margin(int i, int j) {
			return this.marginX(i).marginY(j);
		}

		public Positioner.Impl margin(int i, int j, int k, int l) {
			return this.marginLeft(i).marginRight(k).marginTop(j).marginBottom(l);
		}

		public Positioner.Impl marginLeft(int i) {
			this.marginLeft = i;
			return this;
		}

		public Positioner.Impl marginTop(int i) {
			this.marginTop = i;
			return this;
		}

		public Positioner.Impl marginRight(int i) {
			this.marginRight = i;
			return this;
		}

		public Positioner.Impl marginBottom(int i) {
			this.marginBottom = i;
			return this;
		}

		public Positioner.Impl marginX(int i) {
			return this.marginLeft(i).marginRight(i);
		}

		public Positioner.Impl marginY(int i) {
			return this.marginTop(i).marginBottom(i);
		}

		public Positioner.Impl relative(float f, float g) {
			this.relativeX = f;
			this.relativeY = g;
			return this;
		}

		public Positioner.Impl relativeX(float f) {
			this.relativeX = f;
			return this;
		}

		public Positioner.Impl relativeY(float f) {
			this.relativeY = f;
			return this;
		}

		public Positioner.Impl copy() {
			return new Positioner.Impl(this);
		}

		@Override
		public Positioner.Impl toImpl() {
			return this;
		}
	}
}
