/**
 * 
 */
package gui.configdialog.settings;

/**
 * Ein Setting das der Benutzer einstellen kann und das mit {@code set()} ausgefuehrt werden kann.</br>
 * Diese Art von Settings werden nur am Ende einer "ConfigDialog-Session" durch Klick auf "Ok" ausgefuehrt.</br></br>
 * Command-Pattern
 * 
 * 
 */
public interface ConfigDialogSetting
{
	public void set();
}
