/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import java.util.List;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

public class GlassBottleItem
extends Item {
    public GlassBottleItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
        List<AreaEffectCloudEntity> list = world.getEntities(AreaEffectCloudEntity.class, playerEntity.getBoundingBox().expand(2.0), areaEffectCloudEntity -> areaEffectCloudEntity != null && areaEffectCloudEntity.isAlive() && areaEffectCloudEntity.getOwner() instanceof EnderDragonEntity);
        ItemStack itemStack = playerEntity.getStackInHand(hand);
        if (!list.isEmpty()) {
            AreaEffectCloudEntity areaEffectCloudEntity2 = list.get(0);
            areaEffectCloudEntity2.setRadius(areaEffectCloudEntity2.getRadius() - 0.5f);
            world.playSound(null, playerEntity.x, playerEntity.y, playerEntity.z, SoundEvents.ITEM_BOTTLE_FILL_DRAGONBREATH, SoundCategory.NEUTRAL, 1.0f, 1.0f);
            return new TypedActionResult<ItemStack>(ActionResult.SUCCESS, this.fill(itemStack, playerEntity, new ItemStack(Items.DRAGON_BREATH)));
        }
        HitResult hitResult = GlassBottleItem.getHitResult(world, playerEntity, RayTraceContext.FluidHandling.SOURCE_ONLY);
        if (hitResult.getType() == HitResult.Type.MISS) {
            return new TypedActionResult<ItemStack>(ActionResult.PASS, itemStack);
        }
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockPos blockPos = ((BlockHitResult)hitResult).getBlockPos();
            if (!world.canPlayerModifyAt(playerEntity, blockPos)) {
                return new TypedActionResult<ItemStack>(ActionResult.PASS, itemStack);
            }
            if (world.getFluidState(blockPos).matches(FluidTags.WATER)) {
                world.playSound(playerEntity, playerEntity.x, playerEntity.y, playerEntity.z, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0f, 1.0f);
                return new TypedActionResult<ItemStack>(ActionResult.SUCCESS, this.fill(itemStack, playerEntity, PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.WATER)));
            }
        }
        return new TypedActionResult<ItemStack>(ActionResult.PASS, itemStack);
    }

    protected ItemStack fill(ItemStack itemStack, PlayerEntity playerEntity, ItemStack itemStack2) {
        itemStack.subtractAmount(1);
        playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
        if (itemStack.isEmpty()) {
            return itemStack2;
        }
        if (!playerEntity.inventory.insertStack(itemStack2)) {
            playerEntity.dropItem(itemStack2, false);
        }
        return itemStack;
    }
}

