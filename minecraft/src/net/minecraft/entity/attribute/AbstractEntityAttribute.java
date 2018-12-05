package net.minecraft.entity.attribute;

import javax.annotation.Nullable;

public abstract class AbstractEntityAttribute implements EntityAttribute {
	private final EntityAttribute parent;
	private final String id;
	private final double defaultValue;
	private boolean field_6338;

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
	public boolean method_6168() {
		return this.field_6338;
	}

	public AbstractEntityAttribute method_6212(boolean bl) {
		this.field_6338 = bl;
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
