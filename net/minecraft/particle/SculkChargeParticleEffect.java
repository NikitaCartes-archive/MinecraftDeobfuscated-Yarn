/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Locale;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;

public record SculkChargeParticleEffect(float roll) implements ParticleEffect
{
    public static final Codec<SculkChargeParticleEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.FLOAT.fieldOf("roll")).forGetter(particleEffect -> Float.valueOf(particleEffect.roll))).apply((Applicative<SculkChargeParticleEffect, ?>)instance, SculkChargeParticleEffect::new));
    public static final ParticleEffect.Factory<SculkChargeParticleEffect> FACTORY = new ParticleEffect.Factory<SculkChargeParticleEffect>(){

        @Override
        public SculkChargeParticleEffect read(ParticleType<SculkChargeParticleEffect> particleType, StringReader stringReader) throws CommandSyntaxException {
            stringReader.expect(' ');
            float f = stringReader.readFloat();
            return new SculkChargeParticleEffect(f);
        }

        @Override
        public SculkChargeParticleEffect read(ParticleType<SculkChargeParticleEffect> particleType, PacketByteBuf packetByteBuf) {
            return new SculkChargeParticleEffect(packetByteBuf.readFloat());
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

    public ParticleType<SculkChargeParticleEffect> getType() {
        return ParticleTypes.SCULK_CHARGE;
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeFloat(this.roll);
    }

    @Override
    public String asString() {
        return String.format(Locale.ROOT, "%s %.2f", Registries.PARTICLE_TYPE.getId(this.getType()), Float.valueOf(this.roll));
    }
}

