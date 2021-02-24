package net.minecraft.network.packet.c2s.play;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class UpdateJigsawC2SPacket implements Packet<ServerPlayPacketListener> {
	private final BlockPos pos;
	private final Identifier attachmentType;
	private final Identifier targetPool;
	private final Identifier pool;
	private final String finalState;
	private final JigsawBlockEntity.Joint jointType;

	@Environment(EnvType.CLIENT)
	public UpdateJigsawC2SPacket(
		BlockPos pos, Identifier attachmentType, Identifier targetPool, Identifier pool, String finalState, JigsawBlockEntity.Joint jointType
	) {
		this.pos = pos;
		this.attachmentType = attachmentType;
		this.targetPool = targetPool;
		this.pool = pool;
		this.finalState = finalState;
		this.jointType = jointType;
	}

	public UpdateJigsawC2SPacket(PacketByteBuf packetByteBuf) {
		this.pos = packetByteBuf.readBlockPos();
		this.attachmentType = packetByteBuf.readIdentifier();
		this.targetPool = packetByteBuf.readIdentifier();
		this.pool = packetByteBuf.readIdentifier();
		this.finalState = packetByteBuf.readString();
		this.jointType = (JigsawBlockEntity.Joint)JigsawBlockEntity.Joint.byName(packetByteBuf.readString()).orElse(JigsawBlockEntity.Joint.ALIGNED);
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeBlockPos(this.pos);
		buf.writeIdentifier(this.attachmentType);
		buf.writeIdentifier(this.targetPool);
		buf.writeIdentifier(this.pool);
		buf.writeString(this.finalState);
		buf.writeString(this.jointType.asString());
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onJigsawUpdate(this);
	}

	public BlockPos getPos() {
		return this.pos;
	}

	public Identifier getAttachmentType() {
		return this.attachmentType;
	}

	public Identifier getTargetPool() {
		return this.targetPool;
	}

	public Identifier getPool() {
		return this.pool;
	}

	public String getFinalState() {
		return this.finalState;
	}

	public JigsawBlockEntity.Joint getJointType() {
		return this.jointType;
	}
}
