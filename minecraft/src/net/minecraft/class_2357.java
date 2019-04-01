package net.minecraft;

import java.util.Random;

public interface class_2357 {
	class_2357 field_16902 = (arg, arg2) -> arg2;

	class_1799 dispense(class_2342 arg, class_1799 arg2);

	static void method_18346() {
		class_2315.method_10009(class_1802.field_8107, new class_2965() {
			@Override
			protected class_1676 method_12844(class_1937 arg, class_2374 arg2, class_1799 arg3) {
				class_1667 lv = new class_1667(arg, arg2.method_10216(), arg2.method_10214(), arg2.method_10215());
				lv.field_7572 = class_1665.class_1666.field_7593;
				return lv;
			}
		});
		class_2315.method_10009(class_1802.field_8087, new class_2965() {
			@Override
			protected class_1676 method_12844(class_1937 arg, class_2374 arg2, class_1799 arg3) {
				class_1667 lv = new class_1667(arg, arg2.method_10216(), arg2.method_10214(), arg2.method_10215());
				lv.method_7459(arg3);
				lv.field_7572 = class_1665.class_1666.field_7593;
				return lv;
			}
		});
		class_2315.method_10009(class_1802.field_8236, new class_2965() {
			@Override
			protected class_1676 method_12844(class_1937 arg, class_2374 arg2, class_1799 arg3) {
				class_1665 lv = new class_1679(arg, arg2.method_10216(), arg2.method_10214(), arg2.method_10215());
				lv.field_7572 = class_1665.class_1666.field_7593;
				return lv;
			}
		});
		class_2315.method_10009(class_1802.field_8803, new class_2965() {
			@Override
			protected class_1676 method_12844(class_1937 arg, class_2374 arg2, class_1799 arg3) {
				return class_156.method_654(new class_1681(arg, arg2.method_10216(), arg2.method_10214(), arg2.method_10215()), arg2x -> arg2x.method_16940(arg3));
			}
		});
		class_2315.method_10009(class_1802.field_8543, new class_2965() {
			@Override
			protected class_1676 method_12844(class_1937 arg, class_2374 arg2, class_1799 arg3) {
				return class_156.method_654(new class_1680(arg, arg2.method_10216(), arg2.method_10214(), arg2.method_10215()), arg2x -> arg2x.method_16940(arg3));
			}
		});
		class_2315.method_10009(class_1802.field_8287, new class_2965() {
			@Override
			protected class_1676 method_12844(class_1937 arg, class_2374 arg2, class_1799 arg3) {
				return class_156.method_654(new class_1683(arg, arg2.method_10216(), arg2.method_10214(), arg2.method_10215()), arg2x -> arg2x.method_16940(arg3));
			}

			@Override
			protected float method_12845() {
				return super.method_12845() * 0.5F;
			}

			@Override
			protected float method_12846() {
				return super.method_12846() * 1.25F;
			}
		});
		class_2315.method_10009(class_1802.field_8436, new class_2357() {
			@Override
			public class_1799 dispense(class_2342 arg, class_1799 arg2) {
				return (new class_2965() {
					@Override
					protected class_1676 method_12844(class_1937 arg, class_2374 arg2, class_1799 arg3) {
						return class_156.method_654(new class_1686(arg, arg2.method_10216(), arg2.method_10214(), arg2.method_10215()), arg2x -> arg2x.method_7494(arg3));
					}

					@Override
					protected float method_12845() {
						return super.method_12845() * 0.5F;
					}

					@Override
					protected float method_12846() {
						return super.method_12846() * 1.25F;
					}
				}).dispense(arg, arg2);
			}
		});
		class_2315.method_10009(class_1802.field_8150, new class_2357() {
			@Override
			public class_1799 dispense(class_2342 arg, class_1799 arg2) {
				return (new class_2965() {
					@Override
					protected class_1676 method_12844(class_1937 arg, class_2374 arg2, class_1799 arg3) {
						return class_156.method_654(new class_1686(arg, arg2.method_10216(), arg2.method_10214(), arg2.method_10215()), arg2x -> arg2x.method_7494(arg3));
					}

					@Override
					protected float method_12845() {
						return super.method_12845() * 0.5F;
					}

					@Override
					protected float method_12846() {
						return super.method_12846() * 1.25F;
					}
				}).dispense(arg, arg2);
			}
		});
		class_2347 lv = new class_2347() {
			@Override
			public class_1799 method_10135(class_2342 arg, class_1799 arg2) {
				class_2350 lv = arg.method_10120().method_11654(class_2315.field_10918);
				class_1299<?> lv2 = ((class_1826)arg2.method_7909()).method_8015(arg2.method_7969());
				lv2.method_5894(arg.method_10207(), arg2, null, arg.method_10122().method_10093(lv), class_3730.field_16470, lv != class_2350.field_11036, false);
				arg2.method_7934(1);
				return arg2;
			}
		};

		for (class_1826 lv2 : class_1826.method_8017()) {
			class_2315.method_10009(lv2, lv);
		}

		class_2315.method_10009(class_1802.field_8639, new class_2347() {
			@Override
			public class_1799 method_10135(class_2342 arg, class_1799 arg2) {
				class_2350 lv = arg.method_10120().method_11654(class_2315.field_10918);
				double d = arg.method_10216() + (double)lv.method_10148();
				double e = (double)((float)arg.method_10122().method_10264() + 0.2F);
				double f = arg.method_10215() + (double)lv.method_10165();
				arg.method_10207().method_8649(new class_1671(arg.method_10207(), d, e, f, arg2));
				arg2.method_7934(1);
				return arg2;
			}

			@Override
			protected void method_10136(class_2342 arg) {
				arg.method_10207().method_8535(1004, arg.method_10122(), 0);
			}
		});
		class_2315.method_10009(class_1802.field_8814, new class_2347() {
			@Override
			public class_1799 method_10135(class_2342 arg, class_1799 arg2) {
				class_2350 lv = arg.method_10120().method_11654(class_2315.field_10918);
				class_2374 lv2 = class_2315.method_10010(arg);
				double d = lv2.method_10216() + (double)((float)lv.method_10148() * 0.3F);
				double e = lv2.method_10214() + (double)((float)lv.method_10164() * 0.3F);
				double f = lv2.method_10215() + (double)((float)lv.method_10165() * 0.3F);
				class_1937 lv3 = arg.method_10207();
				Random random = lv3.field_9229;
				double g = random.nextGaussian() * 0.05 + (double)lv.method_10148();
				double h = random.nextGaussian() * 0.05 + (double)lv.method_10164();
				double i = random.nextGaussian() * 0.05 + (double)lv.method_10165();
				lv3.method_8649(class_156.method_654(new class_1677(lv3, d, e, f, g, h, i), arg2x -> arg2x.method_16936(arg2)));
				arg2.method_7934(1);
				return arg2;
			}

			@Override
			protected void method_10136(class_2342 arg) {
				arg.method_10207().method_8535(1018, arg.method_10122(), 0);
			}
		});
		class_2315.method_10009(class_1802.field_8533, new class_2967(class_1690.class_1692.field_7727));
		class_2315.method_10009(class_1802.field_8486, new class_2967(class_1690.class_1692.field_7728));
		class_2315.method_10009(class_1802.field_8442, new class_2967(class_1690.class_1692.field_7729));
		class_2315.method_10009(class_1802.field_8730, new class_2967(class_1690.class_1692.field_7730));
		class_2315.method_10009(class_1802.field_8138, new class_2967(class_1690.class_1692.field_7723));
		class_2315.method_10009(class_1802.field_8094, new class_2967(class_1690.class_1692.field_7725));
		class_2357 lv3 = new class_2347() {
			private final class_2347 field_13367 = new class_2347();

			@Override
			public class_1799 method_10135(class_2342 arg, class_1799 arg2) {
				class_1755 lv = (class_1755)arg2.method_7909();
				class_2338 lv2 = arg.method_10122().method_10093(arg.method_10120().method_11654(class_2315.field_10918));
				class_1937 lv3 = arg.method_10207();
				if (lv.method_7731(null, lv3, lv2, null)) {
					lv.method_7728(lv3, arg2, lv2);
					return new class_1799(class_1802.field_8550);
				} else {
					return this.field_13367.dispense(arg, arg2);
				}
			}
		};
		class_2315.method_10009(class_1802.field_8187, lv3);
		class_2315.method_10009(class_1802.field_8705, lv3);
		class_2315.method_10009(class_1802.field_8714, lv3);
		class_2315.method_10009(class_1802.field_8666, lv3);
		class_2315.method_10009(class_1802.field_8108, lv3);
		class_2315.method_10009(class_1802.field_8478, lv3);
		class_2315.method_10009(class_1802.field_8550, new class_2347() {
			private final class_2347 field_13368 = new class_2347();

			@Override
			public class_1799 method_10135(class_2342 arg, class_1799 arg2) {
				class_1936 lv = arg.method_10207();
				class_2338 lv2 = arg.method_10122().method_10093(arg.method_10120().method_11654(class_2315.field_10918));
				class_2680 lv3 = lv.method_8320(lv2);
				class_2248 lv4 = lv3.method_11614();
				if (lv4 instanceof class_2263) {
					class_3611 lv5 = ((class_2263)lv4).method_9700(lv, lv2, lv3);
					if (!(lv5 instanceof class_3609)) {
						return super.method_10135(arg, arg2);
					} else {
						class_1792 lv6 = lv5.method_15774();
						arg2.method_7934(1);
						if (arg2.method_7960()) {
							return new class_1799(lv6);
						} else {
							if (arg.<class_2601>method_10121().method_11075(new class_1799(lv6)) < 0) {
								this.field_13368.dispense(arg, new class_1799(lv6));
							}

							return arg2;
						}
					}
				} else {
					return super.method_10135(arg, arg2);
				}
			}
		});
		class_2315.method_10009(class_1802.field_8884, new class_2969() {
			@Override
			protected class_1799 method_10135(class_2342 arg, class_1799 arg2) {
				class_1937 lv = arg.method_10207();
				this.field_13364 = true;
				class_2338 lv2 = arg.method_10122().method_10093(arg.method_10120().method_11654(class_2315.field_10918));
				class_2680 lv3 = lv.method_8320(lv2);
				if (class_1786.method_7825(lv3, lv, lv2)) {
					lv.method_8501(lv2, class_2246.field_10036.method_9564());
				} else if (class_1786.method_17439(lv3)) {
					lv.method_8501(lv2, lv3.method_11657(class_2741.field_12548, Boolean.valueOf(true)));
				} else if (lv3.method_11614() instanceof class_2530) {
					class_2530.method_10738(lv, lv2);
					lv.method_8650(lv2, false);
				} else {
					this.field_13364 = false;
				}

				if (this.field_13364 && arg2.method_7970(1, lv.field_9229, null)) {
					arg2.method_7939(0);
				}

				return arg2;
			}
		});
		class_2315.method_10009(class_1802.field_8324, new class_2969() {
			@Override
			protected class_1799 method_10135(class_2342 arg, class_1799 arg2) {
				this.field_13364 = true;
				class_1937 lv = arg.method_10207();
				class_2338 lv2 = arg.method_10122().method_10093(arg.method_10120().method_11654(class_2315.field_10918));
				if (!class_1752.method_7720(arg2, lv, lv2) && !class_1752.method_7719(arg2, lv, lv2, null)) {
					this.field_13364 = false;
				} else if (!lv.field_9236) {
					lv.method_8535(2005, lv2, 0);
				}

				return arg2;
			}
		});
		class_2315.method_10009(class_2246.field_10375, new class_2347() {
			@Override
			protected class_1799 method_10135(class_2342 arg, class_1799 arg2) {
				class_1937 lv = arg.method_10207();
				class_2338 lv2 = arg.method_10122().method_10093(arg.method_10120().method_11654(class_2315.field_10918));
				class_1541 lv3 = new class_1541(lv, (double)lv2.method_10263() + 0.5, (double)lv2.method_10264(), (double)lv2.method_10260() + 0.5, null);
				lv.method_8649(lv3);
				lv.method_8465(null, lv3.field_5987, lv3.field_6010, lv3.field_6035, class_3417.field_15079, class_3419.field_15245, 1.0F, 1.0F);
				arg2.method_7934(1);
				return arg2;
			}
		});
		class_2357 lv4 = new class_2969() {
			@Override
			protected class_1799 method_10135(class_2342 arg, class_1799 arg2) {
				this.field_13364 = !class_1738.method_7684(arg, arg2).method_7960();
				return arg2;
			}
		};
		class_2315.method_10009(class_1802.field_8681, lv4);
		class_2315.method_10009(class_1802.field_8470, lv4);
		class_2315.method_10009(class_1802.field_8712, lv4);
		class_2315.method_10009(class_1802.field_8398, lv4);
		class_2315.method_10009(class_1802.field_8575, lv4);
		class_2315.method_10009(
			class_1802.field_8791,
			new class_2969() {
				@Override
				protected class_1799 method_10135(class_2342 arg, class_1799 arg2) {
					class_1937 lv = arg.method_10207();
					class_2350 lv2 = arg.method_10120().method_11654(class_2315.field_10918);
					class_2338 lv3 = arg.method_10122().method_10093(lv2);
					this.field_13364 = true;
					if (lv.method_8623(lv3) && class_2570.method_10899(lv, lv3, arg2)) {
						lv.method_8652(
							lv3,
							class_2246.field_10177
								.method_9564()
								.method_11657(
									class_2484.field_11505, Integer.valueOf(lv2.method_10166() == class_2350.class_2351.field_11052 ? 0 : lv2.method_10153().method_10161() * 4)
								),
							3
						);
						class_2586 lv4 = lv.method_8321(lv3);
						if (lv4 instanceof class_2631) {
							class_2570.method_10898(lv, lv3, (class_2631)lv4);
						}

						arg2.method_7934(1);
					} else if (class_1738.method_7684(arg, arg2).method_7960()) {
						this.field_13364 = false;
					}

					return arg2;
				}
			}
		);
		class_2315.method_10009(class_2246.field_10147, new class_2969() {
			@Override
			protected class_1799 method_10135(class_2342 arg, class_1799 arg2) {
				class_1937 lv = arg.method_10207();
				class_2338 lv2 = arg.method_10122().method_10093(arg.method_10120().method_11654(class_2315.field_10918));
				class_2276 lv3 = (class_2276)class_2246.field_10147;
				this.field_13364 = true;
				if (lv.method_8623(lv2) && lv3.method_9733(lv, lv2)) {
					if (!lv.field_9236) {
						lv.method_8652(lv2, lv3.method_9564(), 3);
					}

					arg2.method_7934(1);
				} else {
					class_1799 lv4 = class_1738.method_7684(arg, arg2);
					if (lv4.method_7960()) {
						this.field_13364 = false;
					}
				}

				return arg2;
			}
		});
		class_2315.method_10009(class_2246.field_10603.method_8389(), new class_2970());

		for (class_1767 lv5 : class_1767.values()) {
			class_2315.method_10009(class_2480.method_10525(lv5).method_8389(), new class_2970());
		}

		class_2315.method_10009(class_1802.field_8868.method_8389(), new class_2969() {
			@Override
			protected class_1799 method_10135(class_2342 arg, class_1799 arg2) {
				class_1937 lv = arg.method_10207();
				if (!lv.method_8608()) {
					this.field_13364 = false;
					class_2338 lv2 = arg.method_10122().method_10093(arg.method_10120().method_11654(class_2315.field_10918));

					for (class_1472 lv3 : lv.method_18467(class_1472.class, new class_238(lv2))) {
						if (lv3.method_5805() && !lv3.method_6629() && !lv3.method_6109()) {
							lv3.method_6636();
							if (arg2.method_7970(1, lv.field_9229, null)) {
								arg2.method_7939(0);
							}

							this.field_13364 = true;
							break;
						}
					}
				}

				return arg2;
			}
		});
	}
}
