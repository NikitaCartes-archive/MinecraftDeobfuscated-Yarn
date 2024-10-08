package net.minecraft.client;

import com.mojang.logging.LogUtils;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.navigation.GuiNavigationType;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.Scroller;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.GlfwUtil;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Smoother;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWDropCallback;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class Mouse {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final MinecraftClient client;
	private boolean leftButtonClicked;
	private boolean middleButtonClicked;
	private boolean rightButtonClicked;
	private double x;
	private double y;
	private int controlLeftClicks;
	private int activeButton = -1;
	private boolean hasResolutionChanged = true;
	private int field_1796;
	private double glfwTime;
	private final Smoother cursorXSmoother = new Smoother();
	private final Smoother cursorYSmoother = new Smoother();
	private double cursorDeltaX;
	private double cursorDeltaY;
	private final Scroller scroller;
	private double lastTickTime = Double.MIN_VALUE;
	private boolean cursorLocked;

	public Mouse(MinecraftClient client) {
		this.client = client;
		this.scroller = new Scroller();
	}

	private void onMouseButton(long window, int button, int action, int mods) {
		if (window == this.client.getWindow().getHandle()) {
			this.client.getInactivityFpsLimiter().onInput();
			if (this.client.currentScreen != null) {
				this.client.setNavigationType(GuiNavigationType.MOUSE);
			}

			boolean bl = action == 1;
			if (MinecraftClient.IS_SYSTEM_MAC && button == 0) {
				if (bl) {
					if ((mods & 2) == 2) {
						button = 1;
						this.controlLeftClicks++;
					}
				} else if (this.controlLeftClicks > 0) {
					button = 1;
					this.controlLeftClicks--;
				}
			}

			int i = button;
			if (bl) {
				if (this.client.options.getTouchscreen().getValue() && this.field_1796++ > 0) {
					return;
				}

				this.activeButton = button;
				this.glfwTime = GlfwUtil.getTime();
			} else if (this.activeButton != -1) {
				if (this.client.options.getTouchscreen().getValue() && --this.field_1796 > 0) {
					return;
				}

				this.activeButton = -1;
			}

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

						try {
							if (screen.mouseClicked(d, e, i)) {
								return;
							}
						} catch (Throwable var17) {
							CrashReport crashReport = CrashReport.create(var17, "mouseClicked event handler");
							screen.addCrashReportSection(crashReport);
							CrashReportSection crashReportSection = crashReport.addElement("Mouse");
							crashReportSection.add("Scaled X", d);
							crashReportSection.add("Scaled Y", e);
							crashReportSection.add("Button", button);
							throw new CrashException(crashReport);
						}
					} else {
						try {
							if (screen.mouseReleased(d, e, i)) {
								return;
							}
						} catch (Throwable var16) {
							CrashReport crashReport = CrashReport.create(var16, "mouseReleased event handler");
							screen.addCrashReportSection(crashReport);
							CrashReportSection crashReportSection = crashReport.addElement("Mouse");
							crashReportSection.add("Scaled X", d);
							crashReportSection.add("Scaled Y", e);
							crashReportSection.add("Button", button);
							throw new CrashException(crashReport);
						}
					}
				}
			}

			if (this.client.currentScreen == null && this.client.getOverlay() == null) {
				if (button == 0) {
					this.leftButtonClicked = bl;
				} else if (button == GLFW.GLFW_MOUSE_BUTTON_MIDDLE) {
					this.middleButtonClicked = bl;
				} else if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
					this.rightButtonClicked = bl;
				}

				KeyBinding.setKeyPressed(InputUtil.Type.MOUSE.createFromCode(button), bl);
				if (bl) {
					if (this.client.player.isSpectator() && button == GLFW.GLFW_MOUSE_BUTTON_MIDDLE) {
						this.client.inGameHud.getSpectatorHud().useSelectedCommand();
					} else {
						KeyBinding.onKeyPressed(InputUtil.Type.MOUSE.createFromCode(button));
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
			this.client.getInactivityFpsLimiter().onInput();
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
					Vector2i vector2i = this.scroller.update(e, f);
					if (vector2i.x == 0 && vector2i.y == 0) {
						return;
					}

					int i = vector2i.y == 0 ? -vector2i.x : vector2i.y;
					if (this.client.player.isSpectator()) {
						if (this.client.inGameHud.getSpectatorHud().isOpen()) {
							this.client.inGameHud.getSpectatorHud().cycleSlot(-i);
						} else {
							float j = MathHelper.clamp(this.client.player.getAbilities().getFlySpeed() + (float)vector2i.y * 0.005F, 0.0F, 0.2F);
							this.client.player.getAbilities().setFlySpeed(j);
						}
					} else {
						PlayerInventory playerInventory = this.client.player.getInventory();
						playerInventory.setSelectedSlot(Scroller.scrollCycling((double)i, playerInventory.selectedSlot, PlayerInventory.getHotbarSize()));
					}
				}
			}
		}
	}

	private void onFilesDropped(long window, List<Path> paths, int invalidFilesCount) {
		this.client.getInactivityFpsLimiter().onInput();
		if (this.client.currentScreen != null) {
			this.client.currentScreen.filesDragged(paths);
		}

		if (invalidFilesCount > 0) {
			SystemToast.addFileDropFailure(this.client, invalidFilesCount);
		}
	}

	public void setup(long window) {
		InputUtil.setMouseCallbacks(
			window,
			(windowx, x, y) -> this.client.execute(() -> this.onCursorPos(windowx, x, y)),
			(windowx, button, action, modifiers) -> this.client.execute(() -> this.onMouseButton(windowx, button, action, modifiers)),
			(windowx, offsetX, offsetY) -> this.client.execute(() -> this.onMouseScroll(windowx, offsetX, offsetY)),
			(windowx, count, names) -> {
				List<Path> list = new ArrayList(count);
				int i = 0;

				for (int j = 0; j < count; j++) {
					String string = GLFWDropCallback.getName(names, j);

					try {
						list.add(Paths.get(string));
					} catch (InvalidPathException var11) {
						i++;
						LOGGER.error("Failed to parse path '{}'", string, var11);
					}
				}

				if (!list.isEmpty()) {
					int j = i;
					this.client.execute(() -> this.onFilesDropped(windowx, list, j));
				}
			}
		);
	}

	private void onCursorPos(long window, double x, double y) {
		if (window == MinecraftClient.getInstance().getWindow().getHandle()) {
			if (this.hasResolutionChanged) {
				this.x = x;
				this.y = y;
				this.hasResolutionChanged = false;
			} else {
				if (this.client.isWindowFocused()) {
					this.cursorDeltaX = this.cursorDeltaX + (x - this.x);
					this.cursorDeltaY = this.cursorDeltaY + (y - this.y);
				}

				this.x = x;
				this.y = y;
			}
		}
	}

	public void tick() {
		double d = GlfwUtil.getTime();
		double e = d - this.lastTickTime;
		this.lastTickTime = d;
		if (this.client.isWindowFocused()) {
			Screen screen = this.client.currentScreen;
			boolean bl = this.cursorDeltaX != 0.0 || this.cursorDeltaY != 0.0;
			if (bl) {
				this.client.getInactivityFpsLimiter().onInput();
			}

			if (screen != null && this.client.getOverlay() == null && bl) {
				double f = this.x * (double)this.client.getWindow().getScaledWidth() / (double)this.client.getWindow().getWidth();
				double g = this.y * (double)this.client.getWindow().getScaledHeight() / (double)this.client.getWindow().getHeight();

				try {
					screen.mouseMoved(f, g);
				} catch (Throwable var19) {
					CrashReport crashReport = CrashReport.create(var19, "mouseMoved event handler");
					screen.addCrashReportSection(crashReport);
					CrashReportSection crashReportSection = crashReport.addElement("Mouse");
					crashReportSection.add("Scaled X", f);
					crashReportSection.add("Scaled Y", g);
					throw new CrashException(crashReport);
				}

				if (this.activeButton != -1 && this.glfwTime > 0.0) {
					double h = this.cursorDeltaX * (double)this.client.getWindow().getScaledWidth() / (double)this.client.getWindow().getWidth();
					double i = this.cursorDeltaY * (double)this.client.getWindow().getScaledHeight() / (double)this.client.getWindow().getHeight();

					try {
						screen.mouseDragged(f, g, this.activeButton, h, i);
					} catch (Throwable var18) {
						CrashReport crashReport2 = CrashReport.create(var18, "mouseDragged event handler");
						screen.addCrashReportSection(crashReport2);
						CrashReportSection crashReportSection2 = crashReport2.addElement("Mouse");
						crashReportSection2.add("Scaled X", f);
						crashReportSection2.add("Scaled Y", g);
						throw new CrashException(crashReport2);
					}
				}

				screen.applyMouseMoveNarratorDelay();
			}

			if (this.isCursorLocked() && this.client.player != null) {
				this.updateMouse(e);
			}
		}

		this.cursorDeltaX = 0.0;
		this.cursorDeltaY = 0.0;
	}

	private void updateMouse(double timeDelta) {
		double d = this.client.options.getMouseSensitivity().getValue() * 0.6F + 0.2F;
		double e = d * d * d;
		double f = e * 8.0;
		double i;
		double j;
		if (this.client.options.smoothCameraEnabled) {
			double g = this.cursorXSmoother.smooth(this.cursorDeltaX * f, timeDelta * f);
			double h = this.cursorYSmoother.smooth(this.cursorDeltaY * f, timeDelta * f);
			i = g;
			j = h;
		} else if (this.client.options.getPerspective().isFirstPerson() && this.client.player.isUsingSpyglass()) {
			this.cursorXSmoother.clear();
			this.cursorYSmoother.clear();
			i = this.cursorDeltaX * e;
			j = this.cursorDeltaY * e;
		} else {
			this.cursorXSmoother.clear();
			this.cursorYSmoother.clear();
			i = this.cursorDeltaX * f;
			j = this.cursorDeltaY * f;
		}

		int k = 1;
		if (this.client.options.getInvertYMouse().getValue()) {
			k = -1;
		}

		this.client.getTutorialManager().onUpdateMouse(i, j);
		if (this.client.player != null) {
			this.client.player.changeLookDirection(i, j * (double)k);
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
