package net.minecraft.network.packet.s2c.play;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.world.border.WorldBorder;

public class WorldBorderSizeChangedS2CPacket implements Packet<ClientPlayPacketListener> {
	private final double sizeLerpTarget;

	public WorldBorderSizeChangedS2CPacket(WorldBorder worldBorder) {
		this.sizeLerpTarget = worldBorder.getSizeLerpTarget();
	}

	public WorldBorderSizeChangedS2CPacket(PacketByteBuf buf) {
		this.sizeLerpTarget = buf.readDouble();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeDouble(this.sizeLerpTarget);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onWorldBorderSizeChanged(this);
	}

	@Environment(EnvType.CLIENT)
	public double getSizeLerpTarget() {
		return this.sizeLerpTarget;
	}
}
