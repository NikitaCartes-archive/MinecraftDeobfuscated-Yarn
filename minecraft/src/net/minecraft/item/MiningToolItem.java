package net.minecraft.item;

import com.google.common.collect.Multimap;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class MiningToolItem extends ToolItem {
	private final Set<Block> effectiveBlocks;
	protected final float miningSpeed;

	protected MiningToolItem(ToolMaterial toolMaterial, Set<Block> set, Item.Settings settings) {
		super(toolMaterial, settings);
		this.effectiveBlocks = set;
		this.miningSpeed = toolMaterial.getMiningSpeed();
	}

	@Override
	public float getMiningSpeed(ItemStack itemStack, BlockState blockState) {
		return this.effectiveBlocks.contains(blockState.getBlock()) ? this.miningSpeed : 1.0F;
	}

	@Override
	public boolean postHit(ItemStack itemStack, LivingEntity livingEntity, LivingEntity livingEntity2) {
		itemStack.damage(2, livingEntity2, livingEntityx -> livingEntityx.sendEquipmentBreakStatus(EquipmentSlot.field_6173));
		return true;
	}

	@Override
	public boolean postMine(ItemStack itemStack, World world, BlockState blockState, BlockPos blockPos, LivingEntity livingEntity) {
		if (!world.isClient && blockState.getHardness(world, blockPos) != 0.0F) {
			itemStack.damage(1, livingEntity, livingEntityx -> livingEntityx.sendEquipmentBreakStatus(EquipmentSlot.field_6173));
		}

		return true;
	}

	protected abstract ToolType getToolType();

	@Override
	public Multimap<String, EntityAttributeModifier> getModifiers(EquipmentSlot equipmentSlot) {
		Multimap<String, EntityAttributeModifier> multimap = super.getModifiers(equipmentSlot);
		if (equipmentSlot == EquipmentSlot.field_6173) {
			this.getToolType().initModifiers(this.getMaterial(), multimap);
		}

		return multimap;
	}
}
