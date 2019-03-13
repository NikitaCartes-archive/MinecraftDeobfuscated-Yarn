package net.minecraft.server.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class UpdateJigsawC2SPacket implements Packet<ServerPlayPacketListener> {
	private BlockPos pos;
	private Identifier field_16563;
	private Identifier field_16566;
	private String finalState;

	public UpdateJigsawC2SPacket(BlockPos blockPos, Identifier identifier, Identifier identifier2, String string) {
		this.pos = blockPos;
		this.field_16563 = identifier;
		this.field_16566 = identifier2;
		this.finalState = string;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.pos = packetByteBuf.readBlockPos();
		this.field_16563 = packetByteBuf.method_10810();
		this.field_16566 = packetByteBuf.method_10810();
		this.finalState = packetByteBuf.readString(32767);
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeBlockPos(this.pos);
		packetByteBuf.method_10812(this.field_16563);
		packetByteBuf.method_10812(this.field_16566);
		packetByteBuf.writeString(this.finalState);
	}

	public void method_16392(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.method_16383(this);
	}

	public BlockPos getPos() {
		return this.pos;
	}

	public Identifier method_16394() {
		return this.field_16566;
	}

	public Identifier method_16395() {
		return this.field_16563;
	}

	public String getFinalState() {
		return this.finalState;
	}
}
