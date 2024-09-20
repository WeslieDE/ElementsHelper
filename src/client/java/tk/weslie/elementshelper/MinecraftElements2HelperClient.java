package tk.weslie.elementshelper;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.client.player.ClientPlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.listener.GameEventListener;


import java.util.HashSet;
import java.util.Set;

public class MinecraftElements2HelperClient implements ClientModInitializer  {
	private static final Set<Block> UNBREAKABLE_BLOCKS = new HashSet<>();

	static {
		UNBREAKABLE_BLOCKS.add(Blocks.KELP_PLANT);
		UNBREAKABLE_BLOCKS.add(Blocks.KELP);
		UNBREAKABLE_BLOCKS.add(Blocks.PUMPKIN_STEM);
		UNBREAKABLE_BLOCKS.add(Blocks.AMETHYST_BLOCK);
		UNBREAKABLE_BLOCKS.add(Blocks.TORCHFLOWER);
	}

	@Override
	public void onInitializeClient() {
		AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
			BlockState state = world.getBlockState(pos);

			if(player.getMainHandStack().getItem() == Items.STICK)
				return ActionResult.PASS;

			if (UNBREAKABLE_BLOCKS.contains(state.getBlock())) {
				BlockPos belowPos = pos.down();
				BlockState belowState = world.getBlockState(belowPos);

				if(!world.isAir(pos.down())){
					if (!UNBREAKABLE_BLOCKS.contains(belowState.getBlock())) {
						player.sendMessage(Text.of("Dieser Block kann nicht abgebaut werden!"), true);
						return ActionResult.FAIL;
					}
				}
			}

            return ActionResult.PASS;
        });
	}
}
