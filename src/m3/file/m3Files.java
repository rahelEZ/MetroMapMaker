package m3.file;
import djf.AppTemplate;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;
import djf.components.AppDataComponent;
import djf.components.AppFileComponent;
import java.io.File;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javax.imageio.ImageIO;
import m3.data.DraggableImage;
import m3.data.DraggableLabel;
import m3.data.DraggableStation;
import m3.data.TrainLine;
import m3.data.TrainHandle;
import m3.data.m3Data;
import m3.gui.m3Workspace;

/**
 * This class serves as the file management component for this application,
 * providing all I/O services.
 *
 * @author Richard McKenna
 * @author ?
 * @version 1.0
 */
public class m3Files implements AppFileComponent {
    // FOR JSON LOADING
    AppTemplate app;
   
    
 
    /**
     * This method is for saving user work, which in the case of this
     * application means the data that together draws the logo.
     * 
     * @param data The data management component for this application.
     * 
     * @param filePath Path (including file name/extension) to where
     * to save the data to.
     * 
     * @throws IOException Thrown should there be an error writing 
     * out data to the file.
     */
    //FOR THE TIME BEING SAVE DOESN'T IMAGES
    public m3Files(AppTemplate initApp) {
        app = initApp;
    }
    @Override
    public void saveData(AppDataComponent data, String filePath) throws IOException {
	// GET THE DATA
	m3Data dataManager = (m3Data)data;
//	
//
//	// NOW BUILD THE JSON OBJCTS TO SAVE
	JsonArrayBuilder stationsArray = Json.createArrayBuilder();
        JsonArrayBuilder linesArray = Json.createArrayBuilder();
        JsonArrayBuilder labelsArray = Json.createArrayBuilder();
        JsonArrayBuilder imagesArray = Json.createArrayBuilder();
        JsonArrayBuilder handlesArray = Json.createArrayBuilder();
        
        
	for (DraggableStation station : dataManager.stationList) {
	    stationsArray.add(makeJsonStationObject(station));
	}
        for (TrainLine line: dataManager.trackList){
            linesArray.add(makeJsonTrackObject(line));
        }
        for (DraggableImage image: dataManager.imageList){
            imagesArray.add(makeJsonImageObject(image));
        }
        for (DraggableLabel label: dataManager.labelList){
            labelsArray.add(makeJsonLabelObject(label));
        }
        for (TrainHandle handle: dataManager.handlesList){
            handlesArray.add(makeJsonHandleObject(handle));
        }
        
        // THEN PUT IT ALL TOGETHER IN A JsonObject
	JsonObject dataManagerJSO = Json.createObjectBuilder()
                .add("name", dataManager.getTitle())
                .add("background_color",makeJsonColorObject(dataManager.backgroundColor))
                .add("background_image", dataManager.bgPath)
		.add("lines",linesArray )
		.add("stations", stationsArray)
                .add("images", imagesArray)
                .add("labels", labelsArray)
                .add("handles", handlesArray)
		.build();
	
	// AND NOW OUTPUT IT TO A JSON FILE WITH PRETTY PRINTING
	Map<String, Object> properties = new HashMap<>(1);
	properties.put(JsonGenerator.PRETTY_PRINTING, true);
	JsonWriterFactory writerFactory = Json.createWriterFactory(properties);
	StringWriter sw = new StringWriter();
	JsonWriter jsonWriter = writerFactory.createWriter(sw);
	jsonWriter.writeObject(dataManagerJSO);
	jsonWriter.close();
//
//	// INIT THE WRITER
	OutputStream os = new FileOutputStream(filePath);
	JsonWriter jsonFileWriter = Json.createWriter(os);
	jsonFileWriter.writeObject(dataManagerJSO);
	String prettyPrinted = sw.toString();
	PrintWriter pw = new PrintWriter(filePath);
	pw.write(prettyPrinted);
	pw.close();
    }
    
    private JsonObject makeJsonColorObject(Color color) {
	JsonObject colorJson = Json.createObjectBuilder()
		.add("red", color.getRed())
		.add("green", color.getGreen())
		.add("blue", color.getBlue())
		.add("alpha", color.getOpacity()).build();
	return colorJson;
    }
    
     @Override
    public String getCurrentFile(){
        
        return "";
    }
    
    
    
    
    
