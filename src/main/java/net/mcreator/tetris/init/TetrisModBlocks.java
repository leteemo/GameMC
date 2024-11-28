
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.tetris.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.level.block.Block;

import net.mcreator.tetris.block.TetrisBlock;
import net.mcreator.tetris.TetrisMod;

public class TetrisModBlocks {
	public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, TetrisMod.MODID);
	public static final RegistryObject<Block> TETRIS = REGISTRY.register("tetris", () -> new TetrisBlock());
}
