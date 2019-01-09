package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;

public class class_2452 {
	private final class_1937 field_11409;
	private final class_2338 field_11410;
	private final class_2241 field_11411;
	private class_2680 field_11406;
	private final boolean field_11408;
	private final List<class_2338> field_11407 = Lists.<class_2338>newArrayList();

	public class_2452(class_1937 arg, class_2338 arg2, class_2680 arg3) {
		this.field_11409 = arg;
		this.field_11410 = arg2;
		this.field_11406 = arg3;
		this.field_11411 = (class_2241)arg3.method_11614();
		class_2768 lv = arg3.method_11654(this.field_11411.method_9474());
		this.field_11408 = this.field_11411.method_9478();
		this.method_10466(lv);
	}

	public List<class_2338> method_10457() {
		return this.field_11407;
	}

	private void method_10466(class_2768 arg) {
		this.field_11407.clear();
		switch (arg) {
			case field_12665:
				this.field_11407.add(this.field_11410.method_10095());
				this.field_11407.add(this.field_11410.method_10072());
				break;
			case field_12674:
				this.field_11407.add(this.field_11410.method_10067());
				this.field_11407.add(this.field_11410.method_10078());
				break;
			case field_12667:
				this.field_11407.add(this.field_11410.method_10067());
				this.field_11407.add(this.field_11410.method_10078().method_10084());
				break;
			case field_12666:
				this.field_11407.add(this.field_11410.method_10067().method_10084());
				this.field_11407.add(this.field_11410.method_10078());
				break;
			case field_12670:
				this.field_11407.add(this.field_11410.method_10095().method_10084());
				this.field_11407.add(this.field_11410.method_10072());
				break;
			case field_12668:
				this.field_11407.add(this.field_11410.method_10095());
				this.field_11407.add(this.field_11410.method_10072().method_10084());
				break;
			case field_12664:
				this.field_11407.add(this.field_11410.method_10078());
				this.field_11407.add(this.field_11410.method_10072());
				break;
			case field_12671:
				this.field_11407.add(this.field_11410.method_10067());
				this.field_11407.add(this.field_11410.method_10072());
				break;
			case field_12672:
				this.field_11407.add(this.field_11410.method_10067());
				this.field_11407.add(this.field_11410.method_10095());
				break;
			case field_12663:
				this.field_11407.add(this.field_11410.method_10078());
				this.field_11407.add(this.field_11410.method_10095());
		}
	}

	private void method_10467() {
		for (int i = 0; i < this.field_11407.size(); i++) {
			class_2452 lv = this.method_10458((class_2338)this.field_11407.get(i));
			if (lv != null && lv.method_10464(this)) {
				this.field_11407.set(i, lv.field_11410);
			} else {
				this.field_11407.remove(i--);
			}
		}
	}

	private boolean method_10456(class_2338 arg) {
		return class_2241.method_9479(this.field_11409, arg)
			|| class_2241.method_9479(this.field_11409, arg.method_10084())
			|| class_2241.method_9479(this.field_11409, arg.method_10074());
	}

	@Nullable
	private class_2452 method_10458(class_2338 arg) {
		class_2680 lv2 = this.field_11409.method_8320(arg);
		if (class_2241.method_9476(lv2)) {
			return new class_2452(this.field_11409, arg, lv2);
		} else {
			class_2338 lv = arg.method_10084();
			lv2 = this.field_11409.method_8320(lv);
			if (class_2241.method_9476(lv2)) {
				return new class_2452(this.field_11409, lv, lv2);
			} else {
				lv = arg.method_10074();
				lv2 = this.field_11409.method_8320(lv);
				return class_2241.method_9476(lv2) ? new class_2452(this.field_11409, lv, lv2) : null;
			}
		}
	}

	private boolean method_10464(class_2452 arg) {
		return this.method_10463(arg.field_11410);
	}

	private boolean method_10463(class_2338 arg) {
		for (int i = 0; i < this.field_11407.size(); i++) {
			class_2338 lv = (class_2338)this.field_11407.get(i);
			if (lv.method_10263() == arg.method_10263() && lv.method_10260() == arg.method_10260()) {
				return true;
			}
		}

		return false;
	}

	protected int method_10460() {
		int i = 0;

		for (class_2350 lv : class_2350.class_2353.field_11062) {
			if (this.method_10456(this.field_11410.method_10093(lv))) {
				i++;
			}
		}

		return i;
	}

	private boolean method_10455(class_2452 arg) {
		return this.method_10464(arg) || this.field_11407.size() != 2;
	}

