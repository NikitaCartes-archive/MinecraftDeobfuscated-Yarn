package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class PotionItem extends Item {
	private static final int MAX_USE_TIME = 32;

	public PotionItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public ItemStack getDefaultStack() {
		return PotionUtil.setPotion(super.getDefaultStack(), Potions.WATER);
	}

	@Override
	public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
		PlayerEntity playerEntity = user instanceof PlayerEntity ? (PlayerEntity)user : null;
		if (playerEntity instanceof ServerPlayerEntity) {
			Criteria.CONSUME_ITEM.trigger((ServerPlayerEntity)playerEntity, stack);
		}

		if (!world.isClient) {
			for (StatusEffectInstance statusEffectInstance : PotionUtil.getPotionEffects(stack)) {
				if (statusEffectInstance.getEffectType().value().isInstant()) {
					statusEffectInstance.getEffectType().value().applyInstantEffect(playerEntity, playerEntity, user, statusEffectInstance.getAmplifier(), 1.0);
				} else {
					user.addStatusEffect(new StatusEffectInstance(statusEffectInstance));
				}
			}
		}

		if (playerEntity != null) {
			playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
			if (!playerEntity.getAbilities().creativeMode) {
				stack.decrement(1);
			}
		}

		if (playerEntity == null || !playerEntity.getAbilities().creativeMode) {
			if (stack.isEmpty()) {
				return new ItemStack(Items.GLASS_BOTTLE);
			}

			if (playerEntity != null) {
				playerEntity.getInventory().insertStack(new ItemStack(Items.GLASS_BOTTLE));
			}
		}

		user.emitGameEvent(GameEvent.DRINK);
		return stack;
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();
		BlockPos blockPos = context.getBlockPos();
		PlayerEntity playerEntity = context.getPlayer();
		ItemStack itemStack = context.getStack();
		BlockState blockState = world.getBlockState(blockPos);
		if (context.getSide() != Direction.DOWN && blockState.isIn(BlockTags.CONVERTABLE_TO_MUD) && PotionUtil.getPotion(itemStack).method_55838(Potions.WATER)) {
			world.playSound(null, blockPos, SoundEvents.ENTITY_GENERIC_SPLASH, SoundCategory.BLOCKS, 1.0F, 1.0F);
			playerEntity.setStackInHand(context.getHand(), ItemUsage.exchangeStack(itemStack, playerEntity, new ItemStack(Items.GLASS_BOTTLE)));
			playerEntity.incrementStat(Stats.USED.getOrCreateStat(itemStack.getItem()));
			if (!world.isClient) {
				ServerWorld serverWorld = (ServerWorld)world;

				for (int i = 0; i < 5; i++) {
					serverWorld.spawnParticles(
						ParticleTypes.SPLASH,
						(double)blockPos.getX() + world.random.nextDouble(),
						(double)(blockPos.getY() + 1),
						(double)blockPos.getZ() + world.random.nextDouble(),
						1,
						0.0,
						0.0,
						0.0,
						1.0
					);
				}
			}

			world.playSound(null, blockPos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
			world.emitGameEvent(null, GameEvent.FLUID_PLACE, blockPos);
			world.setBlockState(blockPos, Blocks.MUD.getDefaultState());
			return ActionResult.success(world.isClient);
		} else {
			return ActionResult.PASS;
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
		return ItemUsage.consumeHeldItem(world, user, hand);
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		return Potion.finishTranslationKey(PotionUtil.getPotion(stack), this.getTranslationKey() + ".effect.");
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		PotionUtil.buildTooltip(stack, tooltip, 1.0F, world == null ? 20.0F : world.getTickManager().getTickRate());
	}
}
