package net.minecraft.component.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Uuids;

public record HeatComponent(@Nullable UUID owner, int slot, int heat) {
	public static final Codec<HeatComponent> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Uuids.INT_STREAM_CODEC.fieldOf("owner").forGetter(component -> component.owner),
					Codec.INT.fieldOf("slot").forGetter(component -> component.slot),
					Codec.INT.fieldOf("heat").forGetter(component -> component.heat)
				)
				.apply(instance, HeatComponent::new)
	);
	public static final PacketCodec<ByteBuf, HeatComponent> PACKET_CODEC = PacketCodecs.codec(CODEC);
}
