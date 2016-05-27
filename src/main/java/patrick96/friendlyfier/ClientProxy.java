package patrick96.friendlyfier;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import static patrick96.friendlyfier.Friendlyfier.MODID;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);

        ModelLoader.setCustomModelResourceLocation(Friendlyfier.itemFriendlyfier, 0, new ModelResourceLocation(MODID + ":friendlyfier"));
    }
}
