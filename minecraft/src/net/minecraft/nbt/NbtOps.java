package net.minecraft.nbt;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.PeekingIterator;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.RecordBuilder.AbstractStringBuilder;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;

/**
 * Used to handle Minecraft NBTs within {@link com.mojang.serialization.Dynamic
 * dynamics} for DataFixerUpper, allowing generalized serialization logic
 * shared across different type of data structures. Use {@link NbtOps#INSTANCE}
 * for the ops singleton.
 * 
 * <p>For instance, dimension data may be stored as JSON in data packs, but
 * they will be transported in packets as NBT. DataFixerUpper allows
 * generalizing the dimension serialization logic to prevent duplicate code,
 * where the NBT ops allow the DataFixerUpper dimension serialization logic
 * to interact with Minecraft NBTs.</p>
 * 
 * @see NbtOps#INSTANCE
 */
public class NbtOps implements DynamicOps<Tag> {
	/**
	 * An singleton of the NBT dynamic ops.
	 * 
	 * <p>This ops does not compress maps (replace field name to value pairs
	 * with an ordered list of values in serialization). In fact, since
	 * Minecraft NBT lists can only contain elements of the same type, this op
	 * cannot compress maps.</p>
	 */
	public static final NbtOps INSTANCE = new NbtOps();

	protected NbtOps() {
	}

	public Tag method_10668() {
		return EndTag.INSTANCE;
	}

	public <U> U method_29146(DynamicOps<U> dynamicOps, Tag tag) {
		switch (tag.getType()) {
			case 0:
				return dynamicOps.empty();
			case 1:
				return dynamicOps.createByte(((AbstractNumberTag)tag).getByte());
			case 2:
				return dynamicOps.createShort(((AbstractNumberTag)tag).getShort());
			case 3:
				return dynamicOps.createInt(((AbstractNumberTag)tag).getInt());
			case 4:
				return dynamicOps.createLong(((AbstractNumberTag)tag).getLong());
			case 5:
				return dynamicOps.createFloat(((AbstractNumberTag)tag).getFloat());
			case 6:
				return dynamicOps.createDouble(((AbstractNumberTag)tag).getDouble());
			case 7:
				return dynamicOps.createByteList(ByteBuffer.wrap(((ByteArrayTag)tag).getByteArray()));
			case 8:
				return dynamicOps.createString(tag.asString());
			case 9:
				return this.convertList(dynamicOps, tag);
			case 10:
				return this.convertMap(dynamicOps, tag);
			case 11:
				return dynamicOps.createIntList(Arrays.stream(((IntArrayTag)tag).getIntArray()));
			case 12:
				return dynamicOps.createLongList(Arrays.stream(((LongArrayTag)tag).getLongArray()));
			default:
				throw new IllegalStateException("Unknown tag type: " + tag);
		}
	}

	public DataResult<Number> method_10645(Tag tag) {
		return tag instanceof AbstractNumberTag ? DataResult.success(((AbstractNumberTag)tag).getNumber()) : DataResult.error("Not a number");
	}

	public Tag method_10660(Number number) {
		return DoubleTag.of(number.doubleValue());
	}

	public Tag method_10640(byte b) {
		return ByteTag.of(b);
	}

	public Tag method_10635(short s) {
		return ShortTag.of(s);
	}

	public Tag method_10661(int i) {
		return IntTag.of(i);
	}

	public Tag method_10654(long l) {
		return LongTag.of(l);
	}

	public Tag method_10662(float f) {
		return FloatTag.of(f);
	}

	public Tag method_10652(double d) {
		return DoubleTag.of(d);
	}

	public Tag method_23253(boolean bl) {
		return ByteTag.of(bl);
	}

	public DataResult<String> method_10656(Tag tag) {
		return tag instanceof StringTag ? DataResult.success(tag.asString()) : DataResult.error("Not a string");
	}

	public Tag method_10639(String string) {
		return StringTag.of(string);
	}

	private static AbstractListTag<?> method_29144(byte b, byte c) {
		if (method_29145(b, c, (byte)4)) {
			return new LongArrayTag(new long[0]);
		} else if (method_29145(b, c, (byte)1)) {
			return new ByteArrayTag(new byte[0]);
		} else {
			return (AbstractListTag<?>)(method_29145(b, c, (byte)3) ? new IntArrayTag(new int[0]) : new ListTag());
		}
	}

	private static boolean method_29145(byte b, byte c, byte d) {
		return b == d && (c == d || c == 0);
	}

	private static <T extends Tag> void method_29151(AbstractListTag<T> abstractListTag, Tag tag, Tag tag2) {
		if (tag instanceof AbstractListTag) {
			AbstractListTag<?> abstractListTag2 = (AbstractListTag<?>)tag;
			abstractListTag2.forEach(tagx -> abstractListTag.add(tagx));
		}

		abstractListTag.add(tag2);
	}

