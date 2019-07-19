package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class EndTag implements Tag {
	@Override
	public void read(DataInput input, int depth, PositionTracker positionTracker) throws IOException {
		positionTracker.add(64L);
	}

	@Override
	public void write(DataOutput output) throws IOException {
	}

	@Override
	public byte getType() {
		return 0;
	}

	@Override
	public String toString() {
		return "END";
	}

	public EndTag copy() {
		return new EndTag();
	}

	@Override
	public Text toText(String indent, int depth) {
		return new LiteralText("");
	}

	public boolean equals(Object o) {
		return o instanceof EndTag;
	}

	public int hashCode() {
		return this.getType();
	}
}
