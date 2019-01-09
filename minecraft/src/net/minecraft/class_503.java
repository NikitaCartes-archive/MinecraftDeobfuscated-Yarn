package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_503 extends class_350<class_503.class_504> {
	private final class_500 field_3075;
	private final List<class_501> field_3074 = Lists.<class_501>newArrayList();
	private final class_503.class_504 field_3076 = new class_499();
	private final List<class_502> field_3077 = Lists.<class_502>newArrayList();
	private int field_3078 = -1;

	private void method_2560() {
		this.method_1902();
		this.field_3074.forEach(this::method_1901);
		this.method_1901(this.field_3076);
		this.field_3077.forEach(this::method_1901);
	}

	public class_503(class_500 arg, class_310 arg2, int i, int j, int k, int l, int m) {
		super(arg2, i, j, k, l, m);
		this.field_3075 = arg;
	}

	public void method_2561(int i) {
		this.field_3078 = i;
	}

	@Override
	protected boolean method_1955(int i) {
		return i == this.field_3078;
	}

	public int method_2563() {
		return this.field_3078;
	}

	public void method_2564(class_641 arg) {
		this.field_3074.clear();

		for (int i = 0; i < arg.method_2984(); i++) {
			this.field_3074.add(new class_501(this.field_3075, arg.method_2982(i)));
		}

		this.method_2560();
	}

	public void method_2562(List<class_1131> list) {
		this.field_3077.clear();

		for (class_1131 lv : list) {
			this.field_3077.add(new class_502(this.field_3075, lv));
		}

		this.method_2560();
	}

	@Override
	protected int method_1948() {
		return super.method_1948() + 30;
	}

	@Override
	public int method_1932() {
		return super.method_1932() + 85;
	}

	@Environment(EnvType.CLIENT)
	public abstract static class class_504 extends class_350.class_351<class_503.class_504> {
	}
}
