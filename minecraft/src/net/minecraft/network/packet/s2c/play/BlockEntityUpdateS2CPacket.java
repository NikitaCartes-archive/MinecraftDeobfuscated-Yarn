package net.minecraft.network.packet.s2c.play;

import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class BlockEntityUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	private final BlockPos pos;
	private final BlockEntityType<?> blockEntityType;
	@Nullable
	private final NbtCompound nbt;

	public static BlockEntityUpdateS2CPacket create(BlockEntity blockEntity, Function<BlockEntity, NbtCompound> nbtGetter) {
		return new BlockEntityUpdateS2CPacket(blockEntity.getPos(), blockEntity.getType(), (NbtCompound)nbtGetter.apply(blockEntity));
	}

	public static BlockEntityUpdateS2CPacket create(BlockEntity blockEntity) {
		return create(blockEntity, BlockEntity::toInitialChunkDataNbt);
	}

	private BlockEntityUpdateS2CPacket(BlockPos pos, BlockEntityType<?> blockEntityType, NbtCompound nbt) {
		this.pos = pos;
		this.blockEntityType = blockEntityType;
		this.nbt = nbt.isEmpty() ? null : nbt;
	}

	public BlockEntityUpdateS2CPacket(PacketByteBuf buf) {
		this.pos = buf.readBlockPos();
		this.blockEntityType = Registry.BLOCK_ENTITY_TYPE.get(buf.readVarInt());
		this.nbt = buf.readNbt();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeBlockPos(this.pos);
		buf.writeVarInt(Registry.BLOCK_ENTITY_TYPE.getRawId(this.blockEntityType));
		buf.writeNbt(this.nbt);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onBlockEntityUpdate(this);
	}

	public BlockPos getPos() {
		return this.pos;
	}

	public BlockEntityType<?> getBlockEntityType() {
		return this.blockEntityType;
	}

	@Nullable
	public NbtCompound getNbt() {
		return this.nbt;
	}
}
