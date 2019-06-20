package net.minecraft;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import it.unimi.dsi.fastutil.ints.Int2ShortMap;
import it.unimi.dsi.fastutil.ints.Int2ShortOpenHashMap;
import java.util.Collections;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3244 implements class_2792 {
	private static final Logger field_14121 = LogManager.getLogger();
	public final class_2535 field_14127;
	private final MinecraftServer field_14148;
	public class_3222 field_14140;
	private int field_14118;
	private long field_14136;
	private boolean field_14125;
	private long field_14134;
	private int field_14116;
	private int field_14133;
	private final Int2ShortMap field_14132 = new Int2ShortOpenHashMap();
	private double field_14130;
	private double field_14146;
	private double field_14128;
	private double field_14145;
	private double field_14126;
	private double field_14144;
	private class_1297 field_14147;
	private double field_14143;
	private double field_14124;
	private double field_14142;
	private double field_14122;
	private double field_14141;
	private double field_14120;
	private class_243 field_14119;
	private int field_14123;
	private int field_14139;
	private boolean field_14131;
	private int field_14138;
	private boolean field_14129;
	private int field_14137;
	private int field_14117;
	private int field_14135;

	public class_3244(MinecraftServer minecraftServer, class_2535 arg, class_3222 arg2) {
		this.field_14148 = minecraftServer;
		this.field_14127 = arg;
		arg.method_10763(this);
		this.field_14140 = arg2;
		arg2.field_13987 = this;
	}

	public void method_18784() {
		this.method_14372();
		this.field_14140.method_14226();
		this.field_14140.method_5641(this.field_14130, this.field_14146, this.field_14128, this.field_14140.field_6031, this.field_14140.field_5965);
		this.field_14118++;
		this.field_14135 = this.field_14117;
		if (this.field_14131) {
			if (++this.field_14138 > 80) {
				field_14121.warn("{} was kicked for floating too long!", this.field_14140.method_5477().getString());
				this.method_14367(new class_2588("multiplayer.disconnect.flying"));
				return;
			}
		} else {
			this.field_14131 = false;
			this.field_14138 = 0;
		}

		this.field_14147 = this.field_14140.method_5668();
		if (this.field_14147 != this.field_14140 && this.field_14147.method_5642() == this.field_14140) {
			this.field_14143 = this.field_14147.field_5987;
			this.field_14124 = this.field_14147.field_6010;
			this.field_14142 = this.field_14147.field_6035;
			this.field_14122 = this.field_14147.field_5987;
			this.field_14141 = this.field_14147.field_6010;
			this.field_14120 = this.field_14147.field_6035;
			if (this.field_14129 && this.field_14140.method_5668().method_5642() == this.field_14140) {
				if (++this.field_14137 > 80) {
					field_14121.warn("{} was kicked for floating a vehicle too long!", this.field_14140.method_5477().getString());
					this.method_14367(new class_2588("multiplayer.disconnect.flying"));
					return;
				}
			} else {
				this.field_14129 = false;
				this.field_14137 = 0;
			}
		} else {
			this.field_14147 = null;
			this.field_14129 = false;
			this.field_14137 = 0;
		}

		this.field_14148.method_16044().method_15396("keepAlive");
		long l = class_156.method_658();
		if (l - this.field_14136 >= 15000L) {
			if (this.field_14125) {
				this.method_14367(new class_2588("disconnect.timeout"));
			} else {
				this.field_14125 = true;
				this.field_14136 = l;
				this.field_14134 = l;
				this.method_14364(new class_2670(this.field_14134));
			}
		}

		this.field_14148.method_16044().method_15407();
		if (this.field_14116 > 0) {
			this.field_14116--;
		}

		if (this.field_14133 > 0) {
			this.field_14133--;
		}

		if (this.field_14140.method_14219() > 0L
			&& this.field_14148.method_3862() > 0
			&& class_156.method_658() - this.field_14140.method_14219() > (long)(this.field_14148.method_3862() * 1000 * 60)) {
			this.method_14367(new class_2588("multiplayer.disconnect.idling"));
		}
	}

	public void method_14372() {
		this.field_14130 = this.field_14140.field_5987;
		this.field_14146 = this.field_14140.field_6010;
		this.field_14128 = this.field_14140.field_6035;
		this.field_14145 = this.field_14140.field_5987;
		this.field_14126 = this.field_14140.field_6010;
		this.field_14144 = this.field_14140.field_6035;
	}

	public class_2535 method_14366() {
		return this.field_14127;
	}

	private boolean method_19507() {
		return this.field_14148.method_19466(this.field_14140.method_7334());
	}

	public void method_14367(class_2561 arg) {
		this.field_14127.method_10752(new class_2661(arg), future -> this.field_14127.method_10747(arg));
		this.field_14127.method_10757();
		this.field_14148.method_19537(this.field_14127::method_10768);
	}

	@Override
	public void method_12067(class_2851 arg) {
		class_2600.method_11073(arg, this, this.field_14140.method_14220());
		this.field_14140.method_14218(arg.method_12372(), arg.method_12373(), arg.method_12371(), arg.method_12370());
	}

	private static boolean method_14362(class_2828 arg) {
		return Doubles.isFinite(arg.method_12269(0.0))
				&& Doubles.isFinite(arg.method_12268(0.0))
				&& Doubles.isFinite(arg.method_12274(0.0))
				&& Floats.isFinite(arg.method_12270(0.0F))
				&& Floats.isFinite(arg.method_12271(0.0F))
			? Math.abs(arg.method_12269(0.0)) > 3.0E7 || Math.abs(arg.method_12268(0.0)) > 3.0E7 || Math.abs(arg.method_12274(0.0)) > 3.0E7
			: true;
	}

	private static boolean method_14371(class_2833 arg) {
		return !Doubles.isFinite(arg.method_12279())
			|| !Doubles.isFinite(arg.method_12280())
			|| !Doubles.isFinite(arg.method_12276())
			|| !Floats.isFinite(arg.method_12277())
			|| !Floats.isFinite(arg.method_12281());
	}

	@Override
	public void method_12078(class_2833 arg) {
		class_2600.method_11073(arg, this, this.field_14140.method_14220());
		if (method_14371(arg)) {
			this.method_14367(new class_2588("multiplayer.disconnect.invalid_vehicle_movement"));
		} else {
			class_1297 lv = this.field_14140.method_5668();
			if (lv != this.field_14140 && lv.method_5642() == this.field_14140 && lv == this.field_14147) {
				class_3218 lv2 = this.field_14140.method_14220();
				double d = lv.field_5987;
				double e = lv.field_6010;
				double f = lv.field_6035;
				double g = arg.method_12279();
				double h = arg.method_12280();
				double i = arg.method_12276();
				float j = arg.method_12281();
				float k = arg.method_12277();
				double l = g - this.field_14143;
				double m = h - this.field_14124;
				double n = i - this.field_14142;
				double o = lv.method_18798().method_1027();
				double p = l * l + m * m + n * n;
				if (p - o > 100.0 && !this.method_19507()) {
					field_14121.warn("{} (vehicle of {}) moved too quickly! {},{},{}", lv.method_5477().getString(), this.field_14140.method_5477().getString(), l, m, n);
					this.field_14127.method_10743(new class_2692(lv));
					return;
				}

				boolean bl = lv2.method_8587(lv, lv.method_5829().method_1011(0.0625));
				l = g - this.field_14122;
				m = h - this.field_14141 - 1.0E-6;
				n = i - this.field_14120;
				lv.method_5784(class_1313.field_6305, new class_243(l, m, n));
				l = g - lv.field_5987;
				m = h - lv.field_6010;
				if (m > -0.5 || m < 0.5) {
					m = 0.0;
				}

				n = i - lv.field_6035;
				p = l * l + m * m + n * n;
				boolean bl2 = false;
				if (p > 0.0625) {
					bl2 = true;
					field_14121.warn("{} moved wrongly!", lv.method_5477().getString());
				}

				lv.method_5641(g, h, i, j, k);
				boolean bl3 = lv2.method_8587(lv, lv.method_5829().method_1011(0.0625));
				if (bl && (bl2 || !bl3)) {
					lv.method_5641(d, e, f, j, k);
					this.field_14127.method_10743(new class_2692(lv));
					return;
				}

				this.field_14140.method_14220().method_14178().method_14096(this.field_14140);
				this.field_14140.method_7282(this.field_14140.field_5987 - d, this.field_14140.field_6010 - e, this.field_14140.field_6035 - f);
				this.field_14129 = m >= -0.03125 && !this.field_14148.method_3718() && !lv2.method_8534(lv.method_5829().method_1014(0.0625).method_1012(0.0, -0.55, 0.0));
				this.field_14122 = lv.field_5987;
				this.field_14141 = lv.field_6010;
				this.field_14120 = lv.field_6035;
			}
		}
	}

	@Override
	public void method_12050(class_2793 arg) {
		class_2600.method_11073(arg, this, this.field_14140.method_14220());
		if (arg.method_12086() == this.field_14123) {
			this.field_14140
				.method_5641(
					this.field_14119.field_1352, this.field_14119.field_1351, this.field_14119.field_1350, this.field_14140.field_6031, this.field_14140.field_5965
				);
			this.field_14145 = this.field_14119.field_1352;
			this.field_14126 = this.field_14119.field_1351;
			this.field_14144 = this.field_14119.field_1350;
			if (this.field_14140.method_14208()) {
				this.field_14140.method_14240();
			}

			this.field_14119 = null;
		}
	}

	@Override
	public void method_12047(class_2853 arg) {
		class_2600.method_11073(arg, this, this.field_14140.method_14220());
		if (arg.method_12402() == class_2853.class_2854.field_13011) {
			this.field_14148.method_3772().method_8130(arg.method_12406()).ifPresent(this.field_14140.method_14253()::method_14886);
		} else if (arg.method_12402() == class_2853.class_2854.field_13010) {
			this.field_14140.method_14253().method_14884(arg.method_12403());
			this.field_14140.method_14253().method_14889(arg.method_12401());
			this.field_14140.method_14253().method_14882(arg.method_12404());
			this.field_14140.method_14253().method_14888(arg.method_12405());
			this.field_14140.method_14253().method_17318(arg.method_17192());
			this.field_14140.method_14253().method_17320(arg.method_17193());
			this.field_14140.method_14253().method_17322(arg.method_17194());
			this.field_14140.method_14253().method_17324(arg.method_17195());
		}
	}

	@Override
	public void method_12058(class_2859 arg) {
		class_2600.method_11073(arg, this, this.field_14140.method_14220());
		if (arg.method_12415() == class_2859.class_2860.field_13024) {
			class_2960 lv = arg.method_12416();
			class_161 lv2 = this.field_14148.method_3851().method_12896(lv);
			if (lv2 != null) {
				this.field_14140.method_14236().method_12888(lv2);
			}
		}
	}

	@Override
	public void method_12059(class_2805 arg) {
		class_2600.method_11073(arg, this, this.field_14140.method_14220());
		StringReader stringReader = new StringReader(arg.method_12148());
		if (stringReader.canRead() && stringReader.peek() == '/') {
			stringReader.skip();
		}

		ParseResults<class_2168> parseResults = this.field_14148.method_3734().method_9235().parse(stringReader, this.field_14140.method_5671());
		this.field_14148
			.method_3734()
			.method_9235()
			.getCompletionSuggestions(parseResults)
			.thenAccept(suggestions -> this.field_14127.method_10743(new class_2639(arg.method_12149(), suggestions)));
	}

	@Override
	public void method_12077(class_2870 arg) {
		class_2600.method_11073(arg, this, this.field_14140.method_14220());
		if (!this.field_14148.method_3812()) {
			this.field_14140.method_9203(new class_2588("advMode.notEnabled"));
		} else if (!this.field_14140.method_7338()) {
			this.field_14140.method_9203(new class_2588("advMode.notAllowed"));
		} else {
			class_1918 lv = null;
			class_2593 lv2 = null;
			class_2338 lv3 = arg.method_12473();
			class_2586 lv4 = this.field_14140.field_6002.method_8321(lv3);
			if (lv4 instanceof class_2593) {
				lv2 = (class_2593)lv4;
				lv = lv2.method_11040();
			}

			String string = arg.method_12470();
			boolean bl = arg.method_12472();
			if (lv != null) {
				class_2350 lv5 = this.field_14140.field_6002.method_8320(lv3).method_11654(class_2288.field_10791);
				switch (arg.method_12468()) {
					case field_11922: {
						class_2680 lv6 = class_2246.field_10395.method_9564();
						this.field_14140
							.field_6002
							.method_8652(lv3, lv6.method_11657(class_2288.field_10791, lv5).method_11657(class_2288.field_10793, Boolean.valueOf(arg.method_12471())), 2);
						break;
					}
					case field_11923: {
						class_2680 lv6 = class_2246.field_10263.method_9564();
						this.field_14140
							.field_6002
							.method_8652(lv3, lv6.method_11657(class_2288.field_10791, lv5).method_11657(class_2288.field_10793, Boolean.valueOf(arg.method_12471())), 2);
						break;
					}
					case field_11924:
					default: {
						class_2680 lv6 = class_2246.field_10525.method_9564();
						this.field_14140
							.field_6002
							.method_8652(lv3, lv6.method_11657(class_2288.field_10791, lv5).method_11657(class_2288.field_10793, Boolean.valueOf(arg.method_12471())), 2);
					}
				}

				lv4.method_10996();
				this.field_14140.field_6002.method_8526(lv3, lv4);
				lv.method_8286(string);
				lv.method_8287(bl);
				if (!bl) {
					lv.method_8291(null);
				}

				lv2.method_11041(arg.method_12474());
				lv.method_8295();
				if (!class_3544.method_15438(string)) {
					this.field_14140.method_9203(new class_2588("advMode.setCommand.success", string));
				}
			}
		}
	}

	@Override
	public void method_12049(class_2871 arg) {
		class_2600.method_11073(arg, this, this.field_14140.method_14220());
		if (!this.field_14148.method_3812()) {
			this.field_14140.method_9203(new class_2588("advMode.notEnabled"));
		} else if (!this.field_14140.method_7338()) {
			this.field_14140.method_9203(new class_2588("advMode.notAllowed"));
		} else {
			class_1918 lv = arg.method_12476(this.field_14140.field_6002);
			if (lv != null) {
				lv.method_8286(arg.method_12475());
				lv.method_8287(arg.method_12478());
				if (!arg.method_12478()) {
					lv.method_8291(null);
				}

				lv.method_8295();
				this.field_14140.method_9203(new class_2588("advMode.setCommand.success", arg.method_12475()));
			}
		}
	}

	@Override
	public void method_12084(class_2838 arg) {
		class_2600.method_11073(arg, this, this.field_14140.method_14220());
		this.field_14140.field_7514.method_7365(arg.method_12293());
		this.field_14140
			.field_13987
			.method_14364(new class_2653(-2, this.field_14140.field_7514.field_7545, this.field_14140.field_7514.method_5438(this.field_14140.field_7514.field_7545)));
		this.field_14140.field_13987.method_14364(new class_2653(-2, arg.method_12293(), this.field_14140.field_7514.method_5438(arg.method_12293())));
		this.field_14140.field_13987.method_14364(new class_2735(this.field_14140.field_7514.field_7545));
	}

	@Override
	public void method_12060(class_2855 arg) {
		class_2600.method_11073(arg, this, this.field_14140.method_14220());
		if (this.field_14140.field_7512 instanceof class_1706) {
			class_1706 lv = (class_1706)this.field_14140.field_7512;
			String string = class_155.method_644(arg.method_12407());
			if (string.length() <= 35) {
				lv.method_7625(string);
			}
		}
	}

	@Override
	public void method_12057(class_2866 arg) {
		class_2600.method_11073(arg, this, this.field_14140.method_14220());
		if (this.field_14140.field_7512 instanceof class_1704) {
			((class_1704)this.field_14140.field_7512).method_17372(arg.method_12436(), arg.method_12435());
		}
	}

	@Override
	public void method_12051(class_2875 arg) {
		class_2600.method_11073(arg, this, this.field_14140.method_14220());
		if (this.field_14140.method_7338()) {
			class_2338 lv = arg.method_12499();
			class_2680 lv2 = this.field_14140.field_6002.method_8320(lv);
			class_2586 lv3 = this.field_14140.field_6002.method_8321(lv);
			if (lv3 instanceof class_2633) {
				class_2633 lv4 = (class_2633)lv3;
				lv4.method_11381(arg.method_12504());
				lv4.method_11343(arg.method_12502());
				lv4.method_11378(arg.method_12496());
				lv4.method_11377(arg.method_12492());
				lv4.method_11356(arg.method_12493());
				lv4.method_11385(arg.method_12498());
				lv4.method_11363(arg.method_12501());
				lv4.method_11352(arg.method_12506());
				lv4.method_11347(arg.method_12503());
				lv4.method_11360(arg.method_12505());
				lv4.method_11370(arg.method_12494());
				lv4.method_11382(arg.method_12497());
				if (lv4.method_11384()) {
					String string = lv4.method_11362();
					if (arg.method_12500() == class_2633.class_2634.field_12110) {
						if (lv4.method_11365()) {
							this.field_14140.method_7353(new class_2588("structure_block.save_success", string), false);
						} else {
							this.field_14140.method_7353(new class_2588("structure_block.save_failure", string), false);
						}
					} else if (arg.method_12500() == class_2633.class_2634.field_12109) {
						if (!lv4.method_11372()) {
							this.field_14140.method_7353(new class_2588("structure_block.load_not_found", string), false);
						} else if (lv4.method_11376()) {
							this.field_14140.method_7353(new class_2588("structure_block.load_success", string), false);
						} else {
							this.field_14140.method_7353(new class_2588("structure_block.load_prepare", string), false);
						}
					} else if (arg.method_12500() == class_2633.class_2634.field_12106) {
						if (lv4.method_11383()) {
							this.field_14140.method_7353(new class_2588("structure_block.size_success", string), false);
						} else {
							this.field_14140.method_7353(new class_2588("structure_block.size_failure"), false);
						}
					}
				} else {
					this.field_14140.method_7353(new class_2588("structure_block.invalid_structure_name", arg.method_12502()), false);
				}

				lv4.method_5431();
				this.field_14140.field_6002.method_8413(lv, lv2, lv2, 3);
			}
		}
	}

	@Override
	public void method_16383(class_3753 arg) {
		class_2600.method_11073(arg, this, this.field_14140.method_14220());
		if (this.field_14140.method_7338()) {
			class_2338 lv = arg.method_16396();
			class_2680 lv2 = this.field_14140.field_6002.method_8320(lv);
			class_2586 lv3 = this.field_14140.field_6002.method_8321(lv);
			if (lv3 instanceof class_3751) {
				class_3751 lv4 = (class_3751)lv3;
				lv4.method_16379(arg.method_16395());
				lv4.method_16378(arg.method_16394());
				lv4.method_16377(arg.method_16393());
				lv4.method_5431();
				this.field_14140.field_6002.method_8413(lv, lv2, lv2, 3);
			}
		}
	}

	@Override
	public void method_12080(class_2863 arg) {
		class_2600.method_11073(arg, this, this.field_14140.method_14220());
		int i = arg.method_12431();
		class_1703 lv = this.field_14140.field_7512;
		if (lv instanceof class_1728) {
			class_1728 lv2 = (class_1728)lv;
			lv2.method_7650(i);
			lv2.method_20215(i);
		}
	}

	@Override
	public void method_12053(class_2820 arg) {
		class_1799 lv = arg.method_12237();
		if (!lv.method_7960()) {
			if (class_1840.method_8047(lv.method_7969())) {
				class_1799 lv2 = this.field_14140.method_5998(arg.method_12235());
				if (lv.method_7909() == class_1802.field_8674 && lv2.method_7909() == class_1802.field_8674) {
					if (arg.method_12238()) {
						class_1799 lv3 = new class_1799(class_1802.field_8360);
						class_2487 lv4 = lv2.method_7969();
						if (lv4 != null) {
							lv3.method_7980(lv4.method_10553());
						}

						lv3.method_7959("author", new class_2519(this.field_14140.method_5477().getString()));
						lv3.method_7959("title", new class_2519(lv.method_7969().method_10558("title")));
						class_2499 lv5 = lv.method_7969().method_10554("pages", 8);

						for (int i = 0; i < lv5.size(); i++) {
							String string = lv5.method_10608(i);
							class_2561 lv6 = new class_2585(string);
							string = class_2561.class_2562.method_10867(lv6);
							lv5.method_10606(i, new class_2519(string));
						}

						lv3.method_7959("pages", lv5);
						this.field_14140.method_6122(arg.method_12235(), lv3);
					} else {
						lv2.method_7959("pages", lv.method_7969().method_10554("pages", 8));
					}
				}
			}
		}
	}

	@Override
	public void method_12074(class_2822 arg) {
		class_2600.method_11073(arg, this, this.field_14140.method_14220());
		if (this.field_14140.method_5687(2)) {
			class_1297 lv = this.field_14140.method_14220().method_8469(arg.method_12244());
			if (lv != null) {
				class_2487 lv2 = lv.method_5647(new class_2487());
				this.field_14140.field_13987.method_14364(new class_2774(arg.method_12245(), lv2));
			}
		}
	}

	@Override
	public void method_12072(class_2795 arg) {
		class_2600.method_11073(arg, this, this.field_14140.method_14220());
		if (this.field_14140.method_5687(2)) {
			class_2586 lv = this.field_14140.method_14220().method_8321(arg.method_12094());
			class_2487 lv2 = lv != null ? lv.method_11007(new class_2487()) : null;
			this.field_14140.field_13987.method_14364(new class_2774(arg.method_12096(), lv2));
		}
	}

	@Override
	public void method_12063(class_2828 arg) {
		class_2600.method_11073(arg, this, this.field_14140.method_14220());
		if (method_14362(arg)) {
			this.method_14367(new class_2588("multiplayer.disconnect.invalid_player_movement"));
		} else {
			class_3218 lv = this.field_14148.method_3847(this.field_14140.field_6026);
			if (!this.field_14140.field_13989) {
				if (this.field_14118 == 0) {
					this.method_14372();
				}

				if (this.field_14119 != null) {
					if (this.field_14118 - this.field_14139 > 20) {
						this.field_14139 = this.field_14118;
						this.method_14363(
							this.field_14119.field_1352, this.field_14119.field_1351, this.field_14119.field_1350, this.field_14140.field_6031, this.field_14140.field_5965
						);
					}
				} else {
					this.field_14139 = this.field_14118;
					if (this.field_14140.method_5765()) {
						this.field_14140
							.method_5641(
								this.field_14140.field_5987,
								this.field_14140.field_6010,
								this.field_14140.field_6035,
								arg.method_12271(this.field_14140.field_6031),
								arg.method_12270(this.field_14140.field_5965)
							);
						this.field_14140.method_14220().method_14178().method_14096(this.field_14140);
					} else {
						double d = this.field_14140.field_5987;
						double e = this.field_14140.field_6010;
						double f = this.field_14140.field_6035;
						double g = this.field_14140.field_6010;
						double h = arg.method_12269(this.field_14140.field_5987);
						double i = arg.method_12268(this.field_14140.field_6010);
						double j = arg.method_12274(this.field_14140.field_6035);
						float k = arg.method_12271(this.field_14140.field_6031);
						float l = arg.method_12270(this.field_14140.field_5965);
						double m = h - this.field_14130;
						double n = i - this.field_14146;
						double o = j - this.field_14128;
						double p = this.field_14140.method_18798().method_1027();
						double q = m * m + n * n + o * o;
						if (this.field_14140.method_6113()) {
							if (q > 1.0) {
								this.method_14363(
									this.field_14140.field_5987,
									this.field_14140.field_6010,
									this.field_14140.field_6035,
									arg.method_12271(this.field_14140.field_6031),
									arg.method_12270(this.field_14140.field_5965)
								);
							}
						} else {
							this.field_14117++;
							int r = this.field_14117 - this.field_14135;
							if (r > 5) {
								field_14121.debug("{} is sending move packets too frequently ({} packets since last tick)", this.field_14140.method_5477().getString(), r);
								r = 1;
							}

							if (!this.field_14140.method_14208()
								&& (!this.field_14140.method_14220().method_8450().method_8355(class_1928.field_19404) || !this.field_14140.method_6128())) {
								float s = this.field_14140.method_6128() ? 300.0F : 100.0F;
								if (q - p > (double)(s * (float)r) && !this.method_19507()) {
									field_14121.warn("{} moved too quickly! {},{},{}", this.field_14140.method_5477().getString(), m, n, o);
									this.method_14363(
										this.field_14140.field_5987, this.field_14140.field_6010, this.field_14140.field_6035, this.field_14140.field_6031, this.field_14140.field_5965
									);
									return;
								}
							}

							boolean bl = this.method_20630(lv);
							m = h - this.field_14145;
							n = i - this.field_14126;
							o = j - this.field_14144;
							if (this.field_14140.field_5952 && !arg.method_12273() && n > 0.0) {
								this.field_14140.method_6043();
							}

							this.field_14140.method_5784(class_1313.field_6305, new class_243(m, n, o));
							this.field_14140.field_5952 = arg.method_12273();
							m = h - this.field_14140.field_5987;
							n = i - this.field_14140.field_6010;
							if (n > -0.5 || n < 0.5) {
								n = 0.0;
							}

							o = j - this.field_14140.field_6035;
							q = m * m + n * n + o * o;
							boolean bl2 = false;
							if (!this.field_14140.method_14208()
								&& q > 0.0625
								&& !this.field_14140.method_6113()
								&& !this.field_14140.field_13974.method_14268()
								&& this.field_14140.field_13974.method_14257() != class_1934.field_9219) {
								bl2 = true;
								field_14121.warn("{} moved wrongly!", this.field_14140.method_5477().getString());
							}

							this.field_14140.method_5641(h, i, j, k, l);
							this.field_14140.method_7282(this.field_14140.field_5987 - d, this.field_14140.field_6010 - e, this.field_14140.field_6035 - f);
							if (!this.field_14140.field_5960 && !this.field_14140.method_6113()) {
								boolean bl3 = this.method_20630(lv);
								if (bl && (bl2 || !bl3)) {
									this.method_14363(d, e, f, k, l);
									return;
								}
							}

							this.field_14131 = n >= -0.03125
								&& this.field_14140.field_13974.method_14257() != class_1934.field_9219
								&& !this.field_14148.method_3718()
								&& !this.field_14140.field_7503.field_7478
								&& !this.field_14140.method_6059(class_1294.field_5902)
								&& !this.field_14140.method_6128()
								&& !lv.method_8534(this.field_14140.method_5829().method_1014(0.0625).method_1012(0.0, -0.55, 0.0));
							this.field_14140.field_5952 = arg.method_12273();
							this.field_14140.method_14220().method_14178().method_14096(this.field_14140);
							this.field_14140.method_14207(this.field_14140.field_6010 - g, arg.method_12273());
							this.field_14145 = this.field_14140.field_5987;
							this.field_14126 = this.field_14140.field_6010;
							this.field_14144 = this.field_14140.field_6035;
						}
					}
				}
			}
		}
	}

	private boolean method_20630(class_1941 arg) {
		return arg.method_8587(this.field_14140, this.field_14140.method_5829().method_1011(1.0E-5F));
	}

	public void method_14363(double d, double e, double f, float g, float h) {
		this.method_14360(d, e, f, g, h, Collections.emptySet());
	}

	public void method_14360(double d, double e, double f, float g, float h, Set<class_2708.class_2709> set) {
		double i = set.contains(class_2708.class_2709.field_12400) ? this.field_14140.field_5987 : 0.0;
		double j = set.contains(class_2708.class_2709.field_12398) ? this.field_14140.field_6010 : 0.0;
		double k = set.contains(class_2708.class_2709.field_12403) ? this.field_14140.field_6035 : 0.0;
		float l = set.contains(class_2708.class_2709.field_12401) ? this.field_14140.field_6031 : 0.0F;
		float m = set.contains(class_2708.class_2709.field_12397) ? this.field_14140.field_5965 : 0.0F;
		this.field_14119 = new class_243(d, e, f);
		if (++this.field_14123 == Integer.MAX_VALUE) {
			this.field_14123 = 0;
		}

		this.field_14139 = this.field_14118;
		this.field_14140.method_5641(d, e, f, g, h);
		this.field_14140.field_13987.method_14364(new class_2708(d - i, e - j, f - k, g - l, h - m, set, this.field_14123));
	}

	@Override
	public void method_12066(class_2846 arg) {
		class_2600.method_11073(arg, this, this.field_14140.method_14220());
		class_3218 lv = this.field_14148.method_3847(this.field_14140.field_6026);
		class_2338 lv2 = arg.method_12362();
		this.field_14140.method_14234();
		switch (arg.method_12363()) {
			case field_12969:
				if (!this.field_14140.method_7325()) {
					class_1799 lv3 = this.field_14140.method_5998(class_1268.field_5810);
					this.field_14140.method_6122(class_1268.field_5810, this.field_14140.method_5998(class_1268.field_5808));
					this.field_14140.method_6122(class_1268.field_5808, lv3);
				}

				return;
			case field_12975:
				if (!this.field_14140.method_7325()) {
					this.field_14140.method_7290(false);
				}

				return;
			case field_12970:
				if (!this.field_14140.method_7325()) {
					this.field_14140.method_7290(true);
				}

				return;
			case field_12974:
				this.field_14140.method_6075();
				return;
			case field_12968:
			case field_12971:
			case field_12973:
				double d = this.field_14140.field_5987 - ((double)lv2.method_10263() + 0.5);
				double e = this.field_14140.field_6010 - ((double)lv2.method_10264() + 0.5) + 1.5;
				double f = this.field_14140.field_6035 - ((double)lv2.method_10260() + 0.5);
				double g = d * d + e * e + f * f;
				if (g > 36.0) {
					return;
				} else if (lv2.method_10264() >= this.field_14148.method_3833()) {
					return;
				} else {
					if (arg.method_12363() == class_2846.class_2847.field_12968) {
						if (!this.field_14148.method_3785(lv, lv2, this.field_14140) && lv.method_8621().method_11952(lv2)) {
							this.field_14140.field_13974.method_14263(lv2, arg.method_12360());
						} else {
							this.field_14140.field_13987.method_14364(new class_2626(lv, lv2));
						}
					} else {
						if (arg.method_12363() == class_2846.class_2847.field_12973) {
							this.field_14140.field_13974.method_14258(lv2);
						} else if (arg.method_12363() == class_2846.class_2847.field_12971) {
							this.field_14140.field_13974.method_14269();
						}

						if (!lv.method_8320(lv2).method_11588()) {
							this.field_14140.field_13987.method_14364(new class_2626(lv, lv2));
						}
					}

					return;
				}
			default:
				throw new IllegalArgumentException("Invalid player action");
		}
	}

	@Override
	public void method_12046(class_2885 arg) {
		class_2600.method_11073(arg, this, this.field_14140.method_14220());
		class_3218 lv = this.field_14148.method_3847(this.field_14140.field_6026);
		class_1268 lv2 = arg.method_12546();
		class_1799 lv3 = this.field_14140.method_5998(lv2);
		class_3965 lv4 = arg.method_12543();
		class_2338 lv5 = lv4.method_17777();
		class_2350 lv6 = lv4.method_17780();
		this.field_14140.method_14234();
		if (lv5.method_10264() < this.field_14148.method_3833() - 1 || lv6 != class_2350.field_11036 && lv5.method_10264() < this.field_14148.method_3833()) {
			if (this.field_14119 == null
				&& this.field_14140.method_5649((double)lv5.method_10263() + 0.5, (double)lv5.method_10264() + 0.5, (double)lv5.method_10260() + 0.5) < 64.0
				&& !this.field_14148.method_3785(lv, lv5, this.field_14140)
				&& lv.method_8621().method_11952(lv5)) {
				this.field_14140.field_13974.method_14262(this.field_14140, lv, lv3, lv2, lv4);
			}
		} else {
			class_2561 lv7 = new class_2588("build.tooHigh", this.field_14148.method_3833()).method_10854(class_124.field_1061);
			this.field_14140.field_13987.method_14364(new class_2635(lv7, class_2556.field_11733));
		}

		this.field_14140.field_13987.method_14364(new class_2626(lv, lv5));
		this.field_14140.field_13987.method_14364(new class_2626(lv, lv5.method_10093(lv6)));
	}

	@Override
	public void method_12065(class_2886 arg) {
		class_2600.method_11073(arg, this, this.field_14140.method_14220());
		class_3218 lv = this.field_14148.method_3847(this.field_14140.field_6026);
		class_1268 lv2 = arg.method_12551();
		class_1799 lv3 = this.field_14140.method_5998(lv2);
		this.field_14140.method_14234();
		if (!lv3.method_7960()) {
			this.field_14140.field_13974.method_14256(this.field_14140, lv, lv3, lv2);
		}
	}

	@Override
	public void method_12073(class_2884 arg) {
		class_2600.method_11073(arg, this, this.field_14140.method_14220());
		if (this.field_14140.method_7325()) {
			for (class_3218 lv : this.field_14148.method_3738()) {
				class_1297 lv2 = arg.method_12541(lv);
				if (lv2 != null) {
					this.field_14140.method_14251(lv, lv2.field_5987, lv2.field_6010, lv2.field_6035, lv2.field_6031, lv2.field_5965);
					return;
				}
			}
		}
	}

	@Override
	public void method_12081(class_2856 arg) {
	}

	@Override
	public void method_12064(class_2836 arg) {
		class_2600.method_11073(arg, this, this.field_14140.method_14220());
		class_1297 lv = this.field_14140.method_5854();
		if (lv instanceof class_1690) {
			((class_1690)lv).method_7538(arg.method_12284(), arg.method_12285());
		}
	}

	@Override
	public void method_10839(class_2561 arg) {
		field_14121.info("{} lost connection: {}", this.field_14140.method_5477().getString(), arg.getString());
		this.field_14148.method_3856();
		this.field_14148.method_3760().method_14593(new class_2588("multiplayer.player.left", this.field_14140.method_5476()).method_10854(class_124.field_1054));
		this.field_14140.method_14231();
		this.field_14148.method_3760().method_14611(this.field_14140);
		if (this.method_19507()) {
			field_14121.info("Stopping singleplayer server as player logged out");
			this.field_14148.method_3747(false);
		}
	}

	public void method_14364(class_2596<?> arg) {
		this.method_14369(arg, null);
	}

	public void method_14369(class_2596<?> arg, @Nullable GenericFutureListener<? extends Future<? super Void>> genericFutureListener) {
		if (arg instanceof class_2635) {
			class_2635 lv = (class_2635)arg;
			class_1659 lv2 = this.field_14140.method_14238();
			if (lv2 == class_1659.field_7536 && lv.method_11389() != class_2556.field_11733) {
				return;
			}

			if (lv2 == class_1659.field_7539 && !lv.method_11387()) {
				return;
			}
		}

		try {
			this.field_14127.method_10752(arg, genericFutureListener);
		} catch (Throwable var6) {
			class_128 lv3 = class_128.method_560(var6, "Sending packet");
			class_129 lv4 = lv3.method_562("Packet being sent");
			lv4.method_577("Packet class", () -> arg.getClass().getCanonicalName());
			throw new class_148(lv3);
		}
	}

	@Override
	public void method_12056(class_2868 arg) {
		class_2600.method_11073(arg, this, this.field_14140.method_14220());
		if (arg.method_12442() >= 0 && arg.method_12442() < class_1661.method_7368()) {
			this.field_14140.field_7514.field_7545 = arg.method_12442();
			this.field_14140.method_14234();
		} else {
			field_14121.warn("{} tried to set an invalid carried item", this.field_14140.method_5477().getString());
		}
	}

	@Override
	public void method_12048(class_2797 arg) {
		class_2600.method_11073(arg, this, this.field_14140.method_14220());
		if (this.field_14140.method_14238() == class_1659.field_7536) {
			this.method_14364(new class_2635(new class_2588("chat.cannotSend").method_10854(class_124.field_1061)));
		} else {
			this.field_14140.method_14234();
			String string = arg.method_12114();
			string = StringUtils.normalizeSpace(string);

			for (int i = 0; i < string.length(); i++) {
				if (!class_155.method_643(string.charAt(i))) {
					this.method_14367(new class_2588("multiplayer.disconnect.illegal_characters"));
					return;
				}
			}

			if (string.startsWith("/")) {
				this.method_14370(string);
			} else {
				class_2561 lv = new class_2588("chat.type.text", this.field_14140.method_5476(), string);
				this.field_14148.method_3760().method_14616(lv, false);
			}

			this.field_14116 += 20;
			if (this.field_14116 > 200 && !this.field_14148.method_3760().method_14569(this.field_14140.method_7334())) {
				this.method_14367(new class_2588("disconnect.spam"));
			}
		}
	}

	private void method_14370(String string) {
		this.field_14148.method_3734().method_9249(this.field_14140.method_5671(), string);
	}

	@Override
	public void method_12052(class_2879 arg) {
		class_2600.method_11073(arg, this, this.field_14140.method_14220());
		this.field_14140.method_14234();
		this.field_14140.method_6104(arg.method_12512());
	}

	@Override
	public void method_12045(class_2848 arg) {
		class_2600.method_11073(arg, this, this.field_14140.method_14220());
		this.field_14140.method_14234();
		switch (arg.method_12365()) {
			case field_12979:
				this.field_14140.method_5660(true);
				break;
			case field_12984:
				this.field_14140.method_5660(false);
				break;
			case field_12981:
				this.field_14140.method_5728(true);
				break;
			case field_12985:
				this.field_14140.method_5728(false);
				break;
			case field_12986:
				if (this.field_14140.method_6113()) {
					this.field_14140.method_7358(false, true, true);
					this.field_14119 = new class_243(this.field_14140.field_5987, this.field_14140.field_6010, this.field_14140.field_6035);
				}
				break;
			case field_12987:
				if (this.field_14140.method_5854() instanceof class_1316) {
					class_1316 lv = (class_1316)this.field_14140.method_5854();
					int i = arg.method_12366();
					if (lv.method_6153() && i > 0) {
						lv.method_6155(i);
					}
				}
				break;
			case field_12980:
				if (this.field_14140.method_5854() instanceof class_1316) {
					class_1316 lv = (class_1316)this.field_14140.method_5854();
					lv.method_6156();
				}
				break;
			case field_12988:
				if (this.field_14140.method_5854() instanceof class_1496) {
					((class_1496)this.field_14140.method_5854()).method_6722(this.field_14140);
				}
				break;
			case field_12982:
				if (!this.field_14140.field_5952 && this.field_14140.method_18798().field_1351 < 0.0 && !this.field_14140.method_6128() && !this.field_14140.method_5799()) {
					class_1799 lv2 = this.field_14140.method_6118(class_1304.field_6174);
					if (lv2.method_7909() == class_1802.field_8833 && class_1770.method_7804(lv2)) {
						this.field_14140.method_14243();
					}
				} else {
					this.field_14140.method_14229();
				}
				break;
			default:
				throw new IllegalArgumentException("Invalid client command!");
		}
	}

	@Override
	public void method_12062(class_2824 arg) {
		class_2600.method_11073(arg, this, this.field_14140.method_14220());
		class_3218 lv = this.field_14148.method_3847(this.field_14140.field_6026);
		class_1297 lv2 = arg.method_12248(lv);
		this.field_14140.method_14234();
		if (lv2 != null) {
			boolean bl = this.field_14140.method_6057(lv2);
			float f = this.field_14140.method_26738(1.0F) + 0.25F + lv2.method_17681() * 0.5F;
			double d = (double)(f * f);
			if (!bl) {
				d = 9.0;
			}

			if (this.field_14140.method_5858(lv2) < d) {
				if (arg.method_12252() == class_2824.class_2825.field_12876) {
					class_1268 lv3 = arg.method_12249();
					this.field_14140.method_7287(lv2, lv3);
				} else if (arg.method_12252() == class_2824.class_2825.field_12873) {
					class_1268 lv3 = arg.method_12249();
					lv2.method_5664(this.field_14140, arg.method_12250(), lv3);
				} else if (arg.method_12252() == class_2824.class_2825.field_12875) {
					if (lv2 instanceof class_1542 || lv2 instanceof class_1303 || lv2 instanceof class_1665 || lv2 == this.field_14140) {
						this.method_14367(new class_2588("multiplayer.disconnect.invalid_entity_attacked"));
						this.field_14148.method_3743("Player " + this.field_14140.method_5477().getString() + " tried to attack an invalid entity");
						return;
					}

					this.field_14140.method_7324(lv2);
				}
			}
		} else {
			this.field_14140.method_26735();
		}
	}

	@Override
	public void method_12068(class_2799 arg) {
		class_2600.method_11073(arg, this, this.field_14140.method_14220());
		this.field_14140.method_14234();
		class_2799.class_2800 lv = arg.method_12119();
		switch (lv) {
			case field_12774:
				if (this.field_14140.field_13989) {
					this.field_14140.field_13989 = false;
					this.field_14140 = this.field_14148.method_3760().method_14556(this.field_14140, class_2874.field_13072, true);
					class_174.field_1183.method_8794(this.field_14140, class_2874.field_13078, class_2874.field_13072);
				} else {
					if (this.field_14140.method_6032() > 0.0F) {
						return;
					}

					this.field_14140 = this.field_14148.method_3760().method_14556(this.field_14140, class_2874.field_13072, false);
					if (this.field_14148.method_3754()) {
						this.field_14140.method_7336(class_1934.field_9219);
						this.field_14140.method_14220().method_8450().method_20746(class_1928.field_19402).method_20758(false, this.field_14148);
					}
				}
				break;
			case field_12775:
				this.field_14140.method_14248().method_14910(this.field_14140);
		}
	}

	@Override
	public void method_12054(class_2815 arg) {
		class_2600.method_11073(arg, this, this.field_14140.method_14220());
		this.field_14140.method_14247();
	}

	@Override
	public void method_12076(class_2813 arg) {
		class_2600.method_11073(arg, this, this.field_14140.method_14220());
		this.field_14140.method_14234();
		if (this.field_14140.field_7512.field_7763 == arg.method_12194() && this.field_14140.field_7512.method_7622(this.field_14140)) {
			if (this.field_14140.method_7325()) {
				class_2371<class_1799> lv = class_2371.method_10211();

				for (int i = 0; i < this.field_14140.field_7512.field_7761.size(); i++) {
					lv.add(((class_1735)this.field_14140.field_7512.field_7761.get(i)).method_7677());
				}

				this.field_14140.method_7634(this.field_14140.field_7512, lv);
			} else {
				class_1799 lv2 = this.field_14140.field_7512.method_7593(arg.method_12192(), arg.method_12193(), arg.method_12195(), this.field_14140);
				if (class_1799.method_7973(arg.method_12190(), lv2)) {
					this.field_14140.field_13987.method_14364(new class_2644(arg.method_12194(), arg.method_12189(), true));
					this.field_14140.field_13991 = true;
					this.field_14140.field_7512.method_7623();
					this.field_14140.method_14241();
					this.field_14140.field_13991 = false;
				} else {
					this.field_14132.put(this.field_14140.field_7512.field_7763, arg.method_12189());
					this.field_14140.field_13987.method_14364(new class_2644(arg.method_12194(), arg.method_12189(), false));
					this.field_14140.field_7512.method_7590(this.field_14140, false);
					class_2371<class_1799> lv3 = class_2371.method_10211();

					for (int j = 0; j < this.field_14140.field_7512.field_7761.size(); j++) {
						class_1799 lv4 = ((class_1735)this.field_14140.field_7512.field_7761.get(j)).method_7677();
						lv3.add(lv4.method_7960() ? class_1799.field_8037 : lv4);
					}

					this.field_14140.method_7634(this.field_14140.field_7512, lv3);
				}
			}
		}
	}

	@Override
	public void method_12061(class_2840 arg) {
		class_2600.method_11073(arg, this, this.field_14140.method_14220());
		this.field_14140.method_14234();
		if (!this.field_14140.method_7325()
			&& this.field_14140.field_7512.field_7763 == arg.method_12318()
			&& this.field_14140.field_7512.method_7622(this.field_14140)
			&& this.field_14140.field_7512 instanceof class_1729) {
			this.field_14148
				.method_3772()
				.method_8130(arg.method_12320())
				.ifPresent(arg2 -> ((class_1729)this.field_14140.field_7512).method_17697(arg.method_12319(), arg2, this.field_14140));
		}
	}

	@Override
	public void method_12055(class_2811 arg) {
		class_2600.method_11073(arg, this, this.field_14140.method_14220());
		this.field_14140.method_14234();
		if (this.field_14140.field_7512.field_7763 == arg.method_12187()
			&& this.field_14140.field_7512.method_7622(this.field_14140)
			&& !this.field_14140.method_7325()) {
			this.field_14140.field_7512.method_7604(this.field_14140, arg.method_12186());
			this.field_14140.field_7512.method_7623();
		}
	}

	@Override
	public void method_12070(class_2873 arg) {
		class_2600.method_11073(arg, this, this.field_14140.method_14220());
		if (this.field_14140.field_13974.method_14268()) {
			boolean bl = arg.method_12481() < 0;
			class_1799 lv = arg.method_12479();
			class_2487 lv2 = lv.method_7941("BlockEntityTag");
			if (!lv.method_7960() && lv2 != null && lv2.method_10545("x") && lv2.method_10545("y") && lv2.method_10545("z")) {
				class_2338 lv3 = new class_2338(lv2.method_10550("x"), lv2.method_10550("y"), lv2.method_10550("z"));
				class_2586 lv4 = this.field_14140.field_6002.method_8321(lv3);
				if (lv4 != null) {
					class_2487 lv5 = lv4.method_11007(new class_2487());
					lv5.method_10551("x");
					lv5.method_10551("y");
					lv5.method_10551("z");
					lv.method_7959("BlockEntityTag", lv5);
				}
			}

			boolean bl2 = arg.method_12481() >= 1 && arg.method_12481() <= 45;
			boolean bl3 = lv.method_7960() || lv.method_7919() >= 0 && lv.method_7947() <= 64 && !lv.method_7960();
			if (bl2 && bl3) {
				if (lv.method_7960()) {
					this.field_14140.field_7498.method_7619(arg.method_12481(), class_1799.field_8037);
				} else {
					this.field_14140.field_7498.method_7619(arg.method_12481(), lv);
				}

				this.field_14140.field_7498.method_7590(this.field_14140, true);
				this.field_14140.field_7498.method_7623();
			} else if (bl && bl3 && this.field_14133 < 200) {
				this.field_14133 += 20;
				class_1542 lv6 = this.field_14140.method_7328(lv, true);
				if (lv6 != null) {
					lv6.method_6980();
				}
			}
		}
	}

	@Override
	public void method_12079(class_2809 arg) {
		class_2600.method_11073(arg, this, this.field_14140.method_14220());
		int i = this.field_14140.field_7512.field_7763;
		if (i == arg.method_12178()
			&& this.field_14132.getOrDefault(i, (short)(arg.method_12176() + 1)) == arg.method_12176()
			&& !this.field_14140.field_7512.method_7622(this.field_14140)
			&& !this.field_14140.method_7325()) {
			this.field_14140.field_7512.method_7590(this.field_14140, true);
		}
	}

	@Override
	public void method_12071(class_2877 arg) {
		class_2600.method_11073(arg, this, this.field_14140.method_14220());
		this.field_14140.method_14234();
		class_3218 lv = this.field_14148.method_3847(this.field_14140.field_6026);
		class_2338 lv2 = arg.method_12510();
		if (lv.method_8591(lv2)) {
			class_2680 lv3 = lv.method_8320(lv2);
			class_2586 lv4 = lv.method_8321(lv2);
			if (!(lv4 instanceof class_2625)) {
				return;
			}

			class_2625 lv5 = (class_2625)lv4;
			if (!lv5.method_11307() || lv5.method_11305() != this.field_14140) {
				this.field_14148.method_3743("Player " + this.field_14140.method_5477().getString() + " just tried to change non-editable sign");
				return;
			}

			String[] strings = arg.method_12508();

			for (int i = 0; i < strings.length; i++) {
				lv5.method_11299(i, new class_2585(class_124.method_539(strings[i])));
			}

			lv5.method_5431();
			lv.method_8413(lv2, lv3, lv3, 3);
		}
	}

	@Override
	public void method_12082(class_2827 arg) {
		if (this.field_14125 && arg.method_12267() == this.field_14134) {
			int i = (int)(class_156.method_658() - this.field_14136);
			this.field_14140.field_13967 = (this.field_14140.field_13967 * 3 + i) / 4;
			this.field_14125 = false;
		} else if (!this.method_19507()) {
			this.method_14367(new class_2588("disconnect.timeout"));
		}
	}

	@Override
	public void method_12083(class_2842 arg) {
		class_2600.method_11073(arg, this, this.field_14140.method_14220());
		this.field_14140.field_7503.field_7479 = arg.method_12346() && this.field_14140.field_7503.field_7478;
	}

	@Override
	public void method_12069(class_2803 arg) {
		class_2600.method_11073(arg, this, this.field_14140.method_14220());
		this.field_14140.method_14213(arg);
	}

	@Override
	public void method_12075(class_2817 arg) {
	}

	@Override
	public void method_19475(class_4210 arg) {
		class_2600.method_11073(arg, this, this.field_14140.method_14220());
		if (this.field_14140.method_5687(2) || this.method_19507()) {
			this.field_14148.method_3776(arg.method_19478(), false);
		}
	}

	@Override
	public void method_19476(class_4211 arg) {
		class_2600.method_11073(arg, this, this.field_14140.method_14220());
		if (this.field_14140.method_5687(2) || this.method_19507()) {
			this.field_14148.method_19467(arg.method_19485());
		}
	}
}
