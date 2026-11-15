package com.emoniph.witchery.item;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.WitcheryCreativeTab;
import com.emoniph.witchery.blocks.BlockMirror;
import com.emoniph.witchery.common.ExtendedPlayer;
import com.emoniph.witchery.entity.EntityMirrorClone;
import com.emoniph.witchery.entity.EntityReflection;
import com.emoniph.witchery.util.Config;
import com.emoniph.witchery.util.Coord;
import com.emoniph.witchery.util.ItemUtil;
import com.emoniph.witchery.util.ParticleEffect;
import com.emoniph.witchery.util.SoundEffect;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;

public class ItemQuicksilverMirror extends Item {

   private static final int COOLDOWN_TICKS = 900; // 45 seconds

   public ItemQuicksilverMirror() {
      this.setMaxStackSize(1);
      this.setMaxDamage(64);
      this.setCreativeTab(WitcheryCreativeTab.INSTANCE);
   }

   public Item setUnlocalizedName(String itemName) {
      ItemUtil.registerItem(this, itemName);
      return super.setUnlocalizedName(itemName);
   }

   @Override
   public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
      Block block = world.getBlock(x, y, z);
      if (block == Witchery.Blocks.MIRROR || block == Witchery.Blocks.MIRROR_UNBREAKABLE) {
         if (!world.isRemote) {
            int meta = world.getBlockMetadata(x, y, z);
            int mirrorTopY = BlockMirror.isBlockTopOfMirror(meta) ? y : y + 1;

            TileEntity te = world.getTileEntity(x, mirrorTopY, z);
            if (te instanceof BlockMirror.TileEntityMirror) {
               BlockMirror.TileEntityMirror tile = (BlockMirror.TileEntityMirror) te;
               if (stack.getTagCompound() == null) {
                  stack.setTagCompound(new NBTTagCompound());
               }

               NBTTagCompound tag = stack.getTagCompound();
               if (world.provider.dimensionId != Config.instance().dimensionMirrorID) {
                  Coord dimCoords = tile.getDimCoords();
                  if (dimCoords != null) {
                     tag.setInteger("MainMirrorDim", world.provider.dimensionId);
                     tag.setInteger("MainMirrorX", x);
                     tag.setInteger("MainMirrorY", mirrorTopY);
                     tag.setInteger("MainMirrorZ", z);
                     tag.setInteger("RoomX", dimCoords.x);
                     tag.setInteger("RoomY", dimCoords.y);
                     tag.setInteger("RoomZ", dimCoords.z);
                     tag.setBoolean("BoundMirror", true);

                     ParticleEffect.SPELL_COLORED.send(SoundEffect.RANDOM_GLASS, world, (double) x + 0.5D, (double) y + 1.0D, (double) z + 0.5D, 0.8D, 1.2D, 16, 0x88CCFF);
                     com.emoniph.witchery.util.ChatUtil.sendTranslated(EnumChatFormatting.AQUA, player, "witchery.quicksilvermirror.bound", new Object[]{Integer.valueOf(dimCoords.x), Integer.valueOf(dimCoords.y), Integer.valueOf(dimCoords.z)});
                     return true;
                  }
               } else {
                  tag.setInteger("RoomX", x);
                  tag.setInteger("RoomY", mirrorTopY);
                  tag.setInteger("RoomZ", z);
                  tag.setBoolean("BoundMirror", true);

                  ParticleEffect.SPELL_COLORED.send(SoundEffect.RANDOM_GLASS, world, (double) x + 0.5D, (double) y + 1.0D, (double) z + 0.5D, 0.8D, 1.2D, 16, 0x88CCFF);
                  com.emoniph.witchery.util.ChatUtil.sendTranslated(EnumChatFormatting.AQUA, player, "witchery.quicksilvermirror.bound", new Object[]{Integer.valueOf(x), Integer.valueOf(mirrorTopY), Integer.valueOf(z)});
                  return true;
               }
            }
         }
         return true;
      }
      return false;
   }

   @Override
   public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
      if (!world.isRemote) {
         if (stack.getTagCompound() == null) {
            stack.setTagCompound(new NBTTagCompound());
         }

         NBTTagCompound tag = stack.getTagCompound();

         // SNEAK + RIGHT CLICK: Teleport to / from Mirror World
         if (player.isSneaking()) {
            if (player.dimension != Config.instance().dimensionMirrorID) {
               int roomX;
               int roomY;
               int roomZ;

               if (tag.getBoolean("BoundMirror")) {
                  roomX = tag.getInteger("RoomX");
                  roomY = tag.getInteger("RoomY");
                  roomZ = tag.getInteger("RoomZ");
               } else {
                  ExtendedPlayer ep = ExtendedPlayer.get(player);
                  Coord entry = ep != null ? ep.getMirrorWorldEntryPoint() : null;
                  if (entry != null) {
                     roomX = entry.x;
                     roomY = entry.y;
                     roomZ = entry.z;
                  } else {
                     SoundEffect.NOTE_SNARE.playOnlyTo(player, 1.0F, 0.5F);
                     com.emoniph.witchery.util.ChatUtil.sendTranslated(EnumChatFormatting.RED, player, "witchery.quicksilvermirror.nobound", new Object[0]);
                     return stack;
                  }
               }

               // Save return point
               tag.setInteger("ReturnDim", player.dimension);
               tag.setDouble("ReturnX", player.posX);
               tag.setDouble("ReturnY", player.posY);
               tag.setDouble("ReturnZ", player.posZ);
               tag.setFloat("ReturnYaw", player.rotationYaw);
               tag.setFloat("ReturnPitch", player.rotationPitch);
               tag.setBoolean("HasReturn", true);

               ExtendedPlayer ep = ExtendedPlayer.get(player);
               if (ep != null) {
                  ep.setMirrorWorldEntryPoint(roomX, roomY, roomZ);
               }

               WorldServer mworld = MinecraftServer.getServer().worldServerForDimension(Config.instance().dimensionMirrorID);
               if (mworld != null) {
                  TileEntity mte = mworld.getTileEntity(roomX, roomY, roomZ);
                  if (mte instanceof BlockMirror.TileEntityMirror) {
                     BlockMirror.TileEntityMirror mMirror = (BlockMirror.TileEntityMirror) mte;
                     if (!mMirror.isDemonKilled()) {
                        double rX = (double) roomX + 1.5D;
                        double rY = (double) (roomY - 1) + 0.01D;
                        double rZ = (double) roomZ + 0.5D;
                        AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(rX - 4.0D, rY - 4.0D, rZ - 4.0D, rX + 4.0D, rY + 4.0D, rZ + 4.0D);
                        List refls = mworld.getEntitiesWithinAABB(EntityReflection.class, aabb);
                        if (refls.isEmpty()) {
                           EntityReflection refl = new EntityReflection(mworld);
                           refl.setPositionAndRotation(rX, rY + 1.0D, rZ, 0.0F, 0.0F);
                           refl.func_110163_bv();
                           mworld.spawnEntityInWorld(refl);
                        }
                     }
                  }
               }

               double targetX = (double) roomX + 1.5D;
               double targetY = (double) (roomY - 1) + 0.01D;
               double targetZ = (double) roomZ + 0.5D;

               ParticleEffect.SPLASH.send(SoundEffect.RANDOM_SPLASH, player, 0.5D, 2.0D, 16);
               ItemGeneral.travelToDimension(player, Config.instance().dimensionMirrorID);
               player.setPositionAndUpdate(targetX, targetY, targetZ);
               player.rotationYaw = 270.0F;
               ParticleEffect.SPLASH.send(SoundEffect.RANDOM_SPLASH, player, 0.5D, 2.0D, 16);

               stack.damageItem(1, player);
               com.emoniph.witchery.util.ChatUtil.sendTranslated(EnumChatFormatting.AQUA, player, "witchery.quicksilvermirror.teleportto", new Object[0]);
               return stack;
            } else {
               // Returning from Mirror World
               int destDim = 0;
               double destX = 0.5D;
               double destY = 64.0D;
               double destZ = 0.5D;
               float destYaw = player.rotationYaw;
               float destPitch = player.rotationPitch;

               if (tag.getBoolean("HasReturn")) {
                  destDim = tag.getInteger("ReturnDim");
                  destX = tag.getDouble("ReturnX");
                  destY = tag.getDouble("ReturnY");
                  destZ = tag.getDouble("ReturnZ");
                  destYaw = tag.getFloat("ReturnYaw");
                  destPitch = tag.getFloat("ReturnPitch");
               } else if (tag.getBoolean("BoundMirror")) {
                  destDim = tag.getInteger("MainMirrorDim");
                  destX = (double) tag.getInteger("MainMirrorX") + 0.5D;
                  destY = (double) tag.getInteger("MainMirrorY");
                  destZ = (double) tag.getInteger("MainMirrorZ") + 0.5D;
               } else {
                  ChunkCoordinates bed = player.getBedLocation(0);
                  if (bed != null) {
                     WorldServer overworld = MinecraftServer.getServer().worldServerForDimension(0);
                     ChunkCoordinates bedSpawn = Blocks.bed.getBedSpawnPosition(overworld, bed.posX, bed.posY, bed.posZ, player);
                     if (bedSpawn != null) {
                        destDim = 0;
                        destX = (double) bedSpawn.posX + 0.5D;
                        destY = (double) bedSpawn.posY + 0.1D;
                        destZ = (double) bedSpawn.posZ + 0.5D;
                     } else {
                        destDim = 0;
                        destX = (double) bed.posX + 0.5D;
                        destY = (double) bed.posY + 0.1D;
                        destZ = (double) bed.posZ + 0.5D;
                     }
                  } else {
                     WorldServer overworld = MinecraftServer.getServer().worldServerForDimension(0);
                     ChunkCoordinates spawn = overworld.getSpawnPoint();
                     destDim = 0;
                     destX = (double) spawn.posX + 0.5D;
                     destY = (double) spawn.posY + 0.1D;
                     destZ = (double) spawn.posZ + 0.5D;
                  }
               }

               ParticleEffect.SPLASH.send(SoundEffect.RANDOM_SPLASH, player, 0.5D, 2.0D, 16);
               ItemGeneral.travelToDimension(player, destDim);
               player.setPositionAndUpdate(destX, destY, destZ);
               player.rotationYaw = destYaw;
               player.rotationPitch = destPitch;
               ParticleEffect.SPLASH.send(SoundEffect.RANDOM_SPLASH, player, 0.5D, 2.0D, 16);

               stack.damageItem(1, player);
               com.emoniph.witchery.util.ChatUtil.sendTranslated(EnumChatFormatting.AQUA, player, "witchery.quicksilvermirror.teleportfrom", new Object[0]);
               return stack;
            }
         }

         // NORMAL RIGHT CLICK: summon holographic clone
         long readyTime = tag.getLong("ReadyTime");
         long now = world.getTotalWorldTime();

         if (now < readyTime && !player.capabilities.isCreativeMode) {
            long remainingSecs = (readyTime - now) / 20L + 1L;
            SoundEffect.NOTE_SNARE.playOnlyTo(player, 1.0F, 0.5F);
            com.emoniph.witchery.util.ChatUtil.sendTranslated(EnumChatFormatting.RED, player, "witchery.quicksilvermirror.cooldown", new Object[]{Long.valueOf(remainingSecs).toString()});
            return stack;
         }

         double angle = Math.toRadians((double)player.rotationYaw);
         double forwardX = -Math.sin(angle) * 1.5D;
         double forwardZ = Math.cos(angle) * 1.5D;

         EntityMirrorClone clone = new EntityMirrorClone(world, player);
         clone.setPosition(player.posX + forwardX, player.posY, player.posZ + forwardZ);
         world.spawnEntityInWorld(clone);

         ParticleEffect.SPELL_COLORED.send(SoundEffect.RANDOM_GLASS, world, clone.posX, clone.posY + 1.0D, clone.posZ, 0.8D, 1.2D, 16, 0x88CCFF);
         com.emoniph.witchery.util.ChatUtil.sendTranslated(EnumChatFormatting.AQUA, player, "witchery.quicksilvermirror.summoned", new Object[0]);
         tag.setLong("ReadyTime", now + COOLDOWN_TICKS);
         stack.damageItem(1, player);
      }

      player.swingItem();
      return stack;
   }

   @SideOnly(Side.CLIENT)
   @Override
   public EnumRarity getRarity(ItemStack stack) {
      return EnumRarity.rare;
   }

   @SideOnly(Side.CLIENT)
   @Override
   public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
      list.add(EnumChatFormatting.LIGHT_PURPLE + StatCollector.translateToLocal("witchery.quicksilvermirror.tip1"));
      list.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("witchery.quicksilvermirror.tip2"));
      list.add(EnumChatFormatting.GOLD + StatCollector.translateToLocal("witchery.quicksilvermirror.tip3"));

      if (stack.getTagCompound() != null) {
         NBTTagCompound tag = stack.getTagCompound();
         if (tag.getBoolean("BoundMirror")) {
            list.add(EnumChatFormatting.AQUA + StatCollector.translateToLocalFormatted("witchery.quicksilvermirror.boundinfo", Integer.valueOf(tag.getInteger("RoomX")), Integer.valueOf(tag.getInteger("RoomY")), Integer.valueOf(tag.getInteger("RoomZ"))));
         } else {
            list.add(EnumChatFormatting.DARK_GRAY + StatCollector.translateToLocal("witchery.quicksilvermirror.notboundinfo"));
         }

         long readyTime = tag.getLong("ReadyTime");
         long now = player.worldObj.getTotalWorldTime();
         if (now < readyTime) {
            long remaining = (readyTime - now) / 20L + 1L;
            list.add(EnumChatFormatting.RED + StatCollector.translateToLocalFormatted("witchery.cooldown.remaining", Long.valueOf(remaining)));
         }
      } else {
         list.add(EnumChatFormatting.DARK_GRAY + StatCollector.translateToLocal("witchery.quicksilvermirror.notboundinfo"));
      }
   }
}
