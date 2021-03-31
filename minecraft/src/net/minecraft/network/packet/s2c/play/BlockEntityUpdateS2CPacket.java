package net.minecraft.network.packet.s2c.play;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.math.BlockPos;

public class BlockEntityUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	public static final int MOB_SPAWNER = 1;
	public static final int COMMAND_BLOCK = 2;
	public static final int BEACON = 3;
	public static final int SKULL = 4;
	public static final int CONDUIT = 5;
	public static final int BANNER = 6;
	public static final int STRUCTURE = 7;
	public static final int END_GATEWAY = 8;
	public static final int SIGN = 9;
	public static final int BED = 11;
	public static final int JIGSAW = 12;
	public static final int CAMPFIRE = 13;
	public static final int BEEHIVE = 14;
	private final BlockPos pos;
	private final int blockEntityType;
	private final NbtCompound nbt;

	public BlockEntityUpdateS2CPacket(BlockPos pos, int blockEntityType, NbtCompound nbt) {
		this.pos = pos;
		this.blockEntityType = blockEntityType;
		this.nbt = nbt;
	}

	public BlockEntityUpdateS2CPacket(PacketByteBuf buf) {
		this.pos = buf.readBlockPos();
		this.blockEntityType = buf.readUnsignedByte();
		this.nbt = buf.readNbt();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeBlockPos(this.pos);
		buf.writeByte((byte)this.blockEntityType);
		buf.writeNbt(this.nbt);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onBlockEntityUpdate(this);
	}

	public BlockPos getPos() {
		return this.pos;
	}

	public int getBlockEntityType() {
		return this.blockEntityType;
	}

	public NbtCompound getNbt() {
		return this.nbt;
	}
}
