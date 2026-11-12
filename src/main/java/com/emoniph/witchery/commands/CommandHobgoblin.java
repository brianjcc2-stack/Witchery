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
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

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
        return "/hobgoblin <follow|stay|work|dismiss|summon|whisper|rito|chest|altar|lab|horno|destilar|caldero|receta|ascender|inventory|guia>";
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
            return getListOfStringsMatchingLastWord(args, "follow", "stay", "work", "attack", "release", "inventory", "guia", "help", "ayuda",
                    "seguir", "quieto", "trabajar", "atacar", "liberar", "mochila", "dismiss", "summon", "despedir", "llamar", "invocar",
                    "whisper", "hablar", "decir", "rito", "ritual", "chest", "cofre", "altar", "lab", "alquimia", "horno", "hornos",
                    "destilar", "destileria", "caldero", "cauldron", "ascender", "awaken", "receta", "recipe", "libro");
        } else if (args.length == 2 && (args[0].equalsIgnoreCase("stay") || args[0].equalsIgnoreCase("follow") || args[0].equalsIgnoreCase("work")
                || args[0].equalsIgnoreCase("quieto") || args[0].equalsIgnoreCase("seguir") || args[0].equalsIgnoreCase("trabajar"))) {
            return getListOfStringsMatchingLastWord(args, "all");
        } else if (args.length == 2 && (args[0].equalsIgnoreCase("rito") || args[0].equalsIgnoreCase("ritual"))) {
            return getListOfStringsMatchingLastWord(args, "tormenta", "dia", "noche", "bendicion", "proteccion");
        } else if (args.length == 2 && (args[0].equalsIgnoreCase("receta") || args[0].equalsIgnoreCase("recipe") || args[0].equalsIgnoreCase("libro"))) {
            return getListOfStringsMatchingLastWord(args, "caldero", "kettle", "destilar");
        } else if (args.length >= 2 && (args[0].equalsIgnoreCase("whisper") || args[0].equalsIgnoreCase("hablar") || args[0].equalsIgnoreCase("decir") || args[0].equalsIgnoreCase("w"))) {
            return getListOfStringsMatchingLastWord(args, "estado", "altar", "cofre", "rito", "horno", "destilar", "caldero", "lab", "protegeme", "hola");
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
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "╔════════════════════════════════════════════╗"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + "  ★ SUCESOR ARCANO Y HOBGOBLINS ★"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "╚════════════════════════════════════════════╝"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "/hobgoblin summon" + EnumChatFormatting.GRAY + " (o llamar) - Invoca a tu Sucesor a tu lado."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "/hobgoblin dismiss" + EnumChatFormatting.GRAY + " (o despedir) - Resguarda a tu Sucesor en el plano astral."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "/hobgoblin lab" + EnumChatFormatting.GRAY + " (o alquimia) - Control total de laboratorio (hornos, destilerías, calderos)."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "/hobgoblin rito [nombre]" + EnumChatFormatting.GRAY + " - Carga los ingredientes de cofres y activa el rito."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "/hobgoblin horno" + EnumChatFormatting.GRAY + " - Recolecta humos y carbón; abastece leña y frascos de arcilla."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "/hobgoblin destilar" + EnumChatFormatting.GRAY + " - Recoge destilados y produce recetas desde cofres."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "/hobgoblin caldero" + EnumChatFormatting.GRAY + " - Llena agua/fuego, embotella pociones a cofres y transmuta."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "/hobgoblin chest" + EnumChatFormatting.GRAY + " (o cofre) - Deposita su inventario en cofres cercanos."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "/hobgoblin altar" + EnumChatFormatting.GRAY + " - Canaliza y monitorea la energía del altar."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "/hobgoblin whisper <mensaje>" + EnumChatFormatting.GRAY + " (o hablar) - Comunicación telepática."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "/hobgoblin ascender" + EnumChatFormatting.GRAY + " - Eleva a un Hobgoblin a Sucesor Arcano pálido."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "/hobgoblin follow [all]" + EnumChatFormatting.GRAY + " (o seguir) - Te sigue lealmente como guardia."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "/hobgoblin stay [all]" + EnumChatFormatting.GRAY + " (o quieto) - Permanece en guardia en el lugar."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "/hobgoblin work [all]" + EnumChatFormatting.GRAY + " (o trabajar) - Cosecha cultivos mágicos y mina."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "/hobgoblin inventory" + EnumChatFormatting.GRAY + " (o mochila) - Abre su mochila de carga."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "/hobgoblin guia" + EnumChatFormatting.GRAY + " (o help) - Guía completa de todas las funciones."));
            return;
        }

        String subCommand = args[0].toLowerCase();

        if (subCommand.equals("guia") || subCommand.equals("help") || subCommand.equals("ayuda")) {
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "╔═════════════════════════════════════════════════════════╗"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + "       ★ GUÍA MAESTRA: SUCESOR ARCANO Y HOBGOBLINS ★"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "╚═════════════════════════════════════════════════════════╝"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "1. TRANSFORMACIÓN PÁLIDA:"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "   • Domestica con una " + EnumChatFormatting.GREEN + "Esmeralda" + EnumChatFormatting.WHITE + " y dale un " + EnumChatFormatting.AQUA + "Pentáculo de Koboldite" + EnumChatFormatting.WHITE + " (o usa " + EnumChatFormatting.LIGHT_PURPLE + "/hobgoblin ascender" + EnumChatFormatting.WHITE + ")."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "   • Su piel se vuelve " + EnumChatFormatting.WHITE + "PÁLIDA ARCANA" + EnumChatFormatting.WHITE + ", sube a 60 HP y se vincula telepáticamente contigo."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "2. BARRERA DEFENSIVA Y PROTECCIÓN:"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "   • Si te atacan o disparan, invoca una " + EnumChatFormatting.LIGHT_PURPLE + "Barrera Arcana" + EnumChatFormatting.WHITE + " (85% reducción de daño, repulsión masiva de enemigos, Resistencia y Regeneración)."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "   • Si tu salud cae bajo el 35%, te envía sanación vital de emergencia."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "3. COMPAÑERO ETERNO:"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "   • " + EnumChatFormatting.AQUA + "/hobgoblin dismiss" + EnumChatFormatting.WHITE + ": Lo resguarda en el plano astral guardando su mochila y vida."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "   • " + EnumChatFormatting.AQUA + "/hobgoblin summon" + EnumChatFormatting.WHITE + ": Lo invoca de regreso a tu lado inmediatamente."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "4. AUTOMATIZACIÓN DE RITUALES (Carga de Cofres):"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "   • " + EnumChatFormatting.AQUA + "/hobgoblin rito [nombre]" + EnumChatFormatting.WHITE + ": El Sucesor busca los ingredientes en cofres cercanos, camina hacia el centro del círculo, los arroja y activa el ritual."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "   • Puedes especificar ritos directos: " + EnumChatFormatting.YELLOW + "tormenta, dia, noche, bendicion, proteccion" + EnumChatFormatting.WHITE + ", o dejar los ítems en un cofre junto al círculo."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "5. CONTROL DE LABORATORIO Y ALQUIMIA:"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "   • " + EnumChatFormatting.AQUA + "/hobgoblin lab" + EnumChatFormatting.WHITE + ": Modo continuo de laboratorio. Atiende hornos, destilerías y ollas automáticamente."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "   • " + EnumChatFormatting.AQUA + "/hobgoblin horno" + EnumChatFormatting.WHITE + ": Vacía carbón y humos (*Foul Fumes*), abastece combustible y frascos de arcilla."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "   • " + EnumChatFormatting.AQUA + "/hobgoblin destilar" + EnumChatFormatting.WHITE + ": Recolecta destilados, coloca frascos y carga recetas desde cofres."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "   • " + EnumChatFormatting.AQUA + "/hobgoblin chest" + EnumChatFormatting.WHITE + ": Almacena todos los productos en tus cofres."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "6. LIBROS DE RECETA (Automatización inteligente):"));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "   • " + EnumChatFormatting.AQUA + "/hobgoblin receta caldero Nightshade Wheat BoneMeal" + EnumChatFormatting.WHITE + ": Libro de receta para el caldero."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "   • " + EnumChatFormatting.AQUA + "/hobgoblin receta kettle MandrakeRoot BelladonnaFlower" + EnumChatFormatting.WHITE + ": Libro para el calderito (Floo)."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "   • " + EnumChatFormatting.AQUA + "/hobgoblin receta destilar BelladonnaFlower" + EnumChatFormatting.WHITE + ": Libro para destilería."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "   ★ Coloca el libro en un cofre cercano. El Sucesor lo leerá y usará los ingredientes automáticamente."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "   ★ El caldero acepta un libro escrito con título 'caldero', con ingredientes en sus páginas (uno por línea)."));
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "═════════════════════════════════════════════════════════"));
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
                goblin.sendWhisper(player, "¡He regresado a tu lado, Maestro! Siempre contigo.");
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
                    g.sendWhisper(player, "¡He acudido a tu llamado de inmediato, Maestro!");
                    return;
                }
            }

            player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "No tienes ningún Sucesor Arcano guardado ni cerca en este mundo."));
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
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "No tienes ningún Sucesor Arcano cerca para despedir."));
                return;
            }

            successor.sendWhisper(player, "Me resguardo en el plano etéreo, Maestro. Llámame con §b/hobgoblin llamar §ecuando me necesites.");
            com.emoniph.witchery.util.ParticleEffect.SMOKE.send(com.emoniph.witchery.util.SoundEffect.WITCHERY_RANDOM_POOF, successor, 1.0D, 1.5D, 30);
            com.emoniph.witchery.util.ParticleEffect.PORTAL.send(com.emoniph.witchery.util.SoundEffect.RANDOM_ORB, successor, 1.0D, 1.5D, 20);

            NBTTagCompound nbt = new NBTTagCompound();
            successor.writeToNBT(nbt);
            playerEx.setStoredSuccessorNBT(nbt);
            successor.setDead();
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + "Tu Sucesor Arcano se ha resguardado en el plano astral."));
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
                    g.sendWhisper(player, "§a¡Modo Laboratorio Mágico activado! Atendiendo " + ovens + " hornos, " + dists + " destilerías y " + kettles + " calderos/ollas. Reabasteceré frascos, leña y guardaré todo en tus cofres continuamente.");
                    return;
                }
            }
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "No hay ningún Sucesor Arcano cerca para asignar al laboratorio."));
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
                    g.sendWhisper(player, "He atendido " + ovens + " hornos de bruja. Productos y humos recolectados; frascos de arcilla y leña abastecidos.");
                    return;
                }
            }
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "No hay ningún Sucesor Arcano cerca para atender hornos."));
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
                successor.sendWhisper(player, "He guardado " + count + " objetos en los cofres cercanos, Maestro.");
            } else if (message.contains("caldero") || message.contains("pocion") || message.contains("transmuta")) {
                int c = successor.manageNearbyCauldrons();
                successor.transmuteInCauldron(player);
                successor.storeAllToChests();
            } else if (message.contains("altar")) {
                successor.successorRole = "altar";
                successor.sendWhisper(player, "Entendido Maestro, vigilaré y canalizaré la energía del altar.");
            } else if (message.contains("protege") || message.contains("escudo") || message.contains("defensa")) {
                successor.sendWhisper(player, "¡Mi barrera arcana está vigilante! Si recibes daño o hay enemigos, actuaré al instante.");
            } else if (message.contains("hola") || message.contains("saludos") || message.contains("buenos")) {
                successor.sendWhisper(player, "Saludos, Maestro. Es un honor estar a tu servicio.");
            } else if (message.contains("gracias")) {
                successor.sendWhisper(player, "Por ti daría mi existencia arcana, Maestro.");
            } else {
                successor.sendWhisper(player, "Entendido, Maestro: \"" + sb.toString() + "\". Tus deseos son órdenes.");
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
                    g.sendWhisper(player, "He organizado y depositado " + count + " objetos en los cofres cercanos, Maestro.");
                    return;
                }
            }
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "No hay ningún Sucesor Arcano cerca para guardar en cofres."));
            return;
        }

        // --- ALTAR ---
        if (subCommand.equals("altar")) {
            AxisAlignedBB searchBB = AxisAlignedBB.getBoundingBox(player.posX - 24.0D, player.posY - 12.0D, player.posZ - 24.0D, player.posX + 24.0D, player.posY + 12.0D, player.posZ + 24.0D);
            List<EntityGoblin> nearby = player.worldObj.getEntitiesWithinAABB(EntityGoblin.class, searchBB);
            for (EntityGoblin g : nearby) {
                if (g.isTamed && g.isSuccessor() && player.getCommandSenderName().equalsIgnoreCase(g.tamedOwnerName)) {
                    g.successorRole = "altar";
                    g.sendWhisper(player, "Vigilando el altar y canalizando energía mística, Maestro.");
                    return;
                }
            }
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "No hay ningún Sucesor Arcano cerca para vincular al altar."));
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
            if (goblin.isSuccessor()) {
                goblin.sendWhisper(player, "Permaneceré aquí en guardia, Maestro.");
            } else {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "El hobgoblin esperará aquí."));
            }
        } else if (subCommand.equals("follow") || subCommand.equals("seguir")) {
            goblin.isSitting = false;
            goblin.isFollowing = true;
            goblin.isWorking = false;
            if (goblin.isSuccessor()) {
                goblin.sendWhisper(player, "Siguiéndote de cerca, Maestro.");
            } else {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + "El hobgoblin te seguirá y protegerá."));
            }
        } else if (subCommand.equals("work") || subCommand.equals("trabajar") || subCommand.equals("curro")) {
            goblin.isSitting = false;
            goblin.isFollowing = false;
            goblin.isWorking = true;
            goblin.setAttackTarget(null);
            if (goblin.isSuccessor()) {
                goblin.sendWhisper(player, "Trabajando en el área y recolectando ingredientes para ti, Maestro.");
            } else {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "El hobgoblin ahora está trabajando en la zona (cosecha y minería)."));
            }
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
