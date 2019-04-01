package net.minecraft;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.MinecraftServer;

public class class_1646 extends class_3988 implements class_4094, class_3851 {
	private static final class_2940<class_3850> field_7445 = class_2945.method_12791(class_1646.class, class_2943.field_17207);
	public static final Map<class_1792, Integer> field_18526 = ImmutableMap.of(
		class_1802.field_8229, 4, class_1802.field_8567, 1, class_1802.field_8179, 1, class_1802.field_8186, 1
	);
	private static final Set<class_1792> field_18527 = ImmutableSet.of(
		class_1802.field_8229,
		class_1802.field_8567,
		class_1802.field_8179,
		class_1802.field_8861,
		class_1802.field_8317,
		class_1802.field_8186,
		class_1802.field_8309
	);
	private int field_18528;
	private boolean field_18529;
	@Nullable
	private class_1657 field_18530;
	@Nullable
	private UUID field_18531;
	private long field_18532 = Long.MIN_VALUE;
	private byte field_18533;
	private final class_4136 field_18534 = new class_4136();
	private long field_18535;
	private int field_18536;
	private long field_18537 = 0L;
	private static final ImmutableList<class_4140<?>> field_18538 = ImmutableList.of(
		class_4140.field_18438,
		class_4140.field_18439,
		class_4140.field_18440,
		class_4140.field_18441,
		class_4140.field_18442,
		class_4140.field_19006,
		class_4140.field_18443,
		class_4140.field_18444,
		class_4140.field_18445,
		class_4140.field_18446,
		class_4140.field_18447,
		class_4140.field_18448,
		class_4140.field_18449,
		class_4140.field_18450,
		class_4140.field_19007,
		class_4140.field_18451,
		class_4140.field_18452,
		class_4140.field_18453,
		class_4140.field_18873,
		class_4140.field_18874,
		class_4140.field_19008,
		class_4140.field_19009
	);
	private static final ImmutableList<class_4149<? extends class_4148<? super class_1646>>> field_18539 = ImmutableList.of(
		class_4149.field_18466,
		class_4149.field_18467,
		class_4149.field_18468,
		class_4149.field_19010,
		class_4149.field_18469,
		class_4149.field_18470,
		class_4149.field_19011,
		class_4149.field_18875
	);
	public static final Map<class_4140<class_4208>, BiPredicate<class_1646, class_4158>> field_18851 = ImmutableMap.of(
		class_4140.field_18438,
		(arg, arg2) -> arg2 == class_4158.field_18517,
		class_4140.field_18439,
		(arg, arg2) -> arg.method_7231().method_16924().method_19198() == arg2,
		class_4140.field_18440,
		(arg, arg2) -> arg2 == class_4158.field_18518
	);

	public class_1646(class_1299<? extends class_1646> arg, class_1937 arg2) {
		this(arg, arg2, class_3854.field_17073);
	}

	public class_1646(class_1299<? extends class_1646> arg, class_1937 arg2, class_3854 arg3) {
		super(arg, arg2);
		((class_1409)this.method_5942()).method_6363(true);
		this.method_5952(true);
		this.method_7221(this.method_7231().method_16922(arg3).method_16921(class_3852.field_17051));
		this.field_18321 = this.method_18867(new Dynamic<>(class_2509.field_11560, new class_2487()));
	}

	@Override
	public class_4095<class_1646> method_18868() {
		return (class_4095<class_1646>)super.method_18868();
	}

	@Override
	protected class_4095<?> method_18867(Dynamic<?> dynamic) {
		class_4095<class_1646> lv = new class_4095<>(field_18538, field_18539, dynamic);
		this.method_19174(lv);
		return lv;
	}

	public void method_19179(class_3218 arg) {
		class_4095<class_1646> lv = this.method_18868();
		lv.method_18900(arg, this);
		this.field_18321 = lv.method_18911();
		this.method_19174(this.method_18868());
	}

