package net.minecraft.loot.function;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentType;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;

public class CopyComponentsLootFunction extends ConditionalLootFunction {
	public static final MapCodec<CopyComponentsLootFunction> CODEC = RecordCodecBuilder.mapCodec(
		instance -> addConditionsField(instance)
				.<CopyComponentsLootFunction.Source, Optional<List<ComponentType<?>>>, Optional<List<ComponentType<?>>>>and(
					instance.group(
						CopyComponentsLootFunction.Source.CODEC.fieldOf("source").forGetter(function -> function.source),
						ComponentType.CODEC.listOf().optionalFieldOf("include").forGetter(function -> function.include),
						ComponentType.CODEC.listOf().optionalFieldOf("exclude").forGetter(function -> function.exclude)
					)
				)
				.apply(instance, CopyComponentsLootFunction::new)
	);
	private final CopyComponentsLootFunction.Source source;
	private final Optional<List<ComponentType<?>>> include;
	private final Optional<List<ComponentType<?>>> exclude;
	private final Predicate<ComponentType<?>> filter;

	CopyComponentsLootFunction(
		List<LootCondition> conditions, CopyComponentsLootFunction.Source source, Optional<List<ComponentType<?>>> include, Optional<List<ComponentType<?>>> exclude
	) {
		super(conditions);
		this.source = source;
		this.include = include.map(List::copyOf);
		this.exclude = exclude.map(List::copyOf);
		List<Predicate<ComponentType<?>>> list = new ArrayList(2);
		exclude.ifPresent(excludedTypes -> list.add((Predicate)type -> !excludedTypes.contains(type)));
		include.ifPresent(includedTypes -> list.add(includedTypes::contains));
		this.filter = Util.allOf(list);
	}

	@Override
	public LootFunctionType<CopyComponentsLootFunction> getType() {
		return LootFunctionTypes.COPY_COMPONENTS;
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return this.source.getRequiredParameters();
	}

	@Override
	public ItemStack process(ItemStack stack, LootContext context) {
		ComponentMap componentMap = this.source.getComponents(context);
		stack.applyComponentsFrom(componentMap.filtered(this.filter));
		return stack;
	}

	public static CopyComponentsLootFunction.Builder builder(CopyComponentsLootFunction.Source source) {
		return new CopyComponentsLootFunction.Builder(source);
	}

	public static class Builder extends ConditionalLootFunction.Builder<CopyComponentsLootFunction.Builder> {
		private final CopyComponentsLootFunction.Source source;
		private Optional<ImmutableList.Builder<ComponentType<?>>> include = Optional.empty();
		private Optional<ImmutableList.Builder<ComponentType<?>>> exclude = Optional.empty();

		Builder(CopyComponentsLootFunction.Source source) {
			this.source = source;
		}

		public CopyComponentsLootFunction.Builder include(ComponentType<?> type) {
			if (this.include.isEmpty()) {
				this.include = Optional.of(ImmutableList.builder());
			}

			((ImmutableList.Builder)this.include.get()).add(type);
			return this;
		}

		public CopyComponentsLootFunction.Builder exclude(ComponentType<?> type) {
			if (this.exclude.isEmpty()) {
				this.exclude = Optional.of(ImmutableList.builder());
			}

			((ImmutableList.Builder)this.exclude.get()).add(type);
			return this;
		}

		protected CopyComponentsLootFunction.Builder getThisBuilder() {
			return this;
		}

		@Override
		public LootFunction build() {
			return new CopyComponentsLootFunction(
				this.getConditions(), this.source, this.include.map(ImmutableList.Builder::build), this.exclude.map(ImmutableList.Builder::build)
			);
		}
	}

	public static enum Source implements StringIdentifiable {
		BLOCK_ENTITY("block_entity");

		public static final Codec<CopyComponentsLootFunction.Source> CODEC = StringIdentifiable.createBasicCodec(CopyComponentsLootFunction.Source::values);
		private final String id;

		private Source(final String id) {
			this.id = id;
		}

		public ComponentMap getComponents(LootContext context) {
			switch (this) {
				case BLOCK_ENTITY:
					BlockEntity blockEntity = context.get(LootContextParameters.BLOCK_ENTITY);
					return blockEntity != null ? blockEntity.createComponentMap() : ComponentMap.EMPTY;
				default:
					throw new MatchException(null, null);
			}
		}

		public Set<LootContextParameter<?>> getRequiredParameters() {
			switch (this) {
				case BLOCK_ENTITY:
					return Set.of(LootContextParameters.BLOCK_ENTITY);
				default:
					throw new MatchException(null, null);
			}
		}

		@Override
		public String asString() {
			return this.id;
		}
	}
}
