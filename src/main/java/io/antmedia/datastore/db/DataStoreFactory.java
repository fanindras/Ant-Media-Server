package io.antmedia.datastore.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import io.antmedia.AppSettings;
import io.antmedia.cluster.DBReader;

public class DataStoreFactory implements IDataStoreFactory, InitializingBean{

	public static final String DB_TYPE_MEMORYDB = "memorydb";
	public static final String DB_TYPE_MAPDB = "mapdb";
	public static final String DB_TYPE_MONGODB = "mongodb";
	public static final String SETTINGS_DB_APP_NAME = "db.app.name";
	public static final String SETTINGS_DB_NAME = "db.name";
	public static final String SETTINGS_DB_TYPE = "db.type";
	public static final String SETTINGS_DB_HOST = "db.host";
	public static final String SETTINGS_DB_USER = "db.user";
	public static final String SETTINGS_DB_PASS = "db.password";


	private static Logger logger = LoggerFactory.getLogger(DataStoreFactory.class);

	
	private DataStore dataStore;
	 
	@Value( "${" + AppSettings.SETTINGS_WRITE_STATS_TO_DATASTORE +":true}")
	private boolean writeStatsToDatastore;
	
	@Value( "${"+SETTINGS_DB_APP_NAME+":#{null}}" )
	private String appName;
	
	@Value( "${"+SETTINGS_DB_NAME+":#{null}}" )
	private String dbName;
	
	/**
	 * One of the DB_TYPE_*
	 */
	
	@Value( "${"+SETTINGS_DB_TYPE+":#{null}}" )
	private String dbType;
	
	@Value( "${"+SETTINGS_DB_HOST+":#{null}}" )
	private String dbHost;
	
	@Value( "${"+SETTINGS_DB_USER+":#{null}}" )
	private String dbUser;
	
	@Value( "${"+SETTINGS_DB_PASS+":#{null}}" )
	private String dbPassword;
	
	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getDbType() {
		return dbType;
	}

	public void setDbType(String dbType) {
		this.dbType = dbType;
	}

	public String getDbHost() {
		return dbHost;
	}

	public void setDbHost(String dbHost) {
		this.dbHost = dbHost;
	}

	public String getDbUser() {
		return dbUser;
	}

	public void setDbUser(String dbUser) {
		this.dbUser = dbUser;
	}

	public String getDbPassword() {
		return dbPassword;
	}

	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}
	
	public void init()  
	{
		if(dbType.contentEquals(DB_TYPE_MONGODB))
		{
			dataStore = new MongoStore(dbHost, dbUser, dbPassword, dbName);
		}
		else if(dbType .contentEquals(DB_TYPE_MAPDB))
		{
			dataStore = new MapDBStore(dbName+".db");
		}
		else if(dbType .contentEquals(DB_TYPE_MEMORYDB))
		{
			dataStore = new InMemoryDataStore(dbName);
		}
		else {
			logger.error("Undefined Datastore:{} app:{} db name:{}", dbType, appName, dbName);
		}
		
		logger.info("Used Datastore:{} app:{} db name:{}", getDbType(), getAppName(), getDbName());
		
		if(dataStore != null) {
			dataStore.setWriteStatsToDatastore(isWriteStatsToDatastore());
			DBReader.instance.addDataStore(appName, dataStore);
			dataStore.clearStreamsOnThisServer();
		}
	}
	
	@Override
	public void afterPropertiesSet() throws Exception 
	{
		init();
	}
	
	
	public DataStore getDataStore() {
		return dataStore;
	}
	
	public void setDataStore(DataStore dataStore) {
		this.dataStore = dataStore;
	}

	public String getAppName()
	{
		return appName;
	}
	
	public void setAppName(String appName)
	{
		this.appName = appName;
	}

	public boolean isWriteStatsToDatastore() {
		return writeStatsToDatastore;
	}

	public void setWriteStatsToDatastore(boolean writeStatsToDatastore) {
		this.writeStatsToDatastore = writeStatsToDatastore;
	}

}
	
