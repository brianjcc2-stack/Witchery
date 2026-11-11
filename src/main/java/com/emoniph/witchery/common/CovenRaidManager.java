package com.emoniph.witchery.common;

import com.emoniph.witchery.entity.EntityRivalFamiliar;
import com.emoniph.witchery.entity.EntityRivalWitch;
import com.emoniph.witchery.util.ParticleEffect;
import com.emoniph.witchery.util.SoundEffect;
import net.minecraft.block.Block;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.util.List;
import java.util.Random;

/**
 * CovenRaidManager: Orquestador del evento de asalto de Aquelarres Rivales.
 * Provoca emboscadas dinámicas de brujas enemigas (Herbolarias, Alquimistas, Nigromantes y la Matriarca)
 * acompañadas de familiares de combate, especialmente dirigidas a la Bruja Suprema o practicantes de alto nivel.
 */
public class CovenRaidManager {

    private static final String RAID_COOLDOWN_KEY = "WITCLastRaidTime";
    private static final long RAID_MIN_COOLDOWN_TICKS = 48000L; // 2 días de Minecraft
    private static long lastGlobalCheckTime = 0L;

    /**
     * Llamado periódicamente desde ServerTickEvents.
     */
    public static void checkAndTriggerRaids(WorldServer world) {
        if (world == null || world.isRemote) return;

        long totalTime = world.getTotalWorldTime();
        if (totalTime - lastGlobalCheckTime < 1200L) { // Cada 60 segundos
            return;
        }
        lastGlobalCheckTime = totalTime;

        MinecraftServer server = MinecraftServer.getServer();
        if (server == null || server.getConfigurationManager() == null) return;

        List players = server.getConfigurationManager().playerEntityList;
        if (players == null || players.isEmpty()) return;

        // Sólo ocurre durante la noche en la superficie del Overworld
        long timeOfDay = world.getWorldTime() % 24000L;
        boolean isNight = timeOfDay >= 13000L && timeOfDay <= 23000L;
        if (!isNight) return;

        int moonPhase = world.provider.getMoonPhase(world.getWorldTime());
        boolean isPowerfulMoon = (moonPhase == 0 || moonPhase == 4); // Luna Llena o Luna Nueva

        for (Object obj : players) {
            if (!(obj instanceof EntityPlayerMP)) continue;
            EntityPlayerMP player = (EntityPlayerMP) obj;

            if (player.capabilities.isCreativeMode) continue;
            if (player.dimension != 0) continue; // Sólo en el Overworld

            // Condición: Es la Bruja Suprema o tiene alta maestría en Witchery
            boolean isSupreme = SupremeWitchData.isPlayerSupreme(player);
            int score = SupremeWitchData.calculateWitcheryMasteryScore(player);

            if (!isSupreme && score < 160) {
                continue;
            }

            // Comprobar si puede ver el cielo (está en la superficie o base exterior)
            int pX = MathHelper.floor_double(player.posX);
            int pY = MathHelper.floor_double(player.posY);
            int pZ = MathHelper.floor_double(player.posZ);
            if (!world.canBlockSeeTheSky(pX, pY, pZ)) {
                continue;
            }

            // Comprobar cooldown por jugador
            long lastRaid = player.getEntityData().getLong(RAID_COOLDOWN_KEY);
            if (totalTime - lastRaid < RAID_MIN_COOLDOWN_TICKS) {
                continue;
            }

            // Probabilidad: Más alta para la Bruja Suprema y durante Luna Llena/Nueva
            float chance = isSupreme ? (isPowerfulMoon ? 0.35F : 0.15F) : (isPowerfulMoon ? 0.15F : 0.05F);
            if (world.rand.nextFloat() < chance) {
                triggerRaid(player, false);
                break; // Máximo 1 raid disparada por tick global
            }
        }
    }

