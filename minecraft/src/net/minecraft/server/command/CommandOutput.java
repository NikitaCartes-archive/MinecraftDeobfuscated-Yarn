package net.minecraft.server.command;

import net.minecraft.network.chat.Component;

public interface CommandOutput {
	CommandOutput DUMMY = new CommandOutput() {
		@Override
		public void sendMessage(Component component) {
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

	void sendMessage(Component component);

	boolean sendCommandFeedback();

	boolean shouldTrackOutput();

	boolean shouldBroadcastConsoleToOps();
}
