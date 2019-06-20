package net.minecraft;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
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
	private final boolean field_18976;

	public class_3872(class_3872.class_3931 arg) {
		this(arg, true);
	}

	public class_3872() {
		this(field_17417, false);
	}

	private class_3872(class_3872.class_3931 arg, boolean bl) {
		super(class_333.field_18967);
		this.field_17418 = arg;
		this.field_18976 = bl;
	}

	public void method_17554(class_3872.class_3931 arg) {
		this.field_17418 = arg;
		this.field_17119 = class_3532.method_15340(this.field_17119, 0, arg.method_17560());
		this.method_17059();
		this.field_17121 = -1;
	}

	public boolean method_17556(int i) {
		int j = class_3532.method_15340(i, 0, this.field_17418.method_17560() - 1);
		if (j != this.field_17119) {
			this.field_17119 = j;
			this.method_17059();
			this.field_17121 = -1;
			return true;
		} else {
			return false;
		}
	}

	protected boolean method_17789(int i) {
		return this.method_17556(i);
	}

	@Override
	protected void init() {
		this.method_17557();
		this.method_17558();
	}

	protected void method_17557() {
		this.addButton(new class_4185(this.width / 2 - 100, 196, 200, 20, class_1074.method_4662("gui.done"), arg -> this.minecraft.method_1507(null)));
	}

	protected void method_17558() {
		int i = (this.width - 192) / 2;
		int j = 2;
		this.field_17122 = this.addButton(new class_474(i + 116, 159, true, arg -> this.method_17058(), this.field_18976));
		this.field_17123 = this.addButton(new class_474(i + 43, 159, false, arg -> this.method_17057(), this.field_18976));
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
		this.field_17122.visible = this.field_17119 < this.method_17055() - 1;
		this.field_17123.visible = this.field_17119 > 0;
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (super.keyPressed(i, j, k)) {
			return true;
		} else {
			switch (i) {
				case 266:
					this.field_17123.onPress();
					return true;
				case 267:
					this.field_17122.onPress();
					return true;
				default:
					return false;
			}
		}
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.method_1531().method_4618(field_17117);
		int k = (this.width - 192) / 2;
		int l = 2;
		this.blit(k, 2, 0, 0, 192, 192);
		String string = class_1074.method_4662("book.pageIndicator", this.field_17119 + 1, Math.max(this.method_17055(), 1));
		if (this.field_17121 != this.field_17119) {
			class_2561 lv = this.field_17418.method_17563(this.field_17119);
			this.field_17120 = class_341.method_1850(lv, 114, this.font, true, true);
		}

		this.field_17121 = this.field_17119;
		int m = this.method_17053(string);
		this.font.method_1729(string, (float)(k - m + 192 - 44), 18.0F, 0);
		int n = Math.min(128 / 9, this.field_17120.size());

		for (int o = 0; o < n; o++) {
			class_2561 lv2 = (class_2561)this.field_17120.get(o);
			this.font.method_1729(lv2.method_10863(), (float)(k + 36), (float)(32 + o * 9), 0);
		}

		class_2561 lv3 = this.method_17048((double)i, (double)j);
		if (lv3 != null) {
			this.renderComponentHoverEffect(lv3, i, j);
		}

		super.render(i, j, f);
	}

	private int method_17053(String string) {
		return this.font.method_1727(this.font.method_1726() ? this.font.method_1721(string) : string);
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (i == 0) {
			class_2561 lv = this.method_17048(d, e);
			if (lv != null && this.handleComponentClicked(lv)) {
				return true;
			}
		}

		return super.mouseClicked(d, e, i);
	}

	@Override
	public boolean handleComponentClicked(class_2561 arg) {
		class_2558 lv = arg.method_10866().method_10970();
		if (lv == null) {
			return false;
		} else if (lv.method_10845() == class_2558.class_2559.field_11748) {
			String string = lv.method_10844();

			try {
				int i = Integer.parseInt(string) - 1;
				return this.method_17789(i);
			} catch (Exception var5) {
				return false;
			}
		} else {
			boolean bl = super.handleComponentClicked(arg);
			if (bl && lv.method_10845() == class_2558.class_2559.field_11750) {
				this.minecraft.method_1507(null);
			}

			return bl;
		}
	}

	@Nullable
	public class_2561 method_17048(double d, double e) {
		if (this.field_17120 == null) {
			return null;
		} else {
			int i = class_3532.method_15357(d - (double)((this.width - 192) / 2) - 36.0);
			int j = class_3532.method_15357(e - 2.0 - 30.0);
			if (i >= 0 && j >= 0) {
				int k = Math.min(128 / 9, this.field_17120.size());
				if (i <= 114 && j < 9 * k + k) {
					int l = j / 9;
					if (l >= 0 && l < this.field_17120.size()) {
						class_2561 lv = (class_2561)this.field_17120.get(l);
						int m = 0;

						for (class_2561 lv2 : lv) {
							if (lv2 instanceof class_2585) {
								m += this.minecraft.field_1772.method_1727(lv2.method_10863());
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
			return (List<String>)(lv != null && class_1843.method_8053(lv)
				? class_3872.method_17555(lv)
				: ImmutableList.of(new class_2588("book.invalid.tag").method_10854(class_124.field_1079).method_10863()));
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
			} catch (Exception var4) {
			}

			return new class_2585(string);
		}
	}
}
