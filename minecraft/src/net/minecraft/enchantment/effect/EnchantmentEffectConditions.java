package net.minecraft.enchantment.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.util.ErrorReporter;

public record EnchantmentEffectConditions<T>(T effect, Optional<LootCondition> requirements) {
	public static Codec<LootCondition> createRequirementsCodec(LootContextType lootContextType) {
		return LootCondition.CODEC
			.validate(
				lootCondition -> {
					ErrorReporter.Impl impl = new ErrorReporter.Impl();
					lootContextType.validate(impl, lootCondition);
					return (DataResult)impl.getErrorsAsString()
						.map(errors -> DataResult.error(() -> "Validation error in enchantment effect condition: " + errors))
						.orElseGet(() -> DataResult.success(lootCondition));
				}
			);
	}

	public static <T> Codec<EnchantmentEffectConditions<T>> createCodec(Codec<T> effectCodec, LootContextType lootContextType) {
		return RecordCodecBuilder.create(
			instance -> instance.group(
						effectCodec.fieldOf("effect").forGetter(EnchantmentEffectConditions::effect),
						createRequirementsCodec(lootContextType).optionalFieldOf("requirements").forGetter(EnchantmentEffectConditions::requirements)
					)
					.apply(instance, EnchantmentEffectConditions::new)
		);
	}

	public boolean test(LootContext context) {
		return this.requirements.isEmpty() ? true : ((LootCondition)this.requirements.get()).test(context);
	}
}
