package com.emoniph.witchery.blocks;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.blocks.BlockBaseContainer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockBarrier extends BlockBaseContainer {

   public static void setBlock(World world, int posX, int posY, int posZ, int ticksUntilExpiration, boolean blocksPlayers, EntityPlayer owner) {
      setBlock(world, posX, posY, posZ, ticksUntilExpiration, blocksPlayers, owner, false);
   }

   public static void setBlock(World world, int posX, int posY, int posZ, int ticksUntilExpiration, boolean blocksPlayers, EntityPlayer owner, boolean skipCreate) {
      if(!skipCreate) {
         world.setBlock(posX, posY, posZ, Witchery.Blocks.BARRIER, 0, 3);
      }

      TileEntity entity = world.getTileEntity(posX, posY, posZ);
      if(entity != null && entity instanceof BlockBarrier.TileEntityBarrier) {
         BlockBarrier.TileEntityBarrier tileEntity = (BlockBarrier.TileEntityBarrier)entity;
         tileEntity.setTicksUntilExpiration(ticksUntilExpiration);
         tileEntity.setBlocksPlayers(blocksPlayers);
         tileEntity.setOwner(owner);
      }

   }

   public BlockBarrier() {
      super(Material.glass, BlockBarrier.TileEntityBarrier.class);
      super.registerWithCreateTab = true;
      this.setBlockUnbreakable();
      this.setResistance(1000.0F);
      this.setLightOpacity(0);
   }

   public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int posX, int posY, int posZ) {
      TileEntity te = world.getTileEntity(posX, posY, posZ);
      if(te != null && te instanceof BlockBarrier.TileEntityBarrier) {
         BlockBarrier.TileEntityBarrier f = (BlockBarrier.TileEntityBarrier)te;
         if(!f.getBlocksPlayers()) {
            return null;
         }
         AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox((double)posX, (double)posY, (double)posZ, (double)(posX + 1), (double)(posY + 1), (double)(posZ + 1)).expand(1.2D, 1.2D, 1.2D);
         List players = world.getEntitiesWithinAABB(EntityPlayer.class, bounds);
         Iterator i$ = players.iterator();

         while(i$.hasNext()) {
            EntityPlayer player = (EntityPlayer)i$.next();
            if(player != null && (player.capabilities.isCreativeMode && player.isSneaking() || f.isOwner(player))) {
               return null;
            }
         }
      }

      float f1 = 0.0625F;
      return AxisAlignedBB.getBoundingBox((double)((float)posX + 0.0625F), (double)((float)posY + 0.0625F), (double)((float)posZ + 0.0625F), (double)((float)(posX + 1) - 0.0625F), (double)((float)(posY + 1) - 0.0625F), (double)((float)(posZ + 1) - 0.0625F));
   }

   protected boolean canSilkHarvest() {
      return false;
   }

   public int quantityDropped(Random rand) {
      return 0;
   }

   @Override
   public int getRenderType() {
      return 0;
   }

   @SideOnly(Side.CLIENT)
   @Override
   public int getRenderBlockPass() {
      return 1;
   }

   @SideOnly(Side.CLIENT)
   @Override
   public boolean shouldSideBeRendered(IBlockAccess blockAccess, int posX, int posY, int posZ, int side) {
      Block i1 = blockAccess.getBlock(posX, posY, posZ);
      return i1 == this ? false : true;
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
   public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
      return new ItemStack(this);
   }

   public static class TileEntityBarrier extends TileEntity {

      private int ticksUntilExpiration = 60;
      private boolean blocksPlayers;
      private String ownerName = "";
      private static final String KEY_REMAINING_TICKS = "remainingTicks";
      private static final String KEY_BLOCKS_PLAYERS = "blocksPlayers";
      private static final String KEY_OWNER_NAME = "owner";


      public void updateEntity() {
         super.updateEntity();
         if(--this.ticksUntilExpiration <= 0 && !super.worldObj.isRemote) {
            super.worldObj.setBlock(super.xCoord, super.yCoord, super.zCoord, Blocks.air, 0, 3);
         }

      }

      public void writeToNBT(NBTTagCompound nbtTag) {
         super.writeToNBT(nbtTag);
         nbtTag.setInteger("remainingTicks", this.ticksUntilExpiration);
         nbtTag.setBoolean("blocksPlayers", this.blocksPlayers);
         nbtTag.setString("owner", this.ownerName);
      }

      public void readFromNBT(NBTTagCompound nbtTag) {
         super.readFromNBT(nbtTag);
         this.ticksUntilExpiration = nbtTag.getInteger("remainingTicks");
         this.blocksPlayers = nbtTag.getBoolean("blocksPlayers");
         this.ownerName = nbtTag.getString("owner");
      }

      public void setTicksUntilExpiration(int ticksUntilExpiration) {
         this.ticksUntilExpiration = ticksUntilExpiration;
      }

      public void setBlocksPlayers(boolean blocksPlayers) {
         this.blocksPlayers = blocksPlayers;
      }

      public boolean getBlocksPlayers() {
         return this.blocksPlayers;
      }

      public void setOwner(EntityPlayer owner) {
         this.ownerName = owner != null?owner.getCommandSenderName():"";
      }

      public boolean isOwner(EntityPlayer player) {
         return player != null && player.getCommandSenderName().equals(this.ownerName);
      }
   }
}
