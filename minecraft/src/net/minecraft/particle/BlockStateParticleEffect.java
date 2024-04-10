package net.minecraft.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;

public class BlockStateParticleEffect implements ParticleEffect {
	private static final Codec<BlockState> BLOCK_STATE_CODEC = Codec.withAlternative(BlockState.CODEC, Registries.BLOCK.getCodec(), Block::getDefaultState);
	private final ParticleType<BlockStateParticleEffect> type;
	private final BlockState blockState;

	public static MapCodec<BlockStateParticleEffect> createCodec(ParticleType<BlockStateParticleEffect> type) {
		return BLOCK_STATE_CODEC.<BlockStateParticleEffect>xmap(state -> new BlockStateParticleEffect(type, state), effect -> effect.blockState)
			.fieldOf("block_state");
	}

	public static PacketCodec<? super RegistryByteBuf, BlockStateParticleEffect> createPacketCodec(ParticleType<BlockStateParticleEffect> type) {
		return PacketCodecs.entryOf(Block.STATE_IDS).xmap(state -> new BlockStateParticleEffect(type, state), effect -> effect.blockState);
	}

	public BlockStateParticleEffect(ParticleType<BlockStateParticleEffect> type, BlockState blockState) {
		this.type = type;
		this.blockState = blockState;
	}

	@Override
	public ParticleType<BlockStateParticleEffect> getType() {
		return this.type;
	}

	public BlockState getBlockState() {
		return this.blockState;
	}
}
