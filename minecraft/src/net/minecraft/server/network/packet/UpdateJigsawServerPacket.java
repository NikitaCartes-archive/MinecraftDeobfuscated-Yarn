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
public class UpdateJigsawServerPacket implements Packet<ServerPlayPacketListener> {
	private BlockPos pos;
	private Identifier field_16563;
	private Identifier field_16566;
	private String field_16564;

	public UpdateJigsawServerPacket(BlockPos blockPos, Identifier identifier, Identifier identifier2, String string) {
		this.pos = blockPos;
		this.field_16563 = identifier;
		this.field_16566 = identifier2;
		this.field_16564 = string;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.pos = packetByteBuf.readBlockPos();
		this.field_16563 = packetByteBuf.readIdentifier();
		this.field_16566 = packetByteBuf.readIdentifier();
		this.field_16564 = packetByteBuf.readString(32767);
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeBlockPos(this.pos);
		packetByteBuf.writeIdentifier(this.field_16563);
		packetByteBuf.writeIdentifier(this.field_16566);
		packetByteBuf.writeString(this.field_16564);
	}

	public void method_16392(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onJigsawUpdate(this);
	}

	public BlockPos method_16396() {
		return this.pos;
	}

	public Identifier method_16394() {
		return this.field_16566;
	}

	public Identifier method_16395() {
		return this.field_16563;
	}

	public String method_16393() {
		return this.field_16564;
	}
}
