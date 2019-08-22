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

public class BowItem extends RangedWeaponItem {
	public BowItem(Item.Settings settings) {
		super(settings);
		this.addPropertyGetter(new Identifier("pull"), (itemStack, world, livingEntity) -> {
			if (livingEntity == null) {
				return 0.0F;
			} else {
				return livingEntity.getActiveItem().getItem() != Items.BOW ? 0.0F : (float)(itemStack.getMaxUseTime() - livingEntity.getItemUseTimeLeft()) / 20.0F;
			}
		});
		this.addPropertyGetter(
			new Identifier("pulling"),
			(itemStack, world, livingEntity) -> livingEntity != null && livingEntity.isUsingItem() && livingEntity.getActiveItem() == itemStack ? 1.0F : 0.0F
		);
	}

	@Override
	public void onStoppedUsing(ItemStack itemStack, World world, LivingEntity livingEntity, int i) {
		if (livingEntity instanceof PlayerEntity) {
			PlayerEntity playerEntity = (PlayerEntity)livingEntity;
			boolean bl = playerEntity.abilities.creativeMode || EnchantmentHelper.getLevel(Enchantments.INFINITY, itemStack) > 0;
			ItemStack itemStack2 = playerEntity.getArrowType(itemStack);
			if (!itemStack2.isEmpty() || bl) {
				if (itemStack2.isEmpty()) {
					itemStack2 = new ItemStack(Items.ARROW);
				}

				int j = this.getMaxUseTime(itemStack) - i;
				float f = getPullProgress(j);
				if (!((double)f < 0.1)) {
					boolean bl2 = bl && itemStack2.getItem() == Items.ARROW;
					if (!world.isClient) {
						ArrowItem arrowItem = (ArrowItem)(itemStack2.getItem() instanceof ArrowItem ? itemStack2.getItem() : Items.ARROW);
						ProjectileEntity projectileEntity = arrowItem.createArrow(world, itemStack2, playerEntity);
						projectileEntity.setProperties(playerEntity, playerEntity.pitch, playerEntity.yaw, 0.0F, f * 3.0F, 1.0F);
						if (f == 1.0F) {
							projectileEntity.setCritical(true);
						}

						int k = EnchantmentHelper.getLevel(Enchantments.POWER, itemStack);
						if (k > 0) {
							projectileEntity.setDamage(projectileEntity.getDamage() + (double)k * 0.5 + 0.5);
						}

						int l = EnchantmentHelper.getLevel(Enchantments.PUNCH, itemStack);
						if (l > 0) {
							projectileEntity.method_7449(l);
						}

						if (EnchantmentHelper.getLevel(Enchantments.FLAME, itemStack) > 0) {
							projectileEntity.setOnFireFor(100);
						}

						itemStack.damage(1, playerEntity, playerEntity2 -> playerEntity2.sendToolBreakStatus(playerEntity.getActiveHand()));
						if (bl2 || playerEntity.abilities.creativeMode && (itemStack2.getItem() == Items.SPECTRAL_ARROW || itemStack2.getItem() == Items.TIPPED_ARROW)) {
							projectileEntity.pickupType = ProjectileEntity.PickupPermission.CREATIVE_ONLY;
						}

						world.spawnEntity(projectileEntity);
					}

					world.playSound(
						null,
						playerEntity.x,
						playerEntity.y,
						playerEntity.z,
						SoundEvents.ENTITY_ARROW_SHOOT,
						SoundCategory.PLAYERS,
						1.0F,
						1.0F / (RANDOM.nextFloat() * 0.4F + 1.2F) + f * 0.5F
					);
					if (!bl2 && !playerEntity.abilities.creativeMode) {
						itemStack2.decrement(1);
						if (itemStack2.isEmpty()) {
							playerEntity.inventory.removeOne(itemStack2);
						}
					}

					playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
				}
			}
		}
	}

	public static float getPullProgress(int i) {
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
		return UseAction.BOW;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		boolean bl = !playerEntity.getArrowType(itemStack).isEmpty();
		if (playerEntity.abilities.creativeMode || bl) {
			playerEntity.setCurrentHand(hand);
			return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
		} else {
			return bl ? new TypedActionResult<>(ActionResult.PASS, itemStack) : new TypedActionResult<>(ActionResult.FAIL, itemStack);
		}
	}

	@Override
	public Predicate<ItemStack> getProjectiles() {
		return BOW_PROJECTILES;
	}
}
