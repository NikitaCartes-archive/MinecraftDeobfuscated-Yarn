package net.minecraft.entity.attribute;

import javax.annotation.Nullable;

public abstract class AbstractEntityAttribute implements EntityAttribute {
	private final EntityAttribute parent;
	private final String id;
	private final double defaultValue;
	private boolean tracked;

	protected AbstractEntityAttribute(@Nullable EntityAttribute entityAttribute, String string, double d) {
		this.parent = entityAttribute;
		this.id = string;
		this.defaultValue = d;
		if (string == null) {
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

	public AbstractEntityAttribute setTracked(boolean bl) {
		this.tracked = bl;
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

	public boolean equals(Object object) {
		return object instanceof EntityAttribute && this.id.equals(((EntityAttribute)object).getId());
	}
}
