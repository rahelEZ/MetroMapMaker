/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package m3.data;

/**
 *
 * @author rahel
 */
public interface CanvasItem {
    public String itemType();
    public boolean isDraggable();
    public double[] getRange();
    public double[] getXy();
}
