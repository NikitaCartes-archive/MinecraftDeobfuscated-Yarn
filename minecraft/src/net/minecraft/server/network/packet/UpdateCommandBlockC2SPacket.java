package net.minecraft.server.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

public class UpdateCommandBlockC2SPacket implements Packet<ServerPlayPacketListener> {
	private BlockPos pos;
	private String command;
	private boolean trackOutput;
	private boolean conditional;
	private boolean alwaysActive;
	private CommandBlockBlockEntity.Type type;

	public UpdateCommandBlockC2SPacket() {
	}

	@Environment(EnvType.CLIENT)
	public UpdateCommandBlockC2SPacket(BlockPos blockPos, String string, CommandBlockBlockEntity.Type type, boolean bl, boolean bl2, boolean bl3) {
		this.pos = blockPos;
		this.command = string;
		this.trackOutput = bl;
		this.conditional = bl2;
		this.alwaysActive = bl3;
		this.type = type;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.pos = buf.readBlockPos();
		this.command = buf.readString(32767);
		this.type = buf.readEnumConstant(CommandBlockBlockEntity.Type.class);
		int i = buf.readByte();
		this.trackOutput = (i & 1) != 0;
		this.conditional = (i & 2) != 0;
		this.alwaysActive = (i & 4) != 0;
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeBlockPos(this.pos);
		buf.writeString(this.command);
		buf.writeEnumConstant(this.type);
		int i = 0;
		if (this.trackOutput) {
			i |= 1;
		}

		if (this.conditional) {
			i |= 2;
		}

		if (this.alwaysActive) {
			i |= 4;
		}

		buf.writeByte(i);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onUpdateCommandBlock(this);
	}

	public BlockPos getBlockPos() {
		return this.pos;
	}

	public String getCommand() {
		return this.command;
	}

	public boolean shouldTrackOutput() {
		return this.trackOutput;
	}

	public boolean isConditional() {
		return this.conditional;
	}

	public boolean isAlwaysActive() {
		return this.alwaysActive;
	}

	public CommandBlockBlockEntity.Type getType() {
		return this.type;
	}
}
