package net.minecraft;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteOpenHashSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
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
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_5628 implements class_5627 {
	private static final Logger field_27831 = LogManager.getLogger();
	private static final ByteCollection field_27832 = new ByteOpenHashSet(Arrays.asList((byte)1, (byte)2, (byte)3, (byte)4, (byte)5, (byte)6));
	private static final Formatting field_27833 = Formatting.AQUA;
	private static final Formatting field_27834 = Formatting.GREEN;
	private static final Formatting field_27835 = Formatting.GOLD;
	private static final Formatting field_27836 = Formatting.RED;
	private static final Pattern field_27837 = Pattern.compile("[A-Za-z0-9._+-]+");
	private static final String field_27838 = String.valueOf(':');
	private static final String field_27839 = String.valueOf(',');
	private final String field_27840;
	private final int field_27841;
	private Text field_27842;

	public class_5628(String string, int i) {
		this.field_27840 = string;
		this.field_27841 = i;
	}

	public Text method_32305(Tag tag) {
		tag.method_32289(this);
		return this.field_27842;
	}

	@Override
	public void method_32302(StringTag stringTag) {
		String string = StringTag.escape(stringTag.asString());
		String string2 = string.substring(0, 1);
		Text text = new LiteralText(string.substring(1, string.length() - 1)).formatted(field_27834);
		this.field_27842 = new LiteralText(string2).append(text).append(string2);
	}

	@Override
	public void method_32291(ByteTag byteTag) {
		Text text = new LiteralText("b").formatted(field_27836);
		this.field_27842 = new LiteralText(String.valueOf(byteTag.getNumber())).append(text).formatted(field_27835);
	}

	@Override
	public void method_32301(ShortTag shortTag) {
		Text text = new LiteralText("s").formatted(field_27836);
		this.field_27842 = new LiteralText(String.valueOf(shortTag.getNumber())).append(text).formatted(field_27835);
	}

	@Override
	public void method_32297(IntTag intTag) {
		this.field_27842 = new LiteralText(String.valueOf(intTag.getNumber())).formatted(field_27835);
	}

	@Override
	public void method_32300(LongTag longTag) {
		Text text = new LiteralText("L").formatted(field_27836);
		this.field_27842 = new LiteralText(String.valueOf(longTag.getNumber())).append(text).formatted(field_27835);
	}

	@Override
	public void method_32295(FloatTag floatTag) {
		Text text = new LiteralText("f").formatted(field_27836);
		this.field_27842 = new LiteralText(String.valueOf(floatTag.getFloat())).append(text).formatted(field_27835);
	}

	@Override
	public void method_32293(DoubleTag doubleTag) {
		Text text = new LiteralText("d").formatted(field_27836);
		this.field_27842 = new LiteralText(String.valueOf(doubleTag.getDouble())).append(text).formatted(field_27835);
	}

	@Override
	public void method_32290(ByteArrayTag byteArrayTag) {
		Text text = new LiteralText("B").formatted(field_27836);
		MutableText mutableText = new LiteralText("[").append(text).append(";");
		byte[] bs = byteArrayTag.getByteArray();

		for (int i = 0; i < bs.length; i++) {
			MutableText mutableText2 = new LiteralText(String.valueOf(bs[i])).formatted(field_27835);
			mutableText.append(" ").append(mutableText2).append(text);
			if (i != bs.length - 1) {
				mutableText.append(field_27839);
			}
		}

		mutableText.append("]");
		this.field_27842 = mutableText;
	}

	@Override
	public void method_32296(IntArrayTag intArrayTag) {
		Text text = new LiteralText("I").formatted(field_27836);
		MutableText mutableText = new LiteralText("[").append(text).append(";");
		int[] is = intArrayTag.getIntArray();

		for (int i = 0; i < is.length; i++) {
			mutableText.append(" ").append(new LiteralText(String.valueOf(is[i])).formatted(field_27835));
			if (i != is.length - 1) {
				mutableText.append(field_27839);
			}
		}

		mutableText.append("]");
		this.field_27842 = mutableText;
	}

	@Override
	public void method_32299(LongArrayTag longArrayTag) {
		Text text = new LiteralText("L").formatted(field_27836);
		MutableText mutableText = new LiteralText("[").append(text).append(";");
		long[] ls = longArrayTag.getLongArray();

		for (int i = 0; i < ls.length; i++) {
			Text text2 = new LiteralText(String.valueOf(ls[i])).formatted(field_27835);
			mutableText.append(" ").append(text2).append(text);
			if (i != ls.length - 1) {
				mutableText.append(field_27839);
			}
		}

		mutableText.append("]");
		this.field_27842 = mutableText;
	}

	@Override
	public void method_32298(ListTag listTag) {
		if (listTag.isEmpty()) {
			this.field_27842 = new LiteralText("[]");
		} else if (field_27832.contains(listTag.getElementType()) && listTag.size() <= 8) {
			String string = field_27839 + " ";
			MutableText mutableText = new LiteralText("[");

			for (int i = 0; i < listTag.size(); i++) {
				if (i != 0) {
					mutableText.append(string);
				}

				mutableText.append(new class_5628(this.field_27840, this.field_27841).method_32305(listTag.get(i)));
			}

			mutableText.append("]");
			this.field_27842 = mutableText;
		} else {
			MutableText mutableText2 = new LiteralText("[");
			if (!this.field_27840.isEmpty()) {
				mutableText2.append("\n");
			}

			for (int j = 0; j < listTag.size(); j++) {
				MutableText mutableText3 = new LiteralText(Strings.repeat(this.field_27840, this.field_27841 + 1));
				mutableText3.append(new class_5628(this.field_27840, this.field_27841 + 1).method_32305(listTag.get(j)));
				if (j != listTag.size() - 1) {
					mutableText3.append(field_27839).append(this.field_27840.isEmpty() ? " " : "\n");
				}

				mutableText2.append(mutableText3);
			}

			if (!this.field_27840.isEmpty()) {
				mutableText2.append("\n").append(Strings.repeat(this.field_27840, this.field_27841));
			}

			mutableText2.append("]");
			this.field_27842 = mutableText2;
		}
	}

	@Override
	public void method_32292(CompoundTag compoundTag) {
		if (compoundTag.isEmpty()) {
			this.field_27842 = new LiteralText("{}");
		} else {
			MutableText mutableText = new LiteralText("{");
			Collection<String> collection = compoundTag.getKeys();
			if (field_27831.isDebugEnabled()) {
				List<String> list = Lists.<String>newArrayList(compoundTag.getKeys());
				Collections.sort(list);
				collection = list;
			}

			if (!this.field_27840.isEmpty()) {
				mutableText.append("\n");
			}

			Iterator<String> iterator = collection.iterator();

			while (iterator.hasNext()) {
				String string = (String)iterator.next();
				MutableText mutableText2 = new LiteralText(Strings.repeat(this.field_27840, this.field_27841 + 1))
					.append(method_32304(string))
					.append(field_27838)
					.append(" ")
					.append(new class_5628(this.field_27840, this.field_27841 + 1).method_32305(compoundTag.get(string)));
				if (iterator.hasNext()) {
					mutableText2.append(field_27839).append(this.field_27840.isEmpty() ? " " : "\n");
				}

				mutableText.append(mutableText2);
			}

			if (!this.field_27840.isEmpty()) {
				mutableText.append("\n").append(Strings.repeat(this.field_27840, this.field_27841));
			}

			mutableText.append("}");
			this.field_27842 = mutableText;
		}
	}

	protected static Text method_32304(String string) {
		if (field_27837.matcher(string).matches()) {
			return new LiteralText(string).formatted(field_27833);
		} else {
			String string2 = StringTag.escape(string);
			String string3 = string2.substring(0, 1);
			Text text = new LiteralText(string2.substring(1, string2.length() - 1)).formatted(field_27833);
			return new LiteralText(string3).append(text).append(string3);
		}
	}

	@Override
	public void method_32294(EndTag endTag) {
		this.field_27842 = LiteralText.EMPTY;
	}
}
