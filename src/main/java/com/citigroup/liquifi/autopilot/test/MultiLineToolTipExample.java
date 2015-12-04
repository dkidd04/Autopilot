package com.citigroup.liquifi.autopilot.test;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JToolTip;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalToolTipUI;

import com.citigroup.liquifi.util.JMultiLineToolTip;

/**
 * @version 1.0 11/09/98
 */
public class MultiLineToolTipExample extends JFrame {
	private static final long serialVersionUID = 1L;

public MultiLineToolTipExample() {
    super("Multi-Line ToolTip Example");
    JButton button = new JButton("Hello, world") {
		private static final long serialVersionUID = 1L;

	public JToolTip createToolTip() {
        JMultiLineToolTip tip = new JMultiLineToolTip();
        tip.setColumns(100);
        tip.setComponent(this);
        return tip;
      }
    };
    button.setToolTipText("237|12:31:19:455|INFO|NewOrderHandler|FIXNEW|8=FIX.4.49=156335=D49=QFF56=CGMI50=QFF157=LIQUIFI52=20080827-19:13:24.000122=20080827-19:13:24.000-9=1219864404256419000\n-11=121986440425642400034=660=20080827-19:13:24.399-17=QDSAQA4154=110080=20080915-12:31:19.454-1=082405113e8#01=QDSAQA4111=082808MANN01#010123=55=TEAM,8012=20080827,8001=QDSAQA41,15=USD18=k37=082808MANN0138=1500.011042=240=P44=6.39212=1-33=;RC:E;55=TEAM59=163=075=20080827100=AEE120=?126=20080827-20:00:00.000207=NASD10006=ContinuousOrder;528=A779=20080827-19:13:24.38710005=610010=CLBK10015=LAVA10016=QFF10029=768083054947310030=QDMA10031=0A10036=MLB10040=Y10041=TB10042=A10043=MLB10044=;8018=10051=0028144517LAV10053=CDMA10074=;RR:;377=N132=-1.0133=-1.0134=1.0135=1.0561=100.021=310081\n=sm48723197010083=QFF8010=Approved:xA:null:null|completed BBE checks on client-side8008=QDMA8015=QDSAQA418014=Y8013=Y8012=200808278003=AEE8002=QDSAQA418001=QDSAQA418007=?8006=MLB8005=MLB8004=QDSAQA418019=OrdrSymb=TCKR;JPRestricted=N;PriceChopPrec=2;AlwaysValidAcct=Y;JPAgencyOnly=N;JPShortSell=Y;SBXNextDayOk=N;AutoDotToManual=N;RptOrigOrdQty=Y;RptOrigClOrdID=Y;RptCxlLastShares=Y;IDPad=Y;GBPFlag=N;TimeLocale=America/New_York;ShortSellRestriction=3P;RptOrigOnPending=Y;Held=N;RoutingCriteria=TargetSubID;AutoRoute=N;BypassPV=Y;WorkflowEnabled=Y;ContinuousOrder=Y;ValueCurr=USD;Prec=4;BranchID=Y;AvgPrxAccount=0028144517LAV;AutoTktF=Y;AutoMergeF=Y;OrdrSecurityId=TCKR;BBESeverity=4;8023=20080827-21:00:008021= 111=10010202=CitiDMA10203=DMA10204=LQFI47=A354=6355=IGNORE-100=NASD8053=010=000|");
    getContentPane().add(button);
  }

  @SuppressWarnings("deprecation")
public static void main(String args[]) {
    try {
        UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
    } catch (Exception evt) {}
  
    MultiLineToolTipExample f = new MultiLineToolTipExample();
    f.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        System.exit(0);
      }
    });
    f.setSize(300, 100);
    f.show();
  }
}

class MultiLineToolTip extends JToolTip {
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

public MultiLineToolTip() {
    setUI(new MultiLineToolTipUI());
  }
}

class MultiLineToolTipUI extends MetalToolTipUI {
  private String[] strs;

  @SuppressWarnings("deprecation")
public void paint(Graphics g, JComponent c) {
    FontMetrics metrics = Toolkit.getDefaultToolkit().getFontMetrics(
        g.getFont());
    Dimension size = c.getSize();
    g.setColor(c.getBackground());
    g.fillRect(0, 0, size.width, size.height);
    g.setColor(c.getForeground());
    if (strs != null) {
      for (int i = 0; i < strs.length; i++) {
        g.drawString(strs[i], 3, (metrics.getHeight()) * (i + 1));
      }
    }
  }

  @SuppressWarnings("deprecation")
public Dimension getPreferredSize(JComponent c) {
    FontMetrics metrics = Toolkit.getDefaultToolkit().getFontMetrics(
        c.getFont());
    String tipText = ((JToolTip) c).getTipText();
    if (tipText == null) {
      tipText = "";
    }
    BufferedReader br = new BufferedReader(new StringReader(tipText));
    String line;
    int maxWidth = 0;
    Vector<String> v = new Vector<String>();
    try {
      while ((line = br.readLine()) != null) {
        int width = SwingUtilities.computeStringWidth(metrics, line);
        maxWidth = (maxWidth < width) ? width : maxWidth;
        v.addElement(line);
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    }
    int lines = v.size();
    if (lines < 1) {
      strs = null;
      lines = 1;
    } else {
      strs = new String[lines];
      int i = 0;
      for (Enumeration<String> e = v.elements(); e.hasMoreElements(); i++) {
        strs[i] = (String) e.nextElement();
      }
    }
    int height = metrics.getHeight() * lines;
    return new Dimension(maxWidth + 6, height + 4);
  }
}


           