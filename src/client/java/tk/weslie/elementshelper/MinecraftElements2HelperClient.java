package tk.weslie.elementshelper;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.client.player.ClientPlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
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
import org.lwjgl.glfw.GLFW;


import java.util.HashSet;
import java.util.Set;

public class MinecraftElements2HelperClient implements ClientModInitializer  {
	private static final Set<Block> UNBREAKABLE_BLOCKS = new HashSet<>();
	private static final Set<Block> FARMLAND_BLOCKS = new HashSet<>();

	private static KeyBinding autoHitKeybind;
	private static long keyPressTimeout = 0;
	private static boolean isFarmingModeEnabled = true;

	static {
		UNBREAKABLE_BLOCKS.add(Blocks.KELP_PLANT);
		UNBREAKABLE_BLOCKS.add(Blocks.KELP);
		UNBREAKABLE_BLOCKS.add(Blocks.PUMPKIN_STEM);
		UNBREAKABLE_BLOCKS.add(Blocks.ATTACHED_PUMPKIN_STEM);
		UNBREAKABLE_BLOCKS.add(Blocks.AMETHYST_BLOCK);
		UNBREAKABLE_BLOCKS.add(Blocks.TORCHFLOWER);
		UNBREAKABLE_BLOCKS.add(Blocks.BAMBOO);
		UNBREAKABLE_BLOCKS.add(Blocks.BAMBOO_SAPLING);

		FARMLAND_BLOCKS.add(Blocks.MUD);
		FARMLAND_BLOCKS.add(Blocks.FARMLAND);
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
			if(!isFarmingModeEnabled)
				return ActionResult.PASS;

			BlockState state = world.getBlockState(pos);

			if(player.getMainHandStack().getItem() == Items.STICK)
				return ActionResult.PASS;

			if (FARMLAND_BLOCKS.contains(state.getBlock())) {
				player.sendMessage(Text.of("Farmland kann nicht abgebaut werden!"), true);
				return ActionResult.FAIL;
			}

			if (UNBREAKABLE_BLOCKS.contains(state.getBlock())) {
				BlockPos belowPos = pos.down();
				BlockState belowState = world.getBlockState(belowPos);

				if (!UNBREAKABLE_BLOCKS.contains(belowState.getBlock()) || world.isAir(belowPos)) {
					player.sendMessage(Text.of("Dieser Block kann nicht abgebaut werden!"), true);
					return ActionResult.FAIL;
				}
			}

            return ActionResult.PASS;
        });
	}
}