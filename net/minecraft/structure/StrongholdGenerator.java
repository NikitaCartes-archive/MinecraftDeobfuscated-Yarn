/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.structure;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.block.AbstractButtonBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.EndPortalFrameBlock;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.LadderBlock;
import net.minecraft.block.PaneBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.block.enums.SlabType;
import net.minecraft.entity.EntityType;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.jetbrains.annotations.Nullable;

public class StrongholdGenerator {
    private static final PieceSetting[] ALL_PIECE_SETTINGS = new PieceSetting[]{new PieceSetting(Corridor.class, 40, 0), new PieceSetting(PrisonHall.class, 5, 5), new PieceSetting(LeftTurn.class, 20, 0), new PieceSetting(RightTurn.class, 20, 0), new PieceSetting(SquareRoom.class, 10, 6), new PieceSetting(Stairs.class, 5, 5), new PieceSetting(SpiralStaircase.class, 5, 5), new PieceSetting(FiveWayCrossing.class, 5, 4), new PieceSetting(ChestCorridor.class, 5, 4), new PieceSetting(Library.class, 10, 2){

        @Override
        public boolean canGenerate(int depth) {
            return super.canGenerate(depth) && depth > 4;
        }
    }, new PieceSetting(PortalRoom.class, 20, 1){

        @Override
        public boolean canGenerate(int depth) {
            return super.canGenerate(depth) && depth > 5;
        }
    }};
    private static List<PieceSetting> possiblePieceSettings;
    private static Class<? extends Piece> activePieceType;
    private static int field_15264;
    private static final StoneBrickRandomizer STONE_BRICK_RANDOMIZER;

    public static void init() {
        possiblePieceSettings = Lists.newArrayList();
        for (PieceSetting pieceSetting : ALL_PIECE_SETTINGS) {
            pieceSetting.generatedCount = 0;
            possiblePieceSettings.add(pieceSetting);
        }
        activePieceType = null;
    }

    private static boolean method_14852() {
        boolean bl = false;
        field_15264 = 0;
        for (PieceSetting pieceSetting : possiblePieceSettings) {
            if (pieceSetting.limit > 0 && pieceSetting.generatedCount < pieceSetting.limit) {
                bl = true;
            }
            field_15264 += pieceSetting.field_15278;
        }
        return bl;
    }

    private static Piece method_14847(Class<? extends Piece> class_, List<StructurePiece> list, Random random, int i, int j, int k, @Nullable Direction direction, int l) {
        Piece piece = null;
        if (class_ == Corridor.class) {
            piece = Corridor.method_14867(list, random, i, j, k, direction, l);
        } else if (class_ == PrisonHall.class) {
            piece = PrisonHall.method_14864(list, random, i, j, k, direction, l);
        } else if (class_ == LeftTurn.class) {
            piece = LeftTurn.method_14859(list, random, i, j, k, direction, l);
        } else if (class_ == RightTurn.class) {
            piece = RightTurn.method_16652(list, random, i, j, k, direction, l);
        } else if (class_ == SquareRoom.class) {
            piece = SquareRoom.method_14865(list, random, i, j, k, direction, l);
        } else if (class_ == Stairs.class) {
            piece = Stairs.method_14868(list, random, i, j, k, direction, l);
        } else if (class_ == SpiralStaircase.class) {
            piece = SpiralStaircase.method_14866(list, random, i, j, k, direction, l);
        } else if (class_ == FiveWayCrossing.class) {
            piece = FiveWayCrossing.method_14858(list, random, i, j, k, direction, l);
        } else if (class_ == ChestCorridor.class) {
            piece = ChestCorridor.method_14856(list, random, i, j, k, direction, l);
        } else if (class_ == Library.class) {
            piece = Library.method_14860(list, random, i, j, k, direction, l);
        } else if (class_ == PortalRoom.class) {
            piece = PortalRoom.method_14863(list, i, j, k, direction, l);
        }
        return piece;
    }

    private static Piece method_14851(Start start, List<StructurePiece> list, Random random, int i, int j, int k, Direction direction, int l) {
        if (!StrongholdGenerator.method_14852()) {
            return null;
        }
        if (activePieceType != null) {
            Piece piece = StrongholdGenerator.method_14847(activePieceType, list, random, i, j, k, direction, l);
            activePieceType = null;
            if (piece != null) {
                return piece;
            }
        }
        int m = 0;
        block0: while (m < 5) {
            ++m;
            int n = random.nextInt(field_15264);
            for (PieceSetting pieceSetting : possiblePieceSettings) {
                if ((n -= pieceSetting.field_15278) >= 0) continue;
                if (!pieceSetting.canGenerate(l) || pieceSetting == start.field_15284) continue block0;
                Piece piece2 = StrongholdGenerator.method_14847(pieceSetting.pieceType, list, random, i, j, k, direction, l);
                if (piece2 == null) continue;
                ++pieceSetting.generatedCount;
                start.field_15284 = pieceSetting;
                if (!pieceSetting.canGenerate()) {
                    possiblePieceSettings.remove(pieceSetting);
                }
                return piece2;
            }
        }
        BlockBox blockBox = SmallCorridor.method_14857(list, random, i, j, k, direction);
        if (blockBox != null && blockBox.minY > 1) {
            return new SmallCorridor(l, blockBox, direction);
        }
        return null;
    }

    private static StructurePiece method_14854(Start start, List<StructurePiece> list, Random random, int i, int j, int k, @Nullable Direction direction, int l) {
        if (l > 50) {
            return null;
        }
        if (Math.abs(i - start.getBoundingBox().minX) > 112 || Math.abs(k - start.getBoundingBox().minZ) > 112) {
            return null;
        }
        Piece structurePiece = StrongholdGenerator.method_14851(start, list, random, i, j, k, direction, l + 1);
        if (structurePiece != null) {
            list.add(structurePiece);
            start.field_15282.add(structurePiece);
        }
        return structurePiece;
    }

    static {
        STONE_BRICK_RANDOMIZER = new StoneBrickRandomizer();
    }

    static class StoneBrickRandomizer
    extends StructurePiece.BlockRandomizer {
        private StoneBrickRandomizer() {
        }

        @Override
        public void setBlock(Random random, int x, int y, int z, boolean placeBlock) {
            float f;
            this.block = placeBlock ? ((f = random.nextFloat()) < 0.2f ? Blocks.CRACKED_STONE_BRICKS.getDefaultState() : (f < 0.5f ? Blocks.MOSSY_STONE_BRICKS.getDefaultState() : (f < 0.55f ? Blocks.INFESTED_STONE_BRICKS.getDefaultState() : Blocks.STONE_BRICKS.getDefaultState()))) : Blocks.CAVE_AIR.getDefaultState();
        }
    }

    public static class PortalRoom
    extends Piece {
        private boolean spawnerPlaced;

        public PortalRoom(int i, BlockBox blockBox, Direction direction) {
            super(StructurePieceType.STRONGHOLD_PORTAL_ROOM, i);
            this.setOrientation(direction);
            this.boundingBox = blockBox;
        }

        public PortalRoom(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.STRONGHOLD_PORTAL_ROOM, compoundTag);
            this.spawnerPlaced = compoundTag.getBoolean("Mob");
        }

        @Override
        protected void toNbt(CompoundTag tag) {
            super.toNbt(tag);
            tag.putBoolean("Mob", this.spawnerPlaced);
        }

        @Override
        public void placeJigsaw(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
            if (structurePiece != null) {
                ((Start)structurePiece).field_15283 = this;
            }
        }

        public static PortalRoom method_14863(List<StructurePiece> list, int i, int j, int k, Direction direction, int l) {
            BlockBox blockBox = BlockBox.rotated(i, j, k, -4, -1, 0, 11, 8, 16, direction);
            if (!PortalRoom.method_14871(blockBox) || StructurePiece.getOverlappingPiece(list, blockBox) != null) {
                return null;
            }
            return new PortalRoom(l, blockBox, direction);
        }

