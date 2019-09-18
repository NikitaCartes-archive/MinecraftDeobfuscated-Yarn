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
import net.minecraft.client.gui.screen.AccessibilityScreen;
import net.minecraft.client.gui.screen.ChatOptionsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.controls.ControlsOptionsScreen;
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
import net.minecraft.util.SystemUtil;
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

    public Keyboard(MinecraftClient minecraftClient) {
        this.client = minecraftClient;
    }

    private void debugWarn(String string, Object ... objects) {
        this.client.inGameHud.getChatHud().addMessage(new LiteralText("").append(new TranslatableText("debug.prefix", new Object[0]).formatted(Formatting.YELLOW, Formatting.BOLD)).append(" ").append(new TranslatableText(string, objects)));
    }

    private void debugError(String string, Object ... objects) {
        this.client.inGameHud.getChatHud().addMessage(new LiteralText("").append(new TranslatableText("debug.prefix", new Object[0]).formatted(Formatting.RED, Formatting.BOLD)).append(" ").append(new TranslatableText(string, objects)));
    }

    private boolean processF3(int i) {
        if (this.debugCrashStartTime > 0L && this.debugCrashStartTime < SystemUtil.getMeasuringTimeMs() - 100L) {
            return true;
        }
        switch (i) {
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
                this.setClipboard(String.format(Locale.ROOT, "/execute in %s run tp @s %.2f %.2f %.2f %.2f %.2f", DimensionType.getId(this.client.player.world.dimension.getType()), this.client.player.x, this.client.player.y, this.client.player.z, Float.valueOf(this.client.player.yaw), Float.valueOf(this.client.player.pitch)));
                return true;
            }
        }
        return false;
    }

    private void copyLookAt(boolean bl, boolean bl2) {
        HitResult hitResult = this.client.hitResult;
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
                Vec3d vec3d = new Vec3d(entity.x, entity.y, entity.z);
                if (bl) {
                    if (bl2) {
                        this.client.player.networkHandler.getDataQueryHandler().queryEntityNbt(entity.getEntityId(), compoundTag -> {
                            this.copyEntity(identifier, vec3d, (CompoundTag)compoundTag);
                            this.debugWarn("debug.inspect.server.entity", new Object[0]);
                        });
                        break;
                    }
                    CompoundTag compoundTag3 = entity.toTag(new CompoundTag());
                    this.copyEntity(identifier, vec3d, compoundTag3);
                    this.debugWarn("debug.inspect.client.entity", new Object[0]);
                    break;
                }
                this.copyEntity(identifier, vec3d, null);
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

    public void onKey(long l, int i, int j, int k, int m) {
        boolean bl;
        if (l != this.client.method_22683().getHandle()) {
            return;
        }
        if (this.debugCrashStartTime > 0L) {
            if (!InputUtil.isKeyPressed(MinecraftClient.getInstance().method_22683().getHandle(), 67) || !InputUtil.isKeyPressed(MinecraftClient.getInstance().method_22683().getHandle(), 292)) {
                this.debugCrashStartTime = -1L;
            }
        } else if (InputUtil.isKeyPressed(MinecraftClient.getInstance().method_22683().getHandle(), 67) && InputUtil.isKeyPressed(MinecraftClient.getInstance().method_22683().getHandle(), 292)) {
            this.switchF3State = true;
            this.debugCrashStartTime = SystemUtil.getMeasuringTimeMs();
            this.debugCrashLastLogTime = SystemUtil.getMeasuringTimeMs();
            this.debugCrashElapsedTime = 0L;
        }
        Screen parentElement = this.client.currentScreen;
        if (!(k != 1 || this.client.currentScreen instanceof ControlsOptionsScreen && ((ControlsOptionsScreen)parentElement).time > SystemUtil.getMeasuringTimeMs() - 20L)) {
            if (this.client.options.keyFullscreen.matchesKey(i, j)) {
                this.client.method_22683().toggleFullscreen();
                this.client.options.fullscreen = this.client.method_22683().isFullscreen();
                return;
            }
            if (this.client.options.keyScreenshot.matchesKey(i, j)) {
                if (Screen.hasControlDown()) {
                    // empty if block
                }
                ScreenshotUtils.saveScreenshot(this.client.runDirectory, this.client.method_22683().getFramebufferWidth(), this.client.method_22683().getFramebufferHeight(), this.client.getFramebuffer(), text -> this.client.execute(() -> this.client.inGameHud.getChatHud().addMessage((Text)text)));
                return;
            }
        }
        boolean bl2 = bl = parentElement == null || !(parentElement.getFocused() instanceof TextFieldWidget) || !((TextFieldWidget)parentElement.getFocused()).isActive();
        if (k != 0 && i == 66 && Screen.hasControlDown() && bl) {
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
                if (k == 1 || k == 2 && this.repeatEvents) {
                    bls[0] = parentElement.keyPressed(i, j, m);
                } else if (k == 0) {
                    bls[0] = parentElement.keyReleased(i, j, m);
                }
            }, "keyPressed event handler", parentElement.getClass().getCanonicalName());
            if (bls[0]) {
                return;
            }
        }
        if (this.client.currentScreen == null || this.client.currentScreen.passEvents) {
            InputUtil.KeyCode keyCode = InputUtil.getKeyCode(i, j);
            if (k == 0) {
                KeyBinding.setKeyPressed(keyCode, false);
                if (i == 292) {
                    if (this.switchF3State) {
                        this.switchF3State = false;
                    } else {
                        this.client.options.debugEnabled = !this.client.options.debugEnabled;
                        this.client.options.debugProfilerEnabled = this.client.options.debugEnabled && Screen.hasShiftDown();
                        this.client.options.debugTpsEnabled = this.client.options.debugEnabled && Screen.hasAltDown();
                    }
                }
            } else {
                if (i == 293 && this.client.gameRenderer != null) {
                    this.client.gameRenderer.toggleShadersEnabled();
                }
                boolean bl22 = false;
                if (this.client.currentScreen == null) {
                    if (i == 256) {
                        boolean bl3 = InputUtil.isKeyPressed(MinecraftClient.getInstance().method_22683().getHandle(), 292);
                        this.client.openPauseMenu(bl3);
                    }
                    bl22 = InputUtil.isKeyPressed(MinecraftClient.getInstance().method_22683().getHandle(), 292) && this.processF3(i);
                    this.switchF3State |= bl22;
                    if (i == 290) {
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
                    if (i == 48) {
                        this.client.handleProfilerKeyPress(0);
                    }
                    for (int n = 0; n < 9; ++n) {
                        if (i != 49 + n) continue;
                        this.client.handleProfilerKeyPress(n + 1);
                    }
                }
            }
        }
    }

    private void onChar(long l, int i, int j) {
        if (l != this.client.method_22683().getHandle()) {
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

    public void enableRepeatEvents(boolean bl) {
        this.repeatEvents = bl;
    }

    public void setup(long l2) {
        InputUtil.setKeyboardCallbacks(l2, (l, i, j, k, m) -> this.client.execute(() -> this.onKey(l, i, j, k, m)), (l, i, j) -> this.client.execute(() -> this.onChar(l, i, j)));
    }

    public String getClipboard() {
        return this.clipboard.getClipboard(this.client.method_22683().getHandle(), (i, l) -> {
            if (i != 65545) {
                this.client.method_22683().logGlError(i, l);
            }
        });
    }

    public void setClipboard(String string) {
        this.clipboard.setClipboard(this.client.method_22683().getHandle(), string);
    }

    public void pollDebugCrash() {
        if (this.debugCrashStartTime > 0L) {
            long l = SystemUtil.getMeasuringTimeMs();
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

