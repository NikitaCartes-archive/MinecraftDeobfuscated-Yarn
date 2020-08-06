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
		StructureWorldAccess structureWorldAccess,
		StructureAccessor structureAccessor,
		ChunkGenerator chunkGenerator,
		BlockPos blockPos,
		BlockPos blockPos2,
		BlockRotation blockRotation,
		BlockBox blockBox,
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

	public static Function<StructurePool.Projection, EmptyPoolElement> method_30438() {
		return projection -> EmptyPoolElement.INSTANCE;
	}

	public static Function<StructurePool.Projection, LegacySinglePoolElement> method_30425(String string) {
		return projection -> new LegacySinglePoolElement(Either.left(new Identifier(string)), () -> StructureProcessorLists.EMPTY, projection);
	}

	public static Function<StructurePool.Projection, LegacySinglePoolElement> method_30426(String string, StructureProcessorList structureProcessorList) {
		return projection -> new LegacySinglePoolElement(Either.left(new Identifier(string)), () -> structureProcessorList, projection);
	}

	public static Function<StructurePool.Projection, SinglePoolElement> method_30434(String string) {
		return projection -> new SinglePoolElement(Either.left(new Identifier(string)), () -> StructureProcessorLists.EMPTY, projection);
	}

	public static Function<StructurePool.Projection, SinglePoolElement> method_30435(String string, StructureProcessorList structureProcessorList) {
		return projection -> new SinglePoolElement(Either.left(new Identifier(string)), () -> structureProcessorList, projection);
	}

	public static Function<StructurePool.Projection, FeaturePoolElement> method_30421(ConfiguredFeature<?, ?> configuredFeature) {
		return projection -> new FeaturePoolElement(() -> configuredFeature, projection);
	}

	public static Function<StructurePool.Projection, ListPoolElement> method_30429(List<Function<StructurePool.Projection, ? extends StructurePoolElement>> list) {
		return projection -> new ListPoolElement(
				(List<StructurePoolElement>)list.stream().map(function -> (StructurePoolElement)function.apply(projection)).collect(Collectors.toList()), projection
			);
	}
}
