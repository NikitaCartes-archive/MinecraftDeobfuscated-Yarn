package net.minecraft.loot.entry;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.loot.condition.LootCondition;

public class GroupEntry extends CombinedEntry {
	public static final Codec<GroupEntry> CODEC = createCodec(GroupEntry::new);

	GroupEntry(List<LootPoolEntry> list, List<LootCondition> list2) {
		super(list, list2);
	}

	@Override
	public LootPoolEntryType getType() {
		return LootPoolEntryTypes.GROUP;
	}

	@Override
	protected EntryCombiner combine(List<? extends EntryCombiner> list) {
		return switch (list.size()) {
			case 0 -> ALWAYS_TRUE;
			case 1 -> (EntryCombiner)list.get(0);
			case 2 -> {
				EntryCombiner entryCombiner = (EntryCombiner)list.get(0);
				EntryCombiner entryCombiner2 = (EntryCombiner)list.get(1);
				yield (context, choiceConsumer) -> {
					entryCombiner.expand(context, choiceConsumer);
					entryCombiner2.expand(context, choiceConsumer);
					return true;
				};
			}
			default -> (context, lootChoiceExpander) -> {
			for (EntryCombiner entryCombinerx : list) {
				entryCombinerx.expand(context, lootChoiceExpander);
			}

			return true;
		};
		};
	}

	public static GroupEntry.Builder create(LootPoolEntry.Builder<?>... entries) {
		return new GroupEntry.Builder(entries);
	}

	public static class Builder extends LootPoolEntry.Builder<GroupEntry.Builder> {
		private final ImmutableList.Builder<LootPoolEntry> entries = ImmutableList.builder();

		public Builder(LootPoolEntry.Builder<?>... entries) {
			for (LootPoolEntry.Builder<?> builder : entries) {
				this.entries.add(builder.build());
			}
		}

		protected GroupEntry.Builder getThisBuilder() {
			return this;
		}

		@Override
		public GroupEntry.Builder sequenceEntry(LootPoolEntry.Builder<?> entry) {
			this.entries.add(entry.build());
			return this;
		}

		@Override
		public LootPoolEntry build() {
			return new GroupEntry(this.entries.build(), this.getConditions());
		}
	}
}
