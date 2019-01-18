package net.minecraft.sortme.structures.processor;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.sortme.Structure;
import net.minecraft.sortme.StructurePlacementData;
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
	public Structure.StructureBlockInfo process(
		ViewableWorld viewableWorld,
		BlockPos blockPos,
		Structure.StructureBlockInfo structureBlockInfo,
		Structure.StructureBlockInfo structureBlockInfo2,
		StructurePlacementData structurePlacementData
	) {
		Random random = new Random(MathHelper.hashCode(structureBlockInfo2.pos));
		return !(this.integrity >= 1.0F) && !(random.nextFloat() <= this.integrity) ? null : structureBlockInfo2;
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
