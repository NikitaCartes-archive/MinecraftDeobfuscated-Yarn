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
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.nbt.Tag;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.TagHelper;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class NbtPathArgumentType
implements ArgumentType<NbtPath> {
    private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo.bar", "foo[0]", "[0]", "[]", "{foo=bar}");
    public static final SimpleCommandExceptionType INVALID_PATH_NODE_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("arguments.nbtpath.node.invalid", new Object[0]));
    public static final DynamicCommandExceptionType NOTHING_FOUND_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("arguments.nbtpath.nothing_found", object));

    public static NbtPathArgumentType create() {
        return new NbtPathArgumentType();
    }

    public static NbtPath getNbtPath(CommandContext<ServerCommandSource> commandContext, String string) {
        return commandContext.getArgument(string, NbtPath.class);
    }

    public NbtPath method_9362(StringReader stringReader) throws CommandSyntaxException {
        ArrayList<NbtPathNode> list = Lists.newArrayList();
        int i = stringReader.getCursor();
        Object2IntOpenHashMap<NbtPathNode> object2IntMap = new Object2IntOpenHashMap<NbtPathNode>();
        boolean bl = true;
        while (stringReader.canRead() && stringReader.peek() != ' ') {
            char c;
            NbtPathNode nbtPathNode = NbtPathArgumentType.parseNode(stringReader, bl);
            list.add(nbtPathNode);
            object2IntMap.put(nbtPathNode, stringReader.getCursor() - i);
            bl = false;
            if (!stringReader.canRead() || (c = stringReader.peek()) == ' ' || c == '[' || c == '{') continue;
            stringReader.expect('.');
        }
        return new NbtPath(stringReader.getString().substring(i, stringReader.getCursor()), list.toArray(new NbtPathNode[0]), object2IntMap);
    }

    private static NbtPathNode parseNode(StringReader stringReader, boolean bl) throws CommandSyntaxException {
        switch (stringReader.peek()) {
            case '{': {
                if (!bl) {
                    throw INVALID_PATH_NODE_EXCEPTION.createWithContext(stringReader);
                }
                CompoundTag compoundTag = new StringNbtReader(stringReader).parseCompoundTag();
                return new EqualCompoundNode(compoundTag);
            }
            case '[': {
                stringReader.skip();
                char i = stringReader.peek();
                if (i == '{') {
                    CompoundTag compoundTag2 = new StringNbtReader(stringReader).parseCompoundTag();
                    stringReader.expect(']');
                    return new EqualListElementNode(compoundTag2);
                }
                if (i == ']') {
                    stringReader.skip();
                    return AllListElementsNode.INSTANCE;
                }
                int j = stringReader.readInt();
                stringReader.expect(']');
                return new ListIndexNode(j);
            }
            case '\"': {
                String string = stringReader.readString();
                return NbtPathArgumentType.readCompoundChildNode(stringReader, string);
            }
        }
        String string = NbtPathArgumentType.readName(stringReader);
        return NbtPathArgumentType.readCompoundChildNode(stringReader, string);
    }

    private static NbtPathNode readCompoundChildNode(StringReader stringReader, String string) throws CommandSyntaxException {
        if (stringReader.canRead() && stringReader.peek() == '{') {
            CompoundTag compoundTag = new StringNbtReader(stringReader).parseCompoundTag();
            return new EqualCompundChildNode(string, compoundTag);
        }
        return new CompoundChildNode(string);
    }

    private static String readName(StringReader stringReader) throws CommandSyntaxException {
        int i = stringReader.getCursor();
        while (stringReader.canRead() && NbtPathArgumentType.isNameCharacter(stringReader.peek())) {
            stringReader.skip();
        }
        if (stringReader.getCursor() == i) {
            throw INVALID_PATH_NODE_EXCEPTION.createWithContext(stringReader);
        }
        return stringReader.getString().substring(i, stringReader.getCursor());
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    private static boolean isNameCharacter(char c) {
        return c != ' ' && c != '\"' && c != '[' && c != ']' && c != '.' && c != '{' && c != '}';
    }

    private static Predicate<Tag> getEqualityPredicate(CompoundTag compoundTag) {
        return tag -> TagHelper.areTagsEqual(compoundTag, tag, true);
    }

    @Override
    public /* synthetic */ Object parse(StringReader stringReader) throws CommandSyntaxException {
        return this.method_9362(stringReader);
    }

    static class EqualCompoundNode
    implements NbtPathNode {
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

    static class EqualCompundChildNode
    implements NbtPathNode {
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
            Tag tag2;
            if (tag instanceof CompoundTag && this.predicate.test(tag2 = ((CompoundTag)tag).getTag(this.name))) {
                list.add(tag2);
            }
        }

        @Override
        public void putIfAbsent(Tag tag, Supplier<Tag> supplier, List<Tag> list) {
            if (tag instanceof CompoundTag) {
                CompoundTag compoundTag = (CompoundTag)tag;
                Tag tag2 = compoundTag.getTag(this.name);
                if (tag2 == null) {
                    tag2 = this.tag.method_10553();
                    compoundTag.put(this.name, tag2);
                    list.add(tag2);
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
            Tag tag3;
            CompoundTag compoundTag;
            Tag tag2;
            if (tag instanceof CompoundTag && this.predicate.test(tag2 = (compoundTag = (CompoundTag)tag).getTag(this.name)) && !(tag3 = supplier.get()).equals(tag2)) {
                compoundTag.put(this.name, tag3);
                return 1;
            }
            return 0;
        }

        @Override
        public int remove(Tag tag) {
            CompoundTag compoundTag;
            Tag tag2;
            if (tag instanceof CompoundTag && this.predicate.test(tag2 = (compoundTag = (CompoundTag)tag).getTag(this.name))) {
                compoundTag.remove(this.name);
                return 1;
            }
            return 0;
        }
    }

    static class AllListElementsNode
    implements NbtPathNode {
        public static final AllListElementsNode INSTANCE = new AllListElementsNode();

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
                AbstractListTag abstractListTag = (AbstractListTag)tag;
                if (abstractListTag.isEmpty()) {
                    Tag tag2 = supplier.get();
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
            if (tag instanceof AbstractListTag) {
                AbstractListTag abstractListTag = (AbstractListTag)tag;
                int i = abstractListTag.size();
                if (i == 0) {
                    abstractListTag.addTag(0, supplier.get());
                    return 1;
                }
                Tag tag2 = supplier.get();
                int j = i - (int)abstractListTag.stream().filter(tag2::equals).count();
                if (j == 0) {
                    return 0;
                }
                abstractListTag.clear();
                if (!abstractListTag.addTag(0, tag2)) {
                    return 0;
                }
                for (int k = 1; k < i; ++k) {
                    abstractListTag.addTag(k, supplier.get());
                }
                return j;
            }
            return 0;
        }

        @Override
        public int remove(Tag tag) {
            AbstractListTag abstractListTag;
            int i;
            if (tag instanceof AbstractListTag && (i = (abstractListTag = (AbstractListTag)tag).size()) > 0) {
                abstractListTag.clear();
                return i;
            }
            return 0;
        }
    }

    static class EqualListElementNode
    implements NbtPathNode {
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
        public void putIfAbsent(Tag tag2, Supplier<Tag> supplier, List<Tag> list) {
            MutableBoolean mutableBoolean = new MutableBoolean();
            if (tag2 instanceof ListTag) {
                ListTag listTag = (ListTag)tag2;
                listTag.stream().filter(this.predicate).forEach(tag -> {
                    list.add((Tag)tag);
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
                    ++i;
                } else {
                    for (int k = 0; k < j; ++k) {
                        Tag tag3;
                        Tag tag2 = listTag.method_10534(k);
                        if (!this.predicate.test(tag2) || (tag3 = supplier.get()).equals(tag2) || !listTag.setTag(k, tag3)) continue;
                        ++i;
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
                for (int j = listTag.size() - 1; j >= 0; --j) {
                    if (!this.predicate.test(listTag.method_10534(j))) continue;
                    listTag.method_10536(j);
                    ++i;
                }
            }
            return i;
        }
    }

    static class ListIndexNode
    implements NbtPathNode {
        private final int index;

        public ListIndexNode(int i) {
            this.index = i;
        }

        @Override
        public void get(Tag tag, List<Tag> list) {
            if (tag instanceof AbstractListTag) {
                int j;
                AbstractListTag abstractListTag = (AbstractListTag)tag;
                int i = abstractListTag.size();
                int n = j = this.index < 0 ? i + this.index : this.index;
                if (0 <= j && j < i) {
                    list.add((Tag)abstractListTag.get(j));
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
                int j;
                AbstractListTag abstractListTag = (AbstractListTag)tag;
                int i = abstractListTag.size();
                int n = j = this.index < 0 ? i + this.index : this.index;
                if (0 <= j && j < i) {
                    Tag tag2 = (Tag)abstractListTag.get(j);
                    Tag tag3 = supplier.get();
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
                int j;
                AbstractListTag abstractListTag = (AbstractListTag)tag;
                int i = abstractListTag.size();
                int n = j = this.index < 0 ? i + this.index : this.index;
                if (0 <= j && j < i) {
                    abstractListTag.method_10536(j);
                    return 1;
                }
            }
            return 0;
        }
    }

    static class CompoundChildNode
    implements NbtPathNode {
        private final String name;

        public CompoundChildNode(String string) {
            this.name = string;
        }

        @Override
        public void get(Tag tag, List<Tag> list) {
            Tag tag2;
            if (tag instanceof CompoundTag && (tag2 = ((CompoundTag)tag).getTag(this.name)) != null) {
                list.add(tag2);
            }
        }

        @Override
        public void putIfAbsent(Tag tag, Supplier<Tag> supplier, List<Tag> list) {
            if (tag instanceof CompoundTag) {
                Tag tag2;
                CompoundTag compoundTag = (CompoundTag)tag;
                if (compoundTag.containsKey(this.name)) {
                    tag2 = compoundTag.getTag(this.name);
                } else {
                    tag2 = supplier.get();
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
                Tag tag3;
                CompoundTag compoundTag = (CompoundTag)tag;
                Tag tag2 = supplier.get();
                if (!tag2.equals(tag3 = compoundTag.put(this.name, tag2))) {
                    return 1;
                }
            }
            return 0;
        }

        @Override
        public int remove(Tag tag) {
            CompoundTag compoundTag;
            if (tag instanceof CompoundTag && (compoundTag = (CompoundTag)tag).containsKey(this.name)) {
                compoundTag.remove(this.name);
                return 1;
            }
            return 0;
        }
    }

    static interface NbtPathNode {
        public void get(Tag var1, List<Tag> var2);

        public void putIfAbsent(Tag var1, Supplier<Tag> var2, List<Tag> var3);

        public Tag createParent();

        public int put(Tag var1, Supplier<Tag> var2);

        public int remove(Tag var1);

        default public List<Tag> get(List<Tag> list) {
            return this.get(list, this::get);
        }

        default public List<Tag> putIfAbsent(List<Tag> list2, Supplier<Tag> supplier) {
            return this.get(list2, (Tag tag, List<Tag> list) -> this.putIfAbsent((Tag)tag, supplier, (List<Tag>)list));
        }

        default public List<Tag> get(List<Tag> list, BiConsumer<Tag, List<Tag>> biConsumer) {
            ArrayList<Tag> list2 = Lists.newArrayList();
            for (Tag tag : list) {
                biConsumer.accept(tag, list2);
            }
            return list2;
        }
    }

    public static class NbtPath {
        private final String string;
        private final Object2IntMap<NbtPathNode> nodeEndIndices;
        private final NbtPathNode[] nodes;

        public NbtPath(String string, NbtPathNode[] nbtPathNodes, Object2IntMap<NbtPathNode> object2IntMap) {
            this.string = string;
            this.nodes = nbtPathNodes;
            this.nodeEndIndices = object2IntMap;
        }

        public List<Tag> get(Tag tag) throws CommandSyntaxException {
            List<Tag> list = Collections.singletonList(tag);
            for (NbtPathNode nbtPathNode : this.nodes) {
                if (!(list = nbtPathNode.get(list)).isEmpty()) continue;
                throw this.createNothingFoundException(nbtPathNode);
            }
            return list;
        }

        public int count(Tag tag) {
            List<Tag> list = Collections.singletonList(tag);
            for (NbtPathNode nbtPathNode : this.nodes) {
                if (!(list = nbtPathNode.get(list)).isEmpty()) continue;
                return 0;
            }
            return list.size();
        }

        private List<Tag> getParents(Tag tag) throws CommandSyntaxException {
            List<Tag> list = Collections.singletonList(tag);
            for (int i = 0; i < this.nodes.length - 1; ++i) {
                NbtPathNode nbtPathNode = this.nodes[i];
                int j = i + 1;
                if (!(list = nbtPathNode.putIfAbsent(list, this.nodes[j]::createParent)).isEmpty()) continue;
                throw this.createNothingFoundException(nbtPathNode);
            }
            return list;
        }

        public List<Tag> putIfAbsent(Tag tag, Supplier<Tag> supplier) throws CommandSyntaxException {
            List<Tag> list = this.getParents(tag);
            NbtPathNode nbtPathNode = this.nodes[this.nodes.length - 1];
            return nbtPathNode.putIfAbsent(list, supplier);
        }

        private static int forEach(List<Tag> list, Function<Tag, Integer> function) {
            return list.stream().map(function).reduce(0, (integer, integer2) -> integer + integer2);
        }

        public int put(Tag tag2, Supplier<Tag> supplier) throws CommandSyntaxException {
            List<Tag> list = this.getParents(tag2);
            NbtPathNode nbtPathNode = this.nodes[this.nodes.length - 1];
            return NbtPath.forEach(list, tag -> nbtPathNode.put((Tag)tag, supplier));
        }

        public int remove(Tag tag) {
            List<Tag> list = Collections.singletonList(tag);
            for (int i = 0; i < this.nodes.length - 1; ++i) {
                list = this.nodes[i].get(list);
            }
            NbtPathNode nbtPathNode = this.nodes[this.nodes.length - 1];
            return NbtPath.forEach(list, nbtPathNode::remove);
        }

        private CommandSyntaxException createNothingFoundException(NbtPathNode nbtPathNode) {
            int i = this.nodeEndIndices.getInt(nbtPathNode);
            return NOTHING_FOUND_EXCEPTION.create(this.string.substring(0, i));
        }

        public String toString() {
            return this.string;
        }
    }
}

