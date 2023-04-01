package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;

public class HealthUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	private final float health;
	private final int food;
	private final float saturation;
	private final int field_44467;

	public HealthUpdateS2CPacket(float health, int food, float saturation, int i) {
		this.health = health;
		this.food = food;
		this.saturation = saturation;
		this.field_44467 = i;
	}

	public HealthUpdateS2CPacket(PacketByteBuf buf) {
		this.health = buf.readFloat();
		this.food = buf.readVarInt();
		this.saturation = buf.readFloat();
		this.field_44467 = buf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeFloat(this.health);
		buf.writeVarInt(this.food);
		buf.writeFloat(this.saturation);
		buf.writeVarInt(this.field_44467);
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

	public int method_51140() {
		return this.field_44467;
	}
}
