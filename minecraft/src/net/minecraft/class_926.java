package net.minecraft;

import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_926 extends class_927<class_1438, class_560<class_1438>> {
	private static final Map<class_1438.class_4053, class_2960> field_4748 = class_156.method_654(
		Maps.<class_1438.class_4053, class_2960>newHashMap(), hashMap -> {
			hashMap.put(class_1438.class_4053.field_18110, new class_2960("textures/entity/cow/brown_mooshroom.png"));
			hashMap.put(class_1438.class_4053.field_18109, new class_2960("textures/entity/cow/red_mooshroom.png"));
		}
	);

	public class_926(class_898 arg) {
		super(arg, new class_560<>(), 0.7F);
		this.method_4046(new class_991<>(this));
	}

	protected class_2960 method_4066(class_1438 arg) {
		return (class_2960)field_4748.get(arg.method_18435());
	}
}
