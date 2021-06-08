package net.minecraft.structure.pool;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.structure.processor.StructureProcessorLists;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;

public abstract class StructurePoolElement {
	public static final Codec<StructurePoolElement> CODEC = Registry.STRUCTURE_POOL_ELEMENT
		.dispatch("element_type", StructurePoolElement::getType, StructurePoolElementType::codec);
	@Nullable
	private volatile StructurePool.Projection projection;

	protected static <E extends StructurePoolElement> RecordCodecBuilder<E, StructurePool.Projection> method_28883() {
		return StructurePool.Projection.CODEC.fieldOf("projection").forGetter(StructurePoolElement::getProjection);
	}

	protected StructurePoolElement(StructurePool.Projection projection) {
		this.projection = projection;
	}

	public abstract Vec3i getStart(StructureManager structureManager, BlockRotation rotation);

	public abstract List<Structure.StructureBlockInfo> getStructureBlockInfos(
		StructureManager structureManager, BlockPos pos, BlockRotation rotation, Random random
	);

	public abstract BlockBox getBoundingBox(StructureManager structureManager, BlockPos pos, BlockRotation rotation);

	public abstract boolean generate(
		StructureManager structureManager,
		StructureWorldAccess world,
		StructureAccessor structureAccessor,
		ChunkGenerator chunkGenerator,
		BlockPos pos,
		BlockPos blockPos,
		BlockRotation rotation,
		BlockBox box,
		Random random,
		boolean keepJigsaws
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

	public static Function<StructurePool.Projection, EmptyPoolElement> ofEmpty() {
		return projection -> EmptyPoolElement.INSTANCE;
	}

	public static Function<StructurePool.Projection, LegacySinglePoolElement> ofLegacySingle(String id) {
		return projection -> new LegacySinglePoolElement(Either.left(new Identifier(id)), () -> StructureProcessorLists.EMPTY, projection);
	}

	public static Function<StructurePool.Projection, LegacySinglePoolElement> ofProcessedLegacySingle(String id, StructureProcessorList processors) {
		return projection -> new LegacySinglePoolElement(Either.left(new Identifier(id)), () -> processors, projection);
	}

	public static Function<StructurePool.Projection, SinglePoolElement> ofSingle(String id) {
		return projection -> new SinglePoolElement(Either.left(new Identifier(id)), () -> StructureProcessorLists.EMPTY, projection);
	}

	public static Function<StructurePool.Projection, SinglePoolElement> ofProcessedSingle(String id, StructureProcessorList processors) {
		return projection -> new SinglePoolElement(Either.left(new Identifier(id)), () -> processors, projection);
	}

	public static Function<StructurePool.Projection, FeaturePoolElement> ofFeature(ConfiguredFeature<?, ?> processors) {
		return projection -> new FeaturePoolElement(() -> processors, projection);
	}

	public static Function<StructurePool.Projection, ListPoolElement> ofList(List<Function<StructurePool.Projection, ? extends StructurePoolElement>> list) {
		return projection -> new ListPoolElement(
				(List<StructurePoolElement>)list.stream().map(function -> (StructurePoolElement)function.apply(projection)).collect(Collectors.toList()), projection
			);
	}
}
