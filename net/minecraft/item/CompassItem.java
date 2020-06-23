/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import java.util.Optional;
import net.minecraft.block.Blocks;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.item.Vanishable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.Tag;
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
        return CompassItem.hasLodestone(stack) || super.hasGlint(stack);
    }

    public static Optional<RegistryKey<World>> getLodestoneDimension(CompoundTag tag) {
        return World.CODEC.parse(NbtOps.INSTANCE, tag.get("LodestoneDimension")).result();
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (world.isClient) {
            return;
        }
        if (CompassItem.hasLodestone(stack)) {
            CompoundTag compoundTag = stack.getOrCreateTag();
            if (compoundTag.contains("LodestoneTracked") && !compoundTag.getBoolean("LodestoneTracked")) {
                return;
            }
            Optional<RegistryKey<World>> optional = CompassItem.getLodestoneDimension(compoundTag);
            if (optional.isPresent() && optional.get() == world.getRegistryKey() && compoundTag.contains("LodestonePos") && !((ServerWorld)world).getPointOfInterestStorage().method_26339(PointOfInterestType.LODESTONE, NbtHelper.toBlockPos(compoundTag.getCompound("LodestonePos")))) {
                compoundTag.remove("LodestonePos");
            }
        }
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockPos blockPos = context.hit.getBlockPos();
        if (context.world.getBlockState(blockPos).isOf(Blocks.LODESTONE)) {
            boolean bl;
            context.world.playSound(null, blockPos, SoundEvents.ITEM_LODESTONE_COMPASS_LOCK, SoundCategory.PLAYERS, 1.0f, 1.0f);
            boolean bl2 = bl = !context.player.abilities.creativeMode && context.stack.getCount() == 1;
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
        return super.useOnBlock(context);
    }

    private void method_27315(RegistryKey<World> registryKey, BlockPos blockPos, CompoundTag compoundTag) {
        compoundTag.put("LodestonePos", NbtHelper.fromBlockPos(blockPos));
        World.CODEC.encodeStart(NbtOps.INSTANCE, registryKey).resultOrPartial(field_24670::error).ifPresent(tag -> compoundTag.put("LodestoneDimension", (Tag)tag));
        compoundTag.putBoolean("LodestoneTracked", true);
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        return CompassItem.hasLodestone(stack) ? "item.minecraft.lodestone_compass" : super.getTranslationKey(stack);
    }
}

