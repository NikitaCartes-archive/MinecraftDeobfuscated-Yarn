/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.message;

import java.util.BitSet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

public class FilterMask {
    public static final FilterMask FULLY_FILTERED = new FilterMask(new BitSet(0), FilterStatus.FULLY_FILTERED);
    public static final FilterMask PASS_THROUGH = new FilterMask(new BitSet(0), FilterStatus.PASS_THROUGH);
    public static final Style FILTERED_STYLE = Style.EMPTY.withColor(Formatting.DARK_GRAY).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.translatable("chat.filtered")));
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
    public Text getFilteredText(String message) {
        return switch (this.status) {
            default -> throw new IncompatibleClassChangeError();
            case FilterStatus.FULLY_FILTERED -> null;
            case FilterStatus.PASS_THROUGH -> Text.literal(message);
            case FilterStatus.PARTIALLY_FILTERED -> {
                MutableText mutableText = Text.empty();
                int i = 0;
                boolean bl = this.mask.get(0);
                while (true) {
                    int j = bl ? this.mask.nextClearBit(i) : this.mask.nextSetBit(i);
                    int v1 = j = j < 0 ? message.length() : j;
                    if (j == i) break;
                    if (bl) {
                        mutableText.append(Text.literal(StringUtils.repeat('#', j - i)).fillStyle(FILTERED_STYLE));
                    } else {
                        mutableText.append(message.substring(i, j));
                    }
                    bl = !bl;
                    i = j;
                }
                yield mutableText;
            }
        };
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

