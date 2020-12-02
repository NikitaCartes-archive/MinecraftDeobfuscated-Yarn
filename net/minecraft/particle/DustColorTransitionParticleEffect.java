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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.AbstractDustParticleEffect;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

public class DustColorTransitionParticleEffect
extends AbstractDustParticleEffect {
    public static final Vec3d SCULK_BLUE = Vec3d.unpackRgb(3790560);
    public static final DustColorTransitionParticleEffect DEFAULT = new DustColorTransitionParticleEffect(SCULK_BLUE, DustParticleEffect.RED, 1.0f);
    public static final Codec<DustColorTransitionParticleEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Vec3d.field_28243.fieldOf("fromColor")).forGetter(dustColorTransitionParticleEffect -> dustColorTransitionParticleEffect.color), ((MapCodec)Vec3d.field_28243.fieldOf("toColor")).forGetter(dustColorTransitionParticleEffect -> dustColorTransitionParticleEffect.toColor), ((MapCodec)Codec.FLOAT.fieldOf("scale")).forGetter(dustColorTransitionParticleEffect -> Float.valueOf(dustColorTransitionParticleEffect.scale))).apply((Applicative<DustColorTransitionParticleEffect, ?>)instance, DustColorTransitionParticleEffect::new));
    public static final ParticleEffect.Factory<DustColorTransitionParticleEffect> FACTORY = new ParticleEffect.Factory<DustColorTransitionParticleEffect>(){

        @Override
        public DustColorTransitionParticleEffect read(ParticleType<DustColorTransitionParticleEffect> particleType, StringReader stringReader) throws CommandSyntaxException {
            Vec3d vec3d = AbstractDustParticleEffect.readColor(stringReader);
            stringReader.expect(' ');
            float f = (float)stringReader.readDouble();
            Vec3d vec3d2 = AbstractDustParticleEffect.readColor(stringReader);
            return new DustColorTransitionParticleEffect(vec3d, vec3d2, f);
        }

        @Override
        public DustColorTransitionParticleEffect read(ParticleType<DustColorTransitionParticleEffect> particleType, PacketByteBuf packetByteBuf) {
            return new DustColorTransitionParticleEffect(new Vec3d(packetByteBuf.readFloat(), packetByteBuf.readFloat(), packetByteBuf.readFloat()), new Vec3d(packetByteBuf.readFloat(), packetByteBuf.readFloat(), packetByteBuf.readFloat()), packetByteBuf.readFloat());
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
    private final Vec3d toColor;

    public DustColorTransitionParticleEffect(Vec3d fromColor, Vec3d toColor, float scale) {
        super(fromColor, scale);
        this.toColor = toColor;
    }

    @Environment(value=EnvType.CLIENT)
    public Vec3d getFromColor() {
        return this.color;
    }

    @Environment(value=EnvType.CLIENT)
    public Vec3d getToColor() {
        return this.toColor;
    }

    @Override
    public void write(PacketByteBuf buf) {
        super.write(buf);
        buf.writeDouble(this.color.x);
        buf.writeDouble(this.color.y);
        buf.writeDouble(this.color.z);
    }

    @Override
    public String asString() {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f %.2f %.2f %.2f", Registry.PARTICLE_TYPE.getId(this.getType()), this.color.x, this.color.y, this.color.z, Float.valueOf(this.scale), this.toColor.x, this.toColor.y, this.toColor.z);
    }

    public ParticleType<DustColorTransitionParticleEffect> getType() {
        return ParticleTypes.DUST_COLOR_TRANSITION;
    }
}

