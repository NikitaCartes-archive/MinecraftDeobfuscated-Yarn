package net.minecraft.component.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.item.TooltipType;
import net.minecraft.item.TooltipAppender;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.dynamic.Codecs;

public record FireworksComponent(int flightDuration, List<FireworkExplosionComponent> explosions) implements TooltipAppender {
	public static final int MAX_EXPLOSIONS = 256;
	public static final Codec<FireworksComponent> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codecs.UNSIGNED_BYTE.optionalFieldOf("flight_duration", 0).forGetter(FireworksComponent::flightDuration),
					FireworkExplosionComponent.CODEC.sizeLimitedListOf(256).optionalFieldOf("explosions", List.of()).forGetter(FireworksComponent::explosions)
				)
				.apply(instance, FireworksComponent::new)
	);
	public static final PacketCodec<ByteBuf, FireworksComponent> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.VAR_INT,
		FireworksComponent::flightDuration,
		FireworkExplosionComponent.PACKET_CODEC.collect(PacketCodecs.toList(256)),
		FireworksComponent::explosions,
		FireworksComponent::new
	);

	@Override
	public void appendTooltip(Consumer<Text> textConsumer, TooltipType context) {
		if (this.flightDuration > 0) {
			textConsumer.accept(
				Text.translatable("item.minecraft.firework_rocket.flight").append(ScreenTexts.SPACE).append(String.valueOf(this.flightDuration)).formatted(Formatting.GRAY)
			);
		}

		for (FireworkExplosionComponent fireworkExplosionComponent : this.explosions) {
			fireworkExplosionComponent.appendShapeTooltip(textConsumer);
			fireworkExplosionComponent.appendOptionalTooltip(text -> textConsumer.accept(Text.literal("  ").append(text)));
		}
	}
}
