package net.minecraft.loot.condition;

public class AllOfLootCondition extends AlternativeLootCondition {
	AllOfLootCondition(LootCondition[] terms) {
		super(terms, LootConditionTypes.matchingAll(terms));
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
		protected LootCondition build(LootCondition[] terms) {
			return new AllOfLootCondition(terms);
		}
	}

	public static class Serializer extends AlternativeLootCondition.Serializer<AllOfLootCondition> {
		protected AllOfLootCondition fromTerms(LootCondition[] lootConditions) {
			return new AllOfLootCondition(lootConditions);
		}
	}
}
