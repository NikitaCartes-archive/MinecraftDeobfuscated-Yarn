package net.minecraft.loot.function;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.SuspiciousStewEffectsComponent;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProviderTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Util;

public class SetStewEffectLootFunction extends ConditionalLootFunction {
	private static final Codec<List<SetStewEffectLootFunction.StewEffect>> STEW_EFFECT_LIST_CODEC = SetStewEffectLootFunction.StewEffect.CODEC
		.listOf()
		.validate(stewEffects -> {
			Set<RegistryEntry<StatusEffect>> set = new ObjectOpenHashSet<>();

			for (SetStewEffectLootFunction.StewEffect stewEffect : stewEffects) {
				if (!set.add(stewEffect.effect())) {
					return DataResult.error(() -> "Encountered duplicate mob effect: '" + stewEffect.effect() + "'");
				}
			}

			return DataResult.success(stewEffects);
		});
	public static final MapCodec<SetStewEffectLootFunction> CODEC = RecordCodecBuilder.mapCodec(
		instance -> addConditionsField(instance)
				.and(STEW_EFFECT_LIST_CODEC.optionalFieldOf("effects", List.of()).forGetter(function -> function.stewEffects))
				.apply(instance, SetStewEffectLootFunction::new)
	);
	private final List<SetStewEffectLootFunction.StewEffect> stewEffects;

	SetStewEffectLootFunction(List<LootCondition> conditions, List<SetStewEffectLootFunction.StewEffect> stewEffects) {
		super(conditions);
		this.stewEffects = stewEffects;
	}

	@Override
	public LootFunctionType<SetStewEffectLootFunction> getType() {
		return LootFunctionTypes.SET_STEW_EFFECT;
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return (Set<LootContextParameter<?>>)this.stewEffects
			.stream()
			.flatMap(stewEffect -> stewEffect.duration().getRequiredParameters().stream())
			.collect(ImmutableSet.toImmutableSet());
	}

	@Override
	public ItemStack process(ItemStack stack, LootContext context) {
		if (stack.isOf(Items.SUSPICIOUS_STEW) && !this.stewEffects.isEmpty()) {
			SetStewEffectLootFunction.StewEffect stewEffect = Util.getRandom(this.stewEffects, context.getRandom());
			RegistryEntry<StatusEffect> registryEntry = stewEffect.effect();
			int i = stewEffect.duration().nextInt(context);
			if (!registryEntry.value().isInstant()) {
				i *= 20;
			}

			SuspiciousStewEffectsComponent.StewEffect stewEffect2 = new SuspiciousStewEffectsComponent.StewEffect(registryEntry, i);
			stack.apply(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS, SuspiciousStewEffectsComponent.DEFAULT, stewEffect2, SuspiciousStewEffectsComponent::with);
			return stack;
		} else {
			return stack;
		}
	}

	public static SetStewEffectLootFunction.Builder builder() {
		return new SetStewEffectLootFunction.Builder();
	}

	public static class Builder extends ConditionalLootFunction.Builder<SetStewEffectLootFunction.Builder> {
		private final ImmutableList.Builder<SetStewEffectLootFunction.StewEffect> map = ImmutableList.builder();

		protected SetStewEffectLootFunction.Builder getThisBuilder() {
			return this;
		}

		public SetStewEffectLootFunction.Builder withEffect(RegistryEntry<StatusEffect> effect, LootNumberProvider durationRange) {
			this.map.add(new SetStewEffectLootFunction.StewEffect(effect, durationRange));
			return this;
		}

		@Override
		public LootFunction build() {
			return new SetStewEffectLootFunction(this.getConditions(), this.map.build());
		}
	}

	static record StewEffect(RegistryEntry<StatusEffect> effect, LootNumberProvider duration) {
		public static final Codec<SetStewEffectLootFunction.StewEffect> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						StatusEffect.ENTRY_CODEC.fieldOf("type").forGetter(SetStewEffectLootFunction.StewEffect::effect),
						LootNumberProviderTypes.CODEC.fieldOf("duration").forGetter(SetStewEffectLootFunction.StewEffect::duration)
					)
					.apply(instance, SetStewEffectLootFunction.StewEffect::new)
		);
	}
}
