package net.minecraft.server.command;

import net.minecraft.text.TextComponent;

public interface CommandOutput {
	CommandOutput field_17395 = new CommandOutput() {
		@Override
		public void appendCommandFeedback(TextComponent textComponent) {
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

	void appendCommandFeedback(TextComponent textComponent);

	boolean sendCommandFeedback();

	boolean shouldTrackOutput();

	boolean shouldBroadcastConsoleToOps();
}