	private void method_19174(class_4095<class_1646> arg) {
		class_3852 lv = this.method_7231().method_16924();
		float f = (float)this.method_5996(class_1612.field_7357).method_6194();
		if (this.method_6109()) {
			arg.method_18884(class_4170.field_18605);
			arg.method_18881(class_4168.field_18885, class_4129.method_19990(f));
		} else {
			arg.method_18884(class_4170.field_18606);
			arg.method_18882(class_4168.field_18596, class_4129.method_19021(lv, f), ImmutableSet.of(Pair.of(class_4140.field_18439, class_4141.field_18456)));
		}

		arg.method_18881(class_4168.field_18594, class_4129.method_19020(lv, f));
		arg.method_18882(class_4168.field_18598, class_4129.method_19023(lv, f), ImmutableSet.of(Pair.of(class_4140.field_18440, class_4141.field_18456)));
		arg.method_18882(class_4168.field_18597, class_4129.method_19022(lv, f), ImmutableSet.of(Pair.of(class_4140.field_18438, class_4141.field_18456)));
		arg.method_18881(class_4168.field_18595, class_4129.method_19024(lv, f));
		arg.method_18881(class_4168.field_18599, class_4129.method_19025(lv, f));
		arg.method_18881(class_4168.field_19042, class_4129.method_19991(lv, f));
		arg.method_18881(class_4168.field_19041, class_4129.method_19992(lv, f));
		arg.method_18881(class_4168.field_19043, class_4129.method_19993(lv, f));
		arg.method_18890(ImmutableSet.of(class_4168.field_18594));
		arg.method_18897(class_4168.field_18595);
		arg.method_18871(this.field_6002.method_8532(), this.field_6002.method_8510());
	}

	@Override
	protected void method_5619() {
		super.method_5619();
		if (this.field_6002 instanceof class_3218) {
			this.method_19179((class_3218)this.field_6002);
		}
	}

	@Override
	protected void method_6001() {
		super.method_6001();
		this.method_5996(class_1612.field_7357).method_6192(0.5);
		this.method_5996(class_1612.field_7365).method_6192(48.0);
	}

	@Override
	protected void method_5958() {
		this.field_6002.method_16107().method_15396("brain");
		this.method_18868().method_19542((class_3218)this.field_6002, this);
		this.field_6002.method_16107().method_15407();
		if (!this.method_18009() && this.field_18528 > 0) {
			this.field_18528--;
			if (this.field_18528 <= 0) {
				if (this.field_18529) {
					this.method_16918();
					this.field_18529 = false;
				}

				this.method_6092(new class_1293(class_1294.field_5924, 200, 0));
			}
		}

		if (this.field_18530 != null && this.field_6002 instanceof class_3218) {
			((class_3218)this.field_6002).method_19496(class_4151.field_18478, this.field_18530, this);
			this.field_18530 = null;
		}

		if (!this.method_5987() && this.field_5974.nextInt(100) == 0) {
			class_3765 lv = ((class_3218)this.field_6002).method_19502(new class_2338(this));
			if (lv != null && lv.method_16504() && !lv.method_16832()) {
				this.field_6002.method_8421(this, (byte)42);
			}
		}

		super.method_5958();
	}

	public void method_19181() {
		this.method_8259(null);
		this.method_19187();
	}

	@Override
	public boolean method_5992(class_1657 arg, class_1268 arg2) {
		class_1799 lv = arg.method_5998(arg2);
		boolean bl = lv.method_7909() == class_1802.field_8448;
		if (bl) {
			lv.method_7920(arg, this, arg2);
			return true;
		} else if (lv.method_7909() != class_1802.field_8086 && this.method_5805() && !this.method_18009() && !this.method_6109()) {
			if (arg2 == class_1268.field_5808) {
				arg.method_7281(class_3468.field_15384);
			}

			if (this.method_8264().isEmpty()) {
				return super.method_5992(arg, arg2);
			} else {
				if (!this.field_6002.field_9236 && !this.field_17721.isEmpty()) {
					this.method_19191(arg);
				}

				return true;
			}
		} else {
			return super.method_5992(arg, arg2);
		}
	}

	private void method_19191(class_1657 arg) {
		this.method_19192(arg);
		this.method_8259(arg);
		this.method_17449(arg, this.method_5476(), this.method_7231().method_16925());
	}

	public void method_19182() {
		for (class_1914 lv : this.method_8264()) {
			lv.method_19274();
			lv.method_19275();
		}

		this.field_18537 = this.field_6002.method_8532() % 24000L;
	}

