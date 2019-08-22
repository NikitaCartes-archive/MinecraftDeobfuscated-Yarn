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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.loot.LootTables;
import org.jetbrains.annotations.Nullable;

public class StrongholdGenerator {
    private static final class_3427[] field_15265 = new class_3427[]{new class_3427(Corridor.class, 40, 0), new class_3427(PrisonHall.class, 5, 5), new class_3427(LeftTurn.class, 20, 0), new class_3427(RightTurn.class, 20, 0), new class_3427(SquareRoom.class, 10, 6), new class_3427(Stairs.class, 5, 5), new class_3427(SpiralStaircase.class, 5, 5), new class_3427(FiveWayCrossing.class, 5, 4), new class_3427(ChestCorridor.class, 5, 4), new class_3427(Library.class, 10, 2){

        @Override
        public boolean method_14862(int i) {
            return super.method_14862(i) && i > 4;
        }
    }, new class_3427(PortalRoom.class, 20, 1){

        @Override
        public boolean method_14862(int i) {
            return super.method_14862(i) && i > 5;
        }
    }};
    private static List<class_3427> field_15267;
    private static Class<? extends Piece> field_15266;
    private static int field_15264;
    private static final StoneBrickRandomizer field_15263;

    public static void method_14855() {
        field_15267 = Lists.newArrayList();
        for (class_3427 lv : field_15265) {
            lv.field_15277 = 0;
            field_15267.add(lv);
        }
        field_15266 = null;
    }

