package net.minecraft.client.texture;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class TextureStitcher<T extends TextureStitcher.Stitchable> {
	private static final Comparator<TextureStitcher.Holder<?>> COMPARATOR = Comparator.comparing(holder -> -holder.height)
		.thenComparing(holder -> -holder.width)
		.thenComparing(holder -> holder.sprite.getId());
	private final int mipLevel;
	private final List<TextureStitcher.Holder<T>> holders = new ArrayList();
	private final List<TextureStitcher.Slot<T>> slots = new ArrayList();
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

	public void add(T info) {
		TextureStitcher.Holder<T> holder = new TextureStitcher.Holder<>(info, this.mipLevel);
		this.holders.add(holder);
	}

	public void stitch() {
		List<TextureStitcher.Holder<T>> list = new ArrayList(this.holders);
		list.sort(COMPARATOR);

		for (TextureStitcher.Holder<T> holder : list) {
			if (!this.fit(holder)) {
				throw new TextureStitcherCannotFitException(
					holder.sprite, (Collection<TextureStitcher.Stitchable>)list.stream().map(holderx -> holderx.sprite).collect(ImmutableList.toImmutableList())
				);
			}
		}

		this.width = MathHelper.smallestEncompassingPowerOfTwo(this.width);
		this.height = MathHelper.smallestEncompassingPowerOfTwo(this.height);
	}

	public void getStitchedSprites(TextureStitcher.SpriteConsumer<T> consumer) {
		for (TextureStitcher.Slot<T> slot : this.slots) {
			slot.addAllFilledSlots(consumer);
		}
	}

	static int applyMipLevel(int size, int mipLevel) {
		return (size >> mipLevel) + ((size & (1 << mipLevel) - 1) == 0 ? 0 : 1) << mipLevel;
	}

	private boolean fit(TextureStitcher.Holder<T> holder) {
		for (TextureStitcher.Slot<T> slot : this.slots) {
			if (slot.fit(holder)) {
				return true;
			}
		}

		return this.growAndFit(holder);
	}

	private boolean growAndFit(TextureStitcher.Holder<T> holder) {
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

			TextureStitcher.Slot<T> slot;
			if (bl5) {
				if (this.height == 0) {
					this.height = holder.height;
				}

				slot = new TextureStitcher.Slot<>(this.width, 0, holder.width, this.height);
				this.width = this.width + holder.width;
			} else {
				slot = new TextureStitcher.Slot<>(0, this.height, this.width, holder.height);
				this.height = this.height + holder.height;
			}

			slot.fit(holder);
			this.slots.add(slot);
			return true;
		}
	}

	@Environment(EnvType.CLIENT)
	static record Holder<T extends TextureStitcher.Stitchable>(T sprite, int width, int height) {

		public Holder(T sprite, int mipLevel) {
			this(sprite, TextureStitcher.applyMipLevel(sprite.getWidth(), mipLevel), TextureStitcher.applyMipLevel(sprite.getHeight(), mipLevel));
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Slot<T extends TextureStitcher.Stitchable> {
		private final int x;
		private final int y;
		private final int width;
		private final int height;
		@Nullable
		private List<TextureStitcher.Slot<T>> subSlots;
		@Nullable
		private TextureStitcher.Holder<T> texture;

		public Slot(int x, int y, int width, int height) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}

		public int getX() {
			return this.x;
		}

		public int getY() {
			return this.y;
		}

		public boolean fit(TextureStitcher.Holder<T> holder) {
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
							this.subSlots = new ArrayList(1);
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

						for (TextureStitcher.Slot<T> slot : this.subSlots) {
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

		public void addAllFilledSlots(TextureStitcher.SpriteConsumer<T> consumer) {
			if (this.texture != null) {
				consumer.load(this.texture.sprite, this.getX(), this.getY());
			} else if (this.subSlots != null) {
				for (TextureStitcher.Slot<T> slot : this.subSlots) {
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
	public interface SpriteConsumer<T extends TextureStitcher.Stitchable> {
		void load(T info, int width, int height);
	}

	@Environment(EnvType.CLIENT)
	public interface Stitchable {
		int getWidth();

		int getHeight();

		Identifier getId();
	}
}
