package net.minecraft;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class class_3765 {
	private static final class_2588 field_19016 = new class_2588("event.minecraft.raid");
	private static final class_2588 field_19017 = new class_2588("event.minecraft.raid.victory");
	private static final class_2588 field_19018 = new class_2588("event.minecraft.raid.defeat");
	private static final class_2561 field_19019 = field_19016.method_11020().method_10864(" - ").method_10852(field_19017);
	private static final class_2561 field_19020 = field_19016.method_11020().method_10864(" - ").method_10852(field_19018);
	private final Map<Integer, class_3763> field_16615 = Maps.<Integer, class_3763>newHashMap();
	private final Map<Integer, Set<class_3763>> field_16618 = Maps.<Integer, Set<class_3763>>newHashMap();
	private final Set<UUID> field_19021 = Sets.<UUID>newHashSet();
	private long field_16605;
	private class_2338 field_16613;
	private final class_3218 field_16619;
	private boolean field_16611;
	private final int field_16625;
	private float field_16620;
	private int field_16623;
	private boolean field_16606;
	private int field_16621;
	private final class_3213 field_16607 = new class_3213(field_19016, class_1259.class_1260.field_5784, class_1259.class_1261.field_5791);
	private int field_16616;
	private int field_16614;
	private final Random field_16608 = new Random();
	private final int field_19022;
	private class_3765.class_4259 field_19023;
	private int field_19024;
	private Optional<class_2338> field_19172 = Optional.empty();

	public class_3765(int i, class_3218 arg, class_2338 arg2) {
		this.field_16625 = i;
		this.field_16619 = arg;
		this.field_16606 = true;
		this.field_16614 = 300;
		this.field_16607.method_5408(0.0F);
		this.field_16613 = arg2;
		this.field_19022 = this.method_20016(arg.method_8407());
		this.field_19023 = class_3765.class_4259.field_19026;
	}

	public class_3765(class_3218 arg, class_2487 arg2) {
		this.field_16619 = arg;
		this.field_16625 = arg2.method_10550("Id");
		this.field_16611 = arg2.method_10577("Started");
		this.field_16606 = arg2.method_10577("Active");
		this.field_16605 = arg2.method_10537("TicksActive");
		this.field_16623 = arg2.method_10550("BadOmenLevel");
		this.field_16621 = arg2.method_10550("GroupsSpawned");
		this.field_16614 = arg2.method_10550("PreRaidTicks");
		this.field_16616 = arg2.method_10550("PostRaidTicks");
		this.field_16620 = arg2.method_10583("TotalHealth");
		this.field_16613 = new class_2338(arg2.method_10550("CX"), arg2.method_10550("CY"), arg2.method_10550("CZ"));
		this.field_19022 = arg2.method_10550("NumGroups");
		this.field_19023 = class_3765.class_4259.method_20028(arg2.method_10558("Status"));
		this.field_19021.clear();
		if (arg2.method_10573("HeroesOfTheVillage", 9)) {
			class_2499 lv = arg2.method_10554("HeroesOfTheVillage", 10);

			for (int i = 0; i < lv.size(); i++) {
				class_2487 lv2 = lv.method_10602(i);
				UUID uUID = lv2.method_10584("UUID");
				this.field_19021.add(uUID);
			}
		}
	}

	public boolean method_16832() {
		return this.method_20023() || this.method_20024();
	}

	public boolean method_20020() {
		return this.method_20021() && this.method_16517() == 0 && this.field_16614 > 0;
	}

	public boolean method_20021() {
		return this.field_16621 > 0;
	}

	public boolean method_20022() {
		return this.field_19023 == class_3765.class_4259.field_19029;
	}

	public boolean method_20023() {
		return this.field_19023 == class_3765.class_4259.field_19027;
	}

	public boolean method_20024() {
		return this.field_19023 == class_3765.class_4259.field_19028;
	}

	public class_1937 method_16831() {
		return this.field_16619;
	}

	public boolean method_16524() {
		return this.field_16611;
	}

	public int method_16490() {
		return this.field_16621;
	}

	private Predicate<class_3222> method_16501() {
		return arg -> {
			class_2338 lv = new class_2338(arg);
			return arg.method_5805() && this.field_16619.method_19502(lv) == this;
		};
	}

	private void method_16499() {
		Collection<class_3222> collection = this.field_16607.method_14092();
		Set<class_3222> set = Sets.<class_3222>newHashSet(collection);

		for (class_3222 lv : collection) {
			class_2338 lv2 = new class_2338(lv);
			if (this.field_16619.method_19502(lv2) != this) {
				set.remove(lv);
				this.field_16607.method_14089(lv);
			}
		}

		Set<class_3222> set2 = Sets.<class_3222>newHashSet();

		for (class_3222 lv3 : this.field_16619.method_18766(this.method_16501())) {
			this.field_16607.method_14088(lv3);
			set2.add(lv3);
		}

		set.removeAll(set2);

		for (class_3222 lv3 : set) {
			this.field_16607.method_14089(lv3);
		}
	}

	public int method_16514() {
		return 5;
	}

	public int method_16493() {
		return this.field_16623;
	}

	public void method_16518(class_1657 arg) {
		if (arg.method_6059(class_1294.field_16595)) {
			this.field_16623 = this.field_16623 + arg.method_6112(class_1294.field_16595).method_5578() + 1;
			this.field_16623 = class_3532.method_15340(this.field_16623, 0, this.method_16514());
		}

		arg.method_6016(class_1294.field_16595);
	}

	public void method_16506() {
		this.field_16606 = false;
		this.field_16607.method_14094();
		this.field_19023 = class_3765.class_4259.field_19029;
	}

	public void method_16509() {
		if (!this.method_20022()) {
			if (this.field_19023 == class_3765.class_4259.field_19026) {
				boolean bl = this.field_16606;
				this.field_16606 = this.field_16619.method_8591(this.field_16613);
				if (this.field_16619.method_8407() == class_1267.field_5801) {
					this.method_16506();
					return;
				}

				if (bl != this.field_16606) {
					this.field_16607.method_14091(this.field_16606);
				}

				if (!this.field_16606) {
					return;
				}

				if (!this.field_16619.method_19500(this.field_16613)) {
					this.method_20511();
				}

				if (!this.field_16619.method_19500(this.field_16613)) {
					if (this.field_16621 > 0) {
						this.field_19023 = class_3765.class_4259.field_19028;
					} else {
						this.method_16506();
					}
				}

				this.field_16605++;
				if (this.field_16605 >= 48000L) {
					this.method_16506();
					return;
				}

				int i = this.method_16517();
				if (i == 0 && this.method_16833()) {
					if (this.field_16614 <= 0) {
						if (this.field_16614 == 0 && this.field_16621 > 0) {
							this.field_16614 = 300;
							this.field_16607.method_5413(field_19016);
							return;
						}
					} else {
						boolean bl2 = this.field_19172.isPresent();
						boolean bl3 = !bl2 && this.field_16614 % 5 == 0;
						if (bl2 && !this.field_16619.method_14178().method_20591(new class_1923((class_2338)this.field_19172.get()))) {
							bl3 = true;
						}

						if (bl3) {
							int j = 0;
							if (this.field_16614 < 100) {
								j = 1;
							} else if (this.field_16614 < 40) {
								j = 2;
							}

							this.field_19172 = this.method_20267(j);
						}

						if (this.field_16614 == 300 || this.field_16614 % 20 == 0) {
							this.method_16499();
						}

						this.field_16614--;
						this.field_16607.method_5408(class_3532.method_15363((float)(300 - this.field_16614) / 300.0F, 0.0F, 1.0F));
					}
				}

				if (this.field_16605 % 20L == 0L) {
					this.method_16499();
					this.method_16834();
					if (i > 0) {
						if (i <= 2) {
							this.field_16607.method_5413(field_19016.method_11020().method_10864(" - ").method_10852(new class_2588("event.minecraft.raid.raiders_remaining", i)));
						} else {
							this.field_16607.method_5413(field_19016);
						}
					} else {
						this.field_16607.method_5413(field_19016);
					}
				}

				boolean bl2x = false;
				int k = 0;

				while (this.method_16519()) {
					class_2338 lv = this.field_19172.isPresent() ? (class_2338)this.field_19172.get() : this.method_16525(k, 20);
					if (lv != null) {
						this.field_16611 = true;
						this.method_16522(lv);
						if (!bl2x) {
							this.method_16521(lv);
							bl2x = true;
						}
					} else {
						k++;
					}

					if (k > 3) {
						this.method_16506();
						break;
					}
				}

				if (this.method_16524() && !this.method_16833() && i == 0) {
					if (this.field_16616 < 40) {
						this.field_16616++;
					} else {
						this.field_19023 = class_3765.class_4259.field_19027;

						for (UUID uUID : this.field_19021) {
							class_1297 lv2 = this.field_16619.method_14190(uUID);
							if (lv2 instanceof class_1309 && !lv2.method_7325()) {
								class_1309 lv3 = (class_1309)lv2;
								lv3.method_6092(new class_1293(class_1294.field_18980, 48000, this.field_16623 - 1, false, false, true));
								if (lv3 instanceof class_3222) {
									class_3222 lv4 = (class_3222)lv3;
									lv4.method_7281(class_3468.field_19257);
									class_174.field_19250.method_9027(lv4);
								}
							}
						}
					}
				}

				this.method_16520();
			} else if (this.method_16832()) {
				this.field_19024++;
				if (this.field_19024 >= 600) {
					this.method_16506();
					return;
				}

				if (this.field_19024 % 20 == 0) {
					this.method_16499();
					this.field_16607.method_14091(true);
					if (this.method_20023()) {
						this.field_16607.method_5408(0.0F);
						this.field_16607.method_5413(field_19019);
					} else {
						this.field_16607.method_5413(field_19020);
					}
				}
			}
		}
	}

	private void method_20511() {
		Stream<class_4076> stream = class_4076.method_20439(class_4076.method_18682(this.field_16613), 2);
		stream.filter(this.field_16619::method_20588)
			.map(class_4076::method_19768)
			.min(Comparator.comparingDouble(arg -> arg.method_10262(this.field_16613)))
			.ifPresent(this::method_20509);
	}

	private Optional<class_2338> method_20267(int i) {
		for (int j = 0; j < 3; j++) {
			class_2338 lv = this.method_16525(i, 1);
			if (lv != null) {
				return Optional.of(lv);
			}
		}

		return Optional.empty();
	}

	private boolean method_16833() {
		return this.method_20013() ? !this.method_20014() : !this.method_20012();
	}

	private boolean method_20012() {
		return this.method_16490() == this.field_19022;
	}

	private boolean method_20013() {
		return this.field_16623 > 1;
	}

	private boolean method_20014() {
		return this.method_16490() > this.field_19022;
	}

	private boolean method_20015() {
		return this.method_20012() && this.method_16517() == 0 && this.method_20013();
	}

	private void method_16834() {
		Iterator<Set<class_3763>> iterator = this.field_16618.values().iterator();
		Set<class_3763> set = Sets.<class_3763>newHashSet();

		while (iterator.hasNext()) {
			Set<class_3763> set2 = (Set<class_3763>)iterator.next();

			for (class_3763 lv : set2) {
				class_2338 lv2 = new class_2338(lv);
				if (lv.field_5988 || lv.field_6026 != this.field_16619.method_8597().method_12460() || this.field_16613.method_10262(lv2) >= 12544.0) {
					set.add(lv);
				} else if (lv.field_6012 > 600) {
					if (this.field_16619.method_14190(lv.method_5667()) == null) {
						set.add(lv);
					}

					if (!this.field_16619.method_19500(lv2) && lv.method_6131() > 2400) {
						lv.method_16835(lv.method_16836() + 1);
					}

					if (lv.method_16836() >= 30) {
						set.add(lv);
					}
				}
			}
		}

		for (class_3763 lv3 : set) {
			this.method_16510(lv3, true);
		}
	}

	private void method_16521(class_2338 arg) {
		float f = 13.0F;
		int i = 64;

		for (class_1657 lv : this.field_16619.method_18456()) {
			class_243 lv2 = new class_243(lv.field_5987, lv.field_6010, lv.field_6035);
			class_243 lv3 = new class_243((double)arg.method_10263(), (double)arg.method_10264(), (double)arg.method_10260());
			float g = class_3532.method_15368(
				(lv3.field_1352 - lv2.field_1352) * (lv3.field_1352 - lv2.field_1352) + (lv3.field_1350 - lv2.field_1350) * (lv3.field_1350 - lv2.field_1350)
			);
			double d = lv2.field_1352 + (double)(13.0F / g) * (lv3.field_1352 - lv2.field_1352);
			double e = lv2.field_1350 + (double)(13.0F / g) * (lv3.field_1350 - lv2.field_1350);
			if (g <= 64.0F || this.field_16619.method_19500(new class_2338(lv))) {
				((class_3222)lv).field_13987.method_14364(new class_2767(class_3417.field_17266, class_3419.field_15254, d, lv.field_6010, e, 64.0F, 1.0F));
			}
		}
	}

	private void method_16522(class_2338 arg) {
		boolean bl = false;
		int i = this.field_16621 + 1;
		this.field_16620 = 0.0F;
		class_1266 lv = this.field_16619.method_8404(arg);
		boolean bl2 = this.method_20015();

		for (class_3765.class_3766 lv2 : class_3765.class_3766.field_16636) {
			int j = this.method_20018(lv2, i, bl2) + this.method_20019(lv2, this.field_16608, i, lv, bl2);
			int k = 0;

			for (int l = 0; l < j; l++) {
				class_3763 lv3 = lv2.field_16629.method_5883(this.field_16619);
				if (!bl && lv3.method_16485()) {
					lv3.method_16217(true);
					this.method_16491(i, lv3);
					bl = true;
				}

				this.method_16516(i, lv3, arg, false);
				if (lv2.field_16629 == class_1299.field_6134) {
					class_3763 lv4 = null;
					if (i == this.method_20016(class_1267.field_5802)) {
						lv4 = class_1299.field_6105.method_5883(this.field_16619);
					} else if (i >= this.method_20016(class_1267.field_5807)) {
						if (k == 0) {
							lv4 = class_1299.field_6090.method_5883(this.field_16619);
						} else {
							lv4 = class_1299.field_6117.method_5883(this.field_16619);
						}
					}

					k++;
					if (lv4 != null) {
						this.method_16516(i, lv4, arg, false);
						lv4.method_5725(arg, 0.0F, 0.0F);
						lv4.method_5804(lv3);
					}
				}
			}
		}

		this.field_19172 = Optional.empty();
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

	public void method_16523() {
		this.field_16607.method_5408(class_3532.method_15363(this.method_16513() / this.field_16620, 0.0F, 1.0F));
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
		return this.field_16614 == 0 && (this.field_16621 < this.field_19022 || this.method_20015()) && this.method_16517() == 0;
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

				arg.method_16476(null);
				this.method_16523();
				this.method_16520();
			}
		}
	}

	private void method_16520() {
		this.field_16619.method_19495().method_80();
	}

	public static class_1799 method_16515() {
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
		lv.method_7977(new class_2588("block.minecraft.ominous_banner").method_10854(class_124.field_1065));
		return lv;
	}

	@Nullable
	public class_3763 method_16496(int i) {
		return (class_3763)this.field_16615.get(i);
	}

	@Nullable
	private class_2338 method_16525(int i, int j) {
		int k = i == 0 ? 2 : 2 - i;
		class_2338.class_2339 lv = new class_2338.class_2339();

		for (int l = 0; l < j; l++) {
			float f = this.field_16619.field_9229.nextFloat() * (float) (Math.PI * 2);
			int m = this.field_16613.method_10263() + class_3532.method_15375(class_3532.method_15362(f) * 32.0F * (float)k) + this.field_16619.field_9229.nextInt(5);
			int n = this.field_16613.method_10260() + class_3532.method_15375(class_3532.method_15374(f) * 32.0F * (float)k) + this.field_16619.field_9229.nextInt(5);
			int o = this.field_16619.method_8589(class_2902.class_2903.field_13202, m, n);
			lv.method_10103(m, o, n);
			if ((!this.field_16619.method_19500(lv) || i >= 2)
				&& this.field_16619
					.method_8627(
						lv.method_10263() - 10, lv.method_10264() - 10, lv.method_10260() - 10, lv.method_10263() + 10, lv.method_10264() + 10, lv.method_10260() + 10
					)
				&& this.field_16619.method_14178().method_20591(new class_1923(lv))
				&& (
					class_1948.method_8660(class_1317.class_1319.field_6317, this.field_16619, lv, class_1299.field_6134)
						|| this.field_16619.method_8320(lv.method_10074()).method_11614() == class_2246.field_10477 && this.field_16619.method_8320(lv).method_11588()
				)) {
				return lv;
			}
		}

		return null;
	}

	private boolean method_16505(int i, class_3763 arg) {
		return this.method_16487(i, arg, true);
	}

	public boolean method_16487(int i, class_3763 arg, boolean bl) {
		this.field_16618.computeIfAbsent(i, integer -> Sets.newHashSet());
		Set<class_3763> set = (Set<class_3763>)this.field_16618.get(i);
		class_3763 lv = null;

		for (class_3763 lv2 : set) {
			if (lv2.method_5667().equals(arg.method_5667())) {
				lv = lv2;
				break;
			}
		}

		if (lv != null) {
			set.remove(lv);
			set.add(arg);
		}

		set.add(arg);
		if (bl) {
			this.field_16620 = this.field_16620 + arg.method_6032();
		}

		this.method_16523();
		this.method_16520();
		return true;
	}

	public void method_16491(int i, class_3763 arg) {
		this.field_16615.put(i, arg);
		arg.method_5673(class_1304.field_6169, method_16515());
		arg.method_5946(class_1304.field_6169, 2.0F);
	}

	public void method_16500(int i) {
		this.field_16615.remove(i);
	}

	public class_2338 method_16495() {
		return this.field_16613;
	}

	private void method_20509(class_2338 arg) {
		this.field_16613 = arg;
	}

	public int method_16494() {
		return this.field_16625;
	}

	private int method_20018(class_3765.class_3766 arg, int i, boolean bl) {
		return bl ? arg.field_16628[this.field_19022] : arg.field_16628[i];
	}

	private int method_20019(class_3765.class_3766 arg, Random random, int i, class_1266 arg2, boolean bl) {
		class_1267 lv = arg2.method_5454();
		boolean bl2 = lv == class_1267.field_5805;
		boolean bl3 = lv == class_1267.field_5802;
		int j;
		switch (arg) {
			case field_16635:
				if (bl2 || i <= 2 || i == 4) {
					return 0;
				}

				j = 1;
				break;
			case field_16633:
			case field_16631:
				if (bl2) {
					j = random.nextInt(2);
				} else if (bl3) {
					j = 1;
				} else {
					j = 2;
				}
				break;
			case field_16630:
				j = !bl2 && bl ? 1 : 0;
				break;
			default:
				return 0;
		}

		return j > 0 ? random.nextInt(j + 1) : 0;
	}

	public boolean method_16504() {
		return this.field_16606;
	}

	public class_2487 method_16502(class_2487 arg) {
		arg.method_10569("Id", this.field_16625);
		arg.method_10556("Started", this.field_16611);
		arg.method_10556("Active", this.field_16606);
		arg.method_10544("TicksActive", this.field_16605);
		arg.method_10569("BadOmenLevel", this.field_16623);
		arg.method_10569("GroupsSpawned", this.field_16621);
		arg.method_10569("PreRaidTicks", this.field_16614);
		arg.method_10569("PostRaidTicks", this.field_16616);
		arg.method_10548("TotalHealth", this.field_16620);
		arg.method_10569("NumGroups", this.field_19022);
		arg.method_10582("Status", this.field_19023.method_20026());
		arg.method_10569("CX", this.field_16613.method_10263());
		arg.method_10569("CY", this.field_16613.method_10264());
		arg.method_10569("CZ", this.field_16613.method_10260());
		class_2499 lv = new class_2499();

		for (UUID uUID : this.field_19021) {
			class_2487 lv2 = new class_2487();
			lv2.method_10560("UUID", uUID);
			lv.add(lv2);
		}

		arg.method_10566("HeroesOfTheVillage", lv);
		return arg;
	}

	public int method_20016(class_1267 arg) {
		switch (arg) {
			case field_5805:
				return 3;
			case field_5802:
				return 5;
			case field_5807:
				return 7;
			default:
				return 0;
		}
	}

	public float method_20025() {
		int i = this.method_16493();
		if (i == 2) {
			return 0.1F;
		} else if (i == 3) {
			return 0.25F;
		} else if (i == 4) {
			return 0.5F;
		} else {
			return i == 5 ? 0.75F : 0.0F;
		}
	}

	public void method_20017(class_1297 arg) {
		this.field_19021.add(arg.method_5667());
	}

	static enum class_3766 {
		field_16631(class_1299.field_6117, new int[]{0, 0, 2, 0, 1, 4, 2, 5}),
		field_16634(class_1299.field_6090, new int[]{0, 0, 0, 0, 0, 1, 1, 2}),
		field_16633(class_1299.field_6105, new int[]{0, 4, 3, 3, 4, 4, 4, 2}),
		field_16635(class_1299.field_6145, new int[]{0, 0, 0, 0, 3, 0, 0, 1}),
		field_16630(class_1299.field_6134, new int[]{0, 0, 0, 1, 0, 1, 0, 2});

		private static final class_3765.class_3766[] field_16636 = values();
		private final class_1299<? extends class_3763> field_16629;
		private final int[] field_16628;

		private class_3766(class_1299<? extends class_3763> arg, int[] is) {
			this.field_16629 = arg;
			this.field_16628 = is;
		}
	}

	static enum class_4259 {
		field_19026,
		field_19027,
		field_19028,
		field_19029;

		private static final class_3765.class_4259[] field_19030 = values();

		private static class_3765.class_4259 method_20028(String string) {
			for (class_3765.class_4259 lv : field_19030) {
				if (string.equalsIgnoreCase(lv.name())) {
					return lv;
				}
			}

			return field_19026;
		}

		public String method_20026() {
			return this.name().toLowerCase(Locale.ROOT);
		}
	}
}
