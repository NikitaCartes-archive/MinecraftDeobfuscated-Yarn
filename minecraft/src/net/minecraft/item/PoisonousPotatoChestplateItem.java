package net.minecraft.item;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.DyeColor;

public class PoisonousPotatoChestplateItem extends ArmorItem {
	public PoisonousPotatoChestplateItem(RegistryEntry<ArmorMaterial> registryEntry, ArmorItem.Type type, Item.Settings settings) {
		super(registryEntry, type, settings);
	}

	@Override
	public SoundEvent getBreakSound() {
		return SoundEvents.ENTITY_SLIME_SQUISH;
	}

	public static Item getPeels(ItemStack stack) {
		DyeColor dyeColor = stack.get(DataComponentTypes.BASE_COLOR);
		return Items.POTATO_PEELS.get(dyeColor != null ? dyeColor : DyeColor.WHITE);
	}
}
