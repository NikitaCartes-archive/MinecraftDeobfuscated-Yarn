package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.datafixers.NbtOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sortme.rule.AbstractRuleTest;
import net.minecraft.sortme.rule.AlwaysTrueRuleTest;
import net.minecraft.util.registry.Registry;

public class class_3821 {
	private final AbstractRuleTest field_16872;
	private final AbstractRuleTest field_16873;
	private final BlockState field_16874;
	@Nullable
	private final CompoundTag field_16875;

	public class_3821(AbstractRuleTest abstractRuleTest, AbstractRuleTest abstractRuleTest2, BlockState blockState) {
		this(abstractRuleTest, abstractRuleTest2, blockState, null);
	}

	public class_3821(AbstractRuleTest abstractRuleTest, AbstractRuleTest abstractRuleTest2, BlockState blockState, @Nullable CompoundTag compoundTag) {
		this.field_16872 = abstractRuleTest;
		this.field_16873 = abstractRuleTest2;
		this.field_16874 = blockState;
		this.field_16875 = compoundTag;
	}

	public boolean method_16762(BlockState blockState, BlockState blockState2, Random random) {
		return this.field_16872.test(blockState, random) && this.field_16873.test(blockState2, random);
	}

	public BlockState method_16763() {
		return this.field_16874;
	}

	@Nullable
	public CompoundTag method_16760() {
		return this.field_16875;
	}

	public <T> Dynamic<T> method_16764(DynamicOps<T> dynamicOps) {
		T object = dynamicOps.createMap(
			ImmutableMap.of(
				dynamicOps.createString("input_predicate"),
				this.field_16872.method_16767(dynamicOps).getValue(),
				dynamicOps.createString("location_predicate"),
				this.field_16873.method_16767(dynamicOps).getValue(),
				dynamicOps.createString("output_state"),
				BlockState.serialize(dynamicOps, this.field_16874).getValue()
			)
		);
		return this.field_16875 == null
			? new Dynamic<>(dynamicOps, object)
			: new Dynamic<>(
				dynamicOps,
				dynamicOps.mergeInto(object, dynamicOps.createString("output_nbt"), new Dynamic<>(NbtOps.INSTANCE, this.field_16875).convert(dynamicOps).getValue())
			);
	}

	public static <T> class_3821 method_16765(Dynamic<T> dynamic) {
		Dynamic<T> dynamic2 = (Dynamic<T>)dynamic.get("input_predicate").orElse(dynamic.emptyMap());
		Dynamic<T> dynamic3 = (Dynamic<T>)dynamic.get("location_predicate").orElse(dynamic.emptyMap());
		AbstractRuleTest abstractRuleTest = class_3817.deserialize(dynamic2, Registry.RULE_TEST, "predicate_type", AlwaysTrueRuleTest.INSTANCE);
		AbstractRuleTest abstractRuleTest2 = class_3817.deserialize(dynamic3, Registry.RULE_TEST, "predicate_type", AlwaysTrueRuleTest.INSTANCE);
		BlockState blockState = BlockState.deserialize((Dynamic<T>)dynamic.get("output_state").orElse(dynamic.emptyMap()));
		CompoundTag compoundTag = (CompoundTag)dynamic.get("output_nbt").map(dynamicx -> dynamicx.convert(NbtOps.INSTANCE).getValue()).orElse(null);
		return new class_3821(abstractRuleTest, abstractRuleTest2, blockState, compoundTag);
	}
}
