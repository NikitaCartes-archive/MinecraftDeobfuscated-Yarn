/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.structure;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.RailBlock;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.ChestMinecartEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.MineshaftFeature;
import net.minecraft.world.loot.LootTables;
import org.jetbrains.annotations.Nullable;

public class MineshaftGenerator {
    private static MineshaftPart method_14712(List<StructurePiece> list, Random random, int i, int j, int k, @Nullable Direction direction, int l, MineshaftFeature.Type type) {
        int m = random.nextInt(100);
        if (m >= 80) {
            MutableIntBoundingBox mutableIntBoundingBox = MineshaftCrossing.method_14717(list, random, i, j, k, direction);
            if (mutableIntBoundingBox != null) {
                return new MineshaftCrossing(l, mutableIntBoundingBox, direction, type);
            }
        } else if (m >= 70) {
            MutableIntBoundingBox mutableIntBoundingBox = MineshaftStairs.method_14720(list, random, i, j, k, direction);
            if (mutableIntBoundingBox != null) {
                return new MineshaftStairs(l, mutableIntBoundingBox, direction, type);
            }
        } else {
            MutableIntBoundingBox mutableIntBoundingBox = MineshaftCorridor.method_14714(list, random, i, j, k, direction);
            if (mutableIntBoundingBox != null) {
                return new MineshaftCorridor(l, random, mutableIntBoundingBox, direction, type);
            }
        }
        return null;
    }

    private static MineshaftPart method_14711(StructurePiece structurePiece, List<StructurePiece> list, Random random, int i, int j, int k, Direction direction, int l) {
        if (l > 8) {
            return null;
        }
        if (Math.abs(i - structurePiece.getBoundingBox().minX) > 80 || Math.abs(k - structurePiece.getBoundingBox().minZ) > 80) {
            return null;
        }
        MineshaftFeature.Type type = ((MineshaftPart)structurePiece).mineshaftType;
        MineshaftPart mineshaftPart = MineshaftGenerator.method_14712(list, random, i, j, k, direction, l + 1, type);
        if (mineshaftPart != null) {
            list.add(mineshaftPart);
            mineshaftPart.method_14918(structurePiece, list, random);
        }
        return mineshaftPart;
    }

    public static class MineshaftStairs
    extends MineshaftPart {
        public MineshaftStairs(int i, MutableIntBoundingBox mutableIntBoundingBox, Direction direction, MineshaftFeature.Type type) {
            super(StructurePieceType.MINESHAFT_STAIRS, i, type);
            this.setOrientation(direction);
            this.boundingBox = mutableIntBoundingBox;
        }

        public MineshaftStairs(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.MINESHAFT_STAIRS, compoundTag);
        }

        public static MutableIntBoundingBox method_14720(List<StructurePiece> list, Random random, int i, int j, int k, Direction direction) {
            MutableIntBoundingBox mutableIntBoundingBox = new MutableIntBoundingBox(i, j - 5, k, i, j + 3 - 1, k);
            switch (direction) {
                default: {
                    mutableIntBoundingBox.maxX = i + 3 - 1;
                    mutableIntBoundingBox.minZ = k - 8;
                    break;
                }
                case SOUTH: {
                    mutableIntBoundingBox.maxX = i + 3 - 1;
                    mutableIntBoundingBox.maxZ = k + 8;
                    break;
                }
                case WEST: {
                    mutableIntBoundingBox.minX = i - 8;
                    mutableIntBoundingBox.maxZ = k + 3 - 1;
                    break;
                }
                case EAST: {
                    mutableIntBoundingBox.maxX = i + 8;
                    mutableIntBoundingBox.maxZ = k + 3 - 1;
                }
            }
            if (StructurePiece.method_14932(list, mutableIntBoundingBox) != null) {
                return null;
            }
            return mutableIntBoundingBox;
        }

        @Override
        public void method_14918(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
            int i = this.method_14923();
            Direction direction = this.getFacing();
            if (direction != null) {
                switch (direction) {
                    default: {
                        MineshaftGenerator.method_14711(structurePiece, list, random, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ - 1, Direction.NORTH, i);
                        break;
                    }
                    case SOUTH: {
                        MineshaftGenerator.method_14711(structurePiece, list, random, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.maxZ + 1, Direction.SOUTH, i);
                        break;
                    }
                    case WEST: {
                        MineshaftGenerator.method_14711(structurePiece, list, random, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ, Direction.WEST, i);
                        break;
                    }
                    case EAST: {
                        MineshaftGenerator.method_14711(structurePiece, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ, Direction.EAST, i);
                    }
                }
            }
        }

