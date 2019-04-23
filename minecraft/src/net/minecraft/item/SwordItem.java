package net.minecraft.item;

import com.google.common.collect.Multimap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SwordItem extends ToolItem {
	private final float weaponDamage;
	private final float weaponCooldownSpeed;

	public SwordItem(ToolMaterial toolMaterial, int i, float f, Item.Settings settings) {
		super(toolMaterial, settings);
		this.weaponCooldownSpeed = f;
		this.weaponDamage = (float)i + toolMaterial.getAttackDamage();
	}

	public float getWeaponDamage() {
		return this.weaponDamage;
	}

	@Override
	public boolean beforeBlockBreak(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity) {
		return !playerEntity.isCreative();
	}

	@Override
	public float getBlockBreakingSpeed(ItemStack itemStack, BlockState blockState) {
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
	public boolean onEntityDamaged(ItemStack itemStack, LivingEntity livingEntity, LivingEntity livingEntity2) {
		itemStack.applyDamage(1, livingEntity2, livingEntityx -> livingEntityx.sendEquipmentBreakStatus(EquipmentSlot.field_6173));
		return true;
	}

	@Override
	public boolean onBlockBroken(ItemStack itemStack, World world, BlockState blockState, BlockPos blockPos, LivingEntity livingEntity) {
		if (blockState.getHardness(world, blockPos) != 0.0F) {
			itemStack.applyDamage(2, livingEntity, livingEntityx -> livingEntityx.sendEquipmentBreakStatus(EquipmentSlot.field_6173));
		}

		return true;
	}

	@Override
	public boolean isEffectiveOn(BlockState blockState) {
		return blockState.getBlock() == Blocks.field_10343;
	}

	@Override
	public Multimap<String, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot equipmentSlot) {
		Multimap<String, EntityAttributeModifier> multimap = super.getAttributeModifiers(equipmentSlot);
		if (equipmentSlot == EquipmentSlot.field_6173) {
			multimap.put(
				EntityAttributes.ATTACK_DAMAGE.getId(),
				new EntityAttributeModifier(MODIFIER_DAMAGE, "Weapon modifier", (double)this.weaponDamage, EntityAttributeModifier.Operation.field_6328)
			);
			multimap.put(
				EntityAttributes.ATTACK_SPEED.getId(),
				new EntityAttributeModifier(MODIFIER_SWING_SPEED, "Weapon modifier", (double)this.weaponCooldownSpeed, EntityAttributeModifier.Operation.field_6328)
			);
		}

		return multimap;
	}
}
