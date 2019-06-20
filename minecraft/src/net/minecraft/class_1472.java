package net.minecraft;

import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1472 extends class_1429 {
	private static final class_2940<Byte> field_6870 = class_2945.method_12791(class_1472.class, class_2943.field_13319);
	private static final Map<class_1767, class_1935> field_6868 = class_156.method_654(Maps.newEnumMap(class_1767.class), enumMap -> {
		enumMap.put(class_1767.field_7952, class_2246.field_10446);
		enumMap.put(class_1767.field_7946, class_2246.field_10095);
		enumMap.put(class_1767.field_7958, class_2246.field_10215);
		enumMap.put(class_1767.field_7951, class_2246.field_10294);
		enumMap.put(class_1767.field_7947, class_2246.field_10490);
		enumMap.put(class_1767.field_7961, class_2246.field_10028);
		enumMap.put(class_1767.field_7954, class_2246.field_10459);
		enumMap.put(class_1767.field_7944, class_2246.field_10423);
		enumMap.put(class_1767.field_7967, class_2246.field_10222);
		enumMap.put(class_1767.field_7955, class_2246.field_10619);
		enumMap.put(class_1767.field_7945, class_2246.field_10259);
		enumMap.put(class_1767.field_7966, class_2246.field_10514);
		enumMap.put(class_1767.field_7957, class_2246.field_10113);
		enumMap.put(class_1767.field_7942, class_2246.field_10170);
		enumMap.put(class_1767.field_7964, class_2246.field_10314);
		enumMap.put(class_1767.field_7963, class_2246.field_10146);
	});
	private static final Map<class_1767, float[]> field_6867 = Maps.newEnumMap(
		(Map)Arrays.stream(class_1767.values()).collect(Collectors.toMap(arg -> arg, class_1472::method_6630))
	);
	private int field_6865;
	private class_1345 field_6869;

	private static float[] method_6630(class_1767 arg) {
		if (arg == class_1767.field_7952) {
			return new float[]{0.9019608F, 0.9019608F, 0.9019608F};
		} else {
			float[] fs = arg.method_7787();
			float f = 0.75F;
			return new float[]{fs[0] * 0.75F, fs[1] * 0.75F, fs[2] * 0.75F};
		}
	}

	@Environment(EnvType.CLIENT)
	public static float[] method_6634(class_1767 arg) {
		return (float[])field_6867.get(arg);
	}

	public class_1472(class_1299<? extends class_1472> arg, class_1937 arg2) {
		super(arg, arg2);
	}

	@Override
	protected void method_5959() {
		this.field_6869 = new class_1345(this);
		this.field_6201.method_6277(0, new class_1347(this));
		this.field_6201.method_6277(1, new class_1374(this, 1.25));
		this.field_6201.method_6277(2, new class_1341(this, 1.0));
		this.field_6201.method_6277(3, new class_1391(this, 1.1, class_1856.method_8091(class_1802.field_8861), false));
		this.field_6201.method_6277(4, new class_1353(this, 1.1));
		this.field_6201.method_6277(5, this.field_6869);
		this.field_6201.method_6277(6, new class_1394(this, 1.0));
		this.field_6201.method_6277(7, new class_1361(this, class_1657.class, 6.0F));
		this.field_6201.method_6277(8, new class_1376(this));
	}

	@Override
	protected void method_5958() {
		this.field_6865 = this.field_6869.method_6258();
		super.method_5958();
	}

	@Override
	public void method_6007() {
		if (this.field_6002.field_9236) {
			this.field_6865 = Math.max(0, this.field_6865 - 1);
		}

		super.method_6007();
	}

	@Override
	protected void method_6001() {
		super.method_6001();
		this.method_5996(class_1612.field_7359).method_6192(8.0);
		this.method_5996(class_1612.field_7357).method_6192(0.23F);
	}

	@Override
	protected void method_5693() {
		super.method_5693();
		this.field_6011.method_12784(field_6870, (byte)0);
	}

	@Override
	public class_2960 method_5991() {
		if (this.method_6629()) {
			return this.method_5864().method_16351();
		} else {
			switch (this.method_6633()) {
				case field_7952:
				default:
					return class_39.field_869;
				case field_7946:
					return class_39.field_814;
				case field_7958:
					return class_39.field_224;
				case field_7951:
					return class_39.field_461;
				case field_7947:
					return class_39.field_385;
				case field_7961:
					return class_39.field_702;
				case field_7954:
					return class_39.field_629;
				case field_7944:
					return class_39.field_878;
				case field_7967:
					return class_39.field_806;
				case field_7955:
					return class_39.field_365;
				case field_7945:
					return class_39.field_285;
				case field_7966:
					return class_39.field_394;
				case field_7957:
					return class_39.field_489;
				case field_7942:
					return class_39.field_607;
				case field_7964:
					return class_39.field_716;
				case field_7963:
					return class_39.field_778;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5711(byte b) {
		if (b == 10) {
			this.field_6865 = 40;
		} else {
			super.method_5711(b);
		}
	}

	@Environment(EnvType.CLIENT)
	public float method_6628(float f) {
		if (this.field_6865 <= 0) {
			return 0.0F;
		} else if (this.field_6865 >= 4 && this.field_6865 <= 36) {
			return 1.0F;
		} else {
			return this.field_6865 < 4 ? ((float)this.field_6865 - f) / 4.0F : -((float)(this.field_6865 - 40) - f) / 4.0F;
		}
	}

	@Environment(EnvType.CLIENT)
	public float method_6641(float f) {
		if (this.field_6865 > 4 && this.field_6865 <= 36) {
			float g = ((float)(this.field_6865 - 4) - f) / 32.0F;
			return (float) (Math.PI / 5) + 0.21991149F * class_3532.method_15374(g * 28.7F);
		} else {
			return this.field_6865 > 0 ? (float) (Math.PI / 5) : this.field_5965 * (float) (Math.PI / 180.0);
		}
	}

	@Override
	public boolean method_5992(class_1657 arg, class_1268 arg2) {
		class_1799 lv = arg.method_5998(arg2);
		if (lv.method_7909() == class_1802.field_8868 && !this.method_6629() && !this.method_6109()) {
			this.method_6636();
			if (!this.field_6002.field_9236) {
				lv.method_7956(1, arg, arg2x -> arg2x.method_20236(arg2));
			}
		}

		return super.method_5992(arg, arg2);
	}

	public void method_6636() {
		if (!this.field_6002.field_9236) {
			this.method_6635(true);
			int i = 1 + this.field_5974.nextInt(3);

			for (int j = 0; j < i; j++) {
				class_1542 lv = this.method_5870((class_1935)field_6868.get(this.method_6633()), 1);
				if (lv != null) {
					lv.method_18799(
						lv.method_18798()
							.method_1031(
								(double)((this.field_5974.nextFloat() - this.field_5974.nextFloat()) * 0.1F),
								(double)(this.field_5974.nextFloat() * 0.05F),
								(double)((this.field_5974.nextFloat() - this.field_5974.nextFloat()) * 0.1F)
							)
					);
				}
			}
		}

		this.method_5783(class_3417.field_14975, 1.0F, 1.0F);
	}

	@Override
	public void method_5652(class_2487 arg) {
		super.method_5652(arg);
		arg.method_10556("Sheared", this.method_6629());
		arg.method_10567("Color", (byte)this.method_6633().method_7789());
	}

	@Override
	public void method_5749(class_2487 arg) {
		super.method_5749(arg);
		this.method_6635(arg.method_10577("Sheared"));
		this.method_6631(class_1767.method_7791(arg.method_10571("Color")));
	}

	@Override
	protected class_3414 method_5994() {
		return class_3417.field_14603;
	}

	@Override
	protected class_3414 method_6011(class_1282 arg) {
		return class_3417.field_14730;
	}

	@Override
	protected class_3414 method_6002() {
		return class_3417.field_14814;
	}

	@Override
	protected void method_5712(class_2338 arg, class_2680 arg2) {
		this.method_5783(class_3417.field_14870, 0.15F, 1.0F);
	}

	public class_1767 method_6633() {
		return class_1767.method_7791(this.field_6011.method_12789(field_6870) & 15);
	}

	public void method_6631(class_1767 arg) {
		byte b = this.field_6011.method_12789(field_6870);
		this.field_6011.method_12778(field_6870, (byte)(b & 240 | arg.method_7789() & 15));
	}

	public boolean method_6629() {
		return (this.field_6011.method_12789(field_6870) & 16) != 0;
	}

	public void method_6635(boolean bl) {
		byte b = this.field_6011.method_12789(field_6870);
		if (bl) {
			this.field_6011.method_12778(field_6870, (byte)(b | 16));
		} else {
			this.field_6011.method_12778(field_6870, (byte)(b & -17));
		}
	}

	public static class_1767 method_6632(Random random) {
		int i = random.nextInt(100);
		if (i < 5) {
			return class_1767.field_7963;
		} else if (i < 10) {
			return class_1767.field_7944;
		} else if (i < 15) {
			return class_1767.field_7967;
		} else if (i < 18) {
			return class_1767.field_7957;
		} else {
			return random.nextInt(500) == 0 ? class_1767.field_7954 : class_1767.field_7952;
		}
	}

	public class_1472 method_6640(class_1296 arg) {
		class_1472 lv = (class_1472)arg;
		class_1472 lv2 = class_1299.field_6115.method_5883(this.field_6002);
		lv2.method_6631(this.method_6639(this, lv));
		return lv2;
	}

	@Override
	public void method_5983() {
		this.method_6635(false);
		if (this.method_6109()) {
			this.method_5615(60);
		}
	}

	@Nullable
	@Override
	public class_1315 method_5943(class_1936 arg, class_1266 arg2, class_3730 arg3, @Nullable class_1315 arg4, @Nullable class_2487 arg5) {
		arg4 = super.method_5943(arg, arg2, arg3, arg4, arg5);
		this.method_6631(method_6632(arg.method_8409()));
		return arg4;
	}

	private class_1767 method_6639(class_1429 arg, class_1429 arg2) {
		class_1767 lv = ((class_1472)arg).method_6633();
		class_1767 lv2 = ((class_1472)arg2).method_6633();
		class_1715 lv3 = method_17690(lv, lv2);
		return (class_1767)this.field_6002
			.method_8433()
			.method_8132(class_3956.field_17545, lv3, this.field_6002)
			.map(arg2x -> arg2x.method_8116(lv3))
			.map(class_1799::method_7909)
			.filter(class_1769.class::isInstance)
			.map(class_1769.class::cast)
			.map(class_1769::method_7802)
			.orElseGet(() -> this.field_6002.field_9229.nextBoolean() ? lv : lv2);
	}

	private static class_1715 method_17690(class_1767 arg, class_1767 arg2) {
		class_1715 lv = new class_1715(new class_1703(null, -1) {
			@Override
			public boolean method_7597(class_1657 arg) {
				return false;
			}
		}, 2, 1);
		lv.method_5447(0, new class_1799(class_1769.method_7803(arg)));
		lv.method_5447(1, new class_1799(class_1769.method_7803(arg2)));
		return lv;
	}

	@Override
	protected float method_18394(class_4050 arg, class_4048 arg2) {
		return 0.95F * arg2.field_18068;
	}
}
