package m3.data;
/**
 * This enum has the various possible states of the logo maker app
 * during the editing process which helps us determine which controls
 * are usable or not and what specific user actions should affect.
 * 
 * @author Rahel Zewde
 * @version 1.0
 */
public enum m3State {
    SELECTING_SHAPE,
    DRAGGING_SHAPE,
    STARTING_LINE,
    STARTING_STATION,
    
    STARTING_IMAGE,
    STARTING_TEXT,
    
    SIZING_SHAPE,
    DRAGGING_NOTHING,
    SIZING_NOTHING, 
    STARTING_LABEL, 
    STARTING_RECTANGLE
}
