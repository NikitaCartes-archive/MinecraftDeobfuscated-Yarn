package net.minecraft.structure;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.structure.pool.StructurePool;

public class JigsawJunction {
	private final int sourceX;
	private final int sourceGroundY;
	private final int sourceZ;
	private final int deltaY;
	private final StructurePool.Projection field_16671;

	public JigsawJunction(int i, int j, int k, int l, StructurePool.Projection projection) {
		this.sourceX = i;
		this.sourceGroundY = j;
		this.sourceZ = k;
		this.deltaY = l;
		this.field_16671 = projection;
	}

	public int getSourceX() {
		return this.sourceX;
	}

	public int getSourceGroundY() {
		return this.sourceGroundY;
	}

	public int getSourceZ() {
		return this.sourceZ;
	}

	public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
		Builder<T, T> builder = ImmutableMap.builder();
		builder.put(dynamicOps.createString("source_x"), dynamicOps.createInt(this.sourceX))
			.put(dynamicOps.createString("source_ground_y"), dynamicOps.createInt(this.sourceGroundY))
			.put(dynamicOps.createString("source_z"), dynamicOps.createInt(this.sourceZ))
			.put(dynamicOps.createString("delta_y"), dynamicOps.createInt(this.deltaY))
			.put(dynamicOps.createString("dest_proj"), dynamicOps.createString(this.field_16671.getId()));
		return new Dynamic<>(dynamicOps, dynamicOps.createMap(builder.build()));
	}

	public static <T> JigsawJunction deserialize(Dynamic<T> dynamic) {
		return new JigsawJunction(
			dynamic.get("source_x").asInt(0),
			dynamic.get("source_ground_y").asInt(0),
			dynamic.get("source_z").asInt(0),
			dynamic.get("delta_y").asInt(0),
			StructurePool.Projection.getById(dynamic.get("dest_proj").asString(""))
		);
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			JigsawJunction jigsawJunction = (JigsawJunction)object;
			if (this.sourceX != jigsawJunction.sourceX) {
				return false;
			} else if (this.sourceZ != jigsawJunction.sourceZ) {
				return false;
			} else {
				return this.deltaY != jigsawJunction.deltaY ? false : this.field_16671 == jigsawJunction.field_16671;
			}
		} else {
			return false;
		}
	}

	public int hashCode() {
		int i = this.sourceX;
		i = 31 * i + this.sourceGroundY;
		i = 31 * i + this.sourceZ;
		i = 31 * i + this.deltaY;
		return 31 * i + this.field_16671.hashCode();
	}

	public String toString() {
		return "JigsawJunction{sourceX="
			+ this.sourceX
			+ ", sourceGroundY="
			+ this.sourceGroundY
			+ ", sourceZ="
			+ this.sourceZ
			+ ", deltaY="
			+ this.deltaY
			+ ", destProjection="
			+ this.field_16671
			+ '}';
	}
}
