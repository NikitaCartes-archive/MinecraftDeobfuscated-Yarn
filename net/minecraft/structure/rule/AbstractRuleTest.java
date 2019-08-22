/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.structure.rule;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.util.registry.Registry;

public abstract class AbstractRuleTest {
    public abstract boolean test(BlockState var1, Random var2);

    public <T> Dynamic<T> method_16767(DynamicOps<T> dynamicOps) {
        return new Dynamic<T>(dynamicOps, dynamicOps.mergeInto(this.serialize(dynamicOps).getValue(), dynamicOps.createString("predicate_type"), dynamicOps.createString(Registry.RULE_TEST.getId(this.getRuleTest()).toString())));
    }

    protected abstract RuleTest getRuleTest();

    protected abstract <T> Dynamic<T> serialize(DynamicOps<T> var1);
}

