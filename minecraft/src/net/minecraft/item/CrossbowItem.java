package net.minecraft.item;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.util.math.Quaternion;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.CrossbowUser;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FireworkEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.Projectile;
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
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class CrossbowItem extends RangedWeaponItem {
	private boolean charged = false;
	private boolean loaded = false;

	public CrossbowItem(Item.Settings settings) {
		super(settings);
		this.addPropertyGetter(new Identifier("pull"), (itemStack, world, livingEntity) -> {
			if (livingEntity == null || itemStack.getItem() != this) {
				return 0.0F;
			} else {
				return isCharged(itemStack) ? 0.0F : (float)(itemStack.getMaxUseTime() - livingEntity.getItemUseTimeLeft()) / (float)getPullTime(itemStack);
			}
		});
		this.addPropertyGetter(
			new Identifier("pulling"),
			(itemStack, world, livingEntity) -> livingEntity != null && livingEntity.isUsingItem() && livingEntity.getActiveItem() == itemStack && !isCharged(itemStack)
					? 1.0F
					: 0.0F
		);
		this.addPropertyGetter(new Identifier("charged"), (itemStack, world, livingEntity) -> livingEntity != null && isCharged(itemStack) ? 1.0F : 0.0F);
		this.addPropertyGetter(
			new Identifier("firework"),
			(itemStack, world, livingEntity) -> livingEntity != null && isCharged(itemStack) && hasProjectile(itemStack, Items.FIREWORK_ROCKET) ? 1.0F : 0.0F
		);
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
	public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		if (isCharged(itemStack)) {
			shootAll(world, playerEntity, hand, itemStack, getSpeed(itemStack), 1.0F);
			setCharged(itemStack, false);
			return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
		} else if (!playerEntity.getArrowType(itemStack).isEmpty()) {
			if (!isCharged(itemStack)) {
				this.charged = false;
				this.loaded = false;
				playerEntity.setCurrentHand(hand);
			}

			return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
		} else {
			return new TypedActionResult<>(ActionResult.FAIL, itemStack);
		}
	}

	@Override
	public void onStoppedUsing(ItemStack itemStack, World world, LivingEntity livingEntity, int i) {
		int j = this.getMaxUseTime(itemStack) - i;
		float f = getPullProgress(j, itemStack);
		if (f >= 1.0F && !isCharged(itemStack) && loadProjectiles(livingEntity, itemStack)) {
			setCharged(itemStack, true);
			SoundCategory soundCategory = livingEntity instanceof PlayerEntity ? SoundCategory.PLAYERS : SoundCategory.HOSTILE;
			world.playSound(
				null,
				livingEntity.x,
				livingEntity.y,
				livingEntity.z,
				SoundEvents.ITEM_CROSSBOW_LOADING_END,
				soundCategory,
				1.0F,
				1.0F / (RANDOM.nextFloat() * 0.5F + 1.0F) + 0.2F
			);
		}
	}

	private static boolean loadProjectiles(LivingEntity livingEntity, ItemStack itemStack) {
		int i = EnchantmentHelper.getLevel(Enchantments.MULTISHOT, itemStack);
		int j = i == 0 ? 1 : 3;
		boolean bl = livingEntity instanceof PlayerEntity && ((PlayerEntity)livingEntity).abilities.creativeMode;
		ItemStack itemStack2 = livingEntity.getArrowType(itemStack);
		ItemStack itemStack3 = itemStack2.copy();

		for (int k = 0; k < j; k++) {
			if (k > 0) {
				itemStack2 = itemStack3.copy();
			}

			if (itemStack2.isEmpty() && bl) {
				itemStack2 = new ItemStack(Items.ARROW);
				itemStack3 = itemStack2.copy();
			}

			if (!loadProjectile(livingEntity, itemStack, itemStack2, k > 0, bl)) {
				return false;
			}
		}

		return true;
	}

	private static boolean loadProjectile(LivingEntity livingEntity, ItemStack itemStack, ItemStack itemStack2, boolean bl, boolean bl2) {
		if (itemStack2.isEmpty()) {
			return false;
		} else {
			boolean bl3 = bl2 && itemStack2.getItem() instanceof ArrowItem;
			ItemStack itemStack3;
			if (!bl3 && !bl2 && !bl) {
				itemStack3 = itemStack2.split(1);
				if (itemStack2.isEmpty() && livingEntity instanceof PlayerEntity) {
					((PlayerEntity)livingEntity).inventory.removeOne(itemStack2);
				}
			} else {
				itemStack3 = itemStack2.copy();
			}

			putProjectile(itemStack, itemStack3);
			return true;
		}
	}

	public static boolean isCharged(ItemStack itemStack) {
		CompoundTag compoundTag = itemStack.getTag();
		return compoundTag != null && compoundTag.getBoolean("Charged");
	}

	public static void setCharged(ItemStack itemStack, boolean bl) {
		CompoundTag compoundTag = itemStack.getOrCreateTag();
		compoundTag.putBoolean("Charged", bl);
	}

	private static void putProjectile(ItemStack itemStack, ItemStack itemStack2) {
		CompoundTag compoundTag = itemStack.getOrCreateTag();
		ListTag listTag;
		if (compoundTag.containsKey("ChargedProjectiles", 9)) {
			listTag = compoundTag.getList("ChargedProjectiles", 10);
		} else {
			listTag = new ListTag();
		}

		CompoundTag compoundTag2 = new CompoundTag();
		itemStack2.toTag(compoundTag2);
		listTag.add(compoundTag2);
		compoundTag.put("ChargedProjectiles", listTag);
	}

	private static List<ItemStack> getProjectiles(ItemStack itemStack) {
		List<ItemStack> list = Lists.<ItemStack>newArrayList();
		CompoundTag compoundTag = itemStack.getTag();
		if (compoundTag != null && compoundTag.containsKey("ChargedProjectiles", 9)) {
			ListTag listTag = compoundTag.getList("ChargedProjectiles", 10);
			if (listTag != null) {
				for (int i = 0; i < listTag.size(); i++) {
					CompoundTag compoundTag2 = listTag.getCompoundTag(i);
					list.add(ItemStack.fromTag(compoundTag2));
				}
			}
		}

		return list;
	}

	private static void clearProjectiles(ItemStack itemStack) {
		CompoundTag compoundTag = itemStack.getTag();
		if (compoundTag != null) {
			ListTag listTag = compoundTag.getList("ChargedProjectiles", 9);
			listTag.clear();
			compoundTag.put("ChargedProjectiles", listTag);
		}
	}

	private static boolean hasProjectile(ItemStack itemStack, Item item) {
		return getProjectiles(itemStack).stream().anyMatch(itemStackx -> itemStackx.getItem() == item);
	}

	private static void shoot(
		World world, LivingEntity livingEntity, Hand hand, ItemStack itemStack, ItemStack itemStack2, float f, boolean bl, float g, float h, float i
	) {
		if (!world.isClient) {
			boolean bl2 = itemStack2.getItem() == Items.FIREWORK_ROCKET;
			Projectile projectile;
			if (bl2) {
				projectile = new FireworkEntity(
					world, itemStack2, livingEntity.x, livingEntity.y + (double)livingEntity.getStandingEyeHeight() - 0.15F, livingEntity.z, true
				);
			} else {
				projectile = createArrow(world, livingEntity, itemStack, itemStack2);
				if (bl || i != 0.0F) {
					((ProjectileEntity)projectile).pickupType = ProjectileEntity.PickupPermission.CREATIVE_ONLY;
				}
			}

			if (livingEntity instanceof CrossbowUser) {
				CrossbowUser crossbowUser = (CrossbowUser)livingEntity;
				crossbowUser.shoot(crossbowUser.getTarget(), itemStack, projectile, i);
			} else {
				Vec3d vec3d = livingEntity.getOppositeRotationVector(1.0F);
				Quaternion quaternion = new Quaternion(new Vector3f(vec3d), i, true);
				Vec3d vec3d2 = livingEntity.getRotationVec(1.0F);
				Vector3f vector3f = new Vector3f(vec3d2);
				vector3f.method_19262(quaternion);
				projectile.setVelocity((double)vector3f.x(), (double)vector3f.y(), (double)vector3f.z(), g, h);
			}

			itemStack.damage(bl2 ? 3 : 1, livingEntity, livingEntityx -> livingEntityx.sendToolBreakStatus(hand));
			world.spawnEntity((Entity)projectile);
			world.playSound(null, livingEntity.x, livingEntity.y, livingEntity.z, SoundEvents.ITEM_CROSSBOW_SHOOT, SoundCategory.PLAYERS, 1.0F, f);
		}
	}

	private static ProjectileEntity createArrow(World world, LivingEntity livingEntity, ItemStack itemStack, ItemStack itemStack2) {
		ArrowItem arrowItem = (ArrowItem)(itemStack2.getItem() instanceof ArrowItem ? itemStack2.getItem() : Items.ARROW);
		ProjectileEntity projectileEntity = arrowItem.createArrow(world, itemStack2, livingEntity);
		if (livingEntity instanceof PlayerEntity) {
			projectileEntity.setCritical(true);
		}

		projectileEntity.setSound(SoundEvents.ITEM_CROSSBOW_HIT);
		projectileEntity.setShotFromCrossbow(true);
		int i = EnchantmentHelper.getLevel(Enchantments.PIERCING, itemStack);
		if (i > 0) {
			projectileEntity.setPierceLevel((byte)i);
		}

		return projectileEntity;
	}

	public static void shootAll(World world, LivingEntity livingEntity, Hand hand, ItemStack itemStack, float f, float g) {
		List<ItemStack> list = getProjectiles(itemStack);
		float[] fs = getSoundPitches(livingEntity.getRand());

		for (int i = 0; i < list.size(); i++) {
			ItemStack itemStack2 = (ItemStack)list.get(i);
			boolean bl = livingEntity instanceof PlayerEntity && ((PlayerEntity)livingEntity).abilities.creativeMode;
			if (!itemStack2.isEmpty()) {
				if (i == 0) {
					shoot(world, livingEntity, hand, itemStack, itemStack2, fs[i], bl, f, g, 0.0F);
				} else if (i == 1) {
					shoot(world, livingEntity, hand, itemStack, itemStack2, fs[i], bl, f, g, -10.0F);
				} else if (i == 2) {
					shoot(world, livingEntity, hand, itemStack, itemStack2, fs[i], bl, f, g, 10.0F);
				}
			}
		}

		postShoot(world, livingEntity, itemStack);
	}

	private static float[] getSoundPitches(Random random) {
		boolean bl = random.nextBoolean();
		return new float[]{1.0F, getSoundPitch(bl), getSoundPitch(!bl)};
	}

	private static float getSoundPitch(boolean bl) {
		float f = bl ? 0.63F : 0.43F;
		return 1.0F / (RANDOM.nextFloat() * 0.5F + 1.8F) + f;
	}

	private static void postShoot(World world, LivingEntity livingEntity, ItemStack itemStack) {
		if (livingEntity instanceof ServerPlayerEntity) {
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)livingEntity;
			if (!world.isClient) {
				Criterions.SHOT_CROSSBOW.trigger(serverPlayerEntity, itemStack);
			}

			serverPlayerEntity.incrementStat(Stats.USED.getOrCreateStat(itemStack.getItem()));
		}

		clearProjectiles(itemStack);
	}

	@Override
	public void usageTick(World world, LivingEntity livingEntity, ItemStack itemStack, int i) {
		if (!world.isClient) {
			int j = EnchantmentHelper.getLevel(Enchantments.QUICK_CHARGE, itemStack);
			SoundEvent soundEvent = this.getQuickChargeSound(j);
			SoundEvent soundEvent2 = j == 0 ? SoundEvents.ITEM_CROSSBOW_LOADING_MIDDLE : null;
			float f = (float)(itemStack.getMaxUseTime() - i) / (float)getPullTime(itemStack);
			if (f < 0.2F) {
				this.charged = false;
				this.loaded = false;
			}

			if (f >= 0.2F && !this.charged) {
				this.charged = true;
				world.playSound(null, livingEntity.x, livingEntity.y, livingEntity.z, soundEvent, SoundCategory.PLAYERS, 0.5F, 1.0F);
			}

			if (f >= 0.5F && soundEvent2 != null && !this.loaded) {
				this.loaded = true;
				world.playSound(null, livingEntity.x, livingEntity.y, livingEntity.z, soundEvent2, SoundCategory.PLAYERS, 0.5F, 1.0F);
			}
		}
	}

	@Override
	public int getMaxUseTime(ItemStack itemStack) {
		return getPullTime(itemStack) + 3;
	}

	public static int getPullTime(ItemStack itemStack) {
		int i = EnchantmentHelper.getLevel(Enchantments.QUICK_CHARGE, itemStack);
		return i == 0 ? 25 : 25 - 5 * i;
	}

	@Override
	public UseAction getUseAction(ItemStack itemStack) {
		return UseAction.CROSSBOW;
	}

	private SoundEvent getQuickChargeSound(int i) {
		switch (i) {
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

	private static float getPullProgress(int i, ItemStack itemStack) {
		float f = (float)i / (float)getPullTime(itemStack);
		if (f > 1.0F) {
			f = 1.0F;
		}

		return f;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void appendTooltip(ItemStack itemStack, @Nullable World world, List<Text> list, TooltipContext tooltipContext) {
		List<ItemStack> list2 = getProjectiles(itemStack);
		if (isCharged(itemStack) && !list2.isEmpty()) {
			ItemStack itemStack2 = (ItemStack)list2.get(0);
			list.add(new TranslatableText("item.minecraft.crossbow.projectile").append(" ").append(itemStack2.toHoverableText()));
			if (tooltipContext.isAdvanced() && itemStack2.getItem() == Items.FIREWORK_ROCKET) {
				List<Text> list3 = Lists.<Text>newArrayList();
				Items.FIREWORK_ROCKET.appendTooltip(itemStack2, world, list3, tooltipContext);
				if (!list3.isEmpty()) {
					for (int i = 0; i < list3.size(); i++) {
						list3.set(i, new LiteralText("  ").append((Text)list3.get(i)).formatted(Formatting.GRAY));
					}

					list.addAll(list3);
				}
			}
		}
	}

	private static float getSpeed(ItemStack itemStack) {
		return itemStack.getItem() == Items.CROSSBOW && hasProjectile(itemStack, Items.FIREWORK_ROCKET) ? 1.6F : 3.15F;
	}
}
