package net.minecraft.item;

import java.util.Optional;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.poi.PointOfInterestType;

public class CompassItem extends Item implements Vanishable {
	public CompassItem(Item.Settings settings) {
		super(settings);
	}

	public static boolean hasLodestone(ItemStack stack) {
		CompoundTag compoundTag = stack.getTag();
		return compoundTag != null && (compoundTag.contains("LodestoneDimension") || compoundTag.contains("LodestonePos"));
	}

	@Override
	public boolean hasEnchantmentGlint(ItemStack stack) {
		return hasLodestone(stack) || super.hasEnchantmentGlint(stack);
	}

	public static Optional<DimensionType> getLodestoneDimension(CompoundTag tag) {
		Identifier identifier = Identifier.tryParse(tag.getString("LodestoneDimension"));
		return identifier != null ? Registry.DIMENSION_TYPE.getOrEmpty(identifier) : Optional.empty();
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
		if (!world.isClient) {
			if (hasLodestone(stack)) {
				CompoundTag compoundTag = stack.getOrCreateTag();
				if (compoundTag.contains("LodestoneTracked") && !compoundTag.getBoolean("LodestoneTracked")) {
					return;
				}

				Optional<DimensionType> optional = getLodestoneDimension(compoundTag);
				if (optional.isPresent()
					&& ((DimensionType)optional.get()).equals(world.dimension.getType())
					&& compoundTag.contains("LodestonePos")
					&& !((ServerWorld)world)
						.getPointOfInterestStorage()
						.method_26339(PointOfInterestType.LODESTONE, NbtHelper.toBlockPos((CompoundTag)compoundTag.get("LodestonePos")))) {
					compoundTag.remove("LodestonePos");
				}
			}
		}
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		BlockPos blockPos = context.hit.getBlockPos();
		if (!context.world.getBlockState(blockPos).isOf(Blocks.LODESTONE)) {
			return super.useOnBlock(context);
		} else {
			context.world.playSound(null, blockPos, SoundEvents.ITEM_LODESTONE_COMPASS_LOCK, SoundCategory.PLAYERS, 1.0F, 1.0F);
			boolean bl = !context.player.abilities.creativeMode && context.stack.getCount() == 1;
			if (bl) {
				this.method_27315(context.world.dimension, blockPos, context.stack.getOrCreateTag());
			} else {
				ItemStack itemStack = new ItemStack(Items.COMPASS, 1);
				CompoundTag compoundTag = context.stack.hasTag() ? context.stack.getTag().copy() : new CompoundTag();
				itemStack.setTag(compoundTag);
				if (!context.player.abilities.creativeMode) {
					context.stack.decrement(1);
				}

				this.method_27315(context.world.dimension, blockPos, compoundTag);
				if (!context.player.inventory.insertStack(itemStack)) {
					context.player.dropItem(itemStack, false);
				}
			}

			return ActionResult.SUCCESS;
		}
	}

	private void method_27315(Dimension dimension, BlockPos blockPos, CompoundTag compoundTag) {
		compoundTag.put("LodestonePos", NbtHelper.fromBlockPos(blockPos));
		compoundTag.putString("LodestoneDimension", DimensionType.getId(dimension.getType()).toString());
		compoundTag.putBoolean("LodestoneTracked", true);
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		return hasLodestone(stack) ? "item.minecraft.lodestone_compass" : super.getTranslationKey(stack);
	}
}
