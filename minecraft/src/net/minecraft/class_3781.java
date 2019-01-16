package net.minecraft;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.StructureMode;
import net.minecraft.sortme.StructurePoolElement;
import net.minecraft.sortme.structures.StructureManager;
import net.minecraft.sortme.structures.processor.AbstractStructureProcessor;
import net.minecraft.sortme.structures.processor.BlockIgnoreStructureProcessor;
import net.minecraft.sortme.structures.processor.JigsawReplacementStructureProcessor;
import net.minecraft.sortme.structures.processor.NopStructureProcessor;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;

public class class_3781 extends class_3784 {
	protected final Identifier location;
	protected final ImmutableList<AbstractStructureProcessor> processors;

	public class_3781(String string, List<AbstractStructureProcessor> list) {
		this.location = new Identifier(string);
		this.processors = ImmutableList.copyOf(list);
	}

	public class_3781(String string) {
		this(string, ImmutableList.of());
	}

	public class_3781(Dynamic<?> dynamic) {
		this(
			dynamic.get("location").asString(""),
			dynamic.get("processors")
				.asList(dynamicx -> class_3817.deserialize(dynamicx, Registry.STRUCTURE_PROCESSOR, "processor_type", NopStructureProcessor.INSTANCE))
		);
	}

	public List<class_3499.class_3501> method_16614(StructureManager structureManager, BlockPos blockPos, Rotation rotation, boolean bl) {
		class_3499 lv = structureManager.method_15091(this.location);
		List<class_3499.class_3501> list = lv.method_15165(blockPos, new class_3492().method_15123(rotation), Blocks.field_10465, bl);
		List<class_3499.class_3501> list2 = Lists.<class_3499.class_3501>newArrayList();

		for (class_3499.class_3501 lv2 : list) {
			if (lv2.field_15595 != null) {
				StructureMode structureMode = StructureMode.valueOf(lv2.field_15595.getString("mode"));
				if (structureMode == StructureMode.field_12696) {
					list2.add(lv2);
				}
			}
		}

		return list2;
	}

	@Override
	public List<class_3499.class_3501> method_16627(StructureManager structureManager, BlockPos blockPos, Rotation rotation, Random random) {
		class_3499 lv = structureManager.method_15091(this.location);
		List<class_3499.class_3501> list = lv.method_15165(blockPos, new class_3492().method_15123(rotation), Blocks.field_16540, true);
		Collections.shuffle(list, random);
		return list;
	}

	@Override
	public MutableIntBoundingBox method_16628(StructureManager structureManager, BlockPos blockPos, Rotation rotation) {
		class_3499 lv = structureManager.method_15091(this.location);
		return lv.method_16187(new class_3492().method_15123(rotation), blockPos);
	}

	@Override
	public boolean method_16626(IWorld iWorld, BlockPos blockPos, Rotation rotation, MutableIntBoundingBox mutableIntBoundingBox, Random random) {
		StructureManager structureManager = iWorld.getSaveHandler().getStructureManager();
		class_3499 lv = structureManager.method_15091(this.location);
		class_3492 lv2 = this.method_16616(rotation, mutableIntBoundingBox);
		if (!lv.method_15172(iWorld, blockPos, lv2, 18)) {
			return false;
		} else {
			for (class_3499.class_3501 lv3 : class_3499.method_16446(iWorld, blockPos, lv2, this.method_16614(structureManager, blockPos, rotation, false))) {
				this.method_16756(iWorld, lv3, blockPos, rotation, random, mutableIntBoundingBox);
			}

			return true;
		}
	}

	protected class_3492 method_16616(Rotation rotation, MutableIntBoundingBox mutableIntBoundingBox) {
		class_3492 lv = new class_3492();
		lv.method_15126(mutableIntBoundingBox);
		lv.method_15123(rotation);
		lv.method_15131(true);
		lv.method_15133(false);
		lv.method_16184(BlockIgnoreStructureProcessor.field_16721);
		lv.method_16184(JigsawReplacementStructureProcessor.INSTANCE);
		this.processors.forEach(lv::method_16184);
		this.method_16624().method_16636().forEach(lv::method_16184);
		return lv;
	}

	@Override
	public StructurePoolElement method_16757() {
		return StructurePoolElement.field_16973;
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
					dynamicOps.createList(this.processors.stream().map(abstractStructureProcessor -> abstractStructureProcessor.method_16771(dynamicOps).getValue()))
				)
			)
		);
	}
}
