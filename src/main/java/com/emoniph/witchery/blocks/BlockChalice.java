package com.emoniph.witchery.blocks;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.blocks.BlockBaseContainer;
import com.emoniph.witchery.item.ItemGeneral;
import com.emoniph.witchery.util.ParticleEffect;
import com.emoniph.witchery.util.SoundEffect;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockChalice extends BlockBaseContainer {

   public BlockChalice() {
      super(Material.anvil, BlockChalice.TileEntityChalice.class);
      super.registerWithCreateTab = false;
      this.setHardness(3.0F);
      this.setStepSound(Block.soundTypeMetal);
      this.setBlockBounds(0.3F, 0.0F, 0.37F, 0.63F, 0.46F, 0.695F);
   }

   public boolean isOpaqueCube() {
      return false;
   }

   public boolean renderAsNormalBlock() {
      return false;
   }

   public int quantityDropped(Random rand) {
      return 1;
   }

   public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_) {
      return Witchery.Items.GENERIC;
   }

   public int damageDropped(int metadata) {
      return metadata == 1?Witchery.Items.GENERIC.itemChaliceFull.damageValue:Witchery.Items.GENERIC.itemChaliceEmpty.damageValue;
   }

   @Override
   public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
      super.onBlockPlacedBy(world, x, y, z, entity, stack);
      if (stack != null && stack.hasTagCompound()) {
         NBTTagCompound tag = stack.getTagCompound();
         if (tag.hasKey("PosX") && tag.hasKey("PosY")) {
            TileEntity te = world.getTileEntity(x, y, z);
            if (te instanceof TileEntityChalice) {
               ((TileEntityChalice)te).setLocation(tag.getInteger("PosX"), tag.getInteger("PosY"), tag.getInteger("PosZ"), tag.getInteger("PosD"));
            }
         }
      }
   }

   @Override
   public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
      TileEntity te = world.getTileEntity(x, y, z);
      if (te instanceof TileEntityChalice) {
         TileEntityChalice chalice = (TileEntityChalice)te;
         if (chalice.isPortkey()) {
            chalice.teleport(player);
            return true;
         }
      }
      return super.onBlockActivated(world, x, y, z, player, side, hitX, hitY, hitZ);
   }

   @Override
   public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
      super.onEntityCollidedWithBlock(world, x, y, z, entity);
      if (!world.isRemote && entity instanceof EntityPlayer) {
         TileEntity te = world.getTileEntity(x, y, z);
         if (te instanceof TileEntityChalice) {
            TileEntityChalice chalice = (TileEntityChalice)te;
            if (chalice.isPortkey()) {
               chalice.teleport((EntityPlayer)entity);
            }
         }
      }
   }

   @Override
   public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player) {
      super.onBlockClicked(world, x, y, z, player);
      if (!world.isRemote && !player.capabilities.isCreativeMode) {
         TileEntity te = world.getTileEntity(x, y, z);
         if (te instanceof TileEntityChalice) {
            TileEntityChalice chalice = (TileEntityChalice)te;
            if (chalice.isPortkey()) {
               chalice.teleport(player);
            }
         }
      }
   }

   @Override
   public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
      TileEntity te = world.getTileEntity(x, y, z);
      if (te instanceof TileEntityChalice && !world.isRemote) {
         TileEntityChalice chalice = (TileEntityChalice)te;
         if (chalice.isPortkey()) {
            ItemStack drop = (meta == 1 ? Witchery.Items.GENERIC.itemChaliceFull : Witchery.Items.GENERIC.itemChaliceEmpty).createStack();
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger("PosX", chalice.getTargetX());
            tag.setInteger("PosY", chalice.getTargetY());
            tag.setInteger("PosZ", chalice.getTargetZ());
            tag.setInteger("PosD", chalice.getTargetDimension());
            drop.setTagCompound(tag);
            drop.setStackDisplayName("\u00a7eTraslador (Portkey)\u00a7r");
            this.dropBlockAsItem(world, x, y, z, drop);
            super.breakBlock(world, x, y, z, block, meta);
            return;
         }
      }
      super.breakBlock(world, x, y, z, block, meta);
   }

   public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, Block par5) {
      this.func_111046_k(par1World, par2, par3, par4);
   }

   private boolean func_111046_k(World par1World, int par2, int par3, int par4) {
      if(!this.canBlockStay(par1World, par2, par3, par4)) {
         this.dropBlockAsItem(par1World, par2, par3, par4, par1World.getBlockMetadata(par2, par3, par4), 0);
         par1World.setBlockToAir(par2, par3, par4);
         return false;
      } else {
         return true;
      }
   }

   public boolean canBlockStay(World world, int x, int y, int z) {
      Material material = world.getBlock(x, y - 1, z).getMaterial();
      return !world.isAirBlock(x, y - 1, z) && material != null && material.isOpaque() && material.isSolid();
   }

   public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
      TileEntity tileEntity = world.getTileEntity(x, y, z);
      if (tileEntity instanceof TileEntityChalice) {
         TileEntityChalice chalice = (TileEntityChalice)tileEntity;
         ItemStack stack = chalice.isFilled() ? Witchery.Items.GENERIC.itemChaliceFull.createStack() : Witchery.Items.GENERIC.itemChaliceEmpty.createStack();
         if (chalice.isPortkey()) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger("PosX", chalice.getTargetX());
            tag.setInteger("PosY", chalice.getTargetY());
            tag.setInteger("PosZ", chalice.getTargetZ());
            tag.setInteger("PosD", chalice.getTargetDimension());
            stack.setTagCompound(tag);
            stack.setStackDisplayName("\u00a7eTraslador (Portkey)\u00a7r");
         }
         return stack;
      }
      return Witchery.Items.GENERIC.itemChaliceEmpty.createStack();
   }

   @SideOnly(Side.CLIENT)
   public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
      return false;
   }

   @SideOnly(Side.CLIENT)
   public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
      int metadata = world.getBlockMetadata(x, y, z);
      if(metadata == 1) {
         double d0 = (double)((float)x + 0.45F);
         double d1 = (double)((float)y + 0.4F);
         double d2 = (double)((float)z + 0.5F);
         world.spawnParticle(ParticleEffect.REDDUST.toString(), d0, d1, d2, 0.0D, 0.0D, 0.0D);
      }
      TileEntity te = world.getTileEntity(x, y, z);
      if (te instanceof TileEntityChalice && ((TileEntityChalice)te).isPortkey()) {
         double px = (double)x + 0.3D + rand.nextDouble() * 0.4D;
         double py = (double)y + 0.2D + rand.nextDouble() * 0.3D;
         double pz = (double)z + 0.3D + rand.nextDouble() * 0.4D;
         world.spawnParticle("portal", px, py, pz, (rand.nextDouble() - 0.5D) * 0.2D, (rand.nextDouble() - 0.5D) * 0.2D, (rand.nextDouble() - 0.5D) * 0.2D);
      }
   }

   public static class TileEntityChalice extends TileEntity {

      private boolean filled;
      private boolean checkState;
      private boolean isPortkey;
      private int targetX;
      private int targetY;
      private int targetZ;
      private int targetDimension;
      private long lastTeleportTime;
      private static final String FILLED_KEY = "WITCFilled";

      public boolean isFilled() {
         return this.filled;
      }

      public boolean isPortkey() {
         return this.isPortkey;
      }

      public int getTargetX() {
         return this.targetX;
      }

      public int getTargetY() {
         return this.targetY;
      }

      public int getTargetZ() {
         return this.targetZ;
      }

      public int getTargetDimension() {
         return this.targetDimension;
      }

      public void setLocation(int x, int y, int z, int dim) {
         this.isPortkey = true;
         this.targetX = x;
         this.targetY = y;
         this.targetZ = z;
         this.targetDimension = dim;
         if (super.worldObj != null && !super.worldObj.isRemote) {
            super.worldObj.markBlockForUpdate(super.xCoord, super.yCoord, super.zCoord);
            this.markDirty();
         }
      }

      public void teleport(EntityPlayer player) {
         if (this.isPortkey && super.worldObj != null && !super.worldObj.isRemote && player != null) {
            long time = super.worldObj.getTotalWorldTime();
            if (time - this.lastTeleportTime < 20L) {
               return;
            }
            this.lastTeleportTime = time;
            ParticleEffect.PORTAL.send(SoundEffect.MOB_ENDERMEN_PORTAL, super.worldObj, (double)super.xCoord + 0.5D, (double)super.yCoord + 0.5D, (double)super.zCoord + 0.5D, 1.0D, 1.0D, 16);
            boolean safe = ItemGeneral.teleportToLocationSafely(super.worldObj, (double)this.targetX + 0.5D, (double)this.targetY, (double)this.targetZ + 0.5D, this.targetDimension, player, true);
            if (!safe) {
               ItemGeneral.teleportToLocation(super.worldObj, (double)this.targetX + 0.5D, (double)this.targetY + 1.0D, (double)this.targetZ + 0.5D, this.targetDimension, player, true);
            }
         }
      }

      public void updateEntity() {
         if(!this.checkState) {
            this.checkState = true;
            if(this.filled && !super.worldObj.isRemote && super.worldObj.getBlock(super.xCoord, super.yCoord, super.zCoord) == Witchery.Blocks.CHALICE) {
               super.worldObj.setBlockMetadataWithNotify(super.xCoord, super.yCoord, super.zCoord, 1, 3);
            }
         }

         super.updateEntity();
      }

      public void writeToNBT(NBTTagCompound nbtTag) {
         super.writeToNBT(nbtTag);
         nbtTag.setBoolean("WITCFilled", this.filled);
         nbtTag.setBoolean("WITCPortkey", this.isPortkey);
         if (this.isPortkey) {
            nbtTag.setInteger("targetX", this.targetX);
            nbtTag.setInteger("targetY", this.targetY);
            nbtTag.setInteger("targetZ", this.targetZ);
            nbtTag.setInteger("targetDimension", this.targetDimension);
         }
      }

      public void readFromNBT(NBTTagCompound nbtTag) {
         super.readFromNBT(nbtTag);
         if(nbtTag.hasKey("WITCFilled")) {
            this.filled = nbtTag.getBoolean("WITCFilled");
         }
         if (nbtTag.hasKey("WITCPortkey")) {
            this.isPortkey = nbtTag.getBoolean("WITCPortkey");
            this.targetX = nbtTag.getInteger("targetX");
            this.targetY = nbtTag.getInteger("targetY");
            this.targetZ = nbtTag.getInteger("targetZ");
            this.targetDimension = nbtTag.getInteger("targetDimension");
         } else if (nbtTag.hasKey("PosX") && nbtTag.hasKey("PosY")) {
            this.isPortkey = true;
            this.targetX = nbtTag.getInteger("PosX");
            this.targetY = nbtTag.getInteger("PosY");
            this.targetZ = nbtTag.getInteger("PosZ");
            this.targetDimension = nbtTag.getInteger("PosD");
         }
      }

      public Packet getDescriptionPacket() {
         NBTTagCompound nbtTag = new NBTTagCompound();
         this.writeToNBT(nbtTag);
         return new S35PacketUpdateTileEntity(super.xCoord, super.yCoord, super.zCoord, 1, nbtTag);
      }

      public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
         super.onDataPacket(net, packet);
         this.readFromNBT(packet.func_148857_g());
         super.worldObj.func_147479_m(super.xCoord, super.yCoord, super.zCoord);
      }

      public void setFilled(boolean filled) {
         if(!super.worldObj.isRemote) {
            this.filled = filled;
            super.worldObj.markBlockForUpdate(super.xCoord, super.yCoord, super.zCoord);
            if(super.worldObj.getBlock(super.xCoord, super.yCoord, super.zCoord) == Witchery.Blocks.CHALICE) {
               super.worldObj.setBlockMetadataWithNotify(super.xCoord, super.yCoord, super.zCoord, filled?1:0, 3);
            }
         }
      }
   }
}
