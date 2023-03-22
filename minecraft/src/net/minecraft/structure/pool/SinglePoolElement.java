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
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.structure.processor.JigsawReplacementStructureProcessor;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class SinglePoolElement extends StructurePoolElement {
	private static final Codec<Either<Identifier, StructureTemplate>> LOCATION_CODEC = Codec.of(
		SinglePoolElement::encodeLocation, Identifier.CODEC.map(Either::left)
	);
	public static final Codec<SinglePoolElement> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(locationGetter(), processorsGetter(), projectionGetter()).apply(instance, SinglePoolElement::new)
	);
	protected final Either<Identifier, StructureTemplate> location;
	protected final RegistryEntry<StructureProcessorList> processors;

	private static <T> DataResult<T> encodeLocation(Either<Identifier, StructureTemplate> location, DynamicOps<T> ops, T prefix) {
		Optional<Identifier> optional = location.left();
		return !optional.isPresent()
			? DataResult.error(() -> "Can not serialize a runtime pool element")
			: Identifier.CODEC.encode((Identifier)optional.get(), ops, prefix);
	}

	protected static <E extends SinglePoolElement> RecordCodecBuilder<E, RegistryEntry<StructureProcessorList>> processorsGetter() {
		return StructureProcessorType.REGISTRY_CODEC.fieldOf("processors").forGetter(pool -> pool.processors);
	}

	protected static <E extends SinglePoolElement> RecordCodecBuilder<E, Either<Identifier, StructureTemplate>> locationGetter() {
		return LOCATION_CODEC.fieldOf("location").forGetter(pool -> pool.location);
	}

	protected SinglePoolElement(
		Either<Identifier, StructureTemplate> location, RegistryEntry<StructureProcessorList> processors, StructurePool.Projection projection
	) {
		super(projection);
		this.location = location;
		this.processors = processors;
	}

	@Override
	public Vec3i getStart(StructureTemplateManager structureTemplateManager, BlockRotation rotation) {
		StructureTemplate structureTemplate = this.getStructure(structureTemplateManager);
		return structureTemplate.getRotatedSize(rotation);
	}

	private StructureTemplate getStructure(StructureTemplateManager structureTemplateManager) {
		return this.location.map(structureTemplateManager::getTemplateOrBlank, Function.identity());
	}

	public List<StructureTemplate.StructureBlockInfo> getDataStructureBlocks(
		StructureTemplateManager structureTemplateManager, BlockPos pos, BlockRotation rotation, boolean mirroredAndRotated
	) {
		StructureTemplate structureTemplate = this.getStructure(structureTemplateManager);
		List<StructureTemplate.StructureBlockInfo> list = structureTemplate.getInfosForBlock(
			pos, new StructurePlacementData().setRotation(rotation), Blocks.STRUCTURE_BLOCK, mirroredAndRotated
		);
		List<StructureTemplate.StructureBlockInfo> list2 = Lists.<StructureTemplate.StructureBlockInfo>newArrayList();

		for (StructureTemplate.StructureBlockInfo structureBlockInfo : list) {
			NbtCompound nbtCompound = structureBlockInfo.nbt();
			if (nbtCompound != null) {
				StructureBlockMode structureBlockMode = StructureBlockMode.valueOf(nbtCompound.getString("mode"));
				if (structureBlockMode == StructureBlockMode.DATA) {
					list2.add(structureBlockInfo);
				}
			}
		}

		return list2;
	}

	@Override
	public List<StructureTemplate.StructureBlockInfo> getStructureBlockInfos(
		StructureTemplateManager structureTemplateManager, BlockPos pos, BlockRotation rotation, Random random
	) {
		StructureTemplate structureTemplate = this.getStructure(structureTemplateManager);
		ObjectArrayList<StructureTemplate.StructureBlockInfo> objectArrayList = structureTemplate.getInfosForBlock(
			pos, new StructurePlacementData().setRotation(rotation), Blocks.JIGSAW, true
		);
		Util.shuffle(objectArrayList, random);
		return objectArrayList;
	}

	@Override
	public BlockBox getBoundingBox(StructureTemplateManager structureTemplateManager, BlockPos pos, BlockRotation rotation) {
		StructureTemplate structureTemplate = this.getStructure(structureTemplateManager);
		return structureTemplate.calculateBoundingBox(new StructurePlacementData().setRotation(rotation), pos);
	}

	@Override
	public boolean generate(
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
	) {
		StructureTemplate structureTemplate = this.getStructure(structureTemplateManager);
		StructurePlacementData structurePlacementData = this.createPlacementData(rotation, box, keepJigsaws);
		if (!structureTemplate.place(world, pos, pivot, structurePlacementData, random, 18)) {
			return false;
		} else {
			for (StructureTemplate.StructureBlockInfo structureBlockInfo : StructureTemplate.process(
				world, pos, pivot, structurePlacementData, this.getDataStructureBlocks(structureTemplateManager, pos, rotation, false)
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
