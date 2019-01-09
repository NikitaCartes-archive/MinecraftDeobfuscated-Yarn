package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_883<T extends class_1492> extends class_875<T, class_559<T>> {
	private static final Map<Class<?>, class_2960> field_4650 = Maps.<Class<?>, class_2960>newHashMap(
		ImmutableMap.of(class_1495.class, new class_2960("textures/entity/horse/donkey.png"), class_1500.class, new class_2960("textures/entity/horse/mule.png"))
	);

	public class_883(class_898 arg, float f) {
		super(arg, new class_559<>(), f);
	}

	protected class_2960 method_3894(T arg) {
		return (class_2960)field_4650.get(arg.getClass());
	}
}
