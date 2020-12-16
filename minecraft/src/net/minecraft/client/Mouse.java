package net.minecraft.client;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.GlfwUtil;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.SmoothUtil;
import net.minecraft.util.math.MathHelper;
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
	private double eventDeltaWheel;
	private double lastMouseUpdateTime = Double.MIN_VALUE;
	private boolean cursorLocked;

	public Mouse(MinecraftClient client) {
		this.client = client;
	}

	private void onMouseButton(long window, int button, int action, int mods) {
		if (window == this.client.getWindow().getHandle()) {
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
				if (this.client.options.touchscreen && this.field_1796++ > 0) {
					return;
				}

				this.activeButton = i;
				this.glfwTime = GlfwUtil.getTime();
			} else if (this.activeButton != -1) {
				if (this.client.options.touchscreen && --this.field_1796 > 0) {
					return;
				}

				this.activeButton = -1;
			}

			boolean[] bls = new boolean[]{false};
			if (this.client.overlay == null) {
				if (this.client.currentScreen == null) {
					if (!this.cursorLocked && bl) {
						this.lockCursor();
					}
				} else {
					double d = this.x * (double)this.client.getWindow().getScaledWidth() / (double)this.client.getWindow().getWidth();
					double e = this.y * (double)this.client.getWindow().getScaledHeight() / (double)this.client.getWindow().getHeight();
					if (bl) {
						Screen.wrapScreenError(
							() -> bls[0] = this.client.currentScreen.mouseClicked(d, e, i), "mouseClicked event handler", this.client.currentScreen.getClass().getCanonicalName()
						);
					} else {
						Screen.wrapScreenError(
							() -> bls[0] = this.client.currentScreen.mouseReleased(d, e, i), "mouseReleased event handler", this.client.currentScreen.getClass().getCanonicalName()
						);
					}
				}
			}

			if (!bls[0] && (this.client.currentScreen == null || this.client.currentScreen.passEvents) && this.client.overlay == null) {
				if (i == 0) {
					this.leftButtonClicked = bl;
				} else if (i == 2) {
					this.middleButtonClicked = bl;
				} else if (i == 1) {
					this.rightButtonClicked = bl;
				}

				KeyBinding.setKeyPressed(InputUtil.Type.MOUSE.createFromCode(i), bl);
				if (bl) {
					if (this.client.player.isSpectator() && i == 2) {
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
	 * @param window the window handle
	 * @param horizontal the horizontal scroll distance
	 * @param vertical the vertical scroll distance
	 */
	private void onMouseScroll(long window, double horizontal, double vertical) {
		if (window == MinecraftClient.getInstance().getWindow().getHandle()) {
			double d = (this.client.options.discreteMouseScroll ? Math.signum(vertical) : vertical) * this.client.options.mouseWheelSensitivity;
			if (this.client.overlay == null) {
				if (this.client.currentScreen != null) {
					double e = this.x * (double)this.client.getWindow().getScaledWidth() / (double)this.client.getWindow().getWidth();
					double f = this.y * (double)this.client.getWindow().getScaledHeight() / (double)this.client.getWindow().getHeight();
					this.client.currentScreen.mouseScrolled(e, f, d);
				} else if (this.client.player != null) {
					if (this.eventDeltaWheel != 0.0 && Math.signum(d) != Math.signum(this.eventDeltaWheel)) {
						this.eventDeltaWheel = 0.0;
					}

					this.eventDeltaWheel += d;
					float g = (float)((int)this.eventDeltaWheel);
					if (g == 0.0F) {
						return;
					}

					this.eventDeltaWheel -= (double)g;
					if (this.client.player.isSpectator()) {
						if (this.client.inGameHud.getSpectatorHud().isOpen()) {
							this.client.inGameHud.getSpectatorHud().cycleSlot((double)(-g));
						} else {
							float h = MathHelper.clamp(this.client.player.getAbilities().getFlySpeed() + g * 0.005F, 0.0F, 0.2F);
							this.client.player.getAbilities().setFlySpeed(h);
						}
					} else {
						this.client.player.getInventory().scrollInHotbar((double)g);
					}
				}
			}
		}
	}

	private void method_29616(long l, List<Path> list) {
		if (this.client.currentScreen != null) {
			this.client.currentScreen.filesDragged(list);
		}
	}

	public void setup(long l) {
		InputUtil.setMouseCallbacks(
			l,
			(lx, d, e) -> this.client.execute(() -> this.onCursorPos(lx, d, e)),
			(lx, i, j, k) -> this.client.execute(() -> this.onMouseButton(lx, i, j, k)),
			(lx, d, e) -> this.client.execute(() -> this.onMouseScroll(lx, d, e)),
			(lx, i, m) -> {
				Path[] paths = new Path[i];

				for (int j = 0; j < i; j++) {
					paths[j] = Paths.get(GLFWDropCallback.getName(m, j));
				}

				this.client.execute(() -> this.method_29616(lx, Arrays.asList(paths)));
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

			Element element = this.client.currentScreen;
			if (element != null && this.client.overlay == null) {
				double d = x * (double)this.client.getWindow().getScaledWidth() / (double)this.client.getWindow().getWidth();
				double e = y * (double)this.client.getWindow().getScaledHeight() / (double)this.client.getWindow().getHeight();
				Screen.wrapScreenError(() -> element.mouseMoved(d, e), "mouseMoved event handler", element.getClass().getCanonicalName());
				if (this.activeButton != -1 && this.glfwTime > 0.0) {
					double f = (x - this.x) * (double)this.client.getWindow().getScaledWidth() / (double)this.client.getWindow().getWidth();
					double g = (y - this.y) * (double)this.client.getWindow().getScaledHeight() / (double)this.client.getWindow().getHeight();
					Screen.wrapScreenError(() -> element.mouseDragged(d, e, this.activeButton, f, g), "mouseDragged event handler", element.getClass().getCanonicalName());
				}
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
			double f = this.client.options.mouseSensitivity * 0.6F + 0.2F;
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
			if (this.client.options.invertYMouse) {
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
				InputUtil.setCursorParameters(this.client.getWindow().getHandle(), 212995, this.x, this.y);
				this.client.openScreen(null);
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
			InputUtil.setCursorParameters(this.client.getWindow().getHandle(), 212993, this.x, this.y);
		}
	}

	public void method_30134() {
		this.hasResolutionChanged = true;
	}
}