        @Override
        public boolean generate(ServerWorldAccess serverWorldAccess, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
            int j;
            this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 0, 0, 0, 10, 7, 15, false, random, STONE_BRICK_RANDOMIZER);
            this.generateEntrance(serverWorldAccess, random, boundingBox, Piece.EntranceType.GRATES, 4, 1, 0);
            int i = 6;
            this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 1, i, 1, 1, i, 14, false, random, STONE_BRICK_RANDOMIZER);
            this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 9, i, 1, 9, i, 14, false, random, STONE_BRICK_RANDOMIZER);
            this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 2, i, 1, 8, i, 2, false, random, STONE_BRICK_RANDOMIZER);
            this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 2, i, 14, 8, i, 14, false, random, STONE_BRICK_RANDOMIZER);
            this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 1, 1, 1, 2, 1, 4, false, random, STONE_BRICK_RANDOMIZER);
            this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 8, 1, 1, 9, 1, 4, false, random, STONE_BRICK_RANDOMIZER);
            this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 1, 1, 1, 1, 1, 3, Blocks.LAVA.getDefaultState(), Blocks.LAVA.getDefaultState(), false);
            this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 9, 1, 1, 9, 1, 3, Blocks.LAVA.getDefaultState(), Blocks.LAVA.getDefaultState(), false);
            this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 3, 1, 8, 7, 1, 12, false, random, STONE_BRICK_RANDOMIZER);
            this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 4, 1, 9, 6, 1, 11, Blocks.LAVA.getDefaultState(), Blocks.LAVA.getDefaultState(), false);
            BlockState blockState = (BlockState)((BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.NORTH, true)).with(PaneBlock.SOUTH, true);
            BlockState blockState2 = (BlockState)((BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.WEST, true)).with(PaneBlock.EAST, true);
            for (j = 3; j < 14; j += 2) {
                this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 0, 3, j, 0, 4, j, blockState, blockState, false);
                this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 10, 3, j, 10, 4, j, blockState, blockState, false);
            }
            for (j = 2; j < 9; j += 2) {
                this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, j, 3, 15, j, 4, 15, blockState2, blockState2, false);
            }
            BlockState blockState3 = (BlockState)Blocks.STONE_BRICK_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.NORTH);
            this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 4, 1, 5, 6, 1, 7, false, random, STONE_BRICK_RANDOMIZER);
            this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 4, 2, 6, 6, 2, 7, false, random, STONE_BRICK_RANDOMIZER);
            this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 4, 3, 7, 6, 3, 7, false, random, STONE_BRICK_RANDOMIZER);
            for (int k = 4; k <= 6; ++k) {
                this.addBlock(serverWorldAccess, blockState3, k, 1, 4, boundingBox);
                this.addBlock(serverWorldAccess, blockState3, k, 2, 5, boundingBox);
                this.addBlock(serverWorldAccess, blockState3, k, 3, 6, boundingBox);
            }
            BlockState blockState4 = (BlockState)Blocks.END_PORTAL_FRAME.getDefaultState().with(EndPortalFrameBlock.FACING, Direction.NORTH);
            BlockState blockState5 = (BlockState)Blocks.END_PORTAL_FRAME.getDefaultState().with(EndPortalFrameBlock.FACING, Direction.SOUTH);
            BlockState blockState6 = (BlockState)Blocks.END_PORTAL_FRAME.getDefaultState().with(EndPortalFrameBlock.FACING, Direction.EAST);
            BlockState blockState7 = (BlockState)Blocks.END_PORTAL_FRAME.getDefaultState().with(EndPortalFrameBlock.FACING, Direction.WEST);
            boolean bl = true;
            boolean[] bls = new boolean[12];
            for (int l = 0; l < bls.length; ++l) {
                bls[l] = random.nextFloat() > 0.9f;
                bl &= bls[l];
            }
            this.addBlock(serverWorldAccess, (BlockState)blockState4.with(EndPortalFrameBlock.EYE, bls[0]), 4, 3, 8, boundingBox);
            this.addBlock(serverWorldAccess, (BlockState)blockState4.with(EndPortalFrameBlock.EYE, bls[1]), 5, 3, 8, boundingBox);
            this.addBlock(serverWorldAccess, (BlockState)blockState4.with(EndPortalFrameBlock.EYE, bls[2]), 6, 3, 8, boundingBox);
            this.addBlock(serverWorldAccess, (BlockState)blockState5.with(EndPortalFrameBlock.EYE, bls[3]), 4, 3, 12, boundingBox);
            this.addBlock(serverWorldAccess, (BlockState)blockState5.with(EndPortalFrameBlock.EYE, bls[4]), 5, 3, 12, boundingBox);
            this.addBlock(serverWorldAccess, (BlockState)blockState5.with(EndPortalFrameBlock.EYE, bls[5]), 6, 3, 12, boundingBox);
            this.addBlock(serverWorldAccess, (BlockState)blockState6.with(EndPortalFrameBlock.EYE, bls[6]), 3, 3, 9, boundingBox);
            this.addBlock(serverWorldAccess, (BlockState)blockState6.with(EndPortalFrameBlock.EYE, bls[7]), 3, 3, 10, boundingBox);
            this.addBlock(serverWorldAccess, (BlockState)blockState6.with(EndPortalFrameBlock.EYE, bls[8]), 3, 3, 11, boundingBox);
            this.addBlock(serverWorldAccess, (BlockState)blockState7.with(EndPortalFrameBlock.EYE, bls[9]), 7, 3, 9, boundingBox);
            this.addBlock(serverWorldAccess, (BlockState)blockState7.with(EndPortalFrameBlock.EYE, bls[10]), 7, 3, 10, boundingBox);
            this.addBlock(serverWorldAccess, (BlockState)blockState7.with(EndPortalFrameBlock.EYE, bls[11]), 7, 3, 11, boundingBox);
            if (bl) {
                BlockState blockState8 = Blocks.END_PORTAL.getDefaultState();
                this.addBlock(serverWorldAccess, blockState8, 4, 3, 9, boundingBox);
                this.addBlock(serverWorldAccess, blockState8, 5, 3, 9, boundingBox);
                this.addBlock(serverWorldAccess, blockState8, 6, 3, 9, boundingBox);
                this.addBlock(serverWorldAccess, blockState8, 4, 3, 10, boundingBox);
                this.addBlock(serverWorldAccess, blockState8, 5, 3, 10, boundingBox);
                this.addBlock(serverWorldAccess, blockState8, 6, 3, 10, boundingBox);
                this.addBlock(serverWorldAccess, blockState8, 4, 3, 11, boundingBox);
                this.addBlock(serverWorldAccess, blockState8, 5, 3, 11, boundingBox);
                this.addBlock(serverWorldAccess, blockState8, 6, 3, 11, boundingBox);
            }
            if (!this.spawnerPlaced) {
                i = this.applyYTransform(3);
                BlockPos blockPos2 = new BlockPos(this.applyXTransform(5, 6), i, this.applyZTransform(5, 6));
                if (boundingBox.contains(blockPos2)) {
                    this.spawnerPlaced = true;
                    serverWorldAccess.setBlockState(blockPos2, Blocks.SPAWNER.getDefaultState(), 2);
                    BlockEntity blockEntity = serverWorldAccess.getBlockEntity(blockPos2);
                    if (blockEntity instanceof MobSpawnerBlockEntity) {
                        ((MobSpawnerBlockEntity)blockEntity).getLogic().setEntityId(EntityType.SILVERFISH);
                    }
                }
            }
            return true;
        }
    }

    public static class FiveWayCrossing
    extends Piece {
        private final boolean lowerLeftExists;
        private final boolean upperLeftExists;
        private final boolean lowerRightExists;
        private final boolean upperRightExists;

        public FiveWayCrossing(int i, Random random, BlockBox blockBox, Direction direction) {
            super(StructurePieceType.STRONGHOLD_FIVE_WAY_CROSSING, i);
            this.setOrientation(direction);
            this.entryDoor = this.getRandomEntrance(random);
            this.boundingBox = blockBox;
            this.lowerLeftExists = random.nextBoolean();
            this.upperLeftExists = random.nextBoolean();
            this.lowerRightExists = random.nextBoolean();
            this.upperRightExists = random.nextInt(3) > 0;
        }

        public FiveWayCrossing(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.STRONGHOLD_FIVE_WAY_CROSSING, compoundTag);
            this.lowerLeftExists = compoundTag.getBoolean("leftLow");
            this.upperLeftExists = compoundTag.getBoolean("leftHigh");
            this.lowerRightExists = compoundTag.getBoolean("rightLow");
            this.upperRightExists = compoundTag.getBoolean("rightHigh");
        }

        @Override
        protected void toNbt(CompoundTag tag) {
            super.toNbt(tag);
            tag.putBoolean("leftLow", this.lowerLeftExists);
            tag.putBoolean("leftHigh", this.upperLeftExists);
            tag.putBoolean("rightLow", this.lowerRightExists);
            tag.putBoolean("rightHigh", this.upperRightExists);
        }

        @Override
        public void placeJigsaw(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
            int i = 3;
            int j = 5;
            Direction direction = this.getFacing();
            if (direction == Direction.WEST || direction == Direction.NORTH) {
                i = 8 - i;
                j = 8 - j;
            }
            this.method_14874((Start)structurePiece, list, random, 5, 1);
            if (this.lowerLeftExists) {
                this.method_14870((Start)structurePiece, list, random, i, 1);
            }
            if (this.upperLeftExists) {
                this.method_14870((Start)structurePiece, list, random, j, 7);
            }
            if (this.lowerRightExists) {
                this.method_14873((Start)structurePiece, list, random, i, 1);
            }
            if (this.upperRightExists) {
                this.method_14873((Start)structurePiece, list, random, j, 7);
            }
        }

        public static FiveWayCrossing method_14858(List<StructurePiece> list, Random random, int i, int j, int k, Direction direction, int l) {
            BlockBox blockBox = BlockBox.rotated(i, j, k, -4, -3, 0, 10, 9, 11, direction);
            if (!FiveWayCrossing.method_14871(blockBox) || StructurePiece.getOverlappingPiece(list, blockBox) != null) {
                return null;
            }
            return new FiveWayCrossing(l, random, blockBox, direction);
        }

        @Override
        public boolean generate(ServerWorldAccess serverWorldAccess, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
            this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 0, 0, 0, 9, 8, 10, true, random, STONE_BRICK_RANDOMIZER);
            this.generateEntrance(serverWorldAccess, random, boundingBox, this.entryDoor, 4, 3, 0);
            if (this.lowerLeftExists) {
                this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 0, 3, 1, 0, 5, 3, AIR, AIR, false);
            }
            if (this.lowerRightExists) {
                this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 9, 3, 1, 9, 5, 3, AIR, AIR, false);
            }
            if (this.upperLeftExists) {
                this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 0, 5, 7, 0, 7, 9, AIR, AIR, false);
            }
            if (this.upperRightExists) {
                this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 9, 5, 7, 9, 7, 9, AIR, AIR, false);
            }
            this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 5, 1, 10, 7, 3, 10, AIR, AIR, false);
            this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 1, 2, 1, 8, 2, 6, false, random, STONE_BRICK_RANDOMIZER);
            this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 4, 1, 5, 4, 4, 9, false, random, STONE_BRICK_RANDOMIZER);
            this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 8, 1, 5, 8, 4, 9, false, random, STONE_BRICK_RANDOMIZER);
            this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 1, 4, 7, 3, 4, 9, false, random, STONE_BRICK_RANDOMIZER);
            this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 1, 3, 5, 3, 3, 6, false, random, STONE_BRICK_RANDOMIZER);
            this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 1, 3, 4, 3, 3, 4, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), Blocks.SMOOTH_STONE_SLAB.getDefaultState(), false);
            this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 1, 4, 6, 3, 4, 6, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), Blocks.SMOOTH_STONE_SLAB.getDefaultState(), false);
            this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 5, 1, 7, 7, 1, 8, false, random, STONE_BRICK_RANDOMIZER);
            this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 5, 1, 9, 7, 1, 9, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), Blocks.SMOOTH_STONE_SLAB.getDefaultState(), false);
            this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 5, 2, 7, 7, 2, 7, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), Blocks.SMOOTH_STONE_SLAB.getDefaultState(), false);
            this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 4, 5, 7, 4, 5, 9, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), Blocks.SMOOTH_STONE_SLAB.getDefaultState(), false);
            this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 8, 5, 7, 8, 5, 9, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), Blocks.SMOOTH_STONE_SLAB.getDefaultState(), false);
            this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 5, 5, 7, 7, 5, 9, (BlockState)Blocks.SMOOTH_STONE_SLAB.getDefaultState().with(SlabBlock.TYPE, SlabType.DOUBLE), (BlockState)Blocks.SMOOTH_STONE_SLAB.getDefaultState().with(SlabBlock.TYPE, SlabType.DOUBLE), false);
            this.addBlock(serverWorldAccess, (BlockState)Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, Direction.SOUTH), 6, 5, 6, boundingBox);
            return true;
        }
    }

    public static class Library
    extends Piece {
        private final boolean tall;

        public Library(int i, Random random, BlockBox blockBox, Direction direction) {
            super(StructurePieceType.STRONGHOLD_LIBRARY, i);
            this.setOrientation(direction);
            this.entryDoor = this.getRandomEntrance(random);
            this.boundingBox = blockBox;
            this.tall = blockBox.getBlockCountY() > 6;
        }

        public Library(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.STRONGHOLD_LIBRARY, compoundTag);
            this.tall = compoundTag.getBoolean("Tall");
        }

        @Override
        protected void toNbt(CompoundTag tag) {
            super.toNbt(tag);
            tag.putBoolean("Tall", this.tall);
        }

        public static Library method_14860(List<StructurePiece> list, Random random, int i, int j, int k, Direction direction, int l) {
            BlockBox blockBox = BlockBox.rotated(i, j, k, -4, -1, 0, 14, 11, 15, direction);
            if (!(Library.method_14871(blockBox) && StructurePiece.getOverlappingPiece(list, blockBox) == null || Library.method_14871(blockBox = BlockBox.rotated(i, j, k, -4, -1, 0, 14, 6, 15, direction)) && StructurePiece.getOverlappingPiece(list, blockBox) == null)) {
                return null;
            }
            return new Library(l, random, blockBox, direction);
        }

        @Override
        public boolean generate(ServerWorldAccess serverWorldAccess, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
            int l;
            int i = 11;
            if (!this.tall) {
                i = 6;
            }
            this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 0, 0, 0, 13, i - 1, 14, true, random, STONE_BRICK_RANDOMIZER);
            this.generateEntrance(serverWorldAccess, random, boundingBox, this.entryDoor, 4, 1, 0);
            this.fillWithOutlineUnderSealevel(serverWorldAccess, boundingBox, random, 0.07f, 2, 1, 1, 11, 4, 13, Blocks.COBWEB.getDefaultState(), Blocks.COBWEB.getDefaultState(), false, false);
            boolean j = true;
            int k = 12;
            for (l = 1; l <= 13; ++l) {
                if ((l - 1) % 4 == 0) {
                    this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 1, 1, l, 1, 4, l, Blocks.OAK_PLANKS.getDefaultState(), Blocks.OAK_PLANKS.getDefaultState(), false);
                    this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 12, 1, l, 12, 4, l, Blocks.OAK_PLANKS.getDefaultState(), Blocks.OAK_PLANKS.getDefaultState(), false);
                    this.addBlock(serverWorldAccess, (BlockState)Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, Direction.EAST), 2, 3, l, boundingBox);
                    this.addBlock(serverWorldAccess, (BlockState)Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, Direction.WEST), 11, 3, l, boundingBox);
                    if (!this.tall) continue;
                    this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 1, 6, l, 1, 9, l, Blocks.OAK_PLANKS.getDefaultState(), Blocks.OAK_PLANKS.getDefaultState(), false);
                    this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 12, 6, l, 12, 9, l, Blocks.OAK_PLANKS.getDefaultState(), Blocks.OAK_PLANKS.getDefaultState(), false);
                    continue;
                }
                this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 1, 1, l, 1, 4, l, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
                this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 12, 1, l, 12, 4, l, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
                if (!this.tall) continue;
                this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 1, 6, l, 1, 9, l, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
                this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 12, 6, l, 12, 9, l, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
            }
            for (l = 3; l < 12; l += 2) {
                this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 3, 1, l, 4, 3, l, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
                this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 6, 1, l, 7, 3, l, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
                this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 9, 1, l, 10, 3, l, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
            }
            if (this.tall) {
                this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 1, 5, 1, 3, 5, 13, Blocks.OAK_PLANKS.getDefaultState(), Blocks.OAK_PLANKS.getDefaultState(), false);
                this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 10, 5, 1, 12, 5, 13, Blocks.OAK_PLANKS.getDefaultState(), Blocks.OAK_PLANKS.getDefaultState(), false);
                this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 4, 5, 1, 9, 5, 2, Blocks.OAK_PLANKS.getDefaultState(), Blocks.OAK_PLANKS.getDefaultState(), false);
                this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 4, 5, 12, 9, 5, 13, Blocks.OAK_PLANKS.getDefaultState(), Blocks.OAK_PLANKS.getDefaultState(), false);
                this.addBlock(serverWorldAccess, Blocks.OAK_PLANKS.getDefaultState(), 9, 5, 11, boundingBox);
                this.addBlock(serverWorldAccess, Blocks.OAK_PLANKS.getDefaultState(), 8, 5, 11, boundingBox);
                this.addBlock(serverWorldAccess, Blocks.OAK_PLANKS.getDefaultState(), 9, 5, 10, boundingBox);
                BlockState blockState = (BlockState)((BlockState)Blocks.OAK_FENCE.getDefaultState().with(FenceBlock.WEST, true)).with(FenceBlock.EAST, true);
                BlockState blockState2 = (BlockState)((BlockState)Blocks.OAK_FENCE.getDefaultState().with(FenceBlock.NORTH, true)).with(FenceBlock.SOUTH, true);
                this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 3, 6, 3, 3, 6, 11, blockState2, blockState2, false);
                this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 10, 6, 3, 10, 6, 9, blockState2, blockState2, false);
                this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 4, 6, 2, 9, 6, 2, blockState, blockState, false);
                this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 4, 6, 12, 7, 6, 12, blockState, blockState, false);
                this.addBlock(serverWorldAccess, (BlockState)((BlockState)Blocks.OAK_FENCE.getDefaultState().with(FenceBlock.NORTH, true)).with(FenceBlock.EAST, true), 3, 6, 2, boundingBox);
                this.addBlock(serverWorldAccess, (BlockState)((BlockState)Blocks.OAK_FENCE.getDefaultState().with(FenceBlock.SOUTH, true)).with(FenceBlock.EAST, true), 3, 6, 12, boundingBox);
                this.addBlock(serverWorldAccess, (BlockState)((BlockState)Blocks.OAK_FENCE.getDefaultState().with(FenceBlock.NORTH, true)).with(FenceBlock.WEST, true), 10, 6, 2, boundingBox);
                for (int m = 0; m <= 2; ++m) {
                    this.addBlock(serverWorldAccess, (BlockState)((BlockState)Blocks.OAK_FENCE.getDefaultState().with(FenceBlock.SOUTH, true)).with(FenceBlock.WEST, true), 8 + m, 6, 12 - m, boundingBox);
                    if (m == 2) continue;
                    this.addBlock(serverWorldAccess, (BlockState)((BlockState)Blocks.OAK_FENCE.getDefaultState().with(FenceBlock.NORTH, true)).with(FenceBlock.EAST, true), 8 + m, 6, 11 - m, boundingBox);
                }
                BlockState blockState3 = (BlockState)Blocks.LADDER.getDefaultState().with(LadderBlock.FACING, Direction.SOUTH);
                this.addBlock(serverWorldAccess, blockState3, 10, 1, 13, boundingBox);
                this.addBlock(serverWorldAccess, blockState3, 10, 2, 13, boundingBox);
                this.addBlock(serverWorldAccess, blockState3, 10, 3, 13, boundingBox);
                this.addBlock(serverWorldAccess, blockState3, 10, 4, 13, boundingBox);
                this.addBlock(serverWorldAccess, blockState3, 10, 5, 13, boundingBox);
                this.addBlock(serverWorldAccess, blockState3, 10, 6, 13, boundingBox);
                this.addBlock(serverWorldAccess, blockState3, 10, 7, 13, boundingBox);
                int n = 7;
                int o = 7;
                BlockState blockState4 = (BlockState)Blocks.OAK_FENCE.getDefaultState().with(FenceBlock.EAST, true);
                this.addBlock(serverWorldAccess, blockState4, 6, 9, 7, boundingBox);
                BlockState blockState5 = (BlockState)Blocks.OAK_FENCE.getDefaultState().with(FenceBlock.WEST, true);
                this.addBlock(serverWorldAccess, blockState5, 7, 9, 7, boundingBox);
                this.addBlock(serverWorldAccess, blockState4, 6, 8, 7, boundingBox);
                this.addBlock(serverWorldAccess, blockState5, 7, 8, 7, boundingBox);
                BlockState blockState6 = (BlockState)((BlockState)blockState2.with(FenceBlock.WEST, true)).with(FenceBlock.EAST, true);
                this.addBlock(serverWorldAccess, blockState6, 6, 7, 7, boundingBox);
                this.addBlock(serverWorldAccess, blockState6, 7, 7, 7, boundingBox);
                this.addBlock(serverWorldAccess, blockState4, 5, 7, 7, boundingBox);
                this.addBlock(serverWorldAccess, blockState5, 8, 7, 7, boundingBox);
                this.addBlock(serverWorldAccess, (BlockState)blockState4.with(FenceBlock.NORTH, true), 6, 7, 6, boundingBox);
                this.addBlock(serverWorldAccess, (BlockState)blockState4.with(FenceBlock.SOUTH, true), 6, 7, 8, boundingBox);
                this.addBlock(serverWorldAccess, (BlockState)blockState5.with(FenceBlock.NORTH, true), 7, 7, 6, boundingBox);
                this.addBlock(serverWorldAccess, (BlockState)blockState5.with(FenceBlock.SOUTH, true), 7, 7, 8, boundingBox);
                BlockState blockState7 = Blocks.TORCH.getDefaultState();
                this.addBlock(serverWorldAccess, blockState7, 5, 8, 7, boundingBox);
                this.addBlock(serverWorldAccess, blockState7, 8, 8, 7, boundingBox);
                this.addBlock(serverWorldAccess, blockState7, 6, 8, 6, boundingBox);
                this.addBlock(serverWorldAccess, blockState7, 6, 8, 8, boundingBox);
                this.addBlock(serverWorldAccess, blockState7, 7, 8, 6, boundingBox);
                this.addBlock(serverWorldAccess, blockState7, 7, 8, 8, boundingBox);
            }
            this.addChest(serverWorldAccess, boundingBox, random, 3, 3, 5, LootTables.STRONGHOLD_LIBRARY_CHEST);
            if (this.tall) {
                this.addBlock(serverWorldAccess, AIR, 12, 9, 1, boundingBox);
                this.addChest(serverWorldAccess, boundingBox, random, 12, 8, 1, LootTables.STRONGHOLD_LIBRARY_CHEST);
            }
            return true;
        }
    }

    public static class PrisonHall
    extends Piece {
        public PrisonHall(int i, Random random, BlockBox blockBox, Direction direction) {
            super(StructurePieceType.STRONGHOLD_PRISON_HALL, i);
            this.setOrientation(direction);
            this.entryDoor = this.getRandomEntrance(random);
            this.boundingBox = blockBox;
        }

        public PrisonHall(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.STRONGHOLD_PRISON_HALL, compoundTag);
        }

        @Override
        public void placeJigsaw(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
            this.method_14874((Start)structurePiece, list, random, 1, 1);
        }

        public static PrisonHall method_14864(List<StructurePiece> list, Random random, int i, int j, int k, Direction direction, int l) {
            BlockBox blockBox = BlockBox.rotated(i, j, k, -1, -1, 0, 9, 5, 11, direction);
            if (!PrisonHall.method_14871(blockBox) || StructurePiece.getOverlappingPiece(list, blockBox) != null) {
                return null;
            }
            return new PrisonHall(l, random, blockBox, direction);
        }

        @Override
        public boolean generate(ServerWorldAccess serverWorldAccess, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
            this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 0, 0, 0, 8, 4, 10, true, random, STONE_BRICK_RANDOMIZER);
            this.generateEntrance(serverWorldAccess, random, boundingBox, this.entryDoor, 1, 1, 0);
            this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 1, 1, 10, 3, 3, 10, AIR, AIR, false);
            this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 4, 1, 1, 4, 3, 1, false, random, STONE_BRICK_RANDOMIZER);
            this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 4, 1, 3, 4, 3, 3, false, random, STONE_BRICK_RANDOMIZER);
            this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 4, 1, 7, 4, 3, 7, false, random, STONE_BRICK_RANDOMIZER);
            this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 4, 1, 9, 4, 3, 9, false, random, STONE_BRICK_RANDOMIZER);
            for (int i = 1; i <= 3; ++i) {
                this.addBlock(serverWorldAccess, (BlockState)((BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.NORTH, true)).with(PaneBlock.SOUTH, true), 4, i, 4, boundingBox);
                this.addBlock(serverWorldAccess, (BlockState)((BlockState)((BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.NORTH, true)).with(PaneBlock.SOUTH, true)).with(PaneBlock.EAST, true), 4, i, 5, boundingBox);
                this.addBlock(serverWorldAccess, (BlockState)((BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.NORTH, true)).with(PaneBlock.SOUTH, true), 4, i, 6, boundingBox);
                this.addBlock(serverWorldAccess, (BlockState)((BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.WEST, true)).with(PaneBlock.EAST, true), 5, i, 5, boundingBox);
                this.addBlock(serverWorldAccess, (BlockState)((BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.WEST, true)).with(PaneBlock.EAST, true), 6, i, 5, boundingBox);
                this.addBlock(serverWorldAccess, (BlockState)((BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.WEST, true)).with(PaneBlock.EAST, true), 7, i, 5, boundingBox);
            }
            this.addBlock(serverWorldAccess, (BlockState)((BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.NORTH, true)).with(PaneBlock.SOUTH, true), 4, 3, 2, boundingBox);
            this.addBlock(serverWorldAccess, (BlockState)((BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.NORTH, true)).with(PaneBlock.SOUTH, true), 4, 3, 8, boundingBox);
            BlockState blockState = (BlockState)Blocks.IRON_DOOR.getDefaultState().with(DoorBlock.FACING, Direction.WEST);
            BlockState blockState2 = (BlockState)((BlockState)Blocks.IRON_DOOR.getDefaultState().with(DoorBlock.FACING, Direction.WEST)).with(DoorBlock.HALF, DoubleBlockHalf.UPPER);
            this.addBlock(serverWorldAccess, blockState, 4, 1, 2, boundingBox);
            this.addBlock(serverWorldAccess, blockState2, 4, 2, 2, boundingBox);
            this.addBlock(serverWorldAccess, blockState, 4, 1, 8, boundingBox);
            this.addBlock(serverWorldAccess, blockState2, 4, 2, 8, boundingBox);
            return true;
        }
    }

    public static class SquareRoom
    extends Piece {
        protected final int roomType;

        public SquareRoom(int i, Random random, BlockBox blockBox, Direction direction) {
            super(StructurePieceType.STRONGHOLD_SQUARE_ROOM, i);
            this.setOrientation(direction);
            this.entryDoor = this.getRandomEntrance(random);
            this.boundingBox = blockBox;
            this.roomType = random.nextInt(5);
        }

        public SquareRoom(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.STRONGHOLD_SQUARE_ROOM, compoundTag);
            this.roomType = compoundTag.getInt("Type");
        }

        @Override
        protected void toNbt(CompoundTag tag) {
            super.toNbt(tag);
            tag.putInt("Type", this.roomType);
        }

        @Override
        public void placeJigsaw(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
            this.method_14874((Start)structurePiece, list, random, 4, 1);
            this.method_14870((Start)structurePiece, list, random, 1, 4);
            this.method_14873((Start)structurePiece, list, random, 1, 4);
        }

        public static SquareRoom method_14865(List<StructurePiece> list, Random random, int i, int j, int k, Direction direction, int l) {
            BlockBox blockBox = BlockBox.rotated(i, j, k, -4, -1, 0, 11, 7, 11, direction);
            if (!SquareRoom.method_14871(blockBox) || StructurePiece.getOverlappingPiece(list, blockBox) != null) {
                return null;
            }
            return new SquareRoom(l, random, blockBox, direction);
        }

        @Override
        public boolean generate(ServerWorldAccess serverWorldAccess, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
            this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 0, 0, 0, 10, 6, 10, true, random, STONE_BRICK_RANDOMIZER);
            this.generateEntrance(serverWorldAccess, random, boundingBox, this.entryDoor, 4, 1, 0);
            this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 4, 1, 10, 6, 3, 10, AIR, AIR, false);
            this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 0, 1, 4, 0, 3, 6, AIR, AIR, false);
            this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 10, 1, 4, 10, 3, 6, AIR, AIR, false);
            switch (this.roomType) {
                default: {
                    break;
                }
                case 0: {
                    this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 5, 1, 5, boundingBox);
                    this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 5, 2, 5, boundingBox);
                    this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 5, 3, 5, boundingBox);
                    this.addBlock(serverWorldAccess, (BlockState)Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, Direction.WEST), 4, 3, 5, boundingBox);
                    this.addBlock(serverWorldAccess, (BlockState)Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, Direction.EAST), 6, 3, 5, boundingBox);
                    this.addBlock(serverWorldAccess, (BlockState)Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, Direction.SOUTH), 5, 3, 4, boundingBox);
                    this.addBlock(serverWorldAccess, (BlockState)Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, Direction.NORTH), 5, 3, 6, boundingBox);
                    this.addBlock(serverWorldAccess, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 4, 1, 4, boundingBox);
                    this.addBlock(serverWorldAccess, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 4, 1, 5, boundingBox);
                    this.addBlock(serverWorldAccess, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 4, 1, 6, boundingBox);
                    this.addBlock(serverWorldAccess, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 6, 1, 4, boundingBox);
                    this.addBlock(serverWorldAccess, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 6, 1, 5, boundingBox);
                    this.addBlock(serverWorldAccess, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 6, 1, 6, boundingBox);
                    this.addBlock(serverWorldAccess, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 5, 1, 4, boundingBox);
                    this.addBlock(serverWorldAccess, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 5, 1, 6, boundingBox);
                    break;
                }
                case 1: {
                    for (int i = 0; i < 5; ++i) {
                        this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 3, 1, 3 + i, boundingBox);
                        this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 7, 1, 3 + i, boundingBox);
                        this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 3 + i, 1, 3, boundingBox);
                        this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 3 + i, 1, 7, boundingBox);
                    }
                    this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 5, 1, 5, boundingBox);
                    this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 5, 2, 5, boundingBox);
                    this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 5, 3, 5, boundingBox);
                    this.addBlock(serverWorldAccess, Blocks.WATER.getDefaultState(), 5, 4, 5, boundingBox);
                    break;
                }
                case 2: {
                    int i;
                    for (i = 1; i <= 9; ++i) {
                        this.addBlock(serverWorldAccess, Blocks.COBBLESTONE.getDefaultState(), 1, 3, i, boundingBox);
                        this.addBlock(serverWorldAccess, Blocks.COBBLESTONE.getDefaultState(), 9, 3, i, boundingBox);
                    }
                    for (i = 1; i <= 9; ++i) {
                        this.addBlock(serverWorldAccess, Blocks.COBBLESTONE.getDefaultState(), i, 3, 1, boundingBox);
                        this.addBlock(serverWorldAccess, Blocks.COBBLESTONE.getDefaultState(), i, 3, 9, boundingBox);
                    }
                    this.addBlock(serverWorldAccess, Blocks.COBBLESTONE.getDefaultState(), 5, 1, 4, boundingBox);
                    this.addBlock(serverWorldAccess, Blocks.COBBLESTONE.getDefaultState(), 5, 1, 6, boundingBox);
                    this.addBlock(serverWorldAccess, Blocks.COBBLESTONE.getDefaultState(), 5, 3, 4, boundingBox);
                    this.addBlock(serverWorldAccess, Blocks.COBBLESTONE.getDefaultState(), 5, 3, 6, boundingBox);
                    this.addBlock(serverWorldAccess, Blocks.COBBLESTONE.getDefaultState(), 4, 1, 5, boundingBox);
                    this.addBlock(serverWorldAccess, Blocks.COBBLESTONE.getDefaultState(), 6, 1, 5, boundingBox);
                    this.addBlock(serverWorldAccess, Blocks.COBBLESTONE.getDefaultState(), 4, 3, 5, boundingBox);
                    this.addBlock(serverWorldAccess, Blocks.COBBLESTONE.getDefaultState(), 6, 3, 5, boundingBox);
                    for (i = 1; i <= 3; ++i) {
                        this.addBlock(serverWorldAccess, Blocks.COBBLESTONE.getDefaultState(), 4, i, 4, boundingBox);
                        this.addBlock(serverWorldAccess, Blocks.COBBLESTONE.getDefaultState(), 6, i, 4, boundingBox);
                        this.addBlock(serverWorldAccess, Blocks.COBBLESTONE.getDefaultState(), 4, i, 6, boundingBox);
                        this.addBlock(serverWorldAccess, Blocks.COBBLESTONE.getDefaultState(), 6, i, 6, boundingBox);
                    }
                    this.addBlock(serverWorldAccess, Blocks.TORCH.getDefaultState(), 5, 3, 5, boundingBox);
                    for (i = 2; i <= 8; ++i) {
                        this.addBlock(serverWorldAccess, Blocks.OAK_PLANKS.getDefaultState(), 2, 3, i, boundingBox);
                        this.addBlock(serverWorldAccess, Blocks.OAK_PLANKS.getDefaultState(), 3, 3, i, boundingBox);
                        if (i <= 3 || i >= 7) {
                            this.addBlock(serverWorldAccess, Blocks.OAK_PLANKS.getDefaultState(), 4, 3, i, boundingBox);
                            this.addBlock(serverWorldAccess, Blocks.OAK_PLANKS.getDefaultState(), 5, 3, i, boundingBox);
                            this.addBlock(serverWorldAccess, Blocks.OAK_PLANKS.getDefaultState(), 6, 3, i, boundingBox);
                        }
                        this.addBlock(serverWorldAccess, Blocks.OAK_PLANKS.getDefaultState(), 7, 3, i, boundingBox);
                        this.addBlock(serverWorldAccess, Blocks.OAK_PLANKS.getDefaultState(), 8, 3, i, boundingBox);
                    }
                    BlockState blockState = (BlockState)Blocks.LADDER.getDefaultState().with(LadderBlock.FACING, Direction.WEST);
                    this.addBlock(serverWorldAccess, blockState, 9, 1, 3, boundingBox);
                    this.addBlock(serverWorldAccess, blockState, 9, 2, 3, boundingBox);
                    this.addBlock(serverWorldAccess, blockState, 9, 3, 3, boundingBox);
                    this.addChest(serverWorldAccess, boundingBox, random, 3, 4, 8, LootTables.STRONGHOLD_CROSSING_CHEST);
                }
            }
            return true;
        }
    }

    public static class RightTurn
    extends Turn {
        public RightTurn(int i, Random random, BlockBox blockBox, Direction direction) {
            super(StructurePieceType.STRONGHOLD_RIGHT_TURN, i);
            this.setOrientation(direction);
            this.entryDoor = this.getRandomEntrance(random);
            this.boundingBox = blockBox;
        }

        public RightTurn(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.STRONGHOLD_RIGHT_TURN, compoundTag);
        }

        @Override
        public void placeJigsaw(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
            Direction direction = this.getFacing();
            if (direction == Direction.NORTH || direction == Direction.EAST) {
                this.method_14873((Start)structurePiece, list, random, 1, 1);
            } else {
                this.method_14870((Start)structurePiece, list, random, 1, 1);
            }
        }

        public static RightTurn method_16652(List<StructurePiece> list, Random random, int i, int j, int k, Direction direction, int l) {
            BlockBox blockBox = BlockBox.rotated(i, j, k, -1, -1, 0, 5, 5, 5, direction);
            if (!RightTurn.method_14871(blockBox) || StructurePiece.getOverlappingPiece(list, blockBox) != null) {
                return null;
            }
            return new RightTurn(l, random, blockBox, direction);
        }

        @Override
        public boolean generate(ServerWorldAccess serverWorldAccess, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
            this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 0, 0, 0, 4, 4, 4, true, random, STONE_BRICK_RANDOMIZER);
            this.generateEntrance(serverWorldAccess, random, boundingBox, this.entryDoor, 1, 1, 0);
            Direction direction = this.getFacing();
            if (direction == Direction.NORTH || direction == Direction.EAST) {
                this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 4, 1, 1, 4, 3, 3, AIR, AIR, false);
            } else {
                this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 0, 1, 1, 0, 3, 3, AIR, AIR, false);
            }
            return true;
        }
    }

    public static class LeftTurn
    extends Turn {
        public LeftTurn(int i, Random random, BlockBox blockBox, Direction direction) {
            super(StructurePieceType.STRONGHOLD_LEFT_TURN, i);
            this.setOrientation(direction);
            this.entryDoor = this.getRandomEntrance(random);
            this.boundingBox = blockBox;
        }

        public LeftTurn(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.STRONGHOLD_LEFT_TURN, compoundTag);
        }

        @Override
        public void placeJigsaw(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
            Direction direction = this.getFacing();
            if (direction == Direction.NORTH || direction == Direction.EAST) {
                this.method_14870((Start)structurePiece, list, random, 1, 1);
            } else {
                this.method_14873((Start)structurePiece, list, random, 1, 1);
            }
        }

        public static LeftTurn method_14859(List<StructurePiece> list, Random random, int i, int j, int k, Direction direction, int l) {
            BlockBox blockBox = BlockBox.rotated(i, j, k, -1, -1, 0, 5, 5, 5, direction);
            if (!LeftTurn.method_14871(blockBox) || StructurePiece.getOverlappingPiece(list, blockBox) != null) {
                return null;
            }
            return new LeftTurn(l, random, blockBox, direction);
        }

        @Override
        public boolean generate(ServerWorldAccess serverWorldAccess, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
            this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 0, 0, 0, 4, 4, 4, true, random, STONE_BRICK_RANDOMIZER);
            this.generateEntrance(serverWorldAccess, random, boundingBox, this.entryDoor, 1, 1, 0);
            Direction direction = this.getFacing();
            if (direction == Direction.NORTH || direction == Direction.EAST) {
                this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 0, 1, 1, 0, 3, 3, AIR, AIR, false);
            } else {
                this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 4, 1, 1, 4, 3, 3, AIR, AIR, false);
            }
            return true;
        }
    }

    public static abstract class Turn
    extends Piece {
        protected Turn(StructurePieceType structurePieceType, int i) {
            super(structurePieceType, i);
        }

        public Turn(StructurePieceType structurePieceType, CompoundTag compoundTag) {
            super(structurePieceType, compoundTag);
        }
    }

    public static class Stairs
    extends Piece {
        public Stairs(int i, Random random, BlockBox blockBox, Direction direction) {
            super(StructurePieceType.STRONGHOLD_STAIRS, i);
            this.setOrientation(direction);
            this.entryDoor = this.getRandomEntrance(random);
            this.boundingBox = blockBox;
        }

        public Stairs(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.STRONGHOLD_STAIRS, compoundTag);
        }

        @Override
        public void placeJigsaw(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
            this.method_14874((Start)structurePiece, list, random, 1, 1);
        }

        public static Stairs method_14868(List<StructurePiece> list, Random random, int i, int j, int k, Direction direction, int l) {
            BlockBox blockBox = BlockBox.rotated(i, j, k, -1, -7, 0, 5, 11, 8, direction);
            if (!Stairs.method_14871(blockBox) || StructurePiece.getOverlappingPiece(list, blockBox) != null) {
                return null;
            }
            return new Stairs(l, random, blockBox, direction);
        }

        @Override
        public boolean generate(ServerWorldAccess serverWorldAccess, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
            this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 0, 0, 0, 4, 10, 7, true, random, STONE_BRICK_RANDOMIZER);
            this.generateEntrance(serverWorldAccess, random, boundingBox, this.entryDoor, 1, 7, 0);
            this.generateEntrance(serverWorldAccess, random, boundingBox, Piece.EntranceType.OPENING, 1, 1, 7);
            BlockState blockState = (BlockState)Blocks.COBBLESTONE_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.SOUTH);
            for (int i = 0; i < 6; ++i) {
                this.addBlock(serverWorldAccess, blockState, 1, 6 - i, 1 + i, boundingBox);
                this.addBlock(serverWorldAccess, blockState, 2, 6 - i, 1 + i, boundingBox);
                this.addBlock(serverWorldAccess, blockState, 3, 6 - i, 1 + i, boundingBox);
                if (i >= 5) continue;
                this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 1, 5 - i, 1 + i, boundingBox);
                this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 2, 5 - i, 1 + i, boundingBox);
                this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 3, 5 - i, 1 + i, boundingBox);
            }
            return true;
        }
    }

    public static class ChestCorridor
    extends Piece {
        private boolean chestGenerated;

        public ChestCorridor(int i, Random random, BlockBox blockBox, Direction direction) {
            super(StructurePieceType.STRONGHOLD_CHEST_CORRIDOR, i);
            this.setOrientation(direction);
            this.entryDoor = this.getRandomEntrance(random);
            this.boundingBox = blockBox;
        }

        public ChestCorridor(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.STRONGHOLD_CHEST_CORRIDOR, compoundTag);
            this.chestGenerated = compoundTag.getBoolean("Chest");
        }

        @Override
        protected void toNbt(CompoundTag tag) {
            super.toNbt(tag);
            tag.putBoolean("Chest", this.chestGenerated);
        }

        @Override
        public void placeJigsaw(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
            this.method_14874((Start)structurePiece, list, random, 1, 1);
        }

        public static ChestCorridor method_14856(List<StructurePiece> list, Random random, int i, int j, int k, Direction direction, int l) {
            BlockBox blockBox = BlockBox.rotated(i, j, k, -1, -1, 0, 5, 5, 7, direction);
            if (!ChestCorridor.method_14871(blockBox) || StructurePiece.getOverlappingPiece(list, blockBox) != null) {
                return null;
            }
            return new ChestCorridor(l, random, blockBox, direction);
        }

        @Override
        public boolean generate(ServerWorldAccess serverWorldAccess, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
            this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 0, 0, 0, 4, 4, 6, true, random, STONE_BRICK_RANDOMIZER);
            this.generateEntrance(serverWorldAccess, random, boundingBox, this.entryDoor, 1, 1, 0);
            this.generateEntrance(serverWorldAccess, random, boundingBox, Piece.EntranceType.OPENING, 1, 1, 6);
            this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 3, 1, 2, 3, 1, 4, Blocks.STONE_BRICKS.getDefaultState(), Blocks.STONE_BRICKS.getDefaultState(), false);
            this.addBlock(serverWorldAccess, Blocks.STONE_BRICK_SLAB.getDefaultState(), 3, 1, 1, boundingBox);
            this.addBlock(serverWorldAccess, Blocks.STONE_BRICK_SLAB.getDefaultState(), 3, 1, 5, boundingBox);
            this.addBlock(serverWorldAccess, Blocks.STONE_BRICK_SLAB.getDefaultState(), 3, 2, 2, boundingBox);
            this.addBlock(serverWorldAccess, Blocks.STONE_BRICK_SLAB.getDefaultState(), 3, 2, 4, boundingBox);
            for (int i = 2; i <= 4; ++i) {
                this.addBlock(serverWorldAccess, Blocks.STONE_BRICK_SLAB.getDefaultState(), 2, 1, i, boundingBox);
            }
            if (!this.chestGenerated && boundingBox.contains(new BlockPos(this.applyXTransform(3, 3), this.applyYTransform(2), this.applyZTransform(3, 3)))) {
                this.chestGenerated = true;
                this.addChest(serverWorldAccess, boundingBox, random, 3, 2, 3, LootTables.STRONGHOLD_CORRIDOR_CHEST);
            }
            return true;
        }
    }

    public static class Corridor
    extends Piece {
        private final boolean leftExitExists;
        private final boolean rightExitExixts;

        public Corridor(int i, Random random, BlockBox blockBox, Direction direction) {
            super(StructurePieceType.STRONGHOLD_CORRIDOR, i);
            this.setOrientation(direction);
            this.entryDoor = this.getRandomEntrance(random);
            this.boundingBox = blockBox;
            this.leftExitExists = random.nextInt(2) == 0;
            this.rightExitExixts = random.nextInt(2) == 0;
        }

        public Corridor(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.STRONGHOLD_CORRIDOR, compoundTag);
            this.leftExitExists = compoundTag.getBoolean("Left");
            this.rightExitExixts = compoundTag.getBoolean("Right");
        }

        @Override
        protected void toNbt(CompoundTag tag) {
            super.toNbt(tag);
            tag.putBoolean("Left", this.leftExitExists);
            tag.putBoolean("Right", this.rightExitExixts);
        }

        @Override
        public void placeJigsaw(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
            this.method_14874((Start)structurePiece, list, random, 1, 1);
            if (this.leftExitExists) {
                this.method_14870((Start)structurePiece, list, random, 1, 2);
            }
            if (this.rightExitExixts) {
                this.method_14873((Start)structurePiece, list, random, 1, 2);
            }
        }

        public static Corridor method_14867(List<StructurePiece> list, Random random, int i, int j, int k, Direction direction, int l) {
            BlockBox blockBox = BlockBox.rotated(i, j, k, -1, -1, 0, 5, 5, 7, direction);
            if (!Corridor.method_14871(blockBox) || StructurePiece.getOverlappingPiece(list, blockBox) != null) {
                return null;
            }
            return new Corridor(l, random, blockBox, direction);
        }

        @Override
        public boolean generate(ServerWorldAccess serverWorldAccess, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
            this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 0, 0, 0, 4, 4, 6, true, random, STONE_BRICK_RANDOMIZER);
            this.generateEntrance(serverWorldAccess, random, boundingBox, this.entryDoor, 1, 1, 0);
            this.generateEntrance(serverWorldAccess, random, boundingBox, Piece.EntranceType.OPENING, 1, 1, 6);
            BlockState blockState = (BlockState)Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, Direction.EAST);
            BlockState blockState2 = (BlockState)Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, Direction.WEST);
            this.addBlockWithRandomThreshold(serverWorldAccess, boundingBox, random, 0.1f, 1, 2, 1, blockState);
            this.addBlockWithRandomThreshold(serverWorldAccess, boundingBox, random, 0.1f, 3, 2, 1, blockState2);
            this.addBlockWithRandomThreshold(serverWorldAccess, boundingBox, random, 0.1f, 1, 2, 5, blockState);
            this.addBlockWithRandomThreshold(serverWorldAccess, boundingBox, random, 0.1f, 3, 2, 5, blockState2);
            if (this.leftExitExists) {
                this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 0, 1, 2, 0, 3, 4, AIR, AIR, false);
            }
            if (this.rightExitExixts) {
                this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 4, 1, 2, 4, 3, 4, AIR, AIR, false);
            }
            return true;
        }
    }

    public static class Start
    extends SpiralStaircase {
        public PieceSetting field_15284;
        @Nullable
        public PortalRoom field_15283;
        public final List<StructurePiece> field_15282 = Lists.newArrayList();

        public Start(Random random, int i, int j) {
            super(StructurePieceType.STRONGHOLD_START, 0, random, i, j);
        }

        public Start(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.STRONGHOLD_START, compoundTag);
        }
    }

    public static class SpiralStaircase
    extends Piece {
        private final boolean isStructureStart;

        public SpiralStaircase(StructurePieceType structurePieceType, int i, Random random, int j, int k) {
            super(structurePieceType, i);
            this.isStructureStart = true;
            this.setOrientation(Direction.Type.HORIZONTAL.random(random));
            this.entryDoor = Piece.EntranceType.OPENING;
            this.boundingBox = this.getFacing().getAxis() == Direction.Axis.Z ? new BlockBox(j, 64, k, j + 5 - 1, 74, k + 5 - 1) : new BlockBox(j, 64, k, j + 5 - 1, 74, k + 5 - 1);
        }

        public SpiralStaircase(int i, Random random, BlockBox blockBox, Direction direction) {
            super(StructurePieceType.STRONGHOLD_SPIRAL_STAIRCASE, i);
            this.isStructureStart = false;
            this.setOrientation(direction);
            this.entryDoor = this.getRandomEntrance(random);
            this.boundingBox = blockBox;
        }

        public SpiralStaircase(StructurePieceType structurePieceType, CompoundTag compoundTag) {
            super(structurePieceType, compoundTag);
            this.isStructureStart = compoundTag.getBoolean("Source");
        }

        public SpiralStaircase(StructureManager structureManager, CompoundTag compoundTag) {
            this(StructurePieceType.STRONGHOLD_SPIRAL_STAIRCASE, compoundTag);
        }

        @Override
        protected void toNbt(CompoundTag tag) {
            super.toNbt(tag);
            tag.putBoolean("Source", this.isStructureStart);
        }

        @Override
        public void placeJigsaw(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
            if (this.isStructureStart) {
                activePieceType = FiveWayCrossing.class;
            }
            this.method_14874((Start)structurePiece, list, random, 1, 1);
        }

        public static SpiralStaircase method_14866(List<StructurePiece> list, Random random, int i, int j, int k, Direction direction, int l) {
            BlockBox blockBox = BlockBox.rotated(i, j, k, -1, -7, 0, 5, 11, 5, direction);
            if (!SpiralStaircase.method_14871(blockBox) || StructurePiece.getOverlappingPiece(list, blockBox) != null) {
                return null;
            }
            return new SpiralStaircase(l, random, blockBox, direction);
        }

        @Override
        public boolean generate(ServerWorldAccess serverWorldAccess, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
            this.fillWithOutline((WorldAccess)serverWorldAccess, boundingBox, 0, 0, 0, 4, 10, 4, true, random, STONE_BRICK_RANDOMIZER);
            this.generateEntrance(serverWorldAccess, random, boundingBox, this.entryDoor, 1, 7, 0);
            this.generateEntrance(serverWorldAccess, random, boundingBox, Piece.EntranceType.OPENING, 1, 1, 4);
            this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 2, 6, 1, boundingBox);
            this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 1, 5, 1, boundingBox);
            this.addBlock(serverWorldAccess, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 1, 6, 1, boundingBox);
            this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 1, 5, 2, boundingBox);
            this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 1, 4, 3, boundingBox);
            this.addBlock(serverWorldAccess, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 1, 5, 3, boundingBox);
            this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 2, 4, 3, boundingBox);
            this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 3, 3, 3, boundingBox);
            this.addBlock(serverWorldAccess, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 3, 4, 3, boundingBox);
            this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 3, 3, 2, boundingBox);
            this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 3, 2, 1, boundingBox);
            this.addBlock(serverWorldAccess, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 3, 3, 1, boundingBox);
            this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 2, 2, 1, boundingBox);
            this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 1, 1, 1, boundingBox);
            this.addBlock(serverWorldAccess, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 1, 2, 1, boundingBox);
            this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 1, 1, 2, boundingBox);
            this.addBlock(serverWorldAccess, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 1, 1, 3, boundingBox);
            return true;
        }
    }

    public static class SmallCorridor
    extends Piece {
        private final int length;

        public SmallCorridor(int i, BlockBox blockBox, Direction direction) {
            super(StructurePieceType.STRONGHOLD_SMALL_CORRIDOR, i);
            this.setOrientation(direction);
            this.boundingBox = blockBox;
            this.length = direction == Direction.NORTH || direction == Direction.SOUTH ? blockBox.getBlockCountZ() : blockBox.getBlockCountX();
        }

        public SmallCorridor(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.STRONGHOLD_SMALL_CORRIDOR, compoundTag);
            this.length = compoundTag.getInt("Steps");
        }

        @Override
        protected void toNbt(CompoundTag tag) {
            super.toNbt(tag);
            tag.putInt("Steps", this.length);
        }

        public static BlockBox method_14857(List<StructurePiece> list, Random random, int i, int j, int k, Direction direction) {
            int l = 3;
            BlockBox blockBox = BlockBox.rotated(i, j, k, -1, -1, 0, 5, 5, 4, direction);
            StructurePiece structurePiece = StructurePiece.getOverlappingPiece(list, blockBox);
            if (structurePiece == null) {
                return null;
            }
            if (structurePiece.getBoundingBox().minY == blockBox.minY) {
                for (int m = 3; m >= 1; --m) {
                    blockBox = BlockBox.rotated(i, j, k, -1, -1, 0, 5, 5, m - 1, direction);
                    if (structurePiece.getBoundingBox().intersects(blockBox)) continue;
                    return BlockBox.rotated(i, j, k, -1, -1, 0, 5, 5, m, direction);
                }
            }
            return null;
        }

        @Override
        public boolean generate(ServerWorldAccess serverWorldAccess, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
            for (int i = 0; i < this.length; ++i) {
                this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 0, 0, i, boundingBox);
                this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 1, 0, i, boundingBox);
                this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 2, 0, i, boundingBox);
                this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 3, 0, i, boundingBox);
                this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 4, 0, i, boundingBox);
                for (int j = 1; j <= 3; ++j) {
                    this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 0, j, i, boundingBox);
                    this.addBlock(serverWorldAccess, Blocks.CAVE_AIR.getDefaultState(), 1, j, i, boundingBox);
                    this.addBlock(serverWorldAccess, Blocks.CAVE_AIR.getDefaultState(), 2, j, i, boundingBox);
                    this.addBlock(serverWorldAccess, Blocks.CAVE_AIR.getDefaultState(), 3, j, i, boundingBox);
                    this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 4, j, i, boundingBox);
                }
                this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 0, 4, i, boundingBox);
                this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 1, 4, i, boundingBox);
                this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 2, 4, i, boundingBox);
                this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 3, 4, i, boundingBox);
                this.addBlock(serverWorldAccess, Blocks.STONE_BRICKS.getDefaultState(), 4, 4, i, boundingBox);
            }
            return true;
        }
    }

    static abstract class Piece
    extends StructurePiece {
        protected EntranceType entryDoor = EntranceType.OPENING;

        protected Piece(StructurePieceType structurePieceType, int i) {
            super(structurePieceType, i);
        }

        public Piece(StructurePieceType structurePieceType, CompoundTag compoundTag) {
            super(structurePieceType, compoundTag);
            this.entryDoor = EntranceType.valueOf(compoundTag.getString("EntryDoor"));
        }

        @Override
        protected void toNbt(CompoundTag tag) {
            tag.putString("EntryDoor", this.entryDoor.name());
        }

        protected void generateEntrance(WorldAccess world, Random random, BlockBox boundingBox, EntranceType type, int x, int y, int z) {
            switch (type) {
                case OPENING: {
                    this.fillWithOutline(world, boundingBox, x, y, z, x + 3 - 1, y + 3 - 1, z, AIR, AIR, false);
                    break;
                }
                case WOOD_DOOR: {
                    this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), x, y, z, boundingBox);
                    this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), x, y + 1, z, boundingBox);
                    this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), x, y + 2, z, boundingBox);
                    this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), x + 1, y + 2, z, boundingBox);
                    this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), x + 2, y + 2, z, boundingBox);
                    this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), x + 2, y + 1, z, boundingBox);
                    this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), x + 2, y, z, boundingBox);
                    this.addBlock(world, Blocks.OAK_DOOR.getDefaultState(), x + 1, y, z, boundingBox);
                    this.addBlock(world, (BlockState)Blocks.OAK_DOOR.getDefaultState().with(DoorBlock.HALF, DoubleBlockHalf.UPPER), x + 1, y + 1, z, boundingBox);
                    break;
                }
                case GRATES: {
                    this.addBlock(world, Blocks.CAVE_AIR.getDefaultState(), x + 1, y, z, boundingBox);
                    this.addBlock(world, Blocks.CAVE_AIR.getDefaultState(), x + 1, y + 1, z, boundingBox);
                    this.addBlock(world, (BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.WEST, true), x, y, z, boundingBox);
                    this.addBlock(world, (BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.WEST, true), x, y + 1, z, boundingBox);
                    this.addBlock(world, (BlockState)((BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.EAST, true)).with(PaneBlock.WEST, true), x, y + 2, z, boundingBox);
                    this.addBlock(world, (BlockState)((BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.EAST, true)).with(PaneBlock.WEST, true), x + 1, y + 2, z, boundingBox);
                    this.addBlock(world, (BlockState)((BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.EAST, true)).with(PaneBlock.WEST, true), x + 2, y + 2, z, boundingBox);
                    this.addBlock(world, (BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.EAST, true), x + 2, y + 1, z, boundingBox);
                    this.addBlock(world, (BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.EAST, true), x + 2, y, z, boundingBox);
                    break;
                }
                case IRON_DOOR: {
                    this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), x, y, z, boundingBox);
                    this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), x, y + 1, z, boundingBox);
                    this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), x, y + 2, z, boundingBox);
                    this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), x + 1, y + 2, z, boundingBox);
                    this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), x + 2, y + 2, z, boundingBox);
                    this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), x + 2, y + 1, z, boundingBox);
                    this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), x + 2, y, z, boundingBox);
                    this.addBlock(world, Blocks.IRON_DOOR.getDefaultState(), x + 1, y, z, boundingBox);
                    this.addBlock(world, (BlockState)Blocks.IRON_DOOR.getDefaultState().with(DoorBlock.HALF, DoubleBlockHalf.UPPER), x + 1, y + 1, z, boundingBox);
                    this.addBlock(world, (BlockState)Blocks.STONE_BUTTON.getDefaultState().with(AbstractButtonBlock.FACING, Direction.NORTH), x + 2, y + 1, z + 1, boundingBox);
                    this.addBlock(world, (BlockState)Blocks.STONE_BUTTON.getDefaultState().with(AbstractButtonBlock.FACING, Direction.SOUTH), x + 2, y + 1, z - 1, boundingBox);
                }
            }
        }

        protected EntranceType getRandomEntrance(Random random) {
            int i = random.nextInt(5);
            switch (i) {
                default: {
                    return EntranceType.OPENING;
                }
                case 2: {
                    return EntranceType.WOOD_DOOR;
                }
                case 3: {
                    return EntranceType.GRATES;
                }
                case 4: 
            }
            return EntranceType.IRON_DOOR;
        }

        @Nullable
        protected StructurePiece method_14874(Start start, List<StructurePiece> list, Random random, int i, int j) {
            Direction direction = this.getFacing();
            if (direction != null) {
                switch (direction) {
                    case NORTH: {
                        return StrongholdGenerator.method_14854(start, list, random, this.boundingBox.minX + i, this.boundingBox.minY + j, this.boundingBox.minZ - 1, direction, this.getLength());
                    }
                    case SOUTH: {
                        return StrongholdGenerator.method_14854(start, list, random, this.boundingBox.minX + i, this.boundingBox.minY + j, this.boundingBox.maxZ + 1, direction, this.getLength());
                    }
                    case WEST: {
                        return StrongholdGenerator.method_14854(start, list, random, this.boundingBox.minX - 1, this.boundingBox.minY + j, this.boundingBox.minZ + i, direction, this.getLength());
                    }
                    case EAST: {
                        return StrongholdGenerator.method_14854(start, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY + j, this.boundingBox.minZ + i, direction, this.getLength());
                    }
                }
            }
            return null;
        }

        @Nullable
        protected StructurePiece method_14870(Start start, List<StructurePiece> list, Random random, int i, int j) {
            Direction direction = this.getFacing();
            if (direction != null) {
                switch (direction) {
                    case NORTH: {
                        return StrongholdGenerator.method_14854(start, list, random, this.boundingBox.minX - 1, this.boundingBox.minY + i, this.boundingBox.minZ + j, Direction.WEST, this.getLength());
                    }
                    case SOUTH: {
                        return StrongholdGenerator.method_14854(start, list, random, this.boundingBox.minX - 1, this.boundingBox.minY + i, this.boundingBox.minZ + j, Direction.WEST, this.getLength());
                    }
                    case WEST: {
                        return StrongholdGenerator.method_14854(start, list, random, this.boundingBox.minX + j, this.boundingBox.minY + i, this.boundingBox.minZ - 1, Direction.NORTH, this.getLength());
                    }
                    case EAST: {
                        return StrongholdGenerator.method_14854(start, list, random, this.boundingBox.minX + j, this.boundingBox.minY + i, this.boundingBox.minZ - 1, Direction.NORTH, this.getLength());
                    }
                }
            }
            return null;
        }

        @Nullable
        protected StructurePiece method_14873(Start start, List<StructurePiece> list, Random random, int i, int j) {
            Direction direction = this.getFacing();
            if (direction != null) {
                switch (direction) {
                    case NORTH: {
                        return StrongholdGenerator.method_14854(start, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY + i, this.boundingBox.minZ + j, Direction.EAST, this.getLength());
                    }
                    case SOUTH: {
                        return StrongholdGenerator.method_14854(start, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY + i, this.boundingBox.minZ + j, Direction.EAST, this.getLength());
                    }
                    case WEST: {
                        return StrongholdGenerator.method_14854(start, list, random, this.boundingBox.minX + j, this.boundingBox.minY + i, this.boundingBox.maxZ + 1, Direction.SOUTH, this.getLength());
                    }
                    case EAST: {
                        return StrongholdGenerator.method_14854(start, list, random, this.boundingBox.minX + j, this.boundingBox.minY + i, this.boundingBox.maxZ + 1, Direction.SOUTH, this.getLength());
                    }
                }
            }
            return null;
        }

        protected static boolean method_14871(BlockBox blockBox) {
            return blockBox != null && blockBox.minY > 10;
        }

        public static enum EntranceType {
            OPENING,
            WOOD_DOOR,
            GRATES,
            IRON_DOOR;

        }
    }

    static class PieceSetting {
        public final Class<? extends Piece> pieceType;
        public final int field_15278;
        public int generatedCount;
        public final int limit;

        public PieceSetting(Class<? extends Piece> class_, int i, int j) {
            this.pieceType = class_;
            this.field_15278 = i;
            this.limit = j;
        }

        public boolean canGenerate(int depth) {
            return this.limit == 0 || this.generatedCount < this.limit;
        }

        public boolean canGenerate() {
            return this.limit == 0 || this.generatedCount < this.limit;
        }
    }
}

