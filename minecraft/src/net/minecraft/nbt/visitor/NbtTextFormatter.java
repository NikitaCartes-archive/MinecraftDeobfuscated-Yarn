package net.minecraft.nbt.visitor;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteOpenHashSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
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
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;

/**
 * Formats an NBT element into a colored, multiline {@link Text} representation suitable for human-readable
 * displays.
 */
public class NbtTextFormatter implements NbtElementVisitor {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final int field_33271 = 8;
	private static final ByteCollection SINGLE_LINE_ELEMENT_TYPES = new ByteOpenHashSet(Arrays.asList((byte)1, (byte)2, (byte)3, (byte)4, (byte)5, (byte)6));
	private static final Formatting NAME_COLOR = Formatting.AQUA;
	private static final Formatting STRING_COLOR = Formatting.GREEN;
	private static final Formatting NUMBER_COLOR = Formatting.GOLD;
	private static final Formatting TYPE_SUFFIX_COLOR = Formatting.RED;
	private static final Pattern SIMPLE_NAME = Pattern.compile("[A-Za-z0-9._+-]+");
	private static final String KEY_VALUE_SEPARATOR = String.valueOf(':');
	private static final String ENTRY_SEPARATOR = String.valueOf(',');
	private static final String SQUARE_OPEN_BRACKET = "[";
	private static final String SQUARE_CLOSE_BRACKET = "]";
	private static final String SEMICOLON = ";";
	private static final String SPACE = " ";
	private static final String CURLY_OPEN_BRACKET = "{";
	private static final String CURLY_CLOSE_BRACKET = "}";
	private static final String NEW_LINE = "\n";
	private final String prefix;
	private final int indentationLevel;
	private Text result = ScreenTexts.EMPTY;

	public NbtTextFormatter(String prefix, int indentationLevel) {
		this.prefix = prefix;
		this.indentationLevel = indentationLevel;
	}

	/**
	 * {@return the textified NBT {@code element}}
	 */
	public Text apply(NbtElement element) {
		element.accept(this);
		return this.result;
	}

	@Override
	public void visitString(NbtString element) {
		String string = NbtString.escape(element.asString());
		String string2 = string.substring(0, 1);
		Text text = Text.literal(string.substring(1, string.length() - 1)).formatted(STRING_COLOR);
		this.result = Text.literal(string2).append(text).append(string2);
	}

	@Override
	public void visitByte(NbtByte element) {
		Text text = Text.literal("b").formatted(TYPE_SUFFIX_COLOR);
		this.result = Text.literal(String.valueOf(element.numberValue())).append(text).formatted(NUMBER_COLOR);
	}

	@Override
	public void visitShort(NbtShort element) {
		Text text = Text.literal("s").formatted(TYPE_SUFFIX_COLOR);
		this.result = Text.literal(String.valueOf(element.numberValue())).append(text).formatted(NUMBER_COLOR);
	}

	@Override
	public void visitInt(NbtInt element) {
		this.result = Text.literal(String.valueOf(element.numberValue())).formatted(NUMBER_COLOR);
	}

	@Override
	public void visitLong(NbtLong element) {
		Text text = Text.literal("L").formatted(TYPE_SUFFIX_COLOR);
		this.result = Text.literal(String.valueOf(element.numberValue())).append(text).formatted(NUMBER_COLOR);
	}

	@Override
	public void visitFloat(NbtFloat element) {
		Text text = Text.literal("f").formatted(TYPE_SUFFIX_COLOR);
		this.result = Text.literal(String.valueOf(element.floatValue())).append(text).formatted(NUMBER_COLOR);
	}

	@Override
	public void visitDouble(NbtDouble element) {
		Text text = Text.literal("d").formatted(TYPE_SUFFIX_COLOR);
		this.result = Text.literal(String.valueOf(element.doubleValue())).append(text).formatted(NUMBER_COLOR);
	}

	@Override
	public void visitByteArray(NbtByteArray element) {
		Text text = Text.literal("B").formatted(TYPE_SUFFIX_COLOR);
		MutableText mutableText = Text.literal("[").append(text).append(";");
		byte[] bs = element.getByteArray();

		for (int i = 0; i < bs.length; i++) {
			MutableText mutableText2 = Text.literal(String.valueOf(bs[i])).formatted(NUMBER_COLOR);
			mutableText.append(" ").append(mutableText2).append(text);
			if (i != bs.length - 1) {
				mutableText.append(ENTRY_SEPARATOR);
			}
		}

		mutableText.append("]");
		this.result = mutableText;
	}

