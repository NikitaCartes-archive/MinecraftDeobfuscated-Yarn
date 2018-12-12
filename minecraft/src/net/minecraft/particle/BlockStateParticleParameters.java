package net.minecraft.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2259;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;

public class BlockStateParticleParameters implements ParticleParameters {
	public static final ParticleParameters.Factory<BlockStateParticleParameters> field_11181 = new ParticleParameters.Factory<BlockStateParticleParameters>() {
		public BlockStateParticleParameters read(ParticleType<BlockStateParticleParameters> particleType, StringReader stringReader) throws CommandSyntaxException {
			stringReader.expect(' ');
			return new BlockStateParticleParameters(particleType, new class_2259(stringReader, false).method_9678(false).method_9669());
		}

		public BlockStateParticleParameters read(ParticleType<BlockStateParticleParameters> particleType, PacketByteBuf packetByteBuf) {
			return new BlockStateParticleParameters(particleType, Block.STATE_IDS.getInt(packetByteBuf.readVarInt()));
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
		return Registry.PARTICLE_TYPE.getId(this.getType()) + " " + class_2259.method_9685(this.blockState);
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
