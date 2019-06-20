package net.minecraft;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.MinecraftServer;

public class class_4114 extends class_4097<class_1646> {
	public class_4114() {
		super(ImmutableMap.of(class_4140.field_18439, class_4141.field_18456));
	}

	protected boolean method_18987(class_3218 arg, class_1646 arg2) {
		return arg2.method_7231().method_16924() == class_3852.field_17051;
	}

	protected void method_18988(class_3218 arg, class_1646 arg2, long l) {
		class_4208 lv = (class_4208)arg2.method_18868().method_18904(class_4140.field_18439).get();
		MinecraftServer minecraftServer = arg.method_8503();
		minecraftServer.method_3847(lv.method_19442())
			.method_19494()
			.method_19132(lv.method_19446())
			.ifPresent(arg3 -> class_2378.field_17167.method_10220().filter(arg2xx -> arg2xx.method_19198() == arg3).findFirst().ifPresent(arg3x -> {
					arg2.method_7221(arg2.method_7231().method_16921(arg3x));
					arg2.method_19179(arg);
				}));
	}
}
