package net.minecraft.container;

public abstract class Property {
	private int id;

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
			private int field_17312;

			@Override
			public int get() {
				return this.field_17312;
			}

			@Override
			public void set(int i) {
				this.field_17312 = i;
			}
		};
	}

	public abstract int get();

	public abstract void set(int i);

	public boolean method_17408() {
		int i = this.get();
		boolean bl = i != this.id;
		this.id = i;
		return bl;
	}
}
