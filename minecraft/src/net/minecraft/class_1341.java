package net.minecraft;

import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;

public class class_1341 extends class_1352 {
	private static final class_4051 field_18086 = new class_4051().method_18418(8.0).method_18417().method_18421().method_18422();
	protected final class_1429 field_6404;
	private final Class<? extends class_1429> field_6403;
	protected final class_1937 field_6405;
	protected class_1429 field_6406;
	private int field_6402;
	private final double field_6407;

	public class_1341(class_1429 arg, double d) {
		this(arg, d, arg.getClass());
	}

	public class_1341(class_1429 arg, double d, Class<? extends class_1429> class_) {
		this.field_6404 = arg;
		this.field_6405 = arg.field_6002;
		this.field_6403 = class_;
		this.field_6407 = d;
		this.method_6265(EnumSet.of(class_1352.class_4134.field_18405, class_1352.class_4134.field_18406));
	}

	@Override
	public boolean method_6264() {
		if (!this.field_6404.method_6479()) {
			return false;
		} else {
			this.field_6406 = this.method_6250();
			return this.field_6406 != null;
		}
	}

	@Override
	public boolean method_6266() {
		return this.field_6406.method_5805() && this.field_6406.method_6479() && this.field_6402 < 60;
	}

	@Override
	public void method_6270() {
		this.field_6406 = null;
		this.field_6402 = 0;
	}

	@Override
	public void method_6268() {
		this.field_6404.method_5988().method_6226(this.field_6406, 10.0F, (float)this.field_6404.method_5978());
		this.field_6404.method_5942().method_6335(this.field_6406, this.field_6407);
		this.field_6402++;
		if (this.field_6402 >= 60 && this.field_6404.method_5858(this.field_6406) < 9.0) {
			this.method_6249();
		}
	}

	@Nullable
	private class_1429 method_6250() {
		List<class_1429> list = this.field_6405.method_18466(this.field_6403, field_18086, this.field_6404, this.field_6404.method_5829().method_1014(8.0));
		double d = Double.MAX_VALUE;
		class_1429 lv = null;

		for (class_1429 lv2 : list) {
			if (this.field_6404.method_6474(lv2) && this.field_6404.method_5858(lv2) < d) {
				lv = lv2;
				d = this.field_6404.method_5858(lv2);
			}
		}

		return lv;
	}

	protected void method_6249() {
		class_1296 lv = this.field_6404.method_5613(this.field_6406);
		if (lv != null) {
			class_3222 lv2 = this.field_6404.method_6478();
			if (lv2 == null && this.field_6406.method_6478() != null) {
				lv2 = this.field_6406.method_6478();
			}

			if (lv2 != null) {
				lv2.method_7281(class_3468.field_15410);
				class_174.field_1190.method_855(lv2, this.field_6404, this.field_6406, lv);
			}

			this.field_6404.method_5614(6000);
			this.field_6406.method_5614(6000);
			this.field_6404.method_6477();
			this.field_6406.method_6477();
			lv.method_5614(-24000);
			lv.method_5808(this.field_6404.field_5987, this.field_6404.field_6010, this.field_6404.field_6035, 0.0F, 0.0F);
			this.field_6405.method_8649(lv);
			Random random = this.field_6404.method_6051();
			this.method_6251(random);
			if (this.field_6405.method_8450().method_8355("doMobLoot")) {
				this.field_6405
					.method_8649(new class_1303(this.field_6405, this.field_6404.field_5987, this.field_6404.field_6010, this.field_6404.field_6035, random.nextInt(7) + 1));
			}
		}
	}

	protected void method_6251(Random random) {
		for (int i = 0; i < 7; i++) {
			double d = random.nextGaussian() * 0.02;
			double e = random.nextGaussian() * 0.02;
			double f = random.nextGaussian() * 0.02;
			double g = random.nextDouble() * (double)this.field_6404.method_17681() * 2.0 - (double)this.field_6404.method_17681();
			double h = 0.5 + random.nextDouble() * (double)this.field_6404.method_17682();
			double j = random.nextDouble() * (double)this.field_6404.method_17681() * 2.0 - (double)this.field_6404.method_17681();
			this.field_6405.method_8406(class_2398.field_11201, this.field_6404.field_5987 + g, this.field_6404.field_6010 + h, this.field_6404.field_6035 + j, d, e, f);
		}
	}
}
