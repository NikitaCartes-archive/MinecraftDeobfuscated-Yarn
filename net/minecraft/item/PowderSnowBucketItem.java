/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import net.fabricmc.yarn.constants.SetBlockStateFlags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.FluidModificationItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class PowderSnowBucketItem
extends BlockItem
implements FluidModificationItem {
    private final SoundEvent placeSound;

    public PowderSnowBucketItem(Block block, SoundEvent placeSound, Item.Settings settings) {
        super(block, settings);
        this.placeSound = placeSound;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        ActionResult actionResult = super.useOnBlock(context);
        PlayerEntity playerEntity = context.getPlayer();
        if (actionResult.isAccepted() && playerEntity != null && !playerEntity.isCreative()) {
            Hand hand = context.getHand();
            playerEntity.setStackInHand(hand, Items.BUCKET.getDefaultStack());
        }
        return actionResult;
    }

    @Override
    public String getTranslationKey() {
        return this.getOrCreateTranslationKey();
    }

    @Override
    protected SoundEvent getPlaceSound(BlockState state) {
        return this.placeSound;
    }

    @Override
    public boolean placeFluid(@Nullable PlayerEntity player, World world, BlockPos pos, @Nullable BlockHitResult hitResult) {
        if (world.isInBuildLimit(pos) && world.isAir(pos)) {
            if (!world.isClient) {
                world.setBlockState(pos, this.getBlock().getDefaultState(), SetBlockStateFlags.DEFAULT);
            }
            world.playSound(player, pos, this.placeSound, SoundCategory.BLOCKS, 1.0f, 1.0f);
            return true;
        }
        return false;
    }
}

