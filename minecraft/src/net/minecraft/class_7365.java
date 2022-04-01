package net.minecraft;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Locale;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.registry.Registry;

public record class_7365(float roll) implements ParticleEffect {
	public static final Codec<class_7365> field_38679 = RecordCodecBuilder.create(
		instance -> instance.group(Codec.FLOAT.fieldOf("roll").forGetter(arg -> arg.roll)).apply(instance, class_7365::new)
	);
	public static final ParticleEffect.Factory<class_7365> field_38680 = new ParticleEffect.Factory<class_7365>() {
		public class_7365 read(ParticleType<class_7365> particleType, StringReader stringReader) throws CommandSyntaxException {
			float f = stringReader.readFloat();
			return new class_7365(f);
		}

		public class_7365 read(ParticleType<class_7365> particleType, PacketByteBuf packetByteBuf) {
			return new class_7365(packetByteBuf.readFloat());
		}
	};

	@Override
	public ParticleType<class_7365> getType() {
		return ParticleTypes.SCULK_CHARGE;
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeFloat(this.roll);
	}

	@Override
	public String asString() {
		return String.format(Locale.ROOT, "%s %.2f", Registry.PARTICLE_TYPE.getId(this.getType()), this.roll);
	}
}