        @Override
        public boolean generate(IWorld iWorld, ChunkGenerator<?> chunkGenerator, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
            if (this.method_14937(iWorld, mutableIntBoundingBox)) {
                return false;
            }
            this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 5, 0, 2, 7, 1, AIR, AIR, false);
            this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 0, 7, 2, 2, 8, AIR, AIR, false);
            for (int i = 0; i < 5; ++i) {
                this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 5 - i - (i < 4 ? 1 : 0), 2 + i, 2, 7 - i, 2 + i, AIR, AIR, false);
            }
            return true;
        }
    }

    public static class MineshaftCrossing
    extends MineshaftPart {
        private final Direction direction;
        private final boolean twoFloors;

        public MineshaftCrossing(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.MINESHAFT_CROSSING, compoundTag);
            this.twoFloors = compoundTag.getBoolean("tf");
            this.direction = Direction.fromHorizontal(compoundTag.getInt("D"));
        }

        @Override
        protected void toNbt(CompoundTag compoundTag) {
            super.toNbt(compoundTag);
            compoundTag.putBoolean("tf", this.twoFloors);
            compoundTag.putInt("D", this.direction.getHorizontal());
        }

        public MineshaftCrossing(int i, MutableIntBoundingBox mutableIntBoundingBox, @Nullable Direction direction, MineshaftFeature.Type type) {
            super(StructurePieceType.MINESHAFT_CROSSING, i, type);
            this.direction = direction;
            this.boundingBox = mutableIntBoundingBox;
            this.twoFloors = mutableIntBoundingBox.getBlockCountY() > 3;
        }

        public static MutableIntBoundingBox method_14717(List<StructurePiece> list, Random random, int i, int j, int k, Direction direction) {
            MutableIntBoundingBox mutableIntBoundingBox = new MutableIntBoundingBox(i, j, k, i, j + 3 - 1, k);
            if (random.nextInt(4) == 0) {
                mutableIntBoundingBox.maxY += 4;
            }
            switch (direction) {
                default: {
                    mutableIntBoundingBox.minX = i - 1;
                    mutableIntBoundingBox.maxX = i + 3;
                    mutableIntBoundingBox.minZ = k - 4;
                    break;
                }
                case SOUTH: {
                    mutableIntBoundingBox.minX = i - 1;
                    mutableIntBoundingBox.maxX = i + 3;
                    mutableIntBoundingBox.maxZ = k + 3 + 1;
                    break;
                }
                case WEST: {
                    mutableIntBoundingBox.minX = i - 4;
                    mutableIntBoundingBox.minZ = k - 1;
                    mutableIntBoundingBox.maxZ = k + 3;
                    break;
                }
                case EAST: {
                    mutableIntBoundingBox.maxX = i + 3 + 1;
                    mutableIntBoundingBox.minZ = k - 1;
                    mutableIntBoundingBox.maxZ = k + 3;
                }
            }
            if (StructurePiece.method_14932(list, mutableIntBoundingBox) != null) {
                return null;
            }
            return mutableIntBoundingBox;
        }

        @Override
        public void method_14918(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
            int i = this.method_14923();
            switch (this.direction) {
                default: {
                    MineshaftGenerator.method_14711(structurePiece, list, random, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ - 1, Direction.NORTH, i);
                    MineshaftGenerator.method_14711(structurePiece, list, random, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, Direction.WEST, i);
                    MineshaftGenerator.method_14711(structurePiece, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, Direction.EAST, i);
                    break;
                }
                case SOUTH: {
                    MineshaftGenerator.method_14711(structurePiece, list, random, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ + 1, Direction.SOUTH, i);
                    MineshaftGenerator.method_14711(structurePiece, list, random, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, Direction.WEST, i);
                    MineshaftGenerator.method_14711(structurePiece, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, Direction.EAST, i);
                    break;
                }
                case WEST: {
                    MineshaftGenerator.method_14711(structurePiece, list, random, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ - 1, Direction.NORTH, i);
                    MineshaftGenerator.method_14711(structurePiece, list, random, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ + 1, Direction.SOUTH, i);
                    MineshaftGenerator.method_14711(structurePiece, list, random, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, Direction.WEST, i);
                    break;
                }
                case EAST: {
                    MineshaftGenerator.method_14711(structurePiece, list, random, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ - 1, Direction.NORTH, i);
                    MineshaftGenerator.method_14711(structurePiece, list, random, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ + 1, Direction.SOUTH, i);
                    MineshaftGenerator.method_14711(structurePiece, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, Direction.EAST, i);
                }
            }
            if (this.twoFloors) {
                if (random.nextBoolean()) {
                    MineshaftGenerator.method_14711(structurePiece, list, random, this.boundingBox.minX + 1, this.boundingBox.minY + 3 + 1, this.boundingBox.minZ - 1, Direction.NORTH, i);
                }
                if (random.nextBoolean()) {
                    MineshaftGenerator.method_14711(structurePiece, list, random, this.boundingBox.minX - 1, this.boundingBox.minY + 3 + 1, this.boundingBox.minZ + 1, Direction.WEST, i);
                }
                if (random.nextBoolean()) {
                    MineshaftGenerator.method_14711(structurePiece, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY + 3 + 1, this.boundingBox.minZ + 1, Direction.EAST, i);
                }
                if (random.nextBoolean()) {
                    MineshaftGenerator.method_14711(structurePiece, list, random, this.boundingBox.minX + 1, this.boundingBox.minY + 3 + 1, this.boundingBox.maxZ + 1, Direction.SOUTH, i);
                }
            }
        }

        @Override
        public boolean generate(IWorld iWorld, ChunkGenerator<?> chunkGenerator, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
            if (this.method_14937(iWorld, mutableIntBoundingBox)) {
                return false;
            }
            BlockState blockState = this.method_16443();
            if (this.twoFloors) {
                this.fillWithOutline(iWorld, mutableIntBoundingBox, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ, this.boundingBox.maxX - 1, this.boundingBox.minY + 3 - 1, this.boundingBox.maxZ, AIR, AIR, false);
                this.fillWithOutline(iWorld, mutableIntBoundingBox, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ + 1, this.boundingBox.maxX, this.boundingBox.minY + 3 - 1, this.boundingBox.maxZ - 1, AIR, AIR, false);
                this.fillWithOutline(iWorld, mutableIntBoundingBox, this.boundingBox.minX + 1, this.boundingBox.maxY - 2, this.boundingBox.minZ, this.boundingBox.maxX - 1, this.boundingBox.maxY, this.boundingBox.maxZ, AIR, AIR, false);
                this.fillWithOutline(iWorld, mutableIntBoundingBox, this.boundingBox.minX, this.boundingBox.maxY - 2, this.boundingBox.minZ + 1, this.boundingBox.maxX, this.boundingBox.maxY, this.boundingBox.maxZ - 1, AIR, AIR, false);
                this.fillWithOutline(iWorld, mutableIntBoundingBox, this.boundingBox.minX + 1, this.boundingBox.minY + 3, this.boundingBox.minZ + 1, this.boundingBox.maxX - 1, this.boundingBox.minY + 3, this.boundingBox.maxZ - 1, AIR, AIR, false);
            } else {
                this.fillWithOutline(iWorld, mutableIntBoundingBox, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ, this.boundingBox.maxX - 1, this.boundingBox.maxY, this.boundingBox.maxZ, AIR, AIR, false);
                this.fillWithOutline(iWorld, mutableIntBoundingBox, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ + 1, this.boundingBox.maxX, this.boundingBox.maxY, this.boundingBox.maxZ - 1, AIR, AIR, false);
            }
            this.method_14716(iWorld, mutableIntBoundingBox, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, this.boundingBox.maxY);
            this.method_14716(iWorld, mutableIntBoundingBox, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ - 1, this.boundingBox.maxY);
            this.method_14716(iWorld, mutableIntBoundingBox, this.boundingBox.maxX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, this.boundingBox.maxY);
            this.method_14716(iWorld, mutableIntBoundingBox, this.boundingBox.maxX - 1, this.boundingBox.minY, this.boundingBox.maxZ - 1, this.boundingBox.maxY);
            for (int i = this.boundingBox.minX; i <= this.boundingBox.maxX; ++i) {
                for (int j = this.boundingBox.minZ; j <= this.boundingBox.maxZ; ++j) {
                    if (!this.getBlockAt(iWorld, i, this.boundingBox.minY - 1, j, mutableIntBoundingBox).isAir() || !this.isUnderSeaLevel(iWorld, i, this.boundingBox.minY - 1, j, mutableIntBoundingBox)) continue;
                    this.addBlock(iWorld, blockState, i, this.boundingBox.minY - 1, j, mutableIntBoundingBox);
                }
            }
            return true;
        }

        private void method_14716(IWorld iWorld, MutableIntBoundingBox mutableIntBoundingBox, int i, int j, int k, int l) {
            if (!this.getBlockAt(iWorld, i, l + 1, k, mutableIntBoundingBox).isAir()) {
                this.fillWithOutline(iWorld, mutableIntBoundingBox, i, j, k, i, l, k, this.method_16443(), AIR, false);
            }
        }
    }

    public static class MineshaftCorridor
    extends MineshaftPart {
        private final boolean hasRails;
        private final boolean hasCobwebs;
        private boolean hasSpawner;
        private final int length;

        public MineshaftCorridor(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.MINESHAFT_CORRIDOR, compoundTag);
            this.hasRails = compoundTag.getBoolean("hr");
            this.hasCobwebs = compoundTag.getBoolean("sc");
            this.hasSpawner = compoundTag.getBoolean("hps");
            this.length = compoundTag.getInt("Num");
        }

        @Override
        protected void toNbt(CompoundTag compoundTag) {
            super.toNbt(compoundTag);
            compoundTag.putBoolean("hr", this.hasRails);
            compoundTag.putBoolean("sc", this.hasCobwebs);
            compoundTag.putBoolean("hps", this.hasSpawner);
            compoundTag.putInt("Num", this.length);
        }

        public MineshaftCorridor(int i, Random random, MutableIntBoundingBox mutableIntBoundingBox, Direction direction, MineshaftFeature.Type type) {
            super(StructurePieceType.MINESHAFT_CORRIDOR, i, type);
            this.setOrientation(direction);
            this.boundingBox = mutableIntBoundingBox;
            this.hasRails = random.nextInt(3) == 0;
            this.hasCobwebs = !this.hasRails && random.nextInt(23) == 0;
            this.length = this.getFacing().getAxis() == Direction.Axis.Z ? mutableIntBoundingBox.getBlockCountZ() / 5 : mutableIntBoundingBox.getBlockCountX() / 5;
        }

        public static MutableIntBoundingBox method_14714(List<StructurePiece> list, Random random, int i, int j, int k, Direction direction) {
            int l;
            MutableIntBoundingBox mutableIntBoundingBox = new MutableIntBoundingBox(i, j, k, i, j + 3 - 1, k);
            for (l = random.nextInt(3) + 2; l > 0; --l) {
                int m = l * 5;
                switch (direction) {
                    default: {
                        mutableIntBoundingBox.maxX = i + 3 - 1;
                        mutableIntBoundingBox.minZ = k - (m - 1);
                        break;
                    }
                    case SOUTH: {
                        mutableIntBoundingBox.maxX = i + 3 - 1;
                        mutableIntBoundingBox.maxZ = k + m - 1;
                        break;
                    }
                    case WEST: {
                        mutableIntBoundingBox.minX = i - (m - 1);
                        mutableIntBoundingBox.maxZ = k + 3 - 1;
                        break;
                    }
                    case EAST: {
                        mutableIntBoundingBox.maxX = i + m - 1;
                        mutableIntBoundingBox.maxZ = k + 3 - 1;
                    }
                }
                if (StructurePiece.method_14932(list, mutableIntBoundingBox) == null) break;
            }
            if (l > 0) {
                return mutableIntBoundingBox;
            }
            return null;
        }

        @Override
        public void method_14918(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
            block24: {
                int i = this.method_14923();
                int j = random.nextInt(4);
                Direction direction = this.getFacing();
                if (direction != null) {
                    switch (direction) {
                        default: {
                            if (j <= 1) {
                                MineshaftGenerator.method_14711(structurePiece, list, random, this.boundingBox.minX, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.minZ - 1, direction, i);
                                break;
                            }
                            if (j == 2) {
                                MineshaftGenerator.method_14711(structurePiece, list, random, this.boundingBox.minX - 1, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.minZ, Direction.WEST, i);
                                break;
                            }
                            MineshaftGenerator.method_14711(structurePiece, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.minZ, Direction.EAST, i);
                            break;
                        }
                        case SOUTH: {
                            if (j <= 1) {
                                MineshaftGenerator.method_14711(structurePiece, list, random, this.boundingBox.minX, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.maxZ + 1, direction, i);
                                break;
                            }
                            if (j == 2) {
                                MineshaftGenerator.method_14711(structurePiece, list, random, this.boundingBox.minX - 1, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.maxZ - 3, Direction.WEST, i);
                                break;
                            }
                            MineshaftGenerator.method_14711(structurePiece, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.maxZ - 3, Direction.EAST, i);
                            break;
                        }
                        case WEST: {
                            if (j <= 1) {
                                MineshaftGenerator.method_14711(structurePiece, list, random, this.boundingBox.minX - 1, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.minZ, direction, i);
                                break;
                            }
                            if (j == 2) {
                                MineshaftGenerator.method_14711(structurePiece, list, random, this.boundingBox.minX, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.minZ - 1, Direction.NORTH, i);
                                break;
                            }
                            MineshaftGenerator.method_14711(structurePiece, list, random, this.boundingBox.minX, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.maxZ + 1, Direction.SOUTH, i);
                            break;
                        }
                        case EAST: {
                            if (j <= 1) {
                                MineshaftGenerator.method_14711(structurePiece, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.minZ, direction, i);
                                break;
                            }
                            if (j == 2) {
                                MineshaftGenerator.method_14711(structurePiece, list, random, this.boundingBox.maxX - 3, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.minZ - 1, Direction.NORTH, i);
                                break;
                            }
                            MineshaftGenerator.method_14711(structurePiece, list, random, this.boundingBox.maxX - 3, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.maxZ + 1, Direction.SOUTH, i);
                        }
                    }
                }
                if (i >= 8) break block24;
                if (direction == Direction.NORTH || direction == Direction.SOUTH) {
                    int k = this.boundingBox.minZ + 3;
                    while (k + 3 <= this.boundingBox.maxZ) {
                        int l = random.nextInt(5);
                        if (l == 0) {
                            MineshaftGenerator.method_14711(structurePiece, list, random, this.boundingBox.minX - 1, this.boundingBox.minY, k, Direction.WEST, i + 1);
                        } else if (l == 1) {
                            MineshaftGenerator.method_14711(structurePiece, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY, k, Direction.EAST, i + 1);
                        }
                        k += 5;
                    }
                } else {
                    int k = this.boundingBox.minX + 3;
                    while (k + 3 <= this.boundingBox.maxX) {
                        int l = random.nextInt(5);
                        if (l == 0) {
                            MineshaftGenerator.method_14711(structurePiece, list, random, k, this.boundingBox.minY, this.boundingBox.minZ - 1, Direction.NORTH, i + 1);
                        } else if (l == 1) {
                            MineshaftGenerator.method_14711(structurePiece, list, random, k, this.boundingBox.minY, this.boundingBox.maxZ + 1, Direction.SOUTH, i + 1);
                        }
                        k += 5;
                    }
                }
            }
        }

        @Override
        protected boolean addChest(IWorld iWorld, MutableIntBoundingBox mutableIntBoundingBox, Random random, int i, int j, int k, Identifier identifier) {
            BlockPos blockPos = new BlockPos(this.applyXTransform(i, k), this.applyYTransform(j), this.applyZTransform(i, k));
            if (mutableIntBoundingBox.contains(blockPos) && iWorld.getBlockState(blockPos).isAir() && !iWorld.getBlockState(blockPos.down()).isAir()) {
                BlockState blockState = (BlockState)Blocks.RAIL.getDefaultState().with(RailBlock.SHAPE, random.nextBoolean() ? RailShape.NORTH_SOUTH : RailShape.EAST_WEST);
                this.addBlock(iWorld, blockState, i, j, k, mutableIntBoundingBox);
                ChestMinecartEntity chestMinecartEntity = new ChestMinecartEntity(iWorld.getWorld(), (float)blockPos.getX() + 0.5f, (float)blockPos.getY() + 0.5f, (float)blockPos.getZ() + 0.5f);
                chestMinecartEntity.setLootTable(identifier, random.nextLong());
                iWorld.spawnEntity(chestMinecartEntity);
                return true;
            }
            return false;
        }

        @Override
        public boolean generate(IWorld iWorld, ChunkGenerator<?> chunkGenerator, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
            int r;
            int p;
            int o;
            int n;
            if (this.method_14937(iWorld, mutableIntBoundingBox)) {
                return false;
            }
            boolean i = false;
            int j = 2;
            boolean k = false;
            int l = 2;
            int m = this.length * 5 - 1;
            BlockState blockState = this.method_16443();
            this.fillWithOutline(iWorld, mutableIntBoundingBox, 0, 0, 0, 2, 1, m, AIR, AIR, false);
            this.fillWithOutlineUnderSealevel(iWorld, mutableIntBoundingBox, random, 0.8f, 0, 2, 0, 2, 2, m, AIR, AIR, false, false);
            if (this.hasCobwebs) {
                this.fillWithOutlineUnderSealevel(iWorld, mutableIntBoundingBox, random, 0.6f, 0, 0, 0, 2, 1, m, Blocks.COBWEB.getDefaultState(), AIR, false, true);
            }
            for (n = 0; n < this.length; ++n) {
                int s;
                o = 2 + n * 5;
                this.method_14713(iWorld, mutableIntBoundingBox, 0, 0, o, 2, 2, random);
                this.method_14715(iWorld, mutableIntBoundingBox, random, 0.1f, 0, 2, o - 1);
                this.method_14715(iWorld, mutableIntBoundingBox, random, 0.1f, 2, 2, o - 1);
                this.method_14715(iWorld, mutableIntBoundingBox, random, 0.1f, 0, 2, o + 1);
                this.method_14715(iWorld, mutableIntBoundingBox, random, 0.1f, 2, 2, o + 1);
                this.method_14715(iWorld, mutableIntBoundingBox, random, 0.05f, 0, 2, o - 2);
                this.method_14715(iWorld, mutableIntBoundingBox, random, 0.05f, 2, 2, o - 2);
                this.method_14715(iWorld, mutableIntBoundingBox, random, 0.05f, 0, 2, o + 2);
                this.method_14715(iWorld, mutableIntBoundingBox, random, 0.05f, 2, 2, o + 2);
                if (random.nextInt(100) == 0) {
                    this.addChest(iWorld, mutableIntBoundingBox, random, 2, 0, o - 1, LootTables.ABANDONED_MINESHAFT_CHEST);
                }
                if (random.nextInt(100) == 0) {
                    this.addChest(iWorld, mutableIntBoundingBox, random, 0, 0, o + 1, LootTables.ABANDONED_MINESHAFT_CHEST);
                }
                if (!this.hasCobwebs || this.hasSpawner) continue;
                p = this.applyYTransform(0);
                int q = o - 1 + random.nextInt(3);
                r = this.applyXTransform(1, q);
                BlockPos blockPos = new BlockPos(r, p, s = this.applyZTransform(1, q));
                if (!mutableIntBoundingBox.contains(blockPos) || !this.isUnderSeaLevel(iWorld, 1, 0, q, mutableIntBoundingBox)) continue;
                this.hasSpawner = true;
                iWorld.setBlockState(blockPos, Blocks.SPAWNER.getDefaultState(), 2);
                BlockEntity blockEntity = iWorld.getBlockEntity(blockPos);
                if (!(blockEntity instanceof MobSpawnerBlockEntity)) continue;
                ((MobSpawnerBlockEntity)blockEntity).getLogic().setEntityId(EntityType.CAVE_SPIDER);
            }
            for (n = 0; n <= 2; ++n) {
                for (o = 0; o <= m; ++o) {
                    p = -1;
                    BlockState blockState2 = this.getBlockAt(iWorld, n, -1, o, mutableIntBoundingBox);
                    if (!blockState2.isAir() || !this.isUnderSeaLevel(iWorld, n, -1, o, mutableIntBoundingBox)) continue;
                    r = -1;
                    this.addBlock(iWorld, blockState, n, -1, o, mutableIntBoundingBox);
                }
            }
            if (this.hasRails) {
                BlockState blockState3 = (BlockState)Blocks.RAIL.getDefaultState().with(RailBlock.SHAPE, RailShape.NORTH_SOUTH);
                for (o = 0; o <= m; ++o) {
                    BlockState blockState4 = this.getBlockAt(iWorld, 1, -1, o, mutableIntBoundingBox);
                    if (blockState4.isAir() || !blockState4.isFullOpaque(iWorld, new BlockPos(this.applyXTransform(1, o), this.applyYTransform(-1), this.applyZTransform(1, o)))) continue;
                    float f = this.isUnderSeaLevel(iWorld, 1, 0, o, mutableIntBoundingBox) ? 0.7f : 0.9f;
                    this.addBlockWithRandomThreshold(iWorld, mutableIntBoundingBox, random, f, 1, 0, o, blockState3);
                }
            }
            return true;
        }

        private void method_14713(IWorld iWorld, MutableIntBoundingBox mutableIntBoundingBox, int i, int j, int k, int l, int m, Random random) {
            if (!this.method_14719(iWorld, mutableIntBoundingBox, i, m, l, k)) {
                return;
            }
            BlockState blockState = this.method_16443();
            BlockState blockState2 = this.method_14718();
            this.fillWithOutline(iWorld, mutableIntBoundingBox, i, j, k, i, l - 1, k, (BlockState)blockState2.with(FenceBlock.WEST, true), AIR, false);
            this.fillWithOutline(iWorld, mutableIntBoundingBox, m, j, k, m, l - 1, k, (BlockState)blockState2.with(FenceBlock.EAST, true), AIR, false);
            if (random.nextInt(4) == 0) {
                this.fillWithOutline(iWorld, mutableIntBoundingBox, i, l, k, i, l, k, blockState, AIR, false);
                this.fillWithOutline(iWorld, mutableIntBoundingBox, m, l, k, m, l, k, blockState, AIR, false);
            } else {
                this.fillWithOutline(iWorld, mutableIntBoundingBox, i, l, k, m, l, k, blockState, AIR, false);
                this.addBlockWithRandomThreshold(iWorld, mutableIntBoundingBox, random, 0.05f, i + 1, l, k - 1, (BlockState)Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, Direction.NORTH));
                this.addBlockWithRandomThreshold(iWorld, mutableIntBoundingBox, random, 0.05f, i + 1, l, k + 1, (BlockState)Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, Direction.SOUTH));
            }
        }

        private void method_14715(IWorld iWorld, MutableIntBoundingBox mutableIntBoundingBox, Random random, float f, int i, int j, int k) {
            if (this.isUnderSeaLevel(iWorld, i, j, k, mutableIntBoundingBox)) {
                this.addBlockWithRandomThreshold(iWorld, mutableIntBoundingBox, random, f, i, j, k, Blocks.COBWEB.getDefaultState());
            }
        }
    }

    public static class MineshaftRoom
    extends MineshaftPart {
        private final List<MutableIntBoundingBox> entrances = Lists.newLinkedList();

        public MineshaftRoom(int i, Random random, int j, int k, MineshaftFeature.Type type) {
            super(StructurePieceType.MINESHAFT_ROOM, i, type);
            this.mineshaftType = type;
            this.boundingBox = new MutableIntBoundingBox(j, 50, k, j + 7 + random.nextInt(6), 54 + random.nextInt(6), k + 7 + random.nextInt(6));
        }

        public MineshaftRoom(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.MINESHAFT_ROOM, compoundTag);
            ListTag listTag = compoundTag.getList("Entrances", 11);
            for (int i = 0; i < listTag.size(); ++i) {
                this.entrances.add(new MutableIntBoundingBox(listTag.getIntArray(i)));
            }
        }

        @Override
        public void method_14918(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
            MutableIntBoundingBox mutableIntBoundingBox;
            MineshaftPart mineshaftPart;
            int k;
            int i = this.method_14923();
            int j = this.boundingBox.getBlockCountY() - 3 - 1;
            if (j <= 0) {
                j = 1;
            }
            for (k = 0; k < this.boundingBox.getBlockCountX() && (k += random.nextInt(this.boundingBox.getBlockCountX())) + 3 <= this.boundingBox.getBlockCountX(); k += 4) {
                mineshaftPart = MineshaftGenerator.method_14711(structurePiece, list, random, this.boundingBox.minX + k, this.boundingBox.minY + random.nextInt(j) + 1, this.boundingBox.minZ - 1, Direction.NORTH, i);
                if (mineshaftPart == null) continue;
                mutableIntBoundingBox = mineshaftPart.getBoundingBox();
                this.entrances.add(new MutableIntBoundingBox(mutableIntBoundingBox.minX, mutableIntBoundingBox.minY, this.boundingBox.minZ, mutableIntBoundingBox.maxX, mutableIntBoundingBox.maxY, this.boundingBox.minZ + 1));
            }
            for (k = 0; k < this.boundingBox.getBlockCountX() && (k += random.nextInt(this.boundingBox.getBlockCountX())) + 3 <= this.boundingBox.getBlockCountX(); k += 4) {
                mineshaftPart = MineshaftGenerator.method_14711(structurePiece, list, random, this.boundingBox.minX + k, this.boundingBox.minY + random.nextInt(j) + 1, this.boundingBox.maxZ + 1, Direction.SOUTH, i);
                if (mineshaftPart == null) continue;
                mutableIntBoundingBox = mineshaftPart.getBoundingBox();
                this.entrances.add(new MutableIntBoundingBox(mutableIntBoundingBox.minX, mutableIntBoundingBox.minY, this.boundingBox.maxZ - 1, mutableIntBoundingBox.maxX, mutableIntBoundingBox.maxY, this.boundingBox.maxZ));
            }
            for (k = 0; k < this.boundingBox.getBlockCountZ() && (k += random.nextInt(this.boundingBox.getBlockCountZ())) + 3 <= this.boundingBox.getBlockCountZ(); k += 4) {
                mineshaftPart = MineshaftGenerator.method_14711(structurePiece, list, random, this.boundingBox.minX - 1, this.boundingBox.minY + random.nextInt(j) + 1, this.boundingBox.minZ + k, Direction.WEST, i);
                if (mineshaftPart == null) continue;
                mutableIntBoundingBox = mineshaftPart.getBoundingBox();
                this.entrances.add(new MutableIntBoundingBox(this.boundingBox.minX, mutableIntBoundingBox.minY, mutableIntBoundingBox.minZ, this.boundingBox.minX + 1, mutableIntBoundingBox.maxY, mutableIntBoundingBox.maxZ));
            }
            for (k = 0; k < this.boundingBox.getBlockCountZ() && (k += random.nextInt(this.boundingBox.getBlockCountZ())) + 3 <= this.boundingBox.getBlockCountZ(); k += 4) {
                MineshaftPart structurePiece2 = MineshaftGenerator.method_14711(structurePiece, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY + random.nextInt(j) + 1, this.boundingBox.minZ + k, Direction.EAST, i);
                if (structurePiece2 == null) continue;
                mutableIntBoundingBox = structurePiece2.getBoundingBox();
                this.entrances.add(new MutableIntBoundingBox(this.boundingBox.maxX - 1, mutableIntBoundingBox.minY, mutableIntBoundingBox.minZ, this.boundingBox.maxX, mutableIntBoundingBox.maxY, mutableIntBoundingBox.maxZ));
            }
        }

        @Override
        public boolean generate(IWorld iWorld, ChunkGenerator<?> chunkGenerator, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
            if (this.method_14937(iWorld, mutableIntBoundingBox)) {
                return false;
            }
            this.fillWithOutline(iWorld, mutableIntBoundingBox, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ, this.boundingBox.maxX, this.boundingBox.minY, this.boundingBox.maxZ, Blocks.DIRT.getDefaultState(), AIR, true);
            this.fillWithOutline(iWorld, mutableIntBoundingBox, this.boundingBox.minX, this.boundingBox.minY + 1, this.boundingBox.minZ, this.boundingBox.maxX, Math.min(this.boundingBox.minY + 3, this.boundingBox.maxY), this.boundingBox.maxZ, AIR, AIR, false);
            for (MutableIntBoundingBox mutableIntBoundingBox2 : this.entrances) {
                this.fillWithOutline(iWorld, mutableIntBoundingBox, mutableIntBoundingBox2.minX, mutableIntBoundingBox2.maxY - 2, mutableIntBoundingBox2.minZ, mutableIntBoundingBox2.maxX, mutableIntBoundingBox2.maxY, mutableIntBoundingBox2.maxZ, AIR, AIR, false);
            }
            this.method_14919(iWorld, mutableIntBoundingBox, this.boundingBox.minX, this.boundingBox.minY + 4, this.boundingBox.minZ, this.boundingBox.maxX, this.boundingBox.maxY, this.boundingBox.maxZ, AIR, false);
            return true;
        }

        @Override
        public void translate(int i, int j, int k) {
            super.translate(i, j, k);
            for (MutableIntBoundingBox mutableIntBoundingBox : this.entrances) {
                mutableIntBoundingBox.translate(i, j, k);
            }
        }

        @Override
        protected void toNbt(CompoundTag compoundTag) {
            super.toNbt(compoundTag);
            ListTag listTag = new ListTag();
            for (MutableIntBoundingBox mutableIntBoundingBox : this.entrances) {
                listTag.add(mutableIntBoundingBox.toNbt());
            }
            compoundTag.put("Entrances", listTag);
        }
    }

    static abstract class MineshaftPart
    extends StructurePiece {
        protected MineshaftFeature.Type mineshaftType;

        public MineshaftPart(StructurePieceType structurePieceType, int i, MineshaftFeature.Type type) {
            super(structurePieceType, i);
            this.mineshaftType = type;
        }

        public MineshaftPart(StructurePieceType structurePieceType, CompoundTag compoundTag) {
            super(structurePieceType, compoundTag);
            this.mineshaftType = MineshaftFeature.Type.byIndex(compoundTag.getInt("MST"));
        }

        @Override
        protected void toNbt(CompoundTag compoundTag) {
            compoundTag.putInt("MST", this.mineshaftType.ordinal());
        }

        protected BlockState method_16443() {
            switch (this.mineshaftType) {
                default: {
                    return Blocks.OAK_PLANKS.getDefaultState();
                }
                case MESA: 
            }
            return Blocks.DARK_OAK_PLANKS.getDefaultState();
        }

        protected BlockState method_14718() {
            switch (this.mineshaftType) {
                default: {
                    return Blocks.OAK_FENCE.getDefaultState();
                }
                case MESA: 
            }
            return Blocks.DARK_OAK_FENCE.getDefaultState();
        }

        protected boolean method_14719(BlockView blockView, MutableIntBoundingBox mutableIntBoundingBox, int i, int j, int k, int l) {
            for (int m = i; m <= j; ++m) {
                if (!this.getBlockAt(blockView, m, k + 1, l, mutableIntBoundingBox).isAir()) continue;
                return false;
            }
            return true;
        }
    }
}

