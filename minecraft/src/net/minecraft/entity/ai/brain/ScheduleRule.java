package net.minecraft.entity.ai.brain;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectSortedMap;
import java.util.Collection;
import java.util.List;

public class ScheduleRule {
	private final List<ScheduleRuleEntry> entries = Lists.<ScheduleRuleEntry>newArrayList();
	private int prioritizedEntryIndex;

	public ImmutableList<ScheduleRuleEntry> getEntries() {
		return ImmutableList.copyOf(this.entries);
	}

	public ScheduleRule add(int startTime, float priority) {
		this.entries.add(new ScheduleRuleEntry(startTime, priority));
		this.sort();
		return this;
	}

	public ScheduleRule add(Collection<ScheduleRuleEntry> entries) {
		this.entries.addAll(entries);
		this.sort();
		return this;
	}

	private void sort() {
		Int2ObjectSortedMap<ScheduleRuleEntry> int2ObjectSortedMap = new Int2ObjectAVLTreeMap<>();
		this.entries.forEach(scheduleRuleEntry -> int2ObjectSortedMap.put(scheduleRuleEntry.getStartTime(), scheduleRuleEntry));
		this.entries.clear();
		this.entries.addAll(int2ObjectSortedMap.values());
		this.prioritizedEntryIndex = 0;
	}

	public float getPriority(int time) {
		if (this.entries.size() <= 0) {
			return 0.0F;
		} else {
			ScheduleRuleEntry scheduleRuleEntry = (ScheduleRuleEntry)this.entries.get(this.prioritizedEntryIndex);
			ScheduleRuleEntry scheduleRuleEntry2 = (ScheduleRuleEntry)this.entries.get(this.entries.size() - 1);
			boolean bl = time < scheduleRuleEntry.getStartTime();
			int i = bl ? 0 : this.prioritizedEntryIndex;
			float f = bl ? scheduleRuleEntry2.getPriority() : scheduleRuleEntry.getPriority();

			for (int j = i; j < this.entries.size(); j++) {
				ScheduleRuleEntry scheduleRuleEntry3 = (ScheduleRuleEntry)this.entries.get(j);
				if (scheduleRuleEntry3.getStartTime() > time) {
					break;
				}

				this.prioritizedEntryIndex = j;
				f = scheduleRuleEntry3.getPriority();
			}

			return f;
		}
	}
}
