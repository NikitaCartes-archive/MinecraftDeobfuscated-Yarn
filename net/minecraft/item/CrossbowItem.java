/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.CrossbowUser;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FireworkEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.Projectile;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CrossbowItem
extends RangedWeaponItem {
    private boolean charged = false;
    private boolean loaded = false;

    public CrossbowItem(Item.Settings settings) {
        super(settings);
        this.addPropertyGetter(new Identifier("pull"), (itemStack, world, livingEntity) -> {
            if (livingEntity == null || itemStack.getItem() != this) {
                return 0.0f;
            }
            if (CrossbowItem.isCharged(itemStack)) {
                return 0.0f;
            }
            return (float)(itemStack.getMaxUseTime() - livingEntity.getItemUseTimeLeft()) / (float)CrossbowItem.getPullTime(itemStack);
        });
        this.addPropertyGetter(new Identifier("pulling"), (itemStack, world, livingEntity) -> livingEntity != null && livingEntity.isUsingItem() && livingEntity.getActiveItem() == itemStack && !CrossbowItem.isCharged(itemStack) ? 1.0f : 0.0f);
        this.addPropertyGetter(new Identifier("charged"), (itemStack, world, livingEntity) -> livingEntity != null && CrossbowItem.isCharged(itemStack) ? 1.0f : 0.0f);
        this.addPropertyGetter(new Identifier("firework"), (itemStack, world, livingEntity) -> livingEntity != null && CrossbowItem.isCharged(itemStack) && CrossbowItem.hasProjectile(itemStack, Items.FIREWORK_ROCKET) ? 1.0f : 0.0f);
    }

    @Override
    public Predicate<ItemStack> getHeldProjectiles() {
        return CROSSBOW_HELD_PROJECTILES;
    }

    @Override
    public Predicate<ItemStack> getProjectiles() {
        return BOW_PROJECTILES;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
        ItemStack itemStack = playerEntity.getStackInHand(hand);
        if (CrossbowItem.isCharged(itemStack)) {
            CrossbowItem.shootAll(world, playerEntity, hand, itemStack, CrossbowItem.getSpeed(itemStack), 1.0f);
            CrossbowItem.setCharged(itemStack, false);
            return new TypedActionResult<ItemStack>(ActionResult.SUCCESS, itemStack);
        }
        if (!playerEntity.getArrowType(itemStack).isEmpty()) {
            if (!CrossbowItem.isCharged(itemStack)) {
                this.charged = false;
                this.loaded = false;
                playerEntity.setCurrentHand(hand);
            }
            return new TypedActionResult<ItemStack>(ActionResult.SUCCESS, itemStack);
        }
        return new TypedActionResult<ItemStack>(ActionResult.FAIL, itemStack);
    }

    @Override
    public void onStoppedUsing(ItemStack itemStack, World world, LivingEntity livingEntity, int i) {
        int j = this.getMaxUseTime(itemStack) - i;
        float f = CrossbowItem.getPullProgress(j, itemStack);
        if (f >= 1.0f && !CrossbowItem.isCharged(itemStack) && CrossbowItem.loadProjectiles(livingEntity, itemStack)) {
            CrossbowItem.setCharged(itemStack, true);
            SoundCategory soundCategory = livingEntity instanceof PlayerEntity ? SoundCategory.PLAYERS : SoundCategory.HOSTILE;
            world.playSound(null, livingEntity.x, livingEntity.y, livingEntity.z, SoundEvents.ITEM_CROSSBOW_LOADING_END, soundCategory, 1.0f, 1.0f / (RANDOM.nextFloat() * 0.5f + 1.0f) + 0.2f);
        }
    }

    private static boolean loadProjectiles(LivingEntity livingEntity, ItemStack itemStack) {
        int i = EnchantmentHelper.getLevel(Enchantments.MULTISHOT, itemStack);
        int j = i == 0 ? 1 : 3;
        boolean bl = livingEntity instanceof PlayerEntity && ((PlayerEntity)livingEntity).abilities.creativeMode;
        ItemStack itemStack2 = livingEntity.getArrowType(itemStack);
        ItemStack itemStack3 = itemStack2.copy();
        for (int k = 0; k < j; ++k) {
            if (k > 0) {
                itemStack2 = itemStack3.copy();
            }
            if (itemStack2.isEmpty() && bl) {
                itemStack2 = new ItemStack(Items.ARROW);
                itemStack3 = itemStack2.copy();
            }
            if (CrossbowItem.loadProjectile(livingEntity, itemStack, itemStack2, k > 0, bl)) continue;
            return false;
        }
        return true;
    }

    private static boolean loadProjectile(LivingEntity livingEntity, ItemStack itemStack, ItemStack itemStack2, boolean bl, boolean bl2) {
        ItemStack itemStack3;
        boolean bl3;
        if (itemStack2.isEmpty()) {
            return false;
        }
        boolean bl4 = bl3 = bl2 && itemStack2.getItem() instanceof ArrowItem;
        if (!(bl3 || bl2 || bl)) {
            itemStack3 = itemStack2.split(1);
            if (itemStack2.isEmpty() && livingEntity instanceof PlayerEntity) {
                ((PlayerEntity)livingEntity).inventory.removeOne(itemStack2);
            }
        } else {
            itemStack3 = itemStack2.copy();
        }
        CrossbowItem.putProjectile(itemStack, itemStack3);
        return true;
    }

    public static boolean isCharged(ItemStack itemStack) {
        CompoundTag compoundTag = itemStack.getTag();
        return compoundTag != null && compoundTag.getBoolean("Charged");
    }

    public static void setCharged(ItemStack itemStack, boolean bl) {
        CompoundTag compoundTag = itemStack.getOrCreateTag();
        compoundTag.putBoolean("Charged", bl);
    }

    private static void putProjectile(ItemStack itemStack, ItemStack itemStack2) {
        CompoundTag compoundTag = itemStack.getOrCreateTag();
        ListTag listTag = compoundTag.containsKey("ChargedProjectiles", 9) ? compoundTag.getList("ChargedProjectiles", 10) : new ListTag();
        CompoundTag compoundTag2 = new CompoundTag();
        itemStack2.toTag(compoundTag2);
        listTag.add(compoundTag2);
        compoundTag.put("ChargedProjectiles", listTag);
    }

    private static List<ItemStack> getProjectiles(ItemStack itemStack) {
        ListTag listTag;
        ArrayList<ItemStack> list = Lists.newArrayList();
        CompoundTag compoundTag = itemStack.getTag();
        if (compoundTag != null && compoundTag.containsKey("ChargedProjectiles", 9) && (listTag = compoundTag.getList("ChargedProjectiles", 10)) != null) {
            for (int i = 0; i < listTag.size(); ++i) {
                CompoundTag compoundTag2 = listTag.getCompoundTag(i);
                list.add(ItemStack.fromTag(compoundTag2));
            }
        }
        return list;
    }

    private static void clearProjectiles(ItemStack itemStack) {
        CompoundTag compoundTag = itemStack.getTag();
        if (compoundTag != null) {
            ListTag listTag = compoundTag.getList("ChargedProjectiles", 9);
            listTag.clear();
            compoundTag.put("ChargedProjectiles", listTag);
        }
    }

    private static boolean hasProjectile(ItemStack itemStack2, Item item) {
        return CrossbowItem.getProjectiles(itemStack2).stream().anyMatch(itemStack -> itemStack.getItem() == item);
    }

    private static void shoot(World world, LivingEntity livingEntity2, Hand hand, ItemStack itemStack, ItemStack itemStack2, float f, boolean bl, float g, float h, float i) {
        Entity projectile;
        boolean bl2;
        if (world.isClient) {
            return;
        }
        boolean bl3 = bl2 = itemStack2.getItem() == Items.FIREWORK_ROCKET;
        if (bl2) {
            projectile = new FireworkEntity(world, itemStack2, livingEntity2.x, livingEntity2.y + (double)livingEntity2.getStandingEyeHeight() - (double)0.15f, livingEntity2.z, true);
        } else {
            projectile = CrossbowItem.createArrow(world, livingEntity2, itemStack, itemStack2);
            if (bl || i != 0.0f) {
                ((ProjectileEntity)projectile).pickupType = ProjectileEntity.PickupPermission.CREATIVE_ONLY;
            }
        }
        if (livingEntity2 instanceof CrossbowUser) {
            CrossbowUser crossbowUser = (CrossbowUser)((Object)livingEntity2);
            crossbowUser.shoot(crossbowUser.getTarget(), itemStack, (Projectile)((Object)projectile), i);
        } else {
            Vec3d vec3d = livingEntity2.getOppositeRotationVector(1.0f);
            Quaternion quaternion = new Quaternion(new Vector3f(vec3d), i, true);
            Vec3d vec3d2 = livingEntity2.getRotationVec(1.0f);
            Vector3f vector3f = new Vector3f(vec3d2);
            vector3f.method_19262(quaternion);
            projectile.setVelocity(vector3f.getX(), vector3f.getY(), vector3f.getZ(), g, h);
        }
        itemStack.damage(bl2 ? 3 : 1, livingEntity2, livingEntity -> livingEntity.sendToolBreakStatus(hand));
        world.spawnEntity(projectile);
        world.playSound(null, livingEntity2.x, livingEntity2.y, livingEntity2.z, SoundEvents.ITEM_CROSSBOW_SHOOT, SoundCategory.PLAYERS, 1.0f, f);
    }

    private static ProjectileEntity createArrow(World world, LivingEntity livingEntity, ItemStack itemStack, ItemStack itemStack2) {
        ArrowItem arrowItem = (ArrowItem)(itemStack2.getItem() instanceof ArrowItem ? itemStack2.getItem() : Items.ARROW);
        ProjectileEntity projectileEntity = arrowItem.createArrow(world, itemStack2, livingEntity);
        if (livingEntity instanceof PlayerEntity) {
            projectileEntity.setCritical(true);
        }
        projectileEntity.setSound(SoundEvents.ITEM_CROSSBOW_HIT);
        projectileEntity.setShotFromCrossbow(true);
        int i = EnchantmentHelper.getLevel(Enchantments.PIERCING, itemStack);
        if (i > 0) {
            projectileEntity.setPierceLevel((byte)i);
        }
        return projectileEntity;
    }

    public static void shootAll(World world, LivingEntity livingEntity, Hand hand, ItemStack itemStack, float f, float g) {
        List<ItemStack> list = CrossbowItem.getProjectiles(itemStack);
        float[] fs = CrossbowItem.getSoundPitches(livingEntity.getRand());
        for (int i = 0; i < list.size(); ++i) {
            boolean bl;
            ItemStack itemStack2 = list.get(i);
            boolean bl2 = bl = livingEntity instanceof PlayerEntity && ((PlayerEntity)livingEntity).abilities.creativeMode;
            if (itemStack2.isEmpty()) continue;
            if (i == 0) {
                CrossbowItem.shoot(world, livingEntity, hand, itemStack, itemStack2, fs[i], bl, f, g, 0.0f);
                continue;
            }
            if (i == 1) {
                CrossbowItem.shoot(world, livingEntity, hand, itemStack, itemStack2, fs[i], bl, f, g, -10.0f);
                continue;
            }
            if (i != 2) continue;
            CrossbowItem.shoot(world, livingEntity, hand, itemStack, itemStack2, fs[i], bl, f, g, 10.0f);
        }
        CrossbowItem.postShoot(world, livingEntity, itemStack);
    }

    private static float[] getSoundPitches(Random random) {
        boolean bl = random.nextBoolean();
        return new float[]{1.0f, CrossbowItem.getSoundPitch(bl), CrossbowItem.getSoundPitch(!bl)};
    }

    private static float getSoundPitch(boolean bl) {
        float f = bl ? 0.63f : 0.43f;
        return 1.0f / (RANDOM.nextFloat() * 0.5f + 1.8f) + f;
    }

    private static void postShoot(World world, LivingEntity livingEntity, ItemStack itemStack) {
        if (livingEntity instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)livingEntity;
            if (!world.isClient) {
                Criterions.SHOT_CROSSBOW.trigger(serverPlayerEntity, itemStack);
            }
            serverPlayerEntity.incrementStat(Stats.USED.getOrCreateStat(itemStack.getItem()));
        }
        CrossbowItem.clearProjectiles(itemStack);
    }

    @Override
    public void usageTick(World world, LivingEntity livingEntity, ItemStack itemStack, int i) {
        if (!world.isClient) {
            int j = EnchantmentHelper.getLevel(Enchantments.QUICK_CHARGE, itemStack);
            SoundEvent soundEvent = this.getQuickChargeSound(j);
            SoundEvent soundEvent2 = j == 0 ? SoundEvents.ITEM_CROSSBOW_LOADING_MIDDLE : null;
            float f = (float)(itemStack.getMaxUseTime() - i) / (float)CrossbowItem.getPullTime(itemStack);
            if (f < 0.2f) {
                this.charged = false;
                this.loaded = false;
            }
            if (f >= 0.2f && !this.charged) {
                this.charged = true;
                world.playSound(null, livingEntity.x, livingEntity.y, livingEntity.z, soundEvent, SoundCategory.PLAYERS, 0.5f, 1.0f);
            }
            if (f >= 0.5f && soundEvent2 != null && !this.loaded) {
                this.loaded = true;
                world.playSound(null, livingEntity.x, livingEntity.y, livingEntity.z, soundEvent2, SoundCategory.PLAYERS, 0.5f, 1.0f);
            }
        }
    }

    @Override
    public int getMaxUseTime(ItemStack itemStack) {
        return CrossbowItem.getPullTime(itemStack) + 3;
    }

    public static int getPullTime(ItemStack itemStack) {
        int i = EnchantmentHelper.getLevel(Enchantments.QUICK_CHARGE, itemStack);
        return i == 0 ? 25 : 25 - 5 * i;
    }

    @Override
    public UseAction getUseAction(ItemStack itemStack) {
        return UseAction.CROSSBOW;
    }

    private SoundEvent getQuickChargeSound(int i) {
        switch (i) {
            case 1: {
                return SoundEvents.ITEM_CROSSBOW_QUICK_CHARGE_1;
            }
            case 2: {
                return SoundEvents.ITEM_CROSSBOW_QUICK_CHARGE_2;
            }
            case 3: {
                return SoundEvents.ITEM_CROSSBOW_QUICK_CHARGE_3;
            }
        }
        return SoundEvents.ITEM_CROSSBOW_LOADING_START;
    }

    private static float getPullProgress(int i, ItemStack itemStack) {
        float f = (float)i / (float)CrossbowItem.getPullTime(itemStack);
        if (f > 1.0f) {
            f = 1.0f;
        }
        return f;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void appendTooltip(ItemStack itemStack, @Nullable World world, List<Text> list, TooltipContext tooltipContext) {
        List<ItemStack> list2 = CrossbowItem.getProjectiles(itemStack);
        if (!CrossbowItem.isCharged(itemStack) || list2.isEmpty()) {
            return;
        }
        ItemStack itemStack2 = list2.get(0);
        list.add(new TranslatableText("item.minecraft.crossbow.projectile", new Object[0]).append(" ").append(itemStack2.toHoverableText()));
        if (tooltipContext.isAdvanced() && itemStack2.getItem() == Items.FIREWORK_ROCKET) {
            ArrayList<Text> list3 = Lists.newArrayList();
            Items.FIREWORK_ROCKET.appendTooltip(itemStack2, world, list3, tooltipContext);
            if (!list3.isEmpty()) {
                for (int i = 0; i < list3.size(); ++i) {
                    list3.set(i, new LiteralText("  ").append((Text)list3.get(i)).formatted(Formatting.GRAY));
                }
                list.addAll(list3);
            }
        }
    }

    private static float getSpeed(ItemStack itemStack) {
        if (itemStack.getItem() == Items.CROSSBOW && CrossbowItem.hasProjectile(itemStack, Items.FIREWORK_ROCKET)) {
            return 1.6f;
        }
        return 3.15f;
    }
}

