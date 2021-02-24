package net.minecraft.network.packet.c2s.play;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;

public class PlayerInputC2SPacket implements Packet<ServerPlayPacketListener> {
	private final float sideways;
	private final float forward;
	private final boolean jumping;
	private final boolean sneaking;

	@Environment(EnvType.CLIENT)
	public PlayerInputC2SPacket(float sideways, float forward, boolean jumping, boolean sneaking) {
		this.sideways = sideways;
		this.forward = forward;
		this.jumping = jumping;
		this.sneaking = sneaking;
	}

	public PlayerInputC2SPacket(PacketByteBuf packetByteBuf) {
		this.sideways = packetByteBuf.readFloat();
		this.forward = packetByteBuf.readFloat();
		byte b = packetByteBuf.readByte();
		this.jumping = (b & 1) > 0;
		this.sneaking = (b & 2) > 0;
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeFloat(this.sideways);
		buf.writeFloat(this.forward);
		byte b = 0;
		if (this.jumping) {
			b = (byte)(b | 1);
		}

		if (this.sneaking) {
			b = (byte)(b | 2);
		}

		buf.writeByte(b);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onPlayerInput(this);
	}

	public float getSideways() {
		return this.sideways;
	}

	public float getForward() {
		return this.forward;
	}

	public boolean isJumping() {
		return this.jumping;
	}

	public boolean isSneaking() {
		return this.sneaking;
	}
}
