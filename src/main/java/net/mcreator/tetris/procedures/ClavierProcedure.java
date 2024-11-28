package net.mcreator.tetris.procedures;

import net.minecraftforge.server.ServerLifecycleHooks;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.level.BlockEvent;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.MinecraftServer;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;

import java.util.ArrayList;
import net.mcreator.tetris.Blocs;
import net.minecraft.world.level.Level;

import net.mcreator.tetris.BlocInfo;
import net.minecraft.world.level.block.state.BlockState;
import java.util.Map;
import net.minecraft.world.level.block.state.properties.Property;
import java.util.concurrent.TimeUnit;

import java.util.HashMap;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.LinkedList;
import java.util.Queue;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import net.mcreator.tetris.init.TetrisModBlocks;
import net.minecraft.core.particles.ParticleTypes;


// Gère les actions en fonction des touches du clavier (equivaut à une factory)
@Mod.EventBusSubscriber
public class ClavierProcedure {

	public static LevelAccessor world;
	public static Entity entity;
	public static ArrayList<Blocs> blocs = new ArrayList<Blocs>();
	public static String direction = "none";
	public static boolean play = false;
	public static BlockPos highest_bp;

	public static BlockPos bloc_balise1;
	public static BlockPos bloc_balise2;
	
	public static int nb_balises = 0;
	public static Entity villager;
	public static ArrayList<BlockPos> play_zone = new ArrayList<BlockPos>();
	public static int height_spawn = 14;


