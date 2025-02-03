package org.csstudio.javafx.rtplot.internal;

import org.csstudio.javafx.rtplot.Trace;
import org.csstudio.javafx.rtplot.data.PlotDataItem;
import org.csstudio.javafx.rtplot.data.PlotDataProvider;

import java.util.List;

public class PlotEdit<XTYPE extends Comparable<XTYPE>> {

    private boolean point_selected = false;
    private int selectedAxis, selectedTrace, selectedIndex;
    private XTYPE posX;
    private double posY;

    public PlotEdit(){
        point_selected = false;
        selectedAxis = 0;
        selectedTrace = 0;
        selectedIndex = 0;
        posX = null;
        posY = 0;
    }

    //find the closest point from all editable traces to the
    public boolean selectClosestPoint(List<YAxisImpl<XTYPE>> y_axes, AxisPart<XTYPE> x_axis, double x1, double y1) {
        //axis, trace, index and graph X and Y of current closest point to click
        int bestMatchAxis = 0;
        int bestMatchTrace = 0;
        int bestMatchIndex = 0;
        XTYPE bestMatchX = null;
        double bestMatchY = 0.0;

        //initially set best matching distance to infinite - any other point will be closer
        double bestMatchDistance = Double.POSITIVE_INFINITY;
        double minClickProximity = 10;

        for (int axisIndex = 0; axisIndex<y_axes.size(); axisIndex++) {
            YAxisImpl<XTYPE> axis = y_axes.get(axisIndex);

            //traces only accessible with foreach iterator, so make a manual index
            int traceIndex = 0;

            for (Trace<XTYPE> trace : axis.getTraces()) {
                try {
                    System.out.println("Editable trace: " + trace.isEditable());
                    if(trace.isEditable()){
                        PlotDataProvider<XTYPE> data =  trace.getData();
                        for (int i = 0; i < data.size(); i++) {

                            PlotDataItem<XTYPE> item = data.get(i);

                            double x2 = x_axis.getScreenCoord(item.getPosition()) ;
                            double y2 = axis.getScreenCoord((double) item.getValue());
                            //compare point position on screen to mouse position, replace if closer
                            double distance = Math.hypot(x1-x2, y1-y2);
                            if (distance < bestMatchDistance) {
                                bestMatchAxis = axisIndex;
                                bestMatchTrace = traceIndex;
                                bestMatchIndex = i;
                                bestMatchX = item.getPosition();
                                bestMatchY = item.getValue();
                                bestMatchDistance = distance;
                            }
                        }
                    }

                } catch (Exception ex) {
                    return false;
                }
                traceIndex ++;

            }

        }
        // only selects a point if it is within acceptable interaction distance
        if (bestMatchDistance > minClickProximity) {
            return false;
        }
        setPointSelected(true);
        setSelectedAxis(bestMatchAxis);
        setSelectedTrace(bestMatchTrace);
        setSelectedIndex(bestMatchIndex);
        setPosX(bestMatchX);
        setPosY(bestMatchY);
        return false;

    }

    public boolean isPointSelected() { return point_selected; }

    public void setPointSelected(boolean point_selected) { this.point_selected = point_selected; }

    public int getSelectedAxis() { return selectedAxis; }

    public void setSelectedAxis(int selectedAxis) { this.selectedAxis = selectedAxis; }

    public XTYPE getPosX() { return posX; }

    public void setPosX(XTYPE posX) { this.posX = posX; }

    public double getPosY() { return posY; }

    public void setPosY(double posY) { this.posY = posY; }

    public int getSelectedTrace() { return selectedTrace; }

    public void setSelectedTrace(int selectedTrace) { this.selectedTrace = selectedTrace; }

    public int getSelectedIndex() { return selectedIndex; }

    public void setSelectedIndex(int selectedIndex) { this.selectedIndex = selectedIndex; }
}
