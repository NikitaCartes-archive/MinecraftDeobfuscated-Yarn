package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.util.registry.Registry;

public class class_4630<P extends class_4629> {
	public static final class_4630<class_4633> SIMPLE_BLOCK_PLACER = method_23405("simple_block_placer", class_4633::new);
	public static final class_4630<class_4632> DOUBLE_PLANT_PLACER = method_23405("double_plant_placer", class_4632::new);
	public static final class_4630<class_4631> COLUMN_PLACER = method_23405("column_placer", class_4631::new);
	private final Function<Dynamic<?>, P> field_21226;

	private static <P extends class_4629> class_4630<P> method_23405(String string, Function<Dynamic<?>, P> function) {
		return Registry.register(Registry.BLOCK_PLACER_TYPE, string, new class_4630<>(function));
	}

	private class_4630(Function<Dynamic<?>, P> function) {
		this.field_21226 = function;
	}

	public P method_23404(Dynamic<?> dynamic) {
		return (P)this.field_21226.apply(dynamic);
	}
}
