package net.minecraft.container;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ContainerWorldContext {
	ContainerWorldContext NO_OP_CONTEXT = new ContainerWorldContext() {
		@Override
		public <T> Optional<T> apply(BiFunction<World, BlockPos, T> biFunction) {
			return Optional.empty();
		}
	};

	static ContainerWorldContext create(World world, BlockPos blockPos) {
		return new ContainerWorldContext() {
			@Override
			public <T> Optional<T> apply(BiFunction<World, BlockPos, T> biFunction) {
				return Optional.of(biFunction.apply(world, blockPos));
			}
		};
	}

	<T> Optional<T> apply(BiFunction<World, BlockPos, T> biFunction);

	default <T> T apply(BiFunction<World, BlockPos, T> biFunction, T object) {
		return (T)this.apply(biFunction).orElse(object);
	}

	default void run(BiConsumer<World, BlockPos> biConsumer) {
		this.apply((world, blockPos) -> {
			biConsumer.accept(world, blockPos);
			return Optional.empty();
		});
	}
}
