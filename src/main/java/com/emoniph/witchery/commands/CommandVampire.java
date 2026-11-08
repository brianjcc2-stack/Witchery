package com.emoniph.witchery.commands;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.common.ExtendedPlayer;
import com.emoniph.witchery.common.Shapeshift;
import com.emoniph.witchery.entity.EntityAttackBat;
import com.emoniph.witchery.util.Config;
import com.emoniph.witchery.util.ParticleEffect;
import com.emoniph.witchery.util.SoundEffect;
import com.emoniph.witchery.util.TimeUtil;
import com.emoniph.witchery.util.TransformCreature;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.WorldInfo;

import java.util.Arrays;
import java.util.List;

public class CommandVampire extends CommandBase {

    @Override
    public String getCommandName() {
        return "vampire";
    }

    @Override
    public List getCommandAliases() {
        return Arrays.asList("vampiro", "vamp");
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/vampire <bat|vision|speed|mesmerize|mist|ultimate|blood|status|level <0-10>>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "bat", "vision", "speed", "mesmerize", "mist", "ultimate", "blood", "status", "level",
                    "murcielago", "ojo", "velocidad", "hipnotizar", "niebla", "definitiva", "sangre", "estado", "nivel");
        }
        return null;
    }

    private EntityLivingBase getLookedAtEntity(EntityPlayer player, double range) {
        Vec3 vec3 = Vec3.createVectorHelper(player.posX, player.posY + (double)player.getEyeHeight(), player.posZ);
        Vec3 look = player.getLook(1.0F);
        Vec3 reachVec = vec3.addVector(look.xCoord * range, look.yCoord * range, look.zCoord * range);

        EntityLivingBase pointed = null;
        List list = player.worldObj.getEntitiesWithinAABBExcludingEntity(player,
                player.boundingBox.addCoord(look.xCoord * range, look.yCoord * range, look.zCoord * range).expand(1.0D, 1.0D, 1.0D));
        double closest = range;

        for (Object obj : list) {
            Entity entity = (Entity) obj;
            if (entity.canBeCollidedWith() && entity instanceof EntityLivingBase) {
                float border = entity.getCollisionBorderSize();
                AxisAlignedBB bb = entity.boundingBox.expand(border, border, border);
                MovingObjectPosition mop = bb.calculateIntercept(vec3, reachVec);
                if (bb.isVecInside(vec3)) {
                    if (closest >= 0.0D) {
                        pointed = (EntityLivingBase) entity;
                        closest = 0.0D;
                    }
                } else if (mop != null) {
                    double dist = vec3.distanceTo(mop.hitVec);
                    if (dist < closest || closest == 0.0D) {
                        pointed = (EntityLivingBase) entity;
                        closest = dist;
                    }
                }
            }
        }
        return pointed;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (!(sender instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) sender;
        ExtendedPlayer playerEx = ExtendedPlayer.get(player);
        if (playerEx == null) return;

        int lvl = playerEx.getVampireLevel();

        if (args.length == 0) {
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_RED + "=== Poderes de Vampiro (Nivel: " + lvl + "/10) ==="));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "/vampire bat" + EnumChatFormatting.GRAY + " (o murcielago) - Transfórmate en murciélago (Req. Nvl 7)."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "/vampire vision" + EnumChatFormatting.GRAY + " (o ojo) - Activa la visión nocturna de sangre."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "/vampire speed" + EnumChatFormatting.GRAY + " (o velocidad) - Desata velocidad y salto sobrenatural (Req. Nvl 4)."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "/vampire mesmerize" + EnumChatFormatting.GRAY + " (o hipnotizar) - Paraliza a tu víctima al mirarla (Req. Nvl 2)."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "/vampire mist" + EnumChatFormatting.GRAY + " (o niebla) - Forma de niebla intangible y resistente al fuego (Req. Nvl 5)."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "/vampire ultimate" + EnumChatFormatting.GRAY + " (o definitiva) - Desata el poder de Nivel 10 (Tormenta/Enjambre/Ataúd)."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "/vampire blood [cant]" + EnumChatFormatting.GRAY + " (o sangre) - Consulta o ajusta tu sangre."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "/vampire status" + EnumChatFormatting.GRAY + " (o estado) - Muestra tu estado vampírico completo."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "/vampire level <0-10>" + EnumChatFormatting.GRAY + " (o nivel) - Ajusta tu nivel de vampirismo."));
            return;
        }

        String sub = args[0].toLowerCase();

        if (sub.equals("status") || sub.equals("estado")) {
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_RED + "--- Estado Vampírico ---"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "Nivel: " + EnumChatFormatting.GOLD + lvl + "/10"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "Sangre: " + EnumChatFormatting.RED + playerEx.getBloodPower() + " / " + playerEx.getMaxBloodPower()));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "Reserva de Sangre: " + EnumChatFormatting.DARK_RED + playerEx.getBloodReserve()));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "Poder Seleccionado: " + EnumChatFormatting.YELLOW + playerEx.getSelectedVampirePower().name()));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "Definitiva: " + EnumChatFormatting.LIGHT_PURPLE + playerEx.getVampireUltimate().name() + " (Cargas: " + playerEx.getVampireUltimateCharges() + ")"));
            return;
        }

        if (sub.equals("level") || sub.equals("nivel")) {
            if (args.length >= 2) {
                try {
                    int targetLvl = Integer.parseInt(args[1]);
                    if (targetLvl < 0 || targetLvl > 10) {
                        player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "El nivel debe estar entre 0 y 10."));
                        return;
                    }
                    playerEx.setVampireLevel(targetLvl);
                    player.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Nivel de Vampiro ajustado a: " + targetLvl));
                    SoundEffect.WITCHERY_RANDOM_DRINK.playAtPlayer(player.worldObj, player);
                } catch (NumberFormatException e) {
                    player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Por favor ingresa un número válido entre 0 y 10."));
                }
            } else {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "Tu nivel actual de vampiro es: " + lvl));
            }
            return;
        }

        if (lvl <= 0 && !player.capabilities.isCreativeMode) {
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "No eres un vampiro. Debes ser mordido o realizar el Ritual del Despertar Sangriento."));
            return;
        }

        if (sub.equals("bat") || sub.equals("murcielago")) {
            if (lvl < 7 && !player.capabilities.isCreativeMode) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Necesitas nivel de Vampiro 7 o superior para transformarte en murciélago."));
                return;
            }

            if (playerEx.getCreatureType() == TransformCreature.BAT) {
                SoundEffect.RANDOM_FIZZ.playAtPlayer(player.worldObj, player);
                Shapeshift.INSTANCE.shiftTo(player, TransformCreature.NONE);
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "Has vuelto a tu forma humana."));
            } else {
                if (playerEx.decreaseBloodPower(ExtendedPlayer.VampirePower.BAT.INITIAL_COST, true) || player.capabilities.isCreativeMode) {
                    SoundEffect.RANDOM_FIZZ.playAtPlayer(player.worldObj, player);
                    Shapeshift.INSTANCE.shiftTo(player, TransformCreature.BAT);
                    ParticleEffect.EXPLODE.send(SoundEffect.NONE, player.worldObj, player.posX, player.posY + 0.5D, player.posZ, 0.5D, 0.5D, 12);
                    player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_RED + "¡Te has transformado en Murciélago!"));
                } else {
                    player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "No tienes suficiente sangre para transformarte."));
                    SoundEffect.NOTE_SNARE.playAtPlayer(player.worldObj, player);
                }
            }
            return;
        }

        if (sub.equals("vision") || sub.equals("ojo")) {
            playerEx.toggleVampireVision();
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Visión vampírica alternada."));
            SoundEffect.RANDOM_ORB.playAtPlayer(player.worldObj, player);
            return;
        }

        if (sub.equals("speed") || sub.equals("velocidad")) {
            if (lvl < 4 && !player.capabilities.isCreativeMode) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Necesitas nivel de Vampiro 4 o superior para usar velocidad sobrenatural."));
                return;
            }

            if (playerEx.decreaseBloodPower(ExtendedPlayer.VampirePower.SPEED.INITIAL_COST, true) || player.capabilities.isCreativeMode) {
                int dur = TimeUtil.secsToTicks(15);
                player.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, dur, 2, true));
                player.addPotionEffect(new PotionEffect(Potion.jump.id, dur, 1, true));
                SoundEffect.RANDOM_FIZZ.playAtPlayer(player.worldObj, player);
                ParticleEffect.REDDUST.send(SoundEffect.NONE, player.worldObj, player.posX, player.posY + 0.8D, player.posZ, 0.5D, 0.5D, 10);
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_RED + "Velocidad vampírica activada."));
            } else {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "No tienes suficiente sangre."));
                SoundEffect.NOTE_SNARE.playAtPlayer(player.worldObj, player);
            }
            return;
        }

        if (sub.equals("mesmerize") || sub.equals("hipnotizar") || sub.equals("paralizar")) {
            if (lvl < 2 && !player.capabilities.isCreativeMode) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Necesitas nivel de Vampiro 2 o superior para hipnotizar víctimas."));
                return;
            }

            EntityLivingBase target = getLookedAtEntity(player, 16.0D);
            if (target == null) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Debes mirar a tu víctima a los ojos para hipnotizarla."));
                return;
            }

            if (playerEx.decreaseBloodPower(ExtendedPlayer.VampirePower.MESMERIZE.INITIAL_COST, true) || player.capabilities.isCreativeMode) {
                target.addPotionEffect(new PotionEffect(Witchery.Potions.PARALYSED.id, TimeUtil.secsToTicks(8), 0));
                target.addPotionEffect(new PotionEffect(Potion.blindness.id, TimeUtil.secsToTicks(4), 0));
                SoundEffect.WITCHERY_RANDOM_HYPNOSIS.playAtPlayer(player.worldObj, player);
                ParticleEffect.SPELL.send(SoundEffect.NONE, target.worldObj, target.posX, target.posY + (double)target.height * 0.8D, target.posZ, 0.4D, 0.4D, 12);
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_RED + "¡Has hipnotizado y paralizado a " + target.getCommandSenderName() + "!"));
            } else {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "No tienes suficiente sangre."));
                SoundEffect.NOTE_SNARE.playAtPlayer(player.worldObj, player);
            }
            return;
        }

        if (sub.equals("mist") || sub.equals("niebla")) {
            if (lvl < 5 && !player.capabilities.isCreativeMode) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Necesitas nivel de Vampiro 5 o superior para entrar en forma de niebla."));
                return;
            }

            int cost = 30;
            if (playerEx.decreaseBloodPower(cost, true) || player.capabilities.isCreativeMode) {
                int dur = TimeUtil.secsToTicks(8);
                player.extinguish();
                player.addPotionEffect(new PotionEffect(Potion.invisibility.id, dur, 0, true));
                player.addPotionEffect(new PotionEffect(Potion.resistance.id, dur, 2, true));
                player.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, dur, 1, true));
                SoundEffect.WITCHERY_RANDOM_POOF.playAtPlayer(player.worldObj, player);
                ParticleEffect.SMOKE.send(SoundEffect.NONE, player.worldObj, player.posX, player.posY + 1.0D, player.posZ, 0.8D, 1.0D, 24);
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "Te has disuelto en una nube de niebla protectora."));
            } else {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "No tienes suficiente sangre."));
                SoundEffect.NOTE_SNARE.playAtPlayer(player.worldObj, player);
            }
            return;
        }

        if (sub.equals("ultimate") || sub.equals("definitiva")) {
            if (lvl < 10 && !player.capabilities.isCreativeMode) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Necesitas nivel de Vampiro 10 para usar tu poder definitivo."));
                return;
            }

            if (playerEx.getVampireUltimateCharges() <= 0 && !player.capabilities.isCreativeMode) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "No te quedan cargas de tu poder definitivo."));
                SoundEffect.NOTE_SNARE.playAtPlayer(player.worldObj, player);
                return;
            }

            switch (playerEx.getVampireUltimate()) {
                case STORM:
                    WorldInfo info = ((WorldServer)player.worldObj).getWorldInfo();
                    int dur = (300 + player.worldObj.rand.nextInt(600)) * 20;
                    info.setThunderTime(dur);
                    info.setThundering(true);
                    info.setRainTime(dur);
                    info.setRaining(true);
                    SoundEffect.MOB_WITHER_SPAWN.playAtPlayer(player.worldObj, player);
                    player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + "¡Has convocado una tormenta eléctrica que bloquea el sol!"));
                    break;
                case SWARM:
                    for (int i = 0; i < 15; i++) {
                        EntityLiving bat = ExtendedPlayer.spawnCreature(player.worldObj, EntityAttackBat.class, player.posX, player.posY + 3.0D + player.worldObj.rand.nextDouble(), player.posZ, 1, 4, ParticleEffect.SMOKE, SoundEffect.WITCHERY_RANDOM_POOF);
                        if (bat instanceof EntityAttackBat) {
                            EntityAttackBat attackBat = (EntityAttackBat) bat;
                            attackBat.setOwner(player);
                            attackBat.setIsBatHanging(false);
                            attackBat.getEntityData().setBoolean("WITCNoDrops", true);
                        }
                    }
                    SoundEffect.WITCHERY_RANDOM_POOF.playAtPlayer(player.worldObj, player);
                    player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_RED + "¡Has invocado un enjambre de murciélagos voraces!"));
                    break;
                case FARM:
                    ChunkCoordinates bed = player.getBedLocation(player.dimension);
                    if (bed != null) {
                        player.setPositionAndUpdate(bed.posX + 0.5D, bed.posY + 1.0D, bed.posZ + 0.5D);
                        SoundEffect.MOB_ENDERMEN_PORTAL.playAtPlayer(player.worldObj, player);
                        ParticleEffect.PORTAL.send(SoundEffect.NONE, player.worldObj, bed.posX + 0.5D, bed.posY + 1.0D, bed.posZ + 0.5D, 0.5D, 1.0D, 20);
                        player.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "¡Has regresado a tu santuario/ataúd!"));
                    } else {
                        player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "No tienes un ataúd o cama vinculada."));
                        return;
                    }
                    break;
                default:
                    player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "No tienes un poder definitivo asignado por Lilith."));
                    return;
            }

            if (!player.capabilities.isCreativeMode) {
                playerEx.setVampireUltimateCharges(playerEx.getVampireUltimateCharges() - 1);
                playerEx.sync();
            }
            return;
        }

        if (sub.equals("blood") || sub.equals("sangre")) {
            if (args.length >= 2) {
                try {
                    int amount = Integer.parseInt(args[1]);
                    playerEx.setBloodPower(amount);
                    player.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Sangre establecida a: " + amount));
                    SoundEffect.WITCHERY_RANDOM_DRINK.playAtPlayer(player.worldObj, player);
                } catch (NumberFormatException e) {
                    player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Por favor ingresa un número válido."));
                }
            } else {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Sangre actual: " + playerEx.getBloodPower() + " / " + playerEx.getMaxBloodPower()));
            }
            return;
        }

        player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Subcomando desconocido. Usa: /vampire <bat|vision|speed|mesmerize|mist|ultimate|blood|status|level>"));
    }
}
