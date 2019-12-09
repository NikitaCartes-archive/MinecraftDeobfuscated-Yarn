/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.GlfwUtil;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.SmoothUtil;
import net.minecraft.util.math.MathHelper;

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
    private boolean isCursorLocked;

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
        if (this.client.overlay == null) {
            if (this.client.currentScreen == null) {
                if (!this.isCursorLocked && bl) {
                    this.lockCursor();
                }
            } else {
                double d = this.x * (double)this.client.getWindow().getScaledWidth() / (double)this.client.getWindow().getWidth();
                double e = this.y * (double)this.client.getWindow().getScaledHeight() / (double)this.client.getWindow().getHeight();
                if (bl) {
                    Screen.wrapScreenError(() -> {
                        bls[0] = this.client.currentScreen.mouseClicked(d, e, i);
                    }, "mouseClicked event handler", this.client.currentScreen.getClass().getCanonicalName());
                } else {
                    Screen.wrapScreenError(() -> {
                        bls[0] = this.client.currentScreen.mouseReleased(d, e, i);
                    }, "mouseReleased event handler", this.client.currentScreen.getClass().getCanonicalName());
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

    private void onMouseScroll(long window, double d, double e) {
        if (window == MinecraftClient.getInstance().getWindow().getHandle()) {
            double f = (this.client.options.discreteMouseScroll ? Math.signum(e) : e) * this.client.options.mouseWheelSensitivity;
            if (this.client.overlay == null) {
                if (this.client.currentScreen != null) {
                    double g = this.x * (double)this.client.getWindow().getScaledWidth() / (double)this.client.getWindow().getWidth();
                    double h = this.y * (double)this.client.getWindow().getScaledHeight() / (double)this.client.getWindow().getHeight();
                    this.client.currentScreen.mouseScrolled(g, h, f);
                } else if (this.client.player != null) {
                    if (this.eventDeltaWheel != 0.0 && Math.signum(f) != Math.signum(this.eventDeltaWheel)) {
                        this.eventDeltaWheel = 0.0;
                    }
                    this.eventDeltaWheel += f;
                    float i = (int)this.eventDeltaWheel;
                    if (i == 0.0f) {
                        return;
                    }
                    this.eventDeltaWheel -= (double)i;
                    if (this.client.player.isSpectator()) {
                        if (this.client.inGameHud.getSpectatorHud().isOpen()) {
                            this.client.inGameHud.getSpectatorHud().cycleSlot(-i);
                        } else {
                            float j = MathHelper.clamp(this.client.player.abilities.getFlySpeed() + i * 0.005f, 0.0f, 0.2f);
                            this.client.player.abilities.setFlySpeed(j);
                        }
                    } else {
                        this.client.player.inventory.scrollInHotbar(i);
                    }
                }
            }
        }
    }

    public void setup(long l2) {
        InputUtil.setMouseCallbacks(l2, (l, d, e) -> this.client.execute(() -> this.onCursorPos(l, d, e)), (l, i, j, k) -> this.client.execute(() -> this.onMouseButton(l, i, j, k)), (l, d, e) -> this.client.execute(() -> this.onMouseScroll(l, d, e)));
    }

    private void onCursorPos(long window, double x, double y) {
        Screen element;
        if (window != MinecraftClient.getInstance().getWindow().getHandle()) {
            return;
        }
        if (this.hasResolutionChanged) {
            this.x = x;
            this.y = y;
            this.hasResolutionChanged = false;
        }
        if ((element = this.client.currentScreen) != null && this.client.overlay == null) {
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
            this.cursorDeltaX += x - this.x;
            this.cursorDeltaY += y - this.y;
        }
        this.updateMouse();
        this.x = x;
        this.y = y;
        this.client.getProfiler().pop();
    }

    public void updateMouse() {
        double k;
        double j;
        double d = GlfwUtil.getTime();
        double e = d - this.lastMouseUpdateTime;
        this.lastMouseUpdateTime = d;
        if (!this.isCursorLocked() || !this.client.isWindowFocused()) {
            this.cursorDeltaX = 0.0;
            this.cursorDeltaY = 0.0;
            return;
        }
        double f = this.client.options.mouseSensitivity * (double)0.6f + (double)0.2f;
        double g = f * f * f * 8.0;
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
        if (!this.client.isWindowFocused()) {
            return;
        }
        if (this.isCursorLocked) {
            return;
        }
        if (!MinecraftClient.IS_SYSTEM_MAC) {
            KeyBinding.updatePressedStates();
        }
        this.isCursorLocked = true;
        this.x = this.client.getWindow().getWidth() / 2;
        this.y = this.client.getWindow().getHeight() / 2;
        InputUtil.setCursorParameters(this.client.getWindow().getHandle(), 212995, this.x, this.y);
        this.client.openScreen(null);
        this.client.attackCooldown = 10000;
        this.hasResolutionChanged = true;
    }

    public void unlockCursor() {
        if (!this.isCursorLocked) {
            return;
        }
        this.isCursorLocked = false;
        this.x = this.client.getWindow().getWidth() / 2;
        this.y = this.client.getWindow().getHeight() / 2;
        InputUtil.setCursorParameters(this.client.getWindow().getHandle(), 212993, this.x, this.y);
    }
}

