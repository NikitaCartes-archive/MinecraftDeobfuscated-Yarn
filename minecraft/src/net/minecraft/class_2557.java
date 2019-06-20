package net.minecraft;

public class class_2557 extends class_2231 {
	public static final class_2758 field_11739 = class_2741.field_12511;
	private final int field_11738;

	protected class_2557(int i, class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(this.field_10647.method_11664().method_11657(field_11739, Integer.valueOf(0)));
		this.field_11738 = i;
	}

	@Override
	protected int method_9434(class_1937 arg, class_2338 arg2) {
		int i = Math.min(arg.method_18467(class_1297.class, field_9941.method_996(arg2)).size(), this.field_11738);
		if (i > 0) {
			float f = (float)Math.min(this.field_11738, i) / (float)this.field_11738;
			return class_3532.method_15386(f * 15.0F);
		} else {
			return 0;
		}
	}

	@Override
	protected void method_9436(class_1936 arg, class_2338 arg2) {
		arg.method_8396(null, arg2, class_3417.field_14988, class_3419.field_15245, 0.3F, 0.90000004F);
	}

	@Override
	protected void method_9438(class_1936 arg, class_2338 arg2) {
		arg.method_8396(null, arg2, class_3417.field_15100, class_3419.field_15245, 0.3F, 0.75F);
	}

	@Override
	protected int method_9435(class_2680 arg) {
		return (Integer)arg.method_11654(field_11739);
	}

	@Override
	protected class_2680 method_9432(class_2680 arg, int i) {
		return arg.method_11657(field_11739, Integer.valueOf(i));
	}

	@Override
	public int method_9563(class_1941 arg) {
		return 10;
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_11739);
	}
}
