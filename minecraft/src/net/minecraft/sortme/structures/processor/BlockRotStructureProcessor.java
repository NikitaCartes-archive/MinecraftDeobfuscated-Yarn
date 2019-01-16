package net.minecraft.sortme.structures.processor;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.class_3492;
import net.minecraft.class_3499;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ViewableWorld;

public class BlockRotStructureProcessor extends AbstractStructureProcessor {
	private final float integrity;

	public BlockRotStructureProcessor(float f) {
		this.integrity = f;
	}

	public BlockRotStructureProcessor(Dynamic<?> dynamic) {
		this(dynamic.get("integrity").asFloat(1.0F));
	}

	@Nullable
	@Override
	public class_3499.class_3501 process(ViewableWorld viewableWorld, BlockPos blockPos, class_3499.class_3501 arg, class_3499.class_3501 arg2, class_3492 arg3) {
		Random random = new Random(MathHelper.hashCode(arg2.field_15597));
		return !(this.integrity >= 1.0F) && !(random.nextFloat() <= this.integrity) ? null : arg2;
	}

	@Override
	protected StructureProcessor getStructureProcessor() {
		return StructureProcessor.field_16988;
	}

	@Override
	protected <T> Dynamic<T> method_16666(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("integrity"), dynamicOps.createFloat(this.integrity))));
	}
}
