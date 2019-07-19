/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;
import java.util.List;
import net.minecraft.entity.ai.brain.ScheduleRuleEntry;

public class ScheduleRule {
    private final List<ScheduleRuleEntry> entries = Lists.newArrayList();
    private int field_18613;

    public ScheduleRule add(int i, float f) {
        this.entries.add(new ScheduleRuleEntry(i, f));
        this.sort();
        return this;
    }

    private void sort() {
        Int2ObjectAVLTreeMap int2ObjectSortedMap = new Int2ObjectAVLTreeMap();
        this.entries.forEach(scheduleRuleEntry -> int2ObjectSortedMap.put(scheduleRuleEntry.getStartTime(), scheduleRuleEntry));
        this.entries.clear();
        this.entries.addAll(int2ObjectSortedMap.values());
        this.field_18613 = 0;
    }

    public float getPriority(int i) {
        ScheduleRuleEntry scheduleRuleEntry3;
        if (this.entries.size() <= 0) {
            return 0.0f;
        }
        ScheduleRuleEntry scheduleRuleEntry = this.entries.get(this.field_18613);
        ScheduleRuleEntry scheduleRuleEntry2 = this.entries.get(this.entries.size() - 1);
        boolean bl = i < scheduleRuleEntry.getStartTime();
        int j = bl ? 0 : this.field_18613;
        float f = bl ? scheduleRuleEntry2.getPriority() : scheduleRuleEntry.getPriority();
        int k = j;
        while (k < this.entries.size() && (scheduleRuleEntry3 = this.entries.get(k)).getStartTime() <= i) {
            this.field_18613 = k++;
            f = scheduleRuleEntry3.getPriority();
        }
        return f;
    }
}

