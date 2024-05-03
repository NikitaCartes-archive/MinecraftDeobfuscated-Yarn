package net.minecraft.predicate.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.WrittenBookContentComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.collection.CollectionPredicate;
import net.minecraft.text.RawFilteredPair;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

public record WrittenBookContentPredicate(
	Optional<CollectionPredicate<RawFilteredPair<Text>, WrittenBookContentPredicate.RawTextPredicate>> pages,
	Optional<String> author,
	Optional<String> title,
	NumberRange.IntRange generation,
	Optional<Boolean> resolved
) implements ComponentSubPredicate<WrittenBookContentComponent> {
	public static final Codec<WrittenBookContentPredicate> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					CollectionPredicate.createCodec(WrittenBookContentPredicate.RawTextPredicate.CODEC).optionalFieldOf("pages").forGetter(WrittenBookContentPredicate::pages),
					Codec.STRING.optionalFieldOf("author").forGetter(WrittenBookContentPredicate::author),
					Codec.STRING.optionalFieldOf("title").forGetter(WrittenBookContentPredicate::title),
					NumberRange.IntRange.CODEC.optionalFieldOf("generation", NumberRange.IntRange.ANY).forGetter(WrittenBookContentPredicate::generation),
					Codec.BOOL.optionalFieldOf("resolved").forGetter(WrittenBookContentPredicate::resolved)
				)
				.apply(instance, WrittenBookContentPredicate::new)
	);

	@Override
	public ComponentType<WrittenBookContentComponent> getComponentType() {
		return DataComponentTypes.WRITTEN_BOOK_CONTENT;
	}

	public boolean test(ItemStack itemStack, WrittenBookContentComponent writtenBookContentComponent) {
		if (this.author.isPresent() && !((String)this.author.get()).equals(writtenBookContentComponent.author())) {
			return false;
		} else if (this.title.isPresent() && !((String)this.title.get()).equals(writtenBookContentComponent.title().raw())) {
			return false;
		} else if (!this.generation.test(writtenBookContentComponent.generation())) {
			return false;
		} else {
			return this.resolved.isPresent() && this.resolved.get() != writtenBookContentComponent.resolved()
				? false
				: !this.pages.isPresent() || ((CollectionPredicate)this.pages.get()).test((Iterable)writtenBookContentComponent.pages());
		}
	}

	public static record RawTextPredicate(Text contents) implements Predicate<RawFilteredPair<Text>> {
		public static final Codec<WrittenBookContentPredicate.RawTextPredicate> CODEC = TextCodecs.CODEC
			.xmap(WrittenBookContentPredicate.RawTextPredicate::new, WrittenBookContentPredicate.RawTextPredicate::contents);

		public boolean test(RawFilteredPair<Text> rawFilteredPair) {
			return rawFilteredPair.raw().equals(this.contents);
		}
	}
}
