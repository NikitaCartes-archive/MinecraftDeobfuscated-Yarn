/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import java.util.Optional;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.item.Vanishable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
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

public class CompassItem
extends Item
implements Vanishable {
    private static final Logger LOGGER = LogManager.getLogger();

    public CompassItem(Item.Settings settings) {
        super(settings);
    }

    public static boolean hasLodestone(ItemStack stack) {
        NbtCompound nbtCompound = stack.getTag();
        return nbtCompound != null && (nbtCompound.contains("LodestoneDimension") || nbtCompound.contains("LodestonePos"));
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return CompassItem.hasLodestone(stack) || super.hasGlint(stack);
    }

    public static Optional<RegistryKey<World>> getLodestoneDimension(NbtCompound tag) {
        return World.CODEC.parse(NbtOps.INSTANCE, tag.get("LodestoneDimension")).result();
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (world.isClient) {
            return;
        }
        if (CompassItem.hasLodestone(stack)) {
            BlockPos blockPos;
            NbtCompound nbtCompound = stack.getOrCreateTag();
            if (nbtCompound.contains("LodestoneTracked") && !nbtCompound.getBoolean("LodestoneTracked")) {
                return;
            }
            Optional<RegistryKey<World>> optional = CompassItem.getLodestoneDimension(nbtCompound);
            if (optional.isPresent() && optional.get() == world.getRegistryKey() && nbtCompound.contains("LodestonePos") && (!world.isInBuildLimit(blockPos = NbtHelper.toBlockPos(nbtCompound.getCompound("LodestonePos"))) || !((ServerWorld)world).getPointOfInterestStorage().hasTypeAt(PointOfInterestType.LODESTONE, blockPos))) {
                nbtCompound.remove("LodestonePos");
            }
        }
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockPos blockPos = context.getBlockPos();
        World world = context.getWorld();
        if (world.getBlockState(blockPos).isOf(Blocks.LODESTONE)) {
            boolean bl;
            world.playSound(null, blockPos, SoundEvents.ITEM_LODESTONE_COMPASS_LOCK, SoundCategory.PLAYERS, 1.0f, 1.0f);
            PlayerEntity playerEntity = context.getPlayer();
            ItemStack itemStack = context.getStack();
            boolean bl2 = bl = !playerEntity.getAbilities().creativeMode && itemStack.getCount() == 1;
            if (bl) {
                this.writeToNbt(world.getRegistryKey(), blockPos, itemStack.getOrCreateTag());
            } else {
                ItemStack itemStack2 = new ItemStack(Items.COMPASS, 1);
                NbtCompound nbtCompound = itemStack.hasTag() ? itemStack.getTag().copy() : new NbtCompound();
                itemStack2.setTag(nbtCompound);
                if (!playerEntity.getAbilities().creativeMode) {
                    itemStack.decrement(1);
                }
                this.writeToNbt(world.getRegistryKey(), blockPos, nbtCompound);
                if (!playerEntity.getInventory().insertStack(itemStack2)) {
                    playerEntity.dropItem(itemStack2, false);
                }
            }
            return ActionResult.success(world.isClient);
        }
        return super.useOnBlock(context);
    }

    private void writeToNbt(RegistryKey<World> worldKey, BlockPos pos, NbtCompound tag) {
        tag.put("LodestonePos", NbtHelper.fromBlockPos(pos));
        World.CODEC.encodeStart(NbtOps.INSTANCE, worldKey).resultOrPartial(LOGGER::error).ifPresent(nbtElement -> tag.put("LodestoneDimension", (NbtElement)nbtElement));
        tag.putBoolean("LodestoneTracked", true);
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        return CompassItem.hasLodestone(stack) ? "item.minecraft.lodestone_compass" : super.getTranslationKey(stack);
    }
}

