package net.minecraft.structure.pool;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.StructureAccessor;
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
		StructureManager structureManager, BlockPos pos, BlockRotation rotation, Random random
	);

	public abstract BlockBox getBoundingBox(StructureManager structureManager, BlockPos pos, BlockRotation rotation);

	public abstract boolean generate(
		StructureManager structureManager,
		IWorld world,
		StructureAccessor structureAccessor,
		ChunkGenerator<?> chunkGenerator,
		BlockPos blockPos,
		BlockPos blockPos2,
		BlockRotation blockRotation,
		BlockBox blockBox,
		Random random,
		boolean bl
	);

	public abstract StructurePoolElementType getType();

	public void method_16756(
		IWorld iWorld, Structure.StructureBlockInfo structureBlockInfo, BlockPos blockPos, BlockRotation blockRotation, Random random, BlockBox blockBox
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

	protected abstract <T> Dynamic<T> rawToDynamic(DynamicOps<T> dynamicOps);

	public <T> Dynamic<T> toDynamic(DynamicOps<T> dynamicOps) {
		T object = this.rawToDynamic(dynamicOps).getValue();
		T object2 = dynamicOps.mergeInto(
			object, dynamicOps.createString("element_type"), dynamicOps.createString(Registry.STRUCTURE_POOL_ELEMENT.getId(this.getType()).toString())
		);
		return new Dynamic<>(dynamicOps, dynamicOps.mergeInto(object2, dynamicOps.createString("projection"), dynamicOps.createString(this.projection.getId())));
	}

	public int getGroundLevelDelta() {
		return 1;
	}
}
