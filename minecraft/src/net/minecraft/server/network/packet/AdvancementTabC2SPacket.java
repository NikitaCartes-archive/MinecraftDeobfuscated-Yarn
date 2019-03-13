package net.minecraft.server.network.packet;

import java.io.IOException;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.SimpleAdvancement;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public class AdvancementTabC2SPacket implements Packet<ServerPlayPacketListener> {
	private AdvancementTabC2SPacket.Action action;
	private Identifier field_13020;

	public AdvancementTabC2SPacket() {
	}

	@Environment(EnvType.CLIENT)
	public AdvancementTabC2SPacket(AdvancementTabC2SPacket.Action action, @Nullable Identifier identifier) {
		this.action = action;
		this.field_13020 = identifier;
	}

	@Environment(EnvType.CLIENT)
	public static AdvancementTabC2SPacket method_12418(SimpleAdvancement simpleAdvancement) {
		return new AdvancementTabC2SPacket(AdvancementTabC2SPacket.Action.field_13024, simpleAdvancement.method_688());
	}

	@Environment(EnvType.CLIENT)
	public static AdvancementTabC2SPacket close() {
		return new AdvancementTabC2SPacket(AdvancementTabC2SPacket.Action.field_13023, null);
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.action = packetByteBuf.readEnumConstant(AdvancementTabC2SPacket.Action.class);
		if (this.action == AdvancementTabC2SPacket.Action.field_13024) {
			this.field_13020 = packetByteBuf.method_10810();
		}
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeEnumConstant(this.action);
		if (this.action == AdvancementTabC2SPacket.Action.field_13024) {
			packetByteBuf.method_10812(this.field_13020);
		}
	}

	public void method_12417(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.method_12058(this);
	}

	public AdvancementTabC2SPacket.Action getAction() {
		return this.action;
	}

	public Identifier method_12416() {
		return this.field_13020;
	}

	public static enum Action {
		field_13024,
		field_13023;
	}
}
