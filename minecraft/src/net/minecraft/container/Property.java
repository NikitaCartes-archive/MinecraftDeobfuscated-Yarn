package net.minecraft.container;

public abstract class Property {
	private int oldValue;

	public static Property create(PropertyDelegate propertyDelegate, int i) {
		return new Property() {
			@Override
			public int get() {
				return propertyDelegate.get(i);
			}

			@Override
			public void set(int i) {
				propertyDelegate.set(i, i);
			}
		};
	}

	public static Property create(int[] is, int i) {
		return new Property() {
			@Override
			public int get() {
				return is[i];
			}

			@Override
			public void set(int i) {
				is[i] = i;
			}
		};
	}

	public static Property create() {
		return new Property() {
			private int value;

			@Override
			public int get() {
				return this.value;
			}

			@Override
			public void set(int i) {
				this.value = i;
			}
		};
	}

	public abstract int get();

	public abstract void set(int i);

	public boolean detectChanges() {
		int i = this.get();
		boolean bl = i != this.oldValue;
		this.oldValue = i;
		return bl;
	}
}
