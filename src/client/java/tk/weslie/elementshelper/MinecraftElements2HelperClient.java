package tk.weslie.elementshelper;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.lwjgl.glfw.GLFW;


import java.util.HashSet;
import java.util.Set;

public class MinecraftElements2HelperClient implements ClientModInitializer  {
	private static final Set<Block> WHITELIST_BLOCKS = new HashSet<>();

	private static KeyBinding autoHitKeybind;
	private static long keyPressTimeout = 0;
	private static boolean isFarmingModeEnabled = true;

	static {

		//Stage 1
		WHITELIST_BLOCKS.add(Blocks.COBBLESTONE);

		//Stage 2
		WHITELIST_BLOCKS.add(Blocks.KELP);
		WHITELIST_BLOCKS.add(Blocks.KELP_PLANT);

		//Stage 3
		WHITELIST_BLOCKS.add(Blocks.PUMPKIN);

		//Stage 4
		WHITELIST_BLOCKS.add(Blocks.AMETHYST_CLUSTER);
		WHITELIST_BLOCKS.add(Blocks.LARGE_AMETHYST_BUD);
		WHITELIST_BLOCKS.add(Blocks.MEDIUM_AMETHYST_BUD);
		WHITELIST_BLOCKS.add(Blocks.SMALL_AMETHYST_BUD);

		//Stage 5
		WHITELIST_BLOCKS.add(Blocks.BAMBOO);

		//Stage 6
		WHITELIST_BLOCKS.add(Blocks.TORCHFLOWER);
	}


	@Override
	public void onInitializeClient() {
		autoHitKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"Toggle Farming Mode",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_F7,
				"Helper Mods"
		));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			try{
				if(autoHitKeybind == null)
					return;

				while (autoHitKeybind.wasPressed()) {
					if (keyPressTimeout + 2000 < System.currentTimeMillis()) {
						keyPressTimeout = System.currentTimeMillis();
						isFarmingModeEnabled = !isFarmingModeEnabled;
						client.player.sendMessage(Text.of(isFarmingModeEnabled ? "Farming-Mode aktiviert!" : "Farming-Mode deaktiviert!"), true);
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		});

		AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
			if(!isFarmingModeEnabled) //Wenn Farming Mode off dann erlaube immer abbauen.
				return ActionResult.PASS;

			BlockState state = world.getBlockState(pos);
			BlockPos belowPos = pos.down();
			BlockState belowState = world.getBlockState(belowPos);

			//Wenn der block nicht in der whitelist ist, verbiete das abbauen.
			if (!WHITELIST_BLOCKS.contains(state.getBlock()))
			{
				player.sendMessage(Text.of("Du kannst diesen Block nicht abbauen!"), true);
				return ActionResult.FAIL;
			}

			//Wenn es der unterste block von Kelp oder Bambus ist, verbiete das abbauen.
			if((state.getBlock().equals(Blocks.BAMBOO) || state.getBlock().equals(Blocks.KELP) || state.getBlock().equals(Blocks.KELP_PLANT)) && !WHITELIST_BLOCKS.contains(belowState.getBlock()))
			{
				player.sendMessage(Text.of("Du kannst diesen Block nicht abbauen!"), true);
				return ActionResult.FAIL;
			}

			return ActionResult.PASS;
        });
	}
}