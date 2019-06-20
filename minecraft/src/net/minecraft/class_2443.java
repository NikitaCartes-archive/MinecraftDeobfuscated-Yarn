package net.minecraft;

public class class_2443 extends class_2241 {
	public static final class_2754<class_2768> field_11369 = class_2741.field_12507;

	protected class_2443(class_2248.class_2251 arg) {
		super(false, arg);
		this.method_9590(this.field_10647.method_11664().method_11657(field_11369, class_2768.field_12665));
	}

	@Override
	protected void method_9477(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2248 arg4) {
		if (arg4.method_9564().method_11634() && new class_2452(arg2, arg3, arg).method_10460() == 3) {
			this.method_9475(arg2, arg3, arg, false);
		}
	}

	@Override
	public class_2769<class_2768> method_9474() {
		return field_11369;
	}

	@Override
	public class_2680 method_9598(class_2680 arg, class_2470 arg2) {
		switch (arg2) {
			case field_11464:
				switch ((class_2768)arg.method_11654(field_11369)) {
					case field_12667:
						return arg.method_11657(field_11369, class_2768.field_12666);
					case field_12666:
						return arg.method_11657(field_11369, class_2768.field_12667);
					case field_12670:
						return arg.method_11657(field_11369, class_2768.field_12668);
					case field_12668:
						return arg.method_11657(field_11369, class_2768.field_12670);
					case field_12664:
						return arg.method_11657(field_11369, class_2768.field_12672);
					case field_12671:
						return arg.method_11657(field_11369, class_2768.field_12663);
					case field_12672:
						return arg.method_11657(field_11369, class_2768.field_12664);
					case field_12663:
						return arg.method_11657(field_11369, class_2768.field_12671);
				}
			case field_11465:
				switch ((class_2768)arg.method_11654(field_11369)) {
					case field_12667:
						return arg.method_11657(field_11369, class_2768.field_12670);
					case field_12666:
						return arg.method_11657(field_11369, class_2768.field_12668);
					case field_12670:
						return arg.method_11657(field_11369, class_2768.field_12666);
					case field_12668:
						return arg.method_11657(field_11369, class_2768.field_12667);
					case field_12664:
						return arg.method_11657(field_11369, class_2768.field_12663);
					case field_12671:
						return arg.method_11657(field_11369, class_2768.field_12664);
					case field_12672:
						return arg.method_11657(field_11369, class_2768.field_12671);
					case field_12663:
						return arg.method_11657(field_11369, class_2768.field_12672);
					case field_12665:
						return arg.method_11657(field_11369, class_2768.field_12674);
					case field_12674:
						return arg.method_11657(field_11369, class_2768.field_12665);
				}
			case field_11463:
				switch ((class_2768)arg.method_11654(field_11369)) {
					case field_12667:
						return arg.method_11657(field_11369, class_2768.field_12668);
					case field_12666:
						return arg.method_11657(field_11369, class_2768.field_12670);
					case field_12670:
						return arg.method_11657(field_11369, class_2768.field_12667);
					case field_12668:
						return arg.method_11657(field_11369, class_2768.field_12666);
					case field_12664:
						return arg.method_11657(field_11369, class_2768.field_12671);
					case field_12671:
						return arg.method_11657(field_11369, class_2768.field_12672);
					case field_12672:
						return arg.method_11657(field_11369, class_2768.field_12663);
					case field_12663:
						return arg.method_11657(field_11369, class_2768.field_12664);
					case field_12665:
						return arg.method_11657(field_11369, class_2768.field_12674);
					case field_12674:
						return arg.method_11657(field_11369, class_2768.field_12665);
				}
			default:
				return arg;
		}
	}

	@Override
	public class_2680 method_9569(class_2680 arg, class_2415 arg2) {
		class_2768 lv = arg.method_11654(field_11369);
		switch (arg2) {
			case field_11300:
				switch (lv) {
					case field_12670:
						return arg.method_11657(field_11369, class_2768.field_12668);
					case field_12668:
						return arg.method_11657(field_11369, class_2768.field_12670);
					case field_12664:
						return arg.method_11657(field_11369, class_2768.field_12663);
					case field_12671:
						return arg.method_11657(field_11369, class_2768.field_12672);
					case field_12672:
						return arg.method_11657(field_11369, class_2768.field_12671);
					case field_12663:
						return arg.method_11657(field_11369, class_2768.field_12664);
					default:
						return super.method_9569(arg, arg2);
				}
			case field_11301:
				switch (lv) {
					case field_12667:
						return arg.method_11657(field_11369, class_2768.field_12666);
					case field_12666:
						return arg.method_11657(field_11369, class_2768.field_12667);
					case field_12670:
					case field_12668:
					default:
						break;
					case field_12664:
						return arg.method_11657(field_11369, class_2768.field_12671);
					case field_12671:
						return arg.method_11657(field_11369, class_2768.field_12664);
					case field_12672:
						return arg.method_11657(field_11369, class_2768.field_12663);
					case field_12663:
						return arg.method_11657(field_11369, class_2768.field_12672);
				}
		}

		return super.method_9569(arg, arg2);
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_11369);
	}
}
