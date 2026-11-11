package com.emoniph.witchery.common;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.blocks.BlockAltar;
import com.emoniph.witchery.entity.EntityCovenWitch;
import com.emoniph.witchery.familiar.Familiar;
import com.emoniph.witchery.infusion.Infusion;
import com.emoniph.witchery.infusion.infusions.symbols.SymbolEffect;
import com.emoniph.witchery.integration.ModHookMCA;
import com.emoniph.witchery.util.SoundEffect;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.List;

/**
 * WitchAdvisor: El Oráculo y Motor de Diagnóstico Místico.
 * Evalúa el progreso completo del jugador en Witchery (Altar, Espíritu,
 * Vampirismo, Licantropía, Aquelarre, Familiares, Bruja Suprema y relación con Aldeas MCA)
 * y genera susurros personalizados para guiar el siguiente nivel de poder.
 */
public class WitchAdvisor {

    public static void whisperProgression(EntityPlayer player, String witchName) {
        if (player == null || player.worldObj == null) {
            return;
        }

        if (witchName == null || witchName.trim().isEmpty()) {
            witchName = "Susurro del Aquelarre";
        }

        ExtendedPlayer playerEx = ExtendedPlayer.get(player);
        List<String> adviceList = new ArrayList<String>();

        // 1. Diagnóstico de Bruja Suprema y Las Siete Maravillas
        boolean isSupreme = SupremeWitchData.isPlayerSupreme(player);
        int masteryScore = SupremeWitchData.calculateWitcheryMasteryScore(player);
        int wondersCount = SupremeWitchData.getCompletedWondersCount(player);

        if (isSupreme) {
            adviceList.add("§6★ ¡Ostentas el manto de Bruja Suprema! §dTu poder resuena en todo el plano (" + masteryScore + " pts). Mantén tu liderazgo cumpliendo ritos y defendiendo el aquelarre.");
        } else {
            adviceList.add("§6[Las Siete Maravillas: " + wondersCount + "/7] §dPosees §6" + masteryScore + " pts§d de maestría. Para reclamar el trono de Bruja Suprema, supera las 7 pruebas arcanas (usa §e/suprema maravillas§d).");
            SupremeWitchData data = SupremeWitchData.get(player.worldObj);
            if (data != null && data.hasSupreme()) {
                adviceList.add("§dLa Bruja Suprema actual (" + data.getSupremeName() + ") domina el plano; supera su puntuación para arrebatarle la corona.");
            }
        }

        // 2. Diagnóstico de Nivel de Espíritu (Progresión Orgánica y Cap de Nivel 3)
        int spiritLevel = playerEx != null ? playerEx.getSpiritLevel() : 0;
        if (spiritLevel == 0) {
            adviceList.add("§5[Espíritu: Inerte] §dTu conexión con el Más Allá duerme. Elabora una Pócima del Mundo de los Sueños (Brew of Sleeping) y bebe cerca de un Atrapasueños para cruzar el velo hacia el Reino Onírico.");
        } else if (spiritLevel == 1 || spiritLevel == 2) {
            adviceList.add("§5[Espíritu: Nivel " + spiritLevel + "] §dPara despertar al Nivel 3 y dominar la forma de Orbe Espiritual ('ex subduo'), debes invocar y someter a un espíritu atormentado usando un Círculo de Protección y un Frasco de Arcilla.");
        } else if (spiritLevel == 3) {
            if (!isSupreme) {
                adviceList.add("§5[Espíritu: Nivel 3 - Límite Mortal] §dHas alcanzado la cima espiritual de una bruja corriente (Orbe: 'ex subduo'). Los Niveles 4 al 10 (Danza de Espíritus, Posesión, Paso Etéreo y Proyección Astral) están reservados para la Bruja Suprema. ¡Consuma las Siete Maravillas (/suprema maravillas) para romper este sello!");
            } else {
                adviceList.add("§5[Espíritu: Nivel 3 - Ascendiendo] §a¡Como Bruja Suprema, puedes quebrar el velo mortal! Derrota a 3 espíritus atormentados en el Mundo de los Sueños para despertar el Nivel 4 (Danza de Espíritus: /spirit summon).");
            }
        } else if (spiritLevel == 4 || spiritLevel == 5) {
            adviceList.add("§5[Espíritu: Nivel " + spiritLevel + "] §dInvoca a tus espíritus en combate con '/spirit summon' para doblegar el Nivel 6 (Posesión de Criaturas con clic derecho en forma espiritual).");
        } else if (spiritLevel == 6 || spiritLevel == 7) {
            adviceList.add("§5[Espíritu: Nivel " + spiritLevel + "] §dPosee criaturas o purga espectros para alcanzar el Nivel 8 (Relicario del Vacío en cofres de ender). Para avanzar, roba un alma con 'RiteSoulThief' o destruye un Demonio.");
        } else if (spiritLevel == 8 || spiritLevel == 9) {
            adviceList.add("§5[Espíritu: Nivel " + spiritLevel + "] §dConsagra un Horrocrux o derrota a Death o al Huntsman para despertar el Nivel 10 (Proyección Astral definitiva: '/spirit astral' o '/chant ex corpus').");
        } else {
            adviceList.add("§5[Espíritu: Nivel 10 - Avatar Astral] §a¡Has alcanzado la cúspide espiritual! Puedes separar tu alma de tu cuerpo físico a voluntad con '/spirit astral'.");
        }

        // 3. Diagnóstico de Aquelarre (Coven) y Tareas Activas
        int covenSize = EntityCovenWitch.getCovenSize(player);
        if (covenSize == 0) {
            adviceList.add("§9[Aquelarre: Solitaria] §dNo tienes ninguna bruja aliada. Adéntrate en los pantanos y bosques profundos, habla con brujas solitarias y cumple su pacto para unirlas a tu círculo.");
        } else if (covenSize < 6) {
            adviceList.add("§9[Aquelarre: " + covenSize + "/6 Hermanas] §dTu círculo cuenta con " + covenSize + " brujas. Recluta hasta 6 hermanas para desatar los Grandes Ritos Celestiales y anular los fallos de tu varita.");
        } else {
            adviceList.add("§9[Aquelarre: Círculo Completo] §a¡Tu aquelarre de 6 hermanas está completo! Invócalas en tus círculos de tiza para multiplicar por diez el poder de tus ritos y hacer infalible tu varita.");
        }

        String activeTaskId = CovenTaskManager.getActiveTaskId(player);
        if (activeTaskId != null && !activeTaskId.isEmpty()) {
            CovenTask task = CovenTaskManager.getTask(activeTaskId);
            if (task != null) {
                boolean ready = task.canComplete(player);
                adviceList.add("§e[Tarea del Aquelarre: «" + task.getTitle() + "»] " + (ready ? "§a¡Tienes los tributos listos! Usa '/coven task' para reclamar tu Alijo Místico." : "§7Falta reunir: " + task.getDescription()));
            }
        } else {
            adviceList.add("§e[Aquelarre: Tareas Pendientes] §dEl aquelarre tiene misiones listas. Usa '/coven task' para solicitar una y ganar Alijos Místicos con flores raras, tarros de boticario y esencias.");
        }

        // 4. Diagnóstico de Familiar Mejorado
        String familiar = Familiar.getFamiliarName(player);
        if (familiar == null || familiar.isEmpty()) {
            adviceList.add("§2[Familiar: Ausente] §dNo tienes compañero animal. Usa '/pet bind' mirando a un gato, sapo o búho domesticado para sellar el pacto y ganar bonos en la Rama Mística.");
        } else {
            String famType = "";
            if (familiar.toLowerCase().contains("cat") || familiar.toLowerCase().contains("gato") || familiar.toLowerCase().contains("ocelot")) {
                famType = "Gato: -25% coste en maldiciones, -50% fallos con varita y habilidad salvavidas 'Nueve Vidas'";
            } else if (familiar.toLowerCase().contains("toad") || familiar.toLowerCase().contains("sapo")) {
                famType = "Sapo: Envenena a atacantes y 25% de lanzar cualquier hechizo de la varita gratis";
            } else if (familiar.toLowerCase().contains("owl") || familiar.toLowerCase().contains("buho") || familiar.toLowerCase().contains("búho")) {
                famType = "Búho: Ciega a atacantes, -50% daño por caída y -25% cooldowns y hechizos de viento/telequinesis";
            } else {
                famType = familiar + ": Compañero espiritual leal (guárdalo con /pet familiar)";
            }
            adviceList.add("§2[Familiar Vinculado] §a" + famType + " (Guárdalo o invócalo con '/pet familiar').");
        }

        // 5. Diagnóstico de Simbología y Hechizos
        int totalSpells = 0, masteredSpells = 0;
        for (Object obj : com.emoniph.witchery.infusion.infusions.symbols.EffectRegistry.instance().getEffects()) {
            SymbolEffect eff = (SymbolEffect) obj;
            if (eff != null && eff.getDefaultStrokes() != null && eff.isVisible(player)) {
                totalSpells++;
                if (SymbolEffect.isSpellMastered(player, eff)) masteredSpells++;
            }
        }
        if (masteredSpells < totalSpells) {
            adviceList.add("§b[Simbología: " + masteredSpells + "/" + totalSpells + " dominados] §dConsulta el libro «Brujería: Simbología» o '/spells' para ver los trazos. Lanza cada hechizo para dominarlo (+10 Maestría).");
        } else {
            adviceList.add("§b[Simbología Completa] §a¡Has dominado todos los hechizos de la Rama Mística! (+10 Maestría cada uno).");
        }

        // 6. Hobgoblins y Batería de Maná
        adviceList.add("§6[Hobgoblins: Obreros y Maná] §dOfréceles esmeraldas para domesticarlos: te otorgarán +40 de energía máxima de infusión cada uno, regeneración de maná y trabajo de cosecha/minería con '/hobgoblin work'.");

        // 7. Diagnóstico de Vampirismo y Licantropía
        int vampLevel = playerEx != null ? playerEx.getVampireLevel() : 0;
        int wolfLevel = playerEx != null ? playerEx.getWerewolfLevel() : 0;

        if (vampLevel > 0 && vampLevel < 10) {
            adviceList.add("§c[Vampiro: Rango " + vampLevel + "] §dLilith aguarda en el Cáliz. Alimenta tu sed bebiendo sangre nocturna de aldeanos dormidos o realiza los tributos oscuros en el ataúd para subir al Rango " + (vampLevel + 1) + ".");
        }
        if (wolfLevel > 0 && wolfLevel < 10) {
            adviceList.add("§6[Licántropo: Rango " + wolfLevel + "] §dLa bestia reclama caza. Haz sonar el Cuerno de Caza durante la Luna Llena y devora carne viva para despertar el Rango " + (wolfLevel + 1) + ".");
        }

        // 8. Diagnóstico de Aldeas y Aldeanos MCA
        if (ModHookMCA.isMCAPresent()) {
            if (ModHookMCA.isTerrorizingVillage(player)) {
                adviceList.add("§4[Aldea MCA: Alerta de Caza] §cLos aldeanos te temen o han visto tus actos oscuros. Los Cazadores de Brujas (Witch Hunters) podrían organizar una purga pronto. Caliéntate con pociones de paz o cúralos.");
            } else {
                adviceList.add("§a[Aldea MCA: Relación Estable] §dPuedes usar el aquelarre para comerciar brebajes, sanar aldeanos enfermos con 'Odour of Purity' o reclamar tributos de amor.");
            }
        }

        // Enviar encabezado místico con sonido
        SoundEffect.WITCHERY_RANDOM_HYPNOSIS.playAtPlayer(player.worldObj, player, 0.6F, 1.2F);
        player.addChatMessage(new ChatComponentText("§5§m-----------------------------------------------------"));
        player.addChatMessage(new ChatComponentText("§5§l🔮 [Voz Mística: " + witchName + "]"));
        player.addChatMessage(new ChatComponentText("§7§o\"Escucha los susurros del velo... esto es lo que tu alma necesita para ascender:\""));

        for (String line : adviceList) {
            player.addChatMessage(new ChatComponentText("  §7• " + line));
        }

        player.addChatMessage(new ChatComponentText("§5§m-----------------------------------------------------"));
    }
}
