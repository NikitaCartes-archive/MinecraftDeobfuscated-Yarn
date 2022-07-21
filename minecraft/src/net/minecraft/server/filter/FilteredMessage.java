package net.minecraft.server.filter;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Util;

/**
 * A message from the {@link TextFilterer}.
 */
public record FilteredMessage<T>(T raw, @Nullable T filtered) {
	public static final FilteredMessage<String> EMPTY = permitted("");

	/**
	 * {@return the message with nothing filtered}
	 */
	public static <T> FilteredMessage<T> permitted(T filterable) {
		return new FilteredMessage<>(filterable, filterable);
	}

	/**
	 * {@return the message with everything filtered}
	 */
	public static <T> FilteredMessage<T> censored(T filterable) {
		return new FilteredMessage<>(filterable, null);
	}

	public <U> FilteredMessage<U> map(Function<T, U> mapper) {
		return this.map(mapper, mapper);
	}

	public <U> FilteredMessage<U> map(Function<T, U> rawMapper, Function<T, U> filteredMapper) {
		return (FilteredMessage<U>)(new FilteredMessage<>(rawMapper.apply(this.raw), Util.map(this.filtered, filteredMapper)));
	}

	public <U> FilteredMessage<U> method_45000(U object, Function<T, U> function) {
		return !this.isFiltered() ? permitted(object) : new FilteredMessage<>(object, Util.map(this.filtered, function));
	}

	public <U> CompletableFuture<FilteredMessage<U>> method_45001(U object, Function<T, CompletableFuture<U>> function) {
		if (this.filtered() == null) {
			return CompletableFuture.completedFuture(censored(object));
		} else {
			return !this.isFiltered()
				? CompletableFuture.completedFuture(permitted(object))
				: ((CompletableFuture)function.apply(this.filtered())).thenApply(object2 -> new FilteredMessage<>(object, (U)object2));
		}
	}

	/**
	 * {@return if some of the messages are filtered}
	 */
	public boolean isFiltered() {
		return !this.raw.equals(this.filtered);
	}

	/**
	 * {@return if all of the messages are filtered, e.g. by using {@link #censored}}
	 */
	public boolean isCensored() {
		return this.filtered == null;
	}

	public T filteredOrElse(T filterable) {
		return this.filtered != null ? this.filtered : filterable;
	}

	@Nullable
	public T getFilterableFor(ServerPlayerEntity sender, ServerPlayerEntity receiver) {
		return sender.shouldFilterMessagesSentTo(receiver) ? this.filtered : this.raw;
	}

	@Nullable
	public T getFilterableFor(ServerCommandSource source, ServerPlayerEntity receiver) {
		ServerPlayerEntity serverPlayerEntity = source.getPlayer();
		return serverPlayerEntity != null ? this.getFilterableFor(serverPlayerEntity, receiver) : this.raw;
	}

	/**
	 * {@return {@link #filtered} if {@code filtered} is {@code true}, otherwise
	 * {@link #raw}}
	 */
	@Nullable
	public T get(boolean filtered) {
		return filtered ? this.filtered : this.raw;
	}
}
