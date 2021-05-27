package net.minecraft.item;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.CrossbowUser;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
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
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;

public class CrossbowItem extends RangedWeaponItem implements Vanishable {
	private static final String CHARGED_KEY = "Charged";
	private static final String CHARGED_PROJECTILES_KEY = "ChargedProjectiles";
	private static final int field_30866 = 25;
	public static final int RANGE = 8;
	private boolean charged = false;
	private boolean loaded = false;
	private static final float field_30867 = 0.2F;
	private static final float field_30868 = 0.5F;
	private static final float field_30869 = 3.15F;
	private static final float field_30870 = 1.6F;

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
			if (user.getOffHandStack().isOf(Items.CROSSBOW) && !user.getMainHandStack().isEmpty()) {
				return TypedActionResult.fail(itemStack);
			} else {
				shootAll(world, user, hand, itemStack, getSpeed(itemStack), 1.0F);
				setCharged(itemStack, false);
				return TypedActionResult.consume(itemStack);
			}
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

	private static float getSpeed(ItemStack stack) {
		return hasProjectile(stack, Items.FIREWORK_ROCKET) ? 1.6F : 3.15F;
	}

	@Override
	public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
		int i = this.getMaxUseTime(stack) - remainingUseTicks;
		float f = getPullProgress(i, stack);
		if (f >= 1.0F && !isCharged(stack) && loadProjectiles(user, stack)) {
			setCharged(stack, true);
			SoundCategory soundCategory = user instanceof PlayerEntity ? SoundCategory.PLAYERS : SoundCategory.HOSTILE;
			world.playSound(
				null,
				user.getX(),
				user.getY(),
				user.getZ(),
				SoundEvents.ITEM_CROSSBOW_LOADING_END,
				soundCategory,
				1.0F,
				1.0F / (world.getRandom().nextFloat() * 0.5F + 1.0F) + 0.2F
			);
		}
	}

	private static boolean loadProjectiles(LivingEntity shooter, ItemStack projectile) {
		int i = EnchantmentHelper.getLevel(Enchantments.MULTISHOT, projectile);
		int j = i == 0 ? 1 : 3;
		boolean bl = shooter instanceof PlayerEntity && ((PlayerEntity)shooter).getAbilities().creativeMode;
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
					((PlayerEntity)shooter).getInventory().removeOne(projectile);
				}
			} else {
				itemStack = projectile.copy();
			}

			putProjectile(crossbow, itemStack);
			return true;
		}
	}

	public static boolean isCharged(ItemStack stack) {
		NbtCompound nbtCompound = stack.getTag();
		return nbtCompound != null && nbtCompound.getBoolean("Charged");
	}

	public static void setCharged(ItemStack stack, boolean charged) {
		NbtCompound nbtCompound = stack.getOrCreateTag();
		nbtCompound.putBoolean("Charged", charged);
	}

	private static void putProjectile(ItemStack crossbow, ItemStack projectile) {
		NbtCompound nbtCompound = crossbow.getOrCreateTag();
		NbtList nbtList;
		if (nbtCompound.contains("ChargedProjectiles", NbtElement.LIST_TYPE)) {
			nbtList = nbtCompound.getList("ChargedProjectiles", NbtElement.COMPOUND_TYPE);
		} else {
			nbtList = new NbtList();
		}

		NbtCompound nbtCompound2 = new NbtCompound();
		projectile.writeNbt(nbtCompound2);
		nbtList.add(nbtCompound2);
		nbtCompound.put("ChargedProjectiles", nbtList);
	}

	private static List<ItemStack> getProjectiles(ItemStack crossbow) {
		List<ItemStack> list = Lists.<ItemStack>newArrayList();
		NbtCompound nbtCompound = crossbow.getTag();
		if (nbtCompound != null && nbtCompound.contains("ChargedProjectiles", NbtElement.LIST_TYPE)) {
			NbtList nbtList = nbtCompound.getList("ChargedProjectiles", NbtElement.COMPOUND_TYPE);
			if (nbtList != null) {
				for (int i = 0; i < nbtList.size(); i++) {
					NbtCompound nbtCompound2 = nbtList.getCompound(i);
					list.add(ItemStack.fromNbt(nbtCompound2));
				}
			}
		}

		return list;
	}

	private static void clearProjectiles(ItemStack crossbow) {
		NbtCompound nbtCompound = crossbow.getTag();
		if (nbtCompound != null) {
			NbtList nbtList = nbtCompound.getList("ChargedProjectiles", NbtElement.LIST_TYPE);
			nbtList.clear();
			nbtCompound.put("ChargedProjectiles", nbtList);
		}
	}

	public static boolean hasProjectile(ItemStack crossbow, Item projectile) {
		return getProjectiles(crossbow).stream().anyMatch(s -> s.isOf(projectile));
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
			boolean bl = projectile.isOf(Items.FIREWORK_ROCKET);
			ProjectileEntity projectileEntity;
			if (bl) {
				projectileEntity = new FireworkRocketEntity(world, projectile, shooter, shooter.getX(), shooter.getEyeY() - 0.15F, shooter.getZ(), true);
			} else {
				projectileEntity = createArrow(world, shooter, crossbow, projectile);
				if (creative || simulated != 0.0F) {
					((PersistentProjectileEntity)projectileEntity).pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
				}
			}

			if (shooter instanceof CrossbowUser crossbowUser) {
				crossbowUser.shoot(crossbowUser.getTarget(), crossbow, projectileEntity, simulated);
			} else {
				Vec3d vec3d = shooter.getOppositeRotationVector(1.0F);
				Quaternion quaternion = new Quaternion(new Vec3f(vec3d), simulated, true);
				Vec3d vec3d2 = shooter.getRotationVec(1.0F);
				Vec3f vec3f = new Vec3f(vec3d2);
				vec3f.rotate(quaternion);
				projectileEntity.setVelocity((double)vec3f.getX(), (double)vec3f.getY(), (double)vec3f.getZ(), speed, divergence);
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
			boolean bl = entity instanceof PlayerEntity && ((PlayerEntity)entity).getAbilities().creativeMode;
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
		return new float[]{1.0F, getSoundPitch(bl, random), getSoundPitch(!bl, random)};
	}

	private static float getSoundPitch(boolean flag, Random random) {
		float f = flag ? 0.63F : 0.43F;
		return 1.0F / (random.nextFloat() * 0.5F + 1.8F) + f;
	}

	private static void postShoot(World world, LivingEntity entity, ItemStack stack) {
		if (entity instanceof ServerPlayerEntity serverPlayerEntity) {
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

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		List<ItemStack> list = getProjectiles(stack);
		if (isCharged(stack) && !list.isEmpty()) {
			ItemStack itemStack = (ItemStack)list.get(0);
			tooltip.add(new TranslatableText("item.minecraft.crossbow.projectile").append(" ").append(itemStack.toHoverableText()));
			if (context.isAdvanced() && itemStack.isOf(Items.FIREWORK_ROCKET)) {
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

	@Override
	public boolean isUsedOnRelease(ItemStack stack) {
		return stack.isOf(this);
	}

	@Override
	public int getRange() {
		return 8;
	}
}
