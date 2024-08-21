package net.minecraft.client.render.model;

import com.mojang.logging.LogUtils;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.model.json.ModelVariantMap;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.registry.Registries;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class BlockStatesLoader {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final String MAP_KEY = "map";
	private static final String MAP_TRUE_VARIANT = "map=true";
	private static final String MAP_FALSE_VARIANT = "map=false";
	private static final StateManager<Block, BlockState> ITEM_FRAME_STATE_MANAGER = new StateManager.Builder<Block, BlockState>(Blocks.AIR)
		.add(BooleanProperty.of("map"))
		.build(Block::getDefaultState, BlockState::new);
	private static final Identifier GLOW_ITEM_FRAME_ID = Identifier.ofVanilla("glow_item_frame");
	private static final Identifier ITEM_FRAME_ID = Identifier.ofVanilla("item_frame");
	private static final Map<Identifier, StateManager<Block, BlockState>> STATIC_DEFINITIONS = Map.of(
		ITEM_FRAME_ID, ITEM_FRAME_STATE_MANAGER, GLOW_ITEM_FRAME_ID, ITEM_FRAME_STATE_MANAGER
	);
	public static final ModelIdentifier MAP_GLOW_ITEM_FRAME_MODEL_ID = new ModelIdentifier(GLOW_ITEM_FRAME_ID, "map=true");
	public static final ModelIdentifier GLOW_ITEM_FRAME_MODEL_ID = new ModelIdentifier(GLOW_ITEM_FRAME_ID, "map=false");
	public static final ModelIdentifier MAP_ITEM_FRAME_MODEL_ID = new ModelIdentifier(ITEM_FRAME_ID, "map=true");
	public static final ModelIdentifier ITEM_FRAME_MODEL_ID = new ModelIdentifier(ITEM_FRAME_ID, "map=false");
	private final UnbakedModel missingModel;

	public BlockStatesLoader(UnbakedModel missingModel) {
		this.missingModel = missingModel;
	}

	public static Function<Identifier, StateManager<Block, BlockState>> getIdToStatesConverter() {
		Map<Identifier, StateManager<Block, BlockState>> map = new HashMap(STATIC_DEFINITIONS);

		for (Block block : Registries.BLOCK) {
			map.put(block.getRegistryEntry().registryKey().getValue(), block.getStateManager());
		}

		return map::get;
	}

	public BlockStatesLoader.BlockStateDefinition combine(
		Identifier id, StateManager<Block, BlockState> stateManager, List<BlockStatesLoader.PackBlockStateDefinition> definitions
	) {
		List<BlockState> list = stateManager.getStates();
		Map<BlockState, BlockStatesLoader.BlockModel> map = new HashMap();
		Map<ModelIdentifier, BlockStatesLoader.BlockModel> map2 = new HashMap();

		try {
			for (BlockStatesLoader.PackBlockStateDefinition packBlockStateDefinition : definitions) {
				packBlockStateDefinition.contents
					.parse(stateManager, id + "/" + packBlockStateDefinition.source)
					.forEach((state, model) -> map.put(state, new BlockStatesLoader.BlockModel(state, model)));
			}
		} finally {
			Iterator var12 = list.iterator();

			while (true) {
				if (!var12.hasNext()) {
					;
				} else {
					BlockState blockState2 = (BlockState)var12.next();
					ModelIdentifier modelIdentifier2 = BlockModels.getModelId(id, blockState2);
					BlockStatesLoader.BlockModel blockModel2 = (BlockStatesLoader.BlockModel)map.get(blockState2);
					if (blockModel2 == null) {
						LOGGER.warn("Missing blockstate definition: '{}' missing model for variant: '{}'", id, modelIdentifier2);
						blockModel2 = new BlockStatesLoader.BlockModel(blockState2, this.missingModel);
					}

					map2.put(modelIdentifier2, blockModel2);
				}
			}
		}

		return new BlockStatesLoader.BlockStateDefinition(map2);
	}

	@Environment(EnvType.CLIENT)
	public static record BlockModel(BlockState state, UnbakedModel model) {
	}

	@Environment(EnvType.CLIENT)
	public static record BlockStateDefinition(Map<ModelIdentifier, BlockStatesLoader.BlockModel> models) {
	}

	@Environment(EnvType.CLIENT)
	public static record PackBlockStateDefinition(String source, ModelVariantMap contents) {
	}
}
