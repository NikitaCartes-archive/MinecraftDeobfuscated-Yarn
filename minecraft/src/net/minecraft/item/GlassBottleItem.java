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
		List<AreaEffectCloudEntity> list = world.getEntitiesByClass(
			AreaEffectCloudEntity.class,
			user.getBoundingBox().expand(2.0),
			entity -> entity != null && entity.isAlive() && entity.getOwner() instanceof EnderDragonEntity
		);
		ItemStack itemStack = user.getStackInHand(hand);
		if (!list.isEmpty()) {
			AreaEffectCloudEntity areaEffectCloudEntity = (AreaEffectCloudEntity)list.get(0);
			areaEffectCloudEntity.setRadius(areaEffectCloudEntity.getRadius() - 0.5F);
			world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.field_15029, SoundCategory.field_15254, 1.0F, 1.0F);
			return TypedActionResult.method_29237(this.fill(itemStack, user, new ItemStack(Items.field_8613)), world.isClient());
		} else {
			HitResult hitResult = rayTrace(world, user, RayTraceContext.FluidHandling.field_1345);
			if (hitResult.getType() == HitResult.Type.field_1333) {
				return TypedActionResult.pass(itemStack);
			} else {
				if (hitResult.getType() == HitResult.Type.field_1332) {
					BlockPos blockPos = ((BlockHitResult)hitResult).getBlockPos();
					if (!world.canPlayerModifyAt(user, blockPos)) {
						return TypedActionResult.pass(itemStack);
					}

					if (world.getFluidState(blockPos).isIn(FluidTags.field_15517)) {
						world.playSound(user, user.getX(), user.getY(), user.getZ(), SoundEvents.field_14779, SoundCategory.field_15254, 1.0F, 1.0F);
						return TypedActionResult.method_29237(
							this.fill(itemStack, user, PotionUtil.setPotion(new ItemStack(Items.field_8574), Potions.field_8991)), world.isClient()
						);
					}
				}

				return TypedActionResult.pass(itemStack);
			}
		}
	}

	protected ItemStack fill(ItemStack itemStack, PlayerEntity playerEntity, ItemStack itemStack2) {
		playerEntity.incrementStat(Stats.field_15372.getOrCreateStat(this));
		return ItemUsage.method_30012(itemStack, playerEntity, itemStack2);
	}
}
