package net.minecraft.predicate.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.collection.CollectionPredicate;

public record BundleContentsPredicate(Optional<CollectionPredicate<ItemStack, ItemPredicate>> items) implements ComponentSubPredicate<BundleContentsComponent> {
	public static final Codec<BundleContentsPredicate> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(CollectionPredicate.createCodec(ItemPredicate.CODEC).optionalFieldOf("items").forGetter(BundleContentsPredicate::items))
				.apply(instance, BundleContentsPredicate::new)
	);

	@Override
	public ComponentType<BundleContentsComponent> getComponentType() {
		return DataComponentTypes.BUNDLE_CONTENTS;
	}

	public boolean test(ItemStack itemStack, BundleContentsComponent bundleContentsComponent) {
		return !this.items.isPresent() || ((CollectionPredicate)this.items.get()).test(bundleContentsComponent.iterate());
	}
}
