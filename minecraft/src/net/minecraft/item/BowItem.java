package net.minecraft.item;

import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.consume.UseAction;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class BowItem extends RangedWeaponItem {
	public static final int TICKS_PER_SECOND = 20;
	public static final int RANGE = 15;

	public BowItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
		if (user instanceof PlayerEntity playerEntity) {
			ItemStack itemStack = playerEntity.getProjectileType(stack);
			if (!itemStack.isEmpty()) {
				int i = this.getMaxUseTime(stack, user) - remainingUseTicks;
				float f = getPullProgress(i);
				if (!((double)f < 0.1)) {
					List<ItemStack> list = load(stack, itemStack, playerEntity);
					if (world instanceof ServerWorld serverWorld && !list.isEmpty()) {
						this.shootAll(serverWorld, playerEntity, playerEntity.getActiveHand(), stack, list, f * 3.0F, 1.0F, f == 1.0F, null);
					}

					world.playSound(
						null,
						playerEntity.getX(),
						playerEntity.getY(),
						playerEntity.getZ(),
						SoundEvents.ENTITY_ARROW_SHOOT,
						SoundCategory.PLAYERS,
						1.0F,
						1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F) + f * 0.5F
					);
					playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
				}
			}
		}
	}

	@Override
	protected void shoot(LivingEntity shooter, ProjectileEntity projectile, int index, float speed, float divergence, float yaw, @Nullable LivingEntity target) {
		projectile.setVelocity(shooter, shooter.getPitch(), shooter.getYaw() + yaw, 0.0F, speed, divergence);
	}

	public static float getPullProgress(int useTicks) {
		float f = (float)useTicks / 20.0F;
		f = (f * f + f * 2.0F) / 3.0F;
		if (f > 1.0F) {
			f = 1.0F;
		}

		return f;
	}

	@Override
	public int getMaxUseTime(ItemStack stack, LivingEntity user) {
		return 72000;
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.BOW;
	}

	@Override
	public ActionResult use(World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = user.getStackInHand(hand);
		boolean bl = !user.getProjectileType(itemStack).isEmpty();
		if (!user.isInCreativeMode() && !bl) {
			return ActionResult.FAIL;
		} else {
			user.setCurrentHand(hand);
			return ActionResult.CONSUME;
		}
	}

	@Override
	public Predicate<ItemStack> getProjectiles() {
		return BOW_PROJECTILES;
	}

	@Override
	public int getRange() {
		return 15;
	}
}
