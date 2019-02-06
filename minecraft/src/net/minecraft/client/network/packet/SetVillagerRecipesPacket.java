package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.village.VillagerRecipeList;

public class SetVillagerRecipesPacket implements Packet<ClientPlayPacketListener> {
	private int syncId;
	private VillagerRecipeList recipes;

	public SetVillagerRecipesPacket() {
	}

	public SetVillagerRecipesPacket(int i, VillagerRecipeList villagerRecipeList) {
		this.syncId = i;
		this.recipes = villagerRecipeList;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.syncId = packetByteBuf.readVarInt();
		this.recipes = VillagerRecipeList.fromPacket(packetByteBuf);
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
	public VillagerRecipeList getRecipes() {
		return this.recipes;
	}
}
