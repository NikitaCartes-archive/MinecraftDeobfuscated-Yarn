package net.minecraft.client;

import java.util.Locale;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
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

@Environment(EnvType.CLIENT)
public class Keyboard {
	private final MinecraftClient field_1678;
	private boolean repeatEvents;
	private final Clipboard clipboard = new Clipboard();
	private long debugCrashStartTime = -1L;
	private long debugCrashLastLogTime = -1L;
	private long debugCrashElapsedTime = -1L;
	private boolean switchF3State;

	public Keyboard(MinecraftClient minecraftClient) {
		this.field_1678 = minecraftClient;
	}

	private void debugWarn(String string, Object... objects) {
		this.field_1678
			.field_1705
			.method_1743()
			.addMessage(
				new LiteralText("")
					.append(new TranslatableText("debug.prefix").formatted(new Formatting[]{Formatting.field_1054, Formatting.field_1067}))
					.append(" ")
					.append(new TranslatableText(string, objects))
			);
	}

	private void debugError(String string, Object... objects) {
		this.field_1678
			.field_1705
			.method_1743()
			.addMessage(
				new LiteralText("")
					.append(new TranslatableText("debug.prefix").formatted(new Formatting[]{Formatting.field_1061, Formatting.field_1067}))
					.append(" ")
					.append(new TranslatableText(string, objects))
			);
	}

