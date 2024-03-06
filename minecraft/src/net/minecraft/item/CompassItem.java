package net.minecraft.item;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LodestoneTrackerComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;

public class CompassItem extends Item {
	public CompassItem(Item.Settings settings) {
		super(settings);
	}

	@Nullable
	public static GlobalPos createSpawnPos(World world) {
		return world.getDimension().natural() ? GlobalPos.create(world.getRegistryKey(), world.getSpawnPos()) : null;
	}

	@Override
	public boolean hasGlint(ItemStack stack) {
		return stack.contains(DataComponentTypes.LODESTONE_TRACKER) || super.hasGlint(stack);
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
		if (world instanceof ServerWorld serverWorld) {
			LodestoneTrackerComponent lodestoneTrackerComponent = stack.get(DataComponentTypes.LODESTONE_TRACKER);
			if (lodestoneTrackerComponent != null) {
				LodestoneTrackerComponent lodestoneTrackerComponent2 = lodestoneTrackerComponent.forWorld(serverWorld);
				if (lodestoneTrackerComponent2 != lodestoneTrackerComponent) {
					stack.set(DataComponentTypes.LODESTONE_TRACKER, lodestoneTrackerComponent2);
				}
			}
		}
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		BlockPos blockPos = context.getBlockPos();
		World world = context.getWorld();
		if (!world.getBlockState(blockPos).isOf(Blocks.LODESTONE)) {
			return super.useOnBlock(context);
		} else {
			world.playSound(null, blockPos, SoundEvents.ITEM_LODESTONE_COMPASS_LOCK, SoundCategory.PLAYERS, 1.0F, 1.0F);
			PlayerEntity playerEntity = context.getPlayer();
			ItemStack itemStack = context.getStack();
			boolean bl = !playerEntity.isInCreativeMode() && itemStack.getCount() == 1;
			LodestoneTrackerComponent lodestoneTrackerComponent = new LodestoneTrackerComponent(Optional.of(GlobalPos.create(world.getRegistryKey(), blockPos)), true);
			if (bl) {
				itemStack.set(DataComponentTypes.LODESTONE_TRACKER, lodestoneTrackerComponent);
			} else {
				ItemStack itemStack2 = itemStack.copyComponentsToNewStack(Items.COMPASS, 1);
				itemStack.decrementUnlessCreative(1, playerEntity);
				itemStack2.set(DataComponentTypes.LODESTONE_TRACKER, lodestoneTrackerComponent);
				if (!playerEntity.getInventory().insertStack(itemStack2)) {
					playerEntity.dropItem(itemStack2, false);
				}
			}

			return ActionResult.success(world.isClient);
		}
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		return stack.contains(DataComponentTypes.LODESTONE_TRACKER) ? "item.minecraft.lodestone_compass" : super.getTranslationKey(stack);
	}
}
