package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class class_3753 implements Packet<ServerPlayPacketListener> {
	private BlockPos field_16565;
	private Identifier field_16563;
	private Identifier field_16566;
	private String field_16564;

	public class_3753(BlockPos blockPos, Identifier identifier, Identifier identifier2, String string) {
		this.field_16565 = blockPos;
		this.field_16563 = identifier;
		this.field_16566 = identifier2;
		this.field_16564 = string;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.field_16565 = packetByteBuf.readBlockPos();
		this.field_16563 = packetByteBuf.readIdentifier();
		this.field_16566 = packetByteBuf.readIdentifier();
		this.field_16564 = packetByteBuf.readString(32767);
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeBlockPos(this.field_16565);
		packetByteBuf.writeIdentifier(this.field_16563);
		packetByteBuf.writeIdentifier(this.field_16566);
		packetByteBuf.writeString(this.field_16564);
	}

	public void method_16392(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.method_16383(this);
	}

	public BlockPos method_16396() {
		return this.field_16565;
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
