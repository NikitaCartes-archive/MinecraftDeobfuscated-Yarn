package net.minecraft.item;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.CrossbowUser;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class CrossbowItem extends RangedWeaponItem implements Vanishable {
	private boolean charged = false;
	private boolean loaded = false;

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
		if (isCharged(itemStack)) {
			shootAll(world, user, hand, itemStack, getSpeed(itemStack), 0.25F);
			setCharged(itemStack, false);
			return TypedActionResult.consume(itemStack);
		} else if (!user.getArrowType(itemStack).isEmpty()) {
			if (!isCharged(itemStack)) {
				this.charged = false;
				this.loaded = false;
				user.setCurrentHand(hand);
			}

			return TypedActionResult.consume(itemStack);
		} else {
			return TypedActionResult.fail(itemStack);
		}
	}

	@Override
	public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
		int i = this.getMaxUseTime(stack) - remainingUseTicks;
		float f = getPullProgress(i, stack);
		if (f >= 1.0F && !isCharged(stack) && loadProjectiles(user, stack)) {
			setCharged(stack, true);
			SoundCategory soundCategory = user instanceof PlayerEntity ? SoundCategory.PLAYERS : SoundCategory.HOSTILE;
			world.playSound(
				null, user.getX(), user.getY(), user.getZ(), SoundEvents.ITEM_CROSSBOW_LOADING_END, soundCategory, 1.0F, 1.0F / (RANDOM.nextFloat() * 0.5F + 1.0F) + 0.2F
			);
		}
	}

	private static boolean loadProjectiles(LivingEntity shooter, ItemStack projectile) {
		int i = EnchantmentHelper.getLevel(Enchantments.MULTISHOT, projectile);
		int j = i == 0 ? 1 : 3;
		boolean bl = shooter instanceof PlayerEntity && ((PlayerEntity)shooter).abilities.creativeMode;
		ItemStack itemStack = shooter.getArrowType(projectile);
		ItemStack itemStack2 = itemStack.copy();

		for (int k = 0; k < j; k++) {
			if (k > 0) {
				itemStack = itemStack2.copy();
			}

			if (itemStack.isEmpty() && bl) {
				itemStack = new ItemStack(Items.ARROW);
				itemStack2 = itemStack.copy();
			}

			if (!loadProjectile(shooter, projectile, itemStack, k > 0, bl)) {
				return false;
			}
		}

		return true;
	}

	private static boolean loadProjectile(LivingEntity shooter, ItemStack crossbow, ItemStack projectile, boolean simulated, boolean creative) {
		if (projectile.isEmpty()) {
			return false;
		} else {
			boolean bl = creative && projectile.getItem() instanceof ArrowItem;
			ItemStack itemStack;
			if (!bl && !creative && !simulated) {
				itemStack = projectile.split(1);
				if (projectile.isEmpty() && shooter instanceof PlayerEntity) {
					((PlayerEntity)shooter).inventory.removeOne(projectile);
				}
			} else {
				itemStack = projectile.copy();
			}

			putProjectile(crossbow, itemStack);
			return true;
		}
	}

	public static boolean isCharged(ItemStack stack) {
		CompoundTag compoundTag = stack.getTag();
		return compoundTag != null && compoundTag.getBoolean("Charged");
	}

	public static void setCharged(ItemStack stack, boolean charged) {
		CompoundTag compoundTag = stack.getOrCreateTag();
		compoundTag.putBoolean("Charged", charged);
	}

	private static void putProjectile(ItemStack crossbow, ItemStack projectile) {
		CompoundTag compoundTag = crossbow.getOrCreateTag();
		ListTag listTag;
		if (compoundTag.contains("ChargedProjectiles", 9)) {
			listTag = compoundTag.getList("ChargedProjectiles", 10);
		} else {
			listTag = new ListTag();
		}

		CompoundTag compoundTag2 = new CompoundTag();
		projectile.toTag(compoundTag2);
		listTag.add(compoundTag2);
		compoundTag.put("ChargedProjectiles", listTag);
	}

	private static List<ItemStack> getProjectiles(ItemStack crossbow) {
		List<ItemStack> list = Lists.<ItemStack>newArrayList();
		CompoundTag compoundTag = crossbow.getTag();
		if (compoundTag != null && compoundTag.contains("ChargedProjectiles", 9)) {
			ListTag listTag = compoundTag.getList("ChargedProjectiles", 10);
			if (listTag != null) {
				for (int i = 0; i < listTag.size(); i++) {
					CompoundTag compoundTag2 = listTag.getCompound(i);
					list.add(ItemStack.fromTag(compoundTag2));
				}
			}
		}

		return list;
	}

	private static void clearProjectiles(ItemStack crossbow) {
		CompoundTag compoundTag = crossbow.getTag();
		if (compoundTag != null) {
			ListTag listTag = compoundTag.getList("ChargedProjectiles", 9);
			listTag.clear();
			compoundTag.put("ChargedProjectiles", listTag);
		}
	}

	public static boolean hasProjectile(ItemStack crossbow, Item projectile) {
		return getProjectiles(crossbow).stream().anyMatch(s -> s.getItem() == projectile);
	}

	private static void shoot(
		World world,
		LivingEntity shooter,
		Hand hand,
		ItemStack crossbow,
		ItemStack projectile,
		float soundPitch,
		boolean creative,
		float speed,
		float divergence,
		float simulated
	) {
		if (!world.isClient) {
			boolean bl = projectile.getItem() == Items.FIREWORK_ROCKET;
			ProjectileEntity projectileEntity;
			if (bl) {
				projectileEntity = new FireworkRocketEntity(world, projectile, shooter, shooter.getX(), shooter.getEyeY() - 0.15F, shooter.getZ(), true);
			} else {
				projectileEntity = createArrow(world, shooter, crossbow, projectile);
				if (creative || simulated != 0.0F) {
					((PersistentProjectileEntity)projectileEntity).pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
				}
			}

			if (shooter instanceof CrossbowUser) {
				CrossbowUser crossbowUser = (CrossbowUser)shooter;
				crossbowUser.shoot(crossbowUser.getTarget(), crossbow, projectileEntity, simulated);
			} else {
				Vec3d vec3d = shooter.getOppositeRotationVector(1.0F);
				Quaternion quaternion = new Quaternion(new Vector3f(vec3d), simulated, true);
				Vec3d vec3d2 = shooter.getRotationVec(1.0F);
				Vector3f vector3f = new Vector3f(vec3d2);
				vector3f.rotate(quaternion);
				projectileEntity.setVelocity((double)vector3f.getX(), (double)vector3f.getY(), (double)vector3f.getZ(), speed, divergence);
			}

			crossbow.damage(bl ? 3 : 1, shooter, e -> e.sendToolBreakStatus(hand));
			world.spawnEntity(projectileEntity);
			world.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(), SoundEvents.ITEM_CROSSBOW_SHOOT, SoundCategory.PLAYERS, 1.0F, soundPitch);
		}
	}

	private static PersistentProjectileEntity createArrow(World world, LivingEntity entity, ItemStack crossbow, ItemStack arrow) {
		ArrowItem arrowItem = (ArrowItem)(arrow.getItem() instanceof ArrowItem ? arrow.getItem() : Items.ARROW);
		PersistentProjectileEntity persistentProjectileEntity = arrowItem.createArrow(world, arrow, entity);
		if (entity instanceof PlayerEntity) {
			persistentProjectileEntity.setCritical(true);
		}

		persistentProjectileEntity.setSound(SoundEvents.ITEM_CROSSBOW_HIT);
		persistentProjectileEntity.setShotFromCrossbow(true);
		int i = EnchantmentHelper.getLevel(Enchantments.PIERCING, crossbow);
		if (i > 0) {
			persistentProjectileEntity.setPierceLevel((byte)i);
		}

		return persistentProjectileEntity;
	}

	public static void shootAll(World world, LivingEntity entity, Hand hand, ItemStack stack, float speed, float divergence) {
		List<ItemStack> list = getProjectiles(stack);
		float[] fs = getSoundPitches(entity.getRandom());

		for (int i = 0; i < list.size(); i++) {
			ItemStack itemStack = (ItemStack)list.get(i);
			boolean bl = entity instanceof PlayerEntity && ((PlayerEntity)entity).abilities.creativeMode;
			if (!itemStack.isEmpty()) {
				if (i == 0) {
					shoot(world, entity, hand, stack, itemStack, fs[i], bl, speed, divergence, 0.0F);
				} else if (i == 1) {
					shoot(world, entity, hand, stack, itemStack, fs[i], bl, speed, divergence, -10.0F);
				} else if (i == 2) {
					shoot(world, entity, hand, stack, itemStack, fs[i], bl, speed, divergence, 10.0F);
				}
			}
		}

		postShoot(world, entity, stack);
	}

	private static float[] getSoundPitches(Random random) {
		boolean bl = random.nextBoolean();
		return new float[]{1.0F, getSoundPitch(bl), getSoundPitch(!bl)};
	}

	private static float getSoundPitch(boolean flag) {
		float f = flag ? 0.63F : 0.43F;
		return 1.0F / (RANDOM.nextFloat() * 0.5F + 1.8F) + f;
	}

	private static void postShoot(World world, LivingEntity entity, ItemStack stack) {
		if (entity instanceof ServerPlayerEntity) {
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity;
			if (!world.isClient) {
				Criteria.SHOT_CROSSBOW.trigger(serverPlayerEntity, stack);
			}

			serverPlayerEntity.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
		}

		clearProjectiles(stack);
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

	@Environment(EnvType.CLIENT)
	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		List<ItemStack> list = getProjectiles(stack);
		if (isCharged(stack) && !list.isEmpty()) {
			ItemStack itemStack = (ItemStack)list.get(0);
			tooltip.add(new TranslatableText("item.minecraft.crossbow.projectile").append(" ").append(itemStack.toHoverableText()));
			if (context.isAdvanced() && itemStack.getItem() == Items.FIREWORK_ROCKET) {
				List<Text> list2 = Lists.<Text>newArrayList();
				Items.FIREWORK_ROCKET.appendTooltip(itemStack, world, list2, context);
				if (!list2.isEmpty()) {
					for (int i = 0; i < list2.size(); i++) {
						list2.set(i, new LiteralText("  ").append((Text)list2.get(i)).formatted(Formatting.GRAY));
					}

					tooltip.addAll(list2);
				}
			}
		}
	}

	private static float getSpeed(ItemStack stack) {
		return stack.getItem() == Items.CROSSBOW && hasProjectile(stack, Items.FIREWORK_ROCKET) ? 1.6F : 3.15F;
	}

	@Override
	public int getRange() {
		return 8;
	}
}
