/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import java.util.function.Predicate;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class BowItem
extends RangedWeaponItem {
    public BowItem(Item.Settings settings) {
        super(settings);
        this.addPropertyGetter(new Identifier("pull"), (itemStack, world, livingEntity) -> {
            if (livingEntity == null) {
                return 0.0f;
            }
            if (livingEntity.getActiveItem().getItem() != Items.BOW) {
                return 0.0f;
            }
            return (float)(itemStack.getMaxUseTime() - livingEntity.getItemUseTimeLeft()) / 20.0f;
        });
        this.addPropertyGetter(new Identifier("pulling"), (itemStack, world, livingEntity) -> livingEntity != null && livingEntity.isUsingItem() && livingEntity.getActiveItem() == itemStack ? 1.0f : 0.0f);
    }

    @Override
    public void onStoppedUsing(ItemStack itemStack, World world, LivingEntity livingEntity, int i) {
        boolean bl2;
        int j;
        float f;
        if (!(livingEntity instanceof PlayerEntity)) {
            return;
        }
        PlayerEntity playerEntity = (PlayerEntity)livingEntity;
        boolean bl = playerEntity.abilities.creativeMode || EnchantmentHelper.getLevel(Enchantments.INFINITY, itemStack) > 0;
        ItemStack itemStack2 = playerEntity.getArrowType(itemStack);
        if (itemStack2.isEmpty() && !bl) {
            return;
        }
        if (itemStack2.isEmpty()) {
            itemStack2 = new ItemStack(Items.ARROW);
        }
        if ((double)(f = BowItem.getPullProgress(j = this.getMaxUseTime(itemStack) - i)) < 0.1) {
            return;
        }
        boolean bl3 = bl2 = bl && itemStack2.getItem() == Items.ARROW;
        if (!world.isClient) {
            int l;
            int k;
            ArrowItem arrowItem = (ArrowItem)(itemStack2.getItem() instanceof ArrowItem ? itemStack2.getItem() : Items.ARROW);
            ProjectileEntity projectileEntity = arrowItem.createArrow(world, itemStack2, playerEntity);
            projectileEntity.setProperties(playerEntity, playerEntity.pitch, playerEntity.yaw, 0.0f, f * 3.0f, 1.0f);
            if (f == 1.0f) {
                projectileEntity.setCritical(true);
            }
            if ((k = EnchantmentHelper.getLevel(Enchantments.POWER, itemStack)) > 0) {
                projectileEntity.setDamage(projectileEntity.getDamage() + (double)k * 0.5 + 0.5);
            }
            if ((l = EnchantmentHelper.getLevel(Enchantments.PUNCH, itemStack)) > 0) {
                projectileEntity.setPunch(l);
            }
            if (EnchantmentHelper.getLevel(Enchantments.FLAME, itemStack) > 0) {
                projectileEntity.setOnFireFor(100);
            }
            itemStack.damage(1, playerEntity, playerEntity2 -> playerEntity2.sendToolBreakStatus(playerEntity.getActiveHand()));
            if (bl2 || playerEntity.abilities.creativeMode && (itemStack2.getItem() == Items.SPECTRAL_ARROW || itemStack2.getItem() == Items.TIPPED_ARROW)) {
                projectileEntity.pickupType = ProjectileEntity.PickupPermission.CREATIVE_ONLY;
            }
            world.spawnEntity(projectileEntity);
        }
        world.playSound(null, playerEntity.x, playerEntity.y, playerEntity.z, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0f, 1.0f / (RANDOM.nextFloat() * 0.4f + 1.2f) + f * 0.5f);
        if (!bl2 && !playerEntity.abilities.creativeMode) {
            itemStack2.decrement(1);
            if (itemStack2.isEmpty()) {
                playerEntity.inventory.removeOne(itemStack2);
            }
        }
        playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
    }

    public static float getPullProgress(int i) {
        float f = (float)i / 20.0f;
        if ((f = (f * f + f * 2.0f) / 3.0f) > 1.0f) {
            f = 1.0f;
        }
        return f;
    }

    @Override
    public int getMaxUseTime(ItemStack itemStack) {
        return 72000;
    }

    @Override
    public UseAction getUseAction(ItemStack itemStack) {
        return UseAction.BOW;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
        boolean bl;
        ItemStack itemStack = playerEntity.getStackInHand(hand);
        boolean bl2 = bl = !playerEntity.getArrowType(itemStack).isEmpty();
        if (playerEntity.abilities.creativeMode || bl) {
            playerEntity.setCurrentHand(hand);
            return TypedActionResult.successWithoutSwing(itemStack);
        }
        if (bl) {
            return TypedActionResult.pass(itemStack);
        }
        return TypedActionResult.fail(itemStack);
    }

    @Override
    public Predicate<ItemStack> getProjectiles() {
        return BOW_PROJECTILES;
    }
}

