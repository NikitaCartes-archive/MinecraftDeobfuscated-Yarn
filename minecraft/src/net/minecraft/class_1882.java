package net.minecraft;

public class class_1882 extends class_1887 {
	private static final String[] field_9065 = new String[]{"all", "undead", "arthropods"};
	private static final int[] field_9063 = new int[]{1, 5, 5};
	private static final int[] field_9066 = new int[]{11, 8, 8};
	private static final int[] field_9064 = new int[]{20, 20, 20};
	public final int field_9067;

	public class_1882(class_1887.class_1888 arg, int i, class_1304... args) {
		super(arg, class_1886.field_9074, args);
		this.field_9067 = i;
	}

	@Override
	public int method_8182(int i) {
		return field_9063[this.field_9067] + (i - 1) * field_9066[this.field_9067];
	}

	@Override
	public int method_8183() {
		return 5;
	}

	@Override
	public float method_8196(int i, class_1310 arg) {
		if (this.field_9067 == 0) {
			return 1.0F + (float)Math.max(0, i - 1) * 0.5F;
		} else if (this.field_9067 == 1 && arg == class_1310.field_6289) {
			return (float)i * 2.5F;
		} else {
			return this.field_9067 == 2 && arg == class_1310.field_6293 ? (float)i * 2.5F : 0.0F;
		}
	}

	@Override
	public boolean method_8180(class_1887 arg) {
		return !(arg instanceof class_1882);
	}

	@Override
	public boolean method_8192(class_1799 arg) {
		return arg.method_7909() instanceof class_1743 ? true : super.method_8192(arg);
	}

	@Override
	public void method_8189(class_1309 arg, class_1297 arg2, int i) {
		if (arg2 instanceof class_1309) {
			class_1309 lv = (class_1309)arg2;
			if (this.field_9067 == 2 && lv.method_6046() == class_1310.field_6293) {
				int j = 20 + arg.method_6051().nextInt(10 * i);
				lv.method_6092(new class_1293(class_1294.field_5909, j, 3));
			}
		}
	}
}
