/**
 * The code of this mod element is always locked.
 *
 * You can register new events in this class too.
 *
 * If you want to make a plain independent class, create it using
 * Project Browser -> New... and make sure to make the class
 * outside net.mcreator.test as this package is managed by MCreator.
 *
 * If you change workspace package, modid or prefix, you will need
 * to manually adapt this file to these changes or remake it.
 *
 * This class will be added in the mod root package.
*/
package net.mcreator.test;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraft.world.level.block.state.BlockState;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class BlocInfo {
	public double x;
	public double y;
	public double z;
	public BlockState _bs;

	public BlocInfo() {
	}

	public BlocInfo(double x, double y, double z, BlockState _bs) {
		this.x = x;
		this.y = y;
		this.z = z;
		this._bs = _bs;
	}

	@SubscribeEvent
	public static void init(FMLCommonSetupEvent event) {
		new BlocInfo();
	}

	@Mod.EventBusSubscriber
	private static class ForgeBusEvents {
		@SubscribeEvent
		public static void serverLoad(ServerStartingEvent event) {
		}

		@OnlyIn(Dist.CLIENT)
		@SubscribeEvent
		public static void clientLoad(FMLClientSetupEvent event) {
		}
	}
}
