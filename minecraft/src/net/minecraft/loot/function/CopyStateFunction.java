package net.minecraft.loot.function;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.state.property.Property;

public class CopyStateFunction extends ConditionalLootFunction {
	public static final Codec<CopyStateFunction> CODEC = RecordCodecBuilder.create(
		instance -> addConditionsField(instance)
				.<RegistryEntry<Block>, List<String>>and(
					instance.group(
						Registries.BLOCK.getEntryCodec().fieldOf("block").forGetter(function -> function.block),
						Codec.STRING.listOf().fieldOf("properties").forGetter(function -> function.properties.stream().map(Property::getName).toList())
					)
				)
				.apply(instance, CopyStateFunction::new)
	);
	private final RegistryEntry<Block> block;
	private final Set<Property<?>> properties;

	CopyStateFunction(List<LootCondition> conditions, RegistryEntry<Block> block, Set<Property<?>> properties) {
		super(conditions);
		this.block = block;
		this.properties = properties;
	}

	private CopyStateFunction(List<LootCondition> conditions, RegistryEntry<Block> block, List<String> properties) {
		this(
			conditions,
			block,
			(Set<Property<?>>)properties.stream().map(block.value().getStateManager()::getProperty).filter(Objects::nonNull).collect(Collectors.toSet())
		);
	}

	@Override
	public LootFunctionType getType() {
		return LootFunctionTypes.COPY_STATE;
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return ImmutableSet.of(LootContextParameters.BLOCK_STATE);
	}

	@Override
	protected ItemStack process(ItemStack stack, LootContext context) {
		BlockState blockState = context.get(LootContextParameters.BLOCK_STATE);
		if (blockState != null) {
			NbtCompound nbtCompound = stack.getOrCreateNbt();
			NbtCompound nbtCompound2;
			if (nbtCompound.contains("BlockStateTag", NbtElement.COMPOUND_TYPE)) {
				nbtCompound2 = nbtCompound.getCompound("BlockStateTag");
			} else {
				nbtCompound2 = new NbtCompound();
				nbtCompound.put("BlockStateTag", nbtCompound2);
			}

			for (Property<?> property : this.properties) {
				if (blockState.contains(property)) {
					nbtCompound2.putString(property.getName(), getPropertyName(blockState, property));
				}
			}
		}

		return stack;
	}

	public static CopyStateFunction.Builder builder(Block block) {
		return new CopyStateFunction.Builder(block);
	}

	private static <T extends Comparable<T>> String getPropertyName(BlockState state, Property<T> property) {
		T comparable = state.get(property);
		return property.name(comparable);
	}

	public static class Builder extends ConditionalLootFunction.Builder<CopyStateFunction.Builder> {
		private final RegistryEntry<Block> block;
		private final ImmutableSet.Builder<Property<?>> properties = ImmutableSet.builder();

		Builder(Block block) {
			this.block = block.getRegistryEntry();
		}

		public CopyStateFunction.Builder addProperty(Property<?> property) {
			if (!this.block.value().getStateManager().getProperties().contains(property)) {
				throw new IllegalStateException("Property " + property + " is not present on block " + this.block);
			} else {
				this.properties.add(property);
				return this;
			}
		}

		protected CopyStateFunction.Builder getThisBuilder() {
			return this;
		}

		@Override
		public LootFunction build() {
			return new CopyStateFunction(this.getConditions(), this.block, this.properties.build());
		}
	}
}
