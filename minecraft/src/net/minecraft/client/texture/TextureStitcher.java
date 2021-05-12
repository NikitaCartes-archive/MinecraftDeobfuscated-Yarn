package net.minecraft.client.texture;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class TextureStitcher {
	private static final Comparator<TextureStitcher.Holder> COMPARATOR = Comparator.comparing(holder -> -holder.height)
		.thenComparing(holder -> -holder.width)
		.thenComparing(holder -> holder.sprite.getId());
	private final int mipLevel;
	private final Set<TextureStitcher.Holder> holders = Sets.<TextureStitcher.Holder>newHashSetWithExpectedSize(256);
	private final List<TextureStitcher.Slot> slots = Lists.<TextureStitcher.Slot>newArrayListWithCapacity(256);
	private int width;
	private int height;
	private final int maxWidth;
	private final int maxHeight;

	public TextureStitcher(int maxWidth, int maxHeight, int mipLevel) {
		this.mipLevel = mipLevel;
		this.maxWidth = maxWidth;
		this.maxHeight = maxHeight;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public void add(Sprite.Info info) {
		TextureStitcher.Holder holder = new TextureStitcher.Holder(info, this.mipLevel);
		this.holders.add(holder);
	}

	public void stitch() {
		List<TextureStitcher.Holder> list = Lists.<TextureStitcher.Holder>newArrayList(this.holders);
		list.sort(COMPARATOR);

		for (TextureStitcher.Holder holder : list) {
			if (!this.fit(holder)) {
				throw new TextureStitcherCannotFitException(
					holder.sprite, (Collection<Sprite.Info>)list.stream().map(holderx -> holderx.sprite).collect(ImmutableList.toImmutableList())
				);
			}
		}

		this.width = MathHelper.smallestEncompassingPowerOfTwo(this.width);
		this.height = MathHelper.smallestEncompassingPowerOfTwo(this.height);
	}

	public void getStitchedSprites(TextureStitcher.SpriteConsumer spriteConsumer) {
		for (TextureStitcher.Slot slot : this.slots) {
			slot.addAllFilledSlots(slotx -> {
				TextureStitcher.Holder holder = slotx.getTexture();
				Sprite.Info info = holder.sprite;
				spriteConsumer.load(info, this.width, this.height, slotx.getX(), slotx.getY());
			});
		}
	}

	static int applyMipLevel(int size, int mipLevel) {
		return (size >> mipLevel) + ((size & (1 << mipLevel) - 1) == 0 ? 0 : 1) << mipLevel;
	}

	private boolean fit(TextureStitcher.Holder holder) {
		for (TextureStitcher.Slot slot : this.slots) {
			if (slot.fit(holder)) {
				return true;
			}
		}

		return this.growAndFit(holder);
	}

	private boolean growAndFit(TextureStitcher.Holder holder) {
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

			slot.fit(holder);
			this.slots.add(slot);
			return true;
		}
	}

	@Environment(EnvType.CLIENT)
	static class Holder {
		public final Sprite.Info sprite;
		public final int width;
		public final int height;

		public Holder(Sprite.Info sprite, int mipLevel) {
			this.sprite = sprite;
			this.width = TextureStitcher.applyMipLevel(sprite.getWidth(), mipLevel);
			this.height = TextureStitcher.applyMipLevel(sprite.getHeight(), mipLevel);
		}

		public String toString() {
			return "Holder{width=" + this.width + ", height=" + this.height + "}";
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Slot {
		private final int x;
		private final int y;
		private final int width;
		private final int height;
		private List<TextureStitcher.Slot> subSlots;
		private TextureStitcher.Holder texture;

		public Slot(int x, int y, int width, int height) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}

		public TextureStitcher.Holder getTexture() {
			return this.texture;
		}

		public int getX() {
			return this.x;
		}

		public int getY() {
			return this.y;
		}

		public boolean fit(TextureStitcher.Holder holder) {
			if (this.texture != null) {
				return false;
			} else {
				int i = holder.width;
				int j = holder.height;
				if (i <= this.width && j <= this.height) {
					if (i == this.width && j == this.height) {
						this.texture = holder;
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
							if (slot.fit(holder)) {
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
			if (this.texture != null) {
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
				+ this.texture
				+ ", subSlots="
				+ this.subSlots
				+ "}";
		}
	}

	@Environment(EnvType.CLIENT)
	public interface SpriteConsumer {
		void load(Sprite.Info info, int width, int height, int x, int y);
	}
}
