package net.mcreator.test.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.ServerChatEvent;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class EProcedure {
	@SubscribeEvent
	public static void onChat(ServerChatEvent.Submitted event) {
		execute(event, event.getPlayer());
	}

	public static void execute(Entity entity) {
		execute(null, entity);
	}

	private static void execute(@Nullable Event event, Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof Player _player) {
			_player.getAbilities().mayfly = (true);
			_player.onUpdateAbilities();
		}
		entity.setDeltaMovement(new Vec3(0, (entity.getDeltaMovement().y()), 0));
	}
}
