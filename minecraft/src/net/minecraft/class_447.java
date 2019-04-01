package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_447 extends class_437 implements class_452 {
	protected final class_437 field_2648;
	private class_447.class_4198 field_2644;
	private class_447.class_4200 field_2642;
	private class_447.class_4202 field_2646;
	private final class_3469 field_2647;
	@Nullable
	private class_350<?> field_2643;
	private boolean field_2645 = true;

	public class_447(class_437 arg, class_3469 arg2) {
		super(new class_2588("gui.stats"));
		this.field_2648 = arg;
		this.field_2647 = arg2;
	}

	@Override
	protected void init() {
		this.field_2645 = true;
		this.minecraft.method_1562().method_2883(new class_2799(class_2799.class_2800.field_12775));
	}

	public void method_2270() {
		this.field_2644 = new class_447.class_4198(this.minecraft);
		this.field_2642 = new class_447.class_4200(this.minecraft);
		this.field_2646 = new class_447.class_4202(this.minecraft);
	}

	public void method_2267() {
		this.addButton(
			new class_4185(this.width / 2 - 120, this.height - 52, 80, 20, class_1074.method_4662("stat.generalButton"), arg -> this.method_19390(this.field_2644))
		);
		class_4185 lv = this.addButton(
			new class_4185(this.width / 2 - 40, this.height - 52, 80, 20, class_1074.method_4662("stat.itemsButton"), arg -> this.method_19390(this.field_2642))
		);
		class_4185 lv2 = this.addButton(
			new class_4185(this.width / 2 + 40, this.height - 52, 80, 20, class_1074.method_4662("stat.mobsButton"), arg -> this.method_19390(this.field_2646))
		);
		this.addButton(
			new class_4185(this.width / 2 - 100, this.height - 28, 200, 20, class_1074.method_4662("gui.done"), arg -> this.minecraft.method_1507(this.field_2648))
		);
		if (this.field_2642.children().isEmpty()) {
			lv.active = false;
		}

		if (this.field_2646.children().isEmpty()) {
			lv2.active = false;
		}
	}

	@Override
	public void render(int i, int j, float f) {
		if (this.field_2645) {
			this.renderBackground();
			this.drawCenteredString(this.font, class_1074.method_4662("multiplayer.downloadingStats"), this.width / 2, this.height / 2, 16777215);
			this.drawCenteredString(
				this.font, field_2668[(int)(class_156.method_658() / 150L % (long)field_2668.length)], this.width / 2, this.height / 2 + 9 * 2, 16777215
			);
		} else {
			this.method_19399().render(i, j, f);
			this.drawCenteredString(this.font, this.title.method_10863(), this.width / 2, 20, 16777215);
			super.render(i, j, f);
		}
	}

	@Override
	public void method_2300() {
		if (this.field_2645) {
			this.method_2270();
			this.method_2267();
			this.method_19390(this.field_2644);
			this.field_2645 = false;
		}
	}

	@Override
	public boolean isPauseScreen() {
		return !this.field_2645;
	}

	@Nullable
	public class_350<?> method_19399() {
		return this.field_2643;
	}

	public void method_19390(@Nullable class_350<?> arg) {
		this.children.remove(this.field_2644);
		this.children.remove(this.field_2642);
		this.children.remove(this.field_2646);
		if (arg != null) {
			this.children.add(0, arg);
			this.field_2643 = arg;
		}
	}

	private int method_2285(int i) {
		return 115 + 40 * i;
	}

	private void method_2289(int i, int j, class_1792 arg) {
		this.method_2272(i + 1, j + 1);
		GlStateManager.enableRescaleNormal();
		class_308.method_1453();
		this.itemRenderer.method_4010(arg.method_7854(), i + 2, j + 2);
		class_308.method_1450();
		GlStateManager.disableRescaleNormal();
	}

	private void method_2272(int i, int j) {
		this.method_2282(i, j, 0, 0);
	}

	private void method_2282(int i, int j, int k, int l) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.method_1531().method_4618(STATS_ICON_LOCATION);
		float f = 0.0078125F;
		float g = 0.0078125F;
		int m = 18;
		int n = 18;
		class_289 lv = class_289.method_1348();
		class_287 lv2 = lv.method_1349();
		lv2.method_1328(7, class_290.field_1585);
		lv2.method_1315((double)(i + 0), (double)(j + 18), (double)this.blitOffset)
			.method_1312((double)((float)(k + 0) * 0.0078125F), (double)((float)(l + 18) * 0.0078125F))
			.method_1344();
		lv2.method_1315((double)(i + 18), (double)(j + 18), (double)this.blitOffset)
			.method_1312((double)((float)(k + 18) * 0.0078125F), (double)((float)(l + 18) * 0.0078125F))
			.method_1344();
		lv2.method_1315((double)(i + 18), (double)(j + 0), (double)this.blitOffset)
			.method_1312((double)((float)(k + 18) * 0.0078125F), (double)((float)(l + 0) * 0.0078125F))
			.method_1344();
		lv2.method_1315((double)(i + 0), (double)(j + 0), (double)this.blitOffset)
			.method_1312((double)((float)(k + 0) * 0.0078125F), (double)((float)(l + 0) * 0.0078125F))
			.method_1344();
		lv.method_1350();
	}

	@Environment(EnvType.CLIENT)
	class class_4198 extends class_350<class_447.class_4198.class_4197> {
		public class_4198(class_310 arg2) {
			super(arg2, class_447.this.width, class_447.this.height, 32, class_447.this.height - 64, 10);

			for (class_3445<class_2960> lv : class_3468.field_15419) {
				this.method_1901(new class_447.class_4198.class_4197(lv));
			}
		}

		@Override
		protected void method_20076() {
			class_447.this.renderBackground();
		}

		@Environment(EnvType.CLIENT)
		class class_4197 extends class_350.class_351<class_447.class_4198.class_4197> {
			private final class_3445<class_2960> field_18749;

			private class_4197(class_3445<class_2960> arg2) {
				this.field_18749 = arg2;
			}

			@Override
			public void method_1903(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
				class_2561 lv = new class_2588("stat." + this.field_18749.method_14951().toString().replace(':', '.')).method_10854(class_124.field_1080);
				class_4198.this.drawString(class_447.this.font, lv.getString(), k + 2, j + 1, i % 2 == 0 ? 16777215 : 9474192);
				String string = this.field_18749.method_14953(class_447.this.field_2647.method_15025(this.field_18749));
				class_4198.this.drawString(class_447.this.font, string, k + 2 + 213 - class_447.this.font.method_1727(string), j + 1, i % 2 == 0 ? 16777215 : 9474192);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	class class_4200 extends class_350<class_447.class_4200.class_4199> {
		protected final List<class_3448<class_2248>> field_18754;
		protected final List<class_3448<class_1792>> field_18755;
		private final int[] field_18753 = new int[]{3, 4, 1, 2, 5, 6};
		protected int field_18756 = -1;
		protected final List<class_1792> field_18757;
		protected final Comparator<class_1792> field_18758 = new class_447.class_4200.class_450();
		@Nullable
		protected class_3448<?> field_18759;
		protected int field_18760;

		public class_4200(class_310 arg2) {
			super(arg2, class_447.this.width, class_447.this.height, 32, class_447.this.height - 64, 20);
			this.field_18754 = Lists.<class_3448<class_2248>>newArrayList();
			this.field_18754.add(class_3468.field_15427);
			this.field_18755 = Lists.<class_3448<class_1792>>newArrayList(
				class_3468.field_15383, class_3468.field_15370, class_3468.field_15372, class_3468.field_15392, class_3468.field_15405
			);
			this.method_20063(true, 20);
			Set<class_1792> set = Sets.newIdentityHashSet();

			for (class_1792 lv : class_2378.field_11142) {
				boolean bl = false;

				for (class_3448<class_1792> lv2 : this.field_18755) {
					if (lv2.method_14958(lv) && class_447.this.field_2647.method_15025(lv2.method_14956(lv)) > 0) {
						bl = true;
					}
				}

				if (bl) {
					set.add(lv);
				}
			}

			for (class_2248 lv3 : class_2378.field_11146) {
				boolean bl = false;

				for (class_3448<class_2248> lv2x : this.field_18754) {
					if (lv2x.method_14958(lv3) && class_447.this.field_2647.method_15025(lv2x.method_14956(lv3)) > 0) {
						bl = true;
					}
				}

				if (bl) {
					set.add(lv3.method_8389());
				}
			}

			set.remove(class_1802.field_8162);
			this.field_18757 = Lists.<class_1792>newArrayList(set);

			for (int i = 0; i < this.field_18757.size(); i++) {
				this.method_1901(new class_447.class_4200.class_4199());
			}
		}

		@Override
		protected void method_20061(int i, int j, class_289 arg) {
			if (!this.field_19081.field_1729.method_1608()) {
				this.field_18756 = -1;
			}

			for (int k = 0; k < this.field_18753.length; k++) {
				class_447.this.method_2282(i + class_447.this.method_2285(k) - 18, j + 1, 0, this.field_18756 == k ? 0 : 18);
			}

			if (this.field_18759 != null) {
				int k = class_447.this.method_2285(this.method_19409(this.field_18759)) - 36;
				int l = this.field_18760 == 1 ? 2 : 1;
				class_447.this.method_2282(i + k, j + 1, 18 * l, 0);
			}

			for (int k = 0; k < this.field_18753.length; k++) {
				int l = this.field_18756 == k ? 1 : 0;
				class_447.this.method_2282(i + class_447.this.method_2285(k) - 18 + l, j + 1 + l, 18 * this.field_18753[k], 18);
			}
		}

		@Override
		public int method_20053() {
			return 375;
		}

		@Override
		protected int method_20078() {
			return this.field_19083 / 2 + 140;
		}

		@Override
		protected void method_20076() {
			class_447.this.renderBackground();
		}

		@Override
		protected void method_20058(int i, int j) {
			this.field_18756 = -1;

			for (int k = 0; k < this.field_18753.length; k++) {
				int l = i - class_447.this.method_2285(k);
				if (l >= -36 && l <= 0) {
					this.field_18756 = k;
					break;
				}
			}

			if (this.field_18756 >= 0) {
				this.method_19408(this.method_19410(this.field_18756));
				this.field_19081.method_1483().method_4873(class_1109.method_4758(class_3417.field_15015, 1.0F));
			}
		}

		private class_3448<?> method_19410(int i) {
			return i < this.field_18754.size() ? (class_3448)this.field_18754.get(i) : (class_3448)this.field_18755.get(i - this.field_18754.size());
		}

		private int method_19409(class_3448<?> arg) {
			int i = this.field_18754.indexOf(arg);
			if (i >= 0) {
				return i;
			} else {
				int j = this.field_18755.indexOf(arg);
				return j >= 0 ? j + this.field_18754.size() : -1;
			}
		}

		@Override
		protected void method_20066(int i, int j) {
			if (j >= this.field_19085 && j <= this.field_19086) {
				class_447.class_4200.class_4199 lv = this.method_20055((double)i, (double)j);
				int k = (this.field_19083 - this.method_20053()) / 2;
				if (lv != null) {
					if (i < k + 40 || i > k + 40 + 20) {
						return;
					}

					class_1792 lv2 = (class_1792)this.field_18757.get(this.children().indexOf(lv));
					this.method_19407(this.method_19406(lv2), i, j);
				} else {
					class_2561 lv3 = null;
					int l = i - k;

					for (int m = 0; m < this.field_18753.length; m++) {
						int n = class_447.this.method_2285(m);
						if (l >= n - 18 && l <= n) {
							lv3 = new class_2588(this.method_19410(m).method_14957());
							break;
						}
					}

					this.method_19407(lv3, i, j);
				}
			}
		}

		protected void method_19407(@Nullable class_2561 arg, int i, int j) {
			if (arg != null) {
				String string = arg.method_10863();
				int k = i + 12;
				int l = j - 12;
				int m = class_447.this.font.method_1727(string);
				this.fillGradient(k - 3, l - 3, k + m + 3, l + 8 + 3, -1073741824, -1073741824);
				class_447.this.font.method_1720(string, (float)k, (float)l, -1);
			}
		}

		protected class_2561 method_19406(class_1792 arg) {
			return arg.method_7848();
		}

		protected void method_19408(class_3448<?> arg) {
			if (arg != this.field_18759) {
				this.field_18759 = arg;
				this.field_18760 = -1;
			} else if (this.field_18760 == -1) {
				this.field_18760 = 1;
			} else {
				this.field_18759 = null;
				this.field_18760 = 0;
			}

			this.field_18757.sort(this.field_18758);
		}

		@Environment(EnvType.CLIENT)
		class class_4199 extends class_350.class_351<class_447.class_4200.class_4199> {
			private class_4199() {
			}

			@Override
			public void method_1903(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
				class_1792 lv = (class_1792)class_447.this.field_2642.field_18757.get(i);
				class_447.this.method_2289(k + 40, j, lv);

				for (int p = 0; p < class_447.this.field_2642.field_18754.size(); p++) {
					class_3445<class_2248> lv2;
					if (lv instanceof class_1747) {
						lv2 = ((class_3448)class_447.this.field_2642.field_18754.get(p)).method_14956(((class_1747)lv).method_7711());
					} else {
						lv2 = null;
					}

					this.method_19405(lv2, k + class_447.this.method_2285(p), j, i % 2 == 0);
				}

				for (int p = 0; p < class_447.this.field_2642.field_18755.size(); p++) {
					this.method_19405(
						((class_3448)class_447.this.field_2642.field_18755.get(p)).method_14956(lv),
						k + class_447.this.method_2285(p + class_447.this.field_2642.field_18754.size()),
						j,
						i % 2 == 0
					);
				}
			}

			protected void method_19405(@Nullable class_3445<?> arg, int i, int j, boolean bl) {
				String string = arg == null ? "-" : arg.method_14953(class_447.this.field_2647.method_15025(arg));
				class_4200.this.drawString(class_447.this.font, string, i - class_447.this.font.method_1727(string), j + 5, bl ? 16777215 : 9474192);
			}
		}

		@Environment(EnvType.CLIENT)
		class class_450 implements Comparator<class_1792> {
			private class_450() {
			}

			public int method_2297(class_1792 arg, class_1792 arg2) {
				int i;
				int j;
				if (class_4200.this.field_18759 == null) {
					i = 0;
					j = 0;
				} else if (class_4200.this.field_18754.contains(class_4200.this.field_18759)) {
					class_3448<class_2248> lv = (class_3448<class_2248>)class_4200.this.field_18759;
					i = arg instanceof class_1747 ? class_447.this.field_2647.method_15024(lv, ((class_1747)arg).method_7711()) : -1;
					j = arg2 instanceof class_1747 ? class_447.this.field_2647.method_15024(lv, ((class_1747)arg2).method_7711()) : -1;
				} else {
					class_3448<class_1792> lv = (class_3448<class_1792>)class_4200.this.field_18759;
					i = class_447.this.field_2647.method_15024(lv, arg);
					j = class_447.this.field_2647.method_15024(lv, arg2);
				}

				return i == j
					? class_4200.this.field_18760 * Integer.compare(class_1792.method_7880(arg), class_1792.method_7880(arg2))
					: class_4200.this.field_18760 * Integer.compare(i, j);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	class class_4202 extends class_350<class_447.class_4202.class_4201> {
		public class_4202(class_310 arg2) {
			super(arg2, class_447.this.width, class_447.this.height, 32, class_447.this.height - 64, 9 * 4);

			for (class_1299<?> lv : class_2378.field_11145) {
				if (class_447.this.field_2647.method_15025(class_3468.field_15403.method_14956(lv)) > 0
					|| class_447.this.field_2647.method_15025(class_3468.field_15411.method_14956(lv)) > 0) {
					this.method_1901(new class_447.class_4202.class_4201(lv));
				}
			}
		}

		@Override
		protected void method_20076() {
			class_447.this.renderBackground();
		}

		@Environment(EnvType.CLIENT)
		class class_4201 extends class_350.class_351<class_447.class_4202.class_4201> {
			private final class_1299<?> field_18762;

			public class_4201(class_1299<?> arg2) {
				this.field_18762 = arg2;
			}

			@Override
			public void method_1903(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
				String string = class_1074.method_4662(class_156.method_646("entity", class_1299.method_5890(this.field_18762)));
				int p = class_447.this.field_2647.method_15025(class_3468.field_15403.method_14956(this.field_18762));
				int q = class_447.this.field_2647.method_15025(class_3468.field_15411.method_14956(this.field_18762));
				class_4202.this.drawString(class_447.this.font, string, k + 2, j + 1, 16777215);
				class_4202.this.drawString(class_447.this.font, this.method_19411(string, p), k + 2 + 10, j + 1 + 9, p == 0 ? 6316128 : 9474192);
				class_4202.this.drawString(class_447.this.font, this.method_19412(string, q), k + 2 + 10, j + 1 + 9 * 2, q == 0 ? 6316128 : 9474192);
			}

			private String method_19411(String string, int i) {
				String string2 = class_3468.field_15403.method_14957();
				return i == 0 ? class_1074.method_4662(string2 + ".none", string) : class_1074.method_4662(string2, i, string);
			}

			private String method_19412(String string, int i) {
				String string2 = class_3468.field_15411.method_14957();
				return i == 0 ? class_1074.method_4662(string2 + ".none", string) : class_1074.method_4662(string2, string, i);
			}
		}
	}
}
