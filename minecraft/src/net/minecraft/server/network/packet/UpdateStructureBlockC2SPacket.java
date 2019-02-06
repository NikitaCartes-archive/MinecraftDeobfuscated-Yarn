package net.minecraft.server.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.Mirror;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class UpdateStructureBlockC2SPacket implements Packet<ServerPlayPacketListener> {
	private BlockPos pos;
	private StructureBlockBlockEntity.Action action;
	private StructureBlockMode mode;
	private String structureName;
	private BlockPos offset;
	private BlockPos size;
	private Mirror mirror;
	private Rotation rotation;
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
		BlockPos blockPos,
		StructureBlockBlockEntity.Action action,
		StructureBlockMode structureBlockMode,
		String string,
		BlockPos blockPos2,
		BlockPos blockPos3,
		Mirror mirror,
		Rotation rotation,
		String string2,
		boolean bl,
		boolean bl2,
		boolean bl3,
		float f,
		long l
	) {
		this.pos = blockPos;
		this.action = action;
		this.mode = structureBlockMode;
		this.structureName = string;
		this.offset = blockPos2;
		this.size = blockPos3;
		this.mirror = mirror;
		this.rotation = rotation;
		this.metadata = string2;
		this.ignoreEntities = bl;
		this.showAir = bl2;
		this.showBoundingBox = bl3;
		this.integrity = f;
		this.seed = l;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.pos = packetByteBuf.readBlockPos();
		this.action = packetByteBuf.readEnumConstant(StructureBlockBlockEntity.Action.class);
		this.mode = packetByteBuf.readEnumConstant(StructureBlockMode.class);
		this.structureName = packetByteBuf.readString(32767);
		this.offset = new BlockPos(
			MathHelper.clamp(packetByteBuf.readByte(), -32, 32),
			MathHelper.clamp(packetByteBuf.readByte(), -32, 32),
			MathHelper.clamp(packetByteBuf.readByte(), -32, 32)
		);
		this.size = new BlockPos(
			MathHelper.clamp(packetByteBuf.readByte(), 0, 32), MathHelper.clamp(packetByteBuf.readByte(), 0, 32), MathHelper.clamp(packetByteBuf.readByte(), 0, 32)
		);
		this.mirror = packetByteBuf.readEnumConstant(Mirror.class);
		this.rotation = packetByteBuf.readEnumConstant(Rotation.class);
		this.metadata = packetByteBuf.readString(12);
		this.integrity = MathHelper.clamp(packetByteBuf.readFloat(), 0.0F, 1.0F);
		this.seed = packetByteBuf.readVarLong();
		int i = packetByteBuf.readByte();
		this.ignoreEntities = (i & 1) != 0;
		this.showAir = (i & 2) != 0;
		this.showBoundingBox = (i & 4) != 0;
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeBlockPos(this.pos);
		packetByteBuf.writeEnumConstant(this.action);
		packetByteBuf.writeEnumConstant(this.mode);
		packetByteBuf.writeString(this.structureName);
		packetByteBuf.writeByte(this.offset.getX());
		packetByteBuf.writeByte(this.offset.getY());
		packetByteBuf.writeByte(this.offset.getZ());
		packetByteBuf.writeByte(this.size.getX());
		packetByteBuf.writeByte(this.size.getY());
		packetByteBuf.writeByte(this.size.getZ());
		packetByteBuf.writeEnumConstant(this.mirror);
		packetByteBuf.writeEnumConstant(this.rotation);
		packetByteBuf.writeString(this.metadata);
		packetByteBuf.writeFloat(this.integrity);
		packetByteBuf.writeVarLong(this.seed);
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

		packetByteBuf.writeByte(i);
	}

	public void method_12495(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.method_12051(this);
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

	public Mirror getMirror() {
		return this.mirror;
	}

	public Rotation getRotation() {
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
