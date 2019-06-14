package net.minecraft.structure.pool;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.structure.processor.JigsawReplacementStructureProcessor;
import net.minecraft.structure.processor.NopStructureProcessor;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.DynamicDeserializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;

public class SinglePoolElement extends StructurePoolElement {
	protected final Identifier location;
	protected final ImmutableList<StructureProcessor> processors;

	@Deprecated
	public SinglePoolElement(String string, List<StructureProcessor> list) {
		this(string, list, StructurePool.Projection.field_16687);
	}

	public SinglePoolElement(String string, List<StructureProcessor> list, StructurePool.Projection projection) {
		super(projection);
		this.location = new Identifier(string);
		this.processors = ImmutableList.copyOf(list);
	}

	@Deprecated
	public SinglePoolElement(String string) {
		this(string, ImmutableList.of());
	}

	public SinglePoolElement(Dynamic<?> dynamic) {
		super(dynamic);
		this.location = new Identifier(dynamic.get("location").asString(""));
		this.processors = ImmutableList.copyOf(
			dynamic.get("processors")
				.asList(dynamicx -> DynamicDeserializer.deserialize(dynamicx, Registry.STRUCTURE_PROCESSOR, "processor_type", NopStructureProcessor.INSTANCE))
		);
	}

	public List<Structure.StructureBlockInfo> method_16614(StructureManager structureManager, BlockPos blockPos, BlockRotation blockRotation, boolean bl) {
		Structure structure = structureManager.method_15091(this.location);
		List<Structure.StructureBlockInfo> list = structure.method_15165(blockPos, new StructurePlacementData().setRotation(blockRotation), Blocks.field_10465, bl);
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
	public List<Structure.StructureBlockInfo> method_16627(StructureManager structureManager, BlockPos blockPos, BlockRotation blockRotation, Random random) {
		Structure structure = structureManager.method_15091(this.location);
		List<Structure.StructureBlockInfo> list = structure.method_15165(blockPos, new StructurePlacementData().setRotation(blockRotation), Blocks.field_16540, true);
		Collections.shuffle(list, random);
		return list;
	}

	@Override
	public MutableIntBoundingBox method_16628(StructureManager structureManager, BlockPos blockPos, BlockRotation blockRotation) {
		Structure structure = structureManager.method_15091(this.location);
		return structure.calculateBoundingBox(new StructurePlacementData().setRotation(blockRotation), blockPos);
	}

	@Override
	public boolean method_16626(
		StructureManager structureManager, IWorld iWorld, BlockPos blockPos, BlockRotation blockRotation, MutableIntBoundingBox mutableIntBoundingBox, Random random
	) {
		Structure structure = structureManager.method_15091(this.location);
		StructurePlacementData structurePlacementData = this.method_16616(blockRotation, mutableIntBoundingBox);
		if (!structure.method_15172(iWorld, blockPos, structurePlacementData, 18)) {
			return false;
		} else {
			for (Structure.StructureBlockInfo structureBlockInfo : Structure.process(
				iWorld, blockPos, structurePlacementData, this.method_16614(structureManager, blockPos, blockRotation, false)
			)) {
				this.method_16756(iWorld, structureBlockInfo, blockPos, blockRotation, random, mutableIntBoundingBox);
			}

			return true;
		}
	}

	protected StructurePlacementData method_16616(BlockRotation blockRotation, MutableIntBoundingBox mutableIntBoundingBox) {
		StructurePlacementData structurePlacementData = new StructurePlacementData();
		structurePlacementData.setBoundingBox(mutableIntBoundingBox);
		structurePlacementData.setRotation(blockRotation);
		structurePlacementData.method_15131(true);
		structurePlacementData.setIgnoreEntities(false);
		structurePlacementData.method_16184(BlockIgnoreStructureProcessor.IGNORE_AIR_AND_STRUCTURE_BLOCKS);
		structurePlacementData.method_16184(JigsawReplacementStructureProcessor.INSTANCE);
		this.processors.forEach(structurePlacementData::method_16184);
		this.getProjection().getProcessors().forEach(structurePlacementData::method_16184);
		return structurePlacementData;
	}

	@Override
	public StructurePoolElementType method_16757() {
		return StructurePoolElementType.field_16973;
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

	public String toString() {
		return "Single[" + this.location + "]";
	}
}
