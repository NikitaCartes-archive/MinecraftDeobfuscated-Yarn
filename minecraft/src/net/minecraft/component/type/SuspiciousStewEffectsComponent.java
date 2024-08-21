package net.minecraft.component.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.world.World;

public record SuspiciousStewEffectsComponent(List<SuspiciousStewEffectsComponent.StewEffect> effects) implements Consumable, TooltipAppender {
	public static final SuspiciousStewEffectsComponent DEFAULT = new SuspiciousStewEffectsComponent(List.of());
	public static final int DEFAULT_DURATION = 160;
	public static final Codec<SuspiciousStewEffectsComponent> CODEC = SuspiciousStewEffectsComponent.StewEffect.CODEC
		.listOf()
		.xmap(SuspiciousStewEffectsComponent::new, SuspiciousStewEffectsComponent::effects);
	public static final PacketCodec<RegistryByteBuf, SuspiciousStewEffectsComponent> PACKET_CODEC = SuspiciousStewEffectsComponent.StewEffect.PACKET_CODEC
		.collect(PacketCodecs.toList())
		.xmap(SuspiciousStewEffectsComponent::new, SuspiciousStewEffectsComponent::effects);

	public SuspiciousStewEffectsComponent with(SuspiciousStewEffectsComponent.StewEffect stewEffect) {
		return new SuspiciousStewEffectsComponent(Util.withAppended(this.effects, stewEffect));
	}

	@Override
	public void onConsume(World world, LivingEntity user, ItemStack stack, ConsumableComponent consumable) {
		for (SuspiciousStewEffectsComponent.StewEffect stewEffect : this.effects) {
			user.addStatusEffect(stewEffect.createStatusEffectInstance());
		}
	}

	@Override
	public void appendTooltip(Item.TooltipContext context, Consumer<Text> tooltip, TooltipType type) {
		if (type.isCreative()) {
			List<StatusEffectInstance> list = new ArrayList();

			for (SuspiciousStewEffectsComponent.StewEffect stewEffect : this.effects) {
				list.add(stewEffect.createStatusEffectInstance());
			}

			PotionContentsComponent.buildTooltip(list, tooltip, 1.0F, context.getUpdateTickRate());
		}
	}

	public static record StewEffect(RegistryEntry<StatusEffect> effect, int duration) {
		public static final Codec<SuspiciousStewEffectsComponent.StewEffect> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						StatusEffect.ENTRY_CODEC.fieldOf("id").forGetter(SuspiciousStewEffectsComponent.StewEffect::effect),
						Codec.INT.lenientOptionalFieldOf("duration", Integer.valueOf(160)).forGetter(SuspiciousStewEffectsComponent.StewEffect::duration)
					)
					.apply(instance, SuspiciousStewEffectsComponent.StewEffect::new)
		);
		public static final PacketCodec<RegistryByteBuf, SuspiciousStewEffectsComponent.StewEffect> PACKET_CODEC = PacketCodec.tuple(
			StatusEffect.ENTRY_PACKET_CODEC,
			SuspiciousStewEffectsComponent.StewEffect::effect,
			PacketCodecs.VAR_INT,
			SuspiciousStewEffectsComponent.StewEffect::duration,
			SuspiciousStewEffectsComponent.StewEffect::new
		);

		public StatusEffectInstance createStatusEffectInstance() {
			return new StatusEffectInstance(this.effect, this.duration);
		}
	}
}
