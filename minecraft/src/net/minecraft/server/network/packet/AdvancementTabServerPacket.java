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

public class AdvancementTabServerPacket implements Packet<ServerPlayPacketListener> {
	private AdvancementTabServerPacket.Action action;
	private Identifier tabToOpen;

	public AdvancementTabServerPacket() {
	}

	@Environment(EnvType.CLIENT)
	public AdvancementTabServerPacket(AdvancementTabServerPacket.Action action, @Nullable Identifier identifier) {
		this.action = action;
		this.tabToOpen = identifier;
	}

	@Environment(EnvType.CLIENT)
	public static AdvancementTabServerPacket open(SimpleAdvancement simpleAdvancement) {
		return new AdvancementTabServerPacket(AdvancementTabServerPacket.Action.field_13024, simpleAdvancement.getId());
	}

	@Environment(EnvType.CLIENT)
	public static AdvancementTabServerPacket close() {
		return new AdvancementTabServerPacket(AdvancementTabServerPacket.Action.field_13023, null);
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.action = packetByteBuf.readEnumConstant(AdvancementTabServerPacket.Action.class);
		if (this.action == AdvancementTabServerPacket.Action.field_13024) {
			this.tabToOpen = packetByteBuf.readIdentifier();
		}
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeEnumConstant(this.action);
		if (this.action == AdvancementTabServerPacket.Action.field_13024) {
			packetByteBuf.writeIdentifier(this.tabToOpen);
		}
	}

	public void method_12417(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onAdvancementTab(this);
	}

	public AdvancementTabServerPacket.Action getAction() {
		return this.action;
	}

	public Identifier getTabToOpen() {
		return this.tabToOpen;
	}

	public static enum Action {
		field_13024,
		field_13023;
	}
}
