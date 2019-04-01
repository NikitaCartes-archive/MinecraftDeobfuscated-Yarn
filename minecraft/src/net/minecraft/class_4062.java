package net.minecraft;

import java.util.function.BiConsumer;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_4062 extends class_316 {
	private final Predicate<class_315> field_18158;
	private final BiConsumer<class_315, Boolean> field_18159;

	public class_4062(String string, Predicate<class_315> predicate, BiConsumer<class_315, Boolean> biConsumer) {
		super(string);
		this.field_18158 = predicate;
		this.field_18159 = biConsumer;
	}

	public void method_18492(class_315 arg, String string) {
		this.method_18493(arg, "true".equals(string));
	}

	public void method_18491(class_315 arg) {
		this.method_18493(arg, !this.method_18494(arg));
		arg.method_1640();
	}

	private void method_18493(class_315 arg, boolean bl) {
		this.field_18159.accept(arg, bl);
	}

	public boolean method_18494(class_315 arg) {
		return this.field_18158.test(arg);
	}

	@Override
	public class_339 method_18520(class_315 arg, int i, int j, int k) {
		return new 1(i, j, k, 20, this, this.method_18495(arg), arg2 -> {
			this.method_18491(arg);
			arg2.setMessage(this.method_18495(arg));
		});
	}

	public String method_18495(class_315 arg) {
		return this.method_18518() + class_1074.method_4662(this.method_18494(arg) ? "options.on" : "options.off");
	}
}
