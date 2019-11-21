/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.command.arguments.BlockArgumentParser;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;

public class BlockStateParticleEffect
implements ParticleEffect {
    public static final ParticleEffect.Factory<BlockStateParticleEffect> PARAMETERS_FACTORY = new ParticleEffect.Factory<BlockStateParticleEffect>(){

        @Override
        public BlockStateParticleEffect read(ParticleType<BlockStateParticleEffect> particleType, StringReader stringReader) throws CommandSyntaxException {
            stringReader.expect(' ');
            return new BlockStateParticleEffect(particleType, new BlockArgumentParser(stringReader, false).parse(false).getBlockState());
        }

        @Override
        public BlockStateParticleEffect read(ParticleType<BlockStateParticleEffect> particleType, PacketByteBuf packetByteBuf) {
            return new BlockStateParticleEffect(particleType, Block.STATE_IDS.get(packetByteBuf.readVarInt()));
        }

        @Override
        public /* synthetic */ ParticleEffect read(ParticleType particleType, PacketByteBuf packetByteBuf) {
            return this.read(particleType, packetByteBuf);
        }

        @Override
        public /* synthetic */ ParticleEffect read(ParticleType particleType, StringReader stringReader) throws CommandSyntaxException {
            return this.read(particleType, stringReader);
        }
    };
    private final ParticleType<BlockStateParticleEffect> type;
    private final BlockState blockState;

    public BlockStateParticleEffect(ParticleType<BlockStateParticleEffect> particleType, BlockState blockState) {
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

    public ParticleType<BlockStateParticleEffect> getType() {
        return this.type;
    }

    @Environment(value=EnvType.CLIENT)
    public BlockState getBlockState() {
        return this.blockState;
    }
}

