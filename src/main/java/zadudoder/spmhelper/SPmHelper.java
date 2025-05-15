package zadudoder.spmhelper;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SPmHelper implements ModInitializer {
    public static final String MOD_ID = "spmhelper";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("SPM Helper initialized");
    }
}