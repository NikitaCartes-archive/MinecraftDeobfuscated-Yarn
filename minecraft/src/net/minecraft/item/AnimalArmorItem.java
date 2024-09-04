package net.minecraft.item;

import net.minecraft.entity.EntityType;
import net.minecraft.item.equipment.ArmorMaterial;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class AnimalArmorItem extends Item {
	private final AnimalArmorItem.Type type;

	public AnimalArmorItem(ArmorMaterial material, AnimalArmorItem.Type type, Item.Settings settings) {
		super(material.applyBodyArmorSettings(settings, type.allowedEntities));
		this.type = type;
	}

	@Override
	public SoundEvent getBreakSound() {
		return this.type.breakSound;
	}

	public static enum Type {
		EQUESTRIAN(SoundEvents.ENTITY_ITEM_BREAK, EntityType.HORSE),
		CANINE(SoundEvents.ITEM_WOLF_ARMOR_BREAK, EntityType.WOLF);

		final SoundEvent breakSound;
		final RegistryEntryList<EntityType<?>> allowedEntities;

		private Type(final SoundEvent breakSound, final EntityType<?>... allowedEntities) {
			this.breakSound = breakSound;
			this.allowedEntities = RegistryEntryList.of(EntityType::getRegistryEntry, allowedEntities);
		}
	}
}
