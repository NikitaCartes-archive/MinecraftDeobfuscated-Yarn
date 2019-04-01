package net.minecraft;

import java.util.EnumSet;

public class class_1361 extends class_1352 {
	protected final class_1308 field_6486;
	protected class_1297 field_6484;
	protected final float field_6482;
	private int field_6483;
	private final float field_6481;
	protected final Class<? extends class_1309> field_6485;
	protected final class_4051 field_18087;

	public class_1361(class_1308 arg, Class<? extends class_1309> class_, float f) {
		this(arg, class_, f, 0.02F);
	}

	public class_1361(class_1308 arg, Class<? extends class_1309> class_, float f, float g) {
		this.field_6486 = arg;
		this.field_6485 = class_;
		this.field_6482 = f;
		this.field_6481 = g;
		this.method_6265(EnumSet.of(class_1352.class_4134.field_18406));
		if (class_ == class_1657.class) {
			this.field_18087 = new class_4051()
				.method_18418((double)f)
				.method_18421()
				.method_18417()
				.method_18423()
				.method_18420(arg2 -> class_1301.method_5913(arg).test(arg2));
		} else {
			this.field_18087 = new class_4051().method_18418((double)f).method_18421().method_18417().method_18423();
		}
	}

	@Override
	public boolean method_6264() {
		if (this.field_6486.method_6051().nextFloat() >= this.field_6481) {
			return false;
		} else {
			if (this.field_6486.method_5968() != null) {
				this.field_6484 = this.field_6486.method_5968();
			}

			if (this.field_6485 == class_1657.class) {
				this.field_6484 = this.field_6486
					.field_6002
					.method_18463(
						this.field_18087,
						this.field_6486,
						this.field_6486.field_5987,
						this.field_6486.field_6010 + (double)this.field_6486.method_5751(),
						this.field_6486.field_6035
					);
			} else {
				this.field_6484 = this.field_6486
					.field_6002
					.method_18465(
						this.field_6485,
						this.field_18087,
						this.field_6486,
						this.field_6486.field_5987,
						this.field_6486.field_6010 + (double)this.field_6486.method_5751(),
						this.field_6486.field_6035,
						this.field_6486.method_5829().method_1009((double)this.field_6482, 3.0, (double)this.field_6482)
					);
			}

			return this.field_6484 != null;
		}
	}

	@Override
	public boolean method_6266() {
		if (!this.field_6484.method_5805()) {
			return false;
		} else {
			return this.field_6486.method_5858(this.field_6484) > (double)(this.field_6482 * this.field_6482) ? false : this.field_6483 > 0;
		}
	}

	@Override
	public void method_6269() {
		this.field_6483 = 40 + this.field_6486.method_6051().nextInt(40);
	}

	@Override
	public void method_6270() {
		this.field_6484 = null;
	}

	@Override
	public void method_6268() {
		this.field_6486
			.method_5988()
			.method_6230(
				this.field_6484.field_5987,
				this.field_6484.field_6010 + (double)this.field_6484.method_5751(),
				this.field_6484.field_6035,
				(float)this.field_6486.method_5986(),
				(float)this.field_6486.method_5978()
			);
		this.field_6483--;
	}
}
