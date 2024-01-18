package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public class HealthUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, HealthUpdateS2CPacket> CODEC = Packet.createCodec(HealthUpdateS2CPacket::write, HealthUpdateS2CPacket::new);
	private final float health;
	private final int food;
	private final float saturation;

	public HealthUpdateS2CPacket(float health, int food, float saturation) {
		this.health = health;
		this.food = food;
		this.saturation = saturation;
	}

	private HealthUpdateS2CPacket(PacketByteBuf buf) {
		this.health = buf.readFloat();
		this.food = buf.readVarInt();
		this.saturation = buf.readFloat();
	}

	private void write(PacketByteBuf buf) {
		buf.writeFloat(this.health);
		buf.writeVarInt(this.food);
		buf.writeFloat(this.saturation);
	}

	@Override
	public PacketType<HealthUpdateS2CPacket> getPacketId() {
		return PlayPackets.SET_HEALTH;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onHealthUpdate(this);
	}

	public float getHealth() {
		return this.health;
	}

	public int getFood() {
		return this.food;
	}

	public float getSaturation() {
		return this.saturation;
	}
}
