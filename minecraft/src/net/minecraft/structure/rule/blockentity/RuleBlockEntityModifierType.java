package net.minecraft.structure.rule.blockentity;

import com.mojang.serialization.Codec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public interface RuleBlockEntityModifierType<P extends RuleBlockEntityModifier> {
	RuleBlockEntityModifierType<ClearRuleBlockEntityModifier> CLEAR = register("clear", ClearRuleBlockEntityModifier.CODEC);
	RuleBlockEntityModifierType<PassthroughRuleBlockEntityModifier> PASSTHROUGH = register("passthrough", PassthroughRuleBlockEntityModifier.CODEC);
	RuleBlockEntityModifierType<AppendStaticRuleBlockEntityModifier> APPEND_STATIC = register("append_static", AppendStaticRuleBlockEntityModifier.CODEC);
	RuleBlockEntityModifierType<AppendLootRuleBlockEntityModifier> APPEND_LOOT = register("append_loot", AppendLootRuleBlockEntityModifier.CODEC);

	Codec<P> codec();

	private static <P extends RuleBlockEntityModifier> RuleBlockEntityModifierType<P> register(String id, Codec<P> codec) {
		return Registry.register(Registries.RULE_BLOCK_ENTITY_MODIFIER, id, () -> codec);
	}
}
