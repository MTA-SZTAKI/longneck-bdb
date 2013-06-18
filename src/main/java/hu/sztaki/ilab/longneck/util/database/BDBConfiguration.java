package hu.sztaki.ilab.longneck.util.database;

import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import java.io.File;
import java.util.Map;
import java.util.Properties;

/**
 *
 * @author Péter Molnár <molnarp@sztaki.mta.hu>
 */
public class BDBConfiguration implements Configuration {

    /** The name of the connection. */
    private String name;
    /** The path to the database environment. */
    private String location;
    /** Allow automatic creation of database, if it doesn't exist. */
    private boolean allowCreate = false;
    /** The database is transactional. */
    private boolean transactional = true;
    /** The database is read-only. */
    private boolean readOnly = false;
    /** Allow locking on the database. */
    private boolean locking = true;
    /** Enable degree 3 serialization of transactions. */
    private boolean txnSerializableIsolation = false;
    /** The node name of the database. */
    private String nodeName = "bdbNode";
    /** The environment configuration read from the properties file. */
    private Properties environmentProperties;
    /** The BerkeleyDB environment object. */
    private Environment environment = null;
    /** The environment directory. */
    private File environmentDirectory;

    public BDBConfiguration() {
        environmentProperties = new Properties();
    }

    public Environment getEnvironment() {
        if (environment == null) {
            EnvironmentConfig environmentConfig = new EnvironmentConfig(environmentProperties);
            environmentConfig.setAllowCreate(allowCreate);
            environmentConfig.setTransactional(transactional);
            environmentConfig.setReadOnly(readOnly);
            environmentConfig.setLocking(locking);
            environmentConfig.setTxnSerializableIsolation(txnSerializableIsolation);
            environmentConfig.setNodeName(nodeName);
            environment = new Environment(environmentDirectory, environmentConfig);
        }

        return environment;
    }

    @Override
    public void readProperites(Properties properties) throws DatabaseConfigurationException {
        if (! "berkeleydb".equals( properties.getProperty(
                String.format("database.connection.%1$s.type", name) ))) {
            throw new DatabaseConfigurationException("Not a Berkeley DB configuration property.");
        }

        String prefix = String.format("database.connection.%1$s.", name);
        String jePrefix = String.format("database.connection.%1$s.je.", name);

        for (Map.Entry e : properties.entrySet()) {
            String key = (String) e.getKey();

            if (key.startsWith(prefix)) {
                if (String.format("database.connection.%1$s.allowCreate", name).equals(key)) {
                    this.allowCreate = Boolean.valueOf((String) e.getValue());
                }
                else if (String.format("database.connection.%1$s.location", name).equals(key)) {
                    this.location = (String) e.getValue();
                }
                else if (String.format("database.connection.%1$s.locking", name).equals(key)) {
                    this.locking = Boolean.valueOf((String) e.getValue());
                }
                else if (String.format("database.connection.%1$s.nodeName", name).equals(key)) {
                    this.nodeName = (String) e.getValue();
                }
                else if (String.format("database.connection.%1$s.readOnly", name).equals(key)) {
                    this.readOnly = Boolean.valueOf((String) e.getValue());
                }
                else if (String.format("database.connection.%1$s.transactional", name).equals(key)) {
                    this.transactional = Boolean.valueOf((String) e.getValue());
                }
                else if (String.format("database.connection.%1$s.txnSerializableIsolation", name).equals(key)) {
                    this.txnSerializableIsolation = Boolean.valueOf((String) e.getValue());
                }
                else if (key.startsWith(jePrefix)) {
                    environmentProperties.setProperty(key.substring(prefix.length()),
                            (String) e.getValue());
                }
            }
        }

        // Check configuration

        // Location must be proper
        if (location == null || "".equals(location)) {
            throw new DatabaseConfigurationException("BerkeleyDB location not specified.");
        }

        // Node name must not be null or empty
        if (nodeName == null || "".equals(nodeName)) {
            throw new DatabaseConfigurationException("BerkeleyDB nodeName is null or empty.");
        }

        // Verify location and create if necessary
        environmentDirectory = new File(location);
        if (! environmentDirectory.exists()) {
            if (allowCreate) {
                boolean ret = environmentDirectory.mkdirs();

                if (ret == false) {
                    throw new DatabaseConfigurationException(
                            String.format("Error while creating BerkeleyDB location directory on location '%1$s'.",
                            location));
                }
            } else {
                throw new DatabaseConfigurationException(
                        String.format("BerkeleyDB location does not exist and allowCreate is false: '%1$s'.",
                        location));
            }
        }
        else if (! environmentDirectory.isDirectory()) {
                throw new DatabaseConfigurationException(
                        String.format("BerkeleyDB location is not a directory: '%1$s'.",
                        location));
        }
    }

    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public boolean isAllowCreate() {
        return allowCreate;
    }

    public void setAllowCreate(boolean allowCreate) {
        this.allowCreate = allowCreate;
    }

    public Properties getEnvironmentProperties() {
        return environmentProperties;
    }

    public void setEnvironmentProperties(Properties environmentProperties) {
        this.environmentProperties = environmentProperties;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isLocking() {
        return locking;
    }

    public void setLocking(boolean locking) {
        this.locking = locking;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public boolean isTransactional() {
        return transactional;
    }

    public void setTransactional(boolean transactional) {
        this.transactional = transactional;
    }

    public boolean isTxnSerializableIsolation() {
        return txnSerializableIsolation;
    }

    public void setTxnSerializableIsolation(boolean txnSerializableIsolation) {
        this.txnSerializableIsolation = txnSerializableIsolation;
    }

    @Override
    public void destroy() {
        try {
            if (environment != null) {
                environment.sync();
                environment.flushLog(true);

                environment.close();
                environment = null;
            }
        } catch (NullPointerException ex) {
            // do nothing
        }
    }
}
