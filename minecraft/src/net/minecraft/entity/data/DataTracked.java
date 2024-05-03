package net.minecraft.entity.data;

import java.util.List;

public interface DataTracked {
	void onTrackedDataSet(TrackedData<?> data);

	void onDataTrackerUpdate(List<DataTracker.SerializedEntry<?>> entries);
}
