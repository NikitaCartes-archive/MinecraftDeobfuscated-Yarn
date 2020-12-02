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
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Vibration;
import net.minecraft.world.event.BlockPositionSource;

public class VibrationParticleEffect
implements ParticleEffect {
    public static final Codec<VibrationParticleEffect> field_28277 = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Vibration.CODEC.fieldOf("vibration")).forGetter(vibrationParticleEffect -> vibrationParticleEffect.field_28279)).apply((Applicative<VibrationParticleEffect, ?>)instance, VibrationParticleEffect::new));
    public static final ParticleEffect.Factory<VibrationParticleEffect> field_28278 = new ParticleEffect.Factory<VibrationParticleEffect>(){

        @Override
        public VibrationParticleEffect read(ParticleType<VibrationParticleEffect> particleType, StringReader stringReader) throws CommandSyntaxException {
            stringReader.expect(' ');
            float f = (float)stringReader.readDouble();
            stringReader.expect(' ');
            float g = (float)stringReader.readDouble();
            stringReader.expect(' ');
            float h = (float)stringReader.readDouble();
            stringReader.expect(' ');
            float i = (float)stringReader.readDouble();
            stringReader.expect(' ');
            float j = (float)stringReader.readDouble();
            stringReader.expect(' ');
            float k = (float)stringReader.readDouble();
            stringReader.expect(' ');
            int l = stringReader.readInt();
            BlockPos blockPos = new BlockPos(f, g, h);
            BlockPos blockPos2 = new BlockPos(i, j, k);
            return new VibrationParticleEffect(new Vibration(blockPos, new BlockPositionSource(blockPos2), l));
        }

        @Override
        public VibrationParticleEffect read(ParticleType<VibrationParticleEffect> particleType, PacketByteBuf packetByteBuf) {
            Vibration vibration = Vibration.readFromBuf(packetByteBuf);
            return new VibrationParticleEffect(vibration);
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
    private final Vibration field_28279;

    public VibrationParticleEffect(Vibration vibration) {
        this.field_28279 = vibration;
    }

    @Override
    public void write(PacketByteBuf buf) {
        Vibration.writeToBuf(buf, this.field_28279);
    }

    @Override
    public String asString() {
        BlockPos blockPos = this.field_28279.getOrigin();
        double d = blockPos.getX();
        double e = blockPos.getY();
        double f = blockPos.getZ();
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f %.2f %.2f %d", Registry.PARTICLE_TYPE.getId(this.getType()), d, e, f, d, e, f, this.field_28279.getArrivalInTicks());
    }

    public ParticleType<VibrationParticleEffect> getType() {
        return ParticleTypes.VIBRATION;
    }

    @Environment(value=EnvType.CLIENT)
    public Vibration method_33125() {
        return this.field_28279;
    }
}

