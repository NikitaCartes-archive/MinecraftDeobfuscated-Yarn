package net.minecraft.text;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.server.filter.FilteredMessage;

public record RawFilteredPair<T>(T raw, Optional<T> filtered) {
	public static <T> Codec<RawFilteredPair<T>> createCodec(Codec<T> baseCodec) {
		Codec<RawFilteredPair<T>> codec = RecordCodecBuilder.create(
			instance -> instance.group(
						baseCodec.fieldOf("raw").forGetter(RawFilteredPair::raw), baseCodec.optionalFieldOf("filtered").forGetter(RawFilteredPair::filtered)
					)
					.apply(instance, RawFilteredPair::new)
		);
		Codec<RawFilteredPair<T>> codec2 = baseCodec.xmap(RawFilteredPair::of, RawFilteredPair::raw);
		return Codec.withAlternative(codec, codec2);
	}

	public static <B extends ByteBuf, T> PacketCodec<B, RawFilteredPair<T>> createPacketCodec(PacketCodec<B, T> basePacketCodec) {
		return PacketCodec.tuple(
			basePacketCodec, RawFilteredPair::raw, basePacketCodec.collect(PacketCodecs::optional), RawFilteredPair::filtered, RawFilteredPair::new
		);
	}

	public static <T> RawFilteredPair<T> of(T raw) {
		return new RawFilteredPair<>(raw, Optional.empty());
	}

	public static RawFilteredPair<String> of(FilteredMessage message) {
		return new RawFilteredPair<>(message.raw(), message.isFiltered() ? Optional.of(message.getString()) : Optional.empty());
	}

	public T get(boolean shouldFilter) {
		return (T)(shouldFilter ? this.filtered.orElse(this.raw) : this.raw);
	}

	public <U> RawFilteredPair<U> map(Function<T, U> mapper) {
		return (RawFilteredPair<U>)(new RawFilteredPair<>(mapper.apply(this.raw), this.filtered.map(mapper)));
	}

	public <U> Optional<RawFilteredPair<U>> resolve(Function<T, Optional<U>> resolver) {
		Optional<U> optional = (Optional<U>)resolver.apply(this.raw);
		if (optional.isEmpty()) {
			return Optional.empty();
		} else if (this.filtered.isPresent()) {
			Optional<U> optional2 = (Optional<U>)resolver.apply(this.filtered.get());
			return optional2.isEmpty() ? Optional.empty() : Optional.of(new RawFilteredPair<>(optional.get(), optional2));
		} else {
			return Optional.of(new RawFilteredPair<>(optional.get(), Optional.empty()));
		}
	}
}
