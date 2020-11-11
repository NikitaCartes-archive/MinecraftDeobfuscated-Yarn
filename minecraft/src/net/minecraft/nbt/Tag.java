package net.minecraft.nbt;

import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.nbt.visitor.NbtTagVisitor;
import net.minecraft.nbt.visitor.StringNbtWriter;

public interface Tag {
	void write(DataOutput output) throws IOException;

	String toString();

	byte getType();

	TagReader<?> getReader();

	Tag copy();

	default String asString() {
		return new StringNbtWriter().apply(this);
	}

	void accept(NbtTagVisitor visitor);
}
