package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_1151 implements class_1155 {
	private static final class_2561 field_5624 = new class_2588(
		"tutorial.move.title", class_1156.method_4913("forward"), class_1156.method_4913("left"), class_1156.method_4913("back"), class_1156.method_4913("right")
	);
	private static final class_2561 field_5617 = new class_2588("tutorial.move.description", class_1156.method_4913("jump"));
	private static final class_2561 field_5621 = new class_2588("tutorial.look.title");
	private static final class_2561 field_5614 = new class_2588("tutorial.look.description");
	private final class_1156 field_5618;
	private class_372 field_5622;
	private class_372 field_5623;
	private int field_5616;
	private int field_5615;
	private int field_5627;
	private boolean field_5620;
	private boolean field_5619;
	private int field_5626 = -1;
	private int field_5625 = -1;

	public class_1151(class_1156 arg) {
		this.field_5618 = arg;
	}

	@Override
	public void method_4899() {
		this.field_5616++;
		if (this.field_5620) {
			this.field_5615++;
			this.field_5620 = false;
		}

		if (this.field_5619) {
			this.field_5627++;
			this.field_5619 = false;
		}

		if (this.field_5626 == -1 && this.field_5615 > 40) {
			if (this.field_5622 != null) {
				this.field_5622.method_1993();
				this.field_5622 = null;
			}

			this.field_5626 = this.field_5616;
		}

		if (this.field_5625 == -1 && this.field_5627 > 40) {
			if (this.field_5623 != null) {
				this.field_5623.method_1993();
				this.field_5623 = null;
			}

			this.field_5625 = this.field_5616;
		}

		if (this.field_5626 != -1 && this.field_5625 != -1) {
			if (this.field_5618.method_4905() == class_1934.field_9215) {
				this.field_5618.method_4910(class_1157.field_5648);
			} else {
				this.field_5618.method_4910(class_1157.field_5653);
			}
		}

		if (this.field_5622 != null) {
			this.field_5622.method_1992((float)this.field_5615 / 40.0F);
		}

		if (this.field_5623 != null) {
			this.field_5623.method_1992((float)this.field_5627 / 40.0F);
		}

		if (this.field_5616 >= 100) {
			if (this.field_5626 == -1 && this.field_5622 == null) {
				this.field_5622 = new class_372(class_372.class_373.field_2230, field_5624, field_5617, true);
				this.field_5618.method_4914().method_1566().method_1999(this.field_5622);
			} else if (this.field_5626 != -1 && this.field_5616 - this.field_5626 >= 20 && this.field_5625 == -1 && this.field_5623 == null) {
				this.field_5623 = new class_372(class_372.class_373.field_2237, field_5621, field_5614, true);
				this.field_5618.method_4914().method_1566().method_1999(this.field_5623);
			}
		}
	}

	@Override
	public void method_4902() {
		if (this.field_5622 != null) {
			this.field_5622.method_1993();
			this.field_5622 = null;
		}

		if (this.field_5623 != null) {
			this.field_5623.method_1993();
			this.field_5623 = null;
		}
	}

	@Override
	public void method_4903(class_744 arg) {
		if (arg.field_3910 || arg.field_3909 || arg.field_3908 || arg.field_3906 || arg.field_3904) {
			this.field_5620 = true;
		}
	}

	@Override
	public void method_4901(double d, double e) {
		if (Math.abs(d) > 0.01 || Math.abs(e) > 0.01) {
			this.field_5619 = true;
		}
	}
}
