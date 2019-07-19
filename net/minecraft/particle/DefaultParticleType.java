/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;

public class DefaultParticleType
extends ParticleType<DefaultParticleType>
implements ParticleEffect {
    private static final ParticleEffect.Factory<DefaultParticleType> PARAMETER_FACTORY = new ParticleEffect.Factory<DefaultParticleType>(){

        @Override
        public DefaultParticleType read(ParticleType<DefaultParticleType> particleType, StringReader stringReader) throws CommandSyntaxException {
            return (DefaultParticleType)particleType;
        }

        @Override
        public DefaultParticleType read(ParticleType<DefaultParticleType> particleType, PacketByteBuf packetByteBuf) {
            return (DefaultParticleType)particleType;
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

    protected DefaultParticleType(boolean bl) {
        super(bl, PARAMETER_FACTORY);
    }

    public ParticleType<DefaultParticleType> getType() {
        return this;
    }

    @Override
    public void write(PacketByteBuf packetByteBuf) {
    }

    @Override
    public String asString() {
        return Registry.PARTICLE_TYPE.getId(this).toString();
    }
}

