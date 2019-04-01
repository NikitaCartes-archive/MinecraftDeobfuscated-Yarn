package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class class_3780 {
	private final int field_16670;
	private final int field_16669;
	private final int field_16668;
	private final int field_16667;
	private final class_3785.class_3786 field_16671;

	public class_3780(int i, int j, int k, int l, class_3785.class_3786 arg) {
		this.field_16670 = i;
		this.field_16669 = j;
		this.field_16668 = k;
		this.field_16667 = l;
		this.field_16671 = arg;
	}

	public int method_16610() {
		return this.field_16670;
	}

	public int method_16611() {
		return this.field_16669;
	}

	public int method_16609() {
		return this.field_16668;
	}

	public <T> Dynamic<T> method_16612(DynamicOps<T> dynamicOps) {
		Builder<T, T> builder = ImmutableMap.builder();
		builder.put(dynamicOps.createString("source_x"), dynamicOps.createInt(this.field_16670))
			.put(dynamicOps.createString("source_ground_y"), dynamicOps.createInt(this.field_16669))
			.put(dynamicOps.createString("source_z"), dynamicOps.createInt(this.field_16668))
			.put(dynamicOps.createString("delta_y"), dynamicOps.createInt(this.field_16667))
			.put(dynamicOps.createString("dest_proj"), dynamicOps.createString(this.field_16671.method_16635()));
		return new Dynamic<>(dynamicOps, dynamicOps.createMap(builder.build()));
	}

	public static <T> class_3780 method_16613(Dynamic<T> dynamic) {
		return new class_3780(
			dynamic.get("source_x").asInt(0),
			dynamic.get("source_ground_y").asInt(0),
			dynamic.get("source_z").asInt(0),
			dynamic.get("delta_y").asInt(0),
			class_3785.class_3786.method_16638(dynamic.get("dest_proj").asString(""))
		);
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			class_3780 lv = (class_3780)object;
			if (this.field_16670 != lv.field_16670) {
				return false;
			} else if (this.field_16668 != lv.field_16668) {
				return false;
			} else {
				return this.field_16667 != lv.field_16667 ? false : this.field_16671 == lv.field_16671;
			}
		} else {
			return false;
		}
	}

	public int hashCode() {
		int i = this.field_16670;
		i = 31 * i + this.field_16669;
		i = 31 * i + this.field_16668;
		i = 31 * i + this.field_16667;
		return 31 * i + this.field_16671.hashCode();
	}

	public String toString() {
		return "JigsawJunction{sourceX="
			+ this.field_16670
			+ ", sourceGroundY="
			+ this.field_16669
			+ ", sourceZ="
			+ this.field_16668
			+ ", deltaY="
			+ this.field_16667
			+ ", destProjection="
			+ this.field_16671
			+ '}';
	}
}
