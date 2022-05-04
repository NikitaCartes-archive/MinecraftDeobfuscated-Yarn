package net.minecraft.nbt.visitor;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtByteArray;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtEnd;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtLong;
import net.minecraft.nbt.NbtLongArray;
import net.minecraft.nbt.NbtShort;
import net.minecraft.nbt.NbtString;

/**
 * A simple converter to turn NBT into single-line SNBT. The output may be parsed back into binary NBT.
 */
public class StringNbtWriter implements NbtElementVisitor {
	private static final Pattern SIMPLE_NAME = Pattern.compile("[A-Za-z0-9._+-]+");
	private final StringBuilder result = new StringBuilder();

	/**
	 * {@return the stringified NBT {@code element}}
	 */
	public String apply(NbtElement element) {
		element.accept(this);
		return this.result.toString();
	}

	@Override
	public void visitString(NbtString element) {
		this.result.append(NbtString.escape(element.asString()));
	}

	@Override
	public void visitByte(NbtByte element) {
		this.result.append(element.numberValue()).append('b');
	}

	@Override
	public void visitShort(NbtShort element) {
		this.result.append(element.numberValue()).append('s');
	}

	@Override
	public void visitInt(NbtInt element) {
		this.result.append(element.numberValue());
	}

	@Override
	public void visitLong(NbtLong element) {
		this.result.append(element.numberValue()).append('L');
	}

	@Override
	public void visitFloat(NbtFloat element) {
		this.result.append(element.floatValue()).append('f');
	}

	@Override
	public void visitDouble(NbtDouble element) {
		this.result.append(element.doubleValue()).append('d');
	}

	@Override
	public void visitByteArray(NbtByteArray element) {
		this.result.append("[B;");
		byte[] bs = element.getByteArray();

		for (int i = 0; i < bs.length; i++) {
			if (i != 0) {
				this.result.append(',');
			}

			this.result.append(bs[i]).append('B');
		}

		this.result.append(']');
	}

	@Override
	public void visitIntArray(NbtIntArray element) {
		this.result.append("[I;");
		int[] is = element.getIntArray();

		for (int i = 0; i < is.length; i++) {
			if (i != 0) {
				this.result.append(',');
			}

			this.result.append(is[i]);
		}

		this.result.append(']');
	}

	@Override
	public void visitLongArray(NbtLongArray element) {
		this.result.append("[L;");
		long[] ls = element.getLongArray();

		for (int i = 0; i < ls.length; i++) {
			if (i != 0) {
				this.result.append(',');
			}

			this.result.append(ls[i]).append('L');
		}

		this.result.append(']');
	}

	@Override
	public void visitList(NbtList element) {
		this.result.append('[');

		for (int i = 0; i < element.size(); i++) {
			if (i != 0) {
				this.result.append(',');
			}

			this.result.append(new StringNbtWriter().apply(element.get(i)));
		}

		this.result.append(']');
	}

	@Override
	public void visitCompound(NbtCompound compound) {
		this.result.append('{');
		List<String> list = Lists.<String>newArrayList(compound.getKeys());
		Collections.sort(list);

		for (String string : list) {
			if (this.result.length() != 1) {
				this.result.append(',');
			}

			this.result.append(escapeName(string)).append(':').append(new StringNbtWriter().apply(compound.get(string)));
		}

		this.result.append('}');
	}

	protected static String escapeName(String name) {
		return SIMPLE_NAME.matcher(name).matches() ? name : NbtString.escape(name);
	}

	@Override
	public void visitEnd(NbtEnd element) {
		this.result.append("END");
	}
}
