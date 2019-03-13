package net.minecraft.structure.pool;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.DynamicDeserializer;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;

public class ListPoolElement extends StructurePoolElement {
	private final List<StructurePoolElement> elements;

	@Deprecated
	public ListPoolElement(List<StructurePoolElement> list) {
		this(list, StructurePool.Projection.RIGID);
	}

	public ListPoolElement(List<StructurePoolElement> list, StructurePool.Projection projection) {
		super(projection);
		if (list.isEmpty()) {
			throw new IllegalArgumentException("Elements are empty");
		} else {
			this.elements = list;
			this.method_19307(projection);
		}
	}

	public ListPoolElement(Dynamic<?> dynamic) {
		super(dynamic);
		List<StructurePoolElement> list = dynamic.get("elements")
			.asList(dynamicx -> DynamicDeserializer.deserialize(dynamicx, Registry.STRUCTURE_POOL_ELEMENT, "element_type", EmptyPoolElement.INSTANCE));
		if (list.isEmpty()) {
			throw new IllegalArgumentException("Elements are empty");
		} else {
			this.elements = list;
		}
	}

	@Override
	public List<Structure.StructureBlockInfo> method_16627(StructureManager structureManager, BlockPos blockPos, Rotation rotation, Random random) {
		return ((StructurePoolElement)this.elements.get(0)).method_16627(structureManager, blockPos, rotation, random);
	}

	@Override
	public MutableIntBoundingBox method_16628(StructureManager structureManager, BlockPos blockPos, Rotation rotation) {
		MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.empty();

		for (StructurePoolElement structurePoolElement : this.elements) {
			MutableIntBoundingBox mutableIntBoundingBox2 = structurePoolElement.method_16628(structureManager, blockPos, rotation);
			mutableIntBoundingBox.setFrom(mutableIntBoundingBox2);
		}

		return mutableIntBoundingBox;
	}

	@Override
	public boolean method_16626(
		StructureManager structureManager, IWorld iWorld, BlockPos blockPos, Rotation rotation, MutableIntBoundingBox mutableIntBoundingBox, Random random
	) {
		for (StructurePoolElement structurePoolElement : this.elements) {
			if (!structurePoolElement.method_16626(structureManager, iWorld, blockPos, rotation, mutableIntBoundingBox, random)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public StructurePoolElementType method_16757() {
		return StructurePoolElementType.LIST_POOL_ELEMENT;
	}

	@Override
	public StructurePoolElement method_16622(StructurePool.Projection projection) {
		super.method_16622(projection);
		this.method_19307(projection);
		return this;
	}

	@Override
	public <T> Dynamic<T> method_16625(DynamicOps<T> dynamicOps) {
		T object = dynamicOps.createList(this.elements.stream().map(structurePoolElement -> structurePoolElement.method_16755(dynamicOps).getValue()));
		return new Dynamic<>(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("elements"), object)));
	}

	public String toString() {
		return "List[" + (String)this.elements.stream().map(Object::toString).collect(Collectors.joining(", ")) + "]";
	}

	private void method_19307(StructurePool.Projection projection) {
		this.elements.forEach(structurePoolElement -> structurePoolElement.method_16622(projection));
	}
}
