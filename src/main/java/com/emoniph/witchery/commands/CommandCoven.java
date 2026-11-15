package com.emoniph.witchery.commands;

import com.emoniph.witchery.entity.EntityCovenWitch;
import com.emoniph.witchery.util.ParticleEffect;
import com.emoniph.witchery.util.SoundEffect;
import com.emoniph.witchery.util.TameableUtil;
import java.util.ArrayList;
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
import net.minecraft.util.MathHelper;
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
        return "/coven <summon|follow|stay|attack|ir|adelante|atras|dismiss|task [claim|abandon|status]|whisper|weekly|raid|guia> [all]";
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
            return getListOfStringsMatchingLastWord(args, "summon", "follow", "stay", "attack", "ir", "goto", "coord", "mover", "adelante", "forward", "atras", "backward", "dismiss",
                    "task", "whisper", "weekly", "raid", "guia", "help", "ayuda", "invocar", "seguir", "quieto", "atacar", "despedir",
                    "tarea", "consejo", "susurro", "semanal", "desafiar", "duelo");
        } else if (args.length == 2 && (args[0].equalsIgnoreCase("summon") || args[0].equalsIgnoreCase("invocar")
                || args[0].equalsIgnoreCase("ir") || args[0].equalsIgnoreCase("goto") || args[0].equalsIgnoreCase("coord")
                || args[0].equalsIgnoreCase("adelante") || args[0].equalsIgnoreCase("forward") || args[0].equalsIgnoreCase("atras") || args[0].equalsIgnoreCase("backward"))) {
            return getListOfStringsMatchingLastWord(args, "all", "aqui");
        } else if (args.length == 2 && (args[0].equalsIgnoreCase("task") || args[0].equalsIgnoreCase("tarea") || args[0].equalsIgnoreCase("mision"))) {
            return getListOfStringsMatchingLastWord(args, "claim", "abandon", "status", "entregar", "abandonar", "ver");
        }
        return null;
    }

    private MovingObjectPosition getTargetBlock(EntityPlayer player, double reach) {
        Vec3 eyePos = Vec3.createVectorHelper(player.posX, player.posY + player.getEyeHeight(), player.posZ);
        Vec3 lookVec = player.getLook(1.0F);
        Vec3 reachVec = eyePos.addVector(lookVec.xCoord * reach, lookVec.yCoord * reach, lookVec.zCoord * reach);
        return player.worldObj.rayTraceBlocks(eyePos, reachVec);
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
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + "/coven ir [x y z]" + EnumChatFormatting.GRAY + " (o goto) - Marchan a donde miras o a coordenadas."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + "/coven adelante|atras [bloques]" + EnumChatFormatting.GRAY + " - Desplazamiento en formación."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + "/coven dismiss" + EnumChatFormatting.GRAY + " - Devuelve las brujas al plano espiritual."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + "/coven raid / desafiar" + EnumChatFormatting.GRAY + " - Desafía o provoca el asalto de un Aquelarre Rival."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + "/coven guia / help" + EnumChatFormatting.GRAY + " - Guía completa sobre el sistema de Aquelarre."));
            return;
        }

        String sub = args[0].toLowerCase();

        if (sub.equals("help") || sub.equals("ayuda") || sub.equals("guia")) {
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + "=== Guía del Aquelarre (Coven) ==="));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "1. Reclutar: " + EnumChatFormatting.WHITE + "Encuentra Brujas de Aquelarre en el mundo (requiere tener Familiar activo) y completa su favor para unirlas (máx. 6)."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "2. Poder Ritual y Varita: " + EnumChatFormatting.WHITE + "Cada bruja reclutada amplifica tus rituales de círculo/caldero y reduce los fallos de la Rama Mística hasta 0% (con 6 brujas)."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "3. Misiones y Decretos: " + EnumChatFormatting.WHITE + "Usa /coven task para misiones rápidas (+Maestría y Alijos) y /coven weekly para el Gran Decreto Semanal."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "4. Combate y Maniobras: " + EnumChatFormatting.WHITE + "Usa /coven summon [all], /coven attack, /coven ir (apuntando o con coordenadas) y /coven raid."));
            return;
        }

        if (sub.equals("raid") || sub.equals("desafiar") || sub.equals("duelo") || sub.equals("emboscada")) {
            boolean triggered = com.emoniph.witchery.common.CovenRaidManager.triggerRaid(player, true);
            if (triggered) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + "¡Has retado a las sombras! Un Aquelarre Rival ha respondido a tu llamado."));
            } else {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "No se pudo invocar la incursión en esta posición."));
            }
            return;
        }

        if (sub.equals("whisper") || sub.equals("consejo") || sub.equals("susurro")) {
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

            if (args.length >= 2) {
                String sub2 = args[1].toLowerCase();
                if (sub2.equals("abandon") || sub2.equals("abandonar") || sub2.equals("cancelar") || sub2.equals("skip")) {
                    if (currentTask == null) {
                        player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "No tienes ninguna tarea activa que cancelar."));
                    } else {
                        com.emoniph.witchery.common.CovenTaskManager.setActiveTask(player, "");
                        player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "Has descartado la tarea «" + currentTask.getTitle() + "». Puedes solicitar una nueva con /coven task."));
                    }
                    return;
                }
                if (sub2.equals("status") || sub2.equals("info") || sub2.equals("ver")) {
                    com.emoniph.witchery.common.CovenTaskManager.showCurrentTaskStatus(player, "El Aquelarre");
                    return;
                }
                if (sub2.equals("claim") || sub2.equals("entregar") || sub2.equals("completar")) {
                    if (currentTask == null) {
                        player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "No tienes ninguna tarea activa. Solicita una con /coven task."));
                    } else if (currentTask.canComplete(player)) {
                        currentTask.completeAndReward(player, "El Aquelarre");
                        com.emoniph.witchery.common.CovenTaskManager.setActiveTask(player, "");
                    } else {
                        player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Aún no tienes los tributos requeridos. Revisa tu objetivo con /coven task status."));
                    }
                    return;
                }
            }

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
                    player.addChatMessage(new ChatComponentText("§8(Usa /coven task claim para entregarla, o /coven task abandon si deseas otra)"));
                    player.addChatMessage(new ChatComponentText("§5§m-----------------------------------------------------"));
                }
            } else {
                com.emoniph.witchery.common.CovenTaskManager.showCurrentTaskStatus(player, "El Aquelarre");
                player.addChatMessage(new ChatComponentText("§8(Puedes descartarla con §e/coven task abandon§8 o entregarla con §a/coven task claim§8 cuando reúnas los ítems)"));
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

        // --- SUBCOMMAND: GOTO / IR / COORD ---
        if (sub.equals("ir") || sub.equals("goto") || sub.equals("coord") || sub.equals("mover")) {
            double targetX = 0, targetY = 0, targetZ = 0;
            boolean hasCoords = false;

            if (args.length >= 4) {
                try {
                    targetX = Double.parseDouble(args[1]);
                    targetY = Double.parseDouble(args[2]);
                    targetZ = Double.parseDouble(args[3]);
                    hasCoords = true;
                } catch (NumberFormatException ignored) {}
            }

            if (!hasCoords) {
                MovingObjectPosition mop = getTargetBlock(player, 48.0D);
                if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                    targetX = (double) mop.blockX + 0.5D;
                    targetY = (double) (mop.sideHit == 1 ? mop.blockY + 1 : mop.blockY);
                    targetZ = (double) mop.blockZ + 0.5D;
                    hasCoords = true;
                } else {
                    player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Mira hacia un bloque en el suelo o escribe: /coven ir <x> <y> <z>"));
                    return;
                }
            }

            int count = 0;
            for (EntityCovenWitch w : witches) {
                if (w.isTamed() && (w.getOwner() == player || TameableUtil.isOwner(w, player))) {
                    w.setSitting(false);
                    w.setAttackTarget(null);
                    w.getNavigator().tryMoveToXYZ(targetX, targetY, targetZ, 1.25D);
                    count++;
                }
            }

            if (count > 0) {
                ParticleEffect.INSTANT_SPELL.send(SoundEffect.NOTE_PLING, player.worldObj, targetX, targetY + 0.5D, targetZ, 0.5D, 0.5D, 14);
                player.addChatMessage(new ChatComponentText("\u00a7d\u00a7l[Aquelarre]\u00a7r \u00a7a" + count + " bruja(s) marchando hacia (" + (int)targetX + ", " + (int)targetY + ", " + (int)targetZ + ").\u00a7r"));
            } else {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "No hay brujas de tu aquelarre cerca para desplazarse."));
            }
            return;
        }

        // --- SUBCOMMAND: ADELANTE / ATRAS ---
        if (sub.equals("adelante") || sub.equals("forward") || sub.equals("atras") || sub.equals("backward")) {
            double dist = 5.0D;
            if (args.length > 1) {
                try {
                    dist = Double.parseDouble(args[1]);
                } catch (NumberFormatException ignored) {}
            }

            boolean forward = sub.equals("adelante") || sub.equals("forward");
            int count = 0;
            for (EntityCovenWitch w : witches) {
                if (w.isTamed() && (w.getOwner() == player || TameableUtil.isOwner(w, player))) {
                    float yaw = w.rotationYawHead;
                    double dx = (forward ? -MathHelper.sin(yaw * (float) Math.PI / 180.0F) : MathHelper.sin(yaw * (float) Math.PI / 180.0F)) * dist;
                    double dz = (forward ? MathHelper.cos(yaw * (float) Math.PI / 180.0F) : -MathHelper.cos(yaw * (float) Math.PI / 180.0F)) * dist;
                    w.setSitting(false);
                    w.setAttackTarget(null);
                    w.getNavigator().tryMoveToXYZ(w.posX + dx, w.posY, w.posZ + dz, 1.25D);
                    count++;
                }
            }

            if (count > 0) {
                player.addChatMessage(new ChatComponentText("\u00a7d\u00a7l[Aquelarre]\u00a7r \u00a7a" + count + " bruja(s) " + (forward ? "avanzando " : "retrocediendo ") + (int)dist + " bloques.\u00a7r"));
            } else {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "No hay brujas de tu aquelarre cerca."));
            }
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
