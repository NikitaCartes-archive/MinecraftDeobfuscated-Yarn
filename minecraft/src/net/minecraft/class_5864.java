package net.minecraft;

import com.mojang.serialization.Codec;
import net.minecraft.util.registry.Registry;

public interface class_5864<P extends class_5863> {
	class_5864<class_5862> field_29008 = method_33925("constant", class_5862.field_29004);
	class_5864<class_5866> field_29009 = method_33925("uniform", class_5866.field_29016);
	class_5864<class_5861> field_29010 = method_33925("clamped_normal", class_5861.field_28998);
	class_5864<class_5865> field_29011 = method_33925("trapezoid", class_5865.field_29012);

	Codec<P> codec();

	static <P extends class_5863> class_5864<P> method_33925(String string, Codec<P> codec) {
		return Registry.register(Registry.field_29076, string, () -> codec);
	}
}
