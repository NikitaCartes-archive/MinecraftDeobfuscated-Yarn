package net.minecraft.world.event;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockPositionSource implements PositionSource {
	public static final Codec<BlockPositionSource> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(BlockPos.CODEC.fieldOf("pos").xmap(Optional::of, Optional::get).forGetter(blockPositionSource -> blockPositionSource.pos))
				.apply(instance, BlockPositionSource::new)
	);
	final Optional<BlockPos> pos;

	public BlockPositionSource(BlockPos pos) {
		this(Optional.of(pos));
	}

	public BlockPositionSource(Optional<BlockPos> pos) {
		this.pos = pos;
	}

	@Override
	public Optional<BlockPos> getPos(World world) {
		return this.pos;
	}

	@Override
	public PositionSourceType<?> getType() {
		return PositionSourceType.BLOCK;
	}

	public static class Type implements PositionSourceType<BlockPositionSource> {
		public BlockPositionSource readFromBuf(PacketByteBuf packetByteBuf) {
			return new BlockPositionSource(Optional.of(packetByteBuf.readBlockPos()));
		}

		public void writeToBuf(PacketByteBuf packetByteBuf, BlockPositionSource blockPositionSource) {
			blockPositionSource.pos.ifPresent(packetByteBuf::writeBlockPos);
		}

		@Override
		public Codec<BlockPositionSource> getCodec() {
			return BlockPositionSource.CODEC;
		}
	}
}
