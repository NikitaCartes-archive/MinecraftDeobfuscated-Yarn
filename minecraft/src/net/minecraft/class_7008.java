package net.minecraft;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.StructureContext;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.StructureWeightType;

public class class_7008 extends PoolStructurePiece {
	public StructureWeightType field_36934;

	public class_7008(
		StructureManager structureManager,
		StructurePoolElement structurePoolElement,
		BlockPos blockPos,
		int i,
		BlockRotation blockRotation,
		BlockBox blockBox,
		StructureWeightType structureWeightType
	) {
		super(structureManager, structurePoolElement, blockPos, i, blockRotation, blockBox);
		this.field_36934 = structureWeightType;
	}

	public class_7008(StructureContext structureContext, NbtCompound nbtCompound) {
		super(structureContext, nbtCompound);
		this.field_36934 = StructureWeightType.valueOf(nbtCompound.getString("noise_effect"));
	}

	@Override
	protected void writeNbt(StructureContext context, NbtCompound nbt) {
		super.writeNbt(context, nbt);
		nbt.putString("noise_effect", this.field_36934.name());
	}

	@Override
	public StructureWeightType getWeightType() {
		return this.field_36934;
	}
}
