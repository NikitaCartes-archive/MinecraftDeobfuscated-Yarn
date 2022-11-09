package net.minecraft.block;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.Registries;

public interface SuspiciousStewIngredient {
	StatusEffect getEffectInStew();

	int getEffectInStewDuration();

	static List<SuspiciousStewIngredient> getAll() {
		return (List<SuspiciousStewIngredient>)Registries.ITEM.stream().map(SuspiciousStewIngredient::of).filter(Objects::nonNull).collect(Collectors.toList());
	}

	@Nullable
	static SuspiciousStewIngredient of(ItemConvertible item) {
		if (item.asItem() instanceof BlockItem blockItem) {
			Block var6 = blockItem.getBlock();
			if (var6 instanceof SuspiciousStewIngredient) {
				return (SuspiciousStewIngredient)var6;
			}
		}

		Item suspiciousStewIngredient = item.asItem();
		return suspiciousStewIngredient instanceof SuspiciousStewIngredient ? (SuspiciousStewIngredient)suspiciousStewIngredient : null;
	}
}
