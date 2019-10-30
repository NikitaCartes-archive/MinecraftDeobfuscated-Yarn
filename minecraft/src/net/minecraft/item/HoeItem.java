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
			Blocks.GRASS_BLOCK,
			Blocks.FARMLAND.getDefaultState(),
			Blocks.GRASS_PATH,
			Blocks.FARMLAND.getDefaultState(),
			Blocks.DIRT,
			Blocks.FARMLAND.getDefaultState(),
			Blocks.COARSE_DIRT,
			Blocks.DIRT.getDefaultState()
		)
	);

	public HoeItem(ToolMaterial material, float attackSpeed, Item.Settings settings) {
		super(material, settings);
		this.attackSpeed = attackSpeed;
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();
		BlockPos blockPos = context.getBlockPos();
		if (context.getSide() != Direction.DOWN && world.getBlockState(blockPos.up()).isAir()) {
			BlockState blockState = (BlockState)TILLED_BLOCKS.get(world.getBlockState(blockPos).getBlock());
			if (blockState != null) {
				PlayerEntity playerEntity = context.getPlayer();
				world.playSound(playerEntity, blockPos, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
				if (!world.isClient) {
					world.setBlockState(blockPos, blockState, 11);
					if (playerEntity != null) {
						context.getStack().damage(1, playerEntity, p -> p.sendToolBreakStatus(context.getHand()));
					}
				}

				return ActionResult.SUCCESS;
			}
		}

		return ActionResult.PASS;
	}

	@Override
	public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		stack.damage(1, attacker, e -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
		return true;
	}

	@Override
	public Multimap<String, EntityAttributeModifier> getModifiers(EquipmentSlot slot) {
		Multimap<String, EntityAttributeModifier> multimap = super.getModifiers(slot);
		if (slot == EquipmentSlot.MAINHAND) {
			multimap.put(
				EntityAttributes.ATTACK_DAMAGE.getId(),
				new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_UUID, "Weapon modifier", 0.0, EntityAttributeModifier.Operation.ADDITION)
			);
			multimap.put(
				EntityAttributes.ATTACK_SPEED.getId(),
				new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_UUID, "Weapon modifier", (double)this.attackSpeed, EntityAttributeModifier.Operation.ADDITION)
			);
		}

		return multimap;
	}
}
