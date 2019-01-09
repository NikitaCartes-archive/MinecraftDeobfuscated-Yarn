package net.minecraft;

import com.google.common.base.Predicates;

public class class_2333 extends class_2248 {
	public static final class_2753 field_10954 = class_2383.field_11177;
	public static final class_2746 field_10958 = class_2741.field_12488;
	protected static final class_265 field_10956 = class_2248.method_9541(0.0, 0.0, 0.0, 16.0, 13.0, 16.0);
	protected static final class_265 field_10953 = class_2248.method_9541(4.0, 13.0, 4.0, 12.0, 16.0, 12.0);
	protected static final class_265 field_10955 = class_259.method_1084(field_10956, field_10953);
	private static class_2700 field_10957;

	public class_2333(class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(this.field_10647.method_11664().method_11657(field_10954, class_2350.field_11043).method_11657(field_10958, Boolean.valueOf(false)));
	}

	@Override
	public boolean method_9526(class_2680 arg) {
		return true;
	}

	@Override
	public class_265 method_9529(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return arg.method_11654(field_10958) ? field_10955 : field_10956;
	}

	@Override
	public class_2680 method_9605(class_1750 arg) {
		return this.method_9564().method_11657(field_10954, arg.method_8042().method_10153()).method_11657(field_10958, Boolean.valueOf(false));
	}

	@Override
	public boolean method_9498(class_2680 arg) {
		return true;
	}

	@Override
	public int method_9572(class_2680 arg, class_1937 arg2, class_2338 arg3) {
		return arg.method_11654(field_10958) ? 15 : 0;
	}

	@Override
	public class_2680 method_9598(class_2680 arg, class_2470 arg2) {
		return arg.method_11657(field_10954, arg2.method_10503(arg.method_11654(field_10954)));
	}

	@Override
	public class_2680 method_9569(class_2680 arg, class_2415 arg2) {
		return arg.method_11626(arg2.method_10345(arg.method_11654(field_10954)));
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_10954, field_10958);
	}

	public static class_2700 method_10054() {
		if (field_10957 == null) {
			field_10957 = class_2697.method_11701()
				.method_11702("?vvv?", ">???<", ">???<", ">???<", "?^^^?")
				.method_11700('?', class_2694.method_11678(class_2715.field_12419))
				.method_11700(
					'^',
					class_2694.method_11678(
						class_2715.method_11758(class_2246.field_10398)
							.method_11762(field_10958, Predicates.equalTo(true))
							.method_11762(field_10954, Predicates.equalTo(class_2350.field_11035))
					)
				)
				.method_11700(
					'>',
					class_2694.method_11678(
						class_2715.method_11758(class_2246.field_10398)
							.method_11762(field_10958, Predicates.equalTo(true))
							.method_11762(field_10954, Predicates.equalTo(class_2350.field_11039))
					)
				)
				.method_11700(
					'v',
					class_2694.method_11678(
						class_2715.method_11758(class_2246.field_10398)
							.method_11762(field_10958, Predicates.equalTo(true))
							.method_11762(field_10954, Predicates.equalTo(class_2350.field_11043))
					)
				)
				.method_11700(
					'<',
					class_2694.method_11678(
						class_2715.method_11758(class_2246.field_10398)
							.method_11762(field_10958, Predicates.equalTo(true))
							.method_11762(field_10954, Predicates.equalTo(class_2350.field_11034))
					)
				)
				.method_11704();
		}

		return field_10957;
	}

	@Override
	public boolean method_9516(class_2680 arg, class_1922 arg2, class_2338 arg3, class_10 arg4) {
		return false;
	}
}
