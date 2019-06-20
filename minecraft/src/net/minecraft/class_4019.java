package net.minecraft;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_4019 extends class_1429 {
	private static final class_2940<Integer> field_17949 = class_2945.method_12791(class_4019.class, class_2943.field_13327);
	private static final class_2940<Byte> field_17950 = class_2945.method_12791(class_4019.class, class_2943.field_13319);
	private static final class_2940<Optional<UUID>> field_17951 = class_2945.method_12791(class_4019.class, class_2943.field_13313);
	private static final class_2940<Optional<UUID>> field_17952 = class_2945.method_12791(class_4019.class, class_2943.field_13313);
	private static final Predicate<class_1542> field_17953 = arg -> !arg.method_6977() && arg.method_5805();
	private static final Predicate<class_1297> field_17954 = arg -> {
		if (!(arg instanceof class_1309)) {
			return false;
		} else {
			class_1309 lv = (class_1309)arg;
			return lv.method_6052() != null && lv.method_6083() < lv.field_6012 + 600;
		}
	};
	private static final Predicate<class_1297> field_17955 = arg -> arg instanceof class_1428 || arg instanceof class_1463;
	private static final Predicate<class_1297> field_17956 = arg -> !arg.method_5715() && class_1301.field_6156.test(arg);
	private class_1352 field_17957;
	private class_1352 field_17958;
	private class_1352 field_17959;
	private float field_17960;
	private float field_17961;
	private float field_17962;
	private float field_17963;
	private int field_17964;

	public class_4019(class_1299<? extends class_4019> arg, class_1937 arg2) {
		super(arg, arg2);
		this.field_6206 = new class_4019.class_4028();
		this.field_6207 = new class_4019.class_4030();
		this.method_5941(class_7.field_5, 0.0F);
		this.method_5941(class_7.field_17, 0.0F);
		this.method_5952(true);
	}

	@Override
	protected void method_5693() {
		super.method_5693();
		this.field_6011.method_12784(field_17951, Optional.empty());
		this.field_6011.method_12784(field_17952, Optional.empty());
		this.field_6011.method_12784(field_17949, 0);
		this.field_6011.method_12784(field_17950, (byte)0);
	}

	@Override
	protected void method_5959() {
		this.field_17957 = new class_1400(this, class_1429.class, 10, false, false, arg -> arg instanceof class_1428 || arg instanceof class_1463);
		this.field_17958 = new class_1400(this, class_1481.class, 10, false, false, class_1481.field_6921);
		this.field_17959 = new class_1400(this, class_1422.class, 20, false, false, arg -> arg instanceof class_1425);
		this.field_6201.method_6277(0, new class_4019.class_4026());
		this.field_6201.method_6277(1, new class_4019.class_4021());
		this.field_6201.method_6277(2, new class_4019.class_4032(2.2));
		this.field_6201
			.method_6277(
				3, new class_1338(this, class_1657.class, 16.0F, 1.6, 1.4, arg -> field_17956.test(arg) && !this.method_18428(arg.method_5667()) && !this.method_18282())
			);
		this.field_6201.method_6277(3, new class_1338(this, class_1493.class, 8.0F, 1.6, 1.4, arg -> !((class_1493)arg).method_6181() && !this.method_18282()));
		this.field_6201.method_6277(4, new class_4019.class_4038());
		this.field_6201.method_6277(5, new class_4019.class_4033());
		this.field_6201.method_6277(5, new class_4019.class_4024(1.0));
		this.field_6201.method_6277(5, new class_4019.class_4036(1.25));
		this.field_6201.method_6277(6, new class_4019.class_4029(1.2F, true));
		this.field_6201.method_6277(6, new class_4019.class_4037());
		this.field_6201.method_6277(7, new class_4019.class_4052(this, 1.25));
		this.field_6201.method_6277(8, new class_4019.class_4031(32, 200));
		this.field_6201.method_6277(9, new class_4019.class_4025(1.2F, 12, 2));
		this.field_6201.method_6277(9, new class_1359(this, 0.4F));
		this.field_6201.method_6277(10, new class_1394(this, 1.0));
		this.field_6201.method_6277(10, new class_4019.class_4034());
		this.field_6201.method_6277(11, new class_4019.class_4292(this, class_1657.class, 24.0F));
		this.field_6201.method_6277(12, new class_4019.class_4035());
		this.field_6185
			.method_6277(3, new class_4019.class_4020(class_1309.class, false, false, arg -> field_17954.test(arg) && !this.method_18428(arg.method_5667())));
	}

	@Override
	public class_3414 method_18869(class_1799 arg) {
		return class_3417.field_18060;
	}

	@Override
	public void method_6007() {
		if (!this.field_6002.field_9236 && this.method_5805() && this.method_6034()) {
			this.field_17964++;
			class_1799 lv = this.method_6118(class_1304.field_6173);
			if (this.method_18430(lv)) {
				if (this.field_17964 > 600) {
					class_1799 lv2 = lv.method_7910(this.field_6002, this);
					if (!lv2.method_7960()) {
						this.method_5673(class_1304.field_6173, lv2);
					}

					this.field_17964 = 0;
				} else if (this.field_17964 > 560 && this.field_5974.nextFloat() < 0.1F) {
					this.method_5783(this.method_18869(lv), 1.0F, 1.0F);
					this.field_6002.method_8421(this, (byte)45);
				}
			}

			class_1309 lv3 = this.method_5968();
			if (lv3 == null || !lv3.method_5805()) {
				this.method_18297(false);
				this.method_18299(false);
			}
		}

		if (this.method_6113() || this.method_6062()) {
			this.field_6282 = false;
			this.field_6212 = 0.0F;
			this.field_6250 = 0.0F;
			this.field_6267 = 0.0F;
		}

		super.method_6007();
		if (this.method_18282() && this.field_5974.nextFloat() < 0.05F) {
			this.method_5783(class_3417.field_18055, 1.0F, 1.0F);
		}
	}

	@Override
	protected boolean method_6062() {
		return this.method_6032() <= 0.0F;
	}

	private boolean method_18430(class_1799 arg) {
		return arg.method_7909().method_19263() && this.method_5968() == null && this.field_5952 && !this.method_6113();
	}

	@Override
	protected void method_5964(class_1266 arg) {
		if (this.field_5974.nextFloat() < 0.2F) {
			float f = this.field_5974.nextFloat();
			class_1799 lv;
			if (f < 0.05F) {
				lv = new class_1799(class_1802.field_8687);
			} else if (f < 0.2F) {
				lv = new class_1799(class_1802.field_8803);
			} else if (f < 0.4F) {
				lv = this.field_5974.nextBoolean() ? new class_1799(class_1802.field_8073) : new class_1799(class_1802.field_8245);
			} else if (f < 0.6F) {
				lv = new class_1799(class_1802.field_8861);
			} else if (f < 0.8F) {
				lv = new class_1799(class_1802.field_8745);
			} else {
				lv = new class_1799(class_1802.field_8153);
			}

			this.method_5673(class_1304.field_6173, lv);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5711(byte b) {
		if (b == 45) {
			class_1799 lv = this.method_6118(class_1304.field_6173);
			if (!lv.method_7960()) {
				for (int i = 0; i < 8; i++) {
					class_243 lv2 = new class_243(((double)this.field_5974.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0)
						.method_1037(-this.field_5965 * (float) (Math.PI / 180.0))
						.method_1024(-this.field_6031 * (float) (Math.PI / 180.0));
					this.field_6002
						.method_8406(
							new class_2392(class_2398.field_11218, lv),
							this.field_5987 + this.method_5720().field_1352 / 2.0,
							this.field_6010,
							this.field_6035 + this.method_5720().field_1350 / 2.0,
							lv2.field_1352,
							lv2.field_1351 + 0.05,
							lv2.field_1350
						);
				}
			}
		} else {
			super.method_5711(b);
		}
	}

	@Override
	protected void method_6001() {
		super.method_6001();
		this.method_5996(class_1612.field_7357).method_6192(0.3F);
		this.method_5996(class_1612.field_7359).method_6192(10.0);
		this.method_5996(class_1612.field_7365).method_6192(32.0);
		this.method_6127().method_6208(class_1612.field_7363).method_6192(2.0);
	}

	public class_4019 method_18260(class_1296 arg) {
		class_4019 lv = class_1299.field_17943.method_5883(this.field_6002);
		lv.method_18255(this.field_5974.nextBoolean() ? this.method_18271() : ((class_4019)arg).method_18271());
		return lv;
	}

	@Nullable
	@Override
	public class_1315 method_5943(class_1936 arg, class_1266 arg2, class_3730 arg3, @Nullable class_1315 arg4, @Nullable class_2487 arg5) {
		class_1959 lv = arg.method_8310(new class_2338(this));
		class_4019.class_4039 lv2 = class_4019.class_4039.method_18313(lv);
		boolean bl = false;
		if (arg4 instanceof class_4019.class_4027) {
			lv2 = ((class_4019.class_4027)arg4).field_17977;
			if (((class_4019.class_4027)arg4).field_17978 >= 2) {
				bl = true;
			} else {
				((class_4019.class_4027)arg4).field_17978++;
			}
		} else {
			arg4 = new class_4019.class_4027(lv2);
			((class_4019.class_4027)arg4).field_17978++;
		}

		this.method_18255(lv2);
		if (bl) {
			this.method_5614(-24000);
		}

		this.method_18280();
		this.method_5964(arg2);
		return super.method_5943(arg, arg2, arg3, arg4, arg5);
	}

	private void method_18280() {
		if (this.method_18271() == class_4019.class_4039.field_17996) {
			this.field_6185.method_6277(4, this.field_17957);
			this.field_6185.method_6277(4, this.field_17958);
			this.field_6185.method_6277(6, this.field_17959);
		} else {
			this.field_6185.method_6277(4, this.field_17959);
			this.field_6185.method_6277(6, this.field_17957);
			this.field_6185.method_6277(6, this.field_17958);
		}
	}

	@Override
	protected void method_6475(class_1657 arg, class_1799 arg2) {
		if (this.method_6481(arg2)) {
			this.method_5783(this.method_18869(arg2), 1.0F, 1.0F);
		}

		super.method_6475(arg, arg2);
	}

	@Override
	protected float method_18394(class_4050 arg, class_4048 arg2) {
		return this.method_6109() ? arg2.field_18068 * 0.85F : 0.4F;
	}

	public class_4019.class_4039 method_18271() {
		return class_4019.class_4039.method_18311(this.field_6011.method_12789(field_17949));
	}

	private void method_18255(class_4019.class_4039 arg) {
		this.field_6011.method_12778(field_17949, arg.method_18317());
	}

	private List<UUID> method_18281() {
		List<UUID> list = Lists.<UUID>newArrayList();
		list.add(this.field_6011.method_12789(field_17951).orElse(null));
		list.add(this.field_6011.method_12789(field_17952).orElse(null));
		return list;
	}

	private void method_18266(@Nullable UUID uUID) {
		if (this.field_6011.method_12789(field_17951).isPresent()) {
			this.field_6011.method_12778(field_17952, Optional.ofNullable(uUID));
		} else {
			this.field_6011.method_12778(field_17951, Optional.ofNullable(uUID));
		}
	}

	@Override
	public void method_5652(class_2487 arg) {
		super.method_5652(arg);
		List<UUID> list = this.method_18281();
		class_2499 lv = new class_2499();

		for (UUID uUID : list) {
			if (uUID != null) {
				lv.add(class_2512.method_10689(uUID));
			}
		}

		arg.method_10566("TrustedUUIDs", lv);
		arg.method_10556("Sleeping", this.method_6113());
		arg.method_10582("Type", this.method_18271().method_18310());
		arg.method_10556("Sitting", this.method_18272());
		arg.method_10556("Crouching", this.method_18276());
	}

	@Override
	public void method_5749(class_2487 arg) {
		super.method_5749(arg);
		class_2499 lv = arg.method_10554("TrustedUUIDs", 10);

		for (int i = 0; i < lv.size(); i++) {
			this.method_18266(class_2512.method_10690(lv.method_10602(i)));
		}

		this.method_18302(arg.method_10577("Sleeping"));
		this.method_18255(class_4019.class_4039.method_18314(arg.method_10558("Type")));
		this.method_18294(arg.method_10577("Sitting"));
		this.method_18297(arg.method_10577("Crouching"));
		this.method_18280();
	}

	public boolean method_18272() {
		return this.method_18293(1);
	}

	public void method_18294(boolean bl) {
		this.method_18269(1, bl);
	}

	public boolean method_18273() {
		return this.method_18293(64);
	}

	private void method_18295(boolean bl) {
		this.method_18269(64, bl);
	}

	private boolean method_18282() {
		return this.method_18293(128);
	}

	private void method_18301(boolean bl) {
		this.method_18269(128, bl);
	}

	@Override
	public boolean method_6113() {
		return this.method_18293(32);
	}

	private void method_18302(boolean bl) {
		this.method_18269(32, bl);
	}

	private void method_18269(int i, boolean bl) {
		if (bl) {
			this.field_6011.method_12778(field_17950, (byte)(this.field_6011.method_12789(field_17950) | i));
		} else {
			this.field_6011.method_12778(field_17950, (byte)(this.field_6011.method_12789(field_17950) & ~i));
		}
	}

	private boolean method_18293(int i) {
		return (this.field_6011.method_12789(field_17950) & i) != 0;
	}

	@Override
	public boolean method_18397(class_1799 arg) {
		class_1304 lv = class_1308.method_5953(arg);
		return !this.method_6118(lv).method_7960() ? false : lv == class_1304.field_6173 && super.method_18397(arg);
	}

	@Override
	protected boolean method_5939(class_1799 arg) {
		class_1792 lv = arg.method_7909();
		class_1799 lv2 = this.method_6118(class_1304.field_6173);
		return lv2.method_7960() || this.field_17964 > 0 && lv.method_19263() && !lv2.method_7909().method_19263();
	}

	private void method_18289(class_1799 arg) {
		if (!arg.method_7960() && !this.field_6002.field_9236) {
			class_1542 lv = new class_1542(
				this.field_6002, this.field_5987 + this.method_5720().field_1352, this.field_6010 + 1.0, this.field_6035 + this.method_5720().field_1350, arg
			);
			lv.method_6982(40);
			lv.method_6981(this.method_5667());
			this.method_5783(class_3417.field_18054, 1.0F, 1.0F);
			this.field_6002.method_8649(lv);
		}
	}

	private void method_18291(class_1799 arg) {
		class_1542 lv = new class_1542(this.field_6002, this.field_5987, this.field_6010, this.field_6035, arg);
		this.field_6002.method_8649(lv);
	}

	@Override
	protected void method_5949(class_1542 arg) {
		class_1799 lv = arg.method_6983();
		if (this.method_5939(lv)) {
			int i = lv.method_7947();
			if (i > 1) {
				this.method_18291(lv.method_7971(i - 1));
			}

			this.method_18289(this.method_6118(class_1304.field_6173));
			this.method_5673(class_1304.field_6173, lv.method_7971(1));
			this.field_6187[class_1304.field_6173.method_5927()] = 2.0F;
			this.method_6103(arg, lv.method_7947());
			arg.method_5650();
			this.field_17964 = 0;
		}
	}

	@Override
	public void method_5773() {
		super.method_5773();
		if (this.method_6034()) {
			boolean bl = this.method_5799();
			if (bl || this.method_5968() != null || this.field_6002.method_8546()) {
				this.method_18283();
			}

			if (bl || this.method_6113()) {
				this.method_18294(false);
			}

			if (this.method_18273() && this.field_6002.field_9229.nextFloat() < 0.2F) {
				class_2338 lv = new class_2338(this.field_5987, this.field_6010, this.field_6035);
				class_2680 lv2 = this.field_6002.method_8320(lv);
				this.field_6002.method_20290(2001, lv, class_2248.method_9507(lv2));
			}
		}

		this.field_17961 = this.field_17960;
		if (this.method_18277()) {
			this.field_17960 = this.field_17960 + (1.0F - this.field_17960) * 0.4F;
		} else {
			this.field_17960 = this.field_17960 + (0.0F - this.field_17960) * 0.4F;
		}

		this.field_17963 = this.field_17962;
		if (this.method_18276()) {
			this.field_17962 += 0.2F;
			if (this.field_17962 > 3.0F) {
				this.field_17962 = 3.0F;
			}
		} else {
			this.field_17962 = 0.0F;
		}
	}

	@Override
	public boolean method_6481(class_1799 arg) {
		return arg.method_7909() == class_1802.field_16998;
	}

	@Override
	protected void method_18249(class_1657 arg, class_1296 arg2) {
		((class_4019)arg2).method_18266(arg.method_5667());
	}

	public boolean method_18274() {
		return this.method_18293(16);
	}

	public void method_18296(boolean bl) {
		this.method_18269(16, bl);
	}

	public boolean method_18275() {
		return this.field_17962 == 3.0F;
	}

	public void method_18297(boolean bl) {
		this.method_18269(4, bl);
	}

	public boolean method_18276() {
		return this.method_18293(4);
	}

	public void method_18299(boolean bl) {
		this.method_18269(8, bl);
	}

	public boolean method_18277() {
		return this.method_18293(8);
	}

	@Environment(EnvType.CLIENT)
	public float method_18298(float f) {
		return class_3532.method_16439(f, this.field_17961, this.field_17960) * 0.11F * (float) Math.PI;
	}

	@Environment(EnvType.CLIENT)
	public float method_18300(float f) {
		return class_3532.method_16439(f, this.field_17963, this.field_17962);
	}

	@Override
	public void method_5980(@Nullable class_1309 arg) {
		if (this.method_18282() && arg == null) {
			this.method_18301(false);
		}

		super.method_5980(arg);
	}

	@Override
	public void method_5747(float f, float g) {
		int i = class_3532.method_15386((f - 5.0F) * g);
		if (i > 0) {
			this.method_5643(class_1282.field_5868, (float)i);
			if (this.method_5782()) {
				for (class_1297 lv : this.method_5736()) {
					lv.method_5643(class_1282.field_5868, (float)i);
				}
			}

			class_2680 lv2 = this.field_6002.method_8320(new class_2338(this.field_5987, this.field_6010 - 0.2 - (double)this.field_5982, this.field_6035));
			if (!lv2.method_11588() && !this.method_5701()) {
				class_2498 lv3 = lv2.method_11638();
				this.field_6002
					.method_8465(
						null, this.field_5987, this.field_6010, this.field_6035, lv3.method_10594(), this.method_5634(), lv3.method_10597() * 0.5F, lv3.method_10599() * 0.75F
					);
			}
		}
	}

	private void method_18283() {
		this.method_18302(false);
	}

	private void method_18284() {
		this.method_18299(false);
		this.method_18297(false);
		this.method_18294(false);
		this.method_18302(false);
		this.method_18301(false);
		this.method_18295(false);
	}

	private boolean method_18285() {
		return !this.method_6113() && !this.method_18272() && !this.method_18273();
	}

	@Override
	public void method_5966() {
		class_3414 lv = this.method_5994();
		if (lv == class_3417.field_18265) {
			this.method_5783(lv, 2.0F, this.method_6017());
		} else {
			super.method_5966();
		}
	}

	@Nullable
	@Override
	protected class_3414 method_5994() {
		if (this.method_6113()) {
			return class_3417.field_18062;
		} else {
			if (!this.field_6002.method_8530() && this.field_5974.nextFloat() < 0.1F) {
				List<class_1657> list = this.field_6002.method_8390(class_1657.class, this.method_5829().method_1009(16.0, 16.0, 16.0), class_1301.field_6155);
				if (list.isEmpty()) {
					return class_3417.field_18265;
				}
			}

			return class_3417.field_18056;
		}
	}

	@Nullable
	@Override
	protected class_3414 method_6011(class_1282 arg) {
		return class_3417.field_18061;
	}

	@Nullable
	@Override
	protected class_3414 method_6002() {
		return class_3417.field_18059;
	}

	private boolean method_18428(UUID uUID) {
		return this.method_18281().contains(uUID);
	}

	@Override
	protected void method_16080(class_1282 arg) {
		class_1799 lv = this.method_6118(class_1304.field_6173);
		if (!lv.method_7960()) {
			this.method_5775(lv);
			this.method_5673(class_1304.field_6173, class_1799.field_8037);
		}

		super.method_16080(arg);
	}

	public static boolean method_18257(class_4019 arg, class_1309 arg2) {
		double d = arg2.field_6035 - arg.field_6035;
		double e = arg2.field_5987 - arg.field_5987;
		double f = d / e;
		int i = 6;

		for (int j = 0; j < 6; j++) {
			double g = f == 0.0 ? 0.0 : d * (double)((float)j / 6.0F);
			double h = f == 0.0 ? e * (double)((float)j / 6.0F) : g / f;

			for (int k = 1; k < 4; k++) {
				if (!arg.field_6002.method_8320(new class_2338(arg.field_5987 + h, arg.field_6010 + (double)k, arg.field_6035 + g)).method_11620().method_15800()) {
					return false;
				}
			}
		}

		return true;
	}

	class class_4020 extends class_1400<class_1309> {
		@Nullable
		private class_1309 field_17966;
		private class_1309 field_17967;
		private int field_17968;

		public class_4020(Class<class_1309> class_, boolean bl, boolean bl2, @Nullable Predicate<class_1309> predicate) {
			super(class_4019.this, class_, 10, bl, bl2, predicate);
		}

		@Override
		public boolean method_6264() {
			if (this.field_6641 > 0 && this.field_6660.method_6051().nextInt(this.field_6641) != 0) {
				return false;
			} else {
				for (UUID uUID : class_4019.this.method_18281()) {
					if (uUID != null && class_4019.this.field_6002 instanceof class_3218) {
						class_1297 lv = ((class_3218)class_4019.this.field_6002).method_14190(uUID);
						if (lv instanceof class_1309) {
							class_1309 lv2 = (class_1309)lv;
							this.field_17967 = lv2;
							this.field_17966 = lv2.method_6065();
							int i = lv2.method_6117();
							return i != this.field_17968 && this.method_6328(this.field_17966, class_4051.field_18092);
						}
					}
				}

				return false;
			}
		}

		@Override
		public void method_6269() {
			class_4019.this.method_5980(this.field_17966);
			this.field_6644 = this.field_17966;
			if (this.field_17967 != null) {
				this.field_17968 = this.field_17967.method_6117();
			}

			class_4019.this.method_5783(class_3417.field_18055, 1.0F, 1.0F);
			class_4019.this.method_18301(true);
			class_4019.this.method_18283();
			super.method_6269();
		}
	}

	class class_4021 extends class_1352 {
		int field_17969;

		public class_4021() {
			this.method_6265(EnumSet.of(class_1352.class_4134.field_18406, class_1352.class_4134.field_18407, class_1352.class_4134.field_18405));
		}

		@Override
		public boolean method_6264() {
			return class_4019.this.method_18273();
		}

		@Override
		public boolean method_6266() {
			return this.method_6264() && this.field_17969 > 0;
		}

		@Override
		public void method_6269() {
			this.field_17969 = 40;
		}

		@Override
		public void method_6270() {
			class_4019.this.method_18295(false);
		}

		@Override
		public void method_6268() {
			this.field_17969--;
		}
	}

	public class class_4022 implements Predicate<class_1309> {
		public boolean method_18303(class_1309 arg) {
			if (arg instanceof class_4019) {
				return false;
			} else if (arg instanceof class_1428 || arg instanceof class_1463 || arg instanceof class_1588) {
				return true;
			} else if (arg instanceof class_1321) {
				return !((class_1321)arg).method_6181();
			} else if (!(arg instanceof class_1657) || !arg.method_7325() && !((class_1657)arg).method_7337()) {
				return class_4019.this.method_18428(arg.method_5667()) ? false : !arg.method_6113() && !arg.method_5715();
			} else {
				return false;
			}
		}
	}

	abstract class class_4023 extends class_1352 {
		private final class_4051 field_18102 = new class_4051().method_18418(12.0).method_18422().method_18420(class_4019.this.new class_4022());

		private class_4023() {
		}

		protected boolean method_18305() {
			class_2338 lv = new class_2338(class_4019.this);
			return !class_4019.this.field_6002.method_8311(lv) && class_4019.this.method_6149(lv) >= 0.0F;
		}

		protected boolean method_18306() {
			return !class_4019.this.field_6002
				.method_18466(class_1309.class, this.field_18102, class_4019.this, class_4019.this.method_5829().method_1009(12.0, 6.0, 12.0))
				.isEmpty();
		}
	}

	class class_4024 extends class_1341 {
		public class_4024(double d) {
			super(class_4019.this, d);
		}

		@Override
		public void method_6269() {
			((class_4019)this.field_6404).method_18284();
			((class_4019)this.field_6406).method_18284();
			super.method_6269();
		}

		@Override
		protected void method_6249() {
			class_4019 lv = (class_4019)this.field_6404.method_5613(this.field_6406);
			if (lv != null) {
				class_3222 lv2 = this.field_6404.method_6478();
				class_3222 lv3 = this.field_6406.method_6478();
				class_3222 lv4 = lv2;
				if (lv2 != null) {
					lv.method_18266(lv2.method_5667());
				} else {
					lv4 = lv3;
				}

				if (lv3 != null && lv2 != lv3) {
					lv.method_18266(lv3.method_5667());
				}

				if (lv4 != null) {
					lv4.method_7281(class_3468.field_15410);
					class_174.field_1190.method_855(lv4, this.field_6404, this.field_6406, lv);
				}

				int i = 6000;
				this.field_6404.method_5614(6000);
				this.field_6406.method_5614(6000);
				this.field_6404.method_6477();
				this.field_6406.method_6477();
				lv.method_5614(-24000);
				lv.method_5808(this.field_6404.field_5987, this.field_6404.field_6010, this.field_6404.field_6035, 0.0F, 0.0F);
				this.field_6405.method_8649(lv);
				this.field_6405.method_8421(this.field_6404, (byte)18);
				if (this.field_6405.method_8450().method_8355(class_1928.field_19391)) {
					this.field_6405
						.method_8649(
							new class_1303(
								this.field_6405, this.field_6404.field_5987, this.field_6404.field_6010, this.field_6404.field_6035, this.field_6404.method_6051().nextInt(7) + 1
							)
						);
				}
			}
		}
	}

	public class class_4025 extends class_1367 {
		protected int field_17974;

		public class_4025(double d, int i, int j) {
			super(class_4019.this, d, i, j);
		}

		@Override
		public double method_6291() {
			return 2.0;
		}

		@Override
		public boolean method_6294() {
			return this.field_6517 % 100 == 0;
		}

		@Override
		protected boolean method_6296(class_1941 arg, class_2338 arg2) {
			class_2680 lv = arg.method_8320(arg2);
			return lv.method_11614() == class_2246.field_16999 && (Integer)lv.method_11654(class_3830.field_17000) >= 2;
		}

		@Override
		public void method_6268() {
			if (this.method_6295()) {
				if (this.field_17974 >= 40) {
					this.method_18307();
				} else {
					this.field_17974++;
				}
			} else if (!this.method_6295() && class_4019.this.field_5974.nextFloat() < 0.05F) {
				class_4019.this.method_5783(class_3417.field_18063, 1.0F, 1.0F);
			}

			super.method_6268();
		}

		protected void method_18307() {
			if (class_4019.this.field_6002.method_8450().method_8355(class_1928.field_19388)) {
				class_2680 lv = class_4019.this.field_6002.method_8320(this.field_6512);
				if (lv.method_11614() == class_2246.field_16999) {
					int i = (Integer)lv.method_11654(class_3830.field_17000);
					lv.method_11657(class_3830.field_17000, Integer.valueOf(1));
					int j = 1 + class_4019.this.field_6002.field_9229.nextInt(2) + (i == 3 ? 1 : 0);
					class_1799 lv2 = class_4019.this.method_6118(class_1304.field_6173);
					if (lv2.method_7960()) {
						class_4019.this.method_5673(class_1304.field_6173, new class_1799(class_1802.field_16998));
						j--;
					}

					if (j > 0) {
						class_2248.method_9577(class_4019.this.field_6002, this.field_6512, new class_1799(class_1802.field_16998, j));
					}

					class_4019.this.method_5783(class_3417.field_17617, 1.0F, 1.0F);
					class_4019.this.field_6002.method_8652(this.field_6512, lv.method_11657(class_3830.field_17000, Integer.valueOf(1)), 2);
				}
			}
		}

		@Override
		public boolean method_6264() {
			return !class_4019.this.method_6113() && super.method_6264();
		}

		@Override
		public void method_6269() {
			this.field_17974 = 0;
			class_4019.this.method_18294(false);
			super.method_6269();
		}
	}

	class class_4026 extends class_1347 {
		public class_4026() {
			super(class_4019.this);
		}

		@Override
		public void method_6269() {
			super.method_6269();
			class_4019.this.method_18284();
		}

		@Override
		public boolean method_6264() {
			return class_4019.this.method_5799() && class_4019.this.method_5861() > 0.25 || class_4019.this.method_5771();
		}
	}

	public static class class_4027 implements class_1315 {
		public final class_4019.class_4039 field_17977;
		public int field_17978;

		public class_4027(class_4019.class_4039 arg) {
			this.field_17977 = arg;
		}
	}

	public class class_4028 extends class_1333 {
		public class_4028() {
			super(class_4019.this);
		}

		@Override
		public void method_6231() {
			if (!class_4019.this.method_6113()) {
				super.method_6231();
			}
		}

		@Override
		protected boolean method_20433() {
			return !class_4019.this.method_18274() && !class_4019.this.method_18276() && !class_4019.this.method_18277() & !class_4019.this.method_18273();
		}
	}

	class class_4029 extends class_1366 {
		public class_4029(double d, boolean bl) {
			super(class_4019.this, d, bl);
		}

		@Override
		protected void method_6288(class_1309 arg, double d) {
			double e = this.method_6289(arg);
			if (d <= e && this.field_6505 <= 0) {
				this.field_6505 = 20;
				this.field_6503.method_6121(arg);
				class_4019.this.method_5783(class_3417.field_18058, 1.0F, 1.0F);
			}
		}

		@Override
		public void method_6269() {
			class_4019.this.method_18299(false);
			super.method_6269();
		}

		@Override
		public boolean method_6264() {
			return !class_4019.this.method_18272()
				&& !class_4019.this.method_6113()
				&& !class_4019.this.method_18276()
				&& !class_4019.this.method_18273()
				&& super.method_6264();
		}
	}

	class class_4030 extends class_1335 {
		public class_4030() {
			super(class_4019.this);
		}

		@Override
		public void method_6240() {
			if (class_4019.this.method_18285()) {
				super.method_6240();
			}
		}
	}

	class class_4031 extends class_4018 {
		public class_4031(int i, int j) {
			super(class_4019.this, j);
		}

		@Override
		public void method_6269() {
			class_4019.this.method_18284();
			super.method_6269();
		}

		@Override
		public boolean method_6264() {
			return super.method_6264() && this.method_18308();
		}

		@Override
		public boolean method_6266() {
			return super.method_6266() && this.method_18308();
		}

		private boolean method_18308() {
			return !class_4019.this.method_6113() && !class_4019.this.method_18272() && !class_4019.this.method_18282() && class_4019.this.method_5968() == null;
		}
	}

	class class_4032 extends class_1374 {
		public class_4032(double d) {
			super(class_4019.this, d);
		}

		@Override
		public boolean method_6264() {
			return !class_4019.this.method_18282() && super.method_6264();
		}
	}

	public class class_4033 extends class_4017 {
		@Override
		public boolean method_6264() {
			if (!class_4019.this.method_18275()) {
				return false;
			} else {
				class_1309 lv = class_4019.this.method_5968();
				if (lv != null && lv.method_5805()) {
					if (lv.method_5755() != lv.method_5735()) {
						return false;
					} else {
						boolean bl = class_4019.method_18257(class_4019.this, lv);
						if (!bl) {
							class_4019.this.method_5942().method_6349(lv);
							class_4019.this.method_18297(false);
							class_4019.this.method_18299(false);
						}

						return bl;
					}
				} else {
					return false;
				}
			}
		}

		@Override
		public boolean method_6266() {
			class_1309 lv = class_4019.this.method_5968();
			if (lv != null && lv.method_5805()) {
				double d = class_4019.this.method_18798().field_1351;
				return (!(d * d < 0.05F) || !(Math.abs(class_4019.this.field_5965) < 15.0F) || !class_4019.this.field_5952) && !class_4019.this.method_18273();
			} else {
				return false;
			}
		}

		@Override
		public boolean method_6267() {
			return false;
		}

		@Override
		public void method_6269() {
			class_4019.this.method_6100(true);
			class_4019.this.method_18296(true);
			class_4019.this.method_18299(false);
			class_1309 lv = class_4019.this.method_5968();
			class_4019.this.method_5988().method_6226(lv, 60.0F, 30.0F);
			class_243 lv2 = new class_243(
					lv.field_5987 - class_4019.this.field_5987, lv.field_6010 - class_4019.this.field_6010, lv.field_6035 - class_4019.this.field_6035
				)
				.method_1029();
			class_4019.this.method_18799(class_4019.this.method_18798().method_1031(lv2.field_1352 * 0.8, 0.9, lv2.field_1350 * 0.8));
			class_4019.this.method_5942().method_6340();
		}

		@Override
		public void method_6270() {
			class_4019.this.method_18297(false);
			class_4019.this.field_17962 = 0.0F;
			class_4019.this.field_17963 = 0.0F;
			class_4019.this.method_18299(false);
			class_4019.this.method_18296(false);
		}

		@Override
		public void method_6268() {
			class_1309 lv = class_4019.this.method_5968();
			if (lv != null) {
				class_4019.this.method_5988().method_6226(lv, 60.0F, 30.0F);
			}

			if (!class_4019.this.method_18273()) {
				class_243 lv2 = class_4019.this.method_18798();
				if (lv2.field_1351 * lv2.field_1351 < 0.03F && class_4019.this.field_5965 != 0.0F) {
					class_4019.this.field_5965 = this.method_18251(class_4019.this.field_5965, 0.0F, 0.2F);
				} else {
					double d = Math.sqrt(class_1297.method_17996(lv2));
					double e = Math.signum(-lv2.field_1351) * Math.acos(d / lv2.method_1033()) * 180.0F / (float)Math.PI;
					class_4019.this.field_5965 = (float)e;
				}
			}

			if (lv != null && class_4019.this.method_5739(lv) <= 2.0F) {
				class_4019.this.method_6121(lv);
			} else if (class_4019.this.field_5965 > 0.0F
				&& class_4019.this.field_5952
				&& (float)class_4019.this.method_18798().field_1351 != 0.0F
				&& class_4019.this.field_6002.method_8320(new class_2338(class_4019.this)).method_11614() == class_2246.field_10477) {
				class_4019.this.field_5965 = 60.0F;
				class_4019.this.method_5980(null);
				class_4019.this.method_18295(true);
			}
		}
	}

	class class_4034 extends class_1352 {
		public class_4034() {
			this.method_6265(EnumSet.of(class_1352.class_4134.field_18405));
		}

		@Override
		public boolean method_6264() {
			if (!class_4019.this.method_6118(class_1304.field_6173).method_7960()) {
				return false;
			} else if (class_4019.this.method_5968() != null || class_4019.this.method_6065() != null) {
				return false;
			} else if (!class_4019.this.method_18285()) {
				return false;
			} else if (class_4019.this.method_6051().nextInt(10) != 0) {
				return false;
			} else {
				List<class_1542> list = class_4019.this.field_6002
					.method_8390(class_1542.class, class_4019.this.method_5829().method_1009(8.0, 8.0, 8.0), class_4019.field_17953);
				return !list.isEmpty() && class_4019.this.method_6118(class_1304.field_6173).method_7960();
			}
		}

		@Override
		public void method_6268() {
			List<class_1542> list = class_4019.this.field_6002
				.method_8390(class_1542.class, class_4019.this.method_5829().method_1009(8.0, 8.0, 8.0), class_4019.field_17953);
			class_1799 lv = class_4019.this.method_6118(class_1304.field_6173);
			if (lv.method_7960() && !list.isEmpty()) {
				class_4019.this.method_5942().method_6335((class_1297)list.get(0), 1.2F);
			}
		}

		@Override
		public void method_6269() {
			List<class_1542> list = class_4019.this.field_6002
				.method_8390(class_1542.class, class_4019.this.method_5829().method_1009(8.0, 8.0, 8.0), class_4019.field_17953);
			if (!list.isEmpty()) {
				class_4019.this.method_5942().method_6335((class_1297)list.get(0), 1.2F);
			}
		}
	}

	class class_4035 extends class_4019.class_4023 {
		private double field_17987;
		private double field_17988;
		private int field_17989;
		private int field_17990;

		public class_4035() {
			this.method_6265(EnumSet.of(class_1352.class_4134.field_18405, class_1352.class_4134.field_18406));
		}

		@Override
		public boolean method_6264() {
			return class_4019.this.method_6065() == null
				&& class_4019.this.method_6051().nextFloat() < 0.02F
				&& !class_4019.this.method_6113()
				&& class_4019.this.method_5968() == null
				&& class_4019.this.method_5942().method_6357()
				&& !this.method_18306()
				&& !class_4019.this.method_18274()
				&& !class_4019.this.method_18276();
		}

		@Override
		public boolean method_6266() {
			return this.field_17990 > 0;
		}

		@Override
		public void method_6269() {
			this.method_18309();
			this.field_17990 = 2 + class_4019.this.method_6051().nextInt(3);
			class_4019.this.method_18294(true);
			class_4019.this.method_5942().method_6340();
		}

		@Override
		public void method_6270() {
			class_4019.this.method_18294(false);
		}

		@Override
		public void method_6268() {
			this.field_17989--;
			if (this.field_17989 <= 0) {
				this.field_17990--;
				this.method_18309();
			}

			class_4019.this.method_5988()
				.method_6230(
					class_4019.this.field_5987 + this.field_17987,
					class_4019.this.field_6010 + (double)class_4019.this.method_5751(),
					class_4019.this.field_6035 + this.field_17988,
					(float)class_4019.this.method_5986(),
					(float)class_4019.this.method_5978()
				);
		}

		private void method_18309() {
			double d = (Math.PI * 2) * class_4019.this.method_6051().nextDouble();
			this.field_17987 = Math.cos(d);
			this.field_17988 = Math.sin(d);
			this.field_17989 = 80 + class_4019.this.method_6051().nextInt(20);
		}
	}

	class class_4036 extends class_1344 {
		private int field_17992 = 100;

		public class_4036(double d) {
			super(class_4019.this, d);
		}

		@Override
		public boolean method_6264() {
			if (class_4019.this.method_6113() || this.field_6419.method_5968() != null) {
				return false;
			} else if (class_4019.this.field_6002.method_8546()) {
				return true;
			} else if (this.field_17992 > 0) {
				this.field_17992--;
				return false;
			} else {
				this.field_17992 = 100;
				class_2338 lv = new class_2338(this.field_6419);
				return class_4019.this.field_6002.method_8530()
					&& class_4019.this.field_6002.method_8311(lv)
					&& !((class_3218)class_4019.this.field_6002).method_19500(lv)
					&& this.method_18250();
			}
		}

		@Override
		public void method_6269() {
			class_4019.this.method_18284();
			super.method_6269();
		}
	}

	class class_4037 extends class_4019.class_4023 {
		private int field_17994 = class_4019.this.field_5974.nextInt(140);

		public class_4037() {
			this.method_6265(EnumSet.of(class_1352.class_4134.field_18405, class_1352.class_4134.field_18406, class_1352.class_4134.field_18407));
		}

		@Override
		public boolean method_6264() {
			return class_4019.this.field_6212 == 0.0F && class_4019.this.field_6227 == 0.0F && class_4019.this.field_6250 == 0.0F
				? this.method_18432() || class_4019.this.method_6113()
				: false;
		}

		@Override
		public boolean method_6266() {
			return this.method_18432();
		}

		private boolean method_18432() {
			if (this.field_17994 > 0) {
				this.field_17994--;
				return false;
			} else {
				return class_4019.this.field_6002.method_8530() && this.method_18305() && !this.method_18306();
			}
		}

		@Override
		public void method_6270() {
			this.field_17994 = class_4019.this.field_5974.nextInt(140);
			class_4019.this.method_18284();
		}

		@Override
		public void method_6269() {
			class_4019.this.method_18294(false);
			class_4019.this.method_18297(false);
			class_4019.this.method_18299(false);
			class_4019.this.method_6100(false);
			class_4019.this.method_18302(true);
			class_4019.this.method_5942().method_6340();
			class_4019.this.method_5962().method_6239(class_4019.this.field_5987, class_4019.this.field_6010, class_4019.this.field_6035, 0.0);
		}
	}

	class class_4038 extends class_1352 {
		public class_4038() {
			this.method_6265(EnumSet.of(class_1352.class_4134.field_18405, class_1352.class_4134.field_18406));
		}

		@Override
		public boolean method_6264() {
			if (class_4019.this.method_6113()) {
				return false;
			} else {
				class_1309 lv = class_4019.this.method_5968();
				return lv != null
					&& lv.method_5805()
					&& class_4019.field_17955.test(lv)
					&& class_4019.this.method_5858(lv) > 36.0
					&& !class_4019.this.method_18276()
					&& !class_4019.this.method_18277()
					&& !class_4019.this.field_6282;
			}
		}

		@Override
		public void method_6269() {
			class_4019.this.method_18294(false);
			class_4019.this.method_18295(false);
		}

		@Override
		public void method_6270() {
			class_1309 lv = class_4019.this.method_5968();
			if (lv != null && class_4019.method_18257(class_4019.this, lv)) {
				class_4019.this.method_18299(true);
				class_4019.this.method_18297(true);
				class_4019.this.method_5942().method_6340();
				class_4019.this.method_5988().method_6226(lv, (float)class_4019.this.method_5986(), (float)class_4019.this.method_5978());
			} else {
				class_4019.this.method_18299(false);
				class_4019.this.method_18297(false);
			}
		}

		@Override
		public void method_6268() {
			class_1309 lv = class_4019.this.method_5968();
			class_4019.this.method_5988().method_6226(lv, (float)class_4019.this.method_5986(), (float)class_4019.this.method_5978());
			if (class_4019.this.method_5858(lv) <= 36.0) {
				class_4019.this.method_18299(true);
				class_4019.this.method_18297(true);
				class_4019.this.method_5942().method_6340();
			} else {
				class_4019.this.method_5942().method_6335(lv, 1.5);
			}
		}
	}

	public static enum class_4039 {
		field_17996(
			0,
			"red",
			class_1972.field_9420,
			class_1972.field_9428,
			class_1972.field_9422,
			class_1972.field_9477,
			class_1972.field_9416,
			class_1972.field_9429,
			class_1972.field_9404
		),
		field_17997(1, "snow", class_1972.field_9454, class_1972.field_9425, class_1972.field_9437);

		private static final class_4019.class_4039[] field_17998 = (class_4019.class_4039[])Arrays.stream(values())
			.sorted(Comparator.comparingInt(class_4019.class_4039::method_18317))
			.toArray(class_4019.class_4039[]::new);
		private static final Map<String, class_4019.class_4039> field_17999 = (Map<String, class_4019.class_4039>)Arrays.stream(values())
			.collect(Collectors.toMap(class_4019.class_4039::method_18310, arg -> arg));
		private final int field_18000;
		private final String field_18001;
		private final List<class_1959> field_18002;

		private class_4039(int j, String string2, class_1959... args) {
			this.field_18000 = j;
			this.field_18001 = string2;
			this.field_18002 = Arrays.asList(args);
		}

		public String method_18310() {
			return this.field_18001;
		}

		public List<class_1959> method_18315() {
			return this.field_18002;
		}

		public int method_18317() {
			return this.field_18000;
		}

		public static class_4019.class_4039 method_18314(String string) {
			return (class_4019.class_4039)field_17999.getOrDefault(string, field_17996);
		}

		public static class_4019.class_4039 method_18311(int i) {
			if (i < 0 || i > field_17998.length) {
				i = 0;
			}

			return field_17998[i];
		}

		public static class_4019.class_4039 method_18313(class_1959 arg) {
			return field_17997.method_18315().contains(arg) ? field_17997 : field_17996;
		}
	}

	class class_4052 extends class_1353 {
		private final class_4019 field_18104;

		public class_4052(class_4019 arg2, double d) {
			super(arg2, d);
			this.field_18104 = arg2;
		}

		@Override
		public boolean method_6264() {
			return !this.field_18104.method_18282() && super.method_6264();
		}

		@Override
		public boolean method_6266() {
			return !this.field_18104.method_18282() && super.method_6266();
		}

		@Override
		public void method_6269() {
			this.field_18104.method_18284();
			super.method_6269();
		}
	}

	class class_4292 extends class_1361 {
		public class_4292(class_1308 arg2, Class<? extends class_1309> class_, float f) {
			super(arg2, class_, f);
		}

		@Override
		public boolean method_6264() {
			return super.method_6264() && !class_4019.this.method_18273() && !class_4019.this.method_18277();
		}

		@Override
		public boolean method_6266() {
			return super.method_6266() && !class_4019.this.method_18273() && !class_4019.this.method_18277();
		}
	}
}
