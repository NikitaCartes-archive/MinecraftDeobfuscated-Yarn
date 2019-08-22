package net.minecraft.data.server;

import com.google.common.collect.Lists;
import java.nio.file.Path;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagContainer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ItemTagsProvider extends AbstractTagProvider<Item> {
	private static final Logger LOG = LogManager.getLogger();

	public ItemTagsProvider(DataGenerator dataGenerator) {
		super(dataGenerator, Registry.ITEM);
	}

	@Override
	protected void configure() {
		this.copy(BlockTags.WOOL, ItemTags.WOOL);
		this.copy(BlockTags.PLANKS, ItemTags.PLANKS);
		this.copy(BlockTags.STONE_BRICKS, ItemTags.STONE_BRICKS);
		this.copy(BlockTags.WOODEN_BUTTONS, ItemTags.WOODEN_BUTTONS);
		this.copy(BlockTags.BUTTONS, ItemTags.BUTTONS);
		this.copy(BlockTags.CARPETS, ItemTags.CARPETS);
		this.copy(BlockTags.WOODEN_DOORS, ItemTags.WOODEN_DOORS);
		this.copy(BlockTags.WOODEN_STAIRS, ItemTags.WOODEN_STAIRS);
		this.copy(BlockTags.WOODEN_SLABS, ItemTags.WOODEN_SLABS);
		this.copy(BlockTags.WOODEN_FENCES, ItemTags.WOODEN_FENCES);
		this.copy(BlockTags.WOODEN_PRESSURE_PLATES, ItemTags.WOODEN_PRESSURE_PLATES);
		this.copy(BlockTags.DOORS, ItemTags.DOORS);
		this.copy(BlockTags.SAPLINGS, ItemTags.SAPLINGS);
		this.copy(BlockTags.OAK_LOGS, ItemTags.OAK_LOGS);
		this.copy(BlockTags.DARK_OAK_LOGS, ItemTags.DARK_OAK_LOGS);
		this.copy(BlockTags.BIRCH_LOGS, ItemTags.BIRCH_LOGS);
		this.copy(BlockTags.ACACIA_LOGS, ItemTags.ACACIA_LOGS);
		this.copy(BlockTags.SPRUCE_LOGS, ItemTags.SPRUCE_LOGS);
		this.copy(BlockTags.JUNGLE_LOGS, ItemTags.JUNGLE_LOGS);
		this.copy(BlockTags.LOGS, ItemTags.LOGS);
		this.copy(BlockTags.SAND, ItemTags.SAND);
		this.copy(BlockTags.SLABS, ItemTags.SLABS);
		this.copy(BlockTags.WALLS, ItemTags.WALLS);
		this.copy(BlockTags.STAIRS, ItemTags.STAIRS);
		this.copy(BlockTags.ANVIL, ItemTags.ANVIL);
		this.copy(BlockTags.RAILS, ItemTags.RAILS);
		this.copy(BlockTags.LEAVES, ItemTags.LEAVES);
		this.copy(BlockTags.WOODEN_TRAPDOORS, ItemTags.WOODEN_TRAPDOORS);
		this.copy(BlockTags.TRAPDOORS, ItemTags.TRAPDOORS);
		this.copy(BlockTags.SMALL_FLOWERS, ItemTags.SMALL_FLOWERS);
		this.copy(BlockTags.BEDS, ItemTags.BEDS);
		this.copy(BlockTags.FENCES, ItemTags.FENCES);
		this.copy(BlockTags.TALL_FLOWERS, ItemTags.TALL_FLOWERS);
		this.copy(BlockTags.FLOWERS, ItemTags.FLOWERS);
		this.method_10512(ItemTags.BANNERS)
			.add(
				Items.WHITE_BANNER,
				Items.ORANGE_BANNER,
				Items.MAGENTA_BANNER,
				Items.LIGHT_BLUE_BANNER,
				Items.YELLOW_BANNER,
				Items.LIME_BANNER,
				Items.PINK_BANNER,
				Items.GRAY_BANNER,
				Items.LIGHT_GRAY_BANNER,
				Items.CYAN_BANNER,
				Items.PURPLE_BANNER,
				Items.BLUE_BANNER,
				Items.BROWN_BANNER,
				Items.GREEN_BANNER,
				Items.RED_BANNER,
				Items.BLACK_BANNER
			);
		this.method_10512(ItemTags.BOATS).add(Items.OAK_BOAT, Items.SPRUCE_BOAT, Items.BIRCH_BOAT, Items.JUNGLE_BOAT, Items.ACACIA_BOAT, Items.DARK_OAK_BOAT);
		this.method_10512(ItemTags.FISHES).add(Items.COD, Items.COOKED_COD, Items.SALMON, Items.COOKED_SALMON, Items.PUFFERFISH, Items.TROPICAL_FISH);
		this.copy(BlockTags.STANDING_SIGNS, ItemTags.SIGNS);
		this.method_10512(ItemTags.MUSIC_DISCS)
			.add(
				Items.MUSIC_DISC_13,
				Items.MUSIC_DISC_CAT,
				Items.MUSIC_DISC_BLOCKS,
				Items.MUSIC_DISC_CHIRP,
				Items.MUSIC_DISC_FAR,
				Items.MUSIC_DISC_MALL,
				Items.MUSIC_DISC_MELLOHI,
				Items.MUSIC_DISC_STAL,
				Items.MUSIC_DISC_STRAD,
				Items.MUSIC_DISC_WARD,
				Items.MUSIC_DISC_11,
				Items.MUSIC_DISC_WAIT
			);
		this.method_10512(ItemTags.COALS).add(Items.COAL, Items.CHARCOAL);
		this.method_10512(ItemTags.ARROWS).add(Items.ARROW, Items.TIPPED_ARROW, Items.SPECTRAL_ARROW);
	}

	protected void copy(Tag<Block> tag, Tag<Item> tag2) {
		Tag.Builder<Item> builder = this.method_10512(tag2);

		for (Tag.Entry<Block> entry : tag.entries()) {
			Tag.Entry<Item> entry2 = this.convert(entry);
			builder.add(entry2);
		}
	}

	private Tag.Entry<Item> convert(Tag.Entry<Block> entry) {
		if (entry instanceof Tag.TagEntry) {
			return new Tag.TagEntry<>(((Tag.TagEntry)entry).getId());
		} else if (entry instanceof Tag.CollectionEntry) {
			List<Item> list = Lists.<Item>newArrayList();

			for (Block block : ((Tag.CollectionEntry)entry).getValues()) {
				Item item = block.asItem();
				if (item == Items.AIR) {
					LOG.warn("Itemless block copied to item tag: {}", Registry.BLOCK.getId(block));
				} else {
					list.add(item);
				}
			}

			return new Tag.CollectionEntry<>(list);
		} else {
			throw new UnsupportedOperationException("Unknown tag entry " + entry);
		}
	}

	@Override
	protected Path getOutput(Identifier identifier) {
		return this.root.getOutput().resolve("data/" + identifier.getNamespace() + "/tags/items/" + identifier.getPath() + ".json");
	}

	@Override
	public String getName() {
		return "Item Tags";
	}

	@Override
	protected void method_10511(TagContainer<Item> tagContainer) {
		ItemTags.setContainer(tagContainer);
	}
}
