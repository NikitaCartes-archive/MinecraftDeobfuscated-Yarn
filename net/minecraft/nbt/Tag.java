/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
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
import net.minecraft.nbt.PositionTracker;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public interface Tag {
    public static final String[] TYPES = new String[]{"END", "BYTE", "SHORT", "INT", "LONG", "FLOAT", "DOUBLE", "BYTE[]", "STRING", "LIST", "COMPOUND", "INT[]", "LONG[]"};
    public static final Formatting AQUA = Formatting.AQUA;
    public static final Formatting GREEN = Formatting.GREEN;
    public static final Formatting GOLD = Formatting.GOLD;
    public static final Formatting RED = Formatting.RED;

    public void write(DataOutput var1) throws IOException;

    public void read(DataInput var1, int var2, PositionTracker var3) throws IOException;

    public String toString();

    public byte getType();

    public static Tag createTag(byte b) {
        switch (b) {
            case 0: {
                return new EndTag();
            }
            case 1: {
                return new ByteTag();
            }
            case 2: {
                return new ShortTag();
            }
            case 3: {
                return new IntTag();
            }
            case 4: {
                return new LongTag();
            }
            case 5: {
                return new FloatTag();
            }
            case 6: {
                return new DoubleTag();
            }
            case 7: {
                return new ByteArrayTag();
            }
            case 11: {
                return new IntArrayTag();
            }
            case 12: {
                return new LongArrayTag();
            }
            case 8: {
                return new StringTag();
            }
            case 9: {
                return new ListTag();
            }
            case 10: {
                return new CompoundTag();
            }
        }
        return null;
    }

    public static String idToString(int i) {
        switch (i) {
            case 0: {
                return "TAG_End";
            }
            case 1: {
                return "TAG_Byte";
            }
            case 2: {
                return "TAG_Short";
            }
            case 3: {
                return "TAG_Int";
            }
            case 4: {
                return "TAG_Long";
            }
            case 5: {
                return "TAG_Float";
            }
            case 6: {
                return "TAG_Double";
            }
            case 7: {
                return "TAG_Byte_Array";
            }
            case 11: {
                return "TAG_Int_Array";
            }
            case 12: {
                return "TAG_Long_Array";
            }
            case 8: {
                return "TAG_String";
            }
            case 9: {
                return "TAG_List";
            }
            case 10: {
                return "TAG_Compound";
            }
            case 99: {
                return "Any Numeric Tag";
            }
        }
        return "UNKNOWN";
    }

    public Tag copy();

    default public String asString() {
        return this.toString();
    }

    default public Text toText() {
        return this.toText("", 0);
    }

    public Text toText(String var1, int var2);
}

