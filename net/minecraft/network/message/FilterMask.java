/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.message;

import java.util.BitSet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.message.DecoratedContents;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

public class FilterMask {
    public static final FilterMask FULLY_FILTERED = new FilterMask(new BitSet(0), FilterStatus.FULLY_FILTERED);
    public static final FilterMask PASS_THROUGH = new FilterMask(new BitSet(0), FilterStatus.PASS_THROUGH);
    private static final char FILTERED = '#';
    private final BitSet mask;
    private final FilterStatus status;

    private FilterMask(BitSet mask, FilterStatus status) {
        this.mask = mask;
        this.status = status;
    }

    public FilterMask(int length) {
        this(new BitSet(length), FilterStatus.PARTIALLY_FILTERED);
    }

    public static FilterMask readMask(PacketByteBuf buf) {
        FilterStatus filterStatus = buf.readEnumConstant(FilterStatus.class);
        return switch (filterStatus) {
            default -> throw new IncompatibleClassChangeError();
            case FilterStatus.PASS_THROUGH -> PASS_THROUGH;
            case FilterStatus.FULLY_FILTERED -> FULLY_FILTERED;
            case FilterStatus.PARTIALLY_FILTERED -> new FilterMask(buf.readBitSet(), FilterStatus.PARTIALLY_FILTERED);
        };
    }

    public static void writeMask(PacketByteBuf buf, FilterMask mask) {
        buf.writeEnumConstant(mask.status);
        if (mask.status == FilterStatus.PARTIALLY_FILTERED) {
            buf.writeBitSet(mask.mask);
        }
    }

    public void markFiltered(int index) {
        this.mask.set(index);
    }

    @Nullable
    public String filter(String raw) {
        return switch (this.status) {
            default -> throw new IncompatibleClassChangeError();
            case FilterStatus.FULLY_FILTERED -> null;
            case FilterStatus.PASS_THROUGH -> raw;
            case FilterStatus.PARTIALLY_FILTERED -> {
                char[] cs = raw.toCharArray();
                for (int i = 0; i < cs.length && i < this.mask.length(); ++i) {
                    if (!this.mask.get(i)) continue;
                    cs[i] = 35;
                }
                yield new String(cs);
            }
        };
    }

    @Nullable
    public Text filter(DecoratedContents contents) {
        String string = contents.plain();
        return Util.map(this.filter(string), Text::literal);
    }

    public boolean isPassThrough() {
        return this.status == FilterStatus.PASS_THROUGH;
    }

    public boolean isFullyFiltered() {
        return this.status == FilterStatus.FULLY_FILTERED;
    }

    static enum FilterStatus {
        PASS_THROUGH,
        FULLY_FILTERED,
        PARTIALLY_FILTERED;

    }
}

