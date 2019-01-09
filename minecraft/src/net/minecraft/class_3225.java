package net.minecraft;

public class class_3225 {
	public class_1937 field_14007;
	public class_3222 field_14008;
	private class_1934 field_14005 = class_1934.field_9218;
	private boolean field_14003;
	private int field_14002;
	private class_2338 field_14006 = class_2338.field_10980;
	private int field_14000;
	private boolean field_14001;
	private class_2338 field_14004 = class_2338.field_10980;
	private int field_14010;
	private int field_14009 = -1;

	public class_3225(class_1937 arg) {
		this.field_14007 = arg;
	}

	public void method_14261(class_1934 arg) {
		this.field_14005 = arg;
		arg.method_8382(this.field_14008.field_7503);
		this.field_14008.method_7355();
		this.field_14008.field_13995.method_3760().method_14581(new class_2703(class_2703.class_2704.field_12375, this.field_14008));
		this.field_14007.method_8448();
	}

	public class_1934 method_14257() {
		return this.field_14005;
	}

	public boolean method_14267() {
		return this.field_14005.method_8388();
	}

	public boolean method_14268() {
		return this.field_14005.method_8386();
	}

	public void method_14260(class_1934 arg) {
		if (this.field_14005 == class_1934.field_9218) {
			this.field_14005 = arg;
		}

		this.method_14261(this.field_14005);
	}

	public void method_14264() {
		this.field_14000++;
		if (this.field_14001) {
			int i = this.field_14000 - this.field_14010;
			class_2680 lv = this.field_14007.method_8320(this.field_14004);
			if (lv.method_11588()) {
				this.field_14001 = false;
			} else {
				float f = lv.method_11589(this.field_14008, this.field_14008.field_6002, this.field_14004) * (float)(i + 1);
				int j = (int)(f * 10.0F);
				if (j != this.field_14009) {
					this.field_14007.method_8517(this.field_14008.method_5628(), this.field_14004, j);
					this.field_14009 = j;
				}

				if (f >= 1.0F) {
					this.field_14001 = false;
					this.method_14266(this.field_14004);
				}
			}
		} else if (this.field_14003) {
			class_2680 lv2 = this.field_14007.method_8320(this.field_14006);
			if (lv2.method_11588()) {
				this.field_14007.method_8517(this.field_14008.method_5628(), this.field_14006, -1);
				this.field_14009 = -1;
				this.field_14003 = false;
			} else {
				int k = this.field_14000 - this.field_14002;
				float fx = lv2.method_11589(this.field_14008, this.field_14008.field_6002, this.field_14004) * (float)(k + 1);
				int jx = (int)(fx * 10.0F);
				if (jx != this.field_14009) {
					this.field_14007.method_8517(this.field_14008.method_5628(), this.field_14006, jx);
					this.field_14009 = jx;
				}
			}
		}
	}

	public void method_14263(class_2338 arg, class_2350 arg2) {
		if (this.method_14268()) {
			if (!this.field_14007.method_8506(null, arg, arg2)) {
				this.method_14266(arg);
			}
		} else {
			if (this.field_14005.method_8387()) {
				if (this.field_14005 == class_1934.field_9219) {
					return;
				}

				if (!this.field_14008.method_7294()) {
					class_1799 lv = this.field_14008.method_6047();
					if (lv.method_7960()) {
						return;
					}

					class_2694 lv2 = new class_2694(this.field_14007, arg, false);
					if (!lv.method_7940(this.field_14007.method_8514(), lv2)) {
						return;
					}
				}
			}

			this.field_14007.method_8506(null, arg, arg2);
			this.field_14002 = this.field_14000;
			float f = 1.0F;
			class_2680 lv3 = this.field_14007.method_8320(arg);
			if (!lv3.method_11588()) {
				lv3.method_11636(this.field_14007, arg, this.field_14008);
				f = lv3.method_11589(this.field_14008, this.field_14008.field_6002, arg);
			}

			if (!lv3.method_11588() && f >= 1.0F) {
				this.method_14266(arg);
			} else {
				this.field_14003 = true;
				this.field_14006 = arg;
				int i = (int)(f * 10.0F);
				this.field_14007.method_8517(this.field_14008.method_5628(), arg, i);
				this.field_14008.field_13987.method_14364(new class_2626(this.field_14007, arg));
				this.field_14009 = i;
			}
		}
	}

	public void method_14258(class_2338 arg) {
		if (arg.equals(this.field_14006)) {
			int i = this.field_14000 - this.field_14002;
			class_2680 lv = this.field_14007.method_8320(arg);
			if (!lv.method_11588()) {
				float f = lv.method_11589(this.field_14008, this.field_14008.field_6002, arg) * (float)(i + 1);
				if (f >= 0.7F) {
					this.field_14003 = false;
					this.field_14007.method_8517(this.field_14008.method_5628(), arg, -1);
					this.method_14266(arg);
				} else if (!this.field_14001) {
					this.field_14003 = false;
					this.field_14001 = true;
					this.field_14004 = arg;
					this.field_14010 = this.field_14002;
				}
			}
		}
	}

