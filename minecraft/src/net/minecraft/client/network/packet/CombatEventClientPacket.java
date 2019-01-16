package net.minecraft.client.network.packet;

import java.io.IOException;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.util.PacketByteBuf;

public class CombatEventClientPacket implements Packet<ClientPlayPacketListener> {
	public CombatEventClientPacket.Type type;
	public int entityId;
	public int attackerEntityId;
	public int timeSinceLastAttack;
	public TextComponent deathMessage;

	public CombatEventClientPacket() {
	}

	public CombatEventClientPacket(DamageTracker damageTracker, CombatEventClientPacket.Type type) {
		this(damageTracker, type, new StringTextComponent(""));
	}

	public CombatEventClientPacket(DamageTracker damageTracker, CombatEventClientPacket.Type type, TextComponent textComponent) {
		this.type = type;
		LivingEntity livingEntity = damageTracker.getBiggestAttacker();
		switch (type) {
			case END:
				this.timeSinceLastAttack = damageTracker.getTimeSinceLastAttack();
				this.attackerEntityId = livingEntity == null ? -1 : livingEntity.getEntityId();
				break;
			case DEATH:
				this.entityId = damageTracker.getEntity().getEntityId();
				this.attackerEntityId = livingEntity == null ? -1 : livingEntity.getEntityId();
				this.deathMessage = textComponent;
		}
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.type = packetByteBuf.readEnumConstant(CombatEventClientPacket.Type.class);
		if (this.type == CombatEventClientPacket.Type.END) {
			this.timeSinceLastAttack = packetByteBuf.readVarInt();
			this.attackerEntityId = packetByteBuf.readInt();
		} else if (this.type == CombatEventClientPacket.Type.DEATH) {
			this.entityId = packetByteBuf.readVarInt();
			this.attackerEntityId = packetByteBuf.readInt();
			this.deathMessage = packetByteBuf.readTextComponent();
		}
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeEnumConstant(this.type);
		if (this.type == CombatEventClientPacket.Type.END) {
			packetByteBuf.writeVarInt(this.timeSinceLastAttack);
			packetByteBuf.writeInt(this.attackerEntityId);
		} else if (this.type == CombatEventClientPacket.Type.DEATH) {
			packetByteBuf.writeVarInt(this.entityId);
			packetByteBuf.writeInt(this.attackerEntityId);
			packetByteBuf.writeTextComponent(this.deathMessage);
		}
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onCombatEvent(this);
	}

	@Override
	public boolean isErrorFatal() {
		return this.type == CombatEventClientPacket.Type.DEATH;
	}

	public static enum Type {
		BEGIN,
		END,
		DEATH;
	}
}
