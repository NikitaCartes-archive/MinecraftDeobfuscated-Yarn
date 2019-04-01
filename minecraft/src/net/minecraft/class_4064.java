package net.minecraft;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_4064 extends class_316 {
	private final BiConsumer<class_315, Integer> field_18169;
	private final BiFunction<class_315, class_4064, String> field_18170;

	public class_4064(String string, BiConsumer<class_315, Integer> biConsumer, BiFunction<class_315, class_4064, String> biFunction) {
		super(string);
		this.field_18169 = biConsumer;
		this.field_18170 = biFunction;
	}

	public void method_18500(class_315 arg, int i) {
		this.field_18169.accept(arg, i);
		arg.method_1640();
	}

	@Override
	public class_339 method_18520(class_315 arg, int i, int j, int k) {
		return new 1(i, j, k, 20, this, this.method_18501(arg), arg2 -> {
			this.method_18500(arg, 1);
			arg2.setMessage(this.method_18501(arg));
		});
	}

	public String method_18501(class_315 arg) {
		return (String)this.field_18170.apply(arg, this);
	}
}
