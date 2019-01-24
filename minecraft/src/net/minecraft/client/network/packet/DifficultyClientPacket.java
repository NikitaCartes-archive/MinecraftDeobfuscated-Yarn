package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.world.Difficulty;

public class DifficultyClientPacket implements Packet<ClientPlayPacketListener> {
	private Difficulty difficulty;
	private boolean field_12091;

	public DifficultyClientPacket() {
	}

	public DifficultyClientPacket(Difficulty difficulty, boolean bl) {
		this.difficulty = difficulty;
		this.field_12091 = bl;
	}

	public void method_11341(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onDifficulty(this);
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.difficulty = Difficulty.byId(packetByteBuf.readUnsignedByte());
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeByte(this.difficulty.getId());
	}

	@Environment(EnvType.CLIENT)
	public boolean method_11340() {
		return this.field_12091;
	}

	@Environment(EnvType.CLIENT)
	public Difficulty getDifficulty() {
		return this.difficulty;
	}
}
