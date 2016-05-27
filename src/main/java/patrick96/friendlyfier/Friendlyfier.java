package patrick96.friendlyfier;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.Level;

@Mod(modid = Friendlyfier.MODID, version = Friendlyfier.VERSION)
public class Friendlyfier
{
    public static final String MODID = "friendlyfier";
    public static final String VERSION = "@VERSION@";

    @SidedProxy(clientSide = "patrick96.friendlyfier.ClientProxy", serverSide = "patrick96.friendlyfier.ServerProxy")
    public static CommonProxy proxy;

    public final static Item itemFriendlyfier = new ItemFriendlyfier();

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);

        MinecraftForge.EVENT_BUS.register(this);
        GameRegistry.register(itemFriendlyfier);

        GameRegistry.addRecipe(new ItemStack(itemFriendlyfier, 2), " AB", " CA", "D  ",
                'A', Items.gold_nugget,
                'B', Items.lead,
                'C', Items.stick,
                'D', Items.bone);

    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        proxy.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @SubscribeEvent
    public void onJoin(EntityJoinWorldEvent event) {

        Entity entity = event.getEntity();
        if(entity instanceof EntityCreature && entity.isCreatureType(EnumCreatureType.MONSTER, false)) {
            if(Utils.friendlyfy(((EntityCreature) entity), true)) {
                Utils.log(Level.INFO, "Friendlyfied " + entity.getName() + "(" + Utils.getOriginalName(entity) + ") on world join");
            }
        }
    }
}
