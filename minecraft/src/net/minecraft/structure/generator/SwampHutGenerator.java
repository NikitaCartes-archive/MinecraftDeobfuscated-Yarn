package net.minecraft.structure.generator;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.enums.StairShape;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.StructurePieceWithDimensions;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.ChunkPos;

public class SwampHutGenerator extends StructurePieceWithDimensions {
	private boolean hasWitch;
	private boolean hasCat;

	public SwampHutGenerator(Random random, int i, int j) {
		super(StructurePieceType.SWAMP_HUT, random, i, 64, j, 7, 7, 9);
	}

	public SwampHutGenerator(StructureManager structureManager, CompoundTag compoundTag) {
		super(StructurePieceType.SWAMP_HUT, compoundTag);
		this.hasWitch = compoundTag.getBoolean("Witch");
		this.hasCat = compoundTag.getBoolean("Cat");
	}

	@Override
	protected void method_14943(CompoundTag compoundTag) {
		super.method_14943(compoundTag);
		compoundTag.putBoolean("Witch", this.hasWitch);
		compoundTag.putBoolean("Cat", this.hasCat);
	}

	@Override
	public boolean generate(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
		if (!this.method_14839(iWorld, mutableIntBoundingBox, 0)) {
			return false;
		} else {
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 1, 1, 5, 1, 7, Blocks.field_9975.method_9564(), Blocks.field_9975.method_9564(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 4, 2, 5, 4, 7, Blocks.field_9975.method_9564(), Blocks.field_9975.method_9564(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 2, 1, 0, 4, 1, 0, Blocks.field_9975.method_9564(), Blocks.field_9975.method_9564(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 2, 2, 2, 3, 3, 2, Blocks.field_9975.method_9564(), Blocks.field_9975.method_9564(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 2, 3, 1, 3, 6, Blocks.field_9975.method_9564(), Blocks.field_9975.method_9564(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 5, 2, 3, 5, 3, 6, Blocks.field_9975.method_9564(), Blocks.field_9975.method_9564(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 2, 2, 7, 4, 3, 7, Blocks.field_9975.method_9564(), Blocks.field_9975.method_9564(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 0, 2, 1, 3, 2, Blocks.field_10431.method_9564(), Blocks.field_10431.method_9564(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 5, 0, 2, 5, 3, 2, Blocks.field_10431.method_9564(), Blocks.field_10431.method_9564(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 0, 7, 1, 3, 7, Blocks.field_10431.method_9564(), Blocks.field_10431.method_9564(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 5, 0, 7, 5, 3, 7, Blocks.field_10431.method_9564(), Blocks.field_10431.method_9564(), false);
			this.addBlock(iWorld, Blocks.field_10620.method_9564(), 2, 3, 2, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10620.method_9564(), 3, 3, 7, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10124.method_9564(), 1, 3, 4, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10124.method_9564(), 5, 3, 4, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10124.method_9564(), 5, 3, 5, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10138.method_9564(), 1, 3, 5, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_9980.method_9564(), 3, 2, 6, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10593.method_9564(), 4, 2, 6, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10620.method_9564(), 1, 2, 1, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.field_10620.method_9564(), 5, 2, 1, mutableIntBoundingBox);
			BlockState blockState = Blocks.field_10569.method_9564().method_11657(StairsBlock.field_11571, Direction.NORTH);
			BlockState blockState2 = Blocks.field_10569.method_9564().method_11657(StairsBlock.field_11571, Direction.EAST);
			BlockState blockState3 = Blocks.field_10569.method_9564().method_11657(StairsBlock.field_11571, Direction.WEST);
			BlockState blockState4 = Blocks.field_10569.method_9564().method_11657(StairsBlock.field_11571, Direction.SOUTH);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 4, 1, 6, 4, 1, blockState, blockState, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 4, 2, 0, 4, 7, blockState2, blockState2, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 6, 4, 2, 6, 4, 7, blockState3, blockState3, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 4, 8, 6, 4, 8, blockState4, blockState4, false);
			this.addBlock(iWorld, blockState.method_11657(StairsBlock.field_11565, StairShape.field_12709), 0, 4, 1, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState.method_11657(StairsBlock.field_11565, StairShape.field_12708), 6, 4, 1, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState4.method_11657(StairsBlock.field_11565, StairShape.field_12708), 0, 4, 8, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState4.method_11657(StairsBlock.field_11565, StairShape.field_12709), 6, 4, 8, mutableIntBoundingBox);

			for (int i = 2; i <= 7; i += 5) {
				for (int j = 1; j <= 5; j += 4) {
					this.method_14936(iWorld, Blocks.field_10431.method_9564(), j, -1, i, mutableIntBoundingBox);
				}
			}

			if (!this.hasWitch) {
				int i = this.applyXTransform(2, 5);
				int j = this.applyYTransform(2);
				int k = this.applyZTransform(2, 5);
				if (mutableIntBoundingBox.method_14662(new BlockPos(i, j, k))) {
					this.hasWitch = true;
					WitchEntity witchEntity = EntityType.WITCH.method_5883(iWorld.getWorld());
					witchEntity.setPersistent();
					witchEntity.setPositionAndAngles((double)i + 0.5, (double)j, (double)k + 0.5, 0.0F, 0.0F);
					witchEntity.method_5943(iWorld, iWorld.method_8404(new BlockPos(i, j, k)), SpawnType.field_16474, null, null);
					iWorld.spawnEntity(witchEntity);
				}
			}

			this.method_16181(iWorld, mutableIntBoundingBox);
			return true;
		}
	}

	private void method_16181(IWorld iWorld, MutableIntBoundingBox mutableIntBoundingBox) {
		if (!this.hasCat) {
			int i = this.applyXTransform(2, 5);
			int j = this.applyYTransform(2);
			int k = this.applyZTransform(2, 5);
			if (mutableIntBoundingBox.method_14662(new BlockPos(i, j, k))) {
				this.hasCat = true;
				CatEntity catEntity = EntityType.CAT.method_5883(iWorld.getWorld());
				catEntity.setPersistent();
				catEntity.setPositionAndAngles((double)i + 0.5, (double)j, (double)k + 0.5, 0.0F, 0.0F);
				catEntity.method_5943(iWorld, iWorld.method_8404(new BlockPos(i, j, k)), SpawnType.field_16474, null, null);
				iWorld.spawnEntity(catEntity);
			}
		}
	}
}
