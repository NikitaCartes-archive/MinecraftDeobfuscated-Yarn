package net.minecraft.item;

import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.ServerAdvancementLoader;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class PotatoStaffItem extends Item {
	public PotatoStaffItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		PlayerEntity playerEntity = context.getPlayer();
		if (playerEntity instanceof ServerPlayerEntity serverPlayerEntity) {
			if (serverPlayerEntity.interactionManager.isSurvivalLike()) {
				ServerAdvancementLoader serverAdvancementLoader = serverPlayerEntity.getServer().getAdvancementLoader();
				Identifier identifier = new Identifier("potato/enter_the_potato");
				AdvancementEntry advancementEntry = serverAdvancementLoader.get(identifier);
				if (advancementEntry != null) {
					AdvancementProgress advancementProgress = serverPlayerEntity.getAdvancementTracker().getProgress(advancementEntry);
					if (!advancementProgress.isDone()) {
						World world = playerEntity.getWorld();
						serverPlayerEntity.sendMessage(Text.translatable("item.minecraft.potato_staff.unworthy", playerEntity.getDisplayName()));
						world.createExplosion(null, world.getDamageSources().generic(), null, playerEntity.getPos(), 5.0F, true, World.ExplosionSourceType.TNT);
						return ActionResult.FAIL;
					}
				}
			}

			return this.tryPlacePotatoPortal(new ItemPlacementContext(context));
		} else {
			return ActionResult.FAIL;
		}
	}

	public ActionResult tryPlacePotatoPortal(ItemPlacementContext context) {
		if (!context.canPlace()) {
			return ActionResult.FAIL;
		} else {
			BlockState blockState = Blocks.POTATO_PORTAL.getDefaultState();
			PlayerEntity playerEntity = context.getPlayer();
			ShapeContext shapeContext = playerEntity == null ? ShapeContext.absent() : ShapeContext.of(playerEntity);
			if (!blockState.canPlaceAt(context.getWorld(), context.getBlockPos())) {
				return ActionResult.FAIL;
			} else if (!context.getWorld().canPlace(blockState, context.getBlockPos(), shapeContext)) {
				return ActionResult.FAIL;
			} else if (!context.getWorld().setBlockState(context.getBlockPos(), blockState, Block.NOTIFY_ALL_AND_REDRAW)) {
				return ActionResult.FAIL;
			} else {
				BlockPos blockPos = context.getBlockPos();
				World world = context.getWorld();
				PlayerEntity playerEntity2 = context.getPlayer();
				world.playSound(null, blockPos, SoundEvents.ENTITY_MEGASPUD_SUMMON, SoundCategory.BLOCKS, 1.0F, 1.0F);
				world.emitGameEvent(GameEvent.BLOCK_PLACE, blockPos, GameEvent.Emitter.of(playerEntity2, world.getBlockState(blockPos)));
				return ActionResult.success(world.isClient);
			}
		}
	}
}
