package net.minecraft;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2480 extends class_2237 {
	public static final class_2754<class_2350> field_11496 = class_2318.field_10927;
	public static final class_2960 field_11495 = new class_2960("contents");
	@Nullable
	private final class_1767 field_11494;

	public class_2480(@Nullable class_1767 arg, class_2248.class_2251 arg2) {
		super(arg2);
		this.field_11494 = arg;
		this.method_9590(this.field_10647.method_11664().method_11657(field_11496, class_2350.field_11036));
	}

	@Override
	public class_2586 method_10123(class_1922 arg) {
		return new class_2627(this.field_11494);
	}

	@Override
	public boolean method_16362(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return true;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_9589(class_2680 arg) {
		return true;
	}

	@Override
	public class_2464 method_9604(class_2680 arg) {
		return class_2464.field_11456;
	}

	@Override
	public boolean method_9534(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1657 arg4, class_1268 arg5, class_3965 arg6) {
		if (arg2.field_9236) {
			return true;
		} else if (arg4.method_7325()) {
			return true;
		} else {
			class_2586 lv = arg2.method_8321(arg3);
			if (lv instanceof class_2627) {
				class_2350 lv2 = arg.method_11654(field_11496);
				class_2627 lv3 = (class_2627)lv;
				boolean bl;
				if (lv3.method_11313() == class_2627.class_2628.field_12065) {
					class_238 lv4 = class_259.method_1077()
						.method_1107()
						.method_1012((double)(0.5F * (float)lv2.method_10148()), (double)(0.5F * (float)lv2.method_10164()), (double)(0.5F * (float)lv2.method_10165()))
						.method_1002((double)lv2.method_10148(), (double)lv2.method_10164(), (double)lv2.method_10165());
					bl = arg2.method_18026(lv4.method_996(arg3.method_10093(lv2)));
				} else {
					bl = true;
				}

				if (bl) {
					arg4.method_17355(lv3);
					arg4.method_7281(class_3468.field_15418);
				}

				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public class_2680 method_9605(class_1750 arg) {
		return this.method_9564().method_11657(field_11496, arg.method_8038());
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_11496);
	}

	@Override
	public void method_9576(class_1937 arg, class_2338 arg2, class_2680 arg3, class_1657 arg4) {
		class_2586 lv = arg.method_8321(arg2);
		if (lv instanceof class_2627) {
			((class_2627)lv).method_11289(arg4);
		}

		super.method_9576(arg, arg2, arg3, arg4);
	}

	@Override
	public List<class_1799> method_9560(class_2680 arg, class_47.class_48 arg2) {
		class_2586 lv = arg2.method_305(class_181.field_1228);
		if (lv instanceof class_2627) {
			class_2627 lv2 = (class_2627)lv;
			arg2 = arg2.method_307(field_11495, (arg2x, consumer) -> {
				for (int i = 0; i < lv2.method_5439(); i++) {
					consumer.accept(lv2.method_5438(i));
				}
			});
		}

		return super.method_9560(arg, arg2);
	}

	@Override
	public void method_9567(class_1937 arg, class_2338 arg2, class_2680 arg3, class_1309 arg4, class_1799 arg5) {
		if (arg5.method_7938()) {
			class_2586 lv = arg.method_8321(arg2);
			if (lv instanceof class_2627) {
				((class_2627)lv).method_17488(arg5.method_7964());
			}
		}
	}

	@Override
	public void method_9536(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2680 arg4, boolean bl) {
		if (arg.method_11614() != arg4.method_11614()) {
			class_2586 lv = arg2.method_8321(arg3);
			if (lv instanceof class_2627) {
				arg2.method_8455(arg3, arg.method_11614());
			}

			super.method_9536(arg, arg2, arg3, arg4, bl);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_9568(class_1799 arg, @Nullable class_1922 arg2, List<class_2561> list, class_1836 arg3) {
		super.method_9568(arg, arg2, list, arg3);
		class_2487 lv = arg.method_7941("BlockEntityTag");
		if (lv != null) {
			if (lv.method_10573("LootTable", 8)) {
				list.add(new class_2585("???????"));
			}

			if (lv.method_10573("Items", 9)) {
				class_2371<class_1799> lv2 = class_2371.method_10213(27, class_1799.field_8037);
				class_1262.method_5429(lv, lv2);
				int i = 0;
				int j = 0;

				for (class_1799 lv3 : lv2) {
					if (!lv3.method_7960()) {
						j++;
						if (i <= 4) {
							i++;
							class_2561 lv4 = lv3.method_7964().method_10853();
							lv4.method_10864(" x").method_10864(String.valueOf(lv3.method_7947()));
							list.add(lv4);
						}
					}
				}

				if (j - i > 0) {
					list.add(new class_2588("container.shulkerBox.more", j - i).method_10854(class_124.field_1056));
				}
			}
		}
	}

	@Override
	public class_3619 method_9527(class_2680 arg) {
		return class_3619.field_15971;
	}

	@Override
	public class_265 method_9530(class_2680 arg, class_1922 arg2, class_2338 arg3, class_3726 arg4) {
		class_2586 lv = arg2.method_8321(arg3);
		return lv instanceof class_2627 ? class_259.method_1078(((class_2627)lv).method_11314(arg)) : class_259.method_1077();
	}

	@Override
	public boolean method_9601(class_2680 arg) {
		return false;
	}

	@Override
	public boolean method_9498(class_2680 arg) {
		return true;
	}

	@Override
	public int method_9572(class_2680 arg, class_1937 arg2, class_2338 arg3) {
		return class_1703.method_7618((class_1263)arg2.method_8321(arg3));
	}

	@Environment(EnvType.CLIENT)
	@Override
	public class_1799 method_9574(class_1922 arg, class_2338 arg2, class_2680 arg3) {
		class_1799 lv = super.method_9574(arg, arg2, arg3);
		class_2627 lv2 = (class_2627)arg.method_8321(arg2);
		class_2487 lv3 = lv2.method_11317(new class_2487());
		if (!lv3.isEmpty()) {
			lv.method_7959("BlockEntityTag", lv3);
		}

		return lv;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public static class_1767 method_10527(class_1792 arg) {
		return method_10526(class_2248.method_9503(arg));
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public static class_1767 method_10526(class_2248 arg) {
		return arg instanceof class_2480 ? ((class_2480)arg).method_10528() : null;
	}

	public static class_2248 method_10525(class_1767 arg) {
		if (arg == null) {
			return class_2246.field_10603;
		} else {
			switch (arg) {
				case field_7952:
					return class_2246.field_10199;
				case field_7946:
					return class_2246.field_10407;
				case field_7958:
					return class_2246.field_10063;
				case field_7951:
					return class_2246.field_10203;
				case field_7947:
					return class_2246.field_10600;
				case field_7961:
					return class_2246.field_10275;
				case field_7954:
					return class_2246.field_10051;
				case field_7944:
					return class_2246.field_10140;
				case field_7967:
					return class_2246.field_10320;
				case field_7955:
					return class_2246.field_10532;
				case field_7945:
				default:
					return class_2246.field_10268;
				case field_7966:
					return class_2246.field_10605;
				case field_7957:
					return class_2246.field_10373;
				case field_7942:
					return class_2246.field_10055;
				case field_7964:
					return class_2246.field_10068;
				case field_7963:
					return class_2246.field_10371;
			}
		}
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public class_1767 method_10528() {
		return this.field_11494;
	}

	public static class_1799 method_10529(class_1767 arg) {
		return new class_1799(method_10525(arg));
	}

	@Override
	public class_2680 method_9598(class_2680 arg, class_2470 arg2) {
		return arg.method_11657(field_11496, arg2.method_10503(arg.method_11654(field_11496)));
	}

	@Override
	public class_2680 method_9569(class_2680 arg, class_2415 arg2) {
		return arg.method_11626(arg2.method_10345(arg.method_11654(field_11496)));
	}
}