	public void method_14269() {
		this.field_14003 = false;
		this.field_14007.method_8517(this.field_14008.method_5628(), this.field_14006, -1);
	}

	private boolean method_14265(class_2338 arg) {
		class_2680 lv = this.field_14007.method_8320(arg);
		lv.method_11614().method_9576(this.field_14007, arg, lv, this.field_14008);
		boolean bl = this.field_14007.method_8650(arg);
		if (bl) {
			lv.method_11614().method_9585(this.field_14007, arg, lv);
		}

		return bl;
	}

	public boolean method_14266(class_2338 arg) {
		class_2680 lv = this.field_14007.method_8320(arg);
		if (!this.field_14008.method_6047().method_7909().method_7885(lv, this.field_14007, arg, this.field_14008)) {
			return false;
		} else {
			class_2586 lv2 = this.field_14007.method_8321(arg);
			class_2248 lv3 = lv.method_11614();
			if ((lv3 instanceof class_2288 || lv3 instanceof class_2515 || lv3 instanceof class_3748) && !this.field_14008.method_7338()) {
				this.field_14007.method_8413(arg, lv, lv, 3);
				return false;
			} else {
				if (this.field_14005.method_8387()) {
					if (this.field_14005 == class_1934.field_9219) {
						return false;
					}

					if (!this.field_14008.method_7294()) {
						class_1799 lv4 = this.field_14008.method_6047();
						if (lv4.method_7960()) {
							return false;
						}

						class_2694 lv5 = new class_2694(this.field_14007, arg, false);
						if (!lv4.method_7940(this.field_14007.method_8514(), lv5)) {
							return false;
						}
					}
				}

				boolean bl = this.method_14265(arg);
				if (!this.method_14268()) {
					class_1799 lv6 = this.field_14008.method_6047();
					boolean bl2 = this.field_14008.method_7305(lv);
					lv6.method_7952(this.field_14007, lv, arg, this.field_14008);
					if (bl && bl2) {
						class_1799 lv7 = lv6.method_7960() ? class_1799.field_8037 : lv6.method_7972();
						lv.method_11614().method_9556(this.field_14007, this.field_14008, arg, lv, lv2, lv7);
					}
				}

				return bl;
			}
		}
	}

	public class_1269 method_14256(class_1657 arg, class_1937 arg2, class_1799 arg3, class_1268 arg4) {
		if (this.field_14005 == class_1934.field_9219) {
			return class_1269.field_5811;
		} else if (arg.method_7357().method_7904(arg3.method_7909())) {
			return class_1269.field_5811;
		} else {
			int i = arg3.method_7947();
			int j = arg3.method_7919();
			class_1271<class_1799> lv = arg3.method_7913(arg2, arg, arg4);
			class_1799 lv2 = lv.method_5466();
			if (lv2 == arg3 && lv2.method_7947() == i && lv2.method_7935() <= 0 && lv2.method_7919() == j) {
				return lv.method_5467();
			} else if (lv.method_5467() == class_1269.field_5814 && lv2.method_7935() > 0 && !arg.method_6115()) {
				return lv.method_5467();
			} else {
				arg.method_6122(arg4, lv2);
				if (this.method_14268()) {
					lv2.method_7939(i);
					if (lv2.method_7963()) {
						lv2.method_7974(j);
					}
				}

				if (lv2.method_7960()) {
					arg.method_6122(arg4, class_1799.field_8037);
				}

				if (!arg.method_6115()) {
					((class_3222)arg).method_14204(arg.field_7498);
				}

				return lv.method_5467();
			}
		}
	}

	public class_1269 method_14262(class_1657 arg, class_1937 arg2, class_1799 arg3, class_1268 arg4, class_2338 arg5, class_2350 arg6, float f, float g, float h) {
		class_2680 lv = arg2.method_8320(arg5);
		if (this.field_14005 == class_1934.field_9219) {
			class_3908 lv2 = lv.method_17526(arg2, arg5);
			if (lv2 != null) {
				arg.method_17355(lv2);
				return class_1269.field_5812;
			} else {
				return class_1269.field_5811;
			}
		} else {
			boolean bl = !arg.method_6047().method_7960() || !arg.method_6079().method_7960();
			boolean bl2 = arg.method_5715() && bl;
			if (!bl2 && lv.method_11629(arg2, arg5, arg, arg4, arg6, f, g, h)) {
				return class_1269.field_5812;
			} else if (!arg3.method_7960() && !arg.method_7357().method_7904(arg3.method_7909())) {
				class_1838 lv3 = new class_1838(arg, arg.method_5998(arg4), arg5, arg6, f, g, h);
				if (this.method_14268()) {
					int i = arg3.method_7947();
					class_1269 lv4 = arg3.method_7981(lv3);
					arg3.method_7939(i);
					return lv4;
				} else {
					return arg3.method_7981(lv3);
				}
			} else {
				return class_1269.field_5811;
			}
		}
	}

	public void method_14259(class_3218 arg) {
		this.field_14007 = arg;
	}
}
