package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.nbt.scanner.NbtScanner;
import net.minecraft.nbt.visitor.NbtElementVisitor;

/**
 * Represents the NBT null value.
 * Defines the end of an NBT compound object,
 * represents nonexistent values in an NBT compound object,
 * and is the type of empty NBT lists.
 */
public class NbtNull implements NbtElement {
	private static final int SIZE = 64;
	public static final NbtType<NbtNull> TYPE = new NbtType<NbtNull>() {
		public NbtNull read(DataInput dataInput, int i, NbtTagSizeTracker nbtTagSizeTracker) {
			nbtTagSizeTracker.add(64L);
			return NbtNull.INSTANCE;
		}

		@Override
		public NbtScanner.Result doAccept(DataInput input, NbtScanner visitor) {
			return visitor.visitNull();
		}

		@Override
		public void skip(DataInput input, int count) {
		}

		@Override
		public void skip(DataInput input) {
		}

		@Override
		public String getCrashReportName() {
			return "END";
		}

		@Override
		public String getCommandFeedbackName() {
			return "TAG_End";
		}

		@Override
		public boolean isImmutable() {
			return true;
		}
	};
	public static final NbtNull INSTANCE = new NbtNull();

	private NbtNull() {
	}

	@Override
	public void write(DataOutput output) throws IOException {
	}

	@Override
	public byte getType() {
		return NbtElement.NULL_TYPE;
	}

	@Override
	public NbtType<NbtNull> getNbtType() {
		return TYPE;
	}

	@Override
	public String toString() {
		return this.asString();
	}

	public NbtNull copy() {
		return this;
	}

	@Override
	public void accept(NbtElementVisitor visitor) {
		visitor.visitNull(this);
	}

	@Override
	public NbtScanner.Result doAccept(NbtScanner visitor) {
		return visitor.visitNull();
	}
}
