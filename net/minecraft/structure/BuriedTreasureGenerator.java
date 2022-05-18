/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.structure;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.StructureContext;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class BuriedTreasureGenerator {

    public static class Piece
    extends StructurePiece {
        public Piece(BlockPos pos) {
            super(StructurePieceType.BURIED_TREASURE, 0, new BlockBox(pos));
        }

        public Piece(NbtCompound nbt) {
            super(StructurePieceType.BURIED_TREASURE, nbt);
        }

        @Override
        protected void writeNbt(StructureContext context, NbtCompound nbt) {
        }

        @Override
        public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pos) {
            int i = world.getTopY(Heightmap.Type.OCEAN_FLOOR_WG, this.boundingBox.getMinX(), this.boundingBox.getMinZ());
            BlockPos.Mutable mutable = new BlockPos.Mutable(this.boundingBox.getMinX(), i, this.boundingBox.getMinZ());
            while (mutable.getY() > world.getBottomY()) {
                BlockState blockState = world.getBlockState(mutable);
                BlockState blockState2 = world.getBlockState((BlockPos)mutable.down());
                if (blockState2 == Blocks.SANDSTONE.getDefaultState() || blockState2 == Blocks.STONE.getDefaultState() || blockState2 == Blocks.ANDESITE.getDefaultState() || blockState2 == Blocks.GRANITE.getDefaultState() || blockState2 == Blocks.DIORITE.getDefaultState()) {
                    BlockState blockState3 = blockState.isAir() || this.isLiquid(blockState) ? Blocks.SAND.getDefaultState() : blockState;
                    for (Direction direction : Direction.values()) {
                        Vec3i blockPos = mutable.offset(direction);
                        BlockState blockState4 = world.getBlockState((BlockPos)blockPos);
                        if (!blockState4.isAir() && !this.isLiquid(blockState4)) continue;
                        BlockPos blockPos2 = ((BlockPos)blockPos).down();
                        BlockState blockState5 = world.getBlockState(blockPos2);
                        if ((blockState5.isAir() || this.isLiquid(blockState5)) && direction != Direction.UP) {
                            world.setBlockState((BlockPos)blockPos, blockState2, Block.NOTIFY_ALL);
                            continue;
                        }
                        world.setBlockState((BlockPos)blockPos, blockState3, Block.NOTIFY_ALL);
                    }
                    this.boundingBox = new BlockBox(mutable);
                    this.addChest(world, chunkBox, random, mutable, LootTables.BURIED_TREASURE_CHEST, null);
                    return;
                }
                mutable.move(0, -1, 0);
            }
        }

        private boolean isLiquid(BlockState state) {
            return state == Blocks.WATER.getDefaultState() || state == Blocks.LAVA.getDefaultState();
        }
    }
}

