package net.minecraft.predicate.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.collection.CollectionPredicate;

public record ContainerPredicate(Optional<CollectionPredicate<ItemStack, ItemPredicate>> items) implements ComponentSubPredicate<ContainerComponent> {
	public static final Codec<ContainerPredicate> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(CollectionPredicate.createCodec(ItemPredicate.CODEC).optionalFieldOf("items").forGetter(ContainerPredicate::items))
				.apply(instance, ContainerPredicate::new)
	);

	@Override
	public ComponentType<ContainerComponent> getComponentType() {
		return DataComponentTypes.CONTAINER;
	}

	public boolean test(ItemStack itemStack, ContainerComponent containerComponent) {
		return !this.items.isPresent() || ((CollectionPredicate)this.items.get()).test(containerComponent.iterateNonEmpty());
	}
}