    public static String getUserDataDirectory() {
    return System.getProperty("user.home") + File.separator + ".jstock" + File.separator + "m3storage" + File.separator;
}
    
    
    
    
    
    
      
    /**
     * This method loads data from a JSON formatted file into the data 
     * management component and then forces the updating of the workspace
     * such that the user may edit the data.
     * 
     * @param data Data management component where we'll load the file into.
     * 
     * @param filePath Path (including file name/extension) to where
     * to load the data from.
     * 
     * @throws IOException Thrown should there be an error reading
     * in data from the file.
     */
    
   
    
    
    @Override
    public void loadData(AppDataComponent data, String filePath) throws IOException {
	// CLEAR THE OLD DATA OUT
	m3Data dataManager = (m3Data)data;
	dataManager.resetData();
	
	// LOAD THE JSON FILE WITH ALL THE DATA
	JsonObject json = loadJSONFile(filePath);
	
	
	// AND NOW LOAD ALL THE SHAPES
	JsonArray jsonStationsArray = json.getJsonArray("stations");
        JsonArray jsonLinesArray = json.getJsonArray("lines");
        JsonArray jsonLabelsArray= json.getJsonArray("labels");
        JsonArray jsonImagesArray= json.getJsonArray("images");
        JsonArray jsonHandlesArray = json.getJsonArray("handles");
        
        
        dataManager.setTitle(json.getString("name"));
        
        
        
        if(jsonStationsArray!=null){
            for (int i = 0; i < jsonStationsArray.size(); i++) {
	    JsonObject jsonShape = jsonStationsArray.getJsonObject(i);
            DraggableStation draggableStation = loadStation(jsonShape);
//	    Shape shape = loadShape(jsonShape);
	    dataManager.addStation(draggableStation);
            System.err.println("");
	}
        }
        
        
        
       if (jsonHandlesArray!=null){
           for (int i = 0; i < jsonHandlesArray.size(); i++){
               JsonObject jsonShape = jsonHandlesArray.getJsonObject(i);
               TrainHandle handle = loadHandle(jsonShape);
               dataManager.addHandle(handle);
           }
       }
	if(jsonLinesArray!=null){
            for (int i = 0 ; i < jsonLinesArray.size(); i ++ ){
             JsonObject jsonShape = jsonLinesArray.getJsonObject(i);
            TrainLine trainLine = loadLine(jsonShape,data);
	    dataManager.addLine(trainLine);
            System.err.println("");
        } 
        }
        
        if(jsonImagesArray!=null){
           for(int i=0; i < jsonImagesArray.size(); i++){
            JsonObject jsonShape= jsonImagesArray.getJsonObject(i);
            DraggableImage image= loadImage(jsonShape);
            dataManager.addImage(image);
        } 
        }
       if(jsonLabelsArray!=null){
            for(int i=0; i < jsonLabelsArray.size(); i++){
            JsonObject jsonShape= jsonLabelsArray.getJsonObject(i);
            DraggableLabel label= loadLabel(jsonShape);
            label.applyFont();
            dataManager.addLabel(label);
        }
       }
       
       try{
       
           Color backgroundColor = loadColor(json,"background_color");
           
           dataManager.setBackGroundColor(backgroundColor);
           
           String backgroundImage= json.getString("background_image");
           dataManager.setBackGroundImage(backgroundImage);
           
       }
       catch(Exception ex){dataManager.setBackGroundColor(m3Data.DEFAULT_BACKGROUND_COLOR);};
        dataManager.reloadWorkspace();
    }
    
