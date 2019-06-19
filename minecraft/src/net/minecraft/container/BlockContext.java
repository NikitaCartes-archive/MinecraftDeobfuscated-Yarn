package net.minecraft.container;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface BlockContext {
	BlockContext EMPTY = new BlockContext() {
		@Override
		public <T> Optional<T> run(BiFunction<World, BlockPos, T> biFunction) {
			return Optional.empty();
		}
	};

	static BlockContext create(World world, BlockPos blockPos) {
		return new BlockContext() {
			@Override
			public <T> Optional<T> run(BiFunction<World, BlockPos, T> biFunction) {
				return Optional.of(biFunction.apply(world, blockPos));
			}
		};
	}

	<T> Optional<T> run(BiFunction<World, BlockPos, T> biFunction);

	default <T> T run(BiFunction<World, BlockPos, T> biFunction, T object) {
		return (T)this.run(biFunction).orElse(object);
	}

	default void run(BiConsumer<World, BlockPos> biConsumer) {
		this.run((BiFunction)((world, blockPos) -> {
			biConsumer.accept(world, blockPos);
			return Optional.empty();
		}));
	}
}
