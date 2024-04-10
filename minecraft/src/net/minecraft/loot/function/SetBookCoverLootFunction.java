package net.minecraft.loot.function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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
	public static final MapCodec<SetBookCoverLootFunction> CODEC = RecordCodecBuilder.mapCodec(
		instance -> addConditionsField(instance)
				.<Optional<RawFilteredPair<String>>, Optional<String>, Optional<Integer>>and(
					instance.group(
						RawFilteredPair.createCodec(Codec.string(0, 32)).optionalFieldOf("title").forGetter(function -> function.title),
						Codec.STRING.optionalFieldOf("author").forGetter(function -> function.author),
						Codecs.rangedInt(0, 3).optionalFieldOf("generation").forGetter(function -> function.generation)
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
			(Integer)this.generation.orElseGet(current::generation),
			current.pages(),
			current.resolved()
		);
	}

	@Override
	public LootFunctionType<SetBookCoverLootFunction> getType() {
		return LootFunctionTypes.SET_BOOK_COVER;
	}
}
