package net.minecraft;

public class class_2442 extends class_2241 {
	public static final class_2754<class_2768> field_11365 = class_2741.field_12542;
	public static final class_2746 field_11364 = class_2741.field_12484;

	protected class_2442(class_2248.class_2251 arg) {
		super(true, arg);
		this.method_9590(this.field_10647.method_11664().method_11657(field_11365, class_2768.field_12665).method_11657(field_11364, Boolean.valueOf(false)));
	}

	protected boolean method_10413(class_1937 arg, class_2338 arg2, class_2680 arg3, boolean bl, int i) {
		if (i >= 8) {
			return false;
		} else {
			int j = arg2.method_10263();
			int k = arg2.method_10264();
			int l = arg2.method_10260();
			boolean bl2 = true;
			class_2768 lv = arg3.method_11654(field_11365);
			switch (lv) {
				case field_12665:
					if (bl) {
						l++;
					} else {
						l--;
					}
					break;
				case field_12674:
					if (bl) {
						j--;
					} else {
						j++;
					}
					break;
				case field_12667:
					if (bl) {
						j--;
					} else {
						j++;
						k++;
						bl2 = false;
					}

					lv = class_2768.field_12674;
					break;
				case field_12666:
					if (bl) {
						j--;
						k++;
						bl2 = false;
					} else {
						j++;
					}

					lv = class_2768.field_12674;
					break;
				case field_12670:
					if (bl) {
						l++;
					} else {
						l--;
						k++;
						bl2 = false;
					}

					lv = class_2768.field_12665;
					break;
				case field_12668:
					if (bl) {
						l++;
						k++;
						bl2 = false;
					} else {
						l--;
					}

					lv = class_2768.field_12665;
			}

			return this.method_10414(arg, new class_2338(j, k, l), bl, i, lv) ? true : bl2 && this.method_10414(arg, new class_2338(j, k - 1, l), bl, i, lv);
		}
	}

