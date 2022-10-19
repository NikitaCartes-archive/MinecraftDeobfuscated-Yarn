package net.minecraft.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.command.CommandRegistryWrapper;
import net.minecraft.command.argument.BlockArgumentParser;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.registry.Registry;

public class BlockStateParticleEffect implements ParticleEffect {
	public static final ParticleEffect.Factory<BlockStateParticleEffect> PARAMETERS_FACTORY = new ParticleEffect.Factory<BlockStateParticleEffect>() {
		public BlockStateParticleEffect read(ParticleType<BlockStateParticleEffect> particleType, StringReader stringReader) throws CommandSyntaxException {
			stringReader.expect(' ');
			return new BlockStateParticleEffect(particleType, BlockArgumentParser.block(CommandRegistryWrapper.of(Registry.BLOCK), stringReader, false).blockState());
		}

		public BlockStateParticleEffect read(ParticleType<BlockStateParticleEffect> particleType, PacketByteBuf packetByteBuf) {
			return new BlockStateParticleEffect(particleType, packetByteBuf.readRegistryValue(Block.STATE_IDS));
		}
	};
	private final ParticleType<BlockStateParticleEffect> type;
	private final BlockState blockState;

	public static Codec<BlockStateParticleEffect> createCodec(ParticleType<BlockStateParticleEffect> type) {
		return BlockState.CODEC.xmap(state -> new BlockStateParticleEffect(type, state), effect -> effect.blockState);
	}

	public BlockStateParticleEffect(ParticleType<BlockStateParticleEffect> type, BlockState blockState) {
		this.type = type;
		this.blockState = blockState;
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeRegistryValue(Block.STATE_IDS, this.blockState);
	}

	@Override
	public String asString() {
		return Registry.PARTICLE_TYPE.getId(this.getType()) + " " + BlockArgumentParser.stringifyBlockState(this.blockState);
	}

	@Override
	public ParticleType<BlockStateParticleEffect> getType() {
		return this.type;
	}

	public BlockState getBlockState() {
		return this.blockState;
	}
}
