/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.dedicated.gui;

import java.util.Vector;
import javax.swing.JList;
import net.minecraft.server.MinecraftServer;

public class PlayerListGui
extends JList<String> {
    private final MinecraftServer server;
    private int tick;

    public PlayerListGui(MinecraftServer minecraftServer) {
        this.server = minecraftServer;
        minecraftServer.addServerGuiTickable(this::tick);
    }

    public void tick() {
        if (this.tick++ % 20 == 0) {
            Vector<String> vector = new Vector<String>();
            for (int i = 0; i < this.server.getPlayerManager().getPlayerList().size(); ++i) {
                vector.add(this.server.getPlayerManager().getPlayerList().get(i).getGameProfile().getName());
            }
            this.setListData(vector);
        }
    }
}

