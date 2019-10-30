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

public class BlockStateParticleEffect implements ParticleEffect {
	public static final ParticleEffect.Factory<BlockStateParticleEffect> PARAMETERS_FACTORY = new ParticleEffect.Factory<BlockStateParticleEffect>() {
		public BlockStateParticleEffect method_10279(ParticleType<BlockStateParticleEffect> particleType, StringReader stringReader) throws CommandSyntaxException {
			stringReader.expect(' ');
			return new BlockStateParticleEffect(particleType, new BlockArgumentParser(stringReader, false).parse(false).getBlockState());
		}

		public BlockStateParticleEffect method_10280(ParticleType<BlockStateParticleEffect> particleType, PacketByteBuf packetByteBuf) {
			return new BlockStateParticleEffect(particleType, Block.STATE_IDS.get(packetByteBuf.readVarInt()));
		}
	};
	private final ParticleType<BlockStateParticleEffect> type;
	private final BlockState blockState;

	public BlockStateParticleEffect(ParticleType<BlockStateParticleEffect> type, BlockState blockState) {
		this.type = type;
		this.blockState = blockState;
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(Block.STATE_IDS.getId(this.blockState));
	}

	@Override
	public String asString() {
		return Registry.PARTICLE_TYPE.getId(this.getType()) + " " + BlockArgumentParser.stringifyBlockState(this.blockState);
	}

	@Override
	public ParticleType<BlockStateParticleEffect> getType() {
		return this.type;
	}

	@Environment(EnvType.CLIENT)
	public BlockState getBlockState() {
		return this.blockState;
	}
}
