package net.minecraft.structure.pool;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Random;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;

public abstract class StructurePoolElement {
	private StructurePool.Projection projection = StructurePool.Projection.RIGID;

	public abstract List<Structure.StructureBlockInfo> getStructureBlockInfos(
		StructureManager structureManager, BlockPos blockPos, Rotation rotation, Random random
	);

	public abstract MutableIntBoundingBox getBoundingBox(StructureManager structureManager, BlockPos blockPos, Rotation rotation);

	public abstract boolean generate(
		StructureManager structureManager, IWorld iWorld, BlockPos blockPos, Rotation rotation, MutableIntBoundingBox mutableIntBoundingBox, Random random
	);

	public abstract StructurePoolElementType getType();

	public void method_16756(
		IWorld iWorld,
		Structure.StructureBlockInfo structureBlockInfo,
		BlockPos blockPos,
		Rotation rotation,
		Random random,
		MutableIntBoundingBox mutableIntBoundingBox
	) {
	}

	public StructurePoolElement setProjection(StructurePool.Projection projection) {
		this.projection = projection;
		return this;
	}

	public StructurePool.Projection getProjection() {
		return this.projection;
	}

	protected abstract <T> Dynamic<T> method_16625(DynamicOps<T> dynamicOps);

	public <T> Dynamic<T> method_16755(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.mergeInto(
				this.method_16625(dynamicOps).getValue(),
				dynamicOps.createString("element_type"),
				dynamicOps.createString(Registry.STRUCTURE_POOL_ELEMENT.getId(this.getType()).toString())
			)
		);
	}
}
