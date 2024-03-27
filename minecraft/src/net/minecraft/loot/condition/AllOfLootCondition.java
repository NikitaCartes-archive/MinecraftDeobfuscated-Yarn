package net.minecraft.loot.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.util.Util;

public class AllOfLootCondition extends AlternativeLootCondition {
	public static final MapCodec<AllOfLootCondition> CODEC = createCodec(AllOfLootCondition::new);
	public static final Codec<AllOfLootCondition> INLINE_CODEC = createInlineCodec(AllOfLootCondition::new);

	AllOfLootCondition(List<LootCondition> terms) {
		super(terms, Util.allOf(terms));
	}

	public static AllOfLootCondition create(List<LootCondition> terms) {
		return new AllOfLootCondition(List.copyOf(terms));
	}

	@Override
	public LootConditionType getType() {
		return LootConditionTypes.ALL_OF;
	}

	public static AllOfLootCondition.Builder builder(LootCondition.Builder... terms) {
		return new AllOfLootCondition.Builder(terms);
	}

	public static class Builder extends AlternativeLootCondition.Builder {
		public Builder(LootCondition.Builder... builders) {
			super(builders);
		}

		@Override
		public AllOfLootCondition.Builder and(LootCondition.Builder builder) {
			this.add(builder);
			return this;
		}

		@Override
		protected LootCondition build(List<LootCondition> terms) {
			return new AllOfLootCondition(terms);
		}
	}
}
