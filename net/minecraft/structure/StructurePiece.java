/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.structure;

import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.jetbrains.annotations.Nullable;

public abstract class StructurePiece {
    protected static final BlockState AIR = Blocks.CAVE_AIR.getDefaultState();
    protected BlockBox boundingBox;
    @Nullable
    private Direction facing;
    private BlockMirror mirror;
    private BlockRotation rotation;
    protected int chainLength;
    private final StructurePieceType type;
    private static final Set<Block> BLOCKS_NEEDING_POST_PROCESSING = ((ImmutableSet.Builder)((ImmutableSet.Builder)((ImmutableSet.Builder)((ImmutableSet.Builder)((ImmutableSet.Builder)((ImmutableSet.Builder)((ImmutableSet.Builder)((ImmutableSet.Builder)((ImmutableSet.Builder)((ImmutableSet.Builder)((ImmutableSet.Builder)ImmutableSet.builder().add(Blocks.NETHER_BRICK_FENCE)).add(Blocks.TORCH)).add(Blocks.WALL_TORCH)).add(Blocks.OAK_FENCE)).add(Blocks.SPRUCE_FENCE)).add(Blocks.DARK_OAK_FENCE)).add(Blocks.ACACIA_FENCE)).add(Blocks.BIRCH_FENCE)).add(Blocks.JUNGLE_FENCE)).add(Blocks.LADDER)).add(Blocks.IRON_BARS)).build();

    protected StructurePiece(StructurePieceType type, int length) {
        this.type = type;
        this.chainLength = length;
    }

    public StructurePiece(StructurePieceType type, CompoundTag tag) {
        this(type, tag.getInt("GD"));
        int i;
        if (tag.contains("BB")) {
            this.boundingBox = new BlockBox(tag.getIntArray("BB"));
        }
        this.setOrientation((i = tag.getInt("O")) == -1 ? null : Direction.fromHorizontal(i));
    }

    public final CompoundTag getTag() {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putString("id", Registry.STRUCTURE_PIECE.getId(this.getType()).toString());
        compoundTag.put("BB", this.boundingBox.toNbt());
        Direction direction = this.getFacing();
        compoundTag.putInt("O", direction == null ? -1 : direction.getHorizontal());
        compoundTag.putInt("GD", this.chainLength);
        this.toNbt(compoundTag);
        return compoundTag;
    }

    protected abstract void toNbt(CompoundTag var1);

    public void fillOpenings(StructurePiece start, List<StructurePiece> pieces, Random random) {
    }

    public abstract boolean generate(StructureWorldAccess var1, StructureAccessor var2, ChunkGenerator var3, Random var4, BlockBox var5, ChunkPos var6, BlockPos var7);

    public BlockBox getBoundingBox() {
        return this.boundingBox;
    }

    public int getChainLength() {
        return this.chainLength;
    }

    public boolean intersectsChunk(ChunkPos chunkPos, int offset) {
        int i = chunkPos.getStartX();
        int j = chunkPos.getStartZ();
        return this.boundingBox.intersectsXZ(i - offset, j - offset, i + 15 + offset, j + 15 + offset);
    }

    public static StructurePiece getOverlappingPiece(List<StructurePiece> pieces, BlockBox blockBox) {
        for (StructurePiece structurePiece : pieces) {
            if (structurePiece.getBoundingBox() == null || !structurePiece.getBoundingBox().intersects(blockBox)) continue;
            return structurePiece;
        }
        return null;
    }

    protected boolean isTouchingLiquid(BlockView blockView, BlockBox blockBox) {
        int p;
        int o;
        int i = Math.max(this.boundingBox.minX - 1, blockBox.minX);
        int j = Math.max(this.boundingBox.minY - 1, blockBox.minY);
        int k = Math.max(this.boundingBox.minZ - 1, blockBox.minZ);
        int l = Math.min(this.boundingBox.maxX + 1, blockBox.maxX);
        int m = Math.min(this.boundingBox.maxY + 1, blockBox.maxY);
        int n = Math.min(this.boundingBox.maxZ + 1, blockBox.maxZ);
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (o = i; o <= l; ++o) {
            for (p = k; p <= n; ++p) {
                if (blockView.getBlockState(mutable.set(o, j, p)).getMaterial().isLiquid()) {
                    return true;
                }
                if (!blockView.getBlockState(mutable.set(o, m, p)).getMaterial().isLiquid()) continue;
                return true;
            }
        }
        for (o = i; o <= l; ++o) {
            for (p = j; p <= m; ++p) {
                if (blockView.getBlockState(mutable.set(o, p, k)).getMaterial().isLiquid()) {
                    return true;
                }
                if (!blockView.getBlockState(mutable.set(o, p, n)).getMaterial().isLiquid()) continue;
                return true;
            }
        }
        for (o = k; o <= n; ++o) {
            for (p = j; p <= m; ++p) {
                if (blockView.getBlockState(mutable.set(i, p, o)).getMaterial().isLiquid()) {
                    return true;
                }
                if (!blockView.getBlockState(mutable.set(l, p, o)).getMaterial().isLiquid()) continue;
                return true;
            }
        }
        return false;
    }

