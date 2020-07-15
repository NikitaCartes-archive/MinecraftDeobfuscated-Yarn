/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.command.argument.BlockArgumentParser;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
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
        public /* synthetic */ ParticleEffect read(ParticleType type, PacketByteBuf buf) {
            return this.read(type, buf);
        }

        @Override
        public /* synthetic */ ParticleEffect read(ParticleType type, StringReader reader) throws CommandSyntaxException {
            return this.read(type, reader);
        }
    };
    private final ParticleType<BlockStateParticleEffect> type;
    private final BlockState blockState;

    public static Codec<BlockStateParticleEffect> method_29128(ParticleType<BlockStateParticleEffect> particleType) {
        return BlockState.CODEC.xmap(blockState -> new BlockStateParticleEffect(particleType, (BlockState)blockState), blockStateParticleEffect -> blockStateParticleEffect.blockState);
    }

    public BlockStateParticleEffect(ParticleType<BlockStateParticleEffect> type, BlockState blockState) {
        this.type = type;
        this.blockState = blockState;
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeVarInt(Block.STATE_IDS.getRawId(this.blockState));
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

