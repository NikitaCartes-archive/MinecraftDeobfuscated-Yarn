package net.minecraft;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2260 extends class_2237 {
	public static final class_2746[] field_10700 = new class_2746[]{class_2741.field_12554, class_2741.field_12500, class_2741.field_12531};
	protected static final class_265 field_10701 = class_259.method_1084(
		class_2248.method_9541(1.0, 0.0, 1.0, 15.0, 2.0, 15.0), class_2248.method_9541(7.0, 0.0, 7.0, 9.0, 14.0, 9.0)
	);

	public class_2260(class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(
			this.field_10647
				.method_11664()
				.method_11657(field_10700[0], Boolean.valueOf(false))
				.method_11657(field_10700[1], Boolean.valueOf(false))
				.method_11657(field_10700[2], Boolean.valueOf(false))
		);
	}

	@Override
	public class_2464 method_9604(class_2680 arg) {
		return class_2464.field_11458;
	}

	@Override
	public class_2586 method_10123(class_1922 arg) {
		return new class_2589();
	}

	@Override
	public class_265 method_9529(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return field_10701;
	}

	@Override
	public boolean method_9534(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1657 arg4, class_1268 arg5, class_2350 arg6, float f, float g, float h) {
		if (arg2.field_9236) {
			return true;
		} else {
			class_2586 lv = arg2.method_8321(arg3);
			if (lv instanceof class_2589) {
				arg4.method_17355((class_2589)lv);
				arg4.method_7281(class_3468.field_15407);
			}

			return true;
		}
	}

	@Override
	public void method_9567(class_1937 arg, class_2338 arg2, class_2680 arg3, class_1309 arg4, class_1799 arg5) {
		if (arg5.method_7938()) {
			class_2586 lv = arg.method_8321(arg2);
			if (lv instanceof class_2589) {
				((class_2589)lv).method_17488(arg5.method_7964());
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_9496(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		double d = (double)((float)arg3.method_10263() + 0.4F + random.nextFloat() * 0.2F);
		double e = (double)((float)arg3.method_10264() + 0.7F + random.nextFloat() * 0.3F);
		double f = (double)((float)arg3.method_10260() + 0.4F + random.nextFloat() * 0.2F);
		arg2.method_8406(class_2398.field_11251, d, e, f, 0.0, 0.0, 0.0);
	}

	@Override
	public void method_9536(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2680 arg4, boolean bl) {
		if (arg.method_11614() != arg4.method_11614()) {
			class_2586 lv = arg2.method_8321(arg3);
			if (lv instanceof class_2589) {
				class_1264.method_5451(arg2, arg3, (class_2589)lv);
			}

			super.method_9536(arg, arg2, arg3, arg4, bl);
		}
	}

	@Override
	public boolean method_9498(class_2680 arg) {
		return true;
	}

	@Override
	public int method_9572(class_2680 arg, class_1937 arg2, class_2338 arg3) {
		return class_1703.method_7608(arg2.method_8321(arg3));
	}

	@Override
	public class_1921 method_9551() {
		return class_1921.field_9174;
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_10700[0], field_10700[1], field_10700[2]);
	}

	@Override
	public boolean method_9516(class_2680 arg, class_1922 arg2, class_2338 arg3, class_10 arg4) {
		return false;
	}
}
