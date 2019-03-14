package net.minecraft.item;

import java.util.function.Predicate;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class BowItem extends BaseBowItem {
	public BowItem(Item.Settings settings) {
		super(settings);
		this.addProperty(new Identifier("pull"), (itemStack, world, livingEntity) -> {
			if (livingEntity == null) {
				return 0.0F;
			} else {
				return livingEntity.getActiveItem().getItem() != Items.field_8102 ? 0.0F : (float)(itemStack.getMaxUseTime() - livingEntity.method_6014()) / 20.0F;
			}
		});
		this.addProperty(
			new Identifier("pulling"),
			(itemStack, world, livingEntity) -> livingEntity != null && livingEntity.isUsingItem() && livingEntity.getActiveItem() == itemStack ? 1.0F : 0.0F
		);
	}

	@Override
	public void onItemStopUsing(ItemStack itemStack, World world, LivingEntity livingEntity, int i) {
		if (livingEntity instanceof PlayerEntity) {
			PlayerEntity playerEntity = (PlayerEntity)livingEntity;
			boolean bl = playerEntity.abilities.creativeMode || EnchantmentHelper.getLevel(Enchantments.field_9125, itemStack) > 0;
			ItemStack itemStack2 = playerEntity.method_18808(itemStack);
			if (!itemStack2.isEmpty() || bl) {
				if (itemStack2.isEmpty()) {
					itemStack2 = new ItemStack(Items.field_8107);
				}

				int j = this.getMaxUseTime(itemStack) - i;
				float f = method_7722(j);
				if (!((double)f < 0.1)) {
					boolean bl2 = bl && itemStack2.getItem() == Items.field_8107;
					if (!world.isClient) {
						ArrowItem arrowItem = (ArrowItem)(itemStack2.getItem() instanceof ArrowItem ? itemStack2.getItem() : Items.field_8107);
						ProjectileEntity projectileEntity = arrowItem.createEntityArrow(world, itemStack2, playerEntity);
						projectileEntity.method_7474(playerEntity, playerEntity.pitch, playerEntity.yaw, 0.0F, f * 3.0F, 1.0F);
						if (f == 1.0F) {
							projectileEntity.setCritical(true);
						}

						int k = EnchantmentHelper.getLevel(Enchantments.field_9103, itemStack);
						if (k > 0) {
							projectileEntity.setDamage(projectileEntity.getDamage() + (double)k * 0.5 + 0.5);
						}

						int l = EnchantmentHelper.getLevel(Enchantments.field_9116, itemStack);
						if (l > 0) {
							projectileEntity.method_7449(l);
						}

						if (EnchantmentHelper.getLevel(Enchantments.field_9126, itemStack) > 0) {
							projectileEntity.setOnFireFor(100);
						}

						itemStack.applyDamage(1, playerEntity);
						if (bl2 || playerEntity.abilities.creativeMode && (itemStack2.getItem() == Items.field_8236 || itemStack2.getItem() == Items.field_8087)) {
							projectileEntity.pickupType = ProjectileEntity.PickupType.CREATIVE_PICKUP;
						}

						world.spawnEntity(projectileEntity);
					}

					world.playSound(
						null,
						playerEntity.x,
						playerEntity.y,
						playerEntity.z,
						SoundEvents.field_14600,
						SoundCategory.field_15248,
						1.0F,
						1.0F / (random.nextFloat() * 0.4F + 1.2F) + f * 0.5F
					);
					if (!bl2 && !playerEntity.abilities.creativeMode) {
						itemStack2.subtractAmount(1);
						if (itemStack2.isEmpty()) {
							playerEntity.inventory.removeOne(itemStack2);
						}
					}

					playerEntity.incrementStat(Stats.field_15372.getOrCreateStat(this));
				}
			}
		}
	}

	public static float method_7722(int i) {
		float f = (float)i / 20.0F;
		f = (f * f + f * 2.0F) / 3.0F;
		if (f > 1.0F) {
			f = 1.0F;
		}

		return f;
	}

	@Override
	public int getMaxUseTime(ItemStack itemStack) {
		return 72000;
	}

	@Override
	public UseAction getUseAction(ItemStack itemStack) {
		return UseAction.field_8953;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		boolean bl = !playerEntity.method_18808(itemStack).isEmpty();
		if (playerEntity.abilities.creativeMode || bl) {
			playerEntity.setCurrentHand(hand);
			return new TypedActionResult<>(ActionResult.field_5812, itemStack);
		} else {
			return bl ? new TypedActionResult<>(ActionResult.PASS, itemStack) : new TypedActionResult<>(ActionResult.field_5814, itemStack);
		}
	}

	@Override
	public Predicate<ItemStack> method_19268() {
		return field_18281;
	}
}
