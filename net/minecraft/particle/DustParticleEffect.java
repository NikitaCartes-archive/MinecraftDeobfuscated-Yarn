/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;

public class DustParticleEffect
implements ParticleEffect {
    public static final DustParticleEffect RED = new DustParticleEffect(1.0f, 0.0f, 0.0f, 1.0f);
    public static final ParticleEffect.Factory<DustParticleEffect> PARAMETERS_FACTORY = new ParticleEffect.Factory<DustParticleEffect>(){

        public DustParticleEffect method_10287(ParticleType<DustParticleEffect> particleType, StringReader stringReader) throws CommandSyntaxException {
            stringReader.expect(' ');
            float f = (float)stringReader.readDouble();
            stringReader.expect(' ');
            float g = (float)stringReader.readDouble();
            stringReader.expect(' ');
            float h = (float)stringReader.readDouble();
            stringReader.expect(' ');
            float i = (float)stringReader.readDouble();
            return new DustParticleEffect(f, g, h, i);
        }

        public DustParticleEffect method_10288(ParticleType<DustParticleEffect> particleType, PacketByteBuf packetByteBuf) {
            return new DustParticleEffect(packetByteBuf.readFloat(), packetByteBuf.readFloat(), packetByteBuf.readFloat(), packetByteBuf.readFloat());
        }

        @Override
        public /* synthetic */ ParticleEffect read(ParticleType particleType, PacketByteBuf packetByteBuf) {
            return this.method_10288(particleType, packetByteBuf);
        }

        @Override
        public /* synthetic */ ParticleEffect read(ParticleType particleType, StringReader stringReader) throws CommandSyntaxException {
            return this.method_10287(particleType, stringReader);
        }
    };
    private final float red;
    private final float green;
    private final float blue;
    private final float scale;

    public DustParticleEffect(float f, float g, float h, float i) {
        this.red = f;
        this.green = g;
        this.blue = h;
        this.scale = MathHelper.clamp(i, 0.01f, 4.0f);
    }

    @Override
    public void write(PacketByteBuf packetByteBuf) {
        packetByteBuf.writeFloat(this.red);
        packetByteBuf.writeFloat(this.green);
        packetByteBuf.writeFloat(this.blue);
        packetByteBuf.writeFloat(this.scale);
    }

    @Override
    public String asString() {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f", Registry.PARTICLE_TYPE.getId(this.getType()), Float.valueOf(this.red), Float.valueOf(this.green), Float.valueOf(this.blue), Float.valueOf(this.scale));
    }

    public ParticleType<DustParticleEffect> getType() {
        return ParticleTypes.DUST;
    }

    @Environment(value=EnvType.CLIENT)
    public float getRed() {
        return this.red;
    }

    @Environment(value=EnvType.CLIENT)
    public float getGreen() {
        return this.green;
    }

    @Environment(value=EnvType.CLIENT)
    public float getBlue() {
        return this.blue;
    }

    @Environment(value=EnvType.CLIENT)
    public float getScale() {
        return this.scale;
    }
}

