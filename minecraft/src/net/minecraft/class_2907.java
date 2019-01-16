package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientLoginPacketListener;
import net.minecraft.util.PacketByteBuf;

public class class_2907 implements Packet<ClientLoginPacketListener> {
	private int field_13232;

	public class_2907() {
	}

	public class_2907(int i) {
		this.field_13232 = i;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.field_13232 = packetByteBuf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(this.field_13232);
	}

	public void apply(ClientLoginPacketListener clientLoginPacketListener) {
		clientLoginPacketListener.method_12585(this);
	}

	@Environment(EnvType.CLIENT)
	public int method_12634() {
		return this.field_13232;
	}
}
