package net.minecraft.client;

import java.util.Locale;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.gui.GuiEventListener;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.menu.settings.ChatSettingsScreen;
import net.minecraft.client.gui.menu.settings.ControlsSettingsScreen;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.Clipboard;
import net.minecraft.client.util.GlfwUtil;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.ScreenshotUtils;
import net.minecraft.command.arguments.BlockArgumentParser;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;
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

@Environment(EnvType.CLIENT)
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

	private void debugWarn(String string, Object... objects) {
		this.client
			.inGameHud
			.getChatHud()
			.addMessage(
				new StringTextComponent("")
					.append(new TranslatableTextComponent("debug.prefix").applyFormat(new TextFormat[]{TextFormat.field_1054, TextFormat.field_1067}))
					.append(" ")
					.append(new TranslatableTextComponent(string, objects))
			);
	}

	private void debugError(String string, Object... objects) {
		this.client
			.inGameHud
			.getChatHud()
			.addMessage(
				new StringTextComponent("")
					.append(new TranslatableTextComponent("debug.prefix").applyFormat(new TextFormat[]{TextFormat.field_1061, TextFormat.field_1067}))
					.append(" ")
					.append(new TranslatableTextComponent(string, objects))
			);
	}

	private boolean processF3(int i) {
		if (this.debugCrashStartTime > 0L && this.debugCrashStartTime < SystemUtil.getMeasuringTimeMs() - 100L) {
			return true;
		} else {
			switch (i) {
				case 65:
					this.client.worldRenderer.reload();
					this.debugWarn("debug.reload_chunks.message");
					return true;
				case 66:
					boolean bl = !this.client.getEntityRenderManager().method_3958();
					this.client.getEntityRenderManager().method_3955(bl);
					this.debugWarn(bl ? "debug.show_hitboxes.on" : "debug.show_hitboxes.off");
					return true;
				case 67:
					if (this.client.player.getReducedDebugInfo()) {
						return false;
					}

					this.debugWarn("debug.copy_location.message");
					this.setClipboard(
						String.format(
							Locale.ROOT,
							"/execute in %s run tp @s %.2f %.2f %.2f %.2f %.2f",
							DimensionType.getId(this.client.player.world.dimension.getType()),
							this.client.player.x,
							this.client.player.y,
							this.client.player.z,
							this.client.player.yaw,
							this.client.player.pitch
						)
					);
					return true;
				case 68:
					if (this.client.inGameHud != null) {
						this.client.inGameHud.getChatHud().clear(false);
					}

					return true;
				case 69:
				case 74:
				case 75:
				case 76:
				case 77:
				case 79:
				case 82:
				case 83:
				default:
					return false;
				case 70:
					this.client.options.setInteger(GameOptions.Option.RENDER_DISTANCE, Screen.isShiftPressed() ? -1 : 1);
					this.debugWarn("debug.cycle_renderdistance.message", this.client.options.viewDistance);
					return true;
				case 71:
					boolean bl2 = this.client.debugRenderer.toggleShowChunkBorder();
					this.debugWarn(bl2 ? "debug.chunk_boundaries.on" : "debug.chunk_boundaries.off");
					return true;
				case 72:
					this.client.options.advancedItemTooltips = !this.client.options.advancedItemTooltips;
					this.debugWarn(this.client.options.advancedItemTooltips ? "debug.advanced_tooltips.on" : "debug.advanced_tooltips.off");
					this.client.options.write();
					return true;
				case 73:
					if (!this.client.player.getReducedDebugInfo()) {
						this.copyLookAt(this.client.player.allowsPermissionLevel(2), !Screen.isShiftPressed());
					}

					return true;
				case 78:
					if (!this.client.player.allowsPermissionLevel(2)) {
						this.debugWarn("debug.creative_spectator.error");
					} else if (this.client.player.isCreative()) {
						this.client.player.sendChatMessage("/gamemode spectator");
					} else if (this.client.player.isSpectator()) {
						this.client.player.sendChatMessage("/gamemode creative");
					}

					return true;
				case 80:
					this.client.options.pauseOnLostFocus = !this.client.options.pauseOnLostFocus;
					this.client.options.write();
					this.debugWarn(this.client.options.pauseOnLostFocus ? "debug.pause_focus.on" : "debug.pause_focus.off");
					return true;
				case 81:
					this.debugWarn("debug.help.message");
					ChatHud chatHud = this.client.inGameHud.getChatHud();
					chatHud.addMessage(new TranslatableTextComponent("debug.reload_chunks.help"));
					chatHud.addMessage(new TranslatableTextComponent("debug.show_hitboxes.help"));
					chatHud.addMessage(new TranslatableTextComponent("debug.copy_location.help"));
					chatHud.addMessage(new TranslatableTextComponent("debug.clear_chat.help"));
					chatHud.addMessage(new TranslatableTextComponent("debug.cycle_renderdistance.help"));
					chatHud.addMessage(new TranslatableTextComponent("debug.chunk_boundaries.help"));
					chatHud.addMessage(new TranslatableTextComponent("debug.advanced_tooltips.help"));
					chatHud.addMessage(new TranslatableTextComponent("debug.inspect.help"));
					chatHud.addMessage(new TranslatableTextComponent("debug.creative_spectator.help"));
					chatHud.addMessage(new TranslatableTextComponent("debug.pause_focus.help"));
					chatHud.addMessage(new TranslatableTextComponent("debug.help.help"));
					chatHud.addMessage(new TranslatableTextComponent("debug.reload_resourcepacks.help"));
					return true;
				case 84:
					this.debugWarn("debug.reload_resourcepacks.message");
					this.client.reloadResources();
					return true;
			}
		}
	}

	private void copyLookAt(boolean bl, boolean bl2) {
		HitResult hitResult = this.client.hitResult;
		if (hitResult != null) {
			switch (hitResult.getType()) {
				case BLOCK:
					BlockPos blockPos = ((BlockHitResult)hitResult).getBlockPos();
					BlockState blockState = this.client.player.world.getBlockState(blockPos);
					if (bl) {
						if (bl2) {
							this.client.player.networkHandler.method_2876().method_1403(blockPos, compoundTagx -> {
								this.copyBlock(blockState, blockPos, compoundTagx);
								this.debugWarn("debug.inspect.server.block");
							});
						} else {
							BlockEntity blockEntity = this.client.player.world.getBlockEntity(blockPos);
							CompoundTag compoundTag = blockEntity != null ? blockEntity.toTag(new CompoundTag()) : null;
							this.copyBlock(blockState, blockPos, compoundTag);
							this.debugWarn("debug.inspect.client.block");
						}
					} else {
						this.copyBlock(blockState, blockPos, null);
						this.debugWarn("debug.inspect.client.block");
					}
					break;
				case ENTITY:
					Entity entity = ((EntityHitResult)hitResult).getEntity();
					Identifier identifier = Registry.ENTITY_TYPE.getId(entity.getType());
					Vec3d vec3d = new Vec3d(entity.x, entity.y, entity.z);
					if (bl) {
						if (bl2) {
							this.client.player.networkHandler.method_2876().method_1405(entity.getEntityId(), compoundTagx -> {
								this.copyEntity(identifier, vec3d, compoundTagx);
								this.debugWarn("debug.inspect.server.entity");
							});
						} else {
							CompoundTag compoundTag = entity.toTag(new CompoundTag());
							this.copyEntity(identifier, vec3d, compoundTag);
							this.debugWarn("debug.inspect.client.entity");
						}
					} else {
						this.copyEntity(identifier, vec3d, null);
						this.debugWarn("debug.inspect.client.entity");
					}
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
			String string = compoundTag.toTextComponent().getString();
			string2 = String.format(Locale.ROOT, "/summon %s %.2f %.2f %.2f %s", identifier.toString(), vec3d.x, vec3d.y, vec3d.z, string);
		} else {
			string2 = String.format(Locale.ROOT, "/summon %s %.2f %.2f %.2f", identifier.toString(), vec3d.x, vec3d.y, vec3d.z);
		}

		this.setClipboard(string2);
	}

	public void onKey(long l, int i, int j, int k, int m) {
		if (l == this.client.window.getHandle()) {
			if (this.debugCrashStartTime > 0L) {
				if (!InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), 67)
					|| !InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), 292)) {
					this.debugCrashStartTime = -1L;
				}
			} else if (InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), 67)
				&& InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), 292)) {
				this.switchF3State = true;
				this.debugCrashStartTime = SystemUtil.getMeasuringTimeMs();
				this.debugCrashLastLogTime = SystemUtil.getMeasuringTimeMs();
				this.debugCrashElapsedTime = 0L;
			}

			GuiEventListener guiEventListener = this.client.currentScreen;
			if (k == 1
				&& (
					!(this.client.currentScreen instanceof ControlsSettingsScreen)
						|| ((ControlsSettingsScreen)guiEventListener).field_2723 <= SystemUtil.getMeasuringTimeMs() - 20L
				)) {
				if (this.client.options.keyFullscreen.matchesKey(i, j)) {
					this.client.window.toggleFullscreen();
					this.client.options.fullscreen = this.client.window.isFullscreen();
					return;
				}

				if (this.client.options.keyScreenshot.matchesKey(i, j)) {
					if (Screen.isControlPressed()) {
					}

					ScreenshotUtils.method_1659(
						this.client.runDirectory,
						this.client.window.getFramebufferWidth(),
						this.client.window.getFramebufferHeight(),
						this.client.getFramebuffer(),
						textComponent -> this.client.execute(() -> this.client.inGameHud.getChatHud().addMessage(textComponent))
					);
					return;
				}
			}

			if (k != 0 && i == 66 && Screen.isControlPressed()) {
				this.client.options.setInteger(GameOptions.Option.NARRATOR, 1);
				if (guiEventListener instanceof ChatSettingsScreen) {
					((ChatSettingsScreen)guiEventListener).method_2096();
				}
			}

			if (guiEventListener != null) {
				boolean[] bls = new boolean[]{false};
				Screen.method_2217(() -> {
					if (k != 1 && (k != 2 || !this.repeatEvents)) {
						if (k == 0) {
							bls[0] = guiEventListener.keyReleased(i, j, m);
						}
					} else {
						bls[0] = guiEventListener.keyPressed(i, j, m);
					}
				}, "keyPressed event handler", guiEventListener.getClass().getCanonicalName());
				if (bls[0]) {
					return;
				}
			}

			if (this.client.currentScreen == null || this.client.currentScreen.field_2558) {
				InputUtil.KeyCode keyCode = InputUtil.getKeyCode(i, j);
				if (k == 0) {
					KeyBinding.setKeyPressed(keyCode, false);
					if (i == 292) {
						if (this.switchF3State) {
							this.switchF3State = false;
						} else {
							this.client.options.debugEnabled = !this.client.options.debugEnabled;
							this.client.options.debugProfilerEnabled = this.client.options.debugEnabled && Screen.isShiftPressed();
							this.client.options.debugTpsEnabled = this.client.options.debugEnabled && Screen.isAltPressed();
						}
					}
				} else {
					if (i == 293 && this.client.gameRenderer != null) {
						this.client.gameRenderer.toggleShadersEnabled();
					}

					boolean bl = false;
					if (this.client.currentScreen == null) {
						if (i == 256) {
							this.client.openPauseMenu();
						}

						bl = InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), 292) && this.processF3(i);
						this.switchF3State |= bl;
						if (i == 290) {
							this.client.options.hudHidden = !this.client.options.hudHidden;
						}
					}

					if (bl) {
						KeyBinding.setKeyPressed(keyCode, false);
					} else {
						KeyBinding.setKeyPressed(keyCode, true);
						KeyBinding.onKeyPressed(keyCode);
					}

					if (this.client.options.debugProfilerEnabled) {
						if (i == 48) {
							this.client.handleProfilerKeyPress(0);
						}

						for (int n = 0; n < 9; n++) {
							if (i == 49 + n) {
								this.client.handleProfilerKeyPress(n + 1);
							}
						}
					}
				}
			}
		}
	}

	private void onChar(long l, int i, int j) {
		if (l == this.client.window.getHandle()) {
			GuiEventListener guiEventListener = this.client.currentScreen;
			if (guiEventListener != null) {
				if (Character.charCount(i) == 1) {
					Screen.method_2217(() -> guiEventListener.charTyped((char)i, j), "charTyped event handler", guiEventListener.getClass().getCanonicalName());
				} else {
					for (char c : Character.toChars(i)) {
						Screen.method_2217(() -> guiEventListener.charTyped(c, j), "charTyped event handler", guiEventListener.getClass().getCanonicalName());
					}
				}
			}
		}
	}

	public void enableRepeatEvents(boolean bl) {
		this.repeatEvents = bl;
	}

	public void setup(long l) {
		InputUtil.setKeyboardCallbacks(l, this::onKey, this::onChar);
	}

	public String getClipboard() {
		return this.clipboard.getClipboard(this.client.window.getHandle(), (i, l) -> {
			if (i != 65545) {
				this.client.window.logGlError(i, l);
			}
		});
	}

	public void setClipboard(String string) {
		this.clipboard.setClipboard(this.client.window.getHandle(), string);
	}

	public void pollDebugCrash() {
		if (this.debugCrashStartTime > 0L) {
			long l = SystemUtil.getMeasuringTimeMs();
			long m = 10000L - (l - this.debugCrashStartTime);
			long n = l - this.debugCrashLastLogTime;
			if (m < 0L) {
				if (Screen.isControlPressed()) {
					GlfwUtil.method_15973();
				}

				throw new CrashException(new CrashReport("Manually triggered debug crash", new Throwable()));
			}

			if (n >= 1000L) {
				if (this.debugCrashElapsedTime == 0L) {
					this.debugWarn("debug.crash.message");
				} else {
					this.debugError("debug.crash.warning", MathHelper.ceil((float)m / 1000.0F));
				}

				this.debugCrashLastLogTime = l;
				this.debugCrashElapsedTime++;
			}
		}
	}
}
