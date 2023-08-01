package net.minecraft.client;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.navigation.GuiNavigationType;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.GlfwUtil;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.SmoothUtil;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWDropCallback;

@Environment(EnvType.CLIENT)
public class Mouse {
	private final MinecraftClient client;
	private boolean leftButtonClicked;
	private boolean middleButtonClicked;
	private boolean rightButtonClicked;
	private double x;
	private double y;
	private int controlLeftTicks;
	private int activeButton = -1;
	private boolean hasResolutionChanged = true;
	private int field_1796;
	private double glfwTime;
	private final SmoothUtil cursorXSmoother = new SmoothUtil();
	private final SmoothUtil cursorYSmoother = new SmoothUtil();
	private double cursorDeltaX;
	private double cursorDeltaY;
	private double eventDeltaHorizontalWheel;
	private double eventDeltaVerticalWheel;
	private double lastMouseUpdateTime = Double.MIN_VALUE;
	private boolean cursorLocked;

	public Mouse(MinecraftClient client) {
		this.client = client;
	}

	private void onMouseButton(long window, int button, int action, int mods) {
		if (window == this.client.getWindow().getHandle()) {
			if (this.client.currentScreen != null) {
				this.client.setNavigationType(GuiNavigationType.MOUSE);
			}

			boolean bl = action == 1;
			if (MinecraftClient.IS_SYSTEM_MAC && button == 0) {
				if (bl) {
					if ((mods & 2) == 2) {
						button = 1;
						this.controlLeftTicks++;
					}
				} else if (this.controlLeftTicks > 0) {
					button = 1;
					this.controlLeftTicks--;
				}
			}

			int i = button;
			if (bl) {
				if (this.client.options.getTouchscreen().getValue() && this.field_1796++ > 0) {
					return;
				}

				this.activeButton = i;
				this.glfwTime = GlfwUtil.getTime();
			} else if (this.activeButton != -1) {
				if (this.client.options.getTouchscreen().getValue() && --this.field_1796 > 0) {
					return;
				}

				this.activeButton = -1;
			}

			boolean[] bls = new boolean[]{false};
			if (this.client.getOverlay() == null) {
				if (this.client.currentScreen == null) {
					if (!this.cursorLocked && bl) {
						this.lockCursor();
					}
				} else {
					double d = this.x * (double)this.client.getWindow().getScaledWidth() / (double)this.client.getWindow().getWidth();
					double e = this.y * (double)this.client.getWindow().getScaledHeight() / (double)this.client.getWindow().getHeight();
					Screen screen = this.client.currentScreen;
					if (bl) {
						screen.applyMousePressScrollNarratorDelay();
						Screen.wrapScreenError(() -> bls[0] = screen.mouseClicked(d, e, i), "mouseClicked event handler", screen.getClass().getCanonicalName());
					} else {
						Screen.wrapScreenError(() -> bls[0] = screen.mouseReleased(d, e, i), "mouseReleased event handler", screen.getClass().getCanonicalName());
					}
				}
			}

			if (!bls[0] && this.client.currentScreen == null && this.client.getOverlay() == null) {
				if (i == 0) {
					this.leftButtonClicked = bl;
				} else if (i == GLFW.GLFW_MOUSE_BUTTON_MIDDLE) {
					this.middleButtonClicked = bl;
				} else if (i == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
					this.rightButtonClicked = bl;
				}

				KeyBinding.setKeyPressed(InputUtil.Type.MOUSE.createFromCode(i), bl);
				if (bl) {
					if (this.client.player.isSpectator() && i == GLFW.GLFW_MOUSE_BUTTON_MIDDLE) {
						this.client.inGameHud.getSpectatorHud().useSelectedCommand();
					} else {
						KeyBinding.onKeyPressed(InputUtil.Type.MOUSE.createFromCode(i));
					}
				}
			}
		}
	}

