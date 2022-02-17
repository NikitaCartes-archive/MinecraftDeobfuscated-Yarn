package net.minecraft.network.packet.s2c.play;

import javax.annotation.Nullable;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class EntityStatusEffectS2CPacket implements Packet<ClientPlayPacketListener> {
	private static final int AMBIENT_MASK = 1;
	private static final int SHOW_PARTICLES_MASK = 2;
	private static final int SHOW_ICON_MASK = 4;
	private final int entityId;
	private final byte effectId;
	private final byte amplifier;
	private final int duration;
	private final byte flags;
	@Nullable
	private final StatusEffectInstance.FactorCalculationData field_37037;

	public EntityStatusEffectS2CPacket(int entityId, StatusEffectInstance effect) {
		this.entityId = entityId;
		this.effectId = (byte)(StatusEffect.getRawId(effect.getEffectType()) & 0xFF);
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
		this.field_37037 = (StatusEffectInstance.FactorCalculationData)effect.getFactorCalculationData().orElse(null);
	}

	public EntityStatusEffectS2CPacket(PacketByteBuf buf) {
		this.entityId = buf.readVarInt();
		this.effectId = buf.readByte();
		this.amplifier = buf.readByte();
		this.duration = buf.readVarInt();
		this.flags = buf.readByte();
		boolean bl = buf.readBoolean();
		if (bl) {
			NbtCompound nbtCompound = buf.readNbt();
			if (nbtCompound == null) {
				throw new RuntimeException("Can't read factor data in update mob effect packet for [EffectId: " + this.effectId + ", EntityId:" + this.entityId + "]");
			}

			this.field_37037 = StatusEffectInstance.FactorCalculationData.fromNbt(nbtCompound);
		} else {
			this.field_37037 = null;
		}
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.entityId);
		buf.writeByte(this.effectId);
		buf.writeByte(this.amplifier);
		buf.writeVarInt(this.duration);
		buf.writeByte(this.flags);
		buf.writeBoolean(this.field_37037 != null);
		if (this.field_37037 != null) {
			NbtCompound nbtCompound = new NbtCompound();
			buf.writeNbt(this.field_37037.writeNbt(nbtCompound));
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

	public byte getEffectId() {
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
	public StatusEffectInstance.FactorCalculationData method_40997() {
		return this.field_37037;
	}
}
