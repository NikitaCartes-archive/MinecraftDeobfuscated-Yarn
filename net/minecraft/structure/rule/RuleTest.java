/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.structure.rule;

import net.minecraft.structure.rule.AbstractRuleTest;
import net.minecraft.structure.rule.AlwaysTrueRuleTest;
import net.minecraft.structure.rule.BlockMatchRuleTest;
import net.minecraft.structure.rule.BlockStateMatchRuleTest;
import net.minecraft.structure.rule.RandomBlockMatchRuleTest;
import net.minecraft.structure.rule.RandomBlockStateMatchRuleTest;
import net.minecraft.structure.rule.TagMatchRuleTest;
import net.minecraft.util.DynamicDeserializer;
import net.minecraft.util.registry.Registry;

public interface RuleTest
extends DynamicDeserializer<AbstractRuleTest> {
    public static final RuleTest ALWAYS_TRUE = RuleTest.register("always_true", dynamic -> AlwaysTrueRuleTest.INSTANCE);
    public static final RuleTest BLOCK_MATCH = RuleTest.register("block_match", BlockMatchRuleTest::new);
    public static final RuleTest BLOCKSTATE_MATCH = RuleTest.register("blockstate_match", BlockStateMatchRuleTest::new);
    public static final RuleTest TAG_MATCH = RuleTest.register("tag_match", TagMatchRuleTest::new);
    public static final RuleTest RANDOM_BLOCK_MATCH = RuleTest.register("random_block_match", RandomBlockMatchRuleTest::new);
    public static final RuleTest RANDOM_BLOCKSTATE_MATCH = RuleTest.register("random_blockstate_match", RandomBlockStateMatchRuleTest::new);

    public static RuleTest register(String id, RuleTest test) {
        return Registry.register(Registry.RULE_TEST, id, test);
    }
}

