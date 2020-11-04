package net.minecraft.server.command;

import java.util.UUID;
import net.minecraft.text.Text;

/**
 * Represents a subject which can receive command feedback.
 */
public interface CommandOutput {
	CommandOutput DUMMY = new CommandOutput() {
		@Override
		public void sendSystemMessage(Text message, UUID senderUuid) {
		}

		@Override
		public boolean shouldReceiveFeedback() {
			return false;
		}

		@Override
		public boolean shouldTrackOutput() {
			return false;
		}

		@Override
		public boolean shouldBroadcastConsoleToOps() {
			return false;
		}
	};

	void sendSystemMessage(Text message, UUID senderUuid);

	boolean shouldReceiveFeedback();

	boolean shouldTrackOutput();

	boolean shouldBroadcastConsoleToOps();
}
