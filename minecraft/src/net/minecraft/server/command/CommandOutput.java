package net.minecraft.server.command;

import net.minecraft.text.Text;

/**
 * Represents a subject which can receive command feedback.
 */
public interface CommandOutput {
	CommandOutput DUMMY = new CommandOutput() {
		@Override
		public void sendMessage(Text message) {
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

	/**
	 * Sends a system message.
	 * 
	 * @implNote The output location depends on the implementation; players will
	 * use the in-game chat, and others will output to the log.
	 */
	void sendMessage(Text message);

	boolean shouldReceiveFeedback();

	boolean shouldTrackOutput();

	boolean shouldBroadcastConsoleToOps();

	default boolean cannotBeSilenced() {
		return false;
	}
}
