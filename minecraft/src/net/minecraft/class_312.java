package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_312 {
	private final class_310 field_1779;
	private boolean field_1791;
	private boolean field_1790;
	private boolean field_1788;
	private double field_1795;
	private double field_1794;
	private int field_1781;
	private int field_1780 = -1;
	private boolean field_1784 = true;
	private int field_1796;
	private double field_1792;
	private final class_3540 field_1793 = new class_3540();
	private final class_3540 field_1782 = new class_3540();
	private double field_1789;
	private double field_1787;
	private double field_1786;
	private double field_1785 = Double.MIN_VALUE;
	private boolean field_1783;

	public class_312(class_310 arg) {
		this.field_1779 = arg;
	}

	private void method_1601(long l, int i, int j, int k) {
		if (l == this.field_1779.field_1704.method_4490()) {
			boolean bl = j == 1;
			if (class_310.field_1703 && i == 0) {
				if (bl) {
					if ((k & 2) == 2) {
						i = 1;
						this.field_1781++;
					}
				} else if (this.field_1781 > 0) {
					i = 1;
					this.field_1781--;
				}
			}

			int m = i;
			if (bl) {
				if (this.field_1779.field_1690.field_1854 && this.field_1796++ > 0) {
					return;
				}

				this.field_1780 = m;
				this.field_1792 = class_3673.method_15974();
			} else if (this.field_1780 != -1) {
				if (this.field_1779.field_1690.field_1854 && --this.field_1796 > 0) {
					return;
				}

				this.field_1780 = -1;
			}

			boolean[] bls = new boolean[]{false};
			if (this.field_1779.field_18175 == null) {
				if (this.field_1779.field_1755 == null) {
					if (!this.field_1783 && bl) {
						this.method_1612();
					}
				} else {
					double d = this.field_1795 * (double)this.field_1779.field_1704.method_4486() / (double)this.field_1779.field_1704.method_4480();
					double e = this.field_1794 * (double)this.field_1779.field_1704.method_4502() / (double)this.field_1779.field_1704.method_4507();
					if (bl) {
						class_437.wrapScreenError(
							() -> bls[0] = this.field_1779.field_1755.mouseClicked(d, e, m), "mouseClicked event handler", this.field_1779.field_1755.getClass().getCanonicalName()
						);
					} else {
						class_437.wrapScreenError(
							() -> bls[0] = this.field_1779.field_1755.mouseReleased(d, e, m),
							"mouseReleased event handler",
							this.field_1779.field_1755.getClass().getCanonicalName()
						);
					}
				}
			}

			if (!bls[0] && (this.field_1779.field_1755 == null || this.field_1779.field_1755.passEvents) && this.field_1779.field_18175 == null) {
				if (m == 0) {
					this.field_1791 = bl;
				} else if (m == 2) {
					this.field_1790 = bl;
				} else if (m == 1) {
					this.field_1788 = bl;
				}

				class_304.method_1416(class_3675.class_307.field_1672.method_1447(m), bl);
				if (bl) {
					if (this.field_1779.field_1724.method_7325() && m == 2) {
						this.field_1779.field_1705.method_1739().method_1983();
					} else {
						class_304.method_1420(class_3675.class_307.field_1672.method_1447(m));
					}
				}
			}
		}
	}

	private void method_1598(long l, double d, double e) {
		if (l == class_310.method_1551().field_1704.method_4490()) {
			double f = e * this.field_1779.field_1690.field_1889;
			if (this.field_1779.field_18175 == null) {
				if (this.field_1779.field_1755 != null) {
					double g = this.field_1795 * (double)this.field_1779.field_1704.method_4486() / (double)this.field_1779.field_1704.method_4480();
					double h = this.field_1794 * (double)this.field_1779.field_1704.method_4502() / (double)this.field_1779.field_1704.method_4507();
					this.field_1779.field_1755.mouseScrolled(g, h, f);
				} else if (this.field_1779.field_1724 != null) {
					if (this.field_1786 != 0.0 && Math.signum(f) != Math.signum(this.field_1786)) {
						this.field_1786 = 0.0;
					}

					this.field_1786 += f;
					float i = (float)((int)this.field_1786);
					if (i == 0.0F) {
						return;
					}

					this.field_1786 -= (double)i;
					if (this.field_1779.field_1724.method_7325()) {
						if (this.field_1779.field_1705.method_1739().method_1980()) {
							this.field_1779.field_1705.method_1739().method_1976((double)(-i));
						} else {
							float j = class_3532.method_15363(this.field_1779.field_1724.field_7503.method_7252() + i * 0.005F, 0.0F, 0.2F);
							this.field_1779.field_1724.field_7503.method_7248(j);
						}
					} else {
						this.field_1779.field_1724.field_7514.method_7373((double)i);
					}
				}
			}
		}
	}

	public void method_1607(long l) {
		class_3675.method_15983(l, this::method_1600, this::method_1601, this::method_1598);
	}

	private void method_1600(long l, double d, double e) {
		if (l == class_310.method_1551().field_1704.method_4490()) {
			if (this.field_1784) {
				this.field_1795 = d;
				this.field_1794 = e;
				this.field_1784 = false;
			}

			class_364 lv = this.field_1779.field_1755;
			if (lv != null && this.field_1779.field_18175 == null) {
				double f = d * (double)this.field_1779.field_1704.method_4486() / (double)this.field_1779.field_1704.method_4480();
				double g = e * (double)this.field_1779.field_1704.method_4502() / (double)this.field_1779.field_1704.method_4507();
				class_437.wrapScreenError(() -> lv.method_16014(f, g), "mouseMoved event handler", lv.getClass().getCanonicalName());
				if (this.field_1780 != -1 && this.field_1792 > 0.0) {
					double h = (d - this.field_1795) * (double)this.field_1779.field_1704.method_4486() / (double)this.field_1779.field_1704.method_4480();
					double i = (e - this.field_1794) * (double)this.field_1779.field_1704.method_4502() / (double)this.field_1779.field_1704.method_4507();
					class_437.wrapScreenError(() -> lv.mouseDragged(f, g, this.field_1780, h, i), "mouseDragged event handler", lv.getClass().getCanonicalName());
				}
			}

			this.field_1779.method_16011().method_15396("mouse");
			if (this.method_1613() && this.field_1779.method_1569()) {
				this.field_1789 = this.field_1789 + (d - this.field_1795);
				this.field_1787 = this.field_1787 + (e - this.field_1794);
			}

			this.method_1606();
			this.field_1795 = d;
			this.field_1794 = e;
			this.field_1779.method_16011().method_15407();
		}
	}

	public void method_1606() {
		double d = class_3673.method_15974();
		double e = d - this.field_1785;
		this.field_1785 = d;
		if (this.method_1613() && this.field_1779.method_1569()) {
			double f = this.field_1779.field_1690.field_1843 * 0.6F + 0.2F;
			double g = f * f * f * 8.0;
			double j;
			double k;
			if (this.field_1779.field_1690.field_1914) {
				double h = this.field_1793.method_15429(this.field_1789 * g, e * g);
				double i = this.field_1782.method_15429(this.field_1787 * g, e * g);
				j = h;
				k = i;
			} else {
				this.field_1793.method_15428();
				this.field_1782.method_15428();
				j = this.field_1789 * g;
				k = this.field_1787 * g;
			}

			this.field_1789 = 0.0;
			this.field_1787 = 0.0;
			int l = 1;
			if (this.field_1779.field_1690.field_1865) {
				l = -1;
			}

			this.field_1779.method_1577().method_4908(j, k);
			if (this.field_1779.field_1724 != null) {
				this.field_1779.field_1724.method_5872(j, k * (double)l);
			}
		} else {
			this.field_1789 = 0.0;
			this.field_1787 = 0.0;
		}
	}

	public boolean method_1608() {
		return this.field_1791;
	}

	public boolean method_1609() {
		return this.field_1788;
	}

	public double method_1603() {
		return this.field_1795;
	}

	public double method_1604() {
		return this.field_1794;
	}

	public void method_1599() {
		this.field_1784 = true;
	}

	public boolean method_1613() {
		return this.field_1783;
	}

	public void method_1612() {
		if (this.field_1779.method_1569()) {
			if (!this.field_1783) {
				if (!class_310.field_1703) {
					class_304.method_1424();
				}

				this.field_1783 = true;
				this.field_1795 = (double)(this.field_1779.field_1704.method_4480() / 2);
				this.field_1794 = (double)(this.field_1779.field_1704.method_4507() / 2);
				class_3675.method_15984(this.field_1779.field_1704.method_4490(), 212995, this.field_1795, this.field_1794);
				this.field_1779.method_1507(null);
				this.field_1779.field_1771 = 10000;
			}
		}
	}

	public void method_1610() {
		if (this.field_1783) {
			this.field_1783 = false;
			this.field_1795 = (double)(this.field_1779.field_1704.method_4480() / 2);
			this.field_1794 = (double)(this.field_1779.field_1704.method_4507() / 2);
			class_3675.method_15984(this.field_1779.field_1704.method_4490(), 212993, this.field_1795, this.field_1794);
		}
	}
}
