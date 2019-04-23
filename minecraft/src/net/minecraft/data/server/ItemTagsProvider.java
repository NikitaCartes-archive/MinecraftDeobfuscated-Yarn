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
		this.copy(BlockTags.field_15481, ItemTags.field_15544);
		this.copy(BlockTags.field_15471, ItemTags.field_15537);
		this.copy(BlockTags.field_15465, ItemTags.field_15531);
		this.copy(BlockTags.field_15499, ItemTags.field_15555);
		this.copy(BlockTags.field_15493, ItemTags.field_15551);
		this.copy(BlockTags.field_15479, ItemTags.field_15542);
		this.copy(BlockTags.field_15494, ItemTags.field_15552);
		this.copy(BlockTags.field_15502, ItemTags.field_15557);
		this.copy(BlockTags.field_15468, ItemTags.field_15534);
		this.copy(BlockTags.field_17619, ItemTags.field_17620);
		this.copy(BlockTags.field_15477, ItemTags.field_15540);
		this.copy(BlockTags.field_15495, ItemTags.field_15553);
		this.copy(BlockTags.field_15462, ItemTags.field_15528);
		this.copy(BlockTags.field_15482, ItemTags.field_15545);
		this.copy(BlockTags.field_15485, ItemTags.field_15546);
		this.copy(BlockTags.field_15498, ItemTags.field_15554);
		this.copy(BlockTags.field_15458, ItemTags.field_15525);
		this.copy(BlockTags.field_15489, ItemTags.field_15549);
		this.copy(BlockTags.field_15474, ItemTags.field_15538);
		this.copy(BlockTags.field_15475, ItemTags.field_15539);
		this.copy(BlockTags.field_15466, ItemTags.field_15532);
		this.copy(BlockTags.field_15469, ItemTags.field_15535);
		this.copy(BlockTags.field_15504, ItemTags.field_15560);
		this.copy(BlockTags.field_15459, ItemTags.field_15526);
		this.copy(BlockTags.field_15486, ItemTags.field_15547);
		this.copy(BlockTags.field_15463, ItemTags.field_15529);
		this.copy(BlockTags.field_15503, ItemTags.field_15558);
		this.copy(BlockTags.field_15491, ItemTags.field_15550);
		this.copy(BlockTags.field_15487, ItemTags.field_15548);
		this.copy(BlockTags.field_15480, ItemTags.field_15543);
		this.copy(BlockTags.field_16443, ItemTags.field_16444);
		this.copy(BlockTags.field_16584, ItemTags.field_16585);
		this.method_10512(ItemTags.field_15556)
			.add(
				Items.field_8539,
				Items.field_8824,
				Items.field_8671,
				Items.field_8379,
				Items.field_8049,
				Items.field_8778,
				Items.field_8329,
				Items.field_8617,
				Items.field_8855,
				Items.field_8629,
				Items.field_8405,
				Items.field_8128,
				Items.field_8124,
				Items.field_8295,
				Items.field_8586,
				Items.field_8572
			);
		this.method_10512(ItemTags.field_15536).add(Items.field_8533, Items.field_8486, Items.field_8442, Items.field_8730, Items.field_8094, Items.field_8138);
		this.method_10512(ItemTags.field_15527).add(Items.field_8429, Items.field_8373, Items.field_8209, Items.field_8509, Items.field_8323, Items.field_8846);
		this.copy(BlockTags.field_15472, ItemTags.field_15533);
		this.method_10512(ItemTags.field_15541)
			.add(
				Items.field_8144,
				Items.field_8075,
				Items.field_8425,
				Items.field_8623,
				Items.field_8502,
				Items.field_8534,
				Items.field_8344,
				Items.field_8834,
				Items.field_8065,
				Items.field_8355,
				Items.field_8731,
				Items.field_8806
			);
		this.method_10512(ItemTags.field_17487).add(Items.field_8713, Items.field_8665);
		this.method_10512(ItemTags.field_18317).add(Items.field_8107, Items.field_8087, Items.field_8236);
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