	private static <T extends Tag> void method_29150(AbstractListTag<T> abstractListTag, Tag tag, List<Tag> list) {
		if (tag instanceof AbstractListTag) {
			AbstractListTag<?> abstractListTag2 = (AbstractListTag<?>)tag;
			abstractListTag2.forEach(tagx -> abstractListTag.add(tagx));
		}

		list.forEach(tagx -> abstractListTag.add(tagx));
	}

	public DataResult<Tag> method_29156(Tag tag, Tag tag2) {
		if (!(tag instanceof AbstractListTag) && !(tag instanceof EndTag)) {
			return DataResult.error("mergeToList called with not a list: " + tag, tag);
		} else {
			AbstractListTag<?> abstractListTag = method_29144(tag instanceof AbstractListTag ? ((AbstractListTag)tag).getElementType() : 0, tag2.getType());
			method_29151(abstractListTag, tag, tag2);
			return DataResult.success(abstractListTag);
		}
	}

	public DataResult<Tag> method_29155(Tag tag, List<Tag> list) {
		if (!(tag instanceof AbstractListTag) && !(tag instanceof EndTag)) {
			return DataResult.error("mergeToList called with not a list: " + tag, tag);
		} else {
			AbstractListTag<?> abstractListTag = method_29144(
				tag instanceof AbstractListTag ? ((AbstractListTag)tag).getElementType() : 0, (Byte)list.stream().findFirst().map(Tag::getType).orElse((byte)0)
			);
			method_29150(abstractListTag, tag, list);
			return DataResult.success(abstractListTag);
		}
	}

	public DataResult<Tag> method_29157(Tag tag, Tag tag2, Tag tag3) {
		if (!(tag instanceof CompoundTag) && !(tag instanceof EndTag)) {
			return DataResult.error("mergeToMap called with not a map: " + tag, tag);
		} else if (!(tag2 instanceof StringTag)) {
			return DataResult.error("key is not a string: " + tag2, tag);
		} else {
			CompoundTag compoundTag = new CompoundTag();
			if (tag instanceof CompoundTag) {
				CompoundTag compoundTag2 = (CompoundTag)tag;
				compoundTag2.getKeys().forEach(string -> compoundTag.put(string, compoundTag2.get(string)));
			}

			compoundTag.put(tag2.asString(), tag3);
			return DataResult.success(compoundTag);
		}
	}

	public DataResult<Tag> method_29154(Tag tag, MapLike<Tag> mapLike) {
		if (!(tag instanceof CompoundTag) && !(tag instanceof EndTag)) {
			return DataResult.error("mergeToMap called with not a map: " + tag, tag);
		} else {
			CompoundTag compoundTag = new CompoundTag();
			if (tag instanceof CompoundTag) {
				CompoundTag compoundTag2 = (CompoundTag)tag;
				compoundTag2.getKeys().forEach(string -> compoundTag.put(string, compoundTag2.get(string)));
			}

			List<Tag> list = Lists.<Tag>newArrayList();
			mapLike.entries().forEach(pair -> {
				Tag tagx = (Tag)pair.getFirst();
				if (!(tagx instanceof StringTag)) {
					list.add(tagx);
				} else {
					compoundTag.put(tagx.asString(), (Tag)pair.getSecond());
				}
			});
			return !list.isEmpty() ? DataResult.error("some keys are not strings: " + list, compoundTag) : DataResult.success(compoundTag);
		}
	}

	public DataResult<Stream<Pair<Tag, Tag>>> method_10669(Tag tag) {
		if (!(tag instanceof CompoundTag)) {
			return DataResult.error("Not a map: " + tag);
		} else {
			CompoundTag compoundTag = (CompoundTag)tag;
			return DataResult.success(compoundTag.getKeys().stream().map(string -> Pair.of(this.method_10639(string), compoundTag.get(string))));
		}
	}

	public DataResult<Consumer<BiConsumer<Tag, Tag>>> method_29162(Tag tag) {
		if (!(tag instanceof CompoundTag)) {
			return DataResult.error("Not a map: " + tag);
		} else {
			CompoundTag compoundTag = (CompoundTag)tag;
			return DataResult.success(biConsumer -> compoundTag.getKeys().forEach(string -> biConsumer.accept(this.method_10639(string), compoundTag.get(string))));
		}
	}

	public DataResult<MapLike<Tag>> method_29163(Tag tag) {
		if (!(tag instanceof CompoundTag)) {
			return DataResult.error("Not a map: " + tag);
		} else {
			final CompoundTag compoundTag = (CompoundTag)tag;
			return DataResult.success(new MapLike<Tag>() {
				@Nullable
				public Tag method_29167(Tag tag) {
					return compoundTag.get(tag.asString());
				}

				@Nullable
				public Tag method_29165(String string) {
					return compoundTag.get(string);
				}

				@Override
				public Stream<Pair<Tag, Tag>> entries() {
					return compoundTag.getKeys().stream().map(string -> Pair.of(NbtOps.this.method_10639(string), compoundTag.get(string)));
				}

				public String toString() {
					return "MapLike[" + compoundTag + "]";
				}
			});
		}
	}

