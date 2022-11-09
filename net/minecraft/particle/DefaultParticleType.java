/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;

public class DefaultParticleType
extends ParticleType<DefaultParticleType>
implements ParticleEffect {
    private static final ParticleEffect.Factory<DefaultParticleType> PARAMETER_FACTORY = new ParticleEffect.Factory<DefaultParticleType>(){

        @Override
        public DefaultParticleType read(ParticleType<DefaultParticleType> particleType, StringReader stringReader) {
            return (DefaultParticleType)particleType;
        }

        @Override
        public DefaultParticleType read(ParticleType<DefaultParticleType> particleType, PacketByteBuf packetByteBuf) {
            return (DefaultParticleType)particleType;
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
    private final Codec<DefaultParticleType> codec = Codec.unit(this::getType);

    protected DefaultParticleType(boolean alwaysShow) {
        super(alwaysShow, PARAMETER_FACTORY);
    }

    public DefaultParticleType getType() {
        return this;
    }

    @Override
    public Codec<DefaultParticleType> getCodec() {
        return this.codec;
    }

    @Override
    public void write(PacketByteBuf buf) {
    }

    @Override
    public String asString() {
        return Registries.PARTICLE_TYPE.getId(this).toString();
    }

    public /* synthetic */ ParticleType getType() {
        return this.getType();
    }
}