	private void method_19192(class_1657 arg) {
		int i = this.field_18534.method_19073(arg.method_5667(), argx -> argx != class_4139.field_18429);
		if (i != 0) {
			for (class_1914 lv : this.method_8264()) {
				lv.method_8245(-class_3532.method_15375((float)i * lv.method_19278()));
			}
		}

		if (arg.method_6059(class_1294.field_18980)) {
			class_1293 lv2 = arg.method_6112(class_1294.field_18980);
			int j = lv2.method_5578();

			for (class_1914 lv3 : this.method_8264()) {
				double d = 0.3 + 0.0625 * (double)j;
				int k = (int)Math.floor(d * (double)lv3.method_8246().method_7947());
				lv3.method_8245(-Math.max(k, 1));
			}
		}
	}

	private void method_19187() {
		for (class_1914 lv : this.method_8264()) {
			lv.method_19276();
		}
	}

	@Override
	protected void method_5693() {
		super.method_5693();
		this.field_6011.method_12784(field_7445, new class_3850(class_3854.field_17073, class_3852.field_17051, 1));
	}

	@Override
	public void method_5652(class_2487 arg) {
		super.method_5652(arg);
		arg.method_10566("VillagerData", this.method_7231().method_16923(class_2509.field_11560));
		arg.method_10567("FoodLevel", this.field_18533);
		arg.method_10566("Gossips", this.field_18534.method_19067(class_2509.field_11560).getValue());
		arg.method_10569("Xp", this.field_18536);
		arg.method_10544("LastRestock", this.field_18537);
		if (this.field_18531 != null) {
			arg.method_10560("BuddyGolem", this.field_18531);
		}
	}

	@Override
	public void method_5749(class_2487 arg) {
		super.method_5749(arg);
		if (arg.method_10573("VillagerData", 10)) {
			this.method_7221(new class_3850(new Dynamic<>(class_2509.field_11560, arg.method_10580("VillagerData"))));
		}

		if (arg.method_10573("Offers", 10)) {
			this.field_17721 = new class_1916(arg.method_10562("Offers"));
		}

		if (arg.method_10573("FoodLevel", 1)) {
			this.field_18533 = arg.method_10571("FoodLevel");
		}

		class_2499 lv = arg.method_10554("Gossips", 10);
		this.field_18534.method_19066(new Dynamic<>(class_2509.field_11560, lv));
		if (arg.method_10573("Xp", 3)) {
			this.field_18536 = arg.method_10550("Xp");
		} else {
			int i = this.method_7231().method_16925();
			if (class_3850.method_19196(i)) {
				this.field_18536 = class_3850.method_19194(i);
			}
		}

		this.field_18537 = arg.method_10537("LastRestock");
		if (arg.method_10576("BuddyGolem")) {
			this.field_18531 = arg.method_10584("BuddyGolem");
		}

		this.method_5952(true);
	}

	@Override
	public boolean method_5974(double d) {
		return false;
	}

	@Nullable
	@Override
	protected class_3414 method_5994() {
		if (this.method_6113()) {
			return null;
		} else {
			return this.method_18009() ? class_3417.field_14933 : class_3417.field_15175;
		}
	}

	@Override
	protected class_3414 method_6011(class_1282 arg) {
		return class_3417.field_15139;
	}

	@Override
	protected class_3414 method_6002() {
		return class_3417.field_15225;
	}

	public void method_19183() {
		class_3414 lv = this.method_7231().method_16924().method_19198().method_19166();
		if (lv != null) {
			this.method_5783(lv, this.method_6107(), this.method_6017());
		}
	}

	public void method_7221(class_3850 arg) {
		class_3850 lv = this.method_7231();
		if (lv.method_16924() != arg.method_16924()) {
			this.field_17721 = null;
		}

		this.field_6011.method_12778(field_7445, arg);
	}

	@Override
	public class_3850 method_7231() {
		return this.field_6011.method_12789(field_7445);
	}

	@Override
	protected void method_18008(class_1914 arg) {
		int i = 3 + this.field_5974.nextInt(4);
		this.field_18536 = this.field_18536 + arg.method_19279();
		this.field_18530 = this.method_8257();
		if (this.method_19188()) {
			this.field_18528 = 40;
			this.field_18529 = true;
			i += 5;
		}

		if (arg.method_8256()) {
			this.field_6002.method_8649(new class_1303(this.field_6002, this.field_5987, this.field_6010 + 0.5, this.field_6035, i));
		}
	}

	@Override
	public void method_6015(@Nullable class_1309 arg) {
		if (arg != null && this.field_6002 instanceof class_3218) {
			((class_3218)this.field_6002).method_19496(class_4151.field_18476, arg, this);
		}

		super.method_6015(arg);
	}

