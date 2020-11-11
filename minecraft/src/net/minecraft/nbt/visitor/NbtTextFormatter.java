package net.minecraft.nbt.visitor;

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

/**
 * Formats an NBT tag into a colored, multiline {@link Text} representation suitable for human-readable
 * displays.
 */
public class NbtTextFormatter implements NbtTagVisitor {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final ByteCollection SINGLE_LINE_ELEMENT_TYPES = new ByteOpenHashSet(Arrays.asList((byte)1, (byte)2, (byte)3, (byte)4, (byte)5, (byte)6));
	private static final Formatting NAME_COLOR = Formatting.AQUA;
	private static final Formatting STRING_COLOR = Formatting.GREEN;
	private static final Formatting NUMBER_COLOR = Formatting.GOLD;
	private static final Formatting TYPE_SUFFIX_COLOR = Formatting.RED;
	private static final Pattern SIMPLE_NAME = Pattern.compile("[A-Za-z0-9._+-]+");
	private static final String KEY_VALUE_SEPARATOR = String.valueOf(':');
	private static final String ENTRY_SEPARATOR = String.valueOf(',');
	private final String prefix;
	private final int indentationLevel;
	private Text result;

	public NbtTextFormatter(String prefix, int indentationLevel) {
		this.prefix = prefix;
		this.indentationLevel = indentationLevel;
	}

	public Text apply(Tag tag) {
		tag.accept(this);
		return this.result;
	}

	@Override
	public void visitStringTag(StringTag tag) {
		String string = StringTag.escape(tag.asString());
		String string2 = string.substring(0, 1);
		Text text = new LiteralText(string.substring(1, string.length() - 1)).formatted(STRING_COLOR);
		this.result = new LiteralText(string2).append(text).append(string2);
	}

	@Override
	public void visitByteTag(ByteTag tag) {
		Text text = new LiteralText("b").formatted(TYPE_SUFFIX_COLOR);
		this.result = new LiteralText(String.valueOf(tag.getNumber())).append(text).formatted(NUMBER_COLOR);
	}

	@Override
	public void visitShortTag(ShortTag tag) {
		Text text = new LiteralText("s").formatted(TYPE_SUFFIX_COLOR);
		this.result = new LiteralText(String.valueOf(tag.getNumber())).append(text).formatted(NUMBER_COLOR);
	}

	@Override
	public void visitIntTag(IntTag tag) {
		this.result = new LiteralText(String.valueOf(tag.getNumber())).formatted(NUMBER_COLOR);
	}

	@Override
	public void visitLongTag(LongTag tag) {
		Text text = new LiteralText("L").formatted(TYPE_SUFFIX_COLOR);
		this.result = new LiteralText(String.valueOf(tag.getNumber())).append(text).formatted(NUMBER_COLOR);
	}

	@Override
	public void visitFloatTag(FloatTag tag) {
		Text text = new LiteralText("f").formatted(TYPE_SUFFIX_COLOR);
		this.result = new LiteralText(String.valueOf(tag.getFloat())).append(text).formatted(NUMBER_COLOR);
	}

	@Override
	public void visitDoubleTag(DoubleTag tag) {
		Text text = new LiteralText("d").formatted(TYPE_SUFFIX_COLOR);
		this.result = new LiteralText(String.valueOf(tag.getDouble())).append(text).formatted(NUMBER_COLOR);
	}

	@Override
	public void visitByteArrayTag(ByteArrayTag tag) {
		Text text = new LiteralText("B").formatted(TYPE_SUFFIX_COLOR);
		MutableText mutableText = new LiteralText("[").append(text).append(";");
		byte[] bs = tag.getByteArray();

		for (int i = 0; i < bs.length; i++) {
			MutableText mutableText2 = new LiteralText(String.valueOf(bs[i])).formatted(NUMBER_COLOR);
			mutableText.append(" ").append(mutableText2).append(text);
			if (i != bs.length - 1) {
				mutableText.append(ENTRY_SEPARATOR);
			}
		}

		mutableText.append("]");
		this.result = mutableText;
	}

