package net.minecraft.container;

public abstract class Property {
	private int oldValue;

	public static Property create(PropertyDelegate propertyDelegate, int key) {
		return new Property() {
			@Override
			public int get() {
				return propertyDelegate.get(key);
			}

			@Override
			public void set(int value) {
				propertyDelegate.set(key, value);
			}
		};
	}

	public static Property create(int[] is, int key) {
		return new Property() {
			@Override
			public int get() {
				return is[key];
			}

			@Override
			public void set(int value) {
				is[key] = value;
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
			public void set(int value) {
				this.value = value;
			}
		};
	}

	public abstract int get();

	public abstract void set(int value);

	public boolean detectChanges() {
		int i = this.get();
		boolean bl = i != this.oldValue;
		this.oldValue = i;
		return bl;
	}
}
