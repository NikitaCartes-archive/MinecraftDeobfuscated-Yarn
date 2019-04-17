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
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.nbt.Tag;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.TagHelper;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class NbtPathArgumentType implements ArgumentType<NbtPathArgumentType.NbtPath> {
	private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo.bar", "foo[0]", "[0]", "[]", "{foo=bar}");
	public static final SimpleCommandExceptionType INVALID_PATH_NODE_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("arguments.nbtpath.node.invalid")
	);
	public static final DynamicCommandExceptionType NOTHING_FOUND_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableTextComponent("arguments.nbtpath.nothing_found", object)
	);

	public static NbtPathArgumentType create() {
		return new NbtPathArgumentType();
	}

	public static NbtPathArgumentType.NbtPath getNbtPath(CommandContext<ServerCommandSource> commandContext, String string) {
		return commandContext.getArgument(string, NbtPathArgumentType.NbtPath.class);
	}

	public NbtPathArgumentType.NbtPath method_9362(StringReader stringReader) throws CommandSyntaxException {
		List<NbtPathArgumentType.NbtPathNode> list = Lists.<NbtPathArgumentType.NbtPathNode>newArrayList();
		int i = stringReader.getCursor();
		Object2IntMap<NbtPathArgumentType.NbtPathNode> object2IntMap = new Object2IntOpenHashMap<>();
		boolean bl = true;

		while (stringReader.canRead() && stringReader.peek() != ' ') {
			NbtPathArgumentType.NbtPathNode nbtPathNode = parseNode(stringReader, bl);
			list.add(nbtPathNode);
			object2IntMap.put(nbtPathNode, stringReader.getCursor() - i);
			bl = false;
			if (stringReader.canRead()) {
				char c = stringReader.peek();
				if (c != ' ' && c != '[' && c != '{') {
					stringReader.expect('.');
				}
			}
		}

		return new NbtPathArgumentType.NbtPath(
			stringReader.getString().substring(i, stringReader.getCursor()),
			(NbtPathArgumentType.NbtPathNode[])list.toArray(new NbtPathArgumentType.NbtPathNode[0]),
			object2IntMap
		);
	}

	private static NbtPathArgumentType.NbtPathNode parseNode(StringReader stringReader, boolean bl) throws CommandSyntaxException {
		switch (stringReader.peek()) {
			case '"': {
				String string = stringReader.readString();
				return readCompoundChildNode(stringReader, string);
			}
			case '[':
				stringReader.skip();
				int i = stringReader.peek();
				if (i == 123) {
					CompoundTag compoundTag2 = new StringNbtReader(stringReader).parseCompoundTag();
					stringReader.expect(']');
					return new NbtPathArgumentType.EqualListElementNode(compoundTag2);
				} else {
					if (i == 93) {
						stringReader.skip();
						return NbtPathArgumentType.AllListElementsNode.INSTANCE;
					}

					int j = stringReader.readInt();
					stringReader.expect(']');
					return new NbtPathArgumentType.ListIndexNode(j);
				}
			case '{':
				if (!bl) {
					throw INVALID_PATH_NODE_EXCEPTION.createWithContext(stringReader);
				}

				CompoundTag compoundTag = new StringNbtReader(stringReader).parseCompoundTag();
				return new NbtPathArgumentType.EqualCompoundNode(compoundTag);
			default: {
				String string = readName(stringReader);
				return readCompoundChildNode(stringReader, string);
			}
		}
	}

	private static NbtPathArgumentType.NbtPathNode readCompoundChildNode(StringReader stringReader, String string) throws CommandSyntaxException {
		if (stringReader.canRead() && stringReader.peek() == '{') {
			CompoundTag compoundTag = new StringNbtReader(stringReader).parseCompoundTag();
			return new NbtPathArgumentType.EqualCompundChildNode(string, compoundTag);
		} else {
			return new NbtPathArgumentType.CompoundChildNode(string);
		}
	}

	private static String readName(StringReader stringReader) throws CommandSyntaxException {
		int i = stringReader.getCursor();

		while (stringReader.canRead() && isNameCharacter(stringReader.peek())) {
			stringReader.skip();
		}

		if (stringReader.getCursor() == i) {
			throw INVALID_PATH_NODE_EXCEPTION.createWithContext(stringReader);
		} else {
			return stringReader.getString().substring(i, stringReader.getCursor());
		}
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}

	private static boolean isNameCharacter(char c) {
		return c != ' ' && c != '"' && c != '[' && c != ']' && c != '.' && c != '{' && c != '}';
	}

	private static Predicate<Tag> getEqualityPredicate(CompoundTag compoundTag) {
		return tag -> TagHelper.areTagsEqual(compoundTag, tag, true);
	}

	static class AllListElementsNode implements NbtPathArgumentType.NbtPathNode {
		public static final NbtPathArgumentType.AllListElementsNode INSTANCE = new NbtPathArgumentType.AllListElementsNode();

		private AllListElementsNode() {
		}

		@Override
		public void get(Tag tag, List<Tag> list) {
			if (tag instanceof AbstractListTag) {
				list.addAll((AbstractListTag)tag);
			}
		}

		@Override
		public void putIfAbsent(Tag tag, Supplier<Tag> supplier, List<Tag> list) {
			if (tag instanceof AbstractListTag) {
				AbstractListTag<?> abstractListTag = (AbstractListTag<?>)tag;
				if (abstractListTag.isEmpty()) {
					Tag tag2 = (Tag)supplier.get();
					if (abstractListTag.addTag(0, tag2)) {
						list.add(tag2);
					}
				} else {
					list.addAll(abstractListTag);
				}
			}
		}

		@Override
		public Tag createParent() {
			return new ListTag();
		}

		@Override
		public int put(Tag tag, Supplier<Tag> supplier) {
			if (!(tag instanceof AbstractListTag)) {
				return 0;
			} else {
				AbstractListTag<?> abstractListTag = (AbstractListTag<?>)tag;
				int i = abstractListTag.size();
				if (i == 0) {
					abstractListTag.addTag(0, (Tag)supplier.get());
					return 1;
				} else {
					Tag tag2 = (Tag)supplier.get();
					int j = i - (int)abstractListTag.stream().filter(tag2::equals).count();
					if (j == 0) {
						return 0;
					} else {
						abstractListTag.clear();
						if (!abstractListTag.addTag(0, tag2)) {
							return 0;
						} else {
							for (int k = 1; k < i; k++) {
								abstractListTag.addTag(k, (Tag)supplier.get());
							}

							return j;
						}
					}
				}
			}
		}

		@Override
		public int remove(Tag tag) {
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

	static class CompoundChildNode implements NbtPathArgumentType.NbtPathNode {
		private final String name;

		public CompoundChildNode(String string) {
			this.name = string;
		}

		@Override
		public void get(Tag tag, List<Tag> list) {
			if (tag instanceof CompoundTag) {
				Tag tag2 = ((CompoundTag)tag).getTag(this.name);
				if (tag2 != null) {
					list.add(tag2);
				}
			}
		}

		@Override
		public void putIfAbsent(Tag tag, Supplier<Tag> supplier, List<Tag> list) {
			if (tag instanceof CompoundTag) {
				CompoundTag compoundTag = (CompoundTag)tag;
				Tag tag2;
				if (compoundTag.containsKey(this.name)) {
					tag2 = compoundTag.getTag(this.name);
				} else {
					tag2 = (Tag)supplier.get();
					compoundTag.put(this.name, tag2);
				}

				list.add(tag2);
			}
		}

		@Override
		public Tag createParent() {
			return new CompoundTag();
		}

		@Override
		public int put(Tag tag, Supplier<Tag> supplier) {
			if (tag instanceof CompoundTag) {
				CompoundTag compoundTag = (CompoundTag)tag;
				Tag tag2 = (Tag)supplier.get();
				Tag tag3 = compoundTag.put(this.name, tag2);
				if (!tag2.equals(tag3)) {
					return 1;
				}
			}

			return 0;
		}

		@Override
		public int remove(Tag tag) {
			if (tag instanceof CompoundTag) {
				CompoundTag compoundTag = (CompoundTag)tag;
				if (compoundTag.containsKey(this.name)) {
					compoundTag.remove(this.name);
					return 1;
				}
			}

			return 0;
		}
	}

	static class EqualCompoundNode implements NbtPathArgumentType.NbtPathNode {
		private final Predicate<Tag> predicate;

		public EqualCompoundNode(CompoundTag compoundTag) {
			this.predicate = NbtPathArgumentType.getEqualityPredicate(compoundTag);
		}

		@Override
		public void get(Tag tag, List<Tag> list) {
			if (tag instanceof CompoundTag && this.predicate.test(tag)) {
				list.add(tag);
			}
		}

		@Override
		public void putIfAbsent(Tag tag, Supplier<Tag> supplier, List<Tag> list) {
			this.get(tag, list);
		}

		@Override
		public Tag createParent() {
			return new CompoundTag();
		}

		@Override
		public int put(Tag tag, Supplier<Tag> supplier) {
			return 0;
		}

		@Override
		public int remove(Tag tag) {
			return 0;
		}
	}

	static class EqualCompundChildNode implements NbtPathArgumentType.NbtPathNode {
		private final String name;
		private final CompoundTag tag;
		private final Predicate<Tag> predicate;

		public EqualCompundChildNode(String string, CompoundTag compoundTag) {
			this.name = string;
			this.tag = compoundTag;
			this.predicate = NbtPathArgumentType.getEqualityPredicate(compoundTag);
		}

		@Override
		public void get(Tag tag, List<Tag> list) {
			if (tag instanceof CompoundTag) {
				Tag tag2 = ((CompoundTag)tag).getTag(this.name);
				if (this.predicate.test(tag2)) {
					list.add(tag2);
				}
			}
		}

		@Override
		public void putIfAbsent(Tag tag, Supplier<Tag> supplier, List<Tag> list) {
			if (tag instanceof CompoundTag) {
				CompoundTag compoundTag = (CompoundTag)tag;
				Tag tag2 = compoundTag.getTag(this.name);
				if (tag2 == null) {
					Tag var6 = this.tag.method_10553();
					compoundTag.put(this.name, var6);
					list.add(var6);
				} else if (this.predicate.test(tag2)) {
					list.add(tag2);
				}
			}
		}

		@Override
		public Tag createParent() {
			return new CompoundTag();
		}

		@Override
		public int put(Tag tag, Supplier<Tag> supplier) {
			if (tag instanceof CompoundTag) {
				CompoundTag compoundTag = (CompoundTag)tag;
				Tag tag2 = compoundTag.getTag(this.name);
				if (this.predicate.test(tag2)) {
					Tag tag3 = (Tag)supplier.get();
					if (!tag3.equals(tag2)) {
						compoundTag.put(this.name, tag3);
						return 1;
					}
				}
			}

			return 0;
		}

		@Override
		public int remove(Tag tag) {
			if (tag instanceof CompoundTag) {
				CompoundTag compoundTag = (CompoundTag)tag;
				Tag tag2 = compoundTag.getTag(this.name);
				if (this.predicate.test(tag2)) {
					compoundTag.remove(this.name);
					return 1;
				}
			}

			return 0;
		}
	}

	static class EqualListElementNode implements NbtPathArgumentType.NbtPathNode {
		private final CompoundTag tag;
		private final Predicate<Tag> predicate;

		public EqualListElementNode(CompoundTag compoundTag) {
			this.tag = compoundTag;
			this.predicate = NbtPathArgumentType.getEqualityPredicate(compoundTag);
		}

		@Override
		public void get(Tag tag, List<Tag> list) {
			if (tag instanceof ListTag) {
				ListTag listTag = (ListTag)tag;
				listTag.stream().filter(this.predicate).forEach(list::add);
			}
		}

		@Override
		public void putIfAbsent(Tag tag, Supplier<Tag> supplier, List<Tag> list) {
			MutableBoolean mutableBoolean = new MutableBoolean();
			if (tag instanceof ListTag) {
				ListTag listTag = (ListTag)tag;
				listTag.stream().filter(this.predicate).forEach(tagx -> {
					list.add(tagx);
					mutableBoolean.setTrue();
				});
				if (mutableBoolean.isFalse()) {
					CompoundTag compoundTag = this.tag.method_10553();
					listTag.add(compoundTag);
					list.add(compoundTag);
				}
			}
		}

		@Override
		public Tag createParent() {
			return new ListTag();
		}

		@Override
		public int put(Tag tag, Supplier<Tag> supplier) {
			int i = 0;
			if (tag instanceof ListTag) {
				ListTag listTag = (ListTag)tag;
				int j = listTag.size();
				if (j == 0) {
					listTag.add(supplier.get());
					i++;
				} else {
					for (int k = 0; k < j; k++) {
						Tag tag2 = listTag.method_10534(k);
						if (this.predicate.test(tag2)) {
							Tag tag3 = (Tag)supplier.get();
							if (!tag3.equals(tag2) && listTag.setTag(k, tag3)) {
								i++;
							}
						}
					}
				}
			}

			return i;
		}

		@Override
		public int remove(Tag tag) {
			int i = 0;
			if (tag instanceof ListTag) {
				ListTag listTag = (ListTag)tag;

				for (int j = listTag.size() - 1; j >= 0; j--) {
					if (this.predicate.test(listTag.method_10534(j))) {
						listTag.method_10536(j);
						i++;
					}
				}
			}

			return i;
		}
	}

	static class ListIndexNode implements NbtPathArgumentType.NbtPathNode {
		private final int index;

		public ListIndexNode(int i) {
			this.index = i;
		}

		@Override
		public void get(Tag tag, List<Tag> list) {
			if (tag instanceof AbstractListTag) {
				AbstractListTag<?> abstractListTag = (AbstractListTag<?>)tag;
				int i = abstractListTag.size();
				int j = this.index < 0 ? i + this.index : this.index;
				if (0 <= j && j < i) {
					list.add(abstractListTag.get(j));
				}
			}
		}

		@Override
		public void putIfAbsent(Tag tag, Supplier<Tag> supplier, List<Tag> list) {
			this.get(tag, list);
		}

		@Override
		public Tag createParent() {
			return new ListTag();
		}

		@Override
		public int put(Tag tag, Supplier<Tag> supplier) {
			if (tag instanceof AbstractListTag) {
				AbstractListTag<?> abstractListTag = (AbstractListTag<?>)tag;
				int i = abstractListTag.size();
				int j = this.index < 0 ? i + this.index : this.index;
				if (0 <= j && j < i) {
					Tag tag2 = (Tag)abstractListTag.get(j);
					Tag tag3 = (Tag)supplier.get();
					if (!tag3.equals(tag2) && abstractListTag.setTag(j, tag3)) {
						return 1;
					}
				}
			}

			return 0;
		}

		@Override
		public int remove(Tag tag) {
			if (tag instanceof AbstractListTag) {
				AbstractListTag<?> abstractListTag = (AbstractListTag<?>)tag;
				int i = abstractListTag.size();
				int j = this.index < 0 ? i + this.index : this.index;
				if (0 <= j && j < i) {
					abstractListTag.method_10536(j);
					return 1;
				}
			}

			return 0;
		}
	}

	public static class NbtPath {
		private final String string;
		private final Object2IntMap<NbtPathArgumentType.NbtPathNode> nodeEndIndices;
		private final NbtPathArgumentType.NbtPathNode[] nodes;

		public NbtPath(String string, NbtPathArgumentType.NbtPathNode[] nbtPathNodes, Object2IntMap<NbtPathArgumentType.NbtPathNode> object2IntMap) {
			this.string = string;
			this.nodes = nbtPathNodes;
			this.nodeEndIndices = object2IntMap;
		}

		public List<Tag> get(Tag tag) throws CommandSyntaxException {
			List<Tag> list = Collections.singletonList(tag);

			for (NbtPathArgumentType.NbtPathNode nbtPathNode : this.nodes) {
				list = nbtPathNode.get(list);
				if (list.isEmpty()) {
					throw this.createNothingFoundException(nbtPathNode);
				}
			}

			return list;
		}

		public int count(Tag tag) {
			List<Tag> list = Collections.singletonList(tag);

			for (NbtPathArgumentType.NbtPathNode nbtPathNode : this.nodes) {
				list = nbtPathNode.get(list);
				if (list.isEmpty()) {
					return 0;
				}
			}

			return list.size();
		}

		private List<Tag> getParents(Tag tag) throws CommandSyntaxException {
			List<Tag> list = Collections.singletonList(tag);

			for (int i = 0; i < this.nodes.length - 1; i++) {
				NbtPathArgumentType.NbtPathNode nbtPathNode = this.nodes[i];
				int j = i + 1;
				list = nbtPathNode.putIfAbsent(list, this.nodes[j]::createParent);
				if (list.isEmpty()) {
					throw this.createNothingFoundException(nbtPathNode);
				}
			}

			return list;
		}

		public List<Tag> putIfAbsent(Tag tag, Supplier<Tag> supplier) throws CommandSyntaxException {
			List<Tag> list = this.getParents(tag);
			NbtPathArgumentType.NbtPathNode nbtPathNode = this.nodes[this.nodes.length - 1];
			return nbtPathNode.putIfAbsent(list, supplier);
		}

		private static int forEach(List<Tag> list, Function<Tag, Integer> function) {
			return (Integer)list.stream().map(function).reduce(0, (integer, integer2) -> integer + integer2);
		}

		public int put(Tag tag, Supplier<Tag> supplier) throws CommandSyntaxException {
			List<Tag> list = this.getParents(tag);
			NbtPathArgumentType.NbtPathNode nbtPathNode = this.nodes[this.nodes.length - 1];
			return forEach(list, tagx -> nbtPathNode.put(tagx, supplier));
		}

		public int remove(Tag tag) {
			List<Tag> list = Collections.singletonList(tag);

			for (int i = 0; i < this.nodes.length - 1; i++) {
				list = this.nodes[i].get(list);
			}

			NbtPathArgumentType.NbtPathNode nbtPathNode = this.nodes[this.nodes.length - 1];
			return forEach(list, nbtPathNode::remove);
		}

		private CommandSyntaxException createNothingFoundException(NbtPathArgumentType.NbtPathNode nbtPathNode) {
			int i = this.nodeEndIndices.getInt(nbtPathNode);
			return NbtPathArgumentType.NOTHING_FOUND_EXCEPTION.create(this.string.substring(0, i));
		}

		public String toString() {
			return this.string;
		}
	}

	interface NbtPathNode {
		void get(Tag tag, List<Tag> list);

		void putIfAbsent(Tag tag, Supplier<Tag> supplier, List<Tag> list);

		Tag createParent();

		int put(Tag tag, Supplier<Tag> supplier);

		int remove(Tag tag);

		default List<Tag> get(List<Tag> list) {
			return this.get(list, this::get);
		}

		default List<Tag> putIfAbsent(List<Tag> list, Supplier<Tag> supplier) {
			return this.get(list, (tag, listx) -> this.putIfAbsent(tag, supplier, listx));
		}

		default List<Tag> get(List<Tag> list, BiConsumer<Tag, List<Tag>> biConsumer) {
			List<Tag> list2 = Lists.<Tag>newArrayList();

			for (Tag tag : list) {
				biConsumer.accept(tag, list2);
			}

			return list2;
		}
	}
}
