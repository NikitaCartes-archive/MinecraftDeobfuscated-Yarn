package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class ElytraItem extends Item implements Wearable {
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
		ItemStack itemStack = user.getStackInHand(hand);
		EquipmentSlot equipmentSlot = MobEntity.getPreferredEquipmentSlot(itemStack);
		ItemStack itemStack2 = user.getEquippedStack(equipmentSlot);
		if (itemStack2.isEmpty()) {
			user.equipStack(equipmentSlot, itemStack.copy());
			if (!world.isClient()) {
				user.incrementStat(Stats.USED.getOrCreateStat(this));
			}

			itemStack.setCount(0);
			return TypedActionResult.success(itemStack, world.isClient());
		} else {
			return TypedActionResult.fail(itemStack);
		}
	}

	@Nullable
	@Override
	public SoundEvent getEquipSound() {
		return SoundEvents.ITEM_ARMOR_EQUIP_ELYTRA;
	}
}
