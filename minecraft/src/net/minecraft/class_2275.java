package net.minecraft;

public class class_2275 extends class_2248 {
	public static final class_2758 field_10745 = class_2741.field_12513;
	private static final class_265 field_10747 = method_9541(2.0, 4.0, 2.0, 14.0, 16.0, 14.0);
	protected static final class_265 field_10746 = class_259.method_1072(
		class_259.method_1077(),
		class_259.method_17786(
			method_9541(0.0, 0.0, 4.0, 16.0, 3.0, 12.0), method_9541(4.0, 0.0, 0.0, 12.0, 3.0, 16.0), method_9541(2.0, 0.0, 2.0, 14.0, 3.0, 14.0), field_10747
		),
		class_247.field_16886
	);

	public class_2275(class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(this.field_10647.method_11664().method_11657(field_10745, Integer.valueOf(0)));
	}

	@Override
	public class_265 method_9530(class_2680 arg, class_1922 arg2, class_2338 arg3, class_3726 arg4) {
		return field_10746;
	}

	@Override
	public boolean method_9601(class_2680 arg) {
		return false;
	}

	@Override
	public class_265 method_9584(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return field_10747;
	}

	@Override
	public void method_9548(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1297 arg4) {
		int i = (Integer)arg.method_11654(field_10745);
		float f = (float)arg3.method_10264() + (6.0F + (float)(3 * i)) / 16.0F;
		if (!arg2.field_9236 && arg4.method_5809() && i > 0 && arg4.method_5829().field_1322 <= (double)f) {
			arg4.method_5646();
			this.method_9726(arg2, arg3, arg, i - 1);
		}
	}

