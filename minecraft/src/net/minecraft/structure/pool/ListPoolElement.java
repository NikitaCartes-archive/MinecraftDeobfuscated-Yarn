package net.minecraft.structure.pool;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Random;
import net.minecraft.class_3817;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;

public class ListPoolElement extends StructurePoolElement {
	private final List<StructurePoolElement> elements;

	public ListPoolElement(List<StructurePoolElement> list) {
		if (list.isEmpty()) {
			throw new IllegalArgumentException("Elements are empty");
		} else {
			this.elements = list;
		}
	}

	public ListPoolElement(Dynamic<?> dynamic) {
		this(dynamic.get("elements").asList(dynamicx -> class_3817.deserialize(dynamicx, Registry.STRUCTURE_POOL_ELEMENT, "element_type", EmptyPoolElement.INSTANCE)));
	}

	@Override
	public List<Structure.StructureBlockInfo> getStructureBlockInfos(StructureManager structureManager, BlockPos blockPos, Rotation rotation, Random random) {
		return ((StructurePoolElement)this.elements.get(0)).getStructureBlockInfos(structureManager, blockPos, rotation, random);
	}

	@Override
	public MutableIntBoundingBox getBoundingBox(StructureManager structureManager, BlockPos blockPos, Rotation rotation) {
		MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.empty();

		for (StructurePoolElement structurePoolElement : this.elements) {
			MutableIntBoundingBox mutableIntBoundingBox2 = structurePoolElement.getBoundingBox(structureManager, blockPos, rotation);
			mutableIntBoundingBox.setFrom(mutableIntBoundingBox2);
		}

		return mutableIntBoundingBox;
	}

	@Override
	public boolean generate(
		StructureManager structureManager, IWorld iWorld, BlockPos blockPos, Rotation rotation, MutableIntBoundingBox mutableIntBoundingBox, Random random
	) {
		for (StructurePoolElement structurePoolElement : this.elements) {
			if (!structurePoolElement.generate(structureManager, iWorld, blockPos, rotation, mutableIntBoundingBox, random)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public StructurePoolElementType getType() {
		return StructurePoolElementType.LIST_POOL_ELEMENT;
	}

	@Override
	public StructurePoolElement setProjection(StructurePool.Projection projection) {
		this.elements.forEach(structurePoolElement -> structurePoolElement.setProjection(projection));
		return this;
	}

	@Override
	public <T> Dynamic<T> method_16625(DynamicOps<T> dynamicOps) {
		T object = dynamicOps.createList(this.elements.stream().map(structurePoolElement -> structurePoolElement.method_16755(dynamicOps).getValue()));
		return new Dynamic<>(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("elements"), object)));
	}
}
