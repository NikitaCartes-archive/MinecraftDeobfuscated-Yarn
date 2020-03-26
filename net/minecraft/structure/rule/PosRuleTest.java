/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.structure.rule;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.structure.rule.PosRuleTestType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public abstract class PosRuleTest {
    public abstract boolean test(BlockPos var1, BlockPos var2, BlockPos var3, Random var4);

    public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
        return new Dynamic<T>(ops, ops.mergeInto(this.serializeContents(ops).getValue(), ops.createString("predicate_type"), ops.createString(Registry.POS_RULE_TEST.getId(this.getType()).toString())));
    }

    protected abstract PosRuleTestType getType();

    protected abstract <T> Dynamic<T> serializeContents(DynamicOps<T> var1);
}

