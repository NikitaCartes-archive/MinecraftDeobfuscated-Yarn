package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public class class_2695 implements Packet<ClientPlayPacketListener> {
	private int field_12333;
	private Identifier field_12332;

	public class_2695() {
	}

	public class_2695(int i, Recipe recipe) {
		this.field_12333 = i;
		this.field_12332 = recipe.getId();
	}

	@Environment(EnvType.CLIENT)
	public Identifier method_11684() {
		return this.field_12332;
	}

	@Environment(EnvType.CLIENT)
	public int method_11685() {
		return this.field_12333;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.field_12333 = packetByteBuf.readByte();
		this.field_12332 = packetByteBuf.readIdentifier();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeByte(this.field_12333);
		packetByteBuf.writeIdentifier(this.field_12332);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.method_11090(this);
	}
}
