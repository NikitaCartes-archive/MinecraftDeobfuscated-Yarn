package net.minecraft.structure.processor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.rule.AlwaysTruePosRuleTest;
import net.minecraft.structure.rule.PosRuleTest;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.util.math.BlockPos;

public class StructureProcessorRule {
	public static final Codec<StructureProcessorRule> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					RuleTest.field_25012.fieldOf("input_predicate").forGetter(structureProcessorRule -> structureProcessorRule.inputPredicate),
					RuleTest.field_25012.fieldOf("location_predicate").forGetter(structureProcessorRule -> structureProcessorRule.locationPredicate),
					PosRuleTest.field_25007
						.optionalFieldOf("position_predicate", AlwaysTruePosRuleTest.INSTANCE)
						.forGetter(structureProcessorRule -> structureProcessorRule.positionPredicate),
					BlockState.CODEC.fieldOf("output_state").forGetter(structureProcessorRule -> structureProcessorRule.outputState),
					CompoundTag.CODEC.optionalFieldOf("output_nbt").forGetter(structureProcessorRule -> Optional.ofNullable(structureProcessorRule.tag))
				)
				.apply(instance, StructureProcessorRule::new)
	);
	private final RuleTest inputPredicate;
	private final RuleTest locationPredicate;
	private final PosRuleTest positionPredicate;
	private final BlockState outputState;
	@Nullable
	private final CompoundTag tag;

	public StructureProcessorRule(RuleTest inputPredicate, RuleTest locationPredicate, BlockState state) {
		this(inputPredicate, locationPredicate, AlwaysTruePosRuleTest.INSTANCE, state, Optional.empty());
	}

	public StructureProcessorRule(RuleTest inputPredicate, RuleTest locationPredicate, PosRuleTest positionPredicate, BlockState state) {
		this(inputPredicate, locationPredicate, positionPredicate, state, Optional.empty());
	}

	public StructureProcessorRule(
		RuleTest inputPredicate, RuleTest locationPredicate, PosRuleTest positionPredicate, BlockState outputState, Optional<CompoundTag> tag
	) {
		this.inputPredicate = inputPredicate;
		this.locationPredicate = locationPredicate;
		this.positionPredicate = positionPredicate;
		this.outputState = outputState;
		this.tag = (CompoundTag)tag.orElse(null);
	}

	public boolean test(BlockState input, BlockState location, BlockPos blockPos, BlockPos blockPos2, BlockPos blockPos3, Random random) {
		return this.inputPredicate.test(input, random)
			&& this.locationPredicate.test(location, random)
			&& this.positionPredicate.test(blockPos, blockPos2, blockPos3, random);
	}

	public BlockState getOutputState() {
		return this.outputState;
	}

	@Nullable
	public CompoundTag getTag() {
		return this.tag;
	}
}
