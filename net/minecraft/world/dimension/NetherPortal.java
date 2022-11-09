/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.dimension;

import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockLocating;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class NetherPortal {
    private static final int MIN_WIDTH = 2;
    public static final int MAX_WIDTH = 21;
    private static final int field_31826 = 3;
    public static final int field_31824 = 21;
    private static final AbstractBlock.ContextPredicate IS_VALID_FRAME_BLOCK = (state, world, pos) -> state.isOf(Blocks.OBSIDIAN);
    private static final float FALLBACK_THRESHOLD = 4.0f;
    private static final double HEIGHT_STRETCH = 1.0;
    private final WorldAccess world;
    private final Direction.Axis axis;
    private final Direction negativeDir;
    private int foundPortalBlocks;
    @Nullable
    private BlockPos lowerCorner;
    private int height;
    private final int width;

    public static Optional<NetherPortal> getNewPortal(WorldAccess world, BlockPos pos, Direction.Axis axis) {
        return NetherPortal.getOrEmpty(world, pos, areaHelper -> areaHelper.isValid() && areaHelper.foundPortalBlocks == 0, axis);
    }

    public static Optional<NetherPortal> getOrEmpty(WorldAccess world, BlockPos pos, Predicate<NetherPortal> validator, Direction.Axis axis) {
        Optional<NetherPortal> optional = Optional.of(new NetherPortal(world, pos, axis)).filter(validator);
        if (optional.isPresent()) {
            return optional;
        }
        Direction.Axis axis2 = axis == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
        return Optional.of(new NetherPortal(world, pos, axis2)).filter(validator);
    }

    public NetherPortal(WorldAccess world, BlockPos pos, Direction.Axis axis) {
        this.world = world;
        this.axis = axis;
        this.negativeDir = axis == Direction.Axis.X ? Direction.WEST : Direction.SOUTH;
        this.lowerCorner = this.getLowerCorner(pos);
        if (this.lowerCorner == null) {
            this.lowerCorner = pos;
            this.width = 1;
            this.height = 1;
        } else {
            this.width = this.getWidth();
            if (this.width > 0) {
                this.height = this.getHeight();
            }
        }
    }

    @Nullable
    private BlockPos getLowerCorner(BlockPos pos) {
        int i = Math.max(this.world.getBottomY(), pos.getY() - 21);
        while (pos.getY() > i && NetherPortal.validStateInsidePortal(this.world.getBlockState(pos.down()))) {
            pos = pos.down();
        }
        Direction direction = this.negativeDir.getOpposite();
        int j = this.getWidth(pos, direction) - 1;
        if (j < 0) {
            return null;
        }
        return pos.offset(direction, j);
    }

    private int getWidth() {
        int i = this.getWidth(this.lowerCorner, this.negativeDir);
        if (i < 2 || i > 21) {
            return 0;
        }
        return i;
    }

    private int getWidth(BlockPos pos, Direction direction) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int i = 0; i <= 21; ++i) {
            mutable.set(pos).move(direction, i);
            BlockState blockState = this.world.getBlockState(mutable);
            if (!NetherPortal.validStateInsidePortal(blockState)) {
                if (!IS_VALID_FRAME_BLOCK.test(blockState, this.world, mutable)) break;
                return i;
            }
            BlockState blockState2 = this.world.getBlockState(mutable.move(Direction.DOWN));
            if (!IS_VALID_FRAME_BLOCK.test(blockState2, this.world, mutable)) break;
        }
        return 0;
    }

    private int getHeight() {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        int i = this.getPotentialHeight(mutable);
        if (i < 3 || i > 21 || !this.isHorizontalFrameValid(mutable, i)) {
            return 0;
        }
        return i;
    }

    private boolean isHorizontalFrameValid(BlockPos.Mutable pos, int height) {
        for (int i = 0; i < this.width; ++i) {
            BlockPos.Mutable mutable = pos.set(this.lowerCorner).move(Direction.UP, height).move(this.negativeDir, i);
            if (IS_VALID_FRAME_BLOCK.test(this.world.getBlockState(mutable), this.world, mutable)) continue;
            return false;
        }
        return true;
    }

    private int getPotentialHeight(BlockPos.Mutable pos) {
        for (int i = 0; i < 21; ++i) {
            pos.set(this.lowerCorner).move(Direction.UP, i).move(this.negativeDir, -1);
            if (!IS_VALID_FRAME_BLOCK.test(this.world.getBlockState(pos), this.world, pos)) {
                return i;
            }
            pos.set(this.lowerCorner).move(Direction.UP, i).move(this.negativeDir, this.width);
            if (!IS_VALID_FRAME_BLOCK.test(this.world.getBlockState(pos), this.world, pos)) {
                return i;
            }
            for (int j = 0; j < this.width; ++j) {
                pos.set(this.lowerCorner).move(Direction.UP, i).move(this.negativeDir, j);
                BlockState blockState = this.world.getBlockState(pos);
                if (!NetherPortal.validStateInsidePortal(blockState)) {
                    return i;
                }
                if (!blockState.isOf(Blocks.NETHER_PORTAL)) continue;
                ++this.foundPortalBlocks;
            }
        }
        return 21;
    }

    private static boolean validStateInsidePortal(BlockState state) {
        return state.isAir() || state.isIn(BlockTags.FIRE) || state.isOf(Blocks.NETHER_PORTAL);
    }

    public boolean isValid() {
        return this.lowerCorner != null && this.width >= 2 && this.width <= 21 && this.height >= 3 && this.height <= 21;
    }

    public void createPortal() {
        BlockState blockState = (BlockState)Blocks.NETHER_PORTAL.getDefaultState().with(NetherPortalBlock.AXIS, this.axis);
        BlockPos.iterate(this.lowerCorner, this.lowerCorner.offset(Direction.UP, this.height - 1).offset(this.negativeDir, this.width - 1)).forEach(blockPos -> this.world.setBlockState((BlockPos)blockPos, blockState, Block.NOTIFY_LISTENERS | Block.FORCE_STATE));
    }

    public boolean wasAlreadyValid() {
        return this.isValid() && this.foundPortalBlocks == this.width * this.height;
    }

    public static Vec3d entityPosInPortal(BlockLocating.Rectangle portalRect, Direction.Axis portalAxis, Vec3d entityPos, EntityDimensions entityDimensions) {
        double h;
        Direction.Axis axis;
        double g;
        double d = (double)portalRect.width - (double)entityDimensions.width;
        double e = (double)portalRect.height - (double)entityDimensions.height;
        BlockPos blockPos = portalRect.lowerLeft;
        if (d > 0.0) {
            float f = (float)blockPos.getComponentAlongAxis(portalAxis) + entityDimensions.width / 2.0f;
            g = MathHelper.clamp(MathHelper.getLerpProgress(entityPos.getComponentAlongAxis(portalAxis) - (double)f, 0.0, d), 0.0, 1.0);
        } else {
            g = 0.5;
        }
        if (e > 0.0) {
            axis = Direction.Axis.Y;
            h = MathHelper.clamp(MathHelper.getLerpProgress(entityPos.getComponentAlongAxis(axis) - (double)blockPos.getComponentAlongAxis(axis), 0.0, e), 0.0, 1.0);
        } else {
            h = 0.0;
        }
        axis = portalAxis == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
        double i = entityPos.getComponentAlongAxis(axis) - ((double)blockPos.getComponentAlongAxis(axis) + 0.5);
        return new Vec3d(g, h, i);
    }

    /**
     * Determines a {@link TeleportTarget} based on a specific portal.
     * 
     * <p>The offset, velocity, and angle are modified based on the portal's axis.
     */
    public static TeleportTarget getNetherTeleportTarget(ServerWorld destination, BlockLocating.Rectangle portalRect, Direction.Axis portalAxis, Vec3d offset, Entity entity, Vec3d velocity, float yaw, float pitch) {
        BlockPos blockPos = portalRect.lowerLeft;
        BlockState blockState = destination.getBlockState(blockPos);
        Direction.Axis axis = blockState.getOrEmpty(Properties.HORIZONTAL_AXIS).orElse(Direction.Axis.X);
        double d = portalRect.width;
        double e = portalRect.height;
        EntityDimensions entityDimensions = entity.getDimensions(entity.getPose());
        int i = portalAxis == axis ? 0 : 90;
        Vec3d vec3d = portalAxis == axis ? velocity : new Vec3d(velocity.z, velocity.y, -velocity.x);
        double f = (double)entityDimensions.width / 2.0 + (d - (double)entityDimensions.width) * offset.getX();
        double g = (e - (double)entityDimensions.height) * offset.getY();
        double h = 0.5 + offset.getZ();
        boolean bl = axis == Direction.Axis.X;
        Vec3d vec3d2 = new Vec3d((double)blockPos.getX() + (bl ? f : h), (double)blockPos.getY() + g, (double)blockPos.getZ() + (bl ? h : f));
        Vec3d vec3d3 = NetherPortal.findOpenPosition(vec3d2, destination, entity, entityDimensions);
        return new TeleportTarget(vec3d3, vec3d, yaw + (float)i, pitch);
    }

    private static Vec3d findOpenPosition(Vec3d fallback, ServerWorld world, Entity entity, EntityDimensions dimensions) {
        if (dimensions.width > 4.0f || dimensions.height > 4.0f) {
            return fallback;
        }
        double d = (double)dimensions.height / 2.0;
        Vec3d vec3d = fallback.add(0.0, d, 0.0);
        VoxelShape voxelShape = VoxelShapes.cuboid(Box.of(vec3d, dimensions.width, 0.0, dimensions.width).stretch(0.0, 1.0, 0.0).expand(1.0E-6));
        Optional<Vec3d> optional = world.findClosestCollision(entity, voxelShape, vec3d, dimensions.width, dimensions.height, dimensions.width);
        Optional<Vec3d> optional2 = optional.map(pos -> pos.subtract(0.0, d, 0.0));
        return optional2.orElse(fallback);
    }
}

