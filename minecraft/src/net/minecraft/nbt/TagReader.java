package net.minecraft.nbt;

import java.io.DataInput;
import java.io.IOException;

public interface TagReader<T extends Tag> {
	T read(DataInput dataInput, int i, PositionTracker positionTracker) throws IOException;

	default boolean isImmutable() {
		return false;
	}

	String getCrashReportName();

	String getCommandFeedbackName();

	static TagReader<EndTag> createInvalid(int i) {
		return new TagReader<EndTag>() {
			public EndTag method_23264(DataInput dataInput, int i, PositionTracker positionTracker) throws IOException {
				throw new IllegalArgumentException("Invalid tag id: " + i);
			}

			@Override
			public String getCrashReportName() {
				return "INVALID[" + i + "]";
			}

			@Override
			public String getCommandFeedbackName() {
				return "UNKNOWN_" + i;
			}
		};
	}
}
