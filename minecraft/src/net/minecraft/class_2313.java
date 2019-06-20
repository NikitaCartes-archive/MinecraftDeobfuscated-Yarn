package net.minecraft;

import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public class class_2313 extends class_2241 {
	public static final class_2754<class_2768> field_10914 = class_2741.field_12542;
	public static final class_2746 field_10913 = class_2741.field_12484;

	public class_2313(class_2248.class_2251 arg) {
		super(true, arg);
		this.method_9590(this.field_10647.method_11664().method_11657(field_10913, Boolean.valueOf(false)).method_11657(field_10914, class_2768.field_12665));
	}

	@Override
	public int method_9563(class_1941 arg) {
		return 20;
	}

	@Override
	public boolean method_9506(class_2680 arg) {
		return true;
	}

	@Override
	public void method_9548(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1297 arg4) {
		if (!arg2.field_9236) {
			if (!(Boolean)arg.method_11654(field_10913)) {
				this.method_10002(arg2, arg3, arg);
			}
		}
	}

	@Override
	public void method_9588(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		if (!arg2.field_9236 && (Boolean)arg.method_11654(field_10913)) {
			this.method_10002(arg2, arg3, arg);
		}
	}

	@Override
	public int method_9524(class_2680 arg, class_1922 arg2, class_2338 arg3, class_2350 arg4) {
		return arg.method_11654(field_10913) ? 15 : 0;
	}

	@Override
	public int method_9603(class_2680 arg, class_1922 arg2, class_2338 arg3, class_2350 arg4) {
		if (!(Boolean)arg.method_11654(field_10913)) {
			return 0;
		} else {
			return arg4 == class_2350.field_11036 ? 15 : 0;
		}
	}

	private void method_10002(class_1937 arg, class_2338 arg2, class_2680 arg3) {
		boolean bl = (Boolean)arg3.method_11654(field_10913);
		boolean bl2 = false;
		List<class_1688> list = this.method_10001(arg, arg2, class_1688.class, null);
		if (!list.isEmpty()) {
			bl2 = true;
		}

		if (bl2 && !bl) {
			arg.method_8652(arg2, arg3.method_11657(field_10913, Boolean.valueOf(true)), 3);
			this.method_10003(arg, arg2, arg3, true);
			arg.method_8452(arg2, this);
			arg.method_8452(arg2.method_10074(), this);
			arg.method_16109(arg2);
		}

		if (!bl2 && bl) {
			arg.method_8652(arg2, arg3.method_11657(field_10913, Boolean.valueOf(false)), 3);
			this.method_10003(arg, arg2, arg3, false);
			arg.method_8452(arg2, this);
			arg.method_8452(arg2.method_10074(), this);
			arg.method_16109(arg2);
		}

		if (bl2) {
			arg.method_8397().method_8676(arg2, this, this.method_9563(arg));
		}

		arg.method_8455(arg2, this);
	}

	protected void method_10003(class_1937 arg, class_2338 arg2, class_2680 arg3, boolean bl) {
		class_2452 lv = new class_2452(arg, arg2, arg3);

		for (class_2338 lv2 : lv.method_10457()) {
			class_2680 lv3 = arg.method_8320(lv2);
			lv3.method_11622(arg, lv2, lv3.method_11614(), arg2, false);
		}
	}

	@Override
	public void method_9615(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2680 arg4, boolean bl) {
		if (arg4.method_11614() != arg.method_11614()) {
			super.method_9615(arg, arg2, arg3, arg4, bl);
			this.method_10002(arg2, arg3, arg);
		}
	}

	@Override
	public class_2769<class_2768> method_9474() {
		return field_10914;
	}

	@Override
	public boolean method_9498(class_2680 arg) {
		return true;
	}

	@Override
	public int method_9572(class_2680 arg, class_1937 arg2, class_2338 arg3) {
		if ((Boolean)arg.method_11654(field_10913)) {
			List<class_1697> list = this.method_10001(arg2, arg3, class_1697.class, null);
			if (!list.isEmpty()) {
				return ((class_1697)list.get(0)).method_7567().method_8304();
			}

			List<class_1688> list2 = this.method_10001(arg2, arg3, class_1688.class, class_1301.field_6152);
			if (!list2.isEmpty()) {
				return class_1703.method_7618((class_1263)list2.get(0));
			}
		}

		return 0;
	}

	protected <T extends class_1688> List<T> method_10001(class_1937 arg, class_2338 arg2, Class<T> class_, @Nullable Predicate<class_1297> predicate) {
		return arg.method_8390(class_, this.method_10004(arg2), predicate);
	}

	private class_238 method_10004(class_2338 arg) {
		float f = 0.2F;
		return new class_238(
			(double)((float)arg.method_10263() + 0.2F),
			(double)arg.method_10264(),
			(double)((float)arg.method_10260() + 0.2F),
			(double)((float)(arg.method_10263() + 1) - 0.2F),
			(double)((float)(arg.method_10264() + 1) - 0.2F),
			(double)((float)(arg.method_10260() + 1) - 0.2F)
		);
	}

	@Override
	public class_2680 method_9598(class_2680 arg, class_2470 arg2) {
		switch (arg2) {
			case field_11464:
				switch ((class_2768)arg.method_11654(field_10914)) {
					case field_12667:
						return arg.method_11657(field_10914, class_2768.field_12666);
					case field_12666:
						return arg.method_11657(field_10914, class_2768.field_12667);
					case field_12670:
						return arg.method_11657(field_10914, class_2768.field_12668);
					case field_12668:
						return arg.method_11657(field_10914, class_2768.field_12670);
					case field_12664:
						return arg.method_11657(field_10914, class_2768.field_12672);
					case field_12671:
						return arg.method_11657(field_10914, class_2768.field_12663);
					case field_12672:
						return arg.method_11657(field_10914, class_2768.field_12664);
					case field_12663:
						return arg.method_11657(field_10914, class_2768.field_12671);
				}
			case field_11465:
				switch ((class_2768)arg.method_11654(field_10914)) {
					case field_12667:
						return arg.method_11657(field_10914, class_2768.field_12670);
					case field_12666:
						return arg.method_11657(field_10914, class_2768.field_12668);
					case field_12670:
						return arg.method_11657(field_10914, class_2768.field_12666);
					case field_12668:
						return arg.method_11657(field_10914, class_2768.field_12667);
					case field_12664:
						return arg.method_11657(field_10914, class_2768.field_12663);
					case field_12671:
						return arg.method_11657(field_10914, class_2768.field_12664);
					case field_12672:
						return arg.method_11657(field_10914, class_2768.field_12671);
					case field_12663:
						return arg.method_11657(field_10914, class_2768.field_12672);
					case field_12665:
						return arg.method_11657(field_10914, class_2768.field_12674);
					case field_12674:
						return arg.method_11657(field_10914, class_2768.field_12665);
				}
			case field_11463:
				switch ((class_2768)arg.method_11654(field_10914)) {
					case field_12667:
						return arg.method_11657(field_10914, class_2768.field_12668);
					case field_12666:
						return arg.method_11657(field_10914, class_2768.field_12670);
					case field_12670:
						return arg.method_11657(field_10914, class_2768.field_12667);
					case field_12668:
						return arg.method_11657(field_10914, class_2768.field_12666);
					case field_12664:
						return arg.method_11657(field_10914, class_2768.field_12671);
					case field_12671:
						return arg.method_11657(field_10914, class_2768.field_12672);
					case field_12672:
						return arg.method_11657(field_10914, class_2768.field_12663);
					case field_12663:
						return arg.method_11657(field_10914, class_2768.field_12664);
					case field_12665:
						return arg.method_11657(field_10914, class_2768.field_12674);
					case field_12674:
						return arg.method_11657(field_10914, class_2768.field_12665);
				}
			default:
				return arg;
		}
	}

	@Override
	public class_2680 method_9569(class_2680 arg, class_2415 arg2) {
		class_2768 lv = arg.method_11654(field_10914);
		switch (arg2) {
			case field_11300:
				switch (lv) {
					case field_12670:
						return arg.method_11657(field_10914, class_2768.field_12668);
					case field_12668:
						return arg.method_11657(field_10914, class_2768.field_12670);
					case field_12664:
						return arg.method_11657(field_10914, class_2768.field_12663);
					case field_12671:
						return arg.method_11657(field_10914, class_2768.field_12672);
					case field_12672:
						return arg.method_11657(field_10914, class_2768.field_12671);
					case field_12663:
						return arg.method_11657(field_10914, class_2768.field_12664);
					default:
						return super.method_9569(arg, arg2);
				}
			case field_11301:
				switch (lv) {
					case field_12667:
						return arg.method_11657(field_10914, class_2768.field_12666);
					case field_12666:
						return arg.method_11657(field_10914, class_2768.field_12667);
					case field_12670:
					case field_12668:
					default:
						break;
					case field_12664:
						return arg.method_11657(field_10914, class_2768.field_12671);
					case field_12671:
						return arg.method_11657(field_10914, class_2768.field_12664);
					case field_12672:
						return arg.method_11657(field_10914, class_2768.field_12663);
					case field_12663:
						return arg.method_11657(field_10914, class_2768.field_12672);
				}
		}

		return super.method_9569(arg, arg2);
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_10914, field_10913);
	}
}
