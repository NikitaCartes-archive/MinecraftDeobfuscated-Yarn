package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.util.registry.Registry;

public class class_5202<P extends class_5201> {
	public static final class_5202<class_5204> field_24147 = method_27382("two_layers_feature_size", class_5204::new);
	public static final class_5202<class_5203> field_24148 = method_27382("three_layers_feature_size", class_5203::new);
	private final Function<Dynamic<?>, P> field_24149;

	private static <P extends class_5201> class_5202<P> method_27382(String string, Function<Dynamic<?>, P> function) {
		return Registry.register(Registry.FEATURE_SIZE_TYPE, string, new class_5202<>(function));
	}

	private class_5202(Function<Dynamic<?>, P> function) {
		this.field_24149 = function;
	}

	public P method_27381(Dynamic<?> dynamic) {
		return (P)this.field_24149.apply(dynamic);
	}
}
