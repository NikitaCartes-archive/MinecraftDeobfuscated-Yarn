package net.minecraft.nbt;

import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public interface Tag {
	Formatting AQUA = Formatting.field_1075;
	Formatting GREEN = Formatting.field_1060;
	Formatting GOLD = Formatting.field_1065;
	Formatting RED = Formatting.field_1061;

	void write(DataOutput output) throws IOException;

	String toString();

	byte getType();

	TagReader<?> getReader();

	Tag copy();

	default String asString() {
		return this.toString();
	}

	default Text toText() {
		return this.toText("", 0);
	}

	Text toText(String indent, int depth);
}
