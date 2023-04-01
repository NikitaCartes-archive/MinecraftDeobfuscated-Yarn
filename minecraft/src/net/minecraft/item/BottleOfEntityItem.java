package net.minecraft.item;

import java.util.Optional;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class BottleOfEntityItem extends Item {
	public static final String ENTITY_TAG_NBT_KEY = "entityTag";

	public BottleOfEntityItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
		user.emitGameEvent(GameEvent.DRINK);
		if (!world.isClient) {
			transformUser(stack, world, user);
			return Items.GLASS_BOTTLE.getDefaultStack();
		} else {
			return stack;
		}
	}

	public static void transformUser(ItemStack stack, World world, LivingEntity user) {
		NbtCompound nbtCompound = stack.getNbt();
		if (nbtCompound != null && nbtCompound.contains("entityTag", NbtElement.COMPOUND_TYPE)) {
			NbtCompound nbtCompound2 = nbtCompound.getCompound("entityTag");
			EntityType<?> entityType = world.getRegistryManager().get(RegistryKeys.ENTITY_TYPE).get(new Identifier(nbtCompound2.getString("id")));
			if (entityType != null) {
				user.editTransformation(transformationType -> transformationType.withEntity(entityType, Optional.ofNullable(nbtCompound2)));
			}
		}
	}

	@Override
	public int getMaxUseTime(ItemStack stack) {
		return 32;
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.DRINK;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		return ItemUsage.consumeHeldItem(world, user, hand);
	}

	@Override
	public Text getName(ItemStack stack) {
		NbtCompound nbtCompound = stack.getNbt();
		if (nbtCompound != null && nbtCompound.contains("entityTag", NbtElement.COMPOUND_TYPE)) {
			NbtCompound nbtCompound2 = nbtCompound.getCompound("entityTag");
			EntityType<?> entityType = Registries.ENTITY_TYPE.get(new Identifier(nbtCompound2.getString("id")));
			return Text.translatable("item.minecraft.bottle_of_entity.specific", Text.translatable(entityType.getTranslationKey()));
		} else {
			return super.getName(stack);
		}
	}
}
