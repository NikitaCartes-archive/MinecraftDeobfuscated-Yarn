package net.minecraft.predicate.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.WritableBookContentComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.collection.CollectionPredicate;
import net.minecraft.text.RawFilteredPair;

public record WritableBookContentPredicate(Optional<CollectionPredicate<RawFilteredPair<String>, WritableBookContentPredicate.RawStringPredicate>> pages)
	implements ComponentSubPredicate<WritableBookContentComponent> {
	public static final Codec<WritableBookContentPredicate> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					CollectionPredicate.createCodec(WritableBookContentPredicate.RawStringPredicate.CODEC)
						.optionalFieldOf("pages")
						.forGetter(WritableBookContentPredicate::pages)
				)
				.apply(instance, WritableBookContentPredicate::new)
	);

	@Override
	public ComponentType<WritableBookContentComponent> getComponentType() {
		return DataComponentTypes.WRITABLE_BOOK_CONTENT;
	}

	public boolean test(ItemStack itemStack, WritableBookContentComponent writableBookContentComponent) {
		return !this.pages.isPresent() || ((CollectionPredicate)this.pages.get()).test((Iterable)writableBookContentComponent.pages());
	}

	public static record RawStringPredicate(String contents) implements Predicate<RawFilteredPair<String>> {
		public static final Codec<WritableBookContentPredicate.RawStringPredicate> CODEC = Codec.STRING
			.xmap(WritableBookContentPredicate.RawStringPredicate::new, WritableBookContentPredicate.RawStringPredicate::contents);

		public boolean test(RawFilteredPair<String> rawFilteredPair) {
			return rawFilteredPair.raw().equals(this.contents);
		}
	}
}
