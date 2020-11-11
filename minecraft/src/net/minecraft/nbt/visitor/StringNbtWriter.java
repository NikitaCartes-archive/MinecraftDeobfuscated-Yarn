package net.minecraft.nbt.visitor;

import com.google.common.collect.Lists;
import java.util.Collections;
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

/**
 * A simple converter to turn NBT into single-line SNBT. The output may be parsed back into binary NBT.
 */
public class StringNbtWriter implements NbtTagVisitor {
	private static final Pattern SIMPLE_NAME = Pattern.compile("[A-Za-z0-9._+-]+");
	private final StringBuilder result = new StringBuilder();

	public String apply(Tag tag) {
		tag.accept(this);
		return this.result.toString();
	}

	@Override
	public void visitStringTag(StringTag tag) {
		this.result.append(StringTag.escape(tag.asString()));
	}

	@Override
	public void visitByteTag(ByteTag tag) {
		this.result.append(tag.getNumber()).append('b');
	}

	@Override
	public void visitShortTag(ShortTag tag) {
		this.result.append(tag.getNumber()).append('s');
	}

	@Override
	public void visitIntTag(IntTag tag) {
		this.result.append(tag.getNumber());
	}

	@Override
	public void visitLongTag(LongTag tag) {
		this.result.append(tag.getNumber()).append('L');
	}

	@Override
	public void visitFloatTag(FloatTag tag) {
		this.result.append(tag.getFloat()).append('f');
	}

	@Override
	public void visitDoubleTag(DoubleTag tag) {
		this.result.append(tag.getDouble()).append('d');
	}

	@Override
	public void visitByteArrayTag(ByteArrayTag tag) {
		this.result.append("[B;");
		byte[] bs = tag.getByteArray();

		for (int i = 0; i < bs.length; i++) {
			if (i != 0) {
				this.result.append(',');
			}

			this.result.append(bs[i]).append('B');
		}

		this.result.append(']');
	}

	@Override
	public void visitIntArrayTag(IntArrayTag tag) {
		this.result.append("[I;");
		int[] is = tag.getIntArray();

		for (int i = 0; i < is.length; i++) {
			if (i != 0) {
				this.result.append(',');
			}

			this.result.append(is[i]);
		}

		this.result.append(']');
	}

	@Override
	public void visitLongArrayTag(LongArrayTag tag) {
		this.result.append("[L;");
		long[] ls = tag.getLongArray();

		for (int i = 0; i < ls.length; i++) {
			if (i != 0) {
				this.result.append(',');
			}

			this.result.append(ls[i]).append('L');
		}

		this.result.append(']');
	}

	@Override
	public void visitListTag(ListTag tag) {
		this.result.append('[');

		for (int i = 0; i < tag.size(); i++) {
			if (i != 0) {
				this.result.append(',');
			}

			this.result.append(new StringNbtWriter().apply(tag.get(i)));
		}

		this.result.append(']');
	}

	@Override
	public void visitCompoundTag(CompoundTag tag) {
		this.result.append('{');
		List<String> list = Lists.<String>newArrayList(tag.getKeys());
		Collections.sort(list);

		for (String string : list) {
			if (this.result.length() != 1) {
				this.result.append(',');
			}

			this.result.append(escapeName(string)).append(':').append(new StringNbtWriter().apply(tag.get(string)));
		}

		this.result.append('}');
	}

	protected static String escapeName(String name) {
		return SIMPLE_NAME.matcher(name).matches() ? name : StringTag.escape(name);
	}

	@Override
	public void visitEndTag(EndTag tag) {
		this.result.append("END");
	}
}
