package net.minecraft.item;

import java.util.List;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

public class GlassBottleItem extends Item {
	public GlassBottleItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		List<AreaEffectCloudEntity> list = world.getEntities(
			AreaEffectCloudEntity.class,
			user.getBoundingBox().expand(2.0),
			entity -> entity != null && entity.isAlive() && entity.getOwner() instanceof EnderDragonEntity
		);
		ItemStack itemStack = user.getStackInHand(hand);
		if (!list.isEmpty()) {
			AreaEffectCloudEntity areaEffectCloudEntity = (AreaEffectCloudEntity)list.get(0);
			areaEffectCloudEntity.setRadius(areaEffectCloudEntity.getRadius() - 0.5F);
			world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ITEM_BOTTLE_FILL_DRAGONBREATH, SoundCategory.NEUTRAL, 1.0F, 1.0F);
			return TypedActionResult.success(this.fill(itemStack, user, new ItemStack(Items.DRAGON_BREATH)));
		} else {
			HitResult hitResult = rayTrace(world, user, RayTraceContext.FluidHandling.SOURCE_ONLY);
			if (hitResult.getType() == HitResult.Type.MISS) {
				return TypedActionResult.pass(itemStack);
			} else {
				if (hitResult.getType() == HitResult.Type.BLOCK) {
					BlockPos blockPos = ((BlockHitResult)hitResult).getBlockPos();
					if (!world.canPlayerModifyAt(user, blockPos)) {
						return TypedActionResult.pass(itemStack);
					}

					if (world.getFluidState(blockPos).matches(FluidTags.WATER)) {
						world.playSound(user, user.getX(), user.getY(), user.getZ(), SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0F, 1.0F);
						return TypedActionResult.success(this.fill(itemStack, user, PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.WATER)));
					}
				}

				return TypedActionResult.pass(itemStack);
			}
		}
	}

	protected ItemStack fill(ItemStack emptyBottle, PlayerEntity player, ItemStack filledBottle) {
		emptyBottle.decrement(1);
		player.incrementStat(Stats.USED.getOrCreateStat(this));
		if (emptyBottle.isEmpty()) {
			return filledBottle;
		} else {
			if (!player.inventory.insertStack(filledBottle)) {
				player.dropItem(filledBottle, false);
			}

			return emptyBottle;
		}
	}
}
