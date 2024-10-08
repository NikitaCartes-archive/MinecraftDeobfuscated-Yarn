package net.minecraft.enchantment.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.context.ContextType;

public record EnchantmentEffectEntry<T>(T effect, Optional<LootCondition> requirements) {
	public static Codec<LootCondition> createRequirementsCodec(ContextType lootContextType) {
		return LootCondition.CODEC
			.validate(
				condition -> {
					ErrorReporter.Impl impl = new ErrorReporter.Impl();
					LootTableReporter lootTableReporter = new LootTableReporter(impl, lootContextType);
					condition.validate(lootTableReporter);
					return (DataResult)impl.getErrorsAsString()
						.map(errors -> DataResult.error(() -> "Validation error in enchantment effect condition: " + errors))
						.orElseGet(() -> DataResult.success(condition));
				}
			);
	}

	public static <T> Codec<EnchantmentEffectEntry<T>> createCodec(Codec<T> effectCodec, ContextType lootContextType) {
		return RecordCodecBuilder.create(
			instance -> instance.group(
						effectCodec.fieldOf("effect").forGetter(EnchantmentEffectEntry::effect),
						createRequirementsCodec(lootContextType).optionalFieldOf("requirements").forGetter(EnchantmentEffectEntry::requirements)
					)
					.apply(instance, EnchantmentEffectEntry::new)
		);
	}

	public boolean test(LootContext context) {
		return this.requirements.isEmpty() ? true : ((LootCondition)this.requirements.get()).test(context);
	}
}
