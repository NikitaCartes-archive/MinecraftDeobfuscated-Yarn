package net.minecraft.item;

import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class ElytraItem extends Item {
	public ElytraItem(Item.Settings settings) {
		super(settings);
		this.addPropertyGetter(new Identifier("broken"), (itemStack, world, livingEntity) -> isUsable(itemStack) ? 0.0F : 1.0F);
		DispenserBlock.registerBehavior(this, ArmorItem.DISPENSER_BEHAVIOR);
	}

	public static boolean isUsable(ItemStack itemStack) {
		return itemStack.getDamage() < itemStack.getMaxDamage() - 1;
	}

	@Override
	public boolean canRepair(ItemStack itemStack, ItemStack itemStack2) {
		return itemStack2.getItem() == Items.PHANTOM_MEMBRANE;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		EquipmentSlot equipmentSlot = MobEntity.getPreferredEquipmentSlot(itemStack);
		ItemStack itemStack2 = playerEntity.getEquippedStack(equipmentSlot);
		if (itemStack2.isEmpty()) {
			playerEntity.equipStack(equipmentSlot, itemStack.copy());
			itemStack.setCount(0);
			return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
		} else {
			return new TypedActionResult<>(ActionResult.FAIL, itemStack);
		}
	}
}
