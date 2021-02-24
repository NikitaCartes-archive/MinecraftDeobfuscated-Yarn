package net.minecraft.network.packet.c2s.play;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;

public class UpdateDifficultyLockC2SPacket implements Packet<ServerPlayPacketListener> {
	private final boolean difficultyLocked;

	@Environment(EnvType.CLIENT)
	public UpdateDifficultyLockC2SPacket(boolean difficultyLocked) {
		this.difficultyLocked = difficultyLocked;
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onUpdateDifficultyLock(this);
	}

	public UpdateDifficultyLockC2SPacket(PacketByteBuf packetByteBuf) {
		this.difficultyLocked = packetByteBuf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeBoolean(this.difficultyLocked);
	}

	public boolean isDifficultyLocked() {
		return this.difficultyLocked;
	}
}
