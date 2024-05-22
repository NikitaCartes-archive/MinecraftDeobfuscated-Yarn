package net.minecraft.entity.data;

import java.util.List;

public interface DataTracked {
	/**
	 * Called on the client when the tracked data is set.
	 * 
	 * <p>This can be overridden to refresh other fields when the tracked data
	 * is set or changed.
	 */
	void onTrackedDataSet(TrackedData<?> data);

	void onDataTrackerUpdate(List<DataTracker.SerializedEntry<?>> entries);
}
