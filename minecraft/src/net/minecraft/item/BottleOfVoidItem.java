package net.minecraft.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.potion.PotionUtil;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.Util;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class BottleOfVoidItem extends Item {
	public BottleOfVoidItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
		user.emitGameEvent(GameEvent.DRINK);
		if (!world.isClient && user instanceof PlayerEntity playerEntity) {
			playerEntity.damageWithModifier(playerEntity.getDamageSources().outOfWorld(), 8.0F);
			StatusEffectInstance statusEffectInstance = createRandomStatusEffect(playerEntity);
			world.playSound(null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0F, 1.0F);
			if (statusEffectInstance != null) {
				ItemStack itemStack = PotionUtil.setCustomPotionEffects(new ItemStack(Items.POTION), List.of(statusEffectInstance));
				return ItemUsage.exchangeStack(stack, playerEntity, itemStack);
			} else {
				return bottleEntity(playerEntity);
			}
		} else {
			return stack;
		}
	}

	@Nullable
	private static StatusEffectInstance createRandomStatusEffect(LivingEntity entity) {
		ArrayList<StatusEffectInstance> arrayList = new ArrayList(entity.getStatusEffects());
		if (!arrayList.isEmpty()) {
			StatusEffectInstance statusEffectInstance = Util.getRandom(arrayList, entity.getRandom());
			entity.removeStatusEffect(statusEffectInstance.getEffectType());
			return new StatusEffectInstance(statusEffectInstance);
		} else {
			return null;
		}
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
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		return user.getStatusEffects().isEmpty() && user.getTransformedLook().entity() == null
			? TypedActionResult.fail(user.getStackInHand(hand))
			: ItemUsage.consumeHeldItem(world, user, hand);
	}

	public static ItemStack bottleEntity(LivingEntity entity) {
		LivingEntity livingEntity = entity;
		if (entity.getTransformedLook().entity() instanceof LivingEntity livingEntity2) {
			entity.editTransformation(transformationType -> transformationType.withEntity(Optional.empty()));
			entity = livingEntity2;
		}

		NbtCompound nbtCompound = new NbtCompound();
		entity.saveSelfNbt(nbtCompound);
		ItemStack itemStack = Items.BOTTLE_OF_ENTITY.getDefaultStack();
		NbtCompound nbtCompound2 = new NbtCompound();
		nbtCompound2.put("entityTag", nbtCompound);
		itemStack.setNbt(nbtCompound2);
		if (livingEntity == entity) {
			entity.damageWithModifier(entity.getDamageSources().outOfWorld(), 1.0F);
		}

		return itemStack;
	}
}
