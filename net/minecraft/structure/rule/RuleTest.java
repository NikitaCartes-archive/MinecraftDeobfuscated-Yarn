/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.structure.rule;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.structure.rule.RuleTestType;
import net.minecraft.util.registry.Registry;

/**
 * Rule tests are used in structure generation to check if a block state matches some condition.
 */
public abstract class RuleTest {
    public abstract boolean test(BlockState var1, Random var2);

    public <T> Dynamic<T> serializeWithId(DynamicOps<T> ops) {
        return new Dynamic<T>(ops, ops.mergeInto(this.serialize(ops).getValue(), ops.createString("predicate_type"), ops.createString(Registry.RULE_TEST.getId(this.getType()).toString())));
    }

    protected abstract RuleTestType getType();

    protected abstract <T> Dynamic<T> serialize(DynamicOps<T> var1);
}

