package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Objects;

public final class class_4208 implements class_4213 {
	private final class_2874 field_18790;
	private final class_2338 field_18791;

	private class_4208(class_2874 arg, class_2338 arg2) {
		this.field_18790 = arg;
		this.field_18791 = arg2;
	}

	public static class_4208 method_19443(class_2874 arg, class_2338 arg2) {
		return new class_4208(arg, arg2);
	}

	public static class_4208 method_19444(Dynamic<?> dynamic) {
		return (class_4208)dynamic.get("dimension")
			.map(class_2874::method_19298)
			.flatMap(arg -> dynamic.get("pos").map(class_2338::method_19438).map(arg2 -> new class_4208(arg, arg2)))
			.orElseThrow(() -> new IllegalArgumentException("Could not parse GlobalPos"));
	}

	public class_2874 method_19442() {
		return this.field_18790;
	}

	public class_2338 method_19446() {
		return this.field_18791;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			class_4208 lv = (class_4208)object;
			return Objects.equals(this.field_18790, lv.field_18790) && Objects.equals(this.field_18791, lv.field_18791);
		} else {
			return false;
		}
	}

	public int hashCode() {
		return Objects.hash(new Object[]{this.field_18790, this.field_18791});
	}

	@Override
	public <T> T method_19508(DynamicOps<T> dynamicOps) {
		return dynamicOps.createMap(
			ImmutableMap.of(
				dynamicOps.createString("dimension"), this.field_18790.method_19508(dynamicOps), dynamicOps.createString("pos"), this.field_18791.method_19508(dynamicOps)
			)
		);
	}

	public String toString() {
		return this.field_18790.toString() + " " + this.field_18791;
	}
}
