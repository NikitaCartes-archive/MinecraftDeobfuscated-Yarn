package net.minecraft;

import java.io.IOException;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class class_2774 implements Packet<ClientPlayPacketListener> {
	private int field_12691;
	@Nullable
	private CompoundTag field_12690;

	public class_2774() {
	}

	public class_2774(int i, @Nullable CompoundTag compoundTag) {
		this.field_12691 = i;
		this.field_12690 = compoundTag;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.field_12691 = packetByteBuf.readVarInt();
		this.field_12690 = packetByteBuf.readCompoundTag();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(this.field_12691);
		packetByteBuf.writeCompoundTag(this.field_12690);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.method_11127(this);
	}

	@Environment(EnvType.CLIENT)
	public int method_11910() {
		return this.field_12691;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public CompoundTag method_11911() {
		return this.field_12690;
	}

	@Override
	public boolean isErrorFatal() {
		return true;
	}
}
