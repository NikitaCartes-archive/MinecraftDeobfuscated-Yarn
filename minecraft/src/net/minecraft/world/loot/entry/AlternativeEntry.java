package net.minecraft.world.loot.entry;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.util.Identifier;
import net.minecraft.world.loot.LootSupplier;
import net.minecraft.world.loot.LootTableReporter;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.world.loot.context.LootContextType;
import org.apache.commons.lang3.ArrayUtils;

public class AlternativeEntry extends CombinedEntry {
	AlternativeEntry(LootEntry[] lootEntrys, LootCondition[] lootConditions) {
		super(lootEntrys, lootConditions);
	}

	@Override
	protected EntryCombiner combine(EntryCombiner[] entryCombiners) {
		switch (entryCombiners.length) {
			case 0:
				return ALWAYS_FALSE;
			case 1:
				return entryCombiners[0];
			case 2:
				return entryCombiners[0].or(entryCombiners[1]);
			default:
				return (lootContext, consumer) -> {
					for (EntryCombiner entryCombiner : entryCombiners) {
						if (entryCombiner.expand(lootContext, consumer)) {
							return true;
						}
					}

					return false;
				};
		}
	}

	@Override
	public void method_415(LootTableReporter lootTableReporter, Function<Identifier, LootSupplier> function, Set<Identifier> set, LootContextType lootContextType) {
		super.method_415(lootTableReporter, function, set, lootContextType);

		for (int i = 0; i < this.field_982.length - 1; i++) {
			if (ArrayUtils.isEmpty((Object[])this.field_982[i].field_988)) {
				lootTableReporter.report("Unreachable entry!");
			}
		}
	}

	public static AlternativeEntry.Builder method_386(LootEntry.Builder<?>... builders) {
		return new AlternativeEntry.Builder(builders);
	}

	public static class Builder extends LootEntry.Builder<AlternativeEntry.Builder> {
		private final List<LootEntry> children = Lists.<LootEntry>newArrayList();

		public Builder(LootEntry.Builder<?>... builders) {
			for (LootEntry.Builder<?> builder : builders) {
				this.children.add(builder.build());
			}
		}

		protected AlternativeEntry.Builder method_388() {
			return this;
		}

		@Override
		public AlternativeEntry.Builder withChild(LootEntry.Builder<?> builder) {
			this.children.add(builder.build());
			return this;
		}

		@Override
		public LootEntry build() {
			return new AlternativeEntry((LootEntry[])this.children.toArray(new LootEntry[0]), this.method_420());
		}
	}
}
