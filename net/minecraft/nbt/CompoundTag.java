/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.nbt;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import net.minecraft.nbt.AbstractNumberTag;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.PositionTracker;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagReader;
import net.minecraft.nbt.TagReaders;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class CompoundTag
implements Tag {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Pattern PATTERN = Pattern.compile("[A-Za-z0-9._+-]+");
    public static final TagReader<CompoundTag> READER = new TagReader<CompoundTag>(){

        @Override
        public CompoundTag read(DataInput dataInput, int i, PositionTracker positionTracker) throws IOException {
            byte b;
            positionTracker.add(384L);
            if (i > 512) {
                throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
            }
            HashMap<String, Tag> map = Maps.newHashMap();
            while ((b = CompoundTag.readByte(dataInput, positionTracker)) != 0) {
                String string = CompoundTag.readString(dataInput, positionTracker);
                positionTracker.add(224 + 16 * string.length());
                Tag tag = CompoundTag.read(TagReaders.of(b), string, dataInput, i + 1, positionTracker);
                if (map.put(string, tag) == null) continue;
                positionTracker.add(288L);
            }
            return new CompoundTag(map);
        }

        @Override
        public String getCrashReportName() {
            return "COMPOUND";
        }

        @Override
        public String getCommandFeedbackName() {
            return "TAG_Compound";
        }

        @Override
        public /* synthetic */ Tag read(DataInput input, int depth, PositionTracker tracker) throws IOException {
            return this.read(input, depth, tracker);
        }
    };
    private final Map<String, Tag> tags;

    private CompoundTag(Map<String, Tag> tags) {
        this.tags = tags;
    }

    public CompoundTag() {
        this(Maps.newHashMap());
    }

    @Override
    public void write(DataOutput output) throws IOException {
        for (String string : this.tags.keySet()) {
            Tag tag = this.tags.get(string);
            CompoundTag.write(string, tag, output);
        }
        output.writeByte(0);
    }

    public Set<String> getKeys() {
        return this.tags.keySet();
    }

    @Override
    public byte getType() {
        return 10;
    }

    public TagReader<CompoundTag> getReader() {
        return READER;
    }

    public int getSize() {
        return this.tags.size();
    }

    @Nullable
    public Tag put(String key, Tag tag) {
        return this.tags.put(key, tag);
    }

    public void putByte(String key, byte value) {
        this.tags.put(key, ByteTag.of(value));
    }

    public void putShort(String key, short value) {
        this.tags.put(key, ShortTag.of(value));
    }

    public void putInt(String key, int value) {
        this.tags.put(key, IntTag.of(value));
    }

    public void putLong(String key, long value) {
        this.tags.put(key, LongTag.of(value));
    }

    /**
     * Reads a {@link UUID} from its NBT representation in this {@code CompoundTag}.
     * 
     * @since 20w10a
     */
    public void putUuidNew(String key, UUID value) {
        this.tags.put(key, NbtHelper.fromUuidNew(value));
    }

    /**
     * Reads a {@link UUID} from its NBT representation in this {@code CompoundTag}.
     * 
     * @since 20w10a
     */
    public UUID getUuidNew(String key) {
        return NbtHelper.toUuidNew(this.get(key));
    }

    /**
     * Returns {@code true} if this {@code CompoundTag} contains a valid UUID representation associated with the given key.
     * A valid UUID is represented by an int array of length 4.
     * 
     * @since 20w10a
     */
    public boolean containsUuidNew(String key) {
        Tag tag = this.get(key);
        return tag != null && tag.getReader() == IntArrayTag.READER && ((IntArrayTag)tag).getIntArray().length == 4;
    }

    /**
     * @deprecated use {@link #putUuid}.
     */
    @Deprecated
    public void putUuidOld(String key, UUID uuid) {
        this.putLong(key + "Most", uuid.getMostSignificantBits());
        this.putLong(key + "Least", uuid.getLeastSignificantBits());
    }

    /**
     * @deprecated use {@link #getUuidNew} for newly added deserialization methods.
     * Other places may keep calling this as well as {@link #getUuidNew} to ensure a smooth transition for old worlds.
     */
    @Deprecated
    public UUID getUuidOld(String key) {
        return new UUID(this.getLong(key + "Most"), this.getLong(key + "Least"));
    }

    /**
     * @deprecated use {@link #containsUuidNew} for newly added deserialization methods.
     * Other places may keep calling this as well as {@link #containsUuidNew} to ensure a smooth transition for old worlds.
     */
    @Deprecated
    public boolean containsUuidOld(String key) {
        return this.contains(key + "Most", 99) && this.contains(key + "Least", 99);
    }

    /**
     * @deprecated use {@link #remove} to remove a UUID serialized using the new {@link #putUuid} method.
     */
    @Deprecated
    public void removeUuidOld(String key) {
        this.remove(key + "Most");
        this.remove(key + "Least");
    }

    public void putFloat(String key, float value) {
        this.tags.put(key, FloatTag.of(value));
    }

    public void putDouble(String key, double value) {
        this.tags.put(key, DoubleTag.of(value));
    }

    public void putString(String key, String value) {
        this.tags.put(key, StringTag.of(value));
    }

    public void putByteArray(String key, byte[] value) {
        this.tags.put(key, new ByteArrayTag(value));
    }

    public void putIntArray(String key, int[] value) {
        this.tags.put(key, new IntArrayTag(value));
    }

    public void putIntArray(String key, List<Integer> value) {
        this.tags.put(key, new IntArrayTag(value));
    }

    public void putLongArray(String key, long[] value) {
        this.tags.put(key, new LongArrayTag(value));
    }

    public void putLongArray(String key, List<Long> value) {
        this.tags.put(key, new LongArrayTag(value));
    }

    public void putBoolean(String key, boolean value) {
        this.tags.put(key, ByteTag.of(value));
    }

    @Nullable
    public Tag get(String key) {
        return this.tags.get(key);
    }

    public byte getType(String key) {
        Tag tag = this.tags.get(key);
        if (tag == null) {
            return 0;
        }
        return tag.getType();
    }

    public boolean contains(String key) {
        return this.tags.containsKey(key);
    }

    public boolean contains(String key, int type) {
        byte i = this.getType(key);
        if (i == type) {
            return true;
        }
        if (type == 99) {
            return i == 1 || i == 2 || i == 3 || i == 4 || i == 5 || i == 6;
        }
        return false;
    }

    public byte getByte(String key) {
        try {
            if (this.contains(key, 99)) {
                return ((AbstractNumberTag)this.tags.get(key)).getByte();
            }
        } catch (ClassCastException classCastException) {
            // empty catch block
        }
        return 0;
    }

    public short getShort(String key) {
        try {
            if (this.contains(key, 99)) {
                return ((AbstractNumberTag)this.tags.get(key)).getShort();
            }
        } catch (ClassCastException classCastException) {
            // empty catch block
        }
        return 0;
    }

    public int getInt(String key) {
        try {
            if (this.contains(key, 99)) {
                return ((AbstractNumberTag)this.tags.get(key)).getInt();
            }
        } catch (ClassCastException classCastException) {
            // empty catch block
        }
        return 0;
    }

    public long getLong(String key) {
        try {
            if (this.contains(key, 99)) {
                return ((AbstractNumberTag)this.tags.get(key)).getLong();
            }
        } catch (ClassCastException classCastException) {
            // empty catch block
        }
        return 0L;
    }

    public float getFloat(String key) {
        try {
            if (this.contains(key, 99)) {
                return ((AbstractNumberTag)this.tags.get(key)).getFloat();
            }
        } catch (ClassCastException classCastException) {
            // empty catch block
        }
        return 0.0f;
    }

    public double getDouble(String key) {
        try {
            if (this.contains(key, 99)) {
                return ((AbstractNumberTag)this.tags.get(key)).getDouble();
            }
        } catch (ClassCastException classCastException) {
            // empty catch block
        }
        return 0.0;
    }

    public String getString(String key) {
        try {
            if (this.contains(key, 8)) {
                return this.tags.get(key).asString();
            }
        } catch (ClassCastException classCastException) {
            // empty catch block
        }
        return "";
    }

    public byte[] getByteArray(String key) {
        try {
            if (this.contains(key, 7)) {
                return ((ByteArrayTag)this.tags.get(key)).getByteArray();
            }
        } catch (ClassCastException classCastException) {
            throw new CrashException(this.createCrashReport(key, ByteArrayTag.READER, classCastException));
        }
        return new byte[0];
    }

    public int[] getIntArray(String key) {
        try {
            if (this.contains(key, 11)) {
                return ((IntArrayTag)this.tags.get(key)).getIntArray();
            }
        } catch (ClassCastException classCastException) {
            throw new CrashException(this.createCrashReport(key, IntArrayTag.READER, classCastException));
        }
        return new int[0];
    }

    public long[] getLongArray(String key) {
        try {
            if (this.contains(key, 12)) {
                return ((LongArrayTag)this.tags.get(key)).getLongArray();
            }
        } catch (ClassCastException classCastException) {
            throw new CrashException(this.createCrashReport(key, LongArrayTag.READER, classCastException));
        }
        return new long[0];
    }

    public CompoundTag getCompound(String key) {
        try {
            if (this.contains(key, 10)) {
                return (CompoundTag)this.tags.get(key);
            }
        } catch (ClassCastException classCastException) {
            throw new CrashException(this.createCrashReport(key, READER, classCastException));
        }
        return new CompoundTag();
    }

    public ListTag getList(String key, int type) {
        try {
            if (this.getType(key) == 9) {
                ListTag listTag = (ListTag)this.tags.get(key);
                if (listTag.isEmpty() || listTag.getElementType() == type) {
                    return listTag;
                }
                return new ListTag();
            }
        } catch (ClassCastException classCastException) {
            throw new CrashException(this.createCrashReport(key, ListTag.READER, classCastException));
        }
        return new ListTag();
    }

    public boolean getBoolean(String key) {
        return this.getByte(key) != 0;
    }

    public void remove(String key) {
        this.tags.remove(key);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("{");
        Collection<String> collection = this.tags.keySet();
        if (LOGGER.isDebugEnabled()) {
            ArrayList<String> list = Lists.newArrayList(this.tags.keySet());
            Collections.sort(list);
            collection = list;
        }
        for (String string : collection) {
            if (stringBuilder.length() != 1) {
                stringBuilder.append(',');
            }
            stringBuilder.append(CompoundTag.escapeTagKey(string)).append(':').append(this.tags.get(string));
        }
        return stringBuilder.append('}').toString();
    }

    public boolean isEmpty() {
        return this.tags.isEmpty();
    }

    private CrashReport createCrashReport(String key, TagReader<?> tagReader, ClassCastException classCastException) {
        CrashReport crashReport = CrashReport.create(classCastException, "Reading NBT data");
        CrashReportSection crashReportSection = crashReport.addElement("Corrupt NBT tag", 1);
        crashReportSection.add("Tag type found", () -> this.tags.get(key).getReader().getCrashReportName());
        crashReportSection.add("Tag type expected", tagReader::getCrashReportName);
        crashReportSection.add("Tag name", key);
        return crashReport;
    }

    @Override
    public CompoundTag copy() {
        HashMap<String, Tag> map = Maps.newHashMap(Maps.transformValues(this.tags, Tag::copy));
        return new CompoundTag(map);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o instanceof CompoundTag && Objects.equals(this.tags, ((CompoundTag)o).tags);
    }

    public int hashCode() {
        return this.tags.hashCode();
    }

    private static void write(String key, Tag tag, DataOutput output) throws IOException {
        output.writeByte(tag.getType());
        if (tag.getType() == 0) {
            return;
        }
        output.writeUTF(key);
        tag.write(output);
    }

    private static byte readByte(DataInput input, PositionTracker tracker) throws IOException {
        return input.readByte();
    }

    private static String readString(DataInput input, PositionTracker tracker) throws IOException {
        return input.readUTF();
    }

    private static Tag read(TagReader<?> reader, String key, DataInput input, int depth, PositionTracker tracker) {
        try {
            return reader.read(input, depth, tracker);
        } catch (IOException iOException) {
            CrashReport crashReport = CrashReport.create(iOException, "Loading NBT data");
            CrashReportSection crashReportSection = crashReport.addElement("NBT Tag");
            crashReportSection.add("Tag name", key);
            crashReportSection.add("Tag type", reader.getCrashReportName());
            throw new CrashException(crashReport);
        }
    }

    public CompoundTag copyFrom(CompoundTag source) {
        for (String string : source.tags.keySet()) {
            Tag tag = source.tags.get(string);
            if (tag.getType() == 10) {
                if (this.contains(string, 10)) {
                    CompoundTag compoundTag = this.getCompound(string);
                    compoundTag.copyFrom((CompoundTag)tag);
                    continue;
                }
                this.put(string, tag.copy());
                continue;
            }
            this.put(string, tag.copy());
        }
        return this;
    }

    protected static String escapeTagKey(String key) {
        if (PATTERN.matcher(key).matches()) {
            return key;
        }
        return StringTag.escape(key);
    }

    protected static Text prettyPrintTagKey(String key) {
        if (PATTERN.matcher(key).matches()) {
            return new LiteralText(key).formatted(AQUA);
        }
        String string = StringTag.escape(key);
        String string2 = string.substring(0, 1);
        Text text = new LiteralText(string.substring(1, string.length() - 1)).formatted(AQUA);
        return new LiteralText(string2).append(text).append(string2);
    }

    @Override
    public Text toText(String indent, int depth) {
        if (this.tags.isEmpty()) {
            return new LiteralText("{}");
        }
        LiteralText text = new LiteralText("{");
        Collection<String> collection = this.tags.keySet();
        if (LOGGER.isDebugEnabled()) {
            ArrayList<String> list = Lists.newArrayList(this.tags.keySet());
            Collections.sort(list);
            collection = list;
        }
        if (!indent.isEmpty()) {
            text.append("\n");
        }
        Iterator iterator = collection.iterator();
        while (iterator.hasNext()) {
            String string = (String)iterator.next();
            Text text2 = new LiteralText(Strings.repeat(indent, depth + 1)).append(CompoundTag.prettyPrintTagKey(string)).append(String.valueOf(':')).append(" ").append(this.tags.get(string).toText(indent, depth + 1));
            if (iterator.hasNext()) {
                text2.append(String.valueOf(',')).append(indent.isEmpty() ? " " : "\n");
            }
            text.append(text2);
        }
        if (!indent.isEmpty()) {
            text.append("\n").append(Strings.repeat(indent, depth));
        }
        text.append("}");
        return text;
    }

    @Override
    public /* synthetic */ Tag copy() {
        return this.copy();
    }
}

