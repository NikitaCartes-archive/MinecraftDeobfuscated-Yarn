package net.minecraft;

import java.util.BitSet;
import javax.annotation.Nullable;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.message.DecoratedContents;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

public class class_7649 {
	public static final class_7649 field_39941 = new class_7649(new BitSet(0), class_7649.class_7650.FULLY_FILTERED);
	public static final class_7649 field_39942 = new class_7649(new BitSet(0), class_7649.class_7650.PASS_THROUGH);
	private static final char field_39943 = '#';
	private final BitSet field_39944;
	private final class_7649.class_7650 field_39945;

	private class_7649(BitSet bitSet, class_7649.class_7650 arg) {
		this.field_39944 = bitSet;
		this.field_39945 = arg;
	}

	public class_7649(int i) {
		this(new BitSet(i), class_7649.class_7650.PARTIALLY_FILTERED);
	}

	public static class_7649 method_45090(PacketByteBuf packetByteBuf) {
		class_7649.class_7650 lv = packetByteBuf.readEnumConstant(class_7649.class_7650.class);

		return switch (lv) {
			case PASS_THROUGH -> field_39942;
			case FULLY_FILTERED -> field_39941;
			case PARTIALLY_FILTERED -> new class_7649(packetByteBuf.readBitSet(), class_7649.class_7650.PARTIALLY_FILTERED);
		};
	}

	public static void method_45091(PacketByteBuf packetByteBuf, class_7649 arg) {
		packetByteBuf.writeEnumConstant(arg.field_39945);
		if (arg.field_39945 == class_7649.class_7650.PARTIALLY_FILTERED) {
			packetByteBuf.writeBitSet(arg.field_39944);
		}
	}

	public void method_45088(int i) {
		this.field_39944.set(i);
	}

	@Nullable
	public String method_45089(String string) {
		return switch (this.field_39945) {
			case PASS_THROUGH -> string;
			case FULLY_FILTERED -> null;
			case PARTIALLY_FILTERED -> {
				char[] cs = string.toCharArray();

				for (int i = 0; i < cs.length && i < this.field_39944.length(); i++) {
					if (this.field_39944.get(i)) {
						cs[i] = '#';
					}
				}

				yield new String(cs);
			}
		};
	}

	@Nullable
	public Text method_45092(DecoratedContents decoratedContents) {
		String string = decoratedContents.plain();
		return Util.map(this.method_45089(string), Text::literal);
	}

	public boolean method_45087() {
		return this.field_39945 == class_7649.class_7650.PASS_THROUGH;
	}

	public boolean method_45093() {
		return this.field_39945 == class_7649.class_7650.FULLY_FILTERED;
	}

	static enum class_7650 {
		PASS_THROUGH,
		FULLY_FILTERED,
		PARTIALLY_FILTERED;
	}
}
