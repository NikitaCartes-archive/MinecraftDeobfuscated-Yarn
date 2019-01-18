package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Random;
import net.minecraft.sortme.Structure;
import net.minecraft.sortme.StructurePoolElement;
import net.minecraft.sortme.structures.StructureManager;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;

public class class_3782 extends class_3784 {
	private final List<class_3784> elements;

	public class_3782(List<class_3784> list) {
		if (list.isEmpty()) {
			throw new IllegalArgumentException("Elements are empty");
		} else {
			this.elements = list;
		}
	}

	public class_3782(Dynamic<?> dynamic) {
		this(dynamic.get("elements").asList(dynamicx -> class_3817.deserialize(dynamicx, Registry.STRUCTURE_POOL_ELEMENT, "element_type", class_3777.field_16663)));
	}

	@Override
	public List<Structure.StructureBlockInfo> method_16627(StructureManager structureManager, BlockPos blockPos, Rotation rotation, Random random) {
		return ((class_3784)this.elements.get(0)).method_16627(structureManager, blockPos, rotation, random);
	}

	@Override
	public MutableIntBoundingBox method_16628(StructureManager structureManager, BlockPos blockPos, Rotation rotation) {
		MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.maxSize();

		for (class_3784 lv : this.elements) {
			MutableIntBoundingBox mutableIntBoundingBox2 = lv.method_16628(structureManager, blockPos, rotation);
			mutableIntBoundingBox.setFrom(mutableIntBoundingBox2);
		}

		return mutableIntBoundingBox;
	}

	@Override
	public boolean method_16626(IWorld iWorld, BlockPos blockPos, Rotation rotation, MutableIntBoundingBox mutableIntBoundingBox, Random random) {
		for (class_3784 lv : this.elements) {
			if (!lv.method_16626(iWorld, blockPos, rotation, mutableIntBoundingBox, random)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public StructurePoolElement method_16757() {
		return StructurePoolElement.field_16974;
	}

	@Override
	public class_3784 method_16622(class_3785.Projection projection) {
		this.elements.forEach(arg -> arg.method_16622(projection));
		return this;
	}

	@Override
	public <T> Dynamic<T> method_16625(DynamicOps<T> dynamicOps) {
		T object = dynamicOps.createList(this.elements.stream().map(arg -> arg.method_16755(dynamicOps).getValue()));
		return new Dynamic<>(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("elements"), object)));
	}
}
