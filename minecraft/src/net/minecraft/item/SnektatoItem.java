package net.minecraft.item;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.SnekComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.CaveSpiderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class SnektatoItem extends Item {
	private static final Text REVEALED_TEXT = Text.translatable("item.minecraft.snektato.revealed");

	public SnektatoItem(Item.Settings settings) {
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
	public Text getName(ItemStack stack) {
		SnekComponent snekComponent = stack.get(DataComponentTypes.SNEK);
		return snekComponent != null && snekComponent.revealed() ? REVEALED_TEXT : super.getName(stack);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		user.setCurrentHand(hand);
		ItemStack itemStack = user.getStackInHand(hand);
		return TypedActionResult.success(itemStack, world.isClient);
	}

	@Override
	public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
		world.playSound(
			null,
			user.getX(),
			user.getY(),
			user.getZ(),
			SoundEvents.ENTITY_SPIDER_AMBIENT,
			SoundCategory.PLAYERS,
			1.0F,
			1.0F + (world.random.nextFloat() - world.random.nextFloat()) * 0.4F
		);
		CaveSpiderEntity.tryInflictPoison(user, null);
		user.emitGameEvent(GameEvent.EAT);
		user.damage(world.getDamageSources().magic(), 2.0F);
		stack.set(DataComponentTypes.SNEK, new SnekComponent(true));
		return stack;
	}
}
