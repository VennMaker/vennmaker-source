/**
 * 
 */
package gui.admin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import data.Question;

/**
 * This is a model class for the whole bunch of tables in the configuration
 * dialog that contain questions about attributes.
 * 
 * This is a really rape of the GUI-Table-Element since it only stores a list of
 * elements rather than a table of values. However, the table-metaphor is best
 * suited for the desired purpose.
 * 
 * 
 * 
 */
public class AttributeQuestionTable implements TableModel, Iterable<Question>
{
	/**
	 * The set of questions that is contained in the GUI-table.
	 */
	private List<Question>	questions	= new ArrayList<Question>();

	/**
	 * Whether the model represents ego-related questions. In this case some
	 * values are ignored.
	 */
	private boolean			ego;

	/**
	 * Creates a new TableModel.
	 * 
	 * @param ego
	 *           Whether the model represents questions related to ego.
	 */
	public AttributeQuestionTable(boolean ego)
	{
		this.ego = ego;
	}

	/**
	 * Returns the question object at the given index.
	 * 
	 * @param index
	 *           A valid index.
	 * @return <code>null</code> if index is wrong.
	 */
	public Question getQuestionAt(int index)
			throws ArrayIndexOutOfBoundsException
	{
		return this.questions.get(index);
	}

	/**
	 * And now for something completely different!
	 * 
	 * @param q
	 *           Something different.
	 */
	public void addQuestion(final Question q)
	{
		questions.add(q);
		fireTableDataChanged();
	}

	/**
	 * Removes the question at the given (and valid!) index.
	 * 
	 * @param index
	 *           A valid index. All listeners will be informed!
	 */
	public void removeQuestion(int index)
	{
		questions.remove(index);
		fireTableDataChanged();
	}

	/**
	 * fires the table data changed.
	 */
	public void fireTableDataChanged()
	{
		for (TableModelListener l : listeners)
		{
			l.tableChanged(new TableModelEvent(this));
		}
	}

	/**
	 * A set of listeners. Listeners are one of the most over-estimated things in
	 * modern java development. In earlier years we had no listeners and
	 * everything worked as well. Nevertheless, nowadays it is believed that
	 * software without a fully designed server-client interface, without a valid
	 * JEE implementation or at least using hibernate is only a fast hack rather
	 * than useful.
	 * 
	 * This is nasty but true. So, don't hesitate to delete this set in order to
	 * start the revolution against the design patterns "Gang of Four" (GoF).
	 * Since the deletion of listeners causes serios harm to the functionality of
	 * this software I will keep it in. :-)
	 */
	private Set<TableModelListener>	listeners	= new HashSet<TableModelListener>();

	/**
	 * @param l
	 * @see javax.swing.table.AbstractTableModel#addTableModelListener(javax.swing.event.TableModelListener)
	 */
	public void addTableModelListener(TableModelListener l)
	{
		listeners.add(l);
	}

	/**
	 * @param row
	 * @param column
	 * @return Something
	 * @see javax.swing.table.DefaultTableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int row, int column)
	{
		if (row < 0 || row > this.questions.size() - 1)
			return null;
		Question q = this.questions.get(row);
		switch (column)
		{
			case 0:
				return q.getLabel();
			case 1:
				return q.getQuestion();
			case 2:
				return q.getDataType();
			case 3:
				return q.getTime();
			default:
				return null;
		}
	}

	/**
	 * @param l
	 * @see javax.swing.table.AbstractTableModel#removeTableModelListener(javax.swing.event.TableModelListener)
	 */
	public void removeTableModelListener(TableModelListener l)
	{
		listeners.remove(l);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getColumnClass(int)
	 */
	@Override
	public Class<?> getColumnClass(int columnIndex)
	{
		return String.class;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount()
	{
		return (ego ? 3 : 4);
	}

	/**
	 * Leert die Fragenliste.
	 */
	public void clear()
	{
		questions.clear();
		fireTableDataChanged();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int columnIndex)
	{
		switch (columnIndex)
		{
			case 0:
				return Messages.getString("AttributeQuestionTable.0"); //$NON-NLS-1$
			case 1:
				return Messages.getString("AttributeQuestionTable.1"); //$NON-NLS-1$
			case 2:
				return Messages.getString("AttributeQuestionTable.2"); //$NON-NLS-1$
			case 4:
				return Messages.getString("AttributeQuestionTable.3"); //$NON-NLS-1$
			default:
				return ""; //$NON-NLS-1$
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getRowCount()
	 * @see javax.swing.table.DefaultTableModel#getRowCount()
	 */
	@Override
	public int getRowCount()
	{
		return this.questions.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#isCellEditable(int, int)
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
	 */
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex)
	{
		System.err.println(Messages.getString("AttributeQuestionTable.5")); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<Question> iterator()
	{
		return this.questions.iterator();
	}

}
