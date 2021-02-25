package net.minecraft.network.packet.s2c.play;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.world.border.WorldBorder;

public class WorldBorderWarningTimeChangedS2CPacket implements Packet<ClientPlayPacketListener> {
	private final int warningTime;

	public WorldBorderWarningTimeChangedS2CPacket(WorldBorder worldBorder) {
		this.warningTime = worldBorder.getWarningTime();
	}

	public WorldBorderWarningTimeChangedS2CPacket(PacketByteBuf buf) {
		this.warningTime = buf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.warningTime);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onWorldBorderWarningTimeChanged(this);
	}

	@Environment(EnvType.CLIENT)
	public int getWarningTime() {
		return this.warningTime;
	}
}
