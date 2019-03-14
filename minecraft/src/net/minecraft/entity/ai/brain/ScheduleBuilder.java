package net.minecraft.entity.ai.brain;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

class ScheduleBuilder {
	private final Schedule schedule;
	private final List<ScheduleBuilder.ActivityEntry> activities = Lists.<ScheduleBuilder.ActivityEntry>newArrayList();

	public ScheduleBuilder(Schedule schedule) {
		this.schedule = schedule;
	}

	public ScheduleBuilder withActivity(int i, Activity activity) {
		this.activities.add(new ScheduleBuilder.ActivityEntry(i, activity));
		return this;
	}

	public Schedule build() {
		((Set)this.activities.stream().map(ScheduleBuilder.ActivityEntry::getActivity).collect(Collectors.toSet())).forEach(this.schedule::addActivity);
		this.activities.forEach(activityEntry -> {
			Activity activity = activityEntry.getActivity();
			this.schedule.getOtherRules(activity).forEach(scheduleRule -> scheduleRule.withEntry(activityEntry.getStartTime(), 0.0F));
			this.schedule.getRule(activity).withEntry(activityEntry.getStartTime(), 1.0F);
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
