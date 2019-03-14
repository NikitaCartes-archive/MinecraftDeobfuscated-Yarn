package net.minecraft.item;

import com.google.common.collect.Multimap;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MiningToolItem extends ToolItem {
	private final Set<Block> effectiveBlocks;
	protected final float blockBreakingSpeed;
	protected final float attackDamage;
	protected final float attackSpeed;

	protected MiningToolItem(float f, float g, ToolMaterial toolMaterial, Set<Block> set, Item.Settings settings) {
		super(toolMaterial, settings);
		this.effectiveBlocks = set;
		this.blockBreakingSpeed = toolMaterial.getBlockBreakingSpeed();
		this.attackDamage = f + toolMaterial.getAttackDamage();
		this.attackSpeed = g;
	}

	@Override
	public float getBlockBreakingSpeed(ItemStack itemStack, BlockState blockState) {
		return this.effectiveBlocks.contains(blockState.getBlock()) ? this.blockBreakingSpeed : 1.0F;
	}

	@Override
	public boolean onEntityDamaged(ItemStack itemStack, LivingEntity livingEntity, LivingEntity livingEntity2) {
		itemStack.applyDamage(2, livingEntity2);
		return true;
	}

	@Override
	public boolean onBlockBroken(ItemStack itemStack, World world, BlockState blockState, BlockPos blockPos, LivingEntity livingEntity) {
		if (!world.isClient && blockState.getHardness(world, blockPos) != 0.0F) {
			itemStack.applyDamage(1, livingEntity);
		}

		return true;
	}

	@Override
	public Multimap<String, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot equipmentSlot) {
		Multimap<String, EntityAttributeModifier> multimap = super.getAttributeModifiers(equipmentSlot);
		if (equipmentSlot == EquipmentSlot.HAND_MAIN) {
			multimap.put(
				EntityAttributes.ATTACK_DAMAGE.getId(),
				new EntityAttributeModifier(MODIFIER_DAMAGE, "Tool modifier", (double)this.attackDamage, EntityAttributeModifier.Operation.field_6328)
			);
			multimap.put(
				EntityAttributes.ATTACK_SPEED.getId(),
				new EntityAttributeModifier(MODIFIER_SWING_SPEED, "Tool modifier", (double)this.attackSpeed, EntityAttributeModifier.Operation.field_6328)
			);
		}

		return multimap;
	}
}
