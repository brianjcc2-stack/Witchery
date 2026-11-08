package com.emoniph.witchery.commands;

import com.emoniph.witchery.entity.EntityCovenWitch;
import com.emoniph.witchery.util.ParticleEffect;
import com.emoniph.witchery.util.SoundEffect;
import com.emoniph.witchery.util.TameableUtil;
import java.util.Arrays;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public class CommandCoven extends CommandBase {

    @Override
    public String getCommandName() {
        return "coven";
    }

    @Override
    public List getCommandAliases() {
        return Arrays.asList("witch", "aquelarre");
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/coven <summon|follow|stay|attack|dismiss|task|whisper|weekly> [all]";
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
            return getListOfStringsMatchingLastWord(args, "summon", "follow", "stay", "attack", "dismiss",
                    "task", "whisper", "weekly", "invocar", "seguir", "quieto", "atacar", "despedir",
                    "tarea", "consejo", "susurro", "semanal");
        } else if (args.length == 2 && (args[0].equalsIgnoreCase("summon") || args[0].equalsIgnoreCase("invocar"))) {
            return getListOfStringsMatchingLastWord(args, "all");
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

        if (args.length == 0) {
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + "=== Control de Aquelarre (Coven) ==="));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + "/coven summon [all]" + EnumChatFormatting.GRAY + " - Convoca a tus brujas."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + "/coven task / tarea" + EnumChatFormatting.GRAY + " - Muestra o solicita una misión del aquelarre."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + "/coven whisper / consejo" + EnumChatFormatting.GRAY + " - Pide un susurro de guía para el siguiente nivel."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + "/coven weekly / semanal" + EnumChatFormatting.GRAY + " - Consulta el Gran Decreto Semanal de la IA."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + "/coven follow" + EnumChatFormatting.GRAY + " - Tus brujas te escoltan y te curan."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + "/coven stay" + EnumChatFormatting.GRAY + " - Tus brujas montan guardia fija."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + "/coven attack" + EnumChatFormatting.GRAY + " - Ordena atacar al objetivo que miras."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + "/coven dismiss" + EnumChatFormatting.GRAY + " - Devuelve las brujas al plano espiritual."));
            return;
        }

        String sub = args[0].toLowerCase();

        if (sub.equals("whisper") || sub.equals("consejo") || sub.equals("susurro") || sub.equals("guia")) {
            com.emoniph.witchery.common.WitchAdvisor.whisperProgression(player, "El Aquelarre");
            return;
        }

        if (sub.equals("weekly") || sub.equals("semanal") || sub.equals("decreto")) {
            com.emoniph.witchery.common.WeeklyQuestEngine.displayWeekly(player, "El Gran Aquelarre");
            return;
        }

        if (sub.equals("task") || sub.equals("tarea") || sub.equals("mision")) {
            String activeTaskId = com.emoniph.witchery.common.CovenTaskManager.getActiveTaskId(player);
            com.emoniph.witchery.common.CovenTask currentTask = (activeTaskId != null && !activeTaskId.isEmpty()) ?
                    com.emoniph.witchery.common.CovenTaskManager.getTask(activeTaskId) : null;

            if (currentTask != null && currentTask.canComplete(player)) {
                currentTask.completeAndReward(player, "El Aquelarre");
                com.emoniph.witchery.common.CovenTaskManager.setActiveTask(player, "");
            } else if (currentTask == null) {
                com.emoniph.witchery.common.CovenTask newTask = com.emoniph.witchery.common.CovenTaskManager.getRandomTask(player);
                if (newTask != null) {
                    com.emoniph.witchery.common.CovenTaskManager.setActiveTask(player, newTask.getId());
                    player.addChatMessage(new ChatComponentText("§5§m-----------------------------------------------------"));
                    player.addChatMessage(new ChatComponentText("§d<El Aquelarre> §f\"Hermana, te hemos encomendado una nueva tarea:\""));
                    player.addChatMessage(new ChatComponentText("§e📜 " + newTask.getCategory().colorPrefix + newTask.getTitle() + " §7- " + newTask.getDescription()));
                    player.addChatMessage(new ChatComponentText("§5★ Recompensa: §6Alijo Místico + Bendición: " + newTask.getBlessingName() + " (+" + newTask.getMasteryReward() + " pts)"));
                    player.addChatMessage(new ChatComponentText("§5§m-----------------------------------------------------"));
                }
            } else {
                com.emoniph.witchery.common.CovenTaskManager.showCurrentTaskStatus(player, "El Aquelarre");
            }
            return;
        }

        if (sub.equals("summon") || sub.equals("invocar") || sub.equals("llamar")) {
            int covenSize = EntityCovenWitch.getCovenSize(player);
            if (covenSize <= 0) {
                player.addChatMessage(new ChatComponentText("\u00a7c\u00a7l[Aquelarre]\u00a7r \u00a7cNo tienes brujas en tu aquelarre todav\u00eda. Encuentra brujas en pantanos o bosques y completa sus favores.\u00a7r"));
                SoundEffect.NOTE_SNARE.playAtPlayer(player.worldObj, player);
                return;
            }

            int count = (args.length > 1 && args[1].equalsIgnoreCase("all")) ? covenSize : Math.min(covenSize, 3);
            for (int i = 0; i < count; i++) {
                EntityCovenWitch.summonCovenMember(player.worldObj, player, 600);
            }
            player.addChatMessage(new ChatComponentText("\u00a7d\u00a7l[Aquelarre]\u00a7r \u00a7aHas convocado a " + count + " bruja(s) de tu aquelarre para servirte y protegerte.\u00a7r"));
            return;
        }

        AxisAlignedBB searchBB = AxisAlignedBB.getBoundingBox(player.posX - 32.0D, player.posY - 16.0D, player.posZ - 32.0D, player.posX + 32.0D, player.posY + 16.0D, player.posZ + 32.0D);
        List<EntityCovenWitch> witches = player.worldObj.getEntitiesWithinAABB(EntityCovenWitch.class, searchBB);

        if (sub.equals("attack") || sub.equals("atacar")) {
            EntityLivingBase target = getLookedAtEntity(player, 30.0D);
            if (target == null) {
                player.addChatMessage(new ChatComponentText("\u00a7cDebes mirar al enemigo que deseas que ataque tu aquelarre.\u00a7r"));
                return;
            }

            int count = 0;
            for (EntityCovenWitch w : witches) {
                if (w.isTamed() && (w.getOwner() == player || TameableUtil.isOwner(w, player))) {
                    w.setSitting(false);
                    w.setAttackTarget(target);
                    w.setAggressive(true);
                    count++;
                }
            }

            if (count > 0) {
                player.addChatMessage(new ChatComponentText("\u00a7d\u00a7l[Aquelarre]\u00a7r \u00a7c\u00a1" + count + " bruja(s) atacando a " + target.getCommandSenderName() + "!\u00a7r"));
                SoundEffect.RANDOM_ORB.playAtPlayer(player.worldObj, player);
            } else {
                player.addChatMessage(new ChatComponentText("\u00a7cNo hay brujas de tu aquelarre cerca para atacar.\u00a7r"));
            }
            return;
        }

        if (sub.equals("follow") || sub.equals("seguir")) {
            int count = 0;
            for (EntityCovenWitch w : witches) {
                if (w.isTamed() && (w.getOwner() == player || TameableUtil.isOwner(w, player))) {
                    w.setSitting(false);
                    w.setAttackTarget(null);
                    count++;
                }
            }
            player.addChatMessage(new ChatComponentText("\u00a7d\u00a7l[Aquelarre]\u00a7r \u00a7a" + count + " bruja(s) de tu aquelarre te seguir\u00e1n y curar\u00e1n.\u00a7r"));
            return;
        }

        if (sub.equals("stay") || sub.equals("quieto") || sub.equals("esperar")) {
            int count = 0;
            for (EntityCovenWitch w : witches) {
                if (w.isTamed() && (w.getOwner() == player || TameableUtil.isOwner(w, player))) {
                    w.setSitting(true);
                    w.getNavigator().clearPathEntity();
                    w.setAttackTarget(null);
                    count++;
                }
            }
            player.addChatMessage(new ChatComponentText("\u00a7d\u00a7l[Aquelarre]\u00a7r \u00a7a" + count + " bruja(s) montar\u00e1n guardia aqu\u00ed.\u00a7r"));
            return;
        }

        if (sub.equals("dismiss") || sub.equals("despedir") || sub.equals("liberar")) {
            int count = 0;
            for (EntityCovenWitch w : witches) {
                if (w.isTamed() && (w.getOwner() == player || TameableUtil.isOwner(w, player))) {
                    ParticleEffect.PORTAL.send(SoundEffect.MOB_ENDERMEN_PORTAL, w, 1.0D, 2.0D, 16);
                    w.setDead();
                    count++;
                }
            }
            player.addChatMessage(new ChatComponentText("\u00a7d\u00a7l[Aquelarre]\u00a7r \u00a7e" + count + " bruja(s) han regresado al plano espiritual.\u00a7r"));
            return;
        }

        player.addChatMessage(new ChatComponentText("\u00a75Subcomando desconocido. Usa: /coven <summon|follow|stay|attack|dismiss> [all]"));
    }
}
