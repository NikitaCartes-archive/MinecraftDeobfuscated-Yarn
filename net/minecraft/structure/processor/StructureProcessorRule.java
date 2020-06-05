/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.structure.processor;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.rule.AlwaysTruePosRuleTest;
import net.minecraft.structure.rule.PosRuleTest;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class StructureProcessorRule {
    public static final Codec<StructureProcessorRule> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)RuleTest.field_25012.fieldOf("input_predicate")).forGetter(structureProcessorRule -> structureProcessorRule.inputPredicate), ((MapCodec)RuleTest.field_25012.fieldOf("location_predicate")).forGetter(structureProcessorRule -> structureProcessorRule.locationPredicate), ((MapCodec)PosRuleTest.field_25007.fieldOf("position_predicate")).forGetter(structureProcessorRule -> structureProcessorRule.positionPredicate), ((MapCodec)BlockState.CODEC.fieldOf("output_state")).forGetter(structureProcessorRule -> structureProcessorRule.outputState), CompoundTag.field_25128.optionalFieldOf("output_nbt").forGetter(structureProcessorRule -> Optional.ofNullable(structureProcessorRule.tag))).apply((Applicative<StructureProcessorRule, ?>)instance, StructureProcessorRule::new));
    private final RuleTest inputPredicate;
    private final RuleTest locationPredicate;
    private final PosRuleTest positionPredicate;
    private final BlockState outputState;
    @Nullable
    private final CompoundTag tag;

    public StructureProcessorRule(RuleTest ruleTest, RuleTest ruleTest2, BlockState blockState) {
        this(ruleTest, ruleTest2, AlwaysTruePosRuleTest.INSTANCE, blockState, Optional.empty());
    }

    public StructureProcessorRule(RuleTest ruleTest, RuleTest ruleTest2, PosRuleTest posRuleTest, BlockState blockState) {
        this(ruleTest, ruleTest2, posRuleTest, blockState, Optional.empty());
    }

    public StructureProcessorRule(RuleTest ruleTest, RuleTest ruleTest2, PosRuleTest posRuleTest, BlockState blockState, Optional<CompoundTag> optional) {
        this.inputPredicate = ruleTest;
        this.locationPredicate = ruleTest2;
        this.positionPredicate = posRuleTest;
        this.outputState = blockState;
        this.tag = optional.orElse(null);
    }

    public boolean test(BlockState input, BlockState location, BlockPos blockPos, BlockPos blockPos2, BlockPos blockPos3, Random random) {
        return this.inputPredicate.test(input, random) && this.locationPredicate.test(location, random) && this.positionPredicate.test(blockPos, blockPos2, blockPos3, random);
    }

    public BlockState getOutputState() {
        return this.outputState;
    }

    @Nullable
    public CompoundTag getTag() {
        return this.tag;
    }
}

