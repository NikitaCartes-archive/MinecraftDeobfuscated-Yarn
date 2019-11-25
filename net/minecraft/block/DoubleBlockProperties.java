/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import java.util.function.BiPredicate;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;

public class DoubleBlockProperties {
    public static <S extends BlockEntity> PropertySource<S> toPropertySource(BlockEntityType<S> blockEntityType, Function<BlockState, Type> function, Function<BlockState, Direction> function2, DirectionProperty directionProperty, BlockState blockState, IWorld iWorld, BlockPos blockPos, BiPredicate<IWorld, BlockPos> biPredicate) {
        Type type2;
        boolean bl2;
        S blockEntity = blockEntityType.get(iWorld, blockPos);
        if (blockEntity == null) {
            return PropertyRetriever::getFallback;
        }
        if (biPredicate.test(iWorld, blockPos)) {
            return PropertyRetriever::getFallback;
        }
        Type type = function.apply(blockState);
        boolean bl = type == Type.SINGLE;
        boolean bl3 = bl2 = type == Type.FIRST;
        if (bl) {
            return new PropertySource.Single<S>(blockEntity);
        }
        BlockPos blockPos2 = blockPos.offset(function2.apply(blockState));
        BlockState blockState2 = iWorld.getBlockState(blockPos2);
        if (blockState2.getBlock() == blockState.getBlock() && (type2 = function.apply(blockState2)) != Type.SINGLE && type != type2 && blockState2.get(directionProperty) == blockState.get(directionProperty)) {
            if (biPredicate.test(iWorld, blockPos2)) {
                return PropertyRetriever::getFallback;
            }
            S blockEntity2 = blockEntityType.get(iWorld, blockPos2);
            if (blockEntity2 != null) {
                S blockEntity3 = bl2 ? blockEntity : blockEntity2;
                S blockEntity4 = bl2 ? blockEntity2 : blockEntity;
                return new PropertySource.Pair<S>(blockEntity3, blockEntity4);
            }
        }
        return new PropertySource.Single<S>(blockEntity);
    }

    public static interface PropertySource<S> {
        public <T> T apply(PropertyRetriever<? super S, T> var1);

        public static final class Single<S>
        implements PropertySource<S> {
            private final S single;

            public Single(S object) {
                this.single = object;
            }

            @Override
            public <T> T apply(PropertyRetriever<? super S, T> propertyRetriever) {
                return propertyRetriever.getFrom(this.single);
            }
        }

        public static final class Pair<S>
        implements PropertySource<S> {
            private final S first;
            private final S second;

            public Pair(S object, S object2) {
                this.first = object;
                this.second = object2;
            }

            @Override
            public <T> T apply(PropertyRetriever<? super S, T> propertyRetriever) {
                return propertyRetriever.getFromBoth(this.first, this.second);
            }
        }
    }

    public static interface PropertyRetriever<S, T> {
        public T getFromBoth(S var1, S var2);

        public T getFrom(S var1);

        public T getFallback();
    }

    public static enum Type {
        SINGLE,
        FIRST,
        SECOND;

    }
}

