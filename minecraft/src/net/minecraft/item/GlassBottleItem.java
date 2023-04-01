package net.minecraft.item;

import java.util.List;
import net.minecraft.class_8293;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class GlassBottleItem extends Item {
	public static final int field_44152 = 32;

	public GlassBottleItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		if (class_8293.field_43540.method_50116() && user.getAir() < user.getMaxAir()) {
			return ItemUsage.consumeHeldItem(world, user, hand);
		} else {
			List<AreaEffectCloudEntity> list = world.getEntitiesByClass(
				AreaEffectCloudEntity.class,
				user.getBoundingBox().expand(2.0),
				entity -> entity != null && entity.isAlive() && entity.getOwner() instanceof EnderDragonEntity
			);
			ItemStack itemStack = user.getStackInHand(hand);
			if (!list.isEmpty()) {
				AreaEffectCloudEntity areaEffectCloudEntity = (AreaEffectCloudEntity)list.get(0);
				areaEffectCloudEntity.setRadius(areaEffectCloudEntity.getRadius() - 0.5F);
				world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ITEM_BOTTLE_FILL_DRAGONBREATH, SoundCategory.NEUTRAL, 1.0F, 1.0F);
				world.emitGameEvent(user, GameEvent.FLUID_PICKUP, user.getPos());
				if (user instanceof ServerPlayerEntity serverPlayerEntity) {
					Criteria.PLAYER_INTERACTED_WITH_ENTITY.trigger(serverPlayerEntity, itemStack, areaEffectCloudEntity);
				}

				return TypedActionResult.success(this.fill(itemStack, user, new ItemStack(Items.DRAGON_BREATH)), world.isClient());
			} else {
				HitResult hitResult = raycast(world, user, RaycastContext.FluidHandling.SOURCE_ONLY);
				if (hitResult.getType() == HitResult.Type.BLOCK) {
					BlockPos blockPos = ((BlockHitResult)hitResult).getBlockPos();
					if (!world.canPlayerModifyAt(user, blockPos)) {
						return TypedActionResult.pass(itemStack);
					}

					if (world.getFluidState(blockPos).isIn(FluidTags.WATER)) {
						world.playSound(user, user.getX(), user.getY(), user.getZ(), SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0F, 1.0F);
						world.emitGameEvent(user, GameEvent.FLUID_PICKUP, blockPos);
						return TypedActionResult.success(this.fill(itemStack, user, PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.WATER)), world.isClient());
					}
				}

				return class_8293.field_43540.method_50116() ? ItemUsage.consumeHeldItem(world, user, hand) : TypedActionResult.pass(itemStack);
			}
		}
	}

	@Override
	public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
		PlayerEntity playerEntity = user instanceof PlayerEntity ? (PlayerEntity)user : null;
		if (playerEntity instanceof ServerPlayerEntity) {
			Criteria.CONSUME_ITEM.trigger((ServerPlayerEntity)playerEntity, stack);
			playerEntity.setAir(Math.min(playerEntity.getMaxAir(), playerEntity.getAir() + 100));
		}

		if (playerEntity != null) {
			playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
			if (!playerEntity.getAbilities().creativeMode) {
				stack.decrement(1);
			}
		}

		if (playerEntity == null || !playerEntity.getAbilities().creativeMode) {
			if (stack.isEmpty()) {
				return new ItemStack(Items.BOTTLE_OF_VOID);
			}

			if (playerEntity != null) {
				playerEntity.getInventory().insertStack(new ItemStack(Items.BOTTLE_OF_VOID));
			}
		}

		user.emitGameEvent(GameEvent.DRINK);
		return stack;
	}

	protected ItemStack fill(ItemStack stack, PlayerEntity player, ItemStack outputStack) {
		player.incrementStat(Stats.USED.getOrCreateStat(this));
		return ItemUsage.exchangeStack(stack, player, outputStack);
	}

	@Override
	public int getMaxUseTime(ItemStack stack) {
		return 32;
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.DRINK;
	}

	@Override
	public String getTranslationKey() {
		return class_8293.field_43540.method_50116() ? "item.minecraft.glass_bottle_air" : super.getTranslationKey();
	}
}
