package net.minecraft.item;

import java.util.Optional;
import net.minecraft.block.Blocks;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.poi.PointOfInterestType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CompassItem extends Item implements Vanishable {
	private static final Logger field_24670 = LogManager.getLogger();

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

	public static Optional<RegistryKey<World>> getLodestoneDimension(CompoundTag tag) {
		return World.CODEC.parse(NbtOps.INSTANCE, tag.get("LodestoneDimension")).result();
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
		if (!world.isClient) {
			if (hasLodestone(stack)) {
				CompoundTag compoundTag = stack.getOrCreateTag();
				if (compoundTag.contains("LodestoneTracked") && !compoundTag.getBoolean("LodestoneTracked")) {
					return;
				}

				Optional<RegistryKey<World>> optional = getLodestoneDimension(compoundTag);
				if (optional.isPresent()
					&& optional.get() == world.getRegistryKey()
					&& compoundTag.contains("LodestonePos")
					&& !((ServerWorld)world)
						.getPointOfInterestStorage()
						.method_26339(PointOfInterestType.LODESTONE, NbtHelper.toBlockPos(compoundTag.getCompound("LodestonePos")))) {
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
				this.method_27315(context.world.getRegistryKey(), blockPos, context.stack.getOrCreateTag());
			} else {
				ItemStack itemStack = new ItemStack(Items.COMPASS, 1);
				CompoundTag compoundTag = context.stack.hasTag() ? context.stack.getTag().copy() : new CompoundTag();
				itemStack.setTag(compoundTag);
				if (!context.player.abilities.creativeMode) {
					context.stack.decrement(1);
				}

				this.method_27315(context.world.getRegistryKey(), blockPos, compoundTag);
				if (!context.player.inventory.insertStack(itemStack)) {
					context.player.dropItem(itemStack, false);
				}
			}

			return ActionResult.success(context.world.isClient);
		}
	}

	private void method_27315(RegistryKey<World> registryKey, BlockPos blockPos, CompoundTag compoundTag) {
		compoundTag.put("LodestonePos", NbtHelper.fromBlockPos(blockPos));
		World.CODEC.encodeStart(NbtOps.INSTANCE, registryKey).resultOrPartial(field_24670::error).ifPresent(tag -> compoundTag.put("LodestoneDimension", tag));
		compoundTag.putBoolean("LodestoneTracked", true);
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		return hasLodestone(stack) ? "item.minecraft.lodestone_compass" : super.getTranslationKey(stack);
	}
}
