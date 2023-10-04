package net.minecraft.loot.function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;

public class SetNbtLootFunction extends ConditionalLootFunction {
	public static final Codec<SetNbtLootFunction> CODEC = RecordCodecBuilder.create(
		instance -> addConditionsField(instance)
				.and(StringNbtReader.STRINGIFIED_CODEC.fieldOf("tag").forGetter(function -> function.nbt))
				.apply(instance, SetNbtLootFunction::new)
	);
	private final NbtCompound nbt;

	private SetNbtLootFunction(List<LootCondition> conditions, NbtCompound nbt) {
		super(conditions);
		this.nbt = nbt;
	}

	@Override
	public LootFunctionType getType() {
		return LootFunctionTypes.SET_NBT;
	}

	@Override
	public ItemStack process(ItemStack stack, LootContext context) {
		stack.getOrCreateNbt().copyFrom(this.nbt);
		return stack;
	}

	@Deprecated
	public static ConditionalLootFunction.Builder<?> builder(NbtCompound nbt) {
		return builder(conditions -> new SetNbtLootFunction(conditions, nbt));
	}
}
