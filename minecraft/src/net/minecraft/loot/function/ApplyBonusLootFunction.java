package net.minecraft.loot.function;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.random.Random;

public class ApplyBonusLootFunction extends ConditionalLootFunction {
	private static final Map<Identifier, ApplyBonusLootFunction.Type> FACTORIES = (Map<Identifier, ApplyBonusLootFunction.Type>)Stream.of(
			ApplyBonusLootFunction.BinomialWithBonusCount.TYPE, ApplyBonusLootFunction.OreDrops.TYPE, ApplyBonusLootFunction.UniformBonusCount.TYPE
		)
		.collect(Collectors.toMap(ApplyBonusLootFunction.Type::id, Function.identity()));
	private static final Codec<ApplyBonusLootFunction.Type> TYPE_CODEC = Identifier.CODEC.comapFlatMap(id -> {
		ApplyBonusLootFunction.Type type = (ApplyBonusLootFunction.Type)FACTORIES.get(id);
		return type != null ? DataResult.success(type) : DataResult.error(() -> "No formula type with id: '" + id + "'");
	}, ApplyBonusLootFunction.Type::id);
	private static final MapCodec<ApplyBonusLootFunction.Formula> FORMULA_CODEC = Codecs.parameters(
		"formula", "parameters", TYPE_CODEC, ApplyBonusLootFunction.Formula::getType, ApplyBonusLootFunction.Type::codec
	);
	public static final MapCodec<ApplyBonusLootFunction> CODEC = RecordCodecBuilder.mapCodec(
		instance -> addConditionsField(instance)
				.<RegistryEntry<Enchantment>, ApplyBonusLootFunction.Formula>and(
					instance.group(
						Enchantment.ENTRY_CODEC.fieldOf("enchantment").forGetter(function -> function.enchantment), FORMULA_CODEC.forGetter(function -> function.formula)
					)
				)
				.apply(instance, ApplyBonusLootFunction::new)
	);
	private final RegistryEntry<Enchantment> enchantment;
	private final ApplyBonusLootFunction.Formula formula;

	private ApplyBonusLootFunction(List<LootCondition> conditions, RegistryEntry<Enchantment> enchantment, ApplyBonusLootFunction.Formula formula) {
		super(conditions);
		this.enchantment = enchantment;
		this.formula = formula;
	}

	@Override
	public LootFunctionType<ApplyBonusLootFunction> getType() {
		return LootFunctionTypes.APPLY_BONUS;
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return ImmutableSet.of(LootContextParameters.TOOL);
	}

	@Override
	public ItemStack process(ItemStack stack, LootContext context) {
		ItemStack itemStack = context.get(LootContextParameters.TOOL);
		if (itemStack != null) {
			int i = EnchantmentHelper.getLevel(this.enchantment, itemStack);
			int j = this.formula.getValue(context.getRandom(), stack.getCount(), i);
			stack.setCount(j);
		}

		return stack;
	}

	public static ConditionalLootFunction.Builder<?> binomialWithBonusCount(RegistryEntry<Enchantment> enchantment, float probability, int extra) {
		return builder(conditions -> new ApplyBonusLootFunction(conditions, enchantment, new ApplyBonusLootFunction.BinomialWithBonusCount(extra, probability)));
	}

	public static ConditionalLootFunction.Builder<?> oreDrops(RegistryEntry<Enchantment> enchantment) {
		return builder(conditions -> new ApplyBonusLootFunction(conditions, enchantment, new ApplyBonusLootFunction.OreDrops()));
	}

	public static ConditionalLootFunction.Builder<?> uniformBonusCount(RegistryEntry<Enchantment> enchantment) {
		return builder(conditions -> new ApplyBonusLootFunction(conditions, enchantment, new ApplyBonusLootFunction.UniformBonusCount(1)));
	}

	public static ConditionalLootFunction.Builder<?> uniformBonusCount(RegistryEntry<Enchantment> enchantment, int bonusMultiplier) {
		return builder(conditions -> new ApplyBonusLootFunction(conditions, enchantment, new ApplyBonusLootFunction.UniformBonusCount(bonusMultiplier)));
	}

	static record BinomialWithBonusCount(int extra, float probability) implements ApplyBonusLootFunction.Formula {
		private static final Codec<ApplyBonusLootFunction.BinomialWithBonusCount> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Codec.INT.fieldOf("extra").forGetter(ApplyBonusLootFunction.BinomialWithBonusCount::extra),
						Codec.FLOAT.fieldOf("probability").forGetter(ApplyBonusLootFunction.BinomialWithBonusCount::probability)
					)
					.apply(instance, ApplyBonusLootFunction.BinomialWithBonusCount::new)
		);
		public static final ApplyBonusLootFunction.Type TYPE = new ApplyBonusLootFunction.Type(Identifier.ofVanilla("binomial_with_bonus_count"), CODEC);

		@Override
		public int getValue(Random random, int initialCount, int enchantmentLevel) {
			for (int i = 0; i < enchantmentLevel + this.extra; i++) {
				if (random.nextFloat() < this.probability) {
					initialCount++;
				}
			}

			return initialCount;
		}

		@Override
		public ApplyBonusLootFunction.Type getType() {
			return TYPE;
		}
	}

	interface Formula {
		int getValue(Random random, int initialCount, int enchantmentLevel);

		ApplyBonusLootFunction.Type getType();
	}

	static record OreDrops() implements ApplyBonusLootFunction.Formula {
		public static final Codec<ApplyBonusLootFunction.OreDrops> CODEC = Codec.unit(ApplyBonusLootFunction.OreDrops::new);
		public static final ApplyBonusLootFunction.Type TYPE = new ApplyBonusLootFunction.Type(Identifier.ofVanilla("ore_drops"), CODEC);

		@Override
		public int getValue(Random random, int initialCount, int enchantmentLevel) {
			if (enchantmentLevel > 0) {
				int i = random.nextInt(enchantmentLevel + 2) - 1;
				if (i < 0) {
					i = 0;
				}

				return initialCount * (i + 1);
			} else {
				return initialCount;
			}
		}

		@Override
		public ApplyBonusLootFunction.Type getType() {
			return TYPE;
		}
	}

	static record Type(Identifier id, Codec<? extends ApplyBonusLootFunction.Formula> codec) {
	}

	static record UniformBonusCount(int bonusMultiplier) implements ApplyBonusLootFunction.Formula {
		public static final Codec<ApplyBonusLootFunction.UniformBonusCount> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(Codec.INT.fieldOf("bonusMultiplier").forGetter(ApplyBonusLootFunction.UniformBonusCount::bonusMultiplier))
					.apply(instance, ApplyBonusLootFunction.UniformBonusCount::new)
		);
		public static final ApplyBonusLootFunction.Type TYPE = new ApplyBonusLootFunction.Type(Identifier.ofVanilla("uniform_bonus_count"), CODEC);

		@Override
		public int getValue(Random random, int initialCount, int enchantmentLevel) {
			return initialCount + random.nextInt(this.bonusMultiplier * enchantmentLevel + 1);
		}

		@Override
		public ApplyBonusLootFunction.Type getType() {
			return TYPE;
		}
	}
}
