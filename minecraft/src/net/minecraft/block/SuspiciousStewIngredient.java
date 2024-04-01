package net.minecraft.block;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.component.type.SuspiciousStewEffectsComponent;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Util;
import net.minecraft.util.math.random.Random;

public interface SuspiciousStewIngredient {
	Random field_50896 = Random.create();
	List<RegistryEntry<StatusEffect>> field_50897 = List.of(
		StatusEffects.MINING_FATIGUE,
		StatusEffects.HASTE,
		StatusEffects.POTATO_OIL,
		StatusEffects.LUCK,
		StatusEffects.UNLUCK,
		StatusEffects.SLOW_FALLING,
		StatusEffects.HERO_OF_THE_VILLAGE,
		StatusEffects.GLOWING
	);

	SuspiciousStewEffectsComponent getStewEffects();

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
		if (suspiciousStewIngredient instanceof SuspiciousStewIngredient) {
			return (SuspiciousStewIngredient)suspiciousStewIngredient;
		} else {
			return item.asItem() == Items.POISONOUS_POTATO
				? () -> new SuspiciousStewEffectsComponent(
						List.of(new SuspiciousStewEffectsComponent.StewEffect(Util.getRandom(field_50897, field_50896), field_50896.nextInt(60)))
					)
				: null;
		}
	}
}
