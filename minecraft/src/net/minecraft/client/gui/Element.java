package net.minecraft.client.gui;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.navigation.FocusedRect;
import net.minecraft.client.gui.navigation.GuiNavigation;
import net.minecraft.client.gui.navigation.GuiNavigationPath;

/**
 * Base GUI interface for handling callbacks related to
 * keyboard or mouse actions.
 * 
 * Mouse coordinate is bounded by the size of the window in
 * pixels.
 */
@Environment(EnvType.CLIENT)
public interface Element {
	long MAX_DOUBLE_CLICK_INTERVAL = 250L;

	/**
	 * Callback for when a mouse move event has been captured.
	 * 
	 * @see net.minecraft.client.Mouse#onCursorPos
	 * 
	 * @param mouseX the X coordinate of the mouse
	 * @param mouseY the Y coordinate of the mouse
	 */
	default void mouseMoved(double mouseX, double mouseY) {
	}

	/**
	 * Callback for when a mouse button down event
	 * has been captured.
	 * 
	 * The button number is identified by the constants in
	 * {@link org.lwjgl.glfw.GLFW GLFW} class.
	 * 
	 * @return {@code true} to indicate that the event handling is successful/valid
	 * @see net.minecraft.client.Mouse#onMouseButton(long, int, int, int)
	 * @see org.lwjgl.glfw.GLFW#GLFW_MOUSE_BUTTON_1
	 * 
	 * @param mouseX the X coordinate of the mouse
	 * @param mouseY the Y coordinate of the mouse
	 * @param button the mouse button number
	 */
	default boolean mouseClicked(double mouseX, double mouseY, int button) {
		return false;
	}

	/**
	 * Callback for when a mouse button release event
	 * has been captured.
	 * 
	 * The button number is identified by the constants in
	 * {@link org.lwjgl.glfw.GLFW GLFW} class.
	 * 
	 * @return {@code true} to indicate that the event handling is successful/valid
	 * @see net.minecraft.client.Mouse#onMouseButton(long, int, int, int)
	 * @see org.lwjgl.glfw.GLFW#GLFW_MOUSE_BUTTON_1
	 * 
	 * @param mouseX the X coordinate of the mouse
	 * @param mouseY the Y coordinate of the mouse
	 * @param button the mouse button number
	 */
	default boolean mouseReleased(double mouseX, double mouseY, int button) {
		return false;
	}

	/**
	 * Callback for when a mouse button drag event
	 * has been captured.
	 * 
	 * The button number is identified by the constants in
	 * {@link org.lwjgl.glfw.GLFW GLFW} class.
	 * 
	 * @return {@code true} to indicate that the event handling is successful/valid
	 * @see net.minecraft.client.Mouse#onCursorPos(long, double, double)
	 * @see org.lwjgl.glfw.GLFW#GLFW_MOUSE_BUTTON_1
	 * 
	 * @param mouseX the current X coordinate of the mouse
	 * @param mouseY the current Y coordinate of the mouse
	 * @param button the mouse button number
	 * @param deltaX the difference of the current X with the previous X coordinate
	 * @param deltaY the difference of the current Y with the previous Y coordinate
	 */
	default boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		return false;
	}

	/**
	 * Callback for when a mouse button scroll event
	 * has been captured.
	 * 
	 * @return {@code true} to indicate that the event handling is successful/valid
	 * @see net.minecraft.client.Mouse#onMouseScroll(long, double, double)
	 * 
	 * @param mouseX the X coordinate of the mouse
	 * @param mouseY the Y coordinate of the mouse
	 * @param amount value is {@code < 0} if scrolled down, {@code > 0} if scrolled up
	 */
	default boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		return false;
	}

	/**
	 * Callback for when a key down event has been captured.
	 * 
	 * The key code is identified by the constants in
	 * {@link org.lwjgl.glfw.GLFW GLFW} class.
	 * 
	 * @return {@code true} to indicate that the event handling is successful/valid
	 * @see net.minecraft.client.Keyboard#onKey(long, int, int, int, int)
	 * @see org.lwjgl.glfw.GLFW#GLFW_KEY_Q
	 * @see org.lwjgl.glfw.GLFWKeyCallbackI#invoke(long, int, int, int, int)
	 * 
	 * @param keyCode the named key code of the event as described in the {@link org.lwjgl.glfw.GLFW GLFW} class
	 * @param scanCode the unique/platform-specific scan code of the keyboard input
	 * @param modifiers a GLFW bitfield describing the modifier keys that are held down (see <a href="https://www.glfw.org/docs/3.3/group__mods.html">GLFW Modifier key flags</a>)
	 */
	default boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		return false;
	}

	/**
	 * Callback for when a key down event has been captured.
	 * 
	 * The key code is identified by the constants in
	 * {@link org.lwjgl.glfw.GLFW GLFW} class.
	 * 
	 * @return {@code true} to indicate that the event handling is successful/valid
	 * @see net.minecraft.client.Keyboard#onKey(long, int, int, int, int)
	 * @see org.lwjgl.glfw.GLFW#GLFW_KEY_Q
	 * @see org.lwjgl.glfw.GLFWKeyCallbackI#invoke(long, int, int, int, int)
	 * 
	 * @param keyCode the named key code of the event as described in the {@link org.lwjgl.glfw.GLFW GLFW} class
	 * @param scanCode the unique/platform-specific scan code of the keyboard input
	 * @param modifiers a GLFW bitfield describing the modifier keys that are held down (see <a href="https://www.glfw.org/docs/3.3/group__mods.html">GLFW Modifier key flags</a>)
	 */
	default boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		return false;
	}

	/**
	 * Callback for when a character input has been captured.
	 * 
	 * The key code is identified by the constants in
	 * {@link org.lwjgl.glfw.GLFW GLFW} class.
	 * 
	 * @return {@code true} to indicate that the event handling is successful/valid
	 * @see net.minecraft.client.Keyboard#onChar(long, int, int)
	 * @see org.lwjgl.glfw.GLFW#GLFW_KEY_Q
	 * @see org.lwjgl.glfw.GLFWKeyCallbackI#invoke(long, int, int, int, int)
	 * 
	 * @param chr the captured character
	 * @param modifiers a GLFW bitfield describing the modifier keys that are held down (see <a href="https://www.glfw.org/docs/3.3/group__mods.html">GLFW Modifier key flags</a>)
	 */
	default boolean charTyped(char chr, int modifiers) {
		return false;
	}

	@Nullable
	default GuiNavigationPath getNavigationPath(GuiNavigation navigation) {
		return null;
	}

	/**
	 * Checks if the mouse position is within the bound
	 * of the element.
	 * 
	 * @return {@code true} if the mouse is within the bound of the element, otherwise {@code false}
	 * 
	 * @param mouseX the X coordinate of the mouse
	 * @param mouseY the Y coordinate of the mouse
	 */
	default boolean isMouseOver(double mouseX, double mouseY) {
		return false;
	}

	void setFocused(boolean focused);

	boolean isFocused();

	@Nullable
	default GuiNavigationPath getFocusedPath() {
		return this.isFocused() ? GuiNavigationPath.of(this) : null;
	}

	default FocusedRect getNavigationFocus() {
		return FocusedRect.empty();
	}
}
