package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_961 extends class_875<class_1496, class_549<class_1496>> {
	private static final Map<Class<?>, class_2960> field_4803 = Maps.<Class<?>, class_2960>newHashMap(
		ImmutableMap.of(
			class_1507.class, new class_2960("textures/entity/horse/horse_zombie.png"), class_1506.class, new class_2960("textures/entity/horse/horse_skeleton.png")
		)
	);

	public class_961(class_898 arg) {
		super(arg, new class_549<>(0.0F), 1.0F);
	}

	protected class_2960 method_4145(class_1496 arg) {
		return (class_2960)field_4803.get(arg.getClass());
	}
}
