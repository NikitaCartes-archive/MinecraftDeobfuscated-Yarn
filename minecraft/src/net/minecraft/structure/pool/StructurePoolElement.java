package net.minecraft.structure.pool;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;

public abstract class StructurePoolElement {
	@Nullable
	private volatile StructurePool.Projection field_16862 = null;

	protected StructurePoolElement(StructurePool.Projection projection) {
		this.field_16862 = projection;
	}

	protected StructurePoolElement(Dynamic<?> dynamic) {
		this.field_16862 = StructurePool.Projection.getById(dynamic.get("projection").asString(StructurePool.Projection.RIGID.getId()));
	}

	public abstract List<Structure.StructureBlockInfo> method_16627(StructureManager structureManager, BlockPos blockPos, Rotation rotation, Random random);

	public abstract MutableIntBoundingBox method_16628(StructureManager structureManager, BlockPos blockPos, Rotation rotation);

	public abstract boolean method_16626(
		StructureManager structureManager, IWorld iWorld, BlockPos blockPos, Rotation rotation, MutableIntBoundingBox mutableIntBoundingBox, Random random
	);

	public abstract StructurePoolElementType method_16757();

	public void method_16756(
		IWorld iWorld,
		Structure.StructureBlockInfo structureBlockInfo,
		BlockPos blockPos,
		Rotation rotation,
		Random random,
		MutableIntBoundingBox mutableIntBoundingBox
	) {
	}

	public StructurePoolElement method_16622(StructurePool.Projection projection) {
		this.field_16862 = projection;
		return this;
	}

	public StructurePool.Projection method_16624() {
		StructurePool.Projection projection = this.field_16862;
		if (projection == null) {
			throw new IllegalStateException();
		} else {
			return projection;
		}
	}

	protected abstract <T> Dynamic<T> method_16625(DynamicOps<T> dynamicOps);

	public <T> Dynamic<T> method_16755(DynamicOps<T> dynamicOps) {
		T object = this.method_16625(dynamicOps).getValue();
		T object2 = dynamicOps.mergeInto(
			object, dynamicOps.createString("element_type"), dynamicOps.createString(Registry.STRUCTURE_POOL_ELEMENT.method_10221(this.method_16757()).toString())
		);
		return new Dynamic<>(dynamicOps, dynamicOps.mergeInto(object2, dynamicOps.createString("projection"), dynamicOps.createString(this.field_16862.getId())));
	}

	public int method_19308() {
		return 1;
	}
}
