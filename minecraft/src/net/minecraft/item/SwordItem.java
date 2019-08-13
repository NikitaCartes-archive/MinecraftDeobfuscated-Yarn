package net.minecraft.item;

import com.google.common.collect.Multimap;
import net.minecraft.class_4465;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SwordItem extends ToolItem {
	public SwordItem(ToolMaterial toolMaterial, Item.Settings settings) {
		super(toolMaterial, settings);
	}

	public float getAttackDamage() {
		return class_4465.field_20340.method_21755(this.getMaterial());
	}

	@Override
	public boolean canMine(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity) {
		return !playerEntity.isCreative();
	}

	@Override
	public float getMiningSpeed(ItemStack itemStack, BlockState blockState) {
		Block block = blockState.getBlock();
		if (block == Blocks.field_10343) {
			return 15.0F;
		} else {
			Material material = blockState.getMaterial();
			return material != Material.PLANT
					&& material != Material.REPLACEABLE_PLANT
					&& material != Material.UNUSED_PLANT
					&& !blockState.matches(BlockTags.field_15503)
					&& material != Material.PUMPKIN
				? 1.0F
				: 1.5F;
		}
	}

	@Override
	public boolean postHit(ItemStack itemStack, LivingEntity livingEntity, LivingEntity livingEntity2) {
		itemStack.damage(1, livingEntity2, livingEntityx -> livingEntityx.sendEquipmentBreakStatus(EquipmentSlot.field_6173));
		return true;
	}

	@Override
	public boolean postMine(ItemStack itemStack, World world, BlockState blockState, BlockPos blockPos, LivingEntity livingEntity) {
		if (blockState.getHardness(world, blockPos) != 0.0F) {
			itemStack.damage(2, livingEntity, livingEntityx -> livingEntityx.sendEquipmentBreakStatus(EquipmentSlot.field_6173));
		}

		return true;
	}

	@Override
	public boolean isEffectiveOn(BlockState blockState) {
		return blockState.getBlock() == Blocks.field_10343;
	}

	@Override
	public Multimap<String, EntityAttributeModifier> getModifiers(EquipmentSlot equipmentSlot) {
		Multimap<String, EntityAttributeModifier> multimap = super.getModifiers(equipmentSlot);
		if (equipmentSlot == EquipmentSlot.field_6173) {
			class_4465.field_20340.method_21756(this.getMaterial(), multimap);
		}

		return multimap;
	}
}
