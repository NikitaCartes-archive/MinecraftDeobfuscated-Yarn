package net.minecraft.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Locale;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Vibration;
import net.minecraft.world.event.BlockPositionSource;

public class VibrationParticleEffect implements ParticleEffect {
	public static final Codec<VibrationParticleEffect> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(Vibration.CODEC.fieldOf("vibration").forGetter(vibrationParticleEffect -> vibrationParticleEffect.vibration))
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
			float i = (float)stringReader.readDouble();
			stringReader.expect(' ');
			float j = (float)stringReader.readDouble();
			stringReader.expect(' ');
			float k = (float)stringReader.readDouble();
			stringReader.expect(' ');
			int l = stringReader.readInt();
			BlockPos blockPos = new BlockPos((double)f, (double)g, (double)h);
			BlockPos blockPos2 = new BlockPos((double)i, (double)j, (double)k);
			return new VibrationParticleEffect(new Vibration(blockPos, new BlockPositionSource(blockPos2), l));
		}

		public VibrationParticleEffect read(ParticleType<VibrationParticleEffect> particleType, PacketByteBuf packetByteBuf) {
			Vibration vibration = Vibration.readFromBuf(packetByteBuf);
			return new VibrationParticleEffect(vibration);
		}
	};
	private final Vibration vibration;

	public VibrationParticleEffect(Vibration vibration) {
		this.vibration = vibration;
	}

	@Override
	public void write(PacketByteBuf buf) {
		Vibration.writeToBuf(buf, this.vibration);
	}

	@Override
	public String asString() {
		BlockPos blockPos = this.vibration.getOrigin();
		double d = (double)blockPos.getX();
		double e = (double)blockPos.getY();
		double f = (double)blockPos.getZ();
		return String.format(
			Locale.ROOT, "%s %.2f %.2f %.2f %.2f %.2f %.2f %d", Registry.PARTICLE_TYPE.getId(this.getType()), d, e, f, d, e, f, this.vibration.getArrivalInTicks()
		);
	}

	@Override
	public ParticleType<VibrationParticleEffect> getType() {
		return ParticleTypes.VIBRATION;
	}

	public Vibration getVibration() {
		return this.vibration;
	}
}
