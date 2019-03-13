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
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.sortme.Projectile;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class CrossbowItem extends BaseBowItem {
	private boolean field_7937 = false;
	private boolean field_7936 = false;

	public CrossbowItem(Item.Settings settings) {
		super(settings);
		this.method_7863(new Identifier("pull"), (itemStack, world, livingEntity) -> {
			if (livingEntity == null || itemStack.getItem() != this) {
				return 0.0F;
			} else {
				return method_7781(itemStack) ? 0.0F : (float)(itemStack.getMaxUseTime() - livingEntity.method_6014()) / (float)method_7775(itemStack);
			}
		});
		this.method_7863(
			new Identifier("pulling"),
			(itemStack, world, livingEntity) -> livingEntity != null && livingEntity.isUsingItem() && livingEntity.method_6030() == itemStack && !method_7781(itemStack)
					? 1.0F
					: 0.0F
		);
		this.method_7863(new Identifier("charged"), (itemStack, world, livingEntity) -> livingEntity != null && method_7781(itemStack) ? 1.0F : 0.0F);
		this.method_7863(
			new Identifier("firework"),
			(itemStack, world, livingEntity) -> livingEntity != null && method_7781(itemStack) && method_7772(itemStack, Items.field_8639) ? 1.0F : 0.0F
		);
	}

	@Override
	public Predicate<ItemStack> method_19268() {
		return field_18282;
	}

	@Override
	public TypedActionResult<ItemStack> method_7836(World world, PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.method_5998(hand);
		if (method_7781(itemStack)) {
			method_7777(world, playerEntity, itemStack, 3.15F, 1.0F);
			method_7782(itemStack, false);
			return new TypedActionResult<>(ActionResult.field_5812, itemStack);
		} else if (!playerEntity.method_18808(itemStack).isEmpty()) {
			if (!method_7781(itemStack)) {
				this.field_7937 = false;
				this.field_7936 = false;
				playerEntity.setCurrentHand(hand);
			}

			return new TypedActionResult<>(ActionResult.field_5812, itemStack);
		} else {
			return new TypedActionResult<>(ActionResult.field_5814, itemStack);
		}
	}

	@Override
	public void method_7840(ItemStack itemStack, World world, LivingEntity livingEntity, int i) {
		int j = this.method_7881(itemStack) - i;
		float f = method_7770(j, itemStack);
		if (f >= 1.0F && !method_7781(itemStack)) {
			method_7767(livingEntity, itemStack);
			method_7782(itemStack, true);
			SoundCategory soundCategory = livingEntity instanceof PlayerEntity ? SoundCategory.field_15248 : SoundCategory.field_15251;
			world.method_8465(
				null, livingEntity.x, livingEntity.y, livingEntity.z, SoundEvents.field_14626, soundCategory, 1.0F, 1.0F / (random.nextFloat() * 0.5F + 1.0F) + 0.2F
			);
		}
	}

	private static void method_7767(LivingEntity livingEntity, ItemStack itemStack) {
		int i = EnchantmentHelper.getLevel(Enchantments.field_9108, itemStack);
		int j = i == 0 ? 1 : 3;
		boolean bl = livingEntity instanceof PlayerEntity && ((PlayerEntity)livingEntity).abilities.creativeMode;
		ItemStack itemStack2 = livingEntity.method_18808(itemStack);
		ItemStack itemStack3 = itemStack2.copy();

		for (int k = 0; k < j; k++) {
			if (k > 0) {
				itemStack2 = itemStack3.copy();
			}

			if (itemStack2.isEmpty() && bl) {
				itemStack2 = new ItemStack(Items.field_8107);
				itemStack3 = itemStack2.copy();
			}

			method_7765(livingEntity, itemStack, itemStack2, k > 0, bl);
		}
	}

	private static void method_7765(LivingEntity livingEntity, ItemStack itemStack, ItemStack itemStack2, boolean bl, boolean bl2) {
		boolean bl3 = bl2 && itemStack2.getItem() instanceof ArrowItem;
		ItemStack itemStack3;
		if (!bl3 && !bl2 && !bl) {
			itemStack3 = itemStack2.split(1);
			if (itemStack2.isEmpty() && livingEntity instanceof PlayerEntity) {
				((PlayerEntity)livingEntity).inventory.method_7378(itemStack2);
			}
		} else {
			itemStack3 = itemStack2.copy();
		}

		method_7778(itemStack, itemStack3);
	}

	public static boolean method_7781(ItemStack itemStack) {
		CompoundTag compoundTag = itemStack.method_7969();
		return compoundTag != null && compoundTag.getBoolean("Charged");
	}

	public static void method_7782(ItemStack itemStack, boolean bl) {
		CompoundTag compoundTag = itemStack.method_7948();
		compoundTag.putBoolean("Charged", bl);
	}

	private static void method_7778(ItemStack itemStack, ItemStack itemStack2) {
		CompoundTag compoundTag = itemStack.method_7948();
		ListTag listTag;
		if (compoundTag.containsKey("ChargedProjectiles", 9)) {
			listTag = compoundTag.method_10554("ChargedProjectiles", 10);
		} else {
			listTag = new ListTag();
		}

		CompoundTag compoundTag2 = new CompoundTag();
		itemStack2.method_7953(compoundTag2);
		listTag.add(compoundTag2);
		compoundTag.method_10566("ChargedProjectiles", listTag);
	}

	private static List<ItemStack> method_7785(ItemStack itemStack) {
		List<ItemStack> list = Lists.<ItemStack>newArrayList();
		CompoundTag compoundTag = itemStack.method_7969();
		if (compoundTag != null && compoundTag.containsKey("ChargedProjectiles", 9)) {
			ListTag listTag = compoundTag.method_10554("ChargedProjectiles", 10);
			if (listTag != null) {
				for (int i = 0; i < listTag.size(); i++) {
					CompoundTag compoundTag2 = listTag.getCompoundTag(i);
					list.add(ItemStack.method_7915(compoundTag2));
				}
			}
		}

		return list;
	}

	private static void method_7766(ItemStack itemStack) {
		CompoundTag compoundTag = itemStack.method_7969();
		if (compoundTag != null) {
			ListTag listTag = compoundTag.method_10554("ChargedProjectiles", 9);
			listTag.clear();
			compoundTag.method_10566("ChargedProjectiles", listTag);
		}
	}

	private static boolean method_7772(ItemStack itemStack, Item item) {
		return method_7785(itemStack).stream().anyMatch(itemStackx -> itemStackx.getItem() == item);
	}

	private static void method_7763(
		World world, LivingEntity livingEntity, ItemStack itemStack, ItemStack itemStack2, float f, boolean bl, float g, float h, float i
	) {
		if (!world.isClient) {
			boolean bl2 = itemStack2.getItem() == Items.field_8639;
			Projectile projectile;
			if (bl2) {
				projectile = new FireworkEntity(
					world, itemStack2, livingEntity.x, livingEntity.y + (double)livingEntity.getStandingEyeHeight() - 0.15F, livingEntity.z, true
				);
			} else {
				projectile = method_18814(world, livingEntity, itemStack, itemStack2);
				if (bl || i != 0.0F) {
					((ProjectileEntity)projectile).pickupType = ProjectileEntity.PickupType.CREATIVE_PICKUP;
				}
			}

			if (livingEntity instanceof CrossbowUser) {
				CrossbowUser crossbowUser = (CrossbowUser)livingEntity;
				crossbowUser.method_18811(crossbowUser.method_5968(), itemStack, projectile, i);
			} else {
				Vec3d vec3d = livingEntity.method_18864(1.0F);
				Quaternion quaternion = new Quaternion(new Vector3f(vec3d), i, true);
				Vec3d vec3d2 = livingEntity.method_5828(1.0F);
				Vector3f vector3f = new Vector3f(vec3d2);
				vector3f.method_19262(quaternion);
				projectile.setVelocity((double)vector3f.x(), (double)vector3f.y(), (double)vector3f.z(), g, h);
			}

			itemStack.applyDamage(bl2 ? 3 : 1, livingEntity);
			world.spawnEntity((Entity)projectile);
			world.method_8465(null, livingEntity.x, livingEntity.y, livingEntity.z, SoundEvents.field_15187, SoundCategory.field_15248, 1.0F, f);
		}
	}

	private static ProjectileEntity method_18814(World world, LivingEntity livingEntity, ItemStack itemStack, ItemStack itemStack2) {
		ArrowItem arrowItem = (ArrowItem)(itemStack2.getItem() instanceof ArrowItem ? itemStack2.getItem() : Items.field_8107);
		ProjectileEntity projectileEntity = arrowItem.method_7702(world, itemStack2, livingEntity);
		if (livingEntity instanceof PlayerEntity) {
			projectileEntity.setCritical(true);
		}

		projectileEntity.method_7444(SoundEvents.field_14636);
		projectileEntity.setShotFromCrossbow(true);
		int i = EnchantmentHelper.getLevel(Enchantments.field_9132, itemStack);
		if (i > 0) {
			projectileEntity.setFlagByte((byte)i);
		}

		return projectileEntity;
	}

	public static void method_7777(World world, LivingEntity livingEntity, ItemStack itemStack, float f, float g) {
		List<ItemStack> list = method_7785(itemStack);
		float[] fs = method_7780(livingEntity.getRand());

		for (int i = 0; i < list.size(); i++) {
			ItemStack itemStack2 = (ItemStack)list.get(i);
			boolean bl = livingEntity instanceof PlayerEntity && ((PlayerEntity)livingEntity).abilities.creativeMode;
			if (!itemStack2.isEmpty()) {
				if (i == 0) {
					method_7763(world, livingEntity, itemStack, itemStack2, fs[i], bl, f, g, 0.0F);
				} else if (i == 1) {
					method_7763(world, livingEntity, itemStack, itemStack2, fs[i], bl, f, g, -10.0F);
				} else if (i == 2) {
					method_7763(world, livingEntity, itemStack, itemStack2, fs[i], bl, f, g, 10.0F);
				}
			}
		}

		method_7769(world, livingEntity, itemStack);
	}

	private static float[] method_7780(Random random) {
		boolean bl = random.nextBoolean();
		return new float[]{1.0F, method_7784(bl), method_7784(!bl)};
	}

	private static float method_7784(boolean bl) {
		float f = bl ? 0.63F : 0.43F;
		return 1.0F / (random.nextFloat() * 0.5F + 1.8F) + f;
	}

	private static void method_7769(World world, LivingEntity livingEntity, ItemStack itemStack) {
		if (livingEntity instanceof ServerPlayerEntity) {
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)livingEntity;
			if (!world.isClient) {
				Criterions.SHOT_CROSSBOW.method_9115(serverPlayerEntity, itemStack);
			}

			serverPlayerEntity.method_7259(Stats.field_15372.getOrCreateStat(itemStack.getItem()));
		}

		method_7766(itemStack);
	}

	@Override
	public void method_7852(World world, LivingEntity livingEntity, ItemStack itemStack, int i) {
		if (!world.isClient) {
			int j = EnchantmentHelper.getLevel(Enchantments.field_9098, itemStack);
			SoundEvent soundEvent = this.method_7773(j);
			SoundEvent soundEvent2 = j == 0 ? SoundEvents.field_14860 : null;
			float f = (float)(itemStack.getMaxUseTime() - i) / (float)method_7775(itemStack);
			if (f < 0.2F) {
				this.field_7937 = false;
				this.field_7936 = false;
			}

			if (f >= 0.2F && !this.field_7937) {
				this.field_7937 = true;
				world.method_8465(null, livingEntity.x, livingEntity.y, livingEntity.z, soundEvent, SoundCategory.field_15248, 0.5F, 1.0F);
			}

			if (f >= 0.5F && soundEvent2 != null && !this.field_7936) {
				this.field_7936 = true;
				world.method_8465(null, livingEntity.x, livingEntity.y, livingEntity.z, soundEvent2, SoundCategory.field_15248, 0.5F, 1.0F);
			}
		}
	}

	@Override
	public int method_7881(ItemStack itemStack) {
		return method_7775(itemStack) + 3;
	}

	public static int method_7775(ItemStack itemStack) {
		int i = EnchantmentHelper.getLevel(Enchantments.field_9098, itemStack);
		return i == 0 ? 25 : 25 - 5 * i;
	}

	@Override
	public UseAction method_7853(ItemStack itemStack) {
		return UseAction.field_8947;
	}

	private SoundEvent method_7773(int i) {
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

	private static float method_7770(int i, ItemStack itemStack) {
		float f = (float)i / (float)method_7775(itemStack);
		if (f > 1.0F) {
			f = 1.0F;
		}

		return f;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_7851(ItemStack itemStack, @Nullable World world, List<TextComponent> list, TooltipContext tooltipContext) {
		if (method_7781(itemStack)) {
			ItemStack itemStack2 = (ItemStack)method_7785(itemStack).get(0);
			list.add(new TranslatableTextComponent("item.minecraft.crossbow.projectile").append(" ").append(itemStack2.method_7954()));
			if (tooltipContext.isAdvanced() && itemStack2.getItem() == Items.field_8639) {
				List<TextComponent> list2 = Lists.<TextComponent>newArrayList();
				Items.field_8639.method_7851(itemStack2, world, list2, tooltipContext);
				if (!list2.isEmpty()) {
					for (int i = 0; i < list2.size(); i++) {
						list2.set(i, new StringTextComponent("  ").append((TextComponent)list2.get(i)).applyFormat(TextFormat.field_1080));
					}

					list.addAll(list2);
				}
			}
		}
	}
}
