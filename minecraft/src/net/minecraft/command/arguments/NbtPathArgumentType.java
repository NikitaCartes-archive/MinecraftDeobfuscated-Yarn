package net.minecraft.command.arguments;

import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.nbt.AbstractListTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.sortme.JsonLikeTagParser;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.TagHelper;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class NbtPathArgumentType implements ArgumentType<NbtPathArgumentType.class_2209> {
	private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo.bar", "foo[0]", "[0]", "[]", "{foo=bar}");
	public static final SimpleCommandExceptionType field_9900 = new SimpleCommandExceptionType(new TranslatableTextComponent("arguments.nbtpath.node.invalid"));
	public static final DynamicCommandExceptionType field_9899 = new DynamicCommandExceptionType(
		object -> new TranslatableTextComponent("arguments.nbtpath.nothing_found", object)
	);

	public static NbtPathArgumentType create() {
		return new NbtPathArgumentType();
	}

	public static NbtPathArgumentType.class_2209 method_9358(CommandContext<ServerCommandSource> commandContext, String string) {
		return commandContext.getArgument(string, NbtPathArgumentType.class_2209.class);
	}

	public NbtPathArgumentType.class_2209 method_9362(StringReader stringReader) throws CommandSyntaxException {
		List<NbtPathArgumentType.class_2210> list = Lists.<NbtPathArgumentType.class_2210>newArrayList();
		int i = stringReader.getCursor();
		Object2IntMap<NbtPathArgumentType.class_2210> object2IntMap = new Object2IntOpenHashMap<>();
		boolean bl = true;

		while (stringReader.canRead() && stringReader.peek() != ' ') {
			NbtPathArgumentType.class_2210 lv = method_9361(stringReader, bl);
			list.add(lv);
			object2IntMap.put(lv, stringReader.getCursor() - i);
			bl = false;
			if (stringReader.canRead()) {
				char c = stringReader.peek();
				if (c != ' ' && c != '[' && c != '{') {
					stringReader.expect('.');
				}
			}
		}

		return new NbtPathArgumentType.class_2209(
			stringReader.getString().substring(i, stringReader.getCursor()),
			(NbtPathArgumentType.class_2210[])list.toArray(new NbtPathArgumentType.class_2210[0]),
			object2IntMap
		);
	}

	private static NbtPathArgumentType.class_2210 method_9361(StringReader stringReader, boolean bl) throws CommandSyntaxException {
		switch (stringReader.peek()) {
			case '"': {
				String string = stringReader.readString();
				return method_9352(stringReader, string);
			}
			case '[':
				stringReader.skip();
				int i = stringReader.peek();
				if (i == 123) {
					CompoundTag compoundTag2 = new JsonLikeTagParser(stringReader).parseCompoundTag();
					stringReader.expect(']');
					return new NbtPathArgumentType.class_2207(compoundTag2);
				} else {
					if (i == 93) {
						stringReader.skip();
						return NbtPathArgumentType.class_2204.field_9901;
					}

					int j = stringReader.readInt();
					stringReader.expect(']');
					return new NbtPathArgumentType.class_2206(j);
				}
			case '{':
				if (!bl) {
					throw field_9900.createWithContext(stringReader);
				}

				CompoundTag compoundTag = new JsonLikeTagParser(stringReader).parseCompoundTag();
				return new NbtPathArgumentType.class_3707(compoundTag);
			default: {
				String string = method_9357(stringReader);
				return method_9352(stringReader, string);
			}
		}
	}

	private static NbtPathArgumentType.class_2210 method_9352(StringReader stringReader, String string) throws CommandSyntaxException {
		if (stringReader.canRead() && stringReader.peek() == '{') {
			CompoundTag compoundTag = new JsonLikeTagParser(stringReader).parseCompoundTag();
			return new NbtPathArgumentType.class_2208(string, compoundTag);
		} else {
			return new NbtPathArgumentType.class_2205(string);
		}
	}

	private static String method_9357(StringReader stringReader) throws CommandSyntaxException {
		int i = stringReader.getCursor();

		while (stringReader.canRead() && method_9355(stringReader.peek())) {
			stringReader.skip();
		}

		if (stringReader.getCursor() == i) {
			throw field_9900.createWithContext(stringReader);
		} else {
			return stringReader.getString().substring(i, stringReader.getCursor());
		}
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}

	private static boolean method_9355(char c) {
		return c != ' ' && c != '"' && c != '[' && c != ']' && c != '.' && c != '{' && c != '}';
	}

	private static Predicate<Tag> method_9359(CompoundTag compoundTag) {
		return tag -> TagHelper.areTagsEqual(compoundTag, tag, true);
	}

	static class class_2204 implements NbtPathArgumentType.class_2210 {
		public static final NbtPathArgumentType.class_2204 field_9901 = new NbtPathArgumentType.class_2204();

		private class_2204() {
		}

		@Override
		public void method_9378(Tag tag, List<Tag> list) {
			if (tag instanceof AbstractListTag) {
				list.addAll((AbstractListTag)tag);
			}
		}

		@Override
		public void method_9380(Tag tag, Supplier<Tag> supplier, List<Tag> list) {
			if (tag instanceof AbstractListTag) {
				AbstractListTag<?> abstractListTag = (AbstractListTag<?>)tag;
				if (abstractListTag.isEmpty()) {
					Tag tag2 = (Tag)supplier.get();
					abstractListTag.append(0, tag2);
					list.add(tag2);
				} else {
					list.addAll(abstractListTag);
				}
			}
		}

		@Override
		public Tag method_9382() {
			return new ListTag();
		}

		@Override
		public int method_9376(Tag tag, Supplier<Tag> supplier) {
			if (!(tag instanceof AbstractListTag)) {
				return 0;
			} else {
				AbstractListTag<?> abstractListTag = (AbstractListTag<?>)tag;
				int i = Math.max(1, abstractListTag.size());
				abstractListTag.clear();

				for (int j = 0; j < i; j++) {
					abstractListTag.append(j, (Tag)supplier.get());
				}

				return i;
			}
		}

		@Override
		public int method_9383(Tag tag) {
			if (tag instanceof AbstractListTag) {
				AbstractListTag<?> abstractListTag = (AbstractListTag<?>)tag;
				int i = abstractListTag.size();
				if (i > 0) {
					abstractListTag.clear();
					return i;
				}
			}

			return 0;
		}
	}

	static class class_2205 implements NbtPathArgumentType.class_2210 {
		private final String field_9902;

		public class_2205(String string) {
			this.field_9902 = string;
		}

		@Override
		public void method_9378(Tag tag, List<Tag> list) {
			if (tag instanceof CompoundTag) {
				Tag tag2 = ((CompoundTag)tag).getTag(this.field_9902);
				if (tag2 != null) {
					list.add(tag2);
				}
			}
		}

		@Override
		public void method_9380(Tag tag, Supplier<Tag> supplier, List<Tag> list) {
			if (tag instanceof CompoundTag) {
				CompoundTag compoundTag = (CompoundTag)tag;
				Tag tag2;
				if (compoundTag.containsKey(this.field_9902)) {
					tag2 = compoundTag.getTag(this.field_9902);
				} else {
					tag2 = (Tag)supplier.get();
					compoundTag.put(this.field_9902, tag2);
				}

				list.add(tag2);
			}
		}

		@Override
		public Tag method_9382() {
			return new CompoundTag();
		}

		@Override
		public int method_9376(Tag tag, Supplier<Tag> supplier) {
			if (tag instanceof CompoundTag) {
				CompoundTag compoundTag = (CompoundTag)tag;
				compoundTag.put(this.field_9902, (Tag)supplier.get());
				return 1;
			} else {
				return 0;
			}
		}

		@Override
		public int method_9383(Tag tag) {
			if (tag instanceof CompoundTag) {
				CompoundTag compoundTag = (CompoundTag)tag;
				if (compoundTag.containsKey(this.field_9902)) {
					compoundTag.remove(this.field_9902);
					return 1;
				}
			}

			return 0;
		}
	}

	static class class_2206 implements NbtPathArgumentType.class_2210 {
		private final int field_9903;

		public class_2206(int i) {
			this.field_9903 = i;
		}

		@Override
		public void method_9378(Tag tag, List<Tag> list) {
			if (tag instanceof AbstractListTag) {
				AbstractListTag<?> abstractListTag = (AbstractListTag<?>)tag;
				int i = abstractListTag.size();
				int j = this.field_9903 < 0 ? i + this.field_9903 : this.field_9903;
				if (0 <= j && j < i) {
					list.add(abstractListTag.getRaw(j));
				}
			}
		}

		@Override
		public void method_9380(Tag tag, Supplier<Tag> supplier, List<Tag> list) {
			this.method_9378(tag, list);
		}

		@Override
		public Tag method_9382() {
			return new ListTag();
		}

		@Override
		public int method_9376(Tag tag, Supplier<Tag> supplier) {
			if (tag instanceof AbstractListTag) {
				AbstractListTag<?> abstractListTag = (AbstractListTag<?>)tag;
				int i = abstractListTag.size();
				int j = this.field_9903 < 0 ? i + this.field_9903 : this.field_9903;
				if (0 <= j && j < i) {
					abstractListTag.setRaw(j, (Tag)supplier.get());
					return 1;
				}
			}

			return 0;
		}

		@Override
		public int method_9383(Tag tag) {
			if (tag instanceof AbstractListTag) {
				AbstractListTag<?> abstractListTag = (AbstractListTag<?>)tag;
				int i = abstractListTag.size();
				int j = this.field_9903 < 0 ? i + this.field_9903 : this.field_9903;
				if (0 <= j && j < i) {
					abstractListTag.removeAt(j);
					return 1;
				}
			}

			return 0;
		}
	}

	static class class_2207 implements NbtPathArgumentType.class_2210 {
		private final CompoundTag field_9904;
		private final Predicate<Tag> field_9905;

		public class_2207(CompoundTag compoundTag) {
			this.field_9904 = compoundTag;
			this.field_9905 = NbtPathArgumentType.method_9359(compoundTag);
		}

		@Override
		public void method_9378(Tag tag, List<Tag> list) {
			if (tag instanceof ListTag) {
				ListTag listTag = (ListTag)tag;
				listTag.stream().filter(this.field_9905).forEach(list::add);
			}
		}

		@Override
		public void method_9380(Tag tag, Supplier<Tag> supplier, List<Tag> list) {
			MutableBoolean mutableBoolean = new MutableBoolean();
			if (tag instanceof ListTag) {
				ListTag listTag = (ListTag)tag;
				listTag.stream().filter(this.field_9905).forEach(tagx -> {
					list.add(tagx);
					mutableBoolean.setTrue();
				});
				if (mutableBoolean.isFalse()) {
					CompoundTag compoundTag = this.field_9904.copy();
					listTag.add((Tag)compoundTag);
					list.add(compoundTag);
				}
			}
		}

		@Override
		public Tag method_9382() {
			return new ListTag();
		}

		private int method_9364(Tag tag, BiConsumer<ListTag, Integer> biConsumer) {
			int i = 0;
			if (tag instanceof ListTag) {
				ListTag listTag = (ListTag)tag;

				for (int j = 0; j < listTag.size(); j++) {
					if (this.field_9905.test(listTag.get(j))) {
						biConsumer.accept(listTag, j);
						i++;
					}
				}
			}

			return i;
		}

		@Override
		public int method_9376(Tag tag, Supplier<Tag> supplier) {
			return this.method_9364(tag, (listTag, integer) -> listTag.setRaw(integer, (Tag)supplier.get()));
		}

		@Override
		public int method_9383(Tag tag) {
			return this.method_9364(tag, ListTag::removeAt);
		}
	}

	static class class_2208 implements NbtPathArgumentType.class_2210 {
		private final String field_9906;
		private final CompoundTag field_9907;
		private final Predicate<Tag> field_9908;

		public class_2208(String string, CompoundTag compoundTag) {
			this.field_9906 = string;
			this.field_9907 = compoundTag;
			this.field_9908 = NbtPathArgumentType.method_9359(compoundTag);
		}

		@Override
		public void method_9378(Tag tag, List<Tag> list) {
			if (tag instanceof CompoundTag) {
				Tag tag2 = ((CompoundTag)tag).getTag(this.field_9906);
				if (this.field_9908.test(tag2)) {
					list.add(tag2);
				}
			}
		}

		@Override
		public void method_9380(Tag tag, Supplier<Tag> supplier, List<Tag> list) {
			if (tag instanceof CompoundTag) {
				CompoundTag compoundTag = (CompoundTag)tag;
				Tag tag2 = compoundTag.getTag(this.field_9906);
				if (tag2 == null) {
					Tag var6 = this.field_9907.copy();
					compoundTag.put(this.field_9906, var6);
					list.add(var6);
				} else if (this.field_9908.test(tag2)) {
					list.add(tag2);
				}
			}
		}

		@Override
		public Tag method_9382() {
			return new CompoundTag();
		}

		@Override
		public int method_9376(Tag tag, Supplier<Tag> supplier) {
			if (tag instanceof CompoundTag) {
				CompoundTag compoundTag = (CompoundTag)tag;
				Tag tag2 = compoundTag.getTag(this.field_9906);
				if (this.field_9908.test(tag2)) {
					compoundTag.put(this.field_9906, (Tag)supplier.get());
					return 1;
				}
			}

			return 0;
		}

		@Override
		public int method_9383(Tag tag) {
			if (tag instanceof CompoundTag) {
				CompoundTag compoundTag = (CompoundTag)tag;
				Tag tag2 = compoundTag.getTag(this.field_9906);
				if (this.field_9908.test(tag2)) {
					compoundTag.remove(this.field_9906);
					return 1;
				}
			}

			return 0;
		}
	}

	public static class class_2209 {
		private final String field_9909;
		private final Object2IntMap<NbtPathArgumentType.class_2210> field_9910;
		private final NbtPathArgumentType.class_2210[] field_9911;

		public class_2209(String string, NbtPathArgumentType.class_2210[] args, Object2IntMap<NbtPathArgumentType.class_2210> object2IntMap) {
			this.field_9909 = string;
			this.field_9911 = args;
			this.field_9910 = object2IntMap;
		}

		public List<Tag> method_9366(Tag tag) throws CommandSyntaxException {
			List<Tag> list = Collections.singletonList(tag);

			for (NbtPathArgumentType.class_2210 lv : this.field_9911) {
				list = lv.method_9381(list);
				if (list.isEmpty()) {
					throw this.method_9375(lv);
				}
			}

			return list;
		}

		public int method_9374(Tag tag) {
			List<Tag> list = Collections.singletonList(tag);

			for (NbtPathArgumentType.class_2210 lv : this.field_9911) {
				list = lv.method_9381(list);
				if (list.isEmpty()) {
					return 0;
				}
			}

			return list.size();
		}

		private List<Tag> method_9369(Tag tag) throws CommandSyntaxException {
			List<Tag> list = Collections.singletonList(tag);

			for (int i = 0; i < this.field_9911.length - 1; i++) {
				NbtPathArgumentType.class_2210 lv = this.field_9911[i];
				int j = i + 1;
				list = lv.method_9377(list, this.field_9911[j]::method_9382);
				if (list.isEmpty()) {
					throw this.method_9375(lv);
				}
			}

			return list;
		}

		public List<Tag> method_9367(Tag tag, Supplier<Tag> supplier) throws CommandSyntaxException {
			List<Tag> list = this.method_9369(tag);
			NbtPathArgumentType.class_2210 lv = this.field_9911[this.field_9911.length - 1];
			return lv.method_9377(list, supplier);
		}

		private static int method_9371(List<Tag> list, Function<Tag, Integer> function) {
			return (Integer)list.stream().map(function).reduce(0, (integer, integer2) -> integer + integer2);
		}

		public int method_9368(Tag tag, Supplier<Tag> supplier) throws CommandSyntaxException {
			List<Tag> list = this.method_9369(tag);
			NbtPathArgumentType.class_2210 lv = this.field_9911[this.field_9911.length - 1];
			int i = method_9371(list, tagx -> lv.method_9376(tagx, supplier));
			if (i == 0) {
				throw NbtPathArgumentType.field_9899.create(this.field_9909);
			} else {
				return i;
			}
		}

		public int method_9372(Tag tag) {
			List<Tag> list = Collections.singletonList(tag);

			for (int i = 0; i < this.field_9911.length - 1; i++) {
				list = this.field_9911[i].method_9381(list);
			}

			NbtPathArgumentType.class_2210 lv = this.field_9911[this.field_9911.length - 1];
			return method_9371(list, lv::method_9383);
		}

		private CommandSyntaxException method_9375(NbtPathArgumentType.class_2210 arg) {
			int i = this.field_9910.getInt(arg);
			return NbtPathArgumentType.field_9899.create(this.field_9909.substring(0, i));
		}

		public String toString() {
			return this.field_9909;
		}
	}

	interface class_2210 {
		void method_9378(Tag tag, List<Tag> list);

		void method_9380(Tag tag, Supplier<Tag> supplier, List<Tag> list);

		Tag method_9382();

		int method_9376(Tag tag, Supplier<Tag> supplier);

		int method_9383(Tag tag);

		default List<Tag> method_9381(List<Tag> list) {
			return this.method_9384(list, this::method_9378);
		}

		default List<Tag> method_9377(List<Tag> list, Supplier<Tag> supplier) {
			return this.method_9384(list, (tag, listx) -> this.method_9380(tag, supplier, listx));
		}

		default List<Tag> method_9384(List<Tag> list, BiConsumer<Tag, List<Tag>> biConsumer) {
			List<Tag> list2 = Lists.<Tag>newArrayList();

			for (Tag tag : list) {
				biConsumer.accept(tag, list2);
			}

			return list2;
		}
	}

	static class class_3707 implements NbtPathArgumentType.class_2210 {
		private final Predicate<Tag> field_16319;

		public class_3707(CompoundTag compoundTag) {
			this.field_16319 = NbtPathArgumentType.method_9359(compoundTag);
		}

		@Override
		public void method_9378(Tag tag, List<Tag> list) {
			if (tag instanceof CompoundTag && this.field_16319.test(tag)) {
				list.add(tag);
			}
		}

		@Override
		public void method_9380(Tag tag, Supplier<Tag> supplier, List<Tag> list) {
			this.method_9378(tag, list);
		}

		@Override
		public Tag method_9382() {
			return new CompoundTag();
		}

		@Override
		public int method_9376(Tag tag, Supplier<Tag> supplier) {
			return 0;
		}

		@Override
		public int method_9383(Tag tag) {
			return 0;
		}
	}
}
