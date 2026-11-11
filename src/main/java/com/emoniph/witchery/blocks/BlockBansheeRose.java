package com.emoniph.witchery.blocks;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.blocks.BlockBaseContainer;
import com.emoniph.witchery.blocks.TileEntityBase;
import com.emoniph.witchery.util.ParticleEffect;
import com.emoniph.witchery.util.SoundEffect;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;

public class BlockBansheeRose extends BlockBaseContainer implements IPlantable {

   public BlockBansheeRose() {
      super(Material.plants, BlockBansheeRose.TileEntityBansheeRose.class);
      this.setHardness(0.0F);
      this.setStepSound(Block.soundTypeGrass);
      this.setBlockBounds(0.3F, 0.0F, 0.3F, 0.7F, 0.8F, 0.7F);
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public boolean renderAsNormalBlock() {
      return false;
   }

   @Override
   public int getRenderType() {
      return 1;
   }

   @Override
   public EnumPlantType getPlantType(IBlockAccess world, int x, int y, int z) {
      return EnumPlantType.Plains;
   }

   @Override
   public Block getPlant(IBlockAccess world, int x, int y, int z) {
      return this;
   }

   @Override
   public int getPlantMetadata(IBlockAccess world, int x, int y, int z) {
      return world.getBlockMetadata(x, y, z);
   }

   @Override
   public boolean canPlaceBlockAt(World world, int x, int y, int z) {
      return super.canPlaceBlockAt(world, x, y, z) && this.canBlockStay(world, x, y, z);
   }

   @Override
   public boolean canBlockStay(World world, int x, int y, int z) {
      Block soil = world.getBlock(x, y - 1, z);
      return soil == Blocks.grass || soil == Blocks.dirt || soil == Blocks.farmland || soil == Blocks.mycelium || soil == Blocks.soul_sand;
   }

   @Override
   public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor) {
      super.onNeighborBlockChange(world, x, y, z, neighbor);
      this.checkAndDropBlock(world, x, y, z);
   }

   protected void checkAndDropBlock(World world, int x, int y, int z) {
      if(!this.canBlockStay(world, x, y, z)) {
         this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
         world.setBlock(x, y, z, Blocks.air, 0, 2);
      }
   }

   @SideOnly(Side.CLIENT)
   @Override
   public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
      double px = (double)x + 0.3D + (double)(rand.nextFloat() * 0.4F);
      double py = (double)y + 0.5D + (double)(rand.nextFloat() * 0.3F);
      double pz = (double)z + 0.3D + (double)(rand.nextFloat() * 0.4F);
      ParticleEffect.PORTAL.send(SoundEffect.NONE, world, px, py, pz, 0.05D, 0.05D, 1);
   }

   public static class TileEntityBansheeRose extends TileEntityBase {

      private int cooldown = 0;

      @Override
      public void updateEntity() {
         super.updateEntity();
         if(this.cooldown > 0) {
            --this.cooldown;
         }

         if(!super.worldObj.isRemote && this.cooldown <= 0 && super.ticks % 20L == 0L) {
            AxisAlignedBB bb = AxisAlignedBB.getBoundingBox((double)(super.xCoord - 5), (double)(super.yCoord - 2), (double)(super.zCoord - 5), (double)(super.xCoord + 6), (double)(super.yCoord + 3), (double)(super.zCoord + 6));
            List entities = super.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, bb);
            boolean triggered = false;

            for(Object obj : entities) {
               EntityLivingBase living = (EntityLivingBase)obj;
               if(living instanceof IMob || (living instanceof EntityPlayer && !((EntityPlayer)living).capabilities.isCreativeMode && !living.isSneaking())) {
                  living.addPotionEffect(new PotionEffect(Potion.blindness.id, 100, 0));
                  living.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 100, 1));
                  triggered = true;
               }
            }

            if(triggered) {
               super.worldObj.playSoundEffect((double)super.xCoord + 0.5D, (double)super.yCoord + 0.5D, (double)super.zCoord + 0.5D, "witchery:mob.banshee.banshee_scream", 1.0F, 1.0F);
               ParticleEffect.MOB_SPELL.send(SoundEffect.NONE, super.worldObj, (double)super.xCoord + 0.5D, (double)super.yCoord + 0.6D, (double)super.zCoord + 0.5D, 0.5D, 0.5D, 16);
               this.cooldown = 240; // 12 seconds cooldown
            }
         }
      }

      @Override
      public void writeToNBT(NBTTagCompound nbt) {
         super.writeToNBT(nbt);
         nbt.setInteger("Cooldown", this.cooldown);
      }

      @Override
      public void readFromNBT(NBTTagCompound nbt) {
         super.readFromNBT(nbt);
         this.cooldown = nbt.getInteger("Cooldown");
      }
   }
}
