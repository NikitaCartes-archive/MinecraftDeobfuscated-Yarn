package net.minecraft.world.loot.entry;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.class_4570;
import net.minecraft.world.loot.LootTableReporter;
import org.apache.commons.lang3.ArrayUtils;

public class AlternativeEntry extends CombinedEntry {
	AlternativeEntry(LootEntry[] lootEntrys, class_4570[] args) {
		super(lootEntrys, args);
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
	public void check(LootTableReporter lootTableReporter) {
		super.check(lootTableReporter);

		for (int i = 0; i < this.children.length - 1; i++) {
			if (ArrayUtils.isEmpty((Object[])this.children[i].conditions)) {
				lootTableReporter.report("Unreachable entry!");
			}
		}
	}

	public static AlternativeEntry.Builder builder(LootEntry.Builder<?>... builders) {
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
			return new AlternativeEntry((LootEntry[])this.children.toArray(new LootEntry[0]), this.getConditions());
		}
	}
}
