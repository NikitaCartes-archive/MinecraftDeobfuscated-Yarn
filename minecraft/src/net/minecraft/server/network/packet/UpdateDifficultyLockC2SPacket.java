package net.minecraft.server.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class UpdateDifficultyLockC2SPacket implements Packet<ServerPlayPacketListener> {
	private boolean difficultyLocked;

	public UpdateDifficultyLockC2SPacket() {
	}

	@Environment(EnvType.CLIENT)
	public UpdateDifficultyLockC2SPacket(boolean bl) {
		this.difficultyLocked = bl;
	}

	public void method_19484(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onUpdateDifficultyLock(this);
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.difficultyLocked = buf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeBoolean(this.difficultyLocked);
	}

	public boolean isDifficultyLocked() {
		return this.difficultyLocked;
	}
}
