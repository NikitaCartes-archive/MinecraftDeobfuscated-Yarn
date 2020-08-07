package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.Advancement;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.Identifier;

public class AdvancementTabC2SPacket implements Packet<ServerPlayPacketListener> {
	private AdvancementTabC2SPacket.Action action;
	private Identifier tabToOpen;

	public AdvancementTabC2SPacket() {
	}

	@Environment(EnvType.CLIENT)
	public AdvancementTabC2SPacket(AdvancementTabC2SPacket.Action action, @Nullable Identifier tab) {
		this.action = action;
		this.tabToOpen = tab;
	}

	@Environment(EnvType.CLIENT)
	public static AdvancementTabC2SPacket open(Advancement advancement) {
		return new AdvancementTabC2SPacket(AdvancementTabC2SPacket.Action.field_13024, advancement.getId());
	}

	@Environment(EnvType.CLIENT)
	public static AdvancementTabC2SPacket close() {
		return new AdvancementTabC2SPacket(AdvancementTabC2SPacket.Action.field_13023, null);
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.action = buf.readEnumConstant(AdvancementTabC2SPacket.Action.class);
		if (this.action == AdvancementTabC2SPacket.Action.field_13024) {
			this.tabToOpen = buf.readIdentifier();
		}
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeEnumConstant(this.action);
		if (this.action == AdvancementTabC2SPacket.Action.field_13024) {
			buf.writeIdentifier(this.tabToOpen);
		}
	}

	public void method_12417(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onAdvancementTab(this);
	}

	public AdvancementTabC2SPacket.Action getAction() {
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
