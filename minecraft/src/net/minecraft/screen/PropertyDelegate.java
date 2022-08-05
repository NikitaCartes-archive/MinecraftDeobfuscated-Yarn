package net.minecraft.screen;

/**
 * A property delegate represents an indexed list of integer properties.
 * 
 * <p>The delegate is passed when creating the screen handler.
 * On the server, access to the property's value is delegated to the delegate (which in
 * turn delegates to another object like a block entity instance).
 * On the client, access to the property's value still uses the synced value.
 * 
 * @see Property#create(PropertyDelegate, int)
 * @see ScreenHandler#addProperties(PropertyDelegate)
 */
public interface PropertyDelegate {
	int get(int index);

	void set(int index, int value);

	int size();
}
