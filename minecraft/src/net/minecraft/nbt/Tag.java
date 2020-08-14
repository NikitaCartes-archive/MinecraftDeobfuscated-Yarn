package net.minecraft.nbt;

import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public interface Tag {
	Formatting AQUA = Formatting.AQUA;
	Formatting GREEN = Formatting.GREEN;
	Formatting GOLD = Formatting.GOLD;
	Formatting RED = Formatting.RED;

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
