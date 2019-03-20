package net.minecraft.entity.ai.brain;

import com.google.common.collect.Maps;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Schedule {
	public static final Schedule EMPTY = register("empty").withActivity(0, Activity.field_18595).build();
	public static final Schedule SIMPLE = register("simple").withActivity(5000, Activity.field_18596).withActivity(11000, Activity.field_18597).build();
	public static final Schedule VILLAGER_BABY = register("villager_baby")
		.withActivity(10, Activity.field_18595)
		.withActivity(3000, Activity.field_18885)
		.withActivity(8000, Activity.field_18595)
		.withActivity(11000, Activity.field_18885)
		.withActivity(13000, Activity.field_18597)
		.build();
	public static final Schedule VILLAGER_DEFAULT = register("villager_default")
		.withActivity(10, Activity.field_18595)
		.withActivity(2000, Activity.field_18596)
		.withActivity(9000, Activity.field_18598)
		.withActivity(12000, Activity.field_18595)
		.withActivity(13000, Activity.field_18597)
		.build();
	private final Map<Activity, ScheduleRule> scheduleRules = Maps.<Activity, ScheduleRule>newHashMap();

	protected static ScheduleBuilder register(String string) {
		Schedule schedule = Registry.SCHEDULE.add(new Identifier(string), new Schedule());
		return new ScheduleBuilder(schedule);
	}

	protected void addActivity(Activity activity) {
		if (!this.scheduleRules.containsKey(activity)) {
			this.scheduleRules.put(activity, new ScheduleRule());
		}
	}

	protected ScheduleRule getRule(Activity activity) {
		return (ScheduleRule)this.scheduleRules.get(activity);
	}

	protected List<ScheduleRule> getOtherRules(Activity activity) {
		return (List<ScheduleRule>)this.scheduleRules
			.entrySet()
			.stream()
			.filter(entry -> entry.getKey() != activity)
			.map(Entry::getValue)
			.collect(Collectors.toList());
	}

	public Activity getActivityForTime(int i) {
		return (Activity)this.scheduleRules
			.entrySet()
			.stream()
			.max(Comparator.comparingDouble(entry -> (double)((ScheduleRule)entry.getValue()).getPriority(i)))
			.map(Entry::getKey)
			.orElse(Activity.field_18595);
	}
}
