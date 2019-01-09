package net.minecraft;

public class class_1783 extends class_1789 {
	private final boolean field_7980;
	private final class_1783.class_1784 field_7979;

	public class_1783(class_1783.class_1784 arg, boolean bl, class_1792.class_1793 arg2) {
		super(0, 0.0F, false, arg2);
		this.field_7979 = arg;
		this.field_7980 = bl;
	}

	@Override
	public int method_7832(class_1799 arg) {
		class_1783.class_1784 lv = class_1783.class_1784.method_7821(arg);
		return this.field_7980 && lv.method_7819() ? lv.method_7818() : lv.method_7820();
	}

	@Override
	public float method_7827(class_1799 arg) {
		return this.field_7980 && this.field_7979.method_7819() ? this.field_7979.method_7823() : this.field_7979.method_7822();
	}

	@Override
	protected void method_7831(class_1799 arg, class_1937 arg2, class_1657 arg3) {
		class_1783.class_1784 lv = class_1783.class_1784.method_7821(arg);
		if (lv == class_1783.class_1784.field_7984) {
			arg3.method_6092(new class_1293(class_1294.field_5899, 1200, 3));
			arg3.method_6092(new class_1293(class_1294.field_5903, 300, 2));
			arg3.method_6092(new class_1293(class_1294.field_5916, 300, 1));
		}

		super.method_7831(arg, arg2, arg3);
	}

	public static enum class_1784 {
		field_7989(2, 0.1F, 5, 0.6F),
		field_7986(2, 0.1F, 6, 0.8F),
		field_7981(1, 0.1F),
		field_7984(1, 0.1F);

		private final int field_7987;
		private final float field_7982;
		private final int field_7985;
		private final float field_7990;
		private final boolean field_7983;

		private class_1784(int j, float f, int k, float g) {
			this.field_7987 = j;
			this.field_7982 = f;
			this.field_7985 = k;
			this.field_7990 = g;
			this.field_7983 = k != 0;
		}

		private class_1784(int j, float f) {
			this(j, f, 0, 0.0F);
		}

		public int method_7820() {
			return this.field_7987;
		}

		public float method_7822() {
			return this.field_7982;
		}

		public int method_7818() {
			return this.field_7985;
		}

		public float method_7823() {
			return this.field_7990;
		}

		public boolean method_7819() {
			return this.field_7983;
		}

		public static class_1783.class_1784 method_7821(class_1799 arg) {
			class_1792 lv = arg.method_7909();
			return lv instanceof class_1783 ? ((class_1783)lv).field_7979 : field_7989;
		}
	}
}
