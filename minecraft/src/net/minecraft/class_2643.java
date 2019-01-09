package net.minecraft;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_2643 extends class_2640 implements class_3000 {
	private static final Logger field_12133 = LogManager.getLogger();
	private long field_12131;
	private int field_12130;
	private class_2338 field_12132;
	private boolean field_12129;

	public class_2643() {
		super(class_2591.field_11906);
	}

	@Override
	public class_2487 method_11007(class_2487 arg) {
		super.method_11007(arg);
		arg.method_10544("Age", this.field_12131);
		if (this.field_12132 != null) {
			arg.method_10566("ExitPortal", class_2512.method_10692(this.field_12132));
		}

		if (this.field_12129) {
			arg.method_10556("ExactTeleport", this.field_12129);
		}

		return arg;
	}

	@Override
	public void method_11014(class_2487 arg) {
		super.method_11014(arg);
		this.field_12131 = arg.method_10537("Age");
		if (arg.method_10573("ExitPortal", 10)) {
			this.field_12132 = class_2512.method_10691(arg.method_10562("ExitPortal"));
		}

		this.field_12129 = arg.method_10577("ExactTeleport");
	}

	@Environment(EnvType.CLIENT)
	@Override
	public double method_11006() {
		return 65536.0;
	}

	@Override
	public void method_16896() {
		boolean bl = this.method_11420();
		boolean bl2 = this.method_11421();
		this.field_12131++;
		if (bl2) {
			this.field_12130--;
		} else if (!this.field_11863.field_9236) {
			List<class_1297> list = this.field_11863.method_8403(class_1297.class, new class_238(this.method_11016()));
			if (!list.isEmpty()) {
				this.method_11409((class_1297)list.get(0));
			}

			if (this.field_12131 % 2400L == 0L) {
				this.method_11411();
			}
		}

		if (bl != this.method_11420() || bl2 != this.method_11421()) {
			this.method_5431();
		}
	}

	public boolean method_11420() {
		return this.field_12131 < 200L;
	}

	public boolean method_11421() {
		return this.field_12130 > 0;
	}

	@Environment(EnvType.CLIENT)
	public float method_11417(float f) {
		return class_3532.method_15363(((float)this.field_12131 + f) / 200.0F, 0.0F, 1.0F);
	}

	@Environment(EnvType.CLIENT)
	public float method_11412(float f) {
		return 1.0F - class_3532.method_15363(((float)this.field_12130 - f) / 40.0F, 0.0F, 1.0F);
	}

	@Nullable
	@Override
	public class_2622 method_16886() {
		return new class_2622(this.field_11867, 8, this.method_16887());
	}

	@Override
	public class_2487 method_16887() {
		return this.method_11007(new class_2487());
	}

	public void method_11411() {
		if (!this.field_11863.field_9236) {
			this.field_12130 = 40;
			this.field_11863.method_8427(this.method_11016(), this.method_11010().method_11614(), 1, 0);
			this.method_5431();
		}
	}

	@Override
	public boolean method_11004(int i, int j) {
		if (i == 1) {
			this.field_12130 = 40;
			return true;
		} else {
			return super.method_11004(i, j);
		}
	}

	public void method_11409(class_1297 arg) {
		if (!this.field_11863.field_9236 && !this.method_11421()) {
			this.field_12130 = 100;
			if (this.field_12132 == null && this.field_11863.field_9247 instanceof class_2880) {
				this.method_11422();
			}

			if (this.field_12132 != null) {
				class_2338 lv = this.field_12129 ? this.field_12132 : this.method_11419();
				arg.method_5859((double)lv.method_10263() + 0.5, (double)lv.method_10264() + 0.5, (double)lv.method_10260() + 0.5);
			}

			this.method_11411();
		}
	}

	private class_2338 method_11419() {
		class_2338 lv = method_11410(this.field_11863, this.field_12132, 5, false);
		field_12133.debug("Best exit position for portal at {} is {}", this.field_12132, lv);
		return lv.method_10084();
	}

	private void method_11422() {
		class_243 lv = new class_243((double)this.method_11016().method_10263(), 0.0, (double)this.method_11016().method_10260()).method_1029();
		class_243 lv2 = lv.method_1021(1024.0);

		for (int i = 16; method_11414(this.field_11863, lv2).method_12031() > 0 && i-- > 0; lv2 = lv2.method_1019(lv.method_1021(-16.0))) {
			field_12133.debug("Skipping backwards past nonempty chunk at {}", lv2);
		}

		for (int var5 = 16; method_11414(this.field_11863, lv2).method_12031() == 0 && var5-- > 0; lv2 = lv2.method_1019(lv.method_1021(16.0))) {
			field_12133.debug("Skipping forward past empty chunk at {}", lv2);
		}

		field_12133.debug("Found chunk at {}", lv2);
		class_2818 lv3 = method_11414(this.field_11863, lv2);
		this.field_12132 = method_11413(lv3);
		if (this.field_12132 == null) {
			this.field_12132 = new class_2338(lv2.field_1352 + 0.5, 75.0, lv2.field_1350 + 0.5);
			field_12133.debug("Failed to find suitable block, settling on {}", this.field_12132);
			class_3031.field_13574
				.method_13151(
					this.field_11863,
					(class_2794<? extends class_2888>)this.field_11863.method_8398().method_12129(),
					new Random(this.field_12132.method_10063()),
					this.field_12132,
					class_3037.field_13603
				);
		} else {
			field_12133.debug("Found block at {}", this.field_12132);
		}

		this.field_12132 = method_11410(this.field_11863, this.field_12132, 16, true);
		field_12133.debug("Creating portal at {}", this.field_12132);
		this.field_12132 = this.field_12132.method_10086(10);
		this.method_11416(this.field_12132);
		this.method_5431();
	}

	private static class_2338 method_11410(class_1922 arg, class_2338 arg2, int i, boolean bl) {
		class_2338 lv = null;

		for (int j = -i; j <= i; j++) {
			for (int k = -i; k <= i; k++) {
				if (j != 0 || k != 0 || bl) {
					for (int l = 255; l > (lv == null ? 0 : lv.method_10264()); l--) {
						class_2338 lv2 = new class_2338(arg2.method_10263() + j, l, arg2.method_10260() + k);
						class_2680 lv3 = arg.method_8320(lv2);
						if (lv3.method_11603(arg, lv2) && (bl || lv3.method_11614() != class_2246.field_9987)) {
							lv = lv2;
							break;
						}
					}
				}
			}
		}

		return lv == null ? arg2 : lv;
	}

	private static class_2818 method_11414(class_1937 arg, class_243 arg2) {
		return arg.method_8497(class_3532.method_15357(arg2.field_1352 / 16.0), class_3532.method_15357(arg2.field_1350 / 16.0));
	}

	@Nullable
	private static class_2338 method_11413(class_2818 arg) {
		class_1923 lv = arg.method_12004();
		class_2338 lv2 = new class_2338(lv.method_8326(), 30, lv.method_8328());
		int i = arg.method_12031() + 16 - 1;
		class_2338 lv3 = new class_2338(lv.method_8327(), i, lv.method_8329());
		class_2338 lv4 = null;
		double d = 0.0;

		for (class_2338 lv5 : class_2338.method_10097(lv2, lv3)) {
			class_2680 lv6 = arg.method_8320(lv5);
			class_2338 lv7 = lv5.method_10084();
			class_2338 lv8 = lv5.method_10086(2);
			if (lv6.method_11614() == class_2246.field_10471 && !arg.method_8320(lv7).method_11603(arg, lv7) && !arg.method_8320(lv8).method_11603(arg, lv8)) {
				double e = lv5.method_10268(0.0, 0.0, 0.0);
				if (lv4 == null || e < d) {
					lv4 = lv5;
					d = e;
				}
			}
		}

		return lv4;
	}

	private void method_11416(class_2338 arg) {
		class_3031.field_13564
			.method_13151(this.field_11863, (class_2794<? extends class_2888>)this.field_11863.method_8398().method_12129(), new Random(), arg, new class_3018(false));
		class_2586 lv = this.field_11863.method_8321(arg);
		if (lv instanceof class_2643) {
			class_2643 lv2 = (class_2643)lv;
			lv2.field_12132 = new class_2338(this.method_11016());
			lv2.method_5431();
		} else {
			field_12133.warn("Couldn't save exit portal at {}", arg);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_11400(class_2350 arg) {
		return class_2248.method_9607(this.method_11010(), this.field_11863, this.method_11016(), arg);
	}

	@Environment(EnvType.CLIENT)
	public int method_11415() {
		int i = 0;

		for (class_2350 lv : class_2350.values()) {
			i += this.method_11400(lv) ? 1 : 0;
		}

		return i;
	}

	public void method_11418(class_2338 arg) {
		this.field_12129 = true;
		this.field_12132 = arg;
	}
}
