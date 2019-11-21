package net.minecraft.nbt;

import java.io.DataInput;
import java.io.IOException;

public interface TagReader<T extends Tag> {
	T read(DataInput input, int depth, PositionTracker tracker) throws IOException;

	default boolean isImmutable() {
		return false;
	}

	String getCrashReportName();

	String getCommandFeedbackName();

	static TagReader<EndTag> createInvalid(int type) {
		return new TagReader<EndTag>() {
			public EndTag read(DataInput dataInput, int i, PositionTracker positionTracker) throws IOException {
				throw new IllegalArgumentException("Invalid tag id: " + type);
			}

			@Override
			public String getCrashReportName() {
				return "INVALID[" + type + "]";
			}

			@Override
			public String getCommandFeedbackName() {
				return "UNKNOWN_" + type;
			}
		};
	}
}
