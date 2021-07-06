/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.GlfwUtil;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.SmoothUtil;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWDropCallback;

@Environment(value=EnvType.CLIENT)
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
        boolean bl;
        if (window != this.client.getWindow().getHandle()) {
            return;
        }
        boolean bl2 = bl = action == 1;
        if (MinecraftClient.IS_SYSTEM_MAC && button == 0) {
            if (bl) {
                if ((mods & 2) == 2) {
                    button = 1;
                    ++this.controlLeftTicks;
                }
            } else if (this.controlLeftTicks > 0) {
                button = 1;
                --this.controlLeftTicks;
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
                    Screen.wrapScreenError(() -> {
                        bls[0] = screen.mouseClicked(d, e, i);
                    }, "mouseClicked event handler", screen.getClass().getCanonicalName());
                } else {
                    Screen.wrapScreenError(() -> {
                        bls[0] = screen.mouseReleased(d, e, i);
                    }, "mouseReleased event handler", screen.getClass().getCanonicalName());
                }
            }
        }
        if (!bls[0] && (this.client.currentScreen == null || this.client.currentScreen.passEvents) && this.client.getOverlay() == null) {
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
            if (this.client.getOverlay() == null) {
                if (this.client.currentScreen != null) {
                    double e = this.x * (double)this.client.getWindow().getScaledWidth() / (double)this.client.getWindow().getWidth();
                    double f = this.y * (double)this.client.getWindow().getScaledHeight() / (double)this.client.getWindow().getHeight();
                    this.client.currentScreen.mouseScrolled(e, f, d);
                    this.client.currentScreen.applyMousePressScrollNarratorDelay();
                } else if (this.client.player != null) {
                    if (this.eventDeltaWheel != 0.0 && Math.signum(d) != Math.signum(this.eventDeltaWheel)) {
                        this.eventDeltaWheel = 0.0;
                    }
                    this.eventDeltaWheel += d;
                    float g = (int)this.eventDeltaWheel;
                    if (g == 0.0f) {
                        return;
                    }
                    this.eventDeltaWheel -= (double)g;
                    if (this.client.player.isSpectator()) {
                        if (this.client.inGameHud.getSpectatorHud().isOpen()) {
                            this.client.inGameHud.getSpectatorHud().cycleSlot(-g);
                        } else {
                            float h = MathHelper.clamp(this.client.player.getAbilities().getFlySpeed() + g * 0.005f, 0.0f, 0.2f);
                            this.client.player.getAbilities().setFlySpeed(h);
                        }
                    } else {
                        this.client.player.getInventory().scrollInHotbar(g);
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

    public void setup(long window2) {
        InputUtil.setMouseCallbacks(window2, (window, x, y) -> this.client.execute(() -> this.onCursorPos(window, x, y)), (window, button, action, modifiers) -> this.client.execute(() -> this.onMouseButton(window, button, action, modifiers)), (window, offsetX, offsetY) -> this.client.execute(() -> this.onMouseScroll(window, offsetX, offsetY)), (window, count, names) -> {
            Path[] paths = new Path[count];
            for (int i = 0; i < count; ++i) {
                paths[i] = Paths.get(GLFWDropCallback.getName(names, i), new String[0]);
            }
            this.client.execute(() -> this.onFilesDropped(window, Arrays.asList(paths)));
        });
    }

    private void onCursorPos(long window, double x, double y) {
        Screen screen;
        if (window != MinecraftClient.getInstance().getWindow().getHandle()) {
            return;
        }
        if (this.hasResolutionChanged) {
            this.x = x;
            this.y = y;
            this.hasResolutionChanged = false;
        }
        if ((screen = this.client.currentScreen) != null && this.client.getOverlay() == null) {
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
            this.cursorDeltaX += x - this.x;
            this.cursorDeltaY += y - this.y;
        }
        this.updateMouse();
        this.x = x;
        this.y = y;
        this.client.getProfiler().pop();
    }

    public void updateMouse() {
        double l;
        double k;
        double d = GlfwUtil.getTime();
        double e = d - this.lastMouseUpdateTime;
        this.lastMouseUpdateTime = d;
        if (!this.isCursorLocked() || !this.client.isWindowFocused()) {
            this.cursorDeltaX = 0.0;
            this.cursorDeltaY = 0.0;
            return;
        }
        double f = this.client.options.mouseSensitivity * (double)0.6f + (double)0.2f;
        double g = f * f * f;
        double h = g * 8.0;
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
        if (!this.client.isWindowFocused()) {
            return;
        }
        if (this.cursorLocked) {
            return;
        }
        if (!MinecraftClient.IS_SYSTEM_MAC) {
            KeyBinding.updatePressedStates();
        }
        this.cursorLocked = true;
        this.x = this.client.getWindow().getWidth() / 2;
        this.y = this.client.getWindow().getHeight() / 2;
        InputUtil.setCursorParameters(this.client.getWindow().getHandle(), 212995, this.x, this.y);
        this.client.setScreen(null);
        this.client.attackCooldown = 10000;
        this.hasResolutionChanged = true;
    }

    public void unlockCursor() {
        if (!this.cursorLocked) {
            return;
        }
        this.cursorLocked = false;
        this.x = this.client.getWindow().getWidth() / 2;
        this.y = this.client.getWindow().getHeight() / 2;
        InputUtil.setCursorParameters(this.client.getWindow().getHandle(), 212993, this.x, this.y);
    }

    public void setResolutionChanged() {
        this.hasResolutionChanged = true;
    }
}

