/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import java.util.Objects;
import java.util.Spliterators;
import java.util.function.Consumer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.util.CuboidBlockIterator;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.CollisionView;
import net.minecraft.world.border.WorldBorder;
import org.jetbrains.annotations.Nullable;

public class BlockCollisionSpliterator
extends Spliterators.AbstractSpliterator<VoxelShape> {
    @Nullable
    private final Entity entity;
    private final Box box;
    private final ShapeContext context;
    private final CuboidBlockIterator blockIterator;
    private final BlockPos.Mutable pos;
    private final VoxelShape boxShape;
    private final CollisionView world;
    private boolean checkEntity;

    public BlockCollisionSpliterator(CollisionView world, @Nullable Entity entity, Box box) {
        super(Long.MAX_VALUE, 1280);
        this.context = entity == null ? ShapeContext.absent() : ShapeContext.of(entity);
        this.pos = new BlockPos.Mutable();
        this.boxShape = VoxelShapes.cuboid(box);
        this.world = world;
        this.checkEntity = entity != null;
        this.entity = entity;
        this.box = box;
        int i = MathHelper.floor(box.minX - 1.0E-7) - 1;
        int j = MathHelper.floor(box.maxX + 1.0E-7) + 1;
        int k = MathHelper.floor(box.minY - 1.0E-7) - 1;
        int l = MathHelper.floor(box.maxY + 1.0E-7) + 1;
        int m = MathHelper.floor(box.minZ - 1.0E-7) - 1;
        int n = MathHelper.floor(box.maxZ + 1.0E-7) + 1;
        this.blockIterator = new CuboidBlockIterator(i, k, m, j, l, n);
    }

    @Override
    public boolean tryAdvance(Consumer<? super VoxelShape> consumer) {
        return this.checkEntity && this.offerEntityShape(consumer) || this.offerEntitylessShape(consumer);
    }

    boolean offerEntitylessShape(Consumer<? super VoxelShape> consumer) {
        while (this.blockIterator.step()) {
            BlockView blockView;
            int i = this.blockIterator.getX();
            int j = this.blockIterator.getY();
            int k = this.blockIterator.getZ();
            int l = this.blockIterator.getEdgeCoordinatesCount();
            if (l == 3 || (blockView = this.getChunk(i, k)) == null) continue;
            this.pos.set(i, j, k);
            BlockState blockState = blockView.getBlockState(this.pos);
            if (l == 1 && !blockState.exceedsCube() || l == 2 && !blockState.isOf(Blocks.MOVING_PISTON)) continue;
            VoxelShape voxelShape = blockState.getCollisionShape(this.world, this.pos, this.context);
            if (voxelShape == VoxelShapes.fullCube()) {
                if (!this.box.intersects(i, j, k, (double)i + 1.0, (double)j + 1.0, (double)k + 1.0)) continue;
                consumer.accept(voxelShape.offset(i, j, k));
                return true;
            }
            VoxelShape voxelShape2 = voxelShape.offset(i, j, k);
            if (!VoxelShapes.matchesAnywhere(voxelShape2, this.boxShape, BooleanBiFunction.AND)) continue;
            consumer.accept(voxelShape2);
            return true;
        }
        return false;
    }

    @Nullable
    private BlockView getChunk(int x, int z) {
        int i = x >> 4;
        int j = z >> 4;
        return this.world.getExistingChunk(i, j);
    }

    boolean offerEntityShape(Consumer<? super VoxelShape> consumer) {
        boolean bl2;
        Objects.requireNonNull(this.entity);
        this.checkEntity = false;
        WorldBorder worldBorder = this.world.getWorldBorder();
        boolean bl = BlockCollisionSpliterator.isInWorldBorder(worldBorder, this.entity.getBoundingBox().contract(1.0E-7));
        boolean bl3 = bl2 = bl && !BlockCollisionSpliterator.isInWorldBorder(worldBorder, this.entity.getBoundingBox().expand(1.0E-7));
        if (bl2) {
            consumer.accept(worldBorder.asVoxelShape());
            return true;
        }
        return false;
    }

    public static boolean isInWorldBorder(WorldBorder border, Box box) {
        double d = MathHelper.floor(border.getBoundWest());
        double e = MathHelper.floor(border.getBoundNorth());
        double f = MathHelper.ceil(border.getBoundEast());
        double g = MathHelper.ceil(border.getBoundSouth());
        return box.minX > d && box.minX < f && box.minZ > e && box.minZ < g && box.maxX > d && box.maxX < f && box.maxZ > e && box.maxZ < g;
    }
}

