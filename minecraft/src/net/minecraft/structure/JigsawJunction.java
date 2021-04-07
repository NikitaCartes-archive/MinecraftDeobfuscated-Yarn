package net.minecraft.structure;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.structure.pool.StructurePool;

public class JigsawJunction {
	private final int sourceX;
	private final int sourceGroundY;
	private final int sourceZ;
	private final int deltaY;
	private final StructurePool.Projection destProjection;

	public JigsawJunction(int sourceX, int sourceGroundY, int sourceZ, int deltaY, StructurePool.Projection destProjection) {
		this.sourceX = sourceX;
		this.sourceGroundY = sourceGroundY;
		this.sourceZ = sourceZ;
		this.deltaY = deltaY;
		this.destProjection = destProjection;
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

	public int getDeltaY() {
		return this.deltaY;
	}

	public StructurePool.Projection getDestProjection() {
		return this.destProjection;
	}

	public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
		Builder<T, T> builder = ImmutableMap.builder();
		builder.put(dynamicOps.createString("source_x"), dynamicOps.createInt(this.sourceX))
			.put(dynamicOps.createString("source_ground_y"), dynamicOps.createInt(this.sourceGroundY))
			.put(dynamicOps.createString("source_z"), dynamicOps.createInt(this.sourceZ))
			.put(dynamicOps.createString("delta_y"), dynamicOps.createInt(this.deltaY))
			.put(dynamicOps.createString("dest_proj"), dynamicOps.createString(this.destProjection.getId()));
		return new Dynamic<>(dynamicOps, dynamicOps.createMap(builder.build()));
	}

	public static <T> JigsawJunction method_28873(Dynamic<T> dynamic) {
		return new JigsawJunction(
			dynamic.get("source_x").asInt(0),
			dynamic.get("source_ground_y").asInt(0),
			dynamic.get("source_z").asInt(0),
			dynamic.get("delta_y").asInt(0),
			StructurePool.Projection.getById(dynamic.get("dest_proj").asString(""))
		);
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (o != null && this.getClass() == o.getClass()) {
			JigsawJunction jigsawJunction = (JigsawJunction)o;
			if (this.sourceX != jigsawJunction.sourceX) {
				return false;
			} else if (this.sourceZ != jigsawJunction.sourceZ) {
				return false;
			} else {
				return this.deltaY != jigsawJunction.deltaY ? false : this.destProjection == jigsawJunction.destProjection;
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
		return 31 * i + this.destProjection.hashCode();
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
			+ this.destProjection
			+ '}';
	}
}
