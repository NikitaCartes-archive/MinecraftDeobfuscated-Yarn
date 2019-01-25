package net.minecraft.structure.pool;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.class_3817;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.structure.processor.JigsawReplacementStructureProcessor;
import net.minecraft.structure.processor.NopStructureProcessor;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;

public class SinglePoolElement extends StructurePoolElement {
	protected final Identifier location;
	protected final ImmutableList<StructureProcessor> processors;

	public SinglePoolElement(String string, List<StructureProcessor> list) {
		this.location = new Identifier(string);
		this.processors = ImmutableList.copyOf(list);
	}

	public SinglePoolElement(String string) {
		this(string, ImmutableList.of());
	}

	public SinglePoolElement(Dynamic<?> dynamic) {
		this(
			dynamic.get("location").asString(""),
			dynamic.get("processors")
				.asList(dynamicx -> class_3817.deserialize(dynamicx, Registry.STRUCTURE_PROCESSOR, "processor_type", NopStructureProcessor.INSTANCE))
		);
	}

	public List<Structure.StructureBlockInfo> method_16614(StructureManager structureManager, BlockPos blockPos, Rotation rotation, boolean bl) {
		Structure structure = structureManager.getStructureOrBlank(this.location);
		List<Structure.StructureBlockInfo> list = structure.method_15165(blockPos, new StructurePlacementData().setRotation(rotation), Blocks.field_10465, bl);
		List<Structure.StructureBlockInfo> list2 = Lists.<Structure.StructureBlockInfo>newArrayList();

		for (Structure.StructureBlockInfo structureBlockInfo : list) {
			if (structureBlockInfo.tag != null) {
				StructureBlockMode structureBlockMode = StructureBlockMode.valueOf(structureBlockInfo.tag.getString("mode"));
				if (structureBlockMode == StructureBlockMode.field_12696) {
					list2.add(structureBlockInfo);
				}
			}
		}

		return list2;
	}

	@Override
	public List<Structure.StructureBlockInfo> getStructureBlockInfos(StructureManager structureManager, BlockPos blockPos, Rotation rotation, Random random) {
		Structure structure = structureManager.getStructureOrBlank(this.location);
		List<Structure.StructureBlockInfo> list = structure.method_15165(blockPos, new StructurePlacementData().setRotation(rotation), Blocks.field_16540, true);
		Collections.shuffle(list, random);
		return list;
	}

	@Override
	public MutableIntBoundingBox getBoundingBox(StructureManager structureManager, BlockPos blockPos, Rotation rotation) {
		Structure structure = structureManager.getStructureOrBlank(this.location);
		return structure.calculateBoundingBox(new StructurePlacementData().setRotation(rotation), blockPos);
	}

	@Override
	public boolean generate(
		StructureManager structureManager, IWorld iWorld, BlockPos blockPos, Rotation rotation, MutableIntBoundingBox mutableIntBoundingBox, Random random
	) {
		Structure structure = structureManager.getStructureOrBlank(this.location);
		StructurePlacementData structurePlacementData = this.method_16616(rotation, mutableIntBoundingBox);
		if (!structure.method_15172(iWorld, blockPos, structurePlacementData, 18)) {
			return false;
		} else {
			for (Structure.StructureBlockInfo structureBlockInfo : Structure.process(
				iWorld, blockPos, structurePlacementData, this.method_16614(structureManager, blockPos, rotation, false)
			)) {
				this.method_16756(iWorld, structureBlockInfo, blockPos, rotation, random, mutableIntBoundingBox);
			}

			return true;
		}
	}

	protected StructurePlacementData method_16616(Rotation rotation, MutableIntBoundingBox mutableIntBoundingBox) {
		StructurePlacementData structurePlacementData = new StructurePlacementData();
		structurePlacementData.setBoundingBox(mutableIntBoundingBox);
		structurePlacementData.setRotation(rotation);
		structurePlacementData.method_15131(true);
		structurePlacementData.setIgnoreEntities(false);
		structurePlacementData.addProcessor(BlockIgnoreStructureProcessor.IGNORE_AIR_AND_STRUCTURE_BLOCKS);
		structurePlacementData.addProcessor(JigsawReplacementStructureProcessor.INSTANCE);
		this.processors.forEach(structurePlacementData::addProcessor);
		this.getProjection().getProcessors().forEach(structurePlacementData::addProcessor);
		return structurePlacementData;
	}

	@Override
	public StructurePoolElementType getType() {
		return StructurePoolElementType.SINGLE_POOL_ELEMENT;
	}

	@Override
	public <T> Dynamic<T> method_16625(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("location"),
					dynamicOps.createString(this.location.toString()),
					dynamicOps.createString("processors"),
					dynamicOps.createList(this.processors.stream().map(structureProcessor -> structureProcessor.method_16771(dynamicOps).getValue()))
				)
			)
		);
	}
}
