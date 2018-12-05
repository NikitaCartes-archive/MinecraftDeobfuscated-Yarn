package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

public class class_2795 implements Packet<ServerPlayPacketListener> {
	private int field_12762;
	private BlockPos field_12763;

	public class_2795() {
	}

	@Environment(EnvType.CLIENT)
	public class_2795(int i, BlockPos blockPos) {
		this.field_12762 = i;
		this.field_12763 = blockPos;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.field_12762 = packetByteBuf.readVarInt();
		this.field_12763 = packetByteBuf.readBlockPos();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(this.field_12762);
		packetByteBuf.writeBlockPos(this.field_12763);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.method_12072(this);
	}

	public int method_12096() {
		return this.field_12762;
	}

	public BlockPos method_12094() {
		return this.field_12763;
	}
}
