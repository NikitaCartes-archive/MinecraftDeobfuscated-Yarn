package net.minecraft;

import com.mojang.serialization.Codec;
import java.util.Collection;
import java.util.Optional;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DataPool;
import net.minecraft.util.math.floatprovider.ClampedNormalFloatProvider;
import net.minecraft.util.math.intprovider.ClampedIntProvider;
import net.minecraft.util.math.intprovider.ClampedNormalIntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.vote.BiomeColorRule;
import net.minecraft.vote.ItemDespawnType;
import net.minecraft.vote.LightEngineOptimizationType;
import net.minecraft.vote.ReplaceNaturalSpawnRule;
import net.minecraft.vote.VehicleCollisionTypes;

public class class_8293 {
	private static final DataPool.Builder<RegistryEntry.Reference<class_8289>> field_43650 = new DataPool.Builder<>();
	public static final int field_43532 = 5;
	public static final int field_43585 = 2;
	public static final int field_43638 = 7;
	public static final int field_43678 = 1000;
	public static final int field_43679 = 1000;
	public static final int field_43680 = 500;
	public static final int field_43681 = 125;
	public static final class_8264 field_43682 = method_50211("test_rule_please_ignore", 7, new class_8264(Text.literal("TEST RULE PLEASE IGNORE")));
	public static final class_8264 field_43683 = method_50211(
		"vote_result_pass_without_voters", 125, new class_8264(Text.translatable("rule.vote_result_pass_without_voters"))
	);
	public static final class_8264 field_43684 = method_50211(
		"vote_result_pass_without_votes", 125, new class_8264(Text.translatable("rule.vote_result_pass_without_votes"))
	);
	public static final class_8264 field_43685 = method_50211("vote_result_show_tally", 500, new class_8264(Text.translatable("rule.vote_result_show_options")));
	public static final class_8264 field_43686 = method_50211("vote_result_show_voters", 500, new class_8264(Text.translatable("rule.vote_result_show_voters")));
	public static final class_8264 field_43687 = method_50211(
		"vote_result_pick_random_if_vote_fails", 125, new class_8264(Text.translatable("rule.vote_result_pick_random_if_vote_fails"))
	);
	public static final class_8264 field_43688 = method_50211(
		"vote_result_reverse_counts", 125, new class_8264(Text.translatable("rule.vote_result_reverse_counts"))
	);
	public static final class_8281.class_8283 field_43689 = method_50211("vote_max_results", 1000, new class_8281.class_8283(1, UniformIntProvider.create(1, 5)) {
		protected Text method_50174(Integer integer) {
			return Text.translatable("rule.vote_max_results", integer);
		}
	});
	public static final class_8281.class_8283 field_43690 = method_50211(
		"new_vote_chance_per_tick", 500, new class_8281.class_8283(200, UniformIntProvider.create(1, 2000)) {
			protected Text method_50174(Integer integer) {
				return Text.translatable("rule.new_vote_chance_per_tick", integer);
			}
		}
	);
	public static final class_8296 field_43691 = method_50211(
		"new_vote_approve_option_count", 500, new class_8296(UniformIntProvider.create(1, 5), UniformIntProvider.create(0, 4), UniformIntProvider.create(2, 4)) {
			@Override
			protected Text method_50232(UniformIntProvider uniformIntProvider) {
				return Text.translatable("rule.new_vote_approve_option_count", method_50265(uniformIntProvider));
			}
		}
	);
	public static final class_8296 field_43692 = method_50211(
		"new_vote_repeal_option_count", 500, new class_8296(UniformIntProvider.create(1, 5), UniformIntProvider.create(0, 4), UniformIntProvider.create(2, 4)) {
			@Override
			protected Text method_50232(UniformIntProvider uniformIntProvider) {
				return Text.translatable("rule.new_vote_repeal_option_count", method_50265(uniformIntProvider));
			}
		}
	);
	public static final class_8296 field_43693 = method_50211(
		"new_vote_duration_minutes", 1000, new class_8296(UniformIntProvider.create(1, 20), UniformIntProvider.create(0, 10), UniformIntProvider.create(8, 16)) {
			@Override
			protected Text method_50232(UniformIntProvider uniformIntProvider) {
				return Text.translatable("rule.new_vote_duration_minutes", method_50265(uniformIntProvider));
			}
		}
	);
	public static final class_8281.class_8283 field_43694 = method_50211(
		"new_vote_extra_effect_chance", 1000, new class_8281.class_8283(30, UniformIntProvider.create(0, 80)) {
			protected Text method_50174(Integer integer) {
				return Text.translatable("rule.new_vote_extra_effect_chance", integer);
			}
		}
	);
	public static final class_8281.class_8283 field_43695 = method_50211(
		"new_vote_extra_effect_max_count", 1000, new class_8281.class_8283(1, UniformIntProvider.create(0, 5)) {
			protected Text method_50174(Integer integer) {
				return Text.translatable("rule.new_vote_extra_effect_max_count", integer);
			}
		}
	);
	public static final class_8281.class_8283 field_43696 = method_50211(
		"new_vote_repeal_vote_chance", 500, new class_8281.class_8283(50, UniformIntProvider.create(20, 80)) {
			protected Text method_50174(Integer integer) {
				return Text.translatable("rule.new_vote_repeal_vote_chance", integer);
			}
		}
	);
	public static final class_8264 field_43697 = method_50211("new_vote_disable_opt_out", 125, new class_8264(Text.translatable("rule.new_vote_disable_opt_out")));
	public static final class_8281.class_8283 field_43698 = method_50211(
		"new_vote_max_approve_vote_count", 500, new class_8281.class_8283(5, UniformIntProvider.create(1, 10)) {
			protected Text method_50174(Integer integer) {
				return Text.translatable("rule.new_vote_max_approve_vote_count", integer);
			}
		}
	);
	public static final class_8281.class_8283 field_43699 = method_50211(
		"new_vote_max_repeal_vote_count", 500, new class_8281.class_8283(2, UniformIntProvider.create(1, 10)) {
			protected Text method_50174(Integer integer) {
				return Text.translatable("rule.new_vote_max_repeal_vote_count", integer);
			}
		}
	);
	public static final class_8298 field_43700 = method_50211("new_vote_cost", 500, new class_8298());
	public static final class_8264 field_43506 = method_50211("invisible_armor", 500, new class_8264(Text.translatable("rule.invisible_armor")));
	public static final class_8271<class_8364> field_43507 = method_50211(
		"world_shape", 1000, new class_8271<class_8364>(class_8364.values(), class_8364.NONE, class_8364.field_43975) {
			protected Text method_50147(class_8364 arg) {
				return Text.translatable("rule.change_world_shape");
			}
		}
	);
	public static final class_8264 field_43508 = method_50211("disable_item_tooltips", 500, new class_8264(Text.translatable("rule.disable_item_tooltips")));
	public static final class_8281.class_8283 field_43509 = method_50211("quorum_percent", 500, new class_8281.class_8283(0, method_50207(20)) {
		protected Text method_50174(Integer integer) {
			return Text.translatable("rule.quorum_percent", integer);
		}
	});
	public static final class_8281.class_8283 field_43510 = method_50211("votes_to_win_percent", 125, new class_8281.class_8283(0, method_50207(20)) {
		protected Text method_50174(Integer integer) {
			return Text.translatable("rule.votes_to_win_percent", integer);
		}
	});
	public static final class_8264 field_43511 = method_50211("other_portal", 500, new class_8264(Text.translatable("rule.other_portal")));
	public static final class_8264 field_43512 = method_50211("anonymize_skins", 500, new class_8264(Text.translatable("rule.anonymize_skins")));
	public static final class_8347 field_43513 = method_50211("special_recipe", 1000, new class_8347());
	public static final class_8325 field_43514 = method_50211("footprints", 500, new class_8325());
	public static final class_8271<class_8383> field_43515 = method_50211(
		"tie_strategy", 500, new class_8271<class_8383>(class_8383.values(), class_8383.PICK_RANDOM, class_8383.field_44017) {
			protected Text method_50147(class_8383 arg) {
				return arg.method_50576();
			}
		}
	);
	public static final class_8264 field_43516 = method_50211("silent_vote", 125, new class_8264(Text.translatable("rule.silent_vote")));
	public static final class_8335 field_43517 = method_50211("replace_item_model", 1000, new class_8335());
	public static final class_8307 field_43518 = method_50211("replace_block_model", 1000, new class_8307());
	public static final class_8271<class_8300> field_43519 = method_50211(
		"auto_jump_alternatives", 500, new class_8271<class_8300>(class_8300.values(), class_8300.OFF, class_8300.field_43728) {
			protected Text method_50147(class_8300 arg) {
				return arg.method_50279();
			}
		}
	);
	public static final class_8264 field_43520 = method_50211("uncontrolable_lave", 125, new class_8264(Text.translatable("rule.uncontrolable_lave")));
	public static final class_8264 field_43521 = method_50211("wheels_on_minecarts", 500, new class_8264(Text.translatable("rule.wheels_on_minecarts")));
	public static final int field_43522 = 30;
	public static final class_8281<Integer> field_43523 = method_50211(
		"lava_spread_tick_delay", 500, new class_8281.class_8283(30, UniformIntProvider.create(1, 9)) {
			protected Text method_50174(Integer integer) {
				return Text.translatable("rule.lava_spread_tick_delay", integer);
			}
		}
	);
	public static final class_8264 field_43524 = method_50211("midas_touch", 125, new class_8264(Text.translatable("rule.midas_touch")));
	public static final class_8310 field_43525 = method_50211("cobblestone_gen_replace", 1000, new class_8337("rule.lava_water_replace", Blocks.COBBLESTONE));
	public static final class_8310 field_43526 = method_50211("stone_gen_replace", 1000, new class_8337("rule.lava_water_replace", Blocks.STONE));
	public static final class_8310 field_43527 = method_50211("obsidian_gen_replace", 125, new class_8337("rule.lava_water_replace", Blocks.OBSIDIAN));
	public static final class_8310 field_43528 = method_50211("basalt_gen_replace", 125, new class_8337("rule.lava_blue_ice_replace", Blocks.BASALT));
	public static final class_8264 field_43529 = method_50211("rowing_up_that_hill", 500, new class_8264(Text.translatable("rule.rowing_up_that_hill")));
	public static final class_8264 field_43530 = method_50211("pot_gems", 500, new class_8264(Text.translatable("rule.pot_gems")));
	public static final class_8264 field_43531 = method_50211("disable_shield", 500, new class_8264(Text.translatable("rule.disable_shield")));
	public static final class_8271<class_8363> field_43559 = method_50211(
		"rain", 1000, new class_8271<class_8363>(class_8363.values(), class_8363.DEFAULT, class_8363.field_43966) {
			protected Text method_50147(class_8363 arg) {
				return arg.method_50454();
			}
		}
	);
	public static final class_8271<class_8363> field_43560 = method_50211(
		"thunder", 1000, new class_8271<class_8363>(class_8363.values(), class_8363.DEFAULT, class_8363.field_43966) {
			protected Text method_50147(class_8363 arg) {
				return arg.method_50455();
			}
		}
	);
	public static final class_8281.class_8282 field_43561 = method_50211(
		"global_pitch", 1000, new class_8281.class_8282(1.0F, ClampedNormalFloatProvider.create(1.5F, 0.6F, 0.3F, 3.0F)) {
			protected Text method_50174(Float float_) {
				return Text.translatable("rule.global_pitch", Math.round(float_ * 100.0F));
			}
		}
	);
	public static final class_8326 field_43562 = method_50211("perma_effect", 1000, new class_8326());
	public static final class_8281.class_8282 field_43563 = method_50211(
		"item_use_speed", 1000, new class_8281.class_8282(1.0F, ClampedNormalFloatProvider.create(1.0F, 0.4F, 0.1F, 8.0F)) {
			protected Text method_50174(Float float_) {
				return Text.translatable("rule.item_use_speed", Math.round(float_ * 100.0F));
			}
		}
	);
	public static final class_8281.class_8282 field_43564 = method_50211(
		"attack_knockback", 1000, new class_8281.class_8282(1.0F, ClampedNormalFloatProvider.create(1.0F, 0.4F, 0.1F, 8.0F)) {
			protected Text method_50174(Float float_) {
				return Text.translatable("rule.attack_knockback", Math.round(float_ * 100.0F));
			}
		}
	);
	public static final class_8264 field_43565 = method_50211("infinite_cakes", 500, new class_8264(Text.translatable("rule.infinite_cakes")));
	public static final class_8264 field_43566 = method_50211("god_of_lightning", 125, new class_8264(Text.translatable("rule.god_of_lightning")));
	public static final class_8264 field_43567 = method_50211(
		"morrowind_power_player_movement", 125, new class_8264(Text.translatable("rule.morrowind_power_player_movement"))
	);
	public static final class_8264 field_43568 = method_50211("evil_eye", 125, new class_8264(Text.translatable("rule.evil_eye")));
	public static final float field_43569 = 200.0F;
	public static final class_8264 field_43570 = method_50211("big_head_mode", 1000, new class_8264(Text.translatable("rule.big_heads")));
	public static final class_8264 field_43571 = method_50211("floating_head_mode", 1000, new class_8264(Text.translatable("rule.floating_heads")));
	public static final class_8264 field_43572 = method_50211("transparent_players", 500, new class_8264(Text.translatable("rule.transparent_players")));
	public static final class_8271<class_8311> field_43573 = method_50211(
		"caep", 1000, new class_8271<class_8311>(class_8311.values(), class_8311.NONE, class_8311.field_43761) {
			protected Text method_50147(class_8311 arg) {
				return arg.method_50318();
			}
		}
	);
	public static final class_8264 field_43574 = method_50211("minime", 500, new class_8264(Text.translatable("rule.mini_players")));
	public static final class_8264 field_43575 = method_50211("milk_every_mob", 500, new class_8264(Text.translatable("rule.milk_every_mob")));
	public static final class_8264 field_43576 = method_50211("french_mode", 500, new class_8264(Text.translatable("rule.french_mode")));
	public static final class_8264 field_43577 = method_50211("mbe", 500, new class_8264(Text.translatable("rule.mbe")));
	public static final class_8264 field_43578 = method_50211("sticky", 500, new class_8264(Text.translatable("rule.sticky")));
	public static final class_8264 field_43579 = method_50211("buttons_on_things", 1000, new class_8264(Text.translatable("rule.buttons_on_things")));
	public static final class_8281<Integer> field_43580 = method_50211("push_limit", 1000, new class_8281.class_8283(12, UniformIntProvider.create(0, 23)) {
		protected Text method_50174(Integer integer) {
			return Text.translatable("rule.push_limit", integer);
		}
	});
	public static final class_8264 field_43581 = method_50211("fire_sponge", 1000, new class_8264(Text.translatable("rule.fire_sponge")));
	public static final class_8264 field_43582 = method_50211("persistent_parrots", 1000, new class_8264(Text.translatable("rule.persistent_parrots")));
	public static final ThreadLocal<Boolean> field_43583 = ThreadLocal.withInitial(() -> true);
	public static final class_8264 field_43584 = method_50211("less_interaction_updates", 125, new class_8264(Text.translatable("rule.less_interaction_updates")));
	public static final class_8264 field_43533 = method_50211("dead_bush_renewability", 1000, new class_8264(Text.translatable("rule.dead_bush_renewability")));
	public static final class_8264 field_43534 = method_50211("fog_off", 500, new class_8264(Text.translatable("rule.fog_off")));
	public static final class_8264 field_43535 = method_50211("fix_qc", 500, new class_8264(Text.translatable("rule.fix_qc")));
	public static final class_8264 field_43536 = method_50211("fast_hoppers", 500, new class_8264(Text.translatable("rule.fast_hoppers")));
	public static final class_8264 field_43537 = method_50211("less_gravity", 125, new class_8264(Text.translatable("rule.less_gravity")));
	public static final class_8264 field_43538 = method_50211("bouncy_castle", 1000, new class_8264(Text.translatable("rule.bouncy_castle")));
	public static final class_8264 field_43539 = method_50211("air_blocks", 1000, new class_8264(Text.translatable("rule.air_blocks")));
	public static final class_8264 field_43540 = method_50211("drink_air", 1000, new class_8264(Text.translatable("rule.drink_air")));
	public static final class_8349 field_43541 = method_50211(
		"replace_items_with_bottle_of_void", 500, new class_8349((registry, random) -> Optional.of(Items.BOTTLE_OF_VOID))
	);
	public static final class_8339 field_43542 = method_50211("big_moon", 500, new class_8339());
	public static final class_8264 field_43543 = method_50211("obfuscate_player_names", 500, new class_8264(Text.translatable("rule.obfuscate_player_names")));
	public static final class_8264 field_43544 = method_50211("beta_entity_ids", 500, new class_8264(Text.translatable("rule.beta_entity_ids")));
	public static final class_8354 field_43545 = method_50211("the_joke", 500, new class_8354());
	public static final class_8271<class_8340> field_43546 = method_50211(
		"normal_name_visibility", 1000, new class_8271<class_8340>(class_8340.values(), class_8340.SEE_THROUGH, class_8340.field_43908) {
			protected Text method_50147(class_8340 arg) {
				return Text.translatable("rule.normal_name_visibility", arg.method_50396());
			}
		}
	);
	public static final class_8271<class_8340> field_43547 = method_50211(
		"sneaking_name_visibility", 1000, new class_8271<class_8340>(class_8340.values(), class_8340.NORMAL, class_8340.field_43908) {
			protected Text method_50147(class_8340 arg) {
				return Text.translatable("rule.sneaking_name_visibility", arg.method_50396());
			}
		}
	);
	public static final class_8264 field_43548 = method_50211("entity_collisions", 500, new class_8264(Text.translatable("rule.entity_collisions")));
	public static final class_8264 field_43549 = method_50211("day_beds", 500, new class_8264(Text.translatable("rule.day_beds")));
	public static final class_8264 field_43550 = method_50211("pickaxe_block", 1000, new class_8264(Text.translatable("rule.pickaxe_block")));
	public static final class_8264 field_43551 = method_50211("place_block", 1000, new class_8264(Text.translatable("rule.place_block")));
	public static final class_8277 field_43552 = method_50211("parent_trap", 500, new class_8343());
	public static final class_8264 field_43553 = method_50211("glow_bees", 1000, new class_8264(Text.translatable("rule.glow_bees")));
	public static final class_8271<class_8323> field_43554 = method_50211(
		"flailing_level", 1000, new class_8271<class_8323>(class_8323.values(), class_8323.NORMAL, class_8323.field_43814) {
			protected Text method_50147(class_8323 arg) {
				return arg.method_50350();
			}
		}
	);
	public static final class_8271<class_8348> field_43555 = method_50211(
		"recipe_flip", 1000, new class_8271<class_8348>(class_8348.values(), class_8348.BOTH, class_8348.field_43925) {
			protected Text method_50147(class_8348 arg) {
				return arg.method_50422();
			}
		}
	);
	public static final class_8346 field_43556 = method_50211("ai_attack", 500, new class_8346() {
		protected Text method_50187(class_8345 arg) {
			return Text.translatable("rule.ai_attack", arg.displayName());
		}
	});
	public static final class_8346 field_43557 = method_50211("president", 1000, new class_8346() {
		protected Text method_50187(class_8345 arg) {
			return Text.translatable("rule.president", arg.displayName());
		}

		@Override
		protected boolean method_50231(class_8345 arg) {
			Collection<class_8345> collection = this.method_50257();
			collection.forEach(arg2 -> this.method_50258(arg2));
			return super.method_50231(arg);
		}
	});
	public static final class_8277 field_43558 = method_50211("copy_skin", 1000, new class_8317());
	public static final class_8271<ItemDespawnType> field_43612 = method_50211(
		"item_despawn", 125, new class_8271<ItemDespawnType>(ItemDespawnType.values(), ItemDespawnType.DESPAWN_ALL, ItemDespawnType.codec) {
			protected Text method_50147(ItemDespawnType itemDespawnType) {
				return itemDespawnType.getName();
			}
		}
	);
	public static final class_8266 field_43613 = method_50211("item_despawn_time", 1000, new class_8331());
	public static final class_8319 field_43614 = method_50211("day_length", 1000, new class_8319());
	public static final class_8264 field_43615 = method_50211("beds_on_banners", 500, new class_8264(Text.translatable("rule.beds_on_banners")));
	public static final class_8271<class_8324> field_43616 = method_50211(
		"food_restriction", 1000, new class_8271<class_8324>(class_8324.values(), class_8324.ANY, class_8324.field_43835) {
			protected Text method_50147(class_8324 arg) {
				return Text.translatable("rule.food_restriction." + arg.asString());
			}
		}
	);
	public static final class_8314 field_43617 = method_50211("codepoint_style", 1000, new class_8314());
	public static final class_8313 field_43618 = method_50211("codepoint_replace", 1000, new class_8313());
	public static final class_8342 field_43619 = method_50211("optimize", 500, new class_8342());
	public static final class_8301 field_43620 = method_50211("binary_gamerule_rule", 1000, new class_8301());
	public static final class_8329 field_43621 = method_50211("integer_gamerule_rule", 1000, new class_8329());
	public static final class_8286<EntityType<?>> field_43622 = method_50211(
		"dinnerbonize", 1000, new class_8286<EntityType<?>>("entity", RegistryKeys.ENTITY_TYPE) {
			@Override
			protected Text method_50189(Text text) {
				return Text.translatable("rule.dinnerbonize", text);
			}
		}
	);
	public static final class_8286<EntityType<?>> field_43623 = method_50211("grummize", 1000, new class_8286<EntityType<?>>("entity", RegistryKeys.ENTITY_TYPE) {
		@Override
		protected Text method_50189(Text text) {
			return Text.translatable("rule.grummize", text);
		}
	});
	public static final class_8333 field_43624 = method_50211("give_item", 1000, new class_8333());
	public static final class_8316 field_43625 = method_50211("default_sheep_color", 500, new class_8316(DyeColor.WHITE) {
		protected Text method_50147(DyeColor dyeColor) {
			return Text.translatable("rule.default_sheep_color", Text.translatable("color.minecraft." + dyeColor.getName()));
		}
	});
	public static final class_8264 field_43626 = method_50211("flintsploder", 500, new class_8264(Text.translatable("rule.flintsploder")));
	public static final class_8264 field_43627 = method_50211("fix_piston", 125, new class_8264(Text.translatable("rule.fix_piston")));
	public static final class_8264 field_43628 = method_50211("player_head_drop", 500, new class_8264(Text.translatable("rule.player_head_drop")));
	public static final class_8264 field_43629 = method_50211("charged_creepers", 500, new class_8264(Text.translatable("rule.charged_creepers")));
	public static final class_8273<Item> field_43630 = method_50211("egg_free", 1000, new class_8312());
	public static final class_8273<Item> field_43631 = method_50211("villager_gem", 1000, new class_8362());
	public static final class_8264 field_43632 = method_50211("unstable_tnt", 500, new class_8264(Text.translatable("rule.unstable_tnt")));
	public static final class_8264 field_43633 = method_50211("tnt_tennis", 500, new class_8264(Text.translatable("rule.tnt_tennis")));
	public static final class_8264 field_43634 = method_50211("undead_players", 125, new class_8264(Text.translatable("rule.undead_players")));
	public static final class_8264 field_43635 = method_50211("haunted_world", 500, new class_8264(Text.translatable("rule.haunted_world")));
	public static final class_8266 field_43636 = method_50211("explosion_power", 500, new class_8322());
	public static final class_8336 field_43637 = method_50211("replace_loot_drop", 1000, new class_8336("rule.replace_loot_drop"));
	public static final class_8321 field_43586 = method_50211("loot_double_or_half", 1000, new class_8321("rule.loot_double_or_half", -4, 4));
	public static final class_8336 field_43587 = method_50211("replace_recipe_output", 1000, new class_8336("rule.replace_recipe_output"));
	public static final class_8321 field_43588 = method_50211("recipe_double_or_half", 1000, new class_8321("rule.recipe_double_or_half", -4, 4));
	public static final int field_43589 = 4;
	public static final int field_43590 = 16;
	public static final class_8321 field_43591 = method_50211("stack_size_double_or_half", 1000, new class_8321("rule.stack_size_double_or_half", -6, 4));
	public static final class_8320 field_43592 = method_50211("damage_modifier", 1000, new class_8320("rule.damage_modifier", -3, 10));
	public static final class_8304 field_43593 = method_50211("inflammability", 1000, new class_8304());
	public static final class_8264 field_43594 = method_50211("minecart_lies", 500, new class_8264(Text.translatable("rule.minecart_lies")));
	public static final class_8289 WIPWIPWI___PWIPWIP = method_50211("wipwipwi-_-pwipwip", 1, new class_8355());
	public static final class_8264 field_43596 = method_50211("swap_sky", 500, new class_8264(Text.translatable("rule.swap_skies")));
	public static final class_8286<EntityType<?>> field_43597 = method_50211(
		"natural_spawn_disable", 1000, new class_8286<EntityType<?>>("entity", RegistryKeys.ENTITY_TYPE) {
			@Override
			protected Text method_50189(Text text) {
				return Text.translatable("rule.natural_spawn_disable", text);
			}
		}
	);
	public static final ReplaceNaturalSpawnRule field_43598 = method_50211("natural_spawn_replacement", 1000, new ReplaceNaturalSpawnRule());
	public static final class_8353 field_43599 = method_50211("sound_replace", 1000, new class_8353());
	public static final class_8271<VehicleCollisionTypes> field_43600 = method_50211(
		"minecart_collisions", 125, new class_8271<VehicleCollisionTypes>(VehicleCollisionTypes.values(), VehicleCollisionTypes.NONE, VehicleCollisionTypes.CODEC) {
			protected Text method_50147(VehicleCollisionTypes vehicleCollisionTypes) {
				return Text.translatable("rule.minecart_collisions." + vehicleCollisionTypes.asString());
			}
		}
	);
	public static final class_8271<VehicleCollisionTypes> field_43601 = method_50211(
		"boat_collisions", 125, new class_8271<VehicleCollisionTypes>(VehicleCollisionTypes.values(), VehicleCollisionTypes.NONE, VehicleCollisionTypes.CODEC) {
			protected Text method_50147(VehicleCollisionTypes vehicleCollisionTypes) {
				return Text.translatable("rule.boat_collisions." + vehicleCollisionTypes.asString());
			}
		}
	);
	public static final BiomeColorRule field_43602 = method_50211("biome_grass_color", 1000, new BiomeColorRule("rule.biome_color.grass"));
	public static final BiomeColorRule field_43603 = method_50211("biome_foliage_color", 1000, new BiomeColorRule("rule.biome_color.foliage"));
	public static final BiomeColorRule field_43604 = method_50211("biome_sky_color", 1000, new BiomeColorRule("rule.biome_color.sky"));
	public static final BiomeColorRule field_43605 = method_50211("biome_water_color", 1000, new BiomeColorRule("rule.biome_color.water"));
	public static final BiomeColorRule field_43606 = method_50211("biome_fog_color", 1000, new BiomeColorRule("rule.biome_color.fog"));
	public static final BiomeColorRule field_43607 = method_50211("biome_water_fog_color", 1000, new BiomeColorRule("rule.biome_color.water_fog"));
	public static final class_8264 field_43608 = method_50211("rubies", 500, new class_8264(Text.translatable("rule.rubies")));
	public static final class_8359 field_43609 = method_50211("transform_scale", 1000, new class_8359());
	public static final class_8357 field_43610 = method_50211("transform_entity", 125, new class_8357());
	public static final class_8264 field_43611 = method_50211("ultra_realistic_mode", 500, new class_8264(Text.translatable("rule.ultra_realistic_mode")));
	public static final class_8264 field_43652 = method_50211("remove_phantoms", 125, new class_8264(Text.translatable("rule.remove_phantoms")));
	public static final class_8264 field_43653 = method_50211("phantom_phantom", 500, new class_8264(Text.translatable("rule.phantom_phantom")));
	public static final class_8349 field_43654 = method_50211(
		"replace_items", 1000, new class_8349((registry, random) -> registry.getRandom(random).map(RegistryEntry::value))
	);
	public static final class_8264 field_43655 = method_50211("dream_mode", 500, new class_8264(Text.translatable("rule.dream_mode")));
	public static final class_8264 field_43656 = method_50211("instacheese", 500, new class_8264(Text.translatable("rule.instacheese")));
	public static final class_8264 field_43657 = method_50211("universal_jeb", 500, new class_8264(Text.translatable("rule.universal_jeb")));
	public static final class_8264 field_43658 = method_50211("world_of_giants", 125, new class_8264(Text.translatable("rule.world_of_giants")));
	public static final class_8264 field_43659 = method_50211("ray_tracing", 125, new class_8264(Text.translatable("rule.ray_tracing")));
	public static final class_8316 field_43660 = method_50211("colored_light", 125, new class_8316(DyeColor.WHITE) {
		protected Text method_50147(DyeColor dyeColor) {
			return Text.translatable("rule.colored_light", Text.translatable("color.minecraft." + dyeColor.getName()));
		}
	});
	public static final class_8264 field_43661 = method_50211("glowing_glow_squids", 500, new class_8264(Text.translatable("rule.glowing_glow_squids")));
	public static final class_8264 field_43662 = method_50211("bedrock_shadows", 1000, new class_8264(Text.translatable("rule.bedrock_shadows")));
	public static final class_8264 field_43663 = method_50211("always_flying", 125, new class_8264(Text.translatable("rule.always_flying")));
	public static final class_8264 field_43664 = method_50211("copper_sink", 500, new class_8264(Text.translatable("rule.copper_sink")));
	public static final class_8264 field_43665 = method_50211("bed_pvp", 125, new class_8264(Text.translatable("rule.bed_pvp")));
	public static final class_8264 field_43666 = method_50211("nbt_crafting", 500, new class_8264(Text.translatable("rule.nbt_crafting")));
	public static final class_8264 field_43667 = method_50211("potions_of_big", 1000, new class_8264(Text.translatable("rule.potions_of_big")));
	public static final class_8264 field_43668 = method_50211("potions_of_small", 1000, new class_8264(Text.translatable("rule.potions_of_small")));
	public static final class_8264 field_43669 = method_50211("keep_friends_close", 125, new class_8264(Text.translatable("rule.keep_friends_close")));
	public static final class_8264 field_43670 = method_50211("prevent_floating_trees", 500, new class_8264(Text.translatable("rule.prevent_floating_trees")));
	public static final class_8264 field_43671 = method_50211("random_tnt_fuse", 500, new class_8264(Text.translatable("rule.random_tnt_fuse")));
	public static final class_8264 field_43672 = method_50211("exploding_phantoms", 125, new class_8264(Text.translatable("rule.exploding_phantoms")));
	public static final class_8264 field_43673 = method_50211("buff_fishing", 500, new class_8264(Text.translatable("rule.buff_fishing")));
	public static final class_8264 field_43674 = method_50211("zombie_apocalypse", 125, new class_8264(Text.translatable("rule.zombie_apocalypse")));
	public static final class_8281.class_8283 field_43675 = method_50211(
		"dupe_hack_occurrence_chance", 500, new class_8281.class_8283(0, ClampedNormalIntProvider.of(30.0F, 30.0F, 0, 500)) {
			protected Text method_50174(Integer integer) {
				return Text.translatable("rule.dupe_hack_occurrence_chance", integer);
			}
		}
	);
	public static final class_8281.class_8283 field_43676 = method_50211(
		"dupe_hack_break_chance", 500, new class_8281.class_8283(30, ClampedNormalIntProvider.of(30.0F, 30.0F, 0, 100)) {
			protected Text method_50174(Integer integer) {
				return Text.translatable("rule.dupe_hack_break_chance", integer);
			}
		}
	);
	public static final class_8281.class_8283 field_43677 = method_50211(
		"spawn_egg_chance", 500, new class_8281.class_8283(0, ClampedNormalIntProvider.of(10.0F, 30.0F, 0, 100)) {
			protected Text method_50174(Integer integer) {
				return Text.translatable("rule.spawn_egg_chance", integer);
			}
		}
	);
	public static final class_8271<LightEngineOptimizationType> field_43639 = method_50211(
		"optimize_light_engine",
		125,
		new class_8271<LightEngineOptimizationType>(LightEngineOptimizationType.values(), LightEngineOptimizationType.NONE, LightEngineOptimizationType.CODEC) {
			protected Text method_50147(LightEngineOptimizationType lightEngineOptimizationType) {
				return lightEngineOptimizationType.getName();
			}
		}
	);
	public static final class_8286<EntityType<?>> field_43640 = method_50211(
		"rideable_entities", 1000, new class_8286<EntityType<?>>("entity", RegistryKeys.ENTITY_TYPE) {
			@Override
			protected Text method_50189(Text text) {
				return Text.translatable("rule.rideable_entities", text);
			}
		}
	);
	public static final class_8264 field_43641 = method_50211(
		"endermen_pick_up_anything", 500, new class_8264(Text.translatable("rule.endermen_pick_up_anything"))
	);
	public static final class_8264 field_43642 = method_50211("endermen_block_update", 500, new class_8264(Text.translatable("rule.endermen_block_update")));
	public static final class_8264 field_43643 = method_50211("voting_fireworks", 500, new class_8264(Text.translatable("rule.voting_fireworks")));
	public static final class_8264 field_43644 = method_50211("snitch", 500, new class_8264(Text.translatable("rule.snitch")));
	public static final class_8264 field_43645 = method_50211("grappling_fishing_rods", 500, new class_8264(Text.translatable("rule.grappling_fishing_rods")));
	public static final class_8264 field_43646 = method_50211("beeloons", 1000, new class_8264(Text.translatable("rule.beeloons")));
	public static final class_8264 field_43647 = method_50211("fish_anything", 500, new class_8264(Text.translatable("rule.fish_anything")));
	public static final class_8264 field_43648 = method_50211("only_mending_trades", 500, new class_8264(Text.translatable("rule.only_mending_trades")));
	public static final class_8264 field_43649 = method_50211("trails_and_tails", 500, new class_8264(Text.translatable("rule.trails_and_tails")));
	private static final DataPool<RegistryEntry.Reference<class_8289>> field_43651 = field_43650.build();

	private static ClampedIntProvider method_50207(int i) {
		return ClampedIntProvider.create(UniformIntProvider.create(-i, 100 + i), 0, 100);
	}

	private static <R extends class_8289> R method_50211(String string, int i, R arg) {
		RegistryEntry.Reference<class_8289> reference = Registry.registerReference(Registries.field_44443, new Identifier(string), arg);
		field_43650.add(reference, i);
		return arg;
	}

	public static final class_8289 method_50209(Registry<class_8289> registry) {
		return field_43682;
	}

	public static final RegistryEntry.Reference<class_8289> method_50208(Random random) {
		return (RegistryEntry.Reference<class_8289>)field_43651.getDataOrEmpty(random).orElseThrow();
	}

	public static final RegistryEntry.Reference<class_8289> method_50212(Random random) {
		return (RegistryEntry.Reference<class_8289>)field_43651.method_50057(random).orElseThrow();
	}
}
