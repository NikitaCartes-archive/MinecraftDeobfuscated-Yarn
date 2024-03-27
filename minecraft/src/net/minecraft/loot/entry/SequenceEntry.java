package net.minecraft.loot.entry;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.loot.condition.LootCondition;

public class SequenceEntry extends CombinedEntry {
	public static final MapCodec<SequenceEntry> CODEC = createCodec(SequenceEntry::new);

	SequenceEntry(List<LootPoolEntry> list, List<LootCondition> list2) {
		super(list, list2);
	}

	@Override
	public LootPoolEntryType getType() {
		return LootPoolEntryTypes.SEQUENCE;
	}

	@Override
	protected EntryCombiner combine(List<? extends EntryCombiner> terms) {
		return switch (terms.size()) {
			case 0 -> ALWAYS_TRUE;
			case 1 -> (EntryCombiner)terms.get(0);
			case 2 -> ((EntryCombiner)terms.get(0)).and((EntryCombiner)terms.get(1));
			default -> (context, lootChoiceExpander) -> {
			for (EntryCombiner entryCombiner : terms) {
				if (!entryCombiner.expand(context, lootChoiceExpander)) {
					return false;
				}
			}

			return true;
		};
		};
	}

	public static SequenceEntry.Builder create(LootPoolEntry.Builder<?>... entries) {
		return new SequenceEntry.Builder(entries);
	}

	public static class Builder extends LootPoolEntry.Builder<SequenceEntry.Builder> {
		private final ImmutableList.Builder<LootPoolEntry> entries = ImmutableList.builder();

		public Builder(LootPoolEntry.Builder<?>... entries) {
			for (LootPoolEntry.Builder<?> builder : entries) {
				this.entries.add(builder.build());
			}
		}

		protected SequenceEntry.Builder getThisBuilder() {
			return this;
		}

		@Override
		public SequenceEntry.Builder groupEntry(LootPoolEntry.Builder<?> entry) {
			this.entries.add(entry.build());
			return this;
		}

		@Override
		public LootPoolEntry build() {
			return new SequenceEntry(this.entries.build(), this.getConditions());
		}
	}
}
