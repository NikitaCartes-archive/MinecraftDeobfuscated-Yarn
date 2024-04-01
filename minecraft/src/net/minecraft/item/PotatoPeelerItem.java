package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PotatoPeelsBlock;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class PotatoPeelerItem extends Item {
	public PotatoPeelerItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
		return !miner.isCreative();
	}

	@Override
	public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		stack.damage(1, attacker, EquipmentSlot.MAINHAND);
		return true;
	}

	public static AttributeModifiersComponent createAttributeModifiersComponent(int attackDamage, float attackSpeed) {
		return AttributeModifiersComponent.builder()
			.add(
				EntityAttributes.GENERIC_ATTACK_DAMAGE,
				new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier", (double)attackDamage, EntityAttributeModifier.Operation.ADD_VALUE),
				AttributeModifierSlot.MAINHAND
			)
			.add(
				EntityAttributes.GENERIC_ATTACK_SPEED,
				new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Weapon modifier", (double)attackSpeed, EntityAttributeModifier.Operation.ADD_VALUE),
				AttributeModifierSlot.MAINHAND
			)
			.build();
	}

	private static float getRandomSoundPitch(World world) {
		return world.random.nextBetweenInclusive(0.8F, 1.2F);
	}

	public static void playPeelBlockSound(World world, @Nullable PlayerEntity player, BlockPos pos, SoundCategory category) {
		world.playSound(player, pos, SoundEvents.ENTITY_POTATO_PEEL, category, 1.0F, getRandomSoundPitch(world));
	}

	public static void playPeelEntitySound(World world, Entity entity) {
		entity.playSound(SoundEvents.ENTITY_POTATO_PEEL, 1.0F, getRandomSoundPitch(world));
	}

	public static void playPeelSheepSound(World world, Entity entity, SoundCategory category) {
		world.playSoundFromEntity(null, entity, SoundEvents.ENTITY_POTATO_PEEL, category, 1.0F, getRandomSoundPitch(world));
	}

	private static ActionResult peelBlock(ItemUsageContext context, ItemStack peelsStack, BlockState resultState) {
		World world = context.getWorld();
		BlockPos blockPos = context.getBlockPos();
		PlayerEntity playerEntity = context.getPlayer();
		ItemStack itemStack = context.getStack();
		playPeelBlockSound(world, playerEntity, blockPos, SoundCategory.BLOCKS);
		world.setBlockState(blockPos, resultState);
		if (world instanceof ServerWorld) {
			if (resultState.isAir()) {
				Block.dropStack(world, blockPos, peelsStack);
			} else {
				Block.dropStack(world, blockPos, context.getSide(), peelsStack);
			}
		}

		if (playerEntity != null) {
			itemStack.damage(1, playerEntity, LivingEntity.getSlotForHand(context.getHand()));
		}

		if (playerEntity instanceof ServerPlayerEntity serverPlayerEntity) {
			Criteria.PEEL_BLOCK.trigger(serverPlayerEntity);
		}

		return ActionResult.success(world.isClient);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();
		BlockPos blockPos = context.getBlockPos();
		BlockState blockState = world.getBlockState(blockPos);
		Block block = blockState.getBlock();
		if (block instanceof PotatoPeelsBlock potatoPeelsBlock) {
			return peelBlock(context, new ItemStack(potatoPeelsBlock.getPeelsItem(), 9), Blocks.AIR.getDefaultState());
		} else if (block == Blocks.PEELGRASS_BLOCK && context.getSide() == Direction.UP) {
			return peelBlock(context, Items.POTATO_PEELS.get(PotatoPeelsItem.PEELGRASS_COLOR).getDefaultStack(), Blocks.TERRE_DE_POMME.getDefaultState());
		} else if (block == Blocks.CORRUPTED_PEELGRASS_BLOCK && context.getSide() == Direction.UP) {
			return peelBlock(context, Items.CORRUPTED_POTATO_PEELS.getDefaultStack(), Blocks.TERRE_DE_POMME.getDefaultState());
		} else {
			return block == Blocks.POISONOUS_POTATO_BLOCK
				? peelBlock(context, new ItemStack(Items.POISONOUS_POTATO, 9), ((Block)Blocks.POTATO_PEELS_BLOCKS.get(DyeColor.WHITE)).getDefaultState())
				: super.useOnBlock(context);
		}
	}
}
