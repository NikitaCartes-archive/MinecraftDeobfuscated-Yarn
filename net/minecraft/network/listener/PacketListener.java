/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.listener;

import net.minecraft.network.ClientConnection;
import net.minecraft.text.Text;

/**
 * A packet listener listens to packets on a {@linkplain ClientConnection
 * connection}.
 * 
 * <p>Its listener methods will be called on the netty event loop than the
 * client or server game engine threads.
 */
public interface PacketListener {
    /**
     * Called when the connection this listener listens to has disconnected.
     * Can be used to display the disconnection reason.
     * 
     * @param reason the reason of disconnection; may be a generic message
     */
    public void onDisconnected(Text var1);

    /**
     * Returns the connection this packet listener intends to listen to.
     * 
     * @apiNote The returned connection may or may not have this listener as
     * its current packet listener.
     * 
     * @see ClientConnection#getPacketListener()
     */
    public ClientConnection getConnection();
}

