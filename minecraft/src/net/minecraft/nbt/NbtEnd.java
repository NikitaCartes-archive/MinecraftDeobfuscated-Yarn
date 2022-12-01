package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.nbt.scanner.NbtScanner;
import net.minecraft.nbt.visitor.NbtElementVisitor;

/**
 * Represents the NBT end value.
 * Defines the end of an {@link NbtCompound} object during serialization,
 * and is the type of an empty {@link NbtList}.
 */
public class NbtEnd implements NbtElement {
	private static final int SIZE = 8;
	public static final NbtType<NbtEnd> TYPE = new NbtType<NbtEnd>() {
		public NbtEnd read(DataInput dataInput, int i, NbtTagSizeTracker nbtTagSizeTracker) {
			nbtTagSizeTracker.add(8L);
			return NbtEnd.INSTANCE;
		}

		@Override
		public NbtScanner.Result doAccept(DataInput input, NbtScanner visitor) {
			return visitor.visitEnd();
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
	/**
	 * A dummy instance of the NBT end. It will never appear nested in any parsed NBT
	 * structure and should never be used as NBT compound values or list elements.
	 */
	public static final NbtEnd INSTANCE = new NbtEnd();

	private NbtEnd() {
	}

	@Override
	public void write(DataOutput output) throws IOException {
	}

	@Override
	public int getSizeInBytes() {
		return 8;
	}

	@Override
	public byte getType() {
		return NbtElement.END_TYPE;
	}

	@Override
	public NbtType<NbtEnd> getNbtType() {
		return TYPE;
	}

	@Override
	public String toString() {
		return this.asString();
	}

	public NbtEnd copy() {
		return this;
	}

	@Override
	public void accept(NbtElementVisitor visitor) {
		visitor.visitEnd(this);
	}

	@Override
	public NbtScanner.Result doAccept(NbtScanner visitor) {
		return visitor.visitEnd();
	}
}
