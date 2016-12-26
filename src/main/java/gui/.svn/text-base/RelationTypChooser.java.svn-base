/**
 * 
 */
package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Arrays;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableCellEditor;

import data.RelationTyp;
import data.RelationTyp.DirectionType;

/**
 * 
 * 
 */
public class RelationTypChooser extends AbstractCellEditor implements
		TableCellEditor, ActionListener
{
	static final long	serialVersionUID	= 1L;

	JButton				button;

	ChooserPanel		cp;

	RelationTyp			relationTyp;

	JDialog				dialog;

	public RelationTypChooser()
	{
		button = new JButton();
		button.addActionListener(this);
		button.setBorderPainted(false);
	}

	public void actionPerformed(ActionEvent e)
	{
		ChooserPanel cp = new ChooserPanel(relationTyp);

		JOptionPane.showConfirmDialog(null, cp, Messages.getString("VennMaker.Relation.Configure_Value"), //$NON-NLS-1$
				JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null);
		relationTyp = cp.getRelationTyp();
		fireEditingStopped();
	}

	public void show(RelationTyp relationTyp)
	{
		ChooserPanel cp = new ChooserPanel(relationTyp);
		JOptionPane.showConfirmDialog(null, cp, Messages.getString("VennMaker.Relation.Configure_Value"), //$NON-NLS-1$
				JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null);
	}

	public Object getCellEditorValue()
	{
		return relationTyp;
	}

	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column)
	{
		relationTyp = (RelationTyp) value;
		return button;
	}

	private class ChooserPanel extends JPanel
	{
		static final long		serialVersionUID	= 1L;

		private RelationTyp	relationTyp;

		private float			lineWidth;

		private float[]		dashArray;

		private ChooserPanel(RelationTyp ptyp)
		{
			if (ptyp == null){
				this.relationTyp = new RelationTyp();
			}else
			this.relationTyp = ptyp;
			
			this.lineWidth = relationTyp.getStroke().getLineWidth();
			this.dashArray = relationTyp.getStroke().getDashArray();

			GridBagLayout gbl = new GridBagLayout();
			setLayout(gbl);
			GridBagConstraints gbc = new GridBagConstraints();

			int zeile = 0;
			JLabel label;

			label = new JLabel( Messages.getString("RelationTypChooser.0") ); //$NON-NLS-1$
			gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.BOTH;
			gbc.gridx = 0;
			gbc.gridy = zeile;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.insets = new Insets(10, 10, 0, 10);
			gbl.setConstraints(label, gbc);
			add(label);

			JSlider widthSlider = new JSlider(JSlider.HORIZONTAL, 1, 10,
					(int) lineWidth);
			widthSlider.setMajorTickSpacing(1);
			widthSlider.setPaintTicks(true);
			widthSlider.setPaintLabels(true);
			widthSlider.setSnapToTicks(true);
			widthSlider.addChangeListener(new ChangeListener()
			{
				@Override
				public void stateChanged(ChangeEvent e)
				{
					lineWidth = ((JSlider) e.getSource()).getValue();
					repaint();
				}
			});
			gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.BOTH;
			gbc.gridx = 1;
			gbc.gridy = zeile;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.insets = new Insets(10, 10, 0, 10);
			gbl.setConstraints(widthSlider, gbc);
			add(widthSlider);

			zeile++;

			label = new JLabel(Messages.getString("RelationTypChooser.1") ); //$NON-NLS-1$
			gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.BOTH;
			gbc.gridx = 0;
			gbc.gridy = zeile;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.insets = new Insets(10, 10, 0, 10);
			gbl.setConstraints(label, gbc);
			add(label);

			JButton colorButton = new JButton( Messages.getString("RelationTypChooser.2") ); //$NON-NLS-1$
			colorButton.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					Color newColor = JColorChooser
							.showDialog(null, Messages
									.getString("VennMaker.Pick_Color"), relationTyp //$NON-NLS-1$
									.getColor());
					if (newColor != null)
					{
						relationTyp.setColor(newColor);
						repaint();
					}
				}
			});
			gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.BOTH;
			gbc.gridx = 1;
			gbc.gridy = zeile;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.insets = new Insets(10, 10, 0, 10);
			gbl.setConstraints(colorButton, gbc);
			add(colorButton);

			zeile++;

			label = new JLabel( Messages.getString("RelationTypChooser.3") ); //$NON-NLS-1$
			gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.BOTH;
			gbc.gridx = 0;
			gbc.gridy = zeile;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.insets = new Insets(10, 10, 0, 10);
			gbl.setConstraints(label, gbc);
			add(label);

			float[][] dashArrays = { { 1.0f, 0.f }, { 1.0f, 3.0f },
					{ 5.0f, 5.0f }, { 10.0f, 10.0f }, { 20.0f, 10.0f },
					{ 20.0f, 20.0f }, { 25.0f, 9.0f, 2.0f, 9.0f },
					{ 25.0f, 9.0f, 2.0f, 9.0f, 2.0f, 9.0f },
					{ 25.0f, 9.0f, 2.0f, 9.0f, 2.0f, 9.0f, 2.0f, 9.0f },
					{ 25.0f, 9.0f, 5.0f, 9.0f } };
			int pos = -1;
			for (int i = 0; i < dashArrays.length; i++)
			{
				if (Arrays.equals(dashArrays[i], dashArray))
					pos = i;
			}
			if (pos == -1 && dashArray == null)
				dashArray = dashArrays[0];
			else if (pos != -1)
				dashArray = dashArrays[pos];
			JComboBox dashTypeComboBox = new JComboBox(dashArrays);
			dashTypeComboBox.setSelectedItem(dashArray);
			dashTypeComboBox.setRenderer(new DashTypeRenderer(dashArray));
			dashTypeComboBox.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					dashArray = (float[]) ((JComboBox) e.getSource())
							.getSelectedItem();
					repaint();
				}
			});
			dashTypeComboBox.addActionListener(new ActionListener()
			{
				public void actionPerformed(final ActionEvent evt)
				{
					((JComboBox) evt.getSource()).transferFocus();
				}
			});
			gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.BOTH;
			gbc.gridx = 1;
			gbc.gridy = zeile;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.insets = new Insets(10, 10, 0, 10);
			gbl.setConstraints(dashTypeComboBox, gbc);
			add(dashTypeComboBox);

			zeile++;

			label = new JLabel( Messages.getString("RelationTypChooser.4") ); //$NON-NLS-1$
			gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.BOTH;
			gbc.gridx = 0;
			gbc.gridy = zeile;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.insets = new Insets(10, 10, 0, 10);
			gbl.setConstraints(label, gbc);
			add(label);

			JComboBox directionTypeComboBox = new JComboBox(
					RelationTyp.DirectionType.values());
			directionTypeComboBox.setSelectedItem(relationTyp.getDirectionType());
			directionTypeComboBox.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					relationTyp.setDirectionType((DirectionType) ((JComboBox) e
							.getSource()).getSelectedItem());
					repaint();
				}
			});
			gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.BOTH;
			gbc.gridx = 1;
			gbc.gridy = zeile;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.insets = new Insets(10, 10, 0, 10);
			gbl.setConstraints(directionTypeComboBox, gbc);
			add(directionTypeComboBox);

			gbc.gridx = 0;
			gbc.gridy = 6;
			gbc.gridwidth = 3;
			add(new JPanel()
			{
				static final long	serialVersionUID	= 1L;

				{
					setPreferredSize(new Dimension(300, 100));
					setBackground(Color.white);
				}

				public void paintComponent(Graphics g)
				{
					super.paintComponent(g);
					p((Graphics2D) g);
				}
			}, gbc);
		}

		public void p(Graphics2D g2)
		{
			relationTyp.setStroke(new BasicStroke(this.lineWidth,
					BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10,
					this.dashArray, 0));
			g2.setPaint(relationTyp.getColor());
			g2.setStroke(relationTyp.getStroke());
			Shape line = null;
			switch (relationTyp.getDirectionType())
			{
				case DIRECTED:
					line = new ArrowLine(50, 50, 250, 50);
					break;
				case UNDIRECTED:
					line = new Line2D.Double(50, 50, 250, 50);
					break;
				default:
					// Sollte niemals passieren!
					assert false;
			}
			g2.draw(line);
		}

		private RelationTyp getRelationTyp()
		{
			return relationTyp;
		}
	}

	public class DashTypeRenderer extends JComponent implements ListCellRenderer
	{
		static final long	serialVersionUID	= 1L;

		private float[]	dashArray;

		private Dimension	preferredSize;

		public DashTypeRenderer(final float[] dashArray)
		{
			this.dashArray = dashArray;
			this.preferredSize = new Dimension(80, 18);
		}

		public float[] getDashArray()
		{
			return this.dashArray;
		}

		public void setDashArray(final float[] dashArray)
		{
			this.dashArray = dashArray;
			repaint();
		}

		public Dimension getPreferredSize()
		{
			return this.preferredSize;
		}

		public void paintComponent(final Graphics g)
		{
			final Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			final Dimension size = getSize();
			final Insets insets = getInsets();
			final double xx = insets.left;
			final double yy = insets.top;
			final double ww = size.getWidth() - insets.left - insets.right;
			final double hh = size.getHeight() - insets.top - insets.bottom;

			final Point2D one = new Point2D.Double(xx + 6, yy + hh / 2);
			final Point2D two = new Point2D.Double(xx + ww - 6, yy + hh / 2);

			// draw a line connecting the points
			final Line2D line = new Line2D.Double(one, two);
			g2.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
					BasicStroke.JOIN_MITER, 10, this.dashArray, 0));
			g2.draw(line);
		}

		public Component getListCellRendererComponent(final JList list,
				final Object value, final int index, final boolean isSelected,
				final boolean cellHasFocus)
		{
			if (value instanceof float[])
				setDashArray((float[]) value);
			return this;
		}
	}

}
