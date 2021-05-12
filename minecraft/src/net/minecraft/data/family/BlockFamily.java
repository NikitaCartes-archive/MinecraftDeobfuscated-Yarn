package net.minecraft.data.family;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import org.apache.commons.lang3.StringUtils;

public class BlockFamily {
	private final Block baseBlock;
	final Map<BlockFamily.Variant, Block> variants = Maps.newHashMap();
	boolean generateModels = true;
	boolean generateRecipes = true;
	@Nullable
	String group;
	@Nullable
	String unlockCriterionName;

	BlockFamily(Block baseBlock) {
		this.baseBlock = baseBlock;
	}

	public Block getBaseBlock() {
		return this.baseBlock;
	}

	public Map<BlockFamily.Variant, Block> getVariants() {
		return this.variants;
	}

	public Block getVariant(BlockFamily.Variant variant) {
		return (Block)this.variants.get(variant);
	}

	public boolean shouldGenerateModels() {
		return this.generateModels;
	}

	public boolean shouldGenerateRecipes() {
		return this.generateRecipes;
	}

	public Optional<String> getGroup() {
		return StringUtils.isBlank(this.group) ? Optional.empty() : Optional.of(this.group);
	}

	public Optional<String> getUnlockCriterionName() {
		return StringUtils.isBlank(this.unlockCriterionName) ? Optional.empty() : Optional.of(this.unlockCriterionName);
	}

	public static class Builder {
		private final BlockFamily family;

		public Builder(Block baseBlock) {
			this.family = new BlockFamily(baseBlock);
		}

		public BlockFamily build() {
			return this.family;
		}

		public BlockFamily.Builder button(Block block) {
			this.family.variants.put(BlockFamily.Variant.BUTTON, block);
			return this;
		}

		public BlockFamily.Builder chiseled(Block block) {
			this.family.variants.put(BlockFamily.Variant.CHISELED, block);
			return this;
		}

		public BlockFamily.Builder cracked(Block block) {
			this.family.variants.put(BlockFamily.Variant.CRACKED, block);
			return this;
		}

		public BlockFamily.Builder cut(Block block) {
			this.family.variants.put(BlockFamily.Variant.CUT, block);
			return this;
		}

		public BlockFamily.Builder door(Block block) {
			this.family.variants.put(BlockFamily.Variant.DOOR, block);
			return this;
		}

		public BlockFamily.Builder fence(Block block) {
			this.family.variants.put(BlockFamily.Variant.FENCE, block);
			return this;
		}

		public BlockFamily.Builder fenceGate(Block block) {
			this.family.variants.put(BlockFamily.Variant.FENCE_GATE, block);
			return this;
		}

		public BlockFamily.Builder sign(Block block, Block wallBlock) {
			this.family.variants.put(BlockFamily.Variant.SIGN, block);
			this.family.variants.put(BlockFamily.Variant.WALL_SIGN, wallBlock);
			return this;
		}

		public BlockFamily.Builder slab(Block block) {
			this.family.variants.put(BlockFamily.Variant.SLAB, block);
			return this;
		}

		public BlockFamily.Builder stairs(Block block) {
			this.family.variants.put(BlockFamily.Variant.STAIRS, block);
			return this;
		}

		public BlockFamily.Builder pressurePlate(Block block) {
			this.family.variants.put(BlockFamily.Variant.PRESSURE_PLATE, block);
			return this;
		}

		public BlockFamily.Builder polished(Block block) {
			this.family.variants.put(BlockFamily.Variant.POLISHED, block);
			return this;
		}

		public BlockFamily.Builder trapdoor(Block block) {
			this.family.variants.put(BlockFamily.Variant.TRAPDOOR, block);
			return this;
		}

		public BlockFamily.Builder wall(Block block) {
			this.family.variants.put(BlockFamily.Variant.WALL, block);
			return this;
		}

		public BlockFamily.Builder noGenerateModels() {
			this.family.generateModels = false;
			return this;
		}

		public BlockFamily.Builder noGenerateRecipes() {
			this.family.generateRecipes = false;
			return this;
		}

		public BlockFamily.Builder group(String group) {
			this.family.group = group;
			return this;
		}

		public BlockFamily.Builder unlockCriterionName(String unlockCriterionName) {
			this.family.unlockCriterionName = unlockCriterionName;
			return this;
		}
	}

	public static enum Variant {
		BUTTON("button"),
		CHISELED("chiseled"),
		CRACKED("cracked"),
		CUT("cut"),
		DOOR("door"),
		FENCE("fence"),
		FENCE_GATE("fence_gate"),
		SIGN("sign"),
		SLAB("slab"),
		STAIRS("stairs"),
		PRESSURE_PLATE("pressure_plate"),
		POLISHED("polished"),
		TRAPDOOR("trapdoor"),
		WALL("wall"),
		WALL_SIGN("wall_sign");

		private final String name;

		private Variant(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}
	}
}
