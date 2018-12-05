package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.text.TextComponent;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

public class class_2877 implements Packet<ServerPlayPacketListener> {
	private BlockPos field_13101;
	private String[] field_13100;

	public class_2877() {
	}

	@Environment(EnvType.CLIENT)
	public class_2877(BlockPos blockPos, TextComponent textComponent, TextComponent textComponent2, TextComponent textComponent3, TextComponent textComponent4) {
		this.field_13101 = blockPos;
		this.field_13100 = new String[]{textComponent.getString(), textComponent2.getString(), textComponent3.getString(), textComponent4.getString()};
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.field_13101 = packetByteBuf.readBlockPos();
		this.field_13100 = new String[4];

		for (int i = 0; i < 4; i++) {
			this.field_13100[i] = packetByteBuf.readString(384);
		}
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeBlockPos(this.field_13101);

		for (int i = 0; i < 4; i++) {
			packetByteBuf.writeString(this.field_13100[i]);
		}
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.method_12071(this);
	}

	public BlockPos method_12510() {
		return this.field_13101;
	}

	public String[] method_12508() {
		return this.field_13100;
	}
}
