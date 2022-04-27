/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import net.minecraft.text.Text;

/**
 * Represents a subject which can receive command feedback.
 */
public interface CommandOutput {
    public static final CommandOutput DUMMY = new CommandOutput(){

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
    public void sendMessage(Text var1);

    public boolean shouldReceiveFeedback();

    public boolean shouldTrackOutput();

    public boolean shouldBroadcastConsoleToOps();

    default public boolean cannotBeSilenced() {
        return false;
    }
}

