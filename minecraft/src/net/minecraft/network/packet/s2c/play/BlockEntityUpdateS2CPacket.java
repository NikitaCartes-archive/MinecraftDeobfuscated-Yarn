package net.minecraft.network.packet.s2c.play;

import java.util.function.BiFunction;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.math.BlockPos;

public class BlockEntityUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<RegistryByteBuf, BlockEntityUpdateS2CPacket> CODEC = PacketCodec.tuple(
		BlockPos.PACKET_CODEC,
		BlockEntityUpdateS2CPacket::getPos,
		PacketCodecs.registryValue(RegistryKeys.BLOCK_ENTITY_TYPE),
		BlockEntityUpdateS2CPacket::getBlockEntityType,
		PacketCodecs.UNLIMITED_NBT_COMPOUND,
		BlockEntityUpdateS2CPacket::getNbt,
		BlockEntityUpdateS2CPacket::new
	);
	private final BlockPos pos;
	private final BlockEntityType<?> blockEntityType;
	private final NbtCompound nbt;

	public static BlockEntityUpdateS2CPacket create(BlockEntity blockEntity, BiFunction<BlockEntity, DynamicRegistryManager, NbtCompound> nbtGetter) {
		DynamicRegistryManager dynamicRegistryManager = blockEntity.getWorld().getRegistryManager();
		return new BlockEntityUpdateS2CPacket(blockEntity.getPos(), blockEntity.getType(), (NbtCompound)nbtGetter.apply(blockEntity, dynamicRegistryManager));
	}

	public static BlockEntityUpdateS2CPacket create(BlockEntity blockEntity) {
		return create(blockEntity, BlockEntity::toInitialChunkDataNbt);
	}

	private BlockEntityUpdateS2CPacket(BlockPos pos, BlockEntityType<?> blockEntityType, NbtCompound nbt) {
		this.pos = pos;
		this.blockEntityType = blockEntityType;
		this.nbt = nbt;
	}

	@Override
	public PacketType<BlockEntityUpdateS2CPacket> getPacketId() {
		return PlayPackets.BLOCK_ENTITY_DATA;
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

	public NbtCompound getNbt() {
		return this.nbt;
	}
}
