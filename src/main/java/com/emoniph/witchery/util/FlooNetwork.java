package com.emoniph.witchery.util;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.item.ItemGeneral;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.WorldServer;

public class FlooNetwork extends WorldSavedData {

   public static final String DATA_KEY = "witchery_floo_network";

   public static class FireplaceLocation {
      public final String name;
      public final int dimension;
      public final int posX;
      public final int posY;
      public final int posZ;

      public FireplaceLocation(String name, int dimension, int posX, int posY, int posZ) {
         this.name = name;
         this.dimension = dimension;
         this.posX = posX;
         this.posY = posY;
         this.posZ = posZ;
      }
   }

   private final Map<String, FireplaceLocation> fireplaces = new HashMap<String, FireplaceLocation>();

   public FlooNetwork(String key) {
      super(key);
   }

   public FlooNetwork() {
      super(DATA_KEY);
   }

   public static FlooNetwork get(World world) {
      World overworld = (world != null && world.provider.dimensionId == 0) ? world :
         (MinecraftServer.getServer() != null ? MinecraftServer.getServer().worldServerForDimension(0) : world);
      if (overworld == null || overworld.mapStorage == null) {
         return null;
      }
      FlooNetwork data = (FlooNetwork) overworld.mapStorage.loadData(FlooNetwork.class, DATA_KEY);
      if (data == null) {
         data = new FlooNetwork();
         overworld.mapStorage.setData(DATA_KEY, data);
      }
      return data;
   }

   public void addFireplace(String name, int dimension, int x, int y, int z) {
      if (name == null || name.trim().isEmpty()) {
         return;
      }
      this.fireplaces.put(name.trim().toLowerCase(), new FireplaceLocation(name.trim(), dimension, x, y, z));
      this.markDirty();
   }

   public FireplaceLocation findFireplace(String name) {
      if (name == null) {
         return null;
      }
      return this.fireplaces.get(name.trim().toLowerCase());
   }

   @Override
   public void readFromNBT(NBTTagCompound nbt) {
      this.fireplaces.clear();
      NBTTagList list = nbt.getTagList("Fireplaces", 10);
      for (int i = 0; i < list.tagCount(); ++i) {
         NBTTagCompound tag = list.getCompoundTagAt(i);
         String name = tag.getString("Name");
         int dim = tag.getInteger("Dim");
         int x = tag.getInteger("X");
         int y = tag.getInteger("Y");
         int z = tag.getInteger("Z");
         this.fireplaces.put(name.toLowerCase(), new FireplaceLocation(name, dim, x, y, z));
      }
   }

   @Override
   public void writeToNBT(NBTTagCompound nbt) {
      NBTTagList list = new NBTTagList();
      for (FireplaceLocation loc : this.fireplaces.values()) {
         NBTTagCompound tag = new NBTTagCompound();
         tag.setString("Name", loc.name);
         tag.setInteger("Dim", loc.dimension);
         tag.setInteger("X", loc.posX);
         tag.setInteger("Y", loc.posY);
         tag.setInteger("Z", loc.posZ);
         list.appendTag(tag);
      }
      nbt.setTag("Fireplaces", list);
   }

   public static String findNearbySignName(World world, int x, int y, int z) {
      for (int dx = -2; dx <= 2; ++dx) {
         for (int dy = -1; dy <= 3; ++dy) {
            for (int dz = -2; dz <= 2; ++dz) {
               TileEntity te = world.getTileEntity(x + dx, y + dy, z + dz);
               if (te instanceof TileEntitySign) {
                  TileEntitySign sign = (TileEntitySign) te;
                  StringBuilder sb = new StringBuilder();
                  for (int line = 0; line < 4; ++line) {
                     if (sign.signText[line] != null && !sign.signText[line].trim().isEmpty()) {
                        if (sb.length() > 0) {
                           sb.append(" ");
                        }
                        sb.append(sign.signText[line].trim());
                     }
                  }
                  if (sb.length() > 0) {
                     return sb.toString().trim();
                  }
               }
            }
         }
      }
      return null;
   }

