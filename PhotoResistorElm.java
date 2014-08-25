// stub PhotoResistorElm based on SparkGapElm
// FIXME need to uncomment PhotoResistorElm line from CirSim.java
// FIXME need to add PhotoResistorElm.java to srclist

import java.awt.*;
import java.util.StringTokenizer;

class PhotoResistorElm extends CircuitElm {
    double minresistance, maxresistance;
    double resistance;
    double sliderPerct;
    Scrollbar slider;
    Label label;

    public PhotoResistorElm(int xx, int yy) {
        super(xx, yy);
        maxresistance = 10000;
        minresistance = 100;
        createSlider();
    }

    public PhotoResistorElm(int xa, int ya, int xb, int yb, int f,
                            StringTokenizer st) {
        super(xa, ya, xb, yb, f);
        minresistance = new Double(st.nextToken()).doubleValue();
        maxresistance = new Double(st.nextToken()).doubleValue();
        createSlider();
    }

    boolean nonLinear() {
        return true;
    }

    int getDumpType() {
        return 190;
    }

    String dump() {
        return super.dump() + " " + minresistance + " " + maxresistance;
    }

    Point ps3, ps4;

    void createSlider() {
        sim.main.add(label = new Label("Light Level", Label.CENTER));
        int value = 50;
        sim.main.add(slider = new Scrollbar(Scrollbar.HORIZONTAL, value, 1, 0, 101));
        sim.main.validate();
    }

    void setPoints() {
        super.setPoints();
        calcLeads(32);
        ps3 = new Point();
        ps4 = new Point();
    }

    void delete() {
        sim.main.remove(label);
        sim.main.remove(slider);
    }

    void draw(Graphics g) {
        int segments = 16;
        int i;
        int ox = 0;
        int hs = sim.euroResistorCheckItem.getState() ? 6 : 8;
        double v1 = volts[0];
        double v2 = volts[1];
        setBbox(point1, point2, hs);
        draw2Leads(g);
        setPowerColor(g, true);
        double segf = 1. / segments;
        if (!sim.euroResistorCheckItem.getState()) {
            // draw zigzag
            for (i = 0; i != segments; i++) {
                int nx = 0;
                switch (i & 3) {
                    case 0:
                        nx = 1;
                        break;
                    case 2:
                        nx = -1;
                        break;
                    default:
                        nx = 0;
                        break;
                }
                double v = v1 + (v2 - v1) * i / segments;
                setVoltageColor(g, v);
                interpPoint(lead1, lead2, ps1, i * segf, hs * ox);
                interpPoint(lead1, lead2, ps2, (i + 1) * segf, hs * nx);
                drawThickLine(g, ps1, ps2);
                ox = nx;
            }
        } else {
            // draw rectangle
            setVoltageColor(g, v1);
            interpPoint2(lead1, lead2, ps1, ps2, 0, hs);
            drawThickLine(g, ps1, ps2);
            for (i = 0; i != segments; i++) {
                double v = v1 + (v2 - v1) * i / segments;
                setVoltageColor(g, v);
                interpPoint2(lead1, lead2, ps1, ps2, i * segf, hs);
                interpPoint2(lead1, lead2, ps3, ps4, (i + 1) * segf, hs);
                drawThickLine(g, ps1, ps3);
                drawThickLine(g, ps2, ps4);
            }
            interpPoint2(lead1, lead2, ps1, ps2, 1, hs);
            drawThickLine(g, ps1, ps2);
        }
        if (sim.showValuesCheckItem.getState()) {
            String s = getShortUnitText(resistance, "");
            drawValues(g, s, hs);
        }
        //  FIXME: add arrow to show that it is a photoresistor
        doDots(g);
        drawPosts(g);
    }

    void calculateCurrent() {
        double vd = volts[0] - volts[1];
        current = vd / resistance;
    }

    void startIteration() {
        double vd = volts[0] - volts[1];
        sliderPerct = (double)slider.getValue() / 100;
        double range = (maxresistance - minresistance) * sliderPerct;
        resistance = maxresistance - range;
//        System.out.println("slider % is" + perct + " current resistance " + range + " max " + maxresistance);
    }

    void doStep() {
        sim.stampResistor(nodes[0], nodes[1], resistance);
    }

    void stamp() {
        sim.stampNonLinear(nodes[0]);
        sim.stampNonLinear(nodes[1]);
    }

    void getInfo(String arr[]) {
        arr[0] = "photo resistor";
        getBasicInfo(arr);
        arr[3] = "R = " + getUnitText(resistance, sim.ohmString);
        arr[4] = "Ron = " + getUnitText(minresistance, sim.ohmString);
        arr[5] = "Roff = " + getUnitText(maxresistance, sim.ohmString);
    }

    public EditInfo getEditInfo(int n) {
        // ohmString doesn't work here on linux
        if (n == 0)
            return new EditInfo("Min resistance (ohms)", minresistance, 0, 0);
        if (n == 1)
            return new EditInfo("Max resistance (ohms)", maxresistance, 0, 0);
        return null;
    }

    public void setEditValue(int n, EditInfo ei) {
        if (ei.value > 0 && n == 0)
            minresistance = ei.value;
        if (ei.value > 0 && n == 1)
            maxresistance = ei.value;
    }
}

