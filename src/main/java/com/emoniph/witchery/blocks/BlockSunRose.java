package com.emoniph.witchery.blocks;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.blocks.BlockBaseBush;
import com.emoniph.witchery.common.ExtendedPlayer;
import com.emoniph.witchery.util.ParticleEffect;
import com.emoniph.witchery.util.SoundEffect;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockSunRose extends BlockBaseBush {

   public BlockSunRose() {
      super(Material.plants);
      this.setHardness(0.0F);
      this.setStepSound(Block.soundTypeGrass);
      this.setTickRandomly(true);
      this.setBlockBounds(0.3F, 0.0F, 0.3F, 0.7F, 0.8F, 0.7F);
   }

   @Override
   public int getLightValue(IBlockAccess world, int x, int y, int z) {
      if(world instanceof World) {
         World w = (World)world;
         if(w.isDaytime() && w.canBlockSeeTheSky(x, y, z)) {
            return 14;
         }
      }
      return 6;
   }

   @SideOnly(Side.CLIENT)
   @Override
   public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
      if(world.isDaytime()) {
         double px = (double)x + 0.3D + (double)(rand.nextFloat() * 0.4F);
         double py = (double)y + 0.5D + (double)(rand.nextFloat() * 0.3F);
         double pz = (double)z + 0.3D + (double)(rand.nextFloat() * 0.4F);
         ParticleEffect.FLAME.send(SoundEffect.NONE, world, px, py, pz, 0.05D, 0.05D, 1);
      }
   }

   @Override
   public void updateTick(World world, int x, int y, int z, Random rand) {
      super.updateTick(world, x, y, z, rand);
      if(!world.isRemote && world.isDaytime()) {
         AxisAlignedBB bb = AxisAlignedBB.getBoundingBox((double)(x - 3), (double)(y - 1), (double)(z - 3), (double)(x + 4), (double)(y + 3), (double)(z + 4));
         List entities = world.getEntitiesWithinAABB(EntityLivingBase.class, bb);
         for(Object obj : entities) {
            EntityLivingBase living = (EntityLivingBase)obj;
            if(living.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD) {
               living.addPotionEffect(new PotionEffect(Potion.weakness.id, 80, 0));
               if(world.canBlockSeeTheSky(x, y, z) && rand.nextInt(3) == 0) {
                  living.setFire(2);
               }
            } else if(living instanceof EntityPlayer) {
               EntityPlayer player = (EntityPlayer)living;
               ExtendedPlayer playerEx = ExtendedPlayer.get(player);
               if(playerEx != null && playerEx.isVampire()) {
                  player.attackEntityFrom(DamageSource.onFire, 1.0F);
                  player.addPotionEffect(new PotionEffect(Potion.blindness.id, 40, 0));
               }
            }
         }
      }
   }

   @Override
   public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
      if(!world.isRemote && entity instanceof EntityLivingBase) {
         EntityLivingBase living = (EntityLivingBase)entity;
         if(living.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD) {
            living.setFire(4);
         } else if(living instanceof EntityPlayer) {
            ExtendedPlayer playerEx = ExtendedPlayer.get((EntityPlayer)living);
            if(playerEx != null && playerEx.isVampire()) {
               living.attackEntityFrom(DamageSource.onFire, 2.0F);
            }
         }
      }
   }
}
