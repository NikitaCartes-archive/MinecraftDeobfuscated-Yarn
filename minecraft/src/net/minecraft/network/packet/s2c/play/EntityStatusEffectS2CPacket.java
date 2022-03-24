package net.minecraft.network.packet.s2c.play;

import javax.annotation.Nullable;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.registry.Registry;

public class EntityStatusEffectS2CPacket implements Packet<ClientPlayPacketListener> {
	private static final int AMBIENT_MASK = 1;
	private static final int SHOW_PARTICLES_MASK = 2;
	private static final int SHOW_ICON_MASK = 4;
	private final int entityId;
	private final StatusEffect effectId;
	private final byte amplifier;
	private final int duration;
	private final byte flags;
	@Nullable
	private final StatusEffectInstance.FactorCalculationData factorCalculationData;

	public EntityStatusEffectS2CPacket(int entityId, StatusEffectInstance effect) {
		this.entityId = entityId;
		this.effectId = effect.getEffectType();
		this.amplifier = (byte)(effect.getAmplifier() & 0xFF);
		if (effect.getDuration() > 32767) {
			this.duration = 32767;
		} else {
			this.duration = effect.getDuration();
		}

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

		this.flags = b;
		this.factorCalculationData = (StatusEffectInstance.FactorCalculationData)effect.getFactorCalculationData().orElse(null);
	}

	public EntityStatusEffectS2CPacket(PacketByteBuf buf) {
		this.entityId = buf.readVarInt();
		this.effectId = buf.readRegistryValue(Registry.STATUS_EFFECT);
		this.amplifier = buf.readByte();
		this.duration = buf.readVarInt();
		this.flags = buf.readByte();
		boolean bl = buf.readBoolean();
		if (bl) {
			this.factorCalculationData = buf.decode(StatusEffectInstance.FactorCalculationData.CODEC);
		} else {
			this.factorCalculationData = null;
		}
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.entityId);
		buf.writeRegistryValue(Registry.STATUS_EFFECT, this.effectId);
		buf.writeByte(this.amplifier);
		buf.writeVarInt(this.duration);
		buf.writeByte(this.flags);
		boolean bl = this.factorCalculationData != null;
		buf.writeBoolean(bl);
		if (bl) {
			buf.encode(StatusEffectInstance.FactorCalculationData.CODEC, this.factorCalculationData);
		}
	}

	public boolean isPermanent() {
		return this.duration == 32767;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onEntityStatusEffect(this);
	}

	public int getEntityId() {
		return this.entityId;
	}

	public StatusEffect getEffectId() {
		return this.effectId;
	}

	public byte getAmplifier() {
		return this.amplifier;
	}

	public int getDuration() {
		return this.duration;
	}

	public boolean shouldShowParticles() {
		return (this.flags & 2) == 2;
	}

	public boolean isAmbient() {
		return (this.flags & 1) == 1;
	}

	public boolean shouldShowIcon() {
		return (this.flags & 4) == 4;
	}

	@Nullable
	public StatusEffectInstance.FactorCalculationData getFactorCalculationData() {
		return this.factorCalculationData;
	}
}
