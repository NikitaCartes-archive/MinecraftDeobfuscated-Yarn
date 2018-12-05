package net.minecraft.sortme.structures.processor;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import javax.annotation.Nullable;
import net.minecraft.class_3492;
import net.minecraft.class_3499;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ViewableWorld;

public abstract class AbstractStructureProcessor {
	@Nullable
	public abstract class_3499.class_3501 process(
		ViewableWorld viewableWorld, BlockPos blockPos, class_3499.class_3501 arg, class_3499.class_3501 arg2, class_3492 arg3
	);

	protected abstract StructureProcessor getStructureProcessor();

	protected abstract <T> Dynamic<T> method_16666(DynamicOps<T> dynamicOps);

	public <T> Dynamic<T> method_16771(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.mergeInto(
				this.method_16666(dynamicOps).getValue(),
				dynamicOps.createString("processor_type"),
				dynamicOps.createString(Registry.STRUCTURE_PROCESSOR.getId(this.getStructureProcessor()).toString())
			)
		);
	}
}
