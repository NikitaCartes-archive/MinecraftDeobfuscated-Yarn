package net.minecraft.item;

import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class PotionItem extends Item {
	public PotionItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public ItemStack getDefaultStack() {
		ItemStack itemStack = super.getDefaultStack();
		itemStack.set(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(Potions.WATER));
		return itemStack;
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();
		BlockPos blockPos = context.getBlockPos();
		PlayerEntity playerEntity = context.getPlayer();
		ItemStack itemStack = context.getStack();
		PotionContentsComponent potionContentsComponent = itemStack.getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT);
		BlockState blockState = world.getBlockState(blockPos);
		if (context.getSide() != Direction.DOWN && blockState.isIn(BlockTags.CONVERTABLE_TO_MUD) && potionContentsComponent.matches(Potions.WATER)) {
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
			return ActionResult.SUCCESS;
		} else {
			return ActionResult.PASS;
		}
	}

	@Override
	public Text getName(ItemStack stack) {
		return (Text)stack.getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT)
			.potion()
			.map(potion -> Text.translatable(this.translationKey + ".effect." + ((Potion)potion.value()).getBaseName()))
			.orElseGet(() -> Text.translatable(this.translationKey + ".effect.empty"));
	}

	@Override
	public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
		PotionContentsComponent potionContentsComponent = stack.get(DataComponentTypes.POTION_CONTENTS);
		if (potionContentsComponent != null) {
			potionContentsComponent.buildTooltip(tooltip::add, 1.0F, context.getUpdateTickRate());
		}
	}
}
