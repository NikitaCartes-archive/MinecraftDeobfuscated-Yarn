package net.minecraft.sortme.structures.processor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.class_3492;
import net.minecraft.class_3499;
import net.minecraft.class_3821;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ViewableWorld;

public class RuleStructureProcessor extends AbstractStructureProcessor {
	private final ImmutableList<class_3821> rules;

	public RuleStructureProcessor(ImmutableList<class_3821> immutableList) {
		this.rules = immutableList;
	}

	public RuleStructureProcessor(Dynamic<?> dynamic) {
		this(
			(ImmutableList<class_3821>)((Stream)dynamic.get("rules").flatMap(Dynamic::getStream).orElse(Stream.empty()))
				.map(class_3821::method_16765)
				.collect(ImmutableList.toImmutableList())
		);
	}

	@Nullable
	@Override
	public class_3499.class_3501 process(ViewableWorld viewableWorld, BlockPos blockPos, class_3499.class_3501 arg, class_3499.class_3501 arg2, class_3492 arg3) {
		Random random = new Random(MathHelper.hashCode(arg2.field_15597));
		BlockState blockState = viewableWorld.getBlockState(arg2.field_15597);

		for (class_3821 lv : this.rules) {
			if (lv.method_16762(arg2.field_15596, blockState, random)) {
				return new class_3499.class_3501(arg2.field_15597, lv.method_16763(), lv.method_16760());
			}
		}

		return arg2;
	}

	@Override
	protected StructureProcessor getStructureProcessor() {
		return StructureProcessor.field_16990;
	}

	@Override
	protected <T> Dynamic<T> method_16666(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(dynamicOps.createString("rules"), dynamicOps.createList(this.rules.stream().map(arg -> arg.method_16764(dynamicOps).getValue())))
			)
		);
	}
}
