package com.emoniph.witchery.common;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.common.ExtendedPlayer;
import com.emoniph.witchery.common.Shapeshift;
import com.emoniph.witchery.dimension.WorldProviderDreamWorld;
import com.emoniph.witchery.infusion.Infusion;
import com.emoniph.witchery.network.PacketPlayerStyle;
import com.emoniph.witchery.util.Config;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import cpw.mods.fml.relauncher.Side;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

public class ServerTickEvents {

   public static final ArrayList TASKS = new ArrayList();
   private long lastSupremeCheck = 0L;

   @SubscribeEvent
   public void onServerTick(ServerTickEvent event) {
      if(event.side == Side.SERVER && event.phase == Phase.START) {
         long now = MinecraftServer.getSystemTimeMillis();
         if(now - this.lastSupremeCheck > 10000L) {
            this.lastSupremeCheck = now;
            if(MinecraftServer.getServer() != null && MinecraftServer.getServer().worldServerForDimension(0) != null) {
               SupremeWitchData.updateSupremeStatus(MinecraftServer.getServer().worldServerForDimension(0));
            }
         }
      }

      if(event.side == Side.SERVER && event.phase == Phase.START && TASKS.size() > 0) {
         ArrayList completedTasks = new ArrayList();
         Iterator i$ = TASKS.iterator();

         ServerTickEvents.ServerTickTask task;
         while(i$.hasNext()) {
            task = (ServerTickEvents.ServerTickTask)i$.next();
            if(task.process()) {
               completedTasks.add(task);
            }
         }

         i$ = completedTasks.iterator();

         while(i$.hasNext()) {
            task = (ServerTickEvents.ServerTickTask)i$.next();
            TASKS.remove(task);
         }
      }

   }

