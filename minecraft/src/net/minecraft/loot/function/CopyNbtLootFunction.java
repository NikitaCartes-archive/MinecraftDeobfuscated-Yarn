package net.minecraft.loot.function;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.provider.nbt.ContextLootNbtProvider;
import net.minecraft.loot.provider.nbt.LootNbtProvider;
import net.minecraft.loot.provider.nbt.LootNbtProviderTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.StringIdentifiable;
import org.apache.commons.lang3.mutable.MutableObject;

public class CopyNbtLootFunction extends ConditionalLootFunction {
	public static final MapCodec<CopyNbtLootFunction> CODEC = RecordCodecBuilder.mapCodec(
		instance -> addConditionsField(instance)
				.<LootNbtProvider, List<CopyNbtLootFunction.Operation>>and(
					instance.group(
						LootNbtProviderTypes.CODEC.fieldOf("source").forGetter(function -> function.source),
						CopyNbtLootFunction.Operation.CODEC.listOf().fieldOf("ops").forGetter(function -> function.operations)
					)
				)
				.apply(instance, CopyNbtLootFunction::new)
	);
	private final LootNbtProvider source;
	private final List<CopyNbtLootFunction.Operation> operations;

	CopyNbtLootFunction(List<LootCondition> conditions, LootNbtProvider source, List<CopyNbtLootFunction.Operation> operations) {
		super(conditions);
		this.source = source;
		this.operations = List.copyOf(operations);
	}

	@Override
	public LootFunctionType<CopyNbtLootFunction> getType() {
		return LootFunctionTypes.COPY_CUSTOM_DATA;
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return this.source.getRequiredParameters();
	}

	@Override
	public ItemStack process(ItemStack stack, LootContext context) {
		NbtElement nbtElement = this.source.getNbt(context);
		if (nbtElement == null) {
			return stack;
		} else {
			MutableObject<NbtCompound> mutableObject = new MutableObject<>();
			Supplier<NbtElement> supplier = () -> {
				if (mutableObject.getValue() == null) {
					mutableObject.setValue(stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt());
				}

				return mutableObject.getValue();
			};
			this.operations.forEach(operation -> operation.execute(supplier, nbtElement));
			NbtCompound nbtCompound = mutableObject.getValue();
			if (nbtCompound != null) {
				NbtComponent.set(DataComponentTypes.CUSTOM_DATA, stack, nbtCompound);
			}

			return stack;
		}
	}

	@Deprecated
	public static CopyNbtLootFunction.Builder builder(LootNbtProvider source) {
		return new CopyNbtLootFunction.Builder(source);
	}

	public static CopyNbtLootFunction.Builder builder(LootContext.EntityTarget target) {
		return new CopyNbtLootFunction.Builder(ContextLootNbtProvider.fromTarget(target));
	}

	public static class Builder extends ConditionalLootFunction.Builder<CopyNbtLootFunction.Builder> {
		private final LootNbtProvider source;
		private final List<CopyNbtLootFunction.Operation> operations = Lists.<CopyNbtLootFunction.Operation>newArrayList();

		Builder(LootNbtProvider source) {
			this.source = source;
		}

		public CopyNbtLootFunction.Builder withOperation(String source, String target, CopyNbtLootFunction.Operator operator) {
			try {
				this.operations.add(new CopyNbtLootFunction.Operation(NbtPathArgumentType.NbtPath.parse(source), NbtPathArgumentType.NbtPath.parse(target), operator));
				return this;
			} catch (CommandSyntaxException var5) {
				throw new IllegalArgumentException(var5);
			}
		}

		public CopyNbtLootFunction.Builder withOperation(String source, String target) {
			return this.withOperation(source, target, CopyNbtLootFunction.Operator.REPLACE);
		}

		protected CopyNbtLootFunction.Builder getThisBuilder() {
			return this;
		}

		@Override
		public LootFunction build() {
			return new CopyNbtLootFunction(this.getConditions(), this.source, this.operations);
		}
	}

	static record Operation(NbtPathArgumentType.NbtPath parsedSourcePath, NbtPathArgumentType.NbtPath parsedTargetPath, CopyNbtLootFunction.Operator operator) {
		public static final Codec<CopyNbtLootFunction.Operation> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						NbtPathArgumentType.NbtPath.CODEC.fieldOf("source").forGetter(CopyNbtLootFunction.Operation::parsedSourcePath),
						NbtPathArgumentType.NbtPath.CODEC.fieldOf("target").forGetter(CopyNbtLootFunction.Operation::parsedTargetPath),
						CopyNbtLootFunction.Operator.CODEC.fieldOf("op").forGetter(CopyNbtLootFunction.Operation::operator)
					)
					.apply(instance, CopyNbtLootFunction.Operation::new)
		);

		public void execute(Supplier<NbtElement> itemNbtGetter, NbtElement sourceEntityNbt) {
			try {
				List<NbtElement> list = this.parsedSourcePath.get(sourceEntityNbt);
				if (!list.isEmpty()) {
					this.operator.merge((NbtElement)itemNbtGetter.get(), this.parsedTargetPath, list);
				}
			} catch (CommandSyntaxException var4) {
			}
		}
	}

	public static enum Operator implements StringIdentifiable {
		REPLACE("replace") {
			@Override
			public void merge(NbtElement itemNbt, NbtPathArgumentType.NbtPath targetPath, List<NbtElement> sourceNbts) throws CommandSyntaxException {
				targetPath.put(itemNbt, Iterables.getLast(sourceNbts));
			}
		},
		APPEND("append") {
			@Override
			public void merge(NbtElement itemNbt, NbtPathArgumentType.NbtPath targetPath, List<NbtElement> sourceNbts) throws CommandSyntaxException {
				List<NbtElement> list = targetPath.getOrInit(itemNbt, NbtList::new);
				list.forEach(foundNbt -> {
					if (foundNbt instanceof NbtList) {
						sourceNbts.forEach(sourceNbt -> ((NbtList)foundNbt).add(sourceNbt.copy()));
					}
				});
			}
		},
		MERGE("merge") {
			@Override
			public void merge(NbtElement itemNbt, NbtPathArgumentType.NbtPath targetPath, List<NbtElement> sourceNbts) throws CommandSyntaxException {
				List<NbtElement> list = targetPath.getOrInit(itemNbt, NbtCompound::new);
				list.forEach(foundNbt -> {
					if (foundNbt instanceof NbtCompound) {
						sourceNbts.forEach(sourceNbt -> {
							if (sourceNbt instanceof NbtCompound) {
								((NbtCompound)foundNbt).copyFrom((NbtCompound)sourceNbt);
							}
						});
					}
				});
			}
		};

		public static final Codec<CopyNbtLootFunction.Operator> CODEC = StringIdentifiable.createCodec(CopyNbtLootFunction.Operator::values);
		private final String name;

		public abstract void merge(NbtElement itemNbt, NbtPathArgumentType.NbtPath targetPath, List<NbtElement> sourceNbts) throws CommandSyntaxException;

		Operator(final String name) {
			this.name = name;
		}

		@Override
		public String asString() {
			return this.name;
		}
	}
}