    private double getDataAsDouble(JsonObject json, String dataName) {
	JsonValue value = json.get(dataName);
	JsonNumber number = (JsonNumber)value;
	return number.bigDecimalValue().doubleValue();	
    }
    
    
    private DraggableStation loadStation(JsonObject jsonObject){
        String name = jsonObject.getString("name");
        Integer x_cor = jsonObject.getInt("x");
        Integer y_cor = jsonObject.getInt("y");
        
        
        
        
        DraggableStation draggableStation = new DraggableStation(name);
        draggableStation.x_cor = x_cor;
        draggableStation.y_cor = y_cor;
        draggableStation.setLocation(x_cor, y_cor);


        try{
                    Integer offset_x = jsonObject.getInt("offset_x");
        Integer offset_y = jsonObject.getInt("offset_y");
        Integer angle = jsonObject.getInt("angle");
 draggableStation.stationLabel.newOffset(offset_x, offset_y);
            draggableStation.stationLabel.setAngle(angle);
            
            
              Color stationColor = loadColor(jsonObject, "color");
              draggableStation.setColor(stationColor);
              Integer radius = jsonObject.getInt("radius");
              draggableStation.sizeStation(radius);
              
        }
catch(Exception ex){
    
}
//        if (offset_x != null && offset_x!=null && angle!=null){
           
           
//        }
        return draggableStation;
    }
    private JsonObject makeJsonStationObject(DraggableStation station){
        JsonObject jsonObj = Json.createObjectBuilder()
                .add("x", station.getX())
                .add("y", station.getY())
                .add("angle" , station.stationLabel.angle)
                .add("offset_x" , station.stationLabel.offsetX)
                .add("offset_y" , station.stationLabel.offsetY)
                .add("color", makeJsonColorObject(station.color))
                .add("radius", station.radius)
                .add("name", station.name).build();
        
        return jsonObj;
    }
    
    private TrainLine loadLine(JsonObject jsonObject,AppDataComponent data){
        String name = jsonObject.getString("name");
        Boolean circular = jsonObject.getBoolean("circular");
        Color lineColor = loadColor(jsonObject, "color");
        m3Data dataObj = (m3Data)data;
        TrainLine trainLine = new TrainLine();
        trainLine.lineName = name;
//        trainLine.setStroke(lineColor);
        trainLine.color= lineColor;
        trainLine.circular = circular;
        
        JsonArray stationNames = jsonObject.getJsonArray("station_names");
        
        for (int i = 0 ; i < stationNames.size(); i++){
            String stationName  = stationNames.getString(i);
            trainLine.stationsNames.add(stationName);
           
        }
        
        try{
            int thickness = jsonObject.getInt("thickness");
            trainLine.setThickness(thickness);
        }
        catch(Exception ex){}
        
        trainLine.loadhandles(dataObj.handleMap);
        trainLine.drawPath(dataObj.stationMap);
        trainLine.setStroke(lineColor);
       return trainLine;
       
    }
    
    private TrainHandle loadHandle(JsonObject obj){
        TrainHandle handle = new TrainHandle(obj.getInt("x"), obj.getInt("y"), obj.getString("line"));
        handle.name = obj.getString("name");
        return handle;
    }
    
    private DraggableLabel loadLabel(JsonObject obj){
        DraggableLabel label= new DraggableLabel();
        label.setLocation(obj.getInt("x"), obj.getInt("y"));
        label.setText(obj.getString("text"));
        
        try{
            label.bold = obj.getBoolean("bold");
            label.italic = obj.getBoolean("italic");
            label.fontfalimy = obj.getString("font");
            label.size = obj.getInt("size");
            label.fontColor = loadColor(obj, "font_color");
        }
        catch(Exception ex){
            
        }
        return label;
    }
    
     private DraggableImage loadImage(JsonObject obj){
        DraggableImage image= new DraggableImage();
        image.setLocation(obj.getInt("x"), obj.getInt("y"));
        image.loadImages(obj.getString("url"));
        return image;
    }
     
    private JsonObject makeJsonTrackObject(TrainLine line){
        JsonArrayBuilder stationsArrayBuilder = Json.createArrayBuilder();
        for (int i = 0; i < line.stationsNames.size(); i++) {
            stationsArrayBuilder.add(line.stationsNames.get(i));
        }
        JsonArray stationsArray = stationsArrayBuilder.build();
        
        JsonObject jsonObj = Json.createObjectBuilder()
                .add("name", line.lineName)
                .add("station_names", stationsArray) 
                .add("color", makeJsonColorObject((Color) line.getStroke()))
                .add("thickness", line.thickness)
                .add("circular", line.circular).build();
                
        return jsonObj;
    }
    
    private JsonObject makeJsonLabelObject(DraggableLabel label){
        JsonObject jsonObj= Json.createObjectBuilder()
                .add("x", label.getX())
                .add("y", label.getY())
                .add("bold", label.bold)
                .add("italic", label.italic)
                .add("size", label.size)
                .add("font", label.fontfalimy)
                .add("font_color", makeJsonColorObject(label.fontColor))
                .add("text", label.getText()).build();
                
        
        return jsonObj;
    }
    
