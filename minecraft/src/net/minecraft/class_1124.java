package net.minecraft;

import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_1124 implements class_3302 {
	public static final class_1124.class_1125<class_1799> field_5495 = new class_1124.class_1125<>();
	public static final class_1124.class_1125<class_1799> field_5494 = new class_1124.class_1125<>();
	public static final class_1124.class_1125<class_516> field_5496 = new class_1124.class_1125<>();
	private final Map<class_1124.class_1125<?>, class_1123<?>> field_5493 = Maps.<class_1124.class_1125<?>, class_1123<?>>newHashMap();

	@Override
	public void method_14491(class_3300 arg) {
		for (class_1123<?> lv : this.field_5493.values()) {
			lv.method_4799();
		}
	}

	public <T> void method_4801(class_1124.class_1125<T> arg, class_1123<T> arg2) {
		this.field_5493.put(arg, arg2);
	}

	public <T> class_1123<T> method_4800(class_1124.class_1125<T> arg) {
		return (class_1123<T>)this.field_5493.get(arg);
	}

	@Environment(EnvType.CLIENT)
	public static class class_1125<T> {
	}
}
