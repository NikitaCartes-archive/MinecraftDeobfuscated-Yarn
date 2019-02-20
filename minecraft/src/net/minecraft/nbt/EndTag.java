package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;

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
	public TextComponent toTextComponent(String string, int i) {
		return new StringTextComponent("");
	}

	public boolean equals(Object object) {
		return object instanceof EndTag;
	}

	public int hashCode() {
		return this.getType();
	}
}
