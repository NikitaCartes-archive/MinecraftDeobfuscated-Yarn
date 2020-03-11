/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.structure.rule;

import net.minecraft.structure.rule.AlwaysTrueRuleTest;
import net.minecraft.structure.rule.BlockMatchRuleTest;
import net.minecraft.structure.rule.BlockStateMatchRuleTest;
import net.minecraft.structure.rule.RandomBlockMatchRuleTest;
import net.minecraft.structure.rule.RandomBlockStateMatchRuleTest;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.structure.rule.TagMatchRuleTest;
import net.minecraft.util.dynamic.DynamicDeserializer;
import net.minecraft.util.registry.Registry;

public interface RuleTestType
extends DynamicDeserializer<RuleTest> {
    public static final RuleTestType ALWAYS_TRUE = RuleTestType.register("always_true", dynamic -> AlwaysTrueRuleTest.INSTANCE);
    public static final RuleTestType BLOCK_MATCH = RuleTestType.register("block_match", BlockMatchRuleTest::new);
    public static final RuleTestType BLOCKSTATE_MATCH = RuleTestType.register("blockstate_match", BlockStateMatchRuleTest::new);
    public static final RuleTestType TAG_MATCH = RuleTestType.register("tag_match", TagMatchRuleTest::new);
    public static final RuleTestType RANDOM_BLOCK_MATCH = RuleTestType.register("random_block_match", RandomBlockMatchRuleTest::new);
    public static final RuleTestType RANDOM_BLOCKSTATE_MATCH = RuleTestType.register("random_blockstate_match", RandomBlockStateMatchRuleTest::new);

    public static RuleTestType register(String id, RuleTestType test) {
        return Registry.register(Registry.RULE_TEST, id, test);
    }
}

