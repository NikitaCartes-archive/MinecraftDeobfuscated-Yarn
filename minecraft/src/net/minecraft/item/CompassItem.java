package net.minecraft.item;

import java.util.Optional;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
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
	private static final Logger LOGGER = LogManager.getLogger();
	public static final String LODESTONE_POS_KEY = "LodestonePos";
	public static final String LODESTONE_DIMENSION_KEY = "LodestoneDimension";
	public static final String LODESTONE_TRACKED_KEY = "LodestoneTracked";

	public CompassItem(Item.Settings settings) {
		super(settings);
	}

	public static boolean hasLodestone(ItemStack stack) {
		NbtCompound nbtCompound = stack.getTag();
		return nbtCompound != null && (nbtCompound.contains("LodestoneDimension") || nbtCompound.contains("LodestonePos"));
	}

	@Override
	public boolean hasGlint(ItemStack stack) {
		return hasLodestone(stack) || super.hasGlint(stack);
	}

	public static Optional<RegistryKey<World>> getLodestoneDimension(NbtCompound nbt) {
		return World.CODEC.parse(NbtOps.INSTANCE, nbt.get("LodestoneDimension")).result();
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
		if (!world.isClient) {
			if (hasLodestone(stack)) {
				NbtCompound nbtCompound = stack.getOrCreateTag();
				if (nbtCompound.contains("LodestoneTracked") && !nbtCompound.getBoolean("LodestoneTracked")) {
					return;
				}

				Optional<RegistryKey<World>> optional = getLodestoneDimension(nbtCompound);
				if (optional.isPresent() && optional.get() == world.getRegistryKey() && nbtCompound.contains("LodestonePos")) {
					BlockPos blockPos = NbtHelper.toBlockPos(nbtCompound.getCompound("LodestonePos"));
					if (!world.isInBuildLimit(blockPos) || !((ServerWorld)world).getPointOfInterestStorage().hasTypeAt(PointOfInterestType.LODESTONE, blockPos)) {
						nbtCompound.remove("LodestonePos");
					}
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
			boolean bl = !playerEntity.getAbilities().creativeMode && itemStack.getCount() == 1;
			if (bl) {
				this.writeNbt(world.getRegistryKey(), blockPos, itemStack.getOrCreateTag());
			} else {
				ItemStack itemStack2 = new ItemStack(Items.COMPASS, 1);
				NbtCompound nbtCompound = itemStack.hasTag() ? itemStack.getTag().copy() : new NbtCompound();
				itemStack2.setTag(nbtCompound);
				if (!playerEntity.getAbilities().creativeMode) {
					itemStack.decrement(1);
				}

				this.writeNbt(world.getRegistryKey(), blockPos, nbtCompound);
				if (!playerEntity.getInventory().insertStack(itemStack2)) {
					playerEntity.dropItem(itemStack2, false);
				}
			}

			return ActionResult.success(world.isClient);
		}
	}

	private void writeNbt(RegistryKey<World> worldKey, BlockPos pos, NbtCompound nbt) {
		nbt.put("LodestonePos", NbtHelper.fromBlockPos(pos));
		World.CODEC.encodeStart(NbtOps.INSTANCE, worldKey).resultOrPartial(LOGGER::error).ifPresent(nbtElement -> nbt.put("LodestoneDimension", nbtElement));
		nbt.putBoolean("LodestoneTracked", true);
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		return hasLodestone(stack) ? "item.minecraft.lodestone_compass" : super.getTranslationKey(stack);
	}
}
