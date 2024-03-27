package net.minecraft.predicate.item;

import com.mojang.serialization.Codec;
import java.util.Map;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;

public interface ItemSubPredicate {
	Codec<Map<ItemSubPredicate.Type<?>, ItemSubPredicate>> PREDICATES_MAP_CODEC = Codec.dispatchedMap(
		Registries.ITEM_SUB_PREDICATE_TYPE.getCodec(), ItemSubPredicate.Type::codec
	);

	boolean test(ItemStack stack);

	public static record Type<T extends ItemSubPredicate>(Codec<T> codec) {
	}
}