   @SubscribeEvent
   public void onPlayerTick(PlayerTickEvent event) {
      if(event.side == Side.SERVER && !event.player.worldObj.isRemote) {
         if(event.phase == Phase.START) {
            int mantleTicks = event.player.getEntityData().getInteger("WITCMantleTicks");
            if (mantleTicks > 0) {
               event.player.getEntityData().setInteger("WITCMantleTicks", mantleTicks - 1);
            }

            int selfLeviosa = event.player.getEntityData().getInteger("WITCLeviosaSelfTicks");
            if (selfLeviosa > 0) {
               event.player.getEntityData().setInteger("WITCLeviosaSelfTicks", selfLeviosa - 1);
               event.player.fallDistance = 0.0F;
               if (event.player.isSneaking()) {
                  event.player.motionY = -0.12D;
                  if (event.player.onGround) {
                     event.player.getEntityData().setInteger("WITCLeviosaSelfTicks", 0);
                  }
               } else if (event.player.motionY < -0.05D) {
                  event.player.motionY = -0.05D;
               }
            }

            int leviosaTicks = event.player.getEntityData().getInteger("WITCLeviosaTicks");
            if (leviosaTicks > 0) {
               if (event.player.getHeldItem() == null || event.player.getHeldItem().getItem() != Witchery.Items.MYSTIC_BRANCH) {
                  event.player.getEntityData().setInteger("WITCLeviosaTicks", 0);
               } else {
                  if (leviosaTicks != Integer.MAX_VALUE) {
                     event.player.getEntityData().setInteger("WITCLeviosaTicks", leviosaTicks - 1);
                  }
                  int entityId = event.player.getEntityData().getInteger("WITCLeviosaEntity");
                  net.minecraft.entity.Entity target = event.player.worldObj.getEntityByID(entityId);
                  if (target != null && !target.isDead && target.getDistanceSqToEntity(event.player) < 1024.0D) {
                     net.minecraft.util.Vec3 look = event.player.getLookVec();
                     float dist = event.player.getEntityData().hasKey("WITCLeviosaDistance") ? event.player.getEntityData().getFloat("WITCLeviosaDistance") : 5.0f;
                     double targetX = event.player.posX + look.xCoord * dist;
                     double targetY = event.player.posY + (double)event.player.getEyeHeight() + look.yCoord * dist;
                     double targetZ = event.player.posZ + look.zCoord * dist;
                     target.motionX = (targetX - target.posX) * 0.25D;
                     target.motionY = (targetY - target.posY) * 0.25D;
                     target.motionZ = (targetZ - target.posZ) * 0.25D;
                     target.fallDistance = 0.0F;
                     if (target instanceof net.minecraft.entity.EntityLivingBase) {
                        ((net.minecraft.entity.EntityLivingBase)target).addPotionEffect(new PotionEffect(net.minecraft.potion.Potion.resistance.id, 20, 4));
                     }
                  } else {
                     event.player.getEntityData().setInteger("WITCLeviosaTicks", 0);
                  }
               }
            }

            Collection playerExt = event.player.getActivePotionEffects();
            ExtendedPlayer playerExt1 = ExtendedPlayer.get(event.player);
            if(playerExt1 != null) {
               boolean isGhost = WorldProviderDreamWorld.getPlayerIsGhost(Infusion.getNBT(event.player));
               boolean isSpirit = playerExt1.getCreatureType() == com.emoniph.witchery.util.TransformCreature.SPIRIT || 
                                  playerExt1.getCreatureType() == com.emoniph.witchery.util.TransformCreature.SUBDUED_SPIRIT || 
                                  playerExt1.isAstralProjecting() || 
                                  (isGhost && playerExt1.getSpiritLevel() >= 1);
               if (isSpirit) {
                  event.player.fallDistance = 0.0F;

                  if (event.player.isSneaking()) {
                     event.player.setInvisible(true);
                  }

                  if ((playerExt1.getSpiritLevel() >= 9 || playerExt1.isAstralProjecting()) && (event.player.isCollidedHorizontally || (event.player.isSneaking() && (Math.abs(event.player.motionX) > 0.02 || Math.abs(event.player.motionZ) > 0.02)))) {
                     double yawRad = Math.toRadians((double)event.player.rotationYaw);
                     double forwardX = -Math.sin(yawRad);
                     double forwardZ = Math.cos(yawRad);

                     for (double d = 1.2D; d <= 3.8D; d += 0.7D) {
                        double testX = event.player.posX + forwardX * d;
                        double testY = event.player.posY;
                        double testZ = event.player.posZ + forwardZ * d;
                        int bx = net.minecraft.util.MathHelper.floor_double(testX);
                        int by = net.minecraft.util.MathHelper.floor_double(testY);
                        int bz = net.minecraft.util.MathHelper.floor_double(testZ);

                        net.minecraft.block.Block b1 = event.player.worldObj.getBlock(bx, by, bz);
                        net.minecraft.block.Block b2 = event.player.worldObj.getBlock(bx, by + 1, bz);
                        if (!b1.getMaterial().isSolid() && !b2.getMaterial().isSolid()) {
                           event.player.setPositionAndUpdate(testX, testY, testZ);
                           event.player.worldObj.playSoundEffect(testX, testY, testZ, "witchery:mob.spirit.say", 0.6F, 1.3F);
                           com.emoniph.witchery.util.ParticleEffect.PORTAL.send(com.emoniph.witchery.util.SoundEffect.NONE, event.player.worldObj, testX, testY, testZ, 0.5D, 1.0D, 12);
                           break;
                        }
                     }
                  }
               }

               playerExt1.updateWorship();
               if(playerExt.size() > 0) {
                  playerExt1.cacheIncurablePotionEffect(playerExt);
               }

               playerExt1.checkSleep(true);
            }
         } else if(event.phase == Phase.END) {
            ExtendedPlayer playerExt2 = ExtendedPlayer.get(event.player);
            if(playerExt2 != null) {
               playerExt2.restoreIncurablePotionEffects();
               playerExt2.checkSleep(false);
            }
         }
      }

   }

   @SubscribeEvent
   public void onPlayerChangedDimension(PlayerChangedDimensionEvent event) {
      EntityPlayer player = event.player;
      World world = event.player.worldObj;
      Shapeshift.INSTANCE.initCurrentShift(player);
      Infusion.syncPlayer(world, player);
      ExtendedPlayer.get(player).scheduleSync();
      Witchery.packetPipeline.sendToDimension(new PacketPlayerStyle(player), world.provider.dimensionId);
      if(player.dimension != Config.instance().dimensionDreamID && WorldProviderDreamWorld.getPlayerIsSpiritWalking(player) && !WorldProviderDreamWorld.getPlayerIsGhost(player)) {
         WorldProviderDreamWorld.setPlayerMustAwaken(player, true);
      } else if(player.dimension == Config.instance().dimensionDreamID && !WorldProviderDreamWorld.getPlayerIsSpiritWalking(player)) {
         WorldProviderDreamWorld.changeDimension(player, 0);
         WorldProviderDreamWorld.findTopAndSetPosition(player.worldObj, player);
      }

   }

