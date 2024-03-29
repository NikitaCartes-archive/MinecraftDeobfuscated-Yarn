package net.minecraft.world.event;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BlockPositionSource implements PositionSource {
	public static final Codec<BlockPositionSource> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(BlockPos.CODEC.fieldOf("pos").forGetter(blockPositionSource -> blockPositionSource.pos)).apply(instance, BlockPositionSource::new)
	);
	public static final PacketCodec<RegistryByteBuf, BlockPositionSource> PACKET_CODEC = PacketCodec.tuple(
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
		public Codec<BlockPositionSource> getCodec() {
			return BlockPositionSource.CODEC;
		}

		@Override
		public PacketCodec<RegistryByteBuf, BlockPositionSource> getPacketCodec() {
			return BlockPositionSource.PACKET_CODEC;
		}
	}
}
