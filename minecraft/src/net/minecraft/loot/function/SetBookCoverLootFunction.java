package net.minecraft.loot.function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import java.util.Optional;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.WrittenBookContentComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.text.RawFilteredPair;
import net.minecraft.util.dynamic.Codecs;

public class SetBookCoverLootFunction extends ConditionalLootFunction {
	public static final Codec<SetBookCoverLootFunction> CODEC = RecordCodecBuilder.create(
		instance -> addConditionsField(instance)
				.and(
					instance.group(
						Codecs.createStrictOptionalFieldCodec(RawFilteredPair.createCodec(Codecs.string(0, 32)), "title").forGetter(function -> function.title),
						Codecs.createStrictOptionalFieldCodec(Codec.STRING, "author").forGetter(function -> function.author),
						Codecs.createStrictOptionalFieldCodec(Codecs.rangedInt(0, 3), "generation").forGetter(function -> function.generation)
					)
				)
				.apply(instance, SetBookCoverLootFunction::new)
	);
	private final Optional<String> author;
	private final Optional<RawFilteredPair<String>> title;
	private final Optional<Integer> generation;

	public SetBookCoverLootFunction(List<LootCondition> conditions, Optional<RawFilteredPair<String>> title, Optional<String> author, Optional<Integer> generation) {
		super(conditions);
		this.author = author;
		this.title = title;
		this.generation = generation;
	}

	@Override
	protected ItemStack process(ItemStack stack, LootContext context) {
		stack.apply(DataComponentTypes.WRITTEN_BOOK_CONTENT, WrittenBookContentComponent.DEFAULT, this::apply);
		return stack;
	}

	private WrittenBookContentComponent apply(WrittenBookContentComponent current) {
		return new WrittenBookContentComponent(
			(RawFilteredPair<String>)this.title.orElseGet(current::title),
			(String)this.author.orElseGet(current::author),
			this.generation.orElseGet(current::generation),
			current.pages(),
			current.resolved()
		);
	}

	@Override
	public LootFunctionType getType() {
		return LootFunctionTypes.SET_BOOK_COVER;
	}
}
