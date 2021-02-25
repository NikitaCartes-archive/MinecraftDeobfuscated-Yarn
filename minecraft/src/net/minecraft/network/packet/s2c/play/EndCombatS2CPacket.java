package net.minecraft.network.packet.s2c.play;

import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class EndCombatS2CPacket implements Packet<ClientPlayPacketListener> {
	private final int attackerId;
	private final int timeSinceLastAttack;

	public EndCombatS2CPacket(DamageTracker damageTracker) {
		this(damageTracker.getBiggestAttackerId(), damageTracker.getTimeSinceLastAttack());
	}

	public EndCombatS2CPacket(int attackerId, int timeSinceLastAttack) {
		this.attackerId = attackerId;
		this.timeSinceLastAttack = timeSinceLastAttack;
	}

	public EndCombatS2CPacket(PacketByteBuf buf) {
		this.timeSinceLastAttack = buf.readVarInt();
		this.attackerId = buf.readInt();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.timeSinceLastAttack);
		buf.writeInt(this.attackerId);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onEndCombat(this);
	}
}
