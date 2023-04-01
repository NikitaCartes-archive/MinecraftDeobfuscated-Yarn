package net.minecraft.item;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.Registries;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class SplashBottleOfEntityItem extends Item {
	public SplashBottleOfEntityItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = user.getStackInHand(hand);
		if (!world.isClient) {
			PotionEntity potionEntity = new PotionEntity(world, user);
			potionEntity.setItem(itemStack);
			potionEntity.setVelocity(user, user.getPitch(), user.getYaw(), -20.0F, 0.5F, 1.0F);
			world.spawnEntity(potionEntity);
		}

		user.incrementStat(Stats.USED.getOrCreateStat(this));
		if (!user.getAbilities().creativeMode) {
			itemStack.decrement(1);
		}

		return TypedActionResult.success(itemStack, world.isClient());
	}

	@Override
	public Text getName(ItemStack stack) {
		NbtCompound nbtCompound = stack.getNbt();
		if (nbtCompound != null && nbtCompound.contains("entityTag", NbtElement.COMPOUND_TYPE)) {
			NbtCompound nbtCompound2 = nbtCompound.getCompound("entityTag");
			EntityType<?> entityType = Registries.ENTITY_TYPE.get(new Identifier(nbtCompound2.getString("id")));
			return Text.translatable("item.minecraft.splash_bottle_of_entity.specific", Text.translatable(entityType.getTranslationKey()));
		} else {
			return super.getName(stack);
		}
	}
}
