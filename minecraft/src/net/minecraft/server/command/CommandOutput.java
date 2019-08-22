package net.minecraft.server.command;

import net.minecraft.text.Text;

public interface CommandOutput {
	CommandOutput DUMMY = new CommandOutput() {
		@Override
		public void sendMessage(Text text) {
		}

		@Override
		public boolean sendCommandFeedback() {
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

	void sendMessage(Text text);

	boolean sendCommandFeedback();

	boolean shouldTrackOutput();

	boolean shouldBroadcastConsoleToOps();
}
