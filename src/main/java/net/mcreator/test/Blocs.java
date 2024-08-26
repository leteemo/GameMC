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
import net.minecraft.core.BlockPos;
import java.util.ArrayList;
import net.mcreator.test.procedures.ClavierProcedure;

import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;

import java.util.Map;

import java.util.concurrent.TimeUnit;
import java.util.Random;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;

import net.mcreator.test.BlocInfo;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class Blocs {
	public ArrayList<BlockPos> blocs = new ArrayList<BlockPos>();
	public double moveXDirection = 0;
	public double turn = 0;
	public boolean stop = false;
	public boolean add_new = true;
	public String type = "";

	public Blocs() {
	}

	@SubscribeEvent
	public static void init(FMLCommonSetupEvent event) {
		new Blocs();
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

	public void displayAll(){
		for(BlockPos bloc: blocs){
			ClavierProcedure.world.setBlock(bloc, Blocks.EXPOSED_CUT_COPPER.defaultBlockState(), 3);
		}
	}

	public void addBloc(BlockPos pos){
		blocs.add(pos);
	}

	public void deleteBloc(BlockPos pos){
		blocs.remove(pos);
	}

	public BlockPos calculerCentre() {
		double sumX = 0;
		double sumY = 0;
		double sumZ = 0;
		int n = blocs.size();
	
		for (BlockPos p : blocs) {
			sumX += p.getX();
			sumY += p.getY();
			sumZ += p.getZ();
		}
	
		return new BlockPos((int)Math.round(sumX / n), (int)Math.round(sumY / n), (int)Math.round(sumZ / n));
	}
	
	public void tournerPoints(double angle) {
		if (angle != 0) {
			BlockPos centre = calculerCentre();
			double angleRad = Math.toRadians(angle);
	
			double centreX = centre.getX();
			double centreY = centre.getY();
	
			for (int i = 0; i < blocs.size(); i++) {
				BlockPos bloc = blocs.get(i);
				double x = bloc.getX() - centreX;
				double y = bloc.getY() - centreY;
	
				double newX = x * Math.cos(angleRad) - y * Math.sin(angleRad) + centreX;
				double newY = x * Math.sin(angleRad) + y * Math.cos(angleRad) + centreY;
	
				blocs.set(i, new BlockPos((int)Math.round(newX), (int)Math.round(newY), bloc.getZ()));
			}
		}
	}

	public ArrayList<BlockPos> getRotatedPoints(double angle) {
		ArrayList<BlockPos> list_blocs = new ArrayList<BlockPos>();
		if(angle != 0){
			BlockPos centre = calculerCentre();
			double angleRad = Math.toRadians(angle);
			
			int i = 0;

			for (BlockPos bloc : blocs) {
				double x = bloc.getX() - centre.getX();
				double y = bloc.getY() - centre.getY();

				double newX = (double) (x * Math.cos(angleRad) - y * Math.sin(angleRad)) + centre.getX();
				double newY = (double) (x * Math.sin(angleRad) + y * Math.cos(angleRad)) + centre.getY();

				list_blocs.add(new BlockPos(newX, newY, bloc.getZ()));

				i++;
			}
		}
		
		return list_blocs;
    }

	public void move(int step){
		if(ClavierProcedure.verifyPlayZone(getMoveData(this.blocs, step))){
			new Thread(() -> ClavierProcedure.moveAllBlocs(step, 0, false)).start();
		}
	}

	public ArrayList<BlockPos> getMoveData(ArrayList<BlockPos> list, int step){
		ArrayList<BlockPos> blocs_moved_right = new ArrayList<>();
		for(BlockPos bloc: blocs){
			blocs_moved_right.add(new BlockPos(bloc.getX() + step, bloc.getY(), bloc.getZ()));
		}
		return blocs_moved_right;
	}
	

	public void moveDown(int moveX, int moveY){
		int i = 0;
		
		for(BlockPos bloc: this.blocs){
			if(!(ClavierProcedure.world.getBlockState(new BlockPos(bloc.getX(), bloc.getY()+moveY, bloc.getZ())) == Blocks.AIR.defaultBlockState() || coordInBlocs(bloc.getX()+moveX, bloc.getY()+moveY, bloc.getZ()))) {
				stop = true;
			}
			if(!(ClavierProcedure.world.getBlockState(new BlockPos(bloc.getX()+moveX, bloc.getY()+moveY, bloc.getZ())) == Blocks.AIR.defaultBlockState())){
				moveX = 0;
			}
		}
		
		if(!stop){
			for(BlockPos bloc: this.blocs){
				blocs.set(i, new BlockPos(bloc.getX()+moveX, bloc.getY()+moveY, bloc.getZ()));
				i++;
			}
		}

		if(stop && add_new){
			new Thread(() -> ClavierProcedure.newBloc2(this)).start();
			add_new = false;
			//ClavierProcedure.removeBlocs(this);
		}

	}

	public boolean coordInBlocs(double x, double y, double z){
		for(BlockPos bloc: this.blocs){
			if(bloc.getX() == x && bloc.getY() == y && bloc.getZ() == z){
				return true;
			}
		}
		return false;
	}


	public void setL(){
		this.addBloc(0, 0, 0);
		this.addBloc(0, -1, 0);
		this.addBloc(0, -2, 0);
		this.addBloc(1, -2, 0);
		this.type = "L";
	}

	public void setT(){
		this.addBloc(0, 0, 0);
		this.addBloc(1, 0, 0);
		this.addBloc(-1, 0, 0);
		this.addBloc(0, 1, 0);
		this.type = "T";
	}

	public void setI(){
		this.addBloc(0, -3, 0);
		this.addBloc(0, -2, 0);
		this.addBloc(0, -1, 0);
		this.addBloc(0, 0, 0);
		this.type = "I";
	}

	public void setO(){
		this.addBloc(0, 0, 0);
		this.addBloc(0, -1, 0);
		this.addBloc(1, 0, 0);
		this.addBloc(1, -1, 0);
		this.type = "O";
	}

	public void addRandom(){
		Random rand = new Random();
        int random_bloc = rand.nextInt(4);
		switch(random_bloc) {
			case 0:
				this.setT();
				break;
			case 1:
				this.setL();
				break;
			case 2:
				this.setI();
				break;
			case 3:
				this.setO();
				break;
		}

	}


	public void addBloc(double x, double y, double z){

		double x_e = (ClavierProcedure.bloc_balise1.getX() + ClavierProcedure.bloc_balise2.getX()) / 2 ;
		double y_e = ((ClavierProcedure.bloc_balise1.getY() + ClavierProcedure.bloc_balise2.getY()) / 2) + ClavierProcedure.height_spawn + 2;
		double z_e = (ClavierProcedure.bloc_balise1.getZ() + ClavierProcedure.bloc_balise2.getZ()) / 2;

		BlockPos bloc = new BlockPos(x_e + x, y_e + y, z_e + z);
		this.blocs.add(bloc);
	}

	public ArrayList<BlockPos> getPos(){
		return this.blocs;
	}
}
