package net.minecraft.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.GlfwUtil;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.SmoothUtil;
import net.minecraft.util.math.MathHelper;

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
	private double field_1785 = Double.MIN_VALUE;
	private boolean isCursorLocked;

	public Mouse(MinecraftClient client) {
		this.client = client;
	}

	private void onMouseButton(long window, int button, int action, int mods) {
		if (window == this.client.window.getHandle()) {
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
					if (!this.isCursorLocked && bl) {
						this.lockCursor();
					}
				} else {
					double d = this.x * (double)this.client.window.getScaledWidth() / (double)this.client.window.getWidth();
					double e = this.y * (double)this.client.window.getScaledHeight() / (double)this.client.window.getHeight();
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
						this.client.inGameHud.getSpectatorHud().method_1983();
					} else {
						KeyBinding.onKeyPressed(InputUtil.Type.MOUSE.createFromCode(i));
					}
				}
			}
		}
	}

	private void onMouseScroll(long window, double d, double e) {
		if (window == MinecraftClient.getInstance().window.getHandle()) {
			double f = (this.client.options.discreteMouseScroll ? Math.signum(e) : e) * this.client.options.mouseWheelSensitivity;
			if (this.client.overlay == null) {
				if (this.client.currentScreen != null) {
					double g = this.x * (double)this.client.window.getScaledWidth() / (double)this.client.window.getWidth();
					double h = this.y * (double)this.client.window.getScaledHeight() / (double)this.client.window.getHeight();
					this.client.currentScreen.mouseScrolled(g, h, f);
				} else if (this.client.player != null) {
					if (this.eventDeltaWheel != 0.0 && Math.signum(f) != Math.signum(this.eventDeltaWheel)) {
						this.eventDeltaWheel = 0.0;
					}

					this.eventDeltaWheel += f;
					float i = (float)((int)this.eventDeltaWheel);
					if (i == 0.0F) {
						return;
					}

					this.eventDeltaWheel -= (double)i;
					if (this.client.player.isSpectator()) {
						if (this.client.inGameHud.getSpectatorHud().method_1980()) {
							this.client.inGameHud.getSpectatorHud().method_1976((double)(-i));
						} else {
							float j = MathHelper.clamp(this.client.player.abilities.getFlySpeed() + i * 0.005F, 0.0F, 0.2F);
							this.client.player.abilities.setFlySpeed(j);
						}
					} else {
						this.client.player.inventory.scrollInHotbar((double)i);
					}
				}
			}
		}
	}

	public void setup(long l) {
		InputUtil.setMouseCallbacks(l, this::onCursorPos, this::onMouseButton, this::onMouseScroll);
	}

	private void onCursorPos(long window, double x, double y) {
		if (window == MinecraftClient.getInstance().window.getHandle()) {
			if (this.hasResolutionChanged) {
				this.x = x;
				this.y = y;
				this.hasResolutionChanged = false;
			}

			Element element = this.client.currentScreen;
			if (element != null && this.client.overlay == null) {
				double d = x * (double)this.client.window.getScaledWidth() / (double)this.client.window.getWidth();
				double e = y * (double)this.client.window.getScaledHeight() / (double)this.client.window.getHeight();
				Screen.wrapScreenError(() -> element.mouseMoved(d, e), "mouseMoved event handler", element.getClass().getCanonicalName());
				if (this.activeButton != -1 && this.glfwTime > 0.0) {
					double f = (x - this.x) * (double)this.client.window.getScaledWidth() / (double)this.client.window.getWidth();
					double g = (y - this.y) * (double)this.client.window.getScaledHeight() / (double)this.client.window.getHeight();
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
		double e = d - this.field_1785;
		this.field_1785 = d;
		if (this.isCursorLocked() && this.client.isWindowFocused()) {
			double f = this.client.options.mouseSensitivity * 0.6F + 0.2F;
			double g = f * f * f * 8.0;
			double j;
			double k;
			if (this.client.options.smoothCameraEnabled) {
				double h = this.cursorXSmoother.smooth(this.cursorDeltaX * g, e * g);
				double i = this.cursorYSmoother.smooth(this.cursorDeltaY * g, e * g);
				j = h;
				k = i;
			} else {
				this.cursorXSmoother.clear();
				this.cursorYSmoother.clear();
				j = this.cursorDeltaX * g;
				k = this.cursorDeltaY * g;
			}

			this.cursorDeltaX = 0.0;
			this.cursorDeltaY = 0.0;
			int l = 1;
			if (this.client.options.invertYMouse) {
				l = -1;
			}

			this.client.getTutorialManager().onUpdateMouse(j, k);
			if (this.client.player != null) {
				this.client.player.changeLookDirection(j, k * (double)l);
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
		return this.isCursorLocked;
	}

	public void lockCursor() {
		if (this.client.isWindowFocused()) {
			if (!this.isCursorLocked) {
				if (!MinecraftClient.IS_SYSTEM_MAC) {
					KeyBinding.updatePressedStates();
				}

				this.isCursorLocked = true;
				this.x = (double)(this.client.window.getWidth() / 2);
				this.y = (double)(this.client.window.getHeight() / 2);
				InputUtil.setCursorParameters(this.client.window.getHandle(), 212995, this.x, this.y);
				this.client.openScreen(null);
				this.client.attackCooldown = 10000;
				this.hasResolutionChanged = true;
			}
		}
	}

	public void unlockCursor() {
		if (this.isCursorLocked) {
			this.isCursorLocked = false;
			this.x = (double)(this.client.window.getWidth() / 2);
			this.y = (double)(this.client.window.getHeight() / 2);
			InputUtil.setCursorParameters(this.client.window.getHandle(), 212993, this.x, this.y);
		}
	}
}
