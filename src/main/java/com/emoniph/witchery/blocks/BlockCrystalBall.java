package com.emoniph.witchery.blocks;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.blocks.BlockBaseContainer;
import com.emoniph.witchery.common.IPowerSource;
import com.emoniph.witchery.common.PowerSources;
import com.emoniph.witchery.predictions.PredictionManager;
import com.emoniph.witchery.util.ChatUtil;
import com.emoniph.witchery.util.Coord;
import com.emoniph.witchery.util.ParticleEffect;
import com.emoniph.witchery.util.SoundEffect;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockCrystalBall extends BlockBaseContainer {

   private static final float ALTAR_POWER_PER_READING = 500.0F;
   private static final int POWER_SOURCE_RADIUS = 16;


   public BlockCrystalBall() {
      super(Material.anvil, BlockCrystalBall.TileEntityCrystalBall.class);
      this.setHardness(2.0F);
      this.setStepSound(Block.soundTypeMetal);
      this.setBlockBounds(0.3F, 0.0F, 0.3F, 0.7F, 0.6F, 0.7F);
   }

   public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
      if(world.isRemote) {
         return true;
      } else {
         TileEntity tile = world.getTileEntity(x, y, z);
         if(tile != null && tile instanceof BlockCrystalBall.TileEntityCrystalBall) {
            BlockCrystalBall.TileEntityCrystalBall ball = (BlockCrystalBall.TileEntityCrystalBall)tile;

            if (player.isSneaking()) {
               ball.updateAltarLink();
               ball.scanNatureSiphon();
               BlockAltar.TileEntityAltar altar = ball.getLinkedAltar();
               player.addChatMessage(new ChatComponentText("\u00a75\u00a7l[Nexo de Cristal de Altar]\u00a7r"));
               if (altar != null && ball.linkedAltarCoord != null) {
                  double dist = Math.sqrt(player.getDistanceSq(ball.linkedAltarCoord.x, ball.linkedAltarCoord.y, ball.linkedAltarCoord.z));
                  player.addChatMessage(new ChatComponentText("\u00a7a\u2714 Altar vinculado en: \u00a7f" + ball.linkedAltarCoord.x + ", " + ball.linkedAltarCoord.y + ", " + ball.linkedAltarCoord.z + " \u00a77(" + (int)dist + "m de distancia)"));
                  player.addChatMessage(new ChatComponentText("\u00a7b\u26a1 Energ\u00eda retransmitida: \u00a7f" + (int)altar.getCurrentPower() + " \u00a77/ \u00a7f" + (int)(altar.getMaxPower() * altar.getPowerScale())));
                  player.addChatMessage(new ChatComponentText("\u00a72\u273f Sif\u00f3n de Naturaleza: \u00a7f+" + (int)ball.gatheredNaturePower + " \u00a77poder aportado al Altar desde este jard\u00edn."));
                  ParticleEffect.INSTANT_SPELL.send(SoundEffect.RANDOM_ORB, world, (double)x + 0.5D, (double)y + 0.5D, (double)z + 0.5D, 0.5D, 0.5D, 8);
               } else {
                  player.addChatMessage(new ChatComponentText("\u00a7c\u2716 No se detect\u00f3 ning\u00fan Altar en un radio de 36 bloques. Coloca un Altar m\u00e1s cerca para vincular este cristal."));
                  SoundEffect.NOTE_SNARE.playAtPlayer(world, player);
               }
               return true;
            }

            if(ball.canBeUsed()) {
               if(tryConsumePower(world, player, x, y, z)) {
                  AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(0.5D + (double)x - 5.0D, 0.5D + (double)y - 2.0D, 0.5D + (double)z - 5.0D, 0.5D + (double)x + 5.0D, 0.5D + (double)y + 2.0D, 0.5D + (double)z + 5.0D);
                  List players = world.getEntitiesWithinAABB(EntityPlayer.class, bounds);
                  EntityPlayer victim = player;
                  double closest = 10000.0D;

                  for(int i = 0; i < players.size(); ++i) {
                     EntityPlayer nearbyPlayer = players.get(i) != null?(EntityPlayer)players.get(i):null;
                     if(nearbyPlayer != null && nearbyPlayer != player) {
                        double distSq = player.getDistanceSq(0.5D + (double)x, 0.5D + (double)y, 0.5D + (double)z);
                        if(distSq < closest) {
                           victim = nearbyPlayer;
                           closest = distSq;
                        }
                     }
                  }

                  PredictionManager.instance().makePrediction(victim, player, true);
                  ball.onUsed();
                  com.emoniph.witchery.common.WitchAdvisor.whisperProgression(player, "El Oráculo de Cristal");
                  ParticleEffect.EXPLODE.send(SoundEffect.RANDOM_LEVELUP, world, 0.5D + (double)x, 0.2D + (double)y, 0.5D + (double)z, 0.2D, 0.2D, 16);
               }
            } else {
               ChatUtil.sendTranslated(EnumChatFormatting.GRAY, player, "witchery.prediction.recharging", new Object[0]);
               SoundEffect.NOTE_SNARE.playAtPlayer(world, player);
            }
         }

         return true;
      }
   }

   public static boolean tryConsumePower(World world, EntityPlayer player, int x, int y, int z) {
      IPowerSource powerSource = findNewPowerSource(world, x, y, z);
      if(powerSource != null && powerSource.consumePower(500.0F)) {
         return true;
      } else {
         if(!world.isRemote) {
            ChatUtil.sendTranslated(EnumChatFormatting.GRAY, player, "witchery.prediction.nopower", new Object[0]);
            SoundEffect.NOTE_SNARE.playAtPlayer(world, player);
         }

         return false;
      }
   }

   private static IPowerSource findNewPowerSource(World world, int posX, int posY, int posZ) {
      ArrayList sources = PowerSources.instance() != null?PowerSources.instance().get(world, new Coord(posX, posY, posZ), 16):null;
      return sources != null && sources.size() > 0?((PowerSources.RelativePowerSource)sources.get(0)).source():null;
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

   @SideOnly(Side.CLIENT)
   public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
      return false;
   }

   @SideOnly(Side.CLIENT)
   public void randomDisplayTick(World world, int x, int y, int z, Random rand) {}

   public static class TileEntityCrystalBall extends TileEntityBase implements IPowerSource {

      private long lastUsedTime = 0L;
      public Coord linkedAltarCoord = null;
      public float gatheredNaturePower = 0.0F;
      private boolean registered = false;

      @Override
      public boolean canUpdate() {
         return true;
      }

      @Override
      public void updateEntity() {
         super.updateEntity();
         if (super.worldObj != null && !super.worldObj.isRemote) {
            if (super.ticks % 100L == 0L || this.linkedAltarCoord == null) {
               this.updateAltarLink();
               this.scanNatureSiphon();
            }

            if (!this.registered && this.linkedAltarCoord != null) {
               PowerSources.instance().registerPowerSource(this);
               this.registered = true;
            }
         }
      }

      public void updateAltarLink() {
         ArrayList sources = PowerSources.instance() != null ? PowerSources.instance().get(super.worldObj, new Coord(this), 36) : null;
         if (sources != null) {
            for (Object obj : sources) {
               PowerSources.RelativePowerSource rps = (PowerSources.RelativePowerSource)obj;
               if (rps.source() instanceof BlockAltar.TileEntityAltar) {
                  this.linkedAltarCoord = rps.source().getLocation();
                  break;
               }
            }
         }
      }

      public BlockAltar.TileEntityAltar getLinkedAltar() {
         if (this.linkedAltarCoord != null && super.worldObj != null) {
            TileEntity te = this.linkedAltarCoord.getBlockTileEntity(super.worldObj);
            if (te instanceof BlockAltar.TileEntityAltar) {
               return (BlockAltar.TileEntityAltar)te;
            }
         }
         return null;
      }

      public void scanNatureSiphon() {
         BlockAltar.TileEntityAltar altar = this.getLinkedAltar();
         if (altar == null) {
            this.gatheredNaturePower = 0.0F;
            return;
         }

         float nature = 0.0F;
         int r = 12;
         for (int dy = -6; dy <= 6; ++dy) {
            for (int dz = -r; dz <= r; dz += 2) {
               for (int dx = -r; dx <= r; dx += 2) {
                  Block b = super.worldObj.getBlock(super.xCoord + dx, super.yCoord + dy, super.zCoord + dz);
                  if (b instanceof net.minecraft.block.BlockLeaves || b instanceof net.minecraft.block.BlockOldLeaf || b instanceof net.minecraft.block.BlockNewLeaf) {
                     nature += 1.5F;
                  } else if (b instanceof net.minecraft.block.BlockFlower || b instanceof net.minecraft.block.BlockCrops) {
                     nature += 2.0F;
                  } else if (b instanceof net.minecraft.block.BlockLog || b instanceof net.minecraft.block.BlockOldLog || b instanceof net.minecraft.block.BlockNewLog) {
                     nature += 1.0F;
                  } else if (b == Witchery.Blocks.CROP_BELLADONNA || b == Witchery.Blocks.CROP_MANDRAKE || b == Witchery.Blocks.CROP_ARTICHOKE || b == Witchery.Blocks.CROP_SNOWBELL || b == Witchery.Blocks.EMBER_MOSS) {
                     nature += 3.0F;
                  } else if (b == Blocks.tallgrass || b == Blocks.reeds || b == Blocks.cactus) {
                     nature += 0.5F;
                  }
               }
            }
         }
         this.gatheredNaturePower = Math.min(nature, 2500.0F);
         altar.externalBonusPower = this.gatheredNaturePower;
      }

      @Override
      public void invalidate() {
         super.invalidate();
         if (super.worldObj != null && !super.worldObj.isRemote) {
            PowerSources.instance().removePowerSource(this);
            BlockAltar.TileEntityAltar altar = this.getLinkedAltar();
            if (altar != null) {
               altar.externalBonusPower = 0.0F;
            }
         }
      }

      @Override
      public World getWorld() {
         return super.worldObj;
      }

      @Override
      public Coord getLocation() {
         return new Coord(this);
      }

      @Override
      public boolean isLocationEqual(Coord c) {
         return c != null && c.equals(new Coord(this));
      }

      @Override
      public boolean consumePower(float amount) {
         BlockAltar.TileEntityAltar altar = this.getLinkedAltar();
         return altar != null && altar.consumePower(amount);
      }

      @Override
      public float getCurrentPower() {
         BlockAltar.TileEntityAltar altar = this.getLinkedAltar();
         return altar != null ? altar.getCurrentPower() : 0.0F;
      }

      @Override
      public float getRange() {
         return 28.0F;
      }

      @Override
      public int getEnhancementLevel() {
         BlockAltar.TileEntityAltar altar = this.getLinkedAltar();
         return altar != null ? altar.getEnhancementLevel() : 0;
      }

      @Override
      public boolean isPowerInvalid() {
         return this.isInvalid();
      }

      public void onUsed() {
         this.lastUsedTime = super.worldObj.getTotalWorldTime();
      }

      public boolean canBeUsed() {
         return super.worldObj.getTotalWorldTime() - this.lastUsedTime > 100L;
      }

      public void writeToNBT(NBTTagCompound nbtTag) {
         super.writeToNBT(nbtTag);
         nbtTag.setLong("LastUsedTime", this.lastUsedTime);
         if (this.linkedAltarCoord != null) {
            this.linkedAltarCoord.setNBT(nbtTag, "AltarCoord");
         }
         nbtTag.setFloat("GatheredNature", this.gatheredNaturePower);
      }

      public void readFromNBT(NBTTagCompound nbtTag) {
         super.readFromNBT(nbtTag);
         this.lastUsedTime = nbtTag.getLong("LastUsedTime");
         if (nbtTag.hasKey("AltarCoordX")) {
            this.linkedAltarCoord = Coord.createFrom(nbtTag, "AltarCoord");
         }
         if (nbtTag.hasKey("GatheredNature")) {
            this.gatheredNaturePower = nbtTag.getFloat("GatheredNature");
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
   }
}
