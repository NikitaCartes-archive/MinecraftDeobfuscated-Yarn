package net.minecraft.client.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class TextureStitcher {
	private final int mipLevel;
	private final Set<TextureStitcher.Holder> holders = Sets.<TextureStitcher.Holder>newHashSetWithExpectedSize(256);
	private final List<TextureStitcher.Slot> slots = Lists.<TextureStitcher.Slot>newArrayListWithCapacity(256);
	private int width;
	private int height;
	private final int maxWidth;
	private final int maxHeight;
	private final int field_5236;

	public TextureStitcher(int i, int j, int k, int l) {
		this.mipLevel = l;
		this.maxWidth = i;
		this.maxHeight = j;
		this.field_5236 = k;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public void add(Sprite sprite) {
		TextureStitcher.Holder holder = new TextureStitcher.Holder(sprite, this.mipLevel);
		if (this.field_5236 > 0) {
			holder.method_4559(this.field_5236);
		}

		this.holders.add(holder);
	}

	public void stitch() {
		TextureStitcher.Holder[] holders = (TextureStitcher.Holder[])this.holders.toArray(new TextureStitcher.Holder[this.holders.size()]);
		Arrays.sort(holders);

		for (TextureStitcher.Holder holder : holders) {
			if (!this.tryFit(holder)) {
				String string = String.format(
					"Unable to fit: %s - size: %dx%d - Maybe try a lowerresolution resourcepack?",
					holder.getSprite().getId(),
					holder.getSprite().getWidth(),
					holder.getSprite().getHeight()
				);
				throw new TextureStitcherCannotFitException(holder, string);
			}
		}

		this.width = MathHelper.smallestEncompassingPowerOfTwo(this.width);
		this.height = MathHelper.smallestEncompassingPowerOfTwo(this.height);
	}

	public List<Sprite> getStitchedSprites() {
		List<TextureStitcher.Slot> list = Lists.<TextureStitcher.Slot>newArrayList();

		for (TextureStitcher.Slot slot : this.slots) {
			slot.addAllFilledSlots(list);
		}

		List<Sprite> list2 = Lists.<Sprite>newArrayList();

		for (TextureStitcher.Slot slot2 : list) {
			TextureStitcher.Holder holder = slot2.getSpriteHolder();
			Sprite sprite = holder.getSprite();
			sprite.init(this.width, this.height, slot2.getX(), slot2.getY(), holder.method_4561());
			list2.add(sprite);
		}

		return list2;
	}

	private static int method_4551(int i, int j) {
		return (i >> j) + ((i & (1 << j) - 1) == 0 ? 0 : 1) << j;
	}

	private boolean tryFit(TextureStitcher.Holder holder) {
		Sprite sprite = holder.getSprite();
		boolean bl = sprite.getWidth() != sprite.getHeight();

		for (int i = 0; i < this.slots.size(); i++) {
			if (((TextureStitcher.Slot)this.slots.get(i)).tryFit(holder)) {
				return true;
			}

			if (bl) {
				holder.method_4564();
				if (((TextureStitcher.Slot)this.slots.get(i)).tryFit(holder)) {
					return true;
				}

				holder.method_4564();
			}
		}

		return this.method_4552(holder);
	}

	private boolean method_4552(TextureStitcher.Holder holder) {
		int i = Math.min(holder.method_4562(), holder.method_4560());
		int j = Math.max(holder.method_4562(), holder.method_4560());
		int k = MathHelper.smallestEncompassingPowerOfTwo(this.width);
		int l = MathHelper.smallestEncompassingPowerOfTwo(this.height);
		int m = MathHelper.smallestEncompassingPowerOfTwo(this.width + i);
		int n = MathHelper.smallestEncompassingPowerOfTwo(this.height + i);
		boolean bl = m <= this.maxWidth;
		boolean bl2 = n <= this.maxHeight;
		if (!bl && !bl2) {
			return false;
		} else {
			boolean bl3 = bl && k != m;
			boolean bl4 = bl2 && l != n;
			boolean bl5;
			if (bl3 ^ bl4) {
				bl5 = bl3;
			} else {
				bl5 = bl && k <= l;
			}

			TextureStitcher.Slot slot;
			if (bl5) {
				if (holder.method_4562() > holder.method_4560()) {
					holder.method_4564();
				}

				if (this.height == 0) {
					this.height = holder.method_4560();
				}

				slot = new TextureStitcher.Slot(this.width, 0, holder.method_4562(), this.height);
				this.width = this.width + holder.method_4562();
			} else {
				slot = new TextureStitcher.Slot(0, this.height, this.width, holder.method_4560());
				this.height = this.height + holder.method_4560();
			}

			slot.tryFit(holder);
			this.slots.add(slot);
			return true;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Holder implements Comparable<TextureStitcher.Holder> {
		private final Sprite sprite;
		private final int width;
		private final int height;
		private final int mipLevel;
		private boolean field_5246;
		private float field_5244 = 1.0F;

		public Holder(Sprite sprite, int i) {
			this.sprite = sprite;
			this.width = sprite.getWidth();
			this.height = sprite.getHeight();
			this.mipLevel = i;
			this.field_5246 = TextureStitcher.method_4551(this.height, i) > TextureStitcher.method_4551(this.width, i);
		}

		public Sprite getSprite() {
			return this.sprite;
		}

		public int method_4562() {
			int i = this.field_5246 ? this.height : this.width;
			return TextureStitcher.method_4551((int)((float)i * this.field_5244), this.mipLevel);
		}

		public int method_4560() {
			int i = this.field_5246 ? this.width : this.height;
			return TextureStitcher.method_4551((int)((float)i * this.field_5244), this.mipLevel);
		}

		public void method_4564() {
			this.field_5246 = !this.field_5246;
		}

		public boolean method_4561() {
			return this.field_5246;
		}

		public void method_4559(int i) {
			if (this.width > i && this.height > i) {
				this.field_5244 = (float)i / (float)Math.min(this.width, this.height);
			}
		}

		public String toString() {
			return "Holder{width=" + this.width + ", height=" + this.height + '}';
		}

		public int method_4563(TextureStitcher.Holder holder) {
			int i;
			if (this.method_4560() == holder.method_4560()) {
				if (this.method_4562() == holder.method_4562()) {
					return this.sprite.getId().toString().compareTo(holder.sprite.getId().toString());
				}

				i = this.method_4562() < holder.method_4562() ? 1 : -1;
			} else {
				i = this.method_4560() < holder.method_4560() ? 1 : -1;
			}

			return i;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Slot {
		private final int x;
		private final int y;
		private final int width;
		private final int height;
		private List<TextureStitcher.Slot> field_5255;
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
				int i = holder.method_4562();
				int j = holder.method_4560();
				if (i <= this.width && j <= this.height) {
					if (i == this.width && j == this.height) {
						this.spriteHolder = holder;
						return true;
					} else {
						if (this.field_5255 == null) {
							this.field_5255 = Lists.<TextureStitcher.Slot>newArrayListWithCapacity(1);
							this.field_5255.add(new TextureStitcher.Slot(this.x, this.y, i, j));
							int k = this.width - i;
							int l = this.height - j;
							if (l > 0 && k > 0) {
								int m = Math.max(this.height, k);
								int n = Math.max(this.width, l);
								if (m >= n) {
									this.field_5255.add(new TextureStitcher.Slot(this.x, this.y + j, i, l));
									this.field_5255.add(new TextureStitcher.Slot(this.x + i, this.y, k, this.height));
								} else {
									this.field_5255.add(new TextureStitcher.Slot(this.x + i, this.y, k, j));
									this.field_5255.add(new TextureStitcher.Slot(this.x, this.y + j, this.width, l));
								}
							} else if (k == 0) {
								this.field_5255.add(new TextureStitcher.Slot(this.x, this.y + j, i, l));
							} else if (l == 0) {
								this.field_5255.add(new TextureStitcher.Slot(this.x + i, this.y, k, j));
							}
						}

						for (TextureStitcher.Slot slot : this.field_5255) {
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

		public void addAllFilledSlots(List<TextureStitcher.Slot> list) {
			if (this.spriteHolder != null) {
				list.add(this);
			} else if (this.field_5255 != null) {
				for (TextureStitcher.Slot slot : this.field_5255) {
					slot.addAllFilledSlots(list);
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
				+ this.field_5255
				+ '}';
		}
	}
}
