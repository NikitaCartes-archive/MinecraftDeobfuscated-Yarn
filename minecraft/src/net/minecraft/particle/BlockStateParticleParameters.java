package net.minecraft.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.command.arguments.BlockArgumentParser;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;

public class BlockStateParticleParameters implements ParticleParameters {
	public static final ParticleParameters.Factory<BlockStateParticleParameters> PARAMETERS_FACTORY = new ParticleParameters.Factory<BlockStateParticleParameters>() {
		public BlockStateParticleParameters method_10279(ParticleType<BlockStateParticleParameters> particleType, StringReader stringReader) throws CommandSyntaxException {
			stringReader.expect(' ');
			return new BlockStateParticleParameters(particleType, new BlockArgumentParser(stringReader, false).parse(false).getBlockState());
		}

		public BlockStateParticleParameters method_10280(ParticleType<BlockStateParticleParameters> particleType, PacketByteBuf packetByteBuf) {
			return new BlockStateParticleParameters(particleType, Block.STATE_IDS.get(packetByteBuf.readVarInt()));
		}
	};
	private final ParticleType<BlockStateParticleParameters> type;
	private final BlockState blockState;

	public BlockStateParticleParameters(ParticleType<BlockStateParticleParameters> particleType, BlockState blockState) {
		this.type = particleType;
		this.blockState = blockState;
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) {
		packetByteBuf.writeVarInt(Block.STATE_IDS.getId(this.blockState));
	}

	@Override
	public String asString() {
		return Registry.PARTICLE_TYPE.getId(this.getType()) + " " + BlockArgumentParser.stringifyBlockState(this.blockState);
	}

	@Override
	public ParticleType<BlockStateParticleParameters> getType() {
		return this.type;
	}

	@Environment(EnvType.CLIENT)
	public BlockState getBlockState() {
		return this.blockState;
	}
}
