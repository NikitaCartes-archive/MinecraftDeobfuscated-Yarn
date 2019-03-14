package net.minecraft.client.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class TextureStitcher {
	private static final Comparator<TextureStitcher.Holder> field_18030 = Comparator.comparing(holder -> -holder.height)
		.thenComparing(holder -> -holder.width)
		.thenComparing(holder -> holder.sprite.getId());
	private final int field_5243;
	private final Set<TextureStitcher.Holder> holders = Sets.<TextureStitcher.Holder>newHashSetWithExpectedSize(256);
	private final List<TextureStitcher.Slot> slots = Lists.<TextureStitcher.Slot>newArrayListWithCapacity(256);
	private int width;
	private int height;
	private final int maxWidth;
	private final int maxHeight;

	public TextureStitcher(int i, int j, int k) {
		this.field_5243 = k;
		this.maxWidth = i;
		this.maxHeight = j;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public void add(Sprite sprite) {
		TextureStitcher.Holder holder = new TextureStitcher.Holder(sprite, this.field_5243);
		this.holders.add(holder);
	}

	public void stitch() {
		List<TextureStitcher.Holder> list = Lists.<TextureStitcher.Holder>newArrayList(this.holders);
		list.sort(field_18030);

		for (TextureStitcher.Holder holder : list) {
			if (!this.tryFit(holder)) {
				throw new TextureStitcherCannotFitException(holder.sprite);
			}
		}

		this.width = MathHelper.smallestEncompassingPowerOfTwo(this.width);
		this.height = MathHelper.smallestEncompassingPowerOfTwo(this.height);
	}

	public List<Sprite> getStitchedSprites() {
		List<Sprite> list = Lists.<Sprite>newArrayList();

		for (TextureStitcher.Slot slot : this.slots) {
			slot.addAllFilledSlots(slotx -> {
				TextureStitcher.Holder holder = slotx.getSpriteHolder();
				Sprite sprite = holder.sprite;
				sprite.init(this.width, this.height, slotx.getX(), slotx.getY());
				list.add(sprite);
			});
		}

		return list;
	}

	private static int method_4551(int i, int j) {
		return (i >> j) + ((i & (1 << j) - 1) == 0 ? 0 : 1) << j;
	}

	private boolean tryFit(TextureStitcher.Holder holder) {
		for (TextureStitcher.Slot slot : this.slots) {
			if (slot.tryFit(holder)) {
				return true;
			}
		}

		return this.method_4552(holder);
	}

	private boolean method_4552(TextureStitcher.Holder holder) {
		int i = MathHelper.smallestEncompassingPowerOfTwo(this.width);
		int j = MathHelper.smallestEncompassingPowerOfTwo(this.height);
		int k = MathHelper.smallestEncompassingPowerOfTwo(this.width + holder.width);
		int l = MathHelper.smallestEncompassingPowerOfTwo(this.height + holder.height);
		boolean bl = k <= this.maxWidth;
		boolean bl2 = l <= this.maxHeight;
		if (!bl && !bl2) {
			return false;
		} else {
			boolean bl3 = bl && i != k;
			boolean bl4 = bl2 && j != l;
			boolean bl5;
			if (bl3 ^ bl4) {
				bl5 = bl3;
			} else {
				bl5 = bl && i <= j;
			}

			TextureStitcher.Slot slot;
			if (bl5) {
				if (this.height == 0) {
					this.height = holder.height;
				}

				slot = new TextureStitcher.Slot(this.width, 0, holder.width, this.height);
				this.width = this.width + holder.width;
			} else {
				slot = new TextureStitcher.Slot(0, this.height, this.width, holder.height);
				this.height = this.height + holder.height;
			}

			slot.tryFit(holder);
			this.slots.add(slot);
			return true;
		}
	}

	@Environment(EnvType.CLIENT)
	static class Holder {
		public final Sprite sprite;
		public final int width;
		public final int height;

		public Holder(Sprite sprite, int i) {
			this.sprite = sprite;
			this.width = TextureStitcher.method_4551(sprite.getWidth(), i);
			this.height = TextureStitcher.method_4551(sprite.getHeight(), i);
		}

		public String toString() {
			return "Holder{width=" + this.width + ", height=" + this.height + '}';
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Slot {
		private final int x;
		private final int y;
		private final int width;
		private final int height;
		private List<TextureStitcher.Slot> subSlots;
		private TextureStitcher.Holder spriteHolder;

		public Slot(int i, int j, int k, int l) {
			this.x = i;
			this.y = j;
			this.width = k;
			this.height = l;
		}

		public TextureStitcher.Holder getSpriteHolder() {
			return this.spriteHolder;
		}

		public int getX() {
			return this.x;
		}

		public int getY() {
			return this.y;
		}

		public boolean tryFit(TextureStitcher.Holder holder) {
			if (this.spriteHolder != null) {
				return false;
			} else {
				int i = holder.width;
				int j = holder.height;
				if (i <= this.width && j <= this.height) {
					if (i == this.width && j == this.height) {
						this.spriteHolder = holder;
						return true;
					} else {
						if (this.subSlots == null) {
							this.subSlots = Lists.<TextureStitcher.Slot>newArrayListWithCapacity(1);
							this.subSlots.add(new TextureStitcher.Slot(this.x, this.y, i, j));
							int k = this.width - i;
							int l = this.height - j;
							if (l > 0 && k > 0) {
								int m = Math.max(this.height, k);
								int n = Math.max(this.width, l);
								if (m >= n) {
									this.subSlots.add(new TextureStitcher.Slot(this.x, this.y + j, i, l));
									this.subSlots.add(new TextureStitcher.Slot(this.x + i, this.y, k, this.height));
								} else {
									this.subSlots.add(new TextureStitcher.Slot(this.x + i, this.y, k, j));
									this.subSlots.add(new TextureStitcher.Slot(this.x, this.y + j, this.width, l));
								}
							} else if (k == 0) {
								this.subSlots.add(new TextureStitcher.Slot(this.x, this.y + j, i, l));
							} else if (l == 0) {
								this.subSlots.add(new TextureStitcher.Slot(this.x + i, this.y, k, j));
							}
						}

						for (TextureStitcher.Slot slot : this.subSlots) {
							if (slot.tryFit(holder)) {
								return true;
							}
						}

						return false;
					}
				} else {
					return false;
				}
			}
		}

		public void addAllFilledSlots(Consumer<TextureStitcher.Slot> consumer) {
			if (this.spriteHolder != null) {
				consumer.accept(this);
			} else if (this.subSlots != null) {
				for (TextureStitcher.Slot slot : this.subSlots) {
					slot.addAllFilledSlots(consumer);
				}
			}
		}

		public String toString() {
			return "Slot{originX="
				+ this.x
				+ ", originY="
				+ this.y
				+ ", width="
				+ this.width
				+ ", height="
				+ this.height
				+ ", texture="
				+ this.spriteHolder
				+ ", subSlots="
				+ this.subSlots
				+ '}';
		}
	}
}
