package net.minecraft.structure.pool;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public abstract class StructurePoolElement {
	@Nullable
	private volatile StructurePool.Projection projection;

	protected StructurePoolElement(StructurePool.Projection projection) {
		this.projection = projection;
	}

	protected StructurePoolElement(Dynamic<?> dynamic) {
		this.projection = StructurePool.Projection.getById(dynamic.get("projection").asString(StructurePool.Projection.RIGID.getId()));
	}

	public abstract List<Structure.StructureBlockInfo> getStructureBlockInfos(
		StructureManager structureManager, BlockPos blockPos, BlockRotation blockRotation, Random random
	);

	public abstract MutableIntBoundingBox getBoundingBox(StructureManager structureManager, BlockPos blockPos, BlockRotation blockRotation);

	public abstract boolean generate(
		StructureManager structureManager,
		IWorld iWorld,
		ChunkGenerator<?> chunkGenerator,
		BlockPos blockPos,
		BlockRotation blockRotation,
		MutableIntBoundingBox mutableIntBoundingBox,
		Random random
	);

	public abstract StructurePoolElementType getType();

	public void method_16756(
		IWorld iWorld,
		Structure.StructureBlockInfo structureBlockInfo,
		BlockPos blockPos,
		BlockRotation blockRotation,
		Random random,
		MutableIntBoundingBox mutableIntBoundingBox
	) {
	}

	public StructurePoolElement setProjection(StructurePool.Projection projection) {
		this.projection = projection;
		return this;
	}

	public StructurePool.Projection getProjection() {
		StructurePool.Projection projection = this.projection;
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
			object, dynamicOps.createString("element_type"), dynamicOps.createString(Registry.STRUCTURE_POOL_ELEMENT.getId(this.getType()).toString())
		);
		return new Dynamic<>(dynamicOps, dynamicOps.mergeInto(object2, dynamicOps.createString("projection"), dynamicOps.createString(this.projection.getId())));
	}

	public int method_19308() {
		return 1;
	}
}
