package net.minecraft.network.message;

import java.util.BitSet;
import javax.annotation.Nullable;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

public class FilterMask {
	public static final FilterMask FULLY_FILTERED = new FilterMask(new BitSet(0), FilterMask.FilterStatus.FULLY_FILTERED);
	public static final FilterMask PASS_THROUGH = new FilterMask(new BitSet(0), FilterMask.FilterStatus.PASS_THROUGH);
	private static final char FILTERED = '#';
	private final BitSet mask;
	private final FilterMask.FilterStatus status;

	private FilterMask(BitSet mask, FilterMask.FilterStatus status) {
		this.mask = mask;
		this.status = status;
	}

	public FilterMask(int length) {
		this(new BitSet(length), FilterMask.FilterStatus.PARTIALLY_FILTERED);
	}

	public static FilterMask readMask(PacketByteBuf buf) {
		FilterMask.FilterStatus filterStatus = buf.readEnumConstant(FilterMask.FilterStatus.class);

		return switch (filterStatus) {
			case PASS_THROUGH -> PASS_THROUGH;
			case FULLY_FILTERED -> FULLY_FILTERED;
			case PARTIALLY_FILTERED -> new FilterMask(buf.readBitSet(), FilterMask.FilterStatus.PARTIALLY_FILTERED);
		};
	}

	public static void writeMask(PacketByteBuf buf, FilterMask mask) {
		buf.writeEnumConstant(mask.status);
		if (mask.status == FilterMask.FilterStatus.PARTIALLY_FILTERED) {
			buf.writeBitSet(mask.mask);
		}
	}

	public void markFiltered(int index) {
		this.mask.set(index);
	}

	@Nullable
	public String filter(String raw) {
		return switch (this.status) {
			case PASS_THROUGH -> raw;
			case FULLY_FILTERED -> null;
			case PARTIALLY_FILTERED -> {
				char[] cs = raw.toCharArray();

				for (int i = 0; i < cs.length && i < this.mask.length(); i++) {
					if (this.mask.get(i)) {
						cs[i] = '#';
					}
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
		return this.status == FilterMask.FilterStatus.PASS_THROUGH;
	}

	public boolean isFullyFiltered() {
		return this.status == FilterMask.FilterStatus.FULLY_FILTERED;
	}

	static enum FilterStatus {
		PASS_THROUGH,
		FULLY_FILTERED,
		PARTIALLY_FILTERED;
	}
}
