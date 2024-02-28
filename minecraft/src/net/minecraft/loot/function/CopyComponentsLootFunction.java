package net.minecraft.loot.function;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Set;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentType;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.util.StringIdentifiable;

public class CopyComponentsLootFunction extends ConditionalLootFunction {
	public static final Codec<CopyComponentsLootFunction> CODEC = RecordCodecBuilder.create(
		instance -> addConditionsField(instance)
				.<CopyComponentsLootFunction.Source, List<DataComponentType<?>>>and(
					instance.group(
						CopyComponentsLootFunction.Source.CODEC.fieldOf("source").forGetter(function -> function.source),
						DataComponentType.CODEC.listOf().fieldOf("components").forGetter(function -> function.components)
					)
				)
				.apply(instance, CopyComponentsLootFunction::new)
	);
	private final CopyComponentsLootFunction.Source source;
	private final List<DataComponentType<?>> components;

	CopyComponentsLootFunction(List<LootCondition> conditions, CopyComponentsLootFunction.Source source, List<DataComponentType<?>> components) {
		super(conditions);
		this.source = source;
		this.components = List.copyOf(components);
	}

	@Override
	public LootFunctionType getType() {
		return LootFunctionTypes.COPY_COMPONENTS;
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return this.source.getRequiredParameters();
	}

	@Override
	public ItemStack process(ItemStack stack, LootContext context) {
		ComponentMap componentMap = this.source.getComponents(context);
		stack.applyComponentsFrom(componentMap.filtered(this.components::contains));
		return stack;
	}

	public static CopyComponentsLootFunction.Builder builder(CopyComponentsLootFunction.Source source) {
		return new CopyComponentsLootFunction.Builder(source);
	}

	public static class Builder extends ConditionalLootFunction.Builder<CopyComponentsLootFunction.Builder> {
		private final CopyComponentsLootFunction.Source source;
		private final ImmutableList.Builder<DataComponentType<?>> types = ImmutableList.builder();

		Builder(CopyComponentsLootFunction.Source source) {
			this.source = source;
		}

		public CopyComponentsLootFunction.Builder add(DataComponentType<?> type) {
			this.types.add(type);
			return this;
		}

		protected CopyComponentsLootFunction.Builder getThisBuilder() {
			return this;
		}

		@Override
		public LootFunction build() {
			return new CopyComponentsLootFunction(this.getConditions(), this.source, this.types.build());
		}
	}

	public static enum Source implements StringIdentifiable {
		BLOCK_ENTITY("block_entity");

		public static final Codec<CopyComponentsLootFunction.Source> CODEC = StringIdentifiable.createBasicCodec(CopyComponentsLootFunction.Source::values);
		private final String id;

		private Source(String id) {
			this.id = id;
		}

		public ComponentMap getComponents(LootContext context) {
			switch (this) {
				case BLOCK_ENTITY:
					BlockEntity blockEntity = context.get(LootContextParameters.BLOCK_ENTITY);
					return blockEntity != null ? blockEntity.createComponentMap() : ComponentMap.EMPTY;
				default:
					throw new IncompatibleClassChangeError();
			}
		}

		public Set<LootContextParameter<?>> getRequiredParameters() {
			switch (this) {
				case BLOCK_ENTITY:
					return Set.of(LootContextParameters.BLOCK_ENTITY);
				default:
					throw new IncompatibleClassChangeError();
			}
		}

		@Override
		public String asString() {
			return this.id;
		}
	}
}
