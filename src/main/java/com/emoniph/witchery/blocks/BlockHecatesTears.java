package com.emoniph.witchery.blocks;

import com.emoniph.witchery.WitcheryCreativeTab;
import com.emoniph.witchery.util.BlockUtil;
import com.emoniph.witchery.util.ParticleEffect;
import com.emoniph.witchery.util.SoundEffect;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLilyPad;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class BlockHecatesTears extends BlockLilyPad {

   public BlockHecatesTears() {
      this.setHardness(0.0F);
      this.setLightLevel(0.4F);
      this.setStepSound(Block.soundTypeGrass);
      this.setTickRandomly(true);
      this.setBlockBounds(0.2F, 0.0F, 0.2F, 0.8F, 0.8F, 0.8F);
      this.setCreativeTab(WitcheryCreativeTab.INSTANCE);
   }

   @Override
   public int getRenderType() {
      return 1;
   }

   @Override
   public Block setBlockName(String blockName) {
      BlockUtil.registerBlock(this, blockName);
      return super.setBlockName(blockName);
   }

   @Override
   protected boolean canPlaceBlockOn(Block block) {
      return block != null && block.getMaterial() != null && (block.getMaterial().isSolid() || block.getMaterial() == Material.water);
   }

   @Override
   public boolean canPlaceBlockAt(World world, int x, int y, int z) {
      Material material = world.getBlock(x, y, z).getMaterial();
      return super.canPlaceBlockAt(world, x, y, z) && material != null && !material.isLiquid();
   }

   @Override
   public boolean canBlockStay(World world, int x, int y, int z) {
      Material material = world.getBlock(x, y - 1, z).getMaterial();
      return material != null && (material.isSolid() || material.isLiquid()) && world.isAirBlock(x, y + 1, z);
   }

   @Override
   public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
      if(!world.isRemote && entity instanceof EntityLivingBase) {
         EntityLivingBase living = (EntityLivingBase)entity;
         if(living.isPotionActive(Potion.poison)) {
            living.removePotionEffect(Potion.poison.id);
            ParticleEffect.SPLASH.send(SoundEffect.RANDOM_ORB, world, (double)x + 0.5D, (double)y + 0.2D, (double)z + 0.5D, 0.5D, 0.5D, 8);
         }
         if(living.isPotionActive(Potion.wither)) {
            living.removePotionEffect(Potion.wither.id);
            ParticleEffect.SPLASH.send(SoundEffect.RANDOM_ORB, world, (double)x + 0.5D, (double)y + 0.2D, (double)z + 0.5D, 0.5D, 0.5D, 8);
         }
         if(!living.isPotionActive(Potion.regeneration)) {
            living.addPotionEffect(new PotionEffect(Potion.regeneration.id, 60, 0));
         }
      }
   }

   @Override
   public void updateTick(World world, int x, int y, int z, Random rand) {
      super.updateTick(world, x, y, z, rand);
      if(!world.isRemote) {
         AxisAlignedBB bb = AxisAlignedBB.getBoundingBox((double)(x - 2), (double)(y - 1), (double)(z - 2), (double)(x + 3), (double)(y + 2), (double)(z + 3));
         List entities = world.getEntitiesWithinAABB(EntityLivingBase.class, bb);
         for(Object obj : entities) {
            EntityLivingBase living = (EntityLivingBase)obj;
            if(living.isPotionActive(Potion.poison)) {
               living.removePotionEffect(Potion.poison.id);
            }
            if(living.isPotionActive(Potion.wither)) {
               living.removePotionEffect(Potion.wither.id);
            }
         }
      }
   }

   @SideOnly(Side.CLIENT)
   @Override
   public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
      double px = (double)x + 0.3D + (double)(rand.nextFloat() * 0.4F);
      double py = (double)y + 0.3D + (double)(rand.nextFloat() * 0.2F);
      double pz = (double)z + 0.3D + (double)(rand.nextFloat() * 0.4F);
      ParticleEffect.INSTANT_SPELL.send(SoundEffect.NONE, world, px, py, pz, 0.05D, 0.05D, 1);
   }
}