	@Override
	public void visitIntArrayTag(IntArrayTag tag) {
		Text text = new LiteralText("I").formatted(TYPE_SUFFIX_COLOR);
		MutableText mutableText = new LiteralText("[").append(text).append(";");
		int[] is = tag.getIntArray();

		for (int i = 0; i < is.length; i++) {
			mutableText.append(" ").append(new LiteralText(String.valueOf(is[i])).formatted(NUMBER_COLOR));
			if (i != is.length - 1) {
				mutableText.append(ENTRY_SEPARATOR);
			}
		}

		mutableText.append("]");
		this.result = mutableText;
	}

	@Override
	public void visitLongArrayTag(LongArrayTag tag) {
		Text text = new LiteralText("L").formatted(TYPE_SUFFIX_COLOR);
		MutableText mutableText = new LiteralText("[").append(text).append(";");
		long[] ls = tag.getLongArray();

		for (int i = 0; i < ls.length; i++) {
			Text text2 = new LiteralText(String.valueOf(ls[i])).formatted(NUMBER_COLOR);
			mutableText.append(" ").append(text2).append(text);
			if (i != ls.length - 1) {
				mutableText.append(ENTRY_SEPARATOR);
			}
		}

		mutableText.append("]");
		this.result = mutableText;
	}

	@Override
	public void visitListTag(ListTag tag) {
		if (tag.isEmpty()) {
			this.result = new LiteralText("[]");
		} else if (SINGLE_LINE_ELEMENT_TYPES.contains(tag.getElementType()) && tag.size() <= 8) {
			String string = ENTRY_SEPARATOR + " ";
			MutableText mutableText = new LiteralText("[");

			for (int i = 0; i < tag.size(); i++) {
				if (i != 0) {
					mutableText.append(string);
				}

				mutableText.append(new NbtTextFormatter(this.prefix, this.indentationLevel).apply(tag.get(i)));
			}

			mutableText.append("]");
			this.result = mutableText;
		} else {
			MutableText mutableText2 = new LiteralText("[");
			if (!this.prefix.isEmpty()) {
				mutableText2.append("\n");
			}

			for (int j = 0; j < tag.size(); j++) {
				MutableText mutableText3 = new LiteralText(Strings.repeat(this.prefix, this.indentationLevel + 1));
				mutableText3.append(new NbtTextFormatter(this.prefix, this.indentationLevel + 1).apply(tag.get(j)));
				if (j != tag.size() - 1) {
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
	public void visitCompoundTag(CompoundTag tag) {
		if (tag.isEmpty()) {
			this.result = new LiteralText("{}");
		} else {
			MutableText mutableText = new LiteralText("{");
			Collection<String> collection = tag.getKeys();
			if (LOGGER.isDebugEnabled()) {
				List<String> list = Lists.<String>newArrayList(tag.getKeys());
				Collections.sort(list);
				collection = list;
			}

			if (!this.prefix.isEmpty()) {
				mutableText.append("\n");
			}

			Iterator<String> iterator = collection.iterator();

			while (iterator.hasNext()) {
				String string = (String)iterator.next();
				MutableText mutableText2 = new LiteralText(Strings.repeat(this.prefix, this.indentationLevel + 1))
					.append(escapeName(string))
					.append(KEY_VALUE_SEPARATOR)
					.append(" ")
					.append(new NbtTextFormatter(this.prefix, this.indentationLevel + 1).apply(tag.get(string)));
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
			return new LiteralText(name).formatted(NAME_COLOR);
		} else {
			String string = StringTag.escape(name);
			String string2 = string.substring(0, 1);
			Text text = new LiteralText(string.substring(1, string.length() - 1)).formatted(NAME_COLOR);
			return new LiteralText(string2).append(text).append(string2);
		}
	}

	@Override
	public void visitEndTag(EndTag tag) {
		this.result = LiteralText.EMPTY;
	}
}
