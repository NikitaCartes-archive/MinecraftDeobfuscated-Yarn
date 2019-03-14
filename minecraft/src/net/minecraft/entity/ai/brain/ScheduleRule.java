package net.minecraft.entity.ai.brain;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectSortedMap;
import java.util.List;

public class ScheduleRule {
	private final List<ScheduleRuleEntry> entries = Lists.<ScheduleRuleEntry>newArrayList();
	private int field_18613;

	public ScheduleRule withEntry(int i, float f) {
		this.entries.add(new ScheduleRuleEntry(i, f));
		this.sort();
		return this;
	}

	private void sort() {
		Int2ObjectSortedMap<ScheduleRuleEntry> int2ObjectSortedMap = new Int2ObjectAVLTreeMap<>();
		this.entries.forEach(scheduleRuleEntry -> {
			ScheduleRuleEntry var10000 = int2ObjectSortedMap.put(scheduleRuleEntry.getStartTime(), scheduleRuleEntry);
		});
		this.entries.clear();
		this.entries.addAll(int2ObjectSortedMap.values());
		this.field_18613 = 0;
	}

	public float getPriority(int i) {
		if (this.entries.size() <= 0) {
			return 0.0F;
		} else {
			ScheduleRuleEntry scheduleRuleEntry = (ScheduleRuleEntry)this.entries.get(this.field_18613);
			ScheduleRuleEntry scheduleRuleEntry2 = (ScheduleRuleEntry)this.entries.get(this.entries.size() - 1);
			boolean bl = i < scheduleRuleEntry.getStartTime();
			int j = bl ? 0 : this.field_18613;
			float f = bl ? scheduleRuleEntry2.getPriority() : scheduleRuleEntry.getPriority();

			for (int k = j; k < this.entries.size(); k++) {
				ScheduleRuleEntry scheduleRuleEntry3 = (ScheduleRuleEntry)this.entries.get(k);
				if (scheduleRuleEntry3.getStartTime() > i) {
					break;
				}

				this.field_18613 = k;
				f = scheduleRuleEntry3.getPriority();
			}

			return f;
		}
	}
}
