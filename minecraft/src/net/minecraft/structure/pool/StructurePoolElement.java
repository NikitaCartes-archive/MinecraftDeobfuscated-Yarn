package net.minecraft.structure.pool;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public abstract class StructurePoolElement {
	public static final Codec<StructurePoolElement> field_24953 = Registry.STRUCTURE_POOL_ELEMENT
		.dispatch("element_type", StructurePoolElement::getType, StructurePoolElementType::codec);
	@Nullable
	private volatile StructurePool.Projection projection;

	protected static <E extends StructurePoolElement> RecordCodecBuilder<E, StructurePool.Projection> method_28883() {
		return StructurePool.Projection.field_24956.fieldOf("projection").forGetter(StructurePoolElement::getProjection);
	}

	protected StructurePoolElement(StructurePool.Projection projection) {
		this.projection = projection;
	}

	public abstract List<Structure.StructureBlockInfo> getStructureBlockInfos(
		StructureManager structureManager, BlockPos pos, BlockRotation rotation, Random random
	);

	public abstract BlockBox getBoundingBox(StructureManager structureManager, BlockPos pos, BlockRotation rotation);

	public abstract boolean generate(
		StructureManager structureManager,
		ServerWorldAccess serverWorldAccess,
		StructureAccessor structureAccessor,
		ChunkGenerator chunkGenerator,
		BlockPos blockPos,
		BlockPos blockPos2,
		BlockRotation blockRotation,
		BlockBox blockBox,
		Random random,
		boolean bl
	);

	public abstract StructurePoolElementType<?> getType();

	public void method_16756(
		WorldAccess worldAccess, Structure.StructureBlockInfo structureBlockInfo, BlockPos blockPos, BlockRotation blockRotation, Random random, BlockBox blockBox
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

	public int getGroundLevelDelta() {
		return 1;
	}
}
