package net.minecraft;

import java.util.Locale;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1474 extends class_1425 {
	private static final class_2940<Integer> field_6874 = class_2945.method_12791(class_1474.class, class_2943.field_13327);
	private static final class_2960[] field_6875 = new class_2960[]{
		new class_2960("textures/entity/fish/tropical_a.png"), new class_2960("textures/entity/fish/tropical_b.png")
	};
	private static final class_2960[] field_6878 = new class_2960[]{
		new class_2960("textures/entity/fish/tropical_a_pattern_1.png"),
		new class_2960("textures/entity/fish/tropical_a_pattern_2.png"),
		new class_2960("textures/entity/fish/tropical_a_pattern_3.png"),
		new class_2960("textures/entity/fish/tropical_a_pattern_4.png"),
		new class_2960("textures/entity/fish/tropical_a_pattern_5.png"),
		new class_2960("textures/entity/fish/tropical_a_pattern_6.png")
	};
	private static final class_2960[] field_6876 = new class_2960[]{
		new class_2960("textures/entity/fish/tropical_b_pattern_1.png"),
		new class_2960("textures/entity/fish/tropical_b_pattern_2.png"),
		new class_2960("textures/entity/fish/tropical_b_pattern_3.png"),
		new class_2960("textures/entity/fish/tropical_b_pattern_4.png"),
		new class_2960("textures/entity/fish/tropical_b_pattern_5.png"),
		new class_2960("textures/entity/fish/tropical_b_pattern_6.png")
	};
	public static final int[] field_6879 = new int[]{
		method_6647(class_1474.class_1475.field_6887, class_1767.field_7946, class_1767.field_7944),
		method_6647(class_1474.class_1475.field_6893, class_1767.field_7944, class_1767.field_7944),
		method_6647(class_1474.class_1475.field_6893, class_1767.field_7944, class_1767.field_7966),
		method_6647(class_1474.class_1475.field_6889, class_1767.field_7952, class_1767.field_7944),
		method_6647(class_1474.class_1475.field_6880, class_1767.field_7966, class_1767.field_7944),
		method_6647(class_1474.class_1475.field_6881, class_1767.field_7946, class_1767.field_7952),
		method_6647(class_1474.class_1475.field_6892, class_1767.field_7954, class_1767.field_7951),
		method_6647(class_1474.class_1475.field_6884, class_1767.field_7945, class_1767.field_7947),
		method_6647(class_1474.class_1475.field_6889, class_1767.field_7952, class_1767.field_7964),
		method_6647(class_1474.class_1475.field_6892, class_1767.field_7952, class_1767.field_7947),
		method_6647(class_1474.class_1475.field_6883, class_1767.field_7952, class_1767.field_7944),
		method_6647(class_1474.class_1475.field_6889, class_1767.field_7952, class_1767.field_7946),
		method_6647(class_1474.class_1475.field_6890, class_1767.field_7955, class_1767.field_7954),
		method_6647(class_1474.class_1475.field_6891, class_1767.field_7961, class_1767.field_7951),
		method_6647(class_1474.class_1475.field_6888, class_1767.field_7964, class_1767.field_7952),
		method_6647(class_1474.class_1475.field_6882, class_1767.field_7944, class_1767.field_7964),
		method_6647(class_1474.class_1475.field_6884, class_1767.field_7964, class_1767.field_7952),
		method_6647(class_1474.class_1475.field_6893, class_1767.field_7952, class_1767.field_7947),
		method_6647(class_1474.class_1475.field_6881, class_1767.field_7964, class_1767.field_7952),
		method_6647(class_1474.class_1475.field_6880, class_1767.field_7944, class_1767.field_7952),
		method_6647(class_1474.class_1475.field_6890, class_1767.field_7955, class_1767.field_7947),
		method_6647(class_1474.class_1475.field_6893, class_1767.field_7947, class_1767.field_7947)
	};
	private boolean field_6877 = true;

	private static int method_6647(class_1474.class_1475 arg, class_1767 arg2, class_1767 arg3) {
		return arg.method_6662() & 0xFF | (arg.method_6663() & 0xFF) << 8 | (arg2.method_7789() & 0xFF) << 16 | (arg3.method_7789() & 0xFF) << 24;
	}

	public class_1474(class_1937 arg) {
		super(class_1299.field_6111, arg);
		this.method_5835(0.5F, 0.4F);
	}

	@Environment(EnvType.CLIENT)
	public static String method_6649(int i) {
		return "entity.minecraft.tropical_fish.predefined." + i;
	}

	@Environment(EnvType.CLIENT)
	public static class_1767 method_6652(int i) {
		return class_1767.method_7791(method_6653(i));
	}

	@Environment(EnvType.CLIENT)
	public static class_1767 method_6651(int i) {
		return class_1767.method_7791(method_6648(i));
	}

	@Environment(EnvType.CLIENT)
	public static String method_6657(int i) {
		int j = method_6656(i);
		int k = method_6645(i);
		return "entity.minecraft.tropical_fish.type." + class_1474.class_1475.method_6660(j, k);
	}

	@Override
	protected void method_5693() {
		super.method_5693();
		this.field_6011.method_12784(field_6874, 0);
	}

	@Override
	public void method_5652(class_2487 arg) {
		super.method_5652(arg);
		arg.method_10569("Variant", this.method_6644());
	}

	@Override
	public void method_5749(class_2487 arg) {
		super.method_5749(arg);
		this.method_6659(arg.method_10550("Variant"));
	}

	public void method_6659(int i) {
		this.field_6011.method_12778(field_6874, i);
	}

	@Override
	public boolean method_5969(int i) {
		return !this.field_6877;
	}

	public int method_6644() {
		return this.field_6011.method_12789(field_6874);
	}

	@Override
	protected void method_6455(class_1799 arg) {
		super.method_6455(arg);
		class_2487 lv = arg.method_7948();
		lv.method_10569("BucketVariantTag", this.method_6644());
	}

	@Override
	protected class_1799 method_6452() {
		return new class_1799(class_1802.field_8478);
	}

	@Override
	protected class_3414 method_5994() {
		return class_3417.field_15085;
	}

	@Override
	protected class_3414 method_6002() {
		return class_3417.field_15201;
	}

	@Override
	protected class_3414 method_6011(class_1282 arg) {
		return class_3417.field_14985;
	}

	@Override
	protected class_3414 method_6457() {
		return class_3417.field_14878;
	}

	@Environment(EnvType.CLIENT)
	private static int method_6653(int i) {
		return (i & 0xFF0000) >> 16;
	}

	@Environment(EnvType.CLIENT)
	public float[] method_6658() {
		return class_1767.method_7791(method_6653(this.method_6644())).method_7787();
	}

	@Environment(EnvType.CLIENT)
	private static int method_6648(int i) {
		return (i & 0xFF000000) >> 24;
	}

	@Environment(EnvType.CLIENT)
	public float[] method_6655() {
		return class_1767.method_7791(method_6648(this.method_6644())).method_7787();
	}

	@Environment(EnvType.CLIENT)
	public static int method_6656(int i) {
		return Math.min(i & 0xFF, 1);
	}

	@Environment(EnvType.CLIENT)
	public int method_6654() {
		return method_6656(this.method_6644());
	}

	@Environment(EnvType.CLIENT)
	private static int method_6645(int i) {
		return Math.min((i & 0xFF00) >> 8, 5);
	}

	@Environment(EnvType.CLIENT)
	public class_2960 method_6646() {
		return method_6656(this.method_6644()) == 0 ? field_6878[method_6645(this.method_6644())] : field_6876[method_6645(this.method_6644())];
	}

	@Environment(EnvType.CLIENT)
	public class_2960 method_6650() {
		return field_6875[method_6656(this.method_6644())];
	}

	@Nullable
	@Override
	public class_1315 method_5943(class_1936 arg, class_1266 arg2, class_3730 arg3, @Nullable class_1315 arg4, @Nullable class_2487 arg5) {
		arg4 = super.method_5943(arg, arg2, arg3, arg4, arg5);
		if (arg5 != null && arg5.method_10573("BucketVariantTag", 3)) {
			this.method_6659(arg5.method_10550("BucketVariantTag"));
			return arg4;
		} else {
			int i;
			int j;
			int k;
			int l;
			if (arg4 instanceof class_1474.class_1476) {
				class_1474.class_1476 lv = (class_1474.class_1476)arg4;
				i = lv.field_6899;
				j = lv.field_6898;
				k = lv.field_6897;
				l = lv.field_6896;
			} else if ((double)this.field_5974.nextFloat() < 0.9) {
				int m = field_6879[this.field_5974.nextInt(field_6879.length)];
				i = m & 0xFF;
				j = (m & 0xFF00) >> 8;
				k = (m & 0xFF0000) >> 16;
				l = (m & 0xFF000000) >> 24;
				arg4 = new class_1474.class_1476(this, i, j, k, l);
			} else {
				this.field_6877 = false;
				i = this.field_5974.nextInt(2);
				j = this.field_5974.nextInt(6);
				k = this.field_5974.nextInt(15);
				l = this.field_5974.nextInt(15);
			}

			this.method_6659(i | j << 8 | k << 16 | l << 24);
			return arg4;
		}
	}

	static enum class_1475 {
		field_6881(0, 0),
		field_6880(0, 1),
		field_6882(0, 2),
		field_6890(0, 3),
		field_6891(0, 4),
		field_6892(0, 5),
		field_6893(1, 0),
		field_6887(1, 1),
		field_6883(1, 2),
		field_6884(1, 3),
		field_6888(1, 4),
		field_6889(1, 5);

		private final int field_6895;
		private final int field_6894;
		private static final class_1474.class_1475[] field_6885 = values();

		private class_1475(int j, int k) {
			this.field_6895 = j;
			this.field_6894 = k;
		}

		public int method_6662() {
			return this.field_6895;
		}

		public int method_6663() {
			return this.field_6894;
		}

		@Environment(EnvType.CLIENT)
		public static String method_6660(int i, int j) {
			return field_6885[j + 6 * i].method_6661();
		}

		@Environment(EnvType.CLIENT)
		public String method_6661() {
			return this.name().toLowerCase(Locale.ROOT);
		}
	}

	static class class_1476 extends class_1425.class_1426 {
		private final int field_6899;
		private final int field_6898;
		private final int field_6897;
		private final int field_6896;

		private class_1476(class_1474 arg, int i, int j, int k, int l) {
			super(arg);
			this.field_6899 = i;
			this.field_6898 = j;
			this.field_6897 = k;
			this.field_6896 = l;
		}
	}
}
