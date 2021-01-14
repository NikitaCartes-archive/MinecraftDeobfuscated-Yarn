package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class NbtQueryResponseS2CPacket implements Packet<ClientPlayPacketListener> {
	private int transactionId;
	@Nullable
	private NbtCompound nbt;

	public NbtQueryResponseS2CPacket() {
	}

	public NbtQueryResponseS2CPacket(int transactionId, @Nullable NbtCompound nbt) {
		this.transactionId = transactionId;
		this.nbt = nbt;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.transactionId = buf.readVarInt();
		this.nbt = buf.readNbt();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.transactionId);
		buf.writeNbt(this.nbt);
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
	public NbtCompound getNbt() {
		return this.nbt;
	}

	@Override
	public boolean isWritingErrorSkippable() {
		return true;
	}
}
