package net.minecraft;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class class_3765 {
	public static final class_1799 field_16609 = method_16515();
	@Nullable
	private final Map<Integer, class_3763> field_16615;
	@Nullable
	private final Map<Integer, Set<class_3763>> field_16618;
	private long field_16605;
	private class_2338 field_16613 = class_2338.field_10980;
	private class_1937 field_16619;
	private boolean field_16612;
	@Nullable
	private class_1415 field_16622;
	private boolean field_16611;
	private boolean field_16610;
	private int field_16625;
	private float field_16620;
	private int field_16623;
	private boolean field_16606;
	private int field_16621;
	private final class_3213 field_16607 = new class_3213(
		new class_2588("event.minecraft.raid"), class_1259.class_1260.field_5784, class_1259.class_1261.field_5791
	);
	private int field_16616;
	private int field_16614;
	@Nullable
	private class_238 field_16617;
	private final Random field_16608 = new Random();

	public class_3765() {
		this.field_16615 = Maps.<Integer, class_3763>newHashMap();
		this.field_16618 = Maps.<Integer, Set<class_3763>>newHashMap();
		this.field_16605 = 0L;
	}

	public class_3765(int i, class_1937 arg, class_1415 arg2) {
		this();
		this.field_16625 = i;
		this.field_16619 = arg;
		this.method_16489(arg);
		this.field_16606 = true;
		this.field_16623 = 0;
		this.field_16621 = 0;
		this.field_16616 = 0;
		this.field_16614 = 300;
		this.field_16607.method_5408(0.0F);
		if (arg2 != null) {
			this.method_16512(arg2);
			arg2.method_16468(i);
		}
	}

	public class_1937 method_16831() {
		return this.field_16619;
	}

	public boolean method_16832() {
		return this.method_16524() && this.method_16504();
	}

	@Nullable
	public class_238 method_16498() {
		return this.field_16617;
	}

	public boolean method_16524() {
		return this.field_16611;
	}

	public int method_16490() {
		return this.field_16621;
	}

	private Predicate<class_1297> method_16501() {
		if (this.field_16622 != null && this.field_16613 != null && this.field_16613 != class_2338.field_10980) {
			float f = (float)(this.field_16622.method_6403() + 24);
			return class_1301.field_6154
				.and(
					class_1301.method_5909(
						(double)this.field_16613.method_10263(), (double)this.field_16613.method_10264(), (double)this.field_16613.method_10260(), (double)f
					)
				);
		} else {
			return class_1301.field_6154.negate();
		}
	}

	private void method_16499() {
		Set<class_3222> set = Sets.<class_3222>newHashSet();

		for (class_3222 lv : this.field_16619.method_8498(class_3222.class, this.method_16501())) {
			this.field_16607.method_14088(lv);
			set.add(lv);
		}

		Set<class_3222> set2 = Sets.<class_3222>newHashSet(this.field_16607.method_14092());
		set2.removeAll(set);

		for (class_3222 lv2 : set2) {
			this.field_16607.method_14089(lv2);
		}
	}

	public int method_16514() {
		return 5;
	}

	public int method_16493() {
		return this.field_16623;
	}

	public void method_16518(class_1657 arg) {
		int i = this.method_16514();
		if (arg.method_6059(class_1294.field_16595)) {
			this.field_16623 = this.field_16623 + arg.method_6112(class_1294.field_16595).method_5578() + 1;
			if (i < this.field_16623) {
				this.field_16623 = i;
			}
		}

		arg.method_6016(class_1294.field_16595);
	}

	public void method_16489(class_1937 arg) {
		this.field_16619 = arg;
	}

	public void method_16506() {
		if (this.field_16622 != null) {
			this.field_16622.method_16468(0);
		}

		this.method_16512(null);
		this.field_16612 = true;
		this.field_16607.method_14094();
		if (this.field_16619.method_8557() != null) {
			this.field_16619.method_8557().method_80();
		}
	}

	public boolean method_16503() {
		return this.field_16612;
	}

	public void method_16509() {
		boolean bl = this.field_16606;
		if (this.field_16619 != null && this.field_16613 != null && this.field_16622 != null && this.field_16619.method_8591(this.field_16613)) {
			this.field_16606 = true;
		} else {
			this.field_16606 = false;
		}

		if (this.field_16619 != null && this.field_16619.method_8407() == class_1267.field_5801) {
			this.method_16506();
		} else {
			if (bl != this.field_16606) {
				this.field_16607.method_14091(this.field_16606);
			}

			if (this.field_16622 == null && !this.field_16606 && this.field_16619.method_8510() % 60L == 0L) {
				this.field_16622 = this.field_16619.method_8557().method_6438(this.field_16613, 16);
			}

			if (this.field_16606) {
				this.field_16605++;
				if (this.field_16622 != null && (this.field_16617 == null || this.field_16605 % 60L == 0L)) {
					this.field_16613 = this.field_16622.method_6382();
					this.field_16617 = new class_238(this.field_16613).method_1014((double)this.field_16622.method_6403()).method_1014(16.0);
				}

				int i = this.method_16517();
				if (i == 0 && !this.method_16833()) {
					if (this.field_16614 > 0) {
						if (this.field_16614 == 300 || this.field_16614 % 20 == 0) {
							this.method_16499();
						}

						this.field_16614--;
						this.field_16607.method_5408(class_3532.method_15363((float)(300 - this.field_16614) / 300.0F, 0.0F, 100.0F));
					} else if (this.field_16614 == 0 && this.field_16621 > 0) {
						this.field_16614 = 300;
						this.field_16607.method_5413(new class_2588("event.minecraft.raid"));
						return;
					}
				}

				if (i > 0 && this.field_16614 == 0 && this.field_16605 % 20L == 0L) {
					this.method_16499();
					this.method_16834();
					if (i <= 2) {
						this.field_16607
							.method_5413(new class_2588("event.minecraft.raid").method_10864(" - ").method_10852(new class_2588("event.minecraft.raid.mobs_remaining", i)));
					} else {
						this.field_16607.method_5413(new class_2588("event.minecraft.raid"));
					}
				}

				boolean bl2 = false;
				int j = 0;

				while (this.method_16519()) {
					class_2338 lv = this.method_16525(this.field_16619, j);
					if (lv != null) {
						this.field_16611 = true;
						this.method_16522(lv);
						if (!bl2) {
							this.method_16521(lv);
							bl2 = true;
						}
					} else {
						j++;
					}

					if (j > 2) {
						this.method_16506();
						break;
					}
				}

				if (this.method_16524() && this.method_16490() >= this.method_16493() * 2 && i == 0) {
					if (this.field_16616 < 40) {
						this.field_16616++;
					} else {
						this.method_16506();
					}
				}

				this.method_16520();
			}
		}
	}

	private boolean method_16833() {
		return this.method_16490() >= this.method_16493() * 2;
	}

	private void method_16834() {
		if (this.field_16622 != null && this.field_16619 != null) {
			Iterator<Set<class_3763>> iterator = this.field_16618.values().iterator();
			Set<class_3763> set = Sets.<class_3763>newHashSet();

			while (iterator.hasNext()) {
				Set<class_3763> set2 = (Set<class_3763>)iterator.next();

				for (class_3763 lv : set2) {
					if (!lv.field_5988 && lv.field_6026 == this.field_16619.method_8597().method_12460()) {
						if (lv.field_6012 <= 300) {
							continue;
						}
					} else {
						lv.method_16835(30);
					}

					if (!class_3767.method_16537(this.field_16622, lv) || lv.method_6131() > 2400) {
						lv.method_16835(lv.method_16836() + 1);
					}

					if (lv.method_16836() >= 30) {
						set.add(lv);
					}
				}
			}

			for (class_3763 lv2 : set) {
				this.method_16510(lv2, true);
			}
		}
	}

	private void method_16521(class_2338 arg) {
		if (this.field_16619 != null && this.field_16622 != null) {
			class_3218 lv = (class_3218)this.field_16619;
			float f = 13.0F;
			int i = 64;

			for (class_1657 lv2 : lv.field_9228) {
				class_243 lv3 = new class_243(lv2.field_5987, lv2.field_6010, lv2.field_6035);
				class_243 lv4 = new class_243((double)arg.method_10263(), (double)arg.method_10264(), (double)arg.method_10260());
				float g = class_3532.method_15368(
					(lv4.field_1352 - lv3.field_1352) * (lv4.field_1352 - lv3.field_1352) + (lv4.field_1350 - lv3.field_1350) * (lv4.field_1350 - lv3.field_1350)
				);
				double d = lv3.field_1352 + (double)(13.0F / g) * (lv4.field_1352 - lv3.field_1352);
				double e = lv3.field_1350 + (double)(13.0F / g) * (lv4.field_1350 - lv3.field_1350);
				if (g <= 64.0F || class_3767.method_16537(this.field_16622, lv2)) {
					((class_3222)lv2).field_13987.method_14364(new class_2767(class_3417.field_17266, class_3419.field_15254, d, lv2.field_6010, e, 64.0F, 1.0F));
				}
			}
		}
	}

	private void method_16522(class_2338 arg) {
		boolean bl = false;
		int i = this.field_16621 + 1;
		this.field_16620 = 0.0F;

		for (class_3765.class_3766 lv : class_3765.class_3766.field_16636) {
			int j = method_16488(lv, this.field_16608, i) + method_16492(lv, this.field_16608, i);

			for (int k = 0; k < j; k++) {
				class_3763 lv2 = lv.field_16629.method_5883(this.field_16619);
				this.method_16516(i, lv2, arg, false);
				if (!bl && lv2.method_16485()) {
					lv2.method_16217(true);
					this.method_16491(i, lv2);
					bl = true;
				}

				if (lv.field_16629 == class_1299.field_6134) {
					class_3763 lv3;
					if (i < 6) {
						lv3 = class_1299.field_6105.method_5883(this.field_16619);
					} else {
						lv3 = class_1299.field_6117.method_5883(this.field_16619);
					}

					this.method_16516(i, lv3, arg, false);
					lv3.method_5725(arg, 0.0F, 0.0F);
					lv3.method_5804(lv2);
				}
			}
		}

		this.field_16621++;
		this.method_16523();
		this.method_16520();
	}

	public void method_16516(int i, class_3763 arg, @Nullable class_2338 arg2, boolean bl) {
		boolean bl2 = this.method_16505(i, arg);
		if (bl2) {
			arg.method_16476(this);
			arg.method_16477(i);
			arg.method_16480(true);
			arg.method_16835(0);
			if (!bl && arg2 != null) {
				arg.method_5814((double)arg2.method_10263() + 0.5, (double)arg2.method_10264() + 1.0, (double)arg2.method_10260() + 0.5);
				arg.method_5943(this.field_16619, this.field_16619.method_8404(arg2), class_3730.field_16467, null, null);
				arg.method_16484(i, false);
				arg.field_5952 = true;
				this.field_16619.method_8649(arg);
			}
		}
	}

	private void method_16523() {
		this.field_16607.method_5408(class_3532.method_15363(this.method_16513() / this.field_16620, 0.0F, 100.0F));
	}

	public float method_16513() {
		float f = 0.0F;

		for (Set<class_3763> set : this.field_16618.values()) {
			for (class_3763 lv : set) {
				f += lv.method_6032();
			}
		}

		return f;
	}

	private boolean method_16519() {
		return this.field_16614 == 0 && this.field_16621 < this.field_16623 * 2 && this.method_16517() == 0;
	}

	public int method_16517() {
		return this.field_16618.values().stream().mapToInt(Set::size).sum();
	}

	public void method_16510(@Nonnull class_3763 arg, boolean bl) {
		Set<class_3763> set = (Set<class_3763>)this.field_16618.get(arg.method_16486());
		if (set != null) {
			boolean bl2 = set.remove(arg);
			if (bl2) {
				if (bl) {
					this.field_16620 = this.field_16620 - arg.method_6032();
				}

				this.method_16523();
				this.method_16520();
			}
		}
	}

	private void method_16520() {
		if (this.field_16619 != null) {
			this.field_16619.method_16542().method_80();
		}
	}

	private static class_1799 method_16515() {
		class_1799 lv = new class_1799(class_1802.field_8539);
		class_2487 lv2 = lv.method_7911("BlockEntityTag");
		class_2499 lv3 = new class_2582.class_3750()
			.method_16376(class_2582.field_11821, class_1767.field_7955)
			.method_16376(class_2582.field_11810, class_1767.field_7967)
			.method_16376(class_2582.field_11819, class_1767.field_7944)
			.method_16376(class_2582.field_11840, class_1767.field_7967)
			.method_16376(class_2582.field_11838, class_1767.field_7963)
			.method_16376(class_2582.field_11843, class_1767.field_7967)
			.method_16376(class_2582.field_11826, class_1767.field_7967)
			.method_16376(class_2582.field_11840, class_1767.field_7963)
			.method_16375();
		lv2.method_10566("Patterns", lv3);
		lv.method_7977(new class_2588("block.minecraft.illager_banner").method_10854(class_124.field_1065));
		return lv;
	}

	public static int method_16944(Random random, boolean bl) {
		int i = random.nextInt(9);
		if (i < 4 || !bl) {
			return 1;
		} else {
			return i < 7 ? 2 : 3;
		}
	}

	@Nullable
	public class_3763 method_16496(int i) {
		return (class_3763)this.field_16615.get(i);
	}

	@Nullable
	private class_2338 method_16525(class_1937 arg, int i) {
		if (this.field_16622 == null) {
			return null;
		} else {
			class_2338 lv = this.field_16622.method_6382();
			float f = (float)this.field_16622.method_6403();
			int j = i == 0 ? 2 : 2 - i;
			class_2338.class_2339 lv2 = new class_2338.class_2339();

			for (int k = 0; k < 20; k++) {
				float g = arg.field_9229.nextFloat() * (float) (Math.PI * 2);
				int l = lv.method_10263() + (int)(class_3532.method_15362(g) * f * (float)j) + arg.field_9229.nextInt(5);
				int m = lv.method_10260() + (int)(class_3532.method_15374(g) * f * (float)j) + arg.field_9229.nextInt(5);
				int n = arg.method_8589(class_2902.class_2903.field_13202, l, m);
				lv2.method_10103(l, n, m);
				if (!this.field_16622.method_6383(lv2)
					&& arg.method_8627(
						lv2.method_10263() - 10, lv2.method_10264() - 10, lv2.method_10260() - 10, lv2.method_10263() + 10, lv2.method_10264() + 10, lv2.method_10260() + 10
					)
					&& (
						class_1948.method_8660(class_1317.class_1319.field_6317, arg, lv2, class_1299.field_6105)
							|| arg.method_8320(lv2.method_10074()).method_11614() == class_2246.field_10477 && arg.method_8320(lv2).method_11588()
					)) {
					return lv2;
				}
			}

			return null;
		}
	}

	private boolean method_16505(int i, class_3763 arg) {
		return this.method_16487(i, arg, true);
	}

	public boolean method_16487(int i, class_3763 arg, boolean bl) {
		this.field_16618.computeIfAbsent(i, integer -> Sets.newHashSet());
		if (((Set)this.field_16618.get(i)).contains(arg)) {
			return false;
		} else {
			((Set)this.field_16618.get(i)).add(arg);
			if (bl) {
				this.field_16620 = this.field_16620 + arg.method_6032();
			}

			this.method_16523();
			this.method_16520();
			return true;
		}
	}

	public void method_16491(int i, class_3763 arg) {
		this.field_16615.put(i, arg);
		arg.method_5673(class_1304.field_6169, field_16609);
		arg.method_5946(class_1304.field_6169, 2.0F);
	}

	public void method_16500(int i) {
		this.field_16615.remove(i);
	}

	@Nullable
	public class_1415 method_16511() {
		return this.field_16622;
	}

	public void method_16512(class_1415 arg) {
		this.field_16622 = arg;
		this.field_16613 = null;
		if (arg != null) {
			this.field_16613 = arg.method_6382();
		}
	}

	public class_2338 method_16495() {
		return this.field_16613;
	}

	public int method_16494() {
		return this.field_16625;
	}

	private static int method_16488(class_3765.class_3766 arg, Random random, int i) {
		if (arg.field_16628 > i) {
			return 0;
		} else {
			switch (arg) {
				case field_16633:
					return class_3532.method_15375(0.125F * (float)i + 2.875F);
				case field_16631:
					return class_3532.method_15375(0.667F * (float)i + 1.334F);
				case field_16634:
					return i == 10 ? 1 : 0;
				case field_16630:
					if (i == 1) {
						return 1;
					}

					return random.nextInt(2);
				case field_16635:
					return i >= 3 ? 1 : 0;
				default:
					return 1;
			}
		}
	}

	private static int method_16492(class_3765.class_3766 arg, Random random, int i) {
		if (arg.field_16628 > i) {
			return 0;
		} else {
			int j;
			switch (arg) {
				case field_16633:
				case field_16631:
					j = 2;
					break;
				case field_16634:
				case field_16630:
				default:
					return 0;
				case field_16635:
					j = 1;
			}

			return random.nextInt(j);
		}
	}

	public boolean method_16504() {
		return this.field_16606;
	}

	public void method_16497(class_2487 arg) {
		this.field_16625 = arg.method_10550("Id");
		this.field_16611 = arg.method_10577("Started");
		this.field_16606 = arg.method_10577("Active");
		this.field_16605 = arg.method_10537("TicksActive");
		this.field_16612 = arg.method_10577("MarkedForRemoval");
		this.field_16610 = arg.method_10577("IgnoreDayRequirement");
		this.field_16623 = arg.method_10550("BadOmenLevel");
		this.field_16621 = arg.method_10550("GroupsSpawned");
		this.field_16614 = arg.method_10550("PreRaidTicks");
		this.field_16616 = arg.method_10550("PostRaidTicks");
		this.field_16620 = arg.method_10583("TotalHealth");
		if (arg.method_10573("CX", 3)) {
			this.field_16613 = new class_2338(arg.method_10550("CX"), arg.method_10550("CY"), arg.method_10550("CZ"));
		}
	}

	public class_2487 method_16502(class_2487 arg) {
		arg.method_10569("Id", this.field_16625);
		arg.method_10556("Started", this.field_16611);
		arg.method_10556("Active", this.field_16606);
		arg.method_10544("TicksActive", this.field_16605);
		arg.method_10556("MarkedForRemoval", this.field_16612);
		arg.method_10556("IgnoreDayRequirement", this.field_16610);
		arg.method_10569("BadOmenLevel", this.field_16623);
		arg.method_10569("GroupsSpawned", this.field_16621);
		arg.method_10569("PreRaidTicks", this.field_16614);
		arg.method_10569("PostRaidTicks", this.field_16616);
		arg.method_10548("TotalHealth", this.field_16620);
		if (this.field_16613 != null) {
			arg.method_10569("CX", this.field_16613.method_10263());
			arg.method_10569("CY", this.field_16613.method_10264());
			arg.method_10569("CZ", this.field_16613.method_10260());
		}

		return arg;
	}

	static enum class_3766 {
		field_16631(class_1299.field_6117, 1),
		field_16634(class_1299.field_6090, 10),
		field_16633(class_1299.field_6105, 1),
		field_16635(class_1299.field_6145, 4),
		field_16630(class_1299.field_6134, 2);

		private static final class_3765.class_3766[] field_16636 = values();
		private final class_1299<? extends class_3763> field_16629;
		private final int field_16628;

		private class_3766(class_1299<? extends class_3763> arg, int j) {
			this.field_16629 = arg;
			this.field_16628 = j;
		}
	}
}
