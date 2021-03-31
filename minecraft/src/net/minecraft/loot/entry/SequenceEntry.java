package net.minecraft.loot.entry;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.loot.condition.LootCondition;

public class SequenceEntry extends CombinedEntry {
	SequenceEntry(LootPoolEntry[] lootPoolEntrys, LootCondition[] lootConditions) {
		super(lootPoolEntrys, lootConditions);
	}

	@Override
	public LootPoolEntryType getType() {
		return LootPoolEntryTypes.GROUP;
	}

	@Override
	protected EntryCombiner combine(EntryCombiner[] children) {
		switch (children.length) {
			case 0:
				return ALWAYS_TRUE;
			case 1:
				return children[0];
			case 2:
				EntryCombiner entryCombiner = children[0];
				EntryCombiner entryCombiner2 = children[1];
				return (context, consumer) -> {
					entryCombiner.expand(context, consumer);
					entryCombiner2.expand(context, consumer);
					return true;
				};
			default:
				return (context, lootChoiceExpander) -> {
					for (EntryCombiner entryCombinerx : children) {
						entryCombinerx.expand(context, lootChoiceExpander);
					}

					return true;
				};
		}
	}

	public static SequenceEntry.Builder create(LootPoolEntry.Builder<?>... entries) {
		return new SequenceEntry.Builder(entries);
	}

	public static class Builder extends LootPoolEntry.Builder<SequenceEntry.Builder> {
		private final List<LootPoolEntry> entries = Lists.<LootPoolEntry>newArrayList();

		public Builder(LootPoolEntry.Builder<?>... entries) {
			for (LootPoolEntry.Builder<?> builder : entries) {
				this.entries.add(builder.build());
			}
		}

		protected SequenceEntry.Builder getThisBuilder() {
			return this;
		}

		@Override
		public SequenceEntry.Builder sequenceEntry(LootPoolEntry.Builder<?> entry) {
			this.entries.add(entry.build());
			return this;
		}

		@Override
		public LootPoolEntry build() {
			return new SequenceEntry((LootPoolEntry[])this.entries.toArray(new LootPoolEntry[0]), this.getConditions());
		}
	}
}
