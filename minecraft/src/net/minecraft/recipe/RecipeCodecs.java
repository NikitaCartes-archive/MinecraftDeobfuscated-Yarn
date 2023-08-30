package net.minecraft.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.dynamic.Codecs;

public class RecipeCodecs {
	private static final Codec<Item> CRAFTING_RESULT_ITEM = Codecs.validate(
		Registries.ITEM.getCodec(), item -> item == Items.AIR ? DataResult.error(() -> "Crafting result must not be minecraft:air") : DataResult.success(item)
	);
	public static final Codec<ItemStack> CRAFTING_RESULT = RecordCodecBuilder.create(
		instance -> instance.group(
					CRAFTING_RESULT_ITEM.fieldOf("item").forGetter(ItemStack::getItem),
					Codecs.createStrictOptionalFieldCodec(Codecs.POSITIVE_INT, "count", 1).forGetter(ItemStack::getCount)
				)
				.apply(instance, ItemStack::new)
	);
	static final Codec<ItemStack> INGREDIENT = Codecs.<Item>validate(
			Registries.ITEM.getCodec(), item -> item == Items.AIR ? DataResult.error(() -> "Empty ingredient not allowed here") : DataResult.success(item)
		)
		.xmap(ItemStack::new, ItemStack::getItem);
}
