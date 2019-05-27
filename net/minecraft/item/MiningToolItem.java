/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import com.google.common.collect.Multimap;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MiningToolItem
extends ToolItem {
    private final Set<Block> effectiveBlocks;
    protected final float miningSpeed;
    protected final float attackDamage;
    protected final float attackSpeed;

    protected MiningToolItem(float f, float g, ToolMaterial toolMaterial, Set<Block> set, Item.Settings settings) {
        super(toolMaterial, settings);
        this.effectiveBlocks = set;
        this.miningSpeed = toolMaterial.getMiningSpeed();
        this.attackDamage = f + toolMaterial.getAttackDamage();
        this.attackSpeed = g;
    }

    @Override
    public float getMiningSpeed(ItemStack itemStack, BlockState blockState) {
        return this.effectiveBlocks.contains(blockState.getBlock()) ? this.miningSpeed : 1.0f;
    }

    @Override
    public boolean postHit(ItemStack itemStack, LivingEntity livingEntity2, LivingEntity livingEntity22) {
        itemStack.damage(2, livingEntity22, livingEntity -> livingEntity.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
        return true;
    }

    @Override
    public boolean postMine(ItemStack itemStack, World world, BlockState blockState, BlockPos blockPos, LivingEntity livingEntity2) {
        if (!world.isClient && blockState.getHardness(world, blockPos) != 0.0f) {
            itemStack.damage(1, livingEntity2, livingEntity -> livingEntity.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
        }
        return true;
    }

    @Override
    public Multimap<String, EntityAttributeModifier> getModifiers(EquipmentSlot equipmentSlot) {
        Multimap<String, EntityAttributeModifier> multimap = super.getModifiers(equipmentSlot);
        if (equipmentSlot == EquipmentSlot.MAINHAND) {
            multimap.put(EntityAttributes.ATTACK_DAMAGE.getId(), new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_UUID, "Tool modifier", (double)this.attackDamage, EntityAttributeModifier.Operation.ADDITION));
            multimap.put(EntityAttributes.ATTACK_SPEED.getId(), new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_UUID, "Tool modifier", (double)this.attackSpeed, EntityAttributeModifier.Operation.ADDITION));
        }
        return multimap;
    }
}

