package net.minecraft.network.packet.c2s.play;

import javax.annotation.Nullable;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketIdentifier;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.util.Identifier;

public class AdvancementTabC2SPacket implements Packet<ServerPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, AdvancementTabC2SPacket> CODEC = Packet.createCodec(
		AdvancementTabC2SPacket::write, AdvancementTabC2SPacket::new
	);
	private final AdvancementTabC2SPacket.Action action;
	@Nullable
	private final Identifier tabToOpen;

	public AdvancementTabC2SPacket(AdvancementTabC2SPacket.Action action, @Nullable Identifier tab) {
		this.action = action;
		this.tabToOpen = tab;
	}

	public static AdvancementTabC2SPacket open(AdvancementEntry advancement) {
		return new AdvancementTabC2SPacket(AdvancementTabC2SPacket.Action.OPENED_TAB, advancement.id());
	}

	public static AdvancementTabC2SPacket close() {
		return new AdvancementTabC2SPacket(AdvancementTabC2SPacket.Action.CLOSED_SCREEN, null);
	}

	private AdvancementTabC2SPacket(PacketByteBuf buf) {
		this.action = buf.readEnumConstant(AdvancementTabC2SPacket.Action.class);
		if (this.action == AdvancementTabC2SPacket.Action.OPENED_TAB) {
			this.tabToOpen = buf.readIdentifier();
		} else {
			this.tabToOpen = null;
		}
	}

	private void write(PacketByteBuf buf) {
		buf.writeEnumConstant(this.action);
		if (this.action == AdvancementTabC2SPacket.Action.OPENED_TAB) {
			buf.writeIdentifier(this.tabToOpen);
		}
	}

	@Override
	public PacketIdentifier<AdvancementTabC2SPacket> getPacketId() {
		return PlayPackets.SEEN_ADVANCEMENTS;
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onAdvancementTab(this);
	}

	public AdvancementTabC2SPacket.Action getAction() {
		return this.action;
	}

	@Nullable
	public Identifier getTabToOpen() {
		return this.tabToOpen;
	}

	public static enum Action {
		OPENED_TAB,
		CLOSED_SCREEN;
	}
}
