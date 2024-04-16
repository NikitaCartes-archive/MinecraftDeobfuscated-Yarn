package net.minecraft.loot.function;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JavaOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.WrittenBookContentComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.text.RawFilteredPair;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.collection.ListOperation;

public class SetWrittenBookPagesLootFunction extends ConditionalLootFunction {
	public static final Codec<Text> TEXT_CODEC = TextCodecs.CODEC
		.validate(text -> WrittenBookContentComponent.PAGE_CODEC.encodeStart(JavaOps.INSTANCE, text).map(value -> text));
	public static final MapCodec<SetWrittenBookPagesLootFunction> CODEC = RecordCodecBuilder.mapCodec(
		instance -> addConditionsField(instance)
				.<List<RawFilteredPair<Text>>, ListOperation>and(
					instance.group(
						WrittenBookContentComponent.createPagesCodec(TEXT_CODEC).fieldOf("pages").forGetter(function -> function.pages),
						ListOperation.UNLIMITED_SIZE_CODEC.forGetter(function -> function.operation)
					)
				)
				.apply(instance, SetWrittenBookPagesLootFunction::new)
	);
	private final List<RawFilteredPair<Text>> pages;
	private final ListOperation operation;

	protected SetWrittenBookPagesLootFunction(List<LootCondition> conditions, List<RawFilteredPair<Text>> pages, ListOperation operation) {
		super(conditions);
		this.pages = pages;
		this.operation = operation;
	}

	@Override
	protected ItemStack process(ItemStack stack, LootContext context) {
		stack.apply(DataComponentTypes.WRITTEN_BOOK_CONTENT, WrittenBookContentComponent.DEFAULT, this::apply);
		return stack;
	}

	@VisibleForTesting
	public WrittenBookContentComponent apply(WrittenBookContentComponent current) {
		List<RawFilteredPair<Text>> list = this.operation.apply(current.pages(), this.pages);
		return current.withPages(list);
	}

	@Override
	public LootFunctionType<SetWrittenBookPagesLootFunction> getType() {
		return LootFunctionTypes.SET_WRITTEN_BOOK_PAGES;
	}
}
