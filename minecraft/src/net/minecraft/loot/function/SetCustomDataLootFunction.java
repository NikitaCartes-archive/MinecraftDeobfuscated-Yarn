package net.minecraft.loot.function;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;

public class SetCustomDataLootFunction extends ConditionalLootFunction {
	public static final MapCodec<SetCustomDataLootFunction> CODEC = RecordCodecBuilder.mapCodec(
		instance -> addConditionsField(instance)
				.and(StringNbtReader.NBT_COMPOUND_CODEC.fieldOf("tag").forGetter(function -> function.nbt))
				.apply(instance, SetCustomDataLootFunction::new)
	);
	private final NbtCompound nbt;

	private SetCustomDataLootFunction(List<LootCondition> conditions, NbtCompound nbt) {
		super(conditions);
		this.nbt = nbt;
	}

	@Override
	public LootFunctionType<SetCustomDataLootFunction> getType() {
		return LootFunctionTypes.SET_CUSTOM_DATA;
	}

	@Override
	public ItemStack process(ItemStack stack, LootContext context) {
		NbtComponent.set(DataComponentTypes.CUSTOM_DATA, stack, nbt -> nbt.copyFrom(this.nbt));
		return stack;
	}

	@Deprecated
	public static ConditionalLootFunction.Builder<?> builder(NbtCompound nbt) {
		return builder(conditions -> new SetCustomDataLootFunction(conditions, nbt));
	}
}
