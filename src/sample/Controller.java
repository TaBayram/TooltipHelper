package sample;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class Controller {


    public TextField textField_Level1;
    public ColorPicker colorPicker_Color;
    public TextField textField_Levels;
    public TextArea textArea_Result;
    public TextFlow textFlow_Colored;
    public ColorPicker colorPicker_ColorSelected;
    public ComboBox comboBox_Indicators;
    public TextField textField_NewIndicator;
    public TextField textField_CurrentIndicator;
    public CheckBox checkBox__LevelIndicatorSave;
    public Label label_notify;

    IndexRange selection;
    String beforeText;
    IndexRange beforeSelection;

    String separator = "s2p4r4t3";
    String currentIndicator = "|d|sLevel |l|e - ";
    Preferences preferences;
    String preferencesLevelIndicators = "levelindicators";


    public void initialize() throws BackingStoreException {
        preferences = Preferences.userNodeForPackage(Controller.class);

        colorPicker_Color.setValue(Color.valueOf("#ffcc00"));
        textArea_Result.setPromptText("|cffffcc00Level 1|r - Deals <A021,DataA1> |cffff0000damage|r! \n" +
                "|cffffcc00Level 2|r - Deals <A021,DataA2> |cffff0000damage|r! \n" +
                "|cffffcc00Level 3|r - Deals <A021,DataA3> |cffff0000damage|r! \n");



        //preferences.clear();

        List<String> list = new ArrayList<>();
        list.add("|sLevel |l|e -");
        //preferences.put(preferencesLevelIndicators,"|d|sLevel |l|e - ");

        var levelIndicatorsRaw = preferences.get(preferencesLevelIndicators,"|d|sLevel |l|e -");


        var levelIndicatorsSplit = levelIndicatorsRaw.split(separator);

        for (String indicator:levelIndicatorsSplit) {
            if(indicator.startsWith("|d")){
                currentIndicator = indicator.substring(2);
                textField_CurrentIndicator.setText(currentIndicator);
                comboBox_Indicators.getItems().add(currentIndicator);
            }
            else{
                comboBox_Indicators.getItems().add(indicator);
            }


        }

        comboBox_Indicators.setValue(comboBox_Indicators.getItems().get(0));




    }





    public void Generate(ActionEvent actionEvent) {

        String color = colorPicker_Color.getValue().toString();
        var textColor = ColorFormat(color);


        String level1 = textField_Level1.getText();

        String[] splitted = level1.split("1>");

        String levelsText = textField_Levels.getText();
        int levels = 0;
        try{
            levels = Integer.parseInt(levelsText);

        }catch(Exception e)
        {
            ErrorBox("Level amount can't be non numeric.");
            return;

        }


        List<String> generatedLevels = new ArrayList<>();
        for(int i = 1; i <= levels; i ++){
            IndicatorFormat(textColor,i);

            String textIndicator = IndicatorFormat(textColor,i);;
            String level = "";
            for (int j = 0; j < splitted.length; j++){
                level += splitted[j];
                if(j != splitted.length-1) level+= i+">";
            }
            generatedLevels.add(textIndicator+level);


        }

        textArea_Result.setText("");
        for (String string:generatedLevels) {
            textArea_Result.setText(textArea_Result.getText() + string + " \n");

        }
        GenerateColoredText(null);


    }


    public void GenerateColoredText(KeyEvent keyEvent) {
        String[] splittedText = textArea_Result.getText().split("\\|cff");
        var rawText = textArea_Result.getText();

        if(rawText.length() == 0) return;

        textFlow_Colored.getChildren().clear();
        int newHeight = 100;
        for(int i = 0; i < splittedText.length; i ++){
            try {
                String[] endSplitted = splittedText[i].split("\\|r");

                if(i == 0 && rawText.indexOf("|cff") >= splittedText[0].length()){
                    Text text = new Text();
                    text.setFill(Color.WHITE);
                    text.setText(splittedText[0]);
                    textFlow_Colored.getChildren().addAll(text);

                }
                else {
                    Text text = new Text();
                    text.setText(endSplitted[0].substring(6));
                    text.setFill(Color.valueOf(splittedText[i].substring(0, 6)));

                if(endSplitted.length == 1){
                    textFlow_Colored.getChildren().add(text);
                }
                else{

                    String nonColored = "";
                    for(int j = 1; j < endSplitted.length; j++){
                        nonColored += endSplitted[j];

                    }

                    Text text1 = new Text();
                    text1.setFill(Color.WHITE);
                    text1.setText(nonColored);




                    textFlow_Colored.getChildren().addAll(text,text1);

                    if(text1.getBoundsInLocal().getHeight() > text.getBoundsInLocal().getHeight())
                        newHeight += text1.getBoundsInLocal().getHeight() - text1.getFont().getSize()*1.4;
                    else
                        newHeight += text.getBoundsInLocal().getHeight();

                }
                }


            }
            catch(StringIndexOutOfBoundsException stringIndexOutOfBoundsException){
                continue;
            }
        }

        textFlow_Colored.setMinHeight(newHeight);

    }

    public void Colorify(ActionEvent actionEvent) {

        if(selection == null || selection.getLength() == 0 ){
            return;
        }

        else{
            var color = ColorFormat(colorPicker_ColorSelected.getValue().toString());
            var fullText = textField_Level1.getText();

            if(beforeSelection == selection){
                fullText = beforeText;
            }


            beforeSelection = selection;
            beforeText = fullText +"";
            String first = fullText.substring(0,selection.getStart()) + color+ fullText.substring(selection.getStart(), selection.getEnd()) + "|r" + fullText.substring(selection.getEnd());
            textField_Level1.setText(first);


        }

    }

    public void SetSelectedText(MouseEvent mouseEvent) {
        selection = textField_Level1.getSelection();
    }


    public String ColorFormat(String rawColor){
        rawColor = rawColor.replaceFirst("0x","");
        rawColor = rawColor.substring(0,6);

        String textColor = "|cff"+rawColor;
        return  textColor;
    }

    public String IndicatorFormat(String textColor, int level){
        String indicator = currentIndicator.replace("|s",textColor);
        indicator = indicator.replace("|e","|r");
        indicator = indicator.replace("|l", ""+level);

        return indicator;
    }


    public void LevelIndicatorAdd(ActionEvent actionEvent) {
        String newIndicator = textField_NewIndicator.getText();

        boolean hasStart = false;
        boolean hasEnd = false;
        boolean hasLevel = false;
        if(newIndicator.contains("|s")){
            hasStart = true;
        }
        if(newIndicator.contains("|e")){
            hasEnd = true;
        }
        if(newIndicator.contains("|l")){
            hasLevel = true;
        }

        if(hasStart && !hasEnd){
            ErrorBox("You must put color ending which is: |e  !");
            return;
        }
        if(!hasStart && hasEnd){
            ErrorBox("You must put color starting which is: |s  !");
            return;
        }
        if(!hasLevel){
            ErrorBox("You must put level which is: |l  !");
            return;
        }

        Preferences preferenes;
        preferenes = Preferences.userNodeForPackage(Controller.class);
        var levelIndicatorsRaw = preferenes.get(preferencesLevelIndicators,"null");
        if(levelIndicatorsRaw != null)
            preferenes.put(preferencesLevelIndicators,levelIndicatorsRaw + separator + newIndicator);
        else
            preferenes.put(preferencesLevelIndicators,newIndicator);

        NotifyOperation("Added!");
        comboBox_Indicators.getItems().add(newIndicator);





    }

    public void LevelIndicatorRemove(ActionEvent actionEvent) {
        String removingText = (String) comboBox_Indicators.getValue();


        var levelIndicatorsRaw = preferences.get(preferencesLevelIndicators,"null");

        if(levelIndicatorsRaw == null) return;

        var levelIndicatorsSplit = levelIndicatorsRaw.split(separator);

        String indicators = "";
        for(int i = 0; i < levelIndicatorsSplit.length; i++) {
            var indicator =  levelIndicatorsSplit[i];
            if(indicator.startsWith("|d")){
                indicator = indicator.substring(2);
            }
            if(!indicator.equals(removingText)){
                if(i != 0) indicators += separator;
                indicators += levelIndicatorsSplit[i];
            }
            else{

            }

        }

        NotifyOperation("Removed!");
        comboBox_Indicators.getItems().remove(removingText);
        preferences.put(preferencesLevelIndicators,indicators);
    }


    public void LevelIndicatorChooseAsCurrentIndicator(ActionEvent actionEvent) {
        String newCurrentIndicator = (String) comboBox_Indicators.getValue();

        var levelIndicatorsRaw = preferences.get(preferencesLevelIndicators,"null");
        if(levelIndicatorsRaw == null) return;
        var levelIndicatorsSplit = levelIndicatorsRaw.split(separator);

        String indicators = "";
        for(int i = 0; i < levelIndicatorsSplit.length; i++) {

            var indicator =  levelIndicatorsSplit[i];

            if(indicator.startsWith("|d")){
                indicator = indicator.substring(2);
            }

            if(indicator.equals(newCurrentIndicator)){
                if(i != 0) indicators += separator;
                currentIndicator = indicator;
                textField_CurrentIndicator.setText(indicator);

                indicators += "|d"+indicator;
            }
            else{
                if(i != 0) indicators += separator;
                indicators += indicator;
            }

        }

        NotifyOperation("Set as current!");
        preferences.put(preferencesLevelIndicators,indicators);

    }

    public void LevelIndicatorPreview(ActionEvent actionEvent) {
        var previewIndicator = textField_NewIndicator.getText();

        String color = colorPicker_Color.getValue().toString();
        var textColor = ColorFormat(color);



        List<String> generatedLevels = new ArrayList<>();
        for(int i = 1; i <= 6; i ++){
            String indicator = previewIndicator.replace("|s",textColor);
            indicator = indicator.replace("|e","|r");
            indicator = indicator.replace("|l", ""+i);

            String textIndicator = indicator;;
            String level = "";

            generatedLevels.add(textIndicator+level);


        }

        textArea_Result.setText("");
        for (String string:generatedLevels) {
            textArea_Result.setText(textArea_Result.getText() + string + " \n");

        }

        GenerateColoredText(null);



    }

    public void ErrorBox(String text){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
    }

    public void NotifyOperation(String text){
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                Thread.sleep(2000);
                return null;
            }
        };

        label_notify.setText(text);
        label_notify.visibleProperty().bind(task.runningProperty());
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();


    }

}
