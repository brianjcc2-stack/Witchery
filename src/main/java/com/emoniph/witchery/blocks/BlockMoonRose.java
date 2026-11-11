package com.emoniph.witchery.blocks;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.blocks.BlockBaseBush;
import com.emoniph.witchery.util.ParticleEffect;
import com.emoniph.witchery.util.SoundEffect;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockMoonRose extends BlockBaseBush {

   public BlockMoonRose() {
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
         if(!w.isDaytime()) {
            return 11;
         }
      }
      return 2;
   }

   @SideOnly(Side.CLIENT)
   @Override
   public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
      if(!world.isDaytime() || world.rand.nextInt(3) == 0) {
         double px = (double)x + 0.3D + (double)(rand.nextFloat() * 0.4F);
         double py = (double)y + 0.5D + (double)(rand.nextFloat() * 0.3F);
         double pz = (double)z + 0.3D + (double)(rand.nextFloat() * 0.4F);
         ParticleEffect.INSTANT_SPELL.send(SoundEffect.NONE, world, px, py, pz, 0.1D, 0.1D, 1);
      }
   }

   @Override
   public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
      if(!world.isRemote && !world.isDaytime()) {
         ParticleEffect.MAGIC_CRIT.send(SoundEffect.RANDOM_ORB, world, (double)x + 0.5D, (double)y + 0.6D, (double)z + 0.5D, 0.3D, 0.3D, 4);
      }
      return super.onBlockActivated(world, x, y, z, player, side, hitX, hitY, hitZ);
   }
}
