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
import net.minecraft.nbt.PositionTracker;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
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
    private final Map<String, Tag> tags = Maps.newHashMap();

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        for (String string : this.tags.keySet()) {
            Tag tag = this.tags.get(string);
            CompoundTag.write(string, tag, dataOutput);
        }
        dataOutput.writeByte(0);
    }

    @Override
    public void read(DataInput dataInput, int i, PositionTracker positionTracker) throws IOException {
        byte b;
        positionTracker.add(384L);
        if (i > 512) {
            throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
        }
        this.tags.clear();
        while ((b = CompoundTag.readByte(dataInput, positionTracker)) != 0) {
            String string = CompoundTag.readString(dataInput, positionTracker);
            positionTracker.add(224 + 16 * string.length());
            Tag tag = CompoundTag.createTag(b, string, dataInput, i + 1, positionTracker);
            if (this.tags.put(string, tag) == null) continue;
            positionTracker.add(288L);
        }
    }

    public Set<String> getKeys() {
        return this.tags.keySet();
    }

    @Override
    public byte getType() {
        return 10;
    }

    public int getSize() {
        return this.tags.size();
    }

    @Nullable
    public Tag put(String string, Tag tag) {
        return this.tags.put(string, tag);
    }

    public void putByte(String string, byte b) {
        this.tags.put(string, new ByteTag(b));
    }

    public void putShort(String string, short s) {
        this.tags.put(string, new ShortTag(s));
    }

    public void putInt(String string, int i) {
        this.tags.put(string, new IntTag(i));
    }

    public void putLong(String string, long l) {
        this.tags.put(string, new LongTag(l));
    }

    public void putUuid(String string, UUID uUID) {
        this.putLong(string + "Most", uUID.getMostSignificantBits());
        this.putLong(string + "Least", uUID.getLeastSignificantBits());
    }

    public UUID getUuid(String string) {
        return new UUID(this.getLong(string + "Most"), this.getLong(string + "Least"));
    }

    public boolean hasUuid(String string) {
        return this.containsKey(string + "Most", 99) && this.containsKey(string + "Least", 99);
    }

    public void putFloat(String string, float f) {
        this.tags.put(string, new FloatTag(f));
    }

    public void putDouble(String string, double d) {
        this.tags.put(string, new DoubleTag(d));
    }

    public void putString(String string, String string2) {
        this.tags.put(string, new StringTag(string2));
    }

    public void putByteArray(String string, byte[] bs) {
        this.tags.put(string, new ByteArrayTag(bs));
    }

    public void putIntArray(String string, int[] is) {
        this.tags.put(string, new IntArrayTag(is));
    }

    public void putIntArray(String string, List<Integer> list) {
        this.tags.put(string, new IntArrayTag(list));
    }

    public void putLongArray(String string, long[] ls) {
        this.tags.put(string, new LongArrayTag(ls));
    }

    public void putLongArray(String string, List<Long> list) {
        this.tags.put(string, new LongArrayTag(list));
    }

    public void putBoolean(String string, boolean bl) {
        this.putByte(string, bl ? (byte)1 : 0);
    }

    @Nullable
    public Tag getTag(String string) {
        return this.tags.get(string);
    }

    public byte getType(String string) {
        Tag tag = this.tags.get(string);
        if (tag == null) {
            return 0;
        }
        return tag.getType();
    }

    public boolean containsKey(String string) {
        return this.tags.containsKey(string);
    }

    public boolean containsKey(String string, int i) {
        byte j = this.getType(string);
        if (j == i) {
            return true;
        }
        if (i == 99) {
            return j == 1 || j == 2 || j == 3 || j == 4 || j == 5 || j == 6;
        }
        return false;
    }

    public byte getByte(String string) {
        try {
            if (this.containsKey(string, 99)) {
                return ((AbstractNumberTag)this.tags.get(string)).getByte();
            }
        } catch (ClassCastException classCastException) {
            // empty catch block
        }
        return 0;
    }

    public short getShort(String string) {
        try {
            if (this.containsKey(string, 99)) {
                return ((AbstractNumberTag)this.tags.get(string)).getShort();
            }
        } catch (ClassCastException classCastException) {
            // empty catch block
        }
        return 0;
    }

    public int getInt(String string) {
        try {
            if (this.containsKey(string, 99)) {
                return ((AbstractNumberTag)this.tags.get(string)).getInt();
            }
        } catch (ClassCastException classCastException) {
            // empty catch block
        }
        return 0;
    }

    public long getLong(String string) {
        try {
            if (this.containsKey(string, 99)) {
                return ((AbstractNumberTag)this.tags.get(string)).getLong();
            }
        } catch (ClassCastException classCastException) {
            // empty catch block
        }
        return 0L;
    }

    public float getFloat(String string) {
        try {
            if (this.containsKey(string, 99)) {
                return ((AbstractNumberTag)this.tags.get(string)).getFloat();
            }
        } catch (ClassCastException classCastException) {
            // empty catch block
        }
        return 0.0f;
    }

    public double getDouble(String string) {
        try {
            if (this.containsKey(string, 99)) {
                return ((AbstractNumberTag)this.tags.get(string)).getDouble();
            }
        } catch (ClassCastException classCastException) {
            // empty catch block
        }
        return 0.0;
    }

    public String getString(String string) {
        try {
            if (this.containsKey(string, 8)) {
                return this.tags.get(string).asString();
            }
        } catch (ClassCastException classCastException) {
            // empty catch block
        }
        return "";
    }

    public byte[] getByteArray(String string) {
        try {
            if (this.containsKey(string, 7)) {
                return ((ByteArrayTag)this.tags.get(string)).getByteArray();
            }
        } catch (ClassCastException classCastException) {
            throw new CrashException(this.createCrashReport(string, 7, classCastException));
        }
        return new byte[0];
    }

    public int[] getIntArray(String string) {
        try {
            if (this.containsKey(string, 11)) {
                return ((IntArrayTag)this.tags.get(string)).getIntArray();
            }
        } catch (ClassCastException classCastException) {
            throw new CrashException(this.createCrashReport(string, 11, classCastException));
        }
        return new int[0];
    }

    public long[] getLongArray(String string) {
        try {
            if (this.containsKey(string, 12)) {
                return ((LongArrayTag)this.tags.get(string)).getLongArray();
            }
        } catch (ClassCastException classCastException) {
            throw new CrashException(this.createCrashReport(string, 12, classCastException));
        }
        return new long[0];
    }

    public CompoundTag getCompound(String string) {
        try {
            if (this.containsKey(string, 10)) {
                return (CompoundTag)this.tags.get(string);
            }
        } catch (ClassCastException classCastException) {
            throw new CrashException(this.createCrashReport(string, 10, classCastException));
        }
        return new CompoundTag();
    }

    public ListTag getList(String string, int i) {
        try {
            if (this.getType(string) == 9) {
                ListTag listTag = (ListTag)this.tags.get(string);
                if (listTag.isEmpty() || listTag.getListType() == i) {
                    return listTag;
                }
                return new ListTag();
            }
        } catch (ClassCastException classCastException) {
            throw new CrashException(this.createCrashReport(string, 9, classCastException));
        }
        return new ListTag();
    }

    public boolean getBoolean(String string) {
        return this.getByte(string) != 0;
    }

    public void remove(String string) {
        this.tags.remove(string);
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

    private CrashReport createCrashReport(String string, int i, ClassCastException classCastException) {
        CrashReport crashReport = CrashReport.create(classCastException, "Reading NBT data");
        CrashReportSection crashReportSection = crashReport.addElement("Corrupt NBT tag", 1);
        crashReportSection.add("Tag type found", () -> TYPES[this.tags.get(string).getType()]);
        crashReportSection.add("Tag type expected", () -> TYPES[i]);
        crashReportSection.add("Tag name", string);
        return crashReport;
    }

    public CompoundTag method_10553() {
        CompoundTag compoundTag = new CompoundTag();
        for (String string : this.tags.keySet()) {
            compoundTag.put(string, this.tags.get(string).copy());
        }
        return compoundTag;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        return object instanceof CompoundTag && Objects.equals(this.tags, ((CompoundTag)object).tags);
    }

    public int hashCode() {
        return this.tags.hashCode();
    }

    private static void write(String string, Tag tag, DataOutput dataOutput) throws IOException {
        dataOutput.writeByte(tag.getType());
        if (tag.getType() == 0) {
            return;
        }
        dataOutput.writeUTF(string);
        tag.write(dataOutput);
    }

    private static byte readByte(DataInput dataInput, PositionTracker positionTracker) throws IOException {
        return dataInput.readByte();
    }

    private static String readString(DataInput dataInput, PositionTracker positionTracker) throws IOException {
        return dataInput.readUTF();
    }

    static Tag createTag(byte b, String string, DataInput dataInput, int i, PositionTracker positionTracker) throws IOException {
        Tag tag = Tag.createTag(b);
        try {
            tag.read(dataInput, i, positionTracker);
        } catch (IOException iOException) {
            CrashReport crashReport = CrashReport.create(iOException, "Loading NBT data");
            CrashReportSection crashReportSection = crashReport.addElement("NBT Tag");
            crashReportSection.add("Tag name", string);
            crashReportSection.add("Tag type", b);
            throw new CrashException(crashReport);
        }
        return tag;
    }

    public CompoundTag copyFrom(CompoundTag compoundTag) {
        for (String string : compoundTag.tags.keySet()) {
            Tag tag = compoundTag.tags.get(string);
            if (tag.getType() == 10) {
                if (this.containsKey(string, 10)) {
                    CompoundTag compoundTag2 = this.getCompound(string);
                    compoundTag2.copyFrom((CompoundTag)tag);
                    continue;
                }
                this.put(string, tag.copy());
                continue;
            }
            this.put(string, tag.copy());
        }
        return this;
    }

    protected static String escapeTagKey(String string) {
        if (PATTERN.matcher(string).matches()) {
            return string;
        }
        return StringTag.escape(string);
    }

    protected static Component prettyPrintTagKey(String string) {
        if (PATTERN.matcher(string).matches()) {
            return new TextComponent(string).applyFormat(AQUA);
        }
        String string2 = StringTag.escape(string);
        String string3 = string2.substring(0, 1);
        Component component = new TextComponent(string2.substring(1, string2.length() - 1)).applyFormat(AQUA);
        return new TextComponent(string3).append(component).append(string3);
    }

    @Override
    public Component toTextComponent(String string, int i) {
        if (this.tags.isEmpty()) {
            return new TextComponent("{}");
        }
        TextComponent component = new TextComponent("{");
        Collection<String> collection = this.tags.keySet();
        if (LOGGER.isDebugEnabled()) {
            ArrayList<String> list = Lists.newArrayList(this.tags.keySet());
            Collections.sort(list);
            collection = list;
        }
        if (!string.isEmpty()) {
            component.append("\n");
        }
        Iterator iterator = collection.iterator();
        while (iterator.hasNext()) {
            String string2 = (String)iterator.next();
            Component component2 = new TextComponent(Strings.repeat(string, i + 1)).append(CompoundTag.prettyPrintTagKey(string2)).append(String.valueOf(':')).append(" ").append(this.tags.get(string2).toTextComponent(string, i + 1));
            if (iterator.hasNext()) {
                component2.append(String.valueOf(',')).append(string.isEmpty() ? " " : "\n");
            }
            component.append(component2);
        }
        if (!string.isEmpty()) {
            component.append("\n").append(Strings.repeat(string, i));
        }
        component.append("}");
        return component;
    }

    @Override
    public /* synthetic */ Tag copy() {
        return this.method_10553();
    }
}

