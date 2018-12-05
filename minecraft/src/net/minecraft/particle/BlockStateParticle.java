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

public class BlockStateParticle implements Particle {
	public static final Particle.class_2395<BlockStateParticle> field_11181 = new Particle.class_2395<BlockStateParticle>() {
		public BlockStateParticle method_10279(ParticleType<BlockStateParticle> particleType, StringReader stringReader) throws CommandSyntaxException {
			stringReader.expect(' ');
			return new BlockStateParticle(particleType, new class_2259(stringReader, false).method_9678(false).method_9669());
		}

		public BlockStateParticle method_10280(ParticleType<BlockStateParticle> particleType, PacketByteBuf packetByteBuf) {
			return new BlockStateParticle(particleType, Block.STATE_IDS.getInt(packetByteBuf.readVarInt()));
		}
	};
	private final ParticleType<BlockStateParticle> field_11183;
	private final BlockState field_11182;

	public BlockStateParticle(ParticleType<BlockStateParticle> particleType, BlockState blockState) {
		this.field_11183 = particleType;
		this.field_11182 = blockState;
	}

	@Override
	public void method_10294(PacketByteBuf packetByteBuf) {
		packetByteBuf.writeVarInt(Block.STATE_IDS.getId(this.field_11182));
	}

	@Override
	public String asString() {
		return Registry.PARTICLE_TYPE.getId(this.getParticleType()) + " " + class_2259.method_9685(this.field_11182);
	}

	@Override
	public ParticleType<BlockStateParticle> getParticleType() {
		return this.field_11183;
	}

	@Environment(EnvType.CLIENT)
	public BlockState method_10278() {
		return this.field_11182;
	}
}
