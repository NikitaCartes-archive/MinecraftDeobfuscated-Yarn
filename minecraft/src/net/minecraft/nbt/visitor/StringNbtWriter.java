package net.minecraft.nbt.visitor;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import net.minecraft.class_5627;
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

public class StringNbtWriter implements class_5627 {
	private static final Pattern SIMPLE_NAME = Pattern.compile("[A-Za-z0-9._+-]+");
	private final StringBuilder result = new StringBuilder();

	public String apply(Tag tag) {
		tag.method_32289(this);
		return this.result.toString();
	}

	@Override
	public void method_32302(StringTag stringTag) {
		this.result.append(StringTag.escape(stringTag.asString()));
	}

	@Override
	public void method_32291(ByteTag byteTag) {
		this.result.append(byteTag.getNumber()).append('b');
	}

	@Override
	public void method_32301(ShortTag shortTag) {
		this.result.append(shortTag.getNumber()).append('s');
	}

	@Override
	public void method_32297(IntTag intTag) {
		this.result.append(intTag.getNumber());
	}

	@Override
	public void method_32300(LongTag longTag) {
		this.result.append(longTag.getNumber()).append('L');
	}

	@Override
	public void method_32295(FloatTag floatTag) {
		this.result.append(floatTag.getFloat()).append('f');
	}

	@Override
	public void method_32293(DoubleTag doubleTag) {
		this.result.append(doubleTag.getDouble()).append('d');
	}

	@Override
	public void method_32290(ByteArrayTag byteArrayTag) {
		this.result.append("[B;");
		byte[] bs = byteArrayTag.getByteArray();

		for (int i = 0; i < bs.length; i++) {
			if (i != 0) {
				this.result.append(',');
			}

			this.result.append(bs[i]).append('B');
		}

		this.result.append(']');
	}

	@Override
	public void method_32296(IntArrayTag intArrayTag) {
		this.result.append("[I;");
		int[] is = intArrayTag.getIntArray();

		for (int i = 0; i < is.length; i++) {
			if (i != 0) {
				this.result.append(',');
			}

			this.result.append(is[i]);
		}

		this.result.append(']');
	}

	@Override
	public void method_32299(LongArrayTag longArrayTag) {
		this.result.append("[L;");
		long[] ls = longArrayTag.getLongArray();

		for (int i = 0; i < ls.length; i++) {
			if (i != 0) {
				this.result.append(',');
			}

			this.result.append(ls[i]).append('L');
		}

		this.result.append(']');
	}

	@Override
	public void method_32298(ListTag listTag) {
		this.result.append('[');

		for (int i = 0; i < listTag.size(); i++) {
			if (i != 0) {
				this.result.append(',');
			}

			this.result.append(new StringNbtWriter().apply(listTag.get(i)));
		}

		this.result.append(']');
	}

	@Override
	public void method_32292(CompoundTag compoundTag) {
		this.result.append('{');
		List<String> list = Lists.<String>newArrayList(compoundTag.getKeys());
		Collections.sort(list);

		for (String string : list) {
			if (this.result.length() != 1) {
				this.result.append(',');
			}

			this.result.append(escapeName(string)).append(':').append(new StringNbtWriter().apply(compoundTag.get(string)));
		}

		this.result.append('}');
	}

	protected static String escapeName(String name) {
		return SIMPLE_NAME.matcher(name).matches() ? name : StringTag.escape(name);
	}

	@Override
	public void method_32294(EndTag endTag) {
		this.result.append("END");
	}
}
