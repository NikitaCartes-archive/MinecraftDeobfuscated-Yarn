package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.block.enums.StructureMode;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.Mirror;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class class_2875 implements Packet<ServerPlayPacketListener> {
	private BlockPos field_13093;
	private StructureBlockBlockEntity.class_2634 field_13082;
	private StructureMode field_13084;
	private String field_13080;
	private BlockPos field_13091;
	private BlockPos field_13083;
	private Mirror field_13081;
	private Rotation field_13088;
	private String field_13085;
	private boolean field_13089;
	private boolean field_13087;
	private boolean field_13086;
	private float field_13090;
	private long field_13092;

	public class_2875() {
	}

	@Environment(EnvType.CLIENT)
	public class_2875(
		BlockPos blockPos,
		StructureBlockBlockEntity.class_2634 arg,
		StructureMode structureMode,
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
		this.field_13093 = blockPos;
		this.field_13082 = arg;
		this.field_13084 = structureMode;
		this.field_13080 = string;
		this.field_13091 = blockPos2;
		this.field_13083 = blockPos3;
		this.field_13081 = mirror;
		this.field_13088 = rotation;
		this.field_13085 = string2;
		this.field_13089 = bl;
		this.field_13087 = bl2;
		this.field_13086 = bl3;
		this.field_13090 = f;
		this.field_13092 = l;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.field_13093 = packetByteBuf.readBlockPos();
		this.field_13082 = packetByteBuf.readEnumConstant(StructureBlockBlockEntity.class_2634.class);
		this.field_13084 = packetByteBuf.readEnumConstant(StructureMode.class);
		this.field_13080 = packetByteBuf.readString(32767);
		this.field_13091 = new BlockPos(
			MathHelper.clamp(packetByteBuf.readByte(), -32, 32),
			MathHelper.clamp(packetByteBuf.readByte(), -32, 32),
			MathHelper.clamp(packetByteBuf.readByte(), -32, 32)
		);
		this.field_13083 = new BlockPos(
			MathHelper.clamp(packetByteBuf.readByte(), 0, 32), MathHelper.clamp(packetByteBuf.readByte(), 0, 32), MathHelper.clamp(packetByteBuf.readByte(), 0, 32)
		);
		this.field_13081 = packetByteBuf.readEnumConstant(Mirror.class);
		this.field_13088 = packetByteBuf.readEnumConstant(Rotation.class);
		this.field_13085 = packetByteBuf.readString(12);
		this.field_13090 = MathHelper.clamp(packetByteBuf.readFloat(), 0.0F, 1.0F);
		this.field_13092 = packetByteBuf.readVarLong();
		int i = packetByteBuf.readByte();
		this.field_13089 = (i & 1) != 0;
		this.field_13087 = (i & 2) != 0;
		this.field_13086 = (i & 4) != 0;
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeBlockPos(this.field_13093);
		packetByteBuf.writeEnumConstant(this.field_13082);
		packetByteBuf.writeEnumConstant(this.field_13084);
		packetByteBuf.writeString(this.field_13080);
		packetByteBuf.writeByte(this.field_13091.getX());
		packetByteBuf.writeByte(this.field_13091.getY());
		packetByteBuf.writeByte(this.field_13091.getZ());
		packetByteBuf.writeByte(this.field_13083.getX());
		packetByteBuf.writeByte(this.field_13083.getY());
		packetByteBuf.writeByte(this.field_13083.getZ());
		packetByteBuf.writeEnumConstant(this.field_13081);
		packetByteBuf.writeEnumConstant(this.field_13088);
		packetByteBuf.writeString(this.field_13085);
		packetByteBuf.writeFloat(this.field_13090);
		packetByteBuf.writeVarLong(this.field_13092);
		int i = 0;
		if (this.field_13089) {
			i |= 1;
		}

		if (this.field_13087) {
			i |= 2;
		}

		if (this.field_13086) {
			i |= 4;
		}

		packetByteBuf.writeByte(i);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.method_12051(this);
	}

	public BlockPos method_12499() {
		return this.field_13093;
	}

	public StructureBlockBlockEntity.class_2634 method_12500() {
		return this.field_13082;
	}

	public StructureMode getMode() {
		return this.field_13084;
	}

	public String getStructureName() {
		return this.field_13080;
	}

	public BlockPos getOffset() {
		return this.field_13091;
	}

	public BlockPos getSize() {
		return this.field_13083;
	}

	public Mirror getMirror() {
		return this.field_13081;
	}

	public Rotation getRotation() {
		return this.field_13088;
	}

	public String getMetadata() {
		return this.field_13085;
	}

	public boolean getIgnoreEntities() {
		return this.field_13089;
	}

	public boolean shouldShowAir() {
		return this.field_13087;
	}

	public boolean shouldShowBoundingBox() {
		return this.field_13086;
	}

	public float getIntegrity() {
		return this.field_13090;
	}

	public long getSeed() {
		return this.field_13092;
	}
}
