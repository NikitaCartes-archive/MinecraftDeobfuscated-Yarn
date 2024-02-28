package net.minecraft.loot.function;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.dynamic.Codecs;

public class SetLoreLootFunction extends ConditionalLootFunction {
	public static final Codec<SetLoreLootFunction> CODEC = RecordCodecBuilder.create(
		instance -> addConditionsField(instance)
				.<boolean, List<Text>, Optional<LootContext.EntityTarget>>and(
					instance.group(
						Codec.BOOL.fieldOf("replace").orElse(false).forGetter(function -> function.replace),
						TextCodecs.CODEC.listOf().fieldOf("lore").forGetter(function -> function.lore),
						Codecs.createStrictOptionalFieldCodec(LootContext.EntityTarget.CODEC, "entity").forGetter(function -> function.entity)
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
		return (Set<LootContextParameter<?>>)this.entity.map(entity -> Set.of(entity.getParameter())).orElseGet(Set::of);
	}

	@Override
	public ItemStack process(ItemStack stack, LootContext context) {
		stack.apply(DataComponentTypes.LORE, LoreComponent.DEFAULT, component -> new LoreComponent(this.getNewLoreTexts(component, context)));
		return stack;
	}

	private List<Text> getNewLoreTexts(@Nullable LoreComponent current, LootContext context) {
		if (current == null && this.lore.isEmpty()) {
			return List.of();
		} else {
			UnaryOperator<Text> unaryOperator = SetNameLootFunction.applySourceEntity(context, (LootContext.EntityTarget)this.entity.orElse(null));
			Stream<Text> stream = this.lore.stream().map(unaryOperator);
			return !this.replace && current != null ? Stream.concat(current.lines().stream(), stream).toList() : stream.toList();
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
