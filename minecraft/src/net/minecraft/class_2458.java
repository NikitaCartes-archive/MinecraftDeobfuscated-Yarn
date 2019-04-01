package net.minecraft;

import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2458 extends class_2459 {
	public static final class_2753 field_11443 = class_2383.field_11177;
	public static final class_2746 field_11444 = class_2459.field_11446;

	protected class_2458(class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(this.field_10647.method_11664().method_11657(field_11443, class_2350.field_11043).method_11657(field_11444, Boolean.valueOf(true)));
	}

	@Override
	public String method_9539() {
		return this.method_8389().method_7876();
	}

	@Override
	public class_265 method_9530(class_2680 arg, class_1922 arg2, class_2338 arg3, class_3726 arg4) {
		return class_2555.method_10841(arg);
	}

	@Override
	public boolean method_9558(class_2680 arg, class_1941 arg2, class_2338 arg3) {
		return class_2246.field_10099.method_9558(arg, arg2, arg3);
	}

	@Override
	public class_2680 method_9559(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
		return class_2246.field_10099.method_9559(arg, arg2, arg3, arg4, arg5, arg6);
	}

	@Nullable
	@Override
	public class_2680 method_9605(class_1750 arg) {
		class_2680 lv = class_2246.field_10099.method_9605(arg);
		return lv == null ? null : this.method_9564().method_11657(field_11443, lv.method_11654(field_11443));
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_9496(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		if ((Boolean)arg.method_11654(field_11444)) {
			class_2350 lv = ((class_2350)arg.method_11654(field_11443)).method_10153();
			double d = 0.27;
			double e = (double)arg3.method_10263() + 0.5 + (random.nextDouble() - 0.5) * 0.2 + 0.27 * (double)lv.method_10148();
			double f = (double)arg3.method_10264() + 0.7 + (random.nextDouble() - 0.5) * 0.2 + 0.22;
			double g = (double)arg3.method_10260() + 0.5 + (random.nextDouble() - 0.5) * 0.2 + 0.27 * (double)lv.method_10165();
			arg2.method_8406(class_2390.field_11188, e, f, g, 0.0, 0.0, 0.0);
		}
	}

	@Override
	protected boolean method_10488(class_1937 arg, class_2338 arg2, class_2680 arg3) {
		class_2350 lv = ((class_2350)arg3.method_11654(field_11443)).method_10153();
		return arg.method_8459(arg2.method_10093(lv), lv);
	}

	@Override
	public int method_9524(class_2680 arg, class_1922 arg2, class_2338 arg3, class_2350 arg4) {
		return arg.method_11654(field_11444) && arg.method_11654(field_11443) != arg4 ? 15 : 0;
	}

	@Override
	public class_2680 method_9598(class_2680 arg, class_2470 arg2) {
		return class_2246.field_10099.method_9598(arg, arg2);
	}

	@Override
	public class_2680 method_9569(class_2680 arg, class_2415 arg2) {
		return class_2246.field_10099.method_9569(arg, arg2);
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_11443, field_11444);
	}
}
