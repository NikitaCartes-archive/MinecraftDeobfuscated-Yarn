package net.minecraft.client.util;

import java.util.Locale;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2259;
import net.minecraft.class_3673;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiEventListener;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.menu.settings.ChatSettingsGui;
import net.minecraft.client.gui.menu.settings.ControlsSettingsGui;
import net.minecraft.client.settings.GameOptions;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
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
	private long field_1682 = -1L;
	private long field_1681 = -1L;
	private long field_1680 = -1L;
	private boolean field_1679;

	public Keyboard(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
	}

	private void debugWarn(String string, Object... objects) {
		this.client
			.hudInGame
			.getHudChat()
			.addMessage(
				new StringTextComponent("")
					.append(new TranslatableTextComponent("debug.prefix").applyFormat(new TextFormat[]{TextFormat.YELLOW, TextFormat.BOLD}))
					.append(" ")
					.append(new TranslatableTextComponent(string, objects))
			);
	}

	private void debugError(String string, Object... objects) {
		this.client
			.hudInGame
			.getHudChat()
			.addMessage(
				new StringTextComponent("")
					.append(new TranslatableTextComponent("debug.prefix").applyFormat(new TextFormat[]{TextFormat.RED, TextFormat.BOLD}))
					.append(" ")
					.append(new TranslatableTextComponent(string, objects))
			);
	}

	private boolean method_1468(int i) {
		if (this.field_1682 > 0L && this.field_1682 < SystemUtil.getMeasuringTimeMili() - 100L) {
			return true;
		} else {
			switch (i) {
				case 65:
					this.client.renderer.reload();
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
					this.setClipbord(
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
					if (this.client.hudInGame != null) {
						this.client.hudInGame.getHudChat().clear(false);
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
					this.client.options.updateOption(GameOptions.Option.RENDER_DISTANCE, Gui.isShiftPressed() ? -1 : 1);
					this.debugWarn("debug.cycle_renderdistance.message", this.client.options.viewDistance);
					return true;
				case 71:
					boolean bl2 = this.client.renderDebug.toggleShowChunkBorder();
					this.debugWarn(bl2 ? "debug.chunk_boundaries.on" : "debug.chunk_boundaries.off");
					return true;
				case 72:
					this.client.options.advancedItemTooltips = !this.client.options.advancedItemTooltips;
					this.debugWarn(this.client.options.advancedItemTooltips ? "debug.advanced_tooltips.on" : "debug.advanced_tooltips.off");
					this.client.options.write();
					return true;
				case 73:
					if (!this.client.player.getReducedDebugInfo()) {
						this.method_1465(this.client.player.allowsPermissionLevel(2), !Gui.isShiftPressed());
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
					ChatHud chatHud = this.client.hudInGame.getHudChat();
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

	private void method_1465(boolean bl, boolean bl2) {
		if (this.client.hitResult != null) {
			switch (this.client.hitResult.type) {
				case BLOCK:
					BlockPos blockPos = this.client.hitResult.getBlockPos();
					BlockState blockState = this.client.player.world.getBlockState(blockPos);
					if (bl) {
						if (bl2) {
							this.client.player.networkHandler.method_2876().method_1403(blockPos, compoundTagx -> {
								this.method_1475(blockState, blockPos, compoundTagx);
								this.debugWarn("debug.inspect.server.block");
							});
						} else {
							BlockEntity blockEntity = this.client.player.world.getBlockEntity(blockPos);
							CompoundTag compoundTag = blockEntity != null ? blockEntity.toTag(new CompoundTag()) : null;
							this.method_1475(blockState, blockPos, compoundTag);
							this.debugWarn("debug.inspect.client.block");
						}
					} else {
						this.method_1475(blockState, blockPos, null);
						this.debugWarn("debug.inspect.client.block");
					}
					break;
				case ENTITY:
					Entity entity = this.client.hitResult.entity;
					if (entity == null) {
						return;
					}

					Identifier identifier = Registry.ENTITY_TYPE.getId(entity.getType());
					Vec3d vec3d = new Vec3d(entity.x, entity.y, entity.z);
					if (bl) {
						if (bl2) {
							this.client.player.networkHandler.method_2876().method_1405(entity.getEntityId(), compoundTagx -> {
								this.method_1469(identifier, vec3d, compoundTagx);
								this.debugWarn("debug.inspect.server.entity");
							});
						} else {
							CompoundTag compoundTag = entity.toTag(new CompoundTag());
							this.method_1469(identifier, vec3d, compoundTag);
							this.debugWarn("debug.inspect.client.entity");
						}
					} else {
						this.method_1469(identifier, vec3d, null);
						this.debugWarn("debug.inspect.client.entity");
					}
			}
		}
	}

	private void method_1475(BlockState blockState, BlockPos blockPos, @Nullable CompoundTag compoundTag) {
		if (compoundTag != null) {
			compoundTag.remove("x");
			compoundTag.remove("y");
			compoundTag.remove("z");
			compoundTag.remove("id");
		}

		StringBuilder stringBuilder = new StringBuilder(class_2259.method_9685(blockState));
		if (compoundTag != null) {
			stringBuilder.append(compoundTag);
		}

		String string = String.format(Locale.ROOT, "/setblock %d %d %d %s", blockPos.getX(), blockPos.getY(), blockPos.getZ(), stringBuilder);
		this.setClipbord(string);
	}

	private void method_1469(Identifier identifier, Vec3d vec3d, @Nullable CompoundTag compoundTag) {
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

		this.setClipbord(string2);
	}

	public void onKey(long l, int i, int j, int k, int m) {
		if (l == this.client.window.getHandle()) {
			if (this.field_1682 > 0L) {
				if (!InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), 67)
					|| !InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), 292)) {
					this.field_1682 = -1L;
				}
			} else if (InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), 67)
				&& InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), 292)) {
				this.field_1679 = true;
				this.field_1682 = SystemUtil.getMeasuringTimeMili();
				this.field_1681 = SystemUtil.getMeasuringTimeMili();
				this.field_1680 = 0L;
			}

			GuiEventListener guiEventListener = this.client.currentGui;
			if (k == 1
				&& (
					!(this.client.currentGui instanceof ControlsSettingsGui) || ((ControlsSettingsGui)guiEventListener).field_2723 <= SystemUtil.getMeasuringTimeMili() - 20L
				)) {
				if (this.client.options.keyFullscreen.matches(i, j)) {
					this.client.window.toggleFullscreen();
					this.client.options.fullscreen = this.client.window.isFullscreen();
					return;
				}

				if (this.client.options.keyScreenshot.matches(i, j)) {
					if (Gui.isControlPressed()) {
					}

					ScreenshotUtils.method_1659(
						this.client.runDirectory,
						this.client.window.getWindowWidth(),
						this.client.window.getWindowHeight(),
						this.client.getFramebuffer(),
						textComponent -> this.client.execute(() -> this.client.hudInGame.getHudChat().addMessage(textComponent))
					);
					return;
				}
			}

			if (guiEventListener != null) {
				boolean[] bls = new boolean[]{false};
				Gui.method_2217(() -> {
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

			if (this.client.currentGui == null || this.client.currentGui.field_2558) {
				InputUtil.KeyCode keyCode = InputUtil.method_15985(i, j);
				if (k == 0) {
					KeyBinding.method_1416(keyCode, false);
					if (i == 292) {
						if (this.field_1679) {
							this.field_1679 = false;
						} else {
							this.client.options.debugEnabled = !this.client.options.debugEnabled;
							this.client.options.debugProfilerEnabled = this.client.options.debugEnabled && Gui.isShiftPressed();
							this.client.options.debugTpsEnabled = this.client.options.debugEnabled && Gui.isAltPressed();
						}
					}
				} else {
					if (i == 66 && Gui.isControlPressed()) {
						this.client.options.updateOption(GameOptions.Option.NARRATOR, 1);
						if (guiEventListener instanceof ChatSettingsGui) {
							((ChatSettingsGui)guiEventListener).method_2096();
						}
					}

					if (i == 293 && this.client.worldRenderer != null) {
						this.client.worldRenderer.method_3184();
					}

					boolean bl = false;
					if (this.client.currentGui == null) {
						if (i == 256) {
							this.client.openInGameMenu();
						}

						bl = InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), 292) && this.method_1468(i);
						this.field_1679 |= bl;
						if (i == 290) {
							this.client.options.field_1842 = !this.client.options.field_1842;
						}
					}

					if (bl) {
						KeyBinding.method_1416(keyCode, false);
					} else {
						KeyBinding.method_1416(keyCode, true);
						KeyBinding.method_1420(keyCode);
					}

					if (this.client.options.debugProfilerEnabled) {
						if (i == 48) {
							this.client.method_1524(0);
						}

						for (int n = 0; n < 9; n++) {
							if (i == 49 + n) {
								this.client.method_1524(n + 1);
							}
						}
					}
				}
			}
		}
	}

	private void onChar(long l, int i, int j) {
		if (l == this.client.window.getHandle()) {
			GuiEventListener guiEventListener = this.client.currentGui;
			if (guiEventListener != null) {
				if (Character.charCount(i) == 1) {
					Gui.method_2217(() -> guiEventListener.charTyped((char)i, j), "charTyped event handler", guiEventListener.getClass().getCanonicalName());
				} else {
					for (char c : Character.toChars(i)) {
						Gui.method_2217(() -> guiEventListener.charTyped(c, j), "charTyped event handler", guiEventListener.getClass().getCanonicalName());
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
				this.client.window.method_4482(i, l);
			}
		});
	}

	public void setClipbord(String string) {
		this.clipboard.setClipboard(this.client.window.getHandle(), string);
	}

	public void method_1474() {
		if (this.field_1682 > 0L) {
			long l = SystemUtil.getMeasuringTimeMili();
			long m = 10000L - (l - this.field_1682);
			long n = l - this.field_1681;
			if (m < 0L) {
				if (Gui.isControlPressed()) {
					class_3673.method_15973();
				}

				throw new CrashException(new CrashReport("Manually triggered debug crash", new Throwable()));
			}

			if (n >= 1000L) {
				if (this.field_1680 == 0L) {
					this.debugWarn("debug.crash.message");
				} else {
					this.debugError("debug.crash.warning", MathHelper.ceil((float)m / 1000.0F));
				}

				this.field_1681 = l;
				this.field_1680++;
			}
		}
	}
}
