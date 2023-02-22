package net.minecraft.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Locale;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.event.BlockPositionSource;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.PositionSourceType;

public class VibrationParticleEffect implements ParticleEffect {
	public static final Codec<VibrationParticleEffect> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					PositionSource.CODEC.fieldOf("destination").forGetter(effect -> effect.destination),
					Codec.INT.fieldOf("arrival_in_ticks").forGetter(effect -> effect.arrivalInTicks)
				)
				.apply(instance, VibrationParticleEffect::new)
	);
	public static final ParticleEffect.Factory<VibrationParticleEffect> PARAMETERS_FACTORY = new ParticleEffect.Factory<VibrationParticleEffect>() {
		public VibrationParticleEffect read(ParticleType<VibrationParticleEffect> particleType, StringReader stringReader) throws CommandSyntaxException {
			stringReader.expect(' ');
			float f = (float)stringReader.readDouble();
			stringReader.expect(' ');
			float g = (float)stringReader.readDouble();
			stringReader.expect(' ');
			float h = (float)stringReader.readDouble();
			stringReader.expect(' ');
			int i = stringReader.readInt();
			BlockPos blockPos = BlockPos.ofFloored((double)f, (double)g, (double)h);
			return new VibrationParticleEffect(new BlockPositionSource(blockPos), i);
		}

		public VibrationParticleEffect read(ParticleType<VibrationParticleEffect> particleType, PacketByteBuf packetByteBuf) {
			PositionSource positionSource = PositionSourceType.read(packetByteBuf);
			int i = packetByteBuf.readVarInt();
			return new VibrationParticleEffect(positionSource, i);
		}
	};
	private final PositionSource destination;
	private final int arrivalInTicks;

	public VibrationParticleEffect(PositionSource destination, int arrivalInTicks) {
		this.destination = destination;
		this.arrivalInTicks = arrivalInTicks;
	}

	@Override
	public void write(PacketByteBuf buf) {
		PositionSourceType.write(this.destination, buf);
		buf.writeVarInt(this.arrivalInTicks);
	}

	@Override
	public String asString() {
		Vec3d vec3d = (Vec3d)this.destination.getPos(null).get();
		double d = vec3d.getX();
		double e = vec3d.getY();
		double f = vec3d.getZ();
		return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %d", Registries.PARTICLE_TYPE.getId(this.getType()), d, e, f, this.arrivalInTicks);
	}

	@Override
	public ParticleType<VibrationParticleEffect> getType() {
		return ParticleTypes.VIBRATION;
	}

	public PositionSource getVibration() {
		return this.destination;
	}

	public int getArrivalInTicks() {
		return this.arrivalInTicks;
	}
}
