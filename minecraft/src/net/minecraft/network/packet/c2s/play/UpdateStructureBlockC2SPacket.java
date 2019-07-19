package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class UpdateStructureBlockC2SPacket implements Packet<ServerPlayPacketListener> {
	private BlockPos pos;
	private StructureBlockBlockEntity.Action action;
	private StructureBlockMode mode;
	private String structureName;
	private BlockPos offset;
	private BlockPos size;
	private BlockMirror mirror;
	private BlockRotation rotation;
	private String metadata;
	private boolean ignoreEntities;
	private boolean showAir;
	private boolean showBoundingBox;
	private float integrity;
	private long seed;

	public UpdateStructureBlockC2SPacket() {
	}

	@Environment(EnvType.CLIENT)
	public UpdateStructureBlockC2SPacket(
		BlockPos pos,
		StructureBlockBlockEntity.Action action,
		StructureBlockMode mode,
		String structureName,
		BlockPos offset,
		BlockPos size,
		BlockMirror mirror,
		BlockRotation rotation,
		String metadata,
		boolean ignoreEntities,
		boolean showAir,
		boolean showBoundingBox,
		float integrity,
		long seed
	) {
		this.pos = pos;
		this.action = action;
		this.mode = mode;
		this.structureName = structureName;
		this.offset = offset;
		this.size = size;
		this.mirror = mirror;
		this.rotation = rotation;
		this.metadata = metadata;
		this.ignoreEntities = ignoreEntities;
		this.showAir = showAir;
		this.showBoundingBox = showBoundingBox;
		this.integrity = integrity;
		this.seed = seed;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.pos = buf.readBlockPos();
		this.action = buf.readEnumConstant(StructureBlockBlockEntity.Action.class);
		this.mode = buf.readEnumConstant(StructureBlockMode.class);
		this.structureName = buf.readString(32767);
		this.offset = new BlockPos(MathHelper.clamp(buf.readByte(), -32, 32), MathHelper.clamp(buf.readByte(), -32, 32), MathHelper.clamp(buf.readByte(), -32, 32));
		this.size = new BlockPos(MathHelper.clamp(buf.readByte(), 0, 32), MathHelper.clamp(buf.readByte(), 0, 32), MathHelper.clamp(buf.readByte(), 0, 32));
		this.mirror = buf.readEnumConstant(BlockMirror.class);
		this.rotation = buf.readEnumConstant(BlockRotation.class);
		this.metadata = buf.readString(12);
		this.integrity = MathHelper.clamp(buf.readFloat(), 0.0F, 1.0F);
		this.seed = buf.readVarLong();
		int i = buf.readByte();
		this.ignoreEntities = (i & 1) != 0;
		this.showAir = (i & 2) != 0;
		this.showBoundingBox = (i & 4) != 0;
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeBlockPos(this.pos);
		buf.writeEnumConstant(this.action);
		buf.writeEnumConstant(this.mode);
		buf.writeString(this.structureName);
		buf.writeByte(this.offset.getX());
		buf.writeByte(this.offset.getY());
		buf.writeByte(this.offset.getZ());
		buf.writeByte(this.size.getX());
		buf.writeByte(this.size.getY());
		buf.writeByte(this.size.getZ());
		buf.writeEnumConstant(this.mirror);
		buf.writeEnumConstant(this.rotation);
		buf.writeString(this.metadata);
		buf.writeFloat(this.integrity);
		buf.writeVarLong(this.seed);
		int i = 0;
		if (this.ignoreEntities) {
			i |= 1;
		}

		if (this.showAir) {
			i |= 2;
		}

		if (this.showBoundingBox) {
			i |= 4;
		}

		buf.writeByte(i);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onStructureBlockUpdate(this);
	}

	public BlockPos getPos() {
		return this.pos;
	}

	public StructureBlockBlockEntity.Action getAction() {
		return this.action;
	}

	public StructureBlockMode getMode() {
		return this.mode;
	}

	public String getStructureName() {
		return this.structureName;
	}

	public BlockPos getOffset() {
		return this.offset;
	}

	public BlockPos getSize() {
		return this.size;
	}

	public BlockMirror getMirror() {
		return this.mirror;
	}

	public BlockRotation getRotation() {
		return this.rotation;
	}

	public String getMetadata() {
		return this.metadata;
	}

	public boolean getIgnoreEntities() {
		return this.ignoreEntities;
	}

	public boolean shouldShowAir() {
		return this.showAir;
	}

	public boolean shouldShowBoundingBox() {
		return this.showBoundingBox;
	}

	public float getIntegrity() {
		return this.integrity;
	}

	public long getSeed() {
		return this.seed;
	}
}
