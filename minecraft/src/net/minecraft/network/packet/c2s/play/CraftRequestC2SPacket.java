package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;

public class CraftRequestC2SPacket implements Packet<ServerPlayPacketListener> {
	private int syncId;
	private Identifier recipe;
	private boolean craftAll;

	public CraftRequestC2SPacket() {
	}

	@Environment(EnvType.CLIENT)
	public CraftRequestC2SPacket(int syncId, Recipe<?> recipe, boolean craftAll) {
		this.syncId = syncId;
		this.recipe = recipe.getId();
		this.craftAll = craftAll;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.syncId = buf.readByte();
		this.recipe = buf.readIdentifier();
		this.craftAll = buf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeByte(this.syncId);
		buf.writeIdentifier(this.recipe);
		buf.writeBoolean(this.craftAll);
	}

	public void method_12317(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onCraftRequest(this);
	}

	public int getSyncId() {
		return this.syncId;
	}

	public Identifier getRecipe() {
		return this.recipe;
	}

	public boolean shouldCraftAll() {
		return this.craftAll;
	}
}