    /**
     * Inicia una incursión de aquelarre rival contra el jugador especificado.
     */
    public static boolean triggerRaid(EntityPlayer player, boolean isForced) {
        if (player == null || player.worldObj.isRemote) return false;

        World world = player.worldObj;
        Random rand = world.rand;

        // Buscar una posición adecuada en tierra a 14-20 bloques
        double angle = rand.nextDouble() * Math.PI * 2.0D;
        double dist = 14.0D + rand.nextDouble() * 6.0D;
        int spawnX = MathHelper.floor_double(player.posX + Math.cos(angle) * dist);
        int spawnZ = MathHelper.floor_double(player.posZ + Math.sin(angle) * dist);
        int spawnY = world.getTopSolidOrLiquidBlock(spawnX, spawnZ);

        if (spawnY <= 0 || Math.abs(spawnY - player.posY) > 8.0D) {
            spawnY = MathHelper.floor_double(player.posY);
        }

        // Efectos atmosféricos del asalto
        world.addWeatherEffect(new EntityLightningBolt(world, spawnX, spawnY, spawnZ));
        ParticleEffect.PORTAL.send(SoundEffect.WITCHERY_RANDOM_THEYCOME, world, spawnX + 0.5D, spawnY + 1.0D, spawnZ + 0.5D, 4.0D, 4.0D, 80);

        // 1. Spawner de la Matriarca (Líder del Aquelarre Rival / Boss)
        EntityRivalWitch matriarch = new EntityRivalWitch(world, EntityRivalWitch.ARCHETYPE_MATRIARCH);
        matriarch.setLocationAndAngles(spawnX + 0.5D, spawnY, spawnZ + 0.5D, rand.nextFloat() * 360.0F, 0.0F);
        matriarch.setAttackTarget(player);
        world.spawnEntityInWorld(matriarch);

        // 2. Acólita Herbolaria
        EntityRivalWitch herbologist = new EntityRivalWitch(world, EntityRivalWitch.ARCHETYPE_HERBOLOGIST);
        herbologist.setLocationAndAngles(spawnX + 2.0D, spawnY, spawnZ - 1.0D, rand.nextFloat() * 360.0F, 0.0F);
        herbologist.setAttackTarget(player);
        world.spawnEntityInWorld(herbologist);

        // 3. Acólita Alquimista
        EntityRivalWitch alchemist = new EntityRivalWitch(world, EntityRivalWitch.ARCHETYPE_ALCHEMIST);
        alchemist.setLocationAndAngles(spawnX - 2.0D, spawnY, spawnZ + 1.0D, rand.nextFloat() * 360.0F, 0.0F);
        alchemist.setAttackTarget(player);
        world.spawnEntityInWorld(alchemist);

        // 4. Familiares de Combate Hostiles
        EntityRivalFamiliar toad = new EntityRivalFamiliar(world, EntityRivalFamiliar.TYPE_TOAD);
        toad.setLocationAndAngles(spawnX + 1.5D, spawnY, spawnZ + 1.5D, 0.0F, 0.0F);
        toad.setAttackTarget(player);
        world.spawnEntityInWorld(toad);

        EntityRivalFamiliar owl = new EntityRivalFamiliar(world, EntityRivalFamiliar.TYPE_OWL);
        owl.setLocationAndAngles(spawnX, spawnY + 2.5D, spawnZ, 0.0F, 0.0F);
        owl.setAttackTarget(player);
        world.spawnEntityInWorld(owl);

        EntityRivalFamiliar cat = new EntityRivalFamiliar(world, EntityRivalFamiliar.TYPE_CAT);
        cat.setLocationAndAngles(spawnX - 1.5D, spawnY, spawnZ - 1.5D, 0.0F, 0.0F);
        cat.setAttackTarget(player);
        world.spawnEntityInWorld(cat);

        // Anuncios místicos en el chat
        String matriarchName = matriarch.getMatriarchName();
        player.addChatMessage(new ChatComponentText(
                EnumChatFormatting.DARK_PURPLE + "" + EnumChatFormatting.BOLD + "[Aquelarre Sombrío] " +
                EnumChatFormatting.RED + "¡Las ramas del bosque susurran traición! " +
                EnumChatFormatting.LIGHT_PURPLE + "La " + EnumChatFormatting.GOLD + matriarchName +
                EnumChatFormatting.LIGHT_PURPLE + " y sus acólitas han descendido para destronarte."));

        MinecraftServer server = MinecraftServer.getServer();
        if (server != null && server.getConfigurationManager() != null) {
            server.getConfigurationManager().sendChatMsg(new ChatComponentText(
                    EnumChatFormatting.DARK_PURPLE + "" + EnumChatFormatting.BOLD + "⚡ [Witchery] " +
                    EnumChatFormatting.LIGHT_PURPLE + "¡Un aquelarre hostil liderado por la " +
                    EnumChatFormatting.GOLD + matriarchName +
                    EnumChatFormatting.LIGHT_PURPLE + " ha desafiado a " + EnumChatFormatting.YELLOW + player.getCommandSenderName() + "!"));
        }

        player.getEntityData().setLong(RAID_COOLDOWN_KEY, world.getTotalWorldTime());
        return true;
    }
}
