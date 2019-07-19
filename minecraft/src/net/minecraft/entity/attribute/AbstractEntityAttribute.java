package net.minecraft.entity.attribute;

import javax.annotation.Nullable;

public abstract class AbstractEntityAttribute implements EntityAttribute {
	private final EntityAttribute parent;
	private final String id;
	private final double defaultValue;
	private boolean tracked;

	protected AbstractEntityAttribute(@Nullable EntityAttribute parent, String id, double defaultValue) {
		this.parent = parent;
		this.id = id;
		this.defaultValue = defaultValue;
		if (id == null) {
			throw new IllegalArgumentException("Name cannot be null!");
		}
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public double getDefaultValue() {
		return this.defaultValue;
	}

	@Override
	public boolean isTracked() {
		return this.tracked;
	}

	public AbstractEntityAttribute setTracked(boolean tracked) {
		this.tracked = tracked;
		return this;
	}

	@Nullable
	@Override
	public EntityAttribute getParent() {
		return this.parent;
	}

	public int hashCode() {
		return this.id.hashCode();
	}

	public boolean equals(Object o) {
		return o instanceof EntityAttribute && this.id.equals(((EntityAttribute)o).getId());
	}
}