	/**
	 * Called when a mouse is used to scroll.
	 * 
	 * @param vertical the vertical scroll distance
	 * @param horizontal the horizontal scroll distance
	 * @param window the window handle
	 */
	private void onMouseScroll(long window, double horizontal, double vertical) {
		if (window == MinecraftClient.getInstance().getWindow().getHandle()) {
			boolean bl = this.client.options.getDiscreteMouseScroll().getValue();
			double d = this.client.options.getMouseWheelSensitivity().getValue();
			double e = (bl ? Math.signum(horizontal) : horizontal) * d;
			double f = (bl ? Math.signum(vertical) : vertical) * d;
			if (this.client.getOverlay() == null) {
				if (this.client.currentScreen != null) {
					double g = this.x * (double)this.client.getWindow().getScaledWidth() / (double)this.client.getWindow().getWidth();
					double h = this.y * (double)this.client.getWindow().getScaledHeight() / (double)this.client.getWindow().getHeight();
					this.client.currentScreen.mouseScrolled(g, h, e, f);
					this.client.currentScreen.applyMousePressScrollNarratorDelay();
				} else if (this.client.player != null) {
					if (this.eventDeltaHorizontalWheel != 0.0 && Math.signum(e) != Math.signum(this.eventDeltaHorizontalWheel)) {
						this.eventDeltaHorizontalWheel = 0.0;
					}

					if (this.eventDeltaVerticalWheel != 0.0 && Math.signum(f) != Math.signum(this.eventDeltaVerticalWheel)) {
						this.eventDeltaVerticalWheel = 0.0;
					}

					this.eventDeltaHorizontalWheel += e;
					this.eventDeltaVerticalWheel += f;
					int i = (int)this.eventDeltaHorizontalWheel;
					int j = (int)this.eventDeltaVerticalWheel;
					if (i == 0 && j == 0) {
						return;
					}

					this.eventDeltaHorizontalWheel -= (double)i;
					this.eventDeltaVerticalWheel -= (double)j;
					int k = j == 0 ? -i : j;
					if (this.client.player.isSpectator()) {
						if (this.client.inGameHud.getSpectatorHud().isOpen()) {
							this.client.inGameHud.getSpectatorHud().cycleSlot(-k);
						} else {
							float l = MathHelper.clamp(this.client.player.getAbilities().getFlySpeed() + (float)j * 0.005F, 0.0F, 0.2F);
							this.client.player.getAbilities().setFlySpeed(l);
						}
					} else {
						this.client.player.getInventory().scrollInHotbar((double)k);
					}
				}
			}
		}
	}

	private void onFilesDropped(long window, List<Path> paths) {
		if (this.client.currentScreen != null) {
			this.client.currentScreen.filesDragged(paths);
		}
	}

	public void setup(long window) {
		InputUtil.setMouseCallbacks(
			window,
			(windowx, x, y) -> this.client.execute(() -> this.onCursorPos(windowx, x, y)),
			(windowx, button, action, modifiers) -> this.client.execute(() -> this.onMouseButton(windowx, button, action, modifiers)),
			(windowx, offsetX, offsetY) -> this.client.execute(() -> this.onMouseScroll(windowx, offsetX, offsetY)),
			(windowx, count, names) -> {
				Path[] paths = new Path[count];

				for (int i = 0; i < count; i++) {
					paths[i] = Paths.get(GLFWDropCallback.getName(names, i));
				}

				this.client.execute(() -> this.onFilesDropped(windowx, Arrays.asList(paths)));
			}
		);
	}

	private void onCursorPos(long window, double x, double y) {
		if (window == MinecraftClient.getInstance().getWindow().getHandle()) {
			if (this.hasResolutionChanged) {
				this.x = x;
				this.y = y;
				this.hasResolutionChanged = false;
			}

			Screen screen = this.client.currentScreen;
			if (screen != null && this.client.getOverlay() == null) {
				double d = x * (double)this.client.getWindow().getScaledWidth() / (double)this.client.getWindow().getWidth();
				double e = y * (double)this.client.getWindow().getScaledHeight() / (double)this.client.getWindow().getHeight();
				Screen.wrapScreenError(() -> screen.mouseMoved(d, e), "mouseMoved event handler", screen.getClass().getCanonicalName());
				if (this.activeButton != -1 && this.glfwTime > 0.0) {
					double f = (x - this.x) * (double)this.client.getWindow().getScaledWidth() / (double)this.client.getWindow().getWidth();
					double g = (y - this.y) * (double)this.client.getWindow().getScaledHeight() / (double)this.client.getWindow().getHeight();
					Screen.wrapScreenError(() -> screen.mouseDragged(d, e, this.activeButton, f, g), "mouseDragged event handler", screen.getClass().getCanonicalName());
				}

				screen.applyMouseMoveNarratorDelay();
			}

			this.client.getProfiler().push("mouse");
			if (this.isCursorLocked() && this.client.isWindowFocused()) {
				this.cursorDeltaX = this.cursorDeltaX + (x - this.x);
				this.cursorDeltaY = this.cursorDeltaY + (y - this.y);
			}

			this.updateMouse();
			this.x = x;
			this.y = y;
			this.client.getProfiler().pop();
		}
	}