    private static boolean method_14852() {
        boolean bl = false;
        field_15264 = 0;
        for (class_3427 lv : field_15267) {
            if (lv.field_15275 > 0 && lv.field_15277 < lv.field_15275) {
                bl = true;
            }
            field_15264 += lv.field_15278;
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
        if (field_15266 != null) {
            Piece piece = StrongholdGenerator.method_14847(field_15266, list, random, i, j, k, direction, l);
            field_15266 = null;
            if (piece != null) {
                return piece;
            }
        }
        int m = 0;
        block0: while (m < 5) {
            ++m;
            int n = random.nextInt(field_15264);
            for (class_3427 lv : field_15267) {
                if ((n -= lv.field_15278) >= 0) continue;
                if (!lv.method_14862(l) || lv == start.field_15284) continue block0;
                Piece piece2 = StrongholdGenerator.method_14847(lv.field_15276, list, random, i, j, k, direction, l);
                if (piece2 == null) continue;
                ++lv.field_15277;
                start.field_15284 = lv;
                if (!lv.method_14861()) {
                    field_15267.remove(lv);
                }
                return piece2;
            }
        }
        MutableIntBoundingBox mutableIntBoundingBox = SmallCorridor.method_14857(list, random, i, j, k, direction);
        if (mutableIntBoundingBox != null && mutableIntBoundingBox.minY > 1) {
            return new SmallCorridor(l, mutableIntBoundingBox, direction);
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
        field_15263 = new StoneBrickRandomizer();
    }

    static class StoneBrickRandomizer
    extends StructurePiece.BlockRandomizer {
        private StoneBrickRandomizer() {
        }

        @Override
        public void setBlock(Random random, int i, int j, int k, boolean bl) {
            float f;
            this.block = bl ? ((f = random.nextFloat()) < 0.2f ? Blocks.CRACKED_STONE_BRICKS.getDefaultState() : (f < 0.5f ? Blocks.MOSSY_STONE_BRICKS.getDefaultState() : (f < 0.55f ? Blocks.INFESTED_STONE_BRICKS.getDefaultState() : Blocks.STONE_BRICKS.getDefaultState()))) : Blocks.CAVE_AIR.getDefaultState();
        }
    }

    public static class PortalRoom
    extends Piece {
        private boolean spawnerPlaced;

        public PortalRoom(int i, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
            super(StructurePieceType.STRONGHOLD_PORTAL_ROOM, i);
            this.setOrientation(direction);
            this.boundingBox = mutableIntBoundingBox;
        }

        public PortalRoom(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.STRONGHOLD_PORTAL_ROOM, compoundTag);
            this.spawnerPlaced = compoundTag.getBoolean("Mob");
        }

        @Override
        protected void toNbt(CompoundTag compoundTag) {
            super.toNbt(compoundTag);
            compoundTag.putBoolean("Mob", this.spawnerPlaced);
        }

        @Override
        public void method_14918(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
            if (structurePiece != null) {
                ((Start)structurePiece).field_15283 = this;
            }
        }

        public static PortalRoom method_14863(List<StructurePiece> list, int i, int j, int k, Direction direction, int l) {
            MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.createRotated(i, j, k, -4, -1, 0, 11, 8, 16, direction);
            if (!PortalRoom.method_14871(mutableIntBoundingBox) || StructurePiece.method_14932(list, mutableIntBoundingBox) != null) {
                return null;
            }
            return new PortalRoom(l, mutableIntBoundingBox, direction);
        }

        @Override
        public boolean generate(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
            int j;
            this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 0, 0, 10, 7, 15, false, random, field_15263);
            this.generateEntrance(iWorld, random, mutableIntBoundingBox, Piece.EntranceType.GRATES, 4, 1, 0);
            int i = 6;
            this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, i, 1, 1, i, 14, false, random, field_15263);
            this.fillWithOutline(iWorld, mutableIntBoundingBox, 9, i, 1, 9, i, 14, false, random, field_15263);
            this.fillWithOutline(iWorld, mutableIntBoundingBox, 2, i, 1, 8, i, 2, false, random, field_15263);
            this.fillWithOutline(iWorld, mutableIntBoundingBox, 2, i, 14, 8, i, 14, false, random, field_15263);
            this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 1, 1, 2, 1, 4, false, random, field_15263);
            this.fillWithOutline(iWorld, mutableIntBoundingBox, 8, 1, 1, 9, 1, 4, false, random, field_15263);
            this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 1, 1, 1, 1, 3, Blocks.LAVA.getDefaultState(), Blocks.LAVA.getDefaultState(), false);
            this.fillWithOutline(iWorld, mutableIntBoundingBox, 9, 1, 1, 9, 1, 3, Blocks.LAVA.getDefaultState(), Blocks.LAVA.getDefaultState(), false);
            this.fillWithOutline(iWorld, mutableIntBoundingBox, 3, 1, 8, 7, 1, 12, false, random, field_15263);
            this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 1, 9, 6, 1, 11, Blocks.LAVA.getDefaultState(), Blocks.LAVA.getDefaultState(), false);
            BlockState blockState = (BlockState)((BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.NORTH, true)).with(PaneBlock.SOUTH, true);
            BlockState blockState2 = (BlockState)((BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.WEST, true)).with(PaneBlock.EAST, true);
            for (j = 3; j < 14; j += 2) {
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 3, j, 0, 4, j, blockState, blockState, false);
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 10, 3, j, 10, 4, j, blockState, blockState, false);
            }
            for (j = 2; j < 9; j += 2) {
                this.fillWithOutline(iWorld, mutableIntBoundingBox, j, 3, 15, j, 4, 15, blockState2, blockState2, false);
            }
            BlockState blockState3 = (BlockState)Blocks.STONE_BRICK_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.NORTH);
            this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 1, 5, 6, 1, 7, false, random, field_15263);
            this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 2, 6, 6, 2, 7, false, random, field_15263);
            this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 3, 7, 6, 3, 7, false, random, field_15263);
            for (int k = 4; k <= 6; ++k) {
                this.addBlock(iWorld, blockState3, k, 1, 4, mutableIntBoundingBox);
                this.addBlock(iWorld, blockState3, k, 2, 5, mutableIntBoundingBox);
                this.addBlock(iWorld, blockState3, k, 3, 6, mutableIntBoundingBox);
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
            this.addBlock(iWorld, (BlockState)blockState4.with(EndPortalFrameBlock.EYE, bls[0]), 4, 3, 8, mutableIntBoundingBox);
            this.addBlock(iWorld, (BlockState)blockState4.with(EndPortalFrameBlock.EYE, bls[1]), 5, 3, 8, mutableIntBoundingBox);
            this.addBlock(iWorld, (BlockState)blockState4.with(EndPortalFrameBlock.EYE, bls[2]), 6, 3, 8, mutableIntBoundingBox);
            this.addBlock(iWorld, (BlockState)blockState5.with(EndPortalFrameBlock.EYE, bls[3]), 4, 3, 12, mutableIntBoundingBox);
            this.addBlock(iWorld, (BlockState)blockState5.with(EndPortalFrameBlock.EYE, bls[4]), 5, 3, 12, mutableIntBoundingBox);
            this.addBlock(iWorld, (BlockState)blockState5.with(EndPortalFrameBlock.EYE, bls[5]), 6, 3, 12, mutableIntBoundingBox);
            this.addBlock(iWorld, (BlockState)blockState6.with(EndPortalFrameBlock.EYE, bls[6]), 3, 3, 9, mutableIntBoundingBox);
            this.addBlock(iWorld, (BlockState)blockState6.with(EndPortalFrameBlock.EYE, bls[7]), 3, 3, 10, mutableIntBoundingBox);
            this.addBlock(iWorld, (BlockState)blockState6.with(EndPortalFrameBlock.EYE, bls[8]), 3, 3, 11, mutableIntBoundingBox);
            this.addBlock(iWorld, (BlockState)blockState7.with(EndPortalFrameBlock.EYE, bls[9]), 7, 3, 9, mutableIntBoundingBox);
            this.addBlock(iWorld, (BlockState)blockState7.with(EndPortalFrameBlock.EYE, bls[10]), 7, 3, 10, mutableIntBoundingBox);
            this.addBlock(iWorld, (BlockState)blockState7.with(EndPortalFrameBlock.EYE, bls[11]), 7, 3, 11, mutableIntBoundingBox);
            if (bl) {
                BlockState blockState8 = Blocks.END_PORTAL.getDefaultState();
                this.addBlock(iWorld, blockState8, 4, 3, 9, mutableIntBoundingBox);
                this.addBlock(iWorld, blockState8, 5, 3, 9, mutableIntBoundingBox);
                this.addBlock(iWorld, blockState8, 6, 3, 9, mutableIntBoundingBox);
                this.addBlock(iWorld, blockState8, 4, 3, 10, mutableIntBoundingBox);
                this.addBlock(iWorld, blockState8, 5, 3, 10, mutableIntBoundingBox);
                this.addBlock(iWorld, blockState8, 6, 3, 10, mutableIntBoundingBox);
                this.addBlock(iWorld, blockState8, 4, 3, 11, mutableIntBoundingBox);
                this.addBlock(iWorld, blockState8, 5, 3, 11, mutableIntBoundingBox);
                this.addBlock(iWorld, blockState8, 6, 3, 11, mutableIntBoundingBox);
            }
            if (!this.spawnerPlaced) {
                i = this.applyYTransform(3);
                BlockPos blockPos = new BlockPos(this.applyXTransform(5, 6), i, this.applyZTransform(5, 6));
                if (mutableIntBoundingBox.contains(blockPos)) {
                    this.spawnerPlaced = true;
                    iWorld.setBlockState(blockPos, Blocks.SPAWNER.getDefaultState(), 2);
                    BlockEntity blockEntity = iWorld.getBlockEntity(blockPos);
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

        public FiveWayCrossing(int i, Random random, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
            super(StructurePieceType.STRONGHOLD_FIVE_WAY_CROSSING, i);
            this.setOrientation(direction);
            this.entryDoor = this.getRandomEntrance(random);
            this.boundingBox = mutableIntBoundingBox;
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
        protected void toNbt(CompoundTag compoundTag) {
            super.toNbt(compoundTag);
            compoundTag.putBoolean("leftLow", this.lowerLeftExists);
            compoundTag.putBoolean("leftHigh", this.upperLeftExists);
            compoundTag.putBoolean("rightLow", this.lowerRightExists);
            compoundTag.putBoolean("rightHigh", this.upperRightExists);
        }

        @Override
        public void method_14918(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
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
            MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.createRotated(i, j, k, -4, -3, 0, 10, 9, 11, direction);
            if (!FiveWayCrossing.method_14871(mutableIntBoundingBox) || StructurePiece.method_14932(list, mutableIntBoundingBox) != null) {
                return null;
            }
            return new FiveWayCrossing(l, random, mutableIntBoundingBox, direction);
        }

        @Override
        public boolean generate(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
            this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 0, 0, 9, 8, 10, true, random, field_15263);
            this.generateEntrance(iWorld, random, mutableIntBoundingBox, this.entryDoor, 4, 3, 0);
            if (this.lowerLeftExists) {
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 3, 1, 0, 5, 3, AIR, AIR, false);
            }
            if (this.lowerRightExists) {
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 9, 3, 1, 9, 5, 3, AIR, AIR, false);
            }
            if (this.upperLeftExists) {
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 5, 7, 0, 7, 9, AIR, AIR, false);
            }
            if (this.upperRightExists) {
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 9, 5, 7, 9, 7, 9, AIR, AIR, false);
            }
            this.fillWithOutline(iWorld, mutableIntBoundingBox, 5, 1, 10, 7, 3, 10, AIR, AIR, false);
            this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 2, 1, 8, 2, 6, false, random, field_15263);
            this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 1, 5, 4, 4, 9, false, random, field_15263);
            this.fillWithOutline(iWorld, mutableIntBoundingBox, 8, 1, 5, 8, 4, 9, false, random, field_15263);
            this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 4, 7, 3, 4, 9, false, random, field_15263);
            this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 3, 5, 3, 3, 6, false, random, field_15263);
            this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 3, 4, 3, 3, 4, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), Blocks.SMOOTH_STONE_SLAB.getDefaultState(), false);
            this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 4, 6, 3, 4, 6, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), Blocks.SMOOTH_STONE_SLAB.getDefaultState(), false);
            this.fillWithOutline(iWorld, mutableIntBoundingBox, 5, 1, 7, 7, 1, 8, false, random, field_15263);
            this.fillWithOutline(iWorld, mutableIntBoundingBox, 5, 1, 9, 7, 1, 9, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), Blocks.SMOOTH_STONE_SLAB.getDefaultState(), false);
            this.fillWithOutline(iWorld, mutableIntBoundingBox, 5, 2, 7, 7, 2, 7, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), Blocks.SMOOTH_STONE_SLAB.getDefaultState(), false);
            this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 5, 7, 4, 5, 9, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), Blocks.SMOOTH_STONE_SLAB.getDefaultState(), false);
            this.fillWithOutline(iWorld, mutableIntBoundingBox, 8, 5, 7, 8, 5, 9, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), Blocks.SMOOTH_STONE_SLAB.getDefaultState(), false);
            this.fillWithOutline(iWorld, mutableIntBoundingBox, 5, 5, 7, 7, 5, 9, (BlockState)Blocks.SMOOTH_STONE_SLAB.getDefaultState().with(SlabBlock.TYPE, SlabType.DOUBLE), (BlockState)Blocks.SMOOTH_STONE_SLAB.getDefaultState().with(SlabBlock.TYPE, SlabType.DOUBLE), false);
            this.addBlock(iWorld, (BlockState)Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, Direction.SOUTH), 6, 5, 6, mutableIntBoundingBox);
            return true;
        }
    }

    public static class Library
    extends Piece {
        private final boolean tall;

        public Library(int i, Random random, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
            super(StructurePieceType.STRONGHOLD_LIBRARY, i);
            this.setOrientation(direction);
            this.entryDoor = this.getRandomEntrance(random);
            this.boundingBox = mutableIntBoundingBox;
            this.tall = mutableIntBoundingBox.getBlockCountY() > 6;
        }

        public Library(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.STRONGHOLD_LIBRARY, compoundTag);
            this.tall = compoundTag.getBoolean("Tall");
        }

        @Override
        protected void toNbt(CompoundTag compoundTag) {
            super.toNbt(compoundTag);
            compoundTag.putBoolean("Tall", this.tall);
        }

        public static Library method_14860(List<StructurePiece> list, Random random, int i, int j, int k, Direction direction, int l) {
            MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.createRotated(i, j, k, -4, -1, 0, 14, 11, 15, direction);
            if (!(Library.method_14871(mutableIntBoundingBox) && StructurePiece.method_14932(list, mutableIntBoundingBox) == null || Library.method_14871(mutableIntBoundingBox = MutableIntBoundingBox.createRotated(i, j, k, -4, -1, 0, 14, 6, 15, direction)) && StructurePiece.method_14932(list, mutableIntBoundingBox) == null)) {
                return null;
            }
            return new Library(l, random, mutableIntBoundingBox, direction);
        }

        @Override
        public boolean generate(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
            int l;
            int i = 11;
            if (!this.tall) {
                i = 6;
            }
            this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 0, 0, 13, i - 1, 14, true, random, field_15263);
            this.generateEntrance(iWorld, random, mutableIntBoundingBox, this.entryDoor, 4, 1, 0);
            this.fillWithOutlineUnderSealevel(iWorld, mutableIntBoundingBox, random, 0.07f, 2, 1, 1, 11, 4, 13, Blocks.COBWEB.getDefaultState(), Blocks.COBWEB.getDefaultState(), false, false);
            boolean j = true;
            int k = 12;
            for (l = 1; l <= 13; ++l) {
                if ((l - 1) % 4 == 0) {
                    this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 1, l, 1, 4, l, Blocks.OAK_PLANKS.getDefaultState(), Blocks.OAK_PLANKS.getDefaultState(), false);
                    this.fillWithOutline(iWorld, mutableIntBoundingBox, 12, 1, l, 12, 4, l, Blocks.OAK_PLANKS.getDefaultState(), Blocks.OAK_PLANKS.getDefaultState(), false);
                    this.addBlock(iWorld, (BlockState)Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, Direction.EAST), 2, 3, l, mutableIntBoundingBox);
                    this.addBlock(iWorld, (BlockState)Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, Direction.WEST), 11, 3, l, mutableIntBoundingBox);
                    if (!this.tall) continue;
                    this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 6, l, 1, 9, l, Blocks.OAK_PLANKS.getDefaultState(), Blocks.OAK_PLANKS.getDefaultState(), false);
                    this.fillWithOutline(iWorld, mutableIntBoundingBox, 12, 6, l, 12, 9, l, Blocks.OAK_PLANKS.getDefaultState(), Blocks.OAK_PLANKS.getDefaultState(), false);
                    continue;
                }
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 1, l, 1, 4, l, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 12, 1, l, 12, 4, l, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
                if (!this.tall) continue;
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 6, l, 1, 9, l, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 12, 6, l, 12, 9, l, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
            }
            for (l = 3; l < 12; l += 2) {
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 3, 1, l, 4, 3, l, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 6, 1, l, 7, 3, l, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 9, 1, l, 10, 3, l, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
            }
            if (this.tall) {
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 5, 1, 3, 5, 13, Blocks.OAK_PLANKS.getDefaultState(), Blocks.OAK_PLANKS.getDefaultState(), false);
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 10, 5, 1, 12, 5, 13, Blocks.OAK_PLANKS.getDefaultState(), Blocks.OAK_PLANKS.getDefaultState(), false);
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 5, 1, 9, 5, 2, Blocks.OAK_PLANKS.getDefaultState(), Blocks.OAK_PLANKS.getDefaultState(), false);
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 5, 12, 9, 5, 13, Blocks.OAK_PLANKS.getDefaultState(), Blocks.OAK_PLANKS.getDefaultState(), false);
                this.addBlock(iWorld, Blocks.OAK_PLANKS.getDefaultState(), 9, 5, 11, mutableIntBoundingBox);
                this.addBlock(iWorld, Blocks.OAK_PLANKS.getDefaultState(), 8, 5, 11, mutableIntBoundingBox);
                this.addBlock(iWorld, Blocks.OAK_PLANKS.getDefaultState(), 9, 5, 10, mutableIntBoundingBox);
                BlockState blockState = (BlockState)((BlockState)Blocks.OAK_FENCE.getDefaultState().with(FenceBlock.WEST, true)).with(FenceBlock.EAST, true);
                BlockState blockState2 = (BlockState)((BlockState)Blocks.OAK_FENCE.getDefaultState().with(FenceBlock.NORTH, true)).with(FenceBlock.SOUTH, true);
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 3, 6, 3, 3, 6, 11, blockState2, blockState2, false);
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 10, 6, 3, 10, 6, 9, blockState2, blockState2, false);
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 6, 2, 9, 6, 2, blockState, blockState, false);
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 6, 12, 7, 6, 12, blockState, blockState, false);
                this.addBlock(iWorld, (BlockState)((BlockState)Blocks.OAK_FENCE.getDefaultState().with(FenceBlock.NORTH, true)).with(FenceBlock.EAST, true), 3, 6, 2, mutableIntBoundingBox);
                this.addBlock(iWorld, (BlockState)((BlockState)Blocks.OAK_FENCE.getDefaultState().with(FenceBlock.SOUTH, true)).with(FenceBlock.EAST, true), 3, 6, 12, mutableIntBoundingBox);
                this.addBlock(iWorld, (BlockState)((BlockState)Blocks.OAK_FENCE.getDefaultState().with(FenceBlock.NORTH, true)).with(FenceBlock.WEST, true), 10, 6, 2, mutableIntBoundingBox);
                for (int m = 0; m <= 2; ++m) {
                    this.addBlock(iWorld, (BlockState)((BlockState)Blocks.OAK_FENCE.getDefaultState().with(FenceBlock.SOUTH, true)).with(FenceBlock.WEST, true), 8 + m, 6, 12 - m, mutableIntBoundingBox);
                    if (m == 2) continue;
                    this.addBlock(iWorld, (BlockState)((BlockState)Blocks.OAK_FENCE.getDefaultState().with(FenceBlock.NORTH, true)).with(FenceBlock.EAST, true), 8 + m, 6, 11 - m, mutableIntBoundingBox);
                }
                BlockState blockState3 = (BlockState)Blocks.LADDER.getDefaultState().with(LadderBlock.FACING, Direction.SOUTH);
                this.addBlock(iWorld, blockState3, 10, 1, 13, mutableIntBoundingBox);
                this.addBlock(iWorld, blockState3, 10, 2, 13, mutableIntBoundingBox);
                this.addBlock(iWorld, blockState3, 10, 3, 13, mutableIntBoundingBox);
                this.addBlock(iWorld, blockState3, 10, 4, 13, mutableIntBoundingBox);
                this.addBlock(iWorld, blockState3, 10, 5, 13, mutableIntBoundingBox);
                this.addBlock(iWorld, blockState3, 10, 6, 13, mutableIntBoundingBox);
                this.addBlock(iWorld, blockState3, 10, 7, 13, mutableIntBoundingBox);
                int n = 7;
                int o = 7;
                BlockState blockState4 = (BlockState)Blocks.OAK_FENCE.getDefaultState().with(FenceBlock.EAST, true);
                this.addBlock(iWorld, blockState4, 6, 9, 7, mutableIntBoundingBox);
                BlockState blockState5 = (BlockState)Blocks.OAK_FENCE.getDefaultState().with(FenceBlock.WEST, true);
                this.addBlock(iWorld, blockState5, 7, 9, 7, mutableIntBoundingBox);
                this.addBlock(iWorld, blockState4, 6, 8, 7, mutableIntBoundingBox);
                this.addBlock(iWorld, blockState5, 7, 8, 7, mutableIntBoundingBox);
                BlockState blockState6 = (BlockState)((BlockState)blockState2.with(FenceBlock.WEST, true)).with(FenceBlock.EAST, true);
                this.addBlock(iWorld, blockState6, 6, 7, 7, mutableIntBoundingBox);
                this.addBlock(iWorld, blockState6, 7, 7, 7, mutableIntBoundingBox);
                this.addBlock(iWorld, blockState4, 5, 7, 7, mutableIntBoundingBox);
                this.addBlock(iWorld, blockState5, 8, 7, 7, mutableIntBoundingBox);
                this.addBlock(iWorld, (BlockState)blockState4.with(FenceBlock.NORTH, true), 6, 7, 6, mutableIntBoundingBox);
                this.addBlock(iWorld, (BlockState)blockState4.with(FenceBlock.SOUTH, true), 6, 7, 8, mutableIntBoundingBox);
                this.addBlock(iWorld, (BlockState)blockState5.with(FenceBlock.NORTH, true), 7, 7, 6, mutableIntBoundingBox);
                this.addBlock(iWorld, (BlockState)blockState5.with(FenceBlock.SOUTH, true), 7, 7, 8, mutableIntBoundingBox);
                BlockState blockState7 = Blocks.TORCH.getDefaultState();
                this.addBlock(iWorld, blockState7, 5, 8, 7, mutableIntBoundingBox);
                this.addBlock(iWorld, blockState7, 8, 8, 7, mutableIntBoundingBox);
                this.addBlock(iWorld, blockState7, 6, 8, 6, mutableIntBoundingBox);
                this.addBlock(iWorld, blockState7, 6, 8, 8, mutableIntBoundingBox);
                this.addBlock(iWorld, blockState7, 7, 8, 6, mutableIntBoundingBox);
                this.addBlock(iWorld, blockState7, 7, 8, 8, mutableIntBoundingBox);
            }
            this.addChest(iWorld, mutableIntBoundingBox, random, 3, 3, 5, LootTables.STRONGHOLD_LIBRARY_CHEST);
            if (this.tall) {
                this.addBlock(iWorld, AIR, 12, 9, 1, mutableIntBoundingBox);
                this.addChest(iWorld, mutableIntBoundingBox, random, 12, 8, 1, LootTables.STRONGHOLD_LIBRARY_CHEST);
            }
            return true;
        }
    }

    public static class PrisonHall
    extends Piece {
        public PrisonHall(int i, Random random, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
            super(StructurePieceType.STRONGHOLD_PRISON_HALL, i);
            this.setOrientation(direction);
            this.entryDoor = this.getRandomEntrance(random);
            this.boundingBox = mutableIntBoundingBox;
        }

        public PrisonHall(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.STRONGHOLD_PRISON_HALL, compoundTag);
        }

        @Override
        public void method_14918(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
            this.method_14874((Start)structurePiece, list, random, 1, 1);
        }

        public static PrisonHall method_14864(List<StructurePiece> list, Random random, int i, int j, int k, Direction direction, int l) {
            MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.createRotated(i, j, k, -1, -1, 0, 9, 5, 11, direction);
            if (!PrisonHall.method_14871(mutableIntBoundingBox) || StructurePiece.method_14932(list, mutableIntBoundingBox) != null) {
                return null;
            }
            return new PrisonHall(l, random, mutableIntBoundingBox, direction);
        }

        @Override
        public boolean generate(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
            this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 0, 0, 8, 4, 10, true, random, field_15263);
            this.generateEntrance(iWorld, random, mutableIntBoundingBox, this.entryDoor, 1, 1, 0);
            this.fillWithOutline(iWorld, mutableIntBoundingBox, 1, 1, 10, 3, 3, 10, AIR, AIR, false);
            this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 1, 1, 4, 3, 1, false, random, field_15263);
            this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 1, 3, 4, 3, 3, false, random, field_15263);
            this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 1, 7, 4, 3, 7, false, random, field_15263);
            this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 1, 9, 4, 3, 9, false, random, field_15263);
            for (int i = 1; i <= 3; ++i) {
                this.addBlock(iWorld, (BlockState)((BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.NORTH, true)).with(PaneBlock.SOUTH, true), 4, i, 4, mutableIntBoundingBox);
                this.addBlock(iWorld, (BlockState)((BlockState)((BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.NORTH, true)).with(PaneBlock.SOUTH, true)).with(PaneBlock.EAST, true), 4, i, 5, mutableIntBoundingBox);
                this.addBlock(iWorld, (BlockState)((BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.NORTH, true)).with(PaneBlock.SOUTH, true), 4, i, 6, mutableIntBoundingBox);
                this.addBlock(iWorld, (BlockState)((BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.WEST, true)).with(PaneBlock.EAST, true), 5, i, 5, mutableIntBoundingBox);
                this.addBlock(iWorld, (BlockState)((BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.WEST, true)).with(PaneBlock.EAST, true), 6, i, 5, mutableIntBoundingBox);
                this.addBlock(iWorld, (BlockState)((BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.WEST, true)).with(PaneBlock.EAST, true), 7, i, 5, mutableIntBoundingBox);
            }
            this.addBlock(iWorld, (BlockState)((BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.NORTH, true)).with(PaneBlock.SOUTH, true), 4, 3, 2, mutableIntBoundingBox);
            this.addBlock(iWorld, (BlockState)((BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.NORTH, true)).with(PaneBlock.SOUTH, true), 4, 3, 8, mutableIntBoundingBox);
            BlockState blockState = (BlockState)Blocks.IRON_DOOR.getDefaultState().with(DoorBlock.FACING, Direction.WEST);
            BlockState blockState2 = (BlockState)((BlockState)Blocks.IRON_DOOR.getDefaultState().with(DoorBlock.FACING, Direction.WEST)).with(DoorBlock.HALF, DoubleBlockHalf.UPPER);
            this.addBlock(iWorld, blockState, 4, 1, 2, mutableIntBoundingBox);
            this.addBlock(iWorld, blockState2, 4, 2, 2, mutableIntBoundingBox);
            this.addBlock(iWorld, blockState, 4, 1, 8, mutableIntBoundingBox);
            this.addBlock(iWorld, blockState2, 4, 2, 8, mutableIntBoundingBox);
            return true;
        }
    }

    public static class SquareRoom
    extends Piece {
        protected final int roomType;

        public SquareRoom(int i, Random random, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
            super(StructurePieceType.STRONGHOLD_SQUARE_ROOM, i);
            this.setOrientation(direction);
            this.entryDoor = this.getRandomEntrance(random);
            this.boundingBox = mutableIntBoundingBox;
            this.roomType = random.nextInt(5);
        }

        public SquareRoom(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.STRONGHOLD_SQUARE_ROOM, compoundTag);
            this.roomType = compoundTag.getInt("Type");
        }

        @Override
        protected void toNbt(CompoundTag compoundTag) {
            super.toNbt(compoundTag);
            compoundTag.putInt("Type", this.roomType);
        }

        @Override
        public void method_14918(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
            this.method_14874((Start)structurePiece, list, random, 4, 1);
            this.method_14870((Start)structurePiece, list, random, 1, 4);
            this.method_14873((Start)structurePiece, list, random, 1, 4);
        }

        public static SquareRoom method_14865(List<StructurePiece> list, Random random, int i, int j, int k, Direction direction, int l) {
            MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.createRotated(i, j, k, -4, -1, 0, 11, 7, 11, direction);
            if (!SquareRoom.method_14871(mutableIntBoundingBox) || StructurePiece.method_14932(list, mutableIntBoundingBox) != null) {
                return null;
            }
            return new SquareRoom(l, random, mutableIntBoundingBox, direction);
        }

        @Override
        public boolean generate(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
            this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 0, 0, 10, 6, 10, true, random, field_15263);
            this.generateEntrance(iWorld, random, mutableIntBoundingBox, this.entryDoor, 4, 1, 0);
            this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 1, 10, 6, 3, 10, AIR, AIR, false);
            this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 1, 4, 0, 3, 6, AIR, AIR, false);
            this.fillWithOutline(iWorld, mutableIntBoundingBox, 10, 1, 4, 10, 3, 6, AIR, AIR, false);
            switch (this.roomType) {
                default: {
                    break;
                }
                case 0: {
                    this.addBlock(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 5, 1, 5, mutableIntBoundingBox);
                    this.addBlock(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 5, 2, 5, mutableIntBoundingBox);
                    this.addBlock(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 5, 3, 5, mutableIntBoundingBox);
                    this.addBlock(iWorld, (BlockState)Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, Direction.WEST), 4, 3, 5, mutableIntBoundingBox);
                    this.addBlock(iWorld, (BlockState)Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, Direction.EAST), 6, 3, 5, mutableIntBoundingBox);
                    this.addBlock(iWorld, (BlockState)Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, Direction.SOUTH), 5, 3, 4, mutableIntBoundingBox);
                    this.addBlock(iWorld, (BlockState)Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, Direction.NORTH), 5, 3, 6, mutableIntBoundingBox);
                    this.addBlock(iWorld, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 4, 1, 4, mutableIntBoundingBox);
                    this.addBlock(iWorld, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 4, 1, 5, mutableIntBoundingBox);
                    this.addBlock(iWorld, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 4, 1, 6, mutableIntBoundingBox);
                    this.addBlock(iWorld, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 6, 1, 4, mutableIntBoundingBox);
                    this.addBlock(iWorld, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 6, 1, 5, mutableIntBoundingBox);
                    this.addBlock(iWorld, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 6, 1, 6, mutableIntBoundingBox);
                    this.addBlock(iWorld, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 5, 1, 4, mutableIntBoundingBox);
                    this.addBlock(iWorld, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 5, 1, 6, mutableIntBoundingBox);
                    break;
                }
                case 1: {
                    for (int i = 0; i < 5; ++i) {
                        this.addBlock(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 3, 1, 3 + i, mutableIntBoundingBox);
                        this.addBlock(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 7, 1, 3 + i, mutableIntBoundingBox);
                        this.addBlock(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 3 + i, 1, 3, mutableIntBoundingBox);
                        this.addBlock(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 3 + i, 1, 7, mutableIntBoundingBox);
                    }
                    this.addBlock(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 5, 1, 5, mutableIntBoundingBox);
                    this.addBlock(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 5, 2, 5, mutableIntBoundingBox);
                    this.addBlock(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 5, 3, 5, mutableIntBoundingBox);
                    this.addBlock(iWorld, Blocks.WATER.getDefaultState(), 5, 4, 5, mutableIntBoundingBox);
                    break;
                }
                case 2: {
                    int i;
                    for (i = 1; i <= 9; ++i) {
                        this.addBlock(iWorld, Blocks.COBBLESTONE.getDefaultState(), 1, 3, i, mutableIntBoundingBox);
                        this.addBlock(iWorld, Blocks.COBBLESTONE.getDefaultState(), 9, 3, i, mutableIntBoundingBox);
                    }
                    for (i = 1; i <= 9; ++i) {
                        this.addBlock(iWorld, Blocks.COBBLESTONE.getDefaultState(), i, 3, 1, mutableIntBoundingBox);
                        this.addBlock(iWorld, Blocks.COBBLESTONE.getDefaultState(), i, 3, 9, mutableIntBoundingBox);
                    }
                    this.addBlock(iWorld, Blocks.COBBLESTONE.getDefaultState(), 5, 1, 4, mutableIntBoundingBox);
                    this.addBlock(iWorld, Blocks.COBBLESTONE.getDefaultState(), 5, 1, 6, mutableIntBoundingBox);
                    this.addBlock(iWorld, Blocks.COBBLESTONE.getDefaultState(), 5, 3, 4, mutableIntBoundingBox);
                    this.addBlock(iWorld, Blocks.COBBLESTONE.getDefaultState(), 5, 3, 6, mutableIntBoundingBox);
                    this.addBlock(iWorld, Blocks.COBBLESTONE.getDefaultState(), 4, 1, 5, mutableIntBoundingBox);
                    this.addBlock(iWorld, Blocks.COBBLESTONE.getDefaultState(), 6, 1, 5, mutableIntBoundingBox);
                    this.addBlock(iWorld, Blocks.COBBLESTONE.getDefaultState(), 4, 3, 5, mutableIntBoundingBox);
                    this.addBlock(iWorld, Blocks.COBBLESTONE.getDefaultState(), 6, 3, 5, mutableIntBoundingBox);
                    for (i = 1; i <= 3; ++i) {
                        this.addBlock(iWorld, Blocks.COBBLESTONE.getDefaultState(), 4, i, 4, mutableIntBoundingBox);
                        this.addBlock(iWorld, Blocks.COBBLESTONE.getDefaultState(), 6, i, 4, mutableIntBoundingBox);
                        this.addBlock(iWorld, Blocks.COBBLESTONE.getDefaultState(), 4, i, 6, mutableIntBoundingBox);
                        this.addBlock(iWorld, Blocks.COBBLESTONE.getDefaultState(), 6, i, 6, mutableIntBoundingBox);
                    }
                    this.addBlock(iWorld, Blocks.TORCH.getDefaultState(), 5, 3, 5, mutableIntBoundingBox);
                    for (i = 2; i <= 8; ++i) {
                        this.addBlock(iWorld, Blocks.OAK_PLANKS.getDefaultState(), 2, 3, i, mutableIntBoundingBox);
                        this.addBlock(iWorld, Blocks.OAK_PLANKS.getDefaultState(), 3, 3, i, mutableIntBoundingBox);
                        if (i <= 3 || i >= 7) {
                            this.addBlock(iWorld, Blocks.OAK_PLANKS.getDefaultState(), 4, 3, i, mutableIntBoundingBox);
                            this.addBlock(iWorld, Blocks.OAK_PLANKS.getDefaultState(), 5, 3, i, mutableIntBoundingBox);
                            this.addBlock(iWorld, Blocks.OAK_PLANKS.getDefaultState(), 6, 3, i, mutableIntBoundingBox);
                        }
                        this.addBlock(iWorld, Blocks.OAK_PLANKS.getDefaultState(), 7, 3, i, mutableIntBoundingBox);
                        this.addBlock(iWorld, Blocks.OAK_PLANKS.getDefaultState(), 8, 3, i, mutableIntBoundingBox);
                    }
                    BlockState blockState = (BlockState)Blocks.LADDER.getDefaultState().with(LadderBlock.FACING, Direction.WEST);
                    this.addBlock(iWorld, blockState, 9, 1, 3, mutableIntBoundingBox);
                    this.addBlock(iWorld, blockState, 9, 2, 3, mutableIntBoundingBox);
                    this.addBlock(iWorld, blockState, 9, 3, 3, mutableIntBoundingBox);
                    this.addChest(iWorld, mutableIntBoundingBox, random, 3, 4, 8, LootTables.STRONGHOLD_CROSSING_CHEST);
                }
            }
            return true;
        }
    }

    public static class RightTurn
    extends class_3466 {
        public RightTurn(int i, Random random, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
            super(StructurePieceType.STRONGHOLD_RIGHT_TURN, i);
            this.setOrientation(direction);
            this.entryDoor = this.getRandomEntrance(random);
            this.boundingBox = mutableIntBoundingBox;
        }

        public RightTurn(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.STRONGHOLD_RIGHT_TURN, compoundTag);
        }

        @Override
        public void method_14918(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
            Direction direction = this.getFacing();
            if (direction == Direction.NORTH || direction == Direction.EAST) {
                this.method_14873((Start)structurePiece, list, random, 1, 1);
            } else {
                this.method_14870((Start)structurePiece, list, random, 1, 1);
            }
        }

        public static RightTurn method_16652(List<StructurePiece> list, Random random, int i, int j, int k, Direction direction, int l) {
            MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.createRotated(i, j, k, -1, -1, 0, 5, 5, 5, direction);
            if (!RightTurn.method_14871(mutableIntBoundingBox) || StructurePiece.method_14932(list, mutableIntBoundingBox) != null) {
                return null;
            }
            return new RightTurn(l, random, mutableIntBoundingBox, direction);
        }

        @Override
        public boolean generate(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
            this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 0, 0, 4, 4, 4, true, random, field_15263);
            this.generateEntrance(iWorld, random, mutableIntBoundingBox, this.entryDoor, 1, 1, 0);
            Direction direction = this.getFacing();
            if (direction == Direction.NORTH || direction == Direction.EAST) {
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 1, 1, 4, 3, 3, AIR, AIR, false);
            } else {
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 1, 1, 0, 3, 3, AIR, AIR, false);
            }
            return true;
        }
    }

    public static class LeftTurn
    extends class_3466 {
        public LeftTurn(int i, Random random, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
            super(StructurePieceType.STRONGHOLD_LEFT_TURN, i);
            this.setOrientation(direction);
            this.entryDoor = this.getRandomEntrance(random);
            this.boundingBox = mutableIntBoundingBox;
        }

        public LeftTurn(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.STRONGHOLD_LEFT_TURN, compoundTag);
        }

        @Override
        public void method_14918(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
            Direction direction = this.getFacing();
            if (direction == Direction.NORTH || direction == Direction.EAST) {
                this.method_14870((Start)structurePiece, list, random, 1, 1);
            } else {
                this.method_14873((Start)structurePiece, list, random, 1, 1);
            }
        }

        public static LeftTurn method_14859(List<StructurePiece> list, Random random, int i, int j, int k, Direction direction, int l) {
            MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.createRotated(i, j, k, -1, -1, 0, 5, 5, 5, direction);
            if (!LeftTurn.method_14871(mutableIntBoundingBox) || StructurePiece.method_14932(list, mutableIntBoundingBox) != null) {
                return null;
            }
            return new LeftTurn(l, random, mutableIntBoundingBox, direction);
        }

        @Override
        public boolean generate(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
            this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 0, 0, 4, 4, 4, true, random, field_15263);
            this.generateEntrance(iWorld, random, mutableIntBoundingBox, this.entryDoor, 1, 1, 0);
            Direction direction = this.getFacing();
            if (direction == Direction.NORTH || direction == Direction.EAST) {
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 1, 1, 0, 3, 3, AIR, AIR, false);
            } else {
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 1, 1, 4, 3, 3, AIR, AIR, false);
            }
            return true;
        }
    }

    public static abstract class class_3466
    extends Piece {
        protected class_3466(StructurePieceType structurePieceType, int i) {
            super(structurePieceType, i);
        }

        public class_3466(StructurePieceType structurePieceType, CompoundTag compoundTag) {
            super(structurePieceType, compoundTag);
        }
    }

    public static class Stairs
    extends Piece {
        public Stairs(int i, Random random, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
            super(StructurePieceType.STRONGHOLD_STAIRS, i);
            this.setOrientation(direction);
            this.entryDoor = this.getRandomEntrance(random);
            this.boundingBox = mutableIntBoundingBox;
        }

        public Stairs(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.STRONGHOLD_STAIRS, compoundTag);
        }

        @Override
        public void method_14918(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
            this.method_14874((Start)structurePiece, list, random, 1, 1);
        }

        public static Stairs method_14868(List<StructurePiece> list, Random random, int i, int j, int k, Direction direction, int l) {
            MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.createRotated(i, j, k, -1, -7, 0, 5, 11, 8, direction);
            if (!Stairs.method_14871(mutableIntBoundingBox) || StructurePiece.method_14932(list, mutableIntBoundingBox) != null) {
                return null;
            }
            return new Stairs(l, random, mutableIntBoundingBox, direction);
        }

        @Override
        public boolean generate(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
            this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 0, 0, 4, 10, 7, true, random, field_15263);
            this.generateEntrance(iWorld, random, mutableIntBoundingBox, this.entryDoor, 1, 7, 0);
            this.generateEntrance(iWorld, random, mutableIntBoundingBox, Piece.EntranceType.OPENING, 1, 1, 7);
            BlockState blockState = (BlockState)Blocks.COBBLESTONE_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.SOUTH);
            for (int i = 0; i < 6; ++i) {
                this.addBlock(iWorld, blockState, 1, 6 - i, 1 + i, mutableIntBoundingBox);
                this.addBlock(iWorld, blockState, 2, 6 - i, 1 + i, mutableIntBoundingBox);
                this.addBlock(iWorld, blockState, 3, 6 - i, 1 + i, mutableIntBoundingBox);
                if (i >= 5) continue;
                this.addBlock(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 1, 5 - i, 1 + i, mutableIntBoundingBox);
                this.addBlock(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 2, 5 - i, 1 + i, mutableIntBoundingBox);
                this.addBlock(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 3, 5 - i, 1 + i, mutableIntBoundingBox);
            }
            return true;
        }
    }

    public static class ChestCorridor
    extends Piece {
        private boolean chestGenerated;

        public ChestCorridor(int i, Random random, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
            super(StructurePieceType.STRONGHOLD_CHEST_CORRIDOR, i);
            this.setOrientation(direction);
            this.entryDoor = this.getRandomEntrance(random);
            this.boundingBox = mutableIntBoundingBox;
        }

        public ChestCorridor(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.STRONGHOLD_CHEST_CORRIDOR, compoundTag);
            this.chestGenerated = compoundTag.getBoolean("Chest");
        }

        @Override
        protected void toNbt(CompoundTag compoundTag) {
            super.toNbt(compoundTag);
            compoundTag.putBoolean("Chest", this.chestGenerated);
        }

        @Override
        public void method_14918(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
            this.method_14874((Start)structurePiece, list, random, 1, 1);
        }

        public static ChestCorridor method_14856(List<StructurePiece> list, Random random, int i, int j, int k, Direction direction, int l) {
            MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.createRotated(i, j, k, -1, -1, 0, 5, 5, 7, direction);
            if (!ChestCorridor.method_14871(mutableIntBoundingBox) || StructurePiece.method_14932(list, mutableIntBoundingBox) != null) {
                return null;
            }
            return new ChestCorridor(l, random, mutableIntBoundingBox, direction);
        }

        @Override
        public boolean generate(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
            this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 0, 0, 4, 4, 6, true, random, field_15263);
            this.generateEntrance(iWorld, random, mutableIntBoundingBox, this.entryDoor, 1, 1, 0);
            this.generateEntrance(iWorld, random, mutableIntBoundingBox, Piece.EntranceType.OPENING, 1, 1, 6);
            this.fillWithOutline(iWorld, mutableIntBoundingBox, 3, 1, 2, 3, 1, 4, Blocks.STONE_BRICKS.getDefaultState(), Blocks.STONE_BRICKS.getDefaultState(), false);
            this.addBlock(iWorld, Blocks.STONE_BRICK_SLAB.getDefaultState(), 3, 1, 1, mutableIntBoundingBox);
            this.addBlock(iWorld, Blocks.STONE_BRICK_SLAB.getDefaultState(), 3, 1, 5, mutableIntBoundingBox);
            this.addBlock(iWorld, Blocks.STONE_BRICK_SLAB.getDefaultState(), 3, 2, 2, mutableIntBoundingBox);
            this.addBlock(iWorld, Blocks.STONE_BRICK_SLAB.getDefaultState(), 3, 2, 4, mutableIntBoundingBox);
            for (int i = 2; i <= 4; ++i) {
                this.addBlock(iWorld, Blocks.STONE_BRICK_SLAB.getDefaultState(), 2, 1, i, mutableIntBoundingBox);
            }
            if (!this.chestGenerated && mutableIntBoundingBox.contains(new BlockPos(this.applyXTransform(3, 3), this.applyYTransform(2), this.applyZTransform(3, 3)))) {
                this.chestGenerated = true;
                this.addChest(iWorld, mutableIntBoundingBox, random, 3, 2, 3, LootTables.STRONGHOLD_CORRIDOR_CHEST);
            }
            return true;
        }
    }

    public static class Corridor
    extends Piece {
        private final boolean leftExitExists;
        private final boolean rightExitExixts;

        public Corridor(int i, Random random, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
            super(StructurePieceType.STRONGHOLD_CORRIDOR, i);
            this.setOrientation(direction);
            this.entryDoor = this.getRandomEntrance(random);
            this.boundingBox = mutableIntBoundingBox;
            this.leftExitExists = random.nextInt(2) == 0;
            this.rightExitExixts = random.nextInt(2) == 0;
        }

        public Corridor(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.STRONGHOLD_CORRIDOR, compoundTag);
            this.leftExitExists = compoundTag.getBoolean("Left");
            this.rightExitExixts = compoundTag.getBoolean("Right");
        }

        @Override
        protected void toNbt(CompoundTag compoundTag) {
            super.toNbt(compoundTag);
            compoundTag.putBoolean("Left", this.leftExitExists);
            compoundTag.putBoolean("Right", this.rightExitExixts);
        }

        @Override
        public void method_14918(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
            this.method_14874((Start)structurePiece, list, random, 1, 1);
            if (this.leftExitExists) {
                this.method_14870((Start)structurePiece, list, random, 1, 2);
            }
            if (this.rightExitExixts) {
                this.method_14873((Start)structurePiece, list, random, 1, 2);
            }
        }

        public static Corridor method_14867(List<StructurePiece> list, Random random, int i, int j, int k, Direction direction, int l) {
            MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.createRotated(i, j, k, -1, -1, 0, 5, 5, 7, direction);
            if (!Corridor.method_14871(mutableIntBoundingBox) || StructurePiece.method_14932(list, mutableIntBoundingBox) != null) {
                return null;
            }
            return new Corridor(l, random, mutableIntBoundingBox, direction);
        }

        @Override
        public boolean generate(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
            this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 0, 0, 4, 4, 6, true, random, field_15263);
            this.generateEntrance(iWorld, random, mutableIntBoundingBox, this.entryDoor, 1, 1, 0);
            this.generateEntrance(iWorld, random, mutableIntBoundingBox, Piece.EntranceType.OPENING, 1, 1, 6);
            BlockState blockState = (BlockState)Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, Direction.EAST);
            BlockState blockState2 = (BlockState)Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, Direction.WEST);
            this.addBlockWithRandomThreshold(iWorld, mutableIntBoundingBox, random, 0.1f, 1, 2, 1, blockState);
            this.addBlockWithRandomThreshold(iWorld, mutableIntBoundingBox, random, 0.1f, 3, 2, 1, blockState2);
            this.addBlockWithRandomThreshold(iWorld, mutableIntBoundingBox, random, 0.1f, 1, 2, 5, blockState);
            this.addBlockWithRandomThreshold(iWorld, mutableIntBoundingBox, random, 0.1f, 3, 2, 5, blockState2);
            if (this.leftExitExists) {
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 1, 2, 0, 3, 4, AIR, AIR, false);
            }
            if (this.rightExitExixts) {
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 4, 1, 2, 4, 3, 4, AIR, AIR, false);
            }
            return true;
        }
    }

    public static class Start
    extends SpiralStaircase {
        public class_3427 field_15284;
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
            this.boundingBox = this.getFacing().getAxis() == Direction.Axis.Z ? new MutableIntBoundingBox(j, 64, k, j + 5 - 1, 74, k + 5 - 1) : new MutableIntBoundingBox(j, 64, k, j + 5 - 1, 74, k + 5 - 1);
        }

        public SpiralStaircase(int i, Random random, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
            super(StructurePieceType.STRONGHOLD_SPIRAL_STAIRCASE, i);
            this.isStructureStart = false;
            this.setOrientation(direction);
            this.entryDoor = this.getRandomEntrance(random);
            this.boundingBox = mutableIntBoundingBox;
        }

        public SpiralStaircase(StructurePieceType structurePieceType, CompoundTag compoundTag) {
            super(structurePieceType, compoundTag);
            this.isStructureStart = compoundTag.getBoolean("Source");
        }

        public SpiralStaircase(StructureManager structureManager, CompoundTag compoundTag) {
            this(StructurePieceType.STRONGHOLD_SPIRAL_STAIRCASE, compoundTag);
        }

        @Override
        protected void toNbt(CompoundTag compoundTag) {
            super.toNbt(compoundTag);
            compoundTag.putBoolean("Source", this.isStructureStart);
        }

        @Override
        public void method_14918(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
            if (this.isStructureStart) {
                field_15266 = FiveWayCrossing.class;
            }
            this.method_14874((Start)structurePiece, list, random, 1, 1);
        }

        public static SpiralStaircase method_14866(List<StructurePiece> list, Random random, int i, int j, int k, Direction direction, int l) {
            MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.createRotated(i, j, k, -1, -7, 0, 5, 11, 5, direction);
            if (!SpiralStaircase.method_14871(mutableIntBoundingBox) || StructurePiece.method_14932(list, mutableIntBoundingBox) != null) {
                return null;
            }
            return new SpiralStaircase(l, random, mutableIntBoundingBox, direction);
        }

        @Override
        public boolean generate(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
            this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 0, 0, 4, 10, 4, true, random, field_15263);
            this.generateEntrance(iWorld, random, mutableIntBoundingBox, this.entryDoor, 1, 7, 0);
            this.generateEntrance(iWorld, random, mutableIntBoundingBox, Piece.EntranceType.OPENING, 1, 1, 4);
            this.addBlock(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 2, 6, 1, mutableIntBoundingBox);
            this.addBlock(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 1, 5, 1, mutableIntBoundingBox);
            this.addBlock(iWorld, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 1, 6, 1, mutableIntBoundingBox);
            this.addBlock(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 1, 5, 2, mutableIntBoundingBox);
            this.addBlock(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 1, 4, 3, mutableIntBoundingBox);
            this.addBlock(iWorld, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 1, 5, 3, mutableIntBoundingBox);
            this.addBlock(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 2, 4, 3, mutableIntBoundingBox);
            this.addBlock(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 3, 3, 3, mutableIntBoundingBox);
            this.addBlock(iWorld, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 3, 4, 3, mutableIntBoundingBox);
            this.addBlock(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 3, 3, 2, mutableIntBoundingBox);
            this.addBlock(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 3, 2, 1, mutableIntBoundingBox);
            this.addBlock(iWorld, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 3, 3, 1, mutableIntBoundingBox);
            this.addBlock(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 2, 2, 1, mutableIntBoundingBox);
            this.addBlock(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 1, 1, 1, mutableIntBoundingBox);
            this.addBlock(iWorld, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 1, 2, 1, mutableIntBoundingBox);
            this.addBlock(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 1, 1, 2, mutableIntBoundingBox);
            this.addBlock(iWorld, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 1, 1, 3, mutableIntBoundingBox);
            return true;
        }
    }

    public static class SmallCorridor
    extends Piece {
        private final int length;

        public SmallCorridor(int i, MutableIntBoundingBox mutableIntBoundingBox, Direction direction) {
            super(StructurePieceType.STRONGHOLD_SMALL_CORRIDOR, i);
            this.setOrientation(direction);
            this.boundingBox = mutableIntBoundingBox;
            this.length = direction == Direction.NORTH || direction == Direction.SOUTH ? mutableIntBoundingBox.getBlockCountZ() : mutableIntBoundingBox.getBlockCountX();
        }

        public SmallCorridor(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.STRONGHOLD_SMALL_CORRIDOR, compoundTag);
            this.length = compoundTag.getInt("Steps");
        }

        @Override
        protected void toNbt(CompoundTag compoundTag) {
            super.toNbt(compoundTag);
            compoundTag.putInt("Steps", this.length);
        }

        public static MutableIntBoundingBox method_14857(List<StructurePiece> list, Random random, int i, int j, int k, Direction direction) {
            int l = 3;
            MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.createRotated(i, j, k, -1, -1, 0, 5, 5, 4, direction);
            StructurePiece structurePiece = StructurePiece.method_14932(list, mutableIntBoundingBox);
            if (structurePiece == null) {
                return null;
            }
            if (structurePiece.getBoundingBox().minY == mutableIntBoundingBox.minY) {
                for (int m = 3; m >= 1; --m) {
                    mutableIntBoundingBox = MutableIntBoundingBox.createRotated(i, j, k, -1, -1, 0, 5, 5, m - 1, direction);
                    if (structurePiece.getBoundingBox().intersects(mutableIntBoundingBox)) continue;
                    return MutableIntBoundingBox.createRotated(i, j, k, -1, -1, 0, 5, 5, m, direction);
                }
            }
            return null;
        }

        @Override
        public boolean generate(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
            for (int i = 0; i < this.length; ++i) {
                this.addBlock(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 0, 0, i, mutableIntBoundingBox);
                this.addBlock(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 1, 0, i, mutableIntBoundingBox);
                this.addBlock(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 2, 0, i, mutableIntBoundingBox);
                this.addBlock(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 3, 0, i, mutableIntBoundingBox);
                this.addBlock(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 4, 0, i, mutableIntBoundingBox);
                for (int j = 1; j <= 3; ++j) {
                    this.addBlock(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 0, j, i, mutableIntBoundingBox);
                    this.addBlock(iWorld, Blocks.CAVE_AIR.getDefaultState(), 1, j, i, mutableIntBoundingBox);
                    this.addBlock(iWorld, Blocks.CAVE_AIR.getDefaultState(), 2, j, i, mutableIntBoundingBox);
                    this.addBlock(iWorld, Blocks.CAVE_AIR.getDefaultState(), 3, j, i, mutableIntBoundingBox);
                    this.addBlock(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 4, j, i, mutableIntBoundingBox);
                }
                this.addBlock(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 0, 4, i, mutableIntBoundingBox);
                this.addBlock(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 1, 4, i, mutableIntBoundingBox);
                this.addBlock(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 2, 4, i, mutableIntBoundingBox);
                this.addBlock(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 3, 4, i, mutableIntBoundingBox);
                this.addBlock(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 4, 4, i, mutableIntBoundingBox);
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
        protected void toNbt(CompoundTag compoundTag) {
            compoundTag.putString("EntryDoor", this.entryDoor.name());
        }

        protected void generateEntrance(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, EntranceType entranceType, int i, int j, int k) {
            switch (entranceType) {
                case OPENING: {
                    this.fillWithOutline(iWorld, mutableIntBoundingBox, i, j, k, i + 3 - 1, j + 3 - 1, k, AIR, AIR, false);
                    break;
                }
                case WOOD_DOOR: {
                    this.addBlock(iWorld, Blocks.STONE_BRICKS.getDefaultState(), i, j, k, mutableIntBoundingBox);
                    this.addBlock(iWorld, Blocks.STONE_BRICKS.getDefaultState(), i, j + 1, k, mutableIntBoundingBox);
                    this.addBlock(iWorld, Blocks.STONE_BRICKS.getDefaultState(), i, j + 2, k, mutableIntBoundingBox);
                    this.addBlock(iWorld, Blocks.STONE_BRICKS.getDefaultState(), i + 1, j + 2, k, mutableIntBoundingBox);
                    this.addBlock(iWorld, Blocks.STONE_BRICKS.getDefaultState(), i + 2, j + 2, k, mutableIntBoundingBox);
                    this.addBlock(iWorld, Blocks.STONE_BRICKS.getDefaultState(), i + 2, j + 1, k, mutableIntBoundingBox);
                    this.addBlock(iWorld, Blocks.STONE_BRICKS.getDefaultState(), i + 2, j, k, mutableIntBoundingBox);
                    this.addBlock(iWorld, Blocks.OAK_DOOR.getDefaultState(), i + 1, j, k, mutableIntBoundingBox);
                    this.addBlock(iWorld, (BlockState)Blocks.OAK_DOOR.getDefaultState().with(DoorBlock.HALF, DoubleBlockHalf.UPPER), i + 1, j + 1, k, mutableIntBoundingBox);
                    break;
                }
                case GRATES: {
                    this.addBlock(iWorld, Blocks.CAVE_AIR.getDefaultState(), i + 1, j, k, mutableIntBoundingBox);
                    this.addBlock(iWorld, Blocks.CAVE_AIR.getDefaultState(), i + 1, j + 1, k, mutableIntBoundingBox);
                    this.addBlock(iWorld, (BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.WEST, true), i, j, k, mutableIntBoundingBox);
                    this.addBlock(iWorld, (BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.WEST, true), i, j + 1, k, mutableIntBoundingBox);
                    this.addBlock(iWorld, (BlockState)((BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.EAST, true)).with(PaneBlock.WEST, true), i, j + 2, k, mutableIntBoundingBox);
                    this.addBlock(iWorld, (BlockState)((BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.EAST, true)).with(PaneBlock.WEST, true), i + 1, j + 2, k, mutableIntBoundingBox);
                    this.addBlock(iWorld, (BlockState)((BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.EAST, true)).with(PaneBlock.WEST, true), i + 2, j + 2, k, mutableIntBoundingBox);
                    this.addBlock(iWorld, (BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.EAST, true), i + 2, j + 1, k, mutableIntBoundingBox);
                    this.addBlock(iWorld, (BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.EAST, true), i + 2, j, k, mutableIntBoundingBox);
                    break;
                }
                case IRON_DOOR: {
                    this.addBlock(iWorld, Blocks.STONE_BRICKS.getDefaultState(), i, j, k, mutableIntBoundingBox);
                    this.addBlock(iWorld, Blocks.STONE_BRICKS.getDefaultState(), i, j + 1, k, mutableIntBoundingBox);
                    this.addBlock(iWorld, Blocks.STONE_BRICKS.getDefaultState(), i, j + 2, k, mutableIntBoundingBox);
                    this.addBlock(iWorld, Blocks.STONE_BRICKS.getDefaultState(), i + 1, j + 2, k, mutableIntBoundingBox);
                    this.addBlock(iWorld, Blocks.STONE_BRICKS.getDefaultState(), i + 2, j + 2, k, mutableIntBoundingBox);
                    this.addBlock(iWorld, Blocks.STONE_BRICKS.getDefaultState(), i + 2, j + 1, k, mutableIntBoundingBox);
                    this.addBlock(iWorld, Blocks.STONE_BRICKS.getDefaultState(), i + 2, j, k, mutableIntBoundingBox);
                    this.addBlock(iWorld, Blocks.IRON_DOOR.getDefaultState(), i + 1, j, k, mutableIntBoundingBox);
                    this.addBlock(iWorld, (BlockState)Blocks.IRON_DOOR.getDefaultState().with(DoorBlock.HALF, DoubleBlockHalf.UPPER), i + 1, j + 1, k, mutableIntBoundingBox);
                    this.addBlock(iWorld, (BlockState)Blocks.STONE_BUTTON.getDefaultState().with(AbstractButtonBlock.FACING, Direction.NORTH), i + 2, j + 1, k + 1, mutableIntBoundingBox);
                    this.addBlock(iWorld, (BlockState)Blocks.STONE_BUTTON.getDefaultState().with(AbstractButtonBlock.FACING, Direction.SOUTH), i + 2, j + 1, k - 1, mutableIntBoundingBox);
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
                        return StrongholdGenerator.method_14854(start, list, random, this.boundingBox.minX + i, this.boundingBox.minY + j, this.boundingBox.minZ - 1, direction, this.method_14923());
                    }
                    case SOUTH: {
                        return StrongholdGenerator.method_14854(start, list, random, this.boundingBox.minX + i, this.boundingBox.minY + j, this.boundingBox.maxZ + 1, direction, this.method_14923());
                    }
                    case WEST: {
                        return StrongholdGenerator.method_14854(start, list, random, this.boundingBox.minX - 1, this.boundingBox.minY + j, this.boundingBox.minZ + i, direction, this.method_14923());
                    }
                    case EAST: {
                        return StrongholdGenerator.method_14854(start, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY + j, this.boundingBox.minZ + i, direction, this.method_14923());
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
                        return StrongholdGenerator.method_14854(start, list, random, this.boundingBox.minX - 1, this.boundingBox.minY + i, this.boundingBox.minZ + j, Direction.WEST, this.method_14923());
                    }
                    case SOUTH: {
                        return StrongholdGenerator.method_14854(start, list, random, this.boundingBox.minX - 1, this.boundingBox.minY + i, this.boundingBox.minZ + j, Direction.WEST, this.method_14923());
                    }
                    case WEST: {
                        return StrongholdGenerator.method_14854(start, list, random, this.boundingBox.minX + j, this.boundingBox.minY + i, this.boundingBox.minZ - 1, Direction.NORTH, this.method_14923());
                    }
                    case EAST: {
                        return StrongholdGenerator.method_14854(start, list, random, this.boundingBox.minX + j, this.boundingBox.minY + i, this.boundingBox.minZ - 1, Direction.NORTH, this.method_14923());
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
                        return StrongholdGenerator.method_14854(start, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY + i, this.boundingBox.minZ + j, Direction.EAST, this.method_14923());
                    }
                    case SOUTH: {
                        return StrongholdGenerator.method_14854(start, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY + i, this.boundingBox.minZ + j, Direction.EAST, this.method_14923());
                    }
                    case WEST: {
                        return StrongholdGenerator.method_14854(start, list, random, this.boundingBox.minX + j, this.boundingBox.minY + i, this.boundingBox.maxZ + 1, Direction.SOUTH, this.method_14923());
                    }
                    case EAST: {
                        return StrongholdGenerator.method_14854(start, list, random, this.boundingBox.minX + j, this.boundingBox.minY + i, this.boundingBox.maxZ + 1, Direction.SOUTH, this.method_14923());
                    }
                }
            }
            return null;
        }

        protected static boolean method_14871(MutableIntBoundingBox mutableIntBoundingBox) {
            return mutableIntBoundingBox != null && mutableIntBoundingBox.minY > 10;
        }

        public static enum EntranceType {
            OPENING,
            WOOD_DOOR,
            GRATES,
            IRON_DOOR;

        }
    }

    static class class_3427 {
        public final Class<? extends Piece> field_15276;
        public final int field_15278;
        public int field_15277;
        public final int field_15275;

        public class_3427(Class<? extends Piece> class_, int i, int j) {
            this.field_15276 = class_;
            this.field_15278 = i;
            this.field_15275 = j;
        }

        public boolean method_14862(int i) {
            return this.field_15275 == 0 || this.field_15277 < this.field_15275;
        }

        public boolean method_14861() {
            return this.field_15275 == 0 || this.field_15277 < this.field_15275;
        }
    }
}

