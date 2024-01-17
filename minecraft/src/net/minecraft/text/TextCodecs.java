package net.minecraft.text;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapDecoder;
import com.mojang.serialization.MapEncoder;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.dynamic.Codecs;

public class TextCodecs {
	public static final Codec<Text> CODEC = Codecs.createRecursive("Component", TextCodecs::createCodec);
	public static final PacketCodec<ByteBuf, Text> PACKET_CODEC = PacketCodecs.ofCodec(CODEC);
	public static final Codec<Text> STRINGIFIED_CODEC = Codecs.STRINGIFIED_TEXT
		.flatXmap(json -> CODEC.parse(JsonOps.INSTANCE, json), text -> CODEC.encodeStart(JsonOps.INSTANCE, text));

	private static MutableText combine(List<Text> texts) {
		MutableText mutableText = ((Text)texts.get(0)).copy();

		for (int i = 1; i < texts.size(); i++) {
			mutableText.append((Text)texts.get(i));
		}

		return mutableText;
	}

	public static <T extends StringIdentifiable, E> MapCodec<E> dispatchingCodec(
		T[] types, Function<T, MapCodec<? extends E>> typeToCodec, Function<E, T> valueToType, String dispatchingKey
	) {
		MapCodec<E> mapCodec = new TextCodecs.FuzzyCodec(
			Stream.of(types).map(typeToCodec).toList(), object -> (MapEncoder)typeToCodec.apply((StringIdentifiable)valueToType.apply(object))
		);
		Codec<T> codec = StringIdentifiable.createBasicCodec(() -> types);
		MapCodec<E> mapCodec2 = codec.dispatchMap(dispatchingKey, valueToType, type -> ((MapCodec)typeToCodec.apply(type)).codec());
		MapCodec<E> mapCodec3 = new TextCodecs.DispatchingCodec<>(dispatchingKey, mapCodec2, mapCodec);
		return Codecs.orCompressed(mapCodec3, mapCodec2);
	}

	private static Codec<Text> createCodec(Codec<Text> selfCodec) {
		TextContent.Type<?>[] types = new TextContent.Type[]{
			PlainTextContent.TYPE, TranslatableTextContent.TYPE, KeybindTextContent.TYPE, ScoreTextContent.TYPE, SelectorTextContent.TYPE, NbtTextContent.TYPE
		};
		MapCodec<TextContent> mapCodec = dispatchingCodec(types, TextContent.Type::codec, TextContent::getType, "type");
		Codec<Text> codec = RecordCodecBuilder.create(
			instance -> instance.group(
						mapCodec.forGetter(Text::getContent),
						Codecs.createStrictOptionalFieldCodec(Codecs.nonEmptyList(selfCodec.listOf()), "extra", List.of()).forGetter(Text::getSiblings),
						Style.Codecs.MAP_CODEC.forGetter(Text::getStyle)
					)
					.apply(instance, MutableText::new)
		);
		return Codec.either(Codec.either(Codec.STRING, Codecs.nonEmptyList(selfCodec.listOf())), codec)
			.xmap(either -> either.map(either2 -> either2.map(Text::literal, TextCodecs::combine), text -> text), text -> {
				String string = text.getLiteralString();
				return string != null ? Either.left(Either.left(string)) : Either.right(text);
			});
	}

	static class DispatchingCodec<T> extends MapCodec<T> {
		private final String dispatchingKey;
		private final MapCodec<T> withKeyCodec;
		private final MapCodec<T> withoutKeyCodec;

		public DispatchingCodec(String dispatchingKey, MapCodec<T> withKeyCodec, MapCodec<T> withoutKeyCodec) {
			this.dispatchingKey = dispatchingKey;
			this.withKeyCodec = withKeyCodec;
			this.withoutKeyCodec = withoutKeyCodec;
		}

		@Override
		public <O> DataResult<T> decode(DynamicOps<O> ops, MapLike<O> input) {
			return input.get(this.dispatchingKey) != null ? this.withKeyCodec.decode(ops, input) : this.withoutKeyCodec.decode(ops, input);
		}

		@Override
		public <O> RecordBuilder<O> encode(T input, DynamicOps<O> ops, RecordBuilder<O> prefix) {
			return this.withoutKeyCodec.encode(input, ops, prefix);
		}

		@Override
		public <T1> Stream<T1> keys(DynamicOps<T1> ops) {
			return Stream.concat(this.withKeyCodec.keys(ops), this.withoutKeyCodec.keys(ops)).distinct();
		}
	}

	static class FuzzyCodec<T> extends MapCodec<T> {
		private final List<MapCodec<? extends T>> codecs;
		private final Function<T, MapEncoder<? extends T>> codecGetter;

		public FuzzyCodec(List<MapCodec<? extends T>> codecs, Function<T, MapEncoder<? extends T>> codecGetter) {
			this.codecs = codecs;
			this.codecGetter = codecGetter;
		}

		@Override
		public <S> DataResult<T> decode(DynamicOps<S> ops, MapLike<S> input) {
			for (MapDecoder<? extends T> mapDecoder : this.codecs) {
				DataResult<? extends T> dataResult = mapDecoder.decode(ops, input);
				if (dataResult.result().isPresent()) {
					return (DataResult<T>)dataResult;
				}
			}

			return DataResult.error(() -> "No matching codec found");
		}

		@Override
		public <S> RecordBuilder<S> encode(T input, DynamicOps<S> ops, RecordBuilder<S> prefix) {
			MapEncoder<T> mapEncoder = (MapEncoder<T>)this.codecGetter.apply(input);
			return mapEncoder.encode(input, ops, prefix);
		}

		@Override
		public <S> Stream<S> keys(DynamicOps<S> ops) {
			return this.codecs.stream().flatMap(codec -> codec.keys(ops)).distinct();
		}

		public String toString() {
			return "FuzzyCodec[" + this.codecs + "]";
		}
	}
}
