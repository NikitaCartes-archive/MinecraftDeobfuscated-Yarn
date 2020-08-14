package net.minecraft.command.argument;

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
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.nbt.Tag;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class NbtPathArgumentType implements ArgumentType<NbtPathArgumentType.NbtPath> {
	private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo.bar", "foo[0]", "[0]", "[]", "{foo=bar}");
	public static final SimpleCommandExceptionType INVALID_PATH_NODE_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableText("arguments.nbtpath.node.invalid")
	);
	public static final DynamicCommandExceptionType NOTHING_FOUND_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableText("arguments.nbtpath.nothing_found", object)
	);

	public static NbtPathArgumentType nbtPath() {
		return new NbtPathArgumentType();
	}

	public static NbtPathArgumentType.NbtPath getNbtPath(CommandContext<ServerCommandSource> context, String name) {
		return context.getArgument(name, NbtPathArgumentType.NbtPath.class);
	}

	public NbtPathArgumentType.NbtPath parse(StringReader stringReader) throws CommandSyntaxException {
		List<NbtPathArgumentType.PathNode> list = Lists.<NbtPathArgumentType.PathNode>newArrayList();
		int i = stringReader.getCursor();
		Object2IntMap<NbtPathArgumentType.PathNode> object2IntMap = new Object2IntOpenHashMap<>();
		boolean bl = true;

		while (stringReader.canRead() && stringReader.peek() != ' ') {
			NbtPathArgumentType.PathNode pathNode = parseNode(stringReader, bl);
			list.add(pathNode);
			object2IntMap.put(pathNode, stringReader.getCursor() - i);
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
			(NbtPathArgumentType.PathNode[])list.toArray(new NbtPathArgumentType.PathNode[0]),
			object2IntMap
		);
	}

	private static NbtPathArgumentType.PathNode parseNode(StringReader reader, boolean root) throws CommandSyntaxException {
		switch (reader.peek()) {
			case '"': {
				String string = reader.readString();
				return readCompoundChildNode(reader, string);
			}
			case '[':
				reader.skip();
				int i = reader.peek();
				if (i == 123) {
					CompoundTag compoundTag2 = new StringNbtReader(reader).parseCompoundTag();
					reader.expect(']');
					return new NbtPathArgumentType.FilteredListElementNode(compoundTag2);
				} else {
					if (i == 93) {
						reader.skip();
						return NbtPathArgumentType.AllListElementNode.INSTANCE;
					}

					int j = reader.readInt();
					reader.expect(']');
					return new NbtPathArgumentType.IndexedListElementNode(j);
				}
			case '{':
				if (!root) {
					throw INVALID_PATH_NODE_EXCEPTION.createWithContext(reader);
				}

				CompoundTag compoundTag = new StringNbtReader(reader).parseCompoundTag();
				return new NbtPathArgumentType.FilteredRootNode(compoundTag);
			default: {
				String string = readName(reader);
				return readCompoundChildNode(reader, string);
			}
		}
	}

	private static NbtPathArgumentType.PathNode readCompoundChildNode(StringReader reader, String name) throws CommandSyntaxException {
		if (reader.canRead() && reader.peek() == '{') {
			CompoundTag compoundTag = new StringNbtReader(reader).parseCompoundTag();
			return new NbtPathArgumentType.FilteredNamedNode(name, compoundTag);
		} else {
			return new NbtPathArgumentType.NamedNode(name);
		}
	}

	private static String readName(StringReader reader) throws CommandSyntaxException {
		int i = reader.getCursor();

		while (reader.canRead() && isNameCharacter(reader.peek())) {
			reader.skip();
		}

		if (reader.getCursor() == i) {
			throw INVALID_PATH_NODE_EXCEPTION.createWithContext(reader);
		} else {
			return reader.getString().substring(i, reader.getCursor());
		}
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}

	private static boolean isNameCharacter(char c) {
		return c != ' ' && c != '"' && c != '[' && c != ']' && c != '.' && c != '{' && c != '}';
	}

	private static Predicate<Tag> getPredicate(CompoundTag filter) {
		return tag -> NbtHelper.matches(filter, tag, true);
	}

	static class AllListElementNode implements NbtPathArgumentType.PathNode {
		public static final NbtPathArgumentType.AllListElementNode INSTANCE = new NbtPathArgumentType.AllListElementNode();

		private AllListElementNode() {
		}

		@Override
		public void get(Tag current, List<Tag> results) {
			if (current instanceof AbstractListTag) {
				results.addAll((AbstractListTag)current);
			}
		}

		@Override
		public void getOrInit(Tag current, Supplier<Tag> source, List<Tag> results) {
			if (current instanceof AbstractListTag) {
				AbstractListTag<?> abstractListTag = (AbstractListTag<?>)current;
				if (abstractListTag.isEmpty()) {
					Tag tag = (Tag)source.get();
					if (abstractListTag.addTag(0, tag)) {
						results.add(tag);
					}
				} else {
					results.addAll(abstractListTag);
				}
			}
		}

		@Override
		public Tag init() {
			return new ListTag();
		}

		@Override
		public int set(Tag current, Supplier<Tag> source) {
			if (!(current instanceof AbstractListTag)) {
				return 0;
			} else {
				AbstractListTag<?> abstractListTag = (AbstractListTag<?>)current;
				int i = abstractListTag.size();
				if (i == 0) {
					abstractListTag.addTag(0, (Tag)source.get());
					return 1;
				} else {
					Tag tag = (Tag)source.get();
					int j = i - (int)abstractListTag.stream().filter(tag::equals).count();
					if (j == 0) {
						return 0;
					} else {
						abstractListTag.clear();
						if (!abstractListTag.addTag(0, tag)) {
							return 0;
						} else {
							for (int k = 1; k < i; k++) {
								abstractListTag.addTag(k, (Tag)source.get());
							}

							return j;
						}
					}
				}
			}
		}

		@Override
		public int clear(Tag current) {
			if (current instanceof AbstractListTag) {
				AbstractListTag<?> abstractListTag = (AbstractListTag<?>)current;
				int i = abstractListTag.size();
				if (i > 0) {
					abstractListTag.clear();
					return i;
				}
			}

			return 0;
		}
	}

	static class FilteredListElementNode implements NbtPathArgumentType.PathNode {
		private final CompoundTag filter;
		private final Predicate<Tag> predicate;

		public FilteredListElementNode(CompoundTag filter) {
			this.filter = filter;
			this.predicate = NbtPathArgumentType.getPredicate(filter);
		}

		@Override
		public void get(Tag current, List<Tag> results) {
			if (current instanceof ListTag) {
				ListTag listTag = (ListTag)current;
				listTag.stream().filter(this.predicate).forEach(results::add);
			}
		}

		@Override
		public void getOrInit(Tag current, Supplier<Tag> source, List<Tag> results) {
			MutableBoolean mutableBoolean = new MutableBoolean();
			if (current instanceof ListTag) {
				ListTag listTag = (ListTag)current;
				listTag.stream().filter(this.predicate).forEach(tag -> {
					results.add(tag);
					mutableBoolean.setTrue();
				});
				if (mutableBoolean.isFalse()) {
					CompoundTag compoundTag = this.filter.copy();
					listTag.add(compoundTag);
					results.add(compoundTag);
				}
			}
		}

		@Override
		public Tag init() {
			return new ListTag();
		}

		@Override
		public int set(Tag current, Supplier<Tag> source) {
			int i = 0;
			if (current instanceof ListTag) {
				ListTag listTag = (ListTag)current;
				int j = listTag.size();
				if (j == 0) {
					listTag.add(source.get());
					i++;
				} else {
					for (int k = 0; k < j; k++) {
						Tag tag = listTag.get(k);
						if (this.predicate.test(tag)) {
							Tag tag2 = (Tag)source.get();
							if (!tag2.equals(tag) && listTag.setTag(k, tag2)) {
								i++;
							}
						}
					}
				}
			}

			return i;
		}

		@Override
		public int clear(Tag current) {
			int i = 0;
			if (current instanceof ListTag) {
				ListTag listTag = (ListTag)current;

				for (int j = listTag.size() - 1; j >= 0; j--) {
					if (this.predicate.test(listTag.get(j))) {
						listTag.remove(j);
						i++;
					}
				}
			}

			return i;
		}
	}

	static class FilteredNamedNode implements NbtPathArgumentType.PathNode {
		private final String name;
		private final CompoundTag filter;
		private final Predicate<Tag> predicate;

		public FilteredNamedNode(String name, CompoundTag filter) {
			this.name = name;
			this.filter = filter;
			this.predicate = NbtPathArgumentType.getPredicate(filter);
		}

		@Override
		public void get(Tag current, List<Tag> results) {
			if (current instanceof CompoundTag) {
				Tag tag = ((CompoundTag)current).get(this.name);
				if (this.predicate.test(tag)) {
					results.add(tag);
				}
			}
		}

		@Override
		public void getOrInit(Tag current, Supplier<Tag> source, List<Tag> results) {
			if (current instanceof CompoundTag) {
				CompoundTag compoundTag = (CompoundTag)current;
				Tag tag = compoundTag.get(this.name);
				if (tag == null) {
					Tag var6 = this.filter.copy();
					compoundTag.put(this.name, var6);
					results.add(var6);
				} else if (this.predicate.test(tag)) {
					results.add(tag);
				}
			}
		}

		@Override
		public Tag init() {
			return new CompoundTag();
		}

		@Override
		public int set(Tag current, Supplier<Tag> source) {
			if (current instanceof CompoundTag) {
				CompoundTag compoundTag = (CompoundTag)current;
				Tag tag = compoundTag.get(this.name);
				if (this.predicate.test(tag)) {
					Tag tag2 = (Tag)source.get();
					if (!tag2.equals(tag)) {
						compoundTag.put(this.name, tag2);
						return 1;
					}
				}
			}

			return 0;
		}

		@Override
		public int clear(Tag current) {
			if (current instanceof CompoundTag) {
				CompoundTag compoundTag = (CompoundTag)current;
				Tag tag = compoundTag.get(this.name);
				if (this.predicate.test(tag)) {
					compoundTag.remove(this.name);
					return 1;
				}
			}

			return 0;
		}
	}

	static class FilteredRootNode implements NbtPathArgumentType.PathNode {
		private final Predicate<Tag> matcher;

		public FilteredRootNode(CompoundTag filter) {
			this.matcher = NbtPathArgumentType.getPredicate(filter);
		}

		@Override
		public void get(Tag current, List<Tag> results) {
			if (current instanceof CompoundTag && this.matcher.test(current)) {
				results.add(current);
			}
		}

		@Override
		public void getOrInit(Tag current, Supplier<Tag> source, List<Tag> results) {
			this.get(current, results);
		}

		@Override
		public Tag init() {
			return new CompoundTag();
		}

		@Override
		public int set(Tag current, Supplier<Tag> source) {
			return 0;
		}

		@Override
		public int clear(Tag current) {
			return 0;
		}
	}

	static class IndexedListElementNode implements NbtPathArgumentType.PathNode {
		private final int index;

		public IndexedListElementNode(int index) {
			this.index = index;
		}

		@Override
		public void get(Tag current, List<Tag> results) {
			if (current instanceof AbstractListTag) {
				AbstractListTag<?> abstractListTag = (AbstractListTag<?>)current;
				int i = abstractListTag.size();
				int j = this.index < 0 ? i + this.index : this.index;
				if (0 <= j && j < i) {
					results.add(abstractListTag.get(j));
				}
			}
		}

		@Override
		public void getOrInit(Tag current, Supplier<Tag> source, List<Tag> results) {
			this.get(current, results);
		}

		@Override
		public Tag init() {
			return new ListTag();
		}

		@Override
		public int set(Tag current, Supplier<Tag> source) {
			if (current instanceof AbstractListTag) {
				AbstractListTag<?> abstractListTag = (AbstractListTag<?>)current;
				int i = abstractListTag.size();
				int j = this.index < 0 ? i + this.index : this.index;
				if (0 <= j && j < i) {
					Tag tag = (Tag)abstractListTag.get(j);
					Tag tag2 = (Tag)source.get();
					if (!tag2.equals(tag) && abstractListTag.setTag(j, tag2)) {
						return 1;
					}
				}
			}

			return 0;
		}

		@Override
		public int clear(Tag current) {
			if (current instanceof AbstractListTag) {
				AbstractListTag<?> abstractListTag = (AbstractListTag<?>)current;
				int i = abstractListTag.size();
				int j = this.index < 0 ? i + this.index : this.index;
				if (0 <= j && j < i) {
					abstractListTag.remove(j);
					return 1;
				}
			}

			return 0;
		}
	}

	static class NamedNode implements NbtPathArgumentType.PathNode {
		private final String name;

		public NamedNode(String name) {
			this.name = name;
		}

		@Override
		public void get(Tag current, List<Tag> results) {
			if (current instanceof CompoundTag) {
				Tag tag = ((CompoundTag)current).get(this.name);
				if (tag != null) {
					results.add(tag);
				}
			}
		}

		@Override
		public void getOrInit(Tag current, Supplier<Tag> source, List<Tag> results) {
			if (current instanceof CompoundTag) {
				CompoundTag compoundTag = (CompoundTag)current;
				Tag tag;
				if (compoundTag.contains(this.name)) {
					tag = compoundTag.get(this.name);
				} else {
					tag = (Tag)source.get();
					compoundTag.put(this.name, tag);
				}

				results.add(tag);
			}
		}

		@Override
		public Tag init() {
			return new CompoundTag();
		}

		@Override
		public int set(Tag current, Supplier<Tag> source) {
			if (current instanceof CompoundTag) {
				CompoundTag compoundTag = (CompoundTag)current;
				Tag tag = (Tag)source.get();
				Tag tag2 = compoundTag.put(this.name, tag);
				if (!tag.equals(tag2)) {
					return 1;
				}
			}

			return 0;
		}

		@Override
		public int clear(Tag current) {
			if (current instanceof CompoundTag) {
				CompoundTag compoundTag = (CompoundTag)current;
				if (compoundTag.contains(this.name)) {
					compoundTag.remove(this.name);
					return 1;
				}
			}

			return 0;
		}
	}

	public static class NbtPath {
		private final String string;
		private final Object2IntMap<NbtPathArgumentType.PathNode> nodeEndIndices;
		private final NbtPathArgumentType.PathNode[] nodes;

		public NbtPath(String string, NbtPathArgumentType.PathNode[] nodes, Object2IntMap<NbtPathArgumentType.PathNode> nodeEndIndices) {
			this.string = string;
			this.nodes = nodes;
			this.nodeEndIndices = nodeEndIndices;
		}

		public List<Tag> get(Tag tag) throws CommandSyntaxException {
			List<Tag> list = Collections.singletonList(tag);

			for (NbtPathArgumentType.PathNode pathNode : this.nodes) {
				list = pathNode.get(list);
				if (list.isEmpty()) {
					throw this.createNothingFoundException(pathNode);
				}
			}

			return list;
		}

		public int count(Tag tag) {
			List<Tag> list = Collections.singletonList(tag);

			for (NbtPathArgumentType.PathNode pathNode : this.nodes) {
				list = pathNode.get(list);
				if (list.isEmpty()) {
					return 0;
				}
			}

			return list.size();
		}

		private List<Tag> getTerminals(Tag start) throws CommandSyntaxException {
			List<Tag> list = Collections.singletonList(start);

			for (int i = 0; i < this.nodes.length - 1; i++) {
				NbtPathArgumentType.PathNode pathNode = this.nodes[i];
				int j = i + 1;
				list = pathNode.getOrInit(list, this.nodes[j]::init);
				if (list.isEmpty()) {
					throw this.createNothingFoundException(pathNode);
				}
			}

			return list;
		}

		public List<Tag> getOrInit(Tag tag, Supplier<Tag> source) throws CommandSyntaxException {
			List<Tag> list = this.getTerminals(tag);
			NbtPathArgumentType.PathNode pathNode = this.nodes[this.nodes.length - 1];
			return pathNode.getOrInit(list, source);
		}

		private static int forEach(List<Tag> tags, Function<Tag, Integer> operation) {
			return (Integer)tags.stream().map(operation).reduce(0, (integer, integer2) -> integer + integer2);
		}

		public int put(Tag tag, Supplier<Tag> source) throws CommandSyntaxException {
			List<Tag> list = this.getTerminals(tag);
			NbtPathArgumentType.PathNode pathNode = this.nodes[this.nodes.length - 1];
			return forEach(list, tagx -> pathNode.set(tagx, source));
		}

		public int remove(Tag tag) {
			List<Tag> list = Collections.singletonList(tag);

			for (int i = 0; i < this.nodes.length - 1; i++) {
				list = this.nodes[i].get(list);
			}

			NbtPathArgumentType.PathNode pathNode = this.nodes[this.nodes.length - 1];
			return forEach(list, pathNode::clear);
		}

		private CommandSyntaxException createNothingFoundException(NbtPathArgumentType.PathNode node) {
			int i = this.nodeEndIndices.getInt(node);
			return NbtPathArgumentType.NOTHING_FOUND_EXCEPTION.create(this.string.substring(0, i));
		}

		public String toString() {
			return this.string;
		}
	}

	interface PathNode {
		void get(Tag current, List<Tag> results);

		void getOrInit(Tag current, Supplier<Tag> source, List<Tag> results);

		Tag init();

		int set(Tag current, Supplier<Tag> source);

		int clear(Tag current);

		default List<Tag> get(List<Tag> tags) {
			return this.process(tags, this::get);
		}

		default List<Tag> getOrInit(List<Tag> tags, Supplier<Tag> supplier) {
			return this.process(tags, (current, results) -> this.getOrInit(current, supplier, results));
		}

		default List<Tag> process(List<Tag> tags, BiConsumer<Tag, List<Tag>> action) {
			List<Tag> list = Lists.<Tag>newArrayList();

			for (Tag tag : tags) {
				action.accept(tag, list);
			}

			return list;
		}
	}
}
