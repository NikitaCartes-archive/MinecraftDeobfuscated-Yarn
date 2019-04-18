package net.minecraft.server.command;

import net.minecraft.text.TextComponent;

public interface CommandOutput {
	CommandOutput DUMMY = new CommandOutput() {
		@Override
		public void sendMessage(TextComponent textComponent) {
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

	void sendMessage(TextComponent textComponent);

	boolean sendCommandFeedback();

	boolean shouldTrackOutput();

	boolean shouldBroadcastConsoleToOps();
}
