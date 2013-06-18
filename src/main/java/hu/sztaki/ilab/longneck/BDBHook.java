package hu.sztaki.ilab.longneck;

import hu.sztaki.ilab.longneck.bootstrap.Hook;
import hu.sztaki.ilab.longneck.util.database.BDBConfiguration;
import hu.sztaki.ilab.longneck.util.database.DatabaseConnectionManager;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.springframework.context.ApplicationContext;


/**
 *
 * @author Péter Molnár <molnarp@sztaki.mta.hu>
 */
public class BDBHook implements Hook {

    @Override
    public void init(Properties properties, ApplicationContext context) {
        DatabaseConnectionManager.registerConfigurationClass("berkeleydb", BDBConfiguration.class);
    }

    @Override
    public List<URL> getSchemas() throws IOException {
        return new ArrayList<URL>(0);
    }

    @Override
    public List<URL> getMappings() throws IOException {
        return new ArrayList<URL>(0);
    }
}
