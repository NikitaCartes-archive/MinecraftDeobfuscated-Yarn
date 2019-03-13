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
	private int field_18801;
	private int field_18802;
	private boolean field_18803;

	public SetVillagerRecipesPacket() {
	}

	public SetVillagerRecipesPacket(int i, TraderRecipeList traderRecipeList, int j, int k, boolean bl) {
		this.syncId = i;
		this.recipes = traderRecipeList;
		this.field_18801 = j;
		this.field_18802 = k;
		this.field_18803 = bl;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.syncId = packetByteBuf.readVarInt();
		this.recipes = TraderRecipeList.method_8265(packetByteBuf);
		this.field_18801 = packetByteBuf.readVarInt();
		this.field_18802 = packetByteBuf.readVarInt();
		this.field_18803 = packetByteBuf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(this.syncId);
		this.recipes.method_8270(packetByteBuf);
		packetByteBuf.writeVarInt(this.field_18801);
		packetByteBuf.writeVarInt(this.field_18802);
		packetByteBuf.writeBoolean(this.field_18803);
	}

	public void method_17588(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.method_17586(this);
	}

	@Environment(EnvType.CLIENT)
	public int getSyncId() {
		return this.syncId;
	}

	@Environment(EnvType.CLIENT)
	public TraderRecipeList getRecipes() {
		return this.recipes;
	}

	@Environment(EnvType.CLIENT)
	public int method_19458() {
		return this.field_18801;
	}

	@Environment(EnvType.CLIENT)
	public int method_19459() {
		return this.field_18802;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_19460() {
		return this.field_18803;
	}
}
