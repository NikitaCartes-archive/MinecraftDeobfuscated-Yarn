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
import net.minecraft.util.ActionResult;
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
	public TypedActionResult<ItemStack> method_7836(World world, PlayerEntity playerEntity, Hand hand) {
		List<AreaEffectCloudEntity> list = world.method_8390(
			AreaEffectCloudEntity.class,
			playerEntity.method_5829().expand(2.0),
			areaEffectCloudEntity -> areaEffectCloudEntity != null
					&& areaEffectCloudEntity.isValid()
					&& areaEffectCloudEntity.method_5601() instanceof EnderDragonEntity
		);
		ItemStack itemStack = playerEntity.method_5998(hand);
		if (!list.isEmpty()) {
			AreaEffectCloudEntity areaEffectCloudEntity = (AreaEffectCloudEntity)list.get(0);
			areaEffectCloudEntity.setRadius(areaEffectCloudEntity.getRadius() - 0.5F);
			world.method_8465(null, playerEntity.x, playerEntity.y, playerEntity.z, SoundEvents.field_15029, SoundCategory.field_15254, 1.0F, 1.0F);
			return new TypedActionResult<>(ActionResult.field_5812, this.method_7725(itemStack, playerEntity, new ItemStack(Items.field_8613)));
		} else {
			HitResult hitResult = method_7872(world, playerEntity, RayTraceContext.FluidHandling.field_1345);
			if (hitResult.getType() == HitResult.Type.NONE) {
				return new TypedActionResult<>(ActionResult.PASS, itemStack);
			} else {
				if (hitResult.getType() == HitResult.Type.BLOCK) {
					BlockPos blockPos = ((BlockHitResult)hitResult).method_17777();
					if (!world.method_8505(playerEntity, blockPos)) {
						return new TypedActionResult<>(ActionResult.PASS, itemStack);
					}

					if (world.method_8316(blockPos).method_15767(FluidTags.field_15517)) {
						world.method_8465(playerEntity, playerEntity.x, playerEntity.y, playerEntity.z, SoundEvents.field_14779, SoundCategory.field_15254, 1.0F, 1.0F);
						return new TypedActionResult<>(
							ActionResult.field_5812, this.method_7725(itemStack, playerEntity, PotionUtil.setPotion(new ItemStack(Items.field_8574), Potions.field_8991))
						);
					}
				}

				return new TypedActionResult<>(ActionResult.PASS, itemStack);
			}
		}
	}

	protected ItemStack method_7725(ItemStack itemStack, PlayerEntity playerEntity, ItemStack itemStack2) {
		itemStack.subtractAmount(1);
		playerEntity.method_7259(Stats.field_15372.getOrCreateStat(this));
		if (itemStack.isEmpty()) {
			return itemStack2;
		} else {
			if (!playerEntity.inventory.method_7394(itemStack2)) {
				playerEntity.method_7328(itemStack2, false);
			}

			return itemStack;
		}
	}
}
