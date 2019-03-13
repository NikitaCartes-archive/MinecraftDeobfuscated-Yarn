package net.minecraft.entity.data;

public class TrackedData<T> {
	private final int id;
	private final TrackedDataHandler<T> field_13307;

	public TrackedData(int i, TrackedDataHandler<T> trackedDataHandler) {
		this.id = i;
		this.field_13307 = trackedDataHandler;
	}

	public int getId() {
		return this.id;
	}

	public TrackedDataHandler<T> method_12712() {
		return this.field_13307;
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
}
