/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import com.google.common.collect.Streams;
import java.util.Collections;
import java.util.Set;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
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
import net.minecraft.world.border.WorldBorder;
import org.jetbrains.annotations.Nullable;

public interface CollisionView
extends BlockView {
    public WorldBorder getWorldBorder();

    @Nullable
    public BlockView getExistingChunk(int var1, int var2);

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

    default public boolean doesNotCollide(Box box) {
        return this.doesNotCollide(null, box, Collections.emptySet());
    }

    default public boolean doesNotCollide(Entity entity) {
        return this.doesNotCollide(entity, entity.getBoundingBox(), Collections.emptySet());
    }

    default public boolean doesNotCollide(Entity entity, Box box) {
        return this.doesNotCollide(entity, box, Collections.emptySet());
    }

    default public boolean doesNotCollide(@Nullable Entity entity, Box entityBoundingBox, Set<Entity> otherEntities) {
        return this.getCollisions(entity, entityBoundingBox, otherEntities).allMatch(VoxelShape::isEmpty);
    }

    default public Stream<VoxelShape> getEntityCollisions(@Nullable Entity entity, Box box, Set<Entity> excluded) {
        return Stream.empty();
    }

    default public Stream<VoxelShape> getCollisions(@Nullable Entity entity, Box box, Set<Entity> excluded) {
        return Streams.concat(this.getBlockCollisions(entity, box), this.getEntityCollisions(entity, box, excluded));
    }

    default public Stream<VoxelShape> getBlockCollisions(final @Nullable Entity entity, Box box) {
        int i = MathHelper.floor(box.x1 - 1.0E-7) - 1;
        int j = MathHelper.floor(box.x2 + 1.0E-7) + 1;
        int k = MathHelper.floor(box.y1 - 1.0E-7) - 1;
        int l = MathHelper.floor(box.y2 + 1.0E-7) + 1;
        int m = MathHelper.floor(box.z1 - 1.0E-7) - 1;
        int n = MathHelper.floor(box.z2 + 1.0E-7) + 1;
        final ShapeContext shapeContext = entity == null ? ShapeContext.absent() : ShapeContext.of(entity);
        final CuboidBlockIterator cuboidBlockIterator = new CuboidBlockIterator(i, k, m, j, l, n);
        final BlockPos.Mutable mutable = new BlockPos.Mutable();
        final VoxelShape voxelShape = VoxelShapes.cuboid(box);
        return StreamSupport.stream(new Spliterators.AbstractSpliterator<VoxelShape>(Long.MAX_VALUE, 1280){
            boolean field_19296;
            {
                super(l, i);
                this.field_19296 = entity == null;
            }

            @Override
            public boolean tryAdvance(Consumer<? super VoxelShape> consumer) {
                if (!this.field_19296) {
                    this.field_19296 = true;
                    VoxelShape voxelShape4 = CollisionView.this.getWorldBorder().asVoxelShape();
                    boolean bl = VoxelShapes.matchesAnywhere(voxelShape4, VoxelShapes.cuboid(entity.getBoundingBox().contract(1.0E-7)), BooleanBiFunction.AND);
                    boolean bl2 = VoxelShapes.matchesAnywhere(voxelShape4, VoxelShapes.cuboid(entity.getBoundingBox().expand(1.0E-7)), BooleanBiFunction.AND);
                    if (!bl && bl2) {
                        consumer.accept(voxelShape4);
                        return true;
                    }
                }
                while (cuboidBlockIterator.step()) {
                    VoxelShape voxelShape2;
                    VoxelShape voxelShape3;
                    int n;
                    int m;
                    BlockView blockView;
                    int i = cuboidBlockIterator.getX();
                    int j = cuboidBlockIterator.getY();
                    int k = cuboidBlockIterator.getZ();
                    int l = cuboidBlockIterator.getEdgeCoordinatesCount();
                    if (l == 3 || (blockView = CollisionView.this.getExistingChunk(m = i >> 4, n = k >> 4)) == null) continue;
                    mutable.set(i, j, k);
                    BlockState blockState = blockView.getBlockState(mutable);
                    if (l == 1 && !blockState.exceedsCube() || l == 2 && blockState.getBlock() != Blocks.MOVING_PISTON || !VoxelShapes.matchesAnywhere(voxelShape, voxelShape3 = (voxelShape2 = blockState.getCollisionShape(CollisionView.this, mutable, shapeContext)).offset(i, j, k), BooleanBiFunction.AND)) continue;
                    consumer.accept(voxelShape3);
                    return true;
                }
                return false;
            }
        }, false);
    }
}

