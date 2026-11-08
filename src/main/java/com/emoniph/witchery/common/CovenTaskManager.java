package com.emoniph.witchery.common;

import com.emoniph.witchery.Witchery;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;

import java.util.*;

/**
 * CovenTaskManager: Catálogo expansivo de misiones del aquelarre.
 * Alberga 70 misiones hand-crafted divididas en 8 categorías temáticas completas,
 * conectadas con el mundo de Witchery y las aldeas de Minecraft Comes Alive (MCA).
 */
public class CovenTaskManager {

    private static final Map<String, CovenTask> TASK_REGISTRY = new HashMap<String, CovenTask>();
    private static final List<String> TASK_KEYS = new ArrayList<String>();

    static {
        registerAllTasks();
    }

    private static void register(CovenTask task) {
        TASK_REGISTRY.put(task.getId(), task);
        TASK_KEYS.add(task.getId());
    }

    public static CovenTask getTask(String id) {
        return TASK_REGISTRY.get(id);
    }

    public static int getTotalTaskCount() {
        return TASK_KEYS.size();
    }

    private static void registerAllTasks() {
        // =========================================================================
        // CATEGORÍA 1: BOTÁNICA ARCANA & RECOLECCIÓN OCULTA (10 Tareas)
        // =========================================================================
        register(new CovenTask("botany_belladonna", CovenTask.TaskCategory.HARVEST,
                "Flores de la Bella Dama",
                "Cosecha 16 flores de Belladona maduras bajo la luz de las estrellas.",
                Witchery.Items.GENERIC.itemBelladonnaFlower.createStack(16), 16, 25, "Brote Fértil"));

        register(new CovenTask("botany_mandrake", CovenTask.TaskCategory.HARVEST,
                "El Grito de la Tierra",
                "Desentierra 10 Raíces de Mandrágora viva para nuestros elixires.",
                Witchery.Items.GENERIC.itemMandrakeRoot.createStack(10), 10, 25, "Resonancia Terrenal"));

        register(new CovenTask("botany_artichoke", CovenTask.TaskCategory.HARVEST,
                "Lágrimas de la Laguna",
                "Recolecta 8 Alcachofas de Agua (Water Artichoke) en humedales.",
                Witchery.Items.GENERIC.itemArtichoke.createStack(8), 8, 20, "Aliento Acuático"));

        register(new CovenTask("botany_wolfsbane", CovenTask.TaskCategory.HARVEST,
                "Acónito de la Bestia",
                "Cosecha 6 flores de Acónito (Wolfsbane) para repeler la licantropía salvaje.",
                Witchery.Items.GENERIC.itemWolfsbane.createStack(6), 6, 30, "Manto Antilupino"));

        register(new CovenTask("botany_rowan", CovenTask.TaskCategory.HARVEST,
                "Frutos del Fresno Sagrado",
                "Reúne 16 Bayas de Serbal (Rowan Berries) de los árboles mágicos.",
                Witchery.Items.GENERIC.itemRowanBerries.createStack(16), 16, 20, "Vigor del Bosque"));

        register(new CovenTask("botany_wormwood", CovenTask.TaskCategory.HARVEST,
                "Amargura de Ajenjo",
                "Cosecha 8 hojas de Ajenjo (Wormwood) cultivadas en suelo consagrado.",
                Witchery.Items.GENERIC.itemWormwood.createStack(8), 8, 25, "Esencia Amarga"));

        register(new CovenTask("botany_woodash", CovenTask.TaskCategory.HARVEST,
                "Cenizas del Fuego Sagrado",
                "Quema brotes en el horno y entréganos 16 Cenizas de Madera (Wood Ash).",
                Witchery.Items.GENERIC.itemAshWood.createStack(16), 16, 20, "Rescoldo Protector"));

        register(new CovenTask("botany_gypsum", CovenTask.TaskCategory.HARVEST,
                "Polvo de Yeso Alquímico",
                "Procesa 12 unidades de Yeso (Gypsum) para preparar bases de tiza ritual.",
                Witchery.Items.GENERIC.itemGypsum.createStack(12), 12, 20, "Solidez Arcana"));

        register(new CovenTask("botany_quicklime", CovenTask.TaskCategory.HARVEST,
                "Cal Viva Purificadora",
                "Fabrica 12 unidades de Cal Viva (Quicklime) para mezclas alquímicas.",
                Witchery.Items.GENERIC.itemQuicklime.createStack(12), 12, 20, "Fuego Blanco"));

        register(new CovenTask("botany_mutandis", CovenTask.TaskCategory.HARVEST,
                "El Catalizador Mutador",
                "Elabora 8 dosis de Mutandis para alterar la flora del aquelarre.",
                Witchery.Items.GENERIC.itemMutandis.createStack(8), 8, 30, "Mutación Primitiva"));

        // =========================================================================
        // CATEGORÍA 2: ALQUIMIA DE CALDERO & BREBAJES PROHIBIDOS (10 Tareas)
        // =========================================================================
        register(new CovenTask("brew_grotesque", CovenTask.TaskCategory.BREWING,
                "Brebaje de lo Grotesco",
                "Cocina en el caldero 4 pócimas de Cerveza Grotesca.",
                Witchery.Items.GENERIC.itemBrewGrotesque.createStack(4), 4, 35, "Voz de las Sombras"));

        register(new CovenTask("brew_love", CovenTask.TaskCategory.BREWING,
                "Elixir de Obsesión",
                "Elabora 3 Brebajes de Amor (Brew of Love) para hechizar a los aldeanos de MCA.",
                Witchery.Items.GENERIC.itemBrewOfLove.createStack(3), 3, 35, "Seducción Astral"));

        register(new CovenTask("brew_wasting", CovenTask.TaskCategory.BREWING,
                "Veneno de Consunción",
                "Prepara 3 Brebajes de Desgaste (Brew of Wasting) para marchitar invasores.",
                Witchery.Items.GENERIC.itemBrewOfWasting.createStack(3), 3, 30, "Manto Venenoso"));

        register(new CovenTask("brew_bats", CovenTask.TaskCategory.BREWING,
                "Torbellino de Quirópteros",
                "Mezcla en el caldero 3 Brebajes de Murciélagos (Brew of Bats).",
                Witchery.Items.GENERIC.itemBrewOfBats.createStack(3), 3, 30, "Enjambre Nocturno"));

        register(new CovenTask("brew_sleeping", CovenTask.TaskCategory.BREWING,
                "Somnífero del Letargo",
                "Elabora 3 Brebajes del Sueño (Brew of Sleeping) para el trance del velo.",
                Witchery.Items.GENERIC.itemBrewOfSleeping.createStack(3), 3, 35, "Velo Onírico"));

        register(new CovenTask("brew_depths", CovenTask.TaskCategory.BREWING,
                "Brebaje de las Fosas Abisales",
                "Cocina 2 Brebajes de las Profundidades (Brew of the Depths).",
                Witchery.Items.GENERIC.itemBrewOfTheDepths.createStack(2), 2, 40, "Señor de las Olas"));

        register(new CovenTask("brew_sprouting", CovenTask.TaskCategory.BREWING,
                "Despertar de Raíces",
                "Prepara 4 Brebajes de Crecimiento (Brew of Sprouting) para los huertos del aquelarre.",
                Witchery.Items.GENERIC.itemBrewOfSprouting.createStack(4), 4, 25, "Abundancia Telúrica"));

        register(new CovenTask("brew_vines", CovenTask.TaskCategory.BREWING,
                "Lianas Sofocantes",
                "Elabora 3 Brebajes de Lianas (Brew of Vines) para fortificar las torres de bruja.",
                Witchery.Items.GENERIC.itemBrewOfVines.createStack(3), 3, 25, "Atadura de Hiedra"));

        register(new CovenTask("brew_ice", CovenTask.TaskCategory.BREWING,
                "Escarcha Sepulcral",
                "Fabrica 3 Brebajes de Hielo (Brew of Ice) para congelar el fuego del averno.",
                Witchery.Items.GENERIC.itemBrewOfIce.createStack(3), 3, 30, "Aliento Ártico"));

        register(new CovenTask("brew_ink", CovenTask.TaskCategory.BREWING,
                "Ceguera de Tinta",
                "Prepara 3 Brebajes de Tinta (Brew of Ink) para cegar a los Witch Hunters.",
                Witchery.Items.GENERIC.itemBrewOfInk.createStack(3), 3, 25, "Noche Líquida"));

        // =========================================================================
        // CATEGORÍA 3: DESTILACIÓN DEL ALAMBIQUE & VAPORES MÍSTICOS (8 Tareas)
        // =========================================================================
        register(new CovenTask("distill_tear", CovenTask.TaskCategory.HARVEST,
                "Lágrimas de la Diosa",
                "Destila en tu alambique 3 Lágrimas de la Diosa (Tear of the Goddess).",
                Witchery.Items.GENERIC.itemTearOfTheGoddess.createStack(3), 3, 45, "Pureza de la Diosa"));

        register(new CovenTask("distill_fumes", CovenTask.TaskCategory.HARVEST,
                "Humos Nefastos Condensados",
                "Atrapa en jarras de arcilla 6 Humos Fétidos (Foul Fumes) durante la destilación.",
                Witchery.Items.GENERIC.itemFoulFume.createStack(6), 6, 25, "Bruma Tóxica"));

        register(new CovenTask("distill_breath", CovenTask.TaskCategory.HARVEST,
                "Aliento de la Gran Diosa",
                "Destila 4 frascos de Aliento de la Diosa (Breath of the Goddess).",
                Witchery.Items.GENERIC.itemBreathOfTheGoddess.createStack(4), 4, 35, "Bendición Celeste"));

        register(new CovenTask("distill_exhale", CovenTask.TaskCategory.HARVEST,
                "Exhalación del Dios Astado",
                "Extrae 4 dosis de Exhalación del Astado (Exhale of the Horned One).",
                Witchery.Items.GENERIC.itemExhaleOfTheHornedOne.createStack(4), 4, 35, "Furia del Astado"));

        register(new CovenTask("distill_whiff", CovenTask.TaskCategory.HARVEST,
                "Vaho de Magia Pura",
                "Destila 4 viales de Vaho Mágico (Whiff of Magic).",
                Witchery.Items.GENERIC.itemWhiffOfMagic.createStack(4), 4, 30, "Resonancia Arcana"));

        register(new CovenTask("distill_diamond", CovenTask.TaskCategory.HARVEST,
                "Vapor de Diamante",
                "Vaporiza diamantes en el alambique y obtén 2 Vapores de Diamante (Diamond Vapour).",
                Witchery.Items.GENERIC.itemDiamondVapour.createStack(2), 2, 50, "Coraza de Diamante"));

        register(new CovenTask("distill_refined_evil", CovenTask.TaskCategory.HARVEST,
                "Maldad Refinada",
                "Destila lágrimas y corazones oscuros para obtener 2 esencias de Maldad Refinada.",
                Witchery.Items.GENERIC.itemRefinedEvil.createStack(2), 2, 45, "Fuerza Sombría"));

        register(new CovenTask("distill_drop_luck", CovenTask.TaskCategory.HARVEST,
                "Gotas de Fortuna",
                "Destila tréboles y mandrágora para crear 2 Gotas de Suerte (Drop of Luck).",
                Witchery.Items.GENERIC.itemDropOfLuck.createStack(2), 2, 40, "Azar Favorable"));

        // =========================================================================
        // CATEGORÍA 4: CAZA, MONSTRUOS & SOMBRAS (10 Tareas)
        // =========================================================================
        register(new CovenTask("hunt_demon", CovenTask.TaskCategory.HUNTING,
                "El Latido del Averno",
                "Caza a un Demonio en el Nether o invócalo para traer su Corazón palpitante.",
                Witchery.Items.GENERIC.itemDemonHeart.createStack(1), 1, 50, "Fuego Infernal"));

        register(new CovenTask("hunt_subdued_spirit", CovenTask.TaskCategory.HUNTING,
                "Espíritu Doblegado",
                "Atrapa un espíritu en pena y entréganos 1 Espíritu Sometido (Subdued Spirit).",
                Witchery.Items.GENERIC.itemSubduedSpirit.createStack(1), 1, 45, "Caminar del Velo"));

        register(new CovenTask("hunt_bones", CovenTask.TaskCategory.HUNTING,
                "Huesos de los Caídos",
                "Recolecta 32 huesos de esqueletos para nutrir los fuegos del altar.",
                new ItemStack(Items.bone, 32), 32, 20, "Vigor Funerario"));

        register(new CovenTask("hunt_ghast_tear", CovenTask.TaskCategory.HUNTING,
                "Llanto del Nether",
                "Caza espectros en el averno y entrega 3 Lágrimas de Ghast.",
                new ItemStack(Items.ghast_tear, 3), 3, 40, "Lamento Ígneo"));

        register(new CovenTask("hunt_ender_pearl", CovenTask.TaskCategory.HUNTING,
                "Ojos del Vacío",
                "Derrota criaturas del fin y entréganos 4 Perlas de Ender.",
                new ItemStack(Items.ender_pearl, 4), 4, 30, "Salto Dimensional"));

        register(new CovenTask("hunt_creeper_heart", CovenTask.TaskCategory.HUNTING,
                "Corazón Inestable",
                "Elimina un Creeper con la Athame para extraer 1 Corazón de Creeper.",
                Witchery.Items.GENERIC.itemCreeperHeart.createStack(1), 1, 35, "Pulso Explosivo"));

        register(new CovenTask("hunt_bat_wool", CovenTask.TaskCategory.HUNTING,
                "Pelaje de Quiróptero",
                "Trasquila o caza murciélagos en cavernas para obtener 4 Pelajes de Murciélago.",
                Witchery.Items.GENERIC.itemBatWool.createStack(4), 4, 25, "Ecolocalización"));

        register(new CovenTask("hunt_dog_tongue", CovenTask.TaskCategory.HUNTING,
                "Lengua de Cánido Salvaje",
                "Consigue 3 Lenguas de Perro/Lobo para pociones de rastreo.",
                Witchery.Items.GENERIC.itemDogTongue.createStack(3), 3, 25, "Olfato Salvaje"));

        register(new CovenTask("hunt_frog_toe", CovenTask.TaskCategory.HUNTING,
                "Dedo de Batracio de Ciénaga",
                "Reúne 4 Dedos de Rana en los pantanos profundos.",
                Witchery.Items.GENERIC.itemToeOfFrog.createStack(4), 4, 25, "Salto Anfibio"));

        register(new CovenTask("hunt_spectral_dust", CovenTask.TaskCategory.HUNTING,
                "Polvo Espectral",
                "Derrota fantasmas y espectros para recolectar 4 Polvos Espectrales.",
                Witchery.Items.GENERIC.itemSpectralDust.createStack(4), 4, 35, "Aura Etérea"));

        // =========================================================================
        // CATEGORÍA 5: MUÑECOS VUDÚ, TAGLOCKS & FETICHES (8 Tareas)
        // =========================================================================
        register(new CovenTask("poppet_base", CovenTask.TaskCategory.RITUAL,
                "El Armazón del Destino",
                "Fabrica 3 Muñecos Vudú sin atar (Poppets) con arcilla, huesos e hilos.",
                new ItemStack(Witchery.Items.POPPET, 3, 0), 3, 30, "Trama Vital"));

        register(new CovenTask("poppet_death", CovenTask.TaskCategory.RITUAL,
                "Fetiche contra la Muerte",
                "Consagra 1 Muñeco de Protección contra la Muerte (Death Protection Poppet).",
                new ItemStack(Witchery.Items.POPPET, 1, 1), 1, 55, "Segunda Vida"));

        register(new CovenTask("poppet_fire", CovenTask.TaskCategory.RITUAL,
                "Inmunidad a las Llamas",
                "Elabora 1 Muñeco de Protección contra el Fuego (Fire Protection Poppet).",
                new ItemStack(Witchery.Items.POPPET, 1, 2), 1, 40, "Piel de Salamandra"));

        register(new CovenTask("poppet_water", CovenTask.TaskCategory.RITUAL,
                "Pulmón de Agua",
                "Crea 1 Muñeco de Protección contra el Ahogamiento (Water Protection Poppet).",
                new ItemStack(Witchery.Items.POPPET, 1, 3), 1, 35, "Branquia Mística"));

        register(new CovenTask("poppet_earth", CovenTask.TaskCategory.RITUAL,
                "Amortiguador Terrenal",
                "Consagra 1 Muñeco contra Caídas (Earth Protection Poppet).",
                new ItemStack(Witchery.Items.POPPET, 1, 6), 1, 35, "Caída de Pluma"));

        register(new CovenTask("poppet_hunger", CovenTask.TaskCategory.RITUAL,
                "Fetiche de Saciedad",
                "Elabora 1 Muñeco contra la Inanición (Hunger Protection Poppet).",
                new ItemStack(Witchery.Items.POPPET, 1, 4), 1, 35, "Festín Perpetuo"));

        register(new CovenTask("poppet_voodoo", CovenTask.TaskCategory.RITUAL,
                "El Sufrimiento Lejano",
                "Fabrica 1 Muñeco Vudú Ofensivo (Voodoo Poppet) para canalizar tormento.",
                new ItemStack(Witchery.Items.POPPET, 1, 8), 1, 45, "Agonía Reflejada"));

        register(new CovenTask("poppet_taglock", CovenTask.TaskCategory.RITUAL,
                "Kits de Muestra Biológica",
                "Prepara 6 Kits de Taglock vacíos para recolectar cabello y sangre.",
                new ItemStack(Witchery.Items.TAGLOCK_KIT, 6), 6, 25, "Rastro Genético"));

        // =========================================================================
        // CATEGORÍA 6: CÍRCULOS RITUALES & RELIQUIAS (8 Tareas)
        // =========================================================================
        register(new CovenTask("rite_crystalball", CovenTask.TaskCategory.RITUAL,
                "El Ojo que Todo lo Ve",
                "Invoca o manufactura una Bola de Cristal (Crystal Ball) para el altar.",
                new ItemStack(Witchery.Blocks.CRYSTAL_BALL), 1, 40, "Clarividencia Cósmica"));

        register(new CovenTask("rite_necrostone", CovenTask.TaskCategory.RITUAL,
                "Piedra del Nigromante",
                "Consagra en un rito de círculo 1 Piedra Nigromántica (Necro Stone).",
                Witchery.Items.GENERIC.itemNecroStone.createStack(1), 1, 45, "Aura Cadavérica"));

        register(new CovenTask("rite_chalk_white", CovenTask.TaskCategory.RITUAL,
                "Tiza Ritual Pura",
                "Fabrica 2 barras de Tiza Ritual blanca para trazar círculos mágicos.",
                new ItemStack(Witchery.Items.CHALK_RITUAL, 2), 2, 25, "Trazo Sagrado"));

        register(new CovenTask("rite_chalk_otherwhere", CovenTask.TaskCategory.RITUAL,
                "Tiza Transdimensional",
                "Elabora 2 barras de Tiza del Más Allá (Otherwhere Chalk) moradas.",
                new ItemStack(Witchery.Items.CHALK_OTHERWHERE, 2), 2, 35, "Trazo Interdimensional"));

        register(new CovenTask("rite_chalk_infernal", CovenTask.TaskCategory.RITUAL,
                "Tiza Ígnea del Nether",
                "Fabrica 2 barras de Tiza Infernal roja cocinadas con fuego abisal.",
                new ItemStack(Witchery.Items.CHALK_INFERNAL, 2), 2, 35, "Trazo del Averno"));

        register(new CovenTask("rite_chalk_golden", CovenTask.TaskCategory.RITUAL,
                "Tiza Dorada del Altar",
                "Forja 1 barra de Tiza Dorada (Golden Chalk) para el centro del círculo.",
                new ItemStack(Witchery.Items.CHALK_GOLDEN, 1), 1, 40, "Eje Dorado"));

        register(new CovenTask("rite_attuned_stone", CovenTask.TaskCategory.RITUAL,
                "Piedra Sintonizada Cargada",
                "Carga con energía del altar 1 Piedra Sintonizada (Charged Attuned Stone).",
                Witchery.Items.GENERIC.itemAttunedStoneCharged.createStack(1), 1, 40, "Reserva de Maná"));

        register(new CovenTask("rite_waystone", CovenTask.TaskCategory.RITUAL,
                "Piedra de Enlace Dimensional",
                "Vincula las coordenadas de un santuario en 1 Piedra de Salto (Waystone).",
                Witchery.Items.GENERIC.itemWaystone.createStack(1), 1, 35, "Portal Anclado"));

        // =========================================================================
        // CATEGORÍA 7: ALDEANOS DE MCA & SOCIEDAD HUMANA (8 Tareas)
        // =========================================================================
        register(new CovenTask("mca_wool", CovenTask.TaskCategory.MCA_VILLAGER,
                "Tejido de la Hermandad",
                "Obtén 20 bloques de lana de los rebaños aldeanos para confeccionar túnicas.",
                new ItemStack(Blocks.wool, 20), 20, 20, "Comunión Aldeana"));

        register(new CovenTask("mca_gold", CovenTask.TaskCategory.MCA_VILLAGER,
                "Tributo de Oro Noble",
                "Comercia con los nobles de la aldea y entrega 10 Lingotes de Oro.",
                new ItemStack(Items.gold_ingot, 10), 10, 30, "Prosperidad Oculta"));

        register(new CovenTask("mca_bread", CovenTask.TaskCategory.MCA_VILLAGER,
                "Pan del Granjero Humano",
                "Consigue 16 barras de pan horneadas por los panaderos de MCA.",
                new ItemStack(Items.bread, 16), 16, 20, "Trigo de la Paz"));

        register(new CovenTask("mca_books", CovenTask.TaskCategory.MCA_VILLAGER,
                "Conocimiento de la Biblioteca",
                "Reúne 8 libros de los eruditos y clérigos de las aldeas de MCA.",
                new ItemStack(Items.book, 8), 8, 25, "Sabiduría Popular"));

        register(new CovenTask("mca_clayjars", CovenTask.TaskCategory.MCA_VILLAGER,
                "Jarras del Alfarero",
                "Modela y entrega 12 Jarras de Arcilla Suave (Soft Clay Jars).",
                Witchery.Items.GENERIC.itemSoftClayJar.createStack(12), 12, 20, "Vasija Receptora"));

        register(new CovenTask("mca_bottles", CovenTask.TaskCategory.MCA_VILLAGER,
                "Vidrio Soplado para Pociones",
                "Reúne 16 Frascos de Vidrio vacíos para almacenar elixires del aquelarre.",
                new ItemStack(Items.glass_bottle, 16), 16, 20, "Contenedor de Cristal"));

        register(new CovenTask("mca_leather", CovenTask.TaskCategory.MCA_VILLAGER,
                "Cuero del Curtidor",
                "Consigue 12 piezas de cuero de los artesanos para correas de grimorio.",
                new ItemStack(Items.leather, 12), 12, 20, "Cinto Firme"));

        register(new CovenTask("mca_iron", CovenTask.TaskCategory.MCA_VILLAGER,
                "Hierro de la Guardia Aldeana",
                "Dona o comercia 12 Lingotes de Hierro para apoyar a la guardia.",
                new ItemStack(Items.iron_ingot, 12), 12, 25, "Escudo del Pueblo"));

        // =========================================================================
        // CATEGORÍA 8: TESOROS DE BRUJA SUPREMA & KOBOLDITA (8 Tareas)
        // =========================================================================
        register(new CovenTask("supreme_koboldite_dust", CovenTask.TaskCategory.BREWING,
                "Polvo de Koboldita Primordial",
                "Extrae o sintetiza 4 puñados de Polvo de Koboldita (Koboldite Dust).",
                Witchery.Items.GENERIC.itemKobolditeDust.createStack(4), 4, 45, "Aura Dorada de Cueva"));

        register(new CovenTask("supreme_koboldite_nugget", CovenTask.TaskCategory.BREWING,
                "Pepitas de Metal Esmeralda",
                "Funde y entrega 3 Pepitas de Koboldita (Koboldite Nuggets).",
                Witchery.Items.GENERIC.itemKobolditeNugget.createStack(3), 3, 50, "Brillo de Kobold"));

        register(new CovenTask("supreme_koboldite_ingot", CovenTask.TaskCategory.BREWING,
                "El Lingote de los Señores Goblins",
                "Forja mediante rito alquímico 1 Lingote de Koboldita (Koboldite Ingot).",
                Witchery.Items.GENERIC.itemKobolditeIngot.createStack(1), 1, 65, "Poder de la Reina"));

        register(new CovenTask("supreme_pentacle", CovenTask.TaskCategory.RITUAL,
                "Pentáculo de Koboldita",
                "Crea 1 Pentáculo de Koboldita para elevar el altar a la cima mundial.",
                Witchery.Items.GENERIC.itemKobolditePentacle.createStack(1), 1, 70, "Corona de Altar"));

        register(new CovenTask("supreme_candelabra", CovenTask.TaskCategory.RITUAL,
                "Candelabro Áureo de Comunión",
                "Forja 1 Candelabro ritual con oro y grasa mágica para el altar.",
                Witchery.Items.GENERIC.itemCandelabra.createStack(1), 1, 40, "Fuego Eterno"));

        register(new CovenTask("supreme_chalice", CovenTask.TaskCategory.RITUAL,
                "El Cáliz de la Diosa",
                "Crea 1 Cáliz Sagrado (Chalice) y colócalo como tributo.",
                Witchery.Items.GENERIC.itemChaliceEmpty.createStack(1), 1, 40, "Receptáculo Santo"));

        register(new CovenTask("supreme_arthana", CovenTask.TaskCategory.HUNTING,
                "La Daga de la Athame Consagrada",
                "Forja 1 Athame de plata y esmeralda para cosechar esencias puras.",
                new ItemStack(Witchery.Items.ARTHANA), 1, 40, "Corte Quirúrgico"));

        register(new CovenTask("supreme_branch", CovenTask.TaskCategory.RITUAL,
                "La Rama Mística Tallada",
                "Talla 1 Rama Mística (Mystic Branch) de madera viva de Ent.",
                new ItemStack(Witchery.Items.MYSTIC_BRANCH), 1, 45, "Canalizador Antiguo"));
    }

