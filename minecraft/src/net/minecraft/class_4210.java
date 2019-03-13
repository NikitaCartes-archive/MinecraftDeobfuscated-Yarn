package net.minecraft;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.world.Difficulty;

public class class_4210 implements Packet<ServerPlayPacketListener> {
	private Difficulty field_18805;

	public class_4210() {
	}

	public class_4210(Difficulty difficulty) {
		this.field_18805 = difficulty;
	}

	public void method_19477(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.method_19475(this);
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.field_18805 = Difficulty.getDifficulty(packetByteBuf.readUnsignedByte());
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeByte(this.field_18805.getId());
	}

	public Difficulty method_19478() {
		return this.field_18805;
	}
}
