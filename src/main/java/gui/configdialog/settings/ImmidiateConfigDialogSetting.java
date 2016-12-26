/**
 * 
 */
package gui.configdialog.settings;

/**
 * Setting welches sofort (noch in der Session im ConfigDialog) ausgefuehrt wird.</br>
 * Somit braucht dieses Setting nicht nur {@code set()} zum ausfuehren</br>
 * sondern auch {@code undo()} zum rueckgaengig machen, wenn der User</br>
 * die Session im ConfigDialog mit "Cancel" abbricht.
 * 
 * 
 */
public interface ImmidiateConfigDialogSetting extends ConfigDialogSetting
{
	/**
	 * reverts all changes done by {@code set()}
	 */
	public void undo();
}
