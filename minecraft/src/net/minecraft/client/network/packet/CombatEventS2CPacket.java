package net.minecraft.client.network.packet;

import java.io.IOException;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.util.PacketByteBuf;

public class CombatEventS2CPacket implements Packet<ClientPlayPacketListener> {
	public CombatEventS2CPacket.Type type;
	public int entityId;
	public int attackerEntityId;
	public int timeSinceLastAttack;
	public TextComponent deathMessage;

	public CombatEventS2CPacket() {
	}

	public CombatEventS2CPacket(DamageTracker damageTracker, CombatEventS2CPacket.Type type) {
		this(damageTracker, type, new StringTextComponent(""));
	}

	public CombatEventS2CPacket(DamageTracker damageTracker, CombatEventS2CPacket.Type type, TextComponent textComponent) {
		this.type = type;
		LivingEntity livingEntity = damageTracker.method_5541();
		switch (type) {
			case END:
				this.timeSinceLastAttack = damageTracker.getTimeSinceLastAttack();
				this.attackerEntityId = livingEntity == null ? -1 : livingEntity.getEntityId();
				break;
			case DEATH:
				this.entityId = damageTracker.method_5540().getEntityId();
				this.attackerEntityId = livingEntity == null ? -1 : livingEntity.getEntityId();
				this.deathMessage = textComponent;
		}
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.type = packetByteBuf.readEnumConstant(CombatEventS2CPacket.Type.class);
		if (this.type == CombatEventS2CPacket.Type.END) {
			this.timeSinceLastAttack = packetByteBuf.readVarInt();
			this.attackerEntityId = packetByteBuf.readInt();
		} else if (this.type == CombatEventS2CPacket.Type.DEATH) {
			this.entityId = packetByteBuf.readVarInt();
			this.attackerEntityId = packetByteBuf.readInt();
			this.deathMessage = packetByteBuf.method_10808();
		}
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeEnumConstant(this.type);
		if (this.type == CombatEventS2CPacket.Type.END) {
			packetByteBuf.writeVarInt(this.timeSinceLastAttack);
			packetByteBuf.writeInt(this.attackerEntityId);
		} else if (this.type == CombatEventS2CPacket.Type.DEATH) {
			packetByteBuf.writeVarInt(this.entityId);
			packetByteBuf.writeInt(this.attackerEntityId);
			packetByteBuf.method_10805(this.deathMessage);
		}
	}

	public void method_11706(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.method_11133(this);
	}

	@Override
	public boolean isErrorFatal() {
		return this.type == CombatEventS2CPacket.Type.DEATH;
	}

	public static enum Type {
		BEGIN,
		END,
		DEATH;
	}
}