	private void method_10461(class_2452 arg) {
		this.field_11407.add(arg.field_11410);
		class_2338 lv = this.field_11410.method_10095();
		class_2338 lv2 = this.field_11410.method_10072();
		class_2338 lv3 = this.field_11410.method_10067();
		class_2338 lv4 = this.field_11410.method_10078();
		boolean bl = this.method_10463(lv);
		boolean bl2 = this.method_10463(lv2);
		boolean bl3 = this.method_10463(lv3);
		boolean bl4 = this.method_10463(lv4);
		class_2768 lv5 = null;
		if (bl || bl2) {
			lv5 = class_2768.field_12665;
		}

		if (bl3 || bl4) {
			lv5 = class_2768.field_12674;
		}

		if (!this.field_11408) {
			if (bl2 && bl4 && !bl && !bl3) {
				lv5 = class_2768.field_12664;
			}

			if (bl2 && bl3 && !bl && !bl4) {
				lv5 = class_2768.field_12671;
			}

			if (bl && bl3 && !bl2 && !bl4) {
				lv5 = class_2768.field_12672;
			}

			if (bl && bl4 && !bl2 && !bl3) {
				lv5 = class_2768.field_12663;
			}
		}

		if (lv5 == class_2768.field_12665) {
			if (class_2241.method_9479(this.field_11409, lv.method_10084())) {
				lv5 = class_2768.field_12670;
			}

			if (class_2241.method_9479(this.field_11409, lv2.method_10084())) {
				lv5 = class_2768.field_12668;
			}
		}

		if (lv5 == class_2768.field_12674) {
			if (class_2241.method_9479(this.field_11409, lv4.method_10084())) {
				lv5 = class_2768.field_12667;
			}

			if (class_2241.method_9479(this.field_11409, lv3.method_10084())) {
				lv5 = class_2768.field_12666;
			}
		}

		if (lv5 == null) {
			lv5 = class_2768.field_12665;
		}

		this.field_11406 = this.field_11406.method_11657(this.field_11411.method_9474(), lv5);
		this.field_11409.method_8652(this.field_11410, this.field_11406, 3);
	}

	private boolean method_10465(class_2338 arg) {
		class_2452 lv = this.method_10458(arg);
		if (lv == null) {
			return false;
		} else {
			lv.method_10467();
			return lv.method_10455(this);
		}
	}

	public class_2452 method_10459(boolean bl, boolean bl2) {
		class_2338 lv = this.field_11410.method_10095();
		class_2338 lv2 = this.field_11410.method_10072();
		class_2338 lv3 = this.field_11410.method_10067();
		class_2338 lv4 = this.field_11410.method_10078();
		boolean bl3 = this.method_10465(lv);
		boolean bl4 = this.method_10465(lv2);
		boolean bl5 = this.method_10465(lv3);
		boolean bl6 = this.method_10465(lv4);
		class_2768 lv5 = null;
		if ((bl3 || bl4) && !bl5 && !bl6) {
			lv5 = class_2768.field_12665;
		}

		if ((bl5 || bl6) && !bl3 && !bl4) {
			lv5 = class_2768.field_12674;
		}

		if (!this.field_11408) {
			if (bl4 && bl6 && !bl3 && !bl5) {
				lv5 = class_2768.field_12664;
			}

			if (bl4 && bl5 && !bl3 && !bl6) {
				lv5 = class_2768.field_12671;
			}

			if (bl3 && bl5 && !bl4 && !bl6) {
				lv5 = class_2768.field_12672;
			}

			if (bl3 && bl6 && !bl4 && !bl5) {
				lv5 = class_2768.field_12663;
			}
		}

		if (lv5 == null) {
			if (bl3 || bl4) {
				lv5 = class_2768.field_12665;
			}

			if (bl5 || bl6) {
				lv5 = class_2768.field_12674;
			}

			if (!this.field_11408) {
				if (bl) {
					if (bl4 && bl6) {
						lv5 = class_2768.field_12664;
					}

					if (bl5 && bl4) {
						lv5 = class_2768.field_12671;
					}

					if (bl6 && bl3) {
						lv5 = class_2768.field_12663;
					}

					if (bl3 && bl5) {
						lv5 = class_2768.field_12672;
					}
				} else {
					if (bl3 && bl5) {
						lv5 = class_2768.field_12672;
					}

					if (bl6 && bl3) {
						lv5 = class_2768.field_12663;
					}

					if (bl5 && bl4) {
						lv5 = class_2768.field_12671;
					}

					if (bl4 && bl6) {
						lv5 = class_2768.field_12664;
					}
				}
			}
		}

		if (lv5 == class_2768.field_12665) {
			if (class_2241.method_9479(this.field_11409, lv.method_10084())) {
				lv5 = class_2768.field_12670;
			}

			if (class_2241.method_9479(this.field_11409, lv2.method_10084())) {
				lv5 = class_2768.field_12668;
			}
		}

		if (lv5 == class_2768.field_12674) {
			if (class_2241.method_9479(this.field_11409, lv4.method_10084())) {
				lv5 = class_2768.field_12667;
			}

			if (class_2241.method_9479(this.field_11409, lv3.method_10084())) {
				lv5 = class_2768.field_12666;
			}
		}

		if (lv5 == null) {
			lv5 = class_2768.field_12665;
		}

		this.method_10466(lv5);
		this.field_11406 = this.field_11406.method_11657(this.field_11411.method_9474(), lv5);
		if (bl2 || this.field_11409.method_8320(this.field_11410) != this.field_11406) {
			this.field_11409.method_8652(this.field_11410, this.field_11406, 3);

			for (int i = 0; i < this.field_11407.size(); i++) {
				class_2452 lv6 = this.method_10458((class_2338)this.field_11407.get(i));
				if (lv6 != null) {
					lv6.method_10467();
					if (lv6.method_10455(this)) {
						lv6.method_10461(this);
					}
				}
			}
		}

		return this;
	}

	public class_2680 method_10462() {
		return this.field_11406;
	}
}