	public Tag method_10655(Stream<Pair<Tag, Tag>> stream) {
		CompoundTag compoundTag = new CompoundTag();
		stream.forEach(pair -> compoundTag.put(((Tag)pair.getFirst()).asString(), (Tag)pair.getSecond()));
		return compoundTag;
	}

	public DataResult<Stream<Tag>> method_10664(Tag tag) {
		return tag instanceof AbstractListTag ? DataResult.success(((AbstractListTag)tag).stream().map(tagx -> tagx)) : DataResult.error("Not a list");
	}

	public DataResult<Consumer<Consumer<Tag>>> method_29164(Tag tag) {
		if (tag instanceof AbstractListTag) {
			AbstractListTag<?> abstractListTag = (AbstractListTag<?>)tag;
			return DataResult.success(abstractListTag::forEach);
		} else {
			return DataResult.error("Not a list: " + tag);
		}
	}

	public DataResult<ByteBuffer> method_10646(Tag tag) {
		return tag instanceof ByteArrayTag ? DataResult.success(ByteBuffer.wrap(((ByteArrayTag)tag).getByteArray())) : DynamicOps.super.getByteBuffer(tag);
	}

	public Tag method_10657(ByteBuffer byteBuffer) {
		return new ByteArrayTag(DataFixUtils.toArray(byteBuffer));
	}

	public DataResult<IntStream> method_10651(Tag tag) {
		return tag instanceof IntArrayTag ? DataResult.success(Arrays.stream(((IntArrayTag)tag).getIntArray())) : DynamicOps.super.getIntStream(tag);
	}

	public Tag method_10663(IntStream intStream) {
		return new IntArrayTag(intStream.toArray());
	}

	public DataResult<LongStream> method_10637(Tag tag) {
		return tag instanceof LongArrayTag ? DataResult.success(Arrays.stream(((LongArrayTag)tag).getLongArray())) : DynamicOps.super.getLongStream(tag);
	}

	public Tag method_10643(LongStream longStream) {
		return new LongArrayTag(longStream.toArray());
	}

	public Tag method_10665(Stream<Tag> stream) {
		PeekingIterator<Tag> peekingIterator = Iterators.peekingIterator(stream.iterator());
		if (!peekingIterator.hasNext()) {
			return new ListTag();
		} else {
			Tag tag = peekingIterator.peek();
			if (tag instanceof ByteTag) {
				List<Byte> list = Lists.<Byte>newArrayList(Iterators.transform(peekingIterator, tagx -> ((ByteTag)tagx).getByte()));
				return new ByteArrayTag(list);
			} else if (tag instanceof IntTag) {
				List<Integer> list = Lists.<Integer>newArrayList(Iterators.transform(peekingIterator, tagx -> ((IntTag)tagx).getInt()));
				return new IntArrayTag(list);
			} else if (tag instanceof LongTag) {
				List<Long> list = Lists.<Long>newArrayList(Iterators.transform(peekingIterator, tagx -> ((LongTag)tagx).getLong()));
				return new LongArrayTag(list);
			} else {
				ListTag listTag = new ListTag();

				while (peekingIterator.hasNext()) {
					Tag tag2 = peekingIterator.next();
					if (!(tag2 instanceof EndTag)) {
						listTag.add(tag2);
					}
				}

				return listTag;
			}
		}
	}

	public Tag method_10648(Tag tag, String string) {
		if (tag instanceof CompoundTag) {
			CompoundTag compoundTag = (CompoundTag)tag;
			CompoundTag compoundTag2 = new CompoundTag();
			compoundTag.getKeys().stream().filter(string2 -> !Objects.equals(string2, string)).forEach(stringx -> compoundTag2.put(stringx, compoundTag.get(stringx)));
			return compoundTag2;
		} else {
			return tag;
		}
	}

	public String toString() {
		return "NBT";
	}

	@Override
	public RecordBuilder<Tag> mapBuilder() {
		return new NbtOps.MapBuilder();
	}

	class MapBuilder extends AbstractStringBuilder<Tag, CompoundTag> {
		protected MapBuilder() {
			super(NbtOps.this);
		}

		protected CompoundTag method_29168() {
			return new CompoundTag();
		}

		protected CompoundTag method_29169(String string, Tag tag, CompoundTag compoundTag) {
			compoundTag.put(string, tag);
			return compoundTag;
		}

		protected DataResult<Tag> method_29170(CompoundTag compoundTag, Tag tag) {
			if (tag == null || tag == EndTag.INSTANCE) {
				return DataResult.success(compoundTag);
			} else if (!(tag instanceof CompoundTag)) {
				return DataResult.error("mergeToMap called with not a map: " + tag, tag);
			} else {
				CompoundTag compoundTag2 = new CompoundTag(Maps.<String, Tag>newHashMap(((CompoundTag)tag).method_29143()));

				for (Entry<String, Tag> entry : compoundTag.method_29143().entrySet()) {
					compoundTag2.put((String)entry.getKey(), (Tag)entry.getValue());
				}

				return DataResult.success(compoundTag2);
			}
		}
	}
}
