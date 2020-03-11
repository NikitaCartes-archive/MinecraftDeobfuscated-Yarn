/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
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
        public /* synthetic */ ParticleEffect read(ParticleType type, PacketByteBuf buf) {
            return this.read(type, buf);
        }

        @Override
        public /* synthetic */ ParticleEffect read(ParticleType type, StringReader reader) throws CommandSyntaxException {
            return this.read(type, reader);
        }
    };

    protected DefaultParticleType(boolean bl) {
        super(bl, PARAMETER_FACTORY);
    }

    public ParticleType<DefaultParticleType> getType() {
        return this;
    }

    @Override
    public void write(PacketByteBuf buf) {
    }

    @Override
    public String asString() {
        return Registry.PARTICLE_TYPE.getId(this).toString();
    }
}

