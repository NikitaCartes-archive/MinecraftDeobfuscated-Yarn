package net.minecraft.structure.pool;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class ListPoolElement extends StructurePoolElement {
	public static final Codec<ListPoolElement> field_24950 = RecordCodecBuilder.create(
		instance -> instance.group(
					StructurePoolElement.field_24953.listOf().fieldOf("elements").forGetter(listPoolElement -> listPoolElement.elements), method_28883()
				)
				.apply(instance, ListPoolElement::new)
	);
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
			this.setAllElementsProjection(projection);
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
		ServerWorldAccess serverWorldAccess,
		StructureAccessor structureAccessor,
		ChunkGenerator chunkGenerator,
		BlockPos blockPos,
		BlockPos blockPos2,
		BlockRotation blockRotation,
		BlockBox blockBox,
		Random random,
		boolean bl
	) {
		for (StructurePoolElement structurePoolElement : this.elements) {
			if (!structurePoolElement.generate(
				structureManager, serverWorldAccess, structureAccessor, chunkGenerator, blockPos, blockPos2, blockRotation, blockBox, random, bl
			)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public StructurePoolElementType<?> getType() {
		return StructurePoolElementType.LIST_POOL_ELEMENT;
	}

	@Override
	public StructurePoolElement setProjection(StructurePool.Projection projection) {
		super.setProjection(projection);
		this.setAllElementsProjection(projection);
		return this;
	}

	public String toString() {
		return "List[" + (String)this.elements.stream().map(Object::toString).collect(Collectors.joining(", ")) + "]";
	}

	private void setAllElementsProjection(StructurePool.Projection projection) {
		this.elements.forEach(structurePoolElement -> structurePoolElement.setProjection(projection));
	}
}
