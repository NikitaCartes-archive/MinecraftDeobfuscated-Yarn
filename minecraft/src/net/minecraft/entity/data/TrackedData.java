package net.minecraft.entity.data;

public class TrackedData<T> {
	private final int id;
	private final TrackedDataHandler<T> dataType;

	public TrackedData(int id, TrackedDataHandler<T> dataType) {
		this.id = id;
		this.dataType = dataType;
	}

	public int getId() {
		return this.id;
	}

	public TrackedDataHandler<T> getType() {
		return this.dataType;
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (o != null && this.getClass() == o.getClass()) {
			TrackedData<?> trackedData = (TrackedData<?>)o;
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
