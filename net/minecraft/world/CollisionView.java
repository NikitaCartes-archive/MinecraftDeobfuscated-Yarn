/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import com.google.common.collect.Iterables;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockCollisionSpliterator;
import net.minecraft.world.BlockView;
import net.minecraft.world.border.WorldBorder;
import org.jetbrains.annotations.Nullable;

public interface CollisionView
extends BlockView {
    public WorldBorder getWorldBorder();

    @Nullable
    public BlockView getChunkAsView(int var1, int var2);

    default public boolean intersectsEntities(@Nullable Entity except, VoxelShape shape) {
        return true;
    }

    default public boolean canPlace(BlockState state, BlockPos pos, ShapeContext context) {
        VoxelShape voxelShape = state.getCollisionShape(this, pos, context);
        return voxelShape.isEmpty() || this.intersectsEntities(null, voxelShape.offset(pos.getX(), pos.getY(), pos.getZ()));
    }

    default public boolean intersectsEntities(Entity entity) {
        return this.intersectsEntities(entity, VoxelShapes.cuboid(entity.getBoundingBox()));
    }

    default public boolean isSpaceEmpty(Box box) {
        return this.isSpaceEmpty(null, box);
    }

    default public boolean isSpaceEmpty(Entity entity) {
        return this.isSpaceEmpty(entity, entity.getBoundingBox());
    }

    default public boolean isSpaceEmpty(@Nullable Entity entity, Box box) {
        for (VoxelShape voxelShape : this.getBlockCollisions(entity, box)) {
            if (voxelShape.isEmpty()) continue;
            return false;
        }
        if (!this.getEntityCollisions(entity, box).isEmpty()) {
            return false;
        }
        if (entity != null) {
            VoxelShape voxelShape2 = this.getWorldBorderCollisions(entity, box);
            return voxelShape2 == null || !VoxelShapes.matchesAnywhere(voxelShape2, VoxelShapes.cuboid(box), BooleanBiFunction.AND);
        }
        return true;
    }

    public List<VoxelShape> getEntityCollisions(@Nullable Entity var1, Box var2);

    default public Iterable<VoxelShape> getCollisions(@Nullable Entity entity, Box box) {
        List<VoxelShape> list = this.getEntityCollisions(entity, box);
        Iterable<VoxelShape> iterable = this.getBlockCollisions(entity, box);
        return list.isEmpty() ? iterable : Iterables.concat(list, iterable);
    }

    default public Iterable<VoxelShape> getBlockCollisions(@Nullable Entity entity, Box box) {
        return () -> new BlockCollisionSpliterator(this, entity, box);
    }

    @Nullable
    private VoxelShape getWorldBorderCollisions(Entity entity, Box box) {
        WorldBorder worldBorder = this.getWorldBorder();
        return worldBorder.canCollide(entity, box) ? worldBorder.asVoxelShape() : null;
    }

    default public boolean canCollide(@Nullable Entity entity, Box box) {
        BlockCollisionSpliterator blockCollisionSpliterator = new BlockCollisionSpliterator(this, entity, box, true);
        while (blockCollisionSpliterator.hasNext()) {
            if (((VoxelShape)blockCollisionSpliterator.next()).isEmpty()) continue;
            return true;
        }
        return false;
    }

    default public Optional<Vec3d> findClosestCollision(@Nullable Entity entity, VoxelShape shape, Vec3d target, double x, double y, double z) {
        if (shape.isEmpty()) {
            return Optional.empty();
        }
        Box box2 = shape.getBoundingBox().expand(x, y, z);
        VoxelShape voxelShape2 = StreamSupport.stream(this.getBlockCollisions(entity, box2).spliterator(), false).filter(voxelShape -> this.getWorldBorder() == null || this.getWorldBorder().contains(voxelShape.getBoundingBox())).flatMap(voxelShape -> voxelShape.getBoundingBoxes().stream()).map(box -> box.expand(x / 2.0, y / 2.0, z / 2.0)).map(VoxelShapes::cuboid).reduce(VoxelShapes.empty(), VoxelShapes::union);
        VoxelShape voxelShape22 = VoxelShapes.combineAndSimplify(shape, voxelShape2, BooleanBiFunction.ONLY_FIRST);
        return voxelShape22.getClosestPointTo(target);
    }
}

