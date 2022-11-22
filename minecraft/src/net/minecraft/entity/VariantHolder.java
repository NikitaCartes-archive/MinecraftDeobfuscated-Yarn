package net.minecraft.entity;

public interface VariantHolder<T> {
	void setVariant(T variant);

	T getVariant();
}
