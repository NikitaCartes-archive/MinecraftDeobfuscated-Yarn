package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import java.io.IOException;
import java.util.Map;
import java.util.function.Function;
import javax.annotation.Nullable;

public class class_37 {
	private final Map<class_2874, class_26> field_218;
	@Nullable
	private final class_30 field_217;

	public class_37(@Nullable class_30 arg) {
		this.field_217 = arg;
		Builder<class_2874, class_26> builder = ImmutableMap.builder();

		for (class_2874 lv : class_2874.method_12482()) {
			class_26 lv2 = new class_26(lv, arg);
			builder.put(lv, lv2);
			lv2.method_122();
		}

		this.field_218 = builder.build();
	}

	@Nullable
	public <T extends class_18> T method_268(class_2874 arg, Function<String, T> function, String string) {
		return ((class_26)this.field_218.get(arg)).method_120(function, string);
	}

	public void method_267(class_2874 arg, String string, class_18 arg2) {
		((class_26)this.field_218.get(arg)).method_123(string, arg2);
	}

	public void method_265() {
		this.field_218.values().forEach(class_26::method_125);
	}

	public int method_266(class_2874 arg, String string) {
		return ((class_26)this.field_218.get(arg)).method_124(string);
	}

	public class_2487 method_264(String string, int i) throws IOException {
		return class_26.method_119(this.field_217, class_2874.field_13072, string, i);
	}
}
