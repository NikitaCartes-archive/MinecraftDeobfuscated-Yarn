package net.minecraft.structure;

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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.IWorld;

public class SwampHutGenerator extends StructurePieceWithDimensions {
	private boolean hasWitch;
	private boolean hasCat;

	public SwampHutGenerator(Random random, int i, int j) {
		super(StructurePieceType.TE_S_H, random, i, 64, j, 7, 7, 9);
	}

	public SwampHutGenerator(StructureManager structureManager, CompoundTag compoundTag) {
		super(StructurePieceType.TE_S_H, compoundTag);
		this.hasWitch = compoundTag.getBoolean("Witch");
		this.hasCat = compoundTag.getBoolean("Cat");
	}

	@Override
	protected void toNbt(CompoundTag compoundTag) {
		super.toNbt(compoundTag);
		compoundTag.putBoolean("Witch", this.hasWitch);
		compoundTag.putBoolean("Cat", this.hasCat);
	}

	@Override
	public boolean generate(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
		if (!this.method_14839(iWorld, mutableIntBoundingBox, 0)) {
			return false;
		} else {
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 1, 1, 5, 1, 7, Blocks.SPRUCE_PLANKS.getDefaultState(), Blocks.SPRUCE_PLANKS.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 4, 2, 5, 4, 7, Blocks.SPRUCE_PLANKS.getDefaultState(), Blocks.SPRUCE_PLANKS.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 2, 1, 0, 4, 1, 0, Blocks.SPRUCE_PLANKS.getDefaultState(), Blocks.SPRUCE_PLANKS.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 2, 2, 2, 3, 3, 2, Blocks.SPRUCE_PLANKS.getDefaultState(), Blocks.SPRUCE_PLANKS.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 2, 3, 1, 3, 6, Blocks.SPRUCE_PLANKS.getDefaultState(), Blocks.SPRUCE_PLANKS.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 5, 2, 3, 5, 3, 6, Blocks.SPRUCE_PLANKS.getDefaultState(), Blocks.SPRUCE_PLANKS.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 2, 2, 7, 4, 3, 7, Blocks.SPRUCE_PLANKS.getDefaultState(), Blocks.SPRUCE_PLANKS.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 0, 2, 1, 3, 2, Blocks.OAK_LOG.getDefaultState(), Blocks.OAK_LOG.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 5, 0, 2, 5, 3, 2, Blocks.OAK_LOG.getDefaultState(), Blocks.OAK_LOG.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 0, 7, 1, 3, 7, Blocks.OAK_LOG.getDefaultState(), Blocks.OAK_LOG.getDefaultState(), false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 5, 0, 7, 5, 3, 7, Blocks.OAK_LOG.getDefaultState(), Blocks.OAK_LOG.getDefaultState(), false);
			this.addBlock(iWorld, Blocks.OAK_FENCE.getDefaultState(), 2, 3, 2, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.OAK_FENCE.getDefaultState(), 3, 3, 7, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.AIR.getDefaultState(), 1, 3, 4, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.AIR.getDefaultState(), 5, 3, 4, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.AIR.getDefaultState(), 5, 3, 5, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.POTTED_RED_MUSHROOM.getDefaultState(), 1, 3, 5, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.CRAFTING_TABLE.getDefaultState(), 3, 2, 6, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.CAULDRON.getDefaultState(), 4, 2, 6, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.OAK_FENCE.getDefaultState(), 1, 2, 1, mutableIntBoundingBox);
			this.addBlock(iWorld, Blocks.OAK_FENCE.getDefaultState(), 5, 2, 1, mutableIntBoundingBox);
			BlockState blockState = Blocks.SPRUCE_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.NORTH);
			BlockState blockState2 = Blocks.SPRUCE_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.EAST);
			BlockState blockState3 = Blocks.SPRUCE_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.WEST);
			BlockState blockState4 = Blocks.SPRUCE_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.SOUTH);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 4, 1, 6, 4, 1, blockState, blockState, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 4, 2, 0, 4, 7, blockState2, blockState2, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 6, 4, 2, 6, 4, 7, blockState3, blockState3, false);
			this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 4, 8, 6, 4, 8, blockState4, blockState4, false);
			this.addBlock(iWorld, blockState.with(StairsBlock.SHAPE, StairShape.OUTER_RIGHT), 0, 4, 1, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState.with(StairsBlock.SHAPE, StairShape.OUTER_LEFT), 6, 4, 1, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState4.with(StairsBlock.SHAPE, StairShape.OUTER_LEFT), 0, 4, 8, mutableIntBoundingBox);
			this.addBlock(iWorld, blockState4.with(StairsBlock.SHAPE, StairShape.OUTER_RIGHT), 6, 4, 8, mutableIntBoundingBox);

			for (int i = 2; i <= 7; i += 5) {
				for (int j = 1; j <= 5; j += 4) {
					this.method_14936(iWorld, Blocks.OAK_LOG.getDefaultState(), j, -1, i, mutableIntBoundingBox);
				}
			}

			if (!this.hasWitch) {
				int i = this.applyXTransform(2, 5);
				int j = this.applyYTransform(2);
				int k = this.applyZTransform(2, 5);
				if (mutableIntBoundingBox.contains(new BlockPos(i, j, k))) {
					this.hasWitch = true;
					WitchEntity witchEntity = EntityType.WITCH.create(iWorld.getWorld());
					witchEntity.setPersistent();
					witchEntity.setPositionAndAngles((double)i + 0.5, (double)j, (double)k + 0.5, 0.0F, 0.0F);
					witchEntity.initialize(iWorld, iWorld.getLocalDifficulty(new BlockPos(i, j, k)), SpawnType.STRUCTURE, null, null);
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
			if (mutableIntBoundingBox.contains(new BlockPos(i, j, k))) {
				this.hasCat = true;
				CatEntity catEntity = EntityType.CAT.create(iWorld.getWorld());
				catEntity.setPersistent();
				catEntity.setPositionAndAngles((double)i + 0.5, (double)j, (double)k + 0.5, 0.0F, 0.0F);
				catEntity.initialize(iWorld, iWorld.getLocalDifficulty(new BlockPos(i, j, k)), SpawnType.STRUCTURE, null, null);
				iWorld.spawnEntity(catEntity);
			}
		}
	}
}
