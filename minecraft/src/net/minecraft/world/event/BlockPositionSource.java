package net.minecraft.world.event;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BlockPositionSource implements PositionSource {
	public static final Codec<BlockPositionSource> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(BlockPos.CODEC.fieldOf("pos").forGetter(blockPositionSource -> blockPositionSource.pos)).apply(instance, BlockPositionSource::new)
	);
	final BlockPos pos;

	public BlockPositionSource(BlockPos pos) {
		this.pos = pos;
	}

	@Override
	public Optional<Vec3d> getPos(World world) {
		return Optional.of(Vec3d.ofCenter(this.pos));
	}

	@Override
	public PositionSourceType<?> getType() {
		return PositionSourceType.BLOCK;
	}

	public static class Type implements PositionSourceType<BlockPositionSource> {
		public BlockPositionSource readFromBuf(PacketByteBuf packetByteBuf) {
			return new BlockPositionSource(packetByteBuf.readBlockPos());
		}

		public void writeToBuf(PacketByteBuf packetByteBuf, BlockPositionSource blockPositionSource) {
			packetByteBuf.writeBlockPos(blockPositionSource.pos);
		}

		@Override
		public Codec<BlockPositionSource> getCodec() {
			return BlockPositionSource.CODEC;
		}
	}
}
