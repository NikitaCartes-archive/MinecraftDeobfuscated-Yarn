/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Schedule;

public class ScheduleBuilder {
    private final Schedule schedule;
    private final List<ActivityEntry> activities = Lists.newArrayList();

    public ScheduleBuilder(Schedule schedule) {
        this.schedule = schedule;
    }

    public ScheduleBuilder withActivity(int i, Activity activity) {
        this.activities.add(new ActivityEntry(i, activity));
        return this;
    }

    public Schedule build() {
        this.activities.stream().map(ActivityEntry::getActivity).collect(Collectors.toSet()).forEach(this.schedule::addActivity);
        this.activities.forEach(activityEntry -> {
            Activity activity = activityEntry.getActivity();
            this.schedule.getOtherRules(activity).forEach(scheduleRule -> scheduleRule.add(activityEntry.getStartTime(), 0.0f));
            this.schedule.getRule(activity).add(activityEntry.getStartTime(), 1.0f);
        });
        return this.schedule;
    }

    static class ActivityEntry {
        private final int startTime;
        private final Activity activity;

        public ActivityEntry(int i, Activity activity) {
            this.startTime = i;
            this.activity = activity;
        }

        public int getStartTime() {
            return this.startTime;
        }

        public Activity getActivity() {
            return this.activity;
        }
    }
}

