package net.minecraft.structure.pool;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.PlacedFeature;

public abstract class StructurePoolElement {
	public static final Codec<StructurePoolElement> CODEC = Registry.STRUCTURE_POOL_ELEMENT
		.getCodec()
		.dispatch("element_type", StructurePoolElement::getType, StructurePoolElementType::codec);
	private static final RegistryEntry<StructureProcessorList> EMPTY_PROCESSORS = RegistryEntry.of(new StructureProcessorList(List.of()));
	@Nullable
	private volatile StructurePool.Projection projection;

	protected static <E extends StructurePoolElement> RecordCodecBuilder<E, StructurePool.Projection> projectionGetter() {
		return StructurePool.Projection.CODEC.fieldOf("projection").forGetter(StructurePoolElement::getProjection);
	}

	protected StructurePoolElement(StructurePool.Projection projection) {
		this.projection = projection;
	}

	public abstract Vec3i getStart(StructureTemplateManager structureTemplateManager, BlockRotation rotation);

	public abstract List<StructureTemplate.StructureBlockInfo> getStructureBlockInfos(
		StructureTemplateManager structureTemplateManager, BlockPos pos, BlockRotation rotation, Random random
	);

	public abstract BlockBox getBoundingBox(StructureTemplateManager structureTemplateManager, BlockPos pos, BlockRotation rotation);

	public abstract boolean generate(
		StructureTemplateManager structureTemplateManager,
		StructureWorldAccess world,
		StructureAccessor structureAccessor,
		ChunkGenerator chunkGenerator,
		BlockPos pos,
		BlockPos pivot,
		BlockRotation rotation,
		BlockBox box,
		Random random,
		boolean keepJigsaws
	);

	public abstract StructurePoolElementType<?> getType();

	public void method_16756(
		WorldAccess world, StructureTemplate.StructureBlockInfo structureBlockInfo, BlockPos pos, BlockRotation rotation, Random random, BlockBox box
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
		return projection -> new LegacySinglePoolElement(Either.left(new Identifier(id)), EMPTY_PROCESSORS, projection);
	}

	public static Function<StructurePool.Projection, LegacySinglePoolElement> ofProcessedLegacySingle(
		String id, RegistryEntry<StructureProcessorList> processorListEntry
	) {
		return projection -> new LegacySinglePoolElement(Either.left(new Identifier(id)), processorListEntry, projection);
	}

	public static Function<StructurePool.Projection, SinglePoolElement> ofSingle(String id) {
		return projection -> new SinglePoolElement(Either.left(new Identifier(id)), EMPTY_PROCESSORS, projection);
	}

	public static Function<StructurePool.Projection, SinglePoolElement> ofProcessedSingle(String id, RegistryEntry<StructureProcessorList> processorListEntry) {
		return projection -> new SinglePoolElement(Either.left(new Identifier(id)), processorListEntry, projection);
	}

	public static Function<StructurePool.Projection, FeaturePoolElement> ofFeature(RegistryEntry<PlacedFeature> placedFeatureEntry) {
		return projection -> new FeaturePoolElement(placedFeatureEntry, projection);
	}

	public static Function<StructurePool.Projection, ListPoolElement> ofList(
		List<Function<StructurePool.Projection, ? extends StructurePoolElement>> elementGetters
	) {
		return projection -> new ListPoolElement(
				(List<StructurePoolElement>)elementGetters.stream()
					.map(elementGetter -> (StructurePoolElement)elementGetter.apply(projection))
					.collect(Collectors.toList()),
				projection
			);
	}
}
