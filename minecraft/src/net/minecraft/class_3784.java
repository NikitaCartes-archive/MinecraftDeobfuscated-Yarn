package net.minecraft;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Random;
import net.minecraft.sortme.StructurePoolElement;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;

public abstract class class_3784 {
	private class_3785.Projection field_16862 = class_3785.Projection.RIGID;

	public abstract List<class_3499.class_3501> method_16627(class_3485 arg, BlockPos blockPos, Rotation rotation, Random random);

	public abstract MutableIntBoundingBox method_16628(class_3485 arg, BlockPos blockPos, Rotation rotation);

	public abstract boolean method_16626(IWorld iWorld, BlockPos blockPos, Rotation rotation, MutableIntBoundingBox mutableIntBoundingBox, Random random);

	public abstract StructurePoolElement method_16757();

	public void method_16756(
		IWorld iWorld, class_3499.class_3501 arg, BlockPos blockPos, Rotation rotation, Random random, MutableIntBoundingBox mutableIntBoundingBox
	) {
	}

	public class_3784 method_16622(class_3785.Projection projection) {
		this.field_16862 = projection;
		return this;
	}

	public class_3785.Projection method_16624() {
		return this.field_16862;
	}

	protected abstract <T> Dynamic<T> method_16625(DynamicOps<T> dynamicOps);

	public <T> Dynamic<T> method_16755(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.mergeInto(
				this.method_16625(dynamicOps).getValue(),
				dynamicOps.createString("element_type"),
				dynamicOps.createString(Registry.STRUCTURE_POOL_ELEMENT.getId(this.method_16757()).toString())
			)
		);
	}
}
