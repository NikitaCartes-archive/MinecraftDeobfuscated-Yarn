package net.minecraft.container;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Block contexts allow to get a value using an optionally present {@link World} and {@link BlockPos}.
 */
public interface BlockContext {
	BlockContext EMPTY = new BlockContext() {
		@Override
		public <T> Optional<T> run(BiFunction<World, BlockPos, T> function) {
			return Optional.empty();
		}
	};

	static BlockContext create(World world, BlockPos pos) {
		return new BlockContext() {
			@Override
			public <T> Optional<T> run(BiFunction<World, BlockPos, T> function) {
				return Optional.of(function.apply(world, pos));
			}
		};
	}

	<T> Optional<T> run(BiFunction<World, BlockPos, T> function);

	default <T> T run(BiFunction<World, BlockPos, T> function, T defaultValue) {
		return (T)this.run(function).orElse(defaultValue);
	}

	default void run(BiConsumer<World, BlockPos> function) {
		this.run((BiFunction)((world, blockPos) -> {
			function.accept(world, blockPos);
			return Optional.empty();
		}));
	}
}
