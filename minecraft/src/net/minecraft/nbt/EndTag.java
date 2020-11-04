package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.class_5627;

public class EndTag implements Tag {
	public static final TagReader<EndTag> READER = new TagReader<EndTag>() {
		public EndTag read(DataInput dataInput, int i, PositionTracker positionTracker) {
			positionTracker.add(64L);
			return EndTag.INSTANCE;
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
	public static final EndTag INSTANCE = new EndTag();

	private EndTag() {
	}

	@Override
	public void write(DataOutput output) throws IOException {
	}

	@Override
	public byte getType() {
		return 0;
	}

	@Override
	public TagReader<EndTag> getReader() {
		return READER;
	}

	@Override
	public String toString() {
		return this.asString();
	}

	public EndTag copy() {
		return this;
	}

	@Override
	public void method_32289(class_5627 arg) {
		arg.method_32294(this);
	}
}
