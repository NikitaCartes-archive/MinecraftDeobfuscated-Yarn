package net.minecraft.network.packet.c2s.play;

import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.math.BlockPos;

public class UpdateCommandBlockC2SPacket implements Packet<ServerPlayPacketListener> {
	private static final int TRACK_OUTPUT_MASK = 1;
	private static final int CONDITIONAL_MASK = 2;
	private static final int ALWAYS_ACTIVE_MASK = 4;
	private final BlockPos pos;
	private final String command;
	private final boolean trackOutput;
	private final boolean conditional;
	private final boolean alwaysActive;
	private final CommandBlockBlockEntity.Type type;

	public UpdateCommandBlockC2SPacket(
		BlockPos pos, String command, CommandBlockBlockEntity.Type type, boolean trackOutput, boolean conditional, boolean alwaysActive
	) {
		this.pos = pos;
		this.command = command;
		this.trackOutput = trackOutput;
		this.conditional = conditional;
		this.alwaysActive = alwaysActive;
		this.type = type;
	}

	public UpdateCommandBlockC2SPacket(PacketByteBuf buf) {
		this.pos = buf.readBlockPos();
		this.command = buf.readString();
		this.type = buf.readEnumConstant(CommandBlockBlockEntity.Type.class);
		int i = buf.readByte();
		this.trackOutput = (i & 1) != 0;
		this.conditional = (i & 2) != 0;
		this.alwaysActive = (i & 4) != 0;
	}

	@Override
	public void write(PacketByteBuf buf) {
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
