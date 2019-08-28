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
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.CuboidBlockIterator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.border.WorldBorder;
import org.jetbrains.annotations.Nullable;

public interface ViewableWorld
extends BlockView {
    public WorldBorder getWorldBorder();

    @Nullable
    public BlockView method_22338(int var1, int var2);

    default public boolean intersectsEntities(@Nullable Entity entity, VoxelShape voxelShape) {
        return true;
    }

    default public boolean canPlace(BlockState blockState, BlockPos blockPos, EntityContext entityContext) {
        VoxelShape voxelShape = blockState.getCollisionShape(this, blockPos, entityContext);
        return voxelShape.isEmpty() || this.intersectsEntities(null, voxelShape.offset(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
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

    default public boolean doesNotCollide(@Nullable Entity entity, Box box, Set<Entity> set) {
        return this.getCollisionShapes(entity, box, set).allMatch(VoxelShape::isEmpty);
    }

    default public Stream<VoxelShape> method_20743(@Nullable Entity entity, Box box, Set<Entity> set) {
        return Stream.empty();
    }

    default public Stream<VoxelShape> getCollisionShapes(@Nullable Entity entity, Box box, Set<Entity> set) {
        return Streams.concat(this.method_20812(entity, box), this.method_20743(entity, box, set));
    }

    default public Stream<VoxelShape> method_20812(final @Nullable Entity entity, Box box) {
        int i = MathHelper.floor(box.minX - 1.0E-7) - 1;
        int j = MathHelper.floor(box.maxX + 1.0E-7) + 1;
        int k = MathHelper.floor(box.minY - 1.0E-7) - 1;
        int l = MathHelper.floor(box.maxY + 1.0E-7) + 1;
        int m = MathHelper.floor(box.minZ - 1.0E-7) - 1;
        int n = MathHelper.floor(box.maxZ + 1.0E-7) + 1;
        final EntityContext entityContext = entity == null ? EntityContext.absent() : EntityContext.of(entity);
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
                    VoxelShape voxelShape4 = ViewableWorld.this.getWorldBorder().asVoxelShape();
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
                    int l = cuboidBlockIterator.method_20789();
                    if (l == 3 || (blockView = ViewableWorld.this.method_22338(m = i >> 4, n = k >> 4)) == null) continue;
                    mutable.set(i, j, k);
                    BlockState blockState = blockView.getBlockState(mutable);
                    if (l == 1 && !blockState.method_17900() || l == 2 && blockState.getBlock() != Blocks.MOVING_PISTON || !VoxelShapes.matchesAnywhere(voxelShape, voxelShape3 = (voxelShape2 = blockState.getCollisionShape(ViewableWorld.this, mutable, entityContext)).offset(i, j, k), BooleanBiFunction.AND)) continue;
                    consumer.accept(voxelShape3);
                    return true;
                }
                return false;
            }
        }, false);
    }
}

