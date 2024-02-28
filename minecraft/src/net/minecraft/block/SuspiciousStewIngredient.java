package net.minecraft.block;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.component.type.SuspiciousStewEffectsComponent;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.Registries;

public interface SuspiciousStewIngredient {
	SuspiciousStewEffectsComponent getStewEffects();

	static List<SuspiciousStewIngredient> getAll() {
		return (List<SuspiciousStewIngredient>)Registries.ITEM.stream().map(SuspiciousStewIngredient::of).filter(Objects::nonNull).collect(Collectors.toList());
	}

	@Nullable
	static SuspiciousStewIngredient of(ItemConvertible item) {
		Item var3 = item.asItem();
		if (var3 instanceof BlockItem blockItem) {
			Block var6 = blockItem.getBlock();
			if (var6 instanceof SuspiciousStewIngredient suspiciousStewIngredient) {
				return suspiciousStewIngredient;
			}
		}

		Item suspiciousStewIngredient = item.asItem();
		return suspiciousStewIngredient instanceof SuspiciousStewIngredient suspiciousStewIngredient2 ? suspiciousStewIngredient2 : null;
	}
}
