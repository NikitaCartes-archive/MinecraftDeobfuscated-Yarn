package net.minecraft.component.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.function.Consumer;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TooltipAppender;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public record PotatoBaneComponent(float damageBoost) implements TooltipAppender {
	public static final Codec<PotatoBaneComponent> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(Codec.FLOAT.fieldOf("damage_boost").forGetter(PotatoBaneComponent::damageBoost)).apply(instance, PotatoBaneComponent::new)
	);
	public static final PacketCodec<ByteBuf, PotatoBaneComponent> PACKET_CODEC = PacketCodecs.codec(CODEC);

	@Override
	public void appendTooltip(Consumer<Text> textConsumer, TooltipContext context) {
		textConsumer.accept(Text.translatable("potato_bane.tooltip.damage_boost", this.damageBoost).formatted(Formatting.GREEN));
	}

	public static float getDamageBoost(ItemStack stack, Entity entity) {
		if (entity.isPotato()) {
			PotatoBaneComponent potatoBaneComponent = stack.get(DataComponentTypes.POTATO_BANE);
			if (potatoBaneComponent != null) {
				return potatoBaneComponent.damageBoost;
			}
		}

		return 0.0F;
	}
}