	private boolean processF3(int i) {
		if (this.debugCrashStartTime > 0L && this.debugCrashStartTime < SystemUtil.getMeasuringTimeMs() - 100L) {
			return true;
		} else {
			switch (i) {
				case 65:
					this.field_1678.field_1769.reload();
					this.debugWarn("debug.reload_chunks.message");
					return true;
				case 66:
					boolean bl = !this.field_1678.method_1561().shouldRenderHitboxes();
					this.field_1678.method_1561().setRenderHitboxes(bl);
					this.debugWarn(bl ? "debug.show_hitboxes.on" : "debug.show_hitboxes.off");
					return true;
				case 67:
					if (this.field_1678.field_1724.getReducedDebugInfo()) {
						return false;
					}

					this.debugWarn("debug.copy_location.message");
					this.setClipboard(
						String.format(
							Locale.ROOT,
							"/execute in %s run tp @s %.2f %.2f %.2f %.2f %.2f",
							DimensionType.getId(this.field_1678.field_1724.field_6002.field_9247.method_12460()),
							this.field_1678.field_1724.x,
							this.field_1678.field_1724.y,
							this.field_1678.field_1724.z,
							this.field_1678.field_1724.yaw,
							this.field_1678.field_1724.pitch
						)
					);
					return true;
				case 68:
					if (this.field_1678.field_1705 != null) {
						this.field_1678.field_1705.method_1743().clear(false);
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
					Option.field_1933
						.set(
							this.field_1678.field_1690,
							MathHelper.clamp(
								(double)(this.field_1678.field_1690.viewDistance + (Screen.hasShiftDown() ? -1 : 1)), Option.field_1933.getMin(), Option.field_1933.getMax()
							)
						);
					this.debugWarn("debug.cycle_renderdistance.message", this.field_1678.field_1690.viewDistance);
					return true;
				case 71:
					boolean bl2 = this.field_1678.field_1709.toggleShowChunkBorder();
					this.debugWarn(bl2 ? "debug.chunk_boundaries.on" : "debug.chunk_boundaries.off");
					return true;
				case 72:
					this.field_1678.field_1690.advancedItemTooltips = !this.field_1678.field_1690.advancedItemTooltips;
					this.debugWarn(this.field_1678.field_1690.advancedItemTooltips ? "debug.advanced_tooltips.on" : "debug.advanced_tooltips.off");
					this.field_1678.field_1690.write();
					return true;
				case 73:
					if (!this.field_1678.field_1724.getReducedDebugInfo()) {
						this.copyLookAt(this.field_1678.field_1724.allowsPermissionLevel(2), !Screen.hasShiftDown());
					}

					return true;
				case 78:
					if (!this.field_1678.field_1724.allowsPermissionLevel(2)) {
						this.debugWarn("debug.creative_spectator.error");
					} else if (this.field_1678.field_1724.isCreative()) {
						this.field_1678.field_1724.sendChatMessage("/gamemode spectator");
					} else {
						this.field_1678.field_1724.sendChatMessage("/gamemode creative");
					}

					return true;
				case 80:
					this.field_1678.field_1690.pauseOnLostFocus = !this.field_1678.field_1690.pauseOnLostFocus;
					this.field_1678.field_1690.write();
					this.debugWarn(this.field_1678.field_1690.pauseOnLostFocus ? "debug.pause_focus.on" : "debug.pause_focus.off");
					return true;
				case 81:
					this.debugWarn("debug.help.message");
					ChatHud chatHud = this.field_1678.field_1705.method_1743();
					chatHud.addMessage(new TranslatableText("debug.reload_chunks.help"));
					chatHud.addMessage(new TranslatableText("debug.show_hitboxes.help"));
					chatHud.addMessage(new TranslatableText("debug.copy_location.help"));
					chatHud.addMessage(new TranslatableText("debug.clear_chat.help"));
					chatHud.addMessage(new TranslatableText("debug.cycle_renderdistance.help"));
					chatHud.addMessage(new TranslatableText("debug.chunk_boundaries.help"));
					chatHud.addMessage(new TranslatableText("debug.advanced_tooltips.help"));
					chatHud.addMessage(new TranslatableText("debug.inspect.help"));
					chatHud.addMessage(new TranslatableText("debug.creative_spectator.help"));
					chatHud.addMessage(new TranslatableText("debug.pause_focus.help"));
					chatHud.addMessage(new TranslatableText("debug.help.help"));
					chatHud.addMessage(new TranslatableText("debug.reload_resourcepacks.help"));
					chatHud.addMessage(new TranslatableText("debug.pause.help"));
					return true;
				case 84:
					this.debugWarn("debug.reload_resourcepacks.message");
					this.field_1678.reloadResources();
					return true;
			}
		}
	}

	private void copyLookAt(boolean bl, boolean bl2) {
		HitResult hitResult = this.field_1678.hitResult;
		if (hitResult != null) {
			switch (hitResult.getType()) {
				case field_1332:
					BlockPos blockPos = ((BlockHitResult)hitResult).getBlockPos();
					BlockState blockState = this.field_1678.field_1724.field_6002.method_8320(blockPos);
					if (bl) {
						if (bl2) {
							this.field_1678.field_1724.networkHandler.getDataQueryHandler().queryBlockNbt(blockPos, compoundTagx -> {
								this.copyBlock(blockState, blockPos, compoundTagx);
								this.debugWarn("debug.inspect.server.block");
							});
						} else {
							BlockEntity blockEntity = this.field_1678.field_1724.field_6002.method_8321(blockPos);
							CompoundTag compoundTag = blockEntity != null ? blockEntity.toTag(new CompoundTag()) : null;
							this.copyBlock(blockState, blockPos, compoundTag);
							this.debugWarn("debug.inspect.client.block");
						}
					} else {
						this.copyBlock(blockState, blockPos, null);
						this.debugWarn("debug.inspect.client.block");
					}
					break;
				case field_1331:
					Entity entity = ((EntityHitResult)hitResult).getEntity();
					Identifier identifier = Registry.ENTITY_TYPE.getId(entity.getType());
					Vec3d vec3d = new Vec3d(entity.x, entity.y, entity.z);
					if (bl) {
						if (bl2) {
							this.field_1678.field_1724.networkHandler.getDataQueryHandler().queryEntityNbt(entity.getEntityId(), compoundTagx -> {
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
			String string = compoundTag.toText().getString();
			string2 = String.format(Locale.ROOT, "/summon %s %.2f %.2f %.2f %s", identifier.toString(), vec3d.x, vec3d.y, vec3d.z, string);
		} else {
			string2 = String.format(Locale.ROOT, "/summon %s %.2f %.2f %.2f", identifier.toString(), vec3d.x, vec3d.y, vec3d.z);
		}

		this.setClipboard(string2);
	}

	public void onKey(long l, int i, int j, int k, int m) {
		if (l == this.field_1678.window.getHandle()) {
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

			ParentElement parentElement = this.field_1678.field_1755;
			if (k == 1
				&& (!(this.field_1678.field_1755 instanceof ControlsOptionsScreen) || ((ControlsOptionsScreen)parentElement).time <= SystemUtil.getMeasuringTimeMs() - 20L)
				)
			 {
				if (this.field_1678.field_1690.keyFullscreen.matchesKey(i, j)) {
					this.field_1678.window.toggleFullscreen();
					this.field_1678.field_1690.fullscreen = this.field_1678.window.isFullscreen();
					return;
				}

				if (this.field_1678.field_1690.keyScreenshot.matchesKey(i, j)) {
					if (Screen.hasControlDown()) {
					}

					ScreenshotUtils.method_1659(
						this.field_1678.runDirectory,
						this.field_1678.window.getFramebufferWidth(),
						this.field_1678.window.getFramebufferHeight(),
						this.field_1678.getFramebuffer(),
						text -> this.field_1678.execute(() -> this.field_1678.field_1705.method_1743().addMessage(text))
					);
					return;
				}
			}

			boolean bl = parentElement == null
				|| !(parentElement.getFocused() instanceof TextFieldWidget)
				|| !((TextFieldWidget)parentElement.getFocused()).method_20315();
			if (k != 0 && i == 66 && Screen.hasControlDown() && bl) {
				Option.NARRATOR.method_18500(this.field_1678.field_1690, 1);
				if (parentElement instanceof ChatOptionsScreen) {
					((ChatOptionsScreen)parentElement).method_2096();
				}

				if (parentElement instanceof AccessibilityScreen) {
					((AccessibilityScreen)parentElement).method_19366();
				}
			}

			if (parentElement != null) {
				boolean[] bls = new boolean[]{false};
				Screen.wrapScreenError(() -> {
					if (k != 1 && (k != 2 || !this.repeatEvents)) {
						if (k == 0) {
							bls[0] = parentElement.keyReleased(i, j, m);
						}
					} else {
						bls[0] = parentElement.keyPressed(i, j, m);
					}
				}, "keyPressed event handler", parentElement.getClass().getCanonicalName());
				if (bls[0]) {
					return;
				}
			}

			if (this.field_1678.field_1755 == null || this.field_1678.field_1755.passEvents) {
				InputUtil.KeyCode keyCode = InputUtil.getKeyCode(i, j);
				if (k == 0) {
					KeyBinding.setKeyPressed(keyCode, false);
					if (i == 292) {
						if (this.switchF3State) {
							this.switchF3State = false;
						} else {
							this.field_1678.field_1690.debugEnabled = !this.field_1678.field_1690.debugEnabled;
							this.field_1678.field_1690.debugProfilerEnabled = this.field_1678.field_1690.debugEnabled && Screen.hasShiftDown();
							this.field_1678.field_1690.debugTpsEnabled = this.field_1678.field_1690.debugEnabled && Screen.hasAltDown();
						}
					}
				} else {
					if (i == 293 && this.field_1678.field_1773 != null) {
						this.field_1678.field_1773.toggleShadersEnabled();
					}

					boolean bl2 = false;
					if (this.field_1678.field_1755 == null) {
						if (i == 256) {
							boolean bl3 = InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), 292);
							this.field_1678.openPauseMenu(bl3);
						}

						bl2 = InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), 292) && this.processF3(i);
						this.switchF3State |= bl2;
						if (i == 290) {
							this.field_1678.field_1690.hudHidden = !this.field_1678.field_1690.hudHidden;
						}
					}

					if (bl2) {
						KeyBinding.setKeyPressed(keyCode, false);
					} else {
						KeyBinding.setKeyPressed(keyCode, true);
						KeyBinding.onKeyPressed(keyCode);
					}

					if (this.field_1678.field_1690.debugProfilerEnabled) {
						if (i == 48) {
							this.field_1678.handleProfilerKeyPress(0);
						}

						for (int n = 0; n < 9; n++) {
							if (i == 49 + n) {
								this.field_1678.handleProfilerKeyPress(n + 1);
							}
						}
					}
				}
			}
		}
	}

	private void onChar(long l, int i, int j) {
		if (l == this.field_1678.window.getHandle()) {
			Element element = this.field_1678.field_1755;
			if (element != null && this.field_1678.method_18506() == null) {
				if (Character.charCount(i) == 1) {
					Screen.wrapScreenError(() -> element.charTyped((char)i, j), "charTyped event handler", element.getClass().getCanonicalName());
				} else {
					for (char c : Character.toChars(i)) {
						Screen.wrapScreenError(() -> element.charTyped(c, j), "charTyped event handler", element.getClass().getCanonicalName());
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
		return this.clipboard.getClipboard(this.field_1678.window.getHandle(), (i, l) -> {
			if (i != 65545) {
				this.field_1678.window.logGlError(i, l);
			}
		});
	}

	public void setClipboard(String string) {
		this.clipboard.setClipboard(this.field_1678.window.getHandle(), string);
	}

	public void pollDebugCrash() {
		if (this.debugCrashStartTime > 0L) {
			long l = SystemUtil.getMeasuringTimeMs();
			long m = 10000L - (l - this.debugCrashStartTime);
			long n = l - this.debugCrashLastLogTime;
			if (m < 0L) {
				if (Screen.hasControlDown()) {
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
