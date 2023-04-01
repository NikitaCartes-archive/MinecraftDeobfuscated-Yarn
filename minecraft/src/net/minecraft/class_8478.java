package net.minecraft;

import java.util.Map;
import java.util.UUID;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;

public record class_8478(boolean clear, Map<UUID, class_8367> votes, Map<class_8373, class_8375> voters) implements Packet<ClientPlayPacketListener> {
	public class_8478(PacketByteBuf packetByteBuf) {
		this(
			packetByteBuf.readBoolean(),
			packetByteBuf.readMap(PacketByteBuf::readUuid, packetByteBufx -> packetByteBufx.decode(NbtOps.INSTANCE, class_8367.field_43979)),
			packetByteBuf.readMap(class_8373.field_43988, class_8375.field_43993)
		);
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeBoolean(this.clear);
		buf.writeMap(this.votes, PacketByteBuf::writeUuid, (packetByteBuf, arg) -> packetByteBuf.encode(NbtOps.INSTANCE, class_8367.field_43979, arg));
		buf.writeMap(this.voters, class_8373.field_43989, class_8375.field_43994);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.method_51009(this);
	}
}
