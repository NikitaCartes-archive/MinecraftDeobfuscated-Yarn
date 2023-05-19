package net.minecraft.network.packet.s2c.play;

import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;

public class EndCombatS2CPacket implements Packet<ClientPlayPacketListener> {
	private final int timeSinceLastAttack;

	public EndCombatS2CPacket(DamageTracker damageTracker) {
		this(damageTracker.getTimeSinceLastAttack());
	}

	public EndCombatS2CPacket(int timeSinceLastAttack) {
		this.timeSinceLastAttack = timeSinceLastAttack;
	}

	public EndCombatS2CPacket(PacketByteBuf buf) {
		this.timeSinceLastAttack = buf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.timeSinceLastAttack);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onEndCombat(this);
	}
}
