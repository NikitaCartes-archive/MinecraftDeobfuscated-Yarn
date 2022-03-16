/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.nbt.AbstractNbtList;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class NbtPathArgumentType
implements ArgumentType<NbtPath> {
    private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo.bar", "foo[0]", "[0]", "[]", "{foo=bar}");
    public static final SimpleCommandExceptionType INVALID_PATH_NODE_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("arguments.nbtpath.node.invalid"));
    public static final DynamicCommandExceptionType NOTHING_FOUND_EXCEPTION = new DynamicCommandExceptionType(path -> new TranslatableText("arguments.nbtpath.nothing_found", path));
    private static final char LEFT_SQUARE_BRACKET = '[';
    private static final char RIGHT_SQUARE_BRACKET = ']';
    private static final char LEFT_CURLY_BRACKET = '{';
    private static final char RIGHT_CURLY_BRACKET = '}';
    private static final char DOUBLE_QUOTE = '\"';

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
                NbtCompound nbtCompound = new StringNbtReader(reader).parseCompound();
                return new FilteredRootNode(nbtCompound);
            }
            case '[': {
                reader.skip();
                char i = reader.peek();
                if (i == '{') {
                    NbtCompound nbtCompound2 = new StringNbtReader(reader).parseCompound();
                    reader.expect(']');
                    return new FilteredListElementNode(nbtCompound2);
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
            NbtCompound nbtCompound = new StringNbtReader(reader).parseCompound();
            return new FilteredNamedNode(name, nbtCompound);
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

    static Predicate<NbtElement> getPredicate(NbtCompound filter) {
        return nbt -> NbtHelper.matches(filter, nbt, true);
    }

    @Override
    public /* synthetic */ Object parse(StringReader reader) throws CommandSyntaxException {
        return this.parse(reader);
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

        public List<NbtElement> get(NbtElement element) throws CommandSyntaxException {
            List<NbtElement> list = Collections.singletonList(element);
            for (PathNode pathNode : this.nodes) {
                if (!(list = pathNode.get(list)).isEmpty()) continue;
                throw this.createNothingFoundException(pathNode);
            }
            return list;
        }

        public int count(NbtElement element) {
            List<NbtElement> list = Collections.singletonList(element);
            for (PathNode pathNode : this.nodes) {
                if (!(list = pathNode.get(list)).isEmpty()) continue;
                return 0;
            }
            return list.size();
        }

        private List<NbtElement> getTerminals(NbtElement start) throws CommandSyntaxException {
            List<NbtElement> list = Collections.singletonList(start);
            for (int i = 0; i < this.nodes.length - 1; ++i) {
                PathNode pathNode = this.nodes[i];
                int j = i + 1;
                if (!(list = pathNode.getOrInit(list, this.nodes[j]::init)).isEmpty()) continue;
                throw this.createNothingFoundException(pathNode);
            }
            return list;
        }

        public List<NbtElement> getOrInit(NbtElement element, Supplier<NbtElement> source) throws CommandSyntaxException {
            List<NbtElement> list = this.getTerminals(element);
            PathNode pathNode = this.nodes[this.nodes.length - 1];
            return pathNode.getOrInit(list, source);
        }

        private static int forEach(List<NbtElement> elements, Function<NbtElement, Integer> operation) {
            return elements.stream().map(operation).reduce(0, (a, b) -> a + b);
        }

        public int put(NbtElement element, NbtElement source) throws CommandSyntaxException {
            return this.put(element, source::copy);
        }

        public int put(NbtElement element, Supplier<NbtElement> source) throws CommandSyntaxException {
            List<NbtElement> list = this.getTerminals(element);
            PathNode pathNode = this.nodes[this.nodes.length - 1];
            return NbtPath.forEach(list, nbt -> pathNode.set((NbtElement)nbt, source));
        }

        public int remove(NbtElement element) {
            List<NbtElement> list = Collections.singletonList(element);
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

    static interface PathNode {
        public void get(NbtElement var1, List<NbtElement> var2);

        public void getOrInit(NbtElement var1, Supplier<NbtElement> var2, List<NbtElement> var3);

        public NbtElement init();

        public int set(NbtElement var1, Supplier<NbtElement> var2);

        public int clear(NbtElement var1);

        default public List<NbtElement> get(List<NbtElement> elements) {
            return this.process(elements, this::get);
        }

        default public List<NbtElement> getOrInit(List<NbtElement> elements, Supplier<NbtElement> supplier) {
            return this.process(elements, (current, results) -> this.getOrInit((NbtElement)current, supplier, (List<NbtElement>)results));
        }

        default public List<NbtElement> process(List<NbtElement> elements, BiConsumer<NbtElement, List<NbtElement>> action) {
            ArrayList<NbtElement> list = Lists.newArrayList();
            for (NbtElement nbtElement : elements) {
                action.accept(nbtElement, list);
            }
            return list;
        }
    }

    static class FilteredRootNode
    implements PathNode {
        private final Predicate<NbtElement> matcher;

        public FilteredRootNode(NbtCompound filter) {
            this.matcher = NbtPathArgumentType.getPredicate(filter);
        }

        @Override
        public void get(NbtElement current, List<NbtElement> results) {
            if (current instanceof NbtCompound && this.matcher.test(current)) {
                results.add(current);
            }
        }

        @Override
        public void getOrInit(NbtElement current, Supplier<NbtElement> source, List<NbtElement> results) {
            this.get(current, results);
        }

        @Override
        public NbtElement init() {
            return new NbtCompound();
        }

        @Override
        public int set(NbtElement current, Supplier<NbtElement> source) {
            return 0;
        }

        @Override
        public int clear(NbtElement current) {
            return 0;
        }
    }

    static class FilteredListElementNode
    implements PathNode {
        private final NbtCompound filter;
        private final Predicate<NbtElement> predicate;

        public FilteredListElementNode(NbtCompound filter) {
            this.filter = filter;
            this.predicate = NbtPathArgumentType.getPredicate(filter);
        }

        @Override
        public void get(NbtElement current, List<NbtElement> results) {
            if (current instanceof NbtList) {
                NbtList nbtList = (NbtList)current;
                nbtList.stream().filter(this.predicate).forEach(results::add);
            }
        }

        @Override
        public void getOrInit(NbtElement current, Supplier<NbtElement> source, List<NbtElement> results) {
            MutableBoolean mutableBoolean = new MutableBoolean();
            if (current instanceof NbtList) {
                NbtList nbtList = (NbtList)current;
                nbtList.stream().filter(this.predicate).forEach(nbt -> {
                    results.add((NbtElement)nbt);
                    mutableBoolean.setTrue();
                });
                if (mutableBoolean.isFalse()) {
                    NbtCompound nbtCompound = this.filter.copy();
                    nbtList.add(nbtCompound);
                    results.add(nbtCompound);
                }
            }
        }

        @Override
        public NbtElement init() {
            return new NbtList();
        }

        @Override
        public int set(NbtElement current, Supplier<NbtElement> source) {
            int i = 0;
            if (current instanceof NbtList) {
                NbtList nbtList = (NbtList)current;
                int j = nbtList.size();
                if (j == 0) {
                    nbtList.add(source.get());
                    ++i;
                } else {
                    for (int k = 0; k < j; ++k) {
                        NbtElement nbtElement2;
                        NbtElement nbtElement = nbtList.get(k);
                        if (!this.predicate.test(nbtElement) || (nbtElement2 = source.get()).equals(nbtElement) || !nbtList.setElement(k, nbtElement2)) continue;
                        ++i;
                    }
                }
            }
            return i;
        }

        @Override
        public int clear(NbtElement current) {
            int i = 0;
            if (current instanceof NbtList) {
                NbtList nbtList = (NbtList)current;
                for (int j = nbtList.size() - 1; j >= 0; --j) {
                    if (!this.predicate.test(nbtList.get(j))) continue;
                    nbtList.remove(j);
                    ++i;
                }
            }
            return i;
        }
    }

    static class AllListElementNode
    implements PathNode {
        public static final AllListElementNode INSTANCE = new AllListElementNode();

        private AllListElementNode() {
        }

        @Override
        public void get(NbtElement current, List<NbtElement> results) {
            if (current instanceof AbstractNbtList) {
                results.addAll((AbstractNbtList)current);
            }
        }

        @Override
        public void getOrInit(NbtElement current, Supplier<NbtElement> source, List<NbtElement> results) {
            if (current instanceof AbstractNbtList) {
                AbstractNbtList abstractNbtList = (AbstractNbtList)current;
                if (abstractNbtList.isEmpty()) {
                    NbtElement nbtElement = source.get();
                    if (abstractNbtList.addElement(0, nbtElement)) {
                        results.add(nbtElement);
                    }
                } else {
                    results.addAll(abstractNbtList);
                }
            }
        }

        @Override
        public NbtElement init() {
            return new NbtList();
        }

        @Override
        public int set(NbtElement current, Supplier<NbtElement> source) {
            if (current instanceof AbstractNbtList) {
                AbstractNbtList abstractNbtList = (AbstractNbtList)current;
                int i = abstractNbtList.size();
                if (i == 0) {
                    abstractNbtList.addElement(0, source.get());
                    return 1;
                }
                NbtElement nbtElement = source.get();
                int j = i - (int)abstractNbtList.stream().filter(nbtElement::equals).count();
                if (j == 0) {
                    return 0;
                }
                abstractNbtList.clear();
                if (!abstractNbtList.addElement(0, nbtElement)) {
                    return 0;
                }
                for (int k = 1; k < i; ++k) {
                    abstractNbtList.addElement(k, source.get());
                }
                return j;
            }
            return 0;
        }

        @Override
        public int clear(NbtElement current) {
            AbstractNbtList abstractNbtList;
            int i;
            if (current instanceof AbstractNbtList && (i = (abstractNbtList = (AbstractNbtList)current).size()) > 0) {
                abstractNbtList.clear();
                return i;
            }
            return 0;
        }
    }

    static class IndexedListElementNode
    implements PathNode {
        private final int index;

        public IndexedListElementNode(int index) {
            this.index = index;
        }

        @Override
        public void get(NbtElement current, List<NbtElement> results) {
            if (current instanceof AbstractNbtList) {
                int j;
                AbstractNbtList abstractNbtList = (AbstractNbtList)current;
                int i = abstractNbtList.size();
                int n = j = this.index < 0 ? i + this.index : this.index;
                if (0 <= j && j < i) {
                    results.add((NbtElement)abstractNbtList.get(j));
                }
            }
        }

        @Override
        public void getOrInit(NbtElement current, Supplier<NbtElement> source, List<NbtElement> results) {
            this.get(current, results);
        }

        @Override
        public NbtElement init() {
            return new NbtList();
        }

        @Override
        public int set(NbtElement current, Supplier<NbtElement> source) {
            if (current instanceof AbstractNbtList) {
                int j;
                AbstractNbtList abstractNbtList = (AbstractNbtList)current;
                int i = abstractNbtList.size();
                int n = j = this.index < 0 ? i + this.index : this.index;
                if (0 <= j && j < i) {
                    NbtElement nbtElement = (NbtElement)abstractNbtList.get(j);
                    NbtElement nbtElement2 = source.get();
                    if (!nbtElement2.equals(nbtElement) && abstractNbtList.setElement(j, nbtElement2)) {
                        return 1;
                    }
                }
            }
            return 0;
        }

        @Override
        public int clear(NbtElement current) {
            if (current instanceof AbstractNbtList) {
                int j;
                AbstractNbtList abstractNbtList = (AbstractNbtList)current;
                int i = abstractNbtList.size();
                int n = j = this.index < 0 ? i + this.index : this.index;
                if (0 <= j && j < i) {
                    abstractNbtList.remove(j);
                    return 1;
                }
            }
            return 0;
        }
    }

    static class FilteredNamedNode
    implements PathNode {
        private final String name;
        private final NbtCompound filter;
        private final Predicate<NbtElement> predicate;

        public FilteredNamedNode(String name, NbtCompound filter) {
            this.name = name;
            this.filter = filter;
            this.predicate = NbtPathArgumentType.getPredicate(filter);
        }

        @Override
        public void get(NbtElement current, List<NbtElement> results) {
            NbtElement nbtElement;
            if (current instanceof NbtCompound && this.predicate.test(nbtElement = ((NbtCompound)current).get(this.name))) {
                results.add(nbtElement);
            }
        }

        @Override
        public void getOrInit(NbtElement current, Supplier<NbtElement> source, List<NbtElement> results) {
            if (current instanceof NbtCompound) {
                NbtCompound nbtCompound = (NbtCompound)current;
                NbtElement nbtElement = nbtCompound.get(this.name);
                if (nbtElement == null) {
                    nbtElement = this.filter.copy();
                    nbtCompound.put(this.name, nbtElement);
                    results.add(nbtElement);
                } else if (this.predicate.test(nbtElement)) {
                    results.add(nbtElement);
                }
            }
        }

        @Override
        public NbtElement init() {
            return new NbtCompound();
        }

        @Override
        public int set(NbtElement current, Supplier<NbtElement> source) {
            NbtElement nbtElement2;
            NbtCompound nbtCompound;
            NbtElement nbtElement;
            if (current instanceof NbtCompound && this.predicate.test(nbtElement = (nbtCompound = (NbtCompound)current).get(this.name)) && !(nbtElement2 = source.get()).equals(nbtElement)) {
                nbtCompound.put(this.name, nbtElement2);
                return 1;
            }
            return 0;
        }

        @Override
        public int clear(NbtElement current) {
            NbtCompound nbtCompound;
            NbtElement nbtElement;
            if (current instanceof NbtCompound && this.predicate.test(nbtElement = (nbtCompound = (NbtCompound)current).get(this.name))) {
                nbtCompound.remove(this.name);
                return 1;
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
        public void get(NbtElement current, List<NbtElement> results) {
            NbtElement nbtElement;
            if (current instanceof NbtCompound && (nbtElement = ((NbtCompound)current).get(this.name)) != null) {
                results.add(nbtElement);
            }
        }

        @Override
        public void getOrInit(NbtElement current, Supplier<NbtElement> source, List<NbtElement> results) {
            if (current instanceof NbtCompound) {
                NbtElement nbtElement;
                NbtCompound nbtCompound = (NbtCompound)current;
                if (nbtCompound.contains(this.name)) {
                    nbtElement = nbtCompound.get(this.name);
                } else {
                    nbtElement = source.get();
                    nbtCompound.put(this.name, nbtElement);
                }
                results.add(nbtElement);
            }
        }

        @Override
        public NbtElement init() {
            return new NbtCompound();
        }

        @Override
        public int set(NbtElement current, Supplier<NbtElement> source) {
            if (current instanceof NbtCompound) {
                NbtElement nbtElement2;
                NbtCompound nbtCompound = (NbtCompound)current;
                NbtElement nbtElement = source.get();
                if (!nbtElement.equals(nbtElement2 = nbtCompound.put(this.name, nbtElement))) {
                    return 1;
                }
            }
            return 0;
        }

        @Override
        public int clear(NbtElement current) {
            NbtCompound nbtCompound;
            if (current instanceof NbtCompound && (nbtCompound = (NbtCompound)current).contains(this.name)) {
                nbtCompound.remove(this.name);
                return 1;
            }
            return 0;
        }
    }
}

