
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.tetris.init;

import org.lwjgl.glfw.GLFW;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;

import net.mcreator.tetris.network.KeyTurnMessage;
import net.mcreator.tetris.network.KeyRightMessage;
import net.mcreator.tetris.network.KeyLeftMessage;
import net.mcreator.tetris.network.KeyInitMessage;
import net.mcreator.tetris.network.KeyActivateMessage;
import net.mcreator.tetris.TetrisMod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = {Dist.CLIENT})
public class TetrisModKeyMappings {
	public static final KeyMapping KEY_RIGHT = new KeyMapping("key.tetris.key_right", GLFW.GLFW_KEY_RIGHT, "key.categories.misc") {
		private boolean isDownOld = false;

		@Override
		public void setDown(boolean isDown) {
			super.setDown(isDown);
			if (isDownOld != isDown && isDown) {
				TetrisMod.PACKET_HANDLER.sendToServer(new KeyRightMessage(0, 0));
				KeyRightMessage.pressAction(Minecraft.getInstance().player, 0, 0);
				KEY_RIGHT_LASTPRESS = System.currentTimeMillis();
			} else if (isDownOld != isDown && !isDown) {
				int dt = (int) (System.currentTimeMillis() - KEY_RIGHT_LASTPRESS);
				TetrisMod.PACKET_HANDLER.sendToServer(new KeyRightMessage(1, dt));
				KeyRightMessage.pressAction(Minecraft.getInstance().player, 1, dt);
			}
			isDownOld = isDown;
		}
	};
	public static final KeyMapping KEY_LEFT = new KeyMapping("key.tetris.key_left", GLFW.GLFW_KEY_LEFT, "key.categories.misc") {
		private boolean isDownOld = false;

		@Override
		public void setDown(boolean isDown) {
			super.setDown(isDown);
			if (isDownOld != isDown && isDown) {
				TetrisMod.PACKET_HANDLER.sendToServer(new KeyLeftMessage(0, 0));
				KeyLeftMessage.pressAction(Minecraft.getInstance().player, 0, 0);
				KEY_LEFT_LASTPRESS = System.currentTimeMillis();
			} else if (isDownOld != isDown && !isDown) {
				int dt = (int) (System.currentTimeMillis() - KEY_LEFT_LASTPRESS);
				TetrisMod.PACKET_HANDLER.sendToServer(new KeyLeftMessage(1, dt));
				KeyLeftMessage.pressAction(Minecraft.getInstance().player, 1, dt);
			}
			isDownOld = isDown;
		}
	};
	public static final KeyMapping KEY_INIT = new KeyMapping("key.tetris.key_init", GLFW.GLFW_KEY_H, "key.categories.misc") {
		private boolean isDownOld = false;

		@Override
		public void setDown(boolean isDown) {
			super.setDown(isDown);
			if (isDownOld != isDown && isDown) {
				TetrisMod.PACKET_HANDLER.sendToServer(new KeyInitMessage(0, 0));
				KeyInitMessage.pressAction(Minecraft.getInstance().player, 0, 0);
			}
			isDownOld = isDown;
		}
	};
	public static final KeyMapping KEY_ACTIVATE = new KeyMapping("key.tetris.key_activate", GLFW.GLFW_KEY_G, "key.categories.creative") {
		private boolean isDownOld = false;

		@Override
		public void setDown(boolean isDown) {
			super.setDown(isDown);
			if (isDownOld != isDown && isDown) {
				TetrisMod.PACKET_HANDLER.sendToServer(new KeyActivateMessage(0, 0));
				KeyActivateMessage.pressAction(Minecraft.getInstance().player, 0, 0);
				KEY_ACTIVATE_LASTPRESS = System.currentTimeMillis();
			} else if (isDownOld != isDown && !isDown) {
				int dt = (int) (System.currentTimeMillis() - KEY_ACTIVATE_LASTPRESS);
				TetrisMod.PACKET_HANDLER.sendToServer(new KeyActivateMessage(1, dt));
				KeyActivateMessage.pressAction(Minecraft.getInstance().player, 1, dt);
			}
			isDownOld = isDown;
		}
	};
	public static final KeyMapping KEY_TURN = new KeyMapping("key.tetris.key_turn", GLFW.GLFW_KEY_J, "key.categories.misc") {
		private boolean isDownOld = false;

		@Override
		public void setDown(boolean isDown) {
			super.setDown(isDown);
			if (isDownOld != isDown && isDown) {
				TetrisMod.PACKET_HANDLER.sendToServer(new KeyTurnMessage(0, 0));
				KeyTurnMessage.pressAction(Minecraft.getInstance().player, 0, 0);
			}
			isDownOld = isDown;
		}
	};
	private static long KEY_RIGHT_LASTPRESS = 0;
	private static long KEY_LEFT_LASTPRESS = 0;
	private static long KEY_ACTIVATE_LASTPRESS = 0;

	@SubscribeEvent
	public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
		event.register(KEY_RIGHT);
		event.register(KEY_LEFT);
		event.register(KEY_INIT);
		event.register(KEY_ACTIVATE);
		event.register(KEY_TURN);
	}

	@Mod.EventBusSubscriber({Dist.CLIENT})
	public static class KeyEventListener {
		@SubscribeEvent
		public static void onClientTick(TickEvent.ClientTickEvent event) {
			if (Minecraft.getInstance().screen == null) {
				KEY_RIGHT.consumeClick();
				KEY_LEFT.consumeClick();
				KEY_INIT.consumeClick();
				KEY_ACTIVATE.consumeClick();
				KEY_TURN.consumeClick();
			}
		}
	}
}
