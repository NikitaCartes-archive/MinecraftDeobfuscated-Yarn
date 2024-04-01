package net.minecraft.item;

import net.minecraft.entity.EyeOfPotatoEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.StructureTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.event.GameEvent;

public class PotatoEyeItem extends Item {
	public PotatoEyeItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = user.getStackInHand(hand);
		if (!(world instanceof ServerWorld serverWorld)) {
			return TypedActionResult.consume(itemStack);
		} else {
			if (user.method_58931("crafted_eyes", 2)) {
				user.method_58932("thrown_eye");
			}

			if (user.method_58931("potato_village", 7)) {
				user.method_58932("thrown_eye_part_two");
			}

			BlockPos blockPos;
			if (!serverWorld.getRegistryKey().equals(World.OVERWORLD) && !serverWorld.isPotato()) {
				blockPos = null;
			} else {
				blockPos = serverWorld.locateStructure(serverWorld.isPotato() ? StructureTags.COLOSSEUM : StructureTags.RUINED_PORTATOL, user.getBlockPos(), 100, false);
			}

			if (blockPos != null) {
				if (user instanceof ServerPlayerEntity serverPlayerEntity) {
					if (world.isPotato()) {
						serverPlayerEntity.method_58793(blockPos);
					} else {
						serverPlayerEntity.method_58792(blockPos);
					}
				}

				EyeOfPotatoEntity eyeOfPotatoEntity = new EyeOfPotatoEntity(world, user.getX(), user.getBodyY(0.5), user.getZ());
				eyeOfPotatoEntity.setItem(itemStack);
				eyeOfPotatoEntity.initTargetPos(blockPos);
				world.emitGameEvent(GameEvent.PROJECTILE_SHOOT, eyeOfPotatoEntity.getPos(), GameEvent.Emitter.of(user));
				world.spawnEntity(eyeOfPotatoEntity);
				world.playSound(
					null,
					user.getX(),
					user.getY(),
					user.getZ(),
					SoundEvents.ENTITY_ENDER_EYE_LAUNCH,
					SoundCategory.NEUTRAL,
					0.5F,
					0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F)
				);
				world.syncWorldEvent(null, WorldEvents.EYE_OF_ENDER_LAUNCHES, user.getBlockPos(), 0);
				itemStack.decrementUnlessCreative(1, user);
				user.incrementStat(Stats.USED.getOrCreateStat(this));
				user.swingHand(hand, true);
			} else {
				world.playSound(
					null,
					user.getX(),
					user.getY(),
					user.getZ(),
					SoundEvents.ENTITY_ENDER_EYE_LAUNCH,
					SoundCategory.NEUTRAL,
					0.5F,
					0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F)
				);
				itemStack.decrementUnlessCreative(1, user);
				user.dropItem(new ItemStack(Items.POTATO_EYE), true);
			}

			return TypedActionResult.success(itemStack);
		}
	}
}