	protected boolean method_10414(class_1937 arg, class_2338 arg2, boolean bl, int i, class_2768 arg3) {
		class_2680 lv = arg.method_8320(arg2);
		if (lv.method_11614() != this) {
			return false;
		} else {
			class_2768 lv2 = lv.method_11654(field_11365);
			if (arg3 != class_2768.field_12674 || lv2 != class_2768.field_12665 && lv2 != class_2768.field_12670 && lv2 != class_2768.field_12668) {
				if (arg3 != class_2768.field_12665 || lv2 != class_2768.field_12674 && lv2 != class_2768.field_12667 && lv2 != class_2768.field_12666) {
					if (!(Boolean)lv.method_11654(field_11364)) {
						return false;
					} else {
						return arg.method_8479(arg2) ? true : this.method_10413(arg, arg2, lv, bl, i + 1);
					}
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
	}

	@Override
	protected void method_9477(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2248 arg4) {
		boolean bl = (Boolean)arg.method_11654(field_11364);
		boolean bl2 = arg2.method_8479(arg3) || this.method_10413(arg2, arg3, arg, true, 0) || this.method_10413(arg2, arg3, arg, false, 0);
		if (bl2 != bl) {
			arg2.method_8652(arg3, arg.method_11657(field_11364, Boolean.valueOf(bl2)), 3);
			arg2.method_8452(arg3.method_10074(), this);
			if (((class_2768)arg.method_11654(field_11365)).method_11897()) {
				arg2.method_8452(arg3.method_10084(), this);
			}
		}
	}

	@Override
	public class_2769<class_2768> method_9474() {
		return field_11365;
	}

	@Override
	public class_2680 method_9598(class_2680 arg, class_2470 arg2) {
		switch (arg2) {
			case field_11464:
				switch ((class_2768)arg.method_11654(field_11365)) {
					case field_12667:
						return arg.method_11657(field_11365, class_2768.field_12666);
					case field_12666:
						return arg.method_11657(field_11365, class_2768.field_12667);
					case field_12670:
						return arg.method_11657(field_11365, class_2768.field_12668);
					case field_12668:
						return arg.method_11657(field_11365, class_2768.field_12670);
					case field_12664:
						return arg.method_11657(field_11365, class_2768.field_12672);
					case field_12671:
						return arg.method_11657(field_11365, class_2768.field_12663);
					case field_12672:
						return arg.method_11657(field_11365, class_2768.field_12664);
					case field_12663:
						return arg.method_11657(field_11365, class_2768.field_12671);
				}
			case field_11465:
				switch ((class_2768)arg.method_11654(field_11365)) {
					case field_12665:
						return arg.method_11657(field_11365, class_2768.field_12674);
					case field_12674:
						return arg.method_11657(field_11365, class_2768.field_12665);
					case field_12667:
						return arg.method_11657(field_11365, class_2768.field_12670);
					case field_12666:
						return arg.method_11657(field_11365, class_2768.field_12668);
					case field_12670:
						return arg.method_11657(field_11365, class_2768.field_12666);
					case field_12668:
						return arg.method_11657(field_11365, class_2768.field_12667);
					case field_12664:
						return arg.method_11657(field_11365, class_2768.field_12663);
					case field_12671:
						return arg.method_11657(field_11365, class_2768.field_12664);
					case field_12672:
						return arg.method_11657(field_11365, class_2768.field_12671);
					case field_12663:
						return arg.method_11657(field_11365, class_2768.field_12672);
				}
			case field_11463:
				switch ((class_2768)arg.method_11654(field_11365)) {
					case field_12665:
						return arg.method_11657(field_11365, class_2768.field_12674);
					case field_12674:
						return arg.method_11657(field_11365, class_2768.field_12665);
					case field_12667:
						return arg.method_11657(field_11365, class_2768.field_12668);
					case field_12666:
						return arg.method_11657(field_11365, class_2768.field_12670);
					case field_12670:
						return arg.method_11657(field_11365, class_2768.field_12667);
					case field_12668:
						return arg.method_11657(field_11365, class_2768.field_12666);
					case field_12664:
						return arg.method_11657(field_11365, class_2768.field_12671);
					case field_12671:
						return arg.method_11657(field_11365, class_2768.field_12672);
					case field_12672:
						return arg.method_11657(field_11365, class_2768.field_12663);
					case field_12663:
						return arg.method_11657(field_11365, class_2768.field_12664);
				}
			default:
				return arg;
		}
	}

	@Override
	public class_2680 method_9569(class_2680 arg, class_2415 arg2) {
		class_2768 lv = arg.method_11654(field_11365);
		switch (arg2) {
			case field_11300:
				switch (lv) {
					case field_12670:
						return arg.method_11657(field_11365, class_2768.field_12668);
					case field_12668:
						return arg.method_11657(field_11365, class_2768.field_12670);
					case field_12664:
						return arg.method_11657(field_11365, class_2768.field_12663);
					case field_12671:
						return arg.method_11657(field_11365, class_2768.field_12672);
					case field_12672:
						return arg.method_11657(field_11365, class_2768.field_12671);
					case field_12663:
						return arg.method_11657(field_11365, class_2768.field_12664);
					default:
						return super.method_9569(arg, arg2);
				}
			case field_11301:
				switch (lv) {
					case field_12667:
						return arg.method_11657(field_11365, class_2768.field_12666);
					case field_12666:
						return arg.method_11657(field_11365, class_2768.field_12667);
					case field_12670:
					case field_12668:
					default:
						break;
					case field_12664:
						return arg.method_11657(field_11365, class_2768.field_12671);
					case field_12671:
						return arg.method_11657(field_11365, class_2768.field_12664);
					case field_12672:
						return arg.method_11657(field_11365, class_2768.field_12663);
					case field_12663:
						return arg.method_11657(field_11365, class_2768.field_12672);
				}
		}

		return super.method_9569(arg, arg2);
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_11365, field_11364);
	}
}