    protected int applyXTransform(int x, int z) {
        Direction direction = this.getFacing();
        if (direction == null) {
            return x;
        }
        switch (direction) {
            case NORTH: 
            case SOUTH: {
                return this.boundingBox.minX + x;
            }
            case WEST: {
                return this.boundingBox.maxX - z;
            }
            case EAST: {
                return this.boundingBox.minX + z;
            }
        }
        return x;
    }

    protected int applyYTransform(int y) {
        if (this.getFacing() == null) {
            return y;
        }
        return y + this.boundingBox.minY;
    }

    protected int applyZTransform(int x, int z) {
        Direction direction = this.getFacing();
        if (direction == null) {
            return z;
        }
        switch (direction) {
            case NORTH: {
                return this.boundingBox.maxZ - z;
            }
            case SOUTH: {
                return this.boundingBox.minZ + z;
            }
            case WEST: 
            case EAST: {
                return this.boundingBox.minZ + x;
            }
        }
        return z;
    }

    protected void addBlock(StructureWorldAccess structureWorldAccess, BlockState block, int x, int y, int z, BlockBox blockBox) {
        BlockPos blockPos = new BlockPos(this.applyXTransform(x, z), this.applyYTransform(y), this.applyZTransform(x, z));
        if (!blockBox.contains(blockPos)) {
            return;
        }
        if (this.mirror != BlockMirror.NONE) {
            block = block.mirror(this.mirror);
        }
        if (this.rotation != BlockRotation.NONE) {
            block = block.rotate(this.rotation);
        }
        structureWorldAccess.setBlockState(blockPos, block, 2);
        FluidState fluidState = structureWorldAccess.getFluidState(blockPos);
        if (!fluidState.isEmpty()) {
            structureWorldAccess.getFluidTickScheduler().schedule(blockPos, fluidState.getFluid(), 0);
        }
        if (BLOCKS_NEEDING_POST_PROCESSING.contains(block.getBlock())) {
            structureWorldAccess.getChunk(blockPos).markBlockForPostProcessing(blockPos);
        }
    }

    protected BlockState getBlockAt(BlockView blockView, int x, int y, int z, BlockBox blockBox) {
        int k;
        int j;
        int i = this.applyXTransform(x, z);
        BlockPos blockPos = new BlockPos(i, j = this.applyYTransform(y), k = this.applyZTransform(x, z));
        if (!blockBox.contains(blockPos)) {
            return Blocks.AIR.getDefaultState();
        }
        return blockView.getBlockState(blockPos);
    }

    protected boolean isUnderSeaLevel(WorldView worldView, int x, int z, int y, BlockBox blockBox) {
        int k;
        int j;
        int i = this.applyXTransform(x, y);
        BlockPos blockPos = new BlockPos(i, j = this.applyYTransform(z + 1), k = this.applyZTransform(x, y));
        if (!blockBox.contains(blockPos)) {
            return false;
        }
        return j < worldView.getTopY(Heightmap.Type.OCEAN_FLOOR_WG, i, k);
    }

    protected void fill(StructureWorldAccess structureWorldAccess, BlockBox bounds, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        for (int i = minY; i <= maxY; ++i) {
            for (int j = minX; j <= maxX; ++j) {
                for (int k = minZ; k <= maxZ; ++k) {
                    this.addBlock(structureWorldAccess, Blocks.AIR.getDefaultState(), j, i, k, bounds);
                }
            }
        }
    }

    protected void fillWithOutline(StructureWorldAccess structureWorldAccess, BlockBox blockBox, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, BlockState outline, BlockState inside, boolean cantReplaceAir) {
        for (int i = minY; i <= maxY; ++i) {
            for (int j = minX; j <= maxX; ++j) {
                for (int k = minZ; k <= maxZ; ++k) {
                    if (cantReplaceAir && this.getBlockAt(structureWorldAccess, j, i, k, blockBox).isAir()) continue;
                    if (i == minY || i == maxY || j == minX || j == maxX || k == minZ || k == maxZ) {
                        this.addBlock(structureWorldAccess, outline, j, i, k, blockBox);
                        continue;
                    }
                    this.addBlock(structureWorldAccess, inside, j, i, k, blockBox);
                }
            }
        }
    }

