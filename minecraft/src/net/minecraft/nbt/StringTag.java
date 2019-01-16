package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;

public class StringTag implements Tag {
	private String value;

	public StringTag() {
		this("");
	}

	public StringTag(String string) {
		Objects.requireNonNull(string, "Null string not allowed");
		this.value = string;
	}

	@Override
	public void write(DataOutput dataOutput) throws IOException {
		dataOutput.writeUTF(this.value);
	}

	@Override
	public void read(DataInput dataInput, int i, PositionTracker positionTracker) throws IOException {
		positionTracker.add(288L);
		this.value = dataInput.readUTF();
		positionTracker.add((long)(16 * this.value.length()));
	}

	@Override
	public byte getType() {
		return 8;
	}

	@Override
	public String toString() {
		return escape(this.value, true);
	}

	public StringTag copy() {
		return new StringTag(this.value);
	}

	public boolean equals(Object object) {
		return this == object ? true : object instanceof StringTag && Objects.equals(this.value, ((StringTag)object).value);
	}

	public int hashCode() {
		return this.value.hashCode();
	}

	@Override
	public String asString() {
		return this.value;
	}

	@Override
	public TextComponent toTextComponent(String string, int i) {
		TextComponent textComponent = new StringTextComponent(escape(this.value, false)).applyFormat(GREEN);
		return new StringTextComponent("\"").append(textComponent).append("\"");
	}

	public static String escape(String string, boolean bl) {
		StringBuilder stringBuilder = new StringBuilder();
		if (bl) {
			stringBuilder.append('"');
		}

		for (int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			if (c == '\\' || c == '"') {
				stringBuilder.append('\\');
			}

			stringBuilder.append(c);
		}

		if (bl) {
			stringBuilder.append('"');
		}

		return stringBuilder.toString();
	}
}
