package net.minecraft;

import java.io.IOException;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerLoginPacketListener;
import net.minecraft.util.PacketByteBuf;

public class class_2913 implements Packet<ServerLoginPacketListener> {
	private int field_13259;
	private PacketByteBuf field_13258;

	public class_2913() {
	}

	@Environment(EnvType.CLIENT)
	public class_2913(int i, @Nullable PacketByteBuf packetByteBuf) {
		this.field_13259 = i;
		this.field_13258 = packetByteBuf;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.field_13259 = packetByteBuf.readVarInt();
		if (packetByteBuf.readBoolean()) {
			int i = packetByteBuf.readableBytes();
			if (i < 0 || i > 1048576) {
				throw new IOException("Payload may not be larger than 1048576 bytes");
			}

			this.field_13258 = new PacketByteBuf(packetByteBuf.readBytes(i));
		} else {
			this.field_13258 = null;
		}
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(this.field_13259);
		if (this.field_13258 != null) {
			packetByteBuf.writeBoolean(true);
			packetByteBuf.writeBytes(this.field_13258.copy());
		} else {
			packetByteBuf.writeBoolean(false);
		}
	}

	public void method_12645(ServerLoginPacketListener serverLoginPacketListener) {
		serverLoginPacketListener.method_12640(this);
	}
}
