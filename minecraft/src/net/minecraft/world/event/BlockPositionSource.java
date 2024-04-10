package net.minecraft.world.event;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.Optional;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BlockPositionSource implements PositionSource {
	public static final MapCodec<BlockPositionSource> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(BlockPos.CODEC.fieldOf("pos").forGetter(blockPositionSource -> blockPositionSource.pos)).apply(instance, BlockPositionSource::new)
	);
	public static final PacketCodec<ByteBuf, BlockPositionSource> PACKET_CODEC = PacketCodec.tuple(
		BlockPos.PACKET_CODEC, source -> source.pos, BlockPositionSource::new
	);
	private final BlockPos pos;

	public BlockPositionSource(BlockPos pos) {
		this.pos = pos;
	}

	@Override
	public Optional<Vec3d> getPos(World world) {
		return Optional.of(Vec3d.ofCenter(this.pos));
	}

	@Override
	public PositionSourceType<BlockPositionSource> getType() {
		return PositionSourceType.BLOCK;
	}

	public static class Type implements PositionSourceType<BlockPositionSource> {
		@Override
		public MapCodec<BlockPositionSource> getCodec() {
			return BlockPositionSource.CODEC;
		}

		@Override
		public PacketCodec<ByteBuf, BlockPositionSource> getPacketCodec() {
			return BlockPositionSource.PACKET_CODEC;
		}
	}
}
