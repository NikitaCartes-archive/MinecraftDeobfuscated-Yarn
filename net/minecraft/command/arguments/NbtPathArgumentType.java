/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
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
import java.util.ArrayList;
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

public class NbtPathArgumentType
implements ArgumentType<NbtPath> {
    private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo.bar", "foo[0]", "[0]", "[]", "{foo=bar}");
    public static final SimpleCommandExceptionType INVALID_PATH_NODE_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("arguments.nbtpath.node.invalid"));
    public static final DynamicCommandExceptionType NOTHING_FOUND_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("arguments.nbtpath.nothing_found", object));

    public static NbtPathArgumentType nbtPath() {
        return new NbtPathArgumentType();
    }

    public static NbtPath getNbtPath(CommandContext<ServerCommandSource> context, String name) {
        return context.getArgument(name, NbtPath.class);
    }

    @Override
    public NbtPath parse(StringReader stringReader) throws CommandSyntaxException {
        ArrayList<PathNode> list = Lists.newArrayList();
        int i = stringReader.getCursor();
        Object2IntOpenHashMap<PathNode> object2IntMap = new Object2IntOpenHashMap<PathNode>();
        boolean bl = true;
        while (stringReader.canRead() && stringReader.peek() != ' ') {
            char c;
            PathNode pathNode = NbtPathArgumentType.parseNode(stringReader, bl);
            list.add(pathNode);
            object2IntMap.put(pathNode, stringReader.getCursor() - i);
            bl = false;
            if (!stringReader.canRead() || (c = stringReader.peek()) == ' ' || c == '[' || c == '{') continue;
            stringReader.expect('.');
        }
        return new NbtPath(stringReader.getString().substring(i, stringReader.getCursor()), list.toArray(new PathNode[0]), object2IntMap);
    }

    private static PathNode parseNode(StringReader reader, boolean root) throws CommandSyntaxException {
        switch (reader.peek()) {
            case '{': {
                if (!root) {
                    throw INVALID_PATH_NODE_EXCEPTION.createWithContext(reader);
                }
                CompoundTag compoundTag = new StringNbtReader(reader).parseCompoundTag();
                return new FilteredRootNode(compoundTag);
            }
            case '[': {
                reader.skip();
                char i = reader.peek();
                if (i == '{') {
                    CompoundTag compoundTag2 = new StringNbtReader(reader).parseCompoundTag();
                    reader.expect(']');
                    return new FilteredListElementNode(compoundTag2);
                }
                if (i == ']') {
                    reader.skip();
                    return AllListElementNode.INSTANCE;
                }
                int j = reader.readInt();
                reader.expect(']');
                return new IndexedListElementNode(j);
            }
            case '\"': {
                String string = reader.readString();
                return NbtPathArgumentType.readCompoundChildNode(reader, string);
            }
        }
        String string = NbtPathArgumentType.readName(reader);
        return NbtPathArgumentType.readCompoundChildNode(reader, string);
    }

    private static PathNode readCompoundChildNode(StringReader reader, String name) throws CommandSyntaxException {
        if (reader.canRead() && reader.peek() == '{') {
            CompoundTag compoundTag = new StringNbtReader(reader).parseCompoundTag();
            return new FilteredNamedNode(name, compoundTag);
        }
        return new NamedNode(name);
    }

    private static String readName(StringReader reader) throws CommandSyntaxException {
        int i = reader.getCursor();
        while (reader.canRead() && NbtPathArgumentType.isNameCharacter(reader.peek())) {
            reader.skip();
        }
        if (reader.getCursor() == i) {
            throw INVALID_PATH_NODE_EXCEPTION.createWithContext(reader);
        }
        return reader.getString().substring(i, reader.getCursor());
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    private static boolean isNameCharacter(char c) {
        return c != ' ' && c != '\"' && c != '[' && c != ']' && c != '.' && c != '{' && c != '}';
    }

    private static Predicate<Tag> getPredicate(CompoundTag filter) {
        return tag -> NbtHelper.matches(filter, tag, true);
    }

    @Override
    public /* synthetic */ Object parse(StringReader reader) throws CommandSyntaxException {
        return this.parse(reader);
    }

    static class FilteredRootNode
    implements PathNode {
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

    static class FilteredNamedNode
    implements PathNode {
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
            Tag tag;
            if (current instanceof CompoundTag && this.predicate.test(tag = ((CompoundTag)current).get(this.name))) {
                results.add(tag);
            }
        }

        @Override
        public void getOrInit(Tag current, Supplier<Tag> source, List<Tag> results) {
            if (current instanceof CompoundTag) {
                CompoundTag compoundTag = (CompoundTag)current;
                Tag tag = compoundTag.get(this.name);
                if (tag == null) {
                    tag = this.filter.copy();
                    compoundTag.put(this.name, tag);
                    results.add(tag);
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
            Tag tag2;
            CompoundTag compoundTag;
            Tag tag;
            if (current instanceof CompoundTag && this.predicate.test(tag = (compoundTag = (CompoundTag)current).get(this.name)) && !(tag2 = source.get()).equals(tag)) {
                compoundTag.put(this.name, tag2);
                return 1;
            }
            return 0;
        }

        @Override
        public int clear(Tag current) {
            CompoundTag compoundTag;
            Tag tag;
            if (current instanceof CompoundTag && this.predicate.test(tag = (compoundTag = (CompoundTag)current).get(this.name))) {
                compoundTag.remove(this.name);
                return 1;
            }
            return 0;
        }
    }

    static class AllListElementNode
    implements PathNode {
        public static final AllListElementNode INSTANCE = new AllListElementNode();

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
                AbstractListTag abstractListTag = (AbstractListTag)current;
                if (abstractListTag.isEmpty()) {
                    Tag tag = source.get();
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
            if (current instanceof AbstractListTag) {
                AbstractListTag abstractListTag = (AbstractListTag)current;
                int i = abstractListTag.size();
                if (i == 0) {
                    abstractListTag.addTag(0, source.get());
                    return 1;
                }
                Tag tag = source.get();
                int j = i - (int)abstractListTag.stream().filter(tag::equals).count();
                if (j == 0) {
                    return 0;
                }
                abstractListTag.clear();
                if (!abstractListTag.addTag(0, tag)) {
                    return 0;
                }
                for (int k = 1; k < i; ++k) {
                    abstractListTag.addTag(k, source.get());
                }
                return j;
            }
            return 0;
        }

        @Override
        public int clear(Tag current) {
            AbstractListTag abstractListTag;
            int i;
            if (current instanceof AbstractListTag && (i = (abstractListTag = (AbstractListTag)current).size()) > 0) {
                abstractListTag.clear();
                return i;
            }
            return 0;
        }
    }

    static class FilteredListElementNode
    implements PathNode {
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
                    results.add((Tag)tag);
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
                    ++i;
                } else {
                    for (int k = 0; k < j; ++k) {
                        Tag tag2;
                        Tag tag = listTag.get(k);
                        if (!this.predicate.test(tag) || (tag2 = source.get()).equals(tag) || !listTag.setTag(k, tag2)) continue;
                        ++i;
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
                for (int j = listTag.size() - 1; j >= 0; --j) {
                    if (!this.predicate.test(listTag.get(j))) continue;
                    listTag.remove(j);
                    ++i;
                }
            }
            return i;
        }
    }

    static class IndexedListElementNode
    implements PathNode {
        private final int index;

        public IndexedListElementNode(int index) {
            this.index = index;
        }

        @Override
        public void get(Tag current, List<Tag> results) {
            if (current instanceof AbstractListTag) {
                int j;
                AbstractListTag abstractListTag = (AbstractListTag)current;
                int i = abstractListTag.size();
                int n = j = this.index < 0 ? i + this.index : this.index;
                if (0 <= j && j < i) {
                    results.add((Tag)abstractListTag.get(j));
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
                int j;
                AbstractListTag abstractListTag = (AbstractListTag)current;
                int i = abstractListTag.size();
                int n = j = this.index < 0 ? i + this.index : this.index;
                if (0 <= j && j < i) {
                    Tag tag = (Tag)abstractListTag.get(j);
                    Tag tag2 = source.get();
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
                int j;
                AbstractListTag abstractListTag = (AbstractListTag)current;
                int i = abstractListTag.size();
                int n = j = this.index < 0 ? i + this.index : this.index;
                if (0 <= j && j < i) {
                    abstractListTag.remove(j);
                    return 1;
                }
            }
            return 0;
        }
    }

    static class NamedNode
    implements PathNode {
        private final String name;

        public NamedNode(String name) {
            this.name = name;
        }

        @Override
        public void get(Tag current, List<Tag> results) {
            Tag tag;
            if (current instanceof CompoundTag && (tag = ((CompoundTag)current).get(this.name)) != null) {
                results.add(tag);
            }
        }

        @Override
        public void getOrInit(Tag current, Supplier<Tag> source, List<Tag> results) {
            if (current instanceof CompoundTag) {
                Tag tag;
                CompoundTag compoundTag = (CompoundTag)current;
                if (compoundTag.contains(this.name)) {
                    tag = compoundTag.get(this.name);
                } else {
                    tag = source.get();
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
                Tag tag2;
                CompoundTag compoundTag = (CompoundTag)current;
                Tag tag = source.get();
                if (!tag.equals(tag2 = compoundTag.put(this.name, tag))) {
                    return 1;
                }
            }
            return 0;
        }

        @Override
        public int clear(Tag current) {
            CompoundTag compoundTag;
            if (current instanceof CompoundTag && (compoundTag = (CompoundTag)current).contains(this.name)) {
                compoundTag.remove(this.name);
                return 1;
            }
            return 0;
        }
    }

    static interface PathNode {
        public void get(Tag var1, List<Tag> var2);

        public void getOrInit(Tag var1, Supplier<Tag> var2, List<Tag> var3);

        public Tag init();

        public int set(Tag var1, Supplier<Tag> var2);

        public int clear(Tag var1);

        default public List<Tag> get(List<Tag> tags) {
            return this.process(tags, this::get);
        }

        default public List<Tag> getOrInit(List<Tag> tags, Supplier<Tag> supplier) {
            return this.process(tags, (current, results) -> this.getOrInit((Tag)current, supplier, (List<Tag>)results));
        }

        default public List<Tag> process(List<Tag> tags, BiConsumer<Tag, List<Tag>> action) {
            ArrayList<Tag> list = Lists.newArrayList();
            for (Tag tag : tags) {
                action.accept(tag, list);
            }
            return list;
        }
    }

    public static class NbtPath {
        private final String string;
        private final Object2IntMap<PathNode> nodeEndIndices;
        private final PathNode[] nodes;

        public NbtPath(String string, PathNode[] nodes, Object2IntMap<PathNode> nodeEndIndices) {
            this.string = string;
            this.nodes = nodes;
            this.nodeEndIndices = nodeEndIndices;
        }

        public List<Tag> get(Tag tag) throws CommandSyntaxException {
            List<Tag> list = Collections.singletonList(tag);
            for (PathNode pathNode : this.nodes) {
                if (!(list = pathNode.get(list)).isEmpty()) continue;
                throw this.createNothingFoundException(pathNode);
            }
            return list;
        }

        public int count(Tag tag) {
            List<Tag> list = Collections.singletonList(tag);
            for (PathNode pathNode : this.nodes) {
                if (!(list = pathNode.get(list)).isEmpty()) continue;
                return 0;
            }
            return list.size();
        }

        private List<Tag> getTerminals(Tag start) throws CommandSyntaxException {
            List<Tag> list = Collections.singletonList(start);
            for (int i = 0; i < this.nodes.length - 1; ++i) {
                PathNode pathNode = this.nodes[i];
                int j = i + 1;
                if (!(list = pathNode.getOrInit(list, this.nodes[j]::init)).isEmpty()) continue;
                throw this.createNothingFoundException(pathNode);
            }
            return list;
        }

        public List<Tag> getOrInit(Tag tag, Supplier<Tag> source) throws CommandSyntaxException {
            List<Tag> list = this.getTerminals(tag);
            PathNode pathNode = this.nodes[this.nodes.length - 1];
            return pathNode.getOrInit(list, source);
        }

        private static int forEach(List<Tag> tags, Function<Tag, Integer> operation) {
            return tags.stream().map(operation).reduce(0, (integer, integer2) -> integer + integer2);
        }

        public int put(Tag tag2, Supplier<Tag> source) throws CommandSyntaxException {
            List<Tag> list = this.getTerminals(tag2);
            PathNode pathNode = this.nodes[this.nodes.length - 1];
            return NbtPath.forEach(list, tag -> pathNode.set((Tag)tag, source));
        }

        public int remove(Tag tag) {
            List<Tag> list = Collections.singletonList(tag);
            for (int i = 0; i < this.nodes.length - 1; ++i) {
                list = this.nodes[i].get(list);
            }
            PathNode pathNode = this.nodes[this.nodes.length - 1];
            return NbtPath.forEach(list, pathNode::clear);
        }

        private CommandSyntaxException createNothingFoundException(PathNode node) {
            int i = this.nodeEndIndices.getInt(node);
            return NOTHING_FOUND_EXCEPTION.create(this.string.substring(0, i));
        }

        public String toString() {
            return this.string;
        }
    }
}

