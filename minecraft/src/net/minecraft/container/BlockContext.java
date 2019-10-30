package net.minecraft.container;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface BlockContext {
	BlockContext EMPTY = new BlockContext() {
		@Override
		public <T> Optional<T> run(BiFunction<World, BlockPos, T> function) {
			return Optional.empty();
		}
	};

	static BlockContext create(World world2, BlockPos world) {
		return new BlockContext() {
			@Override
			public <T> Optional<T> run(BiFunction<World, BlockPos, T> function) {
				return Optional.of(function.apply(world2, world));
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
