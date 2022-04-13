/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity;

import java.util.Optional;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public interface Bucketable {
    public boolean isFromBucket();

    public void setFromBucket(boolean var1);

    public void copyDataToStack(ItemStack var1);

    public void copyDataFromNbt(NbtCompound var1);

    public ItemStack getBucketItem();

    public SoundEvent getBucketFillSound();

    @Deprecated
    public static void copyDataToStack(MobEntity entity, ItemStack stack) {
        NbtCompound nbtCompound = stack.getOrCreateNbt();
        if (entity.hasCustomName()) {
            stack.setCustomName(entity.getCustomName());
        }
        if (entity.isAiDisabled()) {
            nbtCompound.putBoolean("NoAI", entity.isAiDisabled());
        }
        if (entity.isSilent()) {
            nbtCompound.putBoolean("Silent", entity.isSilent());
        }
        if (entity.hasNoGravity()) {
            nbtCompound.putBoolean("NoGravity", entity.hasNoGravity());
        }
        if (entity.isGlowingLocal()) {
            nbtCompound.putBoolean("Glowing", entity.isGlowingLocal());
        }
        if (entity.isInvulnerable()) {
            nbtCompound.putBoolean("Invulnerable", entity.isInvulnerable());
        }
        nbtCompound.putFloat("Health", entity.getHealth());
    }

    @Deprecated
    public static void copyDataFromNbt(MobEntity entity, NbtCompound nbt) {
        if (nbt.contains("NoAI")) {
            entity.setAiDisabled(nbt.getBoolean("NoAI"));
        }
        if (nbt.contains("Silent")) {
            entity.setSilent(nbt.getBoolean("Silent"));
        }
        if (nbt.contains("NoGravity")) {
            entity.setNoGravity(nbt.getBoolean("NoGravity"));
        }
        if (nbt.contains("Glowing")) {
            entity.setGlowing(nbt.getBoolean("Glowing"));
        }
        if (nbt.contains("Invulnerable")) {
            entity.setInvulnerable(nbt.getBoolean("Invulnerable"));
        }
        if (nbt.contains("Health", NbtElement.NUMBER_TYPE)) {
            entity.setHealth(nbt.getFloat("Health"));
        }
    }

    public static <T extends LivingEntity> Optional<ActionResult> tryBucket(PlayerEntity player, Hand hand, T entity) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (itemStack.getItem() == Items.WATER_BUCKET && entity.isAlive()) {
            entity.playSound(((Bucketable)((Object)entity)).getBucketFillSound(), 1.0f, 1.0f);
            ItemStack itemStack2 = ((Bucketable)((Object)entity)).getBucketItem();
            ((Bucketable)((Object)entity)).copyDataToStack(itemStack2);
            ItemStack itemStack3 = ItemUsage.exchangeStack(itemStack, player, itemStack2, false);
            player.setStackInHand(hand, itemStack3);
            World world = entity.world;
            if (!world.isClient) {
                Criteria.FILLED_BUCKET.trigger((ServerPlayerEntity)player, itemStack2);
            }
            entity.discard();
            return Optional.of(ActionResult.success(world.isClient));
        }
        return Optional.empty();
    }
}

