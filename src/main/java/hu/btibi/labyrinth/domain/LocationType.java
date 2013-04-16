package hu.btibi.labyrinth.domain;

import com.google.gson.annotations.SerializedName;

public enum LocationType {
	@SerializedName("Start")
	START,
	
	@SerializedName("Exit")
	EXIT,
	
	@SerializedName("Normal")
	NORMAL,
	
	@SerializedName("PowerPill")
	POWERPILL;
	
}
