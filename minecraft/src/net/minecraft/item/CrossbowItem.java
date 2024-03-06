package net.minecraft.item;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class CrossbowItem extends RangedWeaponItem {
	private static final int DEFAULT_PULL_TIME = 25;
	public static final int RANGE = 8;
	private boolean charged = false;
	private boolean loaded = false;
	private static final float CHARGE_PROGRESS = 0.2F;
	private static final float LOAD_PROGRESS = 0.5F;
	private static final float DEFAULT_SPEED = 3.15F;
	private static final float FIREWORK_ROCKET_SPEED = 1.6F;
	public static final float field_49258 = 1.6F;

	public CrossbowItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public Predicate<ItemStack> getHeldProjectiles() {
		return CROSSBOW_HELD_PROJECTILES;
	}

	@Override
	public Predicate<ItemStack> getProjectiles() {
		return BOW_PROJECTILES;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = user.getStackInHand(hand);
		ChargedProjectilesComponent chargedProjectilesComponent = itemStack.get(DataComponentTypes.CHARGED_PROJECTILES);
		if (chargedProjectilesComponent != null && !chargedProjectilesComponent.isEmpty()) {
			this.shootAll(world, user, hand, itemStack, getSpeed(chargedProjectilesComponent), 1.0F, null);
			return TypedActionResult.consume(itemStack);
		} else if (!user.getProjectileType(itemStack).isEmpty()) {
			this.charged = false;
			this.loaded = false;
			user.setCurrentHand(hand);
			return TypedActionResult.consume(itemStack);
		} else {
			return TypedActionResult.fail(itemStack);
		}
	}

	private static float getSpeed(ChargedProjectilesComponent stack) {
		return stack.contains(Items.FIREWORK_ROCKET) ? 1.6F : 3.15F;
	}

	@Override
	public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
		int i = this.getMaxUseTime(stack) - remainingUseTicks;
		float f = getPullProgress(i, stack);
		if (f >= 1.0F && !isCharged(stack) && loadProjectiles(user, stack)) {
			world.playSound(
				null,
				user.getX(),
				user.getY(),
				user.getZ(),
				SoundEvents.ITEM_CROSSBOW_LOADING_END,
				user.getSoundCategory(),
				1.0F,
				1.0F / (world.getRandom().nextFloat() * 0.5F + 1.0F) + 0.2F
			);
		}
	}

	private static boolean loadProjectiles(LivingEntity shooter, ItemStack crossbow) {
		List<ItemStack> list = load(crossbow, shooter.getProjectileType(crossbow), shooter);
		if (!list.isEmpty()) {
			crossbow.set(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectilesComponent.of(list));
			return true;
		} else {
			return false;
		}
	}

	public static boolean isCharged(ItemStack stack) {
		ChargedProjectilesComponent chargedProjectilesComponent = stack.getOrDefault(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectilesComponent.DEFAULT);
		return !chargedProjectilesComponent.isEmpty();
	}

	@Override
	protected void shoot(LivingEntity shooter, ProjectileEntity projectile, int index, float speed, float divergence, float yaw, @Nullable LivingEntity target) {
		Vector3f vector3f;
		if (target != null) {
			double d = target.getX() - shooter.getX();
			double e = target.getZ() - shooter.getZ();
			double f = Math.sqrt(d * d + e * e);
			double g = target.getBodyY(0.3333333333333333) - projectile.getY() + f * 0.2F;
			vector3f = calcVelocity(shooter, new Vec3d(d, g, e), yaw);
		} else {
			Vec3d vec3d = shooter.getOppositeRotationVector(1.0F);
			Quaternionf quaternionf = new Quaternionf().setAngleAxis((double)(yaw * (float) (Math.PI / 180.0)), vec3d.x, vec3d.y, vec3d.z);
			Vec3d vec3d2 = shooter.getRotationVec(1.0F);
			vector3f = vec3d2.toVector3f().rotate(quaternionf);
		}

		projectile.setVelocity((double)vector3f.x(), (double)vector3f.y(), (double)vector3f.z(), speed, divergence);
		float h = getSoundPitch(shooter.getRandom(), index);
		shooter.getWorld().playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(), SoundEvents.ITEM_CROSSBOW_SHOOT, shooter.getSoundCategory(), 1.0F, h);
	}

	private static Vector3f calcVelocity(LivingEntity shooter, Vec3d direction, float yaw) {
		Vector3f vector3f = direction.toVector3f().normalize();
		Vector3f vector3f2 = new Vector3f(vector3f).cross(new Vector3f(0.0F, 1.0F, 0.0F));
		if ((double)vector3f2.lengthSquared() <= 1.0E-7) {
			Vec3d vec3d = shooter.getOppositeRotationVector(1.0F);
			vector3f2 = new Vector3f(vector3f).cross(vec3d.toVector3f());
		}

		Vector3f vector3f3 = new Vector3f(vector3f).rotateAxis((float) (Math.PI / 2), vector3f2.x, vector3f2.y, vector3f2.z);
		return new Vector3f(vector3f).rotateAxis(yaw * (float) (Math.PI / 180.0), vector3f3.x, vector3f3.y, vector3f3.z);
	}

	@Override
	protected ProjectileEntity createArrowEntity(World world, LivingEntity shooter, ItemStack weaponStack, ItemStack projectileStack, boolean critical) {
		if (projectileStack.isOf(Items.FIREWORK_ROCKET)) {
			return new FireworkRocketEntity(world, projectileStack, shooter, shooter.getX(), shooter.getEyeY() - 0.15F, shooter.getZ(), true);
		} else {
			ProjectileEntity projectileEntity = super.createArrowEntity(world, shooter, weaponStack, projectileStack, critical);
			if (projectileEntity instanceof PersistentProjectileEntity persistentProjectileEntity) {
				persistentProjectileEntity.setShotFromCrossbow(true);
				persistentProjectileEntity.setSound(SoundEvents.ITEM_CROSSBOW_HIT);
			}

			return projectileEntity;
		}
	}

	@Override
	protected int getWeaponStackDamage(ItemStack projectile) {
		return projectile.isOf(Items.FIREWORK_ROCKET) ? 3 : 1;
	}

	public void shootAll(World world, LivingEntity shooter, Hand hand, ItemStack stack, float speed, float divergence, @Nullable LivingEntity livingEntity) {
		if (!world.isClient()) {
			ChargedProjectilesComponent chargedProjectilesComponent = stack.set(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectilesComponent.DEFAULT);
			if (chargedProjectilesComponent != null && !chargedProjectilesComponent.isEmpty()) {
				this.shootAll(world, shooter, hand, stack, chargedProjectilesComponent.getProjectiles(), speed, divergence, shooter instanceof PlayerEntity, livingEntity);
				if (shooter instanceof ServerPlayerEntity serverPlayerEntity) {
					Criteria.SHOT_CROSSBOW.trigger(serverPlayerEntity, stack);
					serverPlayerEntity.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
				}
			}
		}
	}

	private static float getSoundPitch(Random random, int index) {
		return index == 0 ? 1.0F : getSoundPitch((index & 1) == 1, random);
	}

	private static float getSoundPitch(boolean flag, Random random) {
		float f = flag ? 0.63F : 0.43F;
		return 1.0F / (random.nextFloat() * 0.5F + 1.8F) + f;
	}

	@Override
	public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
		if (!world.isClient) {
			int i = EnchantmentHelper.getLevel(Enchantments.QUICK_CHARGE, stack);
			SoundEvent soundEvent = this.getQuickChargeSound(i);
			SoundEvent soundEvent2 = i == 0 ? SoundEvents.ITEM_CROSSBOW_LOADING_MIDDLE : null;
			float f = (float)(stack.getMaxUseTime() - remainingUseTicks) / (float)getPullTime(stack);
			if (f < 0.2F) {
				this.charged = false;
				this.loaded = false;
			}

			if (f >= 0.2F && !this.charged) {
				this.charged = true;
				world.playSound(null, user.getX(), user.getY(), user.getZ(), soundEvent, SoundCategory.PLAYERS, 0.5F, 1.0F);
			}

			if (f >= 0.5F && soundEvent2 != null && !this.loaded) {
				this.loaded = true;
				world.playSound(null, user.getX(), user.getY(), user.getZ(), soundEvent2, SoundCategory.PLAYERS, 0.5F, 1.0F);
			}
		}
	}

	@Override
	public int getMaxUseTime(ItemStack stack) {
		return getPullTime(stack) + 3;
	}

	public static int getPullTime(ItemStack stack) {
		int i = EnchantmentHelper.getLevel(Enchantments.QUICK_CHARGE, stack);
		return i == 0 ? 25 : 25 - 5 * i;
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.CROSSBOW;
	}

	private SoundEvent getQuickChargeSound(int stage) {
		switch (stage) {
			case 1:
				return SoundEvents.ITEM_CROSSBOW_QUICK_CHARGE_1;
			case 2:
				return SoundEvents.ITEM_CROSSBOW_QUICK_CHARGE_2;
			case 3:
				return SoundEvents.ITEM_CROSSBOW_QUICK_CHARGE_3;
			default:
				return SoundEvents.ITEM_CROSSBOW_LOADING_START;
		}
	}

	private static float getPullProgress(int useTicks, ItemStack stack) {
		float f = (float)useTicks / (float)getPullTime(stack);
		if (f > 1.0F) {
			f = 1.0F;
		}

		return f;
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		ChargedProjectilesComponent chargedProjectilesComponent = stack.get(DataComponentTypes.CHARGED_PROJECTILES);
		if (chargedProjectilesComponent != null && !chargedProjectilesComponent.isEmpty()) {
			ItemStack itemStack = (ItemStack)chargedProjectilesComponent.getProjectiles().get(0);
			tooltip.add(Text.translatable("item.minecraft.crossbow.projectile").append(ScreenTexts.SPACE).append(itemStack.toHoverableText()));
			if (context.isAdvanced() && itemStack.isOf(Items.FIREWORK_ROCKET)) {
				List<Text> list = Lists.<Text>newArrayList();
				Items.FIREWORK_ROCKET.appendTooltip(itemStack, world, list, context);
				if (!list.isEmpty()) {
					for (int i = 0; i < list.size(); i++) {
						list.set(i, Text.literal("  ").append((Text)list.get(i)).formatted(Formatting.GRAY));
					}

					tooltip.addAll(list);
				}
			}
		}
	}

	@Override
	public boolean isUsedOnRelease(ItemStack stack) {
		return stack.isOf(this);
	}

	@Override
	public int getRange() {
		return 8;
	}
}
