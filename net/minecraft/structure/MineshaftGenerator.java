/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.structure;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.RailBlock;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.ChestMinecartEntity;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.structure.StructureContext;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.StructurePiecesHolder;
import net.minecraft.tag.BiomeTags;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.world.BlockView;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.structure.MineshaftStructure;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class MineshaftGenerator {
    static final Logger LOGGER = LogUtils.getLogger();
    private static final int field_31551 = 3;
    private static final int field_31552 = 3;
    private static final int field_31553 = 5;
    private static final int field_31554 = 20;
    private static final int field_31555 = 50;
    private static final int field_31556 = 8;
    public static final int field_34729 = 50;

    private static MineshaftPart pickPiece(StructurePiecesHolder holder, AbstractRandom random, int x, int y, int z, @Nullable Direction orientation, int chainLength, MineshaftStructure.Type type) {
        int i = random.nextInt(100);
        if (i >= 80) {
            BlockBox blockBox = MineshaftCrossing.getBoundingBox(holder, random, x, y, z, orientation);
            if (blockBox != null) {
                return new MineshaftCrossing(chainLength, blockBox, orientation, type);
            }
        } else if (i >= 70) {
            BlockBox blockBox = MineshaftStairs.getBoundingBox(holder, random, x, y, z, orientation);
            if (blockBox != null) {
                return new MineshaftStairs(chainLength, blockBox, orientation, type);
            }
        } else {
            BlockBox blockBox = MineshaftCorridor.getBoundingBox(holder, random, x, y, z, orientation);
            if (blockBox != null) {
                return new MineshaftCorridor(chainLength, random, blockBox, orientation, type);
            }
        }
        return null;
    }

    static MineshaftPart pieceGenerator(StructurePiece start, StructurePiecesHolder holder, AbstractRandom random, int x, int y, int z, Direction orientation, int chainLength) {
        if (chainLength > 8) {
            return null;
        }
        if (Math.abs(x - start.getBoundingBox().getMinX()) > 80 || Math.abs(z - start.getBoundingBox().getMinZ()) > 80) {
            return null;
        }
        MineshaftStructure.Type type = ((MineshaftPart)start).mineshaftType;
        MineshaftPart mineshaftPart = MineshaftGenerator.pickPiece(holder, random, x, y, z, orientation, chainLength + 1, type);
        if (mineshaftPart != null) {
            holder.addPiece(mineshaftPart);
            mineshaftPart.fillOpenings(start, holder, random);
        }
        return mineshaftPart;
    }

    public static class MineshaftCrossing
    extends MineshaftPart {
        private final Direction direction;
        private final boolean twoFloors;

        public MineshaftCrossing(NbtCompound nbt) {
            super(StructurePieceType.MINESHAFT_CROSSING, nbt);
            this.twoFloors = nbt.getBoolean("tf");
            this.direction = Direction.fromHorizontal(nbt.getInt("D"));
        }

        @Override
        protected void writeNbt(StructureContext context, NbtCompound nbt) {
            super.writeNbt(context, nbt);
            nbt.putBoolean("tf", this.twoFloors);
            nbt.putInt("D", this.direction.getHorizontal());
        }

        public MineshaftCrossing(int chainLength, BlockBox boundingBox, @Nullable Direction orientation, MineshaftStructure.Type type) {
            super(StructurePieceType.MINESHAFT_CROSSING, chainLength, type, boundingBox);
            this.direction = orientation;
            this.twoFloors = boundingBox.getBlockCountY() > 3;
        }

        @Nullable
        public static BlockBox getBoundingBox(StructurePiecesHolder holder, AbstractRandom random, int x, int y, int z, Direction orientation) {
            int i = random.nextInt(4) == 0 ? 6 : 2;
            BlockBox blockBox = switch (orientation) {
                default -> new BlockBox(-1, 0, -4, 3, i, 0);
                case Direction.SOUTH -> new BlockBox(-1, 0, 0, 3, i, 4);
                case Direction.WEST -> new BlockBox(-4, 0, -1, 0, i, 3);
                case Direction.EAST -> new BlockBox(0, 0, -1, 4, i, 3);
            };
            blockBox.move(x, y, z);
            if (holder.getIntersecting(blockBox) != null) {
                return null;
            }
            return blockBox;
        }

        @Override
        public void fillOpenings(StructurePiece start, StructurePiecesHolder holder, AbstractRandom random) {
            int i = this.getChainLength();
            switch (this.direction) {
                default: {
                    MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMinX() + 1, this.boundingBox.getMinY(), this.boundingBox.getMinZ() - 1, Direction.NORTH, i);
                    MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMinX() - 1, this.boundingBox.getMinY(), this.boundingBox.getMinZ() + 1, Direction.WEST, i);
                    MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMaxX() + 1, this.boundingBox.getMinY(), this.boundingBox.getMinZ() + 1, Direction.EAST, i);
                    break;
                }
                case SOUTH: {
                    MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMinX() + 1, this.boundingBox.getMinY(), this.boundingBox.getMaxZ() + 1, Direction.SOUTH, i);
                    MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMinX() - 1, this.boundingBox.getMinY(), this.boundingBox.getMinZ() + 1, Direction.WEST, i);
                    MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMaxX() + 1, this.boundingBox.getMinY(), this.boundingBox.getMinZ() + 1, Direction.EAST, i);
                    break;
                }
                case WEST: {
                    MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMinX() + 1, this.boundingBox.getMinY(), this.boundingBox.getMinZ() - 1, Direction.NORTH, i);
                    MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMinX() + 1, this.boundingBox.getMinY(), this.boundingBox.getMaxZ() + 1, Direction.SOUTH, i);
                    MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMinX() - 1, this.boundingBox.getMinY(), this.boundingBox.getMinZ() + 1, Direction.WEST, i);
                    break;
                }
                case EAST: {
                    MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMinX() + 1, this.boundingBox.getMinY(), this.boundingBox.getMinZ() - 1, Direction.NORTH, i);
                    MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMinX() + 1, this.boundingBox.getMinY(), this.boundingBox.getMaxZ() + 1, Direction.SOUTH, i);
                    MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMaxX() + 1, this.boundingBox.getMinY(), this.boundingBox.getMinZ() + 1, Direction.EAST, i);
                }
            }
            if (this.twoFloors) {
                if (random.nextBoolean()) {
                    MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMinX() + 1, this.boundingBox.getMinY() + 3 + 1, this.boundingBox.getMinZ() - 1, Direction.NORTH, i);
                }
                if (random.nextBoolean()) {
                    MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMinX() - 1, this.boundingBox.getMinY() + 3 + 1, this.boundingBox.getMinZ() + 1, Direction.WEST, i);
                }
                if (random.nextBoolean()) {
                    MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMaxX() + 1, this.boundingBox.getMinY() + 3 + 1, this.boundingBox.getMinZ() + 1, Direction.EAST, i);
                }
                if (random.nextBoolean()) {
                    MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMinX() + 1, this.boundingBox.getMinY() + 3 + 1, this.boundingBox.getMaxZ() + 1, Direction.SOUTH, i);
                }
            }
        }

        @Override
        public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, AbstractRandom random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pos) {
            if (this.cannotGenerate(world, chunkBox)) {
                return;
            }
            BlockState blockState = this.mineshaftType.getPlanks();
            if (this.twoFloors) {
                this.fillWithOutline(world, chunkBox, this.boundingBox.getMinX() + 1, this.boundingBox.getMinY(), this.boundingBox.getMinZ(), this.boundingBox.getMaxX() - 1, this.boundingBox.getMinY() + 3 - 1, this.boundingBox.getMaxZ(), AIR, AIR, false);
                this.fillWithOutline(world, chunkBox, this.boundingBox.getMinX(), this.boundingBox.getMinY(), this.boundingBox.getMinZ() + 1, this.boundingBox.getMaxX(), this.boundingBox.getMinY() + 3 - 1, this.boundingBox.getMaxZ() - 1, AIR, AIR, false);
                this.fillWithOutline(world, chunkBox, this.boundingBox.getMinX() + 1, this.boundingBox.getMaxY() - 2, this.boundingBox.getMinZ(), this.boundingBox.getMaxX() - 1, this.boundingBox.getMaxY(), this.boundingBox.getMaxZ(), AIR, AIR, false);
                this.fillWithOutline(world, chunkBox, this.boundingBox.getMinX(), this.boundingBox.getMaxY() - 2, this.boundingBox.getMinZ() + 1, this.boundingBox.getMaxX(), this.boundingBox.getMaxY(), this.boundingBox.getMaxZ() - 1, AIR, AIR, false);
                this.fillWithOutline(world, chunkBox, this.boundingBox.getMinX() + 1, this.boundingBox.getMinY() + 3, this.boundingBox.getMinZ() + 1, this.boundingBox.getMaxX() - 1, this.boundingBox.getMinY() + 3, this.boundingBox.getMaxZ() - 1, AIR, AIR, false);
            } else {
                this.fillWithOutline(world, chunkBox, this.boundingBox.getMinX() + 1, this.boundingBox.getMinY(), this.boundingBox.getMinZ(), this.boundingBox.getMaxX() - 1, this.boundingBox.getMaxY(), this.boundingBox.getMaxZ(), AIR, AIR, false);
                this.fillWithOutline(world, chunkBox, this.boundingBox.getMinX(), this.boundingBox.getMinY(), this.boundingBox.getMinZ() + 1, this.boundingBox.getMaxX(), this.boundingBox.getMaxY(), this.boundingBox.getMaxZ() - 1, AIR, AIR, false);
            }
            this.generateCrossingPillar(world, chunkBox, this.boundingBox.getMinX() + 1, this.boundingBox.getMinY(), this.boundingBox.getMinZ() + 1, this.boundingBox.getMaxY());
            this.generateCrossingPillar(world, chunkBox, this.boundingBox.getMinX() + 1, this.boundingBox.getMinY(), this.boundingBox.getMaxZ() - 1, this.boundingBox.getMaxY());
            this.generateCrossingPillar(world, chunkBox, this.boundingBox.getMaxX() - 1, this.boundingBox.getMinY(), this.boundingBox.getMinZ() + 1, this.boundingBox.getMaxY());
            this.generateCrossingPillar(world, chunkBox, this.boundingBox.getMaxX() - 1, this.boundingBox.getMinY(), this.boundingBox.getMaxZ() - 1, this.boundingBox.getMaxY());
            int i = this.boundingBox.getMinY() - 1;
            for (int j = this.boundingBox.getMinX(); j <= this.boundingBox.getMaxX(); ++j) {
                for (int k = this.boundingBox.getMinZ(); k <= this.boundingBox.getMaxZ(); ++k) {
                    this.tryPlaceFloor(world, chunkBox, blockState, j, i, k);
                }
            }
        }

        private void generateCrossingPillar(StructureWorldAccess world, BlockBox boundingBox, int x, int minY, int z, int maxY) {
            if (!this.getBlockAt(world, x, maxY + 1, z, boundingBox).isAir()) {
                this.fillWithOutline(world, boundingBox, x, minY, z, x, maxY, z, this.mineshaftType.getPlanks(), AIR, false);
            }
        }
    }

    public static class MineshaftStairs
    extends MineshaftPart {
        public MineshaftStairs(int chainLength, BlockBox boundingBox, Direction orientation, MineshaftStructure.Type type) {
            super(StructurePieceType.MINESHAFT_STAIRS, chainLength, type, boundingBox);
            this.setOrientation(orientation);
        }

        public MineshaftStairs(NbtCompound nbt) {
            super(StructurePieceType.MINESHAFT_STAIRS, nbt);
        }

        @Nullable
        public static BlockBox getBoundingBox(StructurePiecesHolder holder, AbstractRandom random, int x, int y, int z, Direction orientation) {
            BlockBox blockBox = switch (orientation) {
                default -> new BlockBox(0, -5, -8, 2, 2, 0);
                case Direction.SOUTH -> new BlockBox(0, -5, 0, 2, 2, 8);
                case Direction.WEST -> new BlockBox(-8, -5, 0, 0, 2, 2);
                case Direction.EAST -> new BlockBox(0, -5, 0, 8, 2, 2);
            };
            blockBox.move(x, y, z);
            if (holder.getIntersecting(blockBox) != null) {
                return null;
            }
            return blockBox;
        }

        @Override
        public void fillOpenings(StructurePiece start, StructurePiecesHolder holder, AbstractRandom random) {
            int i = this.getChainLength();
            Direction direction = this.getFacing();
            if (direction != null) {
                switch (direction) {
                    default: {
                        MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMinX(), this.boundingBox.getMinY(), this.boundingBox.getMinZ() - 1, Direction.NORTH, i);
                        break;
                    }
                    case SOUTH: {
                        MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMinX(), this.boundingBox.getMinY(), this.boundingBox.getMaxZ() + 1, Direction.SOUTH, i);
                        break;
                    }
                    case WEST: {
                        MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMinX() - 1, this.boundingBox.getMinY(), this.boundingBox.getMinZ(), Direction.WEST, i);
                        break;
                    }
                    case EAST: {
                        MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMaxX() + 1, this.boundingBox.getMinY(), this.boundingBox.getMinZ(), Direction.EAST, i);
                    }
                }
            }
        }

        @Override
        public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, AbstractRandom random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pos) {
            if (this.cannotGenerate(world, chunkBox)) {
                return;
            }
            this.fillWithOutline(world, chunkBox, 0, 5, 0, 2, 7, 1, AIR, AIR, false);
            this.fillWithOutline(world, chunkBox, 0, 0, 7, 2, 2, 8, AIR, AIR, false);
            for (int i = 0; i < 5; ++i) {
                this.fillWithOutline(world, chunkBox, 0, 5 - i - (i < 4 ? 1 : 0), 2 + i, 2, 7 - i, 2 + i, AIR, AIR, false);
            }
        }
    }

    public static class MineshaftCorridor
    extends MineshaftPart {
        private final boolean hasRails;
        private final boolean hasCobwebs;
        private boolean hasSpawner;
        private final int length;

        public MineshaftCorridor(NbtCompound nbt) {
            super(StructurePieceType.MINESHAFT_CORRIDOR, nbt);
            this.hasRails = nbt.getBoolean("hr");
            this.hasCobwebs = nbt.getBoolean("sc");
            this.hasSpawner = nbt.getBoolean("hps");
            this.length = nbt.getInt("Num");
        }

        @Override
        protected void writeNbt(StructureContext context, NbtCompound nbt) {
            super.writeNbt(context, nbt);
            nbt.putBoolean("hr", this.hasRails);
            nbt.putBoolean("sc", this.hasCobwebs);
            nbt.putBoolean("hps", this.hasSpawner);
            nbt.putInt("Num", this.length);
        }

        public MineshaftCorridor(int chainLength, AbstractRandom random, BlockBox boundingBox, Direction orientation, MineshaftStructure.Type type) {
            super(StructurePieceType.MINESHAFT_CORRIDOR, chainLength, type, boundingBox);
            this.setOrientation(orientation);
            this.hasRails = random.nextInt(3) == 0;
            this.hasCobwebs = !this.hasRails && random.nextInt(23) == 0;
            this.length = this.getFacing().getAxis() == Direction.Axis.Z ? boundingBox.getBlockCountZ() / 5 : boundingBox.getBlockCountX() / 5;
        }

        /*
         * Enabled aggressive block sorting
         */
        @Nullable
        public static BlockBox getBoundingBox(StructurePiecesHolder structurePiecesHolder, AbstractRandom random, int x, int y, int z, Direction orientation) {
            int i = random.nextInt(3) + 2;
            while (i > 0) {
                int j = i * 5;
                BlockBox blockBox = switch (orientation) {
                    default -> new BlockBox(0, 0, -(j - 1), 2, 2, 0);
                    case Direction.SOUTH -> new BlockBox(0, 0, 0, 2, 2, j - 1);
                    case Direction.WEST -> new BlockBox(-(j - 1), 0, 0, 0, 2, 2);
                    case Direction.EAST -> new BlockBox(0, 0, 0, j - 1, 2, 2);
                };
                blockBox.move(x, y, z);
                if (structurePiecesHolder.getIntersecting(blockBox) == null) {
                    return blockBox;
                }
                --i;
            }
            return null;
        }

        @Override
        public void fillOpenings(StructurePiece start, StructurePiecesHolder holder, AbstractRandom random) {
            block24: {
                int i = this.getChainLength();
                int j = random.nextInt(4);
                Direction direction = this.getFacing();
                if (direction != null) {
                    switch (direction) {
                        default: {
                            if (j <= 1) {
                                MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMinX(), this.boundingBox.getMinY() - 1 + random.nextInt(3), this.boundingBox.getMinZ() - 1, direction, i);
                                break;
                            }
                            if (j == 2) {
                                MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMinX() - 1, this.boundingBox.getMinY() - 1 + random.nextInt(3), this.boundingBox.getMinZ(), Direction.WEST, i);
                                break;
                            }
                            MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMaxX() + 1, this.boundingBox.getMinY() - 1 + random.nextInt(3), this.boundingBox.getMinZ(), Direction.EAST, i);
                            break;
                        }
                        case SOUTH: {
                            if (j <= 1) {
                                MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMinX(), this.boundingBox.getMinY() - 1 + random.nextInt(3), this.boundingBox.getMaxZ() + 1, direction, i);
                                break;
                            }
                            if (j == 2) {
                                MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMinX() - 1, this.boundingBox.getMinY() - 1 + random.nextInt(3), this.boundingBox.getMaxZ() - 3, Direction.WEST, i);
                                break;
                            }
                            MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMaxX() + 1, this.boundingBox.getMinY() - 1 + random.nextInt(3), this.boundingBox.getMaxZ() - 3, Direction.EAST, i);
                            break;
                        }
                        case WEST: {
                            if (j <= 1) {
                                MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMinX() - 1, this.boundingBox.getMinY() - 1 + random.nextInt(3), this.boundingBox.getMinZ(), direction, i);
                                break;
                            }
                            if (j == 2) {
                                MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMinX(), this.boundingBox.getMinY() - 1 + random.nextInt(3), this.boundingBox.getMinZ() - 1, Direction.NORTH, i);
                                break;
                            }
                            MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMinX(), this.boundingBox.getMinY() - 1 + random.nextInt(3), this.boundingBox.getMaxZ() + 1, Direction.SOUTH, i);
                            break;
                        }
                        case EAST: {
                            if (j <= 1) {
                                MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMaxX() + 1, this.boundingBox.getMinY() - 1 + random.nextInt(3), this.boundingBox.getMinZ(), direction, i);
                                break;
                            }
                            if (j == 2) {
                                MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMaxX() - 3, this.boundingBox.getMinY() - 1 + random.nextInt(3), this.boundingBox.getMinZ() - 1, Direction.NORTH, i);
                                break;
                            }
                            MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMaxX() - 3, this.boundingBox.getMinY() - 1 + random.nextInt(3), this.boundingBox.getMaxZ() + 1, Direction.SOUTH, i);
                        }
                    }
                }
                if (i >= 8) break block24;
                if (direction == Direction.NORTH || direction == Direction.SOUTH) {
                    int k = this.boundingBox.getMinZ() + 3;
                    while (k + 3 <= this.boundingBox.getMaxZ()) {
                        int l = random.nextInt(5);
                        if (l == 0) {
                            MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMinX() - 1, this.boundingBox.getMinY(), k, Direction.WEST, i + 1);
                        } else if (l == 1) {
                            MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMaxX() + 1, this.boundingBox.getMinY(), k, Direction.EAST, i + 1);
                        }
                        k += 5;
                    }
                } else {
                    int k = this.boundingBox.getMinX() + 3;
                    while (k + 3 <= this.boundingBox.getMaxX()) {
                        int l = random.nextInt(5);
                        if (l == 0) {
                            MineshaftGenerator.pieceGenerator(start, holder, random, k, this.boundingBox.getMinY(), this.boundingBox.getMinZ() - 1, Direction.NORTH, i + 1);
                        } else if (l == 1) {
                            MineshaftGenerator.pieceGenerator(start, holder, random, k, this.boundingBox.getMinY(), this.boundingBox.getMaxZ() + 1, Direction.SOUTH, i + 1);
                        }
                        k += 5;
                    }
                }
            }
        }

        @Override
        protected boolean addChest(StructureWorldAccess world, BlockBox boundingBox, AbstractRandom random, int x, int y, int z, Identifier lootTableId) {
            BlockPos.Mutable blockPos = this.offsetPos(x, y, z);
            if (boundingBox.contains(blockPos) && world.getBlockState(blockPos).isAir() && !world.getBlockState(((BlockPos)blockPos).down()).isAir()) {
                BlockState blockState = (BlockState)Blocks.RAIL.getDefaultState().with(RailBlock.SHAPE, random.nextBoolean() ? RailShape.NORTH_SOUTH : RailShape.EAST_WEST);
                this.addBlock(world, blockState, x, y, z, boundingBox);
                ChestMinecartEntity chestMinecartEntity = new ChestMinecartEntity(world.toServerWorld(), (double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5);
                chestMinecartEntity.setLootTable(lootTableId, random.nextLong());
                world.spawnEntity(chestMinecartEntity);
                return true;
            }
            return false;
        }

        @Override
        public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, AbstractRandom random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pos) {
            int p;
            int o;
            int n;
            if (this.cannotGenerate(world, chunkBox)) {
                return;
            }
            boolean i = false;
            int j = 2;
            boolean k = false;
            int l = 2;
            int m = this.length * 5 - 1;
            BlockState blockState = this.mineshaftType.getPlanks();
            this.fillWithOutline(world, chunkBox, 0, 0, 0, 2, 1, m, AIR, AIR, false);
            this.fillWithOutlineUnderSeaLevel(world, chunkBox, random, 0.8f, 0, 2, 0, 2, 2, m, AIR, AIR, false, false);
            if (this.hasCobwebs) {
                this.fillWithOutlineUnderSeaLevel(world, chunkBox, random, 0.6f, 0, 0, 0, 2, 1, m, Blocks.COBWEB.getDefaultState(), AIR, false, true);
            }
            for (n = 0; n < this.length; ++n) {
                o = 2 + n * 5;
                this.generateSupports(world, chunkBox, 0, 0, o, 2, 2, random);
                this.addCobwebsUnderground(world, chunkBox, random, 0.1f, 0, 2, o - 1);
                this.addCobwebsUnderground(world, chunkBox, random, 0.1f, 2, 2, o - 1);
                this.addCobwebsUnderground(world, chunkBox, random, 0.1f, 0, 2, o + 1);
                this.addCobwebsUnderground(world, chunkBox, random, 0.1f, 2, 2, o + 1);
                this.addCobwebsUnderground(world, chunkBox, random, 0.05f, 0, 2, o - 2);
                this.addCobwebsUnderground(world, chunkBox, random, 0.05f, 2, 2, o - 2);
                this.addCobwebsUnderground(world, chunkBox, random, 0.05f, 0, 2, o + 2);
                this.addCobwebsUnderground(world, chunkBox, random, 0.05f, 2, 2, o + 2);
                if (random.nextInt(100) == 0) {
                    this.addChest(world, chunkBox, random, 2, 0, o - 1, LootTables.ABANDONED_MINESHAFT_CHEST);
                }
                if (random.nextInt(100) == 0) {
                    this.addChest(world, chunkBox, random, 0, 0, o + 1, LootTables.ABANDONED_MINESHAFT_CHEST);
                }
                if (!this.hasCobwebs || this.hasSpawner) continue;
                p = 1;
                int q = o - 1 + random.nextInt(3);
                BlockPos.Mutable blockPos = this.offsetPos(1, 0, q);
                if (!chunkBox.contains(blockPos) || !this.isUnderSeaLevel(world, 1, 0, q, chunkBox)) continue;
                this.hasSpawner = true;
                world.setBlockState(blockPos, Blocks.SPAWNER.getDefaultState(), Block.NOTIFY_LISTENERS);
                BlockEntity blockEntity = world.getBlockEntity(blockPos);
                if (!(blockEntity instanceof MobSpawnerBlockEntity)) continue;
                ((MobSpawnerBlockEntity)blockEntity).getLogic().setEntityId(EntityType.CAVE_SPIDER);
            }
            for (n = 0; n <= 2; ++n) {
                for (o = 0; o <= m; ++o) {
                    this.tryPlaceFloor(world, chunkBox, blockState, n, -1, o);
                }
            }
            n = 2;
            this.fillSupportBeam(world, chunkBox, 0, -1, 2);
            if (this.length > 1) {
                o = m - 2;
                this.fillSupportBeam(world, chunkBox, 0, -1, o);
            }
            if (this.hasRails) {
                BlockState blockState2 = (BlockState)Blocks.RAIL.getDefaultState().with(RailBlock.SHAPE, RailShape.NORTH_SOUTH);
                for (p = 0; p <= m; ++p) {
                    BlockState blockState3 = this.getBlockAt(world, 1, -1, p, chunkBox);
                    if (blockState3.isAir() || !blockState3.isOpaqueFullCube(world, this.offsetPos(1, -1, p))) continue;
                    float f = this.isUnderSeaLevel(world, 1, 0, p, chunkBox) ? 0.7f : 0.9f;
                    this.addBlockWithRandomThreshold(world, chunkBox, random, f, 1, 0, p, blockState2);
                }
            }
        }

        private void fillSupportBeam(StructureWorldAccess world, BlockBox box, int x, int y, int z) {
            BlockState blockState = this.mineshaftType.getLog();
            BlockState blockState2 = this.mineshaftType.getPlanks();
            if (this.getBlockAt(world, x, y, z, box).isOf(blockState2.getBlock())) {
                this.fillSupportBeam(world, blockState, x, y, z, box);
            }
            if (this.getBlockAt(world, x + 2, y, z, box).isOf(blockState2.getBlock())) {
                this.fillSupportBeam(world, blockState, x + 2, y, z, box);
            }
        }

        @Override
        protected void fillDownwards(StructureWorldAccess world, BlockState state, int x, int y, int z, BlockBox box) {
            BlockPos.Mutable mutable = this.offsetPos(x, y, z);
            if (!box.contains(mutable)) {
                return;
            }
            int i = mutable.getY();
            while (this.canReplace(world.getBlockState(mutable)) && mutable.getY() > world.getBottomY() + 1) {
                mutable.move(Direction.DOWN);
            }
            if (!this.isUpsideSolidFullSquare(world, mutable, world.getBlockState(mutable))) {
                return;
            }
            while (mutable.getY() < i) {
                mutable.move(Direction.UP);
                world.setBlockState(mutable, state, Block.NOTIFY_LISTENERS);
            }
        }

        protected void fillSupportBeam(StructureWorldAccess world, BlockState state, int x, int y, int z, BlockBox box) {
            BlockPos.Mutable mutable = this.offsetPos(x, y, z);
            if (!box.contains(mutable)) {
                return;
            }
            int i = mutable.getY();
            int j = 1;
            boolean bl = true;
            boolean bl2 = true;
            while (bl || bl2) {
                boolean bl3;
                BlockState blockState;
                if (bl) {
                    mutable.setY(i - j);
                    blockState = world.getBlockState(mutable);
                    boolean bl4 = bl3 = this.canReplace(blockState) && !blockState.isOf(Blocks.LAVA);
                    if (!bl3 && this.isUpsideSolidFullSquare(world, mutable, blockState)) {
                        MineshaftCorridor.fillColumn(world, state, mutable, i - j + 1, i);
                        return;
                    }
                    boolean bl5 = bl = j <= 20 && bl3 && mutable.getY() > world.getBottomY() + 1;
                }
                if (bl2) {
                    mutable.setY(i + j);
                    blockState = world.getBlockState(mutable);
                    bl3 = this.canReplace(blockState);
                    if (!bl3 && this.sideCoversSmallSquare(world, mutable, blockState)) {
                        world.setBlockState(mutable.setY(i + 1), this.mineshaftType.getFence(), Block.NOTIFY_LISTENERS);
                        MineshaftCorridor.fillColumn(world, Blocks.CHAIN.getDefaultState(), mutable, i + 2, i + j);
                        return;
                    }
                    bl2 = j <= 50 && bl3 && mutable.getY() < world.getTopY() - 1;
                }
                ++j;
            }
        }

        private static void fillColumn(StructureWorldAccess world, BlockState state, BlockPos.Mutable pos, int startY, int endY) {
            for (int i = startY; i < endY; ++i) {
                world.setBlockState(pos.setY(i), state, Block.NOTIFY_LISTENERS);
            }
        }

        private boolean isUpsideSolidFullSquare(WorldView world, BlockPos pos, BlockState state) {
            return state.isSideSolidFullSquare(world, pos, Direction.UP);
        }

        private boolean sideCoversSmallSquare(WorldView world, BlockPos pos, BlockState state) {
            return Block.sideCoversSmallSquare(world, pos, Direction.DOWN) && !(state.getBlock() instanceof FallingBlock);
        }

        private void generateSupports(StructureWorldAccess world, BlockBox boundingBox, int minX, int minY, int z, int maxY, int maxX, AbstractRandom random) {
            if (!this.isSolidCeiling(world, boundingBox, minX, maxX, maxY, z)) {
                return;
            }
            BlockState blockState = this.mineshaftType.getPlanks();
            BlockState blockState2 = this.mineshaftType.getFence();
            this.fillWithOutline(world, boundingBox, minX, minY, z, minX, maxY - 1, z, (BlockState)blockState2.with(FenceBlock.WEST, true), AIR, false);
            this.fillWithOutline(world, boundingBox, maxX, minY, z, maxX, maxY - 1, z, (BlockState)blockState2.with(FenceBlock.EAST, true), AIR, false);
            if (random.nextInt(4) == 0) {
                this.fillWithOutline(world, boundingBox, minX, maxY, z, minX, maxY, z, blockState, AIR, false);
                this.fillWithOutline(world, boundingBox, maxX, maxY, z, maxX, maxY, z, blockState, AIR, false);
            } else {
                this.fillWithOutline(world, boundingBox, minX, maxY, z, maxX, maxY, z, blockState, AIR, false);
                this.addBlockWithRandomThreshold(world, boundingBox, random, 0.05f, minX + 1, maxY, z - 1, (BlockState)Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, Direction.SOUTH));
                this.addBlockWithRandomThreshold(world, boundingBox, random, 0.05f, minX + 1, maxY, z + 1, (BlockState)Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, Direction.NORTH));
            }
        }

        private void addCobwebsUnderground(StructureWorldAccess world, BlockBox box, AbstractRandom random, float threshold, int x, int y, int z) {
            if (this.isUnderSeaLevel(world, x, y, z, box) && random.nextFloat() < threshold && this.method_36422(world, box, x, y, z, 2)) {
                this.addBlock(world, Blocks.COBWEB.getDefaultState(), x, y, z, box);
            }
        }

        private boolean method_36422(StructureWorldAccess world, BlockBox box, int x, int y, int z, int count) {
            BlockPos.Mutable mutable = this.offsetPos(x, y, z);
            int i = 0;
            for (Direction direction : Direction.values()) {
                mutable.move(direction);
                if (box.contains(mutable) && world.getBlockState(mutable).isSideSolidFullSquare(world, mutable, direction.getOpposite()) && ++i >= count) {
                    return true;
                }
                mutable.move(direction.getOpposite());
            }
            return false;
        }
    }

    static abstract class MineshaftPart
    extends StructurePiece {
        protected MineshaftStructure.Type mineshaftType;
        private final TagKey<Biome> biomeTag;

        public MineshaftPart(StructurePieceType structurePieceType, int chainLength, MineshaftStructure.Type type, BlockBox box) {
            super(structurePieceType, chainLength, box);
            this.mineshaftType = type;
            this.biomeTag = type == MineshaftStructure.Type.MESA ? BiomeTags.MINESHAFT_MESA_HAS_STRUCTURE : BiomeTags.MINESHAFT_HAS_STRUCTURE;
        }

        public MineshaftPart(StructurePieceType structurePieceType, NbtCompound nbtCompound) {
            super(structurePieceType, nbtCompound);
            this.mineshaftType = MineshaftStructure.Type.byIndex(nbtCompound.getInt("MST"));
            this.biomeTag = this.mineshaftType == MineshaftStructure.Type.MESA ? BiomeTags.MINESHAFT_MESA_HAS_STRUCTURE : BiomeTags.MINESHAFT_HAS_STRUCTURE;
        }

        @Override
        protected boolean canAddBlock(WorldView world, int x, int y, int z, BlockBox box) {
            BlockState blockState = this.getBlockAt(world, x, y, z, box);
            return !blockState.isOf(this.mineshaftType.getPlanks().getBlock()) && !blockState.isOf(this.mineshaftType.getLog().getBlock()) && !blockState.isOf(this.mineshaftType.getFence().getBlock()) && !blockState.isOf(Blocks.CHAIN);
        }

        @Override
        protected void writeNbt(StructureContext context, NbtCompound nbt) {
            nbt.putInt("MST", this.mineshaftType.ordinal());
        }

        protected boolean isSolidCeiling(BlockView world, BlockBox boundingBox, int minX, int maxX, int y, int z) {
            for (int i = minX; i <= maxX; ++i) {
                if (!this.getBlockAt(world, i, y + 1, z, boundingBox).isAir()) continue;
                return false;
            }
            return true;
        }

        protected boolean cannotGenerate(WorldAccess world, BlockBox box) {
            int p;
            int o;
            int n;
            int m;
            int i = Math.max(this.boundingBox.getMinX() - 1, box.getMinX());
            int j = Math.max(this.boundingBox.getMinY() - 1, box.getMinY());
            int k = Math.max(this.boundingBox.getMinZ() - 1, box.getMinZ());
            int l = Math.min(this.boundingBox.getMaxX() + 1, box.getMaxX());
            BlockPos.Mutable mutable = new BlockPos.Mutable((i + l) / 2, (j + (m = Math.min(this.boundingBox.getMaxY() + 1, box.getMaxY()))) / 2, (k + (n = Math.min(this.boundingBox.getMaxZ() + 1, box.getMaxZ()))) / 2);
            if (!world.getBiome(mutable).isIn(this.biomeTag)) {
                return true;
            }
            for (o = i; o <= l; ++o) {
                for (p = k; p <= n; ++p) {
                    if (world.getBlockState(mutable.set(o, j, p)).getMaterial().isLiquid()) {
                        return true;
                    }
                    if (!world.getBlockState(mutable.set(o, m, p)).getMaterial().isLiquid()) continue;
                    return true;
                }
            }
            for (o = i; o <= l; ++o) {
                for (p = j; p <= m; ++p) {
                    if (world.getBlockState(mutable.set(o, p, k)).getMaterial().isLiquid()) {
                        return true;
                    }
                    if (!world.getBlockState(mutable.set(o, p, n)).getMaterial().isLiquid()) continue;
                    return true;
                }
            }
            for (o = k; o <= n; ++o) {
                for (p = j; p <= m; ++p) {
                    if (world.getBlockState(mutable.set(i, p, o)).getMaterial().isLiquid()) {
                        return true;
                    }
                    if (!world.getBlockState(mutable.set(l, p, o)).getMaterial().isLiquid()) continue;
                    return true;
                }
            }
            return false;
        }

        protected void tryPlaceFloor(StructureWorldAccess world, BlockBox box, BlockState state, int x, int y, int z) {
            if (!this.isUnderSeaLevel(world, x, y, z, box)) {
                return;
            }
            BlockPos.Mutable blockPos = this.offsetPos(x, y, z);
            BlockState blockState = world.getBlockState(blockPos);
            if (!blockState.isSideSolidFullSquare(world, blockPos, Direction.UP)) {
                world.setBlockState(blockPos, state, Block.NOTIFY_LISTENERS);
            }
        }
    }

    public static class MineshaftRoom
    extends MineshaftPart {
        private final List<BlockBox> entrances = Lists.newLinkedList();

        public MineshaftRoom(int chainLength, AbstractRandom random, int x, int z, MineshaftStructure.Type type) {
            super(StructurePieceType.MINESHAFT_ROOM, chainLength, type, new BlockBox(x, 50, z, x + 7 + random.nextInt(6), 54 + random.nextInt(6), z + 7 + random.nextInt(6)));
            this.mineshaftType = type;
        }

        public MineshaftRoom(NbtCompound nbt) {
            super(StructurePieceType.MINESHAFT_ROOM, nbt);
            BlockBox.CODEC.listOf().parse(NbtOps.INSTANCE, nbt.getList("Entrances", NbtElement.INT_ARRAY_TYPE)).resultOrPartial(LOGGER::error).ifPresent(this.entrances::addAll);
        }

        @Override
        public void fillOpenings(StructurePiece start, StructurePiecesHolder holder, AbstractRandom random) {
            BlockBox blockBox;
            MineshaftPart mineshaftPart;
            int k;
            int i = this.getChainLength();
            int j = this.boundingBox.getBlockCountY() - 3 - 1;
            if (j <= 0) {
                j = 1;
            }
            for (k = 0; k < this.boundingBox.getBlockCountX() && (k += random.nextInt(this.boundingBox.getBlockCountX())) + 3 <= this.boundingBox.getBlockCountX(); k += 4) {
                mineshaftPart = MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMinX() + k, this.boundingBox.getMinY() + random.nextInt(j) + 1, this.boundingBox.getMinZ() - 1, Direction.NORTH, i);
                if (mineshaftPart == null) continue;
                blockBox = mineshaftPart.getBoundingBox();
                this.entrances.add(new BlockBox(blockBox.getMinX(), blockBox.getMinY(), this.boundingBox.getMinZ(), blockBox.getMaxX(), blockBox.getMaxY(), this.boundingBox.getMinZ() + 1));
            }
            for (k = 0; k < this.boundingBox.getBlockCountX() && (k += random.nextInt(this.boundingBox.getBlockCountX())) + 3 <= this.boundingBox.getBlockCountX(); k += 4) {
                mineshaftPart = MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMinX() + k, this.boundingBox.getMinY() + random.nextInt(j) + 1, this.boundingBox.getMaxZ() + 1, Direction.SOUTH, i);
                if (mineshaftPart == null) continue;
                blockBox = mineshaftPart.getBoundingBox();
                this.entrances.add(new BlockBox(blockBox.getMinX(), blockBox.getMinY(), this.boundingBox.getMaxZ() - 1, blockBox.getMaxX(), blockBox.getMaxY(), this.boundingBox.getMaxZ()));
            }
            for (k = 0; k < this.boundingBox.getBlockCountZ() && (k += random.nextInt(this.boundingBox.getBlockCountZ())) + 3 <= this.boundingBox.getBlockCountZ(); k += 4) {
                mineshaftPart = MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMinX() - 1, this.boundingBox.getMinY() + random.nextInt(j) + 1, this.boundingBox.getMinZ() + k, Direction.WEST, i);
                if (mineshaftPart == null) continue;
                blockBox = mineshaftPart.getBoundingBox();
                this.entrances.add(new BlockBox(this.boundingBox.getMinX(), blockBox.getMinY(), blockBox.getMinZ(), this.boundingBox.getMinX() + 1, blockBox.getMaxY(), blockBox.getMaxZ()));
            }
            for (k = 0; k < this.boundingBox.getBlockCountZ() && (k += random.nextInt(this.boundingBox.getBlockCountZ())) + 3 <= this.boundingBox.getBlockCountZ(); k += 4) {
                MineshaftPart structurePiece = MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMaxX() + 1, this.boundingBox.getMinY() + random.nextInt(j) + 1, this.boundingBox.getMinZ() + k, Direction.EAST, i);
                if (structurePiece == null) continue;
                blockBox = structurePiece.getBoundingBox();
                this.entrances.add(new BlockBox(this.boundingBox.getMaxX() - 1, blockBox.getMinY(), blockBox.getMinZ(), this.boundingBox.getMaxX(), blockBox.getMaxY(), blockBox.getMaxZ()));
            }
        }

        @Override
        public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, AbstractRandom random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pos) {
            if (this.cannotGenerate(world, chunkBox)) {
                return;
            }
            this.fillWithOutline(world, chunkBox, this.boundingBox.getMinX(), this.boundingBox.getMinY() + 1, this.boundingBox.getMinZ(), this.boundingBox.getMaxX(), Math.min(this.boundingBox.getMinY() + 3, this.boundingBox.getMaxY()), this.boundingBox.getMaxZ(), AIR, AIR, false);
            for (BlockBox blockBox : this.entrances) {
                this.fillWithOutline(world, chunkBox, blockBox.getMinX(), blockBox.getMaxY() - 2, blockBox.getMinZ(), blockBox.getMaxX(), blockBox.getMaxY(), blockBox.getMaxZ(), AIR, AIR, false);
            }
            this.fillHalfEllipsoid(world, chunkBox, this.boundingBox.getMinX(), this.boundingBox.getMinY() + 4, this.boundingBox.getMinZ(), this.boundingBox.getMaxX(), this.boundingBox.getMaxY(), this.boundingBox.getMaxZ(), AIR, false);
        }

        @Override
        public void translate(int x, int y, int z) {
            super.translate(x, y, z);
            for (BlockBox blockBox : this.entrances) {
                blockBox.move(x, y, z);
            }
        }

        @Override
        protected void writeNbt(StructureContext context, NbtCompound nbt) {
            super.writeNbt(context, nbt);
            BlockBox.CODEC.listOf().encodeStart(NbtOps.INSTANCE, this.entrances).resultOrPartial(LOGGER::error).ifPresent(nbtElement -> nbt.put("Entrances", (NbtElement)nbtElement));
        }
    }
}

