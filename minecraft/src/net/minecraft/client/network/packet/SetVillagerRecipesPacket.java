package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.village.TraderRecipeList;

public class SetVillagerRecipesPacket implements Packet<ClientPlayPacketListener> {
	private int syncId;
	private TraderRecipeList recipes;

	public SetVillagerRecipesPacket() {
	}

	public SetVillagerRecipesPacket(int i, TraderRecipeList traderRecipeList) {
		this.syncId = i;
		this.recipes = traderRecipeList;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.syncId = packetByteBuf.readVarInt();
		this.recipes = TraderRecipeList.fromPacket(packetByteBuf);
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(this.syncId);
		this.recipes.toPacket(packetByteBuf);
	}

	public void method_17588(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onSetVillagerRecipes(this);
	}

	@Environment(EnvType.CLIENT)
	public int getSyncId() {
		return this.syncId;
	}

	@Environment(EnvType.CLIENT)
	public TraderRecipeList getRecipes() {
		return this.recipes;
	}
}
