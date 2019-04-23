/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.container;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface BlockContext {
    public static final BlockContext EMPTY = new BlockContext(){

        @Override
        public <T> Optional<T> run(BiFunction<World, BlockPos, T> biFunction) {
            return Optional.empty();
        }
    };

    public static BlockContext create(final World world, final BlockPos blockPos) {
        return new BlockContext(){

            @Override
            public <T> Optional<T> run(BiFunction<World, BlockPos, T> biFunction) {
                return Optional.of(biFunction.apply(world, blockPos));
            }
        };
    }

    public <T> Optional<T> run(BiFunction<World, BlockPos, T> var1);

    default public <T> T run(BiFunction<World, BlockPos, T> biFunction, T object) {
        return this.run(biFunction).orElse(object);
    }

    default public void run(BiConsumer<World, BlockPos> biConsumer) {
        this.run((World world, BlockPos blockPos) -> {
            biConsumer.accept((World)world, (BlockPos)blockPos);
            return Optional.empty();
        });
    }
}

