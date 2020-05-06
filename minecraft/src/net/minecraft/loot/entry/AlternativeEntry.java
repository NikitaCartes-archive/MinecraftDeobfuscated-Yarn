package net.minecraft.loot.entry;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.condition.LootCondition;
import org.apache.commons.lang3.ArrayUtils;

public class AlternativeEntry extends CombinedEntry {
	AlternativeEntry(LootEntry[] lootEntrys, LootCondition[] lootConditions) {
		super(lootEntrys, lootConditions);
	}

	@Override
	protected EntryCombiner combine(EntryCombiner[] children) {
		switch (children.length) {
			case 0:
				return ALWAYS_FALSE;
			case 1:
				return children[0];
			case 2:
				return children[0].or(children[1]);
			default:
				return (context, lootChoiceExpander) -> {
					for (EntryCombiner entryCombiner : children) {
						if (entryCombiner.expand(context, lootChoiceExpander)) {
							return true;
						}
					}

					return false;
				};
		}
	}

	@Override
	public void validate(LootTableReporter reporter) {
		super.validate(reporter);

		for (int i = 0; i < this.children.length - 1; i++) {
			if (ArrayUtils.isEmpty((Object[])this.children[i].conditions)) {
				reporter.report("Unreachable entry!");
			}
		}
	}

	public static AlternativeEntry.Builder builder(LootEntry.Builder<?>... children) {
		return new AlternativeEntry.Builder(children);
	}

	public static class Builder extends LootEntry.Builder<AlternativeEntry.Builder> {
		private final List<LootEntry> children = Lists.<LootEntry>newArrayList();

		public Builder(LootEntry.Builder<?>... children) {
			for (LootEntry.Builder<?> builder : children) {
				this.children.add(builder.build());
			}
		}

		protected AlternativeEntry.Builder getThisBuilder() {
			return this;
		}

		@Override
		public AlternativeEntry.Builder alternatively(LootEntry.Builder<?> builder) {
			this.children.add(builder.build());
			return this;
		}

		@Override
		public LootEntry build() {
			return new AlternativeEntry((LootEntry[])this.children.toArray(new LootEntry[0]), this.getConditions());
		}
	}
}
