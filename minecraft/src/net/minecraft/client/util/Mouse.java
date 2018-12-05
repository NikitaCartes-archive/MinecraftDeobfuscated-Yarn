package net.minecraft.client.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3540;
import net.minecraft.class_3673;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiEventListener;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class Mouse {
	private final MinecraftClient client;
	private boolean field_1791;
	private boolean field_1790;
	private boolean field_1788;
	private double field_1795;
	private double field_1794;
	private int field_1781;
	private int field_1780 = -1;
	private boolean field_1784 = true;
	private int field_1796;
	private double field_1792;
	private final class_3540 field_1793 = new class_3540();
	private final class_3540 field_1782 = new class_3540();
	private double field_1789;
	private double field_1787;
	private double field_1786;
	private double field_1785 = Double.MIN_VALUE;
	private boolean field_1783;

	public Mouse(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
	}

	private void onMouseButton(long l, int i, int j, int k) {
		if (l == this.client.window.getHandle()) {
			boolean bl = j == 1;
			if (MinecraftClient.isSystemMac && i == 0) {
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

				this.field_1780 = m;
				this.field_1792 = class_3673.method_15974();
			} else if (this.field_1780 != -1) {
				if (this.client.options.touchscreen && --this.field_1796 > 0) {
					return;
				}

				this.field_1780 = -1;
			}

			boolean[] bls = new boolean[]{false};
			if (this.client.currentGui == null) {
				if (!this.field_1783 && bl) {
					this.method_1612();
				}
			} else {
				double d = this.field_1795 * (double)this.client.window.getScaledWidth() / (double)this.client.window.method_4480();
				double e = this.field_1794 * (double)this.client.window.getScaledHeight() / (double)this.client.window.method_4507();
				if (bl) {
					Gui.method_2217(
						() -> bls[0] = this.client.currentGui.mouseClicked(d, e, m), "mouseClicked event handler", this.client.currentGui.getClass().getCanonicalName()
					);
				} else {
					Gui.method_2217(
						() -> bls[0] = this.client.currentGui.mouseReleased(d, e, m), "mouseReleased event handler", this.client.currentGui.getClass().getCanonicalName()
					);
				}
			}

			if (!bls[0] && (this.client.currentGui == null || this.client.currentGui.field_2558)) {
				if (m == 0) {
					this.field_1791 = bl;
				} else if (m == 2) {
					this.field_1790 = bl;
				} else if (m == 1) {
					this.field_1788 = bl;
				}

				KeyBinding.method_1416(InputUtil.Type.field_1672.createFromCode(m), bl);
				if (bl) {
					if (this.client.player.isSpectator() && m == 2) {
						this.client.hudInGame.getSpectatorWidget().method_1983();
					} else {
						KeyBinding.method_1420(InputUtil.Type.field_1672.createFromCode(m));
					}
				}
			}
		}
	}

	private void onMouseScroll(long l, double d, double e) {
		if (l == MinecraftClient.getInstance().window.getHandle()) {
			double f = e * this.client.options.mouseWheelSensitivity;
			if (this.client.currentGui != null) {
				this.client.currentGui.mouseScrolled(f);
			} else if (this.client.player != null) {
				if (this.field_1786 != 0.0 && Math.signum(f) != Math.signum(this.field_1786)) {
					this.field_1786 = 0.0;
				}

				this.field_1786 += f;
				double g = (double)((int)this.field_1786);
				if (g == 0.0) {
					return;
				}

				this.field_1786 -= g;
				if (this.client.player.isSpectator()) {
					if (this.client.hudInGame.getSpectatorWidget().method_1980()) {
						this.client.hudInGame.getSpectatorWidget().method_1976(-g);
					} else {
						double h = MathHelper.clamp((double)this.client.player.abilities.getFlySpeed() + g * 0.005F, 0.0, 0.2F);
						this.client.player.abilities.setFlySpeed(h);
					}
				} else {
					this.client.player.inventory.method_7373(g);
				}
			}
		}
	}

	public void setup(long l) {
		InputUtil.setMouseCallbacks(l, this::onCursorPos, this::onMouseButton, this::onMouseScroll);
	}

	private void onCursorPos(long l, double d, double e) {
		if (l == MinecraftClient.getInstance().window.getHandle()) {
			if (this.field_1784) {
				this.field_1795 = d;
				this.field_1794 = e;
				this.field_1784 = false;
			}

			GuiEventListener guiEventListener = this.client.currentGui;
			if (guiEventListener != null) {
				double f = d * (double)this.client.window.getScaledWidth() / (double)this.client.window.method_4480();
				double g = e * (double)this.client.window.getScaledHeight() / (double)this.client.window.method_4507();
				Gui.method_2217(() -> guiEventListener.mouseMoved(f, g), "mouseMoved event handler", guiEventListener.getClass().getCanonicalName());
				if (this.field_1780 != -1 && this.field_1792 > 0.0) {
					double h = (d - this.field_1795) * (double)this.client.window.getScaledWidth() / (double)this.client.window.method_4480();
					double i = (e - this.field_1794) * (double)this.client.window.getScaledHeight() / (double)this.client.window.method_4507();
					Gui.method_2217(
						() -> guiEventListener.mouseDragged(f, g, this.field_1780, h, i), "mouseDragged event handler", guiEventListener.getClass().getCanonicalName()
					);
				}
			}

			this.client.getProfiler().begin("mouse");
			if (this.method_1613() && this.client.method_1569()) {
				this.field_1789 = this.field_1789 + (d - this.field_1795);
				this.field_1787 = this.field_1787 + (e - this.field_1794);
			}

			this.method_1606();
			this.field_1795 = d;
			this.field_1794 = e;
			this.client.getProfiler().end();
		}
	}

	public void method_1606() {
		double d = class_3673.method_15974();
		double e = d - this.field_1785;
		this.field_1785 = d;
		if (this.method_1613() && this.client.method_1569()) {
			double f = this.client.options.mouseSensitivity * 0.6F + 0.2F;
			double g = f * f * f * 8.0;
			double j;
			double k;
			if (this.client.options.field_1914) {
				double h = this.field_1793.method_15429(this.field_1789 * g, e * g);
				double i = this.field_1782.method_15429(this.field_1787 * g, e * g);
				j = h;
				k = i;
			} else {
				this.field_1793.method_15428();
				this.field_1782.method_15428();
				j = this.field_1789 * g;
				k = this.field_1787 * g;
			}

			this.field_1789 = 0.0;
			this.field_1787 = 0.0;
			int l = 1;
			if (this.client.options.invertYMouse) {
				l = -1;
			}

			this.client.getTutorialManager().method_4908(j, k);
			if (this.client.player != null) {
				this.client.player.method_5872(j, k * (double)l);
			}
		} else {
			this.field_1789 = 0.0;
			this.field_1787 = 0.0;
		}
	}

	public boolean method_1608() {
		return this.field_1791;
	}

	public boolean method_1609() {
		return this.field_1788;
	}

	public double method_1603() {
		return this.field_1795;
	}

	public double method_1604() {
		return this.field_1794;
	}

	public void method_1599() {
		this.field_1784 = true;
	}

	public boolean method_1613() {
		return this.field_1783;
	}

	public void method_1612() {
		if (this.client.method_1569()) {
			if (!this.field_1783) {
				if (!MinecraftClient.isSystemMac) {
					KeyBinding.method_1424();
				}

				this.field_1783 = true;
				this.field_1795 = (double)(this.client.window.method_4480() / 2);
				this.field_1794 = (double)(this.client.window.method_4507() / 2);
				InputUtil.setCursorParameters(this.client.window.getHandle(), 212995, this.field_1795, this.field_1794);
				this.client.openGui(null);
				this.client.attackCooldown = 10000;
			}
		}
	}

	public void method_1610() {
		if (this.field_1783) {
			this.field_1783 = false;
			this.field_1795 = (double)(this.client.window.method_4480() / 2);
			this.field_1794 = (double)(this.client.window.method_4507() / 2);
			InputUtil.setCursorParameters(this.client.window.getHandle(), 212993, this.field_1795, this.field_1794);
		}
	}
}
