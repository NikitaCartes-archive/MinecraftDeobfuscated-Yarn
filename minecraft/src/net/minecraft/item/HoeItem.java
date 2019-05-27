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
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class HoeItem extends ToolItem {
	private final float attackSpeed;
	protected static final Map<Block, BlockState> TILLED_BLOCKS = Maps.<Block, BlockState>newHashMap(
		ImmutableMap.of(
			Blocks.field_10219,
			Blocks.field_10362.getDefaultState(),
			Blocks.field_10194,
			Blocks.field_10362.getDefaultState(),
			Blocks.field_10566,
			Blocks.field_10362.getDefaultState(),
			Blocks.field_10253,
			Blocks.field_10566.getDefaultState()
		)
	);

	public HoeItem(ToolMaterial toolMaterial, float f, Item.Settings settings) {
		super(toolMaterial, settings);
		this.attackSpeed = f;
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
		World world = itemUsageContext.getWorld();
		BlockPos blockPos = itemUsageContext.getBlockPos();
		if (itemUsageContext.getSide() != Direction.field_11033 && world.getBlockState(blockPos.up()).isAir()) {
			BlockState blockState = (BlockState)TILLED_BLOCKS.get(world.getBlockState(blockPos).getBlock());
			if (blockState != null) {
				PlayerEntity playerEntity = itemUsageContext.getPlayer();
				world.playSound(playerEntity, blockPos, SoundEvents.field_14846, SoundCategory.field_15245, 1.0F, 1.0F);
				if (!world.isClient) {
					world.setBlockState(blockPos, blockState, 11);
					if (playerEntity != null) {
						itemUsageContext.getStack().damage(1, playerEntity, playerEntityx -> playerEntityx.sendToolBreakStatus(itemUsageContext.getHand()));
					}
				}

				return ActionResult.field_5812;
			}
		}

		return ActionResult.field_5811;
	}

	@Override
	public boolean postHit(ItemStack itemStack, LivingEntity livingEntity, LivingEntity livingEntity2) {
		itemStack.damage(1, livingEntity2, livingEntityx -> livingEntityx.sendEquipmentBreakStatus(EquipmentSlot.field_6173));
		return true;
	}

	@Override
	public Multimap<String, EntityAttributeModifier> getModifiers(EquipmentSlot equipmentSlot) {
		Multimap<String, EntityAttributeModifier> multimap = super.getModifiers(equipmentSlot);
		if (equipmentSlot == EquipmentSlot.field_6173) {
			multimap.put(
				EntityAttributes.ATTACK_DAMAGE.getId(),
				new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_UUID, "Weapon modifier", 0.0, EntityAttributeModifier.Operation.field_6328)
			);
			multimap.put(
				EntityAttributes.ATTACK_SPEED.getId(),
				new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_UUID, "Weapon modifier", (double)this.attackSpeed, EntityAttributeModifier.Operation.field_6328)
			);
		}

		return multimap;
	}
}
