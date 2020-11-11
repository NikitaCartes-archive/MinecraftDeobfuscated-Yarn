/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.nbt.visitor;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
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
import net.minecraft.nbt.visitor.NbtTagVisitor;
import net.minecraft.util.Util;

/**
 * Formats an NBT tag as a multiline string where named tags inside of compounds are sorted
 * according to a defined ordering.
 */
public class NbtOrderedStringFormatter
implements NbtTagVisitor {
    /**
     * Contains the names of tags which should appear before any other tag in a compound, even
     * when they would otherwise appear later lexicographically. The list of tags which should be
     * prioritized differs depending on the path of the compound.
     */
    private static final Map<String, List<String>> ENTRY_ORDER_OVERRIDES = Util.make(Maps.newHashMap(), hashMap -> {
        hashMap.put("{}", Lists.newArrayList("DataVersion", "author", "size", "data", "entities", "palette", "palettes"));
        hashMap.put("{}.data.[].{}", Lists.newArrayList("pos", "state", "nbt"));
        hashMap.put("{}.entities.[].{}", Lists.newArrayList("blockPos", "pos"));
    });
    /**
     * Contains paths for which the indentation prefix should not be prepended to the result.
     */
    private static final Set<String> IGNORED_PATHS = Sets.newHashSet("{}.size.[]", "{}.data.[].{}", "{}.palette.[].{}", "{}.entities.[].{}");
    private static final Pattern SIMPLE_NAME = Pattern.compile("[A-Za-z0-9._+-]+");
    private static final String KEY_VALUE_SEPARATOR = String.valueOf(':');
    private static final String ENTRY_SEPARATOR = String.valueOf(',');
    private final String prefix;
    private final int indentationLevel;
    private final List<String> pathParts;
    private String result;

    public NbtOrderedStringFormatter() {
        this("    ", 0, Lists.newArrayList());
    }

    public NbtOrderedStringFormatter(String prefix, int indentationLevel, List<String> pathParts) {
        this.prefix = prefix;
        this.indentationLevel = indentationLevel;
        this.pathParts = pathParts;
    }

    public String apply(Tag tag) {
        tag.accept(this);
        return this.result;
    }

    @Override
    public void visitStringTag(StringTag tag) {
        this.result = StringTag.escape(tag.asString());
    }

    @Override
    public void visitByteTag(ByteTag tag) {
        this.result = tag.getNumber() + "b";
    }

    @Override
    public void visitShortTag(ShortTag tag) {
        this.result = tag.getNumber() + "s";
    }

    @Override
    public void visitIntTag(IntTag tag) {
        this.result = String.valueOf(tag.getNumber());
    }

    @Override
    public void visitLongTag(LongTag tag) {
        this.result = tag.getNumber() + "L";
    }

    @Override
    public void visitFloatTag(FloatTag tag) {
        this.result = tag.getFloat() + "f";
    }

    @Override
    public void visitDoubleTag(DoubleTag tag) {
        this.result = tag.getDouble() + "d";
    }

    @Override
    public void visitByteArrayTag(ByteArrayTag tag) {
        StringBuilder stringBuilder = new StringBuilder("[").append("B").append(";");
        byte[] bs = tag.getByteArray();
        for (int i = 0; i < bs.length; ++i) {
            stringBuilder.append(" ").append(bs[i]).append("B");
            if (i == bs.length - 1) continue;
            stringBuilder.append(ENTRY_SEPARATOR);
        }
        stringBuilder.append("]");
        this.result = stringBuilder.toString();
    }

    @Override
    public void visitIntArrayTag(IntArrayTag tag) {
        StringBuilder stringBuilder = new StringBuilder("[").append("I").append(";");
        int[] is = tag.getIntArray();
        for (int i = 0; i < is.length; ++i) {
            stringBuilder.append(" ").append(is[i]);
            if (i == is.length - 1) continue;
            stringBuilder.append(ENTRY_SEPARATOR);
        }
        stringBuilder.append("]");
        this.result = stringBuilder.toString();
    }

    @Override
    public void visitLongArrayTag(LongArrayTag tag) {
        String string = "L";
        StringBuilder stringBuilder = new StringBuilder("[").append("L").append(";");
        long[] ls = tag.getLongArray();
        for (int i = 0; i < ls.length; ++i) {
            stringBuilder.append(" ").append(ls[i]).append("L");
            if (i == ls.length - 1) continue;
            stringBuilder.append(ENTRY_SEPARATOR);
        }
        stringBuilder.append("]");
        this.result = stringBuilder.toString();
    }

    @Override
    public void visitListTag(ListTag tag) {
        String string;
        if (tag.isEmpty()) {
            this.result = "[]";
            return;
        }
        StringBuilder stringBuilder = new StringBuilder("[");
        this.pushPathPart("[]");
        String string2 = string = IGNORED_PATHS.contains(this.joinPath()) ? "" : this.prefix;
        if (!string.isEmpty()) {
            stringBuilder.append("\n");
        }
        for (int i = 0; i < tag.size(); ++i) {
            stringBuilder.append(Strings.repeat(string, this.indentationLevel + 1));
            stringBuilder.append(new NbtOrderedStringFormatter(string, this.indentationLevel + 1, this.pathParts).apply(tag.get(i)));
            if (i == tag.size() - 1) continue;
            stringBuilder.append(ENTRY_SEPARATOR).append(string.isEmpty() ? " " : "\n");
        }
        if (!string.isEmpty()) {
            stringBuilder.append("\n").append(Strings.repeat(string, this.indentationLevel));
        }
        stringBuilder.append("]");
        this.result = stringBuilder.toString();
        this.popPathPart();
    }

    @Override
    public void visitCompoundTag(CompoundTag tag) {
        String string;
        if (tag.isEmpty()) {
            this.result = "{}";
            return;
        }
        StringBuilder stringBuilder = new StringBuilder("{");
        this.pushPathPart("{}");
        String string2 = string = IGNORED_PATHS.contains(this.joinPath()) ? "" : this.prefix;
        if (!string.isEmpty()) {
            stringBuilder.append("\n");
        }
        List<String> collection = this.getSortedNames(tag);
        Iterator iterator = collection.iterator();
        while (iterator.hasNext()) {
            String string22 = (String)iterator.next();
            Tag tag2 = tag.get(string22);
            this.pushPathPart(string22);
            stringBuilder.append(Strings.repeat(string, this.indentationLevel + 1)).append(NbtOrderedStringFormatter.escapeName(string22)).append(KEY_VALUE_SEPARATOR).append(" ").append(new NbtOrderedStringFormatter(string, this.indentationLevel + 1, this.pathParts).apply(tag2));
            this.popPathPart();
            if (!iterator.hasNext()) continue;
            stringBuilder.append(ENTRY_SEPARATOR).append(string.isEmpty() ? " " : "\n");
        }
        if (!string.isEmpty()) {
            stringBuilder.append("\n").append(Strings.repeat(string, this.indentationLevel));
        }
        stringBuilder.append("}");
        this.result = stringBuilder.toString();
        this.popPathPart();
    }

    private void popPathPart() {
        this.pathParts.remove(this.pathParts.size() - 1);
    }

    private void pushPathPart(String part) {
        this.pathParts.add(part);
    }

    protected List<String> getSortedNames(CompoundTag tag) {
        HashSet<String> set = Sets.newHashSet(tag.getKeys());
        ArrayList<String> list = Lists.newArrayList();
        List<String> list2 = ENTRY_ORDER_OVERRIDES.get(this.joinPath());
        if (list2 != null) {
            for (String string : list2) {
                if (!set.remove(string)) continue;
                list.add(string);
            }
            if (!set.isEmpty()) {
                set.stream().sorted().forEach(list::add);
            }
        } else {
            list.addAll(set);
            Collections.sort(list);
        }
        return list;
    }

    public String joinPath() {
        return String.join((CharSequence)".", this.pathParts);
    }

    protected static String escapeName(String name) {
        if (SIMPLE_NAME.matcher(name).matches()) {
            return name;
        }
        return StringTag.escape(name);
    }

    @Override
    public void visitEndTag(EndTag tag) {
    }
}

