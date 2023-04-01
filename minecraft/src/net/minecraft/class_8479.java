package net.minecraft;

import java.util.List;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.registry.Registries;

public record class_8479(boolean resetAll, class_8290 action, List<class_8291> rules) implements Packet<ClientPlayPacketListener> {
	public class_8479(PacketByteBuf packetByteBuf) {
		this(packetByteBuf.readBoolean(), packetByteBuf.readEnumConstant(class_8290.class), packetByteBuf.readList(class_8479::method_51139));
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeBoolean(this.resetAll);
		buf.writeEnumConstant(this.action);
		buf.writeCollection(this.rules, class_8479::method_51137);
	}

	private static class_8291 method_51139(PacketByteBuf packetByteBuf) {
		class_8289 lv = packetByteBuf.readRegistryValue(Registries.field_44443);
		return packetByteBuf.decode(NbtOps.INSTANCE, lv.method_50120());
	}

	private static void method_51137(PacketByteBuf packetByteBuf, class_8291 arg) {
		packetByteBuf.writeRegistryValue(Registries.field_44443, arg.method_50121());
		packetByteBuf.encode(NbtOps.INSTANCE, arg.method_50121().method_50120(), arg);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.method_51010(this);
	}
}