     private JsonObject makeJsonImageObject(DraggableImage image){
        JsonObject jsonObj= Json.createObjectBuilder()
                .add("x", image.getX())
                .add("y", image.getY())
                .add("url", image.filePath).build();
        return jsonObj;
    }
     private JsonObject makeJsonHandleObject(TrainHandle handle){
         JsonObject jsonObj = Json.createObjectBuilder()
                    .add("x", handle.getX())
                    .add("y", handle.getY())
                    .add("line", handle.line)
                    .add("name",  handle.name).build();
         return jsonObj;
     }
     
    private Color loadColor(JsonObject json, String colorToGet) {
	JsonObject jsonColor = json.getJsonObject(colorToGet);
	double red = getDataAsDouble(jsonColor, "red");
	double green = getDataAsDouble(jsonColor, "green");
	double blue = getDataAsDouble(jsonColor, "blue");
	double alpha = getDataAsDouble(jsonColor, "alpha");
	Color loadedColor = new Color(red, green, blue, alpha);
	return loadedColor;
    }

    // HELPER METHOD FOR LOADING DATA FROM A JSON FORMAT
    private JsonObject loadJSONFile(String jsonFilePath) throws IOException {
	InputStream is = new FileInputStream(jsonFilePath);
	JsonReader jsonReader = Json.createReader(is);
	JsonObject json = jsonReader.readObject();
	jsonReader.close();
	is.close();
	return json;
    }
    
    /**
     * This method is provided to satisfy the compiler, but it
     * is not used by this application.
     */
    @Override
    public void exportData(AppDataComponent data, String filePath) throws IOException {
        // GET THE DATA
	m3Data dataManager = (m3Data)data;
//	// NOW BUILD THE JSON OBJCTS TO SAVE
        processSnapshot(filePath);
	JsonArrayBuilder stationsArray = Json.createArrayBuilder();
        JsonArrayBuilder linesArray = Json.createArrayBuilder();
        
	for (DraggableStation station : dataManager.stationList) {
	    stationsArray.add(makeJsonStationObject(station));
	}
        for (TrainLine line: dataManager.trackList){
            linesArray.add(makeJsonTrackObject(line));
        }
//	// THEN PUT IT ALL TOGETHER IN A JsonObject
	JsonObject dataManagerJSO = Json.createObjectBuilder()
                .add("name", dataManager.getTitle())
		.add("lines",linesArray )
		.add("stations", stationsArray)
		.build();
//	
//	// AND NOW OUTPUT IT TO A JSON FILE WITH PRETTY PRINTING
	Map<String, Object> properties = new HashMap<>(1);
	properties.put(JsonGenerator.PRETTY_PRINTING, true);
	JsonWriterFactory writerFactory = Json.createWriterFactory(properties);
	StringWriter sw = new StringWriter();
	JsonWriter jsonWriter = writerFactory.createWriter(sw);
	jsonWriter.writeObject(dataManagerJSO);
	jsonWriter.close();
//
//	// INIT THE WRITER
	OutputStream os = new FileOutputStream(filePath);
	JsonWriter jsonFileWriter = Json.createWriter(os);
	jsonFileWriter.writeObject(dataManagerJSO);
	String prettyPrinted = sw.toString();
	PrintWriter pw = new PrintWriter(filePath);
	pw.write(prettyPrinted);
	pw.close();
    }
    
    public void processSnapshot(String filepath){
        m3Workspace workspace = (m3Workspace) app.getWorkspaceComponent();
            Pane canvas = workspace.getCanvas();
            WritableImage image = canvas.snapshot(new SnapshotParameters(), null);
            File file = new File(filepath);
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    
    /**
     * This method is provided to satisfy the compiler, but it
     * is not used by this application.
     */
    @Override
    public void importData(AppDataComponent data, String filePath) throws IOException {
	// AGAIN, WE ARE NOT USING THIS IN THIS ASSIGNMENT
    }

    @Override
    public void processSnaphot(AppDataComponent dataComponent, String path) {
        processSnapshot(path);
    }
}
