package net.minecraft.network.packet.c2s.play;

import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class UpdateJigsawC2SPacket implements Packet<ServerPlayPacketListener> {
	private final BlockPos pos;
	private final Identifier name;
	private final Identifier target;
	private final Identifier pool;
	private final String finalState;
	private final JigsawBlockEntity.Joint jointType;

	public UpdateJigsawC2SPacket(BlockPos pos, Identifier name, Identifier target, Identifier pool, String finalState, JigsawBlockEntity.Joint jointType) {
		this.pos = pos;
		this.name = name;
		this.target = target;
		this.pool = pool;
		this.finalState = finalState;
		this.jointType = jointType;
	}

	public UpdateJigsawC2SPacket(PacketByteBuf buf) {
		this.pos = buf.readBlockPos();
		this.name = buf.readIdentifier();
		this.target = buf.readIdentifier();
		this.pool = buf.readIdentifier();
		this.finalState = buf.readString();
		this.jointType = (JigsawBlockEntity.Joint)JigsawBlockEntity.Joint.byName(buf.readString()).orElse(JigsawBlockEntity.Joint.ALIGNED);
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeBlockPos(this.pos);
		buf.writeIdentifier(this.name);
		buf.writeIdentifier(this.target);
		buf.writeIdentifier(this.pool);
		buf.writeString(this.finalState);
		buf.writeString(this.jointType.asString());
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onUpdateJigsaw(this);
	}

	public BlockPos getPos() {
		return this.pos;
	}

	public Identifier getName() {
		return this.name;
	}

	public Identifier getTarget() {
		return this.target;
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
