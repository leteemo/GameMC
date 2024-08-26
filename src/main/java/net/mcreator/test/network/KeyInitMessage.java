
package net.mcreator.test.network;

import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.FriendlyByteBuf;

import net.mcreator.test.procedures.EProcedure;
import net.mcreator.test.TestMod;

import java.util.function.Supplier;

import java.util.ArrayList;
import net.mcreator.test.Blocs;
import net.minecraft.world.level.Level;
import net.mcreator.test.procedures.ClavierProcedure;

import net.minecraft.network.chat.Component;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class KeyInitMessage {
	int type, pressedms;

	public KeyInitMessage(int type, int pressedms) {
		this.type = type;
		this.pressedms = pressedms;
	}

	public KeyInitMessage(FriendlyByteBuf buffer) {
		this.type = buffer.readInt();
		this.pressedms = buffer.readInt();
	}

	public static void buffer(KeyInitMessage message, FriendlyByteBuf buffer) {
		buffer.writeInt(message.type);
		buffer.writeInt(message.pressedms);
	}

	public static void handler(KeyInitMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> {
			pressAction(context.getSender(), message.type, message.pressedms);
		});
		context.setPacketHandled(true);
	}

	public static void pressAction(Player entity, int type, int pressedms) {
		Level world = entity.level;
		double x = entity.getX();
		double y = entity.getY();
		double z = entity.getZ();

		// security measure to prevent arbitrary chunk generation
		if (!world.hasChunkAt(entity.blockPosition()))
			return;
		if (type == 0) {

			if (!entity.level.isClientSide()){
				ClavierProcedure.execute(entity);
				ClavierProcedure.spawnVillager();
			}

		}
	}

	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		TestMod.addNetworkMessage(KeyInitMessage.class, KeyInitMessage::buffer, KeyInitMessage::new, KeyInitMessage::handler);
	}
}
