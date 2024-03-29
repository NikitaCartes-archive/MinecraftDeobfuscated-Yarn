package net.minecraft.util.collection;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.dynamic.Codecs;
import org.slf4j.Logger;

public interface ListOperation {
	static MapCodec<ListOperation> createCodec(int maxSize) {
		return Codecs.validate(ListOperation.Mode.CODEC.dispatchMap("mode", ListOperation::getMode, mode -> mode.codec.codec()), operation -> {
			if (operation instanceof ListOperation.ReplaceSection replaceSection && replaceSection.size().isPresent()) {
				int j = replaceSection.size().get();
				if (j > maxSize) {
					return DataResult.error(() -> "Size value too large: " + j + ", max size is " + maxSize);
				}
			}

			return DataResult.success(operation);
		});
	}

	ListOperation.Mode getMode();

	<T> List<T> apply(List<T> current, List<T> values, int maxSize);

	public static class Append implements ListOperation {
		private static final Logger LOGGER = LogUtils.getLogger();
		public static final ListOperation.Append INSTANCE = new ListOperation.Append();
		public static final MapCodec<ListOperation.Append> CODEC = MapCodec.unit((Supplier<ListOperation.Append>)(() -> INSTANCE));

		private Append() {
		}

		@Override
		public ListOperation.Mode getMode() {
			return ListOperation.Mode.APPEND;
		}

		@Override
		public <T> List<T> apply(List<T> current, List<T> values, int maxSize) {
			if (current.size() + values.size() > maxSize) {
				LOGGER.error("Contents overflow in section append");
				return current;
			} else {
				return Stream.concat(current.stream(), values.stream()).toList();
			}
		}
	}

	public static record Insert(int offset) implements ListOperation {
		private static final Logger LOGGER = LogUtils.getLogger();
		public static final MapCodec<ListOperation.Insert> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(Codecs.createStrictOptionalFieldCodec(Codecs.NONNEGATIVE_INT, "offset", 0).forGetter(ListOperation.Insert::offset))
					.apply(instance, ListOperation.Insert::new)
		);

		@Override
		public ListOperation.Mode getMode() {
			return ListOperation.Mode.INSERT;
		}

		@Override
		public <T> List<T> apply(List<T> current, List<T> values, int maxSize) {
			int i = current.size();
			if (this.offset > i) {
				LOGGER.error("Cannot insert when offset is out of bounds");
				return current;
			} else if (i + values.size() > maxSize) {
				LOGGER.error("Contents overflow in section insertion");
				return current;
			} else {
				Builder<T> builder = ImmutableList.builder();
				builder.addAll(current.subList(0, this.offset));
				builder.addAll(values);
				builder.addAll(current.subList(this.offset, i));
				return builder.build();
			}
		}
	}

	public static enum Mode implements StringIdentifiable {
		REPLACE_ALL("replace_all", ListOperation.ReplaceAll.CODEC),
		REPLACE_SECTION("replace_section", ListOperation.ReplaceSection.CODEC),
		INSERT("insert", ListOperation.Insert.CODEC),
		APPEND("append", ListOperation.Append.CODEC);

		public static final Codec<ListOperation.Mode> CODEC = StringIdentifiable.createCodec(ListOperation.Mode::values);
		private final String id;
		final MapCodec<? extends ListOperation> codec;

		private Mode(String id, MapCodec<? extends ListOperation> codec) {
			this.id = id;
			this.codec = codec;
		}

		public MapCodec<? extends ListOperation> getCodec() {
			return this.codec;
		}

		@Override
		public String asString() {
			return this.id;
		}
	}

	public static class ReplaceAll implements ListOperation {
		public static final ListOperation.ReplaceAll INSTANCE = new ListOperation.ReplaceAll();
		public static final MapCodec<ListOperation.ReplaceAll> CODEC = MapCodec.unit((Supplier<ListOperation.ReplaceAll>)(() -> INSTANCE));

		private ReplaceAll() {
		}

		@Override
		public ListOperation.Mode getMode() {
			return ListOperation.Mode.REPLACE_ALL;
		}

		@Override
		public <T> List<T> apply(List<T> current, List<T> values, int maxSize) {
			return values;
		}
	}

	public static record ReplaceSection(int offset, Optional<Integer> size) implements ListOperation {
		private static final Logger LOGGER = LogUtils.getLogger();
		public static final MapCodec<ListOperation.ReplaceSection> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
						Codecs.createStrictOptionalFieldCodec(Codecs.NONNEGATIVE_INT, "offset", 0).forGetter(ListOperation.ReplaceSection::offset),
						Codecs.createStrictOptionalFieldCodec(Codecs.NONNEGATIVE_INT, "size").forGetter(ListOperation.ReplaceSection::size)
					)
					.apply(instance, ListOperation.ReplaceSection::new)
		);

		public ReplaceSection(int offset) {
			this(offset, Optional.empty());
		}

		@Override
		public ListOperation.Mode getMode() {
			return ListOperation.Mode.REPLACE_SECTION;
		}

		@Override
		public <T> List<T> apply(List<T> current, List<T> values, int maxSize) {
			int i = current.size();
			if (this.offset > i) {
				LOGGER.error("Cannot replace when offset is out of bounds");
				return current;
			} else {
				Builder<T> builder = ImmutableList.builder();
				builder.addAll(current.subList(0, this.offset));
				builder.addAll(values);
				int j = this.offset + this.size.orElse(values.size());
				if (j < i) {
					builder.addAll(current.subList(j, i));
				}

				List<T> list = builder.build();
				if (list.size() > maxSize) {
					LOGGER.error("Contents overflow in section replacement");
					return current;
				} else {
					return list;
				}
			}
		}
	}
}
