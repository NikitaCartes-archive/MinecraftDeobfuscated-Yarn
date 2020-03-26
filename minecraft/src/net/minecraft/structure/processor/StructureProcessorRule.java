package net.minecraft.structure.processor;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.rule.AlwaysTruePosRuleTest;
import net.minecraft.structure.rule.AlwaysTrueRuleTest;
import net.minecraft.structure.rule.PosRuleTest;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.util.dynamic.DynamicDeserializer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class StructureProcessorRule {
	private final RuleTest inputPredicate;
	private final RuleTest locationPredicate;
	private final PosRuleTest positionPredicate;
	private final BlockState outputState;
	@Nullable
	private final CompoundTag tag;

	public StructureProcessorRule(RuleTest ruleTest, RuleTest ruleTest2, BlockState blockState) {
		this(ruleTest, ruleTest2, AlwaysTruePosRuleTest.INSTANCE, blockState, null);
	}

	public StructureProcessorRule(RuleTest ruleTest, RuleTest ruleTest2, PosRuleTest posRuleTest, BlockState blockState, @Nullable CompoundTag compoundTag) {
		this.inputPredicate = ruleTest;
		this.locationPredicate = ruleTest2;
		this.positionPredicate = posRuleTest;
		this.outputState = blockState;
		this.tag = compoundTag;
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

	public <T> Dynamic<T> toDynamic(DynamicOps<T> dynamicOps) {
		T object = dynamicOps.createMap(
			ImmutableMap.of(
				dynamicOps.createString("input_predicate"),
				this.inputPredicate.serializeWithId(dynamicOps).getValue(),
				dynamicOps.createString("location_predicate"),
				this.locationPredicate.serializeWithId(dynamicOps).getValue(),
				dynamicOps.createString("position_predicate"),
				this.positionPredicate.serialize(dynamicOps).getValue(),
				dynamicOps.createString("output_state"),
				BlockState.serialize(dynamicOps, this.outputState).getValue()
			)
		);
		return this.tag == null
			? new Dynamic<>(dynamicOps, object)
			: new Dynamic<>(
				dynamicOps, dynamicOps.mergeInto(object, dynamicOps.createString("output_nbt"), new Dynamic<>(NbtOps.INSTANCE, this.tag).convert(dynamicOps).getValue())
			);
	}

	public static <T> StructureProcessorRule fromDynamic(Dynamic<T> dynamic) {
		Dynamic<T> dynamic2 = dynamic.get("input_predicate").orElseEmptyMap();
		Dynamic<T> dynamic3 = dynamic.get("location_predicate").orElseEmptyMap();
		Dynamic<T> dynamic4 = dynamic.get("position_predicate").orElseEmptyMap();
		RuleTest ruleTest = DynamicDeserializer.deserialize(dynamic2, Registry.RULE_TEST, "predicate_type", AlwaysTrueRuleTest.INSTANCE);
		RuleTest ruleTest2 = DynamicDeserializer.deserialize(dynamic3, Registry.RULE_TEST, "predicate_type", AlwaysTrueRuleTest.INSTANCE);
		PosRuleTest posRuleTest = DynamicDeserializer.deserialize(dynamic4, Registry.POS_RULE_TEST, "predicate_type", AlwaysTruePosRuleTest.INSTANCE);
		BlockState blockState = BlockState.deserialize(dynamic.get("output_state").orElseEmptyMap());
		CompoundTag compoundTag = (CompoundTag)dynamic.get("output_nbt").map(dynamicx -> dynamicx.convert(NbtOps.INSTANCE).getValue()).orElse(null);
		return new StructureProcessorRule(ruleTest, ruleTest2, posRuleTest, blockState, compoundTag);
	}
}
