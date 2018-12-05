package net.minecraft.server.command;

import net.minecraft.text.TextComponent;

public interface CommandOutput {
	void appendCommandFeedback(TextComponent textComponent);

	boolean sendCommandFeedback();

	boolean shouldTrackOutput();

	boolean shouldBroadcastConsoleToOps();
}