	public void updateMouse() {
		double d = GlfwUtil.getTime();
		double e = d - this.lastMouseUpdateTime;
		this.lastMouseUpdateTime = d;
		if (this.isCursorLocked() && this.client.isWindowFocused()) {
			double f = this.client.options.getMouseSensitivity().getValue() * 0.6F + 0.2F;
			double g = f * f * f;
			double h = g * 8.0;
			double k;
			double l;
			if (this.client.options.smoothCameraEnabled) {
				double i = this.cursorXSmoother.smooth(this.cursorDeltaX * h, e * h);
				double j = this.cursorYSmoother.smooth(this.cursorDeltaY * h, e * h);
				k = i;
				l = j;
			} else if (this.client.options.getPerspective().isFirstPerson() && this.client.player.isUsingSpyglass()) {
				this.cursorXSmoother.clear();
				this.cursorYSmoother.clear();
				k = this.cursorDeltaX * g;
				l = this.cursorDeltaY * g;
			} else {
				this.cursorXSmoother.clear();
				this.cursorYSmoother.clear();
				k = this.cursorDeltaX * h;
				l = this.cursorDeltaY * h;
			}

			this.cursorDeltaX = 0.0;
			this.cursorDeltaY = 0.0;
			int m = 1;
			if (this.client.options.getInvertYMouse().getValue()) {
				m = -1;
			}

			this.client.getTutorialManager().onUpdateMouse(k, l);
			if (this.client.player != null) {
				this.client.player.changeLookDirection(k, l * (double)m);
			}
		} else {
			this.cursorDeltaX = 0.0;
			this.cursorDeltaY = 0.0;
		}
	}

	public boolean wasLeftButtonClicked() {
		return this.leftButtonClicked;
	}

	public boolean wasMiddleButtonClicked() {
		return this.middleButtonClicked;
	}

	public boolean wasRightButtonClicked() {
		return this.rightButtonClicked;
	}

	public double getX() {
		return this.x;
	}

	public double getY() {
		return this.y;
	}

	public void onResolutionChanged() {
		this.hasResolutionChanged = true;
	}

	public boolean isCursorLocked() {
		return this.cursorLocked;
	}

	public void lockCursor() {
		if (this.client.isWindowFocused()) {
			if (!this.cursorLocked) {
				if (!MinecraftClient.IS_SYSTEM_MAC) {
					KeyBinding.updatePressedStates();
				}

				this.cursorLocked = true;
				this.x = (double)(this.client.getWindow().getWidth() / 2);
				this.y = (double)(this.client.getWindow().getHeight() / 2);
				InputUtil.setCursorParameters(this.client.getWindow().getHandle(), InputUtil.GLFW_CURSOR_DISABLED, this.x, this.y);
				this.client.setScreen(null);
				this.client.attackCooldown = 10000;
				this.hasResolutionChanged = true;
			}
		}
	}

	public void unlockCursor() {
		if (this.cursorLocked) {
			this.cursorLocked = false;
			this.x = (double)(this.client.getWindow().getWidth() / 2);
			this.y = (double)(this.client.getWindow().getHeight() / 2);
			InputUtil.setCursorParameters(this.client.getWindow().getHandle(), InputUtil.GLFW_CURSOR_NORMAL, this.x, this.y);
		}
	}

	public void setResolutionChanged() {
		this.hasResolutionChanged = true;
	}
}