   @SubscribeEvent
   public void onPlayerRespawn(PlayerRespawnEvent event) {
      if(!event.player.worldObj.isRemote) {
         NBTTagCompound nbtPlayer = Infusion.getNBT(event.player);
         if(nbtPlayer.hasKey("WITCPoSpawn")) {
            NBTTagList player = nbtPlayer.getTagList("WITCPoSpawn", 10);
            if(player.tagCount() > 0) {
               for(int world = 0; world < player.tagCount(); ++world) {
                  PotionEffect restoredEffect = PotionEffect.readCustomPotionEffectFromNBT(player.getCompoundTagAt(world));
                  if(!event.player.isPotionActive(restoredEffect.getPotionID())) {
                     event.player.addPotionEffect(restoredEffect);
                  }
               }
            }

            nbtPlayer.removeTag("WITCPoSpawn");
         }

         EntityPlayer var6 = event.player;
         World var7 = event.player.worldObj;
         Shapeshift.INSTANCE.initCurrentShift(var6);
         Infusion.syncPlayer(var7, var6);
         ExtendedPlayer.get(var6).scheduleSync();
         Witchery.packetPipeline.sendToDimension(new PacketPlayerStyle(var6), var7.provider.dimensionId);
      }

   }

   @SubscribeEvent
   public void onPlayerLoggedIn(PlayerLoggedInEvent event) {
      if(!event.player.worldObj.isRemote) {
         EntityPlayer player = event.player;
         if(player != null && player.worldObj != null && !player.worldObj.isRemote) {
            long nextUpdate = MinecraftServer.getSystemTimeMillis() + 1500L;
            ExtendedPlayer.get(player).scheduleSync();
            Iterator nbtPlayer = player.worldObj.playerEntities.iterator();

            while(nbtPlayer.hasNext()) {
               Object obj = nbtPlayer.next();
               EntityPlayer otherPlayer = (EntityPlayer)obj;
               NBTTagCompound nbtOtherPlayer = Infusion.getNBT(otherPlayer);
               if(otherPlayer != player) {
                  nbtOtherPlayer.setLong("WITCResyncLook", nextUpdate);
               }
            }

            NBTTagCompound nbtPlayer1 = Infusion.getNBT(player);
            Witchery.packetPipeline.sendToDimension(new PacketPlayerStyle(player), player.worldObj.provider.dimensionId);
            if(player.dimension != Config.instance().dimensionDreamID && WorldProviderDreamWorld.getPlayerIsSpiritWalking(player) && !WorldProviderDreamWorld.getPlayerIsGhost(player)) {
               WorldProviderDreamWorld.setPlayerMustAwaken(player, true);
            }

            SupremeWitchData.updateSupremeStatus(player.worldObj);
            SupremeWitchData supremeData = SupremeWitchData.get(player.worldObj);
            if(supremeData != null) {
               if(supremeData.isSupreme(player)) {
                  player.addChatMessage(new net.minecraft.util.ChatComponentText(net.minecraft.util.EnumChatFormatting.DARK_PURPLE + "" + net.minecraft.util.EnumChatFormatting.BOLD + "[Bruja Suprema] " + net.minecraft.util.EnumChatFormatting.LIGHT_PURPLE + "Bienvenida de vuelta, Bruja Suprema " + net.minecraft.util.EnumChatFormatting.GOLD + player.getCommandSenderName() + net.minecraft.util.EnumChatFormatting.LIGHT_PURPLE + "."));
               }
               if(player instanceof net.minecraft.entity.player.EntityPlayerMP) {
                  supremeData.syncTo((net.minecraft.entity.player.EntityPlayerMP)player);
               }
            }
         }
      }

   }


   public abstract static class ServerTickTask {

      protected final World world;


      public ServerTickTask(World world) {
         this.world = world;
      }

      public abstract boolean process();
   }
}
