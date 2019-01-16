package net.minecraft;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface class_3914 {
	class_3914 field_17304 = new class_3914() {
		@Override
		public <T> Optional<T> method_17395(BiFunction<World, BlockPos, T> biFunction) {
			return Optional.empty();
		}
	};

	static class_3914 method_17392(World world, BlockPos blockPos) {
		return new class_3914() {
			@Override
			public <T> Optional<T> method_17395(BiFunction<World, BlockPos, T> biFunction) {
				return Optional.of(biFunction.apply(world, blockPos));
			}
		};
	}

	<T> Optional<T> method_17395(BiFunction<World, BlockPos, T> biFunction);

	default <T> T method_17396(BiFunction<World, BlockPos, T> biFunction, T object) {
		return (T)this.method_17395(biFunction).orElse(object);
	}

	default void method_17393(BiConsumer<World, BlockPos> biConsumer) {
		this.method_17395((world, blockPos) -> {
			biConsumer.accept(world, blockPos);
			return Optional.empty();
		});
	}
}
