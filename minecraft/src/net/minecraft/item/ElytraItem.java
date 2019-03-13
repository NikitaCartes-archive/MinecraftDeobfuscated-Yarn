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
		this.method_7863(new Identifier("broken"), (itemStack, world, livingEntity) -> method_7804(itemStack) ? 0.0F : 1.0F);
		DispenserBlock.method_10009(this, ArmorItem.field_7879);
	}

	public static boolean method_7804(ItemStack itemStack) {
		return itemStack.getDamage() < itemStack.getDurability() - 1;
	}

	@Override
	public boolean method_7878(ItemStack itemStack, ItemStack itemStack2) {
		return itemStack2.getItem() == Items.field_8614;
	}

	@Override
	public TypedActionResult<ItemStack> method_7836(World world, PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.method_5998(hand);
		EquipmentSlot equipmentSlot = MobEntity.method_5953(itemStack);
		ItemStack itemStack2 = playerEntity.method_6118(equipmentSlot);
		if (itemStack2.isEmpty()) {
			playerEntity.method_5673(equipmentSlot, itemStack.copy());
			itemStack.setAmount(0);
			return new TypedActionResult<>(ActionResult.field_5812, itemStack);
		} else {
			return new TypedActionResult<>(ActionResult.field_5814, itemStack);
		}
	}
}
