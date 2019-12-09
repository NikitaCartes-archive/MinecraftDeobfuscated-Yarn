/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.structure.processor;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.datafixers.NbtOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.rule.AbstractRuleTest;
import net.minecraft.structure.rule.AlwaysTrueRuleTest;
import net.minecraft.util.DynamicDeserializer;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public class StructureProcessorRule {
    private final AbstractRuleTest inputPredicate;
    private final AbstractRuleTest locationPredicate;
    private final BlockState outputState;
    @Nullable
    private final CompoundTag tag;

    public StructureProcessorRule(AbstractRuleTest abstractRuleTest, AbstractRuleTest abstractRuleTest2, BlockState blockState) {
        this(abstractRuleTest, abstractRuleTest2, blockState, null);
    }

    public StructureProcessorRule(AbstractRuleTest abstractRuleTest, AbstractRuleTest abstractRuleTest2, BlockState blockState, @Nullable CompoundTag compoundTag) {
        this.inputPredicate = abstractRuleTest;
        this.locationPredicate = abstractRuleTest2;
        this.outputState = blockState;
        this.tag = compoundTag;
    }

    public boolean test(BlockState input, BlockState location, Random random) {
        return this.inputPredicate.test(input, random) && this.locationPredicate.test(location, random);
    }

    public BlockState getOutputState() {
        return this.outputState;
    }

    @Nullable
    public CompoundTag getTag() {
        return this.tag;
    }

    public <T> Dynamic<T> method_16764(DynamicOps<T> dynamicOps) {
        T object = dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("input_predicate"), this.inputPredicate.method_16767(dynamicOps).getValue(), dynamicOps.createString("location_predicate"), this.locationPredicate.method_16767(dynamicOps).getValue(), dynamicOps.createString("output_state"), BlockState.serialize(dynamicOps, this.outputState).getValue()));
        if (this.tag == null) {
            return new Dynamic<T>(dynamicOps, object);
        }
        return new Dynamic<T>(dynamicOps, dynamicOps.mergeInto(object, dynamicOps.createString("output_nbt"), new Dynamic<CompoundTag>(NbtOps.INSTANCE, this.tag).convert(dynamicOps).getValue()));
    }

    public static <T> StructureProcessorRule method_16765(Dynamic<T> dynamic2) {
        Dynamic<T> dynamic22 = dynamic2.get("input_predicate").orElseEmptyMap();
        Dynamic<T> dynamic3 = dynamic2.get("location_predicate").orElseEmptyMap();
        AbstractRuleTest abstractRuleTest = DynamicDeserializer.deserialize(dynamic22, Registry.RULE_TEST, "predicate_type", AlwaysTrueRuleTest.INSTANCE);
        AbstractRuleTest abstractRuleTest2 = DynamicDeserializer.deserialize(dynamic3, Registry.RULE_TEST, "predicate_type", AlwaysTrueRuleTest.INSTANCE);
        BlockState blockState = BlockState.deserialize(dynamic2.get("output_state").orElseEmptyMap());
        CompoundTag compoundTag = dynamic2.get("output_nbt").map(dynamic -> dynamic.convert(NbtOps.INSTANCE).getValue()).orElse(null);
        return new StructureProcessorRule(abstractRuleTest, abstractRuleTest2, blockState, compoundTag);
    }
}