   public static boolean isEntityInFlooFire(EntityLivingBase entity) {
      if (entity == null || entity.worldObj == null) {
         return false;
      }
      World world = entity.worldObj;
      int x = MathHelper.floor_double(entity.posX);
      int y = MathHelper.floor_double(entity.posY);
      int z = MathHelper.floor_double(entity.posZ);
      if (world.getBlock(x, y, z) == Witchery.Blocks.FLOO_FIRE) {
         return true;
      }
      if (world.getBlock(x, y + 1, z) == Witchery.Blocks.FLOO_FIRE) {
         return true;
      }
      int feetY = MathHelper.floor_double(entity.boundingBox.minY);
      if (world.getBlock(x, feetY, z) == Witchery.Blocks.FLOO_FIRE) {
         return true;
      }
      return false;
   }

   public static Coord getEntityFlooFireCoord(EntityLivingBase entity) {
      if (entity == null || entity.worldObj == null) {
         return null;
      }
      World world = entity.worldObj;
      int x = MathHelper.floor_double(entity.posX);
      int y = MathHelper.floor_double(entity.posY);
      int z = MathHelper.floor_double(entity.posZ);
      if (world.getBlock(x, y, z) == Witchery.Blocks.FLOO_FIRE) {
         return new Coord(x, y, z);
      }
      if (world.getBlock(x, y + 1, z) == Witchery.Blocks.FLOO_FIRE) {
         return new Coord(x, y + 1, z);
      }
      int feetY = MathHelper.floor_double(entity.boundingBox.minY);
      if (world.getBlock(x, feetY, z) == Witchery.Blocks.FLOO_FIRE) {
         return new Coord(x, feetY, z);
      }
      return null;
   }

   public static boolean travelFlooNetwork(EntityPlayer player, String destinationName) {
      if (player == null || player.worldObj.isRemote) {
         return false;
      }
      World world = player.worldObj;
      Coord fireCoord = getEntityFlooFireCoord(player);
      if (fireCoord == null) {
         return false;
      }

      FlooNetwork network = FlooNetwork.get(world);
      if (network == null) {
         return false;
      }

      // Automatically register the current fireplace if nearby sign exists
      String localSignName = findNearbySignName(world, fireCoord.x, fireCoord.y, fireCoord.z);
      if (localSignName != null && !localSignName.isEmpty()) {
         network.addFireplace(localSignName, world.provider.dimensionId, fireCoord.x, fireCoord.y, fireCoord.z);
      }

      FlooNetwork.FireplaceLocation target = network.findFireplace(destinationName);
      if (target != null) {
         world.setBlockToAir(fireCoord.x, fireCoord.y, fireCoord.z);
         ParticleEffect.FLAME.send(SoundEffect.FIRE_IGNITE, world, (double)fireCoord.x + 0.5D, (double)fireCoord.y + 0.5D, (double)fireCoord.z + 0.5D, 1.0D, 1.0D, 24);

         ItemGeneral.teleportToLocationSafely(world, (double)target.posX + 0.5D, (double)target.posY, (double)target.posZ + 0.5D, target.dimension, player, true);

         WorldServer destWorld = MinecraftServer.getServer() != null ? MinecraftServer.getServer().worldServerForDimension(target.dimension) : null;
         if (destWorld != null) {
            ParticleEffect.PORTAL.send(SoundEffect.MOB_ENDERMEN_PORTAL, destWorld, (double)target.posX + 0.5D, (double)target.posY + 1.0D, (double)target.posZ + 0.5D, 1.0D, 2.0D, 32);
         }
         ChatUtil.sendTranslated(EnumChatFormatting.GREEN, player, "witchery.floo.travelsuccess", new Object[]{target.name});
         return true;
      } else {
         ParticleEffect.SMOKE.send(SoundEffect.RANDOM_FIZZ, world, (double)fireCoord.x + 0.5D, (double)fireCoord.y + 0.5D, (double)fireCoord.z + 0.5D, 0.5D, 1.0D, 16);
         ChatUtil.sendTranslated(EnumChatFormatting.RED, player, "witchery.floo.notfound", new Object[]{destinationName});
         return false;
      }
   }
}

