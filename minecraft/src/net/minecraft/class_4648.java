package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.util.registry.Registry;

public class class_4648<P extends class_4647> {
	public static final class_4648<class_4646> BLOB_FOLIAGE_PLACER = method_23454("blob_foliage_placer", class_4646::new);
	public static final class_4648<class_4650> SPRUCE_FOLIAGE_PLACER = method_23454("spruce_foliage_placer", class_4650::new);
	public static final class_4648<class_4649> PINE_FOLIAGE_PLACER = method_23454("pine_foliage_placer", class_4649::new);
	public static final class_4648<class_4645> ACACIA_FOLIAGE_PLACER = method_23454("acacia_foliage_placer", class_4645::new);
	private final Function<Dynamic<?>, P> field_21303;

	private static <P extends class_4647> class_4648<P> method_23454(String string, Function<Dynamic<?>, P> function) {
		return Registry.register(Registry.FOLIAGE_PLACER_TYPE, string, new class_4648<>(function));
	}

	private class_4648(Function<Dynamic<?>, P> function) {
		this.field_21303 = function;
	}

	public P method_23453(Dynamic<?> dynamic) {
		return (P)this.field_21303.apply(dynamic);
	}
}
