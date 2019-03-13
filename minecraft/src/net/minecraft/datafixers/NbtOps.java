package net.minecraft.datafixers;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.PeekingIterator;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import net.minecraft.nbt.AbstractListTag;
import net.minecraft.nbt.AbstractNumberTag;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.EndTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

public class NbtOps implements DynamicOps<Tag> {
	public static final NbtOps INSTANCE = new NbtOps();

	protected NbtOps() {
	}

	public Tag method_10668() {
		return new EndTag();
	}

	public Type<?> method_10642(Tag tag) {
		switch (tag.getType()) {
			case 0:
				return DSL.nilType();
			case 1:
				return DSL.byteType();
			case 2:
				return DSL.shortType();
			case 3:
				return DSL.intType();
			case 4:
				return DSL.longType();
			case 5:
				return DSL.floatType();
			case 6:
				return DSL.doubleType();
			case 7:
				return DSL.list(DSL.byteType());
			case 8:
				return DSL.string();
			case 9:
				return DSL.list(DSL.remainderType());
			case 10:
				return DSL.compoundList(DSL.remainderType(), DSL.remainderType());
			case 11:
				return DSL.list(DSL.intType());
			case 12:
				return DSL.list(DSL.longType());
			default:
				return DSL.remainderType();
		}
	}

	public Optional<Number> method_10645(Tag tag) {
		return tag instanceof AbstractNumberTag ? Optional.of(((AbstractNumberTag)tag).getNumber()) : Optional.empty();
	}

	public Tag method_10660(Number number) {
		return new DoubleTag(number.doubleValue());
	}

	public Tag method_10640(byte b) {
		return new ByteTag(b);
	}

	public Tag method_10635(short s) {
		return new ShortTag(s);
	}

	public Tag method_10661(int i) {
		return new IntTag(i);
	}

	public Tag method_10654(long l) {
		return new LongTag(l);
	}

	public Tag method_10662(float f) {
		return new FloatTag(f);
	}

	public Tag method_10652(double d) {
		return new DoubleTag(d);
	}

	public Optional<String> method_10656(Tag tag) {
		return tag instanceof StringTag ? Optional.of(tag.asString()) : Optional.empty();
	}

	public Tag method_10639(String string) {
		return new StringTag(string);
	}

	public Tag method_10653(Tag tag, Tag tag2) {
		if (tag2 instanceof EndTag) {
			return tag;
		} else if (!(tag instanceof CompoundTag)) {
			if (tag instanceof EndTag) {
				throw new IllegalArgumentException("mergeInto called with a null input.");
			} else if (tag instanceof AbstractListTag) {
				AbstractListTag<Tag> abstractListTag = new ListTag();
				AbstractListTag<?> abstractListTag2 = (AbstractListTag<?>)tag;
				abstractListTag.addAll(abstractListTag2);
				abstractListTag.add(tag2);
				return abstractListTag;
			} else {
				return tag;
			}
		} else if (!(tag2 instanceof CompoundTag)) {
			return tag;
		} else {
			CompoundTag compoundTag = new CompoundTag();
			CompoundTag compoundTag2 = (CompoundTag)tag;

			for (String string : compoundTag2.getKeys()) {
				compoundTag.method_10566(string, compoundTag2.method_10580(string));
			}

			CompoundTag compoundTag3 = (CompoundTag)tag2;

			for (String string2 : compoundTag3.getKeys()) {
				compoundTag.method_10566(string2, compoundTag3.method_10580(string2));
			}

			return compoundTag;
		}
	}

	public Tag method_10644(Tag tag, Tag tag2, Tag tag3) {
		CompoundTag compoundTag;
		if (tag instanceof EndTag) {
			compoundTag = new CompoundTag();
		} else {
			if (!(tag instanceof CompoundTag)) {
				return tag;
			}

			CompoundTag compoundTag2 = (CompoundTag)tag;
			compoundTag = new CompoundTag();
			compoundTag2.getKeys().forEach(string -> compoundTag.method_10566(string, compoundTag2.method_10580(string)));
		}

		compoundTag.method_10566(tag2.asString(), tag3);
		return compoundTag;
	}

	public Tag method_10647(Tag tag, Tag tag2) {
		if (tag instanceof EndTag) {
			return tag2;
		} else if (tag2 instanceof EndTag) {
			return tag;
		} else {
			if (tag instanceof CompoundTag && tag2 instanceof CompoundTag) {
				CompoundTag compoundTag = (CompoundTag)tag;
				CompoundTag compoundTag2 = (CompoundTag)tag2;
				CompoundTag compoundTag3 = new CompoundTag();
				compoundTag.getKeys().forEach(string -> compoundTag3.method_10566(string, compoundTag.method_10580(string)));
				compoundTag2.getKeys().forEach(string -> compoundTag3.method_10566(string, compoundTag2.method_10580(string)));
			}

			if (tag instanceof AbstractListTag && tag2 instanceof AbstractListTag) {
				ListTag listTag = new ListTag();
				listTag.addAll((AbstractListTag)tag);
				listTag.addAll((AbstractListTag)tag2);
				return listTag;
			} else {
				throw new IllegalArgumentException("Could not merge " + tag + " and " + tag2);
			}
		}
	}

	public Optional<Map<Tag, Tag>> method_10669(Tag tag) {
		if (tag instanceof CompoundTag) {
			CompoundTag compoundTag = (CompoundTag)tag;
			return Optional.of(
				compoundTag.getKeys()
					.stream()
					.map(string -> Pair.of(this.method_10639(string), compoundTag.method_10580(string)))
					.collect(Collectors.toMap(Pair::getFirst, Pair::getSecond))
			);
		} else {
			return Optional.empty();
		}
	}

	public Tag method_10655(Map<Tag, Tag> map) {
		CompoundTag compoundTag = new CompoundTag();

		for (Entry<Tag, Tag> entry : map.entrySet()) {
			compoundTag.method_10566(((Tag)entry.getKey()).asString(), (Tag)entry.getValue());
		}

		return compoundTag;
	}

	public Optional<Stream<Tag>> method_10664(Tag tag) {
		return tag instanceof AbstractListTag ? Optional.of(((AbstractListTag)tag).stream().map(tagx -> tagx)) : Optional.empty();
	}

	public Optional<ByteBuffer> method_10646(Tag tag) {
		return tag instanceof ByteArrayTag ? Optional.of(ByteBuffer.wrap(((ByteArrayTag)tag).getByteArray())) : DynamicOps.super.getByteBuffer(tag);
	}

	public Tag method_10657(ByteBuffer byteBuffer) {
		return new ByteArrayTag(DataFixUtils.toArray(byteBuffer));
	}

	public Optional<IntStream> method_10651(Tag tag) {
		return tag instanceof IntArrayTag ? Optional.of(Arrays.stream(((IntArrayTag)tag).getIntArray())) : DynamicOps.super.getIntStream(tag);
	}

	public Tag method_10663(IntStream intStream) {
		return new IntArrayTag(intStream.toArray());
	}

	public Optional<LongStream> method_10637(Tag tag) {
		return tag instanceof LongArrayTag ? Optional.of(Arrays.stream(((LongArrayTag)tag).getLongArray())) : DynamicOps.super.getLongStream(tag);
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
			compoundTag.getKeys()
				.stream()
				.filter(string2 -> !Objects.equals(string2, string))
				.forEach(stringx -> compoundTag2.method_10566(stringx, compoundTag.method_10580(stringx)));
			return compoundTag2;
		} else {
			return tag;
		}
	}

	public String toString() {
		return "NBT";
	}
}
