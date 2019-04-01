package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Objects;

public class class_4156 implements class_4213 {
	private final class_2338 field_18492;
	private final class_4158 field_18493;
	private int field_18494;
	private final Runnable field_18495;

	private class_4156(class_2338 arg, class_4158 arg2, int i, Runnable runnable) {
		this.field_18492 = arg;
		this.field_18493 = arg2;
		this.field_18494 = i;
		this.field_18495 = runnable;
	}

	public class_4156(class_2338 arg, class_4158 arg2, Runnable runnable) {
		this(arg, arg2, arg2.method_19161(), runnable);
	}

	public <T> class_4156(Dynamic<T> dynamic, Runnable runnable) {
		this(
			(class_2338)dynamic.get("pos").map(class_2338::method_19438).orElse(new class_2338(0, 0, 0)),
			class_2378.field_18792.method_10223(new class_2960(dynamic.get("type").asString(""))),
			dynamic.get("free_tickets").asInt(0),
			runnable
		);
	}

	@Override
	public <T> T method_19508(DynamicOps<T> dynamicOps) {
		return dynamicOps.createMap(
			ImmutableMap.of(
				dynamicOps.createString("pos"),
				this.field_18492.method_19508(dynamicOps),
				dynamicOps.createString("type"),
				dynamicOps.createString(class_2378.field_18792.method_10221(this.field_18493).toString()),
				dynamicOps.createString("free_tickets"),
				dynamicOps.createInt(this.field_18494)
			)
		);
	}

	protected boolean method_19137() {
		if (this.field_18494 <= 0) {
			return false;
		} else {
			this.field_18494--;
			this.field_18495.run();
			return true;
		}
	}

	protected boolean method_19138() {
		if (this.field_18494 >= this.field_18493.method_19161()) {
			return false;
		} else {
			this.field_18494++;
			this.field_18495.run();
			return true;
		}
	}

	public boolean method_19139() {
		return this.field_18494 > 0;
	}

	public boolean method_19140() {
		return this.field_18494 != this.field_18493.method_19161();
	}

	public class_2338 method_19141() {
		return this.field_18492;
	}

	public class_4158 method_19142() {
		return this.field_18493;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else {
			return object != null && this.getClass() == object.getClass() ? Objects.equals(this.field_18492, ((class_4156)object).field_18492) : false;
		}
	}

	public int hashCode() {
		return this.field_18492.hashCode();
	}
}
