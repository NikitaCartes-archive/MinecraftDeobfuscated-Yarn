package net.minecraft.client.network.packet;

import java.io.IOException;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.PacketByteBuf;

public class CombatEventS2CPacket implements Packet<ClientPlayPacketListener> {
	public CombatEventS2CPacket.Type type;
	public int entityId;
	public int attackerEntityId;
	public int timeSinceLastAttack;
	public Text deathMessage;

	public CombatEventS2CPacket() {
	}

	public CombatEventS2CPacket(DamageTracker damageTracker, CombatEventS2CPacket.Type type) {
		this(damageTracker, type, new LiteralText(""));
	}

	public CombatEventS2CPacket(DamageTracker damageTracker, CombatEventS2CPacket.Type type, Text text) {
		this.type = type;
		LivingEntity livingEntity = damageTracker.getBiggestAttacker();
		switch (type) {
			case field_12353:
				this.timeSinceLastAttack = damageTracker.getTimeSinceLastAttack();
				this.attackerEntityId = livingEntity == null ? -1 : livingEntity.getEntityId();
				break;
			case field_12350:
				this.entityId = damageTracker.getEntity().getEntityId();
				this.attackerEntityId = livingEntity == null ? -1 : livingEntity.getEntityId();
				this.deathMessage = text;
		}
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.type = packetByteBuf.readEnumConstant(CombatEventS2CPacket.Type.class);
		if (this.type == CombatEventS2CPacket.Type.field_12353) {
			this.timeSinceLastAttack = packetByteBuf.readVarInt();
			this.attackerEntityId = packetByteBuf.readInt();
		} else if (this.type == CombatEventS2CPacket.Type.field_12350) {
			this.entityId = packetByteBuf.readVarInt();
			this.attackerEntityId = packetByteBuf.readInt();
			this.deathMessage = packetByteBuf.readText();
		}
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeEnumConstant(this.type);
		if (this.type == CombatEventS2CPacket.Type.field_12353) {
			packetByteBuf.writeVarInt(this.timeSinceLastAttack);
			packetByteBuf.writeInt(this.attackerEntityId);
		} else if (this.type == CombatEventS2CPacket.Type.field_12350) {
			packetByteBuf.writeVarInt(this.entityId);
			packetByteBuf.writeInt(this.attackerEntityId);
			packetByteBuf.writeText(this.deathMessage);
		}
	}

	public void method_11706(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onCombatEvent(this);
	}

	@Override
	public boolean isErrorFatal() {
		return this.type == CombatEventS2CPacket.Type.field_12350;
	}

	public static enum Type {
		field_12352,
		field_12353,
		field_12350;
	}
}
