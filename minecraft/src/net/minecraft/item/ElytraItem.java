package net.minecraft.item;

import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class ElytraItem extends Item implements Equipment {
	public ElytraItem(Item.Settings settings) {
		super(settings);
		DispenserBlock.registerBehavior(this, ArmorItem.DISPENSER_BEHAVIOR);
	}

	public static boolean isUsable(ItemStack stack) {
		return stack.getDamage() < stack.getMaxDamage() - 1;
	}

	@Override
	public boolean canRepair(ItemStack stack, ItemStack ingredient) {
		return ingredient.isOf(Items.PHANTOM_MEMBRANE);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		return this.equipAndSwap(this, world, user, hand);
	}

	@Override
	public RegistryEntry<SoundEvent> getEquipSound() {
		return SoundEvents.ITEM_ARMOR_EQUIP_ELYTRA;
	}

	@Override
	public EquipmentSlot getSlotType() {
		return EquipmentSlot.CHEST;
	}
}
