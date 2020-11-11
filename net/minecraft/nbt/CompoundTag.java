/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.nbt;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
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
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.PositionTracker;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagReader;
import net.minecraft.nbt.TagReaders;
import net.minecraft.nbt.visitor.NbtTagVisitor;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import org.jetbrains.annotations.Nullable;

public class CompoundTag
implements Tag {
    public static final Codec<CompoundTag> CODEC = Codec.PASSTHROUGH.comapFlatMap(dynamic -> {
        Tag tag = dynamic.convert(NbtOps.INSTANCE).getValue();
        if (tag instanceof CompoundTag) {
            return DataResult.success((CompoundTag)tag);
        }
        return DataResult.error("Not a compound tag: " + tag);
    }, compoundTag -> new Dynamic<CompoundTag>(NbtOps.INSTANCE, (CompoundTag)compoundTag));
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

    protected CompoundTag(Map<String, Tag> tags) {
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
     * Writes a {@link UUID} to its NBT representation in this {@code CompoundTag}.
     */
    public void putUuid(String key, UUID value) {
        this.tags.put(key, NbtHelper.fromUuid(value));
    }

    /**
     * Reads a {@link UUID} from its NBT representation in this {@code CompoundTag}.
     */
    public UUID getUuid(String key) {
        return NbtHelper.toUuid(this.get(key));
    }

    /**
     * Returns {@code true} if this {@code CompoundTag} contains a valid UUID representation associated with the given key.
     * A valid UUID is represented by an int array of length 4.
     */
    public boolean containsUuid(String key) {
        Tag tag = this.get(key);
        return tag != null && tag.getReader() == IntArrayTag.READER && ((IntArrayTag)tag).getIntArray().length == 4;
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
        return this.asString();
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

    @Override
    public void accept(NbtTagVisitor visitor) {
        visitor.visitCompoundTag(this);
    }

    protected Map<String, Tag> toMap() {
        return Collections.unmodifiableMap(this.tags);
    }

    @Override
    public /* synthetic */ Tag copy() {
        return this.copy();
    }
}

