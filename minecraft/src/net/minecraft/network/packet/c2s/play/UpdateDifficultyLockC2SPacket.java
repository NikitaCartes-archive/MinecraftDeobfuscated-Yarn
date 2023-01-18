package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;

public class UpdateDifficultyLockC2SPacket implements Packet<ServerPlayPacketListener> {
	private final boolean difficultyLocked;

	public UpdateDifficultyLockC2SPacket(boolean difficultyLocked) {
		this.difficultyLocked = difficultyLocked;
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onUpdateDifficultyLock(this);
	}

	public UpdateDifficultyLockC2SPacket(PacketByteBuf buf) {
		this.difficultyLocked = buf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeBoolean(this.difficultyLocked);
	}

	public boolean isDifficultyLocked() {
		return this.difficultyLocked;
	}
}
