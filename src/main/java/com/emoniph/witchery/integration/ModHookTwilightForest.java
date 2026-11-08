package com.emoniph.witchery.integration;

import com.emoniph.witchery.blocks.BlockAltar;
import com.emoniph.witchery.entity.EntityCovenWitch;
import com.emoniph.witchery.util.Log;
import cpw.mods.fml.common.registry.EntityRegistry;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.biome.BiomeGenBase;
import twilightforest.biomes.TFBiomeBase;
import twilightforest.block.TFBlocks;

public class ModHookTwilightForest extends ModHook {

   @Override
   public String getModID() {
      return "TwilightForest";
   }

   @Override
   protected void doInit() {
      try {
         // 1. Registro de fuentes de poder sagradas para el Altar de Witchery
         BlockAltar.TileEntityAltar.registerCustomPowerSource(TFBlocks.magicLeaves, 6, 60);
         BlockAltar.TileEntityAltar.registerCustomPowerSource(TFBlocks.magicLog, 5, 40);
         BlockAltar.TileEntityAltar.registerCustomPowerSource(TFBlocks.magicLogSpecial, 8, 20);
         BlockAltar.TileEntityAltar.registerCustomPowerSource(TFBlocks.trophy, 100, 5);
         BlockAltar.TileEntityAltar.registerCustomPowerSource(TFBlocks.trophyPedestal, 15, 5);
         BlockAltar.TileEntityAltar.registerCustomPowerSource(TFBlocks.fireflyJar, 4, 20);
         BlockAltar.TileEntityAltar.registerCustomPowerSource(TFBlocks.cicada, 4, 20);
         BlockAltar.TileEntityAltar.registerCustomPowerSource(TFBlocks.moonworm, 4, 20);
         BlockAltar.TileEntityAltar.registerCustomPowerSource(TFBlocks.thornRose, 5, 20);
         BlockAltar.TileEntityAltar.registerCustomPowerSource(TFBlocks.plant, 4, 30);
         BlockAltar.TileEntityAltar.registerCustomPowerSource(TFBlocks.hugeWaterLily, 3, 20);
         BlockAltar.TileEntityAltar.registerCustomPowerSource(TFBlocks.hugeLilyPad, 3, 20);

         // 2. Spawn natural de Coven Witches en biomas mágicos de Twilight Forest
         EntityRegistry.addSpawn(EntityCovenWitch.class, 2, 1, 1, EnumCreatureType.creature, new BiomeGenBase[]{
            TFBiomeBase.enchantedForest,
            TFBiomeBase.darkForest,
            TFBiomeBase.fireflyForest,
            TFBiomeBase.tfSwamp
         });

         Log.instance().debug("Twilight Forest integration initialized for Witchery successfully.");
      } catch (Throwable t) {
         Log.instance().warning(t, "Failed to initialize Twilight Forest integration hooks");
      }
   }

   @Override
   protected void doPostInit() {
   }

   @Override
   protected void doReduceMagicPower(EntityLivingBase entity, float factor) {
   }
}
