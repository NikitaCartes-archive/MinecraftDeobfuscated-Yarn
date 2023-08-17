package net.minecraft.loot.function;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.command.argument.NbtPathArgumentType;
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

public class CopyNbtLootFunction extends ConditionalLootFunction {
	public static final Codec<CopyNbtLootFunction> CODEC = RecordCodecBuilder.create(
		instance -> method_53344(instance)
				.<LootNbtProvider, List<CopyNbtLootFunction.Operation>>and(
					instance.group(
						LootNbtProviderTypes.CODEC.fieldOf("source").forGetter(copyNbtLootFunction -> copyNbtLootFunction.source),
						CopyNbtLootFunction.Operation.CODEC.listOf().fieldOf("ops").forGetter(copyNbtLootFunction -> copyNbtLootFunction.operations)
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
	public LootFunctionType getType() {
		return LootFunctionTypes.COPY_NBT;
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return this.source.getRequiredParameters();
	}

	@Override
	public ItemStack process(ItemStack stack, LootContext context) {
		NbtElement nbtElement = this.source.getNbt(context);
		if (nbtElement != null) {
			this.operations.forEach(operation -> operation.execute(stack::getOrCreateNbt, nbtElement));
		}

		return stack;
	}

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
				this.operations.add(new CopyNbtLootFunction.Operation(CopyNbtLootFunction.Path.parse(source), CopyNbtLootFunction.Path.parse(target), operator));
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

	static record Operation(CopyNbtLootFunction.Path parsedSourcePath, CopyNbtLootFunction.Path parsedTargetPath, CopyNbtLootFunction.Operator operator) {
		public static final Codec<CopyNbtLootFunction.Operation> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						CopyNbtLootFunction.Path.CODEC.fieldOf("source").forGetter(CopyNbtLootFunction.Operation::parsedSourcePath),
						CopyNbtLootFunction.Path.CODEC.fieldOf("target").forGetter(CopyNbtLootFunction.Operation::parsedTargetPath),
						CopyNbtLootFunction.Operator.CODEC.fieldOf("op").forGetter(CopyNbtLootFunction.Operation::operator)
					)
					.apply(instance, CopyNbtLootFunction.Operation::new)
		);

		public void execute(Supplier<NbtElement> itemNbtGetter, NbtElement sourceEntityNbt) {
			try {
				List<NbtElement> list = this.parsedSourcePath.path().get(sourceEntityNbt);
				if (!list.isEmpty()) {
					this.operator.merge((NbtElement)itemNbtGetter.get(), this.parsedTargetPath.path(), list);
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

		Operator(String name) {
			this.name = name;
		}

		@Override
		public String asString() {
			return this.name;
		}
	}

	static record Path(String string, NbtPathArgumentType.NbtPath path) {
		public static final Codec<CopyNbtLootFunction.Path> CODEC = Codec.STRING.comapFlatMap(string -> {
			try {
				return DataResult.success(parse(string));
			} catch (CommandSyntaxException var2) {
				return DataResult.error(() -> "Failed to parse path " + string + ": " + var2.getMessage());
			}
		}, CopyNbtLootFunction.Path::string);

		public static CopyNbtLootFunction.Path parse(String string) throws CommandSyntaxException {
			NbtPathArgumentType.NbtPath nbtPath = new NbtPathArgumentType().parse(new StringReader(string));
			return new CopyNbtLootFunction.Path(string, nbtPath);
		}
	}
}
