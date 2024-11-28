package net.mcreator.tetris.network;

import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.FriendlyByteBuf;

import net.mcreator.tetris.procedures.ClavierProcedure;
import net.mcreator.tetris.TetrisMod;

import java.util.function.Supplier;
import net.minecraft.world.level.block.Blocks;

import net.mcreator.tetris.procedures.ClavierProcedure;
import net.mcreator.tetris.Blocs;


@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class KeyActivateMessage {
	private int type, pressedms;

	public KeyActivateMessage(int type, int pressedms) {
		this.type = type;
		this.pressedms = pressedms;
	}

	public KeyActivateMessage(FriendlyByteBuf buffer) {
		this.type = buffer.readInt();
		this.pressedms = buffer.readInt();
	}

	public static void buffer(KeyActivateMessage message, FriendlyByteBuf buffer) {
		buffer.writeInt(message.type);
		buffer.writeInt(message.pressedms);
	}

	public static void handler(KeyActivateMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> {
			pressAction(context.getSender(), message.type, message.pressedms);
		});
		context.setPacketHandled(true);
	}

	public static void pressAction(Player entity, int type, int pressedms) {
		Level world = entity.level;
		// security measure to prevent arbitrary chunk generation
		if (!world.hasChunkAt(entity.blockPosition()))
			return;
		if (type == 0) {
			if (!entity.level.isClientSide()){
				ClavierProcedure.tpVillagerTo(ClavierProcedure.highestBlock());
			}
		}
		if (type == 1) {
		}
	}

	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		TetrisMod.addNetworkMessage(KeyActivateMessage.class, KeyActivateMessage::buffer, KeyActivateMessage::new, KeyActivateMessage::handler);
	}
}
