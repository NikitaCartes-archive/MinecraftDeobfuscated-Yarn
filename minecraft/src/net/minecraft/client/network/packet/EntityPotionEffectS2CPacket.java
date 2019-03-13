package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class EntityPotionEffectS2CPacket implements Packet<ClientPlayPacketListener> {
	private int field_12727;
	private byte field_12725;
	private byte field_12729;
	private int field_12726;
	private byte field_12728;

	public EntityPotionEffectS2CPacket() {
	}

	public EntityPotionEffectS2CPacket(int i, StatusEffectInstance statusEffectInstance) {
		this.field_12727 = i;
		this.field_12725 = (byte)(StatusEffect.getRawId(statusEffectInstance.getEffectType()) & 0xFF);
		this.field_12729 = (byte)(statusEffectInstance.getAmplifier() & 0xFF);
		if (statusEffectInstance.getDuration() > 32767) {
			this.field_12726 = 32767;
		} else {
			this.field_12726 = statusEffectInstance.getDuration();
		}

		this.field_12728 = 0;
		if (statusEffectInstance.isAmbient()) {
			this.field_12728 = (byte)(this.field_12728 | 1);
		}

		if (statusEffectInstance.shouldShowParticles()) {
			this.field_12728 = (byte)(this.field_12728 | 2);
		}

		if (statusEffectInstance.shouldShowIcon()) {
			this.field_12728 = (byte)(this.field_12728 | 4);
		}
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.field_12727 = packetByteBuf.readVarInt();
		this.field_12725 = packetByteBuf.readByte();
		this.field_12729 = packetByteBuf.readByte();
		this.field_12726 = packetByteBuf.readVarInt();
		this.field_12728 = packetByteBuf.readByte();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(this.field_12727);
		packetByteBuf.writeByte(this.field_12725);
		packetByteBuf.writeByte(this.field_12729);
		packetByteBuf.writeVarInt(this.field_12726);
		packetByteBuf.writeByte(this.field_12728);
	}

	@Environment(EnvType.CLIENT)
	public boolean method_11947() {
		return this.field_12726 == 32767;
	}

	public void method_11948(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.method_11084(this);
	}

	@Environment(EnvType.CLIENT)
	public int method_11943() {
		return this.field_12727;
	}

	@Environment(EnvType.CLIENT)
	public byte method_11946() {
		return this.field_12725;
	}

	@Environment(EnvType.CLIENT)
	public byte method_11945() {
		return this.field_12729;
	}

	@Environment(EnvType.CLIENT)
	public int method_11944() {
		return this.field_12726;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_11949() {
		return (this.field_12728 & 2) == 2;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_11950() {
		return (this.field_12728 & 1) == 1;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_11942() {
		return (this.field_12728 & 4) == 4;
	}
}