    protected void fillWithOutline(StructureWorldAccess structureWorldAccess, BlockBox blockBox, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, boolean cantReplaceAir, Random random, BlockRandomizer blockRandomizer) {
        for (int i = minY; i <= maxY; ++i) {
            for (int j = minX; j <= maxX; ++j) {
                for (int k = minZ; k <= maxZ; ++k) {
                    if (cantReplaceAir && this.getBlockAt(structureWorldAccess, j, i, k, blockBox).isAir()) continue;
                    blockRandomizer.setBlock(random, j, i, k, i == minY || i == maxY || j == minX || j == maxX || k == minZ || k == maxZ);
                    this.addBlock(structureWorldAccess, blockRandomizer.getBlock(), j, i, k, blockBox);
                }
            }
        }
    }

    protected void fillWithOutlineUnderSeaLevel(StructureWorldAccess structureWorldAccess, BlockBox blockBox, Random random, float blockChance, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, BlockState outline, BlockState inside, boolean cantReplaceAir, boolean stayBelowSeaLevel) {
        for (int i = minY; i <= maxY; ++i) {
            for (int j = minX; j <= maxX; ++j) {
                for (int k = minZ; k <= maxZ; ++k) {
                    if (random.nextFloat() > blockChance || cantReplaceAir && this.getBlockAt(structureWorldAccess, j, i, k, blockBox).isAir() || stayBelowSeaLevel && !this.isUnderSeaLevel(structureWorldAccess, j, i, k, blockBox)) continue;
                    if (i == minY || i == maxY || j == minX || j == maxX || k == minZ || k == maxZ) {
                        this.addBlock(structureWorldAccess, outline, j, i, k, blockBox);
                        continue;
                    }
                    this.addBlock(structureWorldAccess, inside, j, i, k, blockBox);
                }
            }
        }
    }

    protected void addBlockWithRandomThreshold(StructureWorldAccess structureWorldAccess, BlockBox bounds, Random random, float threshold, int x, int y, int z, BlockState blockState) {
        if (random.nextFloat() < threshold) {
            this.addBlock(structureWorldAccess, blockState, x, y, z, bounds);
        }
    }

    protected void fillHalfEllipsoid(StructureWorldAccess structureWorldAccess, BlockBox bounds, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, BlockState block, boolean cantReplaceAir) {
        float f = maxX - minX + 1;
        float g = maxY - minY + 1;
        float h = maxZ - minZ + 1;
        float i = (float)minX + f / 2.0f;
        float j = (float)minZ + h / 2.0f;
        for (int k = minY; k <= maxY; ++k) {
            float l = (float)(k - minY) / g;
            for (int m = minX; m <= maxX; ++m) {
                float n = ((float)m - i) / (f * 0.5f);
                for (int o = minZ; o <= maxZ; ++o) {
                    float q;
                    float p = ((float)o - j) / (h * 0.5f);
                    if (cantReplaceAir && this.getBlockAt(structureWorldAccess, m, k, o, bounds).isAir() || !((q = n * n + l * l + p * p) <= 1.05f)) continue;
                    this.addBlock(structureWorldAccess, block, m, k, o, bounds);
                }
            }
        }
    }

    protected void fillDownwards(StructureWorldAccess structureWorldAccess, BlockState blockState, int x, int y, int z, BlockBox blockBox) {
        int k;
        int j;
        int i = this.applyXTransform(x, z);
        if (!blockBox.contains(new BlockPos(i, j = this.applyYTransform(y), k = this.applyZTransform(x, z)))) {
            return;
        }
        while ((structureWorldAccess.isAir(new BlockPos(i, j, k)) || structureWorldAccess.getBlockState(new BlockPos(i, j, k)).getMaterial().isLiquid()) && j > structureWorldAccess.getBottomSectionLimit() + 1) {
            structureWorldAccess.setBlockState(new BlockPos(i, j, k), blockState, 2);
            --j;
        }
    }

    protected boolean addChest(StructureWorldAccess structureWorldAccess, BlockBox boundingBox, Random random, int x, int y, int z, Identifier lootTableId) {
        BlockPos blockPos = new BlockPos(this.applyXTransform(x, z), this.applyYTransform(y), this.applyZTransform(x, z));
        return this.addChest(structureWorldAccess, boundingBox, random, blockPos, lootTableId, null);
    }

