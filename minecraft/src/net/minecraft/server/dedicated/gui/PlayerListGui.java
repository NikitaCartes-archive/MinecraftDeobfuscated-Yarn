package net.minecraft.server.dedicated.gui;

import java.util.Vector;
import javax.swing.JList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Tickable;

public class PlayerListGui extends JList<String> implements Tickable {
	private final MinecraftServer server;
	private int tick;

	public PlayerListGui(MinecraftServer minecraftServer) {
		this.server = minecraftServer;
		minecraftServer.registerTickable(this);
	}

	@Override
	public void tick() {
		if (this.tick++ % 20 == 0) {
			Vector<String> vector = new Vector();

			for (int i = 0; i < this.server.getPlayerManager().getPlayerList().size(); i++) {
				vector.add(((ServerPlayerEntity)this.server.getPlayerManager().getPlayerList().get(i)).getGameProfile().getName());
			}

			this.setListData(vector);
		}
	}
}
