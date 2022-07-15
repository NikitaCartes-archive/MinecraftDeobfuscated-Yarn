package net.minecraft.server.filter;

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

	/**
	 * {@return the result of applying mappers to both raw and filtered parts}
	 * 
	 * <p>Unlike {@link #map(Function, Function)}, if those two parts are equal,
	 * then this reuses the mapped raw part instead of applying {@code filteredMapper}.
	 */
	public <U> FilteredMessage<U> mapParts(Function<T, U> rawMapper, Function<T, U> filteredMapper) {
		U object = (U)rawMapper.apply(this.raw);
		return this.raw.equals(this.filtered) ? permitted(object) : new FilteredMessage<>(object, Util.map(this.filtered, filteredMapper));
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
