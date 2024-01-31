package net.minecraft.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.command.argument.BlockArgumentParser;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;

public class BlockStateParticleEffect implements ParticleEffect {
	public static final ParticleEffect.Factory<BlockStateParticleEffect> PARAMETERS_FACTORY = new ParticleEffect.Factory<BlockStateParticleEffect>() {
		public BlockStateParticleEffect read(
			ParticleType<BlockStateParticleEffect> particleType, StringReader stringReader, RegistryWrapper.WrapperLookup wrapperLookup
		) throws CommandSyntaxException {
			stringReader.expect(' ');
			return new BlockStateParticleEffect(particleType, BlockArgumentParser.block(Registries.BLOCK.getReadOnlyWrapper(), stringReader, false).blockState());
		}
	};
	private final ParticleType<BlockStateParticleEffect> type;
	private final BlockState blockState;

	public static Codec<BlockStateParticleEffect> createCodec(ParticleType<BlockStateParticleEffect> type) {
		return BlockState.CODEC.xmap(state -> new BlockStateParticleEffect(type, state), effect -> effect.blockState);
	}

	public static PacketCodec<? super RegistryByteBuf, BlockStateParticleEffect> createPacketCodec(ParticleType<BlockStateParticleEffect> type) {
		return PacketCodecs.entryOf(Block.STATE_IDS).xmap(state -> new BlockStateParticleEffect(type, state), effect -> effect.blockState);
	}

	public BlockStateParticleEffect(ParticleType<BlockStateParticleEffect> type, BlockState blockState) {
		this.type = type;
		this.blockState = blockState;
	}

	@Override
	public String asString(RegistryWrapper.WrapperLookup registryLookup) {
		return Registries.PARTICLE_TYPE.getId(this.getType()) + " " + BlockArgumentParser.stringifyBlockState(this.blockState);
	}

	@Override
	public ParticleType<BlockStateParticleEffect> getType() {
		return this.type;
	}

	public BlockState getBlockState() {
		return this.blockState;
	}
}
