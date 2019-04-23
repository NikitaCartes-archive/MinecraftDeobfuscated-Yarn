/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class RayTraceContext {
    private final Vec3d start;
    private final Vec3d end;
    private final ShapeType shapeType;
    private final FluidHandling fluid;
    private final EntityContext entityPosition;

    public RayTraceContext(Vec3d vec3d, Vec3d vec3d2, ShapeType shapeType, FluidHandling fluidHandling, Entity entity) {
        this.start = vec3d;
        this.end = vec3d2;
        this.shapeType = shapeType;
        this.fluid = fluidHandling;
        this.entityPosition = EntityContext.of(entity);
    }

    public Vec3d getEnd() {
        return this.end;
    }

    public Vec3d getStart() {
        return this.start;
    }

    public VoxelShape getBlockShape(BlockState blockState, BlockView blockView, BlockPos blockPos) {
        return this.shapeType.get(blockState, blockView, blockPos, this.entityPosition);
    }

    public VoxelShape getFluidShape(FluidState fluidState, BlockView blockView, BlockPos blockPos) {
        return this.fluid.handled(fluidState) ? fluidState.getShape(blockView, blockPos) : VoxelShapes.empty();
    }

    public static enum FluidHandling {
        NONE(fluidState -> false),
        SOURCE_ONLY(FluidState::isStill),
        ANY(fluidState -> !fluidState.isEmpty());

        private final Predicate<FluidState> predicate;

        private FluidHandling(Predicate<FluidState> predicate) {
            this.predicate = predicate;
        }

        public boolean handled(FluidState fluidState) {
            return this.predicate.test(fluidState);
        }
    }

    public static interface ShapeProvider {
        public VoxelShape get(BlockState var1, BlockView var2, BlockPos var3, EntityContext var4);
    }

    public static enum ShapeType implements ShapeProvider
    {
        COLLIDER(BlockState::getCollisionShape),
        OUTLINE(BlockState::getOutlineShape);

        private final ShapeProvider provider;

        private ShapeType(ShapeProvider shapeProvider) {
            this.provider = shapeProvider;
        }

        @Override
        public VoxelShape get(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
            return this.provider.get(blockState, blockView, blockPos, entityContext);
        }
    }
}

