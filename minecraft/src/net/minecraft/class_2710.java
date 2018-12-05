package net.minecraft;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;

public class class_2710 implements Predicate<BlockState> {
	private static final class_2710 field_12404 = new class_2710(Material.AIR) {
		@Override
		public boolean method_11745(@Nullable BlockState blockState) {
			return blockState != null && blockState.isAir();
		}
	};
	private final Material field_12405;

	private class_2710(Material material) {
		this.field_12405 = material;
	}

	public static class_2710 method_11746(Material material) {
		return material == Material.AIR ? field_12404 : new class_2710(material);
	}

	public boolean method_11745(@Nullable BlockState blockState) {
		return blockState != null && blockState.getMaterial() == this.field_12405;
	}
}