	@Override
	public boolean method_9534(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1657 arg4, class_1268 arg5, class_3965 arg6) {
		class_1799 lv = arg4.method_5998(arg5);
		if (lv.method_7960()) {
			return true;
		} else {
			int i = (Integer)arg.method_11654(field_10745);
			class_1792 lv2 = lv.method_7909();
			if (lv2 == class_1802.field_8705) {
				if (i < 3 && !arg2.field_9236) {
					if (!arg4.field_7503.field_7477) {
						arg4.method_6122(arg5, new class_1799(class_1802.field_8550));
					}

					arg4.method_7281(class_3468.field_15430);
					this.method_9726(arg2, arg3, arg, 3);
					arg2.method_8396(null, arg3, class_3417.field_14834, class_3419.field_15245, 1.0F, 1.0F);
				}

				return true;
			} else if (lv2 == class_1802.field_8550) {
				if (i == 3 && !arg2.field_9236) {
					if (!arg4.field_7503.field_7477) {
						lv.method_7934(1);
						if (lv.method_7960()) {
							arg4.method_6122(arg5, new class_1799(class_1802.field_8705));
						} else if (!arg4.field_7514.method_7394(new class_1799(class_1802.field_8705))) {
							arg4.method_7328(new class_1799(class_1802.field_8705), false);
						}
					}

					arg4.method_7281(class_3468.field_15373);
					this.method_9726(arg2, arg3, arg, 0);
					arg2.method_8396(null, arg3, class_3417.field_15126, class_3419.field_15245, 1.0F, 1.0F);
				}

				return true;
			} else if (lv2 == class_1802.field_8469) {
				if (i > 0 && !arg2.field_9236) {
					if (!arg4.field_7503.field_7477) {
						class_1799 lv3 = class_1844.method_8061(new class_1799(class_1802.field_8574), class_1847.field_8991);
						arg4.method_7281(class_3468.field_15373);
						lv.method_7934(1);
						if (lv.method_7960()) {
							arg4.method_6122(arg5, lv3);
						} else if (!arg4.field_7514.method_7394(lv3)) {
							arg4.method_7328(lv3, false);
						} else if (arg4 instanceof class_3222) {
							((class_3222)arg4).method_14204(arg4.field_7498);
						}
					}

					arg2.method_8396(null, arg3, class_3417.field_14779, class_3419.field_15245, 1.0F, 1.0F);
					this.method_9726(arg2, arg3, arg, i - 1);
				}

				return true;
			} else if (lv2 == class_1802.field_8574 && class_1844.method_8063(lv) == class_1847.field_8991) {
				if (i < 3 && !arg2.field_9236) {
					if (!arg4.field_7503.field_7477) {
						class_1799 lv3 = new class_1799(class_1802.field_8469);
						arg4.method_7281(class_3468.field_15373);
						arg4.method_6122(arg5, lv3);
						if (arg4 instanceof class_3222) {
							((class_3222)arg4).method_14204(arg4.field_7498);
						}
					}

					arg2.method_8396(null, arg3, class_3417.field_14826, class_3419.field_15245, 1.0F, 1.0F);
					this.method_9726(arg2, arg3, arg, i + 1);
				}

				return true;
			} else {
				if (i > 0 && lv2 instanceof class_1768) {
					class_1768 lv4 = (class_1768)lv2;
					if (lv4.method_7801(lv) && !arg2.field_9236) {
						lv4.method_7798(lv);
						this.method_9726(arg2, arg3, arg, i - 1);
						arg4.method_7281(class_3468.field_15382);
						return true;
					}
				}

				if (i > 0 && lv2 instanceof class_1746) {
					if (class_2573.method_10910(lv) > 0 && !arg2.field_9236) {
						class_1799 lv3 = lv.method_7972();
						lv3.method_7939(1);
						class_2573.method_10905(lv3);
						arg4.method_7281(class_3468.field_15390);
						if (!arg4.field_7503.field_7477) {
							lv.method_7934(1);
							this.method_9726(arg2, arg3, arg, i - 1);
						}

						if (lv.method_7960()) {
							arg4.method_6122(arg5, lv3);
						} else if (!arg4.field_7514.method_7394(lv3)) {
							arg4.method_7328(lv3, false);
						} else if (arg4 instanceof class_3222) {
							((class_3222)arg4).method_14204(arg4.field_7498);
						}
					}

					return true;
				} else if (i > 0 && lv2 instanceof class_1747) {
					class_2248 lv5 = ((class_1747)lv2).method_7711();
					if (lv5 instanceof class_2480 && !arg2.method_8608()) {
						class_1799 lv6 = new class_1799(class_2246.field_10603, 1);
						if (lv.method_7985()) {
							lv6.method_7980(lv.method_7969().method_10553());
						}

						arg4.method_6122(arg5, lv6);
						this.method_9726(arg2, arg3, arg, i - 1);
						arg4.method_7281(class_3468.field_15398);
					}

					return true;
				} else {
					return false;
				}
			}
		}
	}

	public void method_9726(class_1937 arg, class_2338 arg2, class_2680 arg3, int i) {
		arg.method_8652(arg2, arg3.method_11657(field_10745, Integer.valueOf(class_3532.method_15340(i, 0, 3))), 2);
		arg.method_8455(arg2, this);
	}

	@Override
	public void method_9504(class_1937 arg, class_2338 arg2) {
		if (arg.field_9229.nextInt(20) == 1) {
			float f = arg.method_8310(arg2).method_8707(arg2);
			if (!(f < 0.15F)) {
				class_2680 lv = arg.method_8320(arg2);
				if ((Integer)lv.method_11654(field_10745) < 3) {
					arg.method_8652(arg2, lv.method_11572(field_10745), 2);
				}
			}
		}
	}

	@Override
	public boolean method_9498(class_2680 arg) {
		return true;
	}

	@Override
	public int method_9572(class_2680 arg, class_1937 arg2, class_2338 arg3) {
		return (Integer)arg.method_11654(field_10745);
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_10745);
	}

	@Override
	public boolean method_9516(class_2680 arg, class_1922 arg2, class_2338 arg3, class_10 arg4) {
		return false;
	}
}
