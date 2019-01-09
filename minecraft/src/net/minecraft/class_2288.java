package net.minecraft;

import java.util.Random;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_2288 extends class_2237 {
	private static final Logger field_10792 = LogManager.getLogger();
	public static final class_2753 field_10791 = class_2318.field_10927;
	public static final class_2746 field_10793 = class_2741.field_12486;

	public class_2288(class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(this.field_10647.method_11664().method_11657(field_10791, class_2350.field_11043).method_11657(field_10793, Boolean.valueOf(false)));
	}

	@Override
	public class_2586 method_10123(class_1922 arg) {
		class_2593 lv = new class_2593();
		lv.method_11041(this == class_2246.field_10395);
		return lv;
	}

	@Override
	public void method_9612(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2248 arg4, class_2338 arg5) {
		if (!arg2.field_9236) {
			class_2586 lv = arg2.method_8321(arg3);
			if (lv instanceof class_2593) {
				class_2593 lv2 = (class_2593)lv;
				boolean bl = arg2.method_8479(arg3);
				boolean bl2 = lv2.method_11043();
				lv2.method_11038(bl);
				if (!bl2 && !lv2.method_11042() && lv2.method_11039() != class_2593.class_2594.field_11922) {
					if (bl) {
						lv2.method_11045();
						arg2.method_8397().method_8676(arg3, this, this.method_9563(arg2));
					}
				}
			}
		}
	}

	@Override
	public void method_9588(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		if (!arg2.field_9236) {
			class_2586 lv = arg2.method_8321(arg3);
			if (lv instanceof class_2593) {
				class_2593 lv2 = (class_2593)lv;
				class_1918 lv3 = lv2.method_11040();
				boolean bl = !class_3544.method_15438(lv3.method_8289());
				class_2593.class_2594 lv4 = lv2.method_11039();
				boolean bl2 = lv2.method_11044();
				if (lv4 == class_2593.class_2594.field_11923) {
					lv2.method_11045();
					if (bl2) {
						this.method_9780(arg, arg2, arg3, lv3, bl);
					} else if (lv2.method_11046()) {
						lv3.method_8298(0);
					}

					if (lv2.method_11043() || lv2.method_11042()) {
						arg2.method_8397().method_8676(arg3, this, this.method_9563(arg2));
					}
				} else if (lv4 == class_2593.class_2594.field_11924) {
					if (bl2) {
						this.method_9780(arg, arg2, arg3, lv3, bl);
					} else if (lv2.method_11046()) {
						lv3.method_8298(0);
					}
				}

				arg2.method_8455(arg3, this);
			}
		}
	}

	private void method_9780(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1918 arg4, boolean bl) {
		if (bl) {
			arg4.method_8301(arg2);
		} else {
			arg4.method_8298(0);
		}

		method_9779(arg2, arg3, arg.method_11654(field_10791));
	}

	@Override
	public int method_9563(class_1941 arg) {
		return 1;
	}

	@Override
	public boolean method_9534(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1657 arg4, class_1268 arg5, class_2350 arg6, float f, float g, float h) {
		class_2586 lv = arg2.method_8321(arg3);
		if (lv instanceof class_2593 && arg4.method_7338()) {
			arg4.method_7323((class_2593)lv);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean method_9498(class_2680 arg) {
		return true;
	}

	@Override
	public int method_9572(class_2680 arg, class_1937 arg2, class_2338 arg3) {
		class_2586 lv = arg2.method_8321(arg3);
		return lv instanceof class_2593 ? ((class_2593)lv).method_11040().method_8304() : 0;
	}

	@Override
	public void method_9567(class_1937 arg, class_2338 arg2, class_2680 arg3, class_1309 arg4, class_1799 arg5) {
		class_2586 lv = arg.method_8321(arg2);
		if (lv instanceof class_2593) {
			class_2593 lv2 = (class_2593)lv;
			class_1918 lv3 = lv2.method_11040();
			if (arg5.method_7938()) {
				lv3.method_8290(arg5.method_7964());
			}

			if (!arg.field_9236) {
				if (arg5.method_7941("BlockEntityTag") == null) {
					lv3.method_8287(arg.method_8450().method_8355("sendCommandFeedback"));
					lv2.method_11041(this == class_2246.field_10395);
				}

				if (lv2.method_11039() == class_2593.class_2594.field_11922) {
					boolean bl = arg.method_8479(arg2);
					lv2.method_11038(bl);
				}
			}
		}
	}

	@Override
	public class_2464 method_9604(class_2680 arg) {
		return class_2464.field_11458;
	}

	@Override
	public class_2680 method_9598(class_2680 arg, class_2470 arg2) {
		return arg.method_11657(field_10791, arg2.method_10503(arg.method_11654(field_10791)));
	}

	@Override
	public class_2680 method_9569(class_2680 arg, class_2415 arg2) {
		return arg.method_11626(arg2.method_10345(arg.method_11654(field_10791)));
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_10791, field_10793);
	}

	@Override
	public class_2680 method_9605(class_1750 arg) {
		return this.method_9564().method_11657(field_10791, arg.method_7715().method_10153());
	}

	private static void method_9779(class_1937 arg, class_2338 arg2, class_2350 arg3) {
		class_2338.class_2339 lv = new class_2338.class_2339(arg2);
		class_1928 lv2 = arg.method_8450();
		int i = lv2.method_8356("maxCommandChainLength");

		while (i-- > 0) {
			lv.method_10098(arg3);
			class_2680 lv3 = arg.method_8320(lv);
			class_2248 lv4 = lv3.method_11614();
			if (lv4 != class_2246.field_10395) {
				break;
			}

			class_2586 lv5 = arg.method_8321(lv);
			if (!(lv5 instanceof class_2593)) {
				break;
			}

			class_2593 lv6 = (class_2593)lv5;
			if (lv6.method_11039() != class_2593.class_2594.field_11922) {
				break;
			}

			if (lv6.method_11043() || lv6.method_11042()) {
				class_1918 lv7 = lv6.method_11040();
				if (lv6.method_11045()) {
					if (!lv7.method_8301(arg)) {
						break;
					}

					arg.method_8455(lv, lv4);
				} else if (lv6.method_11046()) {
					lv7.method_8298(0);
				}
			}

			arg3 = lv3.method_11654(field_10791);
		}

		if (i <= 0) {
			int j = Math.max(lv2.method_8356("maxCommandChainLength"), 0);
			field_10792.warn("Command Block chain tried to execute more than {} steps!", j);
		}
	}
}
