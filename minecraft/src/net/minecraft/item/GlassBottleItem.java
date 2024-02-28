package net.minecraft.item;

import java.util.List;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Potions;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

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
			world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ITEM_BOTTLE_FILL_DRAGONBREATH, SoundCategory.NEUTRAL, 1.0F, 1.0F);
			world.emitGameEvent(user, GameEvent.FLUID_PICKUP, user.getPos());
			if (user instanceof ServerPlayerEntity serverPlayerEntity) {
				Criteria.PLAYER_INTERACTED_WITH_ENTITY.trigger(serverPlayerEntity, itemStack, areaEffectCloudEntity);
			}

			return TypedActionResult.success(this.fill(itemStack, user, new ItemStack(Items.DRAGON_BREATH)), world.isClient());
		} else {
			BlockHitResult blockHitResult = raycast(world, user, RaycastContext.FluidHandling.SOURCE_ONLY);
			if (blockHitResult.getType() == HitResult.Type.MISS) {
				return TypedActionResult.pass(itemStack);
			} else {
				if (blockHitResult.getType() == HitResult.Type.BLOCK) {
					BlockPos blockPos = blockHitResult.getBlockPos();
					if (!world.canPlayerModifyAt(user, blockPos)) {
						return TypedActionResult.pass(itemStack);
					}

					if (world.getFluidState(blockPos).isIn(FluidTags.WATER)) {
						world.playSound(user, user.getX(), user.getY(), user.getZ(), SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0F, 1.0F);
						world.emitGameEvent(user, GameEvent.FLUID_PICKUP, blockPos);
						return TypedActionResult.success(this.fill(itemStack, user, PotionContentsComponent.createStack(Items.POTION, Potions.WATER)), world.isClient());
					}
				}

				return TypedActionResult.pass(itemStack);
			}
		}
	}

	protected ItemStack fill(ItemStack stack, PlayerEntity player, ItemStack outputStack) {
		player.incrementStat(Stats.USED.getOrCreateStat(this));
		return ItemUsage.exchangeStack(stack, player, outputStack);
	}
}
