package net.minecraft.screen;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * A screen handler context allows running code on the server side only. Screen
 * handlers are designed to be used on both sides; any action modifying the world has
 * to be wrapped in a call to the context. This guarantees that no casting error occurs
 * inside the screen handler code.
 * 
 * <p>A context with the world is passed to the screen handler on creation on the server.
 * On the server, the context executes the function with the world and the position.
 * On the client, the {@linkplain #EMPTY empty context} is used.
 */
public interface ScreenHandlerContext {
	/**
	 * The dummy screen handler context for clientside screen handlers.
	 */
	ScreenHandlerContext EMPTY = new ScreenHandlerContext() {
		@Override
		public <T> Optional<T> get(BiFunction<World, BlockPos, T> getter) {
			return Optional.empty();
		}
	};

	/**
	 * Returns an active screen handler context. Used on the logical server.
	 */
	static ScreenHandlerContext create(World world, BlockPos pos) {
		return new ScreenHandlerContext() {
			@Override
			public <T> Optional<T> get(BiFunction<World, BlockPos, T> getter) {
				return Optional.of(getter.apply(world, pos));
			}
		};
	}

	/**
	 * Gets an optional value from this context's world and position
	 * with a {@link BiFunction} getter.
	 * 
	 * @return a present {@link Optional} with the getter's return value,
	 *         or {@link Optional#empty()} if this context is empty
	 * 
	 * @param getter a function that gets a non-null value from this context's world and position
	 */
	<T> Optional<T> get(BiFunction<World, BlockPos, T> getter);

	/**
	 * Gets a value from this context's world and position
	 * with a {@link BiFunction} getter.
	 * 
	 * @return the getter's return value if this context is active,
	 *         the default value otherwise
	 * 
	 * @param defaultValue a fallback default value, used if this context is empty
	 * @param getter a function that gets a non-null value from this context's world and position
	 */
	default <T> T get(BiFunction<World, BlockPos, T> getter, T defaultValue) {
		return (T)this.get(getter).orElse(defaultValue);
	}

	/**
	 * Runs a {@link BiConsumer} with this context's world and position
	 * if this context is active.
	 */
	default void run(BiConsumer<World, BlockPos> function) {
		this.get((world, pos) -> {
			function.accept(world, pos);
			return Optional.empty();
		});
	}
}
