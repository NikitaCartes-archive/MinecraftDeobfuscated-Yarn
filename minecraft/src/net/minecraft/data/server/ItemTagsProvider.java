package net.minecraft.data.server;

import java.nio.file.Path;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ItemTagsProvider extends AbstractTagProvider<Item> {
	private final Function<Tag.Identified<Block>, Tag.Builder> field_23783;

	public ItemTagsProvider(DataGenerator dataGenerator, BlockTagsProvider blockTagsProvider) {
		super(dataGenerator, Registry.ITEM);
		this.field_23783 = blockTagsProvider::method_27169;
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
		this.copy(BlockTags.field_21955, ItemTags.field_21957);
		this.copy(BlockTags.field_21956, ItemTags.field_21958);
		this.copy(BlockTags.field_23210, ItemTags.field_23212);
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
		this.copy(BlockTags.field_20338, ItemTags.field_20343);
		this.copy(BlockTags.field_20339, ItemTags.field_20344);
		this.copy(BlockTags.field_23062, ItemTags.field_23065);
		this.copy(BlockTags.field_23119, ItemTags.field_23801);
		this.getOrCreateTagBuilder(ItemTags.field_15556)
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
		this.getOrCreateTagBuilder(ItemTags.field_15536)
			.add(Items.field_8533, Items.field_8486, Items.field_8442, Items.field_8730, Items.field_8094, Items.field_8138);
		this.getOrCreateTagBuilder(ItemTags.field_15527)
			.add(Items.field_8429, Items.field_8373, Items.field_8209, Items.field_8509, Items.field_8323, Items.field_8846);
		this.copy(BlockTags.field_15472, ItemTags.field_15533);
		this.getOrCreateTagBuilder(ItemTags.field_23969)
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
		this.getOrCreateTagBuilder(ItemTags.field_15541).addTag(ItemTags.field_23969).add(Items.field_23984);
		this.getOrCreateTagBuilder(ItemTags.field_17487).add(Items.field_8713, Items.field_8665);
		this.getOrCreateTagBuilder(ItemTags.field_18317).add(Items.field_8107, Items.field_8087, Items.field_8236);
		this.getOrCreateTagBuilder(ItemTags.field_21465).add(Items.field_8360, Items.field_8674);
		this.getOrCreateTagBuilder(ItemTags.field_22277).add(Items.field_22020, Items.field_8687, Items.field_8477, Items.field_8695, Items.field_8620);
		this.getOrCreateTagBuilder(ItemTags.field_23064).add(Items.SOUL_TORCH).add(Items.SOUL_LANTERN).add(Items.SOUL_CAMPFIRE);
		this.getOrCreateTagBuilder(ItemTags.field_24481)
			.addTag(ItemTags.field_23065)
			.add(
				Items.GOLD_BLOCK,
				Items.GILDED_BLACKSTONE,
				Items.LIGHT_WEIGHTED_PRESSURE_PLATE,
				Items.field_8695,
				Items.BELL,
				Items.field_8557,
				Items.field_8071,
				Items.field_8597,
				Items.field_8463,
				Items.field_8367,
				Items.field_8862,
				Items.field_8678,
				Items.field_8416,
				Items.field_8753,
				Items.field_8560,
				Items.field_8845,
				Items.field_8335,
				Items.field_8322,
				Items.field_8825,
				Items.field_8303
			);
		this.getOrCreateTagBuilder(ItemTags.field_23211)
			.add(
				Items.WARPED_STEM,
				Items.STRIPPED_WARPED_STEM,
				Items.WARPED_HYPHAE,
				Items.STRIPPED_WARPED_HYPHAE,
				Items.CRIMSON_STEM,
				Items.STRIPPED_CRIMSON_STEM,
				Items.CRIMSON_HYPHAE,
				Items.STRIPPED_CRIMSON_HYPHAE,
				Items.CRIMSON_PLANKS,
				Items.WARPED_PLANKS,
				Items.CRIMSON_SLAB,
				Items.WARPED_SLAB,
				Items.CRIMSON_PRESSURE_PLATE,
				Items.WARPED_PRESSURE_PLATE,
				Items.CRIMSON_FENCE,
				Items.WARPED_FENCE,
				Items.CRIMSON_TRAPDOOR,
				Items.WARPED_TRAPDOOR,
				Items.CRIMSON_FENCE_GATE,
				Items.WARPED_FENCE_GATE,
				Items.CRIMSON_STAIRS,
				Items.WARPED_STAIRS,
				Items.CRIMSON_BUTTON,
				Items.WARPED_BUTTON,
				Items.CRIMSON_DOOR,
				Items.WARPED_DOOR,
				Items.field_22011,
				Items.field_22012
			);
		this.getOrCreateTagBuilder(ItemTags.field_23802).add(Items.COBBLESTONE, Items.BLACKSTONE);
		this.getOrCreateTagBuilder(ItemTags.field_25808).add(Items.COBBLESTONE, Items.BLACKSTONE);
	}

	protected void copy(Tag.Identified<Block> identified, Tag.Identified<Item> identified2) {
		Tag.Builder builder = this.method_27169(identified2);
		Tag.Builder builder2 = (Tag.Builder)this.field_23783.apply(identified);
		builder2.streamEntries().forEach(builder::add);
	}

	@Override
	protected Path getOutput(Identifier identifier) {
		return this.root.getOutput().resolve("data/" + identifier.getNamespace() + "/tags/items/" + identifier.getPath() + ".json");
	}

	@Override
	public String getName() {
		return "Item Tags";
	}
}
