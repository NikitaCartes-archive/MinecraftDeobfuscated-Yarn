package net.minecraft;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_3872 extends class_437 {
	public static final class_3872.class_3931 field_17417 = new class_3872.class_3931() {
		@Override
		public int method_17560() {
			return 0;
		}

		@Override
		public class_2561 method_17561(int i) {
			return new class_2585("");
		}
	};
	public static final class_2960 field_17117 = new class_2960("textures/gui/book.png");
	private class_3872.class_3931 field_17418;
	private int field_17119;
	private List<class_2561> field_17120 = Collections.emptyList();
	private int field_17121 = -1;
	private class_474 field_17122;
	private class_474 field_17123;

	public class_3872(class_3872.class_3931 arg) {
		this.field_17418 = arg;
	}

	public class_3872() {
		this(field_17417);
	}

	public void method_17554(class_3872.class_3931 arg) {
		this.field_17418 = arg;
		this.field_17119 = class_3532.method_15340(this.field_17119, 0, arg.method_17560());
		this.method_17059();
		this.field_17121 = -1;
	}

	public void method_17556(int i) {
		if (i != this.field_17119) {
			this.field_17119 = class_3532.method_15340(i, 0, this.field_17418.method_17560());
			this.method_17059();
			this.field_17121 = -1;
		}
	}

	@Override
	protected void method_2224() {
		this.method_17557();
		this.method_17558();
	}

	protected void method_17557() {
		this.method_2219(new class_339(0, this.field_2561 / 2 - 100, 196, 200, 20, class_1074.method_4662("gui.done")) {
			@Override
			public void method_1826(double d, double e) {
				class_3872.this.field_2563.method_1507(null);
			}
		});
	}

	protected void method_17558() {
		int i = (this.field_2561 - 192) / 2;
		int j = 2;
		this.field_17122 = this.method_2219(new class_474(1, i + 116, 159, true) {
			@Override
			public void method_1826(double d, double e) {
				class_3872.this.method_17058();
			}

			@Override
			public void method_1832(class_1144 arg) {
				class_3872.this.method_17559();
			}
		});
		this.field_17123 = this.method_2219(new class_474(2, i + 43, 159, false) {
			@Override
			public void method_1826(double d, double e) {
				class_3872.this.method_17057();
			}

			@Override
			public void method_1832(class_1144 arg) {
				class_3872.this.method_17559();
			}
		});
		this.method_17059();
	}

	private int method_17055() {
		return this.field_17418.method_17560();
	}

	protected void method_17057() {
		if (this.field_17119 > 0) {
			this.field_17119--;
		}

		this.method_17059();
	}

	protected void method_17058() {
		if (this.field_17119 < this.method_17055() - 1) {
			this.field_17119++;
		}

		this.method_17059();
	}

	private void method_17059() {
		this.field_17122.field_2076 = this.field_17119 < this.method_17055() - 1;
		this.field_17123.field_2076 = this.field_17119 > 0;
	}

	@Override
	public boolean method_16805(int i, int j, int k) {
		if (super.method_16805(i, j, k)) {
			return true;
		} else {
			switch (i) {
				case 266:
					this.field_17123.method_1826(0.0, 0.0);
					return true;
				case 267:
					this.field_17122.method_1826(0.0, 0.0);
					return true;
				default:
					return false;
			}
		}
	}

	@Override
	public void method_2214(int i, int j, float f) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.field_2563.method_1531().method_4618(field_17117);
		int k = (this.field_2561 - 192) / 2;
		int l = 2;
		this.method_1788(k, 2, 0, 0, 192, 192);
		String string = class_1074.method_4662("book.pageIndicator", this.field_17119 + 1, Math.max(this.method_17055(), 1));
		if (this.field_17121 != this.field_17119) {
			class_2561 lv = this.field_17418.method_17563(this.field_17119);
			this.field_17120 = class_341.method_1850(lv, 114, this.field_2554, true, true);
		}

		this.field_17121 = this.field_17119;
		int m = this.method_17053(string);
		this.field_2554.method_1729(string, (float)(k - m + 192 - 44), 18.0F, 0);
		int n = Math.min(128 / 9, this.field_17120.size());

		for (int o = 0; o < n; o++) {
			class_2561 lv2 = (class_2561)this.field_17120.get(o);
			this.field_2554.method_1729(lv2.method_10863(), (float)(k + 36), (float)(32 + o * 9), 0);
		}

		class_2561 lv3 = this.method_17048((double)i, (double)j);
		if (lv3 != null) {
			this.method_2229(lv3, i, j);
		}

		super.method_2214(i, j, f);
	}

	private int method_17053(String string) {
		return this.field_2554.method_1727(this.field_2554.method_1726() ? this.field_2554.method_1721(string) : string);
	}

	@Override
	public boolean method_16807(double d, double e, int i) {
		if (i == 0) {
			class_2561 lv = this.method_17048(d, e);
			if (lv != null && this.method_2216(lv)) {
				return true;
			}
		}

		return super.method_16807(d, e, i);
	}

	@Override
	public boolean method_2216(class_2561 arg) {
		class_2558 lv = arg.method_10866().method_10970();
		if (lv == null) {
			return false;
		} else if (lv.method_10845() == class_2558.class_2559.field_11748) {
			String string = lv.method_10844();

			try {
				int i = Integer.parseInt(string) - 1;
				if (i >= 0 && i < this.method_17055() && i != this.field_17119) {
					this.field_17119 = i;
					this.method_17059();
					return true;
				}
			} catch (Exception var5) {
			}

			return false;
		} else {
			boolean bl = super.method_2216(arg);
			if (bl && lv.method_10845() == class_2558.class_2559.field_11750) {
				this.field_2563.method_1507(null);
			}

			return bl;
		}
	}

	@Nullable
	public class_2561 method_17048(double d, double e) {
		if (this.field_17120 == null) {
			return null;
		} else {
			int i = class_3532.method_15357(d - (double)((this.field_2561 - 192) / 2) - 36.0);
			int j = class_3532.method_15357(e - 2.0 - 16.0 - 16.0);
			if (i >= 0 && j >= 0) {
				int k = Math.min(128 / 9, this.field_17120.size());
				if (i <= 114 && j < 9 * k + k) {
					int l = j / 9;
					if (l >= 0 && l < this.field_17120.size()) {
						class_2561 lv = (class_2561)this.field_17120.get(l);
						int m = 0;

						for (class_2561 lv2 : lv) {
							if (lv2 instanceof class_2585) {
								m += this.field_2563.field_1772.method_1727(lv2.method_10863());
								if (m > i) {
									return lv2;
								}
							}
						}
					}

					return null;
				} else {
					return null;
				}
			} else {
				return null;
			}
		}
	}

	public static List<String> method_17555(class_2487 arg) {
		class_2499 lv = arg.method_10554("pages", 8).method_10612();
		Builder<String> builder = ImmutableList.builder();

		for (int i = 0; i < lv.size(); i++) {
			builder.add(lv.method_10608(i));
		}

		return builder.build();
	}

	protected void method_17559() {
		class_310.method_1551().method_1483().method_4873(class_1109.method_4758(class_3417.field_17481, 1.0F));
	}

	@Environment(EnvType.CLIENT)
	public interface class_3931 {
		int method_17560();

		class_2561 method_17561(int i);

		default class_2561 method_17563(int i) {
			return (class_2561)(i >= 0 && i < this.method_17560() ? this.method_17561(i) : new class_2585(""));
		}

		static class_3872.class_3931 method_17562(class_1799 arg) {
			class_1792 lv = arg.method_7909();
			if (lv == class_1802.field_8360) {
				return new class_3872.class_3933(arg);
			} else {
				return (class_3872.class_3931)(lv == class_1802.field_8674 ? new class_3872.class_3932(arg) : class_3872.field_17417);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_3932 implements class_3872.class_3931 {
		private final List<String> field_17419;

		public class_3932(class_1799 arg) {
			this.field_17419 = method_17564(arg);
		}

		private static List<String> method_17564(class_1799 arg) {
			class_2487 lv = arg.method_7969();
			return (List<String>)(lv != null ? class_3872.method_17555(lv) : ImmutableList.of());
		}

		@Override
		public int method_17560() {
			return this.field_17419.size();
		}

		@Override
		public class_2561 method_17561(int i) {
			return new class_2585((String)this.field_17419.get(i));
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_3933 implements class_3872.class_3931 {
		private final List<String> field_17420;

		public class_3933(class_1799 arg) {
			this.field_17420 = method_17565(arg);
		}

		private static List<String> method_17565(class_1799 arg) {
			class_2487 lv = arg.method_7969();
			if (lv != null) {
				return (List<String>)(class_1843.method_8053(lv) ? class_3872.method_17555(lv) : ImmutableList.of(class_124.field_1079 + "* Invalid book tag *"));
			} else {
				return ImmutableList.of();
			}
		}

		@Override
		public int method_17560() {
			return this.field_17420.size();
		}

		@Override
		public class_2561 method_17561(int i) {
			String string = (String)this.field_17420.get(i);

			try {
				class_2561 lv = class_2561.class_2562.method_10877(string);
				if (lv != null) {
					return lv;
				}
			} catch (JsonParseException var4) {
			}

			return new class_2585(string);
		}
	}
}
