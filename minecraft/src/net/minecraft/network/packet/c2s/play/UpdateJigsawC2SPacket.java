package net.minecraft.network.packet.c2s.play;

import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class UpdateJigsawC2SPacket implements Packet<ServerPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, UpdateJigsawC2SPacket> CODEC = Packet.createCodec(UpdateJigsawC2SPacket::write, UpdateJigsawC2SPacket::new);
	private final BlockPos pos;
	private final Identifier name;
	private final Identifier target;
	private final Identifier pool;
	private final String finalState;
	private final JigsawBlockEntity.Joint jointType;
	private final int selectionPriority;
	private final int placementPriority;

	public UpdateJigsawC2SPacket(
		BlockPos pos,
		Identifier name,
		Identifier target,
		Identifier pool,
		String finalState,
		JigsawBlockEntity.Joint jointType,
		int selectionPriority,
		int placementPriority
	) {
		this.pos = pos;
		this.name = name;
		this.target = target;
		this.pool = pool;
		this.finalState = finalState;
		this.jointType = jointType;
		this.selectionPriority = selectionPriority;
		this.placementPriority = placementPriority;
	}

	private UpdateJigsawC2SPacket(PacketByteBuf buf) {
		this.pos = buf.readBlockPos();
		this.name = buf.readIdentifier();
		this.target = buf.readIdentifier();
		this.pool = buf.readIdentifier();
		this.finalState = buf.readString();
		this.jointType = (JigsawBlockEntity.Joint)JigsawBlockEntity.Joint.byName(buf.readString()).orElse(JigsawBlockEntity.Joint.ALIGNED);
		this.selectionPriority = buf.readVarInt();
		this.placementPriority = buf.readVarInt();
	}

	private void write(PacketByteBuf buf) {
		buf.writeBlockPos(this.pos);
		buf.writeIdentifier(this.name);
		buf.writeIdentifier(this.target);
		buf.writeIdentifier(this.pool);
		buf.writeString(this.finalState);
		buf.writeString(this.jointType.asString());
		buf.writeVarInt(this.selectionPriority);
		buf.writeVarInt(this.placementPriority);
	}

	@Override
	public PacketType<UpdateJigsawC2SPacket> getPacketId() {
		return PlayPackets.SET_JIGSAW_BLOCK;
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

	public int getSelectionPriority() {
		return this.selectionPriority;
	}

	public int getPlacementPriority() {
		return this.placementPriority;
	}
}
