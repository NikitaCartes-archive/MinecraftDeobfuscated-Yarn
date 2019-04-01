package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_1295 extends class_1297 {
	private static final Logger field_5935 = LogManager.getLogger();
	private static final class_2940<Float> field_5938 = class_2945.method_12791(class_1295.class, class_2943.field_13320);
	private static final class_2940<Integer> field_5936 = class_2945.method_12791(class_1295.class, class_2943.field_13327);
	private static final class_2940<Boolean> field_5944 = class_2945.method_12791(class_1295.class, class_2943.field_13323);
	private static final class_2940<class_2394> field_5931 = class_2945.method_12791(class_1295.class, class_2943.field_13314);
	private class_1842 field_5933 = class_1847.field_8984;
	private final List<class_1293> field_5934 = Lists.<class_1293>newArrayList();
	private final Map<class_1297, Integer> field_5942 = Maps.<class_1297, Integer>newHashMap();
	private int field_5939 = 600;
	private int field_5941 = 20;
	private int field_5937 = 20;
	private boolean field_5928;
	private int field_5932;
	private float field_5929;
	private float field_5930;
	private class_1309 field_5943;
	private UUID field_5940;

	public class_1295(class_1299<? extends class_1295> arg, class_1937 arg2) {
		super(arg, arg2);
		this.field_5960 = true;
		this.method_5603(3.0F);
	}

	public class_1295(class_1937 arg, double d, double e, double f) {
		this(class_1299.field_6083, arg);
		this.method_5814(d, e, f);
	}

	@Override
	protected void method_5693() {
		this.method_5841().method_12784(field_5936, 0);
		this.method_5841().method_12784(field_5938, 0.5F);
		this.method_5841().method_12784(field_5944, false);
		this.method_5841().method_12784(field_5931, class_2398.field_11226);
	}

	public void method_5603(float f) {
		if (!this.field_6002.field_9236) {
			this.method_5841().method_12778(field_5938, f);
		}
	}

	@Override
	public void method_18382() {
		double d = this.field_5987;
		double e = this.field_6010;
		double f = this.field_6035;
		super.method_18382();
		this.method_5814(d, e, f);
	}

	public float method_5599() {
		return this.method_5841().method_12789(field_5938);
	}

	public void method_5612(class_1842 arg) {
		this.field_5933 = arg;
		if (!this.field_5928) {
			this.method_5597();
		}
	}

	private void method_5597() {
		if (this.field_5933 == class_1847.field_8984 && this.field_5934.isEmpty()) {
			this.method_5841().method_12778(field_5936, 0);
		} else {
			this.method_5841().method_12778(field_5936, class_1844.method_8055(class_1844.method_8059(this.field_5933, this.field_5934)));
		}
	}

	public void method_5610(class_1293 arg) {
		this.field_5934.add(arg);
		if (!this.field_5928) {
			this.method_5597();
		}
	}

	public int method_5606() {
		return this.method_5841().method_12789(field_5936);
	}

	public void method_5602(int i) {
		this.field_5928 = true;
		this.method_5841().method_12778(field_5936, i);
	}

	public class_2394 method_5600() {
		return this.method_5841().method_12789(field_5931);
	}

	public void method_5608(class_2394 arg) {
		this.method_5841().method_12778(field_5931, arg);
	}

	protected void method_5598(boolean bl) {
		this.method_5841().method_12778(field_5944, bl);
	}

	public boolean method_5611() {
		return this.method_5841().method_12789(field_5944);
	}

	public int method_5605() {
		return this.field_5939;
	}

	public void method_5604(int i) {
		this.field_5939 = i;
	}

	@Override
	public void method_5773() {
		super.method_5773();
		boolean bl = this.method_5611();
		float f = this.method_5599();
		if (this.field_6002.field_9236) {
			class_2394 lv = this.method_5600();
			if (bl) {
				if (this.field_5974.nextBoolean()) {
					for (int i = 0; i < 2; i++) {
						float g = this.field_5974.nextFloat() * (float) (Math.PI * 2);
						float h = class_3532.method_15355(this.field_5974.nextFloat()) * 0.2F;
						float j = class_3532.method_15362(g) * h;
						float k = class_3532.method_15374(g) * h;
						if (lv.method_10295() == class_2398.field_11226) {
							int l = this.field_5974.nextBoolean() ? 16777215 : this.method_5606();
							int m = l >> 16 & 0xFF;
							int n = l >> 8 & 0xFF;
							int o = l & 0xFF;
							this.field_6002
								.method_8494(
									lv,
									this.field_5987 + (double)j,
									this.field_6010,
									this.field_6035 + (double)k,
									(double)((float)m / 255.0F),
									(double)((float)n / 255.0F),
									(double)((float)o / 255.0F)
								);
						} else {
							this.field_6002.method_8494(lv, this.field_5987 + (double)j, this.field_6010, this.field_6035 + (double)k, 0.0, 0.0, 0.0);
						}
					}
				}
			} else {
				float p = (float) Math.PI * f * f;

				for (int q = 0; (float)q < p; q++) {
					float h = this.field_5974.nextFloat() * (float) (Math.PI * 2);
					float j = class_3532.method_15355(this.field_5974.nextFloat()) * f;
					float k = class_3532.method_15362(h) * j;
					float r = class_3532.method_15374(h) * j;
					if (lv.method_10295() == class_2398.field_11226) {
						int m = this.method_5606();
						int n = m >> 16 & 0xFF;
						int o = m >> 8 & 0xFF;
						int s = m & 0xFF;
						this.field_6002
							.method_8494(
								lv,
								this.field_5987 + (double)k,
								this.field_6010,
								this.field_6035 + (double)r,
								(double)((float)n / 255.0F),
								(double)((float)o / 255.0F),
								(double)((float)s / 255.0F)
							);
					} else {
						this.field_6002
							.method_8494(
								lv,
								this.field_5987 + (double)k,
								this.field_6010,
								this.field_6035 + (double)r,
								(0.5 - this.field_5974.nextDouble()) * 0.15,
								0.01F,
								(0.5 - this.field_5974.nextDouble()) * 0.15
							);
					}
				}
			}
		} else {
			if (this.field_6012 >= this.field_5941 + this.field_5939) {
				this.method_5650();
				return;
			}

			boolean bl2 = this.field_6012 < this.field_5941;
			if (bl != bl2) {
				this.method_5598(bl2);
			}

			if (bl2) {
				return;
			}

			if (this.field_5930 != 0.0F) {
				f += this.field_5930;
				if (f < 0.5F) {
					this.method_5650();
					return;
				}

				this.method_5603(f);
			}

			if (this.field_6012 % 5 == 0) {
				Iterator<Entry<class_1297, Integer>> iterator = this.field_5942.entrySet().iterator();

				while (iterator.hasNext()) {
					Entry<class_1297, Integer> entry = (Entry<class_1297, Integer>)iterator.next();
					if (this.field_6012 >= (Integer)entry.getValue()) {
						iterator.remove();
					}
				}

				List<class_1293> list = Lists.<class_1293>newArrayList();

				for (class_1293 lv2 : this.field_5933.method_8049()) {
					list.add(new class_1293(lv2.method_5579(), lv2.method_5584() / 4, lv2.method_5578(), lv2.method_5591(), lv2.method_5581()));
				}

				list.addAll(this.field_5934);
				if (list.isEmpty()) {
					this.field_5942.clear();
				} else {
					List<class_1309> list2 = this.field_6002.method_18467(class_1309.class, this.method_5829());
					if (!list2.isEmpty()) {
						for (class_1309 lv3 : list2) {
							if (!this.field_5942.containsKey(lv3) && lv3.method_6086()) {
								double d = lv3.field_5987 - this.field_5987;
								double e = lv3.field_6035 - this.field_6035;
								double t = d * d + e * e;
								if (t <= (double)(f * f)) {
									this.field_5942.put(lv3, this.field_6012 + this.field_5937);

									for (class_1293 lv4 : list) {
										if (lv4.method_5579().method_5561()) {
											lv4.method_5579().method_5564(this, this.method_5601(), lv3, lv4.method_5578(), 0.5);
										} else {
											lv3.method_6092(new class_1293(lv4));
										}
									}

									if (this.field_5929 != 0.0F) {
										f += this.field_5929;
										if (f < 0.5F) {
											this.method_5650();
											return;
										}

										this.method_5603(f);
									}

									if (this.field_5932 != 0) {
										this.field_5939 = this.field_5939 + this.field_5932;
										if (this.field_5939 <= 0) {
											this.method_5650();
											return;
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	public void method_5609(float f) {
		this.field_5929 = f;
	}

	public void method_5596(float f) {
		this.field_5930 = f;
	}

	public void method_5595(int i) {
		this.field_5941 = i;
	}

	public void method_5607(@Nullable class_1309 arg) {
		this.field_5943 = arg;
		this.field_5940 = arg == null ? null : arg.method_5667();
	}

	@Nullable
	public class_1309 method_5601() {
		if (this.field_5943 == null && this.field_5940 != null && this.field_6002 instanceof class_3218) {
			class_1297 lv = ((class_3218)this.field_6002).method_14190(this.field_5940);
			if (lv instanceof class_1309) {
				this.field_5943 = (class_1309)lv;
			}
		}

		return this.field_5943;
	}

	@Override
	protected void method_5749(class_2487 arg) {
		this.field_6012 = arg.method_10550("Age");
		this.field_5939 = arg.method_10550("Duration");
		this.field_5941 = arg.method_10550("WaitTime");
		this.field_5937 = arg.method_10550("ReapplicationDelay");
		this.field_5932 = arg.method_10550("DurationOnUse");
		this.field_5929 = arg.method_10583("RadiusOnUse");
		this.field_5930 = arg.method_10583("RadiusPerTick");
		this.method_5603(arg.method_10583("Radius"));
		this.field_5940 = arg.method_10584("OwnerUUID");
		if (arg.method_10573("Particle", 8)) {
			try {
				this.method_5608(class_2223.method_9418(new StringReader(arg.method_10558("Particle"))));
			} catch (CommandSyntaxException var5) {
				field_5935.warn("Couldn't load custom particle {}", arg.method_10558("Particle"), var5);
			}
		}

		if (arg.method_10573("Color", 99)) {
			this.method_5602(arg.method_10550("Color"));
		}

		if (arg.method_10573("Potion", 8)) {
			this.method_5612(class_1844.method_8057(arg));
		}

		if (arg.method_10573("Effects", 9)) {
			class_2499 lv = arg.method_10554("Effects", 10);
			this.field_5934.clear();

			for (int i = 0; i < lv.size(); i++) {
				class_1293 lv2 = class_1293.method_5583(lv.method_10602(i));
				if (lv2 != null) {
					this.method_5610(lv2);
				}
			}
		}
	}

	@Override
	protected void method_5652(class_2487 arg) {
		arg.method_10569("Age", this.field_6012);
		arg.method_10569("Duration", this.field_5939);
		arg.method_10569("WaitTime", this.field_5941);
		arg.method_10569("ReapplicationDelay", this.field_5937);
		arg.method_10569("DurationOnUse", this.field_5932);
		arg.method_10548("RadiusOnUse", this.field_5929);
		arg.method_10548("RadiusPerTick", this.field_5930);
		arg.method_10548("Radius", this.method_5599());
		arg.method_10582("Particle", this.method_5600().method_10293());
		if (this.field_5940 != null) {
			arg.method_10560("OwnerUUID", this.field_5940);
		}

		if (this.field_5928) {
			arg.method_10569("Color", this.method_5606());
		}

		if (this.field_5933 != class_1847.field_8984 && this.field_5933 != null) {
			arg.method_10582("Potion", class_2378.field_11143.method_10221(this.field_5933).toString());
		}

		if (!this.field_5934.isEmpty()) {
			class_2499 lv = new class_2499();

			for (class_1293 lv2 : this.field_5934) {
				lv.add(lv2.method_5582(new class_2487()));
			}

			arg.method_10566("Effects", lv);
		}
	}

	@Override
	public void method_5674(class_2940<?> arg) {
		if (field_5938.equals(arg)) {
			this.method_18382();
		}

		super.method_5674(arg);
	}

	@Override
	public class_3619 method_5657() {
		return class_3619.field_15975;
	}

	@Override
	public class_2596<?> method_18002() {
		return new class_2604(this);
	}

	@Override
	public class_4048 method_18377(class_4050 arg) {
		return class_4048.method_18384(this.method_5599() * 2.0F, 0.5F);
	}
}
