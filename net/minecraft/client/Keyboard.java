/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client;

import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.options.AccessibilityScreen;
import net.minecraft.client.gui.screen.options.ChatOptionsScreen;
import net.minecraft.client.gui.screen.options.ControlsOptionsScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.options.Option;
import net.minecraft.client.util.Clipboard;
import net.minecraft.client.util.GlfwUtil;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.ScreenshotUtils;
import net.minecraft.command.arguments.BlockArgumentParser;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.dimension.DimensionType;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class Keyboard {
    private final MinecraftClient client;
    private boolean repeatEvents;
    private final Clipboard clipboard = new Clipboard();
    private long debugCrashStartTime = -1L;
    private long debugCrashLastLogTime = -1L;
    private long debugCrashElapsedTime = -1L;
    private boolean switchF3State;

    public Keyboard(MinecraftClient client) {
        this.client = client;
    }

    private void debugWarn(String string, Object ... objects) {
        this.client.inGameHud.getChatHud().addMessage(new LiteralText("").append(new TranslatableText("debug.prefix", new Object[0]).formatted(Formatting.YELLOW, Formatting.BOLD)).append(" ").append(new TranslatableText(string, objects)));
    }

    private void debugError(String string, Object ... objects) {
        this.client.inGameHud.getChatHud().addMessage(new LiteralText("").append(new TranslatableText("debug.prefix", new Object[0]).formatted(Formatting.RED, Formatting.BOLD)).append(" ").append(new TranslatableText(string, objects)));
    }

    private boolean processF3(int key) {
        if (this.debugCrashStartTime > 0L && this.debugCrashStartTime < Util.getMeasuringTimeMs() - 100L) {
            return true;
        }
        switch (key) {
            case 65: {
                this.client.worldRenderer.reload();
                this.debugWarn("debug.reload_chunks.message", new Object[0]);
                return true;
            }
            case 66: {
                boolean bl = !this.client.getEntityRenderManager().shouldRenderHitboxes();
                this.client.getEntityRenderManager().setRenderHitboxes(bl);
                this.debugWarn(bl ? "debug.show_hitboxes.on" : "debug.show_hitboxes.off", new Object[0]);
                return true;
            }
            case 68: {
                if (this.client.inGameHud != null) {
                    this.client.inGameHud.getChatHud().clear(false);
                }
                return true;
            }
            case 70: {
                Option.RENDER_DISTANCE.set(this.client.options, MathHelper.clamp((double)(this.client.options.viewDistance + (Screen.hasShiftDown() ? -1 : 1)), Option.RENDER_DISTANCE.getMin(), Option.RENDER_DISTANCE.getMax()));
                this.debugWarn("debug.cycle_renderdistance.message", this.client.options.viewDistance);
                return true;
            }
            case 71: {
                boolean bl2 = this.client.debugRenderer.toggleShowChunkBorder();
                this.debugWarn(bl2 ? "debug.chunk_boundaries.on" : "debug.chunk_boundaries.off", new Object[0]);
                return true;
            }
            case 72: {
                this.client.options.advancedItemTooltips = !this.client.options.advancedItemTooltips;
                this.debugWarn(this.client.options.advancedItemTooltips ? "debug.advanced_tooltips.on" : "debug.advanced_tooltips.off", new Object[0]);
                this.client.options.write();
                return true;
            }
            case 73: {
                if (!this.client.player.getReducedDebugInfo()) {
                    this.copyLookAt(this.client.player.allowsPermissionLevel(2), !Screen.hasShiftDown());
                }
                return true;
            }
            case 78: {
                if (!this.client.player.allowsPermissionLevel(2)) {
                    this.debugWarn("debug.creative_spectator.error", new Object[0]);
                } else if (this.client.player.isCreative()) {
                    this.client.player.sendChatMessage("/gamemode spectator");
                } else {
                    this.client.player.sendChatMessage("/gamemode creative");
                }
                return true;
            }
            case 80: {
                this.client.options.pauseOnLostFocus = !this.client.options.pauseOnLostFocus;
                this.client.options.write();
                this.debugWarn(this.client.options.pauseOnLostFocus ? "debug.pause_focus.on" : "debug.pause_focus.off", new Object[0]);
                return true;
            }
            case 81: {
                this.debugWarn("debug.help.message", new Object[0]);
                ChatHud chatHud = this.client.inGameHud.getChatHud();
                chatHud.addMessage(new TranslatableText("debug.reload_chunks.help", new Object[0]));
                chatHud.addMessage(new TranslatableText("debug.show_hitboxes.help", new Object[0]));
                chatHud.addMessage(new TranslatableText("debug.copy_location.help", new Object[0]));
                chatHud.addMessage(new TranslatableText("debug.clear_chat.help", new Object[0]));
                chatHud.addMessage(new TranslatableText("debug.cycle_renderdistance.help", new Object[0]));
                chatHud.addMessage(new TranslatableText("debug.chunk_boundaries.help", new Object[0]));
                chatHud.addMessage(new TranslatableText("debug.advanced_tooltips.help", new Object[0]));
                chatHud.addMessage(new TranslatableText("debug.inspect.help", new Object[0]));
                chatHud.addMessage(new TranslatableText("debug.creative_spectator.help", new Object[0]));
                chatHud.addMessage(new TranslatableText("debug.pause_focus.help", new Object[0]));
                chatHud.addMessage(new TranslatableText("debug.help.help", new Object[0]));
                chatHud.addMessage(new TranslatableText("debug.reload_resourcepacks.help", new Object[0]));
                chatHud.addMessage(new TranslatableText("debug.pause.help", new Object[0]));
                return true;
            }
            case 84: {
                this.debugWarn("debug.reload_resourcepacks.message", new Object[0]);
                this.client.reloadResources();
                return true;
            }
            case 67: {
                if (this.client.player.getReducedDebugInfo()) {
                    return false;
                }
                this.debugWarn("debug.copy_location.message", new Object[0]);
                this.setClipboard(String.format(Locale.ROOT, "/execute in %s run tp @s %.2f %.2f %.2f %.2f %.2f", DimensionType.getId(this.client.player.world.dimension.getType()), this.client.player.getX(), this.client.player.getY(), this.client.player.getZ(), Float.valueOf(this.client.player.yaw), Float.valueOf(this.client.player.pitch)));
                return true;
            }
        }
        return false;
    }

    private void copyLookAt(boolean bl, boolean bl2) {
        HitResult hitResult = this.client.crosshairTarget;
        if (hitResult == null) {
            return;
        }
        switch (hitResult.getType()) {
            case BLOCK: {
                BlockPos blockPos = ((BlockHitResult)hitResult).getBlockPos();
                BlockState blockState = this.client.player.world.getBlockState(blockPos);
                if (bl) {
                    if (bl2) {
                        this.client.player.networkHandler.getDataQueryHandler().queryBlockNbt(blockPos, compoundTag -> {
                            this.copyBlock(blockState, blockPos, (CompoundTag)compoundTag);
                            this.debugWarn("debug.inspect.server.block", new Object[0]);
                        });
                        break;
                    }
                    BlockEntity blockEntity = this.client.player.world.getBlockEntity(blockPos);
                    CompoundTag compoundTag2 = blockEntity != null ? blockEntity.toTag(new CompoundTag()) : null;
                    this.copyBlock(blockState, blockPos, compoundTag2);
                    this.debugWarn("debug.inspect.client.block", new Object[0]);
                    break;
                }
                this.copyBlock(blockState, blockPos, null);
                this.debugWarn("debug.inspect.client.block", new Object[0]);
                break;
            }
            case ENTITY: {
                Entity entity = ((EntityHitResult)hitResult).getEntity();
                Identifier identifier = Registry.ENTITY_TYPE.getId(entity.getType());
                if (bl) {
                    if (bl2) {
                        this.client.player.networkHandler.getDataQueryHandler().queryEntityNbt(entity.getEntityId(), compoundTag -> {
                            this.copyEntity(identifier, entity.getPos(), (CompoundTag)compoundTag);
                            this.debugWarn("debug.inspect.server.entity", new Object[0]);
                        });
                        break;
                    }
                    CompoundTag compoundTag2 = entity.toTag(new CompoundTag());
                    this.copyEntity(identifier, entity.getPos(), compoundTag2);
                    this.debugWarn("debug.inspect.client.entity", new Object[0]);
                    break;
                }
                this.copyEntity(identifier, entity.getPos(), null);
                this.debugWarn("debug.inspect.client.entity", new Object[0]);
                break;
            }
        }
    }

    private void copyBlock(BlockState blockState, BlockPos blockPos, @Nullable CompoundTag compoundTag) {
        if (compoundTag != null) {
            compoundTag.remove("x");
            compoundTag.remove("y");
            compoundTag.remove("z");
            compoundTag.remove("id");
        }
        StringBuilder stringBuilder = new StringBuilder(BlockArgumentParser.stringifyBlockState(blockState));
        if (compoundTag != null) {
            stringBuilder.append(compoundTag);
        }
        String string = String.format(Locale.ROOT, "/setblock %d %d %d %s", blockPos.getX(), blockPos.getY(), blockPos.getZ(), stringBuilder);
        this.setClipboard(string);
    }

    private void copyEntity(Identifier identifier, Vec3d vec3d, @Nullable CompoundTag compoundTag) {
        String string2;
        if (compoundTag != null) {
            compoundTag.remove("UUIDMost");
            compoundTag.remove("UUIDLeast");
            compoundTag.remove("Pos");
            compoundTag.remove("Dimension");
            String string = compoundTag.toText().getString();
            string2 = String.format(Locale.ROOT, "/summon %s %.2f %.2f %.2f %s", identifier.toString(), vec3d.x, vec3d.y, vec3d.z, string);
        } else {
            string2 = String.format(Locale.ROOT, "/summon %s %.2f %.2f %.2f", identifier.toString(), vec3d.x, vec3d.y, vec3d.z);
        }
        this.setClipboard(string2);
    }

    public void onKey(long window, int key, int scancode, int i, int j) {
        boolean bl;
        if (window != this.client.getWindow().getHandle()) {
            return;
        }
        if (this.debugCrashStartTime > 0L) {
            if (!InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 67) || !InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 292)) {
                this.debugCrashStartTime = -1L;
            }
        } else if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 67) && InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 292)) {
            this.switchF3State = true;
            this.debugCrashStartTime = Util.getMeasuringTimeMs();
            this.debugCrashLastLogTime = Util.getMeasuringTimeMs();
            this.debugCrashElapsedTime = 0L;
        }
        Screen parentElement = this.client.currentScreen;
        if (!(i != 1 || this.client.currentScreen instanceof ControlsOptionsScreen && ((ControlsOptionsScreen)parentElement).time > Util.getMeasuringTimeMs() - 20L)) {
            if (this.client.options.keyFullscreen.matchesKey(key, scancode)) {
                this.client.getWindow().toggleFullscreen();
                this.client.options.fullscreen = this.client.getWindow().isFullscreen();
                return;
            }
            if (this.client.options.keyScreenshot.matchesKey(key, scancode)) {
                if (Screen.hasControlDown()) {
                    // empty if block
                }
                ScreenshotUtils.saveScreenshot(this.client.runDirectory, this.client.getWindow().getFramebufferWidth(), this.client.getWindow().getFramebufferHeight(), this.client.getFramebuffer(), text -> this.client.execute(() -> this.client.inGameHud.getChatHud().addMessage((Text)text)));
                return;
            }
        }
        boolean bl2 = bl = parentElement == null || !(parentElement.getFocused() instanceof TextFieldWidget) || !((TextFieldWidget)parentElement.getFocused()).isActive();
        if (i != 0 && key == 66 && Screen.hasControlDown() && bl) {
            Option.NARRATOR.cycle(this.client.options, 1);
            if (parentElement instanceof ChatOptionsScreen) {
                ((ChatOptionsScreen)parentElement).setNarratorMessage();
            }
            if (parentElement instanceof AccessibilityScreen) {
                ((AccessibilityScreen)parentElement).setNarratorMessage();
            }
        }
        if (parentElement != null) {
            boolean[] bls = new boolean[]{false};
            Screen.wrapScreenError(() -> {
                if (i == 1 || i == 2 && this.repeatEvents) {
                    bls[0] = parentElement.keyPressed(key, scancode, j);
                } else if (i == 0) {
                    bls[0] = parentElement.keyReleased(key, scancode, j);
                }
            }, "keyPressed event handler", parentElement.getClass().getCanonicalName());
            if (bls[0]) {
                return;
            }
        }
        if (this.client.currentScreen == null || this.client.currentScreen.passEvents) {
            InputUtil.KeyCode keyCode = InputUtil.getKeyCode(key, scancode);
            if (i == 0) {
                KeyBinding.setKeyPressed(keyCode, false);
                if (key == 292) {
                    if (this.switchF3State) {
                        this.switchF3State = false;
                    } else {
                        this.client.options.debugEnabled = !this.client.options.debugEnabled;
                        this.client.options.debugProfilerEnabled = this.client.options.debugEnabled && Screen.hasShiftDown();
                        this.client.options.debugTpsEnabled = this.client.options.debugEnabled && Screen.hasAltDown();
                    }
                }
            } else {
                if (key == 293 && this.client.gameRenderer != null) {
                    this.client.gameRenderer.toggleShadersEnabled();
                }
                boolean bl22 = false;
                if (this.client.currentScreen == null) {
                    if (key == 256) {
                        boolean bl3 = InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 292);
                        this.client.openPauseMenu(bl3);
                    }
                    bl22 = InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 292) && this.processF3(key);
                    this.switchF3State |= bl22;
                    if (key == 290) {
                        boolean bl3 = this.client.options.hudHidden = !this.client.options.hudHidden;
                    }
                }
                if (bl22) {
                    KeyBinding.setKeyPressed(keyCode, false);
                } else {
                    KeyBinding.setKeyPressed(keyCode, true);
                    KeyBinding.onKeyPressed(keyCode);
                }
                if (this.client.options.debugProfilerEnabled) {
                    if (key == 48) {
                        this.client.handleProfilerKeyPress(0);
                    }
                    for (int k = 0; k < 9; ++k) {
                        if (key != 49 + k) continue;
                        this.client.handleProfilerKeyPress(k + 1);
                    }
                }
            }
        }
    }

    private void onChar(long window, int i, int j) {
        if (window != this.client.getWindow().getHandle()) {
            return;
        }
        Screen element = this.client.currentScreen;
        if (element == null || this.client.getOverlay() != null) {
            return;
        }
        if (Character.charCount(i) == 1) {
            Screen.wrapScreenError(() -> element.charTyped((char)i, j), "charTyped event handler", element.getClass().getCanonicalName());
        } else {
            for (char c : Character.toChars(i)) {
                Screen.wrapScreenError(() -> element.charTyped(c, j), "charTyped event handler", element.getClass().getCanonicalName());
            }
        }
    }

    public void enableRepeatEvents(boolean repeatEvents) {
        this.repeatEvents = repeatEvents;
    }

    public void setup(long l2) {
        InputUtil.setKeyboardCallbacks(l2, (l, i, j, k, m) -> this.client.execute(() -> this.onKey(l, i, j, k, m)), (l, i, j) -> this.client.execute(() -> this.onChar(l, i, j)));
    }

    public String getClipboard() {
        return this.clipboard.getClipboard(this.client.getWindow().getHandle(), (i, l) -> {
            if (i != 65545) {
                this.client.getWindow().logGlError(i, l);
            }
        });
    }

    public void setClipboard(String string) {
        this.clipboard.setClipboard(this.client.getWindow().getHandle(), string);
    }

    public void pollDebugCrash() {
        if (this.debugCrashStartTime > 0L) {
            long l = Util.getMeasuringTimeMs();
            long m = 10000L - (l - this.debugCrashStartTime);
            long n = l - this.debugCrashLastLogTime;
            if (m < 0L) {
                if (Screen.hasControlDown()) {
                    GlfwUtil.makeJvmCrash();
                }
                throw new CrashException(new CrashReport("Manually triggered debug crash", new Throwable()));
            }
            if (n >= 1000L) {
                if (this.debugCrashElapsedTime == 0L) {
                    this.debugWarn("debug.crash.message", new Object[0]);
                } else {
                    this.debugError("debug.crash.warning", MathHelper.ceil((float)m / 1000.0f));
                }
                this.debugCrashLastLogTime = l;
                ++this.debugCrashElapsedTime;
            }
        }
    }
}