    public static CovenTask getRandomTask(EntityPlayer player) {
        if (TASK_KEYS.isEmpty()) return null;
        Random rand = player.worldObj != null ? player.worldObj.rand : new Random();
        String randomKey = TASK_KEYS.get(rand.nextInt(TASK_KEYS.size()));
        return TASK_REGISTRY.get(randomKey);
    }

    public static String getActiveTaskId(EntityPlayer player) {
        if (player == null) return "";
        NBTTagCompound nbt = player.getEntityData();
        return nbt.getString("WITCHERY_ACTIVE_COVEN_TASK");
    }

    public static void setActiveTask(EntityPlayer player, String taskId) {
        if (player == null) return;
        player.getEntityData().setString("WITCHERY_ACTIVE_COVEN_TASK", taskId != null ? taskId : "");
    }

    public static void showCurrentTaskStatus(EntityPlayer player, String witchName) {
        String activeId = getActiveTaskId(player);
        if (activeId == null || activeId.isEmpty()) {
            player.addChatMessage(new ChatComponentText("§e<" + witchName + "> §d\"No tienes ninguna tarea activa con el aquelarre. ¡Pídeme una cuando desees poner a prueba tu temple!\""));
            return;
        }

        CovenTask task = getTask(activeId);
        if (task == null) {
            setActiveTask(player, "");
            player.addChatMessage(new ChatComponentText("§c<" + witchName + "> §7\"Ese pacto antiguo ha expirado. Pídeme una nueva tarea.\""));
            return;
        }

        player.addChatMessage(new ChatComponentText("§5§m-----------------------------------------------------"));
        player.addChatMessage(new ChatComponentText("§5§l📜 [Tarea Activa del Aquelarre]"));
        player.addChatMessage(new ChatComponentText("§eTítulo: " + task.getCategory().colorPrefix + task.getTitle() + " §7(" + task.getCategory().displayName + ")"));
        player.addChatMessage(new ChatComponentText("§fObjetivo: §d" + task.getDescription()));

        boolean ready = task.canComplete(player);
        if (ready) {
            player.addChatMessage(new ChatComponentText("§a✔ ¡Tienes los tributos listos en tu inventario! Haz clic derecho para entregar."));
        } else {
            player.addChatMessage(new ChatComponentText("§c✘ Aún no reúnes los elementos requeridos. Revisa tu objetivo y regresa cuando los tengas."));
        }
        player.addChatMessage(new ChatComponentText("§5§m-----------------------------------------------------"));
    }
}
