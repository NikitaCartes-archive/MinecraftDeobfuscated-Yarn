package net.minecraft.container;

/**
 * A property delegate represents an indexed list of integer properties.
 * 
 * <p>Property delegates are used for displaying integer values in screens,
 * such as the progress bars in furnaces.
 */
public interface PropertyDelegate {
	int get(int index);

	void set(int index, int value);

	int size();
}
