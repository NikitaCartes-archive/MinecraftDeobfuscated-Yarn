package net.minecraft.structure.pool;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.IWorld;

public class EmptyPoolElement extends StructurePoolElement {
	public static final EmptyPoolElement INSTANCE = new EmptyPoolElement();

	private EmptyPoolElement() {
		super(StructurePool.Projection.TERRAIN_MATCHING);
	}

	@Override
	public List<Structure.StructureBlockInfo> getStructureBlockInfos(
		StructureManager structureManager, BlockPos blockPos, BlockRotation blockRotation, Random random
	) {
		return Collections.emptyList();
	}

	@Override
	public MutableIntBoundingBox getBoundingBox(StructureManager structureManager, BlockPos blockPos, BlockRotation blockRotation) {
		return MutableIntBoundingBox.empty();
	}

	@Override
	public boolean generate(
		StructureManager structureManager, IWorld iWorld, BlockPos blockPos, BlockRotation blockRotation, MutableIntBoundingBox mutableIntBoundingBox, Random random
	) {
		return true;
	}

	@Override
	public StructurePoolElementType getType() {
		return StructurePoolElementType.EMPTY_POOL_ELEMENT;
	}

	@Override
	public <T> Dynamic<T> method_16625(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(dynamicOps, dynamicOps.emptyMap());
	}

	public String toString() {
		return "Empty";
	}
}
