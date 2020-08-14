package net.minecraft.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import net.minecraft.class_5510;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SwordItem extends ToolItem implements Vanishable {
	private final Multimap<EntityAttribute, EntityAttributeModifier> field_23745;

	public SwordItem(ToolMaterial toolMaterial, Item.Settings settings) {
		super(toolMaterial, settings);
		Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
		class_5510.field_26791.method_31245(this.getMaterial(), builder);
		this.field_23745 = builder.build();
	}

	public float getAttackDamage() {
		return class_5510.field_26791.method_31244(this.getMaterial());
	}

	@Override
	public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
		return !miner.isCreative();
	}

	@Override
	public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
		if (state.isOf(Blocks.COBWEB)) {
			return 15.0F;
		} else {
			Material material = state.getMaterial();
			return material != Material.PLANT
					&& material != Material.REPLACEABLE_PLANT
					&& material != Material.UNUSED_PLANT
					&& !state.isIn(BlockTags.LEAVES)
					&& material != Material.GOURD
				? 1.0F
				: 1.5F;
		}
	}

	@Override
	public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		stack.damage(1, attacker, e -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
		return true;
	}

	@Override
	public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
		if (state.getHardness(world, pos) != 0.0F) {
			stack.damage(2, miner, e -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
		}

		return true;
	}

	@Override
	public boolean isEffectiveOn(BlockState state) {
		return state.isOf(Blocks.COBWEB);
	}

	@Override
	public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
		return slot == EquipmentSlot.MAINHAND ? this.field_23745 : super.getAttributeModifiers(slot);
	}
}
