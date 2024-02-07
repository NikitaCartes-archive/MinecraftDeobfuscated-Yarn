package net.minecraft.entity.data;

public record TrackedData<T>(int id, TrackedDataHandler<T> dataType) {
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