	@Override
	public void visitIntArray(NbtIntArray element) {
		Text text = Text.literal("I").formatted(TYPE_SUFFIX_COLOR);
		MutableText mutableText = Text.literal("[").append(text).append(";");
		int[] is = element.getIntArray();

		for (int i = 0; i < is.length; i++) {
			mutableText.append(" ").append(Text.literal(String.valueOf(is[i])).formatted(NUMBER_COLOR));
			if (i != is.length - 1) {
				mutableText.append(ENTRY_SEPARATOR);
			}
		}

		mutableText.append("]");
		this.result = mutableText;
	}

	@Override
	public void visitLongArray(NbtLongArray element) {
		Text text = Text.literal("L").formatted(TYPE_SUFFIX_COLOR);
		MutableText mutableText = Text.literal("[").append(text).append(";");
		long[] ls = element.getLongArray();

		for (int i = 0; i < ls.length; i++) {
			Text text2 = Text.literal(String.valueOf(ls[i])).formatted(NUMBER_COLOR);
			mutableText.append(" ").append(text2).append(text);
			if (i != ls.length - 1) {
				mutableText.append(ENTRY_SEPARATOR);
			}
		}

		mutableText.append("]");
		this.result = mutableText;
	}

	@Override
	public void visitList(NbtList element) {
		if (element.isEmpty()) {
			this.result = Text.literal("[]");
		} else if (SINGLE_LINE_ELEMENT_TYPES.contains(element.getHeldType()) && element.size() <= 8) {
			String string = ENTRY_SEPARATOR + " ";
			MutableText mutableText = Text.literal("[");

			for (int i = 0; i < element.size(); i++) {
				if (i != 0) {
					mutableText.append(string);
				}

				mutableText.append(new NbtTextFormatter(this.prefix, this.indentationLevel).apply(element.get(i)));
			}

			mutableText.append("]");
			this.result = mutableText;
		} else {
			MutableText mutableText2 = Text.literal("[");
			if (!this.prefix.isEmpty()) {
				mutableText2.append("\n");
			}

			for (int j = 0; j < element.size(); j++) {
				MutableText mutableText3 = Text.literal(Strings.repeat(this.prefix, this.indentationLevel + 1));
				mutableText3.append(new NbtTextFormatter(this.prefix, this.indentationLevel + 1).apply(element.get(j)));
				if (j != element.size() - 1) {
					mutableText3.append(ENTRY_SEPARATOR).append(this.prefix.isEmpty() ? " " : "\n");
				}

				mutableText2.append(mutableText3);
			}

			if (!this.prefix.isEmpty()) {
				mutableText2.append("\n").append(Strings.repeat(this.prefix, this.indentationLevel));
			}

			mutableText2.append("]");
			this.result = mutableText2;
		}
	}

	@Override
	public void visitCompound(NbtCompound compound) {
		if (compound.isEmpty()) {
			this.result = Text.literal("{}");
		} else {
			MutableText mutableText = Text.literal("{");
			Collection<String> collection = compound.getKeys();
			if (LOGGER.isDebugEnabled()) {
				List<String> list = Lists.<String>newArrayList(compound.getKeys());
				Collections.sort(list);
				collection = list;
			}

			if (!this.prefix.isEmpty()) {
				mutableText.append("\n");
			}

			Iterator<String> iterator = collection.iterator();

			while (iterator.hasNext()) {
				String string = (String)iterator.next();
				MutableText mutableText2 = Text.literal(Strings.repeat(this.prefix, this.indentationLevel + 1))
					.append(escapeName(string))
					.append(KEY_VALUE_SEPARATOR)
					.append(" ")
					.append(new NbtTextFormatter(this.prefix, this.indentationLevel + 1).apply(compound.get(string)));
				if (iterator.hasNext()) {
					mutableText2.append(ENTRY_SEPARATOR).append(this.prefix.isEmpty() ? " " : "\n");
				}

				mutableText.append(mutableText2);
			}

			if (!this.prefix.isEmpty()) {
				mutableText.append("\n").append(Strings.repeat(this.prefix, this.indentationLevel));
			}

			mutableText.append("}");
			this.result = mutableText;
		}
	}

	protected static Text escapeName(String name) {
		if (SIMPLE_NAME.matcher(name).matches()) {
			return Text.literal(name).formatted(NAME_COLOR);
		} else {
			String string = NbtString.escape(name);
			String string2 = string.substring(0, 1);
			Text text = Text.literal(string.substring(1, string.length() - 1)).formatted(NAME_COLOR);
			return Text.literal(string2).append(text).append(string2);
		}
	}

	@Override
	public void visitEnd(NbtEnd element) {
		this.result = ScreenTexts.EMPTY;
	}
}
