package de.module;

/**
 * Contains the module data
 * 
 */

public class ModuleData
{
	
	private String moduleName ="";
	private int moduleVersion = 0;
	private String data = "";
	
	
	public ModuleData(){
		
	}
	
	public void setModuleName(String n){
		this.moduleName = n;
	}
	
	public String getModuleName(){
		return this.moduleName;
	}

	public void setModuleVersion(int n){
		this.moduleVersion = n;
	}
	
	public int getModuleVersion(){
		return this.moduleVersion;
	}

	public void setModuleData(String n){
		this.data = n;
	}
	
	public String getModuleData(){
		return this.data;
	}
}
