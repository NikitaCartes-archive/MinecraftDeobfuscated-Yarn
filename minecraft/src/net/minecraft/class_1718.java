package net.minecraft;

import java.util.List;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1718 extends class_1703 {
	private final class_1263 field_7809 = new class_1277(2) {
		@Override
		public void method_5431() {
			super.method_5431();
			class_1718.this.method_7609(this);
		}
	};
	private final class_3914 field_7813;
	private final Random field_7811 = new Random();
	private final class_3915 field_7814 = class_3915.method_17403();
	public final int[] field_7808 = new int[3];
	public final int[] field_7812 = new int[]{-1, -1, -1};
	public final int[] field_7810 = new int[]{-1, -1, -1};

	public class_1718(int i, class_1661 arg) {
		this(i, arg, class_3914.field_17304);
	}

	public class_1718(int i, class_1661 arg, class_3914 arg2) {
		super(class_3917.field_17334, i);
		this.field_7813 = arg2;
		this.method_7621(new class_1735(this.field_7809, 0, 15, 47) {
			@Override
			public boolean method_7680(class_1799 arg) {
				return true;
			}

			@Override
			public int method_7675() {
				return 1;
			}
		});
		this.method_7621(new class_1735(this.field_7809, 1, 35, 47) {
			@Override
			public boolean method_7680(class_1799 arg) {
				return arg.method_7909() == class_1802.field_8759;
			}
		});

		for (int j = 0; j < 3; j++) {
			for (int k = 0; k < 9; k++) {
				this.method_7621(new class_1735(arg, k + j * 9 + 9, 8 + k * 18, 84 + j * 18));
			}
		}

		for (int j = 0; j < 9; j++) {
			this.method_7621(new class_1735(arg, j, 8 + j * 18, 142));
		}

		this.method_17362(class_3915.method_17406(this.field_7808, 0));
		this.method_17362(class_3915.method_17406(this.field_7808, 1));
		this.method_17362(class_3915.method_17406(this.field_7808, 2));
		this.method_17362(this.field_7814).method_17404(arg.field_7546.method_7278());
		this.method_17362(class_3915.method_17406(this.field_7812, 0));
		this.method_17362(class_3915.method_17406(this.field_7812, 1));
		this.method_17362(class_3915.method_17406(this.field_7812, 2));
		this.method_17362(class_3915.method_17406(this.field_7810, 0));
		this.method_17362(class_3915.method_17406(this.field_7810, 1));
		this.method_17362(class_3915.method_17406(this.field_7810, 2));
	}

	@Override
	public void method_7609(class_1263 arg) {
		if (arg == this.field_7809) {
			class_1799 lv = arg.method_5438(0);
			if (!lv.method_7960() && lv.method_7923()) {
				this.field_7813.method_17393((arg2, arg3) -> {
					int ix = 0;

					for (int j = -1; j <= 1; j++) {
						for (int k = -1; k <= 1; k++) {
							if ((j != 0 || k != 0) && arg2.method_8623(arg3.method_10069(k, 0, j)) && arg2.method_8623(arg3.method_10069(k, 1, j))) {
								if (arg2.method_8320(arg3.method_10069(k * 2, 0, j * 2)).method_11614() == class_2246.field_10504) {
									ix++;
								}

								if (arg2.method_8320(arg3.method_10069(k * 2, 1, j * 2)).method_11614() == class_2246.field_10504) {
									ix++;
								}

								if (k != 0 && j != 0) {
									if (arg2.method_8320(arg3.method_10069(k * 2, 0, j)).method_11614() == class_2246.field_10504) {
										ix++;
									}

									if (arg2.method_8320(arg3.method_10069(k * 2, 1, j)).method_11614() == class_2246.field_10504) {
										ix++;
									}

									if (arg2.method_8320(arg3.method_10069(k, 0, j * 2)).method_11614() == class_2246.field_10504) {
										ix++;
									}

									if (arg2.method_8320(arg3.method_10069(k, 1, j * 2)).method_11614() == class_2246.field_10504) {
										ix++;
									}
								}
							}
						}
					}

					this.field_7811.setSeed((long)this.field_7814.method_17407());

					for (int j = 0; j < 3; j++) {
						this.field_7808[j] = class_1890.method_8227(this.field_7811, j, ix, lv);
						this.field_7812[j] = -1;
						this.field_7810[j] = -1;
						if (this.field_7808[j] < j + 1) {
							this.field_7808[j] = 0;
						}
					}

					for (int jx = 0; jx < 3; jx++) {
						if (this.field_7808[jx] > 0) {
							List<class_1889> list = this.method_7637(lv, jx, this.field_7808[jx]);
							if (list != null && !list.isEmpty()) {
								class_1889 lvx = (class_1889)list.get(this.field_7811.nextInt(list.size()));
								this.field_7812[jx] = class_2378.field_11160.method_10249(lvx.field_9093);
								this.field_7810[jx] = lvx.field_9094;
							}
						}
					}

					this.method_7623();
				});
			} else {
				for (int i = 0; i < 3; i++) {
					this.field_7808[i] = 0;
					this.field_7812[i] = -1;
					this.field_7810[i] = -1;
				}
			}
		}
	}

	@Override
	public boolean method_7604(class_1657 arg, int i) {
		class_1799 lv = this.field_7809.method_5438(0);
		class_1799 lv2 = this.field_7809.method_5438(1);
		int j = i + 1;
		if ((lv2.method_7960() || lv2.method_7947() < j) && !arg.field_7503.field_7477) {
			return false;
		} else if (this.field_7808[i] <= 0 || lv.method_7960() || (arg.field_7520 < j || arg.field_7520 < this.field_7808[i]) && !arg.field_7503.field_7477) {
			return false;
		} else {
			this.field_7813.method_17393((arg4, arg5) -> {
				class_1799 lvx = lv;
				List<class_1889> list = this.method_7637(lv, i, this.field_7808[i]);
				if (!list.isEmpty()) {
					arg.method_7286(lv, j);
					boolean bl = lv.method_7909() == class_1802.field_8529;
					if (bl) {
						lvx = new class_1799(class_1802.field_8598);
						this.field_7809.method_5447(0, lvx);
					}

					for (int k = 0; k < list.size(); k++) {
						class_1889 lv2x = (class_1889)list.get(k);
						if (bl) {
							class_1772.method_7807(lvx, lv2x);
						} else {
							lvx.method_7978(lv2x.field_9093, lv2x.field_9094);
						}
					}

					if (!arg.field_7503.field_7477) {
						lv2.method_7934(j);
						if (lv2.method_7960()) {
							this.field_7809.method_5447(1, class_1799.field_8037);
						}
					}

					arg.method_7281(class_3468.field_15420);
					if (arg instanceof class_3222) {
						class_174.field_1181.method_8870((class_3222)arg, lvx, j);
					}

					this.field_7809.method_5431();
					this.field_7814.method_17404(arg.method_7278());
					this.method_7609(this.field_7809);
					arg4.method_8396(null, arg5, class_3417.field_15119, class_3419.field_15245, 1.0F, arg4.field_9229.nextFloat() * 0.1F + 0.9F);
				}
			});
			return true;
		}
	}

	private List<class_1889> method_7637(class_1799 arg, int i, int j) {
		this.field_7811.setSeed((long)(this.field_7814.method_17407() + i));
		List<class_1889> list = class_1890.method_8230(this.field_7811, arg, j, false);
		if (arg.method_7909() == class_1802.field_8529 && list.size() > 1) {
			list.remove(this.field_7811.nextInt(list.size()));
		}

		return list;
	}

	@Environment(EnvType.CLIENT)
	public int method_7638() {
		class_1799 lv = this.field_7809.method_5438(1);
		return lv.method_7960() ? 0 : lv.method_7947();
	}

	@Environment(EnvType.CLIENT)
	public int method_17413() {
		return this.field_7814.method_17407();
	}

	@Override
	public void method_7595(class_1657 arg) {
		super.method_7595(arg);
		this.field_7813.method_17393((arg2, arg3) -> this.method_7607(arg, arg.field_6002, this.field_7809));
	}

	@Override
	public boolean method_7597(class_1657 arg) {
		return method_17695(this.field_7813, arg, class_2246.field_10485);
	}

	@Override
	public class_1799 method_7601(class_1657 arg, int i) {
		class_1799 lv = class_1799.field_8037;
		class_1735 lv2 = (class_1735)this.field_7761.get(i);
		if (lv2 != null && lv2.method_7681()) {
			class_1799 lv3 = lv2.method_7677();
			lv = lv3.method_7972();
			if (i == 0) {
				if (!this.method_7616(lv3, 2, 38, true)) {
					return class_1799.field_8037;
				}
			} else if (i == 1) {
				if (!this.method_7616(lv3, 2, 38, true)) {
					return class_1799.field_8037;
				}
			} else if (lv3.method_7909() == class_1802.field_8759) {
				if (!this.method_7616(lv3, 1, 2, true)) {
					return class_1799.field_8037;
				}
			} else {
				if (((class_1735)this.field_7761.get(0)).method_7681() || !((class_1735)this.field_7761.get(0)).method_7680(lv3)) {
					return class_1799.field_8037;
				}

				if (lv3.method_7985() && lv3.method_7947() == 1) {
					((class_1735)this.field_7761.get(0)).method_7673(lv3.method_7972());
					lv3.method_7939(0);
				} else if (!lv3.method_7960()) {
					((class_1735)this.field_7761.get(0)).method_7673(new class_1799(lv3.method_7909()));
					lv3.method_7934(1);
				}
			}

			if (lv3.method_7960()) {
				lv2.method_7673(class_1799.field_8037);
			} else {
				lv2.method_7668();
			}

			if (lv3.method_7947() == lv.method_7947()) {
				return class_1799.field_8037;
			}

			lv2.method_7667(arg, lv3);
		}

		return lv;
	}
}