	public static void spawnVillager() {
		ClavierProcedure.highestBlock();
		if (world instanceof ServerLevel _level) {
			ClavierProcedure.villager = new Villager(EntityType.VILLAGER, _level);
			ClavierProcedure.villager.moveTo(highest_bp.getX(), highest_bp.getY()+2, highest_bp.getZ(), world.getRandom().nextFloat() * 360F, 0);
			if (villager instanceof Mob _mobToSpawn)
				_mobToSpawn.finalizeSpawn(_level, world.getCurrentDifficultyAt(villager.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
			world.addFreshEntity(villager);
		}

		//	Entity _ent = ClavierProcedure.entity;
		//	_ent.teleportTo(x, y, z);
		//	if (_ent instanceof ServerPlayer _serverPlayer)
		//		_serverPlayer.connection.teleport(x, y, z, _ent.getYRot(), _ent.getXRot());
		//}
	}


	public static void goTo(Entity ent){
		if (ClavierProcedure.villager instanceof Mob _entity){
			boolean mvTo = _entity.getNavigation().moveTo(ent.getX(), ent.getY(), ent.getZ(), 1);
			if (ClavierProcedure.entity instanceof Player _player){
				_player.displayClientMessage(Component.literal(String.valueOf(mvTo)), (false));
			}
		}
	}

	public static void goTo(BlockPos b){

		BlockPos bloc = ClavierProcedure.highestBlock();

		if (ClavierProcedure.villager instanceof Mob _entity){

			boolean mvTo =_entity.getNavigation().moveTo(bloc.getX(), bloc.getY(), bloc.getZ(), 1);

		}

			//_entity.setDeltaMovement(new Vec3(0.3, 0.3, 0.3));
	}

	public static void tpVillagerTo(BlockPos b){
		if (world instanceof ServerLevel _level){
			_level.sendParticles(ParticleTypes.PORTAL, ClavierProcedure.villager.getX(), ClavierProcedure.villager.getY(), ClavierProcedure.villager.getZ(), 200, 1, 1, 1, 0.1);
			ClavierProcedure.villager.moveTo(highest_bp.getX(), highest_bp.getY()+2, highest_bp.getZ(), world.getRandom().nextFloat() * 360F, 0);
		}
	}

	

	public static BlockPos highestBlock(){
		ClavierProcedure.highest_bp = ClavierProcedure.bloc_balise1;

		for(BlockPos bloc: ClavierProcedure.play_zone){

				if(!coordInBlocs(ClavierProcedure.blocs, bloc) && ClavierProcedure.world.getBlockState(bloc) != Blocks.AIR.defaultBlockState()){
					if(bloc.getY() > highest_bp.getY()){
						ClavierProcedure.highest_bp = bloc;
					}
				}
			
		}
		return ClavierProcedure.highest_bp;
	}


	public static void removeTetris() {
		ClavierProcedure.play = false;
		ClavierProcedure.blocs = new ArrayList<Blocs>();
	}


	public static void activateTetris(Entity entity, BlockPos pos) {
		if (entity instanceof Player _player && !_player.level.isClientSide()){
			
			ClavierProcedure.execute(entity);
			bloc_balise1 = new BlockPos(pos.getX()-3, pos.getY(), pos.getZ());
			bloc_balise2 = new BlockPos(pos.getX()+3, pos.getY(), pos.getZ());
			ClavierProcedure.prepareField();

			if(!play){
				play = true;
				ClavierProcedure.setPlayZone();
				ClavierProcedure.newBloc();
				ClavierProcedure.moveAllBlocs(0, -1, true);
			}
		}
	}


	public static void prepareField(){
		ClavierProcedure.addWall();
		for(int y_coord = bloc_balise1.getY()+1; y_coord<bloc_balise1.getY() + height_spawn + 4; y_coord++){
			if(y_coord == bloc_balise1.getY()+1){
				for(int x_init = bloc_balise1.getX(); x_init < bloc_balise2.getX(); x_init++){
					replaceBloc(new BlocInfo(x_init, y_coord, bloc_balise1.getZ(), Blocks.EMERALD_ORE.defaultBlockState()));
				}
			}
			else{
				for(int x_init = bloc_balise1.getX(); x_init < bloc_balise2.getX(); x_init++){
					if(ClavierProcedure.world.getBlockState(new BlockPos(x_init, y_coord, bloc_balise1.getZ())) != Blocks.AIR.defaultBlockState()){
						replaceBloc(new BlocInfo(x_init, y_coord, bloc_balise1.getZ(), Blocks.AIR.defaultBlockState()));
					}
				}
			}
		}
	}

	public static void addWall(){
		for(int y_coord = bloc_balise1.getY()+1; y_coord<bloc_balise1.getY() + height_spawn + 4; y_coord++){
			
			if(isAir(new BlockPos(bloc_balise1.getX()-1, y_coord, bloc_balise1.getZ())))
			replaceBloc(new BlocInfo(bloc_balise1.getX()-1, y_coord, bloc_balise1.getZ(), Blocks.BARRIER.defaultBlockState()));
			if(isAir(new BlockPos(bloc_balise2.getX(), y_coord, bloc_balise1.getZ())))
			replaceBloc(new BlocInfo(bloc_balise2.getX(), y_coord, bloc_balise1.getZ(), Blocks.BARRIER.defaultBlockState()));


			for(int x_init = bloc_balise1.getX(); x_init < bloc_balise2.getX(); x_init++){
				if(isAir(new BlockPos(x_init, y_coord, bloc_balise1.getZ()-1)))
				replaceBloc(new BlocInfo(x_init, y_coord, bloc_balise1.getZ()-1, Blocks.BARRIER.defaultBlockState()));
				if(isAir(new BlockPos(x_init, y_coord, bloc_balise1.getZ()+1)))
				replaceBloc(new BlocInfo(x_init, y_coord, bloc_balise1.getZ()+1, Blocks.BARRIER.defaultBlockState()));
			}
		}
	}

	public static boolean isAir(BlockPos bloc){
		return (ClavierProcedure.world.getBlockState(bloc) == Blocks.AIR.defaultBlockState());
	}


    public static int haveNeighbor(BlockPos blockPos) {
        return haveNeighborHelper(blockPos, 50); // Appel initial avec limite de 50 itérations
    }

    private static int haveNeighborHelper(BlockPos startPos, int maxIterations) {
        Queue<BlockPos> queue = new LinkedList<>();
        Set<BlockPos> visited = new HashSet<>(); // Ensemble pour garder une trace des positions visitées
        queue.add(startPos);
        visited.add(startPos);
        int iterations = 0;

        while (!queue.isEmpty() && iterations < maxIterations) {
            int queueSize = queue.size(); // Taille actuelle de la file d'attente pour cette itération
            for (int i = 0; i < queueSize; i++) {
                BlockPos currentPos = queue.poll();
                iterations++;

                BlockPos[] neighbors = {
                    new BlockPos(currentPos.getX(), currentPos.getY() + 1, currentPos.getZ()),
                    new BlockPos(currentPos.getX(), currentPos.getY() - 1, currentPos.getZ()),
                    new BlockPos(currentPos.getX() + 1, currentPos.getY(), currentPos.getZ()),
                    new BlockPos(currentPos.getX() - 1, currentPos.getY(), currentPos.getZ())
                };

                for (BlockPos neighbor : neighbors) {
                    if (!visited.contains(neighbor) && ClavierProcedure.world.getBlockState(neighbor) != Blocks.AIR.defaultBlockState() && ClavierProcedure.world.getBlockState(neighbor) != Blocks.BARRIER.defaultBlockState()) {
                        queue.add(neighbor);
                        visited.add(neighbor);
                    }
                }
            }
        }

        return iterations;
    }

	/*@SubscribeEvent
	public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
		BlockPos pos = new BlockPos(event.getPos().getX(), event.getPos().getY(), event.getPos().getZ());
		if(event.getLevel().getBlockState(pos) ==  Blocks.BEACON.defaultBlockState()){
			if(nb_balises % 2 == 0){
				bloc_balise1 = pos;
			}
			else{
				bloc_balise2 = pos;
			}
			
			nb_balises++;

			if(bloc_balise1 != null && bloc_balise2 != null){
				if(bloc_balise1.getX() > bloc_balise2.getX()){
					BlockPos bloc_balise = bloc_balise2;
					bloc_balise2 = bloc_balise1;
					bloc_balise1 = bloc_balise;
				}
			}


		}
		
	}*/

	public static void newBloc(){

		Blocs new_blocs = new Blocs();
		ClavierProcedure.blocs.add(new_blocs);
		new_blocs.addRandom();
		new_blocs.displayAll();
	}

	public static void newBloc2(Blocs bloc){
		if (ClavierProcedure.entity instanceof Player _player && !_player.level.isClientSide()){
			try{
				TimeUnit.MILLISECONDS.sleep(50);
				Blocs new_blocs = new Blocs();
				ClavierProcedure.blocs.add(new_blocs);
				new_blocs.addRandom();
				new_blocs.displayAll();
				detect();
				removeBlocs(bloc);
			}
			catch(Exception e) {
			}

		}
	}
	

	public static void detect(){

		for(int y_coord = bloc_balise1.getY()+2; y_coord<bloc_balise1.getY() + height_spawn + 2; y_coord++){
			boolean full = true;
			for(int x_init = bloc_balise1.getX(); x_init < bloc_balise2.getX(); x_init++){

				if(ClavierProcedure.world.getBlockState(new BlockPos(x_init, y_coord, bloc_balise1.getZ())) == Blocks.AIR.defaultBlockState()){
					full = false;
				}
			}

			if(full){
				if (ClavierProcedure.entity instanceof Player _player && !_player.level.isClientSide()){
					_player.displayClientMessage(Component.literal("full: " + String.valueOf(y_coord)), (false));
				}
				for(int x_init = bloc_balise1.getX(); x_init < bloc_balise2.getX(); x_init++){
					removeCoord(new BlockPos(x_init, y_coord, bloc_balise1.getZ()));
					replaceBloc(new BlocInfo(x_init, y_coord, bloc_balise1.getZ(), Blocks.AIR.defaultBlockState()));
				}
			}
		}
	}

	public static void fallRemaining(){

		for(int y_coord = bloc_balise1.getY() + 2; y_coord<bloc_balise1.getY() + height_spawn; y_coord++){
			boolean full = true;

			for(int x_init = bloc_balise1.getX(); x_init < bloc_balise2.getX(); x_init++){
				if(!coordInBlocs(blocs, new BlockPos(x_init, y_coord, bloc_balise1.getZ()))){
					if(ClavierProcedure.world.getBlockState(new BlockPos(x_init, y_coord, bloc_balise1.getZ())) != Blocks.AIR.defaultBlockState() &&
					ClavierProcedure.world.getBlockState(new BlockPos(x_init, y_coord, bloc_balise1.getZ())) != Blocks.BARRIER.defaultBlockState()){

						if(ClavierProcedure.world.getBlockState(new BlockPos(x_init, y_coord-1, bloc_balise1.getZ())) == Blocks.AIR.defaultBlockState()){
							//if (ClavierProcedure.entity instanceof Player _player && !_player.level.isClientSide()){
							//	_player.displayClientMessage(Component.literal("fall: " + String.valueOf(haveNeighbor(new BlockPos(x_init, y_coord, bloc_balise1.getZ())))), (false));
							//}
							if(haveNeighbor(new BlockPos(x_init, y_coord, bloc_balise1.getZ())) < 50){

								replaceBloc(new BlocInfo(x_init, y_coord-1, bloc_balise1.getZ(), ClavierProcedure.world.getBlockState(new BlockPos(x_init, y_coord, bloc_balise1.getZ()))));
								replaceBloc(new BlocInfo(x_init, y_coord, bloc_balise1.getZ(), Blocks.AIR.defaultBlockState()));
							}
						}

					}
				}
			}

		}
	}

	public static void removeCoord(BlockPos pos){
		for(Blocs bloc: ClavierProcedure.blocs){
			bloc.deleteBloc(pos);
		}
	}

	public static void removeBlocs(Blocs bloc){
		if (ClavierProcedure.entity instanceof Player _player && !_player.level.isClientSide()){
			try{
				TimeUnit.MILLISECONDS.sleep(50);
				ClavierProcedure.blocs.remove(bloc);

			}

			catch(Exception e) {
			}
		}
		
	}

	public static void initWorld(LevelAccessor w){
		ClavierProcedure.world = w;
	}

	public static void initEntity(Entity e){
		ClavierProcedure.entity = e;
	}

	public static void displayAllBlocs(){
		for(Blocs bloc: blocs){
			bloc.displayAll();
		}
	}

	public static void moveAllBlocs(int moveX, int moveY, boolean repeat){
		if(play){
			for(Blocs bloc: blocs){
			
				if (entity instanceof Player _player && !_player.level.isClientSide()){
					removeBlocks(bloc);
					bloc.moveDown(moveX, moveY);
					displayBlocks(bloc);
				}
			}
			try{
				TimeUnit.MILLISECONDS.sleep(500);
				fallRemaining();
				if(repeat){
					new Thread(() -> ClavierProcedure.moveAllBlocs(0, -1, true)).start();
				}
			}
			catch(Exception e) {
			}
		}
	}

	public static void setPlayZone(){
		for(int y_coord = bloc_balise1.getY()+2; y_coord< bloc_balise1.getY() + height_spawn + 2; y_coord++){
			for(int x_init = bloc_balise1.getX(); x_init < bloc_balise2.getX(); x_init++){
				play_zone.add(new BlockPos(x_init, y_coord, bloc_balise2.getZ()));
			}
		}
	}

	public static boolean verifyPlayZone(ArrayList<BlockPos> list_blocs){
		for(BlockPos bloc: list_blocs){

			if(!play_zone.contains(bloc)){
				return false;
			}
		}
		return true;
	}

	public static boolean coordInBlockPosList(ArrayList<BlockPos> list, BlockPos pos){
		for(BlockPos bloc: list){
			if(bloc.getX() == pos.getX() && bloc.getY() == pos.getY() && bloc.getZ() == pos.getZ()){
				return true;
			}
		}
		return false;
	}

	public static boolean atLeastOneCoordsInBlocs(ArrayList<Blocs> list_blocs, ArrayList<BlockPos> pos){
		for(BlockPos bloc: pos){
			if(coordInBlocs(list_blocs, bloc)){
				return true;
			}
		}
		return false;
	}

	public static boolean coordInBlocs(ArrayList<Blocs> list_blocs, BlockPos pos){
		for(Blocs list: list_blocs){
			for(BlockPos bloc: list.blocs){
				if(bloc.getX() == pos.getX() && bloc.getY() == pos.getY() && bloc.getZ() == pos.getZ()){
					return true;
				}
			}
		}
		return false;
	}

    public static boolean checkAlignment(ArrayList<Blocs> pointLists) {
        ArrayList<BlockPos> allPoints = new ArrayList<>();

        for (Blocs list : pointLists) {
            allPoints.addAll(list.blocs);
        }

        allPoints.sort((p1, p2) -> Integer.compare(p1.getX(), p2.getX()));

		//if (ClavierProcedure.entity instanceof Player _player && !_player.level.isClientSide()){
		//	_player.displayClientMessage(Component.literal("1:" + String.valueOf(allPoints.get(0)) + ", 2:" + String.valueOf(allPoints.get(1)) + ", 3:" + String.valueOf(allPoints.get(2)) + ", 4:" + String.valueOf(allPoints.get(3))), (false));
		//}

        return alignedOnX(allPoints);

    }
	
	private static boolean alignedOnX(ArrayList<BlockPos> points) {
		int check = 0;
		for(int n=1; n<points.size()-2; n++){
			int add = 0;
			int passage = 0;
			while(passage<2+add){
				if(points.get(n+passage-1).getY() != points.get(n+passage).getY()){
					add++;

					if(points.get(n+passage-(1+add)).getX()+1 != points.get(n+passage).getX()){
						return false;
					}
				}

				else{
					check++;
				}
				passage++;
			}
			if(check >= 2){
				return true;
			}
		}
		if (ClavierProcedure.entity instanceof Player _player && !_player.level.isClientSide()){
			_player.displayClientMessage(Component.literal("fini total"), (false));
		}
		return false;
	}

	public static void turn(){
		if(ClavierProcedure.verifyPlayZone(blocs.get(blocs.size()-1).getRotatedPoints(90))){
			removeBlocks(blocs.get(blocs.size()-1));
			blocs.get(blocs.size()-1).tournerPoints(90);
			//right();
			if(blocs.get(blocs.size()-1).type == "L" || blocs.get(blocs.size()-1).type == "T"){
				displayBlocks(blocs.get(blocs.size()-1));
			}
			else{
				left();
			}
		}
	}

	public static void removeBlocks(Blocs list_blocs){
		for(BlockPos b: list_blocs.blocs){
			replaceBloc(new BlocInfo(b.getX(), b.getY(), b.getZ(), Blocks.AIR.defaultBlockState()));
		}
	}

	public static void displayBlocks(Blocs list_blocs){
		for(BlockPos b: list_blocs.blocs){
			replaceBloc(new BlocInfo(b.getX(), b.getY(), b.getZ(), list_blocs.getActualBlocType()));
		}
	}

	public static void left(){
		blocs.get(blocs.size()-1).move(-1);
	}

	public static void right(){
		blocs.get(blocs.size()-1).move(1);
	}

	public static void replaceBloc(BlocInfo bi){
		double x = bi.x;
		double y = bi.y;
		double z = bi.z;
		BlockState _bs = bi._bs;

		BlockPos _bp = new BlockPos(x, y, z);
		BlockState _bso = ClavierProcedure.world.getBlockState(_bp);
		for (Map.Entry<Property<?>, Comparable<?>> entry : _bso.getValues().entrySet()) {
			Property _property = _bs.getBlock().getStateDefinition().getProperty(entry.getKey().getName());
			if (_property != null && _bs.getValue(_property) != null)
				try {
					_bs = _bs.setValue(_property, (Comparable) entry.getValue());
				} catch (Exception e) {
				}
		}
		ClavierProcedure.world.setBlock(_bp, _bs, 3);
	}


	public static double execute(Entity entity) {
		Level world = entity.level;

		ClavierProcedure.initWorld(world);
		ClavierProcedure.initEntity(entity);
		

		if (entity == null)
			return 0;
		//if (world instanceof ServerLevel _level) {
			//world.setBlock(new BlockPos(entity.getX(), entity.getY(), entity.getZ()+1), Blocks.EXPOSED_CUT_COPPER.defaultBlockState(), 3);
			//FallingBlockEntity.fall(_level, new BlockPos(x, y, z), Blocks.CHISELED_DEEPSLATE.defaultBlockState());
		//}

		try{
			//TimeUnit.MILLISECONDS.sleep(200);
		}
		catch(Exception e) {

		}

		return entity instanceof Player _plr ? _plr.getAbilities().getFlyingSpeed() : 0;
	}
}