    public static BlockState orientateChest(BlockView blockView, BlockPos blockPos, BlockState blockState) {
        Direction direction = null;
        for (Direction direction2 : Direction.Type.HORIZONTAL) {
            BlockPos blockPos2 = blockPos.offset(direction2);
            BlockState blockState2 = blockView.getBlockState(blockPos2);
            if (blockState2.isOf(Blocks.CHEST)) {
                return blockState;
            }
            if (!blockState2.isOpaqueFullCube(blockView, blockPos2)) continue;
            if (direction == null) {
                direction = direction2;
                continue;
            }
            direction = null;
            break;
        }
        if (direction != null) {
            return (BlockState)blockState.with(HorizontalFacingBlock.FACING, direction.getOpposite());
        }
        Direction direction3 = blockState.get(HorizontalFacingBlock.FACING);
        BlockPos blockPos3 = blockPos.offset(direction3);
        if (blockView.getBlockState(blockPos3).isOpaqueFullCube(blockView, blockPos3)) {
            direction3 = direction3.getOpposite();
            blockPos3 = blockPos.offset(direction3);
        }
        if (blockView.getBlockState(blockPos3).isOpaqueFullCube(blockView, blockPos3)) {
            direction3 = direction3.rotateYClockwise();
            blockPos3 = blockPos.offset(direction3);
        }
        if (blockView.getBlockState(blockPos3).isOpaqueFullCube(blockView, blockPos3)) {
            direction3 = direction3.getOpposite();
            blockPos3 = blockPos.offset(direction3);
        }
        return (BlockState)blockState.with(HorizontalFacingBlock.FACING, direction3);
    }

    protected boolean addChest(ServerWorldAccess serverWorldAccess, BlockBox boundingBox, Random random, BlockPos pos, Identifier lootTableId, @Nullable BlockState block) {
        if (!boundingBox.contains(pos) || serverWorldAccess.getBlockState(pos).isOf(Blocks.CHEST)) {
            return false;
        }
        if (block == null) {
            block = StructurePiece.orientateChest(serverWorldAccess, pos, Blocks.CHEST.getDefaultState());
        }
        serverWorldAccess.setBlockState(pos, block, 2);
        BlockEntity blockEntity = serverWorldAccess.getBlockEntity(pos);
        if (blockEntity instanceof ChestBlockEntity) {
            ((ChestBlockEntity)blockEntity).setLootTable(lootTableId, random.nextLong());
        }
        return true;
    }

    protected boolean addDispenser(StructureWorldAccess structureWorldAccess, BlockBox boundingBox, Random random, int x, int y, int z, Direction facing, Identifier lootTableId) {
        BlockPos blockPos = new BlockPos(this.applyXTransform(x, z), this.applyYTransform(y), this.applyZTransform(x, z));
        if (boundingBox.contains(blockPos) && !structureWorldAccess.getBlockState(blockPos).isOf(Blocks.DISPENSER)) {
            this.addBlock(structureWorldAccess, (BlockState)Blocks.DISPENSER.getDefaultState().with(DispenserBlock.FACING, facing), x, y, z, boundingBox);
            BlockEntity blockEntity = structureWorldAccess.getBlockEntity(blockPos);
            if (blockEntity instanceof DispenserBlockEntity) {
                ((DispenserBlockEntity)blockEntity).setLootTable(lootTableId, random.nextLong());
            }
            return true;
        }
        return false;
    }

    public void translate(int x, int y, int z) {
        this.boundingBox.move(x, y, z);
    }

    @Nullable
    public Direction getFacing() {
        return this.facing;
    }

    public void setOrientation(@Nullable Direction orientation) {
        this.facing = orientation;
        if (orientation == null) {
            this.rotation = BlockRotation.NONE;
            this.mirror = BlockMirror.NONE;
        } else {
            switch (orientation) {
                case SOUTH: {
                    this.mirror = BlockMirror.LEFT_RIGHT;
                    this.rotation = BlockRotation.NONE;
                    break;
                }
                case WEST: {
                    this.mirror = BlockMirror.LEFT_RIGHT;
                    this.rotation = BlockRotation.CLOCKWISE_90;
                    break;
                }
                case EAST: {
                    this.mirror = BlockMirror.NONE;
                    this.rotation = BlockRotation.CLOCKWISE_90;
                    break;
                }
                default: {
                    this.mirror = BlockMirror.NONE;
                    this.rotation = BlockRotation.NONE;
                }
            }
        }
    }

    public BlockRotation getRotation() {
        return this.rotation;
    }

    public StructurePieceType getType() {
        return this.type;
    }

    public static abstract class BlockRandomizer {
        protected BlockState block = Blocks.AIR.getDefaultState();

        protected BlockRandomizer() {
        }

        public abstract void setBlock(Random var1, int var2, int var3, int var4, boolean var5);

        public BlockState getBlock() {
            return this.block;
        }
    }
}

