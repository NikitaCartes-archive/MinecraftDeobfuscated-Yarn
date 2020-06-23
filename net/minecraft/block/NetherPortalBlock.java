/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import com.google.common.cache.LoadingCache;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class NetherPortalBlock
extends Block {
    public static final EnumProperty<Direction.Axis> AXIS = Properties.HORIZONTAL_AXIS;
    protected static final VoxelShape X_SHAPE = Block.createCuboidShape(0.0, 0.0, 6.0, 16.0, 16.0, 10.0);
    protected static final VoxelShape Z_SHAPE = Block.createCuboidShape(6.0, 0.0, 0.0, 10.0, 16.0, 16.0);

    public NetherPortalBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(AXIS, Direction.Axis.X));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        switch (state.get(AXIS)) {
            case Z: {
                return Z_SHAPE;
            }
        }
        return X_SHAPE;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (world.getDimension().isNatural() && world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING) && random.nextInt(2000) < world.getDifficulty().getId()) {
            ZombifiedPiglinEntity entity;
            while (world.getBlockState(pos).isOf(this)) {
                pos = pos.down();
            }
            if (world.getBlockState(pos).allowsSpawning(world, pos, EntityType.ZOMBIFIED_PIGLIN) && (entity = EntityType.ZOMBIFIED_PIGLIN.spawn(world, null, null, null, pos.up(), SpawnReason.STRUCTURE, false, false)) != null) {
                entity.netherPortalCooldown = entity.getDefaultNetherPortalCooldown();
            }
        }
    }

    public static boolean createPortalAt(WorldAccess worldAccess, BlockPos blockPos) {
        AreaHelper areaHelper = NetherPortalBlock.createAreaHelper(worldAccess, blockPos);
        if (areaHelper != null) {
            areaHelper.createPortal();
            return true;
        }
        return false;
    }

    @Nullable
    public static AreaHelper createAreaHelper(WorldAccess worldAccess, BlockPos blockPos) {
        AreaHelper areaHelper = new AreaHelper(worldAccess, blockPos, Direction.Axis.X);
        if (areaHelper.isValid() && areaHelper.foundPortalBlocks == 0) {
            return areaHelper;
        }
        AreaHelper areaHelper2 = new AreaHelper(worldAccess, blockPos, Direction.Axis.Z);
        if (areaHelper2.isValid() && areaHelper2.foundPortalBlocks == 0) {
            return areaHelper2;
        }
        return null;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
        boolean bl;
        Direction.Axis axis = direction.getAxis();
        Direction.Axis axis2 = state.get(AXIS);
        boolean bl2 = bl = axis2 != axis && axis.isHorizontal();
        if (bl || newState.isOf(this) || new AreaHelper(world, pos, axis2).wasAlreadyValid()) {
            return super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
        }
        return Blocks.AIR.getDefaultState();
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (!entity.hasVehicle() && !entity.hasPassengers() && entity.canUsePortals()) {
            entity.setInNetherPortal(pos);
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (random.nextInt(100) == 0) {
            world.playSound((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, SoundEvents.BLOCK_PORTAL_AMBIENT, SoundCategory.BLOCKS, 0.5f, random.nextFloat() * 0.4f + 0.8f, false);
        }
        for (int i = 0; i < 4; ++i) {
            double d = (double)pos.getX() + random.nextDouble();
            double e = (double)pos.getY() + random.nextDouble();
            double f = (double)pos.getZ() + random.nextDouble();
            double g = ((double)random.nextFloat() - 0.5) * 0.5;
            double h = ((double)random.nextFloat() - 0.5) * 0.5;
            double j = ((double)random.nextFloat() - 0.5) * 0.5;
            int k = random.nextInt(2) * 2 - 1;
            if (world.getBlockState(pos.west()).isOf(this) || world.getBlockState(pos.east()).isOf(this)) {
                f = (double)pos.getZ() + 0.5 + 0.25 * (double)k;
                j = random.nextFloat() * 2.0f * (float)k;
            } else {
                d = (double)pos.getX() + 0.5 + 0.25 * (double)k;
                g = random.nextFloat() * 2.0f * (float)k;
            }
            world.addParticle(ParticleTypes.PORTAL, d, e, f, g, h, j);
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        return ItemStack.EMPTY;
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        switch (rotation) {
            case COUNTERCLOCKWISE_90: 
            case CLOCKWISE_90: {
                switch (state.get(AXIS)) {
                    case X: {
                        return (BlockState)state.with(AXIS, Direction.Axis.Z);
                    }
                    case Z: {
                        return (BlockState)state.with(AXIS, Direction.Axis.X);
                    }
                }
                return state;
            }
        }
        return state;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AXIS);
    }

    public static BlockPattern.Result findPortal(WorldAccess worldAccess, BlockPos world) {
        Direction.Axis axis = Direction.Axis.Z;
        AreaHelper areaHelper = new AreaHelper(worldAccess, world, Direction.Axis.X);
        LoadingCache<BlockPos, CachedBlockPosition> loadingCache = BlockPattern.makeCache(worldAccess, true);
        if (!areaHelper.isValid()) {
            axis = Direction.Axis.X;
            areaHelper = new AreaHelper(worldAccess, world, Direction.Axis.Z);
        }
        if (!areaHelper.isValid()) {
            return new BlockPattern.Result(world, Direction.NORTH, Direction.UP, loadingCache, 1, 1, 1);
        }
        int[] is = new int[Direction.AxisDirection.values().length];
        Direction direction = areaHelper.negativeDir.rotateYCounterclockwise();
        BlockPos blockPos = areaHelper.lowerCorner.up(areaHelper.getHeight() - 1);
        for (Direction.AxisDirection axisDirection : Direction.AxisDirection.values()) {
            BlockPattern.Result result = new BlockPattern.Result(direction.getDirection() == axisDirection ? blockPos : blockPos.offset(areaHelper.negativeDir, areaHelper.getWidth() - 1), Direction.get(axisDirection, axis), Direction.UP, loadingCache, areaHelper.getWidth(), areaHelper.getHeight(), 1);
            for (int i = 0; i < areaHelper.getWidth(); ++i) {
                for (int j = 0; j < areaHelper.getHeight(); ++j) {
                    CachedBlockPosition cachedBlockPosition = result.translate(i, j, 1);
                    if (cachedBlockPosition.getBlockState().isAir()) continue;
                    int n = axisDirection.ordinal();
                    is[n] = is[n] + 1;
                }
            }
        }
        Direction.AxisDirection axisDirection2 = Direction.AxisDirection.POSITIVE;
        for (Direction.AxisDirection axisDirection3 : Direction.AxisDirection.values()) {
            if (is[axisDirection3.ordinal()] >= is[axisDirection2.ordinal()]) continue;
            axisDirection2 = axisDirection3;
        }
        return new BlockPattern.Result(direction.getDirection() == axisDirection2 ? blockPos : blockPos.offset(areaHelper.negativeDir, areaHelper.getWidth() - 1), Direction.get(axisDirection2, axis), Direction.UP, loadingCache, areaHelper.getWidth(), areaHelper.getHeight(), 1);
    }

    public static class AreaHelper {
        private final WorldAccess world;
        private final Direction.Axis axis;
        private final Direction negativeDir;
        private final Direction positiveDir;
        private int foundPortalBlocks;
        @Nullable
        private BlockPos lowerCorner;
        private int height;
        private int width;

        public AreaHelper(WorldAccess world, BlockPos pos, Direction.Axis axis) {
            this.world = world;
            this.axis = axis;
            if (axis == Direction.Axis.X) {
                this.positiveDir = Direction.EAST;
                this.negativeDir = Direction.WEST;
            } else {
                this.positiveDir = Direction.NORTH;
                this.negativeDir = Direction.SOUTH;
            }
            BlockPos blockPos = pos;
            while (pos.getY() > blockPos.getY() - 21 && pos.getY() > 0 && this.validStateInsidePortal(world.getBlockState(pos.down()))) {
                pos = pos.down();
            }
            int i = this.distanceToPortalEdge(pos, this.positiveDir) - 1;
            if (i >= 0) {
                this.lowerCorner = pos.offset(this.positiveDir, i);
                this.width = this.distanceToPortalEdge(this.lowerCorner, this.negativeDir);
                if (this.width < 2 || this.width > 21) {
                    this.lowerCorner = null;
                    this.width = 0;
                }
            }
            if (this.lowerCorner != null) {
                this.height = this.findHeight();
            }
        }

        protected int distanceToPortalEdge(BlockPos pos, Direction dir) {
            BlockPos blockPos;
            int i;
            for (i = 0; i < 22 && this.validStateInsidePortal(this.world.getBlockState(blockPos = pos.offset(dir, i))) && this.world.getBlockState(blockPos.down()).isOf(Blocks.OBSIDIAN); ++i) {
            }
            if (this.world.getBlockState(pos.offset(dir, i)).isOf(Blocks.OBSIDIAN)) {
                return i;
            }
            return 0;
        }

        public int getHeight() {
            return this.height;
        }

        public int getWidth() {
            return this.width;
        }

        protected int findHeight() {
            int i;
            this.height = 0;
            block0: while (this.height < 21) {
                for (i = 0; i < this.width; ++i) {
                    BlockPos blockPos = this.lowerCorner.offset(this.negativeDir, i).up(this.height);
                    BlockState blockState = this.world.getBlockState(blockPos);
                    if (!this.validStateInsidePortal(blockState)) break block0;
                    if (blockState.isOf(Blocks.NETHER_PORTAL)) {
                        ++this.foundPortalBlocks;
                    }
                    if (i == 0 ? !this.world.getBlockState(blockPos.offset(this.positiveDir)).isOf(Blocks.OBSIDIAN) : i == this.width - 1 && !this.world.getBlockState(blockPos.offset(this.negativeDir)).isOf(Blocks.OBSIDIAN)) break block0;
                }
                ++this.height;
            }
            for (i = 0; i < this.width; ++i) {
                if (this.world.getBlockState(this.lowerCorner.offset(this.negativeDir, i).up(this.height)).isOf(Blocks.OBSIDIAN)) continue;
                this.height = 0;
                break;
            }
            if (this.height > 21 || this.height < 3) {
                this.lowerCorner = null;
                this.width = 0;
                this.height = 0;
                return 0;
            }
            return this.height;
        }

        protected boolean validStateInsidePortal(BlockState state) {
            return state.isAir() || state.isIn(BlockTags.FIRE) || state.isOf(Blocks.NETHER_PORTAL);
        }

        public boolean isValid() {
            return this.lowerCorner != null && this.width >= 2 && this.width <= 21 && this.height >= 3 && this.height <= 21;
        }

        public void createPortal() {
            for (int i = 0; i < this.width; ++i) {
                BlockPos blockPos = this.lowerCorner.offset(this.negativeDir, i);
                for (int j = 0; j < this.height; ++j) {
                    this.world.setBlockState(blockPos.up(j), (BlockState)Blocks.NETHER_PORTAL.getDefaultState().with(AXIS, this.axis), 18);
                }
            }
        }

        private boolean portalAlreadyExisted() {
            return this.foundPortalBlocks >= this.width * this.height;
        }

        public boolean wasAlreadyValid() {
            return this.isValid() && this.portalAlreadyExisted();
        }
    }
}

