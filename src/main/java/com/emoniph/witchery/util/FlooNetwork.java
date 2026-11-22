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

   public String getFormattedDestinations() {
      if (this.fireplaces.isEmpty()) {
         return "";
      }
      StringBuilder sb = new StringBuilder();
      for (FireplaceLocation loc : this.fireplaces.values()) {
         if (sb.length() > 0) {
            sb.append(", ");
         }
         sb.append(loc.name);
      }
      return sb.toString();
   }

   public static boolean isFireplaceWall(World world, int x, int y, int z) {
      if (world == null) {
         return false;
      }
      net.minecraft.block.Block b = world.getBlock(x, y, z);
      if (b == null || b == net.minecraft.init.Blocks.air) {
         return false;
      }
      if (b.getMaterial().getCanBurn()) {
         return false;
      }
      return b.isOpaqueCube() || b.renderAsNormalBlock() 
            || b instanceof net.minecraft.block.BlockStairs 
            || b instanceof net.minecraft.block.BlockSlab 
            || b instanceof net.minecraft.block.BlockWall
            || b instanceof net.minecraft.block.BlockPane 
            || b == net.minecraft.init.Blocks.iron_bars 
            || b == net.minecraft.init.Blocks.hopper
            || World.doesBlockHaveSolidTopSurface(world, x, y, z);
   }

   public static boolean isValidFireplace(World world, int x, int y, int z) {
      if (world == null) {
         return false;
      }

      // 1. Base must be solid non-combustible block
      net.minecraft.block.Block floor = world.getBlock(x, y - 1, z);
      if (floor == null || floor == net.minecraft.init.Blocks.air || floor.getMaterial().getCanBurn()) {
         return false;
      }

      // 2. Wall enclosure: at least 2 or 3 sides around fire (y) must be solid non-combustible walls
      int solidSidesAtY = 0;
      if (isFireplaceWall(world, x + 1, y, z)) solidSidesAtY++;
      if (isFireplaceWall(world, x - 1, y, z)) solidSidesAtY++;
      if (isFireplaceWall(world, x, y, z + 1)) solidSidesAtY++;
      if (isFireplaceWall(world, x, y, z - 1)) solidSidesAtY++;

      if (solidSidesAtY < 2) {
         return false;
      }

      // Also check y+1 (head height of the fireplace opening)
      int solidSidesAtY1 = 0;
      if (isFireplaceWall(world, x + 1, y + 1, z)) solidSidesAtY1++;
      if (isFireplaceWall(world, x - 1, y + 1, z)) solidSidesAtY1++;
      if (isFireplaceWall(world, x, y + 1, z + 1)) solidSidesAtY1++;
      if (isFireplaceWall(world, x, y + 1, z - 1)) solidSidesAtY1++;

      if (solidSidesAtY + solidSidesAtY1 < 4) {
         return false;
      }

      // 3. Overhead mantle / ceiling / chimney flue
      // A fireplace cannot be under open sky without overhead mantle or chimney flue
      boolean hasOverhead = isFireplaceWall(world, x, y + 2, z) || 
                            isFireplaceWall(world, x, y + 3, z) ||
                            !world.canBlockSeeTheSky(x, y + 1, z);

      if (!hasOverhead) {
         int chimneyShaftWalls = 0;
         if (isFireplaceWall(world, x + 1, y + 2, z)) chimneyShaftWalls++;
         if (isFireplaceWall(world, x - 1, y + 2, z)) chimneyShaftWalls++;
         if (isFireplaceWall(world, x, y + 2, z + 1)) chimneyShaftWalls++;
         if (isFireplaceWall(world, x, y + 2, z - 1)) chimneyShaftWalls++;
         if (chimneyShaftWalls < 2) {
            return false;
         }
      }

      return true;
   }

   public static String extractSignText(TileEntitySign sign) {
      if (sign == null || sign.signText == null) {
         return null;
      }
      StringBuilder sb = new StringBuilder();
      for (int line = 0; line < 4; ++line) {
         if (sign.signText[line] != null && !sign.signText[line].trim().isEmpty()) {
            if (sb.length() > 0) {
               sb.append(" ");
            }
            sb.append(sign.signText[line].trim());
         }
      }
      return sb.length() > 0 ? sb.toString().trim() : null;
   }

   public static Coord findNearbySignCoord(World world, int x, int y, int z, String expectedName) {
      Coord fallback = null;
      double bestFallbackDist = Double.MAX_VALUE;
      double bestMatchDist = Double.MAX_VALUE;
      Coord matched = null;

      for (int dx = -2; dx <= 2; ++dx) {
         for (int dy = -1; dy <= 3; ++dy) {
            for (int dz = -2; dz <= 2; ++dz) {
               TileEntity te = world.getTileEntity(x + dx, y + dy, z + dz);
               if (te instanceof TileEntitySign) {
                  String text = extractSignText((TileEntitySign) te);
                  if (text != null && !text.isEmpty()) {
                     double dist = (double)(dx * dx + dy * dy + dz * dz);
                     if (expectedName != null && text.equalsIgnoreCase(expectedName.trim())) {
                        if (dist < bestMatchDist) {
                           bestMatchDist = dist;
                           matched = new Coord(x + dx, y + dy, z + dz);
                        }
                     }
                     if (dist < bestFallbackDist) {
                        bestFallbackDist = dist;
                        fallback = new Coord(x + dx, y + dy, z + dz);
                     }
                  }
               }
            }
         }
      }
      return matched != null ? matched : fallback;
   }

   public static String findNearbySignName(World world, int x, int y, int z) {
      Coord signCoord = findNearbySignCoord(world, x, y, z, null);
      if (signCoord != null) {
         TileEntity te = world.getTileEntity(signCoord.x, signCoord.y, signCoord.z);
         if (te instanceof TileEntitySign) {
            return extractSignText((TileEntitySign) te);
         }
      }
      return null;
   }

   public static boolean isPassableForPlayer(World world, int x, int y, int z) {
      if (world == null || y < 1 || y >= 255) {
         return false;
      }
      net.minecraft.block.Block b = world.getBlock(x, y, z);
      if (b == null || b == net.minecraft.init.Blocks.air || b == Witchery.Blocks.FLOO_FIRE || b == net.minecraft.init.Blocks.fire || b instanceof net.minecraft.block.BlockSign) {
         return true;
      }
      if (b.getMaterial().isLiquid()) {
         return false;
      }
      return b.getCollisionBoundingBoxFromPool(world, x, y, z) == null;
   }

   public static boolean hasSolidFloor(World world, int x, int y, int z) {
      if (world == null || y <= 1) {
         return false;
      }
      net.minecraft.block.Block floor = world.getBlock(x, y - 1, z);
      if (floor == null || floor == net.minecraft.init.Blocks.air || floor == Witchery.Blocks.FLOO_FIRE || floor == net.minecraft.init.Blocks.fire || floor.getMaterial().isLiquid()) {
         return false;
      }
      return floor.getCollisionBoundingBoxFromPool(world, x, y - 1, z) != null || World.doesBlockHaveSolidTopSurface(world, x, y - 1, z);
   }

   public static boolean isSafeStandColumn(World world, int x, int y, int z) {
      return hasSolidFloor(world, x, y, z) && isPassableForPlayer(world, x, y, z) && isPassableForPlayer(world, x, y + 1, z);
   }

   public static double[] findSafeFireplaceArrival(World world, FireplaceLocation target, float currentYaw) {
      int fx = target.posX;
      int fy = target.posY;
      int fz = target.posZ;
      float yaw = currentYaw;

      if (world == null) {
         return new double[]{(double)fx + 0.5D, (double)fy, (double)fz + 0.5D, (double)yaw};
      }

      Coord signCoord = findNearbySignCoord(world, fx, fy, fz, target.name);
      if (signCoord != null) {
         net.minecraft.block.Block signBlock = world.getBlock(signCoord.x, signCoord.y, signCoord.z);
         int signMeta = world.getBlockMetadata(signCoord.x, signCoord.y, signCoord.z);
         int facingDX = 0;
         int facingDZ = 0;

         if (signBlock == net.minecraft.init.Blocks.wall_sign) {
            if (signMeta == 2) { facingDZ = -1; yaw = 180.0F; }
            else if (signMeta == 3) { facingDZ = 1; yaw = 0.0F; }
            else if (signMeta == 4) { facingDX = -1; yaw = 90.0F; }
            else if (signMeta == 5) { facingDX = 1; yaw = 270.0F; }
         } else if (signBlock == net.minecraft.init.Blocks.standing_sign) {
            yaw = (float)(signMeta * 360) / 16.0F;
         }

         // 1. Directly below the sign (at fire floor level or 1-2 blocks under the sign)
         int[] candidateYs = new int[]{fy, signCoord.y - 1, signCoord.y - 2, fy - 1, fy + 1};
         for (int i = 0; i < candidateYs.length; ++i) {
            int cy = candidateYs[i];
            if (cy <= signCoord.y && isSafeStandColumn(world, signCoord.x, cy, signCoord.z)) {
               return new double[]{(double)signCoord.x + 0.5D, (double)cy, (double)signCoord.z + 0.5D, (double)yaw};
            }
         }

         // 2. If the sign is mounted on a solid column, check 1 block in front of the wall sign
         if (facingDX != 0 || facingDZ != 0) {
            int frontX = signCoord.x + facingDX;
            int frontZ = signCoord.z + facingDZ;
            for (int i = 0; i < candidateYs.length; ++i) {
               int cy = candidateYs[i];
               if (isSafeStandColumn(world, frontX, cy, frontZ)) {
                  return new double[]{(double)frontX + 0.5D, (double)cy, (double)frontZ + 0.5D, (double)yaw};
               }
            }
         }
      }

      // 3. Check the hearth itself if it has 2 blocks of headroom (e.g., 2-block tall fireplace opening)
      if (isSafeStandColumn(world, fx, fy, fz)) {
         // Orient player toward the open front of the fireplace
         int[][] dirs = new int[][]{{0, 1, 0}, {0, -1, 180}, {1, 0, 270}, {-1, 0, 90}};
         for (int i = 0; i < dirs.length; ++i) {
            if (!isFireplaceWall(world, fx + dirs[i][0], fy, fz + dirs[i][1])) {
               yaw = (float)dirs[i][2];
               break;
            }
         }
         return new double[]{(double)fx + 0.5D, (double)fy, (double)fz + 0.5D, (double)yaw};
      }

      // 4. Check the 4 cardinal blocks right in front of the fireplace opening (for 1-block tall hearths)
      int[][] dirs = new int[][]{{0, 1, 0}, {0, -1, 180}, {1, 0, 270}, {-1, 0, 90}};
      for (int i = 0; i < dirs.length; ++i) {
         int nx = fx + dirs[i][0];
         int nz = fz + dirs[i][1];
         if (isSafeStandColumn(world, nx, fy, nz)) {
            return new double[]{(double)nx + 0.5D, (double)fy, (double)nz + 0.5D, (double)dirs[i][2]};
         }
      }
      for (int i = 0; i < dirs.length; ++i) {
         int nx = fx + dirs[i][0];
         int nz = fz + dirs[i][1];
         if (isSafeStandColumn(world, nx, fy - 1, nz)) {
            return new double[]{(double)nx + 0.5D, (double)(fy - 1), (double)nz + 0.5D, (double)dirs[i][2]};
         }
      }

      return null;
   }

   public static Coord getEntityFlooFireCoord(EntityLivingBase entity) {
      if (entity == null || entity.worldObj == null) {
         return null;
      }
      World world = entity.worldObj;
      net.minecraft.util.AxisAlignedBB bb = entity.boundingBox.expand(0.35D, 0.35D, 0.35D);
      int minX = MathHelper.floor_double(bb.minX);
      int maxX = MathHelper.floor_double(bb.maxX);
      int minY = MathHelper.floor_double(bb.minY);
      int maxY = MathHelper.floor_double(bb.maxY);
      int minZ = MathHelper.floor_double(bb.minZ);
      int maxZ = MathHelper.floor_double(bb.maxZ);

      for (int x = minX; x <= maxX; ++x) {
         for (int y = minY; y <= maxY; ++y) {
            for (int z = minZ; z <= maxZ; ++z) {
               if (world.getBlock(x, y, z) == Witchery.Blocks.FLOO_FIRE) {
                  return new Coord(x, y, z);
               }
            }
         }
      }
      return null;
   }

   public static boolean isEntityInFlooFire(EntityLivingBase entity) {
      return getEntityFlooFireCoord(entity) != null;
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

         WorldServer destWorld = MinecraftServer.getServer() != null ? MinecraftServer.getServer().worldServerForDimension(target.dimension) : null;
         World checkWorld = destWorld != null ? destWorld : world;
         double[] arrival = findSafeFireplaceArrival(checkWorld, target, player.rotationYaw);
         if (arrival != null) {
            player.rotationYaw = (float)arrival[3];
            ItemGeneral.teleportToLocation(world, arrival[0], arrival[1], arrival[2], target.dimension, player, true);
         } else {
            ItemGeneral.teleportToLocationSafely(world, (double)target.posX + 0.5D, (double)target.posY, (double)target.posZ + 0.5D, target.dimension, player, true);
         }

         if (destWorld != null) {
            double px = arrival != null ? arrival[0] : (double)target.posX + 0.5D;
            double py = arrival != null ? arrival[1] + 1.0D : (double)target.posY + 1.0D;
            double pz = arrival != null ? arrival[2] : (double)target.posZ + 0.5D;
            ParticleEffect.PORTAL.send(SoundEffect.MOB_ENDERMEN_PORTAL, destWorld, px, py, pz, 1.0D, 2.0D, 32);
         }
         ChatUtil.sendTranslated(EnumChatFormatting.GREEN, player, "witchery.floo.travelsuccess", new Object[]{target.name});
         return true;
      } else {
         ParticleEffect.SMOKE.send(SoundEffect.RANDOM_FIZZ, world, (double)fireCoord.x + 0.5D, (double)fireCoord.y + 0.5D, (double)fireCoord.z + 0.5D, 0.5D, 1.0D, 16);
         ChatUtil.sendTranslated(EnumChatFormatting.RED, player, "witchery.floo.notfound", new Object[]{destinationName});
         String available = network.getFormattedDestinations();
         if (available != null && !available.isEmpty()) {
            ChatUtil.sendTranslated(EnumChatFormatting.YELLOW, player, "witchery.floo.available", new Object[]{available});
         }
         return false;
      }
   }
}

