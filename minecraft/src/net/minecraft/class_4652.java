package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.util.registry.Registry;

public class class_4652<P extends class_4651> {
	public static final class_4652<class_4656> SIMPLE_STATE_PROVIDER = method_23457("simple_state_provider", class_4656::new);
	public static final class_4652<class_4657> WEIGHTED_STATE_PROVIDER = method_23457("weighted_state_provider", class_4657::new);
	public static final class_4652<class_4654> PLAIN_FLOWER_PROVIDER = method_23457("plain_flower_provider", class_4654::new);
	public static final class_4652<class_4653> FOREST_FLOWER_PROVIDER = method_23457("forest_flower_provider", class_4653::new);
	private final Function<Dynamic<?>, P> field_21309;

	private static <P extends class_4651> class_4652<P> method_23457(String string, Function<Dynamic<?>, P> function) {
		return Registry.register(Registry.BLOCK_STATE_PROVIDER_TYPE, string, new class_4652<>(function));
	}

	private class_4652(Function<Dynamic<?>, P> function) {
		this.field_21309 = function;
	}

	public P method_23456(Dynamic<?> dynamic) {
		return (P)this.field_21309.apply(dynamic);
	}
}
