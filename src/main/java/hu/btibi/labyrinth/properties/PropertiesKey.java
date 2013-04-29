package hu.btibi.labyrinth.properties;

public enum PropertiesKey {
	STORE_MAZE_IN_DB("false"), 
	STORE_DB_PATH(""), 
	URL_PATTERN("http://labyrinth.lbi.co.uk/Maze/Location/%s/%s/json");

	private String defaultValue;

	private PropertiesKey(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

}
