package net.minecraft;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.sortme.StructurePoolElement;
import net.minecraft.sortme.structures.StructureManager;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.IWorld;

public class class_3777 extends class_3784 {
	public static final class_3777 field_16663 = new class_3777();

	private class_3777() {
	}

	@Override
	public List<class_3499.class_3501> method_16627(StructureManager structureManager, BlockPos blockPos, Rotation rotation, Random random) {
		return Collections.emptyList();
	}

	@Override
	public MutableIntBoundingBox method_16628(StructureManager structureManager, BlockPos blockPos, Rotation rotation) {
		return MutableIntBoundingBox.maxSize();
	}

	@Override
	public boolean method_16626(IWorld iWorld, BlockPos blockPos, Rotation rotation, MutableIntBoundingBox mutableIntBoundingBox, Random random) {
		return true;
	}

	@Override
	public StructurePoolElement method_16757() {
		return StructurePoolElement.field_16972;
	}

	@Override
	public <T> Dynamic<T> method_16625(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(dynamicOps, dynamicOps.emptyMap());
	}
}
