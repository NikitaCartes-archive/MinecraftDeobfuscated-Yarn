package net.minecraft.util;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;

public class MaterialPredicate implements Predicate<BlockState> {
	private static final MaterialPredicate IS_AIR = new MaterialPredicate(Material.AIR) {
		@Override
		public boolean method_11745(@Nullable BlockState blockState) {
			return blockState != null && blockState.isAir();
		}
	};
	private final Material field_12405;

	private MaterialPredicate(Material material) {
		this.field_12405 = material;
	}

	public static MaterialPredicate method_11746(Material material) {
		return material == Material.AIR ? IS_AIR : new MaterialPredicate(material);
	}

	public boolean method_11745(@Nullable BlockState blockState) {
		return blockState != null && blockState.method_11620() == this.field_12405;
	}
}
