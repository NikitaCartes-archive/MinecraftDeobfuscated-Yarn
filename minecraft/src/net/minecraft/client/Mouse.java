package net.minecraft.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.InputListener;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.GlfwUtil;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.SmoothUtil;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class Mouse {
	private final MinecraftClient client;
	private boolean field_1791;
	private boolean field_1790;
	private boolean field_1788;
	private double x;
	private double y;
	private int field_1781;
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

	public Mouse(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
	}

	private void onMouseButton(long l, int i, int j, int k) {
		if (l == this.client.window.getHandle()) {
			boolean bl = j == 1;
			if (MinecraftClient.IS_SYSTEM_MAC && i == 0) {
				if (bl) {
					if ((k & 2) == 2) {
						i = 1;
						this.field_1781++;
					}
				} else if (this.field_1781 > 0) {
					i = 1;
					this.field_1781--;
				}
			}

			int m = i;
			if (bl) {
				if (this.client.options.touchscreen && this.field_1796++ > 0) {
					return;
				}

				this.activeButton = m;
				this.glfwTime = GlfwUtil.getTime();
			} else if (this.activeButton != -1) {
				if (this.client.options.touchscreen && --this.field_1796 > 0) {
					return;
				}

				this.activeButton = -1;
			}

			boolean[] bls = new boolean[]{false};
			if (this.client.field_18175 == null) {
				if (this.client.currentScreen == null) {
					if (!this.isCursorLocked && bl) {
						this.lockCursor();
					}
				} else {
					double d = this.x * (double)this.client.window.getScaledWidth() / (double)this.client.window.getWidth();
					double e = this.y * (double)this.client.window.getScaledHeight() / (double)this.client.window.getHeight();
					if (bl) {
						Screen.method_2217(
							() -> bls[0] = this.client.currentScreen.mouseClicked(d, e, m), "mouseClicked event handler", this.client.currentScreen.getClass().getCanonicalName()
						);
					} else {
						Screen.method_2217(
							() -> bls[0] = this.client.currentScreen.mouseReleased(d, e, m), "mouseReleased event handler", this.client.currentScreen.getClass().getCanonicalName()
						);
					}
				}
			}

			if (!bls[0] && (this.client.currentScreen == null || this.client.currentScreen.field_2558) && this.client.field_18175 == null) {
				if (m == 0) {
					this.field_1791 = bl;
				} else if (m == 2) {
					this.field_1790 = bl;
				} else if (m == 1) {
					this.field_1788 = bl;
				}

				KeyBinding.setKeyPressed(InputUtil.Type.field_1672.createFromCode(m), bl);
				if (bl) {
					if (this.client.player.isSpectator() && m == 2) {
						this.client.inGameHud.getSpectatorWidget().method_1983();
					} else {
						KeyBinding.onKeyPressed(InputUtil.Type.field_1672.createFromCode(m));
					}
				}
			}
		}
	}

	private void onMouseScroll(long l, double d, double e) {
		if (l == MinecraftClient.getInstance().window.getHandle()) {
			double f = e * this.client.options.mouseWheelSensitivity;
			if (this.client.field_18175 == null) {
				if (this.client.currentScreen != null) {
					this.client.currentScreen.mouseScrolled(f);
				} else if (this.client.player != null) {
					if (this.eventDeltaWheel != 0.0 && Math.signum(f) != Math.signum(this.eventDeltaWheel)) {
						this.eventDeltaWheel = 0.0;
					}

					this.eventDeltaWheel += f;
					float g = (float)((int)this.eventDeltaWheel);
					if (g == 0.0F) {
						return;
					}

					this.eventDeltaWheel -= (double)g;
					if (this.client.player.isSpectator()) {
						if (this.client.inGameHud.getSpectatorWidget().method_1980()) {
							this.client.inGameHud.getSpectatorWidget().method_1976((double)(-g));
						} else {
							float h = MathHelper.clamp(this.client.player.abilities.getFlySpeed() + g * 0.005F, 0.0F, 0.2F);
							this.client.player.abilities.setFlySpeed(h);
						}
					} else {
						this.client.player.inventory.method_7373((double)g);
					}
				}
			}
		}
	}

	public void setup(long l) {
		InputUtil.setMouseCallbacks(l, this::onCursorPos, this::onMouseButton, this::onMouseScroll);
	}

	private void onCursorPos(long l, double d, double e) {
		if (l == MinecraftClient.getInstance().window.getHandle()) {
			if (this.hasResolutionChanged) {
				this.x = d;
				this.y = e;
				this.hasResolutionChanged = false;
			}

			InputListener inputListener = this.client.currentScreen;
			if (inputListener != null && this.client.field_18175 == null) {
				double f = d * (double)this.client.window.getScaledWidth() / (double)this.client.window.getWidth();
				double g = e * (double)this.client.window.getScaledHeight() / (double)this.client.window.getHeight();
				Screen.method_2217(() -> inputListener.mouseMoved(f, g), "mouseMoved event handler", inputListener.getClass().getCanonicalName());
				if (this.activeButton != -1 && this.glfwTime > 0.0) {
					double h = (d - this.x) * (double)this.client.window.getScaledWidth() / (double)this.client.window.getWidth();
					double i = (e - this.y) * (double)this.client.window.getScaledHeight() / (double)this.client.window.getHeight();
					Screen.method_2217(
						() -> inputListener.mouseDragged(f, g, this.activeButton, h, i), "mouseDragged event handler", inputListener.getClass().getCanonicalName()
					);
				}
			}

			this.client.getProfiler().push("mouse");
			if (this.isCursorLocked() && this.client.isWindowFocused()) {
				this.cursorDeltaX = this.cursorDeltaX + (d - this.x);
				this.cursorDeltaY = this.cursorDeltaY + (e - this.y);
			}

			this.updateMouse();
			this.x = d;
			this.y = e;
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

			this.client.getTutorialManager().method_4908(j, k);
			if (this.client.player != null) {
				this.client.player.method_5872(j, k * (double)l);
			}
		} else {
			this.cursorDeltaX = 0.0;
			this.cursorDeltaY = 0.0;
		}
	}

	public boolean method_1608() {
		return this.field_1791;
	}

	public boolean method_1609() {
		return this.field_1788;
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
