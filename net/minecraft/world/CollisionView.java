/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Stream;
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
        return this.isSpaceEmpty(null, box, entity -> true);
    }

    default public boolean isSpaceEmpty(Entity entity2) {
        return this.isSpaceEmpty(entity2, entity2.getBoundingBox(), entity -> true);
    }

    default public boolean isSpaceEmpty(Entity entity2, Box box) {
        return this.isSpaceEmpty(entity2, box, entity -> true);
    }

    default public boolean isSpaceEmpty(@Nullable Entity entity, Box box, Predicate<Entity> predicate) {
        return this.getCollisions(entity, box, predicate).allMatch(VoxelShape::isEmpty);
    }

    public Stream<VoxelShape> getEntityCollisions(@Nullable Entity var1, Box var2, Predicate<Entity> var3);

    default public Stream<VoxelShape> getCollisions(@Nullable Entity entity, Box box, Predicate<Entity> predicate) {
        return Stream.concat(this.getBlockCollisions(entity, box), this.getEntityCollisions(entity, box, predicate));
    }

    default public Stream<VoxelShape> getBlockCollisions(@Nullable Entity entity, Box box) {
        return StreamSupport.stream(new BlockCollisionSpliterator(this, entity, box), false);
    }

    default public boolean isBlockSpaceEmpty(@Nullable Entity entity, Box box, BiPredicate<BlockState, BlockPos> biPredicate) {
        return !this.getBlockCollisions(entity, box, biPredicate).allMatch(VoxelShape::isEmpty);
    }

    default public Stream<VoxelShape> getBlockCollisions(@Nullable Entity entity, Box box, BiPredicate<BlockState, BlockPos> biPredicate) {
        return StreamSupport.stream(new BlockCollisionSpliterator(this, entity, box, biPredicate), false);
    }

    default public Optional<Vec3d> method_33594(@Nullable Entity entity, VoxelShape voxelShape2, Vec3d vec3d, double d, double e, double f) {
        if (voxelShape2.isEmpty()) {
            return Optional.empty();
        }
        Box box2 = voxelShape2.getBoundingBox().expand(d, e, f);
        VoxelShape voxelShape22 = this.getBlockCollisions(entity, box2).flatMap(voxelShape -> voxelShape.getBoundingBoxes().stream()).map(box -> box.expand(d / 2.0, e / 2.0, f / 2.0)).map(VoxelShapes::cuboid).reduce(VoxelShapes.empty(), VoxelShapes::union);
        VoxelShape voxelShape3 = VoxelShapes.combineAndSimplify(voxelShape2, voxelShape22, BooleanBiFunction.ONLY_FIRST);
        return voxelShape3.method_33661(vec3d);
    }
}

