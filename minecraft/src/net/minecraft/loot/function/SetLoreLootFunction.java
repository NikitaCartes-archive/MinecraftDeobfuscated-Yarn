package net.minecraft.loot.function;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Text;
import net.minecraft.util.dynamic.Codecs;

public class SetLoreLootFunction extends ConditionalLootFunction {
	public static final Codec<SetLoreLootFunction> CODEC = RecordCodecBuilder.create(
		instance -> method_53344(instance)
				.<boolean, List<Text>, Optional<LootContext.EntityTarget>>and(
					instance.group(
						Codec.BOOL.fieldOf("replace").orElse(false).forGetter(setLoreLootFunction -> setLoreLootFunction.replace),
						Codecs.TEXT.listOf().fieldOf("lore").forGetter(setLoreLootFunction -> setLoreLootFunction.lore),
						Codecs.createStrictOptionalFieldCodec(LootContext.EntityTarget.CODEC, "entity").forGetter(setLoreLootFunction -> setLoreLootFunction.entity)
					)
				)
				.apply(instance, SetLoreLootFunction::new)
	);
	private final boolean replace;
	private final List<Text> lore;
	private final Optional<LootContext.EntityTarget> entity;

	public SetLoreLootFunction(List<LootCondition> conditions, boolean replace, List<Text> lore, Optional<LootContext.EntityTarget> entity) {
		super(conditions);
		this.replace = replace;
		this.lore = List.copyOf(lore);
		this.entity = entity;
	}

	@Override
	public LootFunctionType getType() {
		return LootFunctionTypes.SET_LORE;
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return (Set<LootContextParameter<?>>)this.entity.map(entityTarget -> Set.of(entityTarget.getParameter())).orElseGet(Set::of);
	}

	@Override
	public ItemStack process(ItemStack stack, LootContext context) {
		NbtList nbtList = this.getLoreForMerge(stack, !this.lore.isEmpty());
		if (nbtList != null) {
			if (this.replace) {
				nbtList.clear();
			}

			UnaryOperator<Text> unaryOperator = SetNameLootFunction.applySourceEntity(context, (LootContext.EntityTarget)this.entity.orElse(null));
			this.lore.stream().map(unaryOperator).map(Text.Serializer::toJson).map(NbtString::of).forEach(nbtList::add);
		}

		return stack;
	}

	@Nullable
	private NbtList getLoreForMerge(ItemStack stack, boolean otherLoreExists) {
		NbtCompound nbtCompound;
		if (stack.hasNbt()) {
			nbtCompound = stack.getNbt();
		} else {
			if (!otherLoreExists) {
				return null;
			}

			nbtCompound = new NbtCompound();
			stack.setNbt(nbtCompound);
		}

		NbtCompound nbtCompound2;
		if (nbtCompound.contains("display", NbtElement.COMPOUND_TYPE)) {
			nbtCompound2 = nbtCompound.getCompound("display");
		} else {
			if (!otherLoreExists) {
				return null;
			}

			nbtCompound2 = new NbtCompound();
			nbtCompound.put("display", nbtCompound2);
		}

		if (nbtCompound2.contains("Lore", NbtElement.LIST_TYPE)) {
			return nbtCompound2.getList("Lore", NbtElement.STRING_TYPE);
		} else if (otherLoreExists) {
			NbtList nbtList = new NbtList();
			nbtCompound2.put("Lore", nbtList);
			return nbtList;
		} else {
			return null;
		}
	}

	public static SetLoreLootFunction.Builder builder() {
		return new SetLoreLootFunction.Builder();
	}

	public static class Builder extends ConditionalLootFunction.Builder<SetLoreLootFunction.Builder> {
		private boolean replace;
		private Optional<LootContext.EntityTarget> target = Optional.empty();
		private final ImmutableList.Builder<Text> lore = ImmutableList.builder();

		public SetLoreLootFunction.Builder replace(boolean replace) {
			this.replace = replace;
			return this;
		}

		public SetLoreLootFunction.Builder target(LootContext.EntityTarget target) {
			this.target = Optional.of(target);
			return this;
		}

		public SetLoreLootFunction.Builder lore(Text lore) {
			this.lore.add(lore);
			return this;
		}

		protected SetLoreLootFunction.Builder getThisBuilder() {
			return this;
		}

		@Override
		public LootFunction build() {
			return new SetLoreLootFunction(this.getConditions(), this.replace, this.lore.build(), this.target);
		}
	}
}
