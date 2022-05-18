package net.minecraft.structure.pool;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.structure.processor.JigsawReplacementStructureProcessor;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.structure.processor.StructureProcessorLists;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class SinglePoolElement extends StructurePoolElement {
	private static final Codec<Either<Identifier, Structure>> LOCATION_CODEC = Codec.of(SinglePoolElement::encodeLocation, Identifier.CODEC.map(Either::left));
	public static final Codec<SinglePoolElement> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(locationGetter(), processorsGetter(), projectionGetter()).apply(instance, SinglePoolElement::new)
	);
	protected final Either<Identifier, Structure> location;
	protected final RegistryEntry<StructureProcessorList> processors;

	private static <T> DataResult<T> encodeLocation(Either<Identifier, Structure> either, DynamicOps<T> dynamicOps, T object) {
		Optional<Identifier> optional = either.left();
		return !optional.isPresent()
			? DataResult.error("Can not serialize a runtime pool element")
			: Identifier.CODEC.encode((Identifier)optional.get(), dynamicOps, object);
	}

	protected static <E extends SinglePoolElement> RecordCodecBuilder<E, RegistryEntry<StructureProcessorList>> processorsGetter() {
		return StructureProcessorType.REGISTRY_CODEC.fieldOf("processors").forGetter(singlePoolElement -> singlePoolElement.processors);
	}

	protected static <E extends SinglePoolElement> RecordCodecBuilder<E, Either<Identifier, Structure>> locationGetter() {
		return LOCATION_CODEC.fieldOf("location").forGetter(singlePoolElement -> singlePoolElement.location);
	}

	protected SinglePoolElement(Either<Identifier, Structure> location, RegistryEntry<StructureProcessorList> processors, StructurePool.Projection projection) {
		super(projection);
		this.location = location;
		this.processors = processors;
	}

	public SinglePoolElement(Structure structure) {
		this(Either.right(structure), StructureProcessorLists.EMPTY, StructurePool.Projection.RIGID);
	}

	@Override
	public Vec3i getStart(StructureManager structureManager, BlockRotation rotation) {
		Structure structure = this.getStructure(structureManager);
		return structure.getRotatedSize(rotation);
	}

	private Structure getStructure(StructureManager structureManager) {
		return this.location.map(structureManager::getStructureOrBlank, Function.identity());
	}

	public List<Structure.StructureBlockInfo> getDataStructureBlocks(
		StructureManager structureManager, BlockPos pos, BlockRotation rotation, boolean mirroredAndRotated
	) {
		Structure structure = this.getStructure(structureManager);
		List<Structure.StructureBlockInfo> list = structure.getInfosForBlock(
			pos, new StructurePlacementData().setRotation(rotation), Blocks.STRUCTURE_BLOCK, mirroredAndRotated
		);
		List<Structure.StructureBlockInfo> list2 = Lists.<Structure.StructureBlockInfo>newArrayList();

		for (Structure.StructureBlockInfo structureBlockInfo : list) {
			if (structureBlockInfo.nbt != null) {
				StructureBlockMode structureBlockMode = StructureBlockMode.valueOf(structureBlockInfo.nbt.getString("mode"));
				if (structureBlockMode == StructureBlockMode.DATA) {
					list2.add(structureBlockInfo);
				}
			}
		}

		return list2;
	}

	@Override
	public List<Structure.StructureBlockInfo> getStructureBlockInfos(StructureManager structureManager, BlockPos pos, BlockRotation rotation, Random random) {
		Structure structure = this.getStructure(structureManager);
		ObjectArrayList<Structure.StructureBlockInfo> objectArrayList = structure.getInfosForBlock(
			pos, new StructurePlacementData().setRotation(rotation), Blocks.JIGSAW, true
		);
		Util.shuffle(objectArrayList, random);
		return objectArrayList;
	}

	@Override
	public BlockBox getBoundingBox(StructureManager structureManager, BlockPos pos, BlockRotation rotation) {
		Structure structure = this.getStructure(structureManager);
		return structure.calculateBoundingBox(new StructurePlacementData().setRotation(rotation), pos);
	}

	@Override
	public boolean generate(
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
	) {
		Structure structure = this.getStructure(structureManager);
		StructurePlacementData structurePlacementData = this.createPlacementData(rotation, box, keepJigsaws);
		if (!structure.place(world, pos, blockPos, structurePlacementData, random, 18)) {
			return false;
		} else {
			for (Structure.StructureBlockInfo structureBlockInfo : Structure.process(
				world, pos, blockPos, structurePlacementData, this.getDataStructureBlocks(structureManager, pos, rotation, false)
			)) {
				this.method_16756(world, structureBlockInfo, pos, rotation, random, box);
			}

			return true;
		}
	}

	protected StructurePlacementData createPlacementData(BlockRotation rotation, BlockBox box, boolean keepJigsaws) {
		StructurePlacementData structurePlacementData = new StructurePlacementData();
		structurePlacementData.setBoundingBox(box);
		structurePlacementData.setRotation(rotation);
		structurePlacementData.setUpdateNeighbors(true);
		structurePlacementData.setIgnoreEntities(false);
		structurePlacementData.addProcessor(BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS);
		structurePlacementData.setInitializeMobs(true);
		if (!keepJigsaws) {
			structurePlacementData.addProcessor(JigsawReplacementStructureProcessor.INSTANCE);
		}

		this.processors.value().getList().forEach(structurePlacementData::addProcessor);
		this.getProjection().getProcessors().forEach(structurePlacementData::addProcessor);
		return structurePlacementData;
	}

	@Override
	public StructurePoolElementType<?> getType() {
		return StructurePoolElementType.SINGLE_POOL_ELEMENT;
	}

	public String toString() {
		return "Single[" + this.location + "]";
	}
}
