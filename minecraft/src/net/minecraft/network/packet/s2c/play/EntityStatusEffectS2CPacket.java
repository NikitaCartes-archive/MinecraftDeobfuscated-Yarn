package net.minecraft.network.packet.s2c.play;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;

public class EntityStatusEffectS2CPacket implements Packet<ClientPlayPacketListener> {
	private static final int AMBIENT_MASK = 1;
	private static final int SHOW_PARTICLES_MASK = 2;
	private static final int SHOW_ICON_MASK = 4;
	private static final int field_47706 = 8;
	private final int entityId;
	private final RegistryEntry<StatusEffect> effectId;
	private final byte amplifier;
	private final int duration;
	private final byte flags;

	public EntityStatusEffectS2CPacket(int entityId, StatusEffectInstance effect, boolean bl) {
		this.entityId = entityId;
		this.effectId = effect.getEffectType();
		this.amplifier = (byte)(effect.getAmplifier() & 0xFF);
		this.duration = effect.getDuration();
		byte b = 0;
		if (effect.isAmbient()) {
			b = (byte)(b | 1);
		}

		if (effect.shouldShowParticles()) {
			b = (byte)(b | 2);
		}

		if (effect.shouldShowIcon()) {
			b = (byte)(b | 4);
		}

		if (bl) {
			b = (byte)(b | 8);
		}

		this.flags = b;
	}

	public EntityStatusEffectS2CPacket(PacketByteBuf buf) {
		this.entityId = buf.readVarInt();
		this.effectId = buf.readRegistryValue(Registries.STATUS_EFFECT.getIndexedEntries());
		this.amplifier = buf.readByte();
		this.duration = buf.readVarInt();
		this.flags = buf.readByte();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.entityId);
		buf.writeRegistryValue(Registries.STATUS_EFFECT.getIndexedEntries(), this.effectId);
		buf.writeByte(this.amplifier);
		buf.writeVarInt(this.duration);
		buf.writeByte(this.flags);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onEntityStatusEffect(this);
	}

	public int getEntityId() {
		return this.entityId;
	}

	public RegistryEntry<StatusEffect> getEffectId() {
		return this.effectId;
	}

	public byte getAmplifier() {
		return this.amplifier;
	}

	public int getDuration() {
		return this.duration;
	}

	public boolean shouldShowParticles() {
		return (this.flags & 2) != 0;
	}

	public boolean isAmbient() {
		return (this.flags & 1) != 0;
	}

	public boolean shouldShowIcon() {
		return (this.flags & 4) != 0;
	}

	public boolean method_55629() {
		return (this.flags & 8) != 0;
	}
}
