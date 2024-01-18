package net.minecraft.network.packet.s2c.play;

import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public class EndCombatS2CPacket implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, EndCombatS2CPacket> CODEC = Packet.createCodec(EndCombatS2CPacket::write, EndCombatS2CPacket::new);
	private final int timeSinceLastAttack;

	public EndCombatS2CPacket(DamageTracker damageTracker) {
		this(damageTracker.getTimeSinceLastAttack());
	}

	public EndCombatS2CPacket(int timeSinceLastAttack) {
		this.timeSinceLastAttack = timeSinceLastAttack;
	}

	private EndCombatS2CPacket(PacketByteBuf buf) {
		this.timeSinceLastAttack = buf.readVarInt();
	}

	private void write(PacketByteBuf buf) {
		buf.writeVarInt(this.timeSinceLastAttack);
	}

	@Override
	public PacketType<EndCombatS2CPacket> getPacketId() {
		return PlayPackets.PLAYER_COMBAT_END;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onEndCombat(this);
	}
}
