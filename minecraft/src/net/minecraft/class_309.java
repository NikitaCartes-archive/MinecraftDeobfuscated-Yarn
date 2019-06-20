package net.minecraft;

import java.util.Locale;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_309 {
	private final class_310 field_1678;
	private boolean field_1683;
	private final class_3674 field_16241 = new class_3674();
	private long field_1682 = -1L;
	private long field_1681 = -1L;
	private long field_1680 = -1L;
	private boolean field_1679;

	public class_309(class_310 arg) {
		this.field_1678 = arg;
	}

	private void method_1459(String string, Object... objects) {
		this.field_1678
			.field_1705
			.method_1743()
			.method_1812(
				new class_2585("")
					.method_10852(new class_2588("debug.prefix").method_10856(new class_124[]{class_124.field_1054, class_124.field_1067}))
					.method_10864(" ")
					.method_10852(new class_2588(string, objects))
			);
	}

	private void method_1456(String string, Object... objects) {
		this.field_1678
			.field_1705
			.method_1743()
			.method_1812(
				new class_2585("")
					.method_10852(new class_2588("debug.prefix").method_10856(new class_124[]{class_124.field_1061, class_124.field_1067}))
					.method_10864(" ")
					.method_10852(new class_2588(string, objects))
			);
	}

	private boolean method_1468(int i) {
		if (this.field_1682 > 0L && this.field_1682 < class_156.method_658() - 100L) {
			return true;
		} else {
			switch (i) {
				case 65:
					this.field_1678.field_1769.method_3279();
					this.method_1459("debug.reload_chunks.message");
					return true;
				case 66:
					boolean bl = !this.field_1678.method_1561().method_3958();
					this.field_1678.method_1561().method_3955(bl);
					this.method_1459(bl ? "debug.show_hitboxes.on" : "debug.show_hitboxes.off");
					return true;
				case 67:
					if (this.field_1678.field_1724.method_7302()) {
						return false;
					}

					this.method_1459("debug.copy_location.message");
					this.method_1455(
						String.format(
							Locale.ROOT,
							"/execute in %s run tp @s %.2f %.2f %.2f %.2f %.2f",
							class_2874.method_12485(this.field_1678.field_1724.field_6002.field_9247.method_12460()),
							this.field_1678.field_1724.field_5987,
							this.field_1678.field_1724.field_6010,
							this.field_1678.field_1724.field_6035,
							this.field_1678.field_1724.field_6031,
							this.field_1678.field_1724.field_5965
						)
					);
					return true;
				case 68:
					if (this.field_1678.field_1705 != null) {
						this.field_1678.field_1705.method_1743().method_1808(false);
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
					class_316.field_1933
						.method_18614(
							this.field_1678.field_1690,
							class_3532.method_15350(
								(double)(this.field_1678.field_1690.field_1870 + (class_437.hasShiftDown() ? -1 : 1)),
								class_316.field_1933.method_18615(),
								class_316.field_1933.method_18617()
							)
						);
					this.method_1459("debug.cycle_renderdistance.message", this.field_1678.field_1690.field_1870);
					return true;
				case 71:
					boolean bl2 = this.field_1678.field_1709.method_3713();
					this.method_1459(bl2 ? "debug.chunk_boundaries.on" : "debug.chunk_boundaries.off");
					return true;
				case 72:
					this.field_1678.field_1690.field_1827 = !this.field_1678.field_1690.field_1827;
					this.method_1459(this.field_1678.field_1690.field_1827 ? "debug.advanced_tooltips.on" : "debug.advanced_tooltips.off");
					this.field_1678.field_1690.method_1640();
					return true;
				case 73:
					if (!this.field_1678.field_1724.method_7302()) {
						this.method_1465(this.field_1678.field_1724.method_5687(2), !class_437.hasShiftDown());
					}

					return true;
				case 78:
					if (!this.field_1678.field_1724.method_5687(2)) {
						this.method_1459("debug.creative_spectator.error");
					} else if (this.field_1678.field_1724.method_7337()) {
						this.field_1678.field_1724.method_3142("/gamemode spectator");
					} else {
						this.field_1678.field_1724.method_3142("/gamemode creative");
					}

					return true;
				case 80:
					this.field_1678.field_1690.field_1837 = !this.field_1678.field_1690.field_1837;
					this.field_1678.field_1690.method_1640();
					this.method_1459(this.field_1678.field_1690.field_1837 ? "debug.pause_focus.on" : "debug.pause_focus.off");
					return true;
				case 81:
					this.method_1459("debug.help.message");
					class_338 lv = this.field_1678.field_1705.method_1743();
					lv.method_1812(new class_2588("debug.reload_chunks.help"));
					lv.method_1812(new class_2588("debug.show_hitboxes.help"));
					lv.method_1812(new class_2588("debug.copy_location.help"));
					lv.method_1812(new class_2588("debug.clear_chat.help"));
					lv.method_1812(new class_2588("debug.cycle_renderdistance.help"));
					lv.method_1812(new class_2588("debug.chunk_boundaries.help"));
					lv.method_1812(new class_2588("debug.advanced_tooltips.help"));
					lv.method_1812(new class_2588("debug.inspect.help"));
					lv.method_1812(new class_2588("debug.creative_spectator.help"));
					lv.method_1812(new class_2588("debug.pause_focus.help"));
					lv.method_1812(new class_2588("debug.help.help"));
					lv.method_1812(new class_2588("debug.reload_resourcepacks.help"));
					lv.method_1812(new class_2588("debug.pause.help"));
					return true;
				case 84:
					this.method_1459("debug.reload_resourcepacks.message");
					this.field_1678.method_1521();
					return true;
			}
		}
	}

	private void method_1465(boolean bl, boolean bl2) {
		class_239 lv = this.field_1678.field_1765;
		if (lv != null) {
			switch (lv.method_17783()) {
				case field_1332:
					class_2338 lv2 = ((class_3965)lv).method_17777();
					class_2680 lv3 = this.field_1678.field_1724.field_6002.method_8320(lv2);
					if (bl) {
						if (bl2) {
							this.field_1678.field_1724.field_3944.method_2876().method_1403(lv2, arg3 -> {
								this.method_1475(lv3, lv2, arg3);
								this.method_1459("debug.inspect.server.block");
							});
						} else {
							class_2586 lv4 = this.field_1678.field_1724.field_6002.method_8321(lv2);
							class_2487 lv5 = lv4 != null ? lv4.method_11007(new class_2487()) : null;
							this.method_1475(lv3, lv2, lv5);
							this.method_1459("debug.inspect.client.block");
						}
					} else {
						this.method_1475(lv3, lv2, null);
						this.method_1459("debug.inspect.client.block");
					}
					break;
				case field_1331:
					class_1297 lv6 = ((class_3966)lv).method_17782();
					class_2960 lv7 = class_2378.field_11145.method_10221(lv6.method_5864());
					class_243 lv8 = new class_243(lv6.field_5987, lv6.field_6010, lv6.field_6035);
					if (bl) {
						if (bl2) {
							this.field_1678.field_1724.field_3944.method_2876().method_1405(lv6.method_5628(), arg3 -> {
								this.method_1469(lv7, lv8, arg3);
								this.method_1459("debug.inspect.server.entity");
							});
						} else {
							class_2487 lv5 = lv6.method_5647(new class_2487());
							this.method_1469(lv7, lv8, lv5);
							this.method_1459("debug.inspect.client.entity");
						}
					} else {
						this.method_1469(lv7, lv8, null);
						this.method_1459("debug.inspect.client.entity");
					}
			}
		}
	}

	private void method_1475(class_2680 arg, class_2338 arg2, @Nullable class_2487 arg3) {
		if (arg3 != null) {
			arg3.method_10551("x");
			arg3.method_10551("y");
			arg3.method_10551("z");
			arg3.method_10551("id");
		}

		StringBuilder stringBuilder = new StringBuilder(class_2259.method_9685(arg));
		if (arg3 != null) {
			stringBuilder.append(arg3);
		}

		String string = String.format(Locale.ROOT, "/setblock %d %d %d %s", arg2.method_10263(), arg2.method_10264(), arg2.method_10260(), stringBuilder);
		this.method_1455(string);
	}

	private void method_1469(class_2960 arg, class_243 arg2, @Nullable class_2487 arg3) {
		String string2;
		if (arg3 != null) {
			arg3.method_10551("UUIDMost");
			arg3.method_10551("UUIDLeast");
			arg3.method_10551("Pos");
			arg3.method_10551("Dimension");
			String string = arg3.method_10715().getString();
			string2 = String.format(Locale.ROOT, "/summon %s %.2f %.2f %.2f %s", arg.toString(), arg2.field_1352, arg2.field_1351, arg2.field_1350, string);
		} else {
			string2 = String.format(Locale.ROOT, "/summon %s %.2f %.2f %.2f", arg.toString(), arg2.field_1352, arg2.field_1351, arg2.field_1350);
		}

		this.method_1455(string2);
	}

	public void method_1466(long l, int i, int j, int k, int m) {
		if (l == this.field_1678.field_1704.method_4490()) {
			if (this.field_1682 > 0L) {
				if (!class_3675.method_15987(class_310.method_1551().field_1704.method_4490(), 67)
					|| !class_3675.method_15987(class_310.method_1551().field_1704.method_4490(), 292)) {
					this.field_1682 = -1L;
				}
			} else if (class_3675.method_15987(class_310.method_1551().field_1704.method_4490(), 67)
				&& class_3675.method_15987(class_310.method_1551().field_1704.method_4490(), 292)) {
				this.field_1679 = true;
				this.field_1682 = class_156.method_658();
				this.field_1681 = class_156.method_658();
				this.field_1680 = 0L;
			}

			class_4069 lv = this.field_1678.field_1755;
			if (k == 1 && (!(this.field_1678.field_1755 instanceof class_458) || ((class_458)lv).field_2723 <= class_156.method_658() - 20L)) {
				if (this.field_1678.field_1690.field_1836.method_1417(i, j)) {
					this.field_1678.field_1704.method_4500();
					this.field_1678.field_1690.field_1857 = this.field_1678.field_1704.method_4498();
					return;
				}

				if (this.field_1678.field_1690.field_1835.method_1417(i, j)) {
					if (class_437.hasControlDown()) {
					}

					class_318.method_1659(
						this.field_1678.field_1697,
						this.field_1678.field_1704.method_4489(),
						this.field_1678.field_1704.method_4506(),
						this.field_1678.method_1522(),
						arg -> this.field_1678.execute(() -> this.field_1678.field_1705.method_1743().method_1812(arg))
					);
					return;
				}
			}

			boolean bl = lv == null || !(lv.getFocused() instanceof class_342) || !((class_342)lv.getFocused()).method_20315();
			if (k != 0 && i == 66 && class_437.hasControlDown() && bl) {
				class_316.field_18194.method_18500(this.field_1678.field_1690, 1);
				if (lv instanceof class_404) {
					((class_404)lv).method_2096();
				}

				if (lv instanceof class_4189) {
					((class_4189)lv).method_19366();
				}
			}

			if (lv != null) {
				boolean[] bls = new boolean[]{false};
				class_437.wrapScreenError(() -> {
					if (k != 1 && (k != 2 || !this.field_1683)) {
						if (k == 0) {
							bls[0] = lv.method_16803(i, j, m);
						}
					} else {
						bls[0] = lv.keyPressed(i, j, m);
					}
				}, "keyPressed event handler", lv.getClass().getCanonicalName());
				if (bls[0]) {
					return;
				}
			}

			if (this.field_1678.field_1755 == null || this.field_1678.field_1755.passEvents) {
				class_3675.class_306 lv2 = class_3675.method_15985(i, j);
				if (k == 0) {
					class_304.method_1416(lv2, false);
					if (i == 292) {
						if (this.field_1679) {
							this.field_1679 = false;
						} else {
							this.field_1678.field_1690.field_1866 = !this.field_1678.field_1690.field_1866;
							this.field_1678.field_1690.field_1880 = this.field_1678.field_1690.field_1866 && class_437.hasShiftDown();
							this.field_1678.field_1690.field_1893 = this.field_1678.field_1690.field_1866 && class_437.hasAltDown();
						}
					}
				} else {
					if (i == 293 && this.field_1678.field_1773 != null) {
						this.field_1678.field_1773.method_3184();
					}

					boolean bl2 = false;
					if (this.field_1678.field_1755 == null) {
						if (i == 256) {
							boolean bl3 = class_3675.method_15987(class_310.method_1551().field_1704.method_4490(), 292);
							this.field_1678.method_20539(bl3);
						}

						bl2 = class_3675.method_15987(class_310.method_1551().field_1704.method_4490(), 292) && this.method_1468(i);
						this.field_1679 |= bl2;
						if (i == 290) {
							this.field_1678.field_1690.field_1842 = !this.field_1678.field_1690.field_1842;
						}
					}

					if (bl2) {
						class_304.method_1416(lv2, false);
					} else {
						class_304.method_1416(lv2, true);
						class_304.method_1420(lv2);
					}

					if (this.field_1678.field_1690.field_1880) {
						if (i == 48) {
							this.field_1678.method_1524(0);
						}

						for (int n = 0; n < 9; n++) {
							if (i == 49 + n) {
								this.field_1678.method_1524(n + 1);
							}
						}
					}
				}
			}
		}
	}

	private void method_1457(long l, int i, int j) {
		if (l == this.field_1678.field_1704.method_4490()) {
			class_364 lv = this.field_1678.field_1755;
			if (lv != null && this.field_1678.method_18506() == null) {
				if (Character.charCount(i) == 1) {
					class_437.wrapScreenError(() -> lv.charTyped((char)i, j), "charTyped event handler", lv.getClass().getCanonicalName());
				} else {
					for (char c : Character.toChars(i)) {
						class_437.wrapScreenError(() -> lv.charTyped(c, j), "charTyped event handler", lv.getClass().getCanonicalName());
					}
				}
			}
		}
	}

	public void method_1462(boolean bl) {
		this.field_1683 = bl;
	}

	public void method_1472(long l) {
		class_3675.method_15986(l, this::method_1466, this::method_1457);
	}

	public String method_1460() {
		return this.field_16241.method_15977(this.field_1678.field_1704.method_4490(), (i, l) -> {
			if (i != 65545) {
				this.field_1678.field_1704.method_4482(i, l);
			}
		});
	}

	public void method_1455(String string) {
		this.field_16241.method_15979(this.field_1678.field_1704.method_4490(), string);
	}

	public void method_1474() {
		if (this.field_1682 > 0L) {
			long l = class_156.method_658();
			long m = 10000L - (l - this.field_1682);
			long n = l - this.field_1681;
			if (m < 0L) {
				if (class_437.hasControlDown()) {
					class_3673.method_15973();
				}

				throw new class_148(new class_128("Manually triggered debug crash", new Throwable()));
			}

			if (n >= 1000L) {
				if (this.field_1680 == 0L) {
					this.method_1459("debug.crash.message");
				} else {
					this.method_1456("debug.crash.warning", class_3532.method_15386((float)m / 1000.0F));
				}

				this.field_1681 = l;
				this.field_1680++;
			}
		}
	}
}
