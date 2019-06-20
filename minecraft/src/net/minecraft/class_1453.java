package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1453 extends class_1471 implements class_1432 {
	private static final class_2940<Integer> field_6826 = class_2945.method_12791(class_1453.class, class_2943.field_13327);
	private static final Predicate<class_1308> field_6821 = new Predicate<class_1308>() {
		public boolean method_6590(@Nullable class_1308 arg) {
			return arg != null && class_1453.field_6822.containsKey(arg.method_5864());
		}
	};
	private static final class_1792 field_6828 = class_1802.field_8423;
	private static final Set<class_1792> field_6825 = Sets.<class_1792>newHashSet(
		class_1802.field_8317, class_1802.field_8188, class_1802.field_8706, class_1802.field_8309
	);
	private static final Map<class_1299<?>, class_3414> field_6822 = class_156.method_654(Maps.<class_1299<?>, class_3414>newHashMap(), hashMap -> {
		hashMap.put(class_1299.field_6099, class_3417.field_15199);
		hashMap.put(class_1299.field_6084, class_3417.field_15190);
		hashMap.put(class_1299.field_6046, class_3417.field_14547);
		hashMap.put(class_1299.field_6123, class_3417.field_14647);
		hashMap.put(class_1299.field_6086, class_3417.field_14777);
		hashMap.put(class_1299.field_6116, class_3417.field_14854);
		hashMap.put(class_1299.field_6091, class_3417.field_14950);
		hashMap.put(class_1299.field_6128, class_3417.field_15022);
		hashMap.put(class_1299.field_6090, class_3417.field_15113);
		hashMap.put(class_1299.field_6107, class_3417.field_14577);
		hashMap.put(class_1299.field_6118, class_3417.field_18813);
		hashMap.put(class_1299.field_6071, class_3417.field_15185);
		hashMap.put(class_1299.field_6065, class_3417.field_15064);
		hashMap.put(class_1299.field_6102, class_3417.field_14963);
		hashMap.put(class_1299.field_6050, class_3417.field_15143);
		hashMap.put(class_1299.field_6146, class_3417.field_18814);
		hashMap.put(class_1299.field_6078, class_3417.field_14957);
		hashMap.put(class_1299.field_6105, class_3417.field_18815);
		hashMap.put(class_1299.field_6042, class_3417.field_14866);
		hashMap.put(class_1299.field_6134, class_3417.field_18816);
		hashMap.put(class_1299.field_6109, class_3417.field_14768);
		hashMap.put(class_1299.field_6125, class_3417.field_14683);
		hashMap.put(class_1299.field_6137, class_3417.field_14587);
		hashMap.put(class_1299.field_6069, class_3417.field_15098);
		hashMap.put(class_1299.field_6079, class_3417.field_15190);
		hashMap.put(class_1299.field_6098, class_3417.field_14885);
		hashMap.put(class_1299.field_6059, class_3417.field_15032);
		hashMap.put(class_1299.field_6117, class_3417.field_14790);
		hashMap.put(class_1299.field_6145, class_3417.field_14796);
		hashMap.put(class_1299.field_6119, class_3417.field_14555);
		hashMap.put(class_1299.field_6076, class_3417.field_15073);
		hashMap.put(class_1299.field_6055, class_3417.field_14942);
		hashMap.put(class_1299.field_6051, class_3417.field_15220);
		hashMap.put(class_1299.field_6054, class_3417.field_14676);
	});
	public float field_6818;
	public float field_6819;
	public float field_6827;
	public float field_6829;
	public float field_6824 = 1.0F;
	private boolean field_6823;
	private class_2338 field_6820;

	public class_1453(class_1299<? extends class_1453> arg, class_1937 arg2) {
		super(arg, arg2);
		this.field_6207 = new class_1331(this);
	}

	@Nullable
	@Override
	public class_1315 method_5943(class_1936 arg, class_1266 arg2, class_3730 arg3, @Nullable class_1315 arg4, @Nullable class_2487 arg5) {
		this.method_6585(this.field_5974.nextInt(5));
		return super.method_5943(arg, arg2, arg3, arg4, arg5);
	}

	@Override
	protected void method_5959() {
		this.field_6321 = new class_1386(this);
		this.field_6201.method_6277(0, new class_1374(this, 1.25));
		this.field_6201.method_6277(0, new class_1347(this));
		this.field_6201.method_6277(1, new class_1361(this, class_1657.class, 8.0F));
		this.field_6201.method_6277(2, this.field_6321);
		this.field_6201.method_6277(2, new class_1351(this, 1.0, 5.0F, 1.0F));
		this.field_6201.method_6277(2, new class_1395(this, 1.0));
		this.field_6201.method_6277(3, new class_1360(this));
		this.field_6201.method_6277(3, new class_1348(this, 1.0, 3.0F, 7.0F));
	}

	@Override
	protected void method_6001() {
		super.method_6001();
		this.method_6127().method_6208(class_1612.field_7355);
		this.method_5996(class_1612.field_7359).method_6192(6.0);
		this.method_5996(class_1612.field_7355).method_6192(0.4F);
		this.method_5996(class_1612.field_7357).method_6192(0.2F);
	}

	@Override
	protected class_1408 method_5965(class_1937 arg) {
		class_1407 lv = new class_1407(this, arg);
		lv.method_6332(false);
		lv.method_6354(true);
		lv.method_6331(true);
		return lv;
	}

	@Override
	protected float method_18394(class_4050 arg, class_4048 arg2) {
		return arg2.field_18068 * 0.6F;
	}

	@Override
	public void method_6007() {
		method_6587(this.field_6002, this);
		if (this.field_6820 == null
			|| !this.field_6820.method_19769(this.method_19538(), 3.46)
			|| this.field_6002.method_8320(this.field_6820).method_11614() != class_2246.field_10223) {
			this.field_6823 = false;
			this.field_6820 = null;
		}

		super.method_6007();
		this.method_6578();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_6006(class_2338 arg, boolean bl) {
		this.field_6820 = arg;
		this.field_6823 = bl;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_6582() {
		return this.field_6823;
	}

	private void method_6578() {
		this.field_6829 = this.field_6818;
		this.field_6827 = this.field_6819;
		this.field_6819 = (float)((double)this.field_6819 + (double)(this.field_5952 ? -1 : 4) * 0.3);
		this.field_6819 = class_3532.method_15363(this.field_6819, 0.0F, 1.0F);
		if (!this.field_5952 && this.field_6824 < 1.0F) {
			this.field_6824 = 1.0F;
		}

		this.field_6824 = (float)((double)this.field_6824 * 0.9);
		class_243 lv = this.method_18798();
		if (!this.field_5952 && lv.field_1351 < 0.0) {
			this.method_18799(lv.method_18805(1.0, 0.6, 1.0));
		}

		this.field_6818 = this.field_6818 + this.field_6824 * 2.0F;
	}

	private static boolean method_6587(class_1937 arg, class_1297 arg2) {
		if (arg2.method_5805() && !arg2.method_5701() && arg.field_9229.nextInt(50) == 0) {
			List<class_1308> list = arg.method_8390(class_1308.class, arg2.method_5829().method_1014(20.0), field_6821);
			if (!list.isEmpty()) {
				class_1308 lv = (class_1308)list.get(arg.field_9229.nextInt(list.size()));
				if (!lv.method_5701()) {
					class_3414 lv2 = method_6586(lv.method_5864());
					arg.method_8465(null, arg2.field_5987, arg2.field_6010, arg2.field_6035, lv2, arg2.method_5634(), 0.7F, method_6580(arg.field_9229));
					return true;
				}
			}

			return false;
		} else {
			return false;
		}
	}

	@Override
	public boolean method_5992(class_1657 arg, class_1268 arg2) {
		class_1799 lv = arg.method_5998(arg2);
		if (!this.method_6181() && field_6825.contains(lv.method_7909())) {
			if (!arg.field_7503.field_7477) {
				lv.method_7934(1);
			}

			if (!this.method_5701()) {
				this.field_6002
					.method_8465(
						null,
						this.field_5987,
						this.field_6010,
						this.field_6035,
						class_3417.field_14960,
						this.method_5634(),
						1.0F,
						1.0F + (this.field_5974.nextFloat() - this.field_5974.nextFloat()) * 0.2F
					);
			}

			if (!this.field_6002.field_9236) {
				if (this.field_5974.nextInt(10) == 0) {
					this.method_6170(arg);
					this.method_6180(true);
					this.field_6002.method_8421(this, (byte)7);
				} else {
					this.method_6180(false);
					this.field_6002.method_8421(this, (byte)6);
				}
			}

			return true;
		} else if (lv.method_7909() == field_6828) {
			if (!arg.field_7503.field_7477) {
				lv.method_7934(1);
			}

			this.method_6092(new class_1293(class_1294.field_5899, 900));
			if (arg.method_7337() || !this.method_5655()) {
				this.method_5643(class_1282.method_5532(arg), Float.MAX_VALUE);
			}

			return true;
		} else {
			if (!this.field_6002.field_9236 && !this.method_6581() && this.method_6181() && this.method_6171(arg)) {
				this.field_6321.method_6311(!this.method_6172());
			}

			return super.method_5992(arg, arg2);
		}
	}

	@Override
	public boolean method_6481(class_1799 arg) {
		return false;
	}

	public static boolean method_20667(class_1299<class_1453> arg, class_1936 arg2, class_3730 arg3, class_2338 arg4, Random random) {
		class_2248 lv = arg2.method_8320(arg4.method_10074()).method_11614();
		return (lv.method_9525(class_3481.field_15503) || lv == class_2246.field_10219 || lv instanceof class_2410 || lv == class_2246.field_10124)
			&& arg2.method_8624(arg4, 0) > 8;
	}

	@Override
	public void method_5747(float f, float g) {
	}

	@Override
	protected void method_5623(double d, boolean bl, class_2680 arg, class_2338 arg2) {
	}

	@Override
	public boolean method_6474(class_1429 arg) {
		return false;
	}

	@Nullable
	@Override
	public class_1296 method_5613(class_1296 arg) {
		return null;
	}

	public static void method_6589(class_1937 arg, class_1297 arg2) {
		if (!arg2.method_5701() && !method_6587(arg, arg2) && arg.field_9229.nextInt(200) == 0) {
			arg.method_8465(null, arg2.field_5987, arg2.field_6010, arg2.field_6035, method_6583(arg.field_9229), arg2.method_5634(), 1.0F, method_6580(arg.field_9229));
		}
	}

	@Override
	public boolean method_6121(class_1297 arg) {
		return arg.method_5643(class_1282.method_5511(this), 3.0F);
	}

	@Nullable
	@Override
	public class_3414 method_5994() {
		return method_6583(this.field_5974);
	}

	private static class_3414 method_6583(Random random) {
		if (random.nextInt(1000) == 0) {
			List<class_1299<?>> list = Lists.<class_1299<?>>newArrayList(field_6822.keySet());
			return method_6586((class_1299<?>)list.get(random.nextInt(list.size())));
		} else {
			return class_3417.field_15132;
		}
	}

	public static class_3414 method_6586(class_1299<?> arg) {
		return (class_3414)field_6822.getOrDefault(arg, class_3417.field_15132);
	}

	@Override
	protected class_3414 method_6011(class_1282 arg) {
		return class_3417.field_15077;
	}

	@Override
	protected class_3414 method_6002() {
		return class_3417.field_15234;
	}

	@Override
	protected void method_5712(class_2338 arg, class_2680 arg2) {
		this.method_5783(class_3417.field_14602, 0.15F, 1.0F);
	}

	@Override
	protected float method_5801(float f) {
		this.method_5783(class_3417.field_14925, 0.15F, 1.0F);
		return f + this.field_6819 / 2.0F;
	}

	@Override
	protected boolean method_5776() {
		return true;
	}

	@Override
	protected float method_6017() {
		return method_6580(this.field_5974);
	}

	private static float method_6580(Random random) {
		return (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F;
	}

	@Override
	public class_3419 method_5634() {
		return class_3419.field_15254;
	}

	@Override
	public boolean method_5810() {
		return true;
	}

	@Override
	protected void method_6087(class_1297 arg) {
		if (!(arg instanceof class_1657)) {
			super.method_6087(arg);
		}
	}

	@Override
	public boolean method_5643(class_1282 arg, float f) {
		if (this.method_5679(arg)) {
			return false;
		} else {
			if (this.field_6321 != null) {
				this.field_6321.method_6311(false);
			}

			return super.method_5643(arg, f);
		}
	}

	public int method_6584() {
		return class_3532.method_15340(this.field_6011.method_12789(field_6826), 0, 4);
	}

	public void method_6585(int i) {
		this.field_6011.method_12778(field_6826, i);
	}

	@Override
	protected void method_5693() {
		super.method_5693();
		this.field_6011.method_12784(field_6826, 0);
	}

	@Override
	public void method_5652(class_2487 arg) {
		super.method_5652(arg);
		arg.method_10569("Variant", this.method_6584());
	}

	@Override
	public void method_5749(class_2487 arg) {
		super.method_5749(arg);
		this.method_6585(arg.method_10550("Variant"));
	}

	public boolean method_6581() {
		return !this.field_5952;
	}
}
