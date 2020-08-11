package net.minecraft.item;

import java.util.Optional;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtOps;
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
	public boolean hasGlint(ItemStack stack) {
		return hasLodestone(stack) || super.hasGlint(stack);
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
						.hasTypeAt(PointOfInterestType.LODESTONE, NbtHelper.toBlockPos(compoundTag.getCompound("LodestonePos")))) {
					compoundTag.remove("LodestonePos");
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
			boolean bl = !playerEntity.abilities.creativeMode && itemStack.getCount() == 1;
			if (bl) {
				this.method_27315(world.getRegistryKey(), blockPos, itemStack.getOrCreateTag());
			} else {
				ItemStack itemStack2 = new ItemStack(Items.COMPASS, 1);
				CompoundTag compoundTag = itemStack.hasTag() ? itemStack.getTag().copy() : new CompoundTag();
				itemStack2.setTag(compoundTag);
				if (!playerEntity.abilities.creativeMode) {
					itemStack.decrement(1);
				}

				this.method_27315(world.getRegistryKey(), blockPos, compoundTag);
				if (!playerEntity.inventory.insertStack(itemStack2)) {
					playerEntity.dropItem(itemStack2, false);
				}
			}

			return ActionResult.success(world.isClient);
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
