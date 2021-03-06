package nfn11.xpwars.special.listener;

import nfn11.xpwars.utils.SpecialItemUtils;
import org.bukkit.Bukkit;
import nfn11.xpwars.XPWars;

public class RegisterSpecialListeners {
    public RegisterSpecialListeners() {
        if (XPWars.getConfigurator().config.getBoolean("features.specials")) {
            new SpecialItemUtils();
            Bukkit.getServer().getPluginManager().registerEvents(new RemoteTNTListener(), XPWars.getInstance());
            Bukkit.getServer().getPluginManager().registerEvents(new PortableShopListener(), XPWars.getInstance());
            Bukkit.getServer().getPluginManager().registerEvents(new ThrowableTNTListener(), XPWars.getInstance());
            Bukkit.getServer().getPluginManager().registerEvents(new TrampolineListener(), XPWars.getInstance());
            Bukkit.getServer().getPluginManager().registerEvents(new VouncherListener(), XPWars.getInstance());
            Bukkit.getServer().getPluginManager().registerEvents(new RideableProjectileListener(),
                    XPWars.getInstance());
        }
    }
}
