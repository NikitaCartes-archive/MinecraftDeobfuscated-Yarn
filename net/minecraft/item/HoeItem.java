/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class HoeItem
extends ToolItem {
    private final float swingSpeed;
    protected static final Map<Block, BlockState> BLOCK_TRANSFORMATIONS_MAP = Maps.newHashMap(ImmutableMap.of(Blocks.GRASS_BLOCK, Blocks.FARMLAND.getDefaultState(), Blocks.GRASS_PATH, Blocks.FARMLAND.getDefaultState(), Blocks.DIRT, Blocks.FARMLAND.getDefaultState(), Blocks.COARSE_DIRT, Blocks.DIRT.getDefaultState()));

    public HoeItem(ToolMaterial toolMaterial, float f, Item.Settings settings) {
        super(toolMaterial, settings);
        this.swingSpeed = f;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
        BlockState blockState;
        World world = itemUsageContext.getWorld();
        BlockPos blockPos = itemUsageContext.getBlockPos();
        if (itemUsageContext.getFacing() != Direction.DOWN && world.getBlockState(blockPos.up()).isAir() && (blockState = BLOCK_TRANSFORMATIONS_MAP.get(world.getBlockState(blockPos).getBlock())) != null) {
            PlayerEntity playerEntity2 = itemUsageContext.getPlayer();
            world.playSound(playerEntity2, blockPos, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0f, 1.0f);
            if (!world.isClient) {
                world.setBlockState(blockPos, blockState, 11);
                if (playerEntity2 != null) {
                    itemUsageContext.getItemStack().applyDamage(1, playerEntity2, playerEntity -> playerEntity.sendToolBreakStatus(itemUsageContext.getHand()));
                }
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    @Override
    public boolean onEntityDamaged(ItemStack itemStack, LivingEntity livingEntity2, LivingEntity livingEntity22) {
        itemStack.applyDamage(1, livingEntity22, livingEntity -> livingEntity.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
        return true;
    }

    @Override
    public Multimap<String, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot equipmentSlot) {
        Multimap<String, EntityAttributeModifier> multimap = super.getAttributeModifiers(equipmentSlot);
        if (equipmentSlot == EquipmentSlot.MAINHAND) {
            multimap.put(EntityAttributes.ATTACK_DAMAGE.getId(), new EntityAttributeModifier(MODIFIER_DAMAGE, "Weapon modifier", 0.0, EntityAttributeModifier.Operation.ADDITION));
            multimap.put(EntityAttributes.ATTACK_SPEED.getId(), new EntityAttributeModifier(MODIFIER_SWING_SPEED, "Weapon modifier", (double)this.swingSpeed, EntityAttributeModifier.Operation.ADDITION));
        }
        return multimap;
    }
}

