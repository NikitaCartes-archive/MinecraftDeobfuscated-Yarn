/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import com.google.common.collect.Multimap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class TridentItem
extends Item {
    public TridentItem(Item.Settings settings) {
        super(settings);
        this.addPropertyGetter(new Identifier("throwing"), (itemStack, world, livingEntity) -> livingEntity != null && livingEntity.isUsingItem() && livingEntity.getActiveItem() == itemStack ? 1.0f : 0.0f);
    }

    @Override
    public boolean canMine(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity) {
        return !playerEntity.isCreative();
    }

    @Override
    public UseAction getUseAction(ItemStack itemStack) {
        return UseAction.SPEAR;
    }

    @Override
    public int getMaxUseTime(ItemStack itemStack) {
        return 72000;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean hasEnchantmentGlint(ItemStack itemStack) {
        return false;
    }

    @Override
    public void onStoppedUsing(ItemStack itemStack, World world, LivingEntity livingEntity, int i) {
        if (!(livingEntity instanceof PlayerEntity)) {
            return;
        }
        PlayerEntity playerEntity2 = (PlayerEntity)livingEntity;
        int j = this.getMaxUseTime(itemStack) - i;
        if (j < 10) {
            return;
        }
        int k = EnchantmentHelper.getRiptide(itemStack);
        if (k > 0 && !playerEntity2.isInsideWaterOrRain()) {
            return;
        }
        if (!world.isClient) {
            itemStack.damage(1, playerEntity2, playerEntity -> playerEntity.sendToolBreakStatus(livingEntity.getActiveHand()));
            if (k == 0) {
                TridentEntity tridentEntity = new TridentEntity(world, (LivingEntity)playerEntity2, itemStack);
                tridentEntity.setProperties(playerEntity2, playerEntity2.pitch, playerEntity2.yaw, 0.0f, 2.5f + (float)k * 0.5f, 1.0f);
                if (playerEntity2.abilities.creativeMode) {
                    tridentEntity.pickupType = ProjectileEntity.PickupPermission.CREATIVE_ONLY;
                }
                world.spawnEntity(tridentEntity);
                world.playSoundFromEntity(null, tridentEntity, SoundEvents.ITEM_TRIDENT_THROW, SoundCategory.PLAYERS, 1.0f, 1.0f);
                if (!playerEntity2.abilities.creativeMode) {
                    playerEntity2.inventory.removeOne(itemStack);
                }
            }
        }
        playerEntity2.incrementStat(Stats.USED.getOrCreateStat(this));
        if (k > 0) {
            float f = playerEntity2.yaw;
            float g = playerEntity2.pitch;
            float h = -MathHelper.sin(f * ((float)Math.PI / 180)) * MathHelper.cos(g * ((float)Math.PI / 180));
            float l = -MathHelper.sin(g * ((float)Math.PI / 180));
            float m = MathHelper.cos(f * ((float)Math.PI / 180)) * MathHelper.cos(g * ((float)Math.PI / 180));
            float n = MathHelper.sqrt(h * h + l * l + m * m);
            float o = 3.0f * ((1.0f + (float)k) / 4.0f);
            playerEntity2.addVelocity(h *= o / n, l *= o / n, m *= o / n);
            playerEntity2.method_6018(20);
            if (playerEntity2.onGround) {
                float p = 1.1999999f;
                playerEntity2.move(MovementType.SELF, new Vec3d(0.0, 1.1999999284744263, 0.0));
            }
            SoundEvent soundEvent = k >= 3 ? SoundEvents.ITEM_TRIDENT_RIPTIDE_3 : (k == 2 ? SoundEvents.ITEM_TRIDENT_RIPTIDE_2 : SoundEvents.ITEM_TRIDENT_RIPTIDE_1);
            world.playSoundFromEntity(null, playerEntity2, soundEvent, SoundCategory.PLAYERS, 1.0f, 1.0f);
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
        ItemStack itemStack = playerEntity.getStackInHand(hand);
        if (itemStack.getDamage() >= itemStack.getMaxDamage()) {
            return TypedActionResult.method_22431(itemStack);
        }
        if (EnchantmentHelper.getRiptide(itemStack) > 0 && !playerEntity.isInsideWaterOrRain()) {
            return TypedActionResult.method_22431(itemStack);
        }
        playerEntity.setCurrentHand(hand);
        return TypedActionResult.method_22428(itemStack);
    }

    @Override
    public boolean postHit(ItemStack itemStack, LivingEntity livingEntity2, LivingEntity livingEntity22) {
        itemStack.damage(1, livingEntity22, livingEntity -> livingEntity.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
        return true;
    }

    @Override
    public boolean postMine(ItemStack itemStack, World world, BlockState blockState, BlockPos blockPos, LivingEntity livingEntity2) {
        if ((double)blockState.getHardness(world, blockPos) != 0.0) {
            itemStack.damage(2, livingEntity2, livingEntity -> livingEntity.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
        }
        return true;
    }

    @Override
    public Multimap<String, EntityAttributeModifier> getModifiers(EquipmentSlot equipmentSlot) {
        Multimap<String, EntityAttributeModifier> multimap = super.getModifiers(equipmentSlot);
        if (equipmentSlot == EquipmentSlot.MAINHAND) {
            multimap.put(EntityAttributes.ATTACK_DAMAGE.getId(), new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_UUID, "Tool modifier", 8.0, EntityAttributeModifier.Operation.ADDITION));
            multimap.put(EntityAttributes.ATTACK_SPEED.getId(), new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_UUID, "Tool modifier", (double)-2.9f, EntityAttributeModifier.Operation.ADDITION));
        }
        return multimap;
    }

    @Override
    public int getEnchantability() {
        return 1;
    }
}

