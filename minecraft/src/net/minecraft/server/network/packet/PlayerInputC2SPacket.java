package net.minecraft.server.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class PlayerInputC2SPacket implements Packet<ServerPlayPacketListener> {
	private float sideways;
	private float forward;
	private boolean jumping;
	private boolean sneaking;

	public PlayerInputC2SPacket() {
	}

	@Environment(EnvType.CLIENT)
	public PlayerInputC2SPacket(float sideways, float forward, boolean jumping, boolean sneaking) {
		this.sideways = sideways;
		this.forward = forward;
		this.jumping = jumping;
		this.sneaking = sneaking;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.sideways = buf.readFloat();
		this.forward = buf.readFloat();
		byte b = buf.readByte();
		this.jumping = (b & 1) > 0;
		this.sneaking = (b & 2) > 0;
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
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

	public void method_12369(ServerPlayPacketListener serverPlayPacketListener) {
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
