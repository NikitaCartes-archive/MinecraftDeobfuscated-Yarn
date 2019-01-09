package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3208 {
	private static final Logger field_13907 = LogManager.getLogger();
	private final class_3218 field_13905;
	private final Set<class_3231> field_13904 = Sets.<class_3231>newHashSet();
	private final class_3525<class_3231> field_13908 = new class_3525<>();
	private int field_13906;

	public class_3208(class_3218 arg) {
		this.field_13905 = arg;
		this.method_14069(arg.method_8503().method_3760().method_14568());
	}

	public static long method_14076(double d) {
		return class_3532.method_15372(d * 4096.0);
	}

	@Environment(EnvType.CLIENT)
	public static void method_14070(class_1297 arg, double d, double e, double f) {
		arg.field_6001 = method_14076(d);
		arg.field_6023 = method_14076(e);
		arg.field_5954 = method_14076(f);
	}

	public void method_14066(class_1297 arg) {
		if (arg instanceof class_3222) {
			this.method_14074(arg, 512, 2);
			class_3222 lv = (class_3222)arg;

			for (class_3231 lv2 : this.field_13904) {
				if (lv2.method_14305() != lv) {
					lv2.method_14303(lv);
				}
			}
		} else if (arg instanceof class_1536) {
			this.method_14077(arg, 64, 5, true);
		} else if (arg instanceof class_1665) {
			this.method_14077(arg, 64, 20, false);
		} else if (arg instanceof class_1677) {
			this.method_14077(arg, 64, 10, false);
		} else if (arg instanceof class_1668) {
			this.method_14077(arg, 64, 10, true);
		} else if (arg instanceof class_1680) {
			this.method_14077(arg, 64, 10, true);
		} else if (arg instanceof class_1673) {
			this.method_14077(arg, 64, 10, false);
		} else if (arg instanceof class_1684) {
			this.method_14077(arg, 64, 10, true);
		} else if (arg instanceof class_1672) {
			this.method_14077(arg, 64, 4, true);
		} else if (arg instanceof class_1681) {
			this.method_14077(arg, 64, 10, true);
		} else if (arg instanceof class_1686) {
			this.method_14077(arg, 64, 10, true);
		} else if (arg instanceof class_1683) {
			this.method_14077(arg, 64, 10, true);
		} else if (arg instanceof class_1671) {
			this.method_14077(arg, 64, 10, true);
		} else if (arg instanceof class_1542) {
			this.method_14077(arg, 64, 20, true);
		} else if (arg instanceof class_1688) {
			this.method_14077(arg, 80, 3, true);
		} else if (arg instanceof class_1690) {
			this.method_14077(arg, 80, 3, true);
		} else if (arg instanceof class_1477) {
			this.method_14077(arg, 64, 3, true);
		} else if (arg instanceof class_1528) {
			this.method_14077(arg, 80, 3, false);
		} else if (arg instanceof class_1678) {
			this.method_14077(arg, 80, 3, true);
		} else if (arg instanceof class_1420) {
			this.method_14077(arg, 80, 3, false);
		} else if (arg instanceof class_1510) {
			this.method_14077(arg, 160, 3, true);
		} else if (arg instanceof class_1298) {
			this.method_14077(arg, 80, 3, true);
		} else if (arg instanceof class_1541) {
			this.method_14077(arg, 160, 10, true);
		} else if (arg instanceof class_1540) {
			this.method_14077(arg, 160, 20, true);
		} else if (arg instanceof class_1530) {
			this.method_14077(arg, 160, Integer.MAX_VALUE, false);
		} else if (arg instanceof class_1531) {
			this.method_14077(arg, 160, 3, true);
		} else if (arg instanceof class_1303) {
			this.method_14077(arg, 160, 20, true);
		} else if (arg instanceof class_1295) {
			this.method_14077(arg, 160, Integer.MAX_VALUE, true);
		} else if (arg instanceof class_1511) {
			this.method_14077(arg, 256, Integer.MAX_VALUE, false);
		} else if (arg instanceof class_1669) {
			this.method_14077(arg, 160, 2, false);
		}
	}

	public void method_14074(class_1297 arg, int i, int j) {
		this.method_14077(arg, i, j, false);
	}

	public void method_14077(class_1297 arg, int i, int j, boolean bl) {
		try {
			if (this.field_13908.method_15311(arg.method_5628())) {
				throw new IllegalStateException("Entity is already tracked!");
			}

			class_3231 lv = new class_3231(arg, i, this.field_13906, j, bl);
			this.field_13904.add(lv);
			this.field_13908.method_15313(arg.method_5628(), lv);
			lv.method_14300(this.field_13905.field_9228);
		} catch (Throwable var10) {
			class_128 lv2 = class_128.method_560(var10, "Adding entity to track");
			class_129 lv3 = lv2.method_562("Entity To Track");
			lv3.method_578("Tracking range", i + " blocks");
			lv3.method_577("Update interval", () -> {
				String string = "Once per " + j + " ticks";
				if (j == Integer.MAX_VALUE) {
					string = "Maximum (" + string + ")";
				}

				return string;
			});
			arg.method_5819(lv3);
			this.field_13908.method_15316(arg.method_5628()).method_14305().method_5819(lv2.method_562("Entity That Is Already Tracked"));

			try {
				throw new class_148(lv2);
			} catch (class_148 var9) {
				field_13907.error("\"Silently\" catching entity tracking error.", (Throwable)var9);
			}
		}
	}

	public void method_14068(class_1297 arg) {
		if (arg instanceof class_3222) {
			class_3222 lv = (class_3222)arg;

			for (class_3231 lv2 : this.field_13904) {
				lv2.method_14302(lv);
			}
		}

		class_3231 lv3 = this.field_13908.method_15312(arg.method_5628());
		if (lv3 != null) {
			this.field_13904.remove(lv3);
			lv3.method_14304();
		}
	}

	public void method_14078() {
		List<class_3222> list = Lists.<class_3222>newArrayList();

		for (class_3231 lv : this.field_13904) {
			lv.method_14297(this.field_13905.field_9228);
			if (lv.field_14058) {
				class_1297 lv2 = lv.method_14305();
				if (lv2 instanceof class_3222) {
					list.add((class_3222)lv2);
				}
			}
		}

		for (int i = 0; i < list.size(); i++) {
			class_3222 lv3 = (class_3222)list.get(i);

			for (class_3231 lv4 : this.field_13904) {
				if (lv4.method_14305() != lv3) {
					lv4.method_14303(lv3);
				}
			}
		}
	}

	public void method_14071(class_3222 arg) {
		for (class_3231 lv : this.field_13904) {
			if (lv.method_14305() == arg) {
				lv.method_14300(this.field_13905.field_9228);
			} else {
				lv.method_14303(arg);
			}
		}
	}

	public void method_14079(class_1297 arg, class_2596<?> arg2) {
		class_3231 lv = this.field_13908.method_15316(arg.method_5628());
		if (lv != null) {
			lv.method_14293(arg2);
		}
	}

	public void method_14073(class_1297 arg, class_2596<?> arg2) {
		class_3231 lv = this.field_13908.method_15316(arg.method_5628());
		if (lv != null) {
			lv.method_14295(arg2);
		}
	}

	public void method_14072(class_3222 arg) {
		for (class_3231 lv : this.field_13904) {
			lv.method_14301(arg);
		}
	}

	public void method_14067(class_3222 arg, int i, int j) {
		List<class_1297> list = Lists.<class_1297>newArrayList();
		List<class_1297> list2 = Lists.<class_1297>newArrayList();

		for (class_3231 lv : this.field_13904) {
			class_1297 lv2 = lv.method_14305();
			if (lv2 != arg && lv2.field_6024 == i && lv2.field_5980 == j) {
				lv.method_14303(arg);
				if (lv2 instanceof class_1308 && ((class_1308)lv2).method_5933() != null) {
					list.add(lv2);
				}

				if (!lv2.method_5685().isEmpty()) {
					list2.add(lv2);
				}
			}
		}

		if (!list.isEmpty()) {
			for (class_1297 lv3 : list) {
				arg.field_13987.method_14364(new class_2740(lv3, ((class_1308)lv3).method_5933()));
			}
		}

		if (!list2.isEmpty()) {
			for (class_1297 lv3 : list2) {
				arg.field_13987.method_14364(new class_2752(lv3));
			}
		}
	}

	public void method_14069(int i) {
		this.field_13906 = (i - 1) * 16;

		for (class_3231 lv : this.field_13904) {
			lv.method_14296(this.field_13906);
		}
	}
}