	@Override
	public void method_6078(class_1282 arg) {
		this.method_19176(class_4140.field_18438);
		this.method_19176(class_4140.field_18439);
		this.method_19176(class_4140.field_18440);
		super.method_6078(arg);
	}

	public void method_19176(class_4140<class_4208> arg) {
		if (this.field_6002 instanceof class_3218) {
			MinecraftServer minecraftServer = ((class_3218)this.field_6002).method_8503();
			this.field_18321.method_19543(arg).ifPresent(arg2 -> {
				class_3218 lv = minecraftServer.method_3847(arg2.method_19442());
				class_4153 lv2 = lv.method_19494();
				Optional<class_4158> optional = lv2.method_19132(arg2.method_19446());
				BiPredicate<class_1646, class_4158> biPredicate = (BiPredicate<class_1646, class_4158>)field_18851.get(arg);
				if (optional.isPresent() && biPredicate.test(this, optional.get())) {
					lv2.method_19129(arg2.method_19446());
					class_4209.method_19778(lv, arg2.method_19446());
				}
			});
		}
	}

	public boolean method_19184() {
		return this.field_18533 + this.method_19189() >= 12 && this.method_5618() == 0;
	}

	public void method_19185() {
		if (this.field_18533 < 12 && this.method_19189() != 0) {
			for (int i = 0; i < this.method_18011().method_5439(); i++) {
				class_1799 lv = this.method_18011().method_5438(i);
				if (!lv.method_7960()) {
					Integer integer = (Integer)field_18526.get(lv.method_7909());
					if (integer != null) {
						int j = lv.method_7947();

						for (int k = j; k > 0; k--) {
							this.field_18533 = (byte)(this.field_18533 + integer);
							this.method_18011().method_5434(i, 1);
							if (this.field_18533 >= 12) {
								return;
							}
						}
					}
				}
			}
		}
	}

	public void method_19193(int i) {
		this.field_18533 = (byte)(this.field_18533 - i);
	}

	public void method_16917(class_1916 arg) {
		this.field_17721 = arg;
	}

	private boolean method_19188() {
		int i = this.method_7231().method_16925();
		return class_3850.method_19196(i) && this.field_18536 >= class_3850.method_19195(i);
	}

	private void method_16918() {
		this.method_7221(this.method_7231().method_16920(this.method_7231().method_16925() + 1));
		this.method_7237();
	}

