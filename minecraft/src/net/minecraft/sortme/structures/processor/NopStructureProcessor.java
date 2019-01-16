package net.minecraft.sortme.structures.processor;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import javax.annotation.Nullable;
import net.minecraft.class_3492;
import net.minecraft.class_3499;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ViewableWorld;

public class NopStructureProcessor extends AbstractStructureProcessor {
	public static final NopStructureProcessor INSTANCE = new NopStructureProcessor();

	private NopStructureProcessor() {
	}

	@Nullable
	@Override
	public class_3499.class_3501 process(ViewableWorld viewableWorld, BlockPos blockPos, class_3499.class_3501 arg, class_3499.class_3501 arg2, class_3492 arg3) {
		return arg2;
	}

	@Override
	protected StructureProcessor getStructureProcessor() {
		return StructureProcessor.field_16987;
	}

	@Override
	protected <T> Dynamic<T> method_16666(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(dynamicOps, dynamicOps.emptyMap());
	}
}
