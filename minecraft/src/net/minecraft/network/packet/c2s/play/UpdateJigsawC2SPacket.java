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
	private Identifier field_23401;
	private String finalState;
	private JigsawBlockEntity.Joint field_23402;

	public UpdateJigsawC2SPacket() {
	}

	@Environment(EnvType.CLIENT)
	public UpdateJigsawC2SPacket(
		BlockPos pos, Identifier attachmentType, Identifier targetPool, Identifier identifier, String string, JigsawBlockEntity.Joint joint
	) {
		this.pos = pos;
		this.attachmentType = attachmentType;
		this.targetPool = targetPool;
		this.field_23401 = identifier;
		this.finalState = string;
		this.field_23402 = joint;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.pos = buf.readBlockPos();
		this.attachmentType = buf.readIdentifier();
		this.targetPool = buf.readIdentifier();
		this.field_23401 = buf.readIdentifier();
		this.finalState = buf.readString(32767);
		this.field_23402 = (JigsawBlockEntity.Joint)JigsawBlockEntity.Joint.byName(buf.readString(32767)).orElse(JigsawBlockEntity.Joint.ALIGNED);
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeBlockPos(this.pos);
		buf.writeIdentifier(this.attachmentType);
		buf.writeIdentifier(this.targetPool);
		buf.writeIdentifier(this.field_23401);
		buf.writeString(this.finalState);
		buf.writeString(this.field_23402.asString());
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

	public Identifier method_26435() {
		return this.field_23401;
	}

	public String getFinalState() {
		return this.finalState;
	}

	public JigsawBlockEntity.Joint method_26436() {
		return this.field_23402;
	}
}