	@Override
	public class_2561 method_5476() {
		class_270 lv = this.method_5781();
		class_2561 lv2 = this.method_5797();
		if (lv2 != null) {
			return class_268.method_1142(lv, lv2).method_10859(arg -> arg.method_10949(this.method_5769()).method_10975(this.method_5845()));
		} else {
			class_3852 lv3 = this.method_7231().method_16924();
			class_2561 lv4 = new class_2588(this.method_5864().method_5882() + '.' + class_2378.field_17167.method_10221(lv3).method_12832())
				.method_10859(arg -> arg.method_10949(this.method_5769()).method_10975(this.method_5845()));
			if (lv != null) {
				lv4.method_10854(lv.method_1202());
			}

			return lv4;
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5711(byte b) {
		if (b == 12) {
			this.method_18007(class_2398.field_11201);
		} else if (b == 13) {
			this.method_18007(class_2398.field_11231);
		} else if (b == 14) {
			this.method_18007(class_2398.field_11211);
		} else if (b == 42) {
			this.method_18007(class_2398.field_11202);
		} else {
			super.method_5711(b);
		}
	}

	@Nullable
	@Override
	public class_1315 method_5943(class_1936 arg, class_1266 arg2, class_3730 arg3, @Nullable class_1315 arg4, @Nullable class_2487 arg5) {
		if (arg3 == class_3730.field_16466) {
			this.method_7221(this.method_7231().method_16921(class_3852.field_17051));
		}

		if (arg3 == class_3730.field_16462 || arg3 == class_3730.field_16465 || arg3 == class_3730.field_16469) {
			this.method_7221(this.method_7231().method_16922(class_3854.method_16930(arg.method_8310(new class_2338(this)))));
		}

		return super.method_5943(arg, arg2, arg3, arg4, arg5);
	}

	public class_1646 method_7225(class_1296 arg) {
		double d = this.field_5974.nextDouble();
		class_3854 lv;
		if (d < 0.5) {
			lv = class_3854.method_16930(this.field_6002.method_8310(new class_2338(this)));
		} else if (d < 0.75) {
			lv = this.method_7231().method_16919();
		} else {
			lv = ((class_1646)arg).method_7231().method_16919();
		}

		class_1646 lv2 = new class_1646(class_1299.field_6077, this.field_6002, lv);
		lv2.method_5943(this.field_6002, this.field_6002.method_8404(new class_2338(lv2)), class_3730.field_16466, null, null);
		return lv2;
	}

	@Override
	public void method_5800(class_1538 arg) {
		class_1640 lv = class_1299.field_6145.method_5883(this.field_6002);
		lv.method_5808(this.field_5987, this.field_6010, this.field_6035, this.field_6031, this.field_5965);
		lv.method_5943(this.field_6002, this.field_6002.method_8404(new class_2338(lv)), class_3730.field_16468, null, null);
		lv.method_5977(this.method_5987());
		if (this.method_16914()) {
			lv.method_5665(this.method_5797());
			lv.method_5880(this.method_5807());
		}

		this.field_6002.method_8649(lv);
		this.method_5650();
	}

	@Override
	protected void method_5949(class_1542 arg) {
		class_1799 lv = arg.method_6983();
		class_1792 lv2 = lv.method_7909();
		class_3852 lv3 = this.method_7231().method_16924();
		if (field_18527.contains(lv2) || lv3.method_19199().contains(lv2)) {
			if (lv3 == class_3852.field_17056 && lv2 == class_1802.field_8861) {
				int i = lv.method_7947() / 3;
				if (i > 0) {
					class_1799 lv4 = this.method_18011().method_5491(new class_1799(class_1802.field_8229, i));
					lv.method_7934(i * 3);
					if (!lv4.method_7960()) {
						this.method_5699(lv4, 0.5F);
					}
				}
			}

			class_1799 lv5 = this.method_18011().method_5491(lv);
			if (lv5.method_7960()) {
				arg.method_5650();
			} else {
				lv.method_7939(lv5.method_7947());
			}
		}
	}

	public boolean method_7234() {
		return this.method_19189() >= 24;
	}

	public boolean method_7239() {
		boolean bl = this.method_7231().method_16924() == class_3852.field_17056;
		int i = this.method_19189();
		return bl ? i < 60 : i < 12;
	}

	private int method_19189() {
		class_1277 lv = this.method_18011();
		return field_18526.entrySet().stream().mapToInt(entry -> lv.method_18861((class_1792)entry.getKey()) * (Integer)entry.getValue()).sum();
	}

	public boolean method_19623() {
		class_1277 lv = this.method_18011();
		return lv.method_18862(ImmutableSet.of(class_1802.field_8317, class_1802.field_8567, class_1802.field_8179, class_1802.field_8309));
	}

	@Override
	protected void method_7237() {
		class_3850 lv = this.method_7231();
		Int2ObjectMap<class_3853.class_1652[]> int2ObjectMap = (Int2ObjectMap<class_3853.class_1652[]>)class_3853.field_17067.get(lv.method_16924());
		if (int2ObjectMap != null && !int2ObjectMap.isEmpty()) {
			class_3853.class_1652[] lvs = int2ObjectMap.get(lv.method_16925());
			if (lvs != null) {
				class_1916 lv2 = this.method_8264();
				this.method_19170(lv2, lvs, 2);
			}
		}
	}

	public void method_19177(class_1646 arg, long l) {
		if ((l < this.field_18535 || l >= this.field_18535 + 1200L) && (l < arg.field_18535 || l >= arg.field_18535 + 1200L)) {
			boolean bl = this.method_19171(l);
			if (this.method_19173(this) || bl) {
				this.field_18534.method_19072(this.method_5667(), class_4139.field_18429, class_4139.field_18429.field_18432);
			}

			this.field_18534.method_19061(arg.field_18534, this.field_5974, 10);
			this.field_18535 = l;
			arg.field_18535 = l;
			if (bl) {
				this.method_19624();
			}
		}
	}

	private void method_19624() {
		class_3850 lv = this.method_7231();
		if (lv.method_16924() != class_3852.field_17051 && lv.method_16924() != class_3852.field_17062) {
			Optional<class_1646.class_4222> optional = this.method_18868().method_19543(class_4140.field_18874);
			if (optional.isPresent()) {
				if (((class_1646.class_4222)optional.get()).method_19629(this.field_6002.method_8510())) {
					class_1309 lv2 = this.method_6065();
					boolean bl = lv2 instanceof class_1642 && this.field_6012 - this.method_6117() <= 1200;
					boolean bl2 = this.field_18534.method_19062(class_4139.field_18429, d -> d > 30.0) >= 5L;
					if (bl || bl2) {
						class_238 lv3 = this.method_5829().method_1012(80.0, 80.0, 80.0);
						List<class_1646> list = (List<class_1646>)this.field_6002
							.method_8390(class_1646.class, lv3, this::method_19173)
							.stream()
							.limit(5L)
							.collect(Collectors.toList());
						boolean bl3 = list.size() >= 5;
						if (bl || bl3) {
							class_1439 lv4 = this.method_19190();
							if (lv4 != null) {
								UUID uUID = lv4.method_5667();

								for (class_1646 lv5 : list) {
									for (class_1646 lv6 : list) {
										lv5.field_18534.method_19072(lv6.method_5667(), class_4139.field_18429, -class_4139.field_18429.field_18432);
									}

									lv5.field_18531 = uUID;
								}
							}
						}
					}
				}
			}
		}
	}

	private boolean method_19173(class_1297 arg) {
		return this.field_18534.method_19073(arg.method_5667(), argx -> argx == class_4139.field_18429) > 30;
	}

	private boolean method_19171(long l) {
		if (this.field_18531 == null) {
			return true;
		} else {
			if (this.field_18532 < l + 1200L) {
				this.field_18532 = l + 1200L;
				class_1297 lv = ((class_3218)this.field_6002).method_14190(this.field_18531);
				if (lv == null || !lv.method_5805() || this.method_5858(lv) > 80.0) {
					this.field_18531 = null;
					return true;
				}
			}

			return false;
		}
	}

	@Nullable
	private class_1439 method_19190() {
		class_2338 lv = new class_2338(this);

		for (int i = 0; i < 10; i++) {
			class_2338 lv2 = lv.method_10069(
				this.field_6002.field_9229.nextInt(16) - 8, this.field_6002.field_9229.nextInt(6) - 3, this.field_6002.field_9229.nextInt(16) - 8
			);
			class_1439 lv3 = class_1299.field_6147.method_5888(this.field_6002, null, null, null, lv2, class_3730.field_16471, false, false);
			if (lv3 != null) {
				if (lv3.method_5979(this.field_6002, class_3730.field_16471) && lv3.method_5957(this.field_6002)) {
					this.field_6002.method_8649(lv3);
					return lv3;
				}

				lv3.method_5650();
			}
		}

		return null;
	}

	@Override
	public void method_18870(class_4151 arg, class_1297 arg2) {
		if (arg == class_4151.field_18474) {
			this.field_18534.method_19072(arg2.method_5667(), class_4139.field_18427, 25);
		} else if (arg == class_4151.field_18478) {
			this.field_18534.method_19072(arg2.method_5667(), class_4139.field_18428, 2);
		} else if (arg == class_4151.field_18476) {
			this.field_18534.method_19072(arg2.method_5667(), class_4139.field_18425, 25);
		} else if (arg == class_4151.field_18477) {
			this.field_18534.method_19072(arg2.method_5667(), class_4139.field_18424, 25);
		}
	}

	@Override
	public int method_19269() {
		return this.field_18536;
	}

	public void method_19625(int i) {
		this.field_18536 = i;
	}

	public long method_19186() {
		return this.field_18537;
	}

	@Override
	protected void method_18409() {
		super.method_18409();
		class_4209.method_19774(this);
	}

	@Override
	public void method_18403(class_2338 arg) {
		super.method_18403(arg);
		class_1646.class_4222 lv = (class_1646.class_4222)this.method_18868().method_19543(class_4140.field_18874).orElseGet(class_1646.class_4222::new);
		lv.method_19628(this.field_6002.method_8510());
		this.field_18321.method_18878(class_4140.field_18874, lv);
	}

	public static final class class_4222 {
		private long field_18878;
		private long field_18879;

		public void method_19626(long l) {
			this.field_18878 = l;
		}

		public void method_19628(long l) {
			this.field_18879 = l;
		}

		private boolean method_19629(long l) {
			return l - this.field_18879 < 24000L && l - this.field_18878 < 36000L;
		}
	}
}
