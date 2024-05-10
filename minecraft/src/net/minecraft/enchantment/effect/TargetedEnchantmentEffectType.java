package net.minecraft.enchantment.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextType;

public record TargetedEnchantmentEffectType<T>(
	EnchantmentEffectTarget enchanted, EnchantmentEffectTarget affected, T effect, Optional<LootCondition> requirements
) {
	public static <S> Codec<TargetedEnchantmentEffectType<S>> createPostAttackCodec(Codec<S> effectCodec, LootContextType lootContextType) {
		return RecordCodecBuilder.create(
			instance -> instance.group(
						EnchantmentEffectTarget.CODEC.fieldOf("enchanted").forGetter(TargetedEnchantmentEffectType::enchanted),
						EnchantmentEffectTarget.CODEC.fieldOf("affected").forGetter(TargetedEnchantmentEffectType::affected),
						effectCodec.fieldOf("effect").forGetter(TargetedEnchantmentEffectType::effect),
						EnchantmentEffectEntry.createRequirementsCodec(lootContextType).optionalFieldOf("requirements").forGetter(TargetedEnchantmentEffectType::requirements)
					)
					.apply(instance, TargetedEnchantmentEffectType::new)
		);
	}

	public static <S> Codec<TargetedEnchantmentEffectType<S>> createEquipmentDropsCodec(Codec<S> effectCodec, LootContextType lootContextType) {
		return RecordCodecBuilder.create(
			instance -> instance.group(
						EnchantmentEffectTarget.CODEC
							.validate(
								enchanted -> enchanted != EnchantmentEffectTarget.DAMAGING_ENTITY
										? DataResult.success(enchanted)
										: DataResult.error(() -> "enchanted must be attacker or victim")
							)
							.fieldOf("enchanted")
							.forGetter(TargetedEnchantmentEffectType::enchanted),
						effectCodec.fieldOf("effect").forGetter(TargetedEnchantmentEffectType::effect),
						EnchantmentEffectEntry.createRequirementsCodec(lootContextType).optionalFieldOf("requirements").forGetter(TargetedEnchantmentEffectType::requirements)
					)
					.apply(
						instance, (enchantedx, effect, requirements) -> new TargetedEnchantmentEffectType<>(enchantedx, EnchantmentEffectTarget.VICTIM, effect, requirements)
					)
		);
	}

	public boolean test(LootContext lootContext) {
		return this.requirements.isEmpty() ? true : ((LootCondition)this.requirements.get()).test(lootContext);
	}
}
