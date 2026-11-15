package com.emoniph.witchery.commands;

import com.emoniph.witchery.common.ExtendedPlayer;
import com.emoniph.witchery.entity.EntityGoblin;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandHobgoblin extends CommandBase {

    @Override
    public String getCommandName() {
        return "hobgoblin";
    }

    @Override
    public List getCommandAliases() {
        return Arrays.asList("goblin");
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/hobgoblin <name|follow|stay|work|ir|adelante|atras|attack|dismiss|summon|whisper|rito|chest|altar|lab|horno|destilar|caldero|receta|ascender|inventory|guia>";
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
            return getListOfStringsMatchingLastWord(args, "name", "nombre", "renombrar", "follow", "stay", "work", "attack", "ir", "goto", "coord", "mover", "adelante", "forward", "atras", "backward", "release", "inventory", "guia", "help", "ayuda",
                    "seguir", "quieto", "trabajar", "atacar", "liberar", "mochila", "dismiss", "summon", "despedir", "llamar", "invocar",
                    "whisper", "hablar", "decir", "rito", "ritual", "chest", "cofre", "altar", "lab", "alquimia", "horno", "hornos",
                    "destilar", "destileria", "caldero", "cauldron", "ascender", "awaken", "receta", "recipe", "libro");
        } else if (args.length == 2 && (args[0].equalsIgnoreCase("stay") || args[0].equalsIgnoreCase("follow") || args[0].equalsIgnoreCase("work")
                || args[0].equalsIgnoreCase("quieto") || args[0].equalsIgnoreCase("seguir") || args[0].equalsIgnoreCase("trabajar")
                || args[0].equalsIgnoreCase("ir") || args[0].equalsIgnoreCase("goto") || args[0].equalsIgnoreCase("coord")
                || args[0].equalsIgnoreCase("adelante") || args[0].equalsIgnoreCase("forward") || args[0].equalsIgnoreCase("atras") || args[0].equalsIgnoreCase("backward"))) {
            return getListOfStringsMatchingLastWord(args, "all", "aqui");
        } else if (args.length == 2 && (args[0].equalsIgnoreCase("rito") || args[0].equalsIgnoreCase("ritual"))) {
            return getListOfStringsMatchingLastWord(args, "tormenta", "dia", "noche", "bendicion", "proteccion");
        } else if (args.length == 2 && (args[0].equalsIgnoreCase("receta") || args[0].equalsIgnoreCase("recipe") || args[0].equalsIgnoreCase("libro"))) {
            return getListOfStringsMatchingLastWord(args, "caldero", "kettle", "destilar");
        } else if (args.length >= 2 && (args[0].equalsIgnoreCase("whisper") || args[0].equalsIgnoreCase("hablar") || args[0].equalsIgnoreCase("decir") || args[0].equalsIgnoreCase("w"))) {
            return getListOfStringsMatchingLastWord(args, "estado", "altar", "cofre", "rito", "horno", "destilar", "caldero", "lab", "protegeme", "hola");
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
        Vec3 vec3 = Vec3.createVectorHelper(player.posX, player.posY, player.posZ);
        vec3.yCoord += player.getEyeHeight();
        Vec3 vec31 = player.getLook(1.0F);
        Vec3 vec32 = vec3.addVector(vec31.xCoord * range, vec31.yCoord * range, vec31.zCoord * range);

        EntityLivingBase pointedEntity = null;
        List list = player.worldObj.getEntitiesWithinAABBExcludingEntity(player, player.boundingBox.addCoord(vec31.xCoord * range, vec31.yCoord * range, vec31.zCoord * range).expand(1.0D, 1.0D, 1.0D));
        double d2 = range;

        for (int i = 0; i < list.size(); ++i) {
            net.minecraft.entity.Entity entity = (net.minecraft.entity.Entity)list.get(i);
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
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "=== Hobgoblins y Sucesor ==="));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "/hobgoblin name <nombre>" + EnumChatFormatting.GRAY + " (o nombre) - Asigna un nombre a tu hobgoblin."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "/hobgoblin summon" + EnumChatFormatting.GRAY + " (o llamar) - Invoca a tu Sucesor a tu lado."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "/hobgoblin dismiss" + EnumChatFormatting.GRAY + " (o despedir) - Resguarda a tu Sucesor en el plano astral."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "/hobgoblin lab" + EnumChatFormatting.GRAY + " (o alquimia) - Control de laboratorio (hornos, destilerías, calderos)."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "/hobgoblin rito [nombre]" + EnumChatFormatting.GRAY + " - Carga los ingredientes de cofres y activa el rito."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "/hobgoblin horno" + EnumChatFormatting.GRAY + " - Recolecta humos y carbón; abastece leña y frascos de arcilla."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "/hobgoblin destilar" + EnumChatFormatting.GRAY + " - Recoge destilados y produce recetas desde cofres."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "/hobgoblin caldero" + EnumChatFormatting.GRAY + " - Llena agua/fuego, embotella pociones a cofres y transmuta."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "/hobgoblin chest" + EnumChatFormatting.GRAY + " (o cofre) - Deposita su inventario en cofres cercanos."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "/hobgoblin altar" + EnumChatFormatting.GRAY + " - Canaliza y monitorea la energía del altar."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "/hobgoblin whisper <mensaje>" + EnumChatFormatting.GRAY + " (o hablar) - Comunicación telepática."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "/hobgoblin ascender" + EnumChatFormatting.GRAY + " - Eleva a un Hobgoblin a Sucesor."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "/hobgoblin follow [all]" + EnumChatFormatting.GRAY + " (o seguir) - Te sigue lealmente como guardia."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "/hobgoblin stay [all]" + EnumChatFormatting.GRAY + " (o quieto) - Permanece en guardia en el lugar."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "/hobgoblin work [all]" + EnumChatFormatting.GRAY + " (o trabajar) - Cosecha cultivos mágicos y mina."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "/hobgoblin ir [all] [x y z]" + EnumChatFormatting.GRAY + " (o goto) - Desplaza a tu hobgoblin a donde miras o coordenadas."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "/hobgoblin adelante|atras [all] [bloques]" + EnumChatFormatting.GRAY + " - Movimiento relativo en su dirección."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "/hobgoblin inventory" + EnumChatFormatting.GRAY + " (o mochila) - Abre su mochila de carga."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "/hobgoblin guia" + EnumChatFormatting.GRAY + " (o help) - Guía completa de todas las funciones."));
            return;
        }

        String subCommand = args[0].toLowerCase();

        if (subCommand.equals("guia") || subCommand.equals("help") || subCommand.equals("ayuda")) {
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "=== Guía: Sucesor y Hobgoblins ==="));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "1. DOMESTICACIÓN Y ASCENSIÓN:"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "   • Domestica con una " + EnumChatFormatting.GREEN + "Esmeralda" + EnumChatFormatting.WHITE + " y dale un " + EnumChatFormatting.AQUA + "Pentáculo de Koboldite" + EnumChatFormatting.WHITE + " (o usa " + EnumChatFormatting.YELLOW + "/hobgoblin ascender" + EnumChatFormatting.WHITE + ")."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "   • Su aspecto cambia a marrón cobrizo, sube a 60 HP y te asiste fielmente."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "   • Nómbralo con una etiqueta (" + EnumChatFormatting.AQUA + "Name Tag" + EnumChatFormatting.WHITE + ") o con " + EnumChatFormatting.YELLOW + "/hobgoblin name <nombre>" + EnumChatFormatting.WHITE + "."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "   • Clic derecho con mano vacía: alternar entre Seguir y Esperar (Shift + Clic para su mochila)."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "   • Movimiento táctico: " + EnumChatFormatting.YELLOW + "/hobgoblin ir [all] [x y z]" + EnumChatFormatting.WHITE + " (a donde miras o coordenadas) y " + EnumChatFormatting.YELLOW + "/hobgoblin adelante|atras [bloques]" + EnumChatFormatting.WHITE + "."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "2. BARRERA DEFENSIVA Y PROTECCIÓN:"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "   • Si te atacan o disparan, activa una Barrera Arcana (85% reducción de daño, repulsión, Resistencia y Regeneración)."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "   • Si tu salud cae bajo el 35%, te envía sanación vital de emergencia."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "3. COMPAÑERO ETERNO:"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "   • " + EnumChatFormatting.AQUA + "/hobgoblin dismiss" + EnumChatFormatting.WHITE + ": Lo resguarda en el plano astral guardando su mochila y vida."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "   • " + EnumChatFormatting.AQUA + "/hobgoblin summon" + EnumChatFormatting.WHITE + ": Lo invoca de regreso a tu lado inmediatamente."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "4. AUTOMATIZACIÓN DE RITUALES:"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "   • " + EnumChatFormatting.AQUA + "/hobgoblin rito [nombre]" + EnumChatFormatting.WHITE + ": Carga ingredientes de cofres y activa el ritual."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "5. CONTROL DE LABORATORIO:"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "   • " + EnumChatFormatting.AQUA + "/hobgoblin lab" + EnumChatFormatting.WHITE + ": Atiende hornos, destilerías y calderos automáticamente."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "   • " + EnumChatFormatting.AQUA + "/hobgoblin horno" + EnumChatFormatting.WHITE + ": Recolecta humos/carbón y abastece leña/frascos."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "   • " + EnumChatFormatting.AQUA + "/hobgoblin destilar" + EnumChatFormatting.WHITE + ": Recolecta destilados y produce recetas desde cofres."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "   • " + EnumChatFormatting.AQUA + "/hobgoblin chest" + EnumChatFormatting.WHITE + ": Almacena todos los productos en tus cofres."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "6. LIBROS DE RECETA:"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "   • Usa " + EnumChatFormatting.AQUA + "/hobgoblin receta <caldero|kettle|destilar> <ingredientes...>" + EnumChatFormatting.WHITE + "."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "   Coloca el libro en un cofre cercano y el Sucesor lo leerá automáticamente."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "==================================="));
            return;
        }

        // --- NAME / NOMBRE ---
        if (subCommand.equals("name") || subCommand.equals("nombre") || subCommand.equals("renombrar")) {
            if (args.length < 2) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Uso: /hobgoblin name <nombre> (o /hobgoblin name reset)"));
                return;
            }

            StringBuilder nameBuilder = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                if (i > 1) nameBuilder.append(" ");
                nameBuilder.append(args[i]);
            }
            String newName = nameBuilder.toString().trim();

            AxisAlignedBB searchBB = AxisAlignedBB.getBoundingBox(player.posX - 16.0D, player.posY - 8.0D, player.posZ - 16.0D, player.posX + 16.0D, player.posY + 8.0D, player.posZ + 16.0D);
            List<EntityGoblin> nearbyGoblins = player.worldObj.getEntitiesWithinAABB(EntityGoblin.class, searchBB);
            EntityGoblin targetGoblin = null;

            EntityLivingBase looked = getLookedAtEntity(player, 16.0D);
            if (looked instanceof EntityGoblin) {
                EntityGoblin g = (EntityGoblin) looked;
                if (g.isTamed && player.getCommandSenderName().equalsIgnoreCase(g.tamedOwnerName)) {
                    targetGoblin = g;
                }
            }

            if (targetGoblin == null) {
                double closestDistSq = Double.MAX_VALUE;
                for (EntityGoblin g : nearbyGoblins) {
                    if (g.isTamed && player.getCommandSenderName().equalsIgnoreCase(g.tamedOwnerName)) {
                        double dSq = g.getDistanceSqToEntity(player);
                        if (dSq < closestDistSq) {
                            closestDistSq = dSq;
                            targetGoblin = g;
                        }
                    }
                }
            }

            if (targetGoblin == null) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "No hay ningún hobgoblin tuyo cerca para nombrar."));
                return;
            }

            if (newName.equalsIgnoreCase("reset") || newName.equalsIgnoreCase("clear")) {
                targetGoblin.setCustomNameTag("");
                targetGoblin.setAlwaysRenderNameTag(false);
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Nombre del hobgoblin restablecido."));
            } else {
                targetGoblin.setCustomNameTag(newName);
                targetGoblin.setAlwaysRenderNameTag(true);
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Has nombrado a tu hobgoblin: " + newName));
                targetGoblin.sendWhisper(player, "Mi nombre ahora es " + newName + ".");
            }
            return;
        }

        // --- SUMMON / LLAMAR ---
        if (subCommand.equals("summon") || subCommand.equals("llamar") || subCommand.equals("invocar") || subCommand.equals("ven")) {
            ExtendedPlayer playerEx = ExtendedPlayer.get(player);
            if (playerEx != null && playerEx.hasStoredSuccessor()) {
                NBTTagCompound nbt = playerEx.getStoredSuccessorNBT();
                EntityGoblin goblin = new EntityGoblin(player.worldObj);
                goblin.readFromNBT(nbt);
                goblin.setLocationAndAngles(player.posX + 0.5D, player.posY, player.posZ + 0.5D, player.rotationYaw, 0.0F);
                player.worldObj.spawnEntityInWorld(goblin);
                playerEx.setStoredSuccessorNBT(null);

                com.emoniph.witchery.util.ParticleEffect.PORTAL.send(com.emoniph.witchery.util.SoundEffect.RANDOM_LEVELUP, goblin, 1.5D, 2.0D, 35);
                com.emoniph.witchery.util.ParticleEffect.INSTANT_SPELL.send(com.emoniph.witchery.util.SoundEffect.WITCHERY_RANDOM_HYPNOSIS, goblin, 1.0D, 1.5D, 25);
                goblin.sendWhisper(player, "He regresado a tu lado.");
                return;
            }

            AxisAlignedBB wideBB = AxisAlignedBB.getBoundingBox(player.posX - 64.0D, player.posY - 32.0D, player.posZ - 64.0D, player.posX + 64.0D, player.posY + 32.0D, player.posZ + 64.0D);
            List<EntityGoblin> loadedGoblins = player.worldObj.getEntitiesWithinAABB(EntityGoblin.class, wideBB);
            for (EntityGoblin g : loadedGoblins) {
                if (g.isTamed && g.isSuccessor() && player.getCommandSenderName().equalsIgnoreCase(g.tamedOwnerName)) {
                    g.setPositionAndUpdate(player.posX + 0.5D, player.posY, player.posZ + 0.5D);
                    g.isSitting = false;
                    g.isFollowing = true;
                    com.emoniph.witchery.util.ParticleEffect.PORTAL.send(com.emoniph.witchery.util.SoundEffect.MOB_ENDERMEN_PORTAL, g, 1.0D, 1.5D, 20);
                    g.sendWhisper(player, "He acudido a tu llamado.");
                    return;
                }
            }

            player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "No tienes ningún Sucesor guardado ni cerca en este mundo."));
            return;
        }

        // --- DISMISS / DESPEDIR ---
        if (subCommand.equals("dismiss") || subCommand.equals("despedir") || subCommand.equals("guardar") || subCommand.equals("adios")) {
            ExtendedPlayer playerEx = ExtendedPlayer.get(player);
            if (playerEx == null) return;

            AxisAlignedBB searchBB = AxisAlignedBB.getBoundingBox(player.posX - 32.0D, player.posY - 16.0D, player.posZ - 32.0D, player.posX + 32.0D, player.posY + 16.0D, player.posZ + 32.0D);
            List<EntityGoblin> nearbyGoblins = player.worldObj.getEntitiesWithinAABB(EntityGoblin.class, searchBB);
            EntityGoblin successor = null;
            for (EntityGoblin g : nearbyGoblins) {
                if (g.isTamed && g.isSuccessor() && player.getCommandSenderName().equalsIgnoreCase(g.tamedOwnerName)) {
                    successor = g;
                    break;
                }
            }
            if (successor == null) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "No tienes ningún Sucesor cerca para despedir."));
                return;
            }

            successor.sendWhisper(player, "Me resguardo. Llámame con /hobgoblin llamar cuando me necesites.");
            com.emoniph.witchery.util.ParticleEffect.SMOKE.send(com.emoniph.witchery.util.SoundEffect.WITCHERY_RANDOM_POOF, successor, 1.0D, 1.5D, 30);
            com.emoniph.witchery.util.ParticleEffect.PORTAL.send(com.emoniph.witchery.util.SoundEffect.RANDOM_ORB, successor, 1.0D, 1.5D, 20);

            NBTTagCompound nbt = new NBTTagCompound();
            successor.writeToNBT(nbt);
            playerEx.setStoredSuccessorNBT(nbt);
            successor.setDead();
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "Tu Sucesor se ha resguardado en el plano astral."));
            return;
        }

        // --- LAB / ALQUIMIA ---
        if (subCommand.equals("lab") || subCommand.equals("alquimia") || subCommand.equals("laboratorio")) {
            AxisAlignedBB searchBB = AxisAlignedBB.getBoundingBox(player.posX - 24.0D, player.posY - 12.0D, player.posZ - 24.0D, player.posX + 24.0D, player.posY + 12.0D, player.posZ + 24.0D);
            List<EntityGoblin> nearby = player.worldObj.getEntitiesWithinAABB(EntityGoblin.class, searchBB);
            for (EntityGoblin g : nearby) {
                if (g.isTamed && g.isSuccessor() && player.getCommandSenderName().equalsIgnoreCase(g.tamedOwnerName)) {
                    g.successorRole = "alquimia";
                    g.isSitting = false;
                    int ovens = g.manageNearbyOvens();
                    int dists = g.manageNearbyDistilleries();
                    int kettles = g.manageNearbyKettles();
                    g.storeAllToChests();
                    g.sendWhisper(player, "Modo Laboratorio activado (" + ovens + " hornos, " + dists + " destilerías, " + kettles + " calderos). Guardando en cofres.");
                    return;
                }
            }
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "No hay ningún Sucesor cerca para asignar al laboratorio."));
            return;
        }

        // --- HORNO / HORNOS ---
        if (subCommand.equals("horno") || subCommand.equals("hornos") || subCommand.equals("smelt")) {
            AxisAlignedBB searchBB = AxisAlignedBB.getBoundingBox(player.posX - 24.0D, player.posY - 12.0D, player.posZ - 24.0D, player.posX + 24.0D, player.posY + 12.0D, player.posZ + 24.0D);
            List<EntityGoblin> nearby = player.worldObj.getEntitiesWithinAABB(EntityGoblin.class, searchBB);
            for (EntityGoblin g : nearby) {
                if (g.isTamed && g.isSuccessor() && player.getCommandSenderName().equalsIgnoreCase(g.tamedOwnerName)) {
                    int ovens = g.manageNearbyOvens();
                    g.storeAllToChests();
                    g.sendWhisper(player, "Hornos atendidos (" + ovens + "). Humos y carbón recolectados.");
                    return;
                }
            }
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "No hay ningún Sucesor cerca para atender hornos."));
            return;
        }

        // --- DESTILAR / DESTILERIA ---
        if (subCommand.equals("destilar") || subCommand.equals("destileria") || subCommand.equals("distill")) {
            AxisAlignedBB searchBB = AxisAlignedBB.getBoundingBox(player.posX - 24.0D, player.posY - 12.0D, player.posZ - 24.0D, player.posX + 24.0D, player.posY + 12.0D, player.posZ + 24.0D);
            List<EntityGoblin> nearby = player.worldObj.getEntitiesWithinAABB(EntityGoblin.class, searchBB);
            for (EntityGoblin g : nearby) {
                if (g.isTamed && g.isSuccessor() && player.getCommandSenderName().equalsIgnoreCase(g.tamedOwnerName)) {
                    int dists = g.manageNearbyDistilleries();
                    g.storeAllToChests();
                    g.sendWhisper(player, "He atendido " + dists + " destilerías. Destilados recogidos, frascos de arcilla colocados y recetas puestas a producir desde los cofres.");
                    return;
                }
            }
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "No hay ningún Sucesor Arcano cerca para atender destilerías."));
            return;
        }

        // --- CALDERO / CAULDRON ---
        if (subCommand.equals("caldero") || subCommand.equals("cauldron") || subCommand.equals("pot") || subCommand.equals("pocion")) {
            AxisAlignedBB searchBB = AxisAlignedBB.getBoundingBox(player.posX - 24.0D, player.posY - 12.0D, player.posZ - 24.0D, player.posX + 24.0D, player.posY + 12.0D, player.posZ + 24.0D);
            List<EntityGoblin> nearby = player.worldObj.getEntitiesWithinAABB(EntityGoblin.class, searchBB);
            for (EntityGoblin g : nearby) {
                if (g.isTamed && g.isSuccessor() && player.getCommandSenderName().equalsIgnoreCase(g.tamedOwnerName)) {
                    int cServiced = g.manageNearbyCauldrons();
                    boolean transmuted = g.transmuteInCauldron(player);
                    g.storeAllToChests();
                    if (cServiced > 0 && !transmuted) {
                        g.sendWhisper(player, "He atendido " + cServiced + " calderos: rellenado con agua de cubos, encendido fuego o embotellado de pociones a cofres.");
                    }
                    return;
                }
            }
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "No hay ningún Sucesor Arcano cerca para operar calderos."));
            return;
        }

        // --- RITO / RITUAL ---
        if (subCommand.equals("rito") || subCommand.equals("ritual")) {
            String ritualArg = "";
            if (args.length > 1) {
                StringBuilder rsb = new StringBuilder();
                for (int i = 1; i < args.length; i++) {
                    if (i > 1) rsb.append(" ");
                    rsb.append(args[i]);
                }
                ritualArg = rsb.toString();
            }

            AxisAlignedBB searchBB = AxisAlignedBB.getBoundingBox(player.posX - 24.0D, player.posY - 12.0D, player.posZ - 24.0D, player.posX + 24.0D, player.posY + 12.0D, player.posZ + 24.0D);
            List<EntityGoblin> nearby = player.worldObj.getEntitiesWithinAABB(EntityGoblin.class, searchBB);
            for (EntityGoblin g : nearby) {
                if (g.isTamed && g.isSuccessor() && player.getCommandSenderName().equalsIgnoreCase(g.tamedOwnerName)) {
                    g.loadAndActivateCircle(player, ritualArg);
                    return;
                }
            }
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "No hay ningún Sucesor Arcano cerca para preparar y activar el ritual."));
            return;
        }

        // --- WHISPER / HABLAR ---
        if (subCommand.equals("whisper") || subCommand.equals("hablar") || subCommand.equals("decir") || subCommand.equals("w")) {
            AxisAlignedBB searchBB = AxisAlignedBB.getBoundingBox(player.posX - 24.0D, player.posY - 12.0D, player.posZ - 24.0D, player.posX + 24.0D, player.posY + 12.0D, player.posZ + 24.0D);
            List<EntityGoblin> nearby = player.worldObj.getEntitiesWithinAABB(EntityGoblin.class, searchBB);
            EntityGoblin successor = null;
            for (EntityGoblin g : nearby) {
                if (g.isTamed && g.isSuccessor() && player.getCommandSenderName().equalsIgnoreCase(g.tamedOwnerName)) {
                    successor = g;
                    break;
                }
            }
            if (successor == null) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "No hay ningún Sucesor Arcano cerca con quien comunicarte."));
                return;
            }

            if (args.length < 2) {
                successor.reportStatusToOwner(player);
                return;
            }

            StringBuilder sb = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                if (i > 1) sb.append(" ");
                sb.append(args[i]);
            }
            String message = sb.toString().toLowerCase();

            if (message.contains("estado") || message.contains("reporte") || message.contains("salud") || message.contains("mana")) {
                successor.reportStatusToOwner(player);
            } else if (message.contains("lab") || message.contains("alquimia")) {
                successor.successorRole = "alquimia";
                successor.manageNearbyOvens();
                successor.manageNearbyDistilleries();
                successor.manageNearbyKettles();
                successor.storeAllToChests();
                successor.sendWhisper(player, "¡Control del laboratorio activado! Cuidando hornos, destilerías y calderos.");
            } else if (message.contains("horno")) {
                int ovens = successor.manageNearbyOvens();
                successor.storeAllToChests();
                successor.sendWhisper(player, "Hornos de bruja atendidos (" + ovens + "). Humos almacenados y leña colocada.");
            } else if (message.contains("destil")) {
                int dists = successor.manageNearbyDistilleries();
                successor.storeAllToChests();
                successor.sendWhisper(player, "Destilerías atendidas (" + dists + "). Recetas y frascos de arcilla abastecidos.");
            } else if (message.contains("rito") || message.contains("ritual")) {
                successor.loadAndActivateCircle(player, "");
            } else if (message.contains("cofre") || message.contains("guardar") || message.contains("ordenar")) {
                int count = successor.storeAllToChests();
                successor.sendWhisper(player, "Objetos guardados en cofres cercanos (" + count + ").");
            } else if (message.contains("caldero") || message.contains("pocion") || message.contains("transmuta")) {
                int c = successor.manageNearbyCauldrons();
                successor.transmuteInCauldron(player);
                successor.storeAllToChests();
            } else if (message.contains("altar")) {
                successor.successorRole = "altar";
                successor.sendWhisper(player, "Entendido, vigilaré y canalizaré la energía del altar.");
            } else if (message.contains("protege") || message.contains("escudo") || message.contains("defensa")) {
                successor.sendWhisper(player, "Barrera arcana lista ante cualquier agresión.");
            } else if (message.contains("hola") || message.contains("saludos") || message.contains("buenos")) {
                successor.sendWhisper(player, "Saludos. A tu servicio.");
            } else if (message.contains("gracias")) {
                successor.sendWhisper(player, "A tu servicio.");
            } else {
                successor.sendWhisper(player, "Entendido: \"" + sb.toString() + "\".");
            }
            return;
        }

        // --- CHEST / COFRE ---
        if (subCommand.equals("chest") || subCommand.equals("cofre")) {
            AxisAlignedBB searchBB = AxisAlignedBB.getBoundingBox(player.posX - 24.0D, player.posY - 12.0D, player.posZ - 24.0D, player.posX + 24.0D, player.posY + 12.0D, player.posZ + 24.0D);
            List<EntityGoblin> nearby = player.worldObj.getEntitiesWithinAABB(EntityGoblin.class, searchBB);
            for (EntityGoblin g : nearby) {
                if (g.isTamed && g.isSuccessor() && player.getCommandSenderName().equalsIgnoreCase(g.tamedOwnerName)) {
                    int count = g.storeAllToChests();
                    g.sendWhisper(player, "Objetos guardados en cofres cercanos (" + count + ").");
                    return;
                }
            }
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "No hay ningún Sucesor cerca para guardar en cofres."));
            return;
        }

        // --- ALTAR ---
        if (subCommand.equals("altar")) {
            AxisAlignedBB searchBB = AxisAlignedBB.getBoundingBox(player.posX - 24.0D, player.posY - 12.0D, player.posZ - 24.0D, player.posX + 24.0D, player.posY + 12.0D, player.posZ + 24.0D);
            List<EntityGoblin> nearby = player.worldObj.getEntitiesWithinAABB(EntityGoblin.class, searchBB);
            for (EntityGoblin g : nearby) {
                if (g.isTamed && g.isSuccessor() && player.getCommandSenderName().equalsIgnoreCase(g.tamedOwnerName)) {
                    g.successorRole = "altar";
                    g.sendWhisper(player, "Vigilando el altar y canalizando energía.");
                    return;
                }
            }
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "No hay ningún Sucesor cerca para vincular al altar."));
            return;
        }

        // --- RECETA / RECIPE ---
        // /hobgoblin receta <tipo> <ingrediente1> <ingrediente2> ...
        // /hobgoblin receta caldero Nightshade Wheat Bone_Meal
        // /hobgoblin receta kettle Mandrake_Root Belladonna_Flower
        // /hobgoblin receta destilar Belladonna_Flower
        if (subCommand.equals("receta") || subCommand.equals("recipe") || subCommand.equals("libro")) {
            if (args.length < 2) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "Uso: /hobgoblin receta <tipo> <ingrediente1> <ingrediente2> ..."));
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "Tipos: caldero, kettle, destilar"));
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "Ejemplo: /hobgoblin receta caldero Nightshade Wheat BoneMeal"));
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "Coloca el libro en un cofre cercano y el Sucesor lo leerá."));
                return;
            }
            String recipeType = args[1].toLowerCase();
            java.util.List<String> ingredientsList = new java.util.ArrayList<String>();
            for (int i = 2; i < args.length; i++) ingredientsList.add(args[i].replace("_", " "));

            if (ingredientsList.isEmpty()) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Debes indicar al menos un ingrediente."));
                return;
            }

            // Crear libro escrito con los ingredientes como páginas
            net.minecraft.item.ItemStack book = new net.minecraft.item.ItemStack(net.minecraft.init.Items.written_book);
            net.minecraft.nbt.NBTTagCompound tag = new net.minecraft.nbt.NBTTagCompound();
            String title = recipeType.contains("caldero") || recipeType.contains("brew") || recipeType.contains("pot") ? "caldero" :
                           recipeType.contains("kettle") || recipeType.contains("calderito") ? "kettle" : "destilar";
            tag.setString("title", title + " receta");
            tag.setString("author", player.getCommandSenderName());
            net.minecraft.nbt.NBTTagList pages = new net.minecraft.nbt.NBTTagList();
            // Cada ingrediente en su propia página (para kettles que no usan libro) y juntos en página 1 para caldero
            StringBuilder allIngredients = new StringBuilder();
            for (String ing : ingredientsList) {
                allIngredients.append(ing).append("\n");
            }
            pages.appendTag(new net.minecraft.nbt.NBTTagString(allIngredients.toString()));
            tag.setTag("pages", pages);
            book.setTagCompound(tag);
            // Nombre visible del libro
            net.minecraft.nbt.NBTTagCompound display = new net.minecraft.nbt.NBTTagCompound();
            display.setString("Name", EnumChatFormatting.LIGHT_PURPLE + "Receta de " + title + " §7[Sucesor]");
            tag.setTag("display", display);

            // Dar el libro al jugador
            if (!player.inventory.addItemStackToInventory(book)) {
                player.entityDropItem(book, 0.0F);
            }
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + "📖 Libro de receta creado: §e" + title));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "Ingredientes: §f" + String.join(", ", ingredientsList)));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "Colócalo en un cofre cercano y usa /hobgoblin " + (title.equals("caldero") ? "caldero" : title.equals("kettle") ? "kettle" : "destilar")));
            return;
        }

        EntityLivingBase lookedAtEntity = getLookedAtEntity(player, 30.0D);

        // --- ASCENDER / AWAKEN ---
        if (subCommand.equals("ascender") || subCommand.equals("awaken")) {
            if (lookedAtEntity instanceof EntityGoblin) {
                EntityGoblin goblin = (EntityGoblin) lookedAtEntity;
                goblin.awakenAsSuccessor(player);
                return;
            }
            AxisAlignedBB searchBB = AxisAlignedBB.getBoundingBox(player.posX - 8.0D, player.posY - 4.0D, player.posZ - 8.0D, player.posX + 8.0D, player.posY + 4.0D, player.posZ + 8.0D);
            List<EntityGoblin> nearby = player.worldObj.getEntitiesWithinAABB(EntityGoblin.class, searchBB);
            for (EntityGoblin g : nearby) {
                if (g.isTamed && player.getCommandSenderName().equalsIgnoreCase(g.tamedOwnerName) && !g.isSuccessor()) {
                    g.awakenAsSuccessor(player);
                    return;
                }
            }
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Mira a un Hobgoblin domesticado o quédate cerca de uno para ascenderlo a Sucesor."));
            return;
        }

        // --- ATACAR ---
        if (subCommand.equals("attack") || subCommand.equals("atacar")) {
            if (lookedAtEntity == null) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Debes mirar al enemigo que deseas que ataquen tus hobgoblins."));
                return;
            }

            AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(player.posX - 30.0D, player.posY - 30.0D, player.posZ - 30.0D, player.posX + 30.0D, player.posY + 30.0D, player.posZ + 30.0D);
            List<EntityGoblin> goblins = player.worldObj.getEntitiesWithinAABB(EntityGoblin.class, bb);

            int attackCount = 0;
            for (EntityGoblin goblin : goblins) {
                if (goblin.isTamed && player.getCommandSenderName().equalsIgnoreCase(goblin.tamedOwnerName) && !goblin.isSitting) {
                    goblin.setAttackTarget(lookedAtEntity);
                    attackCount++;
                }
            }

            if (attackCount > 0) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "" + attackCount + " hobgoblins han fijado el objetivo."));
            } else {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "No hay hobgoblins domesticados cerca que puedan atacar."));
            }
            return;
        }

        // --- GOTO / IR / COORD ---
        if (subCommand.equals("ir") || subCommand.equals("goto") || subCommand.equals("coord") || subCommand.equals("mover")) {
            boolean all = false;
            int coordStartIndex = 1;
            if (args.length > 1 && (args[1].equalsIgnoreCase("all") || args[1].equalsIgnoreCase("todos"))) {
                all = true;
                coordStartIndex = 2;
            }

            double targetX = 0, targetY = 0, targetZ = 0;
            boolean hasCoords = false;

            if (args.length >= coordStartIndex + 3) {
                try {
                    targetX = Double.parseDouble(args[coordStartIndex]);
                    targetY = Double.parseDouble(args[coordStartIndex + 1]);
                    targetZ = Double.parseDouble(args[coordStartIndex + 2]);
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
                    player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Mira hacia un bloque en el suelo o escribe: /hobgoblin ir [all] <x> <y> <z>"));
                    return;
                }
            }

            AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(player.posX - 32.0D, player.posY - 16.0D, player.posZ - 32.0D, player.posX + 32.0D, player.posY + 16.0D, player.posZ + 32.0D);
            List<EntityGoblin> goblins = player.worldObj.getEntitiesWithinAABB(EntityGoblin.class, bb);
            List<EntityGoblin> targets = new ArrayList<EntityGoblin>();

            if (all) {
                for (EntityGoblin g : goblins) {
                    if (g.isTamed && player.getCommandSenderName().equalsIgnoreCase(g.tamedOwnerName)) {
                        targets.add(g);
                    }
                }
            } else {
                EntityGoblin target = null;
                if (lookedAtEntity instanceof EntityGoblin) {
                    EntityGoblin g = (EntityGoblin) lookedAtEntity;
                    if (g.isTamed && player.getCommandSenderName().equalsIgnoreCase(g.tamedOwnerName)) {
                        target = g;
                    }
                }
                if (target == null) {
                    double closestDist = Double.MAX_VALUE;
                    for (EntityGoblin g : goblins) {
                        if (g.isTamed && player.getCommandSenderName().equalsIgnoreCase(g.tamedOwnerName)) {
                            double d = g.getDistanceSqToEntity(player);
                            if (d < closestDist) {
                                closestDist = d;
                                target = g;
                            }
                        }
                    }
                }
                if (target != null) {
                    targets.add(target);
                }
            }

            if (targets.isEmpty()) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "No hay hobgoblins tuyos cerca para desplazarse."));
                return;
            }

            for (EntityGoblin g : targets) {
                g.isFollowing = false;
                g.isSitting = false;
                g.isWorking = false;
                g.setAttackTarget(null);
                g.getNavigator().tryMoveToXYZ(targetX, targetY, targetZ, 1.25D);
                g.sendWhisper(player, "Yendo a (" + (int)targetX + ", " + (int)targetY + ", " + (int)targetZ + ").");
            }

            com.emoniph.witchery.util.ParticleEffect.INSTANT_SPELL.send(com.emoniph.witchery.util.SoundEffect.NOTE_PLING, player.worldObj, targetX, targetY + 0.5D, targetZ, 0.5D, 0.5D, 12);
            return;
        }

        // --- ADELANTE / ATRAS ---
        if (subCommand.equals("adelante") || subCommand.equals("forward") || subCommand.equals("atras") || subCommand.equals("backward")) {
            boolean all = false;
            double dist = 5.0D;
            int distIdx = 1;
            if (args.length > 1 && (args[1].equalsIgnoreCase("all") || args[1].equalsIgnoreCase("todos"))) {
                all = true;
                distIdx = 2;
            }
            if (args.length > distIdx) {
                try {
                    dist = Double.parseDouble(args[distIdx]);
                } catch (NumberFormatException ignored) {}
            }

            AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(player.posX - 32.0D, player.posY - 16.0D, player.posZ - 32.0D, player.posX + 32.0D, player.posY + 16.0D, player.posZ + 32.0D);
            List<EntityGoblin> goblins = player.worldObj.getEntitiesWithinAABB(EntityGoblin.class, bb);
            List<EntityGoblin> targets = new ArrayList<EntityGoblin>();

            if (all) {
                for (EntityGoblin g : goblins) {
                    if (g.isTamed && player.getCommandSenderName().equalsIgnoreCase(g.tamedOwnerName)) {
                        targets.add(g);
                    }
                }
            } else {
                EntityGoblin target = null;
                if (lookedAtEntity instanceof EntityGoblin) {
                    EntityGoblin g = (EntityGoblin) lookedAtEntity;
                    if (g.isTamed && player.getCommandSenderName().equalsIgnoreCase(g.tamedOwnerName)) {
                        target = g;
                    }
                }
                if (target == null) {
                    double closestDist = Double.MAX_VALUE;
                    for (EntityGoblin g : goblins) {
                        if (g.isTamed && player.getCommandSenderName().equalsIgnoreCase(g.tamedOwnerName)) {
                            double d = g.getDistanceSqToEntity(player);
                            if (d < closestDist) {
                                closestDist = d;
                                target = g;
                            }
                        }
                    }
                }
                if (target != null) {
                    targets.add(target);
                }
            }

            if (targets.isEmpty()) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "No hay hobgoblins tuyos cerca."));
                return;
            }

            boolean forward = subCommand.equals("adelante") || subCommand.equals("forward");
            for (EntityGoblin g : targets) {
                float yaw = g.rotationYawHead;
                double dx = (forward ? -MathHelper.sin(yaw * (float) Math.PI / 180.0F) : MathHelper.sin(yaw * (float) Math.PI / 180.0F)) * dist;
                double dz = (forward ? MathHelper.cos(yaw * (float) Math.PI / 180.0F) : -MathHelper.cos(yaw * (float) Math.PI / 180.0F)) * dist;
                g.isFollowing = false;
                g.isSitting = false;
                g.isWorking = false;
                g.setAttackTarget(null);
                g.getNavigator().tryMoveToXYZ(g.posX + dx, g.posY, g.posZ + dz, 1.25D);
                g.sendWhisper(player, (forward ? "Avanzando " : "Retrocediendo ") + (int)dist + " bloques.");
            }
            return;
        }

        boolean allStay = (subCommand.equals("stay") || subCommand.equals("quieto") || subCommand.equals("esperar")) && (args.length > 1 && args[1].equalsIgnoreCase("all"));
        boolean allFollow = (subCommand.equals("follow") || subCommand.equals("seguir")) && (args.length > 1 && args[1].equalsIgnoreCase("all"));
        boolean allWork = (subCommand.equals("work") || subCommand.equals("trabajar") || subCommand.equals("curro")) && (args.length > 1 && args[1].equalsIgnoreCase("all"));

        if (allStay || allFollow || allWork) {
            AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(player.posX - 32.0D, player.posY - 16.0D, player.posZ - 32.0D, player.posX + 32.0D, player.posY + 16.0D, player.posZ + 32.0D);
            List<EntityGoblin> goblins = player.worldObj.getEntitiesWithinAABB(EntityGoblin.class, bb);
            int count = 0;
            for (EntityGoblin g : goblins) {
                if (g.isTamed && player.getCommandSenderName().equalsIgnoreCase(g.tamedOwnerName)) {
                    if (allStay) {
                        g.isSitting = true;
                        g.isFollowing = false;
                        g.isWorking = false;
                        g.setAttackTarget(null);
                        g.getNavigator().clearPathEntity();
                    } else if (allFollow) {
                        g.isSitting = false;
                        g.isFollowing = true;
                        g.isWorking = false;
                    } else if (allWork) {
                        g.isSitting = false;
                        g.isFollowing = false;
                        g.isWorking = true;
                        g.setAttackTarget(null);
                    }
                    count++;
                }
            }
            if (allStay) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "" + count + " hobgoblins ahora están esperando aquí."));
            } else if (allFollow) {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + "" + count + " hobgoblins ahora te están siguiendo."));
            } else {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "" + count + " hobgoblins ahora están trabajando en la zona (cosecha y minería)."));
            }
            return;
        }

        if (lookedAtEntity == null || !(lookedAtEntity instanceof EntityGoblin)) {
            AxisAlignedBB searchBB = AxisAlignedBB.getBoundingBox(player.posX - 8.0D, player.posY - 4.0D, player.posZ - 8.0D, player.posX + 8.0D, player.posY + 4.0D, player.posZ + 8.0D);
            List<EntityGoblin> nearbyGoblins = player.worldObj.getEntitiesWithinAABB(EntityGoblin.class, searchBB);
            EntityGoblin closest = null;
            double closestDistSq = Double.MAX_VALUE;
            for (EntityGoblin g : nearbyGoblins) {
                if (g.isTamed && player.getCommandSenderName().equalsIgnoreCase(g.tamedOwnerName)) {
                    double dSq = g.getDistanceSqToEntity(player);
                    if (dSq < closestDistSq) {
                        closestDistSq = dSq;
                        closest = g;
                    }
                }
            }
            if (closest != null) {
                lookedAtEntity = closest;
            }
        }

        if (lookedAtEntity == null || !(lookedAtEntity instanceof EntityGoblin)) {
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Debes mirar a uno de tus hobgoblins o estar cerca de uno para darle esta orden."));
            return;
        }

        EntityGoblin goblin = (EntityGoblin) lookedAtEntity;

        if (!goblin.isTamed || goblin.tamedOwnerName == null || !goblin.tamedOwnerName.equalsIgnoreCase(player.getCommandSenderName())) {
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Este hobgoblin no te pertenece. No obedecerá tus órdenes."));
            return;
        }

        if (subCommand.equals("stay") || subCommand.equals("quieto") || subCommand.equals("esperar") || subCommand.equals("guardia")) {
            goblin.isSitting = true;
            goblin.isFollowing = false;
            goblin.isWorking = false;
            goblin.setAttackTarget(null);
            goblin.getNavigator().clearPathEntity();
            goblin.sendWhisper(player, "Esperando aquí.");
        } else if (subCommand.equals("follow") || subCommand.equals("seguir")) {
            goblin.isSitting = false;
            goblin.isFollowing = true;
            goblin.isWorking = false;
            goblin.sendWhisper(player, "Siguiéndote.");
        } else if (subCommand.equals("work") || subCommand.equals("trabajar") || subCommand.equals("curro")) {
            goblin.isSitting = false;
            goblin.isFollowing = false;
            goblin.isWorking = true;
            goblin.setAttackTarget(null);
            goblin.sendWhisper(player, "Trabajando en el área.");
        } else if (subCommand.equals("release") || subCommand.equals("liberar")) {
            goblin.isTamed = false;
            goblin.tamedOwnerName = "";
            goblin.isSitting = false;
            goblin.isFollowing = false;
            goblin.isWorking = false;
            goblin.setAttackTarget(null);
            goblin.getNavigator().clearPathEntity();
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "Has liberado al hobgoblin."));
        } else if (subCommand.equals("inventory") || subCommand.equals("inv") || subCommand.equals("mochila") || subCommand.equals("inventario")) {
            if (player.isSneaking()) {
                player.displayGUIChest(new com.emoniph.witchery.infusion.infusions.symbols.InventoryMobEquipment(goblin));
            } else {
                player.displayGUIChest(goblin.goblinInventory);
            }
        } else {
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Comando desconocido. Escribe /hobgoblin guia para ver todos los comandos."));
        }
    }
}
