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
			return new BlockStateParticleParameters(particleType, Block.field_10651.get(packetByteBuf.readVarInt()));
		}
	};
	private final ParticleType<BlockStateParticleParameters> field_11183;
	private final BlockState blockState;

	public BlockStateParticleParameters(ParticleType<BlockStateParticleParameters> particleType, BlockState blockState) {
		this.field_11183 = particleType;
		this.blockState = blockState;
	}

	@Override
	public void method_10294(PacketByteBuf packetByteBuf) {
		packetByteBuf.writeVarInt(Block.field_10651.getId(this.blockState));
	}

	@Override
	public String asString() {
		return Registry.PARTICLE_TYPE.method_10221(this.method_10295()) + " " + BlockArgumentParser.stringifyBlockState(this.blockState);
	}

	@Override
	public ParticleType<BlockStateParticleParameters> method_10295() {
		return this.field_11183;
	}

	@Environment(EnvType.CLIENT)
	public BlockState getBlockState() {
		return this.blockState;
	}
}
