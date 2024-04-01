package net.minecraft.item;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.XpComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class PotatoOfKnowledgeItem extends Item {
	public PotatoOfKnowledgeItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public int getMaxUseTime(ItemStack stack) {
		return 20;
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.EAT;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		user.setCurrentHand(hand);
		ItemStack itemStack = user.getStackInHand(hand);
		return TypedActionResult.success(itemStack, world.isClient);
	}

	@Override
	public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
		if (user instanceof ServerPlayerEntity serverPlayerEntity) {
			int i = stack.getOrDefault(DataComponentTypes.XP, XpComponent.DEFAULT).value();
			serverPlayerEntity.addExperience(i);
		}

		world.playSound(
			null,
			user.getX(),
			user.getY(),
			user.getZ(),
			SoundEvents.ENTITY_PLAYER_BURP,
			SoundCategory.PLAYERS,
			1.0F,
			1.0F + (world.random.nextFloat() - world.random.nextFloat()) * 0.4F
		);
		stack.decrementUnlessCreative(1, user);
		user.emitGameEvent(GameEvent.EAT);
		return stack;
	}
}
