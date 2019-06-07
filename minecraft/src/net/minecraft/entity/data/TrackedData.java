package net.minecraft.entity.data;

public class TrackedData<T> {
	private final int id;
	private final TrackedDataHandler<T> dataType;

	public TrackedData(int i, TrackedDataHandler<T> trackedDataHandler) {
		this.id = i;
		this.dataType = trackedDataHandler;
	}

	public int getId() {
		return this.id;
	}

	public TrackedDataHandler<T> getType() {
		return this.dataType;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			TrackedData<?> trackedData = (TrackedData<?>)object;
			return this.id == trackedData.id;
		} else {
			return false;
		}
	}

	public int hashCode() {
		return this.id;
	}

	public String toString() {
		return "<entity data: " + this.id + ">";
	}
}
