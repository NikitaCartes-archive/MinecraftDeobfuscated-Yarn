package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class UpdateJigsawC2SPacket implements Packet<ServerPlayPacketListener> {
	private BlockPos pos;
	private Identifier attachmentType;
	private Identifier targetPool;
	private Identifier pool;
	private String finalState;
	private JigsawBlockEntity.Joint jointType;

	public UpdateJigsawC2SPacket() {
	}

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

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.pos = buf.readBlockPos();
		this.attachmentType = buf.readIdentifier();
		this.targetPool = buf.readIdentifier();
		this.pool = buf.readIdentifier();
		this.finalState = buf.readString(32767);
		this.jointType = (JigsawBlockEntity.Joint)JigsawBlockEntity.Joint.byName(buf.readString(32767)).orElse(JigsawBlockEntity.Joint.field_23330);
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeBlockPos(this.pos);
		buf.writeIdentifier(this.attachmentType);
		buf.writeIdentifier(this.targetPool);
		buf.writeIdentifier(this.pool);
		buf.writeString(this.finalState);
		buf.writeString(this.jointType.asString());
	}

	public void method_16392(ServerPlayPacketListener serverPlayPacketListener) {
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
