package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class EndTag implements Tag {
	@Override
	public void read(DataInput dataInput, int i, PositionTracker positionTracker) throws IOException {
		positionTracker.add(64L);
	}

	@Override
	public void write(DataOutput dataOutput) throws IOException {
	}

	@Override
	public byte getType() {
		return 0;
	}

	@Override
	public String toString() {
		return "END";
	}

	public EndTag method_10586() {
		return new EndTag();
	}

	@Override
	public Text toText(String string, int i) {
		return new LiteralText("");
	}

	public boolean equals(Object object) {
		return object instanceof EndTag;
	}

	public int hashCode() {
		return this.getType();
	}
}
