package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1540 extends class_1297 {
	private class_2680 field_7188 = class_2246.field_10102.method_9564();
	public int field_7192;
	public boolean field_7193 = true;
	private boolean field_7189;
	private boolean field_7191;
	private int field_7190 = 40;
	private float field_7187 = 2.0F;
	public class_2487 field_7194;
	protected static final class_2940<class_2338> field_7195 = class_2945.method_12791(class_1540.class, class_2943.field_13324);

	public class_1540(class_1937 arg) {
		super(class_1299.field_6089, arg);
	}

	public class_1540(class_1937 arg, double d, double e, double f, class_2680 arg2) {
		this(arg);
		this.field_7188 = arg2;
		this.field_6033 = true;
		this.method_5835(0.98F, 0.98F);
		this.method_5814(d, e + (double)((1.0F - this.field_6019) / 2.0F), f);
		this.field_5967 = 0.0;
		this.field_5984 = 0.0;
		this.field_6006 = 0.0;
		this.field_6014 = d;
		this.field_6036 = e;
		this.field_5969 = f;
		this.method_6963(new class_2338(this));
	}

	@Override
	public boolean method_5732() {
		return false;
	}

	public void method_6963(class_2338 arg) {
		this.field_6011.method_12778(field_7195, arg);
	}

	@Environment(EnvType.CLIENT)
	public class_2338 method_6964() {
		return this.field_6011.method_12789(field_7195);
	}

	@Override
	protected boolean method_5658() {
		return false;
	}

	@Override
	protected void method_5693() {
		this.field_6011.method_12784(field_7195, class_2338.field_10980);
	}

	@Override
	public boolean method_5863() {
		return !this.field_5988;
	}

	@Override
	public void method_5773() {
		if (this.field_7188.method_11588()) {
			this.method_5650();
		} else {
			this.field_6014 = this.field_5987;
			this.field_6036 = this.field_6010;
			this.field_5969 = this.field_6035;
			class_2248 lv = this.field_7188.method_11614();
			if (this.field_7192++ == 0) {
				class_2338 lv2 = new class_2338(this);
				if (this.field_6002.method_8320(lv2).method_11614() == lv) {
					this.field_6002.method_8650(lv2);
				} else if (!this.field_6002.field_9236) {
					this.method_5650();
					return;
				}
			}

			if (!this.method_5740()) {
				this.field_5984 -= 0.04F;
			}

			this.method_5784(class_1313.field_6308, this.field_5967, this.field_5984, this.field_6006);
			if (!this.field_6002.field_9236) {
				class_2338 lv2 = new class_2338(this);
				boolean bl = this.field_7188.method_11614() instanceof class_2292;
				boolean bl2 = bl && this.field_6002.method_8316(lv2).method_15767(class_3486.field_15517);
				double d = this.field_5967 * this.field_5967 + this.field_5984 * this.field_5984 + this.field_6006 * this.field_6006;
				if (bl && d > 1.0) {
					class_239 lv3 = this.field_6002
						.method_8418(
							new class_243(this.field_6014, this.field_6036, this.field_5969), new class_243(this.field_5987, this.field_6010, this.field_6035), class_242.field_1345
						);
					if (lv3 != null && this.field_6002.method_8316(lv3.method_1015()).method_15767(class_3486.field_15517)) {
						lv2 = lv3.method_1015();
						bl2 = true;
					}
				}

				if (!this.field_5952 && !bl2) {
					if (this.field_7192 > 100 && !this.field_6002.field_9236 && (lv2.method_10264() < 1 || lv2.method_10264() > 256) || this.field_7192 > 600) {
						if (this.field_7193 && this.field_6002.method_8450().method_8355("doEntityDrops")) {
							this.method_5706(lv);
						}

						this.method_5650();
					}
				} else {
					class_2680 lv4 = this.field_6002.method_8320(lv2);
					if (!bl2 && class_2346.method_10128(this.field_6002.method_8320(new class_2338(this.field_5987, this.field_6010 - 0.01F, this.field_6035)))) {
						this.field_5952 = false;
						return;
					}

					this.field_5967 *= 0.7F;
					this.field_6006 *= 0.7F;
					this.field_5984 *= -0.5;
					if (lv4.method_11614() != class_2246.field_10008) {
						this.method_5650();
						if (!this.field_7189) {
							if (lv4.method_11620().method_15800() && (bl2 || !class_2346.method_10128(this.field_6002.method_8320(lv2.method_10074())))) {
								if (this.field_7188.method_11570(class_2741.field_12508) && this.field_6002.method_8316(lv2).method_15772() == class_3612.field_15910) {
									this.field_7188 = this.field_7188.method_11657(class_2741.field_12508, Boolean.valueOf(true));
								}

								if (this.field_6002.method_8652(lv2, this.field_7188, 3)) {
									if (lv instanceof class_2346) {
										((class_2346)lv).method_10127(this.field_6002, lv2, this.field_7188, lv4);
									}

									if (this.field_7194 != null && lv instanceof class_2343) {
										class_2586 lv5 = this.field_6002.method_8321(lv2);
										if (lv5 != null) {
											class_2487 lv6 = lv5.method_11007(new class_2487());

											for (String string : this.field_7194.method_10541()) {
												class_2520 lv7 = this.field_7194.method_10580(string);
												if (!"x".equals(string) && !"y".equals(string) && !"z".equals(string)) {
													lv6.method_10566(string, lv7.method_10707());
												}
											}

											lv5.method_11014(lv6);
											lv5.method_5431();
										}
									}
								} else if (this.field_7193 && this.field_6002.method_8450().method_8355("doEntityDrops")) {
									this.method_5706(lv);
								}
							} else if (this.field_7193 && this.field_6002.method_8450().method_8355("doEntityDrops")) {
								this.method_5706(lv);
							}
						} else if (lv instanceof class_2346) {
							((class_2346)lv).method_10129(this.field_6002, lv2);
						}
					}
				}
			}

			this.field_5967 *= 0.98F;
			this.field_5984 *= 0.98F;
			this.field_6006 *= 0.98F;
		}
	}

	@Override
	public void method_5747(float f, float g) {
		if (this.field_7191) {
			int i = class_3532.method_15386(f - 1.0F);
			if (i > 0) {
				List<class_1297> list = Lists.<class_1297>newArrayList(this.field_6002.method_8335(this, this.method_5829()));
				boolean bl = this.field_7188.method_11602(class_3481.field_15486);
				class_1282 lv = bl ? class_1282.field_5865 : class_1282.field_5847;

				for (class_1297 lv2 : list) {
					lv2.method_5643(lv, (float)Math.min(class_3532.method_15375((float)i * this.field_7187), this.field_7190));
				}

				if (bl && (double)this.field_5974.nextFloat() < 0.05F + (double)i * 0.05) {
					class_2680 lv3 = class_2199.method_9346(this.field_7188);
					if (lv3 == null) {
						this.field_7189 = true;
					} else {
						this.field_7188 = lv3;
					}
				}
			}
		}
	}

	@Override
	protected void method_5652(class_2487 arg) {
		arg.method_10566("BlockState", class_2512.method_10686(this.field_7188));
		arg.method_10569("Time", this.field_7192);
		arg.method_10556("DropItem", this.field_7193);
		arg.method_10556("HurtEntities", this.field_7191);
		arg.method_10548("FallHurtAmount", this.field_7187);
		arg.method_10569("FallHurtMax", this.field_7190);
		if (this.field_7194 != null) {
			arg.method_10566("TileEntityData", this.field_7194);
		}
	}

	@Override
	protected void method_5749(class_2487 arg) {
		this.field_7188 = class_2512.method_10681(arg.method_10562("BlockState"));
		this.field_7192 = arg.method_10550("Time");
		if (arg.method_10573("HurtEntities", 99)) {
			this.field_7191 = arg.method_10577("HurtEntities");
			this.field_7187 = arg.method_10583("FallHurtAmount");
			this.field_7190 = arg.method_10550("FallHurtMax");
		} else if (this.field_7188.method_11602(class_3481.field_15486)) {
			this.field_7191 = true;
		}

		if (arg.method_10573("DropItem", 99)) {
			this.field_7193 = arg.method_10577("DropItem");
		}

		if (arg.method_10573("TileEntityData", 10)) {
			this.field_7194 = arg.method_10562("TileEntityData");
		}

		if (this.field_7188.method_11588()) {
			this.field_7188 = class_2246.field_10102.method_9564();
		}
	}

	@Environment(EnvType.CLIENT)
	public class_1937 method_6966() {
		return this.field_6002;
	}

	public void method_6965(boolean bl) {
		this.field_7191 = bl;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_5862() {
		return false;
	}

	@Override
	public void method_5819(class_129 arg) {
		super.method_5819(arg);
		arg.method_578("Immitating BlockState", this.field_7188.toString());
	}

	public class_2680 method_6962() {
		return this.field_7188;
	}

	@Override
	public boolean method_5833() {
		return true;
	}
}
