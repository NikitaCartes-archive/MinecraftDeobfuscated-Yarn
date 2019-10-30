package net.minecraft.structure.pool;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.DynamicDeserializer;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;

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
	public List<Structure.StructureBlockInfo> getStructureBlockInfos(StructureManager structureManager, BlockPos pos, BlockRotation rotation, Random random) {
		return ((StructurePoolElement)this.elements.get(0)).getStructureBlockInfos(structureManager, pos, rotation, random);
	}

	@Override
	public BlockBox getBoundingBox(StructureManager structureManager, BlockPos pos, BlockRotation rotation) {
		BlockBox blockBox = BlockBox.empty();

		for (StructurePoolElement structurePoolElement : this.elements) {
			BlockBox blockBox2 = structurePoolElement.getBoundingBox(structureManager, pos, rotation);
			blockBox.encompass(blockBox2);
		}

		return blockBox;
	}

	@Override
	public boolean generate(
		StructureManager structureManager,
		IWorld world,
		ChunkGenerator<?> chunkGenerator,
		BlockPos blockPos,
		BlockRotation blockRotation,
		BlockBox blockBox,
		Random random
	) {
		for (StructurePoolElement structurePoolElement : this.elements) {
			if (!structurePoolElement.generate(structureManager, world, chunkGenerator, blockPos, blockRotation, blockBox, random)) {
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
		super.setProjection(projection);
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
		this.elements.forEach(structurePoolElement -> structurePoolElement.setProjection(projection));
	}
}
