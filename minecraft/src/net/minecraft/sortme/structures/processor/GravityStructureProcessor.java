package net.minecraft.sortme.structures.processor;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import javax.annotation.Nullable;
import net.minecraft.class_3492;
import net.minecraft.class_3499;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.gen.Heightmap;

public class GravityStructureProcessor extends AbstractStructureProcessor {
	private final Heightmap.Type heightmap;
	private final int offset;

	public GravityStructureProcessor(Heightmap.Type type, int i) {
		this.heightmap = type;
		this.offset = i;
	}

	public GravityStructureProcessor(Dynamic<?> dynamic) {
		this(Heightmap.Type.byName(dynamic.get("heightmap").asString(Heightmap.Type.WORLD_SURFACE_WG.getName())), dynamic.get("offset").asInt(0));
	}

	@Nullable
	@Override
	public class_3499.class_3501 process(ViewableWorld viewableWorld, BlockPos blockPos, class_3499.class_3501 arg, class_3499.class_3501 arg2, class_3492 arg3) {
		int i = viewableWorld.getTop(this.heightmap, arg2.field_15597.getX(), arg2.field_15597.getZ()) + this.offset;
		int j = arg.field_15597.getY();
		return new class_3499.class_3501(new BlockPos(arg2.field_15597.getX(), i + j, arg2.field_15597.getZ()), arg2.field_15596, arg2.field_15595);
	}

	@Override
	protected StructureProcessor getStructureProcessor() {
		return StructureProcessor.field_16989;
	}

	@Override
	protected <T> Dynamic<T> method_16666(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("heightmap"),
					dynamicOps.createString(this.heightmap.getName()),
					dynamicOps.createString("offset"),
					dynamicOps.createInt(this.offset)
				)
			)
		);
	}
}
