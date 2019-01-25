package net.minecraft.item;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.FireworkEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class CrossbowItem extends BaseBowItem {
	private boolean field_7937 = false;
	private boolean field_7936 = false;

	public CrossbowItem(Item.Settings settings) {
		super(settings);
		this.addProperty(new Identifier("pull"), (itemStack, world, livingEntity) -> {
			if (livingEntity == null || itemStack.getItem() != this) {
				return 0.0F;
			} else {
				return isCharged(itemStack) ? 0.0F : (float)(itemStack.getMaxUseTime() - livingEntity.method_6014()) / (float)getPullTime(itemStack);
			}
		});
		this.addProperty(
			new Identifier("pulling"),
			(itemStack, world, livingEntity) -> livingEntity != null && livingEntity.isUsingItem() && livingEntity.getActiveItem() == itemStack && !isCharged(itemStack)
					? 1.0F
					: 0.0F
		);
		this.addProperty(new Identifier("charged"), (itemStack, world, livingEntity) -> livingEntity != null && isCharged(itemStack) ? 1.0F : 0.0F);
		this.addProperty(
			new Identifier("firework"),
			(itemStack, world, livingEntity) -> livingEntity != null && isCharged(itemStack) && this.hasProjectile(itemStack, Items.field_8639) ? 1.0F : 0.0F
		);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		boolean bl = !this.getHeldFireworks(playerEntity).isEmpty();
		ItemStack itemStack2 = this.findArrowStack(playerEntity);
		if (isCharged(itemStack)) {
			this.shootAllProjectiles(world, playerEntity, itemStack);
			setCharged(itemStack, false);
			return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
		} else if (!playerEntity.abilities.creativeMode && itemStack2.isEmpty() && !bl) {
			return itemStack2.isEmpty() && !bl ? new TypedActionResult<>(ActionResult.FAILURE, itemStack) : new TypedActionResult<>(ActionResult.PASS, itemStack);
		} else {
			if (!isCharged(itemStack)) {
				this.field_7937 = false;
				this.field_7936 = false;
				playerEntity.setCurrentHand(hand);
			}

			return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
		}
	}

	@Override
	public void onItemStopUsing(ItemStack itemStack, World world, LivingEntity livingEntity, int i) {
		if (livingEntity instanceof PlayerEntity) {
			int j = this.getMaxUseTime(itemStack) - i;
			float f = method_7770(j, itemStack);
			if (f >= 1.0F && !isCharged(itemStack)) {
				this.method_7767((PlayerEntity)livingEntity, itemStack);
				setCharged(itemStack, true);
				world.playSound(
					null,
					livingEntity.x,
					livingEntity.y,
					livingEntity.z,
					SoundEvents.field_14626,
					SoundCategory.field_15248,
					1.0F,
					1.0F / (random.nextFloat() * 0.5F + 1.0F) + 0.2F
				);
			}
		}
	}

	private void method_7767(PlayerEntity playerEntity, ItemStack itemStack) {
		ItemStack itemStack2 = this.getHeldFireworks(playerEntity);
		boolean bl = playerEntity.abilities.creativeMode;
		int i = EnchantmentHelper.getLevel(Enchantments.field_9108, itemStack);
		int j = i == 0 ? 1 : 3;
		ItemStack itemStack3 = itemStack2.isEmpty() ? this.findArrowStack(playerEntity) : itemStack2;
		ItemStack itemStack4 = itemStack3.copy();

		for (int k = 0; k < j; k++) {
			if (k > 0) {
				itemStack3 = itemStack4.copy();
			}

			if (itemStack3.isEmpty() && bl) {
				itemStack3 = new ItemStack(Items.field_8107);
				itemStack4 = itemStack3.copy();
			}

			this.method_7765(playerEntity, itemStack, itemStack3, k > 0);
		}
	}

	private void method_7765(PlayerEntity playerEntity, ItemStack itemStack, ItemStack itemStack2, boolean bl) {
		boolean bl2 = playerEntity.abilities.creativeMode;
		boolean bl3 = bl2 && itemStack2.getItem() == Items.field_8107;
		ItemStack itemStack3;
		if (!bl3 && !bl2 && !bl) {
			itemStack3 = itemStack2.split(1);
			if (itemStack2.isEmpty()) {
				playerEntity.inventory.removeOne(itemStack2);
			}
		} else {
			itemStack3 = itemStack2.copy();
		}

		this.storeChargedProjectile(itemStack, itemStack3);
	}

	public static boolean isCharged(ItemStack itemStack) {
		CompoundTag compoundTag = itemStack.getTag();
		return compoundTag != null ? compoundTag.getBoolean("Charged") : false;
	}

	public static CompoundTag setCharged(ItemStack itemStack, boolean bl) {
		CompoundTag compoundTag = itemStack.getOrCreateTag();
		compoundTag.putBoolean("Charged", bl);
		return compoundTag;
	}

	private void storeChargedProjectile(ItemStack itemStack, ItemStack itemStack2) {
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

	private List<ItemStack> getChargedProjectiles(ItemStack itemStack) {
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

	private void clearProjectiles(ItemStack itemStack) {
		CompoundTag compoundTag = itemStack.getTag();
		if (compoundTag != null) {
			ListTag listTag = compoundTag.getList("ChargedProjectiles", 9);
			listTag.clear();
			compoundTag.put("ChargedProjectiles", listTag);
		}
	}

	private boolean hasProjectile(ItemStack itemStack, Item item) {
		return this.getChargedProjectiles(itemStack).stream().anyMatch(itemStackx -> itemStackx.getItem() == item);
	}

	private void shoot(World world, PlayerEntity playerEntity, ItemStack itemStack, ItemStack itemStack2, float f, float g, boolean bl) {
		if (!this.tryShootFireworks(world, playerEntity, itemStack, itemStack2, f) && !itemStack2.isEmpty()) {
			boolean bl2 = playerEntity.abilities.creativeMode;
			boolean bl3 = bl2 && itemStack2.getItem() == Items.field_8107;
			if (!world.isClient) {
				ArrowItem arrowItem = (ArrowItem)(itemStack2.getItem() instanceof ArrowItem ? itemStack2.getItem() : Items.field_8107);
				ProjectileEntity projectileEntity = arrowItem.createEntityArrow(world, itemStack2, playerEntity);
				projectileEntity.method_7437(playerEntity, playerEntity.pitch, f, 0.0F, 3.15F, 1.0F);
				projectileEntity.setCritical(true);
				projectileEntity.setSound(SoundEvents.field_14636);
				projectileEntity.setShotFromCrossbow(true);
				int i = EnchantmentHelper.getLevel(Enchantments.field_9132, itemStack);
				if (i > 0) {
					projectileEntity.setFlagByte((byte)i);
				}

				itemStack.applyDamage(1, playerEntity);
				if (bl3 || bl || playerEntity.abilities.creativeMode && (itemStack2.getItem() == Items.field_8236 || itemStack2.getItem() == Items.field_8087)) {
					projectileEntity.pickupType = ProjectileEntity.PickupType.CREATIVE_PICKUP;
				}

				world.spawnEntity(projectileEntity);
			}

			world.playSound(null, playerEntity.x, playerEntity.y, playerEntity.z, SoundEvents.field_15187, SoundCategory.field_15248, 1.0F, g);
		}
	}

	private boolean tryShootFireworks(World world, PlayerEntity playerEntity, ItemStack itemStack, ItemStack itemStack2, float f) {
		if (itemStack2.getItem() != Items.field_8639) {
			return false;
		} else if (!itemStack2.isEmpty()) {
			if (!world.isClient) {
				FireworkEntity fireworkEntity = new FireworkEntity(
					world, itemStack2, playerEntity.x, playerEntity.y + (double)playerEntity.getEyeHeight() - 0.15F, playerEntity.z, true
				);
				fireworkEntity.method_7474(playerEntity, f, 1.0F);
				itemStack.applyDamage(3, playerEntity);
				world.spawnEntity(fireworkEntity);
				world.playSound(null, playerEntity.x, playerEntity.y, playerEntity.z, SoundEvents.field_15187, SoundCategory.field_15248, 1.0F, 1.0F);
			}

			this.method_7769(world, playerEntity, itemStack);
			return true;
		} else {
			return false;
		}
	}

	private void shootAllProjectiles(World world, PlayerEntity playerEntity, ItemStack itemStack) {
		List<ItemStack> list = this.getChargedProjectiles(itemStack);
		float f = 10.0F;
		float[] fs = this.method_7780(playerEntity.getRand());

		for (int i = 0; i < list.size(); i++) {
			ItemStack itemStack2 = (ItemStack)list.get(i);
			if (!itemStack2.isEmpty()) {
				if (i == 0) {
					this.shoot(world, playerEntity, itemStack, itemStack2, playerEntity.yaw, fs[i], false);
				} else if (i == 1) {
					this.shoot(world, playerEntity, itemStack, itemStack2, playerEntity.yaw - 10.0F, fs[i], true);
				} else if (i == 2) {
					this.shoot(world, playerEntity, itemStack, itemStack2, playerEntity.yaw + 10.0F, fs[i], true);
				}
			}
		}

		this.method_7769(world, playerEntity, itemStack);
	}

	private float[] method_7780(Random random) {
		boolean bl = random.nextBoolean();
		return new float[]{1.0F, this.method_7784(bl), this.method_7784(!bl)};
	}

	private float method_7784(boolean bl) {
		float f = bl ? 0.63F : 0.43F;
		return 1.0F / (random.nextFloat() * 0.5F + 1.8F) + f;
	}

	private void method_7769(World world, PlayerEntity playerEntity, ItemStack itemStack) {
		if (!world.isClient) {
			Criterions.SHOT_CROSSBOW.method_9115((ServerPlayerEntity)playerEntity, itemStack);
		}

		playerEntity.incrementStat(Stats.field_15372.getOrCreateStat(this));
		this.clearProjectiles(itemStack);
	}

	@Override
	public void method_7852(World world, LivingEntity livingEntity, ItemStack itemStack, int i) {
		if (!world.isClient) {
			int j = EnchantmentHelper.getLevel(Enchantments.field_9098, itemStack);
			SoundEvent soundEvent = this.getChargeSound(j);
			SoundEvent soundEvent2 = j == 0 ? SoundEvents.field_14860 : null;
			float f = (float)(itemStack.getMaxUseTime() - i) / (float)getPullTime(itemStack);
			if (f < 0.2F) {
				this.field_7937 = false;
				this.field_7936 = false;
			}

			if (f >= 0.2F && !this.field_7937) {
				this.field_7937 = true;
				world.playSound(null, livingEntity.x, livingEntity.y, livingEntity.z, soundEvent, SoundCategory.field_15248, 0.5F, 1.0F);
			}

			if (f >= 0.5F && soundEvent2 != null && !this.field_7936) {
				this.field_7936 = true;
				world.playSound(null, livingEntity.x, livingEntity.y, livingEntity.z, soundEvent2, SoundCategory.field_15248, 0.5F, 1.0F);
			}
		}
	}

	@Override
	public int getMaxUseTime(ItemStack itemStack) {
		return getPullTime(itemStack) + 3;
	}

	public static int getPullTime(ItemStack itemStack) {
		int i = EnchantmentHelper.getLevel(Enchantments.field_9098, itemStack);
		return i == 0 ? 25 : 25 - 5 * i;
	}

	@Override
	public UseAction getUseAction(ItemStack itemStack) {
		return UseAction.field_8947;
	}

	private ItemStack getHeldFireworks(PlayerEntity playerEntity) {
		ItemStack itemStack = playerEntity.getOffHandStack();
		if (!itemStack.isEmpty() && itemStack.getItem() instanceof FireworksItem) {
			return itemStack;
		} else {
			ItemStack itemStack2 = playerEntity.getMainHandStack();
			return !itemStack2.isEmpty() && itemStack2.getItem() instanceof FireworksItem ? itemStack2 : ItemStack.EMPTY;
		}
	}

	private SoundEvent getChargeSound(int i) {
		switch (i) {
			case 1:
				return SoundEvents.field_15011;
			case 2:
				return SoundEvents.field_14916;
			case 3:
				return SoundEvents.field_15089;
			default:
				return SoundEvents.field_14765;
		}
	}

	public static float method_7770(int i, ItemStack itemStack) {
		float f = (float)i / (float)getPullTime(itemStack);
		if (f > 1.0F) {
			f = 1.0F;
		}

		return f;
	}
}
