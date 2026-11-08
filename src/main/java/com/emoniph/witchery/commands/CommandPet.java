package com.emoniph.witchery.commands;

import com.emoniph.witchery.common.ExtendedPlayer;
import com.emoniph.witchery.util.ParticleEffect;
import com.emoniph.witchery.util.SoundEffect;
import com.emoniph.witchery.util.TameableUtil;
import java.util.Arrays;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public class CommandPet extends CommandBase {

    @Override
    public String getCommandName() {
        return "pet";
    }

    @Override
    public List getCommandAliases() {
        return Arrays.asList("mascota", "familiar");
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/pet <follow [all]|stay [all]|attack|summon [all]|list|rename <name>|release|inventory>";
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
            return getListOfStringsMatchingLastWord(args, "follow", "stay", "attack", "summon", "list", "rename", "release", "inventory",
                    "seguir", "quieto", "atacar", "invocar", "lista", "renombrar", "liberar", "inventario");
        } else if (args.length == 2 && (args[0].equalsIgnoreCase("stay") || args[0].equalsIgnoreCase("follow") || args[0].equalsIgnoreCase("summon")
                || args[0].equalsIgnoreCase("quieto") || args[0].equalsIgnoreCase("seguir") || args[0].equalsIgnoreCase("invocar"))) {
            return getListOfStringsMatchingLastWord(args, "all");
        }
        return null;
    }

    private EntityLivingBase getLookedAtEntity(EntityPlayer player, double range) {
        Vec3 vec3 = Vec3.createVectorHelper(player.posX, player.posY, player.posZ);
        vec3.yCoord += player.getEyeHeight();
        Vec3 vec31 = player.getLook(1.0F);
        Vec3 vec32 = vec3.addVector(vec31.xCoord * range, vec31.yCoord * range, vec31.zCoord * range);

        EntityLivingBase pointedEntity = null;
        List list = player.worldObj.getEntitiesWithinAABBExcludingEntity(player, player.boundingBox.addCoord(vec31.xCoord * range, vec31.yCoord * range, vec31.zCoord * range).expand(1.0D, 1.0D, 1.0D));
        double d2 = range;

        for (int i = 0; i < list.size(); ++i) {
            Entity entity = (Entity)list.get(i);
            if (entity.canBeCollidedWith() && entity instanceof EntityLivingBase) {
                float f1 = entity.getCollisionBorderSize();
                AxisAlignedBB axisalignedbb = entity.boundingBox.expand((double)f1, (double)f1, (double)f1);
                MovingObjectPosition mop = axisalignedbb.calculateIntercept(vec3, vec32);
                if (axisalignedbb.isVecInside(vec3)) {
                    if (d2 >= 0.0D) {
                        pointedEntity = (EntityLivingBase)entity;
                        d2 = 0.0D;
                    }
                } else if (mop != null) {
                    double d3 = vec3.distanceTo(mop.hitVec);
                    if (d3 < d2 || d2 == 0.0D) {
                        pointedEntity = (EntityLivingBase)entity;
                        d2 = d3;
                    }
                }
            }
        }
        return pointedEntity;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (!(sender instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) sender;

        if (args.length == 0) {
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "=== Control de Mascotas y Familiares ==="));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "/pet follow [all]" + EnumChatFormatting.GRAY + " (o seguir) - Te sigue y defiende."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "/pet stay [all]" + EnumChatFormatting.GRAY + " (o quieto) - Se sienta y espera."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "/pet attack" + EnumChatFormatting.GRAY + " (o atacar) - Ataca al objetivo que estás mirando."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "/pet summon [all]" + EnumChatFormatting.GRAY + " (o invocar) - Teletransporta tus mascotas a tu lado."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "/pet list" + EnumChatFormatting.GRAY + " (o lista) - Muestra tus mascotas cercanas y su salud."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "/pet inventory" + EnumChatFormatting.GRAY + " (o inv) - Abre su equipamiento."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "/pet rename <nombre>" + EnumChatFormatting.GRAY + " - Asigna un nombre a la mascota."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "/pet release" + EnumChatFormatting.GRAY + " (o liberar) - Libera la mascota."));
            return;
        }

        String subCommand = args[0].toLowerCase();
        EntityLivingBase lookedAtEntity = getLookedAtEntity(player, 30.0D);

        // --- SUBCOMMAND: ATTACK ---
        if (subCommand.equals("attack") || subCommand.equals("atacar")) {
            if (lookedAtEntity == null || !lookedAtEntity.isEntityAlive()) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Debes mirar directamente a un enemigo vivo para ordenarle a tus mascotas atacar."));
                return;
            }

            if (lookedAtEntity == player) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "¡No puedes ordenar a tus mascotas que te ataquen a ti mismo!"));
                return;
            }

            if (lookedAtEntity instanceof EntityPlayer) {
                ExtendedPlayer targetEx = ExtendedPlayer.get((EntityPlayer) lookedAtEntity);
                ExtendedPlayer playerEx = ExtendedPlayer.get(player);
                if (targetEx != null && playerEx != null && !playerEx.getUnbreakableVowID().isEmpty() && playerEx.getUnbreakableVowID().equals(targetEx.getUnbreakableVowID())) {
                    player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "¡No puedes ordenar a tus mascotas atacar a un aliado con Juramento Inquebrantable!"));
                    return;
                }
            }

            if (lookedAtEntity instanceof EntityTameable && TameableUtil.isOwner((EntityTameable) lookedAtEntity, player)) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "¡No puedes ordenar atacar a otra de tus propias mascotas!"));
                return;
            }

            AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(player.posX - 30.0D, player.posY - 30.0D, player.posZ - 30.0D, player.posX + 30.0D, player.posY + 30.0D, player.posZ + 30.0D);
            List<EntityTameable> pets = player.worldObj.getEntitiesWithinAABB(EntityTameable.class, bb);

            int attackCount = 0;
            for (EntityTameable pet : pets) {
                if (pet.isTamed() && TameableUtil.isOwner(pet, player) && !pet.isSitting()) {
                    pet.setAttackTarget(lookedAtEntity);
                    attackCount++;
                }
            }

            if (attackCount > 0) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "¡" + attackCount + " mascota(s) atacando al objetivo!"));
            } else {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "No hay mascotas activas y siguiéndote cerca que puedan atacar."));
            }
            return;
        }

        // --- SUBCOMMAND: SUMMON / TP ---
        if (subCommand.equals("summon") || subCommand.equals("tp") || subCommand.equals("invocar") || subCommand.equals("llamar")) {
            boolean summonAll = args.length > 1 && args[1].equalsIgnoreCase("all");
            if (!summonAll && lookedAtEntity instanceof EntityTameable && ((EntityTameable)lookedAtEntity).isTamed() && TameableUtil.isOwner((EntityTameable)lookedAtEntity, player)) {
                EntityTameable pet = (EntityTameable) lookedAtEntity;
                pet.setLocationAndAngles(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
                pet.setSitting(false);
                pet.getNavigator().clearPathEntity();
                ParticleEffect.PORTAL.send(SoundEffect.MOB_ENDERMEN_PORTAL, pet, 0.5D, 1.0D, 16);
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "¡Mascota teletransportada a tu posición!"));
                return;
            }

            AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(player.posX - 64.0D, player.posY - 32.0D, player.posZ - 64.0D, player.posX + 64.0D, player.posY + 32.0D, player.posZ + 64.0D);
            List<EntityTameable> pets = player.worldObj.getEntitiesWithinAABB(EntityTameable.class, bb);
            int count = 0;
            for (EntityTameable pet : pets) {
                if (pet.isTamed() && TameableUtil.isOwner(pet, player)) {
                    pet.setLocationAndAngles(player.posX + (player.getRNG().nextDouble() - 0.5D) * 2.0D, player.posY, player.posZ + (player.getRNG().nextDouble() - 0.5D) * 2.0D, player.rotationYaw, player.rotationPitch);
                    pet.setSitting(false);
                    pet.getNavigator().clearPathEntity();
                    ParticleEffect.PORTAL.send(SoundEffect.MOB_ENDERMEN_PORTAL, pet, 0.5D, 1.0D, 16);
                    count++;
                }
            }

            if (count > 0) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "¡" + count + " mascota(s) invocada(s) a tu lado!"));
            } else {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "No se encontraron mascotas domesticadas en un radio de 64 bloques."));
            }
            return;
        }

        // --- SUBCOMMAND: LIST / STATUS ---
        if (subCommand.equals("list") || subCommand.equals("status") || subCommand.equals("lista") || subCommand.equals("estado")) {
            AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(player.posX - 128.0D, player.posY - 64.0D, player.posZ - 128.0D, player.posX + 128.0D, player.posY + 64.0D, player.posZ + 128.0D);
            List<EntityTameable> pets = player.worldObj.getEntitiesWithinAABB(EntityTameable.class, bb);
            int count = 0;
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "=== Tus Mascotas Cercanas ==="));
            for (EntityTameable pet : pets) {
                if (pet.isTamed() && TameableUtil.isOwner(pet, player)) {
                    String name = pet.hasCustomNameTag() ? pet.getCustomNameTag() : pet.getCommandSenderName();
                    String state = pet.isSitting() ? (EnumChatFormatting.YELLOW + "[Esperando]") : (EnumChatFormatting.GREEN + "[Siguiendo]");
                    float hp = pet.getHealth();
                    float maxHp = pet.getMaxHealth();
                    int dist = (int) pet.getDistanceToEntity(player);
                    player.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "- " + EnumChatFormatting.AQUA + name + " " + state + EnumChatFormatting.GRAY + " (" + String.format("%.1f", hp) + "/" + String.format("%.1f", maxHp) + " HP) - a " + dist + "m"));
                    count++;
                }
            }

            if (count == 0) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "No tienes mascotas domesticadas cerca."));
            }
            return;
        }

        // --- GROUP ORDERS: STAY ALL / FOLLOW ALL ---
        boolean isStayCmd = subCommand.equals("stay") || subCommand.equals("quieto") || subCommand.equals("esperar");
        boolean isFollowCmd = subCommand.equals("follow") || subCommand.equals("seguir");

        boolean allStay = isStayCmd && (args.length > 1 && args[1].equalsIgnoreCase("all") || lookedAtEntity == null);
        boolean allFollow = isFollowCmd && (args.length > 1 && args[1].equalsIgnoreCase("all") || lookedAtEntity == null);

        if (allStay || allFollow) {
            AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(player.posX - 32.0D, player.posY - 16.0D, player.posZ - 32.0D, player.posX + 32.0D, player.posY + 16.0D, player.posZ + 32.0D);
            List<EntityTameable> pets = player.worldObj.getEntitiesWithinAABB(EntityTameable.class, bb);
            int count = 0;
            for (EntityTameable p : pets) {
                if (p.isTamed() && TameableUtil.isOwner(p, player)) {
                    p.setSitting(allStay);
                    if (allStay) {
                        p.setAttackTarget(null);
                        p.getNavigator().clearPathEntity();
                    }
                    count++;
                }
            }
            if (count > 0) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + (allStay ? (count + " mascota(s) ahora están esperando.") : (count + " mascota(s) ahora te están siguiendo."))));
            } else {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "No hay mascotas cercanas para ordenar."));
            }
            return;
        }

        // --- TARGETED ORDERS: REQUIRE LOOKING AT A PET (OR NEAREST OWNED PET) ---
        if (lookedAtEntity == null || !(lookedAtEntity instanceof EntityTameable)) {
            AxisAlignedBB searchBB = AxisAlignedBB.getBoundingBox(player.posX - 8.0D, player.posY - 4.0D, player.posZ - 8.0D, player.posX + 8.0D, player.posY + 4.0D, player.posZ + 8.0D);
            List<EntityTameable> nearbyPets = player.worldObj.getEntitiesWithinAABB(EntityTameable.class, searchBB);
            EntityTameable closestPet = null;
            double closestDistSq = Double.MAX_VALUE;
            for (EntityTameable p : nearbyPets) {
                if (p.isTamed() && TameableUtil.isOwner(p, player)) {
                    double dSq = p.getDistanceSqToEntity(player);
                    if (dSq < closestDistSq) {
                        closestDistSq = dSq;
                        closestPet = p;
                    }
                }
            }
            if (closestPet != null) {
                lookedAtEntity = closestPet;
            }
        }

        if (lookedAtEntity == null || !(lookedAtEntity instanceof EntityTameable)) {
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Debes mirar a una de tus mascotas o estar cerca de una para darle esta orden."));
            return;
        }

        EntityTameable pet = (EntityTameable) lookedAtEntity;

        if (!pet.isTamed() || !TameableUtil.isOwner(pet, player)) {
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Esta mascota no es tuya. No obedecerá tus órdenes."));
            return;
        }

        if (subCommand.equals("stay") || subCommand.equals("quieto") || subCommand.equals("esperar")) {
            pet.setSitting(true);
            pet.setAttackTarget(null);
            pet.getNavigator().clearPathEntity();
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "La mascota esperará aquí."));
        } else if (subCommand.equals("follow") || subCommand.equals("seguir")) {
            pet.setSitting(false);
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "La mascota te seguirá y protegerá."));
        } else if (subCommand.equals("rename") || subCommand.equals("nombrar") || subCommand.equals("renombrar")) {
            if (args.length < 2) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Uso: /pet rename <nuevo nombre>"));
                return;
            }
            StringBuilder nameBuilder = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                if (i > 1) nameBuilder.append(" ");
                nameBuilder.append(args[i]);
            }
            String newName = nameBuilder.toString().trim();
            pet.setCustomNameTag(newName);
            pet.setAlwaysRenderNameTag(true);
            ParticleEffect.INSTANT_SPELL.send(SoundEffect.RANDOM_ORB, pet, 0.5D, 1.0D, 16);
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Tu mascota ha sido renombrada a: " + EnumChatFormatting.AQUA + newName));
        } else if (subCommand.equals("release") || subCommand.equals("liberar")) {
            pet.setTamed(false);
            pet.func_152115_b(""); // Clear owner ID string
            pet.setSitting(false);
            pet.setAttackTarget(null);
            pet.getNavigator().clearPathEntity();
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "Has liberado a la mascota. Ya no te seguirá."));
        } else if (subCommand.equals("inventory") || subCommand.equals("inv") || subCommand.equals("inventario")) {
            player.displayGUIChest(new com.emoniph.witchery.infusion.infusions.symbols.InventoryMobEquipment(pet));
        } else {
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Comando desconocido. Opciones: follow [all], stay [all], attack, summon [all], list, rename <nombre>, release, inventory."));
        }
    }
}
