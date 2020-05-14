/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import com.google.common.collect.Streams;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Predicate;
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
        return this.doesNotCollide(null, box, entity -> true);
    }

    default public boolean doesNotCollide(Entity entity2) {
        return this.doesNotCollide(entity2, entity2.getBoundingBox(), entity -> true);
    }

    default public boolean doesNotCollide(Entity entity2, Box box) {
        return this.doesNotCollide(entity2, box, entity -> true);
    }

    default public boolean doesNotCollide(@Nullable Entity entity, Box box, Predicate<Entity> predicate) {
        return this.getCollisions(entity, box, predicate).allMatch(VoxelShape::isEmpty);
    }

    default public Stream<VoxelShape> getEntityCollisions(@Nullable Entity entity, Box box, Predicate<Entity> predicate) {
        return Stream.empty();
    }

    default public Stream<VoxelShape> getCollisions(@Nullable Entity entity, Box box, Predicate<Entity> predicate) {
        return Streams.concat(this.getBlockCollisions(entity, box), this.getEntityCollisions(entity, box, predicate));
    }

    default public Stream<VoxelShape> getBlockCollisions(final @Nullable Entity entity, final Box box) {
        int i = MathHelper.floor(box.minX - 1.0E-7) - 1;
        int j = MathHelper.floor(box.maxX + 1.0E-7) + 1;
        int k = MathHelper.floor(box.minY - 1.0E-7) - 1;
        int l = MathHelper.floor(box.maxY + 1.0E-7) + 1;
        int m = MathHelper.floor(box.minZ - 1.0E-7) - 1;
        int n = MathHelper.floor(box.maxZ + 1.0E-7) + 1;
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
                    boolean bl2;
                    this.field_19296 = true;
                    WorldBorder worldBorder = CollisionView.this.getWorldBorder();
                    boolean bl = CollisionView.method_27087(worldBorder, entity.getBoundingBox().contract(1.0E-7));
                    boolean bl3 = bl2 = bl && !CollisionView.method_27087(worldBorder, entity.getBoundingBox().expand(1.0E-7));
                    if (bl2) {
                        consumer.accept(worldBorder.asVoxelShape());
                        return true;
                    }
                }
                while (cuboidBlockIterator.step()) {
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
                    if (l == 1 && !blockState.exceedsCube() || l == 2 && !blockState.isOf(Blocks.MOVING_PISTON)) continue;
                    VoxelShape voxelShape3 = blockState.getCollisionShape(CollisionView.this, mutable, shapeContext);
                    if (voxelShape3 == VoxelShapes.fullCube()) {
                        if (!box.intersects(i, j, k, (double)i + 1.0, (double)j + 1.0, (double)k + 1.0)) continue;
                        consumer.accept(voxelShape3.offset(i, j, k));
                        return true;
                    }
                    VoxelShape voxelShape2 = voxelShape3.offset(i, j, k);
                    if (!VoxelShapes.matchesAnywhere(voxelShape2, voxelShape, BooleanBiFunction.AND)) continue;
                    consumer.accept(voxelShape2);
                    return true;
                }
                return false;
            }
        }, false);
    }

    public static boolean method_27087(WorldBorder worldBorder, Box box) {
        double d = MathHelper.floor(worldBorder.getBoundWest());
        double e = MathHelper.floor(worldBorder.getBoundNorth());
        double f = MathHelper.ceil(worldBorder.getBoundEast());
        double g = MathHelper.ceil(worldBorder.getBoundSouth());
        return box.minX > d && box.minX < f && box.minZ > e && box.minZ < g && box.maxX > d && box.maxX < f && box.maxZ > e && box.maxZ < g;
    }
}

