package net.minecraft.network.packet.s2c.play;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class TagQueryResponseS2CPacket implements Packet<ClientPlayPacketListener> {
	private final int transactionId;
	@Nullable
	private final NbtCompound tag;

	public TagQueryResponseS2CPacket(int transactionId, @Nullable NbtCompound tag) {
		this.transactionId = transactionId;
		this.tag = tag;
	}

	public TagQueryResponseS2CPacket(PacketByteBuf buf) {
		this.transactionId = buf.readVarInt();
		this.tag = buf.readCompound();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.transactionId);
		buf.writeCompound(this.tag);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onTagQuery(this);
	}

	@Environment(EnvType.CLIENT)
	public int getTransactionId() {
		return this.transactionId;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public NbtCompound getTag() {
		return this.tag;
	}

	@Override
	public boolean isWritingErrorSkippable() {
		return true;
	}
}
