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
	private final float swingSpeed;
	protected static final Map<Block, BlockState> BLOCK_TRANSFORMATIONS_MAP = Maps.<Block, BlockState>newHashMap(
		ImmutableMap.of(
			Blocks.field_10219,
			Blocks.field_10362.method_9564(),
			Blocks.field_10194,
			Blocks.field_10362.method_9564(),
			Blocks.field_10566,
			Blocks.field_10362.method_9564(),
			Blocks.field_10253,
			Blocks.field_10566.method_9564()
		)
	);

	public HoeItem(ToolMaterial toolMaterial, float f, Item.Settings settings) {
		super(toolMaterial, settings);
		this.swingSpeed = f;
	}

	@Override
	public ActionResult method_7884(ItemUsageContext itemUsageContext) {
		World world = itemUsageContext.method_8045();
		BlockPos blockPos = itemUsageContext.method_8037();
		if (itemUsageContext.method_8038() != Direction.DOWN && world.method_8320(blockPos.up()).isAir()) {
			BlockState blockState = (BlockState)BLOCK_TRANSFORMATIONS_MAP.get(world.method_8320(blockPos).getBlock());
			if (blockState != null) {
				PlayerEntity playerEntity = itemUsageContext.getPlayer();
				world.method_8396(playerEntity, blockPos, SoundEvents.field_14846, SoundCategory.field_15245, 1.0F, 1.0F);
				if (!world.isClient) {
					world.method_8652(blockPos, blockState, 11);
					if (playerEntity != null) {
						itemUsageContext.getItemStack().applyDamage(1, playerEntity);
					}
				}

				return ActionResult.field_5812;
			}
		}

		return ActionResult.PASS;
	}

	@Override
	public boolean method_7873(ItemStack itemStack, LivingEntity livingEntity, LivingEntity livingEntity2) {
		itemStack.applyDamage(1, livingEntity2);
		return true;
	}

	@Override
	public Multimap<String, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot equipmentSlot) {
		Multimap<String, EntityAttributeModifier> multimap = super.getAttributeModifiers(equipmentSlot);
		if (equipmentSlot == EquipmentSlot.HAND_MAIN) {
			multimap.put(
				EntityAttributes.ATTACK_DAMAGE.getId(), new EntityAttributeModifier(MODIFIER_DAMAGE, "Weapon modifier", 0.0, EntityAttributeModifier.Operation.field_6328)
			);
			multimap.put(
				EntityAttributes.ATTACK_SPEED.getId(),
				new EntityAttributeModifier(MODIFIER_SWING_SPEED, "Weapon modifier", (double)this.swingSpeed, EntityAttributeModifier.Operation.field_6328)
			);
		}

		return multimap;
	}
}
