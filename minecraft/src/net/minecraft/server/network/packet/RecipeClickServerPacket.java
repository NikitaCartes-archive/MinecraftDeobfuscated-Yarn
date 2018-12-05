package net.minecraft.server.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public class RecipeClickServerPacket implements Packet<ServerPlayPacketListener> {
	private int syncId;
	private Identifier recipe;
	private boolean field_12932;

	public RecipeClickServerPacket() {
	}

	@Environment(EnvType.CLIENT)
	public RecipeClickServerPacket(int i, Recipe recipe, boolean bl) {
		this.syncId = i;
		this.recipe = recipe.getId();
		this.field_12932 = bl;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.syncId = packetByteBuf.readByte();
		this.recipe = packetByteBuf.readIdentifier();
		this.field_12932 = packetByteBuf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeByte(this.syncId);
		packetByteBuf.writeIdentifier(this.recipe);
		packetByteBuf.writeBoolean(this.field_12932);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.method_12061(this);
	}

	public int getSyncId() {
		return this.syncId;
	}

	public Identifier getRecipe() {
		return this.recipe;
	}

	public boolean method_12319() {
		return this.field_12932;
	}
}
